package com.github.pjfanning.scala.duration

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, SerializationFeature}
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.{DurationLong, FiniteDuration}

class DurationDeserializerTest extends AnyWordSpec with Matchers {
  private val week = DurationWrapper(7.days)
  "DurationModule" should {
    "deserialize week (default)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .addModule(new JavaTimeModule)
        .build()
      val str = """{"duration":604800}"""
      mapper.readValue(str, classOf[DurationWrapper]) shouldEqual week
      val str2 = """{"duration":604800.0000}"""
      mapper.readValue(str2, classOf[DurationWrapper]) shouldEqual week
    }
    "deserialize week (nanos no effect)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .addModule(new JavaTimeModule)
        .enable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .build()
      val str = """{"duration":604800.0000}"""
      mapper.readValue(str, classOf[DurationWrapper]) shouldEqual week
    }
    "deserialize week (as period)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .addModule(new JavaTimeModule)
        .build()
      val str = """{"duration":"PT168H"}"""
      mapper.readValue(str, classOf[DurationWrapper]) shouldEqual week
      val str2 = """{"duration":"P7D"}"""
      mapper.readValue(str2, classOf[DurationWrapper]) shouldEqual week
    }
    "serialize duration as map key" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .addModule(new JavaTimeModule)
        .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build()
      val json = """{"PT1S":"mapped"}"""
      val map = mapper.readValue(json, new TypeReference[Map[FiniteDuration, String]] {})
      map should have size 1
      map(1.second) shouldEqual "mapped"
    }
  }

}
