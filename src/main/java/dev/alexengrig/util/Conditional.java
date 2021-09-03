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

import static java.util.Objects.requireNonNull;

/**
 * Like {@code java.lang.Optional<Boolean>}.
 *
 * @param <T> the type of value
 */
public class Conditional<T> {

    /**
     * Common instance for {@code empty()}.
     *
     * @since 0.1.0
     */
    private static final Conditional<?> EMPTY = new Conditional<>(null);

    /**
     * If non-null, the value for evaluating; if null, indicates no value is present.
     *
     * @since 0.1.0
     */
    private final T value;

    /**
     * Constructs an instance with the value.
     *
     * @param value the value for evaluating
     * @since 0.1.0
     */
    private Conditional(T value) {
        this.value = value;
    }

    /**
     * Returns an empty {@code Conditional} instance.
     *
     * @param <T> the type of the non-existent value
     * @return an empty {@code Conditional}
     * @since 0.1.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Conditional<T> empty() {
        return (Conditional<T>) EMPTY;
    }

    /**
     * Returns a {@code Conditional} with the given value,
     * if the value is {@code null}, then {@code empty()}.
     *
     * @param value the nullable value
     * @param <T>   the type of the value
     * @return a {@code Conditional} with {@code value},
     * if {@code value} is {@code null} then an empty {@code Conditional}
     * @since 0.1.0
     */
    public static <T> Conditional<T> of(T value) {
        if (value != null) {
            return new Conditional<>(value);
        } else {
            return empty();
        }
    }

