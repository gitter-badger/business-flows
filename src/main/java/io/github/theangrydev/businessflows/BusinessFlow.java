package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

public class BusinessFlow<Sad, Happy> extends Projection<Sad, Happy, Happy> {

    BusinessFlow(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    @Override
    public Optional<Happy> toOptional() {
        return Optional.ofNullable(happy);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        return happyAttempt(happyAttempt.andThen(BusinessFlow::happyPath), BusinessFlow::technicalFailure);
    }

    public static <Result> Result happyAttempt(HappyAttempt<Result> happyAttempt, Function<Exception, Result> failureHandler) {
        try {
            return happyAttempt.happy();
        } catch (Exception technicalFailure) {
            return failureHandler.apply(technicalFailure);
        }
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> happyPath(Happy happy) {
        return new BusinessFlow<>(null, happy, null);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> sadPath(Sad sad) {
        return new BusinessFlow<>(sad, null, null);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> technicalFailure(Exception technicalFailure) {
        return new BusinessFlow<>(null, null, technicalFailure);
    }

    public <NewHappy, Result extends BusinessFlow<Sad, NewHappy>> BusinessFlow<Sad, NewHappy> then(Mapping<Happy, Result> action) {
        return join(BusinessFlow::sadPath, happy -> {
            try {
                return action.map(happy);
            } catch (Exception technicalFailure) {
                return BusinessFlow.technicalFailure(technicalFailure);
            }
        }, BusinessFlow::technicalFailure);
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> map(Mapping<Happy, NewHappy> mapping) {
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
}
