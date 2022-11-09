package weiss.kotlin.flashcardapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

//TODO: This class may be updated as we shift towards other methods of data storage.

//FolderAdapter is the recyclerView adapter for MainActivity. It displays the names of the available folders.
open class FolderAdapter(private val mList: List<String>) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {
    private lateinit var mycontext: Context
    private lateinit var sharedPref: SharedPreferences
    private val TAG = "CustomAdapterActivity"

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val folderName: TextView = itemView.findViewById(R.id.folderName)
        val folderEditButton: FloatingActionButton = itemView.findViewById(R.id.folderEditButton)
        val background: TextView = itemView.findViewById(R.id.backgroundView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.folders_view_design, parent, false)
        mycontext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        sharedPref = mycontext.getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        val current = mList[position]


        //TODO: Need an ELSE to deal with null lists here.
        if (current != null) {
            holder.folderName.text = current
        }
        var myColor = ColorPicker.getFolderColor(position, mycontext)
        holder.background.setBackgroundColor(myColor)

        //Position 0 is reserved for the folder that a user can click to create a new folder. Therefore
        //editButton should not be available.
        if (position == 0){
            holder.folderEditButton.visibility = View.GONE
        }

        //If the user clicks on folder 0, they are directed to Editing Activity to create a new folder.
        //Otherwise, they will be directed to OptionsPage, carrying along with them the selected folder.
        holder.folderName.setOnClickListener{
            if (position == 0){
                val intent = Intent(mycontext, EditingActivity::class.java)
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putInt("current", -5)
                editor.putString("currentFolder", "")
                editor.apply()
                startActivity(mycontext, intent, Bundle.EMPTY)
                Toast.makeText(mycontext, "Edit", Toast.LENGTH_SHORT).show()
            }else {
                chooseFolder(current)
            }
        }

        //TODO: editButton will allow the user to edit a folder's name, merge it with another folder or delete it.
        holder.folderEditButton.setOnClickListener{
            Toast.makeText(mycontext, "Edit", Toast.LENGTH_SHORT).show()
        }

    }

    //User selects a folder and is redirected to OptionsPage.
    fun chooseFolder(string: String) {
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString("currentFolder", string)
            editor.apply()
            val intent = Intent(mycontext, OptionsPage::class.java)
            startActivity(mycontext, intent, Bundle.EMPTY)
    }

        override fun getItemCount(): Int {
            return mList.size
        }
}