package com.github.diinnk.powly.controllers.castvote

import com.github.diinnk.powly.common.Constants.pollVotesDBTableName
import com.github.diinnk.powly.common.payloads.CastVoteRequest
import com.github.diinnk.powly.db.PollDBHelper

import java.sql.Connection
import scala.util.{Failure, Success, Try}

object DBOps extends PollDBHelper {
  def addVoteToDB(voteRequest: CastVoteRequest): CastVoteRequest = Try {
    implicit val h2Conn: Connection = getH2Conn
    autoClose {
      val uniqueIndividualIdentifierLabel = voteRequest.uniqueIndividualIdentifier match {
        case Some(u) => s"'$u'"
        case None => "null"
      }
      execH2Cmd(
        voteRequest.optionIds.
          map(v => s"($v, $uniqueIndividualIdentifierLabel, '${voteRequest.sourceIP}', CURRENT_TIMESTAMP)").
          mkString(
            s"insert into $pollVotesDBTableName (optionId, uniqueIndividualIdentifier, sourceIP, added) values ",
            ",",
            ";"
          )
      )
    }
  } match {
    case Success(_) => voteRequest.copy(successful = Option(true), message = Option("Cast vote DB insert(s) successful"))
    case Failure(e) =>
      log.error(s"Failed to successfully add the cast vote DB row(s) for $voteRequest\n${e.getMessage}")
      voteRequest.copy(successful = Option(false), message = Option(e.getMessage))
  }
}
