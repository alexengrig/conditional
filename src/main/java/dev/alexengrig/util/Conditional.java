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
     * if {@code value} is {@code null} then {@code empty()}
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
     * if {@code optional} is empty, then {@code empty()}
     * @throws NullPointerException if {@code optional} is {@code null}
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

    public void isPresent(Predicate<? super T> predicate, Consumer<? super Boolean> action) {
        requireNonNull(predicate);
        requireNonNull(action);
        if (isPresent()) {
            action.accept(predicate.test(value));
        }
    }

    public void ifHasOrElse(Predicate<? super T> predicate, Consumer<? super Boolean> action, Runnable emptyAction) {
        requireNonNull(predicate);
        requireNonNull(action);
        requireNonNull(emptyAction);
        if (isPresent()) {
            action.accept(predicate.test(value));
        } else {
            emptyAction.run();
        }
    }

    public Conditional<T> filter(Predicate<? super T> filter) {
        requireNonNull(filter);
        if (isEmpty() || filter.test(value)) {
            return this;
        } else {
            return empty();
        }
    }

    public Conditional<T> evaluate(Predicate<Conditional<? extends T>> evaluator) {
        requireNonNull(evaluator);
        if (isEmpty() || evaluator.test(this)) {
            return this;
        } else {
            return empty();
        }
    }

    public <U> Conditional<U> map(Function<? super T, ? extends U> mapper) {
        requireNonNull(mapper);
        if (isPresent()) {
            return Conditional.of(mapper.apply(value));
        } else {
            return empty();
        }
    }

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
