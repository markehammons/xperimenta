package fr.hammons.xperimenta.core
import scala.quoted.*

object StringLiteral:
  opaque type StringLiteral = String
  inline def apply(inline s: String): StringLiteral = ${
    applyImpl('s)
  }

  given FromExpr[StringLiteral] = FromExpr.StringFromExpr
  given ToExpr[StringLiteral] = ToExpr.StringToExpr

  private def applyImpl(s: Expr[String])(using Quotes) =
    Expr(s.valueOrAbort)

  extension (inline sl: StringLiteral)
    inline def inlineTake(inline i: Int): StringLiteral = ${
      inlineTakeImpl('sl, 'i)
    }

  private def inlineTakeImpl(sl: Expr[StringLiteral], i: Expr[Int])(using
      Quotes
  ): Expr[StringLiteral] =
    Expr(sl.valueOrAbort.take(i.valueOrAbort))
