package chat;

import combo.Combo;

import java.util.*;

import static combo.ComboFactory.httpCombo;
import static java.net.URI.create;

public final class Chat {

    private static final List<String> messages = Arrays.asList(
            "Chuck Norris has pink underpants",
            "Chuck Norris likes Barbie Dolls",
            "Chuck Norris couldn't round-house kick a child",
            "Chuck Norris enjoys a good perm"
    );

    public static void main(final String[] args) {
        final Combo combo = httpCombo(create("http://combo-squirrel.herokuapp.com"));

        combo.subscribeTo("chat", Map.class)
                .filter(fact -> "Chuck Norris".equals(fact.get("who")))
                .forEach(fact -> {
                    combo.publishFact("chat", chatMessage(messages.get(new Random().nextInt(messages.size()))));
                });
    }

    private static Map<String, Object> chatMessage(final String message) {
        final Map<String, Object> fact = new HashMap<>();
        fact.put("who", "Bruce Lee");
        fact.put("says", message);
        return fact;
    }
}
