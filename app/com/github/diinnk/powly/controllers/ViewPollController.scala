package com.github.diinnk.powly.controllers

import com.github.diinnk.powly.config.ConfigManager.{hideCreateAPoleOnFrontPage, includeStaticVoteCountOnCastVotePage}
import com.github.diinnk.powly.common.payloads.GetPollPayload
import com.github.diinnk.powly.common.{Defaults, GlobalWritesAndFormats}
import com.github.diinnk.powly.controllers.Common.{getPollID, getRenderedSelect}
import com.github.diinnk.powly.controllers.view.DBOps.{getAll, getLatestPollID, getPollPayload}
import com.github.diinnk.powly.db.PollDBHelper
import com.github.diinnk.powly.error.NoPollFoundException
import play.api.libs.json._
import play.api.mvc._

import javax.inject._
import scala.util.{Failure, Success, Try}

@Singleton
class ViewPollController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with PollDBHelper with GlobalWritesAndFormats {

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val returnedPollID = getPollID(request.queryString)
    val (pollSelectionList, renderedSelect) = getRenderedSelect(returnedPollID)
    val poll = getPollPayload(returnedPollID)
    val renderedPoll = getRenderedPoll(
      if (pollSelectionList.isEmpty) poll.copy(pollTitle = "No polls have been created yet")
      else if (!poll.found) poll.copy(pollTitle = "Poll doesn't exist, please select a different poll")
      else poll
    )
    val comboHTML = renderedSelect+renderedPoll

    Ok(views.html.viewpoll.index(comboHTML, returnedPollID))
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
    val htmlFooterLineListPt1: List[String] = if (pollPayload.found) List(
      s"""<br>""",
      s"""<input type="submit" value="Cast Vote"><button type="button" style="float: right;" onclick="window.location.replace('./results?p=${pollPayload.pollID}')">See Results</button>""",
      s"""</form>"""
    ) else List.empty
    val htmlFooterLineListPt2 = List(
      if (hideCreateAPoleOnFrontPage) "" else s"""<div><button type="button" style="float: left;" onclick="window.location.replace('./createPoll')">Create Poll</button></div>""",
      s"""<div id="voteMessage" hidden=""></div>"""
    )
    val htmlInputLineList: List[String] = pollPayload.pollOptions.map{o =>
      val inputType = if (pollPayload.allowMultipleSelections) "checkbox" else "radio"
      val voteCount = if (includeStaticVoteCountOnCastVotePage) {
        val summaryVoteCount = pollPayload.voteSummaries.find(_.optionId == o.optionID).
          getOrElse(Defaults.voteSummary).voteCount
        s" ($summaryVoteCount)"
      } else ""
      s"""<input type="$inputType" name="pollOptions" value=${o.optionID} id="po${o.optionID}"> <label for="po${o.optionID}">${o.optionName}$voteCount</label><br>"""
    }
    (htmlHeaderLineList ++ htmlInputLineList ++ htmlFooterLineListPt1 ++ htmlFooterLineListPt2).filter(_.nonEmpty).mkString("\n")
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
