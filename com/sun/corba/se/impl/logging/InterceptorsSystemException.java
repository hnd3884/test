package com.sun.corba.se.impl.logging;

import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.BAD_INV_ORDER;
import java.util.logging.Level;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class InterceptorsSystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int TYPE_OUT_OF_RANGE = 1398080289;
    public static final int NAME_NULL = 1398080290;
    public static final int RIR_INVALID_PRE_INIT = 1398080289;
    public static final int BAD_STATE1 = 1398080290;
    public static final int BAD_STATE2 = 1398080291;
    public static final int IOEXCEPTION_DURING_CANCEL_REQUEST = 1398080289;
    public static final int EXCEPTION_WAS_NULL = 1398080289;
    public static final int OBJECT_HAS_NO_DELEGATE = 1398080290;
    public static final int DELEGATE_NOT_CLIENTSUB = 1398080291;
    public static final int OBJECT_NOT_OBJECTIMPL = 1398080292;
    public static final int EXCEPTION_INVALID = 1398080293;
    public static final int REPLY_STATUS_NOT_INIT = 1398080294;
    public static final int EXCEPTION_IN_ARGUMENTS = 1398080295;
    public static final int EXCEPTION_IN_EXCEPTIONS = 1398080296;
    public static final int EXCEPTION_IN_CONTEXTS = 1398080297;
    public static final int EXCEPTION_WAS_NULL_2 = 1398080298;
    public static final int SERVANT_INVALID = 1398080299;
    public static final int CANT_POP_ONLY_PICURRENT = 1398080300;
    public static final int CANT_POP_ONLY_CURRENT_2 = 1398080301;
    public static final int PI_DSI_RESULT_IS_NULL = 1398080302;
    public static final int PI_DII_RESULT_IS_NULL = 1398080303;
    public static final int EXCEPTION_UNAVAILABLE = 1398080304;
    public static final int CLIENT_INFO_STACK_NULL = 1398080305;
    public static final int SERVER_INFO_STACK_NULL = 1398080306;
    public static final int MARK_AND_RESET_FAILED = 1398080307;
    public static final int SLOT_TABLE_INVARIANT = 1398080308;
    public static final int INTERCEPTOR_LIST_LOCKED = 1398080309;
    public static final int SORT_SIZE_MISMATCH = 1398080310;
    public static final int PI_ORB_NOT_POLICY_BASED = 1398080289;
    public static final int ORBINITINFO_INVALID = 1398080289;
    public static final int UNKNOWN_REQUEST_INVOKE = 1398080289;
    
    public InterceptorsSystemException(final Logger logger) {
        super(logger);
    }
    
    public static InterceptorsSystemException get(final ORB orb, final String s) {
        return (InterceptorsSystemException)orb.getLogWrapper(s, "INTERCEPTORS", InterceptorsSystemException.factory);
    }
    
    public static InterceptorsSystemException get(final String s) {
        return (InterceptorsSystemException)ORB.staticGetLogWrapper(s, "INTERCEPTORS", InterceptorsSystemException.factory);
    }
    
    public BAD_PARAM typeOutOfRange(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080289, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.typeOutOfRange", new Object[] { o }, InterceptorsSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM typeOutOfRange(final CompletionStatus completionStatus, final Object o) {
        return this.typeOutOfRange(completionStatus, null, o);
    }
    
    public BAD_PARAM typeOutOfRange(final Throwable t, final Object o) {
        return this.typeOutOfRange(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM typeOutOfRange(final Object o) {
        return this.typeOutOfRange(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM nameNull(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080290, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.nameNull", null, InterceptorsSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM nameNull(final CompletionStatus completionStatus) {
        return this.nameNull(completionStatus, null);
    }
    
    public BAD_PARAM nameNull(final Throwable t) {
        return this.nameNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM nameNull() {
        return this.nameNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER rirInvalidPreInit(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398080289, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.rirInvalidPreInit", null, InterceptorsSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER rirInvalidPreInit(final CompletionStatus completionStatus) {
        return this.rirInvalidPreInit(completionStatus, null);
    }
    
    public BAD_INV_ORDER rirInvalidPreInit(final Throwable t) {
        return this.rirInvalidPreInit(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER rirInvalidPreInit() {
        return this.rirInvalidPreInit(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badState1(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398080290, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.badState1", new Object[] { o, o2 }, InterceptorsSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badState1(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.badState1(completionStatus, null, o, o2);
    }
    
    public BAD_INV_ORDER badState1(final Throwable t, final Object o, final Object o2) {
        return this.badState1(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public BAD_INV_ORDER badState1(final Object o, final Object o2) {
        return this.badState1(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public BAD_INV_ORDER badState2(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398080291, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.badState2", new Object[] { o, o2, o3 }, InterceptorsSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badState2(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.badState2(completionStatus, null, o, o2, o3);
    }
    
    public BAD_INV_ORDER badState2(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.badState2(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public BAD_INV_ORDER badState2(final Object o, final Object o2, final Object o3) {
        return this.badState2(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public COMM_FAILURE ioexceptionDuringCancelRequest(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398080289, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.ioexceptionDuringCancelRequest", null, InterceptorsSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE ioexceptionDuringCancelRequest(final CompletionStatus completionStatus) {
        return this.ioexceptionDuringCancelRequest(completionStatus, null);
    }
    
    public COMM_FAILURE ioexceptionDuringCancelRequest(final Throwable t) {
        return this.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE ioexceptionDuringCancelRequest() {
        return this.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionWasNull(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080289, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.exceptionWasNull", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionWasNull(final CompletionStatus completionStatus) {
        return this.exceptionWasNull(completionStatus, null);
    }
    
    public INTERNAL exceptionWasNull(final Throwable t) {
        return this.exceptionWasNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionWasNull() {
        return this.exceptionWasNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL objectHasNoDelegate(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080290, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.objectHasNoDelegate", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL objectHasNoDelegate(final CompletionStatus completionStatus) {
        return this.objectHasNoDelegate(completionStatus, null);
    }
    
    public INTERNAL objectHasNoDelegate(final Throwable t) {
        return this.objectHasNoDelegate(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL objectHasNoDelegate() {
        return this.objectHasNoDelegate(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL delegateNotClientsub(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080291, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.delegateNotClientsub", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL delegateNotClientsub(final CompletionStatus completionStatus) {
        return this.delegateNotClientsub(completionStatus, null);
    }
    
    public INTERNAL delegateNotClientsub(final Throwable t) {
        return this.delegateNotClientsub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL delegateNotClientsub() {
        return this.delegateNotClientsub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL objectNotObjectimpl(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080292, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.objectNotObjectimpl", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL objectNotObjectimpl(final CompletionStatus completionStatus) {
        return this.objectNotObjectimpl(completionStatus, null);
    }
    
    public INTERNAL objectNotObjectimpl(final Throwable t) {
        return this.objectNotObjectimpl(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL objectNotObjectimpl() {
        return this.objectNotObjectimpl(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionInvalid(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080293, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInvalid", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionInvalid(final CompletionStatus completionStatus) {
        return this.exceptionInvalid(completionStatus, null);
    }
    
    public INTERNAL exceptionInvalid(final Throwable t) {
        return this.exceptionInvalid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionInvalid() {
        return this.exceptionInvalid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL replyStatusNotInit(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080294, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.replyStatusNotInit", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL replyStatusNotInit(final CompletionStatus completionStatus) {
        return this.replyStatusNotInit(completionStatus, null);
    }
    
    public INTERNAL replyStatusNotInit(final Throwable t) {
        return this.replyStatusNotInit(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL replyStatusNotInit() {
        return this.replyStatusNotInit(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionInArguments(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080295, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInArguments", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionInArguments(final CompletionStatus completionStatus) {
        return this.exceptionInArguments(completionStatus, null);
    }
    
    public INTERNAL exceptionInArguments(final Throwable t) {
        return this.exceptionInArguments(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionInArguments() {
        return this.exceptionInArguments(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionInExceptions(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080296, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInExceptions", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionInExceptions(final CompletionStatus completionStatus) {
        return this.exceptionInExceptions(completionStatus, null);
    }
    
    public INTERNAL exceptionInExceptions(final Throwable t) {
        return this.exceptionInExceptions(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionInExceptions() {
        return this.exceptionInExceptions(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionInContexts(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080297, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.exceptionInContexts", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionInContexts(final CompletionStatus completionStatus) {
        return this.exceptionInContexts(completionStatus, null);
    }
    
    public INTERNAL exceptionInContexts(final Throwable t) {
        return this.exceptionInContexts(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionInContexts() {
        return this.exceptionInContexts(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionWasNull2(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080298, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.exceptionWasNull2", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionWasNull2(final CompletionStatus completionStatus) {
        return this.exceptionWasNull2(completionStatus, null);
    }
    
    public INTERNAL exceptionWasNull2(final Throwable t) {
        return this.exceptionWasNull2(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionWasNull2() {
        return this.exceptionWasNull2(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL servantInvalid(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080299, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.servantInvalid", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL servantInvalid(final CompletionStatus completionStatus) {
        return this.servantInvalid(completionStatus, null);
    }
    
    public INTERNAL servantInvalid(final Throwable t) {
        return this.servantInvalid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL servantInvalid() {
        return this.servantInvalid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL cantPopOnlyPicurrent(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080300, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.cantPopOnlyPicurrent", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL cantPopOnlyPicurrent(final CompletionStatus completionStatus) {
        return this.cantPopOnlyPicurrent(completionStatus, null);
    }
    
    public INTERNAL cantPopOnlyPicurrent(final Throwable t) {
        return this.cantPopOnlyPicurrent(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL cantPopOnlyPicurrent() {
        return this.cantPopOnlyPicurrent(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL cantPopOnlyCurrent2(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080301, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.cantPopOnlyCurrent2", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL cantPopOnlyCurrent2(final CompletionStatus completionStatus) {
        return this.cantPopOnlyCurrent2(completionStatus, null);
    }
    
    public INTERNAL cantPopOnlyCurrent2(final Throwable t) {
        return this.cantPopOnlyCurrent2(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL cantPopOnlyCurrent2() {
        return this.cantPopOnlyCurrent2(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL piDsiResultIsNull(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080302, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.piDsiResultIsNull", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL piDsiResultIsNull(final CompletionStatus completionStatus) {
        return this.piDsiResultIsNull(completionStatus, null);
    }
    
    public INTERNAL piDsiResultIsNull(final Throwable t) {
        return this.piDsiResultIsNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL piDsiResultIsNull() {
        return this.piDsiResultIsNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL piDiiResultIsNull(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080303, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.piDiiResultIsNull", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL piDiiResultIsNull(final CompletionStatus completionStatus) {
        return this.piDiiResultIsNull(completionStatus, null);
    }
    
    public INTERNAL piDiiResultIsNull(final Throwable t) {
        return this.piDiiResultIsNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL piDiiResultIsNull() {
        return this.piDiiResultIsNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionUnavailable(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080304, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.exceptionUnavailable", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionUnavailable(final CompletionStatus completionStatus) {
        return this.exceptionUnavailable(completionStatus, null);
    }
    
    public INTERNAL exceptionUnavailable(final Throwable t) {
        return this.exceptionUnavailable(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionUnavailable() {
        return this.exceptionUnavailable(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL clientInfoStackNull(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080305, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.clientInfoStackNull", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL clientInfoStackNull(final CompletionStatus completionStatus) {
        return this.clientInfoStackNull(completionStatus, null);
    }
    
    public INTERNAL clientInfoStackNull(final Throwable t) {
        return this.clientInfoStackNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL clientInfoStackNull() {
        return this.clientInfoStackNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL serverInfoStackNull(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080306, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.serverInfoStackNull", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL serverInfoStackNull(final CompletionStatus completionStatus) {
        return this.serverInfoStackNull(completionStatus, null);
    }
    
    public INTERNAL serverInfoStackNull(final Throwable t) {
        return this.serverInfoStackNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL serverInfoStackNull() {
        return this.serverInfoStackNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL markAndResetFailed(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080307, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.markAndResetFailed", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL markAndResetFailed(final CompletionStatus completionStatus) {
        return this.markAndResetFailed(completionStatus, null);
    }
    
    public INTERNAL markAndResetFailed(final Throwable t) {
        return this.markAndResetFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL markAndResetFailed() {
        return this.markAndResetFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL slotTableInvariant(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398080308, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.slotTableInvariant", new Object[] { o, o2 }, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL slotTableInvariant(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.slotTableInvariant(completionStatus, null, o, o2);
    }
    
    public INTERNAL slotTableInvariant(final Throwable t, final Object o, final Object o2) {
        return this.slotTableInvariant(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL slotTableInvariant(final Object o, final Object o2) {
        return this.slotTableInvariant(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL interceptorListLocked(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080309, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.interceptorListLocked", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL interceptorListLocked(final CompletionStatus completionStatus) {
        return this.interceptorListLocked(completionStatus, null);
    }
    
    public INTERNAL interceptorListLocked(final Throwable t) {
        return this.interceptorListLocked(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL interceptorListLocked() {
        return this.interceptorListLocked(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL sortSizeMismatch(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080310, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.sortSizeMismatch", null, InterceptorsSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL sortSizeMismatch(final CompletionStatus completionStatus) {
        return this.sortSizeMismatch(completionStatus, null);
    }
    
    public INTERNAL sortSizeMismatch(final Throwable t) {
        return this.sortSizeMismatch(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL sortSizeMismatch() {
        return this.sortSizeMismatch(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT piOrbNotPolicyBased(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398080289, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "INTERCEPTORS.piOrbNotPolicyBased", null, InterceptorsSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT piOrbNotPolicyBased(final CompletionStatus completionStatus) {
        return this.piOrbNotPolicyBased(completionStatus, null);
    }
    
    public NO_IMPLEMENT piOrbNotPolicyBased(final Throwable t) {
        return this.piOrbNotPolicyBased(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT piOrbNotPolicyBased() {
        return this.piOrbNotPolicyBased(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST orbinitinfoInvalid(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398080289, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "INTERCEPTORS.orbinitinfoInvalid", null, InterceptorsSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST orbinitinfoInvalid(final CompletionStatus completionStatus) {
        return this.orbinitinfoInvalid(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST orbinitinfoInvalid(final Throwable t) {
        return this.orbinitinfoInvalid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST orbinitinfoInvalid() {
        return this.orbinitinfoInvalid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownRequestInvoke(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080289, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "INTERCEPTORS.unknownRequestInvoke", null, InterceptorsSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownRequestInvoke(final CompletionStatus completionStatus) {
        return this.unknownRequestInvoke(completionStatus, null);
    }
    
    public UNKNOWN unknownRequestInvoke(final Throwable t) {
        return this.unknownRequestInvoke(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownRequestInvoke() {
        return this.unknownRequestInvoke(CompletionStatus.COMPLETED_NO, null);
    }
    
    static {
        InterceptorsSystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new InterceptorsSystemException(logger);
            }
        };
    }
}
