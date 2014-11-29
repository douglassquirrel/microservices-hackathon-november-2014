package game;

import combo.Combo;

import java.util.UUID;

import static combo.ComboFactory.httpCombo;
import static java.net.URI.create;

public final class GameEndPublisher {

    public static void main(final String[] args) {
        final Combo combo = httpCombo(create("http://combo-squirrel.herokuapp.com"));
        combo.publishFact("game.end", GameEnd.win(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        combo.publishFact("game.end", GameEnd.draw(UUID.randomUUID().toString()));
    }
}
