package io.github.theangrydev.businessflows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class HappyFlow<Happy> {

    final Happy happyPath;
    final Exception exceptionPath;

    private HappyFlow(Happy happyPath, Exception exceptionPath) {
        this.happyPath = happyPath;
        this.exceptionPath = exceptionPath;
    }

    public static <Happy> HappyFlow<Happy> happyPath(Happy happyPath) {
        return new HappyFlow<>(happyPath, null);
    }

    public static <Happy> HappyFlow<Happy> technicalFailure(Exception exception) {
        return new HappyFlow<>(null, exception);
    }

    @SafeVarargs
    public final <Sad> ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return join(happy -> validate(happy, validators), ValidationFlow::technicalFailure);
    }

    public <Result> Result join(Function<Happy, Result> happyJoiner, Function<Exception, Result> exceptionJoiner) {
        return Optional.ofNullable(happyPath).map(happyJoiner)
                    .orElseGet(() -> Optional.ofNullable(exceptionPath).map(exceptionJoiner)
                            .orElseThrow(() -> new RuntimeException("Impossible scenario. There must always be a happy or exception.")));
    }

    private <Sad> ValidationFlow<Sad, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>[] validators) {
        List<Sad> failures = new ArrayList<>();
        for (ActionThatMightFail<Sad, Happy> validator : validators) {
            BusinessFlow<Sad, Happy> attempt =  attempt(validator);
            if (attempt.exceptionPath != null) {
                return ValidationFlow.technicalFailure(attempt.exceptionPath);
            }
            attempt.sadPath().peek(failures::add);
        }
        if (failures.isEmpty()) {
            return ValidationFlow.happyPath(happy);
        } else {
            return ValidationFlow.sadPath(failures);
        }
    }

    public <NewHappy> HappyFlow<NewHappy> then(HappyMapping<Happy, HappyFlow<NewHappy>> action) {
        return happyPath().map(happy -> tryHappyAction(happy, action)).orElse(HappyFlow.technicalFailure(exceptionPath));
    }

    public <NewHappy> HappyFlow<NewHappy> map(HappyMapping<Happy, NewHappy> mapping) {
        return then(mapping.andThen(happy -> new HappyFlow<>(happy, exceptionPath)));
    }

    public <Sad> BusinessFlow<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail) {
        return new BusinessFlow<Sad, Happy>(null, happyPath, exceptionPath).attempt(actionThatMightFail);
    }

    public HappyFlow<Happy> peek(Peek<Happy> peek) {
        return then(happy -> {
            peek.peek(happy);
            return this;
        });
    }

//    public BusinessFlowTechnicalFailure<Sad, Happy> technicalFailure() {
//        return new BusinessFlowTechnicalFailure<>(sadPath, happyPath, exceptionPath);
//    }

//    public Happy get() {
//        return happyPath().orElseThrow(() -> new RuntimeException(format("Happy path not present. Sad path was '%s'. Exception was '%s'", sadPath, exceptionPath)));
//    }

    private <NewHappy> HappyFlow<NewHappy> tryHappyAction(Happy happy, HappyMapping<Happy, HappyFlow<NewHappy>> action) {
        return tryCatch(HappyFlow::technicalFailure, () -> action.map(happy));
    }

    private Optional<Happy> happyPath() {
        return Optional.ofNullable(happyPath);
    }

    private <Result> Result tryCatch(Function<Exception, Result> onException, Projection.SupplierThatMightThrowException<Result> something) {
        try {
            return something.supply();
        } catch (Exception exception) {
            return onException.apply(exception);
        }
    }
}
