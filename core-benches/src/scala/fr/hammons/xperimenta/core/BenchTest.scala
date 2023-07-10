package fr.hammons.xperimenta.core

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode.Throughput
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.OutputTimeUnit
import java.util.concurrent.TimeUnit
import scala.util.Random

@BenchmarkMode(Array(Throughput))
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class BenchTest {
  val keys = List.fill(2000)(Random.nextString(5))
  val values = List.fill(2000)(Random.nextInt())
  val map = keys.zip(values).toMap

  val pthash = PTHash(keys.zip(values))

  val key = keys.head

  @Benchmark
  def mapBench(blackhole: Blackhole) =
    blackhole.consume(map(key))

  @Benchmark
  def ptHashBench(blackhole: Blackhole) =
    blackhole.consume(pthash(key))
}
