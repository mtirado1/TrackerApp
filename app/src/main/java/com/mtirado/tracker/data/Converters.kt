package com.mtirado.tracker.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mtirado.tracker.domain.route.Path

class Converters {
    @TypeConverter
    public fun PathfromGson(data: String): Path = gson.fromJson(data, Path::class.java)

    @TypeConverter
    public fun PathtoGson(path: Path): String = gson.toJson(path)

    companion object {
        val gson = Gson()
    }
}