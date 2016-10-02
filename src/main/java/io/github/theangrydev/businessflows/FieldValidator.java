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

import java.util.Arrays;
import java.util.List;

import static io.github.theangrydev.businessflows.ValidationPath.validate;

/**
 * A {@link Validator} for a {@link Field} of a {@link Happy} object.
 *
 * @param <Happy> The type that contains the {@link Field}
 * @param <Sad> The type of validation failure
 * @param <Field> The type of the field to validate
 */
public class FieldValidator<Happy, Sad, Field> implements Validator<Happy, Sad> {
    private final Mapping<Happy, Field> fieldExtractor;
    private final List<? extends Validator<Field, Sad>> fieldValidators;

    private FieldValidator(Mapping<Happy, Field> fieldExtractor, List<? extends Validator<Field, Sad>> fieldValidators) {
        this.fieldExtractor = fieldExtractor;
        this.fieldValidators = fieldValidators;
    }

    /**
     * Produces a {@link FieldValidator} that will validate the extracted field using the given validators.
     *
     * @param fieldExtractor Extracts the {@link Field} from the {@link Happy}
     * @param fieldValidators Each {@link Validator} will validate the {@link Field}, possibly producing {@link Sad} failures
     * @param <Happy> The type that contains the {@link Field}
     * @param <Sad> The type of validation failure
     * @param <Field> The type of the field to validate
     * @return The validator
     */
    public static <Happy, Sad, Field> FieldValidator<Happy, Sad, Field> fieldValidator(Mapping<Happy, Field> fieldExtractor, List<? extends Validator<Field, Sad>> fieldValidators) {
        return new FieldValidator<>(fieldExtractor, fieldValidators);
    }

    /**
     * Produces a {@link FieldValidator} that will validate the extracted field using the given validators.
     *
     * @param fieldExtractor Extracts the {@link Field} from the {@link Happy}
     * @param fieldValidators Each {@link Validator} will validate the {@link Field}, possibly producing {@link Sad} failures
     * @param <Happy> The type that contains the {@link Field}
     * @param <Sad> The type of validation failure
     * @param <Field> The type of the field to validate
     * @return The validator
     */
    @SafeVarargs
    public static <Happy, Sad, Field> FieldValidator<Happy, Sad, Field> fieldValidator(Mapping<Happy, Field> fieldExtractor, Validator<Field, Sad>... fieldValidators) {
        return fieldValidator(fieldExtractor, Arrays.asList(fieldValidators));
    }

    @Override
    public PotentialFailure<List<Sad>> attempt(Happy happy) throws Exception {
        Field field = fieldExtractor.map(happy);
        return validate(field, fieldValidators)
                .ifSad().map(PotentialFailure::failure)
                .orElse(PotentialFailure.success());
    }
}
