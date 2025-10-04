package com.github.pjfanning.scala.duration

import tools.jackson.core.`type`.TypeReference
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.scala.DefaultScalaModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import tools.jackson.databind.cfg.DateTimeFeature

import scala.concurrent.duration.{DurationLong, FiniteDuration}

class DurationDeserializerTest extends AnyWordSpec with Matchers {
  private val week = DurationWrapper(7.days)
  "DurationModule" should {
    "deserialize week (default)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
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
        .enable(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .build()
      val str = """{"duration":604800.0000}"""
      mapper.readValue(str, classOf[DurationWrapper]) shouldEqual week
    }
    "deserialize week (as period)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
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
        .disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build()
      val json = """{"PT1S":"mapped"}"""
      val map = mapper.readValue(json, new TypeReference[Map[FiniteDuration, String]] {})
      map should have size 1
      map(1.second) shouldEqual "mapped"
    }
  }

}
