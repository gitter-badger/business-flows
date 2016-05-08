package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;

public class BusinessFlow<Sad, Happy> extends BusinessFlowProjection<Sad, Happy> {

    BusinessFlow(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> happyPath(Happy happy) {
        return new BusinessFlow<>(null, happy, null);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> sadPath(Sad sad) {
        return new BusinessFlow<>(sad, null, null);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> technicalFailure(Exception exception) {
        return new BusinessFlow<>(null, null, exception);
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> then(HappyMapping<Happy, BusinessFlow<Sad, NewHappy>> action) {
        return join(BusinessFlow::sadPath, happy -> {
            try {
                return action.map(happy);
            } catch (Exception exception) {
                return BusinessFlow.technicalFailure(exception);
            }
        }, BusinessFlow::technicalFailure);
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> map(HappyMapping<Happy, NewHappy> mapping) {
        return then(mapping.andThen(BusinessFlow::happyPath));
    }

    public BusinessFlow<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail) {
        return then(happy -> actionThatMightFail.attempt(happy).map(BusinessFlow::<Sad, Happy>sadPath).orElse(BusinessFlow.happyPath(happy)));
    }

    public BusinessFlow<Sad, Happy> peek(Peek<Happy> peek) {
        return then(happy -> {
            peek.peek(happy);
            return this;
        });
    }

    public SadPath<Sad, Happy> sadPath() {
        return new SadPath<>(sadPath, happyPath, exceptionPath);
    }

    public Happy get() {
        return happyPath().orElseThrow(() -> new RuntimeException(format("Happy path not present. Sad path was '%s'. Exception was '%s'", sadPath, exceptionPath)));
    }

    private Optional<Happy> happyPath() {
        return Optional.ofNullable(happyPath);
    }
}
