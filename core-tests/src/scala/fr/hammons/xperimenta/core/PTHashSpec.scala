package fr.hammons.xperimenta.core

import scala.util.Random
import org.scalacheck.Prop.*
import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import java.util.UUID

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
      val hash = MurmurHash.hash32(key.##, s)
      val hashK = MurmurHash.hash32(k, s)
      assert(
        PTHash.optimizedPosition(hash, hashK, num) < num,
        PTHash.optimizedPosition(hash, hashK, num)
      )
      assert(PTHash.optimizedPosition(hash, hashK, num) >= 0)

  // property("position returns uniquish positions") {
  //   forAll(Gen.infiniteLazyList(Gen.uuid), Gen.choose(10,2000), Arbitrary.arbitrary[Int], Arbitrary.arbitrary[Int]): (keys: LazyList[UUID], num: Int, k: Int, s: Int) =>
  //     val counts = keys.take(num).map(position(_, k, s, num)).groupBy(identity).mapValues(_.size).toList
  //     assert(counts.forall(_._2 <= 10), counts)
  // }

  property("PTHash should store keys and values properly"):
    forAll(
      Gen.infiniteLazyList(Arbitrary.arbitrary[UUID]),
      Gen.infiniteLazyList(Arbitrary.arbitrary[UUID]),
      Gen.choose(1, 25)
    ): (keys, values, num) =>
        println("starting")
        val ks = keys.take(num).toList
        val list = ks.zip(values).take(num).toList
        val map = list.toMap
        println("starting construction")
        println(s"$ks, $list, $map")
        if num == 0 then 
          fail("cannot work on 0")
        val ptHash = PTHash(list)

        println("pthash constructed")

        ks.foreach(k => 
          println(ptHash(k))
          assertEquals(ptHash(k), map(k))
        )
        println("assertions complete")

  test("PTHash should store keys and values properly specific 1"):
    val list = List(("d2b13df8-1619-4e8a-b4f5-c0b83330cc3e","f9d3cde3-c94e-4789-af4b-890e9eac1686"), ("84f5c0b8-3330-4c3e-84a2-a9e17861cb23","7f4b890e-9eac-4686-b67a-e210a2d981ce"), ("e4a2a9e1-7861-4b23-b3ad-51e5a2cf3b8c","567ae210-a2d9-41ce-bf55-031d8d7bebff"), ("e3ad51e5-a2cf-4b8c-9252-3a6360bb65bf","8f55031d-8d7b-4bff-a310-6f9539542d1b"), ("b2523a63-60bb-45bf-adf4-2ca57678b681","c3106f95-3954-4d1b-bd26-c0d538953082"), ("5df42ca5-7678-4681-a37b-c097ed76669a","ad26c0d5-3895-4082-b442-0e724c0e991f"), ("637bc097-ed76-469a-9436-cbf73f338fc2","b4420e72-4c0e-491f-8450-61ad606ac5eb"), ("a436cbf7-3f33-4fc2-bcef-a151f3a9b68d","345061ad-606a-45eb-a631-251bd5dafd64"), ("1cefa151-f3a9-468d-b4a2-e157c2f14f6f","2631251b-d5da-4d64-83ae-23a398b239f2"), ("a4a2e157-c2f1-4f6f-bf43-a4ce820ce73f","53ae23a3-98b2-49f2-bda8-bc6dbbf3caa4")).map((k,v) => UUID.fromString(k) -> UUID.fromString(v))

    val map = list.toMap
    val ptHash = PTHash(list)
    val keys = list.map(_._1)

    keys.foreach: k =>
      assertEquals(ptHash(k), map(k))