    /**
     * Returns a {@code Conditional} with a value of the {@code Optional},
     * if the {@code Optional} is empty, then {@code empty()}.
     *
     * @param optional {@code Optional}
     * @param <T>      the type of the {@code Optional}
     * @return a {@code Conditional} with a value of {@code optional},
     * if {@code optional} is empty, then an empty {@code Conditional}
     * @throws NullPointerException if {@code optional} is {@code null}
     * @see Conditional#flatMapOptional(Function)
     * @see Conditional#optional()
     * @since 0.1.0
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Conditional<T> ofOptional(Optional<? extends T> optional) {
        requireNonNull(optional);
        return optional.<Conditional<T>>map(Conditional::of).orElseGet(Conditional::empty);
    }

    /**
     * If a value is non-{@code null}, passes the value to the given predicate and returns his result,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @param predicate the predicate for evaluating
     * @return {@code predicate}'s result for the non-{@code null} value
     * @throws NoSuchElementException if there is no value
     * @throws NullPointerException   if {@code predicate} is {@code null}
     * @apiNote The preferred alternative to this method is {@link #orElseThrow(Predicate)}.
     * @since 0.1.0
     */
    public boolean test(Predicate<? super T> predicate) {
        requireNonNull(predicate);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            throw new NoSuchElementException("No value");
        }
    }

    /**
     * If a value is not present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is not present, otherwise {@code false}
     * @since 0.1.0
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * If a value is not present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is not present, otherwise {@code false}
     * @since 0.1.0
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If a value is present, invoke the given consumer with a result of the given predicate,
     * otherwise do nothing.
     *
     * @param predicate the predicate for evaluating the value
     * @param consumer  the consumer to be executed with a result of the predicate if a value is present
     * @throws NullPointerException if {@code predicate} is null,
     *                              if {@code consumer} is null
     * @apiNote This method is like {@code if}:
     * <pre>{@code
     *     if (value != null) {
     *         consumer.accept(predicate.test(value));
     *     }
     * }</pre>
     * @since 0.1.0
     */
    public void isPresent(Predicate<? super T> predicate, Consumer<? super Boolean> consumer) {
        requireNonNull(predicate);
        requireNonNull(consumer);
        if (isPresent()) {
            consumer.accept(predicate.test(value));
        }
    }

    /**
     * If a value is present, invoke the given consumer with a result of the given predicate,
     * otherwise invoke the empty action.
     *
     * @param predicate   the predicate for evaluating the value
     * @param consumer    the consumer to be executed with a result of the predicate if the value is present
     * @param emptyAction the action to be executed if the value is not present
     * @throws NullPointerException if {@code predicate} is null,
     *                              if {@code consumer} is null,
     *                              if {@code emptyAction} is null
     * @apiNote This method is like {@code if/else}:
     * <pre>{@code
     *     if (value != null) {
     *         consumer.accept(predicate.test(value));
     *     } else {
     *         emptyAction.run();
     *     }
     * }</pre>
     * @since 0.1.0
     */
    public void ifPresentOrElse(Predicate<? super T> predicate, Consumer<? super Boolean> consumer, Runnable emptyAction) {
        requireNonNull(predicate);
        requireNonNull(consumer);
        requireNonNull(emptyAction);
        if (isPresent()) {
            consumer.accept(predicate.test(value));
        } else {
            emptyAction.run();
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code Conditional} with the value,
     * otherwise return an empty {@code Conditional}.
     *
     * @param predicate the predicate to test to the value, if present
     * @return an {@code Conditional} with the value of this {@code Conditional}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code Conditional}
     * @throws NullPointerException if {@code predicate} is null
     * @since 0.1.0
     */
    public Conditional<T> filter(Predicate<? super T> predicate) {
        requireNonNull(predicate);
        if (isEmpty() || predicate.test(value)) {
            return this;
        } else {
            return empty();
        }
    }

    /**
     * If a value is present, and this {@code Conditional} matches the given {@code Conditional}-bearing predicate,
     * return this {@code Conditional},
     * otherwise return an empty {@code Conditional}.
     *
     * @param predicate the predicate to test to this {@code Conditional}, if present
     * @return this {@code Conditional} if a value is present
     * and this {@code Conditional} matches the given {@code Conditional}-bearing predicate,
     * otherwise an empty {@code Conditional}
     * @throws NullPointerException if {@code predicate} is null
     * @apiNote This method is like {@code filter(o -> Conditional.of(o).test(predicate))}.
     * @since 0.1.0
     */
    public Conditional<T> evaluate(Predicate<Conditional<? extends T>> predicate) {
        requireNonNull(predicate);
        if (isEmpty() || predicate.test(this)) {
            return this;
        } else {
            return empty();
        }
    }

    /**
     * If a value is present, apply the given mapping function to the value,
     * and if the result is non-null, return an {@code Conditional} with the result.
     * Otherwise return an empty {@code Conditional}.
     *
     * @param <U>    the type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return an {@code Optional} describing the result of applying a mapping
     * function to the value of this {@code Optional}, if a value is present,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException if {@code mapper} is null
     * @since 0.1.0
     */
    public <U> Conditional<U> map(Function<? super T, ? extends U> mapper) {
        requireNonNull(mapper);
        if (isPresent()) {
            return Conditional.of(mapper.apply(value));
        } else {
            return empty();
        }
    }

    /**
     * If a value is present, apply the provided {@code Conditional}-bearing
     * mapping function to it, return that result,
     * otherwise return an empty {@code Conditional}.
     *
     * <p>This method is similar to {@link #map(Function)},
     * but the provided mapper is one whose result is already a {@code Conditional},
     * and if invoked, {@code flatMap} does not wrap it with an additional {@code Conditional}.
     *
     * @param <U>    the type parameter to the {@code Conditional} returned by
     * @param mapper a mapping function to apply to the value, if present
     * @return the result of applying a {@code Conditional}-bearing mapping
     * function to the value of this {@code Conditional}, if a value is present,
     * otherwise an empty {@code Conditional}
     * @throws NullPointerException if {@code mapper} is null or returns a null result
     * @since 0.1.0
     */
    public <U> Conditional<U> flatMap(Function<? super T, ? extends Conditional<? extends U>> mapper) {
        requireNonNull(mapper);
        if (isPresent()) {
            @SuppressWarnings("unchecked")
            Conditional<U> other = (Conditional<U>) mapper.apply(value);
            return requireNonNull(other);
        } else {
            return empty();
        }
    }

    /**
     * If a value is present, apply the provided {@code Optional}-bearing
     * mapping function to it, return a {@code Conditional} with that result,
     * otherwise return an empty {@code Conditional}.
     *
     * @param <U>    the type parameter to the {@code Optional} returned by
     * @param mapper a mapping function to apply to the value, if present
     * @return a {@code Conditional} with the result of applying a {@code Optional}-bearing mapping
     * function to the value of this {@code Conditional}, if a value is present,
     * otherwise an empty {@code Conditional}
     * @throws NullPointerException if {@code mapper} is null or returns a null {@code Optional}
     * @see Conditional#ofOptional(Optional)
     * @see Conditional#optional()
     * @since 0.1.0
     */
    public <U> Conditional<U> flatMapOptional(Function<? super T, ? extends Optional<? extends U>> mapper) {
        requireNonNull(mapper);
        if (isPresent()) {
            @SuppressWarnings("unchecked")
            Optional<U> other = (Optional<U>) mapper.apply(value);
            return ofOptional(requireNonNull(other));
        } else {
            return empty();
        }
    }

    public Conditional<T> or(Supplier<? extends Conditional<? extends T>> supplier) {
        requireNonNull(supplier);
        if (isPresent()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            Conditional<T> other = (Conditional<T>) supplier.get();
            return requireNonNull(other);
        }
    }

    public Optional<T> optional() {
        if (isPresent()) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    public Stream<T> stream() {
        if (isPresent()) {
            return Stream.of(value);
        } else {
            return Stream.empty();
        }
    }

    public boolean orElse(Predicate<? super T> predicate, boolean other) {
        requireNonNull(predicate);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            return other;
        }
    }

    public boolean orElseTrue(Predicate<? super T> predicate) {
        requireNonNull(predicate);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            return true;
        }
    }

    public boolean orElseFalse(Predicate<? super T> predicate) {
        requireNonNull(predicate);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            return false;
        }
    }

    public boolean orElseGet(Predicate<? super T> predicate, BooleanSupplier supplier) {
        requireNonNull(predicate);
        requireNonNull(supplier);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            return supplier.getAsBoolean();
        }
    }

    public boolean orElseGet(Predicate<? super T> predicate, Supplier<Boolean> supplier) {
        requireNonNull(predicate);
        requireNonNull(supplier);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            Boolean other = supplier.get();
            return requireNonNull(other);
        }
    }

    public boolean orElseThrow(Predicate<? super T> predicate) {
        requireNonNull(predicate);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            throw new NoSuchElementException("No value");
        }
    }

    public <X extends Throwable> boolean orElseThrow(Predicate<? super T> predicate, Supplier<? extends X> exceptionSupplier) throws X {
        requireNonNull(predicate);
        requireNonNull(exceptionSupplier);
        if (isPresent()) {
            return predicate.test(value);
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Conditional}.
     * The other object is considered equal if:
     * <ul>
     *     <li>it is also an {@code Conditional} and;
     *     <li>both instances have no value present or;
     *     <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object otherwise {@code false}
     * @since 0.1.0
     */

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

    /**
     * Returns the hash code of the value, if present, otherwise {@code 0} (zero) if no value is present.
     *
     * @return hash code value of the present value or {@code 0} if no value is present
     * @since 0.1.0
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this {@code Conditional}.
     *
     * @return the string representation of this instance
     * @since 0.1.0
     */
    @Override
    public String toString() {
        return isPresent()
                ? String.format("Conditional[%s]", value)
                : "Conditional.empty";
    }
}
