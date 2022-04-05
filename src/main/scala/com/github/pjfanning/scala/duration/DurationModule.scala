package com.github.pjfanning.scala.duration

import com.github.pjfanning.scala.duration.deser.DurationDeserializerModule
import com.github.pjfanning.scala.duration.ser.DurationSerializerModule

class DurationModule extends DurationSerializerModule with DurationDeserializerModule

object DurationModule extends DurationModule