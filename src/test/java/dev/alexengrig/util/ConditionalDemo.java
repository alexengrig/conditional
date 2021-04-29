package dev.alexengrig.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionalDemo {

    @Test
    @SuppressWarnings("ConstantConditions")
    void demo_nested_parameters() {
        class Grandson {
            final String grandsonValue;
            final String value;

            Grandson(String grandsonValue, String value) {
                this.value = value;
                this.grandsonValue = grandsonValue;
            }

            public String getGrandsonValue() {
                return grandsonValue;
            }

            public String getValue() {
                return value;
            }
        }
        class Son {
            final String sonValue;
            final Grandson grandson;

            Son(String sonValue, Grandson grandson) {
                this.grandson = grandson;
                this.sonValue = sonValue;
            }

            public String getSonValue() {
                return sonValue;
            }

            public Grandson getGrandson() {
                return grandson;
            }
        }
        class Father {
            final String fatherValue;
            final Son son;

            public Father(String fatherValue, Son son) {
                this.son = son;
                this.fatherValue = fatherValue;
            }

            public String getFatherValue() {
                return fatherValue;
            }

            public Son getSon() {
                return son;
            }
        }
        Father father = new Father("father", new Son("son", new Grandson("grandson", "value")));
        // Plain old Java
        assertTrue(father != null
                && father.getFatherValue() != null && father.getFatherValue().startsWith("father")
                && father.getSon() != null
                && father.getSon().getSonValue() != null && father.getSon().getSonValue().startsWith("son")
                && father.getSon().getGrandson() != null
                && father.getSon().getGrandson().getGrandsonValue() != null && father.getSon().getGrandson().getGrandsonValue().startsWith("grandson")
                && father.getSon().getGrandson().getValue() != null && father.getSon().getGrandson().getValue().startsWith("value"));
        // Optional
        assertTrue(Optional.ofNullable(father)
                .filter(f -> f.getFatherValue() != null && f.getFatherValue().startsWith("father"))
                .map(Father::getSon)
                .filter(s -> s.getSonValue() != null && s.getSonValue().startsWith("son"))
                .map(Son::getGrandson)
                .filter(g -> g.getGrandsonValue() != null && g.getGrandsonValue().startsWith("grandson"))
                .map(Grandson::getValue)
                .filter(value -> value.startsWith("value"))
                .isPresent());
        // Conditional
        assertTrue(Conditional.of(father)
                .evaluate(conditionalFather -> conditionalFather
                        .map(Father::getFatherValue)
                        .get(fatherValue -> fatherValue.startsWith("father")))
                .map(Father::getSon)
                .evaluate(conditionalSon -> conditionalSon
                        .map(Son::getSonValue)
                        .get(sonValue -> sonValue.startsWith("son")))
                .map(Son::getGrandson)
                .evaluate(conditionalGrandson -> conditionalGrandson
                        .map(Grandson::getGrandsonValue)
                        .get(grandsonValue -> grandsonValue.startsWith("grandson")))
                .map(Grandson::getValue)
                .get(value -> value.startsWith("value")));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void demo_nullable_nested_parameters() {
        class Grandson {
            String value;
            String grandsonValue;

            public String getValue() {
                return value;
            }

            public String getGrandsonValue() {
                return grandsonValue;
            }
        }
        class Son {
            Grandson grandson;
            String sonValue;

            public Grandson getGrandson() {
                return grandson;
            }

            public String getSonValue() {
                return sonValue;
            }
        }
        class Father {
            Son son;
            String fatherValue;

            public Son getSon() {
                return son;
            }

            public String getFatherValue() {
                return fatherValue;
            }
        }
        Father father = new Father();
        // Plain old Java
        assertFalse(father != null
                && father.getFatherValue() != null && father.getFatherValue().startsWith("ignored")
                && father.getSon() != null
                && father.getSon().getSonValue() != null && father.getSon().getSonValue().startsWith("ignored")
                && father.getSon().getGrandson() != null
                && father.getSon().getGrandson().getGrandsonValue() != null && father.getSon().getGrandson().getGrandsonValue().startsWith("ignored")
                && father.getSon().getGrandson().getValue() != null && father.getSon().getGrandson().getValue().startsWith("ignored"));
        // Optional
        assertFalse(Optional.ofNullable(father)
                .filter(f -> f.getFatherValue() != null && f.getFatherValue().startsWith("ignored"))
                .map(Father::getSon)
                .filter(s -> s.getSonValue() != null && s.getSonValue().startsWith("ignored"))
                .map(Son::getGrandson)
                .filter(g -> g.getGrandsonValue() != null && g.getGrandsonValue().startsWith("ignored"))
                .map(Grandson::getValue)
                .filter(value -> value.startsWith("ignored"))
                .isPresent());
        // Conditional
        assertFalse(Conditional.of(father)
                .evaluate(conditionalFather -> conditionalFather
                        .map(Father::getFatherValue)
                        .get(fatherValue -> fatherValue.startsWith("ignored")))
                .map(Father::getSon)
                .evaluate(conditionalSon -> conditionalSon
                        .map(Son::getSonValue)
                        .get(sonValue -> sonValue.startsWith("ignored")))
                .map(Son::getGrandson)
                .evaluate(conditionalGrandson -> conditionalGrandson
                        .map(Grandson::getGrandsonValue)
                        .get(grandsonValue -> grandsonValue.startsWith("ignored")))
                .map(Grandson::getValue)
                .get(value -> value.startsWith("ignored")));
    }
}
