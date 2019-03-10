package com.gaston.macbook.room.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import com.gaston.macbook.room.data.Word
import com.gaston.macbook.room.repository.dao.WordDao

/**
 * Created by Gastón Saillén on 10 March 2019
 */
class WordRepository(private val wordDao:WordDao) {

    val allWords: LiveData<List<Word>> = wordDao.getAllWords()

    @WorkerThread
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }
}