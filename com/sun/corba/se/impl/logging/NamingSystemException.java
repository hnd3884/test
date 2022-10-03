package com.sun.corba.se.impl.logging;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.UNKNOWN;
import java.util.logging.Level;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class NamingSystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int TRANSIENT_NAME_SERVER_BAD_PORT = 1398080088;
    public static final int TRANSIENT_NAME_SERVER_BAD_HOST = 1398080089;
    public static final int OBJECT_IS_NULL = 1398080090;
    public static final int INS_BAD_ADDRESS = 1398080091;
    public static final int BIND_UPDATE_CONTEXT_FAILED = 1398080088;
    public static final int BIND_FAILURE = 1398080089;
    public static final int RESOLVE_CONVERSION_FAILURE = 1398080090;
    public static final int RESOLVE_FAILURE = 1398080091;
    public static final int UNBIND_FAILURE = 1398080092;
    public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC_SYS = 1398080138;
    public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC = 1398080139;
    public static final int NAMING_CTX_REBIND_ALREADY_BOUND = 1398080088;
    public static final int NAMING_CTX_REBINDCTX_ALREADY_BOUND = 1398080089;
    public static final int NAMING_CTX_BAD_BINDINGTYPE = 1398080090;
    public static final int NAMING_CTX_RESOLVE_CANNOT_NARROW_TO_CTX = 1398080091;
    public static final int NAMING_CTX_BINDING_ITERATOR_CREATE = 1398080092;
    public static final int TRANS_NC_BIND_ALREADY_BOUND = 1398080188;
    public static final int TRANS_NC_LIST_GOT_EXC = 1398080189;
    public static final int TRANS_NC_NEWCTX_GOT_EXC = 1398080190;
    public static final int TRANS_NC_DESTROY_GOT_EXC = 1398080191;
    public static final int INS_BAD_SCHEME_NAME = 1398080193;
    public static final int INS_BAD_SCHEME_SPECIFIC_PART = 1398080195;
    public static final int INS_OTHER = 1398080196;
    
    public NamingSystemException(final Logger logger) {
        super(logger);
    }
    
    public static NamingSystemException get(final ORB orb, final String s) {
        return (NamingSystemException)orb.getLogWrapper(s, "NAMING", NamingSystemException.factory);
    }
    
    public static NamingSystemException get(final String s) {
        return (NamingSystemException)ORB.staticGetLogWrapper(s, "NAMING", NamingSystemException.factory);
    }
    
    public BAD_PARAM transientNameServerBadPort(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080088, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transientNameServerBadPort", null, NamingSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM transientNameServerBadPort(final CompletionStatus completionStatus) {
        return this.transientNameServerBadPort(completionStatus, null);
    }
    
    public BAD_PARAM transientNameServerBadPort(final Throwable t) {
        return this.transientNameServerBadPort(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM transientNameServerBadPort() {
        return this.transientNameServerBadPort(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM transientNameServerBadHost(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080089, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transientNameServerBadHost", null, NamingSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM transientNameServerBadHost(final CompletionStatus completionStatus) {
        return this.transientNameServerBadHost(completionStatus, null);
    }
    
    public BAD_PARAM transientNameServerBadHost(final Throwable t) {
        return this.transientNameServerBadHost(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM transientNameServerBadHost() {
        return this.transientNameServerBadHost(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM objectIsNull(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080090, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.objectIsNull", null, NamingSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM objectIsNull(final CompletionStatus completionStatus) {
        return this.objectIsNull(completionStatus, null);
    }
    
    public BAD_PARAM objectIsNull(final Throwable t) {
        return this.objectIsNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM objectIsNull() {
        return this.objectIsNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM insBadAddress(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080091, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.insBadAddress", null, NamingSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM insBadAddress(final CompletionStatus completionStatus) {
        return this.insBadAddress(completionStatus, null);
    }
    
    public BAD_PARAM insBadAddress(final Throwable t) {
        return this.insBadAddress(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM insBadAddress() {
        return this.insBadAddress(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN bindUpdateContextFailed(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080088, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.bindUpdateContextFailed", null, NamingSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN bindUpdateContextFailed(final CompletionStatus completionStatus) {
        return this.bindUpdateContextFailed(completionStatus, null);
    }
    
    public UNKNOWN bindUpdateContextFailed(final Throwable t) {
        return this.bindUpdateContextFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN bindUpdateContextFailed() {
        return this.bindUpdateContextFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN bindFailure(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080089, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.bindFailure", null, NamingSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN bindFailure(final CompletionStatus completionStatus) {
        return this.bindFailure(completionStatus, null);
    }
    
    public UNKNOWN bindFailure(final Throwable t) {
        return this.bindFailure(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN bindFailure() {
        return this.bindFailure(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN resolveConversionFailure(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080090, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.resolveConversionFailure", null, NamingSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN resolveConversionFailure(final CompletionStatus completionStatus) {
        return this.resolveConversionFailure(completionStatus, null);
    }
    
    public UNKNOWN resolveConversionFailure(final Throwable t) {
        return this.resolveConversionFailure(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN resolveConversionFailure() {
        return this.resolveConversionFailure(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN resolveFailure(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080091, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.resolveFailure", null, NamingSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN resolveFailure(final CompletionStatus completionStatus) {
        return this.resolveFailure(completionStatus, null);
    }
    
    public UNKNOWN resolveFailure(final Throwable t) {
        return this.resolveFailure(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN resolveFailure() {
        return this.resolveFailure(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unbindFailure(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080092, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.unbindFailure", null, NamingSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unbindFailure(final CompletionStatus completionStatus) {
        return this.unbindFailure(completionStatus, null);
    }
    
    public UNKNOWN unbindFailure(final Throwable t) {
        return this.unbindFailure(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unbindFailure() {
        return this.unbindFailure(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE transNsCannotCreateInitialNcSys(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398080138, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transNsCannotCreateInitialNcSys", null, NamingSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE transNsCannotCreateInitialNcSys(final CompletionStatus completionStatus) {
        return this.transNsCannotCreateInitialNcSys(completionStatus, null);
    }
    
    public INITIALIZE transNsCannotCreateInitialNcSys(final Throwable t) {
        return this.transNsCannotCreateInitialNcSys(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE transNsCannotCreateInitialNcSys() {
        return this.transNsCannotCreateInitialNcSys(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE transNsCannotCreateInitialNc(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398080139, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transNsCannotCreateInitialNc", null, NamingSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE transNsCannotCreateInitialNc(final CompletionStatus completionStatus) {
        return this.transNsCannotCreateInitialNc(completionStatus, null);
    }
    
    public INITIALIZE transNsCannotCreateInitialNc(final Throwable t) {
        return this.transNsCannotCreateInitialNc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE transNsCannotCreateInitialNc() {
        return this.transNsCannotCreateInitialNc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL namingCtxRebindAlreadyBound(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080088, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.namingCtxRebindAlreadyBound", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL namingCtxRebindAlreadyBound(final CompletionStatus completionStatus) {
        return this.namingCtxRebindAlreadyBound(completionStatus, null);
    }
    
    public INTERNAL namingCtxRebindAlreadyBound(final Throwable t) {
        return this.namingCtxRebindAlreadyBound(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL namingCtxRebindAlreadyBound() {
        return this.namingCtxRebindAlreadyBound(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL namingCtxRebindctxAlreadyBound(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080089, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.namingCtxRebindctxAlreadyBound", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL namingCtxRebindctxAlreadyBound(final CompletionStatus completionStatus) {
        return this.namingCtxRebindctxAlreadyBound(completionStatus, null);
    }
    
    public INTERNAL namingCtxRebindctxAlreadyBound(final Throwable t) {
        return this.namingCtxRebindctxAlreadyBound(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL namingCtxRebindctxAlreadyBound() {
        return this.namingCtxRebindctxAlreadyBound(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL namingCtxBadBindingtype(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080090, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.namingCtxBadBindingtype", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL namingCtxBadBindingtype(final CompletionStatus completionStatus) {
        return this.namingCtxBadBindingtype(completionStatus, null);
    }
    
    public INTERNAL namingCtxBadBindingtype(final Throwable t) {
        return this.namingCtxBadBindingtype(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL namingCtxBadBindingtype() {
        return this.namingCtxBadBindingtype(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL namingCtxResolveCannotNarrowToCtx(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080091, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.namingCtxResolveCannotNarrowToCtx", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL namingCtxResolveCannotNarrowToCtx(final CompletionStatus completionStatus) {
        return this.namingCtxResolveCannotNarrowToCtx(completionStatus, null);
    }
    
    public INTERNAL namingCtxResolveCannotNarrowToCtx(final Throwable t) {
        return this.namingCtxResolveCannotNarrowToCtx(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL namingCtxResolveCannotNarrowToCtx() {
        return this.namingCtxResolveCannotNarrowToCtx(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL namingCtxBindingIteratorCreate(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080092, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.namingCtxBindingIteratorCreate", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL namingCtxBindingIteratorCreate(final CompletionStatus completionStatus) {
        return this.namingCtxBindingIteratorCreate(completionStatus, null);
    }
    
    public INTERNAL namingCtxBindingIteratorCreate(final Throwable t) {
        return this.namingCtxBindingIteratorCreate(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL namingCtxBindingIteratorCreate() {
        return this.namingCtxBindingIteratorCreate(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL transNcBindAlreadyBound(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080188, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transNcBindAlreadyBound", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL transNcBindAlreadyBound(final CompletionStatus completionStatus) {
        return this.transNcBindAlreadyBound(completionStatus, null);
    }
    
    public INTERNAL transNcBindAlreadyBound(final Throwable t) {
        return this.transNcBindAlreadyBound(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL transNcBindAlreadyBound() {
        return this.transNcBindAlreadyBound(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL transNcListGotExc(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080189, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transNcListGotExc", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL transNcListGotExc(final CompletionStatus completionStatus) {
        return this.transNcListGotExc(completionStatus, null);
    }
    
    public INTERNAL transNcListGotExc(final Throwable t) {
        return this.transNcListGotExc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL transNcListGotExc() {
        return this.transNcListGotExc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL transNcNewctxGotExc(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080190, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transNcNewctxGotExc", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL transNcNewctxGotExc(final CompletionStatus completionStatus) {
        return this.transNcNewctxGotExc(completionStatus, null);
    }
    
    public INTERNAL transNcNewctxGotExc(final Throwable t) {
        return this.transNcNewctxGotExc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL transNcNewctxGotExc() {
        return this.transNcNewctxGotExc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL transNcDestroyGotExc(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080191, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.transNcDestroyGotExc", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL transNcDestroyGotExc(final CompletionStatus completionStatus) {
        return this.transNcDestroyGotExc(completionStatus, null);
    }
    
    public INTERNAL transNcDestroyGotExc(final Throwable t) {
        return this.transNcDestroyGotExc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL transNcDestroyGotExc() {
        return this.transNcDestroyGotExc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL insBadSchemeName(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080193, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.insBadSchemeName", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL insBadSchemeName(final CompletionStatus completionStatus) {
        return this.insBadSchemeName(completionStatus, null);
    }
    
    public INTERNAL insBadSchemeName(final Throwable t) {
        return this.insBadSchemeName(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL insBadSchemeName() {
        return this.insBadSchemeName(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL insBadSchemeSpecificPart(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080195, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.insBadSchemeSpecificPart", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL insBadSchemeSpecificPart(final CompletionStatus completionStatus) {
        return this.insBadSchemeSpecificPart(completionStatus, null);
    }
    
    public INTERNAL insBadSchemeSpecificPart(final Throwable t) {
        return this.insBadSchemeSpecificPart(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL insBadSchemeSpecificPart() {
        return this.insBadSchemeSpecificPart(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL insOther(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080196, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "NAMING.insOther", null, NamingSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL insOther(final CompletionStatus completionStatus) {
        return this.insOther(completionStatus, null);
    }
    
    public INTERNAL insOther(final Throwable t) {
        return this.insOther(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL insOther() {
        return this.insOther(CompletionStatus.COMPLETED_NO, null);
    }
    
    static {
        NamingSystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new NamingSystemException(logger);
            }
        };
    }
}
