package com.github.pjfanning.scala.duration.deser

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind._
import com.github.pjfanning.scala.duration.JacksonModule

import java.time.{Duration => JavaDuration}
import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.FiniteDuration
import scala.languageFeature.postfixOps

private object FiniteDurationDeserializer extends StdDeserializer[FiniteDuration](classOf[FiniteDuration] ) {
  private val JavaDurationClass = classOf[JavaDuration]

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): FiniteDuration = {
    Option(ctxt.readValue(p, JavaDurationClass)) match {
      case Some(duration) => duration.toScala
      case _ => None.orNull
    }
  }
}

private object FiniteDurationDeserializerResolver extends Deserializers.Base {
  private val FiniteDurationClass = classOf[FiniteDuration]

  override def findBeanDeserializer(javaType: JavaType, config: DeserializationConfig, beanDesc: BeanDescription): JsonDeserializer[FiniteDuration] =
    if (FiniteDurationClass isAssignableFrom javaType.getRawClass)
      FiniteDurationDeserializer
    else None.orNull
}

trait DurationDeserializerModule extends JacksonModule {
  this += { _ addDeserializers FiniteDurationDeserializerResolver }
}
