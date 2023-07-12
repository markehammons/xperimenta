package fr.hammons.xperimenta.core

import scala.util.Random
import scala.collection.immutable.BitSet
import scala.util.hashing.MurmurHash3
import fr.hammons.xperimenta.core.PTHash.bucket
import scala.reflect.ClassTag

class PTHash[K, V](
    seed: Int,
    primes: Array[Int],
    table: Array[V],
    p1: Int,
    p2: Int,
    m: Int
):
  def apply(a: K): V = table(
    PTHash.position(
      a,
      primes(PTHash.bucket(a, seed, p1, p2, table.length, m)),
      seed,
      table.length
    )
  )

object PTHash:

  def apply[K, V, U <: Iterable[(K,V)]](keysAndValues: U)(using
      ClassTag[V]
  ): PTHash[K, V] =
    val n =
      if keysAndValues.size % 2 == 0 then keysAndValues.size + 1
      else keysAndValues.size

    val m = PTHash.numBuckets(5, n)
    val p1 = (0.6 * n).toInt
    val p2 = (0.3 * m).toInt

    val s = Random.nextInt()
    val buckets = PTHash.mapping(keysAndValues.toArray, m, s, n)
    val pilots = PTHash.search(buckets, s, n)

    val table = Array.ofDim[V](n)

    println(keysAndValues)
    keysAndValues.foreach((k, v) =>
      table(PTHash.position(k, pilots(PTHash.bucket(k, s, p1, p2, n, m)), s, n)) = v
    )

    println(table.mkString(","))

    new PTHash(s, pilots, table, p1, p2, m)

  def numBuckets(c: Int, elements: Int): Int =
    java.lang.Math
      .ceil(
        c * elements / ((java.lang.Math.log(elements) / java.lang.Math
          .log(2)) + 1)
      )
      .toInt

  def bucket[A](element: A, s1: Int, p1: Int, p2: Int, n: Int, m: Int): Int =
    if isSet1(element, s1, p1, n) then
      Math.floorMod(MurmurHash.hash32(element.##, s1), p2)
    else
      val bucketPos =
        p2 + Math.floorMod(MurmurHash.hash32(element.hashCode(), s1), (m - p2))
      bucketPos

  def isSet1[A](element: A, s1: Int, p1: Int, n: Int): Boolean =
    Math.floorMod(MurmurHash.hash32(element.hashCode(), s1), n) < p1

  def mapping[K, V](
      elements: Array[(K, V)],
      m: Int,
      s1: Int,
      n: Int
  ): Array[Vector[K]] =
    val buckets = Array.fill(m)(Vector.empty[K])
    val p1 = (0.6 * n).toInt
    val p2 = (0.3 * m).toInt

    elements.foreach(e => buckets(bucket(e._1, s1, p1, p2, n, m)) :+= e._1)

    buckets

  def position[A](element: A, k: Int, s: Int, n: Int): Int =
    optimizedPosition(
      MurmurHash.hash32(element.hashCode(), s),
      MurmurHash.hash32(k, s),
      n
    )
  def optimizedPosition(elementHash: Int, hashedK: Int, n: Int) =
    Math.floorMod((elementHash ^ hashedK), n)

  inline def time[A](inline f: A): (A, Long) =
    val start = System.currentTimeMillis()
    (f, System.currentTimeMillis() - start)
  def search[A](buckets: Array[Vector[A]], s1: Int, n: Int): Array[Int] =

    var taken = BitSet()
    var k = 0
    val (result, runtime) = time {
      buckets.zipWithIndex
        .sortBy((v, _) => v.size)
        .reverse
        .view
        .map { case (bucket, num) =>
          var nTaken = taken
          var done = false
          val hashes =
            bucket.map(elem => MurmurHash.hash32(elem.hashCode(), s1))
          if hashes.distinct != hashes then println("duplicate hashes found!!")

          while !done do
            val hashedK = MurmurHash.hash32(k, s1)
            var i = 0
            var conflict = false
            val size = bucket.size
            while i < size && !conflict do
              val pos = optimizedPosition(hashes(i), hashedK, n)
              conflict = nTaken.contains(pos)
              nTaken += pos
              i += 1
            if conflict then
              if k % 1_000_000 == 0 then
                println(
                  s"$k failed on the ${num}th bucket of ${buckets.size} $num with size ${bucket.size} on $i"
                )
                if k > 100_000_000 then
                  println(s"n: $n")
                  println(s"hashedK: $hashedK")
                  println(s"taken set: ${taken.toSet}")
                  println(s"nTaken set: ${nTaken.toSet}")
                  println(
                    s"positions: ${hashes.map(optimizedPosition(_, hashedK, n))}"
                  )
                  println(s"bucket: $bucket")
                  println(s"hashes: $hashes")
                  println(s"seed: $s1")

              nTaken = taken

              if k == -1 then
                println("Search failed!!")
                throw Error("Search failed!!")
              k += 1
            else done = true
          taken = nTaken
          num -> k
        }
        .sortBy((num, k) => num)
        .tapEach((num, k) => println(num -> k))
        .map((num, k) => k)
        .toArray
    }

    println(s"search done in $runtime millis")
    result
