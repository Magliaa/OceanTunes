package com.tunagold.oceantunes.storage.room

class Converters {
    class StringListConverter {
        @androidx.room.TypeConverter
        fun fromList(list: List<String>): String {
            return list.joinToString(",")
        }

        @androidx.room.TypeConverter
        fun toList(data: String): List<String> {
            return data.split(",").map { it.trim() }
        }
    }
}