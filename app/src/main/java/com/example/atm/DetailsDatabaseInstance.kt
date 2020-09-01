package com.example.atm

import android.app.Application
import androidx.room.Room

class DetailsDatabaseInstance : Application() {
    companion object {
        var DETAILSBASE: DetailsDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        DETAILSBASE = Room.databaseBuilder(this, DetailsDatabase::class.java, "my_db")
            .fallbackToDestructiveMigration().build()

    }
}