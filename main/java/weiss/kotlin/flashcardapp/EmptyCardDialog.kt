package weiss.kotlin.flashcardapp


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

//This dialog appears in Editing Activity if the user tries to save an empty card.
class EmptyCardDialog : DialogFragment(){

    internal lateinit var listener: EmptyDialogListener

    interface EmptyDialogListener {
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EmptyDialogListener
            println("dialog listener attached")
        } catch (e: ClassCastException) {
            println("listener exception")
            throw ClassCastException((context.toString() +
                    " must implement SaveChangesDialogListener"))
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.emptyDialog)

                .setNegativeButton(R.string.discard,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogNegativeClick(this)
                    })
                .setNeutralButton(R.string.editEmptyDialog,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog. I'm leaving this empty because it seems to be working as-is.
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}