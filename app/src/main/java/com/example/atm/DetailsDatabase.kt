package com.example.atm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AccountDetails::class, MiniStatementEntity::class],
    version = 3,
    exportSchema = false
)
abstract class DetailsDatabase : RoomDatabase() {
    abstract fun details(): DetailsInterface
    abstract fun transactionDetails(): MiniStatementDao

    companion object {
        @Volatile
        var DETAILSBASE: DetailsDatabase? = null
        fun getAppDataBase(context: Context): DetailsDatabase? {
            if (DETAILSBASE == null) {
                synchronized(DetailsDatabase::class) {
                    DETAILSBASE = Room.databaseBuilder(
                        context.applicationContext,
                        DetailsDatabase::class.java,
                        "ATM"
                    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()


                }


            }

            return DETAILSBASE


        }


        fun destroyDataBase() {
            DETAILSBASE = null
        }
    }
}