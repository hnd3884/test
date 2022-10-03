package io.opencensus.common;

import javax.annotation.Nullable;

public final class Functions
{
    private static final Function<Object, Void> RETURN_NULL;
    private static final Function<Object, Void> THROW_ILLEGAL_ARGUMENT_EXCEPTION;
    private static final Function<Object, Void> THROW_ASSERTION_ERROR;
    private static final Function<Object, String> RETURN_TO_STRING;
    
    private Functions() {
    }
    
    public static <T> Function<Object, T> returnNull() {
        final Function<Object, T> function = (Function<Object, T>)Functions.RETURN_NULL;
        return function;
    }
    
    public static <T> Function<Object, T> returnConstant(final T constant) {
        return new Function<Object, T>() {
            @Override
            public T apply(final Object ignored) {
                return constant;
            }
        };
    }
    
    public static Function<Object, String> returnToString() {
        return Functions.RETURN_TO_STRING;
    }
    
    public static <T> Function<Object, T> throwIllegalArgumentException() {
        final Function<Object, T> function = (Function<Object, T>)Functions.THROW_ILLEGAL_ARGUMENT_EXCEPTION;
        return function;
    }
    
    public static <T> Function<Object, T> throwAssertionError() {
        final Function<Object, T> function = (Function<Object, T>)Functions.THROW_ASSERTION_ERROR;
        return function;
    }
    
    static {
        RETURN_NULL = new Function<Object, Void>() {
            @Nullable
            @Override
            public Void apply(final Object ignored) {
                return null;
            }
        };
        THROW_ILLEGAL_ARGUMENT_EXCEPTION = new Function<Object, Void>() {
            @Override
            public Void apply(final Object ignored) {
                throw new IllegalArgumentException();
            }
        };
        THROW_ASSERTION_ERROR = new Function<Object, Void>() {
            @Override
            public Void apply(final Object ignored) {
                throw new AssertionError();
            }
        };
        RETURN_TO_STRING = new Function<Object, String>() {
            @Override
            public String apply(final Object input) {
                return (input == null) ? null : input.toString();
            }
        };
    }
}
