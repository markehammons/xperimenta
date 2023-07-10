package fr.hammons.xperimenta.core.macros

import fr.hammons.xperimenta.core.Structural
import scala.quoted.*
import scala.NonEmptyTuple

object StructuralMacros:
  transparent inline def addValueMacro[
      A <: String & Singleton,
      B,
      C <: Structural
  ](s: C): C = ${
    valueApply[A, B, C]('s)
  }

  def valueApply[A <: String & Singleton, B, C <: Structural](
      s: Expr[C]
  )(using Quotes, Type[A], Type[B], Type[C]): Expr[C] =
    import quotes.reflect.*

    val name = TypeRepr.of[A] match
      case ConstantType(StringConstant(name)) => name
      case t                                  => report.errorAndAbort(t.show)
    Refinement(TypeRepr.of[C], name, TypeRepr.of[B]).asType match
      case '[a & C] => '{ $s.asInstanceOf[a & C] }

  transparent inline def addMethodMacro[
      A <: String & Singleton,
      B <: NonEmptyTuple,
      C <: Structural
  ](s: C) = ???
