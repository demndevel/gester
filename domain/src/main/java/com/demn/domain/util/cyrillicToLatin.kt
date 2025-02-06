package com.demn.domain.util

fun cyrillicToLatin(text: String): String {
    val convertedText = StringBuilder()

    text.forEach {
        val lookedUpChar = cyrillicToLatinAlphabet[it.toString()]

        if (lookedUpChar.isNullOrEmpty()) {
            convertedText.append(it)
        } else {
            convertedText.append(lookedUpChar)
        }
    }

    return convertedText.toString()
}