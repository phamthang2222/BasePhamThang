package com.phamthang.reverseaudio.extention

import com.phamthang.reverseaudio.helper.game.SudokuBoard
import com.phamthang.reverseaudio.helper.game.SudokuGame
import com.phamthang.reverseaudio.model.Puzzle

fun Puzzle.toSudokuGame(isEditMode: Boolean): SudokuGame {
    // Logic này được lấy cảm hứng từ hàm 'extractSudokuGameFromRawData'
    // và 'getRawGameData' trong code cũ.

    // Ưu tiên dùng 'cellsData' nếu có, nếu không thì dùng 'originalValues'.
    val boardData = this.cellsData ?: this.originalValues

    // Deserialize chuỗi dữ liệu bảng cờ thành đối tượng SudokuBoard
    val (board, dataVersion) = SudokuBoard.deserialize(boardData, isEditMode)

    // Tạo đối tượng SudokuGame và gán các thuộc tính từ Puzzle entity
    return SudokuGame(board).apply {
        id = this@toSudokuGame.id
        created = this@toSudokuGame.created
        lastPlayed = this@toSudokuGame.lastPlayed
        state = this@toSudokuGame.state
        mistakeCounter = this@toSudokuGame.mistakeCounter
        hintCounter = this@toSudokuGame.hintCounter
        playingDuration = this@toSudokuGame.time // Chú ý: Tên thuộc tính khác nhau (time vs playingDuration)
        userNote = this@toSudokuGame.userNote ?: ""
        folderId = this@toSudokuGame.folderId
        commandStack.deserialize(this@toSudokuGame.commandStack ?: "", dataVersion)
    }
}