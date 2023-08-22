package com.github.diinnk.powly.controllers.castvote

import com.github.diinnk.powly.common.GetAttribute
import com.github.diinnk.powly.common.payloads.CastVoteRequest
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
