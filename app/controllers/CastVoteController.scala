package controllers

import common.GlobalWritesAndFormats
import controllers.castvote.{DBOps, GetRequest}
import db.PollDBHelper
import payloads.CastVoteRequest
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

  def validateVote(request: CastVoteRequest): CastVoteRequest = {

    request.copy(passedValidation = Option(true))
  }
}
