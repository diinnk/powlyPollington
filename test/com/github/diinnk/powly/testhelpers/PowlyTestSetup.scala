package com.github.diinnk.powly.testhelpers

import com.github.diinnk.powly.common.payloads.{CreatePollRequest, PollOptions}
import com.github.diinnk.powly.controllers.create.DBOps
import com.github.diinnk.powly.controllers.view

trait PowlyTestSetup {
  PowlyTestSetup.getClass
}
object PowlyTestSetup {
  if (view.DBOps.getAll(0).isEmpty)
    DBOps.addPollToDB(CreatePollRequest(
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
