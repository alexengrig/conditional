package dev.alexengrig.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ConditionalTest {

    interface MethodTest {

        void should_check_success_case();

        default void should_check_failure_case() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @see Conditional#empty()
     */
    @Nested
    class EmptyMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.empty().isEmpty());
        }
    }

    /**
     * @see Conditional#of(java.lang.Object)
     */
    @Nested
    class OfMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").isPresent());
            assertTrue(Conditional.of(null).isEmpty());
        }
    }

    /**
     * @see Conditional#ofOptional(java.util.Optional)
     */
    @Nested
    class OfOptionalMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.ofOptional(Optional.of("string")).isPresent());
            assertTrue(Conditional.ofOptional(Optional.empty()).isEmpty());
        }

        @Test
        @Override
        @SuppressWarnings("OptionalAssignedToNull")
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.ofOptional(null));
        }
    }

    /**
     * @see Conditional#isPresent()
     */
    @Nested
    class IsPresentMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").isEmpty());
            assertTrue(Conditional.empty().isEmpty());
        }
    }

    /**
     * @see Conditional#isEmpty()
     */
    @Nested
    class IsEmptyMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {

        }
    }

    /**
     * @see Conditional#isPresent(java.util.function.Predicate, java.util.function.Consumer)
     */
    @Nested
    class IfHasMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            Conditional.of("string").isPresent(s -> !s.isEmpty(), Assertions::assertTrue);
            Conditional.<String>empty().isPresent(s -> !s.isEmpty(), ignore -> fail());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().isPresent(null, null));
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().isPresent(Objects::isNull, null));
        }
    }

    /**
     * @see Conditional#ifHasOrElse(java.util.function.Predicate, java.util.function.Consumer, java.lang.Runnable)
     */
    @Nested
    class IfHasOrElseMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            Conditional.empty().ifHasOrElse(Objects::isNull, ignore -> fail(), atomicInteger::incrementAndGet);
            assertEquals(1, atomicInteger.get());
            Conditional.of("string").ifHasOrElse(Objects::nonNull, Assertions::assertTrue, Assertions::fail);
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().ifHasOrElse(null, null, null));
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().ifHasOrElse(Objects::isNull, null, null));
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().ifHasOrElse(Objects::isNull, r -> {
                    }, null));
        }
    }

    /**
     * @see Conditional#filter(java.util.function.Predicate)
     */
    @Nested
    class FilterMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").filter(string -> string.startsWith("s")).isPresent());
            assertTrue(Conditional.of("string").filter(string -> string.startsWith("g")).isEmpty());
            assertTrue(Conditional.empty().filter(s -> fail()).isEmpty());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().filter(null));
        }
    }

    /**
     * @see Conditional#evaluate(java.util.function.Predicate)
     */
    @Nested
    class EvaluateMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").evaluate(Conditional::isEmpty).isEmpty());
            assertTrue(Conditional.of("string").evaluate(Conditional::isPresent).isPresent());
            assertTrue(Conditional.empty().evaluate(c -> fail()).isEmpty());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.of("string").evaluate(null));
        }
    }

    /**
     * @see Conditional#map(java.util.function.Function)
     */
    @Nested
    class MapMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").map(String::length).test(l -> l == 6));
            assertTrue(Conditional.empty().map(o -> fail()).isEmpty());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().map(null));
        }
    }

    /**
     * @see Conditional#flatMap(java.util.function.Function)
     */
    @Nested
    class FlatMapMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").flatMap(s -> Conditional.of(s.length())).test(l -> l == 6));
            assertTrue(Conditional.empty().flatMap(o -> fail()).isEmpty());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().flatMap(null));
        }
    }

    /**
     * @see Conditional#flatMapOptional(java.util.function.Function)
     */
    @Nested
    class FlatMapOptionalMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").flatMapOptional(s -> Optional.of(s.length())).test(l -> l == 6));
            assertTrue(Conditional.empty().flatMapOptional(o -> fail()).isEmpty());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().flatMapOptional(null));
        }
    }

    /**
     * @see Conditional#or(java.util.function.Supplier)
     */
    @Nested
    class OrMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string1").or(() -> Conditional.of("string2")).test("string1"::equals));
            assertTrue(Conditional.<String>empty().or(() -> Conditional.of("string")).isPresent());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().or(null));
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().or(() -> null));
        }
    }

    /**
     * @see Conditional#optional()
     */
    @Nested
    class OptionalMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            Optional<String> optional = Conditional.of("string").optional();
            assertTrue(optional.isPresent());
            assertEquals("string", optional.get());

            optional = Conditional.<String>empty().optional();
            assertFalse(optional.isPresent());
        }
    }

    /**
     * @see Conditional#stream()
     */
    @Nested
    class StreamMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            Stream<String> stream = Conditional.of("string").stream();
            Optional<String> optional = stream.findFirst();
            assertTrue(optional.isPresent());
            assertEquals("string", optional.get());

            stream = Conditional.<String>empty().stream();
            optional = stream.findFirst();
            assertFalse(optional.isPresent());
        }
    }

    /**
     * @see Conditional#test(java.util.function.Predicate)
     */
    @Nested
    class TestMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").test(s -> !s.isEmpty()));
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    Conditional.empty().test(s -> fail()));
            assertEquals("No value", exception.getMessage());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().test(null));
        }
    }

    /**
     * @see Conditional#orElse(java.util.function.Predicate, boolean)
     */
    @Nested
    class OrElseMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElse(String::isEmpty, true));
            assertTrue(Conditional.<String>empty().orElse(String::isEmpty, true));
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().orElse(null, true));
        }
    }

    /**
     * @see Conditional#orElseTrue(java.util.function.Predicate)
     */
    @Nested
    class OrElseTrueMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseTrue(String::isEmpty));
            assertTrue(Conditional.<String>empty().orElseTrue(String::isEmpty));
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () -> Conditional.empty().orElseTrue(null));
        }
    }

    /**
     * @see Conditional#orElseFalse(java.util.function.Predicate)
     */
    @Nested
    class OrElseFalseMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseFalse(String::isEmpty));
            assertFalse(Conditional.<String>empty().orElseFalse(String::isEmpty));
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () -> Conditional.empty().orElseFalse(null));
        }
    }

    /**
     * @see Conditional#orElseGet(java.util.function.Predicate, java.util.function.BooleanSupplier)
     * @see Conditional#orElseGet(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Nested
    class OrElseGetMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseGet(String::isEmpty, () -> true));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, () -> true));

            BooleanSupplier trueSupplier = this::trueSupplier;
            assertTrue(Conditional.of("").orElseGet(String::isEmpty, trueSupplier));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, trueSupplier));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, this::trueSupplier));

            Supplier<Boolean> boxedTrueSupplier = this::boxedTrueSupplier;
            assertTrue(Conditional.of("").orElseGet(String::isEmpty, boxedTrueSupplier));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, boxedTrueSupplier));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, this::boxedTrueSupplier));
        }

        private boolean trueSupplier() {
            return true;
        }

        @Test
        private Boolean boxedTrueSupplier() {
            return true;
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().orElseGet(null, this::trueSupplier));
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().orElseGet(null, this::boxedTrueSupplier));
            assertThrows(NullPointerException.class, () ->
                    Conditional.<String>empty().orElseGet(String::isEmpty, (BooleanSupplier) null));
            assertThrows(NullPointerException.class, () ->
                    Conditional.<String>empty().orElseGet(String::isEmpty, (Supplier<Boolean>) null));
        }
    }

    /**
     * @see Conditional#orElseThrow(java.util.function.Predicate)
     * @see Conditional#orElseThrow(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Nested
    class OrElseThrowMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseThrow(String::isEmpty));
            assertFalse(Conditional.of("string").orElseThrow(String::isEmpty, AssertionError::new));
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                    Conditional.<String>empty().orElseThrow(String::isEmpty));
            assertEquals("No value", exception.getMessage());
            assertThrows(AssertionError.class, () ->
                    Conditional.<String>empty().orElseThrow(String::isEmpty, AssertionError::new));
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.of("string").orElseThrow(null));
        }
    }

    /**
     * @see Conditional#equals(java.lang.Object)
     */
    @Nested
    class EqualsMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            Conditional<String> conditional = Conditional.of("string");
            assertEquals(conditional, conditional);
            assertNotEquals(conditional, new Object());
            assertEquals(conditional, Conditional.of("string"));
        }
    }

    /**
     * @see Conditional#hashCode()
     */
    @Nested
    class HashCodeMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertEquals("string".hashCode(), Conditional.of("string").hashCode());
            assertEquals(0, Conditional.empty().hashCode());
        }
    }

    /**
     * @see Conditional#toString()
     */
    @Nested
    class ToStringMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertEquals("Conditional[string]", Conditional.of("string").toString());
            assertEquals("Conditional.empty", Conditional.empty().toString());
        }
    }
}