# jackson-module-scala-duration

This module needs to be used along with [jackson-datatype-jsr310](https://github.com/FasterXML/jackson-modules-java8/tree/2.14/datetime)
JavaTimeModule. What this module does is to convert Scala FiniteDurations into Java Time Durations and vice versa.

The serialization format defaults to [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601) format (eg 'P12H' for 12 hours).
The format can be changed using these jackson-datatype-jsr310 features:
* SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
* SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
* DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS

```scala
val jacksonVersion = "2.13.2"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion,
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

