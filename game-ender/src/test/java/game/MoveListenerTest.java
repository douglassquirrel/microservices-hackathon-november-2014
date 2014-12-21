package game;

import combo.RDG;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.org.fyodor.range.Range;

import static game.GameEnd.draw;
import static game.GameEnd.win;
import static game.Position.position;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public final class MoveListenerTest {

    @Mock
    private GameEndListener gameEndListener;

    private MoveListener moveListener;

    @Before
    public void underTest() {
        this.moveListener = new MoveListener(gameEndListener);
    }

    @Test
    public void playerMayPlayTwoGamesAndWinOneOfThem() {
        //Given
        final String winningGameId = RDG.string().next();
        final String nonWinningGameId = RDG.string().next();
        final String playerOneId = "player_1";
        final String playerTwoId = "player_2";

        //When
        moveListener.move(winningGameId, playerOneId, position(0, 0));
        moveListener.move(nonWinningGameId, playerTwoId, position(0, 0));
        moveListener.move(winningGameId, playerOneId, position(0, 1));
        moveListener.move(nonWinningGameId, playerOneId, position(0, 1));
        moveListener.move(winningGameId, playerOneId, position(0, 2));

        //Then
        verify(gameEndListener).gameEnded(win(winningGameId, playerOneId));
    }

    @Test
    public void gameEndsWithWinForPlayerWithThreeAcceptedHorizontalMoves() {
        //Given
        final String gameId = RDG.string().next();
        final String playerOneId = "player_1";
        final Integer yPos = RDG.integer(Range.closed(0, 2)).next();

        //And
        moveListener.move(gameId, playerOneId, position(0, yPos));
        moveListener.move(gameId, playerOneId, position(1, yPos));
        moveListener.move(gameId, playerOneId, position(2, yPos));

        //Then
        verify(gameEndListener).gameEnded(win(gameId, playerOneId));
    }

    @Test
    public void gameEndsWithWinForPlayerWithThreeAcceptedVerticalMoves() {
        //Given
        final String gameId = RDG.string().next();
        final String playerOneId = "player_1";
        final Integer xPos = RDG.integer(Range.closed(0, 2)).next();

        //And
        moveListener.move(gameId, playerOneId, position(xPos, 0));
        moveListener.move(gameId, playerOneId, position(xPos, 1));
        moveListener.move(gameId, playerOneId, position(xPos, 2));

        //Then
        verify(gameEndListener).gameEnded(win(gameId, playerOneId));
    }

    @Test
    public void gameEndsWithWinForPlayerWithThreeAcceptedDiagonalLeftToRightMoves() {
        //Given
        final String gameId = RDG.string().next();
        final String playerOneId = "player_1";

        //And
        moveListener.move(gameId, playerOneId, position(0, 0));
        moveListener.move(gameId, playerOneId, position(1, 1));
        moveListener.move(gameId, playerOneId, position(2, 2));

        //Then
        verify(gameEndListener).gameEnded(win(gameId, playerOneId));
    }

    @Test
    public void gameEndsWithWinForPlayerWithThreeAcceptedDiagonalRightToLeftMoves() {
        //Given
        final String gameId = RDG.string().next();
        final String playerOneId = "player_1";

        //And
        moveListener.move(gameId, playerOneId, position(2, 0));
        moveListener.move(gameId, playerOneId, position(1, 1));
        moveListener.move(gameId, playerOneId, position(0, 2));

        //Then
        verify(gameEndListener).gameEnded(win(gameId, playerOneId));
    }

    @Test
    public void gameEndsWithDrawWhenBoardIsFull() {
        //Given
        final String gameId = RDG.string().next();

        //And
        moveListener.move(gameId, RDG.string().next(), position(0, 0));
        moveListener.move(gameId, RDG.string().next(), position(0, 1));
        moveListener.move(gameId, RDG.string().next(), position(0, 2));
        moveListener.move(gameId, RDG.string().next(), position(1, 0));
        moveListener.move(gameId, RDG.string().next(), position(1, 1));
        moveListener.move(gameId, RDG.string().next(), position(1, 2));
        moveListener.move(gameId, RDG.string().next(), position(2, 0));
        moveListener.move(gameId, RDG.string().next(), position(2, 1));
        moveListener.move(gameId, RDG.string().next(), position(2, 2));

        //Then
        verify(gameEndListener).gameEnded(draw(gameId));
    }

    @Test
    public void gameDoesNotEndWhenBoardIsOneAcceptedMoveFromBeingFull() {
        //Given
        final String gameId = RDG.string().next();

        //And
        moveListener.move(gameId, RDG.string().next(), position(0, 0));
        moveListener.move(gameId, RDG.string().next(), position(0, 1));
        moveListener.move(gameId, RDG.string().next(), position(0, 2));
        moveListener.move(gameId, RDG.string().next(), position(1, 0));
        moveListener.move(gameId, RDG.string().next(), position(1, 1));
        moveListener.move(gameId, RDG.string().next(), position(1, 2));
        moveListener.move(gameId, RDG.string().next(), position(2, 0));
        moveListener.move(gameId, RDG.string().next(), position(2, 1));

        //Then
        verify(gameEndListener, never()).gameEnded(any(GameEnd.class));
    }

}
