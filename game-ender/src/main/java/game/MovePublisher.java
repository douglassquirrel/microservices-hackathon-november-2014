package game;

import combo.Combo;

import static combo.ComboFactory.httpCombo;
import static game.Position.position;
import static java.net.URI.create;

public final class MovePublisher {

    public static void main(final String[] args) {
        final Combo combo = httpCombo(create("http://combo-squirrel.herokuapp.com"));
        combo.publishFact("game.moveaccepted", new MoveAccepted("game3", "player2", position(0, 0)));
        combo.publishFact("game.moveaccepted", new MoveAccepted("game3", "player2", position(1, 1)));
        combo.publishFact("game.moveaccepted", new MoveAccepted("game3", "player2", position(2, 2)));
    }
}
