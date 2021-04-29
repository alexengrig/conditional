package dev.alexengrig.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Conditional<T> {

    private static final Conditional<?> EMPTY = new Conditional<>(null);

    private final T value;

    private Conditional(T value) {
        this.value = value;
    }

    private static <T> Conditional<T> empty() {
        @SuppressWarnings("unchecked")
        Conditional<T> target = (Conditional<T>) EMPTY;
        return target;
    }

    public static <T> Conditional<T> of(T value) {
        return new Conditional<>(value);
    }

    public boolean isNull() {
        return value == null;
    }

    public boolean nonNull() {
        return value != null;
    }

    public Conditional<T> filter(Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        if (isNull() || filter.test(value)) {
            return this;
        } else {
            return empty();
        }
    }

    public Conditional<T> evaluate(Predicate<Conditional<T>> evaluator) {
        Objects.requireNonNull(evaluator);
        if (isNull() || evaluator.test(of(value))) {
            return this;
        } else {
            return empty();
        }
    }

    public <U> Conditional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (nonNull()) {
            return Conditional.of(mapper.apply(value));
        } else {
            return empty();
        }
    }

    public boolean get(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
