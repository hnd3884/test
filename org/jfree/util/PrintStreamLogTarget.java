package org.jfree.util;

import java.io.PrintStream;
import java.io.Serializable;

public class PrintStreamLogTarget implements LogTarget, Serializable
{
    private static final long serialVersionUID = 6510564403264504688L;
    private PrintStream printStream;
    
    public PrintStreamLogTarget() {
        this(System.out);
    }
    
    public PrintStreamLogTarget(final PrintStream printStream) {
        if (printStream == null) {
            throw new NullPointerException();
        }
        this.printStream = printStream;
    }
    
    public void log(int level, final Object message) {
        if (level > 3) {
            level = 3;
        }
        this.printStream.print(LogTarget.LEVELS[level]);
        this.printStream.println(message);
        if (level < 3) {
            System.out.flush();
        }
    }
    
    public void log(int level, final Object message, final Exception e) {
        if (level > 3) {
            level = 3;
        }
        this.printStream.print(LogTarget.LEVELS[level]);
        this.printStream.println(message);
        e.printStackTrace(this.printStream);
        if (level < 3) {
            System.out.flush();
        }
    }
}
