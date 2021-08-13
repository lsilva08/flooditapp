package uk.ac.bournemouth.ap.floodit.app

class Square(val line: Int, val column: Int, var color: Int) {

    private fun isSameLineAdjacent(sqr: Square): Boolean {
        return (sqr.line == this.line && (sqr.column == (this.column + 1) || sqr.column == (this.column - 1)))
    }

    private fun isSameColumnAdjacent(sqr: Square): Boolean {
        return (sqr.column == this.column && (sqr.line == (this.line + 1) || sqr.line == (this.line - 1)))
    }

    fun getAdjacentSameColorSquares(board: Board): List<Square> {
        return board.squares.filter {
            (isSameLineAdjacent(it) || isSameColumnAdjacent(it)) && it.color == this.color
        }
    }
}