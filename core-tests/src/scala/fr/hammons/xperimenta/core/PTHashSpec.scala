package fr.hammons.xperimenta.core

import scala.util.Random
import org.scalacheck.Prop.*
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import java.util.UUID
import fr.hammons.xperimenta.core.PTHash.position

class PTHashSpec extends munit.ScalaCheckSuite:
  var diffPcts = Vector.empty[Double]
  var tolPcts = Vector.empty[Double]
  // override def afterAll(): Unit =
  //   println(s"max diff pct ${diffPcts.max}")
  //   println(s"min diff pct ${diffPcts.min}")
  //   println(s"avg diff pct ${diffPcts.sum / diffPcts.size}")
  //   val overcompensations = tolPcts.zip(diffPcts).map(_ - _)
  //   println(s"min overcompensation ${overcompensations.min}")
  //   println(s"max overcompensation ${overcompensations.max}")
  //   println(s"avg overcompensation ${overcompensations.sum / overcompensations.size}")

  // "mapping" should "distribute buckets correctly" in :
  //   forAll(Gen.infiniteLazyList(Gen.uuid), Gen.choose(100,10000)): (keyStream: LazyList[UUID], num: Int) =>
  //     val keys = keyStream.take(num).toList
  //     val x = PTHash.mapping(keys.toArray, PTHash.numBuckets(5,keys.size), Random.nextInt())

  //     // println(keys.size)
  //     // println(keys.size * .6)
  //     // println(x.map(_.size).sorted.reverse.take((x.size*.3).toInt).sum)

  //     val diff = math.abs(x.map(_.size).sorted.reverse.take((x.size * .3).toInt).sum - (keys.size * .6))

  //     val tolPct = Math.max(.4000001 - Math.max(.1 * Math.log10(keys.size) - .13999, 0), 0)
  //     val tolerance = keys.size * tolPct

  //     diffPcts :+= diff / keys.size
  //     tolPcts :+= tolPct
  //     if diff > tolerance then
  //       println(s"${keys.size} size")
  //       println(s"${diff} diff")
  //       println(s"${keys.size * .6} expected")
  //       println(s"${x.map(_.size).sorted.reverse.take((x.size*.3).toInt).sum} received")

  //     //assert(x.map(_.size).sorted.reverse.take((x.size*.3).toInt).sum, keys.size * .6, tolerance)

  // property("mapping distributes buckets correctly"):
  //   forAll(Gen.infiniteLazyList(Gen.uuid), Gen.choose(100,10000)): (keyStream: LazyList[UUID], num: Int) =>
  //     val keys = keyStream.take(num).toList
  //     val x = PTHash.mapping(keys.toArray, PTHash.numBuckets(5,keys.size), Random.nextInt())

  //     // println(keys.size)
  //     // println(keys.size * .6)
  //     // println(x.map(_.size).sorted.reverse.take((x.size*.3).toInt).sum)

  //     val diff = math.abs(x.map(_.size).sorted.reverse.take((x.size * .3).toInt).sum - (keys.size * .6))

  //     val tolPct = Math.max(.4000001 - Math.max(.1 * Math.log10(keys.size) - .13999, 0), 0)
  //     val tolerance = keys.size * tolPct

  //     diffPcts :+= diff / keys.size
  //     tolPcts :+= tolPct
  //     if diff > tolerance then
  //       println(s"${keys.size} size")
  //       println(s"${diff} diff")
  //       println(s"${keys.size * .6} expected")
  //       println(s"${x.map(_.size).sorted.reverse.take((x.size*.3).toInt).sum} received")

  //     assertEqualsDouble(x.map(_.size).sorted.reverse.take((x.size*.3).toInt).sum, keys.size * .6, tolerance)

  property(
    "position should return positions within the size of the original set"
  ):
    forAll(
      Arbitrary.arbitrary[UUID],
      Gen.choose(1, 2000),
      Arbitrary.arbitrary[Int],
      Arbitrary.arbitrary[Int]
    ): (key: UUID, num: Int, k: Int, s: Int) =>
      assert(
        PTHash.position(key, k, s, num) < num,
        PTHash.position(key, k, s, num)
      )
      assert(PTHash.position(key, k, s, num) >= 0)

  // property("position returns uniquish positions") {
  //   forAll(Gen.infiniteLazyList(Gen.uuid), Gen.choose(10,2000), Arbitrary.arbitrary[Int], Arbitrary.arbitrary[Int]): (keys: LazyList[UUID], num: Int, k: Int, s: Int) =>
  //     val counts = keys.take(num).map(position(_, k, s, num)).groupBy(identity).mapValues(_.size).toList
  //     assert(counts.forall(_._2 <= 10), counts)
  // }

  property("search should find keys that work to produce unique positions"):
    forAll(Gen.infiniteLazyList(Gen.uuid), Gen.choose(1, 3_000)):
      (keyStream: LazyList[UUID], num: Int) =>
        val keys = keyStream.take(num).zip(keyStream.take(num))

        println("keys done")
        if num <= 0 then
          println(s"num invalid $num")
          assert(true)
        else
          try {
            if keys.distinct.toList != keys.toList then assert(true)

            val n =
              if keys.size % 2 == 0 then keys.size + 1
              else keys.size
            val m = PTHash.numBuckets(5, n)
            println(s"$m buckets")
            val s = Random.nextInt

            val buckets = PTHash.mapping(keys.toArray, m, s, n)
            println("buckets done")

            val primes = PTHash.search(buckets, s, n)

            val missing = buckets
              .zip(primes)
              .flatMap((bucket, prime) =>
                bucket.map(PTHash.position(_, prime, s, n))
              )
              .distinct
              .sorted
              .diff(0 until n)
              .toList
            if missing.size > 1 then
              println("fail")
              println(missing)
              println(missing.size <= 1)
              println(keys)
              println(
                buckets
                  .zip(primes)
                  .flatMap((bucket, prime) =>
                    bucket.map(k => PTHash.position(k, prime, s, n) -> k)
                  )
                  .sortBy((pos, _) => pos)
                  .toList
              )
              println(num)
              println(n)

            println(missing)
            assert(
              missing.size <= 1,
              missing
            )
          } catch {
            case e =>
              e.printStackTrace()
              throw e
          }
