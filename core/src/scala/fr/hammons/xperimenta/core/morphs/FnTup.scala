package fr.hammons.xperimenta.core.morphs

import scala.NonEmptyTuple
import scala.quoted.*
import fr.hammons.xperimenta.core.NonEmptyInvariantTuple
import fr.hammons.xperimenta.core.InvariantTuple
import fr.hammons.xperimenta.core.InvariantTuple2
import fr.hammons.xperimenta.core.InvariantTuple0
import fr.hammons.xperimenta.core.InvariantTuple3
import fr.hammons.xperimenta.core.InvariantTuple0.EmptyInvariantTuple
import fr.hammons.xperimenta.core.InvariantTuple1

opaque type FnTup[T <: NonEmptyInvariantTuple] = Unit

type MapType[In, Out, T <: NonEmptyInvariantTuple] <: NonEmptyInvariantTuple =
  InvariantTuple.Uncons[T] match
    case InvariantTuple2[head, InvariantTuple0] =>
      head match
        case In => InvariantTuple.Cons[Out, InvariantTuple0]
        case _  => InvariantTuple.Cons[head, InvariantTuple0]
    case InvariantTuple2[head, tail] =>
      head match
        case In => InvariantTuple.Cons[Out, MapType[In, Out, tail]]
        case _  => InvariantTuple.Cons[head, MapType[In, Out, tail]]

val x: MapType[Long, Double, InvariantTuple3[Int, Long, Float]] =
  EmptyInvariantTuple.prepend(3f).prepend(3d).prepend(2)

// class FnTup[T <: NonEmptyTuple] private:
//   inline def prepend[A]: FnTup[A *: T] = FnTup[A *: T]
//   transparent inline def map[U,V](inline mappings: (Typ[?], Typ[?])*): FnTup[? <: NonEmptyTuple] = ${
//     FnTup.mappingsHelper[T]('mappings)
//   }

// object FnTup:
//   private def constructor[T <: NonEmptyTuple]: FnTup[T] = new FnTup[T]
//   inline def apply[T <: NonEmptyTuple]: FnTup[T] = constructor[T]

//   def mappingsHelper[T <: NonEmptyTuple](mappings: Expr[Seq[(Typ[?], Typ[?])]])(using Quotes, Type[T]): Expr[FnTup[? <: NonEmptyTuple]] =
//     import quotes.reflect.*
//     mappings match
//       case Varargs(mappings) =>
//         mappings.foldLeft('{FnTup[T]}: Expr[FnTup[?]]){
//           case (fn, mapping) =>
//             fn match
//               case '{${_}: FnTup[t]} =>
//                 mapping match
//                   case '{(${_}: Typ[m1], ${_}: Typ[m2])} =>
//                     mappingHelper[m1,m2, t]
//                   case '{->(${_}: Typ[m1],${_}: Typ[m2])} =>
//                     mappingHelper[m1,m2, t]

//                   case '{Predef.ArrowAssoc(${_}: Typ[m1]).->(${_}: Typ[m2])} =>
//                     mappingHelper[m1,m2,t]
//                   case expr =>
//                     report.errorAndAbort(expr.show)
//         }

//   def mappingHelper[A,B, T <: Tuple](using Quotes, Type[A], Type[B], Type[T]): Expr[FnTup[? <: NonEmptyTuple]] =
//     Type.of[T] match
//       case '[Tuple1[A]] => '{FnTup[Tuple1[B]]}
//       case '[Tuple1[first]] => '{FnTup[Tuple1[first]]}
//       case '[A *: rest] => '{${mappingHelper[A,B, rest]}.prepend[B]}
//       case '[first *: rest] => '{${mappingHelper[A,B,rest]}.prepend[first]}
