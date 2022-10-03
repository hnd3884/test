package com.sun.corba.se.impl.logging;

import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.NO_RESOURCES;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.INTF_REPOS;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_INV_ORDER;
import java.util.logging.Level;
import org.omg.CORBA.BAD_CONTEXT;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class OMGSystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int IDL_CONTEXT_NOT_FOUND = 1330446337;
    public static final int NO_MATCHING_IDL_CONTEXT = 1330446338;
    public static final int DEP_PREVENT_DESTRUCTION = 1330446337;
    public static final int DESTROY_INDESTRUCTIBLE = 1330446338;
    public static final int SHUTDOWN_WAIT_FOR_COMPLETION_DEADLOCK = 1330446339;
    public static final int BAD_OPERATION_AFTER_SHUTDOWN = 1330446340;
    public static final int BAD_INVOKE = 1330446341;
    public static final int BAD_SET_SERVANT_MANAGER = 1330446342;
    public static final int BAD_ARGUMENTS_CALL = 1330446343;
    public static final int BAD_CTX_CALL = 1330446344;
    public static final int BAD_RESULT_CALL = 1330446345;
    public static final int BAD_SEND = 1330446346;
    public static final int BAD_POLL_BEFORE = 1330446347;
    public static final int BAD_POLL_AFTER = 1330446348;
    public static final int BAD_POLL_SYNC = 1330446349;
    public static final int INVALID_PI_CALL1 = 1330446350;
    public static final int INVALID_PI_CALL2 = 1330446350;
    public static final int INVALID_PI_CALL3 = 1330446350;
    public static final int INVALID_PI_CALL4 = 1330446350;
    public static final int SERVICE_CONTEXT_ADD_FAILED = 1330446351;
    public static final int POLICY_FACTORY_REG_FAILED = 1330446352;
    public static final int CREATE_POA_DESTROY = 1330446353;
    public static final int PRIORITY_REASSIGN = 1330446354;
    public static final int XA_START_OUTSIZE = 1330446355;
    public static final int XA_START_PROTO = 1330446356;
    public static final int BAD_SERVANT_MANAGER_TYPE = 1330446337;
    public static final int OPERATION_UNKNOWN_TO_TARGET = 1330446338;
    public static final int UNABLE_REGISTER_VALUE_FACTORY = 1330446337;
    public static final int RID_ALREADY_DEFINED = 1330446338;
    public static final int NAME_USED_IFR = 1330446339;
    public static final int TARGET_NOT_CONTAINER = 1330446340;
    public static final int NAME_CLASH = 1330446341;
    public static final int NOT_SERIALIZABLE = 1330446342;
    public static final int SO_BAD_SCHEME_NAME = 1330446343;
    public static final int SO_BAD_ADDRESS = 1330446344;
    public static final int SO_BAD_SCHEMA_SPECIFIC = 1330446345;
    public static final int SO_NON_SPECIFIC = 1330446346;
    public static final int IR_DERIVE_ABS_INT_BASE = 1330446347;
    public static final int IR_VALUE_SUPPORT = 1330446348;
    public static final int INCOMPLETE_TYPECODE = 1330446349;
    public static final int INVALID_OBJECT_ID = 1330446350;
    public static final int TYPECODE_BAD_NAME = 1330446351;
    public static final int TYPECODE_BAD_REPID = 1330446352;
    public static final int TYPECODE_INV_MEMBER = 1330446353;
    public static final int TC_UNION_DUP_LABEL = 1330446354;
    public static final int TC_UNION_INCOMPATIBLE = 1330446355;
    public static final int TC_UNION_BAD_DISC = 1330446356;
    public static final int SET_EXCEPTION_BAD_ANY = 1330446357;
    public static final int SET_EXCEPTION_UNLISTED = 1330446358;
    public static final int NO_CLIENT_WCHAR_CODESET_CTX = 1330446359;
    public static final int ILLEGAL_SERVICE_CONTEXT = 1330446360;
    public static final int ENUM_OUT_OF_RANGE = 1330446361;
    public static final int INVALID_SERVICE_CONTEXT_ID = 1330446362;
    public static final int RIR_WITH_NULL_OBJECT = 1330446363;
    public static final int INVALID_COMPONENT_ID = 1330446364;
    public static final int INVALID_PROFILE_ID = 1330446365;
    public static final int POLICY_TYPE_DUPLICATE = 1330446366;
    public static final int BAD_ONEWAY_DEFINITION = 1330446367;
    public static final int DII_FOR_IMPLICIT_OPERATION = 1330446368;
    public static final int XA_CALL_INVAL = 1330446369;
    public static final int UNION_BAD_DISCRIMINATOR = 1330446370;
    public static final int CTX_ILLEGAL_PROPERTY_NAME = 1330446371;
    public static final int CTX_ILLEGAL_SEARCH_STRING = 1330446372;
    public static final int CTX_ILLEGAL_NAME = 1330446373;
    public static final int CTX_NON_EMPTY = 1330446374;
    public static final int INVALID_STREAM_FORMAT_VERSION = 1330446375;
    public static final int NOT_A_VALUEOUTPUTSTREAM = 1330446376;
    public static final int NOT_A_VALUEINPUTSTREAM = 1330446377;
    public static final int MARSHALL_INCOMPLETE_TYPECODE = 1330446337;
    public static final int BAD_MEMBER_TYPECODE = 1330446338;
    public static final int ILLEGAL_PARAMETER = 1330446339;
    public static final int CHAR_NOT_IN_CODESET = 1330446337;
    public static final int PRIORITY_MAP_FAILRE = 1330446338;
    public static final int NO_USABLE_PROFILE = 1330446337;
    public static final int PRIORITY_RANGE_RESTRICT = 1330446337;
    public static final int NO_SERVER_WCHAR_CODESET_CMP = 1330446337;
    public static final int CODESET_COMPONENT_REQUIRED = 1330446338;
    public static final int IOR_POLICY_RECONCILE_ERROR = 1330446337;
    public static final int POLICY_UNKNOWN = 1330446338;
    public static final int NO_POLICY_FACTORY = 1330446339;
    public static final int XA_RMERR = 1330446337;
    public static final int XA_RMFAIL = 1330446338;
    public static final int NO_IR = 1330446337;
    public static final int NO_INTERFACE_IN_IR = 1330446338;
    public static final int UNABLE_LOCATE_VALUE_FACTORY = 1330446337;
    public static final int SET_RESULT_BEFORE_CTX = 1330446338;
    public static final int BAD_NVLIST = 1330446339;
    public static final int NOT_AN_OBJECT_IMPL = 1330446340;
    public static final int WCHAR_BAD_GIOP_VERSION_SENT = 1330446341;
    public static final int WCHAR_BAD_GIOP_VERSION_RETURNED = 1330446342;
    public static final int UNSUPPORTED_FORMAT_VERSION = 1330446343;
    public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1 = 1330446344;
    public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE2 = 1330446344;
    public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE3 = 1330446344;
    public static final int MISSING_LOCAL_VALUE_IMPL = 1330446337;
    public static final int INCOMPATIBLE_VALUE_IMPL = 1330446338;
    public static final int NO_USABLE_PROFILE_2 = 1330446339;
    public static final int DII_LOCAL_OBJECT = 1330446340;
    public static final int BIO_RESET = 1330446341;
    public static final int BIO_META_NOT_AVAILABLE = 1330446342;
    public static final int BIO_GENOMIC_NO_ITERATOR = 1330446343;
    public static final int PI_OPERATION_NOT_SUPPORTED1 = 1330446337;
    public static final int PI_OPERATION_NOT_SUPPORTED2 = 1330446337;
    public static final int PI_OPERATION_NOT_SUPPORTED3 = 1330446337;
    public static final int PI_OPERATION_NOT_SUPPORTED4 = 1330446337;
    public static final int PI_OPERATION_NOT_SUPPORTED5 = 1330446337;
    public static final int PI_OPERATION_NOT_SUPPORTED6 = 1330446337;
    public static final int PI_OPERATION_NOT_SUPPORTED7 = 1330446337;
    public static final int PI_OPERATION_NOT_SUPPORTED8 = 1330446337;
    public static final int NO_CONNECTION_PRIORITY = 1330446338;
    public static final int XA_RB = 1330446337;
    public static final int XA_NOTA = 1330446338;
    public static final int XA_END_TRUE_ROLLBACK_DEFERRED = 1330446339;
    public static final int POA_REQUEST_DISCARD = 1330446337;
    public static final int NO_USABLE_PROFILE_3 = 1330446338;
    public static final int REQUEST_CANCELLED = 1330446339;
    public static final int POA_DESTROYED = 1330446340;
    public static final int UNREGISTERED_VALUE_AS_OBJREF = 1330446337;
    public static final int NO_OBJECT_ADAPTOR = 1330446338;
    public static final int BIO_NOT_AVAILABLE = 1330446339;
    public static final int OBJECT_ADAPTER_INACTIVE = 1330446340;
    public static final int ADAPTER_ACTIVATOR_EXCEPTION = 1330446337;
    public static final int BAD_SERVANT_TYPE = 1330446338;
    public static final int NO_DEFAULT_SERVANT = 1330446339;
    public static final int NO_SERVANT_MANAGER = 1330446340;
    public static final int BAD_POLICY_INCARNATE = 1330446341;
    public static final int PI_EXC_COMP_ESTABLISHED = 1330446342;
    public static final int NULL_SERVANT_RETURNED = 1330446343;
    public static final int UNKNOWN_USER_EXCEPTION = 1330446337;
    public static final int UNSUPPORTED_SYSTEM_EXCEPTION = 1330446338;
    public static final int PI_UNKNOWN_USER_EXCEPTION = 1330446339;
    
    public OMGSystemException(final Logger logger) {
        super(logger);
    }
    
    public static OMGSystemException get(final ORB orb, final String s) {
        return (OMGSystemException)orb.getLogWrapper(s, "OMG", OMGSystemException.factory);
    }
    
    public static OMGSystemException get(final String s) {
        return (OMGSystemException)ORB.staticGetLogWrapper(s, "OMG", OMGSystemException.factory);
    }
    
    public BAD_CONTEXT idlContextNotFound(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_CONTEXT bad_CONTEXT = new BAD_CONTEXT(1330446337, completionStatus);
        if (t != null) {
            bad_CONTEXT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.idlContextNotFound", null, OMGSystemException.class, bad_CONTEXT);
        }
        return bad_CONTEXT;
    }
    
    public BAD_CONTEXT idlContextNotFound(final CompletionStatus completionStatus) {
        return this.idlContextNotFound(completionStatus, null);
    }
    
    public BAD_CONTEXT idlContextNotFound(final Throwable t) {
        return this.idlContextNotFound(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_CONTEXT idlContextNotFound() {
        return this.idlContextNotFound(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_CONTEXT noMatchingIdlContext(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_CONTEXT bad_CONTEXT = new BAD_CONTEXT(1330446338, completionStatus);
        if (t != null) {
            bad_CONTEXT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noMatchingIdlContext", null, OMGSystemException.class, bad_CONTEXT);
        }
        return bad_CONTEXT;
    }
    
    public BAD_CONTEXT noMatchingIdlContext(final CompletionStatus completionStatus) {
        return this.noMatchingIdlContext(completionStatus, null);
    }
    
    public BAD_CONTEXT noMatchingIdlContext(final Throwable t) {
        return this.noMatchingIdlContext(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_CONTEXT noMatchingIdlContext() {
        return this.noMatchingIdlContext(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER depPreventDestruction(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446337, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.depPreventDestruction", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER depPreventDestruction(final CompletionStatus completionStatus) {
        return this.depPreventDestruction(completionStatus, null);
    }
    
    public BAD_INV_ORDER depPreventDestruction(final Throwable t) {
        return this.depPreventDestruction(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER depPreventDestruction() {
        return this.depPreventDestruction(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER destroyIndestructible(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446338, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.destroyIndestructible", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER destroyIndestructible(final CompletionStatus completionStatus) {
        return this.destroyIndestructible(completionStatus, null);
    }
    
    public BAD_INV_ORDER destroyIndestructible(final Throwable t) {
        return this.destroyIndestructible(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER destroyIndestructible() {
        return this.destroyIndestructible(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446339, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.shutdownWaitForCompletionDeadlock", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(final CompletionStatus completionStatus) {
        return this.shutdownWaitForCompletionDeadlock(completionStatus, null);
    }
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(final Throwable t) {
        return this.shutdownWaitForCompletionDeadlock(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER shutdownWaitForCompletionDeadlock() {
        return this.shutdownWaitForCompletionDeadlock(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badOperationAfterShutdown(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446340, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badOperationAfterShutdown", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badOperationAfterShutdown(final CompletionStatus completionStatus) {
        return this.badOperationAfterShutdown(completionStatus, null);
    }
    
    public BAD_INV_ORDER badOperationAfterShutdown(final Throwable t) {
        return this.badOperationAfterShutdown(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badOperationAfterShutdown() {
        return this.badOperationAfterShutdown(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badInvoke(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446341, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badInvoke", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badInvoke(final CompletionStatus completionStatus) {
        return this.badInvoke(completionStatus, null);
    }
    
    public BAD_INV_ORDER badInvoke(final Throwable t) {
        return this.badInvoke(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badInvoke() {
        return this.badInvoke(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badSetServantManager(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446342, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badSetServantManager", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badSetServantManager(final CompletionStatus completionStatus) {
        return this.badSetServantManager(completionStatus, null);
    }
    
    public BAD_INV_ORDER badSetServantManager(final Throwable t) {
        return this.badSetServantManager(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badSetServantManager() {
        return this.badSetServantManager(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badArgumentsCall(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446343, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badArgumentsCall", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badArgumentsCall(final CompletionStatus completionStatus) {
        return this.badArgumentsCall(completionStatus, null);
    }
    
    public BAD_INV_ORDER badArgumentsCall(final Throwable t) {
        return this.badArgumentsCall(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badArgumentsCall() {
        return this.badArgumentsCall(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badCtxCall(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446344, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badCtxCall", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badCtxCall(final CompletionStatus completionStatus) {
        return this.badCtxCall(completionStatus, null);
    }
    
    public BAD_INV_ORDER badCtxCall(final Throwable t) {
        return this.badCtxCall(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badCtxCall() {
        return this.badCtxCall(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badResultCall(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446345, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badResultCall", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badResultCall(final CompletionStatus completionStatus) {
        return this.badResultCall(completionStatus, null);
    }
    
    public BAD_INV_ORDER badResultCall(final Throwable t) {
        return this.badResultCall(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badResultCall() {
        return this.badResultCall(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badSend(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446346, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badSend", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badSend(final CompletionStatus completionStatus) {
        return this.badSend(completionStatus, null);
    }
    
    public BAD_INV_ORDER badSend(final Throwable t) {
        return this.badSend(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badSend() {
        return this.badSend(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badPollBefore(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446347, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badPollBefore", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badPollBefore(final CompletionStatus completionStatus) {
        return this.badPollBefore(completionStatus, null);
    }
    
    public BAD_INV_ORDER badPollBefore(final Throwable t) {
        return this.badPollBefore(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badPollBefore() {
        return this.badPollBefore(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badPollAfter(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446348, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badPollAfter", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badPollAfter(final CompletionStatus completionStatus) {
        return this.badPollAfter(completionStatus, null);
    }
    
    public BAD_INV_ORDER badPollAfter(final Throwable t) {
        return this.badPollAfter(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badPollAfter() {
        return this.badPollAfter(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER badPollSync(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446349, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badPollSync", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER badPollSync(final CompletionStatus completionStatus) {
        return this.badPollSync(completionStatus, null);
    }
    
    public BAD_INV_ORDER badPollSync(final Throwable t) {
        return this.badPollSync(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER badPollSync() {
        return this.badPollSync(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER invalidPiCall1(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446350, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.invalidPiCall1", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER invalidPiCall1(final CompletionStatus completionStatus) {
        return this.invalidPiCall1(completionStatus, null);
    }
    
    public BAD_INV_ORDER invalidPiCall1(final Throwable t) {
        return this.invalidPiCall1(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER invalidPiCall1() {
        return this.invalidPiCall1(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER invalidPiCall2(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446350, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.invalidPiCall2", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER invalidPiCall2(final CompletionStatus completionStatus) {
        return this.invalidPiCall2(completionStatus, null);
    }
    
    public BAD_INV_ORDER invalidPiCall2(final Throwable t) {
        return this.invalidPiCall2(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER invalidPiCall2() {
        return this.invalidPiCall2(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER invalidPiCall3(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446350, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.invalidPiCall3", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER invalidPiCall3(final CompletionStatus completionStatus) {
        return this.invalidPiCall3(completionStatus, null);
    }
    
    public BAD_INV_ORDER invalidPiCall3(final Throwable t) {
        return this.invalidPiCall3(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER invalidPiCall3() {
        return this.invalidPiCall3(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER invalidPiCall4(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446350, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.invalidPiCall4", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER invalidPiCall4(final CompletionStatus completionStatus) {
        return this.invalidPiCall4(completionStatus, null);
    }
    
    public BAD_INV_ORDER invalidPiCall4(final Throwable t) {
        return this.invalidPiCall4(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER invalidPiCall4() {
        return this.invalidPiCall4(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER serviceContextAddFailed(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446351, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.serviceContextAddFailed", new Object[] { o }, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER serviceContextAddFailed(final CompletionStatus completionStatus, final Object o) {
        return this.serviceContextAddFailed(completionStatus, null, o);
    }
    
    public BAD_INV_ORDER serviceContextAddFailed(final Throwable t, final Object o) {
        return this.serviceContextAddFailed(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_INV_ORDER serviceContextAddFailed(final Object o) {
        return this.serviceContextAddFailed(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_INV_ORDER policyFactoryRegFailed(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446352, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.policyFactoryRegFailed", new Object[] { o }, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER policyFactoryRegFailed(final CompletionStatus completionStatus, final Object o) {
        return this.policyFactoryRegFailed(completionStatus, null, o);
    }
    
    public BAD_INV_ORDER policyFactoryRegFailed(final Throwable t, final Object o) {
        return this.policyFactoryRegFailed(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_INV_ORDER policyFactoryRegFailed(final Object o) {
        return this.policyFactoryRegFailed(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_INV_ORDER createPoaDestroy(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446353, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.createPoaDestroy", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER createPoaDestroy(final CompletionStatus completionStatus) {
        return this.createPoaDestroy(completionStatus, null);
    }
    
    public BAD_INV_ORDER createPoaDestroy(final Throwable t) {
        return this.createPoaDestroy(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER createPoaDestroy() {
        return this.createPoaDestroy(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER priorityReassign(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446354, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.priorityReassign", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER priorityReassign(final CompletionStatus completionStatus) {
        return this.priorityReassign(completionStatus, null);
    }
    
    public BAD_INV_ORDER priorityReassign(final Throwable t) {
        return this.priorityReassign(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER priorityReassign() {
        return this.priorityReassign(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER xaStartOutsize(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446355, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaStartOutsize", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER xaStartOutsize(final CompletionStatus completionStatus) {
        return this.xaStartOutsize(completionStatus, null);
    }
    
    public BAD_INV_ORDER xaStartOutsize(final Throwable t) {
        return this.xaStartOutsize(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER xaStartOutsize() {
        return this.xaStartOutsize(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER xaStartProto(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1330446356, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaStartProto", null, OMGSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER xaStartProto(final CompletionStatus completionStatus) {
        return this.xaStartProto(completionStatus, null);
    }
    
    public BAD_INV_ORDER xaStartProto(final Throwable t) {
        return this.xaStartProto(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER xaStartProto() {
        return this.xaStartProto(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION badServantManagerType(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1330446337, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badServantManagerType", null, OMGSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION badServantManagerType(final CompletionStatus completionStatus) {
        return this.badServantManagerType(completionStatus, null);
    }
    
    public BAD_OPERATION badServantManagerType(final Throwable t) {
        return this.badServantManagerType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION badServantManagerType() {
        return this.badServantManagerType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION operationUnknownToTarget(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1330446338, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.operationUnknownToTarget", null, OMGSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION operationUnknownToTarget(final CompletionStatus completionStatus) {
        return this.operationUnknownToTarget(completionStatus, null);
    }
    
    public BAD_OPERATION operationUnknownToTarget(final Throwable t) {
        return this.operationUnknownToTarget(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION operationUnknownToTarget() {
        return this.operationUnknownToTarget(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM unableRegisterValueFactory(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446337, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.unableRegisterValueFactory", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM unableRegisterValueFactory(final CompletionStatus completionStatus) {
        return this.unableRegisterValueFactory(completionStatus, null);
    }
    
    public BAD_PARAM unableRegisterValueFactory(final Throwable t) {
        return this.unableRegisterValueFactory(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM unableRegisterValueFactory() {
        return this.unableRegisterValueFactory(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM ridAlreadyDefined(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446338, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.ridAlreadyDefined", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM ridAlreadyDefined(final CompletionStatus completionStatus) {
        return this.ridAlreadyDefined(completionStatus, null);
    }
    
    public BAD_PARAM ridAlreadyDefined(final Throwable t) {
        return this.ridAlreadyDefined(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM ridAlreadyDefined() {
        return this.ridAlreadyDefined(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM nameUsedIfr(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446339, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.nameUsedIfr", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM nameUsedIfr(final CompletionStatus completionStatus) {
        return this.nameUsedIfr(completionStatus, null);
    }
    
    public BAD_PARAM nameUsedIfr(final Throwable t) {
        return this.nameUsedIfr(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM nameUsedIfr() {
        return this.nameUsedIfr(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM targetNotContainer(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446340, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.targetNotContainer", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM targetNotContainer(final CompletionStatus completionStatus) {
        return this.targetNotContainer(completionStatus, null);
    }
    
    public BAD_PARAM targetNotContainer(final Throwable t) {
        return this.targetNotContainer(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM targetNotContainer() {
        return this.targetNotContainer(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM nameClash(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446341, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.nameClash", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM nameClash(final CompletionStatus completionStatus) {
        return this.nameClash(completionStatus, null);
    }
    
    public BAD_PARAM nameClash(final Throwable t) {
        return this.nameClash(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM nameClash() {
        return this.nameClash(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM notSerializable(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446342, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.notSerializable", new Object[] { o }, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM notSerializable(final CompletionStatus completionStatus, final Object o) {
        return this.notSerializable(completionStatus, null, o);
    }
    
    public BAD_PARAM notSerializable(final Throwable t, final Object o) {
        return this.notSerializable(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM notSerializable(final Object o) {
        return this.notSerializable(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM soBadSchemeName(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446343, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.soBadSchemeName", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM soBadSchemeName(final CompletionStatus completionStatus) {
        return this.soBadSchemeName(completionStatus, null);
    }
    
    public BAD_PARAM soBadSchemeName(final Throwable t) {
        return this.soBadSchemeName(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM soBadSchemeName() {
        return this.soBadSchemeName(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM soBadAddress(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446344, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.soBadAddress", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM soBadAddress(final CompletionStatus completionStatus) {
        return this.soBadAddress(completionStatus, null);
    }
    
    public BAD_PARAM soBadAddress(final Throwable t) {
        return this.soBadAddress(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM soBadAddress() {
        return this.soBadAddress(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM soBadSchemaSpecific(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446345, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.soBadSchemaSpecific", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM soBadSchemaSpecific(final CompletionStatus completionStatus) {
        return this.soBadSchemaSpecific(completionStatus, null);
    }
    
    public BAD_PARAM soBadSchemaSpecific(final Throwable t) {
        return this.soBadSchemaSpecific(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM soBadSchemaSpecific() {
        return this.soBadSchemaSpecific(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM soNonSpecific(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446346, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.soNonSpecific", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM soNonSpecific(final CompletionStatus completionStatus) {
        return this.soNonSpecific(completionStatus, null);
    }
    
    public BAD_PARAM soNonSpecific(final Throwable t) {
        return this.soNonSpecific(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM soNonSpecific() {
        return this.soNonSpecific(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM irDeriveAbsIntBase(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446347, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.irDeriveAbsIntBase", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM irDeriveAbsIntBase(final CompletionStatus completionStatus) {
        return this.irDeriveAbsIntBase(completionStatus, null);
    }
    
    public BAD_PARAM irDeriveAbsIntBase(final Throwable t) {
        return this.irDeriveAbsIntBase(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM irDeriveAbsIntBase() {
        return this.irDeriveAbsIntBase(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM irValueSupport(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446348, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.irValueSupport", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM irValueSupport(final CompletionStatus completionStatus) {
        return this.irValueSupport(completionStatus, null);
    }
    
    public BAD_PARAM irValueSupport(final Throwable t) {
        return this.irValueSupport(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM irValueSupport() {
        return this.irValueSupport(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM incompleteTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446349, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.incompleteTypecode", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM incompleteTypecode(final CompletionStatus completionStatus) {
        return this.incompleteTypecode(completionStatus, null);
    }
    
    public BAD_PARAM incompleteTypecode(final Throwable t) {
        return this.incompleteTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM incompleteTypecode() {
        return this.incompleteTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidObjectId(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446350, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.invalidObjectId", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidObjectId(final CompletionStatus completionStatus) {
        return this.invalidObjectId(completionStatus, null);
    }
    
    public BAD_PARAM invalidObjectId(final Throwable t) {
        return this.invalidObjectId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM invalidObjectId() {
        return this.invalidObjectId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM typecodeBadName(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446351, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.typecodeBadName", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM typecodeBadName(final CompletionStatus completionStatus) {
        return this.typecodeBadName(completionStatus, null);
    }
    
    public BAD_PARAM typecodeBadName(final Throwable t) {
        return this.typecodeBadName(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM typecodeBadName() {
        return this.typecodeBadName(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM typecodeBadRepid(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446352, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.typecodeBadRepid", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM typecodeBadRepid(final CompletionStatus completionStatus) {
        return this.typecodeBadRepid(completionStatus, null);
    }
    
    public BAD_PARAM typecodeBadRepid(final Throwable t) {
        return this.typecodeBadRepid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM typecodeBadRepid() {
        return this.typecodeBadRepid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM typecodeInvMember(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446353, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.typecodeInvMember", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM typecodeInvMember(final CompletionStatus completionStatus) {
        return this.typecodeInvMember(completionStatus, null);
    }
    
    public BAD_PARAM typecodeInvMember(final Throwable t) {
        return this.typecodeInvMember(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM typecodeInvMember() {
        return this.typecodeInvMember(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM tcUnionDupLabel(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446354, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.tcUnionDupLabel", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM tcUnionDupLabel(final CompletionStatus completionStatus) {
        return this.tcUnionDupLabel(completionStatus, null);
    }
    
    public BAD_PARAM tcUnionDupLabel(final Throwable t) {
        return this.tcUnionDupLabel(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM tcUnionDupLabel() {
        return this.tcUnionDupLabel(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM tcUnionIncompatible(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446355, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.tcUnionIncompatible", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM tcUnionIncompatible(final CompletionStatus completionStatus) {
        return this.tcUnionIncompatible(completionStatus, null);
    }
    
    public BAD_PARAM tcUnionIncompatible(final Throwable t) {
        return this.tcUnionIncompatible(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM tcUnionIncompatible() {
        return this.tcUnionIncompatible(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM tcUnionBadDisc(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446356, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.tcUnionBadDisc", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM tcUnionBadDisc(final CompletionStatus completionStatus) {
        return this.tcUnionBadDisc(completionStatus, null);
    }
    
    public BAD_PARAM tcUnionBadDisc(final Throwable t) {
        return this.tcUnionBadDisc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM tcUnionBadDisc() {
        return this.tcUnionBadDisc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM setExceptionBadAny(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446357, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.setExceptionBadAny", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM setExceptionBadAny(final CompletionStatus completionStatus) {
        return this.setExceptionBadAny(completionStatus, null);
    }
    
    public BAD_PARAM setExceptionBadAny(final Throwable t) {
        return this.setExceptionBadAny(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM setExceptionBadAny() {
        return this.setExceptionBadAny(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM setExceptionUnlisted(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446358, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.setExceptionUnlisted", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM setExceptionUnlisted(final CompletionStatus completionStatus) {
        return this.setExceptionUnlisted(completionStatus, null);
    }
    
    public BAD_PARAM setExceptionUnlisted(final Throwable t) {
        return this.setExceptionUnlisted(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM setExceptionUnlisted() {
        return this.setExceptionUnlisted(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM noClientWcharCodesetCtx(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446359, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noClientWcharCodesetCtx", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM noClientWcharCodesetCtx(final CompletionStatus completionStatus) {
        return this.noClientWcharCodesetCtx(completionStatus, null);
    }
    
    public BAD_PARAM noClientWcharCodesetCtx(final Throwable t) {
        return this.noClientWcharCodesetCtx(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM noClientWcharCodesetCtx() {
        return this.noClientWcharCodesetCtx(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM illegalServiceContext(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446360, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.illegalServiceContext", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM illegalServiceContext(final CompletionStatus completionStatus) {
        return this.illegalServiceContext(completionStatus, null);
    }
    
    public BAD_PARAM illegalServiceContext(final Throwable t) {
        return this.illegalServiceContext(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM illegalServiceContext() {
        return this.illegalServiceContext(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM enumOutOfRange(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446361, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.enumOutOfRange", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM enumOutOfRange(final CompletionStatus completionStatus) {
        return this.enumOutOfRange(completionStatus, null);
    }
    
    public BAD_PARAM enumOutOfRange(final Throwable t) {
        return this.enumOutOfRange(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM enumOutOfRange() {
        return this.enumOutOfRange(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidServiceContextId(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446362, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.invalidServiceContextId", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidServiceContextId(final CompletionStatus completionStatus) {
        return this.invalidServiceContextId(completionStatus, null);
    }
    
    public BAD_PARAM invalidServiceContextId(final Throwable t) {
        return this.invalidServiceContextId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM invalidServiceContextId() {
        return this.invalidServiceContextId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM rirWithNullObject(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446363, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.rirWithNullObject", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM rirWithNullObject(final CompletionStatus completionStatus) {
        return this.rirWithNullObject(completionStatus, null);
    }
    
    public BAD_PARAM rirWithNullObject(final Throwable t) {
        return this.rirWithNullObject(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM rirWithNullObject() {
        return this.rirWithNullObject(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidComponentId(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446364, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.invalidComponentId", new Object[] { o }, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidComponentId(final CompletionStatus completionStatus, final Object o) {
        return this.invalidComponentId(completionStatus, null, o);
    }
    
    public BAD_PARAM invalidComponentId(final Throwable t, final Object o) {
        return this.invalidComponentId(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM invalidComponentId(final Object o) {
        return this.invalidComponentId(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM invalidProfileId(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446365, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.invalidProfileId", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidProfileId(final CompletionStatus completionStatus) {
        return this.invalidProfileId(completionStatus, null);
    }
    
    public BAD_PARAM invalidProfileId(final Throwable t) {
        return this.invalidProfileId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM invalidProfileId() {
        return this.invalidProfileId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM policyTypeDuplicate(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446366, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.policyTypeDuplicate", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM policyTypeDuplicate(final CompletionStatus completionStatus) {
        return this.policyTypeDuplicate(completionStatus, null);
    }
    
    public BAD_PARAM policyTypeDuplicate(final Throwable t) {
        return this.policyTypeDuplicate(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM policyTypeDuplicate() {
        return this.policyTypeDuplicate(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM badOnewayDefinition(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446367, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badOnewayDefinition", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM badOnewayDefinition(final CompletionStatus completionStatus) {
        return this.badOnewayDefinition(completionStatus, null);
    }
    
    public BAD_PARAM badOnewayDefinition(final Throwable t) {
        return this.badOnewayDefinition(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM badOnewayDefinition() {
        return this.badOnewayDefinition(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM diiForImplicitOperation(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446368, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.diiForImplicitOperation", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM diiForImplicitOperation(final CompletionStatus completionStatus) {
        return this.diiForImplicitOperation(completionStatus, null);
    }
    
    public BAD_PARAM diiForImplicitOperation(final Throwable t) {
        return this.diiForImplicitOperation(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM diiForImplicitOperation() {
        return this.diiForImplicitOperation(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM xaCallInval(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446369, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaCallInval", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM xaCallInval(final CompletionStatus completionStatus) {
        return this.xaCallInval(completionStatus, null);
    }
    
    public BAD_PARAM xaCallInval(final Throwable t) {
        return this.xaCallInval(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM xaCallInval() {
        return this.xaCallInval(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM unionBadDiscriminator(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446370, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.unionBadDiscriminator", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM unionBadDiscriminator(final CompletionStatus completionStatus) {
        return this.unionBadDiscriminator(completionStatus, null);
    }
    
    public BAD_PARAM unionBadDiscriminator(final Throwable t) {
        return this.unionBadDiscriminator(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM unionBadDiscriminator() {
        return this.unionBadDiscriminator(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM ctxIllegalPropertyName(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446371, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.ctxIllegalPropertyName", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM ctxIllegalPropertyName(final CompletionStatus completionStatus) {
        return this.ctxIllegalPropertyName(completionStatus, null);
    }
    
    public BAD_PARAM ctxIllegalPropertyName(final Throwable t) {
        return this.ctxIllegalPropertyName(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM ctxIllegalPropertyName() {
        return this.ctxIllegalPropertyName(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM ctxIllegalSearchString(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446372, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.ctxIllegalSearchString", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM ctxIllegalSearchString(final CompletionStatus completionStatus) {
        return this.ctxIllegalSearchString(completionStatus, null);
    }
    
    public BAD_PARAM ctxIllegalSearchString(final Throwable t) {
        return this.ctxIllegalSearchString(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM ctxIllegalSearchString() {
        return this.ctxIllegalSearchString(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM ctxIllegalName(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446373, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.ctxIllegalName", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM ctxIllegalName(final CompletionStatus completionStatus) {
        return this.ctxIllegalName(completionStatus, null);
    }
    
    public BAD_PARAM ctxIllegalName(final Throwable t) {
        return this.ctxIllegalName(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM ctxIllegalName() {
        return this.ctxIllegalName(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM ctxNonEmpty(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446374, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.ctxNonEmpty", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM ctxNonEmpty(final CompletionStatus completionStatus) {
        return this.ctxNonEmpty(completionStatus, null);
    }
    
    public BAD_PARAM ctxNonEmpty(final Throwable t) {
        return this.ctxNonEmpty(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM ctxNonEmpty() {
        return this.ctxNonEmpty(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidStreamFormatVersion(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446375, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.invalidStreamFormatVersion", new Object[] { o }, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidStreamFormatVersion(final CompletionStatus completionStatus, final Object o) {
        return this.invalidStreamFormatVersion(completionStatus, null, o);
    }
    
    public BAD_PARAM invalidStreamFormatVersion(final Throwable t, final Object o) {
        return this.invalidStreamFormatVersion(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM invalidStreamFormatVersion(final Object o) {
        return this.invalidStreamFormatVersion(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM notAValueoutputstream(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446376, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.notAValueoutputstream", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM notAValueoutputstream(final CompletionStatus completionStatus) {
        return this.notAValueoutputstream(completionStatus, null);
    }
    
    public BAD_PARAM notAValueoutputstream(final Throwable t) {
        return this.notAValueoutputstream(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM notAValueoutputstream() {
        return this.notAValueoutputstream(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM notAValueinputstream(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1330446377, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.notAValueinputstream", null, OMGSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM notAValueinputstream(final CompletionStatus completionStatus) {
        return this.notAValueinputstream(completionStatus, null);
    }
    
    public BAD_PARAM notAValueinputstream(final Throwable t) {
        return this.notAValueinputstream(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM notAValueinputstream() {
        return this.notAValueinputstream(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_TYPECODE marshallIncompleteTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_TYPECODE bad_TYPECODE = new BAD_TYPECODE(1330446337, completionStatus);
        if (t != null) {
            bad_TYPECODE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.marshallIncompleteTypecode", null, OMGSystemException.class, bad_TYPECODE);
        }
        return bad_TYPECODE;
    }
    
    public BAD_TYPECODE marshallIncompleteTypecode(final CompletionStatus completionStatus) {
        return this.marshallIncompleteTypecode(completionStatus, null);
    }
    
    public BAD_TYPECODE marshallIncompleteTypecode(final Throwable t) {
        return this.marshallIncompleteTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_TYPECODE marshallIncompleteTypecode() {
        return this.marshallIncompleteTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_TYPECODE badMemberTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_TYPECODE bad_TYPECODE = new BAD_TYPECODE(1330446338, completionStatus);
        if (t != null) {
            bad_TYPECODE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badMemberTypecode", null, OMGSystemException.class, bad_TYPECODE);
        }
        return bad_TYPECODE;
    }
    
    public BAD_TYPECODE badMemberTypecode(final CompletionStatus completionStatus) {
        return this.badMemberTypecode(completionStatus, null);
    }
    
    public BAD_TYPECODE badMemberTypecode(final Throwable t) {
        return this.badMemberTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_TYPECODE badMemberTypecode() {
        return this.badMemberTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_TYPECODE illegalParameter(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_TYPECODE bad_TYPECODE = new BAD_TYPECODE(1330446339, completionStatus);
        if (t != null) {
            bad_TYPECODE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.illegalParameter", null, OMGSystemException.class, bad_TYPECODE);
        }
        return bad_TYPECODE;
    }
    
    public BAD_TYPECODE illegalParameter(final CompletionStatus completionStatus) {
        return this.illegalParameter(completionStatus, null);
    }
    
    public BAD_TYPECODE illegalParameter(final Throwable t) {
        return this.illegalParameter(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_TYPECODE illegalParameter() {
        return this.illegalParameter(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION charNotInCodeset(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1330446337, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.charNotInCodeset", null, OMGSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION charNotInCodeset(final CompletionStatus completionStatus) {
        return this.charNotInCodeset(completionStatus, null);
    }
    
    public DATA_CONVERSION charNotInCodeset(final Throwable t) {
        return this.charNotInCodeset(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION charNotInCodeset() {
        return this.charNotInCodeset(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION priorityMapFailre(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1330446338, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.priorityMapFailre", null, OMGSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION priorityMapFailre(final CompletionStatus completionStatus) {
        return this.priorityMapFailre(completionStatus, null);
    }
    
    public DATA_CONVERSION priorityMapFailre(final Throwable t) {
        return this.priorityMapFailre(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION priorityMapFailre() {
        return this.priorityMapFailre(CompletionStatus.COMPLETED_NO, null);
    }
    
    public IMP_LIMIT noUsableProfile(final CompletionStatus completionStatus, final Throwable t) {
        final IMP_LIMIT imp_LIMIT = new IMP_LIMIT(1330446337, completionStatus);
        if (t != null) {
            imp_LIMIT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noUsableProfile", null, OMGSystemException.class, imp_LIMIT);
        }
        return imp_LIMIT;
    }
    
    public IMP_LIMIT noUsableProfile(final CompletionStatus completionStatus) {
        return this.noUsableProfile(completionStatus, null);
    }
    
    public IMP_LIMIT noUsableProfile(final Throwable t) {
        return this.noUsableProfile(CompletionStatus.COMPLETED_NO, t);
    }
    
    public IMP_LIMIT noUsableProfile() {
        return this.noUsableProfile(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE priorityRangeRestrict(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1330446337, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.priorityRangeRestrict", null, OMGSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE priorityRangeRestrict(final CompletionStatus completionStatus) {
        return this.priorityRangeRestrict(completionStatus, null);
    }
    
    public INITIALIZE priorityRangeRestrict(final Throwable t) {
        return this.priorityRangeRestrict(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE priorityRangeRestrict() {
        return this.priorityRangeRestrict(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INV_OBJREF noServerWcharCodesetCmp(final CompletionStatus completionStatus, final Throwable t) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1330446337, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noServerWcharCodesetCmp", null, OMGSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF noServerWcharCodesetCmp(final CompletionStatus completionStatus) {
        return this.noServerWcharCodesetCmp(completionStatus, null);
    }
    
    public INV_OBJREF noServerWcharCodesetCmp(final Throwable t) {
        return this.noServerWcharCodesetCmp(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_OBJREF noServerWcharCodesetCmp() {
        return this.noServerWcharCodesetCmp(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INV_OBJREF codesetComponentRequired(final CompletionStatus completionStatus, final Throwable t) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1330446338, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.codesetComponentRequired", null, OMGSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF codesetComponentRequired(final CompletionStatus completionStatus) {
        return this.codesetComponentRequired(completionStatus, null);
    }
    
    public INV_OBJREF codesetComponentRequired(final Throwable t) {
        return this.codesetComponentRequired(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_OBJREF codesetComponentRequired() {
        return this.codesetComponentRequired(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INV_POLICY iorPolicyReconcileError(final CompletionStatus completionStatus, final Throwable t) {
        final INV_POLICY inv_POLICY = new INV_POLICY(1330446337, completionStatus);
        if (t != null) {
            inv_POLICY.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.iorPolicyReconcileError", null, OMGSystemException.class, inv_POLICY);
        }
        return inv_POLICY;
    }
    
    public INV_POLICY iorPolicyReconcileError(final CompletionStatus completionStatus) {
        return this.iorPolicyReconcileError(completionStatus, null);
    }
    
    public INV_POLICY iorPolicyReconcileError(final Throwable t) {
        return this.iorPolicyReconcileError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_POLICY iorPolicyReconcileError() {
        return this.iorPolicyReconcileError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INV_POLICY policyUnknown(final CompletionStatus completionStatus, final Throwable t) {
        final INV_POLICY inv_POLICY = new INV_POLICY(1330446338, completionStatus);
        if (t != null) {
            inv_POLICY.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.policyUnknown", null, OMGSystemException.class, inv_POLICY);
        }
        return inv_POLICY;
    }
    
    public INV_POLICY policyUnknown(final CompletionStatus completionStatus) {
        return this.policyUnknown(completionStatus, null);
    }
    
    public INV_POLICY policyUnknown(final Throwable t) {
        return this.policyUnknown(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_POLICY policyUnknown() {
        return this.policyUnknown(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INV_POLICY noPolicyFactory(final CompletionStatus completionStatus, final Throwable t) {
        final INV_POLICY inv_POLICY = new INV_POLICY(1330446339, completionStatus);
        if (t != null) {
            inv_POLICY.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noPolicyFactory", null, OMGSystemException.class, inv_POLICY);
        }
        return inv_POLICY;
    }
    
    public INV_POLICY noPolicyFactory(final CompletionStatus completionStatus) {
        return this.noPolicyFactory(completionStatus, null);
    }
    
    public INV_POLICY noPolicyFactory(final Throwable t) {
        return this.noPolicyFactory(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_POLICY noPolicyFactory() {
        return this.noPolicyFactory(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL xaRmerr(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1330446337, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaRmerr", null, OMGSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL xaRmerr(final CompletionStatus completionStatus) {
        return this.xaRmerr(completionStatus, null);
    }
    
    public INTERNAL xaRmerr(final Throwable t) {
        return this.xaRmerr(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL xaRmerr() {
        return this.xaRmerr(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL xaRmfail(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1330446338, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaRmfail", null, OMGSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL xaRmfail(final CompletionStatus completionStatus) {
        return this.xaRmfail(completionStatus, null);
    }
    
    public INTERNAL xaRmfail(final Throwable t) {
        return this.xaRmfail(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL xaRmfail() {
        return this.xaRmfail(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTF_REPOS noIr(final CompletionStatus completionStatus, final Throwable t) {
        final INTF_REPOS intf_REPOS = new INTF_REPOS(1330446337, completionStatus);
        if (t != null) {
            intf_REPOS.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noIr", null, OMGSystemException.class, intf_REPOS);
        }
        return intf_REPOS;
    }
    
    public INTF_REPOS noIr(final CompletionStatus completionStatus) {
        return this.noIr(completionStatus, null);
    }
    
    public INTF_REPOS noIr(final Throwable t) {
        return this.noIr(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTF_REPOS noIr() {
        return this.noIr(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTF_REPOS noInterfaceInIr(final CompletionStatus completionStatus, final Throwable t) {
        final INTF_REPOS intf_REPOS = new INTF_REPOS(1330446338, completionStatus);
        if (t != null) {
            intf_REPOS.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noInterfaceInIr", null, OMGSystemException.class, intf_REPOS);
        }
        return intf_REPOS;
    }
    
    public INTF_REPOS noInterfaceInIr(final CompletionStatus completionStatus) {
        return this.noInterfaceInIr(completionStatus, null);
    }
    
    public INTF_REPOS noInterfaceInIr(final Throwable t) {
        return this.noInterfaceInIr(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTF_REPOS noInterfaceInIr() {
        return this.noInterfaceInIr(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL unableLocateValueFactory(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446337, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.unableLocateValueFactory", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unableLocateValueFactory(final CompletionStatus completionStatus) {
        return this.unableLocateValueFactory(completionStatus, null);
    }
    
    public MARSHAL unableLocateValueFactory(final Throwable t) {
        return this.unableLocateValueFactory(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL unableLocateValueFactory() {
        return this.unableLocateValueFactory(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL setResultBeforeCtx(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446338, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.setResultBeforeCtx", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL setResultBeforeCtx(final CompletionStatus completionStatus) {
        return this.setResultBeforeCtx(completionStatus, null);
    }
    
    public MARSHAL setResultBeforeCtx(final Throwable t) {
        return this.setResultBeforeCtx(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL setResultBeforeCtx() {
        return this.setResultBeforeCtx(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL badNvlist(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446339, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badNvlist", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badNvlist(final CompletionStatus completionStatus) {
        return this.badNvlist(completionStatus, null);
    }
    
    public MARSHAL badNvlist(final Throwable t) {
        return this.badNvlist(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL badNvlist() {
        return this.badNvlist(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL notAnObjectImpl(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446340, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.notAnObjectImpl", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL notAnObjectImpl(final CompletionStatus completionStatus) {
        return this.notAnObjectImpl(completionStatus, null);
    }
    
    public MARSHAL notAnObjectImpl(final Throwable t) {
        return this.notAnObjectImpl(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL notAnObjectImpl() {
        return this.notAnObjectImpl(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL wcharBadGiopVersionSent(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446341, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.wcharBadGiopVersionSent", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL wcharBadGiopVersionSent(final CompletionStatus completionStatus) {
        return this.wcharBadGiopVersionSent(completionStatus, null);
    }
    
    public MARSHAL wcharBadGiopVersionSent(final Throwable t) {
        return this.wcharBadGiopVersionSent(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL wcharBadGiopVersionSent() {
        return this.wcharBadGiopVersionSent(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL wcharBadGiopVersionReturned(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446342, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.wcharBadGiopVersionReturned", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL wcharBadGiopVersionReturned(final CompletionStatus completionStatus) {
        return this.wcharBadGiopVersionReturned(completionStatus, null);
    }
    
    public MARSHAL wcharBadGiopVersionReturned(final Throwable t) {
        return this.wcharBadGiopVersionReturned(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL wcharBadGiopVersionReturned() {
        return this.wcharBadGiopVersionReturned(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL unsupportedFormatVersion(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446343, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.unsupportedFormatVersion", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unsupportedFormatVersion(final CompletionStatus completionStatus) {
        return this.unsupportedFormatVersion(completionStatus, null);
    }
    
    public MARSHAL unsupportedFormatVersion(final Throwable t) {
        return this.unsupportedFormatVersion(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL unsupportedFormatVersion() {
        return this.unsupportedFormatVersion(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible1(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446344, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible1", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible1(final CompletionStatus completionStatus) {
        return this.rmiiiopOptionalDataIncompatible1(completionStatus, null);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible1(final Throwable t) {
        return this.rmiiiopOptionalDataIncompatible1(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible1() {
        return this.rmiiiopOptionalDataIncompatible1(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible2(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446344, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible2", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible2(final CompletionStatus completionStatus) {
        return this.rmiiiopOptionalDataIncompatible2(completionStatus, null);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible2(final Throwable t) {
        return this.rmiiiopOptionalDataIncompatible2(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible2() {
        return this.rmiiiopOptionalDataIncompatible2(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible3(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1330446344, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.rmiiiopOptionalDataIncompatible3", null, OMGSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible3(final CompletionStatus completionStatus) {
        return this.rmiiiopOptionalDataIncompatible3(completionStatus, null);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible3(final Throwable t) {
        return this.rmiiiopOptionalDataIncompatible3(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL rmiiiopOptionalDataIncompatible3() {
        return this.rmiiiopOptionalDataIncompatible3(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT missingLocalValueImpl(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1330446337, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.missingLocalValueImpl", null, OMGSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT missingLocalValueImpl(final CompletionStatus completionStatus) {
        return this.missingLocalValueImpl(completionStatus, null);
    }
    
    public NO_IMPLEMENT missingLocalValueImpl(final Throwable t) {
        return this.missingLocalValueImpl(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT missingLocalValueImpl() {
        return this.missingLocalValueImpl(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT incompatibleValueImpl(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1330446338, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.incompatibleValueImpl", null, OMGSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT incompatibleValueImpl(final CompletionStatus completionStatus) {
        return this.incompatibleValueImpl(completionStatus, null);
    }
    
    public NO_IMPLEMENT incompatibleValueImpl(final Throwable t) {
        return this.incompatibleValueImpl(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT incompatibleValueImpl() {
        return this.incompatibleValueImpl(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT noUsableProfile2(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1330446339, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noUsableProfile2", null, OMGSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT noUsableProfile2(final CompletionStatus completionStatus) {
        return this.noUsableProfile2(completionStatus, null);
    }
    
    public NO_IMPLEMENT noUsableProfile2(final Throwable t) {
        return this.noUsableProfile2(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT noUsableProfile2() {
        return this.noUsableProfile2(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT diiLocalObject(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1330446340, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.diiLocalObject", null, OMGSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT diiLocalObject(final CompletionStatus completionStatus) {
        return this.diiLocalObject(completionStatus, null);
    }
    
    public NO_IMPLEMENT diiLocalObject(final Throwable t) {
        return this.diiLocalObject(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT diiLocalObject() {
        return this.diiLocalObject(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT bioReset(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1330446341, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.bioReset", null, OMGSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT bioReset(final CompletionStatus completionStatus) {
        return this.bioReset(completionStatus, null);
    }
    
    public NO_IMPLEMENT bioReset(final Throwable t) {
        return this.bioReset(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT bioReset() {
        return this.bioReset(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT bioMetaNotAvailable(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1330446342, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.bioMetaNotAvailable", null, OMGSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT bioMetaNotAvailable(final CompletionStatus completionStatus) {
        return this.bioMetaNotAvailable(completionStatus, null);
    }
    
    public NO_IMPLEMENT bioMetaNotAvailable(final Throwable t) {
        return this.bioMetaNotAvailable(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT bioMetaNotAvailable() {
        return this.bioMetaNotAvailable(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT bioGenomicNoIterator(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1330446343, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.bioGenomicNoIterator", null, OMGSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT bioGenomicNoIterator(final CompletionStatus completionStatus) {
        return this.bioGenomicNoIterator(completionStatus, null);
    }
    
    public NO_IMPLEMENT bioGenomicNoIterator(final Throwable t) {
        return this.bioGenomicNoIterator(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT bioGenomicNoIterator() {
        return this.bioGenomicNoIterator(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported1(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported1", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported1(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported1(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported1(final Throwable t) {
        return this.piOperationNotSupported1(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported1() {
        return this.piOperationNotSupported1(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported2(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported2", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported2(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported2(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported2(final Throwable t) {
        return this.piOperationNotSupported2(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported2() {
        return this.piOperationNotSupported2(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported3(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported3", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported3(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported3(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported3(final Throwable t) {
        return this.piOperationNotSupported3(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported3() {
        return this.piOperationNotSupported3(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported4(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported4", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported4(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported4(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported4(final Throwable t) {
        return this.piOperationNotSupported4(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported4() {
        return this.piOperationNotSupported4(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported5(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported5", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported5(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported5(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported5(final Throwable t) {
        return this.piOperationNotSupported5(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported5() {
        return this.piOperationNotSupported5(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported6(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported6", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported6(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported6(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported6(final Throwable t) {
        return this.piOperationNotSupported6(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported6() {
        return this.piOperationNotSupported6(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported7(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported7", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported7(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported7(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported7(final Throwable t) {
        return this.piOperationNotSupported7(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported7() {
        return this.piOperationNotSupported7(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES piOperationNotSupported8(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446337, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.piOperationNotSupported8", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES piOperationNotSupported8(final CompletionStatus completionStatus) {
        return this.piOperationNotSupported8(completionStatus, null);
    }
    
    public NO_RESOURCES piOperationNotSupported8(final Throwable t) {
        return this.piOperationNotSupported8(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES piOperationNotSupported8() {
        return this.piOperationNotSupported8(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_RESOURCES noConnectionPriority(final CompletionStatus completionStatus, final Throwable t) {
        final NO_RESOURCES no_RESOURCES = new NO_RESOURCES(1330446338, completionStatus);
        if (t != null) {
            no_RESOURCES.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noConnectionPriority", null, OMGSystemException.class, no_RESOURCES);
        }
        return no_RESOURCES;
    }
    
    public NO_RESOURCES noConnectionPriority(final CompletionStatus completionStatus) {
        return this.noConnectionPriority(completionStatus, null);
    }
    
    public NO_RESOURCES noConnectionPriority(final Throwable t) {
        return this.noConnectionPriority(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_RESOURCES noConnectionPriority() {
        return this.noConnectionPriority(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSACTION_ROLLEDBACK xaRb(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSACTION_ROLLEDBACK transaction_ROLLEDBACK = new TRANSACTION_ROLLEDBACK(1330446337, completionStatus);
        if (t != null) {
            transaction_ROLLEDBACK.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaRb", null, OMGSystemException.class, transaction_ROLLEDBACK);
        }
        return transaction_ROLLEDBACK;
    }
    
    public TRANSACTION_ROLLEDBACK xaRb(final CompletionStatus completionStatus) {
        return this.xaRb(completionStatus, null);
    }
    
    public TRANSACTION_ROLLEDBACK xaRb(final Throwable t) {
        return this.xaRb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSACTION_ROLLEDBACK xaRb() {
        return this.xaRb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSACTION_ROLLEDBACK xaNota(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSACTION_ROLLEDBACK transaction_ROLLEDBACK = new TRANSACTION_ROLLEDBACK(1330446338, completionStatus);
        if (t != null) {
            transaction_ROLLEDBACK.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaNota", null, OMGSystemException.class, transaction_ROLLEDBACK);
        }
        return transaction_ROLLEDBACK;
    }
    
    public TRANSACTION_ROLLEDBACK xaNota(final CompletionStatus completionStatus) {
        return this.xaNota(completionStatus, null);
    }
    
    public TRANSACTION_ROLLEDBACK xaNota(final Throwable t) {
        return this.xaNota(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSACTION_ROLLEDBACK xaNota() {
        return this.xaNota(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSACTION_ROLLEDBACK transaction_ROLLEDBACK = new TRANSACTION_ROLLEDBACK(1330446339, completionStatus);
        if (t != null) {
            transaction_ROLLEDBACK.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.xaEndTrueRollbackDeferred", null, OMGSystemException.class, transaction_ROLLEDBACK);
        }
        return transaction_ROLLEDBACK;
    }
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(final CompletionStatus completionStatus) {
        return this.xaEndTrueRollbackDeferred(completionStatus, null);
    }
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(final Throwable t) {
        return this.xaEndTrueRollbackDeferred(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred() {
        return this.xaEndTrueRollbackDeferred(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSIENT poaRequestDiscard(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSIENT transient1 = new TRANSIENT(1330446337, completionStatus);
        if (t != null) {
            transient1.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.poaRequestDiscard", null, OMGSystemException.class, transient1);
        }
        return transient1;
    }
    
    public TRANSIENT poaRequestDiscard(final CompletionStatus completionStatus) {
        return this.poaRequestDiscard(completionStatus, null);
    }
    
    public TRANSIENT poaRequestDiscard(final Throwable t) {
        return this.poaRequestDiscard(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSIENT poaRequestDiscard() {
        return this.poaRequestDiscard(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSIENT noUsableProfile3(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSIENT transient1 = new TRANSIENT(1330446338, completionStatus);
        if (t != null) {
            transient1.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noUsableProfile3", null, OMGSystemException.class, transient1);
        }
        return transient1;
    }
    
    public TRANSIENT noUsableProfile3(final CompletionStatus completionStatus) {
        return this.noUsableProfile3(completionStatus, null);
    }
    
    public TRANSIENT noUsableProfile3(final Throwable t) {
        return this.noUsableProfile3(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSIENT noUsableProfile3() {
        return this.noUsableProfile3(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSIENT requestCancelled(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSIENT transient1 = new TRANSIENT(1330446339, completionStatus);
        if (t != null) {
            transient1.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.requestCancelled", null, OMGSystemException.class, transient1);
        }
        return transient1;
    }
    
    public TRANSIENT requestCancelled(final CompletionStatus completionStatus) {
        return this.requestCancelled(completionStatus, null);
    }
    
    public TRANSIENT requestCancelled(final Throwable t) {
        return this.requestCancelled(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSIENT requestCancelled() {
        return this.requestCancelled(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSIENT poaDestroyed(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSIENT transient1 = new TRANSIENT(1330446340, completionStatus);
        if (t != null) {
            transient1.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.poaDestroyed", null, OMGSystemException.class, transient1);
        }
        return transient1;
    }
    
    public TRANSIENT poaDestroyed(final CompletionStatus completionStatus) {
        return this.poaDestroyed(completionStatus, null);
    }
    
    public TRANSIENT poaDestroyed(final Throwable t) {
        return this.poaDestroyed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSIENT poaDestroyed() {
        return this.poaDestroyed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1330446337, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.unregisteredValueAsObjref", null, OMGSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref(final CompletionStatus completionStatus) {
        return this.unregisteredValueAsObjref(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref(final Throwable t) {
        return this.unregisteredValueAsObjref(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST unregisteredValueAsObjref() {
        return this.unregisteredValueAsObjref(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST noObjectAdaptor(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1330446338, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.noObjectAdaptor", null, OMGSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST noObjectAdaptor(final CompletionStatus completionStatus) {
        return this.noObjectAdaptor(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST noObjectAdaptor(final Throwable t) {
        return this.noObjectAdaptor(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST noObjectAdaptor() {
        return this.noObjectAdaptor(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST bioNotAvailable(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1330446339, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.bioNotAvailable", null, OMGSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST bioNotAvailable(final CompletionStatus completionStatus) {
        return this.bioNotAvailable(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST bioNotAvailable(final Throwable t) {
        return this.bioNotAvailable(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST bioNotAvailable() {
        return this.bioNotAvailable(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST objectAdapterInactive(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1330446340, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.objectAdapterInactive", null, OMGSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST objectAdapterInactive(final CompletionStatus completionStatus) {
        return this.objectAdapterInactive(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST objectAdapterInactive(final Throwable t) {
        return this.objectAdapterInactive(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST objectAdapterInactive() {
        return this.objectAdapterInactive(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER adapterActivatorException(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1330446337, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.adapterActivatorException", new Object[] { o, o2 }, OMGSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER adapterActivatorException(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.adapterActivatorException(completionStatus, null, o, o2);
    }
    
    public OBJ_ADAPTER adapterActivatorException(final Throwable t, final Object o, final Object o2) {
        return this.adapterActivatorException(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public OBJ_ADAPTER adapterActivatorException(final Object o, final Object o2) {
        return this.adapterActivatorException(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public OBJ_ADAPTER badServantType(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1330446338, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badServantType", null, OMGSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER badServantType(final CompletionStatus completionStatus) {
        return this.badServantType(completionStatus, null);
    }
    
    public OBJ_ADAPTER badServantType(final Throwable t) {
        return this.badServantType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER badServantType() {
        return this.badServantType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER noDefaultServant(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1330446339, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noDefaultServant", null, OMGSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER noDefaultServant(final CompletionStatus completionStatus) {
        return this.noDefaultServant(completionStatus, null);
    }
    
    public OBJ_ADAPTER noDefaultServant(final Throwable t) {
        return this.noDefaultServant(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER noDefaultServant() {
        return this.noDefaultServant(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER noServantManager(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1330446340, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.noServantManager", null, OMGSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER noServantManager(final CompletionStatus completionStatus) {
        return this.noServantManager(completionStatus, null);
    }
    
    public OBJ_ADAPTER noServantManager(final Throwable t) {
        return this.noServantManager(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER noServantManager() {
        return this.noServantManager(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER badPolicyIncarnate(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1330446341, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.badPolicyIncarnate", null, OMGSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER badPolicyIncarnate(final CompletionStatus completionStatus) {
        return this.badPolicyIncarnate(completionStatus, null);
    }
    
    public OBJ_ADAPTER badPolicyIncarnate(final Throwable t) {
        return this.badPolicyIncarnate(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER badPolicyIncarnate() {
        return this.badPolicyIncarnate(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER piExcCompEstablished(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1330446342, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.piExcCompEstablished", null, OMGSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER piExcCompEstablished(final CompletionStatus completionStatus) {
        return this.piExcCompEstablished(completionStatus, null);
    }
    
    public OBJ_ADAPTER piExcCompEstablished(final Throwable t) {
        return this.piExcCompEstablished(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER piExcCompEstablished() {
        return this.piExcCompEstablished(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER nullServantReturned(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1330446343, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.nullServantReturned", null, OMGSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER nullServantReturned(final CompletionStatus completionStatus) {
        return this.nullServantReturned(completionStatus, null);
    }
    
    public OBJ_ADAPTER nullServantReturned(final Throwable t) {
        return this.nullServantReturned(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER nullServantReturned() {
        return this.nullServantReturned(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownUserException(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1330446337, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "OMG.unknownUserException", null, OMGSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownUserException(final CompletionStatus completionStatus) {
        return this.unknownUserException(completionStatus, null);
    }
    
    public UNKNOWN unknownUserException(final Throwable t) {
        return this.unknownUserException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownUserException() {
        return this.unknownUserException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unsupportedSystemException(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1330446338, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.unsupportedSystemException", null, OMGSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unsupportedSystemException(final CompletionStatus completionStatus) {
        return this.unsupportedSystemException(completionStatus, null);
    }
    
    public UNKNOWN unsupportedSystemException(final Throwable t) {
        return this.unsupportedSystemException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unsupportedSystemException() {
        return this.unsupportedSystemException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN piUnknownUserException(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1330446339, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "OMG.piUnknownUserException", null, OMGSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN piUnknownUserException(final CompletionStatus completionStatus) {
        return this.piUnknownUserException(completionStatus, null);
    }
    
    public UNKNOWN piUnknownUserException(final Throwable t) {
        return this.piUnknownUserException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN piUnknownUserException() {
        return this.piUnknownUserException(CompletionStatus.COMPLETED_NO, null);
    }
    
    static {
        OMGSystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new OMGSystemException(logger);
            }
        };
    }
}
