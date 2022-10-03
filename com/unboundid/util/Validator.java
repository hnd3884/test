package com.unboundid.util;

import java.util.Map;
import java.util.Collection;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Validator
{
    private Validator() {
    }
    
    public static void ensureNotNull(final Object o) throws LDAPSDKUsageException {
        if (o == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_NULL_CHECK_FAILURE.get(0, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullWithMessage(final Object o, final String message) throws LDAPSDKUsageException {
        if (o == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_FAILURE_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNull(final Object o1, final Object o2) throws LDAPSDKUsageException {
        if (o1 == null || o2 == null) {
            int index;
            if (o1 == null) {
                index = 0;
            }
            else {
                index = 1;
            }
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_NULL_CHECK_FAILURE.get(index, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNull(final Object o1, final Object o2, final Object o3) throws LDAPSDKUsageException {
        if (o1 == null || o2 == null || o3 == null) {
            int index;
            if (o1 == null) {
                index = 0;
            }
            else if (o2 == null) {
                index = 1;
            }
            else {
                index = 2;
            }
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_NULL_CHECK_FAILURE.get(index, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNull(final Object o1, final Object o2, final Object o3, final Object o4) throws LDAPSDKUsageException {
        if (o1 == null || o2 == null || o3 == null || o4 == null) {
            int index;
            if (o1 == null) {
                index = 0;
            }
            else if (o2 == null) {
                index = 1;
            }
            else if (o3 == null) {
                index = 2;
            }
            else {
                index = 3;
            }
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_NULL_CHECK_FAILURE.get(index, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNull(final Object o1, final Object o2, final Object o3, final Object o4, final Object o5) throws LDAPSDKUsageException {
        if (o1 == null || o2 == null || o3 == null || o4 == null || o5 == null) {
            int index;
            if (o1 == null) {
                index = 0;
            }
            else if (o2 == null) {
                index = 1;
            }
            else if (o3 == null) {
                index = 2;
            }
            else if (o4 == null) {
                index = 3;
            }
            else {
                index = 4;
            }
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_NULL_CHECK_FAILURE.get(index, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final Collection<?> collection) {
        if (collection == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_COLLECTION_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (collection.isEmpty()) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_COLLECTION_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final Collection<?> collection, final String message) {
        if (collection == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_COLLECTION_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (collection.isEmpty()) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_COLLECTION_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final Map<?, ?> map) {
        if (map == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_MAP_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (map.isEmpty()) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_MAP_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final Map<?, ?> map, final String message) {
        if (map == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_MAP_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (map.isEmpty()) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_MAP_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final Object[] array) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final Object[] array, final String message) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final byte[] array) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final byte[] array, final String message) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final char[] array) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final char[] array, final String message) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final int[] array) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final int[] array, final String message) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final long[] array) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final long[] array, final String message) {
        if (array == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (array.length == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_ARRAY_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final CharSequence charSequence) {
        if (charSequence == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_CHAR_SEQUENCE_NULL.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (charSequence.length() == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_CHAR_SEQUENCE_EMPTY.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureNotNullOrEmpty(final CharSequence charSequence, final String message) {
        if (charSequence == null) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_CHAR_SEQUENCE_NULL_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
        if (charSequence.length() == 0) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_CHAR_SEQUENCE_EMPTY_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureTrue(final boolean condition) throws LDAPSDKUsageException {
        if (!condition) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_TRUE_CHECK_FAILURE.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureTrue(final boolean condition, final String message) throws LDAPSDKUsageException {
        if (!condition) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_FAILURE_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureFalse(final boolean condition) throws LDAPSDKUsageException {
        if (condition) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_FALSE_CHECK_FAILURE.get(StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void ensureFalse(final boolean condition, final String message) throws LDAPSDKUsageException {
        if (condition) {
            final LDAPSDKUsageException e = new LDAPSDKUsageException(UtilityMessages.ERR_VALIDATOR_FAILURE_CUSTOM_MESSAGE.get(message, StaticUtils.getStackTrace(Thread.currentThread().getStackTrace())));
            Debug.debugCodingError(e);
            throw e;
        }
    }
    
    public static void violation(final String message) throws LDAPSDKUsageException {
        violation(message, null);
    }
    
    public static void violation(final String message, final Throwable cause) throws LDAPSDKUsageException {
        final LDAPSDKUsageException e = new LDAPSDKUsageException(message, cause);
        Debug.debugCodingError(e);
        throw e;
    }
}
