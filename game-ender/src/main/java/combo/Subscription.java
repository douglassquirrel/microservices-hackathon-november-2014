package combo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Subscription<T> {

    Optional<T> nextFact();

    void forEach(Consumer<T> factConsumer);

    default Subscription<T> filter(final Predicate<T> predicate) {
        final Subscription<T> thisSubscription = this;
        return new Subscription<T>() {
            @Override public Optional<T> nextFact() {
                return thisSubscription.nextFact();
            }

            @Override public void forEach(final Consumer<T> factConsumer) {
                thisSubscription.forEach(t -> {
                    if (predicate.test(t)) {
                        factConsumer.accept(t);
                    }
                });
            }
        };
    }
}
