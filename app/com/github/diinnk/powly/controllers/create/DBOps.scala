package com.github.diinnk.powly.controllers.create

import com.github.diinnk.powly.common.Constants.{pollDBTableName, pollOptionsDBTableName}
import com.github.diinnk.powly.common.payloads.CreatePollRequest
import com.github.diinnk.powly.db.PollDBHelper
import com.github.diinnk.powly.error.NoPollOptionsFoundException

import java.sql.Connection
import scala.util.{Failure, Success, Try}

object DBOps extends PollDBHelper {
  def addPollToDB(createRequest: CreatePollRequest): CreatePollRequest = Try {
    if (createRequest.pollOptions.nonEmpty) {
      implicit val h2Conn: Connection = getH2Conn
      autoClose {
        val uniqueIndividualIdentifierLabel = createRequest.uniqueIndividualIdentifierLabel match {
          case Some(u) => s"'$u'"
          case None => "null"
        }
        val newPollID = execH2Cmd(
          s"""insert into $pollDBTableName (pollTitle, pollDesc, added, allowMultipleSelections, allowMultipleIndividualVoteActions, uniqueIndividualIdentifierLabel) values (
             | '${createRequest.pollTitle}',
             | '${createRequest.pollDesc}',
             | CURRENT_TIMESTAMP,
             | ${createRequest.allowMultipleSelections},
             | ${createRequest.allowMultipleIndividualVoteActions},
             | $uniqueIndividualIdentifierLabel
             | );""".stripMargin, returnID = true)
        execH2Cmd(
          createRequest.pollOptions.map(r => s"($newPollID, '${r.optionName}')").
            mkString(
              s"insert into $pollOptionsDBTableName (pollID, optionName) values",
              ",",
              ";"
            )
        )
        newPollID
      }
    } else throw NoPollOptionsFoundException(s"No poll options provided for new poll titled '${createRequest.pollTitle}")
  } match {
    case Success(s) => createRequest.copy(successful = Option(true), message = Option("DB Inserts successful"), createdID = Option(s))
    case Failure(e) =>
      log.error(s"Failed to successfully add the DB rows for $createRequest\n${e.getMessage}")
      createRequest.copy(successful = Option(false), message = Option(e.getMessage))
  }
}
