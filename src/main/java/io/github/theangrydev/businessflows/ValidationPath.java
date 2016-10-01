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
import java.util.List;

/**
 * A {@link ValidationPath} is a special kind of {@link HappyPath} that can accumulate several validation failures into
 * a list of {@link Sad}, if there are any failures.
 *
 * {@inheritDoc}
 */
public interface ValidationPath<Happy, Sad> extends HappyPath<Happy, List<Sad>> {

    /**
     * Validate the given {@link Happy} object by running the given list of validators over it.
     * All validators that fail will be accumulated into the list of {@link Sad} results.
     * The first technical failure encountered will result in a technical failure overall.
     *
     * @param happy The {@link Happy} object to validate
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @param <Happy> The type of happy  object the resulting {@link ValidationPath} may represent
     * @param <Sad> The type of sad object the resulting {@link ValidationPath} may represent
     * @return The result of applying all the validators
     */
    static <Happy, Sad> ValidationPath<Happy, Sad> validate(Happy happy, List<? extends ActionThatMightFail<Happy, Sad>> validators) {
        List<Sad> validationFailures = new ArrayList<>(validators.size());
        for (ActionThatMightFail<Happy, Sad> validator : validators) {
            try {
                validator.attemptHappyPath(happy).ifSad().peek(validationFailures::add);
            } catch (Exception technicalFailure) {
                return new TechnicalFailureCaseValidationPath<>(technicalFailure);
            }
        }
        if (validationFailures.isEmpty()) {
            return new HappyCaseValidationPath<>(happy);
        } else {
            return new SadCaseValidationPath<>(validationFailures);
        }
    }

    /**
     * Perform a subsequent round of validation, which will take place if the previous one succeeded.
     * This can be useful when you want e.g. expensive validators to run after cheap ones, or if you want to group validators together.
     *
     * @param validators Actions that act on the happy object and may indicate a validation failure by returning {@link Sad}
     * @return The result of applying all the validators
     */
    ValidationPath<Happy, Sad> validate(List<? extends ActionThatMightFail<Happy, Sad>> validators);

}
