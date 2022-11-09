package weiss.kotlin.flashcardapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

//This dialog is called in the Editing Activity when a user attempts to create a new card
//while the current card is still being edited.
class CreateNewCardDialog  : DialogFragment(){

    private val TAG = "CreateNewCardDialogActivity"
    internal lateinit var listener: CreateNewCardListener


    interface CreateNewCardListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
        fun onDialogNeutralClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            // Instantiate the CreateNewCardListener so we can send events to the host
            listener = context as CreateNewCardListener
            println("dialog listener attached")
        } catch (e: ClassCastException) {
            println("listener exception")
            throw ClassCastException((context.toString() +
                    " must implement CreateNewCardListener"))
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.createNewDialog)

                .setPositiveButton(R.string.confirm,
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d(TAG, "save button pressed.")
                        listener.onDialogPositiveClick(this)
                    })
                .setNegativeButton(R.string.discard,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogNegativeClick(this)
                    })
                .setNeutralButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}