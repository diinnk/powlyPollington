package com.github.diinnk.powly.controllers

import com.github.diinnk.powly.common.payloads.GetPollPayload
import com.github.diinnk.powly.common.{Defaults, GlobalWritesAndFormats}
import com.github.diinnk.powly.controllers.Common.{getPollID, getRenderedSelect}
import com.github.diinnk.powly.controllers.view.DBOps.getPollPayload
import com.github.diinnk.powly.db.PollDBHelper
import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class ResultsController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with PollDBHelper with GlobalWritesAndFormats {

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val returnedPollID = getPollID(request.queryString)
    val (_, renderedSelect) = getRenderedSelect(returnedPollID)
    val poll = getPollPayload(returnedPollID)
    val renderedPayload = renderedSelect ++ getRenderedPoll(poll)

    Ok(views.html.results.index(renderedPayload,returnedPollID))
  }

  def getRenderedPoll(poll: GetPollPayload): String = {
    val htmlHeaderLineList: List[String] = List(
      s"""<h3>${poll.pollTitle} (${poll.pollID})</h3>""",
      s"""<h6>${poll.pollDesc}</h6>""",
      """<table class="resultsChart"><tbody>"""
    )
    val htmlFooterLineList: List[String] = List(
    """<tr style="visibility: collapse"><th class="blankCell"></th></tr></tbody></table>""",
      s"""<div><button type="button" style="float: right;" onclick="window.location.replace('./?p=${poll.pollID}')">Go Vote</button></div>"""
    )
    val totalVotes = poll.voteDetail.length.toDouble
    val htmlTableRowList: List[(Int, String, String)] = poll.pollOptions.map{s =>
      val voteCount = poll.voteSummaries.find(_.optionId == s.optionID).getOrElse(Defaults.voteSummary).voteCount
      val widthPercentage = ((voteCount.toDouble / totalVotes)*100).round
      (voteCount, s.optionName, s"""<tr style="background: transparent;"><th scope="row">${s.optionName}:</th><td><span style="width:$widthPercentage%"><b>$voteCount</b></span></td>""")
    }.sortBy(r => (- r._1, r._2))
    (htmlHeaderLineList ++ htmlTableRowList.map(_._3) ++ htmlFooterLineList).filter(_.nonEmpty).mkString("\n")
  }
}
