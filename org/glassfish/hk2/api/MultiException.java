package org.glassfish.hk2.api;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MultiException extends HK2RuntimeException
{
    private static final long serialVersionUID = 2112432697858621044L;
    private final Object lock;
    private final List<Throwable> throwables;
    private boolean reportToErrorService;
    
    public MultiException() {
        this.lock = new byte[0];
        this.throwables = new LinkedList<Throwable>();
        this.reportToErrorService = true;
    }
    
    public MultiException(final List<Throwable> ths) {
        super(ths.get(0).getMessage(), ths.get(0));
        this.lock = new byte[0];
        this.throwables = new LinkedList<Throwable>();
        this.reportToErrorService = true;
        for (final Throwable th : ths) {
            if (th instanceof MultiException) {
                final MultiException me = (MultiException)th;
                this.throwables.addAll(me.throwables);
            }
            else {
                this.throwables.add(th);
            }
        }
    }
    
    public MultiException(final Throwable th, final boolean reportToErrorService) {
        super(th.getMessage(), th);
        this.lock = new byte[0];
        this.throwables = new LinkedList<Throwable>();
        this.reportToErrorService = true;
        if (th instanceof MultiException) {
            final MultiException me = (MultiException)th;
            this.throwables.addAll(me.throwables);
        }
        else {
            this.throwables.add(th);
        }
        this.reportToErrorService = reportToErrorService;
    }
    
    public MultiException(final Throwable th) {
        this(th, true);
    }
    
    public List<Throwable> getErrors() {
        synchronized (this.lock) {
            return new LinkedList<Throwable>(this.throwables);
        }
    }
    
    public void addError(final Throwable error) {
        synchronized (this.lock) {
            this.throwables.add(error);
        }
    }
    
    @Override
    public String getMessage() {
        final List<Throwable> listCopy = this.getErrors();
        final StringBuffer sb = new StringBuffer("A MultiException has " + listCopy.size() + " exceptions.  They are:\n");
        int lcv = 1;
        for (final Throwable th : listCopy) {
            sb.append(lcv++ + ". " + th.getClass().getName() + ((th.getMessage() != null) ? (": " + th.getMessage()) : "") + "\n");
        }
        return sb.toString();
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        final List<Throwable> listCopy = this.getErrors();
        if (listCopy.size() <= 0) {
            super.printStackTrace(s);
            return;
        }
        int lcv = 1;
        for (final Throwable th : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            th.printStackTrace(s);
        }
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        final List<Throwable> listCopy = this.getErrors();
        if (listCopy.size() <= 0) {
            super.printStackTrace(s);
            return;
        }
        int lcv = 1;
        for (final Throwable th : listCopy) {
            s.println("MultiException stack " + lcv++ + " of " + listCopy.size());
            th.printStackTrace(s);
        }
    }
    
    public boolean getReportToErrorService() {
        return this.reportToErrorService;
    }
    
    public void setReportToErrorService(final boolean report) {
        this.reportToErrorService = report;
    }
    
    @Override
    public String toString() {
        return this.getMessage();
    }
}
