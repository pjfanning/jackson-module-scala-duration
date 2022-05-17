package com.github.pjfanning.scala.duration.deser

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.deser.{Deserializers, KeyDeserializers}
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.`type`.SimpleType
import com.github.pjfanning.scala.duration.JacksonModule

import java.time.{Duration => JavaDuration}
import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.FiniteDuration
import scala.languageFeature.postfixOps

private object FiniteDurationDeserializerShared {
  val JavaDurationClass = classOf[JavaDuration]
  lazy val JavaDurationType = SimpleType.constructUnsafe(JavaDurationClass)
  val FiniteDurationClass = classOf[FiniteDuration]
}

private object FiniteDurationDeserializer extends StdDeserializer[FiniteDuration](classOf[FiniteDuration]) {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): FiniteDuration = {
    Option(ctxt.readValue(p, FiniteDurationDeserializerShared.JavaDurationClass)) match {
      case Some(duration) => duration.toScala
      case _ => None.orNull
    }
  }
}

private object FiniteDurationKeyDeserializer extends KeyDeserializer {
  override def deserializeKey(key: String, ctxt: DeserializationContext): AnyRef = {
    val keyDeserializer = ctxt.findKeyDeserializer(FiniteDurationDeserializerShared.JavaDurationType, null)
    keyDeserializer.deserializeKey(key, ctxt).asInstanceOf[JavaDuration].toScala
  }
}

private object FiniteDurationDeserializerResolver extends Deserializers.Base {
  override def findBeanDeserializer(javaType: JavaType, config: DeserializationConfig, beanDesc: BeanDescription): JsonDeserializer[FiniteDuration] =
    if (FiniteDurationDeserializerShared.FiniteDurationClass isAssignableFrom javaType.getRawClass)
      FiniteDurationDeserializer
    else None.orNull
}

private object FiniteDurationKeyDeserializerResolver extends KeyDeserializers {
  override def findKeyDeserializer(javaType: JavaType, config: DeserializationConfig, beanDesc: BeanDescription): KeyDeserializer =
    if (FiniteDurationDeserializerShared.FiniteDurationClass isAssignableFrom javaType.getRawClass)
      FiniteDurationKeyDeserializer
    else None.orNull
}

trait DurationDeserializerModule extends JacksonModule {
  this += { _ addDeserializers FiniteDurationDeserializerResolver }
  this += { _ addKeyDeserializers FiniteDurationKeyDeserializerResolver }
}
