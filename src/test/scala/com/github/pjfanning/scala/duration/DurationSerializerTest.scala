package com.github.pjfanning.scala.duration

import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.scala.DefaultScalaModule
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import tools.jackson.databind.cfg.DateTimeFeature

import scala.concurrent.duration.DurationLong

class DurationSerializerTest extends AnyWordSpec with Matchers {
  private val week = DurationWrapper(7.days)
  private val second = DurationWrapper(1.second)
  "DurationModule" should {
    "serialize week (WRITE_DURATIONS_AS_TIMESTAMPS enabled)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build()
      mapper.writeValueAsString(week) shouldEqual """{"duration":604800.000000000}"""
    }
    "serialize second (WRITE_DURATIONS_AS_TIMESTAMPS enabled)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build()
      mapper.writeValueAsString(second) shouldEqual """{"duration":1.000000000}"""
    }
    "serialize week (nanos no effect)" in {
      val mapper = JsonMapper.builderWithJackson2Defaults()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .build()
      mapper.writeValueAsString(week) shouldEqual """{"duration":604800.000000000}"""
    }
    "serialize week (as period)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .build()
      mapper.writeValueAsString(week) shouldEqual """{"duration":"PT168H"}"""
    }
    "serialize second (as period)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .build()
      mapper.writeValueAsString(second) shouldEqual """{"duration":"PT1S"}"""
    }
    "serialize duration as map key" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .build()
      val map = Map(second.duration -> "mapped")
      mapper.writeValueAsString(map) shouldEqual """{"PT1S":"mapped"}"""
    }
  }

}
