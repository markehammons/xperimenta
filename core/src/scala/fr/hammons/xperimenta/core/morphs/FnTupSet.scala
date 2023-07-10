package fr.hammons.xperimenta.core.morphs

import scala.NonEmptyTuple
import scala.quoted.*
import scala.util.chaining.*

// sealed trait FnTupSet:
//   transparent inline def map(inline mappings: (Typ[?], Typ[?])*): FnTupSet

// object FnTupSet:
//   type **:[A <: NonEmptyTuple, B <: FnTupSet] <: FnTupSet = B match
//     case FnTupSet0    => FnTupSet1[A]
//     case FnTupSet1[a] => FnTupSet2[A, a]

//   extension [T <: FnTupSet](fn: T)
//     def prepend[A <: NonEmptyTuple](fnTup: FnTup[A]): **:[A, T] = fn match
//       case fn: FnTupSet0    => FnTupSet1[A](fnTup)
//       case fn: FnTupSet1[b] => FnTupSet2[A, b](fnTup, fn.fnTup1)

// type x = FnTupSet.**:[(Int, Float), FnTupSet0]

// final class FnTupSet0 private extends FnTupSet:
//   inline transparent def map(inline mappings: (Typ[?], Typ[?])*): FnTupSet0 =
//     FnTupSet0.EmptyFnTupSet

// object FnTupSet0:
//   val EmptyFnTupSet = new FnTupSet0

// export FnTupSet0.EmptyFnTupSet

// final case class FnTupSet1[A <: NonEmptyTuple](fnTup1: FnTup[A])
//     extends FnTupSet:
//   inline transparent def map(inline mappings: (Typ[?], Typ[?])*): FnTupSet1[?] =
//     FnTupSet1(fnTup1.map(mappings*))

// final case class FnTupSet2[A <: NonEmptyTuple, B <: NonEmptyTuple](
//     fnTup1: FnTup[A],
//     fnTup2: FnTup[B]
// ) extends FnTupSet:
//   transparent inline def map(
//       inline mappings: (Typ[?], Typ[?])*
//   ): FnTupSet2[? <: NonEmptyTuple, ? <: NonEmptyTuple] = ${
//     FnTupSet2.mapImpl[A, B]('this, 'mappings)
//   }

// object FnTupSet2:
//   def mapImpl[A <: NonEmptyTuple, B <: NonEmptyTuple](
//       expr: Expr[FnTupSet2[A, B]],
//       mappings: Expr[Seq[(Typ[?], Typ[?])]]
//   )(using Quotes, Type[A], Type[B]): Expr[FnTupSet2[? <: NonEmptyTuple,? <: NonEmptyTuple]] =
//     import quotes.reflect.*
//     val expr1 = '{ $expr.fnTup1.map($mappings*) }
//     val expr2 = '{ $expr.fnTup2.map($mappings*) }

//     ValDef.let(Symbol.spliceOwner,
//       List(
//         expr1.asTerm,
//         expr2.asTerm
//       )
//     )(
//       refs =>
//         val tpePair = refs.map(ref => ref.asExpr -> ref.tpe.asType).pipe(tpes => tpes(0) -> tpes(1))

//         tpePair match
//           case ((exp, '[FnTup[m1]]), (exp2, '[FnTup[m2]])) =>
//             (TypeRepr.of[m1].widen.asType, TypeRepr.of[m2].widen.asType) match
//               case ('[p1], '[p2]) =>
//                 report.error(Type.show[p1])
//                 '{
//                   FnTupSet2[p1 & NonEmptyTuple, p2 & NonEmptyTuple](${exp}.asInstanceOf[FnTup[p1 & NonEmptyTuple]], ${exp2}.asInstanceOf[FnTup[p2 & NonEmptyTuple]])
//                 }.asTerm

//     ).asExprOf[FnTupSet2[? <: NonEmptyTuple, ? <: NonEmptyTuple]]

//     // (expr1, expr2) match
//     //   case ('{${exp1}: FnTup[m1]}, '{${exp2}: FnTup[m2]}) =>
//     //     report.errorAndAbort(Type.show[m1])
//     //     '{
//     //       FnTupSet2[m1,m2]($exp1, $exp2)
//     //     }
//     //   case (exp1, exp2) =>
//     //     report.errorAndAbort(exp1.show)
