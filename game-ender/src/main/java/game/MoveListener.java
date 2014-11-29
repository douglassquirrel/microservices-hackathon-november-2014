package game;

import java.util.HashMap;
import java.util.Map;

import static game.GameEnd.draw;
import static game.GameEnd.win;

final class MoveListener {

    private final Map<String, String[][]> boardByGameId = new HashMap<>();
    private final GameEndListener gameEndListener;

    MoveListener(final GameEndListener gameEndListener) {
        this.gameEndListener = gameEndListener;
    }

    public void move(final String gameId,
                     final String playerId,
                     final Position position) {

        if (!boardByGameId.containsKey(gameId)) {
            boardByGameId.put(gameId, new String[3][3]);
        }
        final String[][] board = boardByGameId.get(gameId);

        board[position.getX()][position.getY()] = playerId;

        for (int y = 0; y < 3; y++) {
            boolean horizontalMatch = true;
            for (int x = 0; x < 3; x++) {
                if (!playerId.equals(board[x][y])) {
                    horizontalMatch = false;
                }
            }
            if (horizontalMatch) {
                gameEndListener.gameEnded(win(gameId, playerId));
            }
        }

        for (int x = 0; x < 3; x++) {
            boolean verticalMatch = true;
            for (int y = 0; y < 3; y++) {
                if (!playerId.equals(board[x][y])) {
                    verticalMatch = false;
                }
            }
            if (verticalMatch) {
                gameEndListener.gameEnded(win(gameId, playerId));
            }
        }

        boolean leftToRightDiagonalMatch = true;
        for (int xy = 0; xy < 3; xy++) {
            if (!playerId.equals(board[xy][xy])) {
                leftToRightDiagonalMatch = false;
            }
        }
        if (leftToRightDiagonalMatch) {
            gameEndListener.gameEnded(win(gameId, playerId));
        }

        if (board[2][0] != null && board[1][1] != null && board[0][2] != null) {
            if (board[2][0].equals(board[1][1]) && board[1][1].equals(board[0][2]) && board[0][2].equals(playerId)) {
                gameEndListener.gameEnded(win(gameId, playerId));
            }
        }

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (board[x][y] == null) {
                    return;
                }
            }
        }
        gameEndListener.gameEnded(draw(gameId));
    }
}
