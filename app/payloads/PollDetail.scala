package payloads

import java.sql.Timestamp

trait PollDetail {
  def message: Option[String]
  def pollTitle: String
  def pollDesc: String
  def allowMultipleSelections: Boolean
  def allowMultipleIndividualVoteActions: Boolean
  def uniqueIndividualIdentifierLabel: Option[String]
  def pollOptions: List[PollOptions]
  def successful: Option[Boolean]
}

case class GetPollPayload(message: Option[String],
                          found: Boolean,
                          pollID: Int,
                          pollTitle: String,
                          pollDesc: String,
                          pollAdded: Timestamp,
                          allowMultipleSelections: Boolean,
                          allowMultipleIndividualVoteActions: Boolean,
                          uniqueIndividualIdentifierLabel: Option[String],
                          pollOptions: List[PollOptions],
                          successful: Option[Boolean],
                          voteSummaries: List[VoteSummary],
                          voteDetail: List[VoteDetail]
                         ) extends PollDetail

case class CreatePollRequest(message: Option[String],
                             pollTitle: String,
                             pollDesc: String,
                             allowMultipleSelections: Boolean,
                             allowMultipleIndividualVoteActions: Boolean,
                             uniqueIndividualIdentifierLabel: Option[String],
                             pollOptions: List[PollOptions],
                             successful: Option[Boolean]
                            ) extends PollDetail
