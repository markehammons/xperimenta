package fr.hammons.xperimenta.core

import Capabilities.*

class ContainerSpec extends munit.FunSuite:
  test("Container should convert list of containers"):
    assertNoDiff(
      compileErrors("""
    import fr.hammons.xperimenta.core.Capabilities.*
    val x: List[Container[Numeric *::: End]] = List(1,2f)"""),
      ""
    )

    val v: List[Container[Numeric *::: End]] = List(1, 2f, 3d)

    val y = v.map(_.use[Numeric](numeric ?=> d => numeric.toDouble(d)))

    assert(y == List(1d, 2d, 3d))
