package com.gaston.macbook

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.Context
import com.gaston.macbook.room.repository.dao.WordDao
import com.gaston.macbook.room.data.Word
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch

/**
 * Created by Gastón Saillén on 10 March 2019
 */

@Database(entities = [Word::class] ,version = 1)
public abstract class WordRoomDatabase: RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope): WordRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "Word_database"
                ).addCallback(WordDatabaseCallback(scope)).build()

                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun wordDao(): WordDao


/*
To delete all content and repopulate the database whenever the app is started, we create a RoomDatabase.Callback and override onOpen().
Because we cannot do Room database operations on the UI thread, onOpen() launches a coroutine on the IO Dispatcher.
 */
    private class WordDatabaseCallback(
        private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.wordDao())
                }
            }
        }

        //Sample populate data
        fun populateDatabase(wordDao: WordDao) {
            wordDao.deleteAll()

            var word = Word("Hello")
            wordDao.insert(word)
            word = Word("World!")
            wordDao.insert(word)
        }
    }

}