package controllers

import controllers.view.DBOps.getLatestPollID

object Common {
  def getPollID(requestMap: Map[String, Seq[String]]): Int = requestMap.getOrElse("p", Seq(getLatestPollID.toString)).head.toInt

}
