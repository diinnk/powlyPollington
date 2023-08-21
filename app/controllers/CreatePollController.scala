package controllers

import common.Constants.{pollDBTableName, pollOptionsDBTableName}
import common.{GetAttribute, GlobalWritesAndFormats}
import controllers.create.{DBOps, GetRequest}
import db.PollDBHelper
import db.PollDBHelper.execH2Cmd
import error.NoPollOptionsFoundException
import payloads.{CreatePollRequest, PollOptions}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}

import java.sql.Connection
import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}


@Singleton
class CreatePollController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with PollDBHelper with GlobalWritesAndFormats {

  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.create.index("222"))
  }

  def create: Action[AnyContent] = Action { implicit r: Request[AnyContent] =>
    val request: CreatePollRequest = new GetRequest(r.body.asJson.get).get
    Ok(Json.toJson(DBOps.addPollToDB(createRequest = request)))
  }
}
