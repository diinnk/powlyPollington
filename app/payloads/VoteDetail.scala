package payloads

import java.sql.Timestamp

case class VoteDetail(optionId: Int,
                      uniqueIndividualIdentifier: Option[String],
                      sourceIP: String,
                      voteAdded: Timestamp
                     )
