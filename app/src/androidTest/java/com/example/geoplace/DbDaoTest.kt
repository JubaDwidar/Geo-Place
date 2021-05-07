package com.example.geoplace

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.geoplace.db.Dao
import com.example.geoplace.db.Place
import com.example.geoplace.db.RoomDb
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import org.junit.Rule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DbDaoTest
{

    @get:Rule
    var instantTaskExecutorRule=InstantTaskExecutorRule()


    private lateinit var dbRoom:RoomDb
    private lateinit var dbDao:Dao

    @Before
    private fun setUp(@ApplicationContext context: Context){
      dbRoom=  Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),RoomDb::class.java )
          .allowMainThreadQueries().build()
    dbDao=dbRoom.placesDao()
    }
    @After
    private fun tearDown(){
        dbRoom.close()
    }

    @Test
    private fun insertItem_returnTrue()= runBlockingTest {


        val placeTest=  Place("London",25)
        dbDao.insertNewPlace(placeTest)
        val placeItem=dbDao.getAllSavedPlace()
        assertThat(placeItem.contains(placeTest))

    }

    @Test
    private fun deleteItem_returnTrue(){
        val placeTest=  Place("cairo",20)
        dbDao.deletePlace(placeTest)
        val placeItem=dbDao.getAllSavedPlace()
        assertThat(placeItem).doesNotContain(placeTest)
    }


}