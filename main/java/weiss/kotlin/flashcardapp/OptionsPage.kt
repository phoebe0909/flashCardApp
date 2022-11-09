package weiss.kotlin.flashcardapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type

//OptionsPage is the hub of the app. From here, you can choose your activity: Creating new cards,
//taking a quiz, browsing and editing cards, and any other future activities we add.
//TODO: OptionsPage currently has very basic UI. It should be bright and inviting.
class OptionsPage : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var flashCardList: MutableList<Card?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options_page)
        setSupportActionBar(findViewById(R.id.toolbarOptions))

        val scrollingButton: Button = findViewById(R.id.scrollingButton2)
        val quizModeButton: Button = findViewById(R.id.quizButton2)
        val changeFolderButton: Button = findViewById(R.id.folderActionButton2)
        val createFolderButton: Button = findViewById(R.id.createFolderButton)
        val createNewCardButton: Button = findViewById(R.id.createNewButton2)
        var currentFolderName: TextView = findViewById(R.id.currentFolderName)

        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)

        //If there is a problem with flashCardList, return to MainActivity.
        if (getList("flashCardList") == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //TODO: Send error toast?
        } else flashCardList = getList("flashCardList")!!

        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("recentLocation", "OptionsActivity")
        editor.apply()

        currentFolderName.text = sharedPref.getString("currentFolder", "")

        //This series of onClickListeners directs the user to other Activities within the app.
        scrollingButton.setOnClickListener {
                val intent = Intent(this, ScrollingActivity::class.java)
                startActivity(intent)
        }

        createNewCardButton.setOnClickListener {
                val intent = Intent(this, EditingActivity::class.java)
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putInt("current", -1)
                editor.apply()
                startActivity(intent)
        }

        quizModeButton.setOnClickListener {
                val intent = Intent(this, QuizActivity::class.java)
                startActivity(intent)
        }

        changeFolderButton.setOnClickListener {
               val intent = Intent(this, MainActivity::class.java)
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putString("currentFolder", "")
                editor.apply()
                startActivity(intent)
        }

        createFolderButton.setOnClickListener {
                val intent = Intent(this, EditingActivity::class.java)
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putString("currentFolder", "")
                editor.putInt("current", -5)
                editor.apply()
                startActivity(intent)
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

        @Override
        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            getMenuInflater().inflate(R.menu.main_menu, menu)
            return true
        }


        @Override
        override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
            R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.createNewCard -> {
                val intent = Intent(this, EditingActivity::class.java)
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putInt("current", -1)
                editor.apply()
                startActivity(intent)
                true
            }
            R.id.settings -> {
                true
            }
            R.id.folders -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.search -> {
                true
            }
            else -> {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
            }
        }

}