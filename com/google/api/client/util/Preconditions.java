package com.google.api.client.util;

public final class Preconditions
{
    public static void checkArgument(final boolean expression) {
        com.google.common.base.Preconditions.checkArgument(expression);
    }
    
    public static void checkArgument(final boolean expression, final Object errorMessage) {
        com.google.common.base.Preconditions.checkArgument(expression, errorMessage);
    }
    
    public static void checkArgument(final boolean expression, final String errorMessageTemplate, final Object... errorMessageArgs) {
        com.google.common.base.Preconditions.checkArgument(expression, errorMessageTemplate, errorMessageArgs);
    }
    
    public static void checkState(final boolean expression) {
        com.google.common.base.Preconditions.checkState(expression);
    }
    
    public static void checkState(final boolean expression, final Object errorMessage) {
        com.google.common.base.Preconditions.checkState(expression, errorMessage);
    }
    
    public static void checkState(final boolean expression, final String errorMessageTemplate, final Object... errorMessageArgs) {
        com.google.common.base.Preconditions.checkState(expression, errorMessageTemplate, errorMessageArgs);
    }
    
    public static <T> T checkNotNull(final T reference) {
        return (T)com.google.common.base.Preconditions.checkNotNull((Object)reference);
    }
    
    public static <T> T checkNotNull(final T reference, final Object errorMessage) {
        return (T)com.google.common.base.Preconditions.checkNotNull((Object)reference, errorMessage);
    }
    
    public static <T> T checkNotNull(final T reference, final String errorMessageTemplate, final Object... errorMessageArgs) {
        return (T)com.google.common.base.Preconditions.checkNotNull((Object)reference, errorMessageTemplate, errorMessageArgs);
    }
    
    private Preconditions() {
    }
}
