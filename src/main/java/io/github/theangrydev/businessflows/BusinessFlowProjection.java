package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

abstract class BusinessFlowProjection<Sad, Happy> {

    final Sad sadPath;
    final Happy happyPath;
    final Exception exceptionPath;

    BusinessFlowProjection(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        this.sadPath = sadPath;
        this.happyPath = happyPath;
        this.exceptionPath = exceptionPath;
    }

    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> exceptionJoiner) {
        return Optional.ofNullable(happyPath).map(happyJoiner)
                .orElseGet(() -> Optional.ofNullable(sadPath).map(sadJoiner)
                        .orElseGet(() -> Optional.ofNullable(exceptionPath).map(exceptionJoiner)
                                .orElseThrow(() -> new RuntimeException("Impossible scenario. There must always be a happy or sad or exception."))));
    }

    public HappyFlow<Happy> failIfSad(Function<Sad, Exception> failure) {
        return join(failure.andThen(HappyFlow::failure), HappyFlow::happyPath, HappyFlow::failure);
    }

    public Failure<Sad, Happy> ifFailure() {
        return new Failure<>(sadPath, happyPath, exceptionPath);
    }

    @FunctionalInterface
    interface SupplierThatMightThrowException<Result> {
        Result supply() throws Exception;
    }

}
