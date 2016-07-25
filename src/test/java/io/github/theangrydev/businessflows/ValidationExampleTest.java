/*
 * Copyright 2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of business-flows.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.theangrydev.businessflows;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ValidationExampleTest {

    private static class ValidationError {
        private final String error;

        private ValidationError(String error) {
            this.error = error;
        }
    }

    private static class RegistrationForm {
        private final String firstName;
        private final String lastName;
        private final String age;

        private RegistrationForm(String firstName, String lastName, String age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public static <TypeToValidate> Validator<RegistrationForm> validator(Function<RegistrationForm, TypeToValidate> field, Validator<TypeToValidate> validator) {
            return registrationForm -> validator.attempt(field.apply(registrationForm));
        }
    }

    private interface Validator<TypeToValidate> extends ActionThatMightFail<ValidationError, TypeToValidate> {
    }

    private class NotBlankValidator implements Validator<String> {

        @Override
        public Optional<ValidationError> attempt(String string) {
            if (string == null || string.trim().isEmpty()) {
                return Optional.of(new ValidationError("Field was empty"));
            }
            return Optional.empty();
        }
    }

    @Test
    public void validateRegistrationForm() {
        String result = validate(registrationForm())
                .peek(this::registerUser)
                .ifTechnicalFailure().peek(this::logFailure)
                .join(this::renderValidationErrors, this::renderJoinedPage, this::renderFailure);
        System.out.println("result = " + result);
    }

    private ValidationPath<ValidationError, RegistrationForm> validate(RegistrationForm registrationForm) {
        return ValidationPath.validate(registrationForm, cheapValidators()).validate(expensiveValidators());
    }

    private ActionThatMightFail<ValidationError, RegistrationForm> cheapValidators() {
        return ageValidator();
    }

    private List<ActionThatMightFail<ValidationError, RegistrationForm>> expensiveValidators() {
        return Arrays.asList(lastNameValidator(), firstNameValidator());
    }

    private void logFailure(Exception exception) {
        System.out.println("e = " + exception);
    }

    private String renderJoinedPage(RegistrationForm registrationForm) {
        return "You joined!";
    }

    private String renderValidationErrors(List<ValidationError> validationErrors) {
        return "Please fix the errors: " + validationErrors;
    }

    private String renderFailure(Exception e) {
        return "There was a technical technicalFailure. Please try again.";
    }

    private void registerUser(RegistrationForm registrationForm) {
        System.out.println("Register in database");
    }

    private RegistrationForm registrationForm() {
        return new RegistrationForm("first", "last", "25");
    }

    private Validator<RegistrationForm> ageValidator() {
        return RegistrationForm.validator(form -> form.age, new NotBlankValidator());
    }

    private Validator<RegistrationForm> lastNameValidator() {
        return RegistrationForm.validator(form -> form.lastName, new NotBlankValidator());
    }

    private Validator<RegistrationForm> firstNameValidator() {
        return RegistrationForm.validator(form -> form.firstName, new NotBlankValidator());
    }
}
