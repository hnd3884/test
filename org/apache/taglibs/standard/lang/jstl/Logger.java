package org.apache.taglibs.standard.lang.jstl;

import java.text.MessageFormat;
import java.io.PrintStream;

public class Logger
{
    PrintStream mOut;
    
    public Logger(final PrintStream pOut) {
        this.mOut = pOut;
    }
    
    public boolean isLoggingWarning() {
        return false;
    }
    
    public void logWarning(final String pMessage, final Throwable pRootCause) throws ELException {
        if (this.isLoggingWarning() && this.mOut != null) {
            if (pMessage == null) {
                this.mOut.println(pRootCause);
            }
            else if (pRootCause == null) {
                this.mOut.println(pMessage);
            }
            else {
                this.mOut.println(pMessage + ": " + pRootCause);
            }
        }
    }
    
    public void logWarning(final String pTemplate) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(pTemplate, null);
        }
    }
    
    public void logWarning(final Throwable pRootCause) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(null, pRootCause);
        }
    }
    
    public void logWarning(final String pTemplate, final Object pArg0) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0));
        }
    }
    
    public void logWarning(final String pTemplate, final Throwable pRootCause, final Object pArg0) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0), pRootCause);
        }
    }
    
    public void logWarning(final String pTemplate, final Object pArg0, final Object pArg1) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1));
        }
    }
    
    public void logWarning(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1), pRootCause);
        }
    }
    
    public void logWarning(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2));
        }
    }
    
    public void logWarning(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2), pRootCause);
        }
    }
    
    public void logWarning(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3));
        }
    }
    
    public void logWarning(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3), pRootCause);
        }
    }
    
    public void logWarning(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4));
        }
    }
    
    public void logWarning(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4), pRootCause);
        }
    }
    
    public void logWarning(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4, final Object pArg5) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4, "" + pArg5));
        }
    }
    
    public void logWarning(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4, final Object pArg5) throws ELException {
        if (this.isLoggingWarning()) {
            this.logWarning(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4, "" + pArg5), pRootCause);
        }
    }
    
    public boolean isLoggingError() {
        return true;
    }
    
    public void logError(final String pMessage, final Throwable pRootCause) throws ELException {
        if (!this.isLoggingError()) {
            return;
        }
        if (pMessage == null) {
            throw new ELException(pRootCause);
        }
        if (pRootCause == null) {
            throw new ELException(pMessage);
        }
        throw new ELException(pMessage, pRootCause);
    }
    
    public void logError(final String pTemplate) throws ELException {
        if (this.isLoggingError()) {
            this.logError(pTemplate, null);
        }
    }
    
    public void logError(final Throwable pRootCause) throws ELException {
        if (this.isLoggingError()) {
            this.logError(null, pRootCause);
        }
    }
    
    public void logError(final String pTemplate, final Object pArg0) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0));
        }
    }
    
    public void logError(final String pTemplate, final Throwable pRootCause, final Object pArg0) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0), pRootCause);
        }
    }
    
    public void logError(final String pTemplate, final Object pArg0, final Object pArg1) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1));
        }
    }
    
    public void logError(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1), pRootCause);
        }
    }
    
    public void logError(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2));
        }
    }
    
    public void logError(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2), pRootCause);
        }
    }
    
    public void logError(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3));
        }
    }
    
    public void logError(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3), pRootCause);
        }
    }
    
    public void logError(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4));
        }
    }
    
    public void logError(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4), pRootCause);
        }
    }
    
    public void logError(final String pTemplate, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4, final Object pArg5) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4, "" + pArg5));
        }
    }
    
    public void logError(final String pTemplate, final Throwable pRootCause, final Object pArg0, final Object pArg1, final Object pArg2, final Object pArg3, final Object pArg4, final Object pArg5) throws ELException {
        if (this.isLoggingError()) {
            this.logError(MessageFormat.format(pTemplate, "" + pArg0, "" + pArg1, "" + pArg2, "" + pArg3, "" + pArg4, "" + pArg5), pRootCause);
        }
    }
}
