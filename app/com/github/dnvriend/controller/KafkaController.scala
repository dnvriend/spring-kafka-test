package com.github.dnvriend.controller

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.github.dnvriend.util.SpringFutureOps._
import com.google.inject.Inject
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class KafkaController @Inject()(template: KafkaTemplate[String, String], container: KafkaMessageListenerContainer[String, String])(implicit ec: ExecutionContext, mat: Materializer) extends Controller {

  def send(key: String, value: String) = Action.async {
    template.send("test", key, value).asScala
      .map { result =>
        val producerRecord = result.getProducerRecord
        val recordMetaData = result.getRecordMetadata
        Ok(s"$producerRecord, $recordMetaData")
      }
  }

  def putOnKafka = Action.async {
    Source.cycle(() => List("foo", "bar", "baz").iterator)
      .zipWithIndex
      .map { case (foo, index) => s"foo-$index" }
      .take(10000)
      .mapAsync(1)(msg => template.send("test", "1", msg).asScala)
      .runWith(Sink.ignore)
      .map(_ => Ok)
  }

  def stop = Action.async {
    Future.fromTry(Try(container.stop())).map(_ => Ok)
  }

  def start = Action.async {
    Future.fromTry(Try(container.start())).map(_ => Ok)
  }
}
