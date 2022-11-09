package weiss.kotlin.flashcardapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

//This dialog appears in Editing Activity. It asks users if they want to save changes to their card.
class SaveChangesDialog : DialogFragment(){

    private val TAG = "SaveChangesDialogActivity"
    internal lateinit var listener: SaveChangesDialogListener

    interface SaveChangesDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SaveChangesDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement SaveChangesDialogListener"))
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.save_changes)

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
                        // User cancelled the dialog. I'm leaving this empty because it is working as-is.
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}