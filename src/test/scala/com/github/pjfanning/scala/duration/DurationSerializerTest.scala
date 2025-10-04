package com.github.pjfanning.scala.duration

import tools.jackson.databind.SerializationFeature
import tools.jackson.databind.exc.InvalidDefinitionException
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.scala.DefaultScalaModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationLong

class DurationSerializerTest extends AnyWordSpec with Matchers {
  private val week = DurationWrapper(7.days)
  private val second = DurationWrapper(1.second)
  "DurationModule" should {
    "serialize week (default)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .build()
      mapper.writeValueAsString(week) shouldEqual """{"duration":604800.000000000}"""
    }
    "serialize second (default)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .build()
      mapper.writeValueAsString(second) shouldEqual """{"duration":1.000000000}"""
    }
    "serialize week (nanos no effect)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .enable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .build()
      mapper.writeValueAsString(week) shouldEqual """{"duration":604800.000000000}"""
    }
    "serialize week (as period)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build()
      mapper.writeValueAsString(week) shouldEqual """{"duration":"PT168H"}"""
    }
    "serialize second (as period)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build()
      mapper.writeValueAsString(second) shouldEqual """{"duration":"PT1S"}"""
    }
    "serialize duration as map key" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build()
      val map = Map(second.duration -> "mapped")
      mapper.writeValueAsString(map) shouldEqual """{"PT1S":"mapped"}"""
    }
    "serialize week (without java time module)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .build()
      val idex = intercept[InvalidDefinitionException] {
        mapper.writeValueAsString(week)
      }
      idex.getMessage should startWith("Java 8 date/time type `java.time.Duration` not supported by default")
    }
  }

}
