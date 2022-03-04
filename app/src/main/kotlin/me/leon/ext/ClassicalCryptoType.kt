package me.leon.ext

import me.leon.P1
import me.leon.P2
import me.leon.classical.*
import me.leon.ctf.*
import me.leon.railFenceWDecrypt
import me.leon.railFenceWEncrypt

enum class ClassicalCryptoType(val type: String) : IClassical {
    CAESAR("caesar") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.caesar25()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.caesar25()
    },
    ROT5("rot5") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.shift10(5)

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.shift10(5)

        override fun isIgnoreSpace() = false
    },
    ROT13("rot13") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.shift26(13)

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.shift26(13)

        override fun isIgnoreSpace() = false
    },
    ROT18("rot18") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.rot18()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.rot18()

        override fun isIgnoreSpace() = false
    },
    ROT47("rot47") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.shift94(47)

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.shift94(47)

        override fun isIgnoreSpace() = false
    },
    AFFINE("affine") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.affineEncrypt(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.affineDecrypt(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("factor a", "b")

        override fun isIgnoreSpace() = false
    },
    RAILFENCE("railFence") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.railFenceEncrypt(params[P1]!!.toInt())

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.railFenceDecrypt(params[P1]!!.toInt())

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("fence number", "")
    },
    RAILFENCEW("railFenceW") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.railFenceWEncrypt(params[P1]!!.toInt())

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.railFenceWDecrypt(params[P1]!!.toInt())

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("fence number", "")
    },
    VIRGENENE("virgenene") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.virgeneneEncode(params[P1]!!)

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.virgeneneDecode(params[P1]!!)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key", "")

        override fun isIgnoreSpace() = false
    },
    ATBASH("atbash") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.atBash()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.atBash()

        override fun isIgnoreSpace() = false
    },
    MORSE("morse") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.morseEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.morseDecrypt()

        override fun isIgnoreSpace() = false
    },
    QWE("qwe") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.qweEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.qweDecrypt()
    },
    POLYBIUS("polybius") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.polybius(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.polybiusDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun isIgnoreSpace() = false
        override fun paramsCount() = 2

        override fun paramsHints() =
            listOf("table, ABCDEFGHIKLMNOPQRSTUVWXYZ as default", "encode map, 12345 as default")
    },
    NIHILIST("nihilist") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.nihilist(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.nihilistDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("keyword", "encodeMap 12345 is as default")

        override fun isIgnoreSpace() = false
    },
    ADFGX("ADFGX") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.adfgx(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.adfgxDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("table ABCDEFGHIKLMNOPQRSTUVWXYZ", "keyword")
    },
    ADFGVX("ADFGVX") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.adfgvx(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.adfgvxDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE,
                params[P2].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_ENCODE_MAP
            )

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("table ABCDEFGHIKLMNOPQRSTUVWXYZ", "keyword")
    },
    PLAYFAIR("playFair") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.playFair(params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE)

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.playFairDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE
            )

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key", "")
    },
    AUTOKEY("autoKey") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.autoKey(params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE)

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.autoKeyDecrypt(
                params[P1].takeUnless { it.isNullOrEmpty() } ?: DEFAULT_POLYBIUS_TABLE
            )

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("table ABCDEFGHIKLMNOPQRSTUVWXYZ", "")

        override fun isIgnoreSpace() = false
    },
    BACON24("bacon24") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.baconEncrypt24()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.baconDecrypt24()

        override fun isIgnoreSpace() = false
    },
    BACON26("bacon26") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.baconEncrypt26()

        override fun decrypt(raw: String, params: MutableMap<String, String>) = raw.baconDecrypt26()

        override fun isIgnoreSpace() = false
    },
    OTP("oneTimePad") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.oneTimePad(params[P1]!!)

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.oneTimePadDecrypt(params[P1]!!)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("key data as long as data size", "")
    },
    SOCIALISM("socialistCoreValue") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.socialistCoreValues()

        override fun decrypt(raw: String, params: MutableMap<String, String>) =
            raw.socialistCoreValuesDecrypt()
    },
    BRAINFUCK("brain fuck") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.brainFuckEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.brainFuckDecrypt()
    },
    Ook("Ook") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.ookEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.ookDecrypt()

        override fun isIgnoreSpace() = false
    },
    TROLLSCRIPT("troll script") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.trollScriptEncrypt()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.trollScriptDecrypt()
    },
    XOR("xorBase64") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.xorBase64(params[P1]!!).also { println("xor $raw $params") }

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.xorBase64Decode(params[P1]!!)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("xor key", "")
    },
    XOR2("xorHex") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.xorBase64(params[P1]!!).also { println("xor $raw $params") }

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.xorBase64Decode(params[P1]!!)

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("xor key", "")
    },
    Braille("braille") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.blindEncode().also { println("Braille $raw $params") }

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.blindDecode()
    },
    BauDot("baudot") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.baudot().also { println("baudot $raw $params") }

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.baudotDecode()

        override fun isIgnoreSpace() = false
    },
    AlphabetIndex("a1z26") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.alphabetIndex(params[P1]?.ifEmpty { " " } ?: " ").also {
                println("alphabetIndex $raw $params")
            }

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.alphabetIndexDecode()

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("separator(space as default)", "")

        override fun isIgnoreSpace() = false
    },
    Zero1248("01248") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.zero1248().also { println("01248 $raw $params") }

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.zero1248Decode()
    },
    BubbleBabble("bubbleBabble") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.bubbleBabble()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.bubbleBabbleDecode()

        override fun isIgnoreSpace() = false
    },
    ZWC("zeroWidthChar") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.zwc(params[P1]?.ifEmpty { "hide" } ?: "hide")

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.zwcDecode()

        override fun paramsCount() = 1

        override fun paramsHints() = listOf("show plain text", "")
    },
    PeriodicTable("periodicTable") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.elementPeriodEncode()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.elementPeriodDecode()

        override fun isIgnoreSpace() = false
    },
    PawnShop("pawnShop") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) = raw.pawnshop()

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.pawnshopDecode()

        override fun isIgnoreSpace() = false
    },
    CurveCipher("curveCipher") {
        override fun encrypt(raw: String, params: MutableMap<String, String>) =
            raw.curveCipher(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun decrypt(raw: String, params: MutableMap<String, String>): String =
            raw.curveCipherDecode(params[P1]!!.toInt(), params[P2]!!.toInt())

        override fun paramsCount() = 2

        override fun paramsHints() = listOf("row", "column")
    },
}
