package fr.hammons.xperimenta.core

import scala.util.hashing.MurmurHash3
import org.scalacheck.Prop.*

class MurmurHashSpec extends munit.ScalaCheckSuite:
  property("hash32 should work"):
    forAll: (i: Int, j: Int) =>
      assertEquals(
        MurmurHash.hash32(i, j).toInt,
        org.apache.commons.codec.digest.MurmurHash3.hash32(i, j)
      )
