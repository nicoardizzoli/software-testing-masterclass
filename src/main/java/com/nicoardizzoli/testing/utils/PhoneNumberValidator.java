package com.nicoardizzoli.testing.utils;

import java.util.function.Predicate;

public class PhoneNumberValidator implements Predicate<String> {

    @Override
    public boolean test(String phoneNumber) {
        return phoneNumber.startsWith("+44") && phoneNumber.length() == 13;
    }




    public PhoneNumberValidator() {
    }

}
