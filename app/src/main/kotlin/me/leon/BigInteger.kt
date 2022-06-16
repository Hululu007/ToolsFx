package me.leon

import java.math.BigInteger
import me.leon.ext.readFromNet

// this = p
fun BigInteger.phi(q: BigInteger) = (this - BigInteger.ONE) * (q - BigInteger.ONE)

// this = p
fun BigInteger.phi(q: String) = phi(BigInteger(q))

fun BigInteger.lcm(other: BigInteger) = this * other / this.gcd(other)

fun BigInteger.mutualPrime(other: BigInteger) = this.gcd(other) == BigInteger.ZERO

// this 关于 other的逆元
fun BigInteger.invert(other: String): BigInteger = modInverse(other.toBigInteger())

// this = e
fun BigInteger.invert(phi: BigInteger): BigInteger = modInverse(phi)

fun BigInteger.gcdExt(other: BigInteger) = Kgcd.gcdext(this, other)

// this = c
fun BigInteger.decrypt(d: BigInteger, n: BigInteger) = modPow(d, n).toByteArray().decodeToString()

fun BigInteger.n2s() = toByteArray().decodeToString()

fun String.s2n() = BigInteger(toByteArray())

fun ByteArray.toBigInteger() = BigInteger(this)

// this = c
fun BigInteger.decrypt(d: String, n: String) = decrypt(BigInteger(d), BigInteger(n))

// this = n
fun BigInteger.factorDb() = getPrimeFromFactorDb(this)

fun List<BigInteger>.phi(): BigInteger =
    filter { it > BigInteger.ZERO }.fold(BigInteger.ONE) { acc, int ->
        acc * (int - BigInteger.ONE)
    }

fun List<BigInteger>.propN(n: BigInteger) =
    filter { it < BigInteger.ZERO }.fold(n) { acc, bigInteger -> acc / bigInteger.abs() }

fun BigInteger.eulerPhi(n: Int) = minus(BigInteger.ONE) * pow(n - 1)

fun getPrimeFromFactorDb(digit: BigInteger) = getPrimeFromFactorDb(digit.toString())

fun getPrimeFromFactorDb(digit: String): List<BigInteger> {
    val response = "http://www.factordb.com/index.php?query=$digit".readFromNet()

    var result = emptyList<BigInteger>()

    "<td>(\\w+)</td>".toRegex().find(response)?.let {
        result =
            when (it.groupValues[1]) {
                "P" -> listOf(digit.toBigInteger())
                "FF" -> emptyList<BigInteger>().also { println("Composite, fully factored") }
                "C" -> listOf(digit.toBigInteger()).also { println("Composite, no factors known") }
                "CF" -> emptyList<BigInteger>().also { println("Composite, factors known") }
                else -> listOf(digit.toBigInteger()).also { println("Unknown") }
            }
    }

    if (result.isEmpty()) {
        "index\\.php\\?id=\\d+"
            .toRegex()
            .findAll(response.substringAfter(" = "))
            .toList()
            .map { it.value }
            .also {
                println(it)
                val resultLine =
                    response.lines().find { it.contains(" = ") }!!
                        .substringAfter(" = ")
                        .replace("<font color=\"#\\d+\">|</\\w+>".toRegex(), "")
                println(resultLine)
                result =
                    if (it.size >= 2) {
                        it.map { getPrimeFromFactorDbPath(it) }
                    } else {
                        val prime = getPrimeFromFactorDbPath(it.first())
                        val list = mutableListOf<BigInteger>()
                        "\\^(\\d+)".toRegex().find(response)?.run {
                            //                            println("$prime ^" + groupValues[1])
                            repeat(groupValues[1].toInt()) { list.add(prime) }
                        }
                        list.apply {
                            if (isEmpty()) {
                                add(digit.toBigInteger())
                                println("无法分解")
                            }
                        }
                    }
            }
    }
    return result
}

private fun getPrimeFromFactorDbPath(path: String) =
    if (path.length < 32 && path.substringAfter("=").toBigInteger().isProbablePrime(100)) {
        println("quick judge is prime")
        path.substringAfter("=").toBigInteger()
    } else {
        "http://www.factordb.com/$path".readFromNet().run {
            println("getPrimeFromFactorDbPath $path")
            "value=\"(\\d+)\"".toRegex().find(this)!!.groupValues[1].toBigInteger().also { digit ->
                "<td>(\\w+)</td>".toRegex().find(this)?.let {
                    when (it.groupValues[1]) {
                        "P" -> return digit
                        "FF" -> println("Composite, fully factored")
                        "C" -> return -digit.also { println("Composite, no factors known") }
                        "CF" -> println("Composite, factors known")
                        else -> return digit.also { println("Unknown") }
                    }
                }
            }
        }
    }

// ported from
// https://github.com/ryanInf/python2-libnum/blob/316c378ba268577320a239b2af0d766c1c9bfc6d/libnum/common.py
fun BigInteger.root(n: Int = 2): Array<BigInteger> {
    if (this.signum() < 0 && n % 2 == 0) error("n must be even")

    val sig = this.signum()
    val v = this.abs()
    var high = BigInteger.ONE
    while (high.pow(n) <= v) high = high.shiftLeft(1)
    var low = high.shiftRight(1)
    var mid = BigInteger.ONE
    var midCount = 0
    while (low < high) {
        mid = (low + high).shiftRight(1)
        if (low < mid && mid.pow(n) <= v) low = mid
        else if (high > mid && mid.pow(n) >= v) high = mid else mid.also { midCount++ }
        if (midCount > 1) break
    }
    return with(mid * sig.toBigInteger()) { arrayOf(this, this@root - this.pow(n)) }
}
