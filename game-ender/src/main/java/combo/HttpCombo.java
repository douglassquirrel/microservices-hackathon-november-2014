package combo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

final class HttpCombo implements Combo {

    private final FactProvider factProvider;
    private final FactPublisher factPublisher;
    private final TopicSubscriber topicSubscriber;

    HttpCombo(final RestTemplate restTemplate) {
        this.factProvider = new FactProvider(restTemplate);
        this.factPublisher = new FactPublisher(restTemplate);
        this.topicSubscriber = new TopicSubscriber(restTemplate);
    }

    @Override public <T> Subscription<T> subscribeTo(final String topicName, final Class<? extends T> factClass) {
        final SubscriptionId subscriptionId = topicSubscriber.subscribeTo(topicName);
        return new BlockingSubscription<>(factProvider, subscriptionId, factClass);
    }

    @Override public <T> void publishFact(final String topicName, final T fact) {
        factPublisher.publishFact(topicName, fact);
    }

    @Override public List<Map<String, Object>> allFacts(final String topicName) {
        return factProvider.allFacts(topicName, 0);
    }

    @Override public List<Map<String, Object>> allFacts(final String topicName, final int afterId) {
        return factProvider.allFacts(topicName, afterId);
    }

    private static final class BlockingSubscription<T> implements Subscription<T> {

        private final FactProvider factProvider;
        private final SubscriptionId subscriptionId;
        private final Class<? extends T> factClass;

        private BlockingSubscription(final FactProvider factProvider,
                                     final SubscriptionId subscriptionId,
                                     final Class<? extends T> factClass) {
            this.factProvider = factProvider;
            this.subscriptionId = subscriptionId;
            this.factClass = factClass;
        }

        @Override public Optional<T> nextFact() {
            return factProvider.nextFact(subscriptionId, factClass);
        }

        @Override public void forEach(final Consumer<T> factConsumer) {
            //noinspection InfiniteLoopStatement
            while (true) {
                nextFact().ifPresent(factConsumer);
            }
        }
    }

    private static final class FactProvider {

        private final RestTemplate restTemplate;

        private FactProvider(final RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        private <T> Optional<T> nextFact(final SubscriptionId subscriptionId, final Class<? extends T> classOfFact) {
            try {
                System.out.println(" [x] Fetching next fact");
                final ResponseEntity<? extends T> response = restTemplate.getForEntity(Paths.nextFact(subscriptionId), classOfFact);
                if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                    System.out.println(" [x] No content");
                    return Optional.empty();
                } else {
                    System.out.printf(" [x] Next fact for %s: '%s'%n", subscriptionId, response.getBody());
                }
                return Optional.ofNullable(response.getBody());
            } catch (final HttpServerErrorException hsee) {
                System.out.println(" [x] Server Error fetching facts");
                hsee.printStackTrace(System.err);
                return Optional.empty();
            } catch (final HttpClientErrorException hcee) {
                System.out.println(" [x] Client Error fetching facts");
                hcee.printStackTrace(System.err);
                return Optional.empty();
            }
        }

        public List<Map<String, Object>> allFacts(final String topicName, final int afterId) {
            return Arrays.asList(restTemplate.getForEntity(Paths.facts(topicName, afterId), Map[].class).getBody());
        }
    }

    private static final class TopicSubscriber {

        private final RestTemplate restTemplate;

        private TopicSubscriber(final RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        private SubscriptionId subscribeTo(final String topicName) {
            final ResponseEntity<Map> response = restTemplate.postForEntity(Paths.subscriptions(topicName), null, Map.class);
            final String comboId = (String) response.getBody().get("subscription_id");
            System.out.printf(" [x] Subscribed to %s with id %s%n", topicName, comboId);
            return new SubscriptionId(topicName, comboId);
        }
    }

    private static final class FactPublisher {

        private final RestTemplate restTemplate;

        private FactPublisher(final RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        private <T> void publishFact(final String topicName, final T fact) {
            System.out.printf(" [x] Publishing [%s]: '%s'%n", topicName, fact);
            restTemplate.postForEntity(Paths.facts(topicName), fact, Void.class);
        }
    }

    private static final class Paths {

        private static URI subscriptions(final String topicName) {
            return new UriTemplate("/topics/{topicName}/subscriptions").expand(topicName);
        }

        private static URI nextFact(final SubscriptionId subscriptionId) {
            return new UriTemplate("/topics/{topicName}/subscriptions/{subscriptionId}/next")
                    .expand(subscriptionId.topicName(), subscriptionId.comboId());
        }

        public static URI facts(final String topicName) {
            return new UriTemplate("/topics/{topicName}/facts").expand(topicName);
        }

        public static URI facts(final String topicName, final int afterId) {
            return new UriTemplate("/topics/{topicName}/facts?after_id=" + afterId).expand(topicName);
        }
    }
}
