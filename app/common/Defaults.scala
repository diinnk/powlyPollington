package common

import payloads.{GetPollPayload, PollOptions, VoteSummary}

import java.sql.Timestamp

object Defaults {
  val getPollPayload: GetPollPayload = GetPollPayload(
    message = None,
    found = false,
    pollID = 0,
    pollTitle = "",
    pollDesc = "",
    pollOptions = List.empty,
    pollAdded = new Timestamp(0),
    allowMultipleSelections = false,
    allowMultipleIndividualVoteActions = false,
    uniqueIndividualIdentifierLabel = None,
    successful = None,
    voteSummaries = List.empty,
    voteDetail = List.empty
  )

  val voteSummary: VoteSummary = VoteSummary(0,0)

  val pollOptions: PollOptions = PollOptions(optionID = 0, "")

}
