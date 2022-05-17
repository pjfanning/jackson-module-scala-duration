package com.github.pjfanning.scala.duration.ser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ser.Serializers
import com.fasterxml.jackson.databind._
import com.github.pjfanning.scala.duration.JacksonModule

import java.time.Duration
import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.FiniteDuration
import scala.languageFeature.postfixOps

private object FiniteDurationSerializerShared {
  val FiniteDurationClass = classOf[FiniteDuration]
}

private object FiniteDurationSerializer extends JsonSerializer[FiniteDuration] {
  def serialize(value: FiniteDuration, jgen: JsonGenerator, provider: SerializerProvider): Unit = {
    provider.defaultSerializeValue(value.toJava, jgen)
  }
}

private object FiniteDurationKeySerializer extends JsonSerializer[FiniteDuration] {
  private val JavaDurationClass = classOf[Duration]

  def serialize(value: FiniteDuration, jgen: JsonGenerator, provider: SerializerProvider): Unit = {
    val keySerializer = provider.findKeySerializer(JavaDurationClass, null)
    keySerializer.serialize(value.toJava, jgen, provider)
  }
}

private object FiniteDurationSerializerResolver extends Serializers.Base {
  override def findSerializer(config: SerializationConfig, javaType: JavaType, beanDesc: BeanDescription): JsonSerializer[FiniteDuration] =
    if (FiniteDurationSerializerShared.FiniteDurationClass.isAssignableFrom(javaType.getRawClass))
      FiniteDurationSerializer
    else None.orNull
}

private object FiniteDurationKeySerializerResolver extends Serializers.Base {
  override def findSerializer(config: SerializationConfig, javaType: JavaType, beanDesc: BeanDescription): JsonSerializer[FiniteDuration] =
    if (FiniteDurationSerializerShared.FiniteDurationClass.isAssignableFrom(javaType.getRawClass))
      FiniteDurationKeySerializer
    else None.orNull
}

trait DurationSerializerModule extends JacksonModule {
  this += { _ addSerializers FiniteDurationSerializerResolver }
  this += { _ addKeySerializers FiniteDurationKeySerializerResolver }
}
