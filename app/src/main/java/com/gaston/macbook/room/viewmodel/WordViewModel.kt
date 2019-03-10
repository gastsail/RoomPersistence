package com.gaston.macbook.room.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.gaston.macbook.WordRoomDatabase
import com.gaston.macbook.room.data.Word
import com.gaston.macbook.room.repository.WordRepository
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Created by Gastón Saillén on 10 March 2019
 */
class WordViewModel(application: Application): AndroidViewModel(application) {

    private val repository: WordRepository
    val allWords: LiveData<List<Word>>

    private var parentJob = Job()

    private val coroutineContext: kotlin.coroutines.experimental.CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    init {
        val wordsDao = WordRoomDatabase.getDatabase(application, scope).wordDao()
        repository = WordRepository(wordsDao)
        allWords = repository.allWords

    }



    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun insert(word: Word) = scope.launch(Dispatchers.IO) {
        repository.insert(word)
    }
}