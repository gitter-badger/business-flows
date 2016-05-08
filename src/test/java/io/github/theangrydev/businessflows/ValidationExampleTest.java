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
            return registrationForm -> validator.validate(field.apply(registrationForm));
        }
    }

    @FunctionalInterface
    private interface Validator<TypeToValidate> {
        Optional<ValidationError> validate(TypeToValidate typeToValidate);
    }

    private class NotBlankValidator implements Validator<String> {

        @Override
        public Optional<ValidationError> validate(String string) {
            if (string == null || string.trim().isEmpty()) {
                return Optional.of(new ValidationError("Field was empty"));
            }
            return Optional.empty();
        }
    }

    @Test
    public void validateRegistrationForm() {
        registrationForm()
                .validate(cheapValidators())
                .validate(expensiveValidators())
                .ifHappy(this::registerUser)
                .ifFailure().peek(this::logFailure)
                .join(this::renderValidationErrors, this::renderJoinedPage, this::renderFailure);
    }

    private ActionThatMightFail<ValidationError, RegistrationForm> cheapValidators() {
        return ageValidator()::validate;
    }

    private List<ActionThatMightFail<ValidationError, RegistrationForm>> expensiveValidators() {
        return Arrays.asList(lastNameValidator()::validate, firstNameValidator()::validate);
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
        return "There was a technical failure. Please try again.";
    }

    private void registerUser(RegistrationForm registrationForm) {
        System.out.println("Register in database");
    }

    private HappyFlow<RegistrationForm> registrationForm() {
        return HappyFlow.happyPath(new RegistrationForm("first", "last", "25"));
    }

    private Validator<RegistrationForm> ageValidator() {
        return RegistrationForm.validator(x -> x.age, new NotBlankValidator());
    }

    private Validator<RegistrationForm> lastNameValidator() {
        return RegistrationForm.validator(x -> x.lastName, new NotBlankValidator());
    }

    private Validator<RegistrationForm> firstNameValidator() {
        return RegistrationForm.validator(x -> x.firstName, new NotBlankValidator());
    }
}
