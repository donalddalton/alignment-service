package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

class HomeContollerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "HomeController" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("Hello, GBW")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("Hello, GBW")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get
      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("Hello, GBW")
    }
  }
}
