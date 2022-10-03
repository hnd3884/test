package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

public class ProcessTaskManager implements Runnable
{
    Compiler compiler;
    private int unitIndex;
    private Thread processingThread;
    CompilationUnitDeclaration unitToProcess;
    private Throwable caughtException;
    volatile int currentIndex;
    volatile int availableIndex;
    volatile int size;
    volatile int sleepCount;
    CompilationUnitDeclaration[] units;
    public static final int PROCESSED_QUEUE_SIZE = 12;
    
    public ProcessTaskManager(final Compiler compiler, final int startingIndex) {
        this.compiler = compiler;
        this.unitIndex = startingIndex;
        this.currentIndex = 0;
        this.availableIndex = 0;
        this.size = 12;
        this.sleepCount = 0;
        this.units = new CompilationUnitDeclaration[this.size];
        synchronized (this) {
            (this.processingThread = new Thread(this, "Compiler Processing Task")).setDaemon(true);
            this.processingThread.start();
        }
    }
    
    private synchronized void addNextUnit(final CompilationUnitDeclaration newElement) {
        while (this.units[this.availableIndex] != null) {
            this.sleepCount = 1;
            try {
                this.wait(250L);
            }
            catch (final InterruptedException ex) {}
            this.sleepCount = 0;
        }
        this.units[this.availableIndex++] = newElement;
        if (this.availableIndex >= this.size) {
            this.availableIndex = 0;
        }
        if (this.sleepCount <= -1) {
            this.notify();
        }
    }
    
    public CompilationUnitDeclaration removeNextUnit() throws Error {
        CompilationUnitDeclaration next = null;
        boolean yield = false;
        synchronized (this) {
            next = this.units[this.currentIndex];
            Label_0108: {
                if (next == null || this.caughtException != null) {
                    while (this.processingThread != null) {
                        this.sleepCount = -1;
                        try {
                            this.wait(100L);
                        }
                        catch (final InterruptedException ex) {}
                        this.sleepCount = 0;
                        next = this.units[this.currentIndex];
                        if (next != null) {
                            break Label_0108;
                        }
                    }
                    if (this.caughtException == null) {
                        monitorexit(this);
                        return null;
                    }
                    if (this.caughtException instanceof Error) {
                        throw (Error)this.caughtException;
                    }
                    throw (RuntimeException)this.caughtException;
                }
            }
            this.units[this.currentIndex++] = null;
            if (this.currentIndex >= this.size) {
                this.currentIndex = 0;
            }
            if (this.sleepCount >= 1 && ++this.sleepCount > 4) {
                this.notify();
                yield = (this.sleepCount > 8);
            }
        }
        if (yield) {
            Thread.yield();
        }
        return next;
    }
    
    @Override
    public void run() {
        final boolean noAnnotations = this.compiler.annotationProcessorManager == null;
        while (this.processingThread != null) {
            this.unitToProcess = null;
            int index = -1;
            final boolean cleanup = noAnnotations || this.compiler.shouldCleanup(this.unitIndex);
            try {
                synchronized (this) {
                    if (this.processingThread == null) {
                        monitorexit(this);
                        return;
                    }
                    this.unitToProcess = this.compiler.getUnitToProcess(this.unitIndex);
                    if (this.unitToProcess == null) {
                        this.processingThread = null;
                        monitorexit(this);
                        return;
                    }
                    index = this.unitIndex++;
                    if (this.unitToProcess.compilationResult.hasBeenAccepted) {
                        monitorexit(this);
                        continue;
                    }
                }
                try {
                    this.compiler.reportProgress(Messages.bind(Messages.compilation_processing, new String(this.unitToProcess.getFileName())));
                    if (this.compiler.options.verbose) {
                        this.compiler.out.println(Messages.bind(Messages.compilation_process, new String[] { String.valueOf(index + 1), String.valueOf(this.compiler.totalUnits), new String(this.unitToProcess.getFileName()) }));
                    }
                    this.compiler.process(this.unitToProcess, index);
                }
                finally {
                    if (this.unitToProcess != null && cleanup) {
                        this.unitToProcess.cleanUp();
                    }
                }
                if (this.unitToProcess != null && cleanup) {
                    this.unitToProcess.cleanUp();
                }
                this.addNextUnit(this.unitToProcess);
            }
            catch (final Error e) {
                synchronized (this) {
                    this.processingThread = null;
                    this.caughtException = e;
                }
            }
            catch (final RuntimeException e2) {
                synchronized (this) {
                    this.processingThread = null;
                    this.caughtException = e2;
                }
            }
        }
    }
    
    public void shutdown() {
        try {
            Thread t = null;
            synchronized (this) {
                if (this.processingThread != null) {
                    t = this.processingThread;
                    this.processingThread = null;
                    this.notifyAll();
                }
            }
            if (t != null) {
                t.join(250L);
            }
        }
        catch (final InterruptedException ex) {}
    }
}
