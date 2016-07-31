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

/**
 * Attempt to perform an action that will either:
 * <ul>
 *     <li>Succeed and return a {@link Happy}</li>
 *     <li>Result in a technical failure and throw any kind of {@link Exception}</li>
 * </ul>
 *
 * @param <Happy> The type of happy object that will be produced in the successful case
 */
@FunctionalInterface
public interface HappyAttempt<Happy> {

    /**
     * @return The happy object that the method attempts to produce
     * @throws Exception If there was a technical failure in producing
     */
    Happy happy() throws Exception;

    /**
     * Helper method to extend an existing {@link HappyAttempt} by mapping the result in the successful case to a new type.
     *
     * @param after The {@link Mapping} to apply after {@link #happy()} is called
     * @param <NewHappy> The new type of happy object that will be produced in the successful case
     * @return A {@link HappyAttempt} that will apply this and then the given mapping
     */
    default <NewHappy> HappyAttempt<NewHappy> andThen(Mapping<Happy, NewHappy> after) {
        return () -> after.map(happy());
    }
}
