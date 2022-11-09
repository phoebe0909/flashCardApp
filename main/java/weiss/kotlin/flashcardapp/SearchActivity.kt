package weiss.kotlin.flashcardapp

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


//TODO: This will be the search function from the menu toolbar. It is under construction.
class SearchActivity : Activity() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        handleIntent(intent)
        println("searchActivity onCreate called.")
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    fun getList(key: String?): MutableList<Card?>? {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<MutableList<Card?>?>() {}.type
      //  println("Get array list called")
        println(gson.toString())
        return gson.fromJson(json, type)
    }

    private fun handleIntent(intent: Intent) {
        println("Query called.")
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val flashCardList = getList("flashCardList")
            if (flashCardList != null) {
                for (it in flashCardList){
                    if (it != null) {
                        if (it.subjectCategory == query)
                            println("Found: $it.subjectCategory.toString()")
                    }
                }
            }
          //use the query to search your data somehow
        }
    }

//    //Searches the flashCard list for duplicates. Compares category and question only.
//    //Not currently in use.
//    fun searchList(cardInfo: Card): Boolean {
//        for (it in flashCardList) {
//            if (it != null){
//                if (cardInfo.compareTo(it) == 0) {
//                    println(it.toString() + " and " + cardInfo.toString())
//                    return true
//                }
//            }
//        }
//        return false
//    }
//
//    //Overloads previous function to search for duplicates by string. This function should be called
//    //before the user can generate a new flash card.
//    fun searchList(category: String, question: String): Boolean {
//        val item = Card(category, question, "Empty")
//        return searchList(item)
   // }



}