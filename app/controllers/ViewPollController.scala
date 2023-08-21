package controllers

import common.GlobalWritesAndFormats
import controllers.view.DBOps.{getAll, getLatestPollID, getPollPayload}
import db.PollDBHelper
import error.NoPollFoundException
import payloads.{GetPollPayload, PollBasics}
import play.api.libs.json._

import javax.inject._
import play.api.mvc._

import scala.util.{Failure, Success, Try}

@Singleton
class ViewPollController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with PollDBHelper with GlobalWritesAndFormats {

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val returnedPollID = getPollID(request.queryString)
    val pollSelectionList = getAll(getLatestPollID)
    val renderedSelect = getRenderedSelect(pollSelectionList, returnedPollID)
    val poll = getPollPayload(returnedPollID)
    val renderedPoll = getRenderedPoll(poll)
    val comboHTML = renderedSelect+renderedPoll

    Ok(views.html.get.index(comboHTML, returnedPollID))
  }

  def getRenderedSelect(pollBasicsList: List[PollBasics], pollID: Int): String = {
    if (pollBasicsList.nonEmpty) pollBasicsList.map{p =>
    val selected = if (p.pollId == pollID) " selected" else ""
        s"""<option value=${p.pollId}$selected>${p.pollTitle} (${p.pollId})</option>"""
      }.mkString("""<select name="pollList" id="pollList">""","\n","</select>")
    else ""
  }

  def getRenderedPoll(pollPayload: GetPollPayload): String = {
    val htmlHeaderLineList: List[String] = List(
      s"""<form method="post" name="voteForm">""",
      s"""<h2>${pollPayload.pollTitle} (${pollPayload.pollID})</h2>""",
      s"""<h3>${pollPayload.pollDesc}</h2>""",
      pollPayload.uniqueIndividualIdentifierLabel match {
        case Some(s) => s"""$s: &nbsp;<input type="text" id="uniqueIndividualIdentifier" name="uniqueIndividualIdentifier"><br>"""
        case None => ""
      },
    )
    val htmlFooterLineList: List[String] = List(
      s"""<br>""",
      s"""<input type="submit" value="Cast Vote">""",
      s"""</form>""",
      s"""<div id="voteMessage" hidden=""></div>"""
    )
    val htmlInputLineList: List[String] = pollPayload.pollOptions.map{o =>
      val inputType = if (pollPayload.allowMultipleSelections) "checkbox" else "radio"
      s"""<input type="$inputType" name="pollOptions" value=${o.optionID}>${o.optionName}<br>"""
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

  private def getPollID(requestMap: Map[String, Seq[String]]): Int = requestMap.getOrElse("p", Seq(getLatestPollID.toString)).head.toInt
}
