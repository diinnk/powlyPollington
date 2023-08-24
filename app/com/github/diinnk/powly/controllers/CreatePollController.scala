package com.github.diinnk.powly.controllers

import com.github.diinnk.powly.common.GlobalWritesAndFormats
import com.github.diinnk.powly.common.payloads.CreatePollRequest
import com.github.diinnk.powly.controllers.create.{DBOps, GetRequest}
import com.github.diinnk.powly.db.PollDBHelper
import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.{Inject, Singleton}


@Singleton
class CreatePollController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with PollDBHelper with GlobalWritesAndFormats {

  def index: Action[AnyContent] = Action {
    Ok(views.html.create.index())
  }

  def create: Action[AnyContent] = Action { implicit r: Request[AnyContent] =>
    val request: CreatePollRequest = new GetRequest(r.body.asJson.get).get
    Ok(Json.toJson(DBOps.addPollToDB(createRequest = request)))
  }
}
