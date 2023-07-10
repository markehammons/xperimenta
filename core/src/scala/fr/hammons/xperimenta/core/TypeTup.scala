package fr.hammons.xperimenta.core

opaque type TypeTup[T <: Tuple] = Any

object TypeTup:
  def apply[T <: Tuple]: TypeTup[T] = ()

  extension [T <: Tuple](typeTup: TypeTup[T])
    inline def map[U <: Tuple](
        inline fn: TypeTup[T] => TypeTup[U]
    ): TypeTup[U] = fn(typeTup)
