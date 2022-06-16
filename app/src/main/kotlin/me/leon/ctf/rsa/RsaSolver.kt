package me.leon.ctf.rsa

import java.math.BigInteger
import me.leon.*

object RsaSolver {

    private val modeNECP = listOf("n", "e", "c", "p")
    private val modeNEC = listOf("n", "e", "c")
    private val modeNCD = listOf("n", "c", "d")
    private val modeN2E2C2 = listOf("n1", "e1", "c1", "n2", "e2", "c2")
    private val modeN2EC2 = listOf("n1", "c1", "n2", "e", "c2")
    private val modeNE2C2 = listOf("n", "c1", "e2", "e1", "c2")
    private val modeEC = listOf("e", "c")
    private val modeNECPhi = listOf("n", "e", "c", "phi")
    private val modePQEC = listOf("p", "q", "e", "c")
    private val modeDpDq = listOf("dp", "dq", "p", "q", "c")
    private val modeDp = listOf("dp", "e", "c", "n")

    /**
     * 1. dp泄露
     * 2. e = 1
     * 3. e大小判断,小于6直接开方
     * 4. n为素数
     * 5. NC互素判断 N C 不互素 gcm(n,c) = p
     * 6. factor Db分解,自动过滤合数
     */
    fun solve(params: MutableMap<String, BigInteger>): String =
        when {
            params.containKeys(modeNECPhi) ->
                params["c"]!!.decrypt(params["e"]!!.invert(params["phi"]!!), params["n"]!!).also {
                    println("solve N E C Phi ")
                }
            params.containKeys(modeDp) && params["dq"] == null -> dpLeak(params)
            params.containKeys(modeDpDq) -> solveDpDq(params)
            params.containKeys(modeNCD) ->
                params["c"]!!.decrypt(params["d"]!!, params["n"]!!).also { println("solve N C D ") }
            params.containKeys(modeN2E2C2) -> solveN2E2C2(params)
            params.containKeys(modeNE2C2) -> solveNE2C2(params)
            params.containKeys(modeN2EC2) -> solveN2EC2(params)
            params.containKeys(modePQEC) -> solvePQEC(params)
            params.containKeys(modeNECP) -> solvePQEC(params)
            params.containKeys(modeNEC) -> solveNEC(params)
            params.containKeys(modeEC) && params["e"] == BigInteger.ONE -> params["c"]!!.n2s()
            else -> error("wrong parameters!!!")
        }

    private fun solveN2EC2(params: MutableMap<String, BigInteger>): String {
        println("solve N2 E C2")
        val e = requireNotNull(params["e"])
        val n1 = requireNotNull(params["n1"])
        val c1 = requireNotNull(params["c1"])
        val n2 = requireNotNull(params["n2"])
        val p = n1.gcd(n2)
        val q1 = n1 / p
        val d1 = e.invert(p.phi(q1))
        return c1.decrypt(d1, n1)
    }

    private fun solveNE2C2(params: MutableMap<String, BigInteger>): String {
        params["n1"] = requireNotNull(params["n"])
        params["n2"] = requireNotNull(params["n"])
        return solveN2E2C2(params)
    }

    fun solveN2E2C2(params: MutableMap<String, BigInteger>): String {
        println("solve N2 E2 C2")
        val n1 = requireNotNull(params["n1"])
        val n2 = requireNotNull(params["n2"])
        val e1 = requireNotNull(params["e1"])
        val e2 = requireNotNull(params["e2"])
        val c1 = requireNotNull(params["c1"])
        val c2 = requireNotNull(params["c2"])
        val (_, s1, s2) = e1.gcdExt(e2)
        return (c1.modPow(s1, n1) * c2.modPow(s2, n2) % n1).n2s()
    }

    private fun solveNEC(params: MutableMap<String, BigInteger>): String {
        val n = requireNotNull(params["n"])
        val e = requireNotNull(params["e"])
        val c = requireNotNull(params["c"])
        return solveNEC(n, e, c)
    }

