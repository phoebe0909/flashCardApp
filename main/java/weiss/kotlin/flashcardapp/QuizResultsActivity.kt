package weiss.kotlin.flashcardapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//This Activity displays the results after user has finished a quiz and allows them to continue to on
//ResultsBreakdownActivity, where they can review which flashcards were correct or incorrect.
//TODO: Clearly, we need more pizazz here. Animations and graphics that reward good quiz results.
class QuizResultsActivity : AppCompatActivity() {
    private lateinit var flashCardList: MutableList<Card?>
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_results)
        setSupportActionBar(findViewById(R.id.toolbarResults))
        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("recentLocation", "QuizResultsActivity")
        editor.apply()

        //If the flashCardList is null, return to MainActivity. Otherwise, get the list.
        if (getList("flashCardList") == null){
            println("list is null")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }else {
            flashCardList = getList("flashCardList")!!
        }

        //Displays the number of correct and incorrect answers from QuizActivity.
        val correctText: TextView = findViewById(R.id.correctView)
        val incorrectText: TextView = findViewById(R.id.incorrectView)
        val correct = sharedPref.getInt("correct", 0)
        val incorrect = sharedPref.getInt("incorrect", 0)
        //TODO: Use string values here.
        correctText.text = "Correct Answers: $correct"
        incorrectText.text = "Wrong Answers: $incorrect"

        val resultsButton: Button = findViewById(R.id.viewResultsButton)
        resultsButton.setOnClickListener{
            val intent = Intent(this, ResultsBreakdownActivity::class.java)
            startActivity(intent)
        }
    }

    //See MainActivity
    fun getList(key: String?): MutableList<Card?>? {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<MutableList<Card?>?>() {}.type
        println(gson.toString())
        return gson.fromJson(json, type)
    }


    @Override
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu)
        // Associate searchable configuration with the SearchView
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        (menu.findItem(R.id.search).actionView as SearchView).apply {
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//        }
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
        }R.id.folders -> {
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