package game;

import combo.Combo;

import java.util.List;
import java.util.Map;

import static combo.ComboFactory.httpCombo;
import static game.Position.position;
import static java.net.URI.create;

public final class Game {

    public static void main(final String[] args) {
        final Combo combo = httpCombo(create("http://combo-squirrel.herokuapp.com"));

        final MoveListener moveListener = new MoveListener(gameEnd -> combo.publishFact("game.end", gameEnd));

        int lastFactId = 0;
        while (true) {
            final List<Map<String, Object>> facts = combo.allFacts("game.moveaccepted", lastFactId);
            if (!facts.isEmpty()) {
                lastFactId = ((Double) facts.get(facts.size() - 1).get("combo_id")).intValue();

                facts.stream()
                        .forEach(fact -> {
                            System.out.println(fact);
                            final String gameId = fact.get("game").toString();
                            final String playerId = fact.get("player").toString();
                            final Map<String, Object> position = (Map<String, Object>) fact.get("position");
                            final int x = ((Double) position.get("x")).intValue();
                            final int y = ((Double) position.get("y")).intValue();
                            moveListener.move(gameId, playerId, position(x, y));
                        });
            }

            sleep();
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(5000);
        } catch (final InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

}
