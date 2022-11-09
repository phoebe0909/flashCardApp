package weiss.kotlin.flashcardapp

import android.app.SearchManager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type

//ScrollingActivity allows users to browse their flashcards and make edits.
//This activity only shows the flashcards from the current folder.
//ScrollingActivity's layout is enabled by CustomAdapter.
class ScrollingActivity : AppCompatActivity() {
    private val TAG = "Scrolling Activity"
    private lateinit var flashCardList: MutableList<Card?>
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbarScrolling))
        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)

        //RecyclerView uses CustomAdapter to display flashcards.
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewScrolling)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = CustomAdapter(flashCardList, "")
        recyclerview.adapter = adapter

        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("recentLocation", "ScrollingActivity")
        editor.apply()

        //If the flashCardList is null, return to MainActivity. Otherwise, get the list.
        if (getList("flashCardList") == null){
            println("list is null")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //create a null list dialog and exit this activity.
        }else {
            flashCardList = getList("flashCardList")!!
        }
    }

    //See MainActivity
    fun getList(key: String?): MutableList<Card?>? {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<MutableList<Card?>?>() {}.type
        println("Get array list called")
        println(gson.toString())
        return gson.fromJson(json, type)
    }

    //Searches the flashCard list for duplicates. Compares category and question only.
    //Not currently in use.
    fun searchList(cardInfo: Card): Boolean {
        for (it in flashCardList) {
            if (it != null){
               if (cardInfo.compareTo(it) == 0) {
                    println(it.toString() + " and " + cardInfo.toString())
                    return true
                }
            }
        }
        return false
    }

    //Overloads previous function to search for duplicates by string. This function should be called
    //before the user can generate a new flash card.
    fun searchList(category: String, question: String): Boolean {
        val item = Card(category, question, "Empty")
        return searchList(item)
    }


    @Override
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }


    @Override
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }R.id.createNewCard -> {
            val intent = Intent(this, EditingActivity::class.java)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt("current", -1)
            editor.apply()
            startActivity(intent)
            true

        }
        R.id.folders -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.settings -> {
            //Should send you to settings page
            true}
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}