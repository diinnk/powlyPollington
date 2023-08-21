package common

import payloads.GetPollPayload

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

}
