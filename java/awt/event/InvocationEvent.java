package java.awt.event;

import sun.awt.AWTAccessor;
import java.awt.ActiveEvent;
import java.awt.AWTEvent;

public class InvocationEvent extends AWTEvent implements ActiveEvent
{
    public static final int INVOCATION_FIRST = 1200;
    public static final int INVOCATION_DEFAULT = 1200;
    public static final int INVOCATION_LAST = 1200;
    protected Runnable runnable;
    protected volatile Object notifier;
    private final Runnable listener;
    private volatile boolean dispatched;
    protected boolean catchExceptions;
    private Exception exception;
    private Throwable throwable;
    private long when;
    private static final long serialVersionUID = 436056344909459450L;
    
    public InvocationEvent(final Object o, final Runnable runnable) {
        this(o, 1200, runnable, null, null, false);
    }
    
    public InvocationEvent(final Object o, final Runnable runnable, final Object o2, final boolean b) {
        this(o, 1200, runnable, o2, null, b);
    }
    
    public InvocationEvent(final Object o, final Runnable runnable, final Runnable runnable2, final boolean b) {
        this(o, 1200, runnable, null, runnable2, b);
    }
    
    protected InvocationEvent(final Object o, final int n, final Runnable runnable, final Object o2, final boolean b) {
        this(o, n, runnable, o2, null, b);
    }
    
    private InvocationEvent(final Object o, final int n, final Runnable runnable, final Object notifier, final Runnable listener, final boolean catchExceptions) {
        super(o, n);
        this.dispatched = false;
        this.exception = null;
        this.throwable = null;
        this.runnable = runnable;
        this.notifier = notifier;
        this.listener = listener;
        this.catchExceptions = catchExceptions;
        this.when = System.currentTimeMillis();
    }
    
    @Override
    public void dispatch() {
        try {
            if (this.catchExceptions) {
                try {
                    this.runnable.run();
                }
                catch (final Throwable throwable) {
                    if (throwable instanceof Exception) {
                        this.exception = (Exception)throwable;
                    }
                    this.throwable = throwable;
                }
            }
            else {
                this.runnable.run();
            }
        }
        finally {
            this.finishedDispatching(true);
        }
    }
    
    public Exception getException() {
        return this.catchExceptions ? this.exception : null;
    }
    
    public Throwable getThrowable() {
        return this.catchExceptions ? this.throwable : null;
    }
    
    public long getWhen() {
        return this.when;
    }
    
    public boolean isDispatched() {
        return this.dispatched;
    }
    
    private void finishedDispatching(final boolean dispatched) {
        this.dispatched = dispatched;
        if (this.notifier != null) {
            synchronized (this.notifier) {
                this.notifier.notifyAll();
            }
        }
        if (this.listener != null) {
            this.listener.run();
        }
    }
    
    @Override
    public String paramString() {
        String s = null;
        switch (this.id) {
            case 1200: {
                s = "INVOCATION_DEFAULT";
                break;
            }
            default: {
                s = "unknown type";
                break;
            }
        }
        return s + ",runnable=" + this.runnable + ",notifier=" + this.notifier + ",catchExceptions=" + this.catchExceptions + ",when=" + this.when;
    }
    
    static {
        AWTAccessor.setInvocationEventAccessor(new AWTAccessor.InvocationEventAccessor() {
            @Override
            public void dispose(final InvocationEvent invocationEvent) {
                invocationEvent.finishedDispatching(false);
            }
        });
    }
}
