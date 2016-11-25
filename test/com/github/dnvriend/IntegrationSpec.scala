package com.github.dnvriend

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.stream.Materializer
import akka.testkit.TestProbe
import akka.util.Timeout
import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.WsTestClient

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.reflect.ClassTag

class IntegrationSpec extends FlatSpec
  with Matchers
  with GivenWhenThen
  with OptionValues
  with TryValues
  with ScalaFutures
  with WsTestClient
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with Eventually
  with GuiceOneServerPerSuite {

  def getComponent[A: ClassTag]: A = app.injector.instanceOf[A]

  // set the port number of the HTTP server
  override lazy val port: Int = 9001
  implicit val pc: PatienceConfig = PatienceConfig(timeout = 30.seconds, interval = 300.millis)
  implicit val system: ActorSystem = getComponent[ActorSystem]
  implicit val ec: ExecutionContext = getComponent[ExecutionContext]
  implicit val mat: Materializer = getComponent[Materializer]
  implicit val timeout: Timeout = 10.seconds

  def killActors(actors: ActorRef*): Unit = {
    val tp = TestProbe()
    actors.foreach { (actor: ActorRef) =>
      tp watch actor
      actor ! PoisonPill
      tp.expectTerminated(actor)
    }
  }
}
