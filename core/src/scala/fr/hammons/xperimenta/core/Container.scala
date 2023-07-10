package fr.hammons.xperimenta.core

import scala.compiletime.{erasedValue, error, summonAll, summonInline}
import Capabilities.*
import scala.quoted.*
import scala.annotation.nowarn

class Data[A](a: A):
  type B = A
  val b: B = a

class Use[A[_]](val b: Data[?])(using ev: A[b.B]):
  def apply[C](fn: A[b.B] ?=> b.B => C) = fn(b.b)

class Container[A <: Capabilities](
    val data: Data[?],
    val evidences: Array[AnyRef]
):
  inline def fetchIdx[C[_], A <: Capabilities]: Int =
    inline erasedValue[A] match
      case _: (C *::: ?)    => 0
      case _: (? *::: rest) => fetchIdx[C, rest] + 1
      case _: End           => error("ended")

  inline def fetchable[C[_], A <: Capabilities]: Boolean =
    inline erasedValue[A] match
      case _: (C *::: ?)    => true
      case _: (? *::: rest) => fetchable[C, rest]
      case _: End           => false

  inline def use[C[_]]: Use[C] =
    inline if fetchable[C, A] then
      Use(data)(using evidences(fetchIdx[C, A]).asInstanceOf[C[data.B]])
    else error("The requisite capability doesn't exist")

object Container:
  inline def apply[A <: Capabilities](data: Any): Container[A] =
    ${
      applyImpl[A]('data)
    }

  private def getEvidences[A <: Tuple](using
      Type[A],
      Quotes
  ): Expr[Vector[AnyRef]] =
    import quotes.reflect.*
    Type.of[A] match
      case '[head *: tail] =>
        val ev = Expr
          .summon[head]
          .getOrElse(
            report.errorAndAbort(
              s"Couldn't find ${Type.show[head]} in scope"
            )
          )
          .asExprOf[AnyRef]

        val rest = getEvidences[tail]
        '{
          $ev +: $rest
        }

      case '[EmptyTuple] =>
        '{ Vector.empty }

  private def applyImpl[A <: Capabilities](
      data: Expr[Any]
  )(using Quotes, Type[A]): Expr[Container[A]] =
    import quotes.reflect.*

    data match
      case '{ $a: i } =>
        TypeRepr.of[i].widen.asType match
          case '[j] =>
            val expr = getEvidences[Capabilities.ToTuple[A, j]]
            '{
              new Container[A](Data($data), ${ expr }.toArray)
            }

  private def conversionImpl[A <: Capabilities, B](using
      Quotes,
      Type[A],
      Type[B]
  ): Expr[Conversion[B, Container[A]]] = '{
    new Conversion[B, Container[A]]:
      def apply(x: B): Container[A] = ${ applyImpl[A]('x) }
  }
  @nowarn
  inline given [A <: Capabilities, B]: Conversion[B, Container[A]] = ${
    conversionImpl[A, B]
  }
