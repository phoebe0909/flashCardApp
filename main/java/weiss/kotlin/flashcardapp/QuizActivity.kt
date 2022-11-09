package weiss.kotlin.flashcardapp

import  android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.animation.doOnEnd
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//QuizActivity allows users to review their flashcards and mark which they are getting right and wrong.
//We reach QuizActivity from OptionsPage and head to QuizActivityResults when the quiz is over.
//TODO: The final quiz question has an odd animation blip before it moves to QuizResultsActivity.
//Good to know: QuizActivity a number of animations, available in the res/anim and res/animator folders.
class QuizActivity : AppCompatActivity() {

    private val TAG = "Quiz Activity"
    private lateinit var flashCardList: MutableList<Card?>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var context: Context
    private var frontIsSide1 = true //Since the cards are animated to flip over, we need to track which side of the card is visible before loading the next card.
    private lateinit var side1TextView: TextView
    private lateinit var side2TextView: TextView
    private var position = 0 //Our index number within flashCardList
    private lateinit var animRight: Animation //Slide right animation
    private lateinit var animLeft: Animation //Slide left animation
    private var correctTotal = 0 //Total number of correct answers
    private var incorrectTotal = 0 //Total number of incorrect answers
    private lateinit var currentFolder: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        setSupportActionBar(findViewById(R.id.toolbarQuiz))
        sharedPref = getSharedPreferences("preferences_file", Context.MODE_PRIVATE)
        context = applicationContext
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("recentLocation", "QuizActivity")
        editor.apply()

        //If the flashCardList is null, return to MainActivity. Otherwise, get the list.
        if (getList("flashCardList") == null){
            println("list is null")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //create a null list dialog and exit this activity.
        }else {
            flashCardList = getList("flashCardList")!!
        }

