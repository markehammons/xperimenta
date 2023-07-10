package fr.hammons.xperimenta.core

sealed trait InvariantTuple

object InvariantTuple:
  type Cons[A, B <: NonMaxSizeInvariantTuple] <: NonEmptyInvariantTuple =
    B match
      case InvariantTuple0             => InvariantTuple1[A]
      case InvariantTuple1[a]          => InvariantTuple2[A, a]
      case InvariantTuple2[a, b]       => InvariantTuple3[A, a, b]
      case InvariantTuple3[a, b, c]    => InvariantTuple4[A, a, b, c]
      case InvariantTuple4[a, b, c, d] => InvariantTuple5[A, a, b, c, d]

  type Uncons[T <: NonEmptyInvariantTuple] <: InvariantTuple =
    T match
      case InvariantTuple5[a, b, c, d, e] =>
        InvariantTuple2[a, InvariantTuple4[b, c, d, e]]
      case InvariantTuple4[a, b, c, d] =>
        InvariantTuple2[a, InvariantTuple3[b, c, d]]
      case InvariantTuple3[a, b, c] => InvariantTuple2[a, InvariantTuple2[b, c]]
      case InvariantTuple2[a, b]    => InvariantTuple2[a, InvariantTuple1[b]]
      case InvariantTuple1[a]       => InvariantTuple2[a, InvariantTuple0]

  extension [T <: NonMaxSizeInvariantTuple](t: T)
    def prepend[A](a: A): Cons[A, T] =
      t match
        case t: InvariantTuple0       => InvariantTuple1[A](a)
        case t: InvariantTuple1[a]    => InvariantTuple2[A, a](a, t._1)
        case t: InvariantTuple2[a, b] => InvariantTuple3[A, a, b](a, t._1, t._2)
        case t: InvariantTuple3[a, b, c] =>
          InvariantTuple4[A, a, b, c](a, t._1, t._2, t._3)
        case t: InvariantTuple4[a, b, c, d] =>
          InvariantTuple5[A, a, b, c, d](a, t._1, t._2, t._3, t._4)

sealed trait NonEmptyInvariantTuple extends InvariantTuple

sealed trait NonMaxSizeInvariantTuple extends InvariantTuple

sealed class InvariantTuple0 private extends NonMaxSizeInvariantTuple

object InvariantTuple0:
  val EmptyInvariantTuple = new InvariantTuple0

export InvariantTuple0.*

case class InvariantTuple1[A](_1: A)
    extends NonMaxSizeInvariantTuple,
      NonEmptyInvariantTuple

case class InvariantTuple2[A, B](_1: A, _2: B)
    extends NonMaxSizeInvariantTuple,
      NonEmptyInvariantTuple

case class InvariantTuple3[A, B, C](_1: A, _2: B, _3: C)
    extends NonMaxSizeInvariantTuple,
      NonEmptyInvariantTuple

case class InvariantTuple4[A, B, C, D](_1: A, _2: B, _3: C, _4: D)
    extends NonMaxSizeInvariantTuple,
      NonEmptyInvariantTuple

case class InvariantTuple5[A, B, C, D, E](_1: A, _2: B, _3: C, _4: D, _5: E)
    extends NonEmptyInvariantTuple

// case class InvariantTuple22[
//     A,
//     B,
//     C,
//     D,
//     E,
//     F,
//     G,
//     H,
//     I,
//     J,
//     K,
//     L,
//     M,
//     N,
//     O,
//     P,
//     Q,
//     R,
//     S,
//     T,
//     U,
//     V
// ](
//     _1: A,
//     _2: B,
//     _3: C,
//     _4: D,
//     _5: E,
//     _6: F,
//     _7: G,
//     _8: H,
//     _9: I,
//     _10: J,
//     _11: K,
//     _12: L,
//     _13: M,
//     _14: N,
//     _15: O,
//     _16: P,
//     _17: Q,
//     _18: R,
//     _19: S,
//     _20: T,
//     _21: U,
//     _22: V
// ) extends NonEmptyInvariantTuple
