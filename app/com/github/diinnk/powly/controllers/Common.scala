package com.github.diinnk.powly.controllers

import com.github.diinnk.powly.common.payloads.PollBasics
import com.github.diinnk.powly.controllers.view.DBOps.{getAll, getLatestPollID}

object Common {
  def getPollID(requestMap: Map[String, Seq[String]]): Int = requestMap.getOrElse("p", Seq(getLatestPollID.toString)).head.toInt

  def getRenderedSelectHTML(pollBasicsList: List[PollBasics], pollID: Int): String = {
    if (pollBasicsList.length > 1) pollBasicsList.map { p =>
      val selected = if (p.pollId == pollID) " selected" else ""
      s"""<option value=${p.pollId}$selected>${p.pollTitle} (${p.pollId})</option>"""
    }.mkString("""<select tabindex="1" name="pollList" id="pollList">""", "\n", "</select>")
    else """<div id="pollList" hidden=""><select name="pollList" id="pollList"></select></div>"""
  }

  def getRenderedSelect(pollID: Int): (List[PollBasics], String) = {
    val pollSelectionList = getAll(pollID)
    (pollSelectionList, getRenderedSelectHTML(pollSelectionList, pollID))
  }

}
