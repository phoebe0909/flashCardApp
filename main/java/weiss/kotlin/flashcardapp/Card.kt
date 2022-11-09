package weiss.kotlin.flashcardapp

//This is the data model for individual cards in flashCardList. (FlashCardList is the mutableList that holds all of our user's flashcards.)
//TODO: Refactor subjectCategory to a name that includes "folder" for clarity.
class Card (var subjectCategory: String, var question: String, var solution: String
): Comparable<Card>{

    //These variables track quiz results.
    var attempts = 0
    var mostRecentResult = ""
    var correctAnswers = 0

    @Override
    override fun toString(): String {
        return "Card(subjectCategory='$subjectCategory', question='$question', solution='$solution')"
    }

    override fun compareTo(other: Card): Int {
        return compareValuesBy(other, this, { it.subjectCategory }, {it.question})
    }

    //Tracking quiz results.
    fun addCorrect(x: Int){
        attempts+=x
        correctAnswers+=x
        mostRecentResult = "correct"
    }

    //Tracking quiz results.
    fun addAttempt(x: Int){
        attempts+=x
        mostRecentResult = "wrong"
    }

}

