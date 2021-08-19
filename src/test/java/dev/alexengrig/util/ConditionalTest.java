package dev.alexengrig.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ConditionalTest {

    @Test
    void should_create_of_value() {
        User user = new User("admin-creator");
        Conditional<User> conditionalUser = Conditional.of(user);
        assertEquals(user.hashCode(), conditionalUser.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_create_of_optional() {
        User user = new User("admin-creator");
        Optional<User> optional = Optional.of(user);
        Conditional<User> conditional = Conditional.ofOptional(optional);
        assertEquals(user.hashCode(), conditional.hashCode());
        assertEquals(0, Conditional.ofOptional(Optional.empty()).hashCode());
    }

    @Test
    void should_be_nonNull() {
        User user = new User("admin-non-null");
        Conditional<User> conditionalUser = Conditional.of(user);
        assertTrue(conditionalUser.nonNull(), "Conditional value is null");
    }

    @Test
    void should_be_null() {
        Conditional<User> conditionalUser = Conditional.of(null);
        assertTrue(conditionalUser.isNull(), "Conditional value is not null");
    }

    @Test
    void should_check_ifHas() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Consumer<Boolean> incrementation = result -> atomicInteger.incrementAndGet();
        Conditional.of("string").ifHas(String::isEmpty, incrementation);
        assertEquals(1, atomicInteger.get());
        Conditional.<String>empty().ifHas(String::isEmpty, incrementation);
        assertEquals(1, atomicInteger.get());
    }

    @Test
    void should_check_ifHasOrElse() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Consumer<Boolean> incrementation = result -> atomicInteger.incrementAndGet();
        Conditional.of("string").ifHasOrElse(String::isEmpty, incrementation, atomicInteger::decrementAndGet);
        assertEquals(1, atomicInteger.get());
        Conditional.<String>empty().ifHasOrElse(String::isEmpty, incrementation, atomicInteger::decrementAndGet);
        assertEquals(0, atomicInteger.get());
    }

    @Test
    void should_filter_value() {
        User user = new User("admin-filter");
        Conditional<User> conditionalUser = Conditional.of(user)
                .filter(u -> "admin-filter".equals(u.getName()));
        assertEquals(user.hashCode(), conditionalUser.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_not_filter_value() {
        User user = new User("admin-not-filter");
        Conditional<User> conditionalUser = Conditional.of(user)
                .filter(u -> "user".equals(u.getName()))
                .filter(u -> fail("This predicate should not run"));
        assertEquals(0, conditionalUser.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_throw_npe_on_filter_value() {
        User user = new User("admin-npe-filter");
        assertThrows(NullPointerException.class, () -> Conditional.of(user).filter(null), "Nullable predicate");
    }

    @Test
    void should_evaluate_value() {
        User user = new User("admin-evaluator");
        Conditional<User> conditionalUser = Conditional.of(user)
                .evaluate(u -> u.map(User::getName).test("admin-evaluator"::equals));
        assertEquals(user.hashCode(), conditionalUser.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_not_evaluate_value() {
        User user = new User("admin-not-evaluator");
        Conditional<User> conditionalUser = Conditional.of(user)
                .evaluate(u -> u.map(User::getName).test("user"::equals))
                .evaluate(u -> fail("This predicate should not run"));
        assertEquals(0, conditionalUser.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_throw_npe_on_evaluate_value() {
        User user = new User("admin-npe-evaluator");
        assertThrows(NullPointerException.class, () -> Conditional.of(user).evaluate(null), "Nullable predicate");
    }

    @Test
    void should_map_value() {
        User user = new User("admin-mapper");
        Conditional<String> conditionalUserName = Conditional.of(user)
                .map(User::getName);
        assertEquals(user.getName().hashCode(), conditionalUserName.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_not_map_value() {
        User user = new User("admin-not-mapper");
        Conditional<User> conditionalUser = Conditional.of(user)
                .filter(u -> "user".equals(u.getName()))
                .map(u -> fail("This function should not run"));
        assertEquals(0, conditionalUser.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_throw_npe_on_map_value() {
        User user = new User("admin-npe-mapper");
        assertThrows(NullPointerException.class, () -> Conditional.of(user).map(null), "Nullable function");
    }

    @Test
    void should_get_with_predicate_for_nonNull() {
        User user = new User("admin-getter-predicate-non-null");
        Conditional<String> conditionalUserName = Conditional.of(user)
                .map(User::getName);
        assertTrue(conditionalUserName.test("admin-getter-predicate-non-null"::equals), "User name is not equal to 'admin-getter-predicate-non-null'");
    }

    @Test
    void should_get_with_predicate_for_null() {
        Conditional<String> conditionalString = Conditional.of(null);
        assertFalse(conditionalString.test("ignore-string"::equals), "Null is equal to 'ignore-string'");
    }

    @Test
    void should_throw_npe_on_get_with_predicate() {
        User user = new User("admin-getter-predicate-npe");
        assertThrows(NullPointerException.class, () -> Conditional.of(user).test(null), "Nullable predicate");
    }

    @Test
    void should_return_toString() {
        Conditional<String> conditional = Conditional.of("something");
        assertEquals("Conditional[something]", conditional.toString());
        Conditional<String> emptyConditional = Conditional.empty();
        assertEquals("Conditional.empty", emptyConditional.toString());
    }

    @Test
    void should_check_equals() {
        Conditional<String> conditional = Conditional.of("string");
        assertEquals(conditional, Conditional.of("string"));
        assertNotEquals(conditional, Conditional.of("non-string"));
        assertNotEquals(conditional, Conditional.empty());
    }

    interface MethodTest {
        void testSuccess();

        void testFailure();
    }

    static class OrElseMethodTest implements MethodTest {

        @Test
        @Override
        public void testSuccess() {
            assertFalse(Conditional.of("string").orElse(String::isEmpty, true));
            assertTrue(Conditional.<String>empty().orElse(String::isEmpty, true));
        }

        @Test
        @Override
        public void testFailure() {

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