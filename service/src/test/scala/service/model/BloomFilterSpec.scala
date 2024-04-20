package service.model

import zio.test.*

object BloomFilterSpec extends ZIOSpecDefault:
  val bf = BloomFilter(Int.MaxValue.toLong + 1000, 0.0000001)

  def spec = suite("BloomFilterSpec")(
    test("should correctly identify an item that has been added") {
      bf.add("hello")
      assertTrue(bf.contains("hello"))
    },
    test("should correctly identify an item that has not been added") {
      assertTrue(!bf.contains("world"))
    },
  )
end BloomFilterSpec
