package com.github.diinnk.powly.controllers

import com.github.diinnk.powly.testhelpers.PowlyTestSetup
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class ViewPollControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with PowlyTestSetup {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new ViewPollController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to")
    }

    "render the index page from the application" in {
      val controller = inject[ViewPollController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to")
    }
  }
}
