package controllers.create

import common.GetAttribute
import payloads.{CreatePollRequest, PollOptions}
import play.api.libs.json.JsValue

class GetRequest(json: JsValue) extends GetAttribute(json) {
  def get: CreatePollRequest = {
    val pollOptions: List[PollOptions] = getAttrList("pollOptions").zipWithIndex.
      map(o => PollOptions(optionID = o._2+1, optionName = cleanDBString(o._1)))
    val pollTitle: String = cleanDBString(getAttr("pollTitle"))

    CreatePollRequest(
      message = None,
      pollTitle = pollTitle,
      pollDesc = cleanDBString(getAttr("pollDesc", pollTitle)),
      allowMultipleSelections = getAttr("allowMultipleSelections", false),
      allowMultipleIndividualVoteActions = getAttr("allowMultipleIndividualVoteActions", false),
      uniqueIndividualIdentifierLabel = cleanDBString(getAttrOption("uniqueIndividualIdentifierLabel")),
      pollOptions = pollOptions,
      successful = None
    )
  }

  private def cleanDBString(s: String): String = s.replace("'", "''")
  private def cleanDBString(os: Option[String]): Option[String] = os match {
    case Some(s) => Option(cleanDBString(s))
    case None => None
  }
}
