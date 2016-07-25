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

import java.util.Optional;
import java.util.function.Function;

public class SadPath<Sad, Happy> extends BusinessFlow<Sad, Happy, Sad> {

    protected SadPath(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public static <Sad, Happy> SadPath<Sad, Happy> sadPath(Sad sad) {
        return new SadPath<>(sad, null, null);
    }

    public static <Sad, Happy> SadPath<Sad, Happy> happyPath(Happy happy) {
        return new SadPath<>(null, happy, null);
    }

    public static <Sad, Happy> SadPath<Sad, Happy> technicalFailure(Exception technicalFailure) {
        return new SadPath<>(null, null, technicalFailure);
    }

    @Override
    public Optional<Sad> toOptional() {
        return Optional.ofNullable(sad);
    }

    public SadPath<Sad, Happy> peek(Peek<Sad> peek) {
        return then(sad -> {
            peek.peek(sad);
            return sadPath(sad);
        });
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException") // This is intentional to ensure that all exceptions are converted to technical failures
    public <NewSad> SadPath<NewSad, Happy> then(Mapping<Sad, SadPath<NewSad, Happy>> action) {
        return join(sad -> {
            try {
                return action.map(sad);
            } catch (Exception technicalFailure) {
                return technicalFailure(technicalFailure);
            }
        }, SadPath::happyPath, SadPath::technicalFailure);
    }

    public HappyPath<Sad, Happy> recover(Mapping<Sad, Happy> recovery) {
        return this.<Sad>then(sad -> happyPath(recovery.map(sad))).ifHappy();
    }

    public <NewSad> SadPath<NewSad, Happy> map(Mapping<Sad, NewSad> mapping) {
        return then(mapping.andThen(SadPath::sadPath));
    }

    public TechnicalFailure<Sad, Happy> technicalFailure(Function<Sad, Exception> sadToTechnicalFailure) {
        return join(sadToTechnicalFailure.andThen(TechnicalFailure::technicalFailure), TechnicalFailure::happyPath, TechnicalFailure::technicalFailure);
    }
}
