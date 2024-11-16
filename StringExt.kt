package com.mayad.instagram.android.extension

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.google.firebase.Timestamp
import com.mayad.instagram.R
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun String.generateSuggestedUsername(): String {
    val username = this.lowercase().replace(" ", "_")
    val rand = (0..9999).random()
    val suggested = "${username}${rand}"
    return if (suggested.matches(Regex("\\w{3,20}"))) {
        suggested
    } else {
        suggested.substring(0, suggested.length - rand.toString().length) +
                (0..9999).random().toString()
    }
}

fun String.extractHashtags(): List<String> {
    val hashtagRegex = Regex("#\\w+")
    val matches = hashtagRegex.findAll(this)
    return matches.map { it.value }.toList()
}

fun String.generateSearchKeywords(): List<String> {
    val excludedWords =
        listOf("a", "an", "the", "is", "of", "to", "and") // List of excluded common words
    val inputString = this.lowercase()
    val keywords = mutableListOf<String>()

    // Split all words from the string
    val words = inputString.split(" ")

    for (word in words) {
        var appendString = ""

        // For every char in the whole word
        for (charPosition in word.indices) {
            appendString += word[charPosition].toString()
            if (!excludedWords.contains(appendString)) {
                keywords.add(appendString)
            }
        }
    }
    return keywords
}

fun String.generateUserSearchKeywords(): List<String> {
    val inputString = this.lowercase()
    val keywords = mutableSetOf<String>()

    // Split all words from the string
    val words = inputString.split(" ")

    for (word in words) {
        val wordLength = word.length
        for (i in 0 until wordLength) {
            for (j in i + 1..wordLength) {
                keywords.add(word.substring(i, j))
            }
        }

        var appendString = ""
        for (charPosition in word.indices) {
            appendString += word[charPosition].toString()
            keywords.add(appendString)
        }
    }
    return keywords.toList()
}


fun Timestamp.getReadableTimeDifference(context: Context): String {
    val resources = context.resources
    val currentTimeMillis = Timestamp.now().toDate().time
    val timestampMillis = this.seconds * 1000 + this.nanoseconds / 1000000
    val durationMillis = currentTimeMillis - timestampMillis

    return when {
        durationMillis < 60_000 -> resources.getString(R.string.time_just_now)
        durationMillis > TimeUnit.DAYS.toMillis(7) -> SimpleDateFormat(
            resources.getString(R.string.date_format),
            Locale.getDefault()
        ).format(Date(timestampMillis))
        else -> {
            val (quantityResId, quantityValue) = when {
                durationMillis > TimeUnit.DAYS.toMillis(1) -> R.plurals.time_days_ago to
                        TimeUnit.MILLISECONDS.toDays(durationMillis).toInt()
                durationMillis > TimeUnit.HOURS.toMillis(1) -> R.plurals.time_hours_ago to
                        TimeUnit.MILLISECONDS.toHours(durationMillis).toInt()
                else -> R.plurals.time_minutes_ago to
                        TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()
            }
            resources.getQuantityString(quantityResId, quantityValue, quantityValue)
        }
    }
}

fun Timestamp.getReadableTimeDifferenceFor1Hour(context: Context): String {
        val resources = context.resources
        val currentTimeMillis = System.currentTimeMillis()
        val timestampMillis = this.seconds * 1000 + this.nanoseconds / 1000000
        val durationMillis = currentTimeMillis - timestampMillis

        return when {
            durationMillis < 60_000 -> resources.getString(R.string.time_just_now)
            durationMillis > TimeUnit.DAYS.toMillis(7) -> DateFormat.format(
                resources.getString(R.string.date_format),
                Date(timestampMillis)
            ).toString()
            else -> {
                val formattedTime = DateFormat.format("hh:mm a", Date(timestampMillis)).toString()
                val (quantityResId, quantityValue) = when {
                    durationMillis > TimeUnit.DAYS.toMillis(1) -> R.plurals.time_days_ago to
                            TimeUnit.MILLISECONDS.toDays(durationMillis).toInt()
                    durationMillis > TimeUnit.HOURS.toMillis(1) -> R.plurals.time_hours_ago to
                            TimeUnit.MILLISECONDS.toHours(durationMillis).toInt()
                    else -> R.plurals.time_minutes_ago to
                            TimeUnit.MILLISECONDS.toMinutes(durationMillis).toInt()
                }
                val timeAgo = resources.getQuantityString(quantityResId, quantityValue, quantityValue)
                "$timeAgo ($formattedTime)"
            }
        }
}

fun Int.formatCount(context: Context): String {
    val resources = context.resources
    val formatString = resources.getString(R.string.count_format)

    return when {
        this < 1000 -> this.toString() // No abbreviation needed
        else -> {
            val (divisor, abbreviationResId) = when {
                this < 1_000_000 -> 1000.0 to R.string.count_abbreviation_k
                else -> 1_000_000.0 to R.string.count_abbreviation_m
            }
            val formattedCount = String.format(Locale.getDefault(), "%.1f", this / divisor)
            String.format(formatString, formattedCount, resources.getString(abbreviationResId))
        }
    }
}

fun Long.getDuration(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}