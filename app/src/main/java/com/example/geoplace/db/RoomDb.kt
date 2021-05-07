package com.example.geoplace.db


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Place::class],version = 1,exportSchema = false)
abstract class RoomDb : RoomDatabase() {
abstract fun placesDao():Dao
}