package weiss.kotlin.flashcardapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type


//MainActivity is the opening page of the App. Here, users select a folder and proceed to OptionsPage.

//TODO: Home page: create a function for users to upload their csv file or google spreadsheet.
//TODO: Additionally, create a function for users to download their flashcards as a CSV.
//TODO: Edit button lingers in view when it should already have disappeared in Scrolling Activity.
//TODO: Quiz Activity -- could we swipe left and right?
//TODO: Empty slots could appear in MainActivity to show where folders would be once they've been created. Is this a feature we would like?
//TODO: Add a Spinner to select folders in editing activity.
//TODO: Go through Toolbars to make uniform and optimize. Add search and settings functions.
//TODO: Create landscape XML for all activities.
//TODO: Quiz results breakdown needs to sort by folder.
//TODO: Continue to learn about data storage options in Android, possibly moving the user's flashcard data away from shared preferences.
//TODO: Decide whether "Home" on the toolbar should lead to MainActivity or OptionsPage.
//TODO: getList and saveList functions appear throughout the app. Can we access them from one central place?
//TODO: Think about exception handling and null items.

//Good to know: MainActivity's layout is enabled by FolderAdapter.
//All activities in the app reference the Card class indirectly, through flashCardList.
//Most activities in the app also rely on the ColorPicker object.

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var flashCardList: MutableList<Card?>
    private var filename: String = "SpanishCardsCSV.csv"
    private lateinit var folderList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar3))

        //FlashCardList carries the data for all of the folders and cards.
        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        val defaultList: MutableList<Card?> = readCSV(filename)
        flashCardList = getList("flashCardList") ?: defaultList

        //Creating a list of folders by sorting through flashCardList, then displaying it with recyclerView.
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewFolders)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val list: ArrayList<String> = ArrayList()
        list.add("Create a New Folder")
        for (it in flashCardList) {
            if (it != null) {
                if (it.subjectCategory != null) {
                        list.add(it.subjectCategory)
                }
            }
        }
        folderList = list
        folderList = folderList.distinctBy { it.uppercase() }
        val adapter = FolderAdapter(folderList)
        recyclerview.adapter = adapter

        //Saving our list of folders.
        //Saving our flashCardList to shared preferences so it will be available in other Activities.
        saveFolderList(folderList, "folderList")
        saveList(flashCardList, "flashCardList")

        //Saving our current location so other activities will redirect the user back to to the correct activity.
        //Each activity in the app does this.
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("recentLocation", "MainActivity")
        editor.apply()

    }


    //This function converts our flashCardList into a single long string that can be stored in shared preferences.
    fun saveList(list: MutableList<Card?>?, key: String?) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    //This function retrieves the flashCardList from shared preferences.
    fun getList(key: String?): MutableList<Card?>? {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<MutableList<Card?>?>() {}.type
        return gson.fromJson(json, type)
    }

    //Same as saveList, but creates a list that only includes folder names.
    fun saveFolderList(list: List<String>, key: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    //Same as getList, but folder names only.
    fun getFolderList(key: String?): List<String> {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<List<String>>() {}.type
        println("Get array list called")
        println(gson.toString())
        return gson.fromJson(json, type)
    }

    //This function reads our saved CSV, a sample folder of Spanish flashcards.
    //Refer to https://d2dreamdevelopers.blogspot.com/2021/04/read-csv-file-from-assets-folder-in.html
    fun readCSV(fileName: String): MutableList<Card?> {
        //open an input stream reader and feed it into a buffered reader.
        val input = InputStreamReader(assets.open(fileName))
        val bufferedReader = BufferedReader(input)

        //This takes the csv and turns each row into a List.
        val cardListByCategory = mutableListOf<Card?>()

        //Reads each line from the CSV file and transfers it into a MutableList.
        try {
            bufferedReader.forEachLine { it
                var row: List<String> = it.split(",")
                if (row[0] == "*Entry*") {
                    var card = Card(row[1], row[2], row[3])
                    cardListByCategory.add(card)
                }else {
                    var lastIndex = cardListByCategory.lastIndex
                    cardListByCategory[lastIndex]?.solution = (cardListByCategory[lastIndex]?.solution + "\n" + it)
                }
            }
        }catch(e: Exception){
            println("Exception in home activity readcsv")
        }
        return cardListByCategory
    }


    //Creates the menu that lives in the toolbar across the top of the screen.
    @Override
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.landing_menu, menu)
        // Associate searchable configuration with the SearchView
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        (menu.findItem(R.id.search).actionView as SearchView).apply {
//            setSearchableInfo(searchManager.getSearchableInfo(componentName))
//        }
        return true
    }

    //Selecting toolbar menu items.
    @Override
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.settings -> {
            //Should send you to settings page
            true
        } R.id.search -> {
            //Will connect to a search function.
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


}