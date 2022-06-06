package com.cn.jvm.jvm.reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;

/**
 * @Description: ReferenceTest
 * @Author: 一方通行
 * @Date: 2021-09-07
 * @Version:v1.0
 */
public class ReferenceTest {

    @Test
    public void test() {
        List<Person> list = Lists.newArrayList(Person.create("name1"), Person.create("name2"), Person.create("name1"));

        long count = list.stream().filter(exist(Person::getName)).count();
        System.out.println("count " + count);
        String value ="";
        final int i = value.compareTo("1");

        Assertions.assertEquals(2, count);

    }

    private Predicate<Person> exist(Function<Person, String> function) {
        Map<String, Boolean> map = new HashMap<>();
        return p -> map.putIfAbsent(function.apply(p), Boolean.TRUE) == null;
    }


    private static class Person {
        private String name;

        public static Person create(String name) {
            return new Person(name);
        }

        private Person(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
