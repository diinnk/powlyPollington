package common

import payloads.{CastVoteRequest, CreatePollRequest, GetPollPayload, PollBasics, PollOptions, VoteDetail, VoteSummary}
import play.api.libs.json.{Format, JsResult, JsValue, Json, OFormat, OWrites}

import java.sql.Timestamp

trait GlobalWritesAndFormats {
  private def timestampToLong(t: Timestamp): Long = t.getTime
  private def longToTimestamp(dt: Long): Timestamp = new Timestamp(dt)

  implicit protected val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = Json.toJson(timestampToLong(t))
    def reads(json: JsValue): JsResult[Timestamp] = Json.fromJson[Long](json).map(longToTimestamp)
  }

  implicit val pollOptionsWrites: OWrites[PollOptions] = Json.writes[PollOptions]
  implicit val voteSummaryWrites: OWrites[VoteSummary] = Json.writes[VoteSummary]
  implicit val voteDetailWrites: OWrites[VoteDetail] = Json.writes[VoteDetail]
  implicit val getPollPayloadWrites: OWrites[GetPollPayload] = Json.writes[GetPollPayload]
  implicit val createPollRequestWrites: OWrites[CreatePollRequest] = Json.writes[CreatePollRequest]
  implicit val castVoteRequestWrite: OWrites[CastVoteRequest] = Json.writes[CastVoteRequest]
  implicit val pollBasicsWrite: OWrites[PollBasics] = Json.writes[PollBasics]

  implicit val pollOptionsFormats: OFormat[PollOptions] = Json.format[PollOptions]
  implicit val voteSummaryFormats: OFormat[VoteSummary] = Json.format[VoteSummary]
  implicit val voteDetailFormats: OFormat[VoteDetail] = Json.format[VoteDetail]
  implicit val getPollPayloadFormats: OFormat[GetPollPayload] = Json.format[GetPollPayload]
  implicit val createPollRequestFormats: OFormat[CreatePollRequest] = Json.format[CreatePollRequest]
  implicit val castVoteRequestFormats: OFormat[CastVoteRequest] = Json.format[CastVoteRequest]
  implicit val pollBasicsFormats: OFormat[PollBasics] = Json.format[PollBasics]
}
