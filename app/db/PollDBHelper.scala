package db

import common.Constants.{pollDBTableName, pollOptionsDBTableName, pollVotesDBTableName}
import common.H2Helper
import payloads.{CreatePollRequest, PollOptions}

import java.sql.Connection
import scala.util.{Failure, Success, Try}

trait PollDBHelper extends H2Helper {
  PollDBHelper.getClass
}

object PollDBHelper extends H2Helper {
  // stand-up the H2 database
  val pollDBStoodUp: Boolean = {
    implicit val standUpConn: Connection = getH2Conn
    Try(
      autoClose {
        execH2Cmd(
          s"""create table if not exists $pollDBTableName (
            |   pollId int auto_increment not null PRIMARY KEY,
            |   pollTitle varchar not null,
            |   pollDesc varchar not null,
            |   added datetime not null,
            |   allowMultipleSelections bool not null,
            |   allowMultipleIndividualVoteActions bool not null,
            |   uniqueIndividualIdentifierLabel varchar null
            |);""".stripMargin)
        execH2Cmd(
          s"""create table if not exists $pollOptionsDBTableName (
             |  optionId int auto_increment not null PRIMARY KEY,
             |  pollId int not null,
             |  optionName varchar not null
             |);""".stripMargin)
        execH2Cmd(
          s"""create table if not exists $pollVotesDBTableName (
             |  voteId int auto_increment not null PRIMARY KEY,
             |  optionId int not null,
             |  uniqueIndividualIdentifier varchar null,
             |  sourceIP varchar not null,
             |  added datetime not null
             |);""".stripMargin)

        //todo: remove me later
        controllers.create.DBOps.addPollToDB(CreatePollRequest(
          message = None,
          pollTitle = "Pick a beer",
          pollDesc = "Pick a new beer for one of our empty taps",
          allowMultipleSelections = false,
          allowMultipleIndividualVoteActions = true,
          uniqueIndividualIdentifierLabel = Option("Member Number"),
          pollOptions = List(
            PollOptions(1, "Pineapple GOAT"),
            PollOptions(1, "Vienna Red"),
            PollOptions(1, "Mexican Lager with Salt and Lime"),
            PollOptions(1, "Vanilla Tangerine Cream Ale"),
            PollOptions(1, "Luigi''s Salted Lime Ale"),
          ),
          successful = None
        ))

      }
    ) match {
      case Success(_) =>
        log.info("Poll DB Stood-up")
        true
      case Failure(e) =>
        log.error(s"poll DB failed to be stood up: ${e.getMessage}")
        e.printStackTrace()
        false
    }
  }
}
