package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

public class SadCase<Sad, Happy> implements BusinessCase<Sad, Happy> {

    private final Sad sad;

    public SadCase(Sad sad) {
        this.sad = sad;
    }

    @Override
    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner) {
        return sadJoiner.apply(sad);
    }

    @Override
    public Optional<Happy> happy() {
        return Optional.empty();
    }

    @Override
    public Optional<Sad> sad() {
        return Optional.of(sad);
    }

    @Override
    public Optional<Exception> technicalFailure() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Sad: " + sad;
    }
}
