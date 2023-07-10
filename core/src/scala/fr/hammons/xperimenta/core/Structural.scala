package fr.hammons.xperimenta.core

import scala.quoted.*
import macros.StructuralMacros

class Structural(array: Array[Any]) extends Selectable
object Structural:

  extension [C <: Structural](s: C)
    transparent inline def addValue[A <: String & Singleton, B] =
      StructuralMacros.addValueMacro[A, B, C](s)

  def x: Structural = ???

  def y = x.addValue["hello", Int]
