package com.example.geoplace.db

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.StateFlow

@Dao
interface Dao {
     @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPlace( place: Place)

     @Query("Select * From places ")
     fun  getAllSavedPlace() : List<Place>

     @Delete
     fun deletePlace(place: Place)
}