[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.theangrydev/business-flows/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/io.github.theangrydev/business-flows)
[![Build Status](https://travis-ci.org/theangrydev/business-flows.svg?branch=master)](https://travis-ci.org/theangrydev/business-flows)
[![codecov](https://codecov.io/gh/theangrydev/business-flows/branch/master/graph/badge.svg)](https://codecov.io/gh/theangrydev/business-flows)
[![Javadoc](http://javadoc-badge.appspot.com/io.github.theangrydev/business-flows.svg?label=javadoc)](http://javadoc-badge.appspot.com/io.github.theangrydev/business-flows)

# business-flows
A combination of the Try monad and the Either monad, to help tame complex business flows

## Frequently asked questions
### What is it?
A small Java 8 library that can help you to model a 3-part (happy/sad/exception) business flow. No more try-catch blocks if you don't want to use them!

### When should I use it?
If you have a flow that fits into the "two track" model, as spoken about by Scott W in his [Railyway Oriented Programming](https://fsharpforfunandprofit.com/rop) model, which this library owes a lot to. This library has 3 tracks instead of 2; the third is supposed to be reserved for "stuff that is never supposed to happen" in the form of uncaught exceptions.

### When should I not use it?
There is no point in using this if your flows are "all or nothing". If there are no expected sad paths and all failures are exceptional circumstances, then this is not a good fit. The Try monad is a better fit. You could use e.g. [better-java-monads](https://github.com/jasongoodwin/better-java-monads/blob/master/src/main/java/com/jasongoodwin/monads/Try.java) instead.

### How to get it?
```xml
<dependency>
    <groupId>io.github.theangrydev</groupId>
    <artifactId>business-flows</artifactId>
    <version>6.0.0</version>
</dependency>
```

## Releases
### 6.0.0
* `ValidationPath` now has a `SadAggregate` type parameter that defaults to `List<Sad>` and can be used to map validation errors into an aggregate. ValidationPath now has a SadAggregate type parameter that defaults to List<Sad> and can be used to map validation errors into an aggregate. There are corresponding `validateInto` methods in `ValidationPath` that allow specifying a `Mapping` to a `SadAggregate` and `validate` methods that default to `List<Sad>`. This change is not backwards compatible

### 5.1.2
* Helper methods for `FieldValidator` to cope with the case that a common field name should be passed to validators so they can use the name in the validation message

### 5.0.0
* Validation revamp. Helper `FieldValidator` for field validation, a new `Validator` type, `PotentialFailure.failures` helper method and `ValidationPath` helper methods. This change is not backwards compatible

### 4.0.3
* Updated javadoc and license headers

### 4.0.1
* Inline methods to make debugging easier (closes [#1](https://github.com/theangrydev/business-flows/issues/2))
* Removed varargs methods from `ValidationPath`. This change is not backwards compatible
* Removed `andThen` methods from `Attempt` and `Mapping`. This change is not backwards compatible

### 3.1.1
* Optimized overrides for ifHappy etc (closes [#2](https://github.com/theangrydev/business-flows/issues/2))

### 3.1.0
* Allow `? extends ActionThatMightFail<Happy, Sad>` in the `ValidationPath`

### 3.0.2
* Made `PotentialFailure.toHappyPath` package private since it is only supposed to be used internally

### 3.0.1
* License headers updated

### 3.0.0
* Using a new class `PotentialFailure` to represent the result of an `ActionThatMightFail` instead of an `Optional`. This change is not backwards compatible 

### 2.7.1
* Updated javadoc

### 2.7.0
* If a technical failure occurs while joining to happy or sad, it is joined to a technical failure instead

### 2.6.0
* Unchecked version of `join` with only happy and sad joiners has taken the `join` name. The checked version is called `joinOrThrow` now

### 2.5.0
* Recovery methods no longer need to be e.g. `exception -> sad`, they can choose to ignore the parameter and just supply a value, e.g. `() -> sad`

### 2.4.0
* Added `HappyPath.happyPathAttempt` to cover the case where the entry point to a flow is a method that could e.g. return either `HappyPath.happyPath` or `SadPath.sadPath` and may also throw an uncaught exception

### 2.3.0
* Made all the static factory methods for `HappyPath`, `SadPath` and `TechnicalFailure` public so that it is possible to do e.g. `HappyPath.sadPath(sad)` to get a happy biased view of a sad path without having to do `SadPath.sadPath(sad).ifHappy()`

### 2.2.0
* Updated javadoc

### 2.1.0
* `HappyAttempt` now has an exception to technical failure factory method and an exception to sad path factory method

### 2.0.0
* Switched order from `<Sad, Happy>` to `<Happy, Sad>` because it is more intuitive to users. This change is not backwards compatible

### 1.1.0
* Added another `join` method in which you can omit the technical failure mapping and allow it to be thrown as an `Exception`
* Reduced visibility of methods that should have been `private`

### 1.0.1
* Changed `orElseThrow` so that it throws `X extends Exception` rather than `X extends Throwable`
* Changed `get` failure message wording

### 1.0.0
* Initial stab at business-flows
