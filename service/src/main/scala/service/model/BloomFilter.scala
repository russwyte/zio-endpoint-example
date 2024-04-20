package service.model

import scala.collection.mutable.BitSet
import scala.util.hashing.MurmurHash3

case class HashResult(base: Long, seed: Int)

/** Represents a slot in the bloom filter
  *
  * Right now this is mapped to a bit in an in memory bitset but it could be a key in redis etc
  *
  * Both redis keys and Scala std lib BitSets can only hold 2^32 bits so we need to split the bloom filter bits into
  * multiple BitSets
  *
  * @param bitSetIndex
  * @param bitIndex
  */
case class HashSlot(bitSetIndex: Int, bitIndex: Int)

case class BloomFilter private (numberOfBits: Long, numberOfHashes: Int):
  val numberOfBitSets    = numberOfBits / Integer.MAX_VALUE + 1
  val smallestBitSetSize = numberOfBits % Integer.MAX_VALUE
  println(
    s"numberOfBits: $numberOfBits, numberOfHashes: $numberOfHashes, numberOfBitSets: $numberOfBitSets, smallestBitSetSize: $smallestBitSetSize"
  )
  val bitSets = (0 until numberOfBitSets.toInt).map {
    case i if i == numberOfBitSets - 1 => new BitSet(smallestBitSetSize.toInt)
    case _                             => new BitSet(Integer.MAX_VALUE)
  }.toArray
  import Math.abs

  private def computeHash(s: String): HashResult =
    val lower32    = Math.abs(MurmurHash3.stringHash(s))
    val base: Long = Math.abs(MurmurHash3.stringHash(s, lower32).toLong << 32) + lower32
    val seed       = Math.abs(MurmurHash3.stringHash(s, lower32))
    HashResult(base, seed)

  private def bitPosition(result: HashResult)(i: Int) =
    val x = abs((result.base + i * result.seed) % numberOfBits)
    HashSlot((x / Integer.MAX_VALUE).toInt, (x % Integer.MAX_VALUE).toInt)

  def add(s: String) =
    val bitPos = bitPosition(computeHash(s))
    (0 until numberOfHashes).foreach: i =>
      val slot = bitPos(i)
      bitSets(slot.bitSetIndex).add(slot.bitIndex)

  def contains(s: String) =
    val bitPos = bitPosition(computeHash(s))
    (0 until numberOfHashes).forall: i =>
      val slot = bitPos(i)
      bitSets(slot.bitSetIndex).contains(slot.bitIndex)
end BloomFilter
object BloomFilter:

  def optimalNumberOfBits(numberOfItems: Long, falsePositiveRate: Double): Long =
    math.ceil(-1 * numberOfItems * math.log(falsePositiveRate) / math.log(2) / math.log(2)).toLong

  def optimalNumberOfHashes(numberOfItems: Long, numberOfBits: Long): Int =
    math.ceil(numberOfBits / numberOfItems * math.log(2)).toInt

  def apply(numberOfItems: Long, falsePositiveRate: Double): BloomFilter =
    val numberOfBits   = optimalNumberOfBits(numberOfItems, falsePositiveRate)
    val numberOfHashes = optimalNumberOfHashes(numberOfItems, numberOfBits)
    BloomFilter(numberOfBits, numberOfHashes)
end BloomFilter
