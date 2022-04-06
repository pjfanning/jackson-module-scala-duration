# jackson-module-scala-duration

This module needs to be used along with [jackson-datatype-jsr310](https://github.com/FasterXML/jackson-modules-java8/tree/2.14/datetime)
JavaTimeModule. What this module does is to convert Scala [FiniteDurations](https://www.scala-lang.org/api/2.13.8/scala/concurrent/duration/FiniteDuration.html)
into Java Time [Durations](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html) and vice versa.
JavaTimeModule then does the rest.

If you need to support Scala classes generally, you will also need to add [jackson-module-scala](https://github.com/FasterXML/jackson-module-scala).

If you don't use this module, Scala FiniteDurations can still be serialized but they will appear in a format like:

```
{"length":7,"unit":"DAYS","finite":true}
```

The format seems to differ depending on Scala version (because the internals of the FiniteDuration class can change
from release to release). Deserialization seems to be problematic regardless of Scala version.

When this module is used, the serialization format defaults to writing durations as numbers.
The format can be changed by enabling/disabling these jackson-datatype-jsr310 features:
* SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
  * if you disable this, the format used is [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601) format (eg 'PT12H' for 12 hours) 
* SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS appears to have no effect on durations
* DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS appears to have no effect on durations

```scala
val jacksonVersion = "2.13.2"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.github.pjfanning" %% "jackson-module-scala-duration" % jacksonVersion
)
```

```scala
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.pjfanning.scala.duration.DurationModule

val mapper = JsonMapper.builder()
    .addModule(new JavaTimeModule)
    .addModule(DefaultScalaModule)
    .addModule(DurationModule)
    .build()
```

