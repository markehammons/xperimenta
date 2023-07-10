package fr.hammons.xperimenta.core

sealed trait Capabilities

object Capabilities:
  type ToTuple[A <: Capabilities, T] <: Tuple = A match
    case head *::: tail => head[T] *: ToTuple[tail, T]
    case End            => EmptyTuple

  sealed trait *:::[A[_], B <: Capabilities] extends Capabilities
  sealed trait End extends Capabilities
