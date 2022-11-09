package weiss.kotlin.flashcardapp


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//Editing Activity allows the user to create new cards and folders, or edit current cards.
//TODO: This class is currently being reconstructed to add a spinner that allows the user to choose a folder or create a new folder.
//Good to know: EditingActivity references Custom Adapter for its layout. See the next line for a list of Dialogs referenced from this activity.
class EditingActivity : AppCompatActivity(), DeleteForeverDialog.NoticeDialogListener, SaveChangesDialog.SaveChangesDialogListener, CreateNewCardDialog.CreateNewCardListener, EmptyCardDialog.EmptyDialogListener{

    private lateinit var sharedPref: SharedPreferences
    private lateinit var flashCardList: MutableList<Card?>
    lateinit var categoryView: TextView
    lateinit var questionView: EditText
    lateinit var solutionView: EditText
    lateinit var currentPosition: Integer //I can't use lateinit with a basic int?
    private var chooseDialog = "SaveChanges"
    lateinit var currentFolder: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editing)
        setSupportActionBar(findViewById(R.id.toolbar))
        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)

        categoryView =(findViewById(R.id.textViewCategory))
        questionView =(findViewById(R.id.editTextQuestion))
        solutionView =(findViewById(R.id.editTextSolution))
        var folderDropDownArrow: ImageView = (findViewById(R.id.folderDropDown))
        var folderChoiceView: TextView = (findViewById(R.id.folderChoiceView))



        currentFolder = (sharedPref.getString("currentFolder", ""))?: ""
        categoryView.text = Editable.Factory.getInstance().newEditable(currentFolder)

        //This Activity does not currently use custom colors for the flashcards. Perhaps it will in the future, TBD.
        //  var myColor = ColorPicker.getColor(currentPositionInt, applicationContext)
         // questionView.setTextColor(myColor)

        //CurrentPositionInt tracks which card will open in the Editing Activity. -1 represents a new card.
        val currentPositionInt =(sharedPref.getInt("current", -1))
        currentPosition = Integer(currentPositionInt) //Saving this as an integer because lateinit can't be used on an int.
        val currentCard: Card?

        //If we have trouble accessing the flashCardList, return the user to Main Activity.
        if (getList("flashCardList") == null){
            println("list is null")
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }else {
            flashCardList = getList("flashCardList")!!
        }

        //Converting Strings into Editables and displaying the question and answer of the card.
        try{
            if (currentPositionInt > -1){
                currentCard = flashCardList[currentPositionInt]
            if (currentCard != null){
                    categoryView.text = Editable.Factory.getInstance().newEditable(currentCard.subjectCategory)
                    solutionView.text = Editable.Factory.getInstance().newEditable(currentCard.solution)
                    questionView.text = Editable.Factory.getInstance().newEditable(currentCard.question)
                }else{ //A position less than -1 would signify a new card or new folder.
                    categoryView.setHint("Folder")
                }
            }
        }catch (e: Exception){
            println("exception in Editing Activity, set Text")
        }

        //TODO: create a spinner for users to choose a folder or create a new one. That will replace the following section of code.
        if (currentPositionInt == -5){
           // folderDropDownArrow.visibility = View.GONE
            //make spinner open
            categoryView.setHint("Enter your new folder name here.")
            questionView.setHint("Folder must contain at least one question.")
        }
        var folderDropDownStatus = "closed"
        folderDropDownArrow.setOnClickListener{
            var list =  getFolderList("folderList")
            if (folderDropDownStatus == "closed"){
                var string: String = "New Folder \n"
                for (it in list){
                    string+=("$it \n")
                }
                folderChoiceView.visibility = View.VISIBLE
                folderChoiceView.text = string
                folderDropDownStatus = "open"
            }else{
               folderChoiceView.visibility = View.GONE
               folderDropDownStatus="closed"
           }
        }
    }

    //See Main Activity
    fun getList(key: String?): MutableList<Card?>? {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<List<Card?>?>() {}.getType()
        return gson.fromJson(json, type)
    }

    //See Main Activity.
    fun saveList(list: MutableList<Card?>?, key: String?) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    //See Main Activity.
    fun getFolderList(key: String?): List<String> {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<List<String>>() {}.type
        println("Get array list called")
        println(gson.toString())
        return gson.fromJson(json, type)
    }

    //Shared Preferences "current" marks which card we may be editing in Editing Activity.
    //-1 represents a new card. We are resetting so that Editing Activity will assume a new card next time we return.
    fun resetCurrentCard(){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt("current", -1)
        editor.apply()
    }

    //Shared Preferences "recent" represents the location we came from before this activity.
    //This activity redirects us to the appropriate location. There may be more options as we create
    //more features in the app.
    fun exitEditingActivity(){
        val recent = sharedPref.getString("recentLocation", "")
        if (recent == "ScrollingActivity"){
            val intent = Intent(applicationContext, ScrollingActivity::class.java)
            startActivity(intent)
        }else if (recent == "OptionsActivity"){
            val intent = Intent(applicationContext, OptionsPage::class.java)
            startActivity(intent)
        }
        else{
            val intent = Intent(applicationContext, OptionsPage::class.java)
            startActivity(intent)
        }
    }


    //The menu that appears in the toolbar.
    @Override
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.editing_activity_menu, menu)
        return true
    }

    //Selecting options from the toolbar menu.
    @Override
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.save -> {
            if (questionView.text.toString()=="" || solutionView.text.toString()== ""){
                val emptyCardDialog = EmptyCardDialog()
                emptyCardDialog.show(supportFragmentManager, "Empty Card")
            }else {
                val dialog = SaveChangesDialog()
                dialog.show(supportFragmentManager, "Save Changes")
            }
            true
        }
        R.id.delete -> {
            if (questionView.text.toString()=="" || solutionView.text.toString()== "") {
                resetCurrentCard()
                exitEditingActivity()
            }
            else{
                chooseDialog = "DeleteButton"
                val dialog = DeleteForeverDialog()
                dialog.show(supportFragmentManager, "Delete Forever")
            }
            true
        }
        R.id.discardChanges -> {
            if (questionView.text.toString()=="" || solutionView.text.toString()== ""){
                resetCurrentCard()
                exitEditingActivity()
            }else {
                val dialog = SaveChangesDialog()
                dialog.show(supportFragmentManager, "Save Changes")
            }
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


    //When the user chooses "save" on the edit Dialog, we update the card and save it to the flashcardlist.
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (chooseDialog) {
            "DeleteButton" -> deleteCardPositive()
            else -> saveChangesPositive()
        }
        saveList(flashCardList, "flashCardList")
        resetCurrentCard()
    }

    //Delete Forever Dialog appears, user clicks Delete, then this:
    fun deleteCardPositive(){
        val position = currentPosition.toInt()
        try{
            flashCardList.remove(flashCardList[position])
            saveList(flashCardList, "flashCardList")
            Toast.makeText(applicationContext, "Card deleted.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            println("No card present")
        }
        resetCurrentCard()
        exitEditingActivity()
    }


    fun saveChangesPositive(){
            val position = currentPosition.toInt()

            //Modifying an existing card and saving changes.
            if (position > -1) {
                try {
                    flashCardList[position]!!.solution = solutionView.text.toString()
                    flashCardList[position]!!.question = questionView.text.toString()
                    flashCardList[position]!!.subjectCategory = categoryView.text.toString()
                    Toast.makeText(applicationContext, "Card saved.", Toast.LENGTH_SHORT).show()
                    saveList(flashCardList, "flashCardList")
                } catch (e: Exception) {
                    println("No card present")
                }
                resetCurrentCard()
                exitEditingActivity()
           }
            //Making a new card.
            else {
                try {
                    val solution = solutionView.text.toString()
                    val question = questionView.text.toString()
                    val category = categoryView.text.toString()
                    val card = Card(category, question, solution)
                    flashCardList.add(card)
                    saveList(flashCardList, "flashCardList")
                    Toast.makeText(applicationContext, "Card saved.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    println("No card present")
                }
                val intent = Intent(applicationContext, EditingActivity::class.java)
                startActivity(intent)
                //   ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            }
    }

    //User chooses to discard their current card.
    override fun onDialogNegativeClick(dialog: DialogFragment) {
        resetCurrentCard()
        exitEditingActivity()
    }

    override fun onDialogNeutralClick(dialog: DialogFragment) {
       //Returns to editing the card without making any changes.
    }

}