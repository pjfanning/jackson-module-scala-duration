package com.github.pjfanning.scala.duration.ser

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ser.Serializers
import com.fasterxml.jackson.databind._
import com.github.pjfanning.scala.duration.JacksonModule

import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.FiniteDuration
import scala.languageFeature.postfixOps

private object FiniteDurationSerializer extends JsonSerializer[FiniteDuration] {
  def serialize(value: FiniteDuration, jgen: JsonGenerator, provider: SerializerProvider): Unit = {
    provider.defaultSerializeValue(value.toJava, jgen)
  }
}

private object FiniteDurationSerializerResolver extends Serializers.Base {
  private val FiniteDurationClass = classOf[FiniteDuration]

  override def findSerializer(config: SerializationConfig, javaType: JavaType, beanDesc: BeanDescription): JsonSerializer[FiniteDuration] =
    if (FiniteDurationClass.isAssignableFrom(javaType.getRawClass))
      FiniteDurationSerializer
    else None.orNull
}

trait DurationSerializerModule extends JacksonModule {
  this += { _ addSerializers FiniteDurationSerializerResolver }
}
