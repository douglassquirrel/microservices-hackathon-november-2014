package chat;

import combo.Combo;

import java.util.HashMap;
import java.util.Map;

import static combo.ComboFactory.httpCombo;
import static java.net.URI.create;

public final class ChatPublisher {

    public static void main(final String[] args) {
        final Combo combo = httpCombo(create("http://combo-squirrel.herokuapp.com"));

        combo.publishFact("chat", chatMessage("Hello World"));
    }

    private static Map<String, Object> chatMessage(final String message) {
        final Map<String, Object> fact = new HashMap<>();
        fact.put("who", "test");
        fact.put("says", message);
        return fact;
    }
}
