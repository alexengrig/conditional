package dev.alexengrig.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ConditionalTest {

    interface MethodTest {

        void should_check_success_case();

        default void should_check_failure_case() {
        }
    }

    @Nested
    class EmptyMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.empty().hasNo());
        }
    }

    @Nested
    class OfMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.of("string").has());
            assertTrue(Conditional.of(null).hasNo());
        }
    }

    @Nested
    class OfOptionalMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertTrue(Conditional.ofOptional(Optional.of("string")).has());
            assertTrue(Conditional.ofOptional(Optional.empty()).hasNo());
        }

        @Test
        @Override
        @SuppressWarnings("OptionalAssignedToNull")
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.ofOptional(null));
        }
    }

    @Nested
    class HasMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").hasNo());
            assertTrue(Conditional.empty().hasNo());
        }
    }

    @Nested
    class HasNoMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {

        }
    }

    @Nested
    class IfHasMethodTest implements MethodTest {

        @Test
        @Override
        public void should_check_success_case() {
            Conditional.of("string").ifHas(s -> !s.isEmpty(), Assertions::assertTrue);
            Conditional.<String>empty().ifHas(s -> !s.isEmpty(), ignore -> fail());
        }

        @Test
        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().ifHas(null, null));
            assertThrows(NullPointerException.class, () ->
                    Conditional.empty().ifHas(Objects::isNull, null));
        }
    }

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
                    Conditional.empty().ifHasOrElse(Objects::isNull, r -> {}, null));
        }
    }
//TODO add other tests
    @Nested
    class OrElseMethodTest implements MethodTest {

        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElse(String::isEmpty, true));
            assertTrue(Conditional.<String>empty().orElse(String::isEmpty, true));
        }

        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () -> Conditional.empty().orElse(null, true));
        }
    }

    @Nested
    class OrElseTrueMethodTest implements MethodTest {

        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseTrue(String::isEmpty));
            assertTrue(Conditional.<String>empty().orElseTrue(String::isEmpty));
        }

        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () -> Conditional.empty().orElseTrue(null));
        }
    }

    @Nested
    class OrElseFalseMethodTest implements MethodTest {

        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseFalse(String::isEmpty));
            assertFalse(Conditional.<String>empty().orElseFalse(String::isEmpty));
        }

        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () -> Conditional.empty().orElseFalse(null));
        }
    }

    @Nested
    class OrElseGetMethodTest implements MethodTest {

        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseGet(String::isEmpty, () -> true));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, () -> true));

            BooleanSupplier trueSupplier = this::trueSupplier;
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, trueSupplier));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, this::trueSupplier));

            Supplier<Boolean> boxedTrueSupplier = this::boxedTrueSupplier;
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, boxedTrueSupplier));
            assertTrue(Conditional.<String>empty().orElseGet(String::isEmpty, this::boxedTrueSupplier));
        }

        private boolean trueSupplier() {
            return true;
        }

        private Boolean boxedTrueSupplier() {
            return true;
        }

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

    @Nested
    class OrElseThrowMethodTest implements MethodTest {

        @Override
        public void should_check_success_case() {
            assertFalse(Conditional.of("string").orElseThrow(String::isEmpty));
            assertThrows(NoSuchElementException.class, () ->
                    Conditional.<String>empty().orElseThrow(String::isEmpty));
        }

        @Override
        public void should_check_failure_case() {
            assertThrows(NullPointerException.class, () ->
                    Conditional.of("string").orElseThrow(null));
        }
    }

    static class User {

        private final String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}