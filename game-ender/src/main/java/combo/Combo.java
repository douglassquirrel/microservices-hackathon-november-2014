package combo;

import java.util.List;
import java.util.Map;

public interface Combo {
    <T> Subscription<T> subscribeTo(String topic, Class<? extends T> factClass);

    <T> void publishFact(String topicName, T fact);

    List<Map<String, Object>> allFacts(String topicName);

    List<Map<String, Object>> allFacts(String topicName, int afterId);
}
