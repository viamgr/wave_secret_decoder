package com.example.myapplication.decode

import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val file = getFileFromPath("file_2.wav") ?: error("file not found")

        var passed = 0

        val stringArray = mutableListOf<String>()
        var oldDif = 0
        file.inputStream().buffered().use { input ->
            println(input.available())
            input.skip(48)
            var totalSum = 0
            var oldDiff = 0
            var oldResult = false
            var bitArray = ""
            var passedIndex = 0
            var count = 0
            while (true) {
                //har 7 ta passed mishe ye 320 ms
                val buff = ByteArray(2)

                val sz = input.read(buff)

                val buffer = ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN)

                if (passed != 0 && passed % 28 == 0) {
                    val diff = oldDiff - totalSum

                    val result = diff - oldDiff > 100
                    passedIndex++
//001100110011
//001010101100
//000110101100
//0110011001100
//0011011001100 -> 0010000
//87528
//306936
//307076 00100001011 ->01000010 -> 01000010
//328524: 01100000011
//329000: 01001001011
//1243536: 01100000011


                    if (passedIndex == 2) {
                        if (result == oldResult) {
                            passedIndex = 0
                        } else {
                            passedIndex = 1
                        }

                        bitArray = bitArray.plus(if (result == oldResult) "0" else "1")
                        if (bitArray.length == 11) {

                            if (passed in 308056..1222592) {
                                count++
                                val elements = bitArray.substring(1, 9).reversed()
//                                println(((count%31)))
                                if (((count % 31 != 30)))
                                    stringArray.add(elements)

                                val decimal: Int = elements.toInt(2)

                                val hexStr = decimal.toString(16)
                                print("$elements ")
                            }
                            bitArray = ""

                        }

                    }


                    oldResult = result


                    oldDiff = diff
                    totalSum = 0
                }

                if (passed % 2 == 0) {
                    val short = buffer.short

                    totalSum += short
                }


                /*if (passed!=0 && passed % 7 == 0) {

                    totalSum = 0
                }*/
//                totalSum += toUShort.toInt()

//                println("passed:$passed sum:${sum}")


                if (passed > 1400000) {


                    println()
                    return
                }

                passed++
            }


        }

    }





}
fun Any.getFileFromPath(fileName: String): File? {
    val classLoader = javaClass.classLoader
    val resource = classLoader.getResource(fileName)
    return if (resource != null) {
        File(resource.path)
    } else null
}