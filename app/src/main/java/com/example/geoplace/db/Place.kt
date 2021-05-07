package com.example.geoplace.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "places")
 data class Place(
  var name:String,

  @PrimaryKey val id:Int?=null
)
