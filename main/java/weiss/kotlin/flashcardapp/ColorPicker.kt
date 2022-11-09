package weiss.kotlin.flashcardapp

import android.content.Context
import androidx.core.content.ContextCompat

//This object allows us to standardize the colors of the cards throughout the app. Each card is assigned
//a color according to its place in the flashCardList. The purpose is to help with memorization -- for some,
//a color helps them remember information better.
//See colors in res/colors.xml
object ColorPicker {

    fun getColor(int: Int, context: Context): Int{
        var position = (int % 10)
        var myColor = ContextCompat.getColor(context, R.color.gray1)
        when (position) {
            -1 -> myColor = ContextCompat.getColor(context, R.color.blue1)
            -5 -> myColor = ContextCompat.getColor(context, R.color.blue2)
            0 -> myColor = ContextCompat.getColor(context, R.color.yellow1)
            1 -> myColor = ContextCompat.getColor(context, R.color.green1)
            2 -> myColor = ContextCompat.getColor(context, R.color.blue1)
            3 -> myColor = ContextCompat.getColor(context, R.color.blue2)
            4 -> myColor = ContextCompat.getColor(context, R.color.fuschia1)
            5 -> myColor = ContextCompat.getColor(context, R.color.purple1)
            6 -> myColor = ContextCompat.getColor(context, R.color.pink1)
            7 -> myColor = ContextCompat.getColor(context, R.color.gray1)
            8 -> myColor = ContextCompat.getColor(context, R.color.red1)
            else -> myColor = ContextCompat.getColor(context, R.color.orange1)
        }
        return myColor
    }

    fun getFolderColor(position: Int, context: Context): Int{
        var myColor = ContextCompat.getColor(context, R.color.gray1)
        when (position) {
            0 -> myColor = ContextCompat.getColor(context, R.color.teal_faded)
            1 -> myColor = ContextCompat.getColor(context, R.color.green1)
            2 -> myColor = ContextCompat.getColor(context, R.color.blue1)
            3 -> myColor = ContextCompat.getColor(context, R.color.blue2)
            4 -> myColor = ContextCompat.getColor(context, R.color.fuschia1)
            5 -> myColor = ContextCompat.getColor(context, R.color.purple1)
            6 -> myColor = ContextCompat.getColor(context, R.color.pink1)
            7 -> myColor = ContextCompat.getColor(context, R.color.gray1)
            8 -> myColor = ContextCompat.getColor(context, R.color.red1)
            else -> myColor = ContextCompat.getColor(context, R.color.orange1)
        }
        return myColor
    }

}