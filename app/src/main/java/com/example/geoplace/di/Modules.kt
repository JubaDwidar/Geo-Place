package com.example.geoplace.di


import android.content.Context
import androidx.room.Room
import com.example.geoplace.db.RoomDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Modules {


    @Singleton
    @Provides
    fun getRoomDb(@ApplicationContext context: Context)=Room.databaseBuilder(context,RoomDb::class.java,"placesDb").build()




    @Singleton
    @Provides
    fun getDao(Room:RoomDb)=Room.placesDao()

}