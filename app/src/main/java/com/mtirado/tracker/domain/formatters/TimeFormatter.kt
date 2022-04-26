package com.mtirado.tracker.domain.formatters

class TimeFormatter {
    fun format(time: Long): String {
        var duration = time / 1000
        val days = duration / 86400
        duration -= days * 86400
        val hours = duration / 3600
        duration -= hours * 3600
        val minutes = duration / 60
        duration -= minutes * 60
        val seconds = duration

        return when {
            (days > 0)  -> "${days}d ${hours}h"
            (hours > 0) -> "${formatSexagesimal(hours)}:${formatSexagesimal(minutes)}:${formatSexagesimal(seconds)}"
            else        -> "${formatSexagesimal(minutes)}:${formatSexagesimal(seconds)}"
        }
    }

    private fun formatSexagesimal(time: Long): String {
        return if (time < 10) "0$time" else "$time"
    }
}