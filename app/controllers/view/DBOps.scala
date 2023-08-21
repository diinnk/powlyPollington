package controllers.view

import common.Constants.{pollDBTableName, pollOptionsDBTableName, pollVotesDBTableName}
import common.Defaults
import config.ConfigManager.limitAllRowCount
import db.PollDBHelper
import payloads.{GetPollPayload, PollBasics, PollOptions, VoteDetail, VoteSummary}

import java.sql.{Connection, ResultSet, Timestamp}
import scala.annotation.tailrec

object DBOps extends PollDBHelper {
  def getPollPayload(pollID: Int): GetPollPayload = {
    if (pollID == 0)
      Defaults.getPollPayload.copy(message = Option("No polls have been created yet"))
    else {
      implicit val h2Conn: Connection = getH2Conn
      autoClose {
        val (found, pollTitle, pollDesc, added, allowMultipleSelections, allowMultipleIndividualVoteActions, uniqueIndividualIdentifierLabel) = {
          val pollDetailRS = getH2ResultSet(
            s"""select
               |  pollTitle,
               |  pollDesc,
               |  added,
               |  allowMultipleSelections,
               |  allowMultipleIndividualVoteActions,
               |  uniqueIndividualIdentifierLabel
               |from $pollDBTableName
               |where pollID = $pollID
               |;""".stripMargin
          )
          if (pollDetailRS.next()) {
            val uniqueIndividualIdentifierLabel = pollDetailRS.getString("uniqueIndividualIdentifierLabel")
            (true,
              pollDetailRS.getString("pollTitle"),
              pollDetailRS.getString("pollDesc"),
              pollDetailRS.getTimestamp("added"),
              pollDetailRS.getBoolean("allowMultipleSelections"),
              pollDetailRS.getBoolean("allowMultipleIndividualVoteActions"),
              if (uniqueIndividualIdentifierLabel == null) None else Option(uniqueIndividualIdentifierLabel)
            )
          } else
            (false, "Unknown poll title", "", new Timestamp(0), false, false, None)
        }

        val pollOptions =
          if (found)
            getPollOptionJsonString(getH2ResultSet(s"select optionName, optionId from $pollOptionsDBTableName where pollID = $pollID"))
          else List.empty

        val votSummaries = getVoteSummaries(pollID)
        val voteDetail = getVoteDetail(pollID)

        GetPollPayload(
          message = Option(s"$pollID ${if (found) "found" else "not found"}"),
          found = found,
          pollID = pollID,
          pollTitle = pollTitle,
          pollDesc = pollDesc,
          pollAdded = added,
          allowMultipleSelections = allowMultipleSelections,
          allowMultipleIndividualVoteActions = allowMultipleIndividualVoteActions,
          uniqueIndividualIdentifierLabel = uniqueIndividualIdentifierLabel,
          pollOptions = pollOptions,
          successful = None,
          voteSummaries = votSummaries,
          voteDetail = voteDetail
        )
      }
    }
  }

  @tailrec private def getPollOptionJsonString(optionsRS: ResultSet, optionsStringList: List[(String, Int)] = List.empty): List[PollOptions] =
    if (!optionsRS.next())
      optionsStringList.map { r => PollOptions(optionID = r._2, optionName = r._1) }
    else
      getPollOptionJsonString(optionsRS, optionsStringList :+ (optionsRS.getString("optionName"), optionsRS.getInt("optionID")))

  def getLatestPollID: Int = {
    implicit val h2Conn: Connection = getH2Conn
    autoClose {
      val rs = getH2ResultSet(s"""select max(pollId) as maxPollID from $pollDBTableName""")
      rs.next()
      rs.getInt("maxPollID")
    }
  }

  def getVoteSummaries(pollId: Int)(implicit h2Conn: Connection): List[VoteSummary] = {
    val rs = getH2ResultSet(
      s"""select
         |  a.optionId,
         |  count(*) as voteCount
         |from $pollVotesDBTableName a
         |       inner join $pollOptionsDBTableName b
         |         on  a.optionId = b.optionId
         |         and b.pollId = $pollId
         |group by a.optionId
         |;""".stripMargin
    )

    @tailrec def summaryLoop(voteSummaryList: List[VoteSummary] = List.empty): List[VoteSummary] = {
      if (!rs.next()) voteSummaryList
      else summaryLoop(voteSummaryList :+ VoteSummary(optionId = rs.getInt("optionId"), voteCount = rs.getInt("voteCount")))
    }
    summaryLoop().sortBy(- _.voteCount)
  }

  def getVoteDetail(pollId: Int)(implicit h2Conn: Connection): List[VoteDetail] = {
    val rs = getH2ResultSet(
      s"""select
         |  a.optionId,
         |  a.uniqueIndividualIdentifier,
         |  a.sourceIP,
         |  a.added as voteAdded
         |from $pollVotesDBTableName a
         |where exists (select 1 from $pollOptionsDBTableName z where a.optionId = z.optionId and z.pollId = $pollId)
         |;""".stripMargin)

    @tailrec def detailLoop(voteDetailList: List[VoteDetail] = List.empty): List[VoteDetail] = {
      if (!rs.next()) voteDetailList
      else {
        val uniqueIndividualIdentifier = rs.getString("uniqueIndividualIdentifier")
        detailLoop(voteDetailList :+ VoteDetail(
          optionId = rs.getInt("optionId"),
          uniqueIndividualIdentifier = if (uniqueIndividualIdentifier == null) None else Option(uniqueIndividualIdentifier),
          sourceIP = rs.getString("sourceIP"),
          voteAdded = rs.getTimestamp("voteAdded")
        ))
      }
    }
    detailLoop()
  }

  def getAll(currentPollID: Int): List[PollBasics] = {
    implicit val h2Conn: Connection = getH2Conn
    autoClose {
      val rs = getH2ResultSet(
        s"""select top $limitAllRowCount
           |   *
           |from (
           |  select top $limitAllRowCount
           |    pollId,
           |    pollTitle
           |  from $pollDBTableName
           |  union
           |  select top $limitAllRowCount
           |    pollId,
           |    pollTitle
           |  from $pollDBTableName a
           |  where a.pollId = $currentPollID
           |)
           |order by pollId desc
           |;""".stripMargin
      )

      @tailrec def pollRSLoop(pollBasicsList: List[PollBasics] = List.empty): List[PollBasics] =
        if (!rs.next()) pollBasicsList
        else pollRSLoop(pollBasicsList :+ PollBasics(
          pollId = rs.getInt("pollId"),
          pollTitle = rs.getString("pollTitle")
        ))

      pollRSLoop()
    }
  }
}
