package com.example.helloworld

import android.app.SearchManager
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.helloworld.di.DaggerAppComponent
import com.example.helloworld.di.MainViewModelFactory
import com.example.helloworld.interfaces.BaseActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity(), BaseActivity {
    private lateinit var searchView: SearchView
    private lateinit var cursorAdapter: CursorAdapter
    private lateinit var mainViewModel: MainViewModel
    private val presenter by lazy { App.INSTANCE.presenter }

    @Inject lateinit var viewModelFactory: MainViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        viewModelFactory = DaggerAppComponent.create().viewModelFactory()
        mainViewModel = viewModelFactory.create(MainViewModel::class.java)
        mainViewModel.responseLiveDate.observe(this) {
            searchView.suggestionsAdapter.changeCursor(it)
        }

        //presenter.attach(this)

        cursorAdapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_2,
            null,
            arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager

        searchView = (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        searchView.suggestionsAdapter = cursorAdapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query.length >=2) mainViewModel.search(query)  //presenter.search(query)
                else searchView.suggestionsAdapter.changeCursor(null)

                return true
            }
        })
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean = false

            override fun onSuggestionClick(position: Int): Boolean {

                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun showResult(cursor: Cursor) {
        searchView.suggestionsAdapter.changeCursor(cursor)
    }

    override fun onDestroy() {
        //presenter.detach()
        super.onDestroy()
    }
}