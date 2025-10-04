package com.github.pjfanning.scala.duration.deser

import tools.jackson.core.JsonParser
import tools.jackson.databind.deser.{Deserializers, KeyDeserializers}
import tools.jackson.databind.deser.std.StdDeserializer
import tools.jackson.databind._
import tools.jackson.databind.`type`.SimpleType
import com.github.pjfanning.scala.duration.JacksonModule

import java.time.{Duration => JavaDuration}
import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.FiniteDuration
import scala.languageFeature.postfixOps

private object FiniteDurationDeserializerShared {
  private[deser] val JavaDurationClass = classOf[JavaDuration]
  private[deser] lazy val JavaDurationType = SimpleType.constructUnsafe(JavaDurationClass)
  private[deser] val FiniteDurationClass = classOf[FiniteDuration]
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
  override def findBeanDeserializer(javaType: JavaType, config: DeserializationConfig, beanDescRef: BeanDescription.Supplier): ValueDeserializer[FiniteDuration] =
    if (FiniteDurationDeserializerShared.FiniteDurationClass isAssignableFrom javaType.getRawClass)
      FiniteDurationDeserializer
    else None.orNull

  override def hasDeserializerFor(config: DeserializationConfig, valueType: Class[_]): Boolean =
    FiniteDurationDeserializerShared.FiniteDurationClass isAssignableFrom valueType
}

private object FiniteDurationKeyDeserializerResolver extends KeyDeserializers {
  override def findKeyDeserializer(javaType: JavaType, config: DeserializationConfig, beanDescRef: BeanDescription.Supplier): KeyDeserializer =
    if (FiniteDurationDeserializerShared.FiniteDurationClass isAssignableFrom javaType.getRawClass)
      FiniteDurationKeyDeserializer
    else None.orNull
}

trait DurationDeserializerModule extends JacksonModule {
  override def getModuleName: String = "DurationDeserializerModule"
  this += { _ addDeserializers FiniteDurationDeserializerResolver }
  this += { _ addKeyDeserializers FiniteDurationKeyDeserializerResolver }
}
