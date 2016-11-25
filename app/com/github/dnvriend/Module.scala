package com.github.dnvriend

import com.google.inject.{AbstractModule, Provides}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.springframework.kafka.core.{DefaultKafkaConsumerFactory, DefaultKafkaProducerFactory, KafkaTemplate}
import org.springframework.kafka.listener.config.ContainerProperties
import org.springframework.kafka.listener.{KafkaMessageListenerContainer, MessageListener}

import scala.collection.JavaConversions._

class Module extends AbstractModule {
  protected def configure(): Unit = {
  }

  @Provides
  def createProducerTemplate: KafkaTemplate[String, String] = {
    val senderProps: java.util.Map[String, Any] = Map(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ProducerConfig.RETRIES_CONFIG -> 0,
      ProducerConfig.BATCH_SIZE_CONFIG -> 16384,
      ProducerConfig.LINGER_MS_CONFIG -> 1,
      ProducerConfig.BUFFER_MEMORY_CONFIG -> 33554432,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer],
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer]
    )
    val producerFactory = new DefaultKafkaProducerFactory[String, String](senderProps.mapValues(_.asInstanceOf[AnyRef]))
    new KafkaTemplate[String, String](producerFactory)
  }

  @Provides
  def createKafkaMessageListenerContainer(messageListener: MessageListener[String, String]): KafkaMessageListenerContainer[String, String] = {
    val consumerProps: java.util.Map[String, Any] = Map(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ConsumerConfig.GROUP_ID_CONFIG -> "group",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> true,
      ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG -> "100",
      ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG -> "15000",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
    )

    val containerProperties = new ContainerProperties("test")
    containerProperties.setMessageListener(messageListener)

    val consumerFactory = new DefaultKafkaConsumerFactory[String, String](consumerProps.mapValues(_.asInstanceOf[AnyRef]))
    val container = new KafkaMessageListenerContainer[String, String](consumerFactory, containerProperties)
    container.setBeanName("testAuto")
    container.start()
    container
  }

  @Provides
  def messageListener: MessageListener[String, String] = new MessageListener[String, String] {
    override def onMessage(message: ConsumerRecord[String, String]): Unit = {
      println(s"received: $message")
    }
  }
}