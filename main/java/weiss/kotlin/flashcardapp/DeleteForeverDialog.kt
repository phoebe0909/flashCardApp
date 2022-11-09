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

//This dialog appears in Editing activity when a user attempts to delete a card.
class DeleteForeverDialog : DialogFragment(){

    internal lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNeutralClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as NoticeDialogListener
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
            builder.setMessage(R.string.DeleteForever)

                .setPositiveButton(R.string.Delete,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogPositiveClick(this)
                    })

                .setNeutralButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog. I'm leaving this empty because it seems to be working as-is.
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }



}