package uk.ac.bournemouth.ap.floodit.app

class Board(val totalLines: Int, val totalColumns: Int, val colorList: MutableList<Int>) {
    val squares = mutableListOf<Square>()

    init {
        this.paint()
    }

    private fun paint() {
        var x = totalLines;
        while (x > 0) {
            var y = totalColumns
            while (y > 0) {
                val color = colorList.random();
                val square = Square(x, y, color)
                this.squares.add(square);
                y--
            }
            x--
        }
    }

    fun getSquare(line: Int, column: Int): Square {
        return this.squares.find {
            it.line == line && it.column == column
        }!!;
    }

}