package controllers.castvote

import common.GetAttribute
import payloads.{CastVoteRequest, CreatePollRequest, PollOptions}
import play.api.libs.json.JsValue

class GetRequest(json: JsValue) extends GetAttribute(json) {
  def get(sourceIP: String): CastVoteRequest = {
    CastVoteRequest(
      pollId = getAttr("pollId"),
      optionIds = getAttrList("optionId"),
      uniqueIndividualIdentifier = getAttrOption("uniqueIndividualIdentifier"),
      sourceIP = sourceIP,
      successful = None,
      passedValidation = None,
      message = None
    )
  }
}
