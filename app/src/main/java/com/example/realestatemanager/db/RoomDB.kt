package com.example.realestatemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.realestatemanager.model.RealEstate

//Our source of data in the app. Only one instance can exist of this class. This class
@Database(entities = [RealEstate::class], version = 2)
abstract class RoomDB : RoomDatabase() {

    abstract val dao: RealEstateDao?

    companion object {
        private const val DB_NAME = "R_ESTATE"
        private var instance: RoomDB? = null
        fun getInstance(context: Context): RoomDB? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDB::class.java,
                        DB_NAME
                )
                        .fallbackToDestructiveMigration()
                        .build()
            }
            return instance
        }
    }
}