package com.github.diinnk.powly.controllers

import com.github.diinnk.powly.common.GlobalWritesAndFormats
import com.github.diinnk.powly.common.payloads.CastVoteRequest
import com.github.diinnk.powly.controllers.castvote.{DBOps, GetRequest}
import com.github.diinnk.powly.db.PollDBHelper
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.{Inject, Singleton}


@Singleton
class CastVoteController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with PollDBHelper with GlobalWritesAndFormats {

  def castVote: Action[AnyContent] = Action { implicit r: Request[AnyContent] =>
    val request: CastVoteRequest = new GetRequest(
      r.body.asJson.get
    ).get(r.remoteAddress)
    val postValidationRequest = validateVote(request)
    val postDBOpsRequest: CastVoteRequest =
      if (postValidationRequest.passedValidation.getOrElse(false))
        DBOps.addVoteToDB(voteRequest = postValidationRequest)
      else postValidationRequest
    Ok(Json.toJson(postDBOpsRequest))
  }

  private def validateVote(request: CastVoteRequest): CastVoteRequest = {

    request.copy(passedValidation = Option(true))
  }
}
