package com.github.pjfanning.scala.duration.ser

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.scala.duration.{DurationModule, DurationWrapper}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationLong

class DurationSerializerTest extends AnyWordSpec with Matchers {
  private val week = DurationWrapper(7.days)
  "DurationModule" should {
    "serialize week (default)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .addModule(new JavaTimeModule)
        .build()
      mapper.writeValueAsString(week) shouldEqual (s"""{"duration":604800.000000000}""")
    }
    "serialize week (as period)" in {
      val mapper = JsonMapper.builder()
        .addModule(DefaultScalaModule)
        .addModule(DurationModule)
        .addModule(new JavaTimeModule)
        .enable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .build()
      mapper.writeValueAsString(week) shouldEqual (s"""{"duration":604800.000000000}""")
    }
  }

}
