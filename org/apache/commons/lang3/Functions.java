package org.apache.commons.lang3;

import java.lang.reflect.UndeclaredThrowableException;
import java.io.UncheckedIOException;
import java.io.IOException;

public class Functions
{
    public static <T extends Throwable> void run(final FailableRunnable<T> pRunnable) {
        try {
            pRunnable.run();
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <O, T extends Throwable> O call(final FailableCallable<O, T> pCallable) {
        try {
            return pCallable.call();
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <O, T extends Throwable> void accept(final FailableConsumer<O, T> pConsumer, final O pObject) {
        try {
            pConsumer.accept(pObject);
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <O1, O2, T extends Throwable> void accept(final FailableBiConsumer<O1, O2, T> pConsumer, final O1 pObject1, final O2 pObject2) {
        try {
            pConsumer.accept(pObject1, pObject2);
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <I, O, T extends Throwable> O apply(final FailableFunction<I, O, T> pFunction, final I pInput) {
        try {
            return pFunction.apply(pInput);
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <I1, I2, O, T extends Throwable> O apply(final FailableBiFunction<I1, I2, O, T> pFunction, final I1 pInput1, final I2 pInput2) {
        try {
            return pFunction.apply(pInput1, pInput2);
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <O, T extends Throwable> boolean test(final FailablePredicate<O, T> pPredicate, final O pObject) {
        try {
            return pPredicate.test(pObject);
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <O1, O2, T extends Throwable> boolean test(final FailableBiPredicate<O1, O2, T> pPredicate, final O1 pObject1, final O2 pObject2) {
        try {
            return pPredicate.test(pObject1, pObject2);
        }
        catch (final Throwable t) {
            throw rethrow(t);
        }
    }
    
    @SafeVarargs
    public static void tryWithResources(final FailableRunnable<? extends Throwable> pAction, final FailableConsumer<Throwable, ? extends Throwable> pErrorHandler, final FailableRunnable<? extends Throwable>... pResources) {
        FailableConsumer<Throwable, ? extends Throwable> errorHandler;
        final Throwable t;
        if (pErrorHandler == null) {
            errorHandler = (t -> rethrow(t));
        }
        else {
            errorHandler = pErrorHandler;
        }
        if (pResources != null) {
            for (final FailableRunnable<? extends Throwable> runnable : pResources) {
                if (runnable == null) {
                    throw new NullPointerException("A resource action must not be null.");
                }
            }
        }
        Throwable th = null;
        try {
            pAction.run();
        }
        catch (final Throwable t) {
            th = t;
        }
        if (pResources != null) {
            for (final FailableRunnable<?> runnable2 : pResources) {
                try {
                    runnable2.run();
                }
                catch (final Throwable t2) {
                    if (th == null) {
                        th = t2;
                    }
                }
            }
        }
        if (th != null) {
            try {
                errorHandler.accept(th);
            }
            catch (final Throwable t) {
                throw rethrow(t);
            }
        }
    }
    
    @SafeVarargs
    public static void tryWithResources(final FailableRunnable<? extends Throwable> pAction, final FailableRunnable<? extends Throwable>... pResources) {
        tryWithResources(pAction, (FailableConsumer<Throwable, ? extends Throwable>)null, pResources);
    }
    
    public static RuntimeException rethrow(final Throwable pThrowable) {
        if (pThrowable == null) {
            throw new NullPointerException("The Throwable must not be null.");
        }
        if (pThrowable instanceof RuntimeException) {
            throw (RuntimeException)pThrowable;
        }
        if (pThrowable instanceof Error) {
            throw (Error)pThrowable;
        }
        if (pThrowable instanceof IOException) {
            throw new UncheckedIOException((IOException)pThrowable);
        }
        throw new UndeclaredThrowableException(pThrowable);
    }
    
    @FunctionalInterface
    public interface FailableBiPredicate<O1, O2, T extends Throwable>
    {
        boolean test(final O1 p0, final O2 p1) throws T, Throwable;
    }
    
    @FunctionalInterface
    public interface FailablePredicate<O, T extends Throwable>
    {
        boolean test(final O p0) throws T, Throwable;
    }
    
    @FunctionalInterface
    public interface FailableBiFunction<I1, I2, O, T extends Throwable>
    {
        O apply(final I1 p0, final I2 p1) throws T, Throwable;
    }
    
    @FunctionalInterface
    public interface FailableFunction<I, O, T extends Throwable>
    {
        O apply(final I p0) throws T, Throwable;
    }
    
    @FunctionalInterface
    public interface FailableBiConsumer<O1, O2, T extends Throwable>
    {
        void accept(final O1 p0, final O2 p1) throws T, Throwable;
    }
    
    @FunctionalInterface
    public interface FailableConsumer<O, T extends Throwable>
    {
        void accept(final O p0) throws T, Throwable;
    }
    
    @FunctionalInterface
    public interface FailableCallable<O, T extends Throwable>
    {
        O call() throws T, Throwable;
    }
    
    @FunctionalInterface
    public interface FailableRunnable<T extends Throwable>
    {
        void run() throws T, Throwable;
    }
}
