package com.sun.corba.se.impl.logging;

import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.INTERNAL;
import java.util.logging.Level;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class ActivationSystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int CANNOT_READ_REPOSITORY_DB = 1398079889;
    public static final int CANNOT_ADD_INITIAL_NAMING = 1398079890;
    public static final int CANNOT_WRITE_REPOSITORY_DB = 1398079889;
    public static final int SERVER_NOT_EXPECTED_TO_REGISTER = 1398079891;
    public static final int UNABLE_TO_START_PROCESS = 1398079892;
    public static final int SERVER_NOT_RUNNING = 1398079894;
    public static final int ERROR_IN_BAD_SERVER_ID_HANDLER = 1398079889;
    
    public ActivationSystemException(final Logger logger) {
        super(logger);
    }
    
    public static ActivationSystemException get(final ORB orb, final String s) {
        return (ActivationSystemException)orb.getLogWrapper(s, "ACTIVATION", ActivationSystemException.factory);
    }
    
    public static ActivationSystemException get(final String s) {
        return (ActivationSystemException)ORB.staticGetLogWrapper(s, "ACTIVATION", ActivationSystemException.factory);
    }
    
    public INITIALIZE cannotReadRepositoryDb(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079889, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ACTIVATION.cannotReadRepositoryDb", null, ActivationSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE cannotReadRepositoryDb(final CompletionStatus completionStatus) {
        return this.cannotReadRepositoryDb(completionStatus, null);
    }
    
    public INITIALIZE cannotReadRepositoryDb(final Throwable t) {
        return this.cannotReadRepositoryDb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE cannotReadRepositoryDb() {
        return this.cannotReadRepositoryDb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE cannotAddInitialNaming(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079890, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ACTIVATION.cannotAddInitialNaming", null, ActivationSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE cannotAddInitialNaming(final CompletionStatus completionStatus) {
        return this.cannotAddInitialNaming(completionStatus, null);
    }
    
    public INITIALIZE cannotAddInitialNaming(final Throwable t) {
        return this.cannotAddInitialNaming(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE cannotAddInitialNaming() {
        return this.cannotAddInitialNaming(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL cannotWriteRepositoryDb(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079889, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ACTIVATION.cannotWriteRepositoryDb", null, ActivationSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL cannotWriteRepositoryDb(final CompletionStatus completionStatus) {
        return this.cannotWriteRepositoryDb(completionStatus, null);
    }
    
    public INTERNAL cannotWriteRepositoryDb(final Throwable t) {
        return this.cannotWriteRepositoryDb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL cannotWriteRepositoryDb() {
        return this.cannotWriteRepositoryDb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL serverNotExpectedToRegister(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079891, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ACTIVATION.serverNotExpectedToRegister", null, ActivationSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL serverNotExpectedToRegister(final CompletionStatus completionStatus) {
        return this.serverNotExpectedToRegister(completionStatus, null);
    }
    
    public INTERNAL serverNotExpectedToRegister(final Throwable t) {
        return this.serverNotExpectedToRegister(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL serverNotExpectedToRegister() {
        return this.serverNotExpectedToRegister(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unableToStartProcess(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079892, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ACTIVATION.unableToStartProcess", null, ActivationSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unableToStartProcess(final CompletionStatus completionStatus) {
        return this.unableToStartProcess(completionStatus, null);
    }
    
    public INTERNAL unableToStartProcess(final Throwable t) {
        return this.unableToStartProcess(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL unableToStartProcess() {
        return this.unableToStartProcess(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL serverNotRunning(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079894, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ACTIVATION.serverNotRunning", null, ActivationSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL serverNotRunning(final CompletionStatus completionStatus) {
        return this.serverNotRunning(completionStatus, null);
    }
    
    public INTERNAL serverNotRunning(final Throwable t) {
        return this.serverNotRunning(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL serverNotRunning() {
        return this.serverNotRunning(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079889, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ACTIVATION.errorInBadServerIdHandler", null, ActivationSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler(final CompletionStatus completionStatus) {
        return this.errorInBadServerIdHandler(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler(final Throwable t) {
        return this.errorInBadServerIdHandler(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler() {
        return this.errorInBadServerIdHandler(CompletionStatus.COMPLETED_NO, null);
    }
    
    static {
        ActivationSystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new ActivationSystemException(logger);
            }
        };
    }
}
