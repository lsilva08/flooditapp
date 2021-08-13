package uk.ac.bournemouth.ap.floodit.app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewParent
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.get
import org.example.student.floodit.StudentFlooditGame
import uk.ac.bournemouth.ap.floodit.logic.FlooditGame


class GameActivity : AppCompatActivity() {

    lateinit var triesTextView: TextView

    lateinit var gridSizeTextView: TextView

    lateinit var colourCountTextView: TextView


    lateinit var gridSpinner: Spinner
    lateinit var colourSpinner: Spinner

    val colourList = listOf<String>(
        "#e63946",
        "#1d3557",
        "#fca311",
        "#f72585",
        "#fdffb6",
        "#7f5539",
        "#7b2cbf",
        "#ffb703",
        "#fae1dd",
        "#660708",
        "#c9cba3",
        "#0a85ed",
        "#1d1a05",
        "#f2e8cf",
        "#d90368",
        "#ffed66",
        "#eaeaea",
        "#7b6b43",
        "#d88c9a",
    )

    lateinit var studentFlooditGame: StudentFlooditGame

    val gridSpinners = arrayOf("2", "6", "10", "14")
    val coloursCounts = arrayOf("3", "4", "5", "6", "7", "8")

    val gameTriesByGridAndColours = listOf(
        Triple(2, 3, 1),
        Triple(2, 4, 2),
        Triple(2, 5, 2),
        Triple(2, 6, 3),
        Triple(2, 7, 4),
        Triple(2, 8, 4),

        Triple(6, 3, 5),
        Triple(6, 4, 7),
        Triple(6, 5, 8),
        Triple(6, 6, 10),
        Triple(6, 7, 12),
        Triple(6, 8, 14),

        Triple(10, 3, 8),
        Triple(10, 4, 11),
        Triple(10, 5, 14),
        Triple(10, 6, 17),
        Triple(10, 7, 20),
        Triple(10, 8, 23),

        Triple(14, 3, 12),
        Triple(14, 4, 16),
        Triple(14, 5, 20),
        Triple(14, 6, 25),
        Triple(14, 7, 29),
        Triple(14, 8, 33),

        )
    val defaultGameMaxTries = arrayOf(3, 10, 17)

    var gridSize = 6

    var colourCount = 6


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        triesTextView = findViewById(R.id.txtTries)
        gridSizeTextView = findViewById(R.id.txtGridSize)
        colourCountTextView = findViewById(R.id.txtColourCount)
        gridSpinner = findViewById(R.id.spinner4)
        colourSpinner = findViewById(R.id.spinner9)
        gridSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gridSpinner.setSelection(position)
                gridSize = parent!!.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                TODO("Not yet implemented")
            }

        }
        val gridAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gridSpinners)
        gridAdapter.setDropDownViewResource(R.layout.grid_spinner_value)
        gridSpinner.adapter = gridAdapter
        colourSpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                colourSpinner.setSelection(position)
                colourCount = parent!!.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                TODO("Not yet implemented")
            }

        }
        val colourAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coloursCounts)
        colourAdapter.setDropDownViewResource(R.layout.grid_spinner_value)
        colourSpinner.adapter = colourAdapter
        findViewById<Button>(R.id.btnStart).setOnClickListener { startNewGame() }
        startNewGame()
    }

    fun startNewGame() {
        //this.boardSize = get Screen text view value
        //change this.colour on line 54 to a new List of Random Values based on all colors List
        val gameMaxTries = gameTriesByGridAndColours.find {
            it.first == gridSize && it.second == colourCount
        }!!.third
        studentFlooditGame = StudentFlooditGame(gridSize, gridSize, colourCount, gameMaxTries)
        updateTries()
        gridSizeTextView.text = "Grid Size:" + gridSize.toString()
        colourCountTextView.text = "Colour NO:" + colourCount.toString()
        paintBoard()
        studentFlooditGame.addGameOverListener { game, rounds, isWon ->
            this.basicAlert(findViewById<LinearLayout>(R.id.parentLinearLayout), isWon)
        }
        studentFlooditGame.addGamePlayListener { game, round ->
            updateTries()
            paintBoard()
        }
    }


    fun paintBoard() {
        val parentLinearLayout = findViewById<LinearLayout>(R.id.parentLinearLayout)
        parentLinearLayout.removeAllViews()
        for (lines in 0 until (studentFlooditGame.height)) {
            val linearLayout = LinearLayout(this)
            linearLayout.id = ViewCompat.generateViewId()
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            for (collumns in 0 until (studentFlooditGame.width)) {
                val squareButton = Button(this)
                squareButton.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
                val color = studentFlooditGame.mutableGrid[collumns, lines]
                squareButton.setBackgroundColor(Color.parseColor(colourList[color]))
                squareButton.setOnClickListener {
                    studentFlooditGame.playColour(color)
                }
                linearLayout.addView(squareButton)

            }
            parentLinearLayout.addView(linearLayout)
        }
    }

    fun updateTries() {
        triesTextView.text = "${studentFlooditGame.round}/${studentFlooditGame.maxTurns}"
    }

    fun basicAlert(view: View, isWon: Boolean) {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Game is Over")
            setMessage(if (isWon) "You Win" else "You Lost")
            show()
        }

    }
}



