package fr.hammons.xperimenta.core

object MurmurHash:
  private val c132 = 0xcc9e2d51
  private val c232 = 0x1b873593
  private val r132 = 15
  private val r232 = 13
  private val m32 = 5
  private val n32 = 0xe6546b64
  private val longBytes = java.lang.Long.BYTES

  def mix32(_k: Int, _hash: Int): Int =
    var k = _k * c132
    k = Integer.rotateLeft(k, r132)
    k *= c232
    val hash = _hash ^ k
    Integer.rotateLeft(hash, r232) * m32 + n32

  def fmix32(_hash: Int) =
    var hash = _hash ^ (_hash >>> 16)
    hash *= 0x85ebca6b
    hash ^= hash >>> 13
    hash *= 0xc2b2ae35
    hash ^= hash >>> 16
    hash

  def hash32(data: Long, seed: Int): Int =
    var hash = seed
    var r0 = java.lang.Long.reverseBytes(data)
    hash = mix32(r0.toInt, hash)
    hash = mix32((r0 >>> 32).toInt, hash)

    hash ^= longBytes
    fmix32(hash)
