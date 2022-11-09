package weiss.kotlin.flashcardapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

//This is the recyclerView adapter for Scrolling Activity and QuizResultsActivity. It will also be used
//as the adapter for other upcoming quiz features.
//TODO: Refactor to a more specific name for this class.
open class CustomAdapter(private val mList: MutableList<Card?>, val listType: String) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    //The expanded position variables track whether a card is in it's fully expanded position or not.
    //Only one card may be open at a time.
    private var previousExpandedPosition = -1
    private var mExpandedPosition = -1
    private lateinit var mycontext: Context
    private lateinit var sharedPref: SharedPreferences
    private val TAG = "CustomAdapterActivity"


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val questionView: TextView = itemView.findViewById(R.id.questionView)
        val categoryView: TextView = itemView.findViewById(R.id.category_view)
        val solutionView: TextView = itemView.findViewById(R.id.solutionView)
        val editButton: FloatingActionButton = itemView.findViewById(R.id.floatingActionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scrolling_card_view_design, parent, false)
        mycontext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sharedPref = mycontext.getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        val current = mList[position]

        //Using the variable "position" in the final code block of this method was triggering an error message, so I
        //created a shadow variable to replace it, positionInt.
        val positionInt = position

        //Checking for null items to prepare for users uploading a CSV file with errors.
        if (current != null) {
            holder.questionView.text = current.question
        }else holder.questionView.text = ""

        if (current != null) {
            holder.categoryView.text = current.subjectCategory
        }else holder.questionView.text = ""

        if (current != null) {
            holder.solutionView.text = current.solution
        }else holder.questionView.text = ""

        //Adding color to the questions.
        var myColor = ColorPicker.getColor(position, mycontext)
        holder.questionView.setBackgroundColor(myColor)

        //When one item is expanded, the previous selection collapses.
        var isExpanded: Boolean = (positionInt == mExpandedPosition)
        holder.solutionView.setVisibility(if (isExpanded) View.VISIBLE else View.GONE)
        holder.editButton.setVisibility(if (isExpanded) View.VISIBLE else View.GONE)
        holder.itemView.isActivated = isExpanded
        if (isExpanded) previousExpandedPosition = positionInt

        //TODO: When we go to the editing activity, we want to return to the item we just edited and expand it.
        var previouslyOpenItem = sharedPref.getInt("current", -1)

        //When a question is clicked, it expands to show the solution and the edit button.
        holder.questionView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else positionInt
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(positionInt)
        }

        //Edit button sends us to Editing Activity.
        holder.editButton.setOnClickListener{
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt("current", positionInt)
            editor.apply()
            saveList(mList,"flashCardList")
            val intent: Intent = Intent(mycontext, EditingActivity::class.java)
            //Seems wrong that I'm using Bundle.EMPTY
            startActivity(mycontext, intent, Bundle.EMPTY)
        }

        //This allows us to use the CustomAdapter for QuizResultsActivity without allowing editing in that activity.
        if (listType == "correct" || listType == "wrong"){
            holder.editButton.visibility = View.GONE
        }

        //Sort the flashcards so that only cards from the current folder are shown.
        val currentFolder = sharedPref.getString("currentFolder", "").toString()
        if (holder.categoryView.text != currentFolder){
            holder.editButton.visibility = View.GONE
            holder.questionView.visibility = View.GONE
            holder.solutionView.visibility = View.GONE
        }
    }

        override fun getItemCount(): Int {
            return mList.size
        }

    //See MainActivity.
    fun saveList(list: MutableList<Card?>?, key: String?) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }
}