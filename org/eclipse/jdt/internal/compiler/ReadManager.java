package org.eclipse.jdt.internal.compiler;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

public class ReadManager implements Runnable
{
    ICompilationUnit[] units;
    int nextFileToRead;
    ICompilationUnit[] filesRead;
    char[][] contentsRead;
    int readyToReadPosition;
    int nextAvailablePosition;
    Thread[] readingThreads;
    char[] readInProcessMarker;
    int sleepingThreadCount;
    private Throwable caughtException;
    static final int START_CUSHION = 5;
    public static final int THRESHOLD = 10;
    static final int CACHE_SIZE = 15;
    
    public ReadManager(final ICompilationUnit[] files, final int length) {
        this.readInProcessMarker = new char[0];
        int threadCount = 0;
        try {
            final Class runtime = Class.forName("java.lang.Runtime");
            final Method m = runtime.getDeclaredMethod("availableProcessors", (Class[])new Class[0]);
            if (m != null) {
                final Integer result = (Integer)m.invoke(Runtime.getRuntime(), (Object[])null);
                threadCount = result + 1;
                if (threadCount < 2) {
                    threadCount = 0;
                }
                else if (threadCount > 15) {
                    threadCount = 15;
                }
            }
        }
        catch (final IllegalAccessException ex) {}
        catch (final ClassNotFoundException ex2) {}
        catch (final SecurityException ex3) {}
        catch (final NoSuchMethodException ex4) {}
        catch (final IllegalArgumentException ex5) {}
        catch (final InvocationTargetException ex6) {}
        if (threadCount > 0) {
            synchronized (this) {
                System.arraycopy(files, 0, this.units = new ICompilationUnit[length], 0, length);
                this.nextFileToRead = 5;
                this.filesRead = new ICompilationUnit[15];
                this.contentsRead = new char[15][];
                this.readyToReadPosition = 0;
                this.nextAvailablePosition = 0;
                this.sleepingThreadCount = 0;
                this.readingThreads = new Thread[threadCount];
                int i = threadCount;
                while (--i >= 0) {
                    (this.readingThreads[i] = new Thread(this, "Compiler Source File Reader")).setDaemon(true);
                    this.readingThreads[i].start();
                }
            }
        }
    }
    
    public char[] getContents(final ICompilationUnit unit) throws Error {
        if (this.readingThreads == null || this.units.length == 0) {
            if (this.caughtException == null) {
                return unit.getContents();
            }
            if (this.caughtException instanceof Error) {
                throw (Error)this.caughtException;
            }
            throw (RuntimeException)this.caughtException;
        }
        else {
            boolean yield = false;
            char[] result = null;
            synchronized (this) {
                if (unit == this.filesRead[this.readyToReadPosition]) {
                    result = this.contentsRead[this.readyToReadPosition];
                    while (result == this.readInProcessMarker || result == null) {
                        this.contentsRead[this.readyToReadPosition] = null;
                        try {
                            this.wait(250L);
                        }
                        catch (final InterruptedException ex) {}
                        if (this.caughtException != null) {
                            if (this.caughtException instanceof Error) {
                                throw (Error)this.caughtException;
                            }
                            throw (RuntimeException)this.caughtException;
                        }
                        else {
                            result = this.contentsRead[this.readyToReadPosition];
                        }
                    }
                    this.filesRead[this.readyToReadPosition] = null;
                    this.contentsRead[this.readyToReadPosition] = null;
                    if (++this.readyToReadPosition >= this.contentsRead.length) {
                        this.readyToReadPosition = 0;
                    }
                    if (this.sleepingThreadCount > 0) {
                        this.notify();
                        yield = (this.sleepingThreadCount == this.readingThreads.length);
                    }
                }
                else {
                    int unitIndex = 0;
                    for (int l = this.units.length; unitIndex < l && this.units[unitIndex] != unit; ++unitIndex) {}
                    if (unitIndex == this.units.length) {
                        this.units = new ICompilationUnit[0];
                    }
                    else if (unitIndex >= this.nextFileToRead) {
                        this.nextFileToRead = unitIndex + 5;
                        this.readyToReadPosition = 0;
                        this.nextAvailablePosition = 0;
                        this.filesRead = new ICompilationUnit[15];
                        this.contentsRead = new char[15][];
                        this.notifyAll();
                    }
                }
            }
            if (yield) {
                Thread.yield();
            }
            if (result != null) {
                return result;
            }
            return unit.getContents();
        }
    }
    
    @Override
    public void run() {
        try {
            while (this.readingThreads != null) {
                if (this.nextFileToRead >= this.units.length) {
                    break;
                }
                ICompilationUnit unit = null;
                int position = -1;
                synchronized (this) {
                    if (this.readingThreads == null) {
                        monitorexit(this);
                        return;
                    }
                    while (this.filesRead[this.nextAvailablePosition] != null) {
                        ++this.sleepingThreadCount;
                        try {
                            this.wait(250L);
                        }
                        catch (final InterruptedException ex) {}
                        --this.sleepingThreadCount;
                        if (this.readingThreads == null) {
                            monitorexit(this);
                            return;
                        }
                    }
                    if (this.nextFileToRead >= this.units.length) {
                        monitorexit(this);
                        return;
                    }
                    unit = this.units[this.nextFileToRead++];
                    position = this.nextAvailablePosition;
                    if (++this.nextAvailablePosition >= this.contentsRead.length) {
                        this.nextAvailablePosition = 0;
                    }
                    this.filesRead[position] = unit;
                    this.contentsRead[position] = this.readInProcessMarker;
                }
                final char[] result = unit.getContents();
                synchronized (this) {
                    if (this.filesRead[position] != unit) {
                        continue;
                    }
                    if (this.contentsRead[position] == null) {
                        this.notifyAll();
                    }
                    this.contentsRead[position] = result;
                }
            }
        }
        catch (final Error e) {
            synchronized (this) {
                this.caughtException = e;
                this.shutdown();
            }
        }
        catch (final RuntimeException e2) {
            synchronized (this) {
                this.caughtException = e2;
                this.shutdown();
            }
        }
    }
    
    public synchronized void shutdown() {
        this.readingThreads = null;
        this.notifyAll();
    }
}
