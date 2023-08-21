package controllers

import common.{Defaults, GlobalWritesAndFormats}
import config.ConfigManager.includeStaticVoteCountOnCastVotePage
import controllers.view.DBOps.{getAll, getLatestPollID, getPollPayload}
import db.PollDBHelper
import error.NoPollFoundException
import payloads.{GetPollPayload, PollBasics}
import play.api.libs.json._
import Common.getPollID

import javax.inject._
import play.api.mvc._

import scala.util.{Failure, Success, Try}

@Singleton
class ViewPollController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with PollDBHelper with GlobalWritesAndFormats {

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val returnedPollID = getPollID(request.queryString)
    val pollSelectionList = getAll(returnedPollID)
    val renderedSelect = getRenderedSelect(pollSelectionList, returnedPollID)
    val poll = getPollPayload(returnedPollID)
    val renderedPoll = getRenderedPoll(
      if (pollSelectionList.isEmpty) poll.copy(pollTitle = "No polls have been created yet")
      else if (!poll.found) poll.copy(pollTitle = "Poll doesn't exist, please select a different poll")
      else poll
    )
    val comboHTML = renderedSelect+renderedPoll

    Ok(views.html.viewpoll.index(comboHTML, returnedPollID))
  }

  def getRenderedSelect(pollBasicsList: List[PollBasics], pollID: Int): String = {
    if (pollBasicsList.length > 1) pollBasicsList.map{p =>
    val selected = if (p.pollId == pollID) " selected" else ""
        s"""<option value=${p.pollId}$selected>${p.pollTitle} (${p.pollId})</option>"""
      }.mkString("""<select tabindex="1" name="pollList" id="pollList">""","\n","</select>")
    else """<div id="pollList" hidden=""><select name="pollList" id="pollList"></select></div>"""
  }

  def getRenderedPoll(pollPayload: GetPollPayload): String = {
    val htmlHeaderLineList: List[String] = List(
      s"""<form method="post" name="voteForm">""",
      s"""<h3>${pollPayload.pollTitle} (${pollPayload.pollID})</h3>""",
      s"""<h5>${pollPayload.pollDesc}</h5>""",
      pollPayload.uniqueIndividualIdentifierLabel match {
        case Some(s) => s"""$s: &nbsp;<input type="text" id="uniqueIndividualIdentifier" name="uniqueIndividualIdentifier"><br>"""
        case None => ""
      },
    )
    val htmlFooterLineList: List[String] = if (pollPayload.found) List(
      s"""<br>""",
      s"""<input type="submit" value="Cast Vote"><button type="button" style="float: right;" onclick="window.location.replace('./results?p=${pollPayload.pollID}')">See Results</button>""",
      s"""</form>""",
      s"""<div id="voteMessage" hidden=""></div>"""
    ) else List.empty
    val htmlInputLineList: List[String] = pollPayload.pollOptions.map{o =>
      val inputType = if (pollPayload.allowMultipleSelections) "checkbox" else "radio"
      val voteCount = if (includeStaticVoteCountOnCastVotePage) {
        val summaryVoteCount = pollPayload.voteSummaries.find(_.optionId == o.optionID).
          getOrElse(Defaults.voteSummary).voteCount
        s" ($summaryVoteCount)"
      } else ""
      s"""<input type="$inputType" name="pollOptions" value=${o.optionID} id="po${o.optionID}"> <label for="po${o.optionID}">${o.optionName}$voteCount</label><br>"""
    }
    (htmlHeaderLineList ++ htmlInputLineList ++ htmlFooterLineList).filter(_.nonEmpty).mkString("\n")
  }

  def viewPoll: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Try(getPollJsonPayload(request.queryString)) match {
      case Success(s) => Ok(s)
      case Failure(e) =>
        val result = Json.toJson(Map(
          "exception" -> e.getClass.toString,
          "message" -> e.getMessage
        ))
        BadRequest(result)
    }
  }

  def allPolls(): Action[AnyContent] = Action (_ => Ok(Json.toJson(getAll(getLatestPollID))))

  private def getPollJsonPayload(queryString: Map[String, Seq[String]]): JsValue = {
    if (!queryString.contains("p")) throw NoPollFoundException("Specific poll query parameter not set and must be provided when using this endpoint")
    val pollID = getPollID(queryString)
    val returnedPoll = getPollPayload(pollID)
    Json.toJson(returnedPoll)
  }
}