    private fun solveNEC(n: BigInteger, e: BigInteger, c: BigInteger): String {
        println("solve N E C")
        return when {
            e == BigInteger.ONE -> c.n2s().also { println("e = 1") }
            n.isProbablePrime(100) -> {
                println("nec n is prime")
                val phi = n - BigInteger.ONE
                c.modPow(e.invert(phi), n).n2s()
            }
            e < 6.toBigInteger() -> smallE(n, c, e).also { println("small e= $e") }
            n.gcd(c) != BigInteger.ONE -> {
                println("n c not mutual prime")
                val p = n.gcd(c)
                val q = n / p
                val phi = p.phi(q)
                (c.modPow(e.invert(phi), n) / p).n2s()
            }
            fermat(n).isNotEmpty() -> {
                println("fermat factor")
                val fermatResult = fermat(n)
                return solvePQEC(fermatResult.first(), fermatResult.last(), e, c)
            }
            else -> {
                println("factor db")
                n.factorDb().let {
                    if (it.groupBy { it }.size == 1) {
                        println("${it.first()} ^ ${it.size}")
                        val phi = it.first().eulerPhi(it.size)
                        val d = e.invert(phi).also { println(it) }
                        val propN = it.propN(n)
                        c.decrypt(d, propN).also { println(it) }
                    } else if (it.size >= 2) {
                        val phi = it.phi()
                        val d = e.invert(phi).also { println(it) }
                        val propN = it.propN(n)
                        c.decrypt(d, propN).also { println(it) }
                    } else "no solution"
                }
            }
        }
    }

    fun fermat(n: BigInteger): Array<BigInteger> {
        with(n.sqrtAndRemainder()) {
            if (this.last() != BigInteger.ZERO) {
                var a = first() + BigInteger.ONE
                var count = 0
                var b: BigInteger = BigInteger.ONE
                while (count < 10_000) {
                    val b1 = a.pow(2) - n
                    b = b1.sqrt()
                    count++
                    if (b * b == b1) {
                        println("solved iteration $count \n\tp = ${a + b} \n\tq= ${a - b}\n")
                        return arrayOf(a + b, a - b)
                    } else a++
                }
            }
        }
        println("no fermat solution")
        return arrayOf()
    }

    private fun smallE(n: BigInteger, c: BigInteger, e: BigInteger): String {
        for (k in 1..1000) {
            val m = (k.toBigInteger() * n + c).root(e.toInt())
            if (m[1] == BigInteger.ZERO) {
                return m.first().n2s()
            }
        }
        return "no solution"
    }

    private fun dpLeak(params: MutableMap<String, BigInteger>): String {
        println("dp leak")
        val n = requireNotNull(params["n"])
        val e = requireNotNull(params["e"])
        val c = requireNotNull(params["c"])
        val dp = requireNotNull(params["dp"])
        var p = BigInteger.ONE
        var q = BigInteger.ONE
        for (k in 1..65_537) {
            p = (e * dp - BigInteger.ONE) / k.toBigInteger() + BigInteger.ONE
            if (n.gcd(p) != BigInteger.ONE) {
                q = n / p
                break
            }
        }
        return c.decrypt(e.invert(p.phi(q)), n)
    }

    private fun solveDpDq(params: MutableMap<String, BigInteger>): String {
        println("solveDpDq")
        val p = requireNotNull(params["p"])
        val q = requireNotNull(params["q"])
        val c = requireNotNull(params["c"])
        val dp = requireNotNull(params["dp"])
        val dq = requireNotNull(params["dq"])

        val invQ = q.invert(p)
        val mp = c.modPow(dp, p)
        val mq = c.modPow(dq, q)

        val m = (((mp - mq) * invQ) % p) * q + mq

        return m.n2s()
    }

    /** 知p,q, 即只知 n d e phi互素判断 */
    fun solvePQEC(params: MutableMap<String, BigInteger>): String {
        val p = requireNotNull(params["p"])
        val q = params["q"] ?: (params["n"]!! / p)
        val e = requireNotNull(params["e"])
        val c = requireNotNull(params["c"])
        return solvePQEC(p, q, e, c)
    }

    private fun solvePQEC(p: BigInteger, q: BigInteger, e: BigInteger, c: BigInteger): String {
        println("solve P Q E C")
        val n = p * q
        val phi = p.phi(q)

        return if (e.gcd(phi) == BigInteger.ONE) {
            val d = e.invert(p.phi(q))
            c.decrypt(d, p * q)
        } else {
            val t = e.gcd(phi)
            val t1 = e / t
            val dt1 = t1.invert(phi)
            c.modPow(dt1, n).root(t.toInt()).first().n2s()
        }
    }
}

fun <T : Any> MutableMap<String, T>.containKeys(keys: List<String>) =
    keys.map { this[it] }.all { it != null }