        //Get the current folder. In case of null, return to main Activity.
        currentFolder = sharedPref.getString("currentFolder", "")?: ""
        if (currentFolder == "") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            Log.d("TAG", "Error in Quiz Activity. No current folder present.")
        }

        //Flips the card from question to answer and vice versa.
        var flipButton: FloatingActionButton = findViewById(R.id.flipbutton)
        flipButton.setOnClickListener{
            animateCard()
        }

        //Users mark a correct answer. A new card loads.
        var correctButton: FloatingActionButton = findViewById(R.id.correctButton)
        correctButton.setOnClickListener{
            correctTotal+=1

            //Each Card in the list has int values for tracking performance over time.
            //See also: Card Class.
            flashCardList[position]?.addCorrect(1)

            if (flashCardList.size-1 == position){
                //If we are end the end of the flashCardList, save results and exit the quiz.
                //See also: cueNextCard and incorrectButton.setOnClickListener.
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putInt("correct", correctTotal)
                editor.putInt("incorrect", incorrectTotal)
                editor.apply()
                val intent = Intent(this, QuizResultsActivity::class.java)
                startActivity(intent)
            }else {
                //Determine which way the card is facing and begin the animation series to load a new card.
                if (frontIsSide1) {
                    side1TextView.startAnimation(animRight)
                } else {
                    side2TextView.startAnimation(animRight)
                }
            }
        }

        //Same functions as correctButton.
        var incorrectButton: FloatingActionButton = findViewById(R.id.incorrectButton)
        incorrectButton.setOnClickListener{
            Log.d(TAG, "incorrect button $position position and ${flashCardList.size} size of list")
            incorrectTotal+=1
            flashCardList[position]?.addAttempt(1)

            if (flashCardList.size-1 == position){
                saveList(flashCardList, "flashCardList")
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putInt("incorrect", incorrectTotal)
                editor.putInt("correct", correctTotal)
                editor.apply()
                val intent = Intent(this, QuizResultsActivity::class.java)
                startActivity(intent)
            }else {
                if (frontIsSide1) {
                    side1TextView.startAnimation(animLeft)
                } else {
                    side2TextView.startAnimation(animLeft)
                }
            }
        }

        //Assigning text to each side of the card and animating its movement.
        side1TextView = findViewById(R.id.side1View) as TextView
        side1TextView.movementMethod = ScrollingMovementMethod()
        side2TextView = findViewById(R.id.side2View)
        side2TextView.movementMethod = ScrollingMovementMethod()

        //Checking whether each item in the array belongs in the current folder and only displaying items that do.
        if (flashCardList[position]?.subjectCategory == currentFolder) {
            side1TextView.text = flashCardList[position]?.question ?: " "
            side2TextView.text = flashCardList[position]?.solution ?: " "
            addBorderColor()
        }else cueNextCard()

        animRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
        animLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

        //AnimationListener waits until animRight/Left have completed before cueing the next card.
        animRight.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) {
             cueNextCard()
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }
        })

        animLeft.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
            }
            override fun onAnimationEnd(p0: Animation?) {
                cueNextCard()
            }
            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
    }

    //This creates a custom colored border for each card.
    //TODO: I don't yet know how to programmatically change the color of the border only. This method is more work than necessary. Replace it with a simpler method that colors border only.
    fun addBorderColor(){
        val borderColor : Int = Color.parseColor(ColorPicker.getColor(position, context).toString())
        val backgroundColor : Int = Color.DKGRAY
        val borderWidth : Float = 15F
        val borderRadius : Float = 20F
        val borderShape = ShapeDrawable().apply {
            shape = RoundRectShape(floatArrayOf(borderRadius, borderRadius, borderRadius, borderRadius, borderRadius, borderRadius, borderRadius, borderRadius), null, null)
            paint.color = borderColor
            paint.style = Paint.Style.STROKE;
            paint.strokeWidth = borderWidth;
        }
        val backgroundShape = ShapeDrawable().apply {
            shape = RoundRectShape(floatArrayOf(borderRadius, borderRadius, borderRadius, borderRadius, borderRadius, borderRadius, borderRadius, borderRadius), null, null)
            paint.color = backgroundColor
            paint.style = Paint.Style.FILL_AND_STROKE;
        }
        val composite = LayerDrawable(arrayOf<Drawable>(backgroundShape, borderShape))
        side1TextView.background = composite
        side2TextView.background = composite
    }

    //After a card has completed the animation of sliding off screen, this method cues of up the next card by
    //animating it to slide up, adding color, adding the text of the questions and answers, and determining which side
    //is up. If it is the final card in the deck, this method will save the results and send the user to QuizResultsActivity.
    fun cueNextCard(){
        val slideUpAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            position += 1
        if (flashCardList[position]?.subjectCategory == currentFolder) {
            if (frontIsSide1) {
                side1TextView.text = flashCardList[position]?.question ?: " "
                side2TextView.text = flashCardList[position]?.solution ?: " "
                side1TextView.startAnimation(slideUpAnim)
            } else {
                side1TextView.text = flashCardList[position]?.solution ?: " "
                side2TextView.text = flashCardList[position]?.question ?: " "
                side2TextView.startAnimation(slideUpAnim)
                frontIsSide1 = false
            }
            addBorderColor()
        }else {
            position+=1
            if (position >= flashCardList.size-1){
                saveList(flashCardList, "flashCardList")
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putInt("incorrect", incorrectTotal)
                editor.putInt("correct", correctTotal)
                editor.apply()
                val intent = Intent(this, QuizResultsActivity::class.java)
                startActivity(intent)
            }else cueNextCard()
        }

    }

    //Determine which side of the card is up, and call the FlipCard animation.
    fun animateCard(){
        if (frontIsSide1) {
            flipCard(side2TextView,side1TextView)
            frontIsSide1 = false
        }else {
            flipCard(side1TextView, side2TextView)
            frontIsSide1 = true
        }
    }

    //This animation shows the card flipping from question to answer or vice versa.
    //https://medium.com/geekculture/how-to-add-card-flip-animation-in-the-android-app-3060afeadd45
    //TODO: Speed this up.
    fun flipCard(visibleView: View, inVisibleView: View) {
        try {
            visibleView.visibility = View.VISIBLE
            val scale = context.resources.displayMetrics.density
            val cameraDist = (8000) * scale
            visibleView.cameraDistance = cameraDist
            inVisibleView.cameraDistance = cameraDist
            val flipOutAnimatorSet =
                AnimatorInflater.loadAnimator(context, R.animator.flip_out) as AnimatorSet
            flipOutAnimatorSet.setTarget(inVisibleView)
            val flipInAnimationSet =
                AnimatorInflater.loadAnimator(
                    context,
                    R.animator.flip_in
                ) as AnimatorSet
            flipInAnimationSet.setTarget(visibleView)
            flipOutAnimatorSet.start()
            flipInAnimationSet.start()
            flipInAnimationSet.doOnEnd {
                inVisibleView.visibility = View.GONE
            }
        } catch (e: Exception) {
        }
    }

    //See MainActivity
    fun getList(key: String?): MutableList<Card?>? {
        val gson = Gson()
        val json: String? = sharedPref.getString(key, null)
        val type: Type = object : TypeToken<MutableList<Card?>?>() {}.type
        println(gson.toString())
        return gson.fromJson(json, type)
    }

    //See MainActivity
    fun saveList(list: MutableList<Card?>?, key: String?) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    //The saveList function must be called no matter how we exit this Activity so that the user's quiz results are preserved.
    @Override
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.home -> {
            saveList(flashCardList, "flashCardList")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }R.id.createNewCard -> {
            saveList(flashCardList, "flashCardList")
            val intent = Intent(this, EditingActivity::class.java)
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt("current", -1)
            editor.apply()
            startActivity(intent)
            true
        }
        R.id.folders -> {
            saveList(flashCardList, "flashCardList")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.settings -> {
            saveList(flashCardList, "flashCardList")
            //Should send you to settings page
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true}
        else -> {
            saveList(flashCardList, "flashCardList")
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


}
