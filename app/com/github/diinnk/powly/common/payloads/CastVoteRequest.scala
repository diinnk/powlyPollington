package com.github.diinnk.powly.common.payloads

case class CastVoteRequest(pollId: Int,
                           optionIds: List[Int],
                           uniqueIndividualIdentifier: Option[String],
                           sourceIP: String,
                           successful: Option[Boolean],
                           passedValidation: Option[Boolean],
                           message: Option[String]
                          )
