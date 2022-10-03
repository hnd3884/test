package sun.misc;

import java.util.Hashtable;

public final class Signal
{
    private static Hashtable<Signal, SignalHandler> handlers;
    private static Hashtable<Integer, Signal> signals;
    private int number;
    private String name;
    
    public int getNumber() {
        return this.number;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Signal)) {
            return false;
        }
        final Signal signal = (Signal)o;
        return this.name.equals(signal.name) && this.number == signal.number;
    }
    
    @Override
    public int hashCode() {
        return this.number;
    }
    
    @Override
    public String toString() {
        return "SIG" + this.name;
    }
    
    public Signal(final String name) {
        this.number = findSignal(name);
        this.name = name;
        if (this.number < 0) {
            throw new IllegalArgumentException("Unknown signal: " + name);
        }
    }
    
    public static synchronized SignalHandler handle(final Signal signal, final SignalHandler signalHandler) throws IllegalArgumentException {
        final long n = (signalHandler instanceof NativeSignalHandler) ? ((NativeSignalHandler)signalHandler).getHandler() : 2L;
        final long handle0 = handle0(signal.number, n);
        if (handle0 == -1L) {
            throw new IllegalArgumentException("Signal already used by VM or OS: " + signal);
        }
        Signal.signals.put(signal.number, signal);
        synchronized (Signal.handlers) {
            final SignalHandler signalHandler2 = Signal.handlers.get(signal);
            Signal.handlers.remove(signal);
            if (n == 2L) {
                Signal.handlers.put(signal, signalHandler);
            }
            if (handle0 == 0L) {
                return SignalHandler.SIG_DFL;
            }
            if (handle0 == 1L) {
                return SignalHandler.SIG_IGN;
            }
            if (handle0 == 2L) {
                return signalHandler2;
            }
            return new NativeSignalHandler(handle0);
        }
    }
    
    public static void raise(final Signal signal) throws IllegalArgumentException {
        if (Signal.handlers.get(signal) == null) {
            throw new IllegalArgumentException("Unhandled signal: " + signal);
        }
        raise0(signal.number);
    }
    
    private static void dispatch(final int n) {
        final Signal signal = Signal.signals.get(n);
        final SignalHandler signalHandler = Signal.handlers.get(signal);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                signalHandler.handle(signal);
            }
        };
        if (signalHandler != null) {
            new Thread(runnable, signal + " handler").start();
        }
    }
    
    private static native int findSignal(final String p0);
    
    private static native long handle0(final int p0, final long p1);
    
    private static native void raise0(final int p0);
    
    static {
        Signal.handlers = new Hashtable<Signal, SignalHandler>(4);
        Signal.signals = new Hashtable<Integer, Signal>(4);
    }
}
