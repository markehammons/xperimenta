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

import java.util.{HashMap as JHashMap}

@BenchmarkMode(Array(Throughput))
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class BenchTest {
  val num = 10000
  val keys = List.fill(num)(Random.nextString(5))
  val values = List.fill(num)(Random.nextInt())
  val map = keys.zip(values).toMap

  val pthash: PTHash[String, Int] = PTHash(keys.zip(values))

  val jmap =
    val init = JHashMap[String, Int]()
    keys.zip(values).foreach((k, v) => init.put(k, v))
    init

  val index = Random.nextInt(num)
  val key = keys(index)
  val value = values(index)

  @Benchmark
  def mapBench(blackhole: Blackhole) =
    blackhole.consume(map(key))

  @Benchmark
  def ptHashBench(blackhole: Blackhole) =
    blackhole.consume(pthash(key))

  @Benchmark
  def javaMapBench(blackhole: Blackhole) =
    val result = jmap.get(key)
    blackhole.consume(result)
}
