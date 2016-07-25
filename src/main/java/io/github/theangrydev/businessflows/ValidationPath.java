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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationPath<Sad, Happy> extends HappyPath<List<Sad>, Happy> {

    protected ValidationPath(List<Sad> sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public static <Sad, Happy> ValidationPath<Sad, Happy> validationSuccess(Happy happy) {
        return new ValidationPath<>(null, happy, null);
    }

    public static <Sad, Happy> ValidationPath<Sad, Happy> validationFailed(List<Sad> sad) {
        return new ValidationPath<>(sad, null, null);
    }

    public static <Sad, Happy> ValidationPath<Sad, Happy> technicalFailureDuringValidation(Exception technicalFailure) {
        return new ValidationPath<>(null, null, technicalFailure);
    }

    @SafeVarargs
    public static <Sad, Happy> ValidationPath<Sad, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>... validators) {
        return validate(happy, Arrays.asList(validators));
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException") // This is intentional to ensure that all exceptions are converted to technical failures
    public static <Sad, Happy> ValidationPath<Sad, Happy> validate(Happy happy, List<ActionThatMightFail<Sad, Happy>> validators) {
        List<Sad> validationFailures = new ArrayList<>(validators.size());
        for (ActionThatMightFail<Sad, Happy> validator : validators) {
            try {
                validator.attempt(happy).ifPresent(validationFailures::add);
            } catch (Exception technicalFailure) {
                return technicalFailureDuringValidation(technicalFailure);
            }
        }
        if (validationFailures.isEmpty()) {
            return validationSuccess(happy);
        } else {
            return validationFailed(validationFailures);
        }
    }

    @SafeVarargs
    public final ValidationPath<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return validate(Arrays.asList(validators));
    }

    public ValidationPath<Sad, Happy> validate(List<ActionThatMightFail<Sad, Happy>> validators) {
        return join(ValidationPath::validationFailed, happy -> validate(happy, validators), ValidationPath::technicalFailureDuringValidation);
    }
}
