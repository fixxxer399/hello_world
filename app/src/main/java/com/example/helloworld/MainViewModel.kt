package com.example.helloworld

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _responseLiveData = MutableLiveData<Cursor>()
    private val subscriptions = CompositeDisposable()

    val responseLiveDate: LiveData<Cursor> get() = _responseLiveData

    init {
        repository.responseObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                if (it.isNotEmpty()) {
                    val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2))
                    it.forEachIndexed { index, dataModel ->
                        matrixCursor.addRow(arrayOf(index, dataModel.text, dataModel.meanings?.toTypedArray().contentToString()))
                    }
                    _responseLiveData.value = matrixCursor
                }
            }
            .addTo(subscriptions)
    }

    fun search(word: String) = repository.searchWord(word)
}