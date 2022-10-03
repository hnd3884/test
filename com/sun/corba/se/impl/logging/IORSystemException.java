package com.sun.corba.se.impl.logging;

import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_OPERATION;
import java.util.logging.Level;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class IORSystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int ORT_NOT_INITIALIZED = 1398080689;
    public static final int NULL_POA = 1398080690;
    public static final int BAD_MAGIC = 1398080691;
    public static final int STRINGIFY_WRITE_ERROR = 1398080692;
    public static final int TAGGED_PROFILE_TEMPLATE_FACTORY_NOT_FOUND = 1398080693;
    public static final int INVALID_JDK1_3_1_PATCH_LEVEL = 1398080694;
    public static final int GET_LOCAL_SERVANT_FAILURE = 1398080695;
    public static final int ADAPTER_ID_NOT_AVAILABLE = 1398080689;
    public static final int SERVER_ID_NOT_AVAILABLE = 1398080690;
    public static final int ORB_ID_NOT_AVAILABLE = 1398080691;
    public static final int OBJECT_ADAPTER_ID_NOT_AVAILABLE = 1398080692;
    public static final int BAD_OID_IN_IOR_TEMPLATE_LIST = 1398080689;
    public static final int INVALID_TAGGED_PROFILE = 1398080690;
    public static final int BAD_IIOP_ADDRESS_PORT = 1398080691;
    public static final int IOR_MUST_HAVE_IIOP_PROFILE = 1398080689;
    
    public IORSystemException(final Logger logger) {
        super(logger);
    }
    
    public static IORSystemException get(final ORB orb, final String s) {
        return (IORSystemException)orb.getLogWrapper(s, "IOR", IORSystemException.factory);
    }
    
    public static IORSystemException get(final String s) {
        return (IORSystemException)ORB.staticGetLogWrapper(s, "IOR", IORSystemException.factory);
    }
    
    public INTERNAL ortNotInitialized(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080689, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.ortNotInitialized", null, IORSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL ortNotInitialized(final CompletionStatus completionStatus) {
        return this.ortNotInitialized(completionStatus, null);
    }
    
    public INTERNAL ortNotInitialized(final Throwable t) {
        return this.ortNotInitialized(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL ortNotInitialized() {
        return this.ortNotInitialized(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL nullPoa(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080690, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.nullPoa", null, IORSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL nullPoa(final CompletionStatus completionStatus) {
        return this.nullPoa(completionStatus, null);
    }
    
    public INTERNAL nullPoa(final Throwable t) {
        return this.nullPoa(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL nullPoa() {
        return this.nullPoa(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badMagic(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080691, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.badMagic", new Object[] { o }, IORSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badMagic(final CompletionStatus completionStatus, final Object o) {
        return this.badMagic(completionStatus, null, o);
    }
    
    public INTERNAL badMagic(final Throwable t, final Object o) {
        return this.badMagic(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL badMagic(final Object o) {
        return this.badMagic(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL stringifyWriteError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080692, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.stringifyWriteError", null, IORSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL stringifyWriteError(final CompletionStatus completionStatus) {
        return this.stringifyWriteError(completionStatus, null);
    }
    
    public INTERNAL stringifyWriteError(final Throwable t) {
        return this.stringifyWriteError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL stringifyWriteError() {
        return this.stringifyWriteError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL taggedProfileTemplateFactoryNotFound(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080693, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.taggedProfileTemplateFactoryNotFound", new Object[] { o }, IORSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL taggedProfileTemplateFactoryNotFound(final CompletionStatus completionStatus, final Object o) {
        return this.taggedProfileTemplateFactoryNotFound(completionStatus, null, o);
    }
    
    public INTERNAL taggedProfileTemplateFactoryNotFound(final Throwable t, final Object o) {
        return this.taggedProfileTemplateFactoryNotFound(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL taggedProfileTemplateFactoryNotFound(final Object o) {
        return this.taggedProfileTemplateFactoryNotFound(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL invalidJdk131PatchLevel(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080694, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.invalidJdk131PatchLevel", new Object[] { o }, IORSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidJdk131PatchLevel(final CompletionStatus completionStatus, final Object o) {
        return this.invalidJdk131PatchLevel(completionStatus, null, o);
    }
    
    public INTERNAL invalidJdk131PatchLevel(final Throwable t, final Object o) {
        return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL invalidJdk131PatchLevel(final Object o) {
        return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL getLocalServantFailure(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080695, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "IOR.getLocalServantFailure", new Object[] { o }, IORSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL getLocalServantFailure(final CompletionStatus completionStatus, final Object o) {
        return this.getLocalServantFailure(completionStatus, null, o);
    }
    
    public INTERNAL getLocalServantFailure(final Throwable t, final Object o) {
        return this.getLocalServantFailure(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL getLocalServantFailure(final Object o) {
        return this.getLocalServantFailure(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_OPERATION adapterIdNotAvailable(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080689, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.adapterIdNotAvailable", null, IORSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION adapterIdNotAvailable(final CompletionStatus completionStatus) {
        return this.adapterIdNotAvailable(completionStatus, null);
    }
    
    public BAD_OPERATION adapterIdNotAvailable(final Throwable t) {
        return this.adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION adapterIdNotAvailable() {
        return this.adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION serverIdNotAvailable(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080690, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.serverIdNotAvailable", null, IORSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION serverIdNotAvailable(final CompletionStatus completionStatus) {
        return this.serverIdNotAvailable(completionStatus, null);
    }
    
    public BAD_OPERATION serverIdNotAvailable(final Throwable t) {
        return this.serverIdNotAvailable(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION serverIdNotAvailable() {
        return this.serverIdNotAvailable(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION orbIdNotAvailable(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080691, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.orbIdNotAvailable", null, IORSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION orbIdNotAvailable(final CompletionStatus completionStatus) {
        return this.orbIdNotAvailable(completionStatus, null);
    }
    
    public BAD_OPERATION orbIdNotAvailable(final Throwable t) {
        return this.orbIdNotAvailable(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION orbIdNotAvailable() {
        return this.orbIdNotAvailable(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION objectAdapterIdNotAvailable(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080692, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.objectAdapterIdNotAvailable", null, IORSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION objectAdapterIdNotAvailable(final CompletionStatus completionStatus) {
        return this.objectAdapterIdNotAvailable(completionStatus, null);
    }
    
    public BAD_OPERATION objectAdapterIdNotAvailable(final Throwable t) {
        return this.objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION objectAdapterIdNotAvailable() {
        return this.objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM badOidInIorTemplateList(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080689, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.badOidInIorTemplateList", null, IORSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM badOidInIorTemplateList(final CompletionStatus completionStatus) {
        return this.badOidInIorTemplateList(completionStatus, null);
    }
    
    public BAD_PARAM badOidInIorTemplateList(final Throwable t) {
        return this.badOidInIorTemplateList(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM badOidInIorTemplateList() {
        return this.badOidInIorTemplateList(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidTaggedProfile(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080690, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.invalidTaggedProfile", null, IORSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidTaggedProfile(final CompletionStatus completionStatus) {
        return this.invalidTaggedProfile(completionStatus, null);
    }
    
    public BAD_PARAM invalidTaggedProfile(final Throwable t) {
        return this.invalidTaggedProfile(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM invalidTaggedProfile() {
        return this.invalidTaggedProfile(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM badIiopAddressPort(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080691, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.badIiopAddressPort", new Object[] { o }, IORSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM badIiopAddressPort(final CompletionStatus completionStatus, final Object o) {
        return this.badIiopAddressPort(completionStatus, null, o);
    }
    
    public BAD_PARAM badIiopAddressPort(final Throwable t, final Object o) {
        return this.badIiopAddressPort(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM badIiopAddressPort(final Object o) {
        return this.badIiopAddressPort(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INV_OBJREF iorMustHaveIiopProfile(final CompletionStatus completionStatus, final Throwable t) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1398080689, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "IOR.iorMustHaveIiopProfile", null, IORSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF iorMustHaveIiopProfile(final CompletionStatus completionStatus) {
        return this.iorMustHaveIiopProfile(completionStatus, null);
    }
    
    public INV_OBJREF iorMustHaveIiopProfile(final Throwable t) {
        return this.iorMustHaveIiopProfile(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_OBJREF iorMustHaveIiopProfile() {
        return this.iorMustHaveIiopProfile(CompletionStatus.COMPLETED_NO, null);
    }
    
    static {
        IORSystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new IORSystemException(logger);
            }
        };
    }
}
