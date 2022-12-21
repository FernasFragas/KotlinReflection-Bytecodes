package pt.isel

import org.openjdk.jmh.annotations.*
import pt.isel.sample.Age
import pt.isel.sample.Student
import pt.isel.sample.Date
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@Fork(value = 1, warmups = 1)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class JsonParserBenchmarckKotlin {
    companion object {
        private const val student = "{name: \"Ze Manel\", nr: 7353}"
        private const val date = "{ year: 2000, month: 1, day: 15}"
        private const val age = "{ student: $student, birth: $date}"
    }
    //Object with only primitive properties
    @Benchmark
    fun parseInReflectDate():Date{
        return JsonParserReflect.parse(date, Date::class) as Date
    }

    @Benchmark
    fun parseInDynamicDate():Date{
        return JsonParserDynamic.parse(date, Date::class) as Date
    }

    //Object without primitive properties
    @Benchmark
    fun parseInReflectAge(): Age {
        return JsonParserDynamic.parse(age, Age::class) as Age
    }

    @Benchmark
    fun parseInDynamicAge():Age{
        return JsonParserDynamic.parse(age, Age::class) as Age
    }

    //Object with primitive and non primitives properties
    @Benchmark
    fun  parseInReflectStudent():Student {
        return JsonParserReflect.parse(student, Student::class) as Student
    }

    @Benchmark
    fun parseInDynamicStudent():Student{
        return JsonParserDynamic.parse(student, Student::class) as Student
    }

}