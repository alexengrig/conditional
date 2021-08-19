package dev.alexengrig.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Like {@code java.lang.Optional<Boolean>}.
 *
 * @param <T> the type of value
 */
public class Conditional<T> {

    private static final Conditional<?> EMPTY = new Conditional<>(null);

    private final T value;

    private Conditional(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <T> Conditional<T> empty() {
        return (Conditional<T>) EMPTY;
    }

    public static <T> Conditional<T> of(T value) {
        if (value != null) {
            return new Conditional<>(value);
        } else {
            return empty();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Conditional<T> ofOptional(Optional<? extends T> optional) {
        Objects.requireNonNull(optional);
        return optional.<Conditional<T>>map(Conditional::of).orElseGet(Conditional::empty);
    }

    public boolean isNull() {
        return value == null;
    }

    public boolean nonNull() {
        return value != null;
    }

    public void ifHas(Predicate<? super T> predicate, Consumer<? super Boolean> action) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        if (nonNull()) {
            action.accept(predicate.test(value));
        }
    }

    public void ifHasOrElse(Predicate<? super T> predicate, Consumer<? super Boolean> action, Runnable emptyAction) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(action);
        Objects.requireNonNull(emptyAction);
        if (nonNull()) {
            action.accept(predicate.test(value));
        } else {
            emptyAction.run();
        }
    }

    public Conditional<T> filter(Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        if (isNull() || filter.test(value)) {
            return this;
        } else {
            return empty();
        }
    }

    public Conditional<T> evaluate(Predicate<Conditional<? extends T>> evaluator) {
        Objects.requireNonNull(evaluator);
        if (isNull() || evaluator.test(this)) {
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

    public <U> Conditional<U> flatMap(Function<? super T, ? extends Conditional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (nonNull()) {
            @SuppressWarnings("unchecked")
            Conditional<U> other = (Conditional<U>) mapper.apply(value);
            return Objects.requireNonNull(other);
        } else {
            return empty();
        }
    }

    public Conditional<T> or(Supplier<? extends Conditional<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (nonNull()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            Conditional<T> other = (Conditional<T>) supplier.get();
            return Objects.requireNonNull(other);
        }
    }

    public Optional<T> optional() {
        if (nonNull()) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    public Stream<T> stream() {
        if (nonNull()) {
            return Stream.of(value);
        } else {
            return Stream.empty();
        }
    }

    public boolean test(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            return false;
        }
    }

    public boolean orElse(Predicate<? super T> predicate, boolean other) {
        Objects.requireNonNull(predicate);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            return other;
        }
    }

    public boolean orElseTrue(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            return true;
        }
    }

    public boolean orElseFalse(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            return true;
        }
    }

    public boolean orElseGet(Predicate<? super T> predicate, BooleanSupplier supplier) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(supplier);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            return supplier.getAsBoolean();
        }
    }

    public boolean orElseThrow(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            throw new NoSuchElementException("Value is null");
        }
    }

    public <X extends Throwable> boolean orElseThrow(Predicate<? super T> predicate, Supplier<? extends X> exceptionSupplier) throws X {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(exceptionSupplier);
        if (nonNull()) {
            return predicate.test(value);
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Conditional)) {
            return false;
        } else {
            Conditional<?> other = (Conditional<?>) obj;
            return Objects.equals(value, other.value);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return nonNull()
                ? String.format("Conditional[%s]", value)
                : "Conditional.empty";
    }
}
