package com.sun.corba.se.impl.logging;

import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_OPERATION;
import java.util.logging.Level;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class POASystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int SERVANT_MANAGER_ALREADY_SET = 1398080489;
    public static final int DESTROY_DEADLOCK = 1398080490;
    public static final int SERVANT_ORB = 1398080489;
    public static final int BAD_SERVANT = 1398080490;
    public static final int ILLEGAL_FORWARD_REQUEST = 1398080491;
    public static final int BAD_TRANSACTION_CONTEXT = 1398080489;
    public static final int BAD_REPOSITORY_ID = 1398080490;
    public static final int INVOKESETUP = 1398080489;
    public static final int BAD_LOCALREPLYSTATUS = 1398080490;
    public static final int PERSISTENT_SERVERPORT_ERROR = 1398080491;
    public static final int SERVANT_DISPATCH = 1398080492;
    public static final int WRONG_CLIENTSC = 1398080493;
    public static final int CANT_CLONE_TEMPLATE = 1398080494;
    public static final int POACURRENT_UNBALANCED_STACK = 1398080495;
    public static final int POACURRENT_NULL_FIELD = 1398080496;
    public static final int POA_INTERNAL_GET_SERVANT_ERROR = 1398080497;
    public static final int MAKE_FACTORY_NOT_POA = 1398080498;
    public static final int DUPLICATE_ORB_VERSION_SC = 1398080499;
    public static final int PREINVOKE_CLONE_ERROR = 1398080500;
    public static final int PREINVOKE_POA_DESTROYED = 1398080501;
    public static final int PMF_CREATE_RETAIN = 1398080502;
    public static final int PMF_CREATE_NON_RETAIN = 1398080503;
    public static final int POLICY_MEDIATOR_BAD_POLICY_IN_FACTORY = 1398080504;
    public static final int SERVANT_TO_ID_OAA = 1398080505;
    public static final int SERVANT_TO_ID_SAA = 1398080506;
    public static final int SERVANT_TO_ID_WP = 1398080507;
    public static final int CANT_RESOLVE_ROOT_POA = 1398080508;
    public static final int SERVANT_MUST_BE_LOCAL = 1398080509;
    public static final int NO_PROFILES_IN_IOR = 1398080510;
    public static final int AOM_ENTRY_DEC_ZERO = 1398080511;
    public static final int ADD_POA_INACTIVE = 1398080512;
    public static final int ILLEGAL_POA_STATE_TRANS = 1398080513;
    public static final int UNEXPECTED_EXCEPTION = 1398080514;
    public static final int SINGLE_THREAD_NOT_SUPPORTED = 1398080489;
    public static final int METHOD_NOT_IMPLEMENTED = 1398080490;
    public static final int POA_LOOKUP_ERROR = 1398080489;
    public static final int POA_INACTIVE = 1398080490;
    public static final int POA_NO_SERVANT_MANAGER = 1398080491;
    public static final int POA_NO_DEFAULT_SERVANT = 1398080492;
    public static final int POA_SERVANT_NOT_UNIQUE = 1398080493;
    public static final int POA_WRONG_POLICY = 1398080494;
    public static final int FINDPOA_ERROR = 1398080495;
    public static final int POA_SERVANT_ACTIVATOR_LOOKUP_FAILED = 1398080497;
    public static final int POA_BAD_SERVANT_MANAGER = 1398080498;
    public static final int POA_SERVANT_LOCATOR_LOOKUP_FAILED = 1398080499;
    public static final int POA_UNKNOWN_POLICY = 1398080500;
    public static final int POA_NOT_FOUND = 1398080501;
    public static final int SERVANT_LOOKUP = 1398080502;
    public static final int LOCAL_SERVANT_LOOKUP = 1398080503;
    public static final int SERVANT_MANAGER_BAD_TYPE = 1398080504;
    public static final int DEFAULT_POA_NOT_POAIMPL = 1398080505;
    public static final int WRONG_POLICIES_FOR_THIS_OBJECT = 1398080506;
    public static final int THIS_OBJECT_SERVANT_NOT_ACTIVE = 1398080507;
    public static final int THIS_OBJECT_WRONG_POLICY = 1398080508;
    public static final int NO_CONTEXT = 1398080509;
    public static final int INCARNATE_RETURNED_NULL = 1398080510;
    public static final int JTS_INIT_ERROR = 1398080489;
    public static final int PERSISTENT_SERVERID_NOT_SET = 1398080490;
    public static final int PERSISTENT_SERVERPORT_NOT_SET = 1398080491;
    public static final int ORBD_ERROR = 1398080492;
    public static final int BOOTSTRAP_ERROR = 1398080493;
    public static final int POA_DISCARDING = 1398080489;
    public static final int OTSHOOKEXCEPTION = 1398080489;
    public static final int UNKNOWN_SERVER_EXCEPTION = 1398080490;
    public static final int UNKNOWN_SERVERAPP_EXCEPTION = 1398080491;
    public static final int UNKNOWN_LOCALINVOCATION_ERROR = 1398080492;
    public static final int ADAPTER_ACTIVATOR_NONEXISTENT = 1398080489;
    public static final int ADAPTER_ACTIVATOR_FAILED = 1398080490;
    public static final int BAD_SKELETON = 1398080491;
    public static final int NULL_SERVANT = 1398080492;
    public static final int ADAPTER_DESTROYED = 1398080493;
    
    public POASystemException(final Logger logger) {
        super(logger);
    }
    
    public static POASystemException get(final ORB orb, final String s) {
        return (POASystemException)orb.getLogWrapper(s, "POA", POASystemException.factory);
    }
    
    public static POASystemException get(final String s) {
        return (POASystemException)ORB.staticGetLogWrapper(s, "POA", POASystemException.factory);
    }
    
    public BAD_INV_ORDER servantManagerAlreadySet(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398080489, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantManagerAlreadySet", null, POASystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER servantManagerAlreadySet(final CompletionStatus completionStatus) {
        return this.servantManagerAlreadySet(completionStatus, null);
    }
    
    public BAD_INV_ORDER servantManagerAlreadySet(final Throwable t) {
        return this.servantManagerAlreadySet(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER servantManagerAlreadySet() {
        return this.servantManagerAlreadySet(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER destroyDeadlock(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398080490, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.destroyDeadlock", null, POASystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER destroyDeadlock(final CompletionStatus completionStatus) {
        return this.destroyDeadlock(completionStatus, null);
    }
    
    public BAD_INV_ORDER destroyDeadlock(final Throwable t) {
        return this.destroyDeadlock(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER destroyDeadlock() {
        return this.destroyDeadlock(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION servantOrb(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080489, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantOrb", null, POASystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION servantOrb(final CompletionStatus completionStatus) {
        return this.servantOrb(completionStatus, null);
    }
    
    public BAD_OPERATION servantOrb(final Throwable t) {
        return this.servantOrb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION servantOrb() {
        return this.servantOrb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION badServant(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080490, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.badServant", null, POASystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION badServant(final CompletionStatus completionStatus) {
        return this.badServant(completionStatus, null);
    }
    
    public BAD_OPERATION badServant(final Throwable t) {
        return this.badServant(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION badServant() {
        return this.badServant(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION illegalForwardRequest(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080491, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.illegalForwardRequest", null, POASystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION illegalForwardRequest(final CompletionStatus completionStatus) {
        return this.illegalForwardRequest(completionStatus, null);
    }
    
    public BAD_OPERATION illegalForwardRequest(final Throwable t) {
        return this.illegalForwardRequest(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION illegalForwardRequest() {
        return this.illegalForwardRequest(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM badTransactionContext(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080489, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.badTransactionContext", null, POASystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM badTransactionContext(final CompletionStatus completionStatus) {
        return this.badTransactionContext(completionStatus, null);
    }
    
    public BAD_PARAM badTransactionContext(final Throwable t) {
        return this.badTransactionContext(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM badTransactionContext() {
        return this.badTransactionContext(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM badRepositoryId(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080490, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.badRepositoryId", null, POASystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM badRepositoryId(final CompletionStatus completionStatus) {
        return this.badRepositoryId(completionStatus, null);
    }
    
    public BAD_PARAM badRepositoryId(final Throwable t) {
        return this.badRepositoryId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM badRepositoryId() {
        return this.badRepositoryId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL invokesetup(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080489, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.invokesetup", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invokesetup(final CompletionStatus completionStatus) {
        return this.invokesetup(completionStatus, null);
    }
    
    public INTERNAL invokesetup(final Throwable t) {
        return this.invokesetup(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL invokesetup() {
        return this.invokesetup(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badLocalreplystatus(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080490, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.badLocalreplystatus", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badLocalreplystatus(final CompletionStatus completionStatus) {
        return this.badLocalreplystatus(completionStatus, null);
    }
    
    public INTERNAL badLocalreplystatus(final Throwable t) {
        return this.badLocalreplystatus(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badLocalreplystatus() {
        return this.badLocalreplystatus(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL persistentServerportError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080491, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.persistentServerportError", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL persistentServerportError(final CompletionStatus completionStatus) {
        return this.persistentServerportError(completionStatus, null);
    }
    
    public INTERNAL persistentServerportError(final Throwable t) {
        return this.persistentServerportError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL persistentServerportError() {
        return this.persistentServerportError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL servantDispatch(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080492, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantDispatch", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL servantDispatch(final CompletionStatus completionStatus) {
        return this.servantDispatch(completionStatus, null);
    }
    
    public INTERNAL servantDispatch(final Throwable t) {
        return this.servantDispatch(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL servantDispatch() {
        return this.servantDispatch(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL wrongClientsc(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080493, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.wrongClientsc", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL wrongClientsc(final CompletionStatus completionStatus) {
        return this.wrongClientsc(completionStatus, null);
    }
    
    public INTERNAL wrongClientsc(final Throwable t) {
        return this.wrongClientsc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL wrongClientsc() {
        return this.wrongClientsc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL cantCloneTemplate(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080494, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.cantCloneTemplate", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL cantCloneTemplate(final CompletionStatus completionStatus) {
        return this.cantCloneTemplate(completionStatus, null);
    }
    
    public INTERNAL cantCloneTemplate(final Throwable t) {
        return this.cantCloneTemplate(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL cantCloneTemplate() {
        return this.cantCloneTemplate(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL poacurrentUnbalancedStack(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080495, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poacurrentUnbalancedStack", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL poacurrentUnbalancedStack(final CompletionStatus completionStatus) {
        return this.poacurrentUnbalancedStack(completionStatus, null);
    }
    
    public INTERNAL poacurrentUnbalancedStack(final Throwable t) {
        return this.poacurrentUnbalancedStack(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL poacurrentUnbalancedStack() {
        return this.poacurrentUnbalancedStack(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL poacurrentNullField(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080496, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poacurrentNullField", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL poacurrentNullField(final CompletionStatus completionStatus) {
        return this.poacurrentNullField(completionStatus, null);
    }
    
    public INTERNAL poacurrentNullField(final Throwable t) {
        return this.poacurrentNullField(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL poacurrentNullField() {
        return this.poacurrentNullField(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL poaInternalGetServantError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080497, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaInternalGetServantError", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL poaInternalGetServantError(final CompletionStatus completionStatus) {
        return this.poaInternalGetServantError(completionStatus, null);
    }
    
    public INTERNAL poaInternalGetServantError(final Throwable t) {
        return this.poaInternalGetServantError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL poaInternalGetServantError() {
        return this.poaInternalGetServantError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL makeFactoryNotPoa(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080498, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.makeFactoryNotPoa", new Object[] { o }, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL makeFactoryNotPoa(final CompletionStatus completionStatus, final Object o) {
        return this.makeFactoryNotPoa(completionStatus, null, o);
    }
    
    public INTERNAL makeFactoryNotPoa(final Throwable t, final Object o) {
        return this.makeFactoryNotPoa(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL makeFactoryNotPoa(final Object o) {
        return this.makeFactoryNotPoa(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL duplicateOrbVersionSc(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080499, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.duplicateOrbVersionSc", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL duplicateOrbVersionSc(final CompletionStatus completionStatus) {
        return this.duplicateOrbVersionSc(completionStatus, null);
    }
    
    public INTERNAL duplicateOrbVersionSc(final Throwable t) {
        return this.duplicateOrbVersionSc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL duplicateOrbVersionSc() {
        return this.duplicateOrbVersionSc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL preinvokeCloneError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080500, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.preinvokeCloneError", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL preinvokeCloneError(final CompletionStatus completionStatus) {
        return this.preinvokeCloneError(completionStatus, null);
    }
    
    public INTERNAL preinvokeCloneError(final Throwable t) {
        return this.preinvokeCloneError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL preinvokeCloneError() {
        return this.preinvokeCloneError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL preinvokePoaDestroyed(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080501, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.preinvokePoaDestroyed", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL preinvokePoaDestroyed(final CompletionStatus completionStatus) {
        return this.preinvokePoaDestroyed(completionStatus, null);
    }
    
    public INTERNAL preinvokePoaDestroyed(final Throwable t) {
        return this.preinvokePoaDestroyed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL preinvokePoaDestroyed() {
        return this.preinvokePoaDestroyed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL pmfCreateRetain(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080502, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.pmfCreateRetain", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL pmfCreateRetain(final CompletionStatus completionStatus) {
        return this.pmfCreateRetain(completionStatus, null);
    }
    
    public INTERNAL pmfCreateRetain(final Throwable t) {
        return this.pmfCreateRetain(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL pmfCreateRetain() {
        return this.pmfCreateRetain(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL pmfCreateNonRetain(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080503, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.pmfCreateNonRetain", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL pmfCreateNonRetain(final CompletionStatus completionStatus) {
        return this.pmfCreateNonRetain(completionStatus, null);
    }
    
    public INTERNAL pmfCreateNonRetain(final Throwable t) {
        return this.pmfCreateNonRetain(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL pmfCreateNonRetain() {
        return this.pmfCreateNonRetain(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL policyMediatorBadPolicyInFactory(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080504, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.policyMediatorBadPolicyInFactory", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL policyMediatorBadPolicyInFactory(final CompletionStatus completionStatus) {
        return this.policyMediatorBadPolicyInFactory(completionStatus, null);
    }
    
    public INTERNAL policyMediatorBadPolicyInFactory(final Throwable t) {
        return this.policyMediatorBadPolicyInFactory(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL policyMediatorBadPolicyInFactory() {
        return this.policyMediatorBadPolicyInFactory(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL servantToIdOaa(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080505, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantToIdOaa", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL servantToIdOaa(final CompletionStatus completionStatus) {
        return this.servantToIdOaa(completionStatus, null);
    }
    
    public INTERNAL servantToIdOaa(final Throwable t) {
        return this.servantToIdOaa(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL servantToIdOaa() {
        return this.servantToIdOaa(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL servantToIdSaa(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080506, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantToIdSaa", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL servantToIdSaa(final CompletionStatus completionStatus) {
        return this.servantToIdSaa(completionStatus, null);
    }
    
    public INTERNAL servantToIdSaa(final Throwable t) {
        return this.servantToIdSaa(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL servantToIdSaa() {
        return this.servantToIdSaa(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL servantToIdWp(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080507, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantToIdWp", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL servantToIdWp(final CompletionStatus completionStatus) {
        return this.servantToIdWp(completionStatus, null);
    }
    
    public INTERNAL servantToIdWp(final Throwable t) {
        return this.servantToIdWp(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL servantToIdWp() {
        return this.servantToIdWp(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL cantResolveRootPoa(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080508, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.cantResolveRootPoa", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL cantResolveRootPoa(final CompletionStatus completionStatus) {
        return this.cantResolveRootPoa(completionStatus, null);
    }
    
    public INTERNAL cantResolveRootPoa(final Throwable t) {
        return this.cantResolveRootPoa(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL cantResolveRootPoa() {
        return this.cantResolveRootPoa(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL servantMustBeLocal(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080509, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantMustBeLocal", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL servantMustBeLocal(final CompletionStatus completionStatus) {
        return this.servantMustBeLocal(completionStatus, null);
    }
    
    public INTERNAL servantMustBeLocal(final Throwable t) {
        return this.servantMustBeLocal(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL servantMustBeLocal() {
        return this.servantMustBeLocal(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL noProfilesInIor(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080510, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.noProfilesInIor", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL noProfilesInIor(final CompletionStatus completionStatus) {
        return this.noProfilesInIor(completionStatus, null);
    }
    
    public INTERNAL noProfilesInIor(final Throwable t) {
        return this.noProfilesInIor(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL noProfilesInIor() {
        return this.noProfilesInIor(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL aomEntryDecZero(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080511, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.aomEntryDecZero", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL aomEntryDecZero(final CompletionStatus completionStatus) {
        return this.aomEntryDecZero(completionStatus, null);
    }
    
    public INTERNAL aomEntryDecZero(final Throwable t) {
        return this.aomEntryDecZero(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL aomEntryDecZero() {
        return this.aomEntryDecZero(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL addPoaInactive(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080512, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.addPoaInactive", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL addPoaInactive(final CompletionStatus completionStatus) {
        return this.addPoaInactive(completionStatus, null);
    }
    
    public INTERNAL addPoaInactive(final Throwable t) {
        return this.addPoaInactive(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL addPoaInactive() {
        return this.addPoaInactive(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL illegalPoaStateTrans(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080513, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.illegalPoaStateTrans", null, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL illegalPoaStateTrans(final CompletionStatus completionStatus) {
        return this.illegalPoaStateTrans(completionStatus, null);
    }
    
    public INTERNAL illegalPoaStateTrans(final Throwable t) {
        return this.illegalPoaStateTrans(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL illegalPoaStateTrans() {
        return this.illegalPoaStateTrans(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unexpectedException(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080514, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.unexpectedException", new Object[] { o }, POASystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unexpectedException(final CompletionStatus completionStatus, final Object o) {
        return this.unexpectedException(completionStatus, null, o);
    }
    
    public INTERNAL unexpectedException(final Throwable t, final Object o) {
        return this.unexpectedException(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL unexpectedException(final Object o) {
        return this.unexpectedException(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public NO_IMPLEMENT singleThreadNotSupported(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398080489, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.singleThreadNotSupported", null, POASystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT singleThreadNotSupported(final CompletionStatus completionStatus) {
        return this.singleThreadNotSupported(completionStatus, null);
    }
    
    public NO_IMPLEMENT singleThreadNotSupported(final Throwable t) {
        return this.singleThreadNotSupported(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT singleThreadNotSupported() {
        return this.singleThreadNotSupported(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT methodNotImplemented(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398080490, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.methodNotImplemented", null, POASystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT methodNotImplemented(final CompletionStatus completionStatus) {
        return this.methodNotImplemented(completionStatus, null);
    }
    
    public NO_IMPLEMENT methodNotImplemented(final Throwable t) {
        return this.methodNotImplemented(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT methodNotImplemented() {
        return this.methodNotImplemented(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaLookupError(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080489, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaLookupError", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaLookupError(final CompletionStatus completionStatus) {
        return this.poaLookupError(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaLookupError(final Throwable t) {
        return this.poaLookupError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaLookupError() {
        return this.poaLookupError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaInactive(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080490, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "POA.poaInactive", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaInactive(final CompletionStatus completionStatus) {
        return this.poaInactive(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaInactive(final Throwable t) {
        return this.poaInactive(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaInactive() {
        return this.poaInactive(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaNoServantManager(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080491, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaNoServantManager", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaNoServantManager(final CompletionStatus completionStatus) {
        return this.poaNoServantManager(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaNoServantManager(final Throwable t) {
        return this.poaNoServantManager(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaNoServantManager() {
        return this.poaNoServantManager(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaNoDefaultServant(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080492, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaNoDefaultServant", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaNoDefaultServant(final CompletionStatus completionStatus) {
        return this.poaNoDefaultServant(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaNoDefaultServant(final Throwable t) {
        return this.poaNoDefaultServant(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaNoDefaultServant() {
        return this.poaNoDefaultServant(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaServantNotUnique(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080493, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaServantNotUnique", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaServantNotUnique(final CompletionStatus completionStatus) {
        return this.poaServantNotUnique(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaServantNotUnique(final Throwable t) {
        return this.poaServantNotUnique(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaServantNotUnique() {
        return this.poaServantNotUnique(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaWrongPolicy(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080494, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaWrongPolicy", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaWrongPolicy(final CompletionStatus completionStatus) {
        return this.poaWrongPolicy(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaWrongPolicy(final Throwable t) {
        return this.poaWrongPolicy(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaWrongPolicy() {
        return this.poaWrongPolicy(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER findpoaError(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080495, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.findpoaError", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER findpoaError(final CompletionStatus completionStatus) {
        return this.findpoaError(completionStatus, null);
    }
    
    public OBJ_ADAPTER findpoaError(final Throwable t) {
        return this.findpoaError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER findpoaError() {
        return this.findpoaError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080497, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaServantActivatorLookupFailed", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed(final CompletionStatus completionStatus) {
        return this.poaServantActivatorLookupFailed(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed(final Throwable t) {
        return this.poaServantActivatorLookupFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaServantActivatorLookupFailed() {
        return this.poaServantActivatorLookupFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaBadServantManager(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080498, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaBadServantManager", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaBadServantManager(final CompletionStatus completionStatus) {
        return this.poaBadServantManager(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaBadServantManager(final Throwable t) {
        return this.poaBadServantManager(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaBadServantManager() {
        return this.poaBadServantManager(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080499, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaServantLocatorLookupFailed", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed(final CompletionStatus completionStatus) {
        return this.poaServantLocatorLookupFailed(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed(final Throwable t) {
        return this.poaServantLocatorLookupFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaServantLocatorLookupFailed() {
        return this.poaServantLocatorLookupFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaUnknownPolicy(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080500, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaUnknownPolicy", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaUnknownPolicy(final CompletionStatus completionStatus) {
        return this.poaUnknownPolicy(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaUnknownPolicy(final Throwable t) {
        return this.poaUnknownPolicy(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaUnknownPolicy() {
        return this.poaUnknownPolicy(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER poaNotFound(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080501, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.poaNotFound", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER poaNotFound(final CompletionStatus completionStatus) {
        return this.poaNotFound(completionStatus, null);
    }
    
    public OBJ_ADAPTER poaNotFound(final Throwable t) {
        return this.poaNotFound(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER poaNotFound() {
        return this.poaNotFound(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER servantLookup(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080502, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantLookup", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER servantLookup(final CompletionStatus completionStatus) {
        return this.servantLookup(completionStatus, null);
    }
    
    public OBJ_ADAPTER servantLookup(final Throwable t) {
        return this.servantLookup(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER servantLookup() {
        return this.servantLookup(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER localServantLookup(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080503, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.localServantLookup", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER localServantLookup(final CompletionStatus completionStatus) {
        return this.localServantLookup(completionStatus, null);
    }
    
    public OBJ_ADAPTER localServantLookup(final Throwable t) {
        return this.localServantLookup(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER localServantLookup() {
        return this.localServantLookup(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER servantManagerBadType(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080504, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.servantManagerBadType", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER servantManagerBadType(final CompletionStatus completionStatus) {
        return this.servantManagerBadType(completionStatus, null);
    }
    
    public OBJ_ADAPTER servantManagerBadType(final Throwable t) {
        return this.servantManagerBadType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER servantManagerBadType() {
        return this.servantManagerBadType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080505, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.defaultPoaNotPoaimpl", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl(final CompletionStatus completionStatus) {
        return this.defaultPoaNotPoaimpl(completionStatus, null);
    }
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl(final Throwable t) {
        return this.defaultPoaNotPoaimpl(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER defaultPoaNotPoaimpl() {
        return this.defaultPoaNotPoaimpl(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER wrongPoliciesForThisObject(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080506, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.wrongPoliciesForThisObject", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER wrongPoliciesForThisObject(final CompletionStatus completionStatus) {
        return this.wrongPoliciesForThisObject(completionStatus, null);
    }
    
    public OBJ_ADAPTER wrongPoliciesForThisObject(final Throwable t) {
        return this.wrongPoliciesForThisObject(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER wrongPoliciesForThisObject() {
        return this.wrongPoliciesForThisObject(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER thisObjectServantNotActive(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080507, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.thisObjectServantNotActive", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER thisObjectServantNotActive(final CompletionStatus completionStatus) {
        return this.thisObjectServantNotActive(completionStatus, null);
    }
    
    public OBJ_ADAPTER thisObjectServantNotActive(final Throwable t) {
        return this.thisObjectServantNotActive(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER thisObjectServantNotActive() {
        return this.thisObjectServantNotActive(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER thisObjectWrongPolicy(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080508, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.thisObjectWrongPolicy", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER thisObjectWrongPolicy(final CompletionStatus completionStatus) {
        return this.thisObjectWrongPolicy(completionStatus, null);
    }
    
    public OBJ_ADAPTER thisObjectWrongPolicy(final Throwable t) {
        return this.thisObjectWrongPolicy(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER thisObjectWrongPolicy() {
        return this.thisObjectWrongPolicy(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER noContext(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080509, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "POA.noContext", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER noContext(final CompletionStatus completionStatus) {
        return this.noContext(completionStatus, null);
    }
    
    public OBJ_ADAPTER noContext(final Throwable t) {
        return this.noContext(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER noContext() {
        return this.noContext(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER incarnateReturnedNull(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398080510, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.incarnateReturnedNull", null, POASystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER incarnateReturnedNull(final CompletionStatus completionStatus) {
        return this.incarnateReturnedNull(completionStatus, null);
    }
    
    public OBJ_ADAPTER incarnateReturnedNull(final Throwable t) {
        return this.incarnateReturnedNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER incarnateReturnedNull() {
        return this.incarnateReturnedNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE jtsInitError(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398080489, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.jtsInitError", null, POASystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE jtsInitError(final CompletionStatus completionStatus) {
        return this.jtsInitError(completionStatus, null);
    }
    
    public INITIALIZE jtsInitError(final Throwable t) {
        return this.jtsInitError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE jtsInitError() {
        return this.jtsInitError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE persistentServeridNotSet(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398080490, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.persistentServeridNotSet", null, POASystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE persistentServeridNotSet(final CompletionStatus completionStatus) {
        return this.persistentServeridNotSet(completionStatus, null);
    }
    
    public INITIALIZE persistentServeridNotSet(final Throwable t) {
        return this.persistentServeridNotSet(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE persistentServeridNotSet() {
        return this.persistentServeridNotSet(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE persistentServerportNotSet(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398080491, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.persistentServerportNotSet", null, POASystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE persistentServerportNotSet(final CompletionStatus completionStatus) {
        return this.persistentServerportNotSet(completionStatus, null);
    }
    
    public INITIALIZE persistentServerportNotSet(final Throwable t) {
        return this.persistentServerportNotSet(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE persistentServerportNotSet() {
        return this.persistentServerportNotSet(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE orbdError(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398080492, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.orbdError", null, POASystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE orbdError(final CompletionStatus completionStatus) {
        return this.orbdError(completionStatus, null);
    }
    
    public INITIALIZE orbdError(final Throwable t) {
        return this.orbdError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE orbdError() {
        return this.orbdError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE bootstrapError(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398080493, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.bootstrapError", null, POASystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE bootstrapError(final CompletionStatus completionStatus) {
        return this.bootstrapError(completionStatus, null);
    }
    
    public INITIALIZE bootstrapError(final Throwable t) {
        return this.bootstrapError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE bootstrapError() {
        return this.bootstrapError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSIENT poaDiscarding(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSIENT transient1 = new TRANSIENT(1398080489, completionStatus);
        if (t != null) {
            transient1.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "POA.poaDiscarding", null, POASystemException.class, transient1);
        }
        return transient1;
    }
    
    public TRANSIENT poaDiscarding(final CompletionStatus completionStatus) {
        return this.poaDiscarding(completionStatus, null);
    }
    
    public TRANSIENT poaDiscarding(final Throwable t) {
        return this.poaDiscarding(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSIENT poaDiscarding() {
        return this.poaDiscarding(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN otshookexception(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080489, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.otshookexception", null, POASystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN otshookexception(final CompletionStatus completionStatus) {
        return this.otshookexception(completionStatus, null);
    }
    
    public UNKNOWN otshookexception(final Throwable t) {
        return this.otshookexception(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN otshookexception() {
        return this.otshookexception(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownServerException(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080490, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.unknownServerException", null, POASystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownServerException(final CompletionStatus completionStatus) {
        return this.unknownServerException(completionStatus, null);
    }
    
    public UNKNOWN unknownServerException(final Throwable t) {
        return this.unknownServerException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownServerException() {
        return this.unknownServerException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownServerappException(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080491, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.unknownServerappException", null, POASystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownServerappException(final CompletionStatus completionStatus) {
        return this.unknownServerappException(completionStatus, null);
    }
    
    public UNKNOWN unknownServerappException(final Throwable t) {
        return this.unknownServerappException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownServerappException() {
        return this.unknownServerappException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownLocalinvocationError(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080492, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.unknownLocalinvocationError", null, POASystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownLocalinvocationError(final CompletionStatus completionStatus) {
        return this.unknownLocalinvocationError(completionStatus, null);
    }
    
    public UNKNOWN unknownLocalinvocationError(final Throwable t) {
        return this.unknownLocalinvocationError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownLocalinvocationError() {
        return this.unknownLocalinvocationError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398080489, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.adapterActivatorNonexistent", null, POASystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent(final CompletionStatus completionStatus) {
        return this.adapterActivatorNonexistent(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent(final Throwable t) {
        return this.adapterActivatorNonexistent(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST adapterActivatorNonexistent() {
        return this.adapterActivatorNonexistent(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST adapterActivatorFailed(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398080490, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.adapterActivatorFailed", null, POASystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST adapterActivatorFailed(final CompletionStatus completionStatus) {
        return this.adapterActivatorFailed(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST adapterActivatorFailed(final Throwable t) {
        return this.adapterActivatorFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST adapterActivatorFailed() {
        return this.adapterActivatorFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST badSkeleton(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398080491, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.badSkeleton", null, POASystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST badSkeleton(final CompletionStatus completionStatus) {
        return this.badSkeleton(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST badSkeleton(final Throwable t) {
        return this.badSkeleton(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST badSkeleton() {
        return this.badSkeleton(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST nullServant(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398080492, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "POA.nullServant", null, POASystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST nullServant(final CompletionStatus completionStatus) {
        return this.nullServant(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST nullServant(final Throwable t) {
        return this.nullServant(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST nullServant() {
        return this.nullServant(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST adapterDestroyed(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398080493, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "POA.adapterDestroyed", null, POASystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST adapterDestroyed(final CompletionStatus completionStatus) {
        return this.adapterDestroyed(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST adapterDestroyed(final Throwable t) {
        return this.adapterDestroyed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST adapterDestroyed() {
        return this.adapterDestroyed(CompletionStatus.COMPLETED_NO, null);
    }
    
    static {
        POASystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new POASystemException(logger);
            }
        };
    }
}
