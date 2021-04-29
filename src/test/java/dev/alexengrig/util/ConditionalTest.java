package dev.alexengrig.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
                .evaluate(u -> u.map(User::getName).get("admin-evaluator"::equals));
        assertEquals(user.hashCode(), conditionalUser.hashCode(), "Hash code is incorrect");
    }

    @Test
    void should_not_evaluate_value() {
        User user = new User("admin-not-evaluator");
        Conditional<User> conditionalUser = Conditional.of(user)
                .evaluate(u -> u.map(User::getName).get("user"::equals))
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
        assertTrue(conditionalUserName.get("admin-getter-predicate-non-null"::equals), "User name is not equal to 'admin-getter-predicate-non-null'");
    }

    @Test
    void should_get_with_predicate_for_null() {
        Conditional<String> conditionalString = Conditional.of(null);
        assertFalse(conditionalString.get("ignore-string"::equals), "Null is equal to 'ignore-string'");
    }

    @Test
    void should_throw_npe_on_get_with_predicate() {
        User user = new User("admin-getter-predicate-npe");
        assertThrows(NullPointerException.class, () -> Conditional.of(user).get(null), "Nullable predicate");
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