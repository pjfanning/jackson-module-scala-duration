package com.github.pjfanning.scala.duration.ser

import com.fasterxml.jackson.annotation.JsonFormat
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.ser.Serializers
import tools.jackson.databind._
import com.github.pjfanning.scala.duration.JacksonModule

import java.time.Duration
import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.FiniteDuration
import scala.languageFeature.postfixOps

private object FiniteDurationSerializerShared {
  private[ser] val FiniteDurationClass = classOf[FiniteDuration]
}

private object FiniteDurationSerializer extends ValueSerializer[FiniteDuration] {
  def serialize(value: FiniteDuration, jgen: JsonGenerator, provider: SerializationContext): Unit = {
    provider.writeValue(jgen, value.toJava)
  }
}

private object FiniteDurationKeySerializer extends ValueSerializer[FiniteDuration] {
  private val JavaDurationClass = classOf[Duration]

  def serialize(value: FiniteDuration, jgen: JsonGenerator, provider: SerializationContext): Unit = {
    val keySerializer = provider.findKeySerializer(JavaDurationClass, null)
    keySerializer.serialize(value.toJava, jgen, provider)
  }
}

private object FiniteDurationSerializerResolver extends Serializers.Base {
  override def findSerializer(config: SerializationConfig, javaType: JavaType,
                              beanDescRef: BeanDescription.Supplier, formatOverrides: JsonFormat.Value): ValueSerializer[FiniteDuration] =
    if (FiniteDurationSerializerShared.FiniteDurationClass.isAssignableFrom(javaType.getRawClass))
      FiniteDurationSerializer
    else None.orNull
}

private object FiniteDurationKeySerializerResolver extends Serializers.Base {
  override def findSerializer(config: SerializationConfig, javaType: JavaType,
                              beanDescRef: BeanDescription.Supplier, formatOverrides: JsonFormat.Value): ValueSerializer[FiniteDuration] =
    if (FiniteDurationSerializerShared.FiniteDurationClass.isAssignableFrom(javaType.getRawClass))
      FiniteDurationKeySerializer
    else None.orNull
}

trait DurationSerializerModule extends JacksonModule {
  override def getModuleName: String = "DurationSerializerModule"
  this += { _ addSerializers FiniteDurationSerializerResolver }
  this += { _ addKeySerializers FiniteDurationKeySerializerResolver }
}
