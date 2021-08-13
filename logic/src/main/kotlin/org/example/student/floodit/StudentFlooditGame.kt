package org.example.student.floodit

import uk.ac.bournemouth.ap.lib.matrix.int.ArrayMutableIntMatrix
import uk.ac.bournemouth.ap.lib.matrix.int.IntMatrix
import uk.ac.bournemouth.ap.lib.matrix.int.MutableIntMatrix
import uk.ac.bournemouth.ap.floodit.logic.FlooditGame
import uk.ac.bournemouth.ap.floodit.logic.FlooditGame.State
import java.awt.Color
import java.lang.Exception
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Concrete example implementation of the assignment.
 *
 * @param colourCount The amount of colours the game is played with.
 * @param grid The colour grid to use
 * @param round The round at this point (this should increase as turns are played).
 * @param maxTurns The amount of turns to win, before the game is lost.
 */
open class StudentFlooditGame(
    override val colourCount: Int,
    grid: IntMatrix,
    round: Int = 0,
    override val maxTurns: Int
) : FlooditGame {

    override val width: Int get() = grid.width
    override val height: Int get() = grid.height

    /**
     * Implement the round with a private set function, it shouldn't be possible to change this
     * without making an actual turn
     */
    final override var round: Int = round
        private set

    private val grid: IntMatrix = grid

    val mutableGrid = MutableIntMatrix(grid)


    /**
     * The current state of the game. Initial state is running, but can become won/lost. Private
     * set as this should be changed externally.
     */
    final override var state: State = State.RUNNING
        protected set

    init {
        if (width <= 0 || height <= 0 || maxTurns <= 0) {
            throw Exception("Invalid Parameters")
        }

        var higherColour = 0
        grid.forEach {
            if (it > higherColour) {
                higherColour = it
            }
        }

        if (colourCount <= higherColour) {
            throw Exception("Low colour count")
        }

        grid.all {
            if (it < 0) {
                throw Exception("The game cannot have negative colors")
            }
            true
        }
    }

    /**
     * A set of objects that have registered to be informed when the game state changes (after a play
     * has been made).
     */
    private val gamePlayListeners: MutableSet<FlooditGame.GamePlayListener> = HashSet()

    /**
     * A set of objects that have registered to be informed when the game has been won.
     */
    private val gameOverListeners: MutableSet<FlooditGame.GameOverListener> = HashSet()

    constructor(
        width: Int,
        height: Int,
        colourCount: Int,
        maxRounds: Int = defaultMaxRounds(width, height, colourCount),
        random: Random = Random(Random.nextLong())
    ) : this(
        colourCount,
        generateRandomGrid(width, height, colourCount, random),
        round = 0,
        maxTurns = maxRounds
    )

    fun recursiveSquareChange(
        line: Int,
        collumn: Int,
        color: Int,
        list: MutableList<Pair<Int, Int>>
    ) {
        list.add(Pair(collumn, line))
        val squareColor = this.mutableGrid[collumn, line]
        this.mutableGrid[collumn, line] = color
        val adjacentSquares = listOf(
            if (collumn < width - 1) Pair(
                Pair(collumn + 1, line),
                this.get(collumn + 1, line)
            ) else Pair(null, 0),
            if (collumn > 0) Pair(Pair(collumn - 1, line), this.get(collumn - 1, line)) else Pair(
                null,
                0
            ),
            if (line < height - 1) Pair(
                Pair(collumn, line + 1),
                this.get(collumn, line + 1)
            ) else Pair(null, 0),
            if (line > 0) Pair(Pair(collumn, line - 1), this.get(collumn, line - 1)) else Pair(
                null,
                0
            ),
        ).filter { it.second == squareColor && it.first != null && !list.contains(it.first) }
        adjacentSquares.forEach {
            recursiveSquareChange(
                it.first!!.second,
                it.first!!.first,
                color,
                list
            )
        }

    }

    override fun playColour(clr: Int) {
        if (this.round < maxTurns && this.state == State.RUNNING) {
            this.round++
            recursiveSquareChange(0, 0, clr, mutableListOf())
            notifyMove(this.round)

            val areAllColorEquals = mutableGrid.all { it == clr }
            if (areAllColorEquals) {
                this.state = State.WON
                notifyWin(this.round)
            } else if (this.round == maxTurns) {
                this.state = State.LOST
                notifyLoss(this.round)
            }
        } else {
            throw Exception("the game has already finished")
        }
    }

    override operator fun get(x: Int, y: Int): Int {
        return mutableGrid[x, y]
    }

    override fun notifyMove(round: Int) {
        gamePlayListeners.forEach { it.onGameChanged(this, round) }
    }

    override fun notifyWin(round: Int) {
        gameOverListeners.forEach { it.onGameOver(this, round, true) }
    }

    private fun notifyLoss(round: Int) {
        gameOverListeners.forEach { it.onGameOver(this, round, false) }
    }

    /**
     * Add the given listener to the set of listeners that want to listen to game updates.
     * @param listener The listener to add (if it is not there yet).
     */
    override fun addGamePlayListener(listener: FlooditGame.GamePlayListener) {
        if (listener !in gamePlayListeners) {
            gamePlayListeners.add(listener)
        }
    }

    /**
     * Remove the given listener from the game play listener set.
     * @param listener Listener to remove
     */
    override fun removeGamePlayListener(listener: FlooditGame.GamePlayListener) {
        gamePlayListeners.remove(listener)
    }

    /**
     * Add the given listener to the set of listeners that want to listen to game wins.
     * @param gameOverListener The listener to add (if it is not there yet).
     */
    override fun addGameOverListener(gameOverListener: FlooditGame.GameOverListener) {
        if (gameOverListener !in gameOverListeners) {
            gameOverListeners.add(gameOverListener)
        }
    }

    /**
     * Remove the given listener from the game win listener set.
     * @param gameOverListener Listener to remove
     */
    override fun removeGameOverListener(gameOverListener: FlooditGame.GameOverListener) {
        gameOverListeners.remove(gameOverListener)
    }

    companion object {

        fun defaultMaxRounds(width: Int, height: Int, colourCount: Int): Int {
            return sqrt(width * height * ((height + width) * 0.1) * colourCount).roundToInt()
        }

        @JvmStatic
        fun generateRandomGrid(
            width: Int,
            height: Int,
            colourCount: Int,
            random: Random
        ): IntMatrix {
            if (colourCount <= 0) {
                throw Exception("The amount of colors to randomize cannot be less than 0")
            }
            val cellCount = width * height
            val colorMaxOcurrences = cellCount / colourCount
            val totalLeftColors = cellCount % colourCount
            val leftColors = mutableListOf<Int>()
            val colors = mutableListOf<Int>()

            val colorsInLoop = mutableListOf<Int>()
            val leftColorInLoop = mutableListOf<Int>()
            (0 until colourCount).forEach {
                colorsInLoop.add(it)
                leftColorInLoop.add(it)
            }


            val grid = MutableIntMatrix(width, height, init = { i: Int, i1: Int ->

                var generatedColor = 0

                generatedColor = colorsInLoop.random()
                colorsInLoop.remove(generatedColor)

                if (colorsInLoop.isEmpty()) {
                    (0 until colourCount).forEach {
                        colorsInLoop.add(it)
                        leftColorInLoop.add(it)
                    }
                }
                generatedColor
            })

            return grid
        }

    }
}