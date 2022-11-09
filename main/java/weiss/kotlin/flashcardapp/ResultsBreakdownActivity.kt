package weiss.kotlin.flashcardapp

import android.app.SearchManager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//ResultsBreakdownActivity is only accessible by finishing a quiz in QuizActivity (and potentially
//future Activities as well.) It breaks down the current folder into right and wrong answers for user review.
//TODO: This Activity could use more UI sparkle.
//ResultsBreakdownActivity references CustomAdapter for its layout.
class ResultsBreakdownActivity : AppCompatActivity() {
    private val TAG = "Results Breakdown Activity"
    private lateinit var flashCardList: MutableList<Card?>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var currentFolder: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_breakdown)
        setSupportActionBar(findViewById(R.id.toolbarResultsBreakdown))
        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("recentLocation", "ResultsBreakdownActivity")
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

        //Get the current folder. In case of null, return to main Activity.
        currentFolder = sharedPref.getString("currentFolder", "")?: ""
        if (currentFolder == "") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Log.d("TAG", "Error in Quiz Activity. No current folder present.")
        }

        var correctList: MutableList<Card?> = ArrayList<Card?>()
        var incorrectList: MutableList<Card?> = ArrayList<Card?>()

        //If a card was marked correct, add it to the correctList, etc.
        for (it in flashCardList) {
                if (it?.mostRecentResult == "correct" && it?.subjectCategory == currentFolder) {
                    correctList.add(it)
                }else if (it?.mostRecentResult == "incorrect" && it?.subjectCategory == currentFolder)
                    incorrectList.add(it)
            }

        //Display the number of correct answers and a column with all the correct answers.
        val correctInt = sharedPref.getInt("correct", 0)
        val correctText: TextView = (findViewById(R.id.correctView2))
        correctText.text = "Correct Answers: $correctInt"
        val recyclerviewCorrect = findViewById<RecyclerView>(R.id.recyclerviewCorrect)
        recyclerviewCorrect.layoutManager = LinearLayoutManager(this)
        val adapterCorrect = CustomAdapter(correctList, "correct")
        recyclerviewCorrect.adapter = adapterCorrect

        //Display the number of incorrect answers and a column with all the incorrect answers.
        val incorrectInt =  sharedPref.getInt("incorrect", 0)
        val incorrectText: TextView = (findViewById(R.id.incorrectView5))
        incorrectText.text = "Incorrect Answers: $incorrectInt"
        val recyclerviewIncorrect = findViewById<RecyclerView>(R.id.recyclerviewWrong)
        recyclerviewIncorrect.layoutManager = LinearLayoutManager(this)
        val adapterIncorrect = CustomAdapter(incorrectList, "wrong")
        recyclerviewIncorrect.adapter = adapterIncorrect
    }

    //See MainActivity
    fun saveList(list: MutableList<Card?>?, key: String?) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
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

    //Searches the flashCard list for duplicates. Compares category and question only. Not currently in use.
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
    //before the user can generate a new flash card. Not currently in use.
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