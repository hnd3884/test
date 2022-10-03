package java.lang;

import sun.misc.Signal;
import sun.misc.SignalHandler;

class Terminator
{
    private static SignalHandler handler;
    
    static void setup() {
        if (Terminator.handler != null) {
            return;
        }
        final SignalHandler signalHandler = Terminator.handler = new SignalHandler() {
            @Override
            public void handle(final Signal signal) {
                Shutdown.exit(signal.getNumber() + 128);
            }
        };
        try {
            Signal.handle(new Signal("INT"), signalHandler);
        }
        catch (final IllegalArgumentException ex) {}
        try {
            Signal.handle(new Signal("TERM"), signalHandler);
        }
        catch (final IllegalArgumentException ex2) {}
    }
    
    static void teardown() {
    }
    
    static {
        Terminator.handler = null;
    }
}
