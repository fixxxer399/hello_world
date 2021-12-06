package com.example.helloworld

import android.util.Log
import androidx.lifecycle.*
import com.example.helloworld.data.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    //private val _responseLiveData = MutableLiveData<Cursor>()
    //private val subscriptions = CompositeDisposable()

    // using StateFlow instead of LiveData
    private val _responseFlow = MutableStateFlow(emptyList<DataModel>())
    val responseFlow: StateFlow<List<DataModel>> get() = _responseFlow

    //val responseLiveDate: LiveData<Cursor> get() = _responseLiveData

    init {
        viewModelScope.launch(Dispatchers.Main) {
            repository.queryFlow
                .debounce(350)
                .flatMapLatest { Log.d("Kotlin", "Query $it"); flowOf(repository.suspendSearch(it)) }
                .flowOn(Dispatchers.IO)
                .collect {
                    _responseFlow.value = it
                }
        }


        /*repository.responseObservable
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
            .addTo(subscriptions)*/
    }

    fun search(word: String) = repository.setQueryValue(word) //repository.searchWord(word)
}