package com.xjtlu.slip;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class TestFaker {
    @Test
    public void testFaker() {
        Faker faker = new Faker(new Locale("zn_CN"));
        Name name = faker.name();
        // get fake name,email,address,phone number,company name,company email,company phone number

    }
}
