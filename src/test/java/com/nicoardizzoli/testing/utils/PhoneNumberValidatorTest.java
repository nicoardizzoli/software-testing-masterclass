package com.nicoardizzoli.testing.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTestPhoneNumberValidator;

    @BeforeEach
    void setUp() {
        underTestPhoneNumberValidator = new PhoneNumberValidator();
    }

    @Test
    void itShouldValidatePhoneNumber() {
        //Given
        String phoneNumber = "+447000000000";

        //When
        boolean isValid = underTestPhoneNumberValidator.test(phoneNumber);

        //Then
        Assertions.assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"+447000000000, true", "+44700000000220, false"})
    void itShouldValidatePhoneNumberWithParameters(String phoneNumber, boolean expected) {
        //Given

        //When
        boolean isValid = underTestPhoneNumberValidator.test(phoneNumber);

        //Then
        Assertions.assertThat(isValid).isEqualTo(expected);

    }
}
