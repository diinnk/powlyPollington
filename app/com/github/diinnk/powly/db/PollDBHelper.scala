package com.github.diinnk.powly.db

import com.github.diinnk.powly.common.Constants.{pollDBTableName, pollOptionsDBTableName, pollVotesDBTableName}
import com.github.diinnk.powly.common.H2Helper

import java.sql.Connection
import scala.util.{Failure, Success, Try}

trait PollDBHelper extends H2Helper {
  PollDBHelper.getClass
}

object PollDBHelper extends H2Helper {
  // stand-up the H2 database
  {
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

      }
    ) match {
      case Success(_) => log.info("Poll DB Stood-up")
      case Failure(e) =>
        log.error(s"poll DB failed to be stood up: ${e.getMessage}")
        e.printStackTrace()

    }
  }
}
