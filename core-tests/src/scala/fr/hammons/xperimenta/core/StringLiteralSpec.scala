package fr.hammons.xperimenta.core

import scala.compiletime.erasedValue
import fr.hammons.xperimenta.core.morphs.Typ
import scala.compiletime.error
import scala.compiletime.codeOf
import java.util.UUID

class StringLiteralSpec extends munit.FunSuite:
  test("StringLiteral should compile string literals"):
    assertNoDiff(compileErrors("""StringLiteral("hello")"""), "")

  test("should compile inline val strings"):
    assertNoDiff(
      compileErrors("""
    inline val s = "hello"
    StringLiteral(s)
    """),
      ""
    )

  test("should be assigned to inline vals"):
    assertNoDiff(
      compileErrors("""
    inline val s = StringLiteral("hello")
    """),
      ""
    )

  // val y = FnTup[Tuple1[Int]].prepend[Float]
  // val x = FnTup[(Int, Float, Int)].map(Typ[Float] -> Typ[String], Typ[Int] -> Typ[Double])

  // import fr.hammons.xperimenta.core.morphs.FnTupSet.*
  // import fr.hammons.xperimenta.core.morphs.FnTupSet.**:
  // val set: FnTupSet2[(Double, String, Double), (Float,Int)] = EmptyFnTupSet.prepend(y).prepend(x)

  // trait B
  // class A extends B
  // class C extends B
  // type MyMatch[T] = T match
  //   case Typ[A] => Int
  //   case Typ[C] => Float

  // val m: MyMatch[Typ[A]] = 4

  val x = EmptyInvariantTuple.prepend(5)

  // val set2: FnTupSet2[(UUID, Char, UUID), (Float,Long)] = set.map(Typ[Double] -> Typ[UUID], Typ[String] -> Typ[Char], Typ[Int] -> Typ[Long])
