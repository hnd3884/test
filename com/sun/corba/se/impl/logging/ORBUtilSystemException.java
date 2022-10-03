package com.sun.corba.se.impl.logging;

import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import java.util.logging.Level;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class ORBUtilSystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int ADAPTER_ID_NOT_AVAILABLE = 1398079689;
    public static final int SERVER_ID_NOT_AVAILABLE = 1398079690;
    public static final int ORB_ID_NOT_AVAILABLE = 1398079691;
    public static final int OBJECT_ADAPTER_ID_NOT_AVAILABLE = 1398079692;
    public static final int CONNECTING_SERVANT = 1398079693;
    public static final int EXTRACT_WRONG_TYPE = 1398079694;
    public static final int EXTRACT_WRONG_TYPE_LIST = 1398079695;
    public static final int BAD_STRING_BOUNDS = 1398079696;
    public static final int INSERT_OBJECT_INCOMPATIBLE = 1398079698;
    public static final int INSERT_OBJECT_FAILED = 1398079699;
    public static final int EXTRACT_OBJECT_INCOMPATIBLE = 1398079700;
    public static final int FIXED_NOT_MATCH = 1398079701;
    public static final int FIXED_BAD_TYPECODE = 1398079702;
    public static final int SET_EXCEPTION_CALLED_NULL_ARGS = 1398079711;
    public static final int SET_EXCEPTION_CALLED_BAD_TYPE = 1398079712;
    public static final int CONTEXT_CALLED_OUT_OF_ORDER = 1398079713;
    public static final int BAD_ORB_CONFIGURATOR = 1398079714;
    public static final int ORB_CONFIGURATOR_ERROR = 1398079715;
    public static final int ORB_DESTROYED = 1398079716;
    public static final int NEGATIVE_BOUNDS = 1398079717;
    public static final int EXTRACT_NOT_INITIALIZED = 1398079718;
    public static final int EXTRACT_OBJECT_FAILED = 1398079719;
    public static final int METHOD_NOT_FOUND_IN_TIE = 1398079720;
    public static final int CLASS_NOT_FOUND1 = 1398079721;
    public static final int CLASS_NOT_FOUND2 = 1398079722;
    public static final int CLASS_NOT_FOUND3 = 1398079723;
    public static final int GET_DELEGATE_SERVANT_NOT_ACTIVE = 1398079724;
    public static final int GET_DELEGATE_WRONG_POLICY = 1398079725;
    public static final int SET_DELEGATE_REQUIRES_STUB = 1398079726;
    public static final int GET_DELEGATE_REQUIRES_STUB = 1398079727;
    public static final int GET_TYPE_IDS_REQUIRES_STUB = 1398079728;
    public static final int GET_ORB_REQUIRES_STUB = 1398079729;
    public static final int CONNECT_REQUIRES_STUB = 1398079730;
    public static final int IS_LOCAL_REQUIRES_STUB = 1398079731;
    public static final int REQUEST_REQUIRES_STUB = 1398079732;
    public static final int BAD_ACTIVATE_TIE_CALL = 1398079733;
    public static final int IO_EXCEPTION_ON_CLOSE = 1398079734;
    public static final int NULL_PARAM = 1398079689;
    public static final int UNABLE_FIND_VALUE_FACTORY = 1398079690;
    public static final int ABSTRACT_FROM_NON_ABSTRACT = 1398079691;
    public static final int INVALID_TAGGED_PROFILE = 1398079692;
    public static final int OBJREF_FROM_FOREIGN_ORB = 1398079693;
    public static final int LOCAL_OBJECT_NOT_ALLOWED = 1398079694;
    public static final int NULL_OBJECT_REFERENCE = 1398079695;
    public static final int COULD_NOT_LOAD_CLASS = 1398079696;
    public static final int BAD_URL = 1398079697;
    public static final int FIELD_NOT_FOUND = 1398079698;
    public static final int ERROR_SETTING_FIELD = 1398079699;
    public static final int BOUNDS_ERROR_IN_DII_REQUEST = 1398079700;
    public static final int PERSISTENT_SERVER_INIT_ERROR = 1398079701;
    public static final int COULD_NOT_CREATE_ARRAY = 1398079702;
    public static final int COULD_NOT_SET_ARRAY = 1398079703;
    public static final int ILLEGAL_BOOTSTRAP_OPERATION = 1398079704;
    public static final int BOOTSTRAP_RUNTIME_EXCEPTION = 1398079705;
    public static final int BOOTSTRAP_EXCEPTION = 1398079706;
    public static final int STRING_EXPECTED = 1398079707;
    public static final int INVALID_TYPECODE_KIND = 1398079708;
    public static final int SOCKET_FACTORY_AND_CONTACT_INFO_LIST_AT_SAME_TIME = 1398079709;
    public static final int ACCEPTORS_AND_LEGACY_SOCKET_FACTORY_AT_SAME_TIME = 1398079710;
    public static final int BAD_ORB_FOR_SERVANT = 1398079711;
    public static final int INVALID_REQUEST_PARTITIONING_POLICY_VALUE = 1398079712;
    public static final int INVALID_REQUEST_PARTITIONING_COMPONENT_VALUE = 1398079713;
    public static final int INVALID_REQUEST_PARTITIONING_ID = 1398079714;
    public static final int ERROR_IN_SETTING_DYNAMIC_STUB_FACTORY_FACTORY = 1398079715;
    public static final int DSIMETHOD_NOTCALLED = 1398079689;
    public static final int ARGUMENTS_CALLED_MULTIPLE = 1398079690;
    public static final int ARGUMENTS_CALLED_AFTER_EXCEPTION = 1398079691;
    public static final int ARGUMENTS_CALLED_NULL_ARGS = 1398079692;
    public static final int ARGUMENTS_NOT_CALLED = 1398079693;
    public static final int SET_RESULT_CALLED_MULTIPLE = 1398079694;
    public static final int SET_RESULT_AFTER_EXCEPTION = 1398079695;
    public static final int SET_RESULT_CALLED_NULL_ARGS = 1398079696;
    public static final int BAD_REMOTE_TYPECODE = 1398079689;
    public static final int UNRESOLVED_RECURSIVE_TYPECODE = 1398079690;
    public static final int CONNECT_FAILURE = 1398079689;
    public static final int CONNECTION_CLOSE_REBIND = 1398079690;
    public static final int WRITE_ERROR_SEND = 1398079691;
    public static final int GET_PROPERTIES_ERROR = 1398079692;
    public static final int BOOTSTRAP_SERVER_NOT_AVAIL = 1398079693;
    public static final int INVOKE_ERROR = 1398079694;
    public static final int DEFAULT_CREATE_SERVER_SOCKET_GIVEN_NON_IIOP_CLEAR_TEXT = 1398079695;
    public static final int CONNECTION_ABORT = 1398079696;
    public static final int CONNECTION_REBIND = 1398079697;
    public static final int RECV_MSG_ERROR = 1398079698;
    public static final int IOEXCEPTION_WHEN_READING_CONNECTION = 1398079699;
    public static final int SELECTION_KEY_INVALID = 1398079700;
    public static final int EXCEPTION_IN_ACCEPT = 1398079701;
    public static final int SECURITY_EXCEPTION_IN_ACCEPT = 1398079702;
    public static final int TRANSPORT_READ_TIMEOUT_EXCEEDED = 1398079703;
    public static final int CREATE_LISTENER_FAILED = 1398079704;
    public static final int BUFFER_READ_MANAGER_TIMEOUT = 1398079705;
    public static final int BAD_STRINGIFIED_IOR_LEN = 1398079689;
    public static final int BAD_STRINGIFIED_IOR = 1398079690;
    public static final int BAD_MODIFIER = 1398079691;
    public static final int CODESET_INCOMPATIBLE = 1398079692;
    public static final int BAD_HEX_DIGIT = 1398079693;
    public static final int BAD_UNICODE_PAIR = 1398079694;
    public static final int BTC_RESULT_MORE_THAN_ONE_CHAR = 1398079695;
    public static final int BAD_CODESETS_FROM_CLIENT = 1398079696;
    public static final int INVALID_SINGLE_CHAR_CTB = 1398079697;
    public static final int BAD_GIOP_1_1_CTB = 1398079698;
    public static final int BAD_SEQUENCE_BOUNDS = 1398079700;
    public static final int ILLEGAL_SOCKET_FACTORY_TYPE = 1398079701;
    public static final int BAD_CUSTOM_SOCKET_FACTORY = 1398079702;
    public static final int FRAGMENT_SIZE_MINIMUM = 1398079703;
    public static final int FRAGMENT_SIZE_DIV = 1398079704;
    public static final int ORB_INITIALIZER_FAILURE = 1398079705;
    public static final int ORB_INITIALIZER_TYPE = 1398079706;
    public static final int ORB_INITIALREFERENCE_SYNTAX = 1398079707;
    public static final int ACCEPTOR_INSTANTIATION_FAILURE = 1398079708;
    public static final int ACCEPTOR_INSTANTIATION_TYPE_FAILURE = 1398079709;
    public static final int ILLEGAL_CONTACT_INFO_LIST_FACTORY_TYPE = 1398079710;
    public static final int BAD_CONTACT_INFO_LIST_FACTORY = 1398079711;
    public static final int ILLEGAL_IOR_TO_SOCKET_INFO_TYPE = 1398079712;
    public static final int BAD_CUSTOM_IOR_TO_SOCKET_INFO = 1398079713;
    public static final int ILLEGAL_IIOP_PRIMARY_TO_CONTACT_INFO_TYPE = 1398079714;
    public static final int BAD_CUSTOM_IIOP_PRIMARY_TO_CONTACT_INFO = 1398079715;
    public static final int BAD_CORBALOC_STRING = 1398079689;
    public static final int NO_PROFILE_PRESENT = 1398079690;
    public static final int CANNOT_CREATE_ORBID_DB = 1398079689;
    public static final int CANNOT_READ_ORBID_DB = 1398079690;
    public static final int CANNOT_WRITE_ORBID_DB = 1398079691;
    public static final int GET_SERVER_PORT_CALLED_BEFORE_ENDPOINTS_INITIALIZED = 1398079692;
    public static final int PERSISTENT_SERVERPORT_NOT_SET = 1398079693;
    public static final int PERSISTENT_SERVERID_NOT_SET = 1398079694;
    public static final int NON_EXISTENT_ORBID = 1398079689;
    public static final int NO_SERVER_SUBCONTRACT = 1398079690;
    public static final int SERVER_SC_TEMP_SIZE = 1398079691;
    public static final int NO_CLIENT_SC_CLASS = 1398079692;
    public static final int SERVER_SC_NO_IIOP_PROFILE = 1398079693;
    public static final int GET_SYSTEM_EX_RETURNED_NULL = 1398079694;
    public static final int PEEKSTRING_FAILED = 1398079695;
    public static final int GET_LOCAL_HOST_FAILED = 1398079696;
    public static final int BAD_LOCATE_REQUEST_STATUS = 1398079698;
    public static final int STRINGIFY_WRITE_ERROR = 1398079699;
    public static final int BAD_GIOP_REQUEST_TYPE = 1398079700;
    public static final int ERROR_UNMARSHALING_USEREXC = 1398079701;
    public static final int RequestDispatcherRegistry_ERROR = 1398079702;
    public static final int LOCATIONFORWARD_ERROR = 1398079703;
    public static final int WRONG_CLIENTSC = 1398079704;
    public static final int BAD_SERVANT_READ_OBJECT = 1398079705;
    public static final int MULT_IIOP_PROF_NOT_SUPPORTED = 1398079706;
    public static final int GIOP_MAGIC_ERROR = 1398079708;
    public static final int GIOP_VERSION_ERROR = 1398079709;
    public static final int ILLEGAL_REPLY_STATUS = 1398079710;
    public static final int ILLEGAL_GIOP_MSG_TYPE = 1398079711;
    public static final int FRAGMENTATION_DISALLOWED = 1398079712;
    public static final int BAD_REPLYSTATUS = 1398079713;
    public static final int CTB_CONVERTER_FAILURE = 1398079714;
    public static final int BTC_CONVERTER_FAILURE = 1398079715;
    public static final int WCHAR_ARRAY_UNSUPPORTED_ENCODING = 1398079716;
    public static final int ILLEGAL_TARGET_ADDRESS_DISPOSITION = 1398079717;
    public static final int NULL_REPLY_IN_GET_ADDR_DISPOSITION = 1398079718;
    public static final int ORB_TARGET_ADDR_PREFERENCE_IN_EXTRACT_OBJECTKEY_INVALID = 1398079719;
    public static final int INVALID_ISSTREAMED_TCKIND = 1398079720;
    public static final int INVALID_JDK1_3_1_PATCH_LEVEL = 1398079721;
    public static final int SVCCTX_UNMARSHAL_ERROR = 1398079722;
    public static final int NULL_IOR = 1398079723;
    public static final int UNSUPPORTED_GIOP_VERSION = 1398079724;
    public static final int APPLICATION_EXCEPTION_IN_SPECIAL_METHOD = 1398079725;
    public static final int STATEMENT_NOT_REACHABLE1 = 1398079726;
    public static final int STATEMENT_NOT_REACHABLE2 = 1398079727;
    public static final int STATEMENT_NOT_REACHABLE3 = 1398079728;
    public static final int STATEMENT_NOT_REACHABLE4 = 1398079729;
    public static final int STATEMENT_NOT_REACHABLE5 = 1398079730;
    public static final int STATEMENT_NOT_REACHABLE6 = 1398079731;
    public static final int UNEXPECTED_DII_EXCEPTION = 1398079732;
    public static final int METHOD_SHOULD_NOT_BE_CALLED = 1398079733;
    public static final int CANCEL_NOT_SUPPORTED = 1398079734;
    public static final int EMPTY_STACK_RUN_SERVANT_POST_INVOKE = 1398079735;
    public static final int PROBLEM_WITH_EXCEPTION_TYPECODE = 1398079736;
    public static final int ILLEGAL_SUBCONTRACT_ID = 1398079737;
    public static final int BAD_SYSTEM_EXCEPTION_IN_LOCATE_REPLY = 1398079738;
    public static final int BAD_SYSTEM_EXCEPTION_IN_REPLY = 1398079739;
    public static final int BAD_COMPLETION_STATUS_IN_LOCATE_REPLY = 1398079740;
    public static final int BAD_COMPLETION_STATUS_IN_REPLY = 1398079741;
    public static final int BADKIND_CANNOT_OCCUR = 1398079742;
    public static final int ERROR_RESOLVING_ALIAS = 1398079743;
    public static final int TK_LONG_DOUBLE_NOT_SUPPORTED = 1398079744;
    public static final int TYPECODE_NOT_SUPPORTED = 1398079745;
    public static final int BOUNDS_CANNOT_OCCUR = 1398079747;
    public static final int NUM_INVOCATIONS_ALREADY_ZERO = 1398079749;
    public static final int ERROR_INIT_BADSERVERIDHANDLER = 1398079750;
    public static final int NO_TOA = 1398079751;
    public static final int NO_POA = 1398079752;
    public static final int INVOCATION_INFO_STACK_EMPTY = 1398079753;
    public static final int BAD_CODE_SET_STRING = 1398079754;
    public static final int UNKNOWN_NATIVE_CODESET = 1398079755;
    public static final int UNKNOWN_CONVERSION_CODE_SET = 1398079756;
    public static final int INVALID_CODE_SET_NUMBER = 1398079757;
    public static final int INVALID_CODE_SET_STRING = 1398079758;
    public static final int INVALID_CTB_CONVERTER_NAME = 1398079759;
    public static final int INVALID_BTC_CONVERTER_NAME = 1398079760;
    public static final int COULD_NOT_DUPLICATE_CDR_INPUT_STREAM = 1398079761;
    public static final int BOOTSTRAP_APPLICATION_EXCEPTION = 1398079762;
    public static final int DUPLICATE_INDIRECTION_OFFSET = 1398079763;
    public static final int BAD_MESSAGE_TYPE_FOR_CANCEL = 1398079764;
    public static final int DUPLICATE_EXCEPTION_DETAIL_MESSAGE = 1398079765;
    public static final int BAD_EXCEPTION_DETAIL_MESSAGE_SERVICE_CONTEXT_TYPE = 1398079766;
    public static final int UNEXPECTED_DIRECT_BYTE_BUFFER_WITH_NON_CHANNEL_SOCKET = 1398079767;
    public static final int UNEXPECTED_NON_DIRECT_BYTE_BUFFER_WITH_CHANNEL_SOCKET = 1398079768;
    public static final int INVALID_CONTACT_INFO_LIST_ITERATOR_FAILURE_EXCEPTION = 1398079770;
    public static final int REMARSHAL_WITH_NOWHERE_TO_GO = 1398079771;
    public static final int EXCEPTION_WHEN_SENDING_CLOSE_CONNECTION = 1398079772;
    public static final int INVOCATION_ERROR_IN_REFLECTIVE_TIE = 1398079773;
    public static final int BAD_HELPER_WRITE_METHOD = 1398079774;
    public static final int BAD_HELPER_READ_METHOD = 1398079775;
    public static final int BAD_HELPER_ID_METHOD = 1398079776;
    public static final int WRITE_UNDECLARED_EXCEPTION = 1398079777;
    public static final int READ_UNDECLARED_EXCEPTION = 1398079778;
    public static final int UNABLE_TO_SET_SOCKET_FACTORY_ORB = 1398079779;
    public static final int UNEXPECTED_EXCEPTION = 1398079780;
    public static final int NO_INVOCATION_HANDLER = 1398079781;
    public static final int INVALID_BUFF_MGR_STRATEGY = 1398079782;
    public static final int JAVA_STREAM_INIT_FAILED = 1398079783;
    public static final int DUPLICATE_ORB_VERSION_SERVICE_CONTEXT = 1398079784;
    public static final int DUPLICATE_SENDING_CONTEXT_SERVICE_CONTEXT = 1398079785;
    public static final int WORK_QUEUE_THREAD_INTERRUPTED = 1398079786;
    public static final int WORKER_THREAD_CREATED = 1398079792;
    public static final int WORKER_THREAD_THROWABLE_FROM_REQUEST_WORK = 1398079797;
    public static final int WORKER_THREAD_NOT_NEEDED = 1398079798;
    public static final int WORKER_THREAD_DO_WORK_THROWABLE = 1398079799;
    public static final int WORKER_THREAD_CAUGHT_UNEXPECTED_THROWABLE = 1398079800;
    public static final int WORKER_THREAD_CREATION_FAILURE = 1398079801;
    public static final int WORKER_THREAD_SET_NAME_FAILURE = 1398079802;
    public static final int WORK_QUEUE_REQUEST_WORK_NO_WORK_FOUND = 1398079804;
    public static final int THREAD_POOL_CLOSE_ERROR = 1398079814;
    public static final int THREAD_GROUP_IS_DESTROYED = 1398079815;
    public static final int THREAD_GROUP_HAS_ACTIVE_THREADS_IN_CLOSE = 1398079816;
    public static final int THREAD_GROUP_HAS_SUB_GROUPS_IN_CLOSE = 1398079817;
    public static final int THREAD_GROUP_DESTROY_FAILED = 1398079818;
    public static final int INTERRUPTED_JOIN_CALL_WHILE_CLOSING_THREAD_POOL = 1398079819;
    public static final int CHUNK_OVERFLOW = 1398079689;
    public static final int UNEXPECTED_EOF = 1398079690;
    public static final int READ_OBJECT_EXCEPTION = 1398079691;
    public static final int CHARACTER_OUTOFRANGE = 1398079692;
    public static final int DSI_RESULT_EXCEPTION = 1398079693;
    public static final int IIOPINPUTSTREAM_GROW = 1398079694;
    public static final int END_OF_STREAM = 1398079695;
    public static final int INVALID_OBJECT_KEY = 1398079696;
    public static final int MALFORMED_URL = 1398079697;
    public static final int VALUEHANDLER_READ_ERROR = 1398079698;
    public static final int VALUEHANDLER_READ_EXCEPTION = 1398079699;
    public static final int BAD_KIND = 1398079700;
    public static final int CNFE_READ_CLASS = 1398079701;
    public static final int BAD_REP_ID_INDIRECTION = 1398079702;
    public static final int BAD_CODEBASE_INDIRECTION = 1398079703;
    public static final int UNKNOWN_CODESET = 1398079704;
    public static final int WCHAR_DATA_IN_GIOP_1_0 = 1398079705;
    public static final int NEGATIVE_STRING_LENGTH = 1398079706;
    public static final int EXPECTED_TYPE_NULL_AND_NO_REP_ID = 1398079707;
    public static final int READ_VALUE_AND_NO_REP_ID = 1398079708;
    public static final int UNEXPECTED_ENCLOSING_VALUETYPE = 1398079710;
    public static final int POSITIVE_END_TAG = 1398079711;
    public static final int NULL_OUT_CALL = 1398079712;
    public static final int WRITE_LOCAL_OBJECT = 1398079713;
    public static final int BAD_INSERTOBJ_PARAM = 1398079714;
    public static final int CUSTOM_WRAPPER_WITH_CODEBASE = 1398079715;
    public static final int CUSTOM_WRAPPER_INDIRECTION = 1398079716;
    public static final int CUSTOM_WRAPPER_NOT_SINGLE_REPID = 1398079717;
    public static final int BAD_VALUE_TAG = 1398079718;
    public static final int BAD_TYPECODE_FOR_CUSTOM_VALUE = 1398079719;
    public static final int ERROR_INVOKING_HELPER_WRITE = 1398079720;
    public static final int BAD_DIGIT_IN_FIXED = 1398079721;
    public static final int REF_TYPE_INDIR_TYPE = 1398079722;
    public static final int BAD_RESERVED_LENGTH = 1398079723;
    public static final int NULL_NOT_ALLOWED = 1398079724;
    public static final int UNION_DISCRIMINATOR_ERROR = 1398079726;
    public static final int CANNOT_MARSHAL_NATIVE = 1398079727;
    public static final int CANNOT_MARSHAL_BAD_TCKIND = 1398079728;
    public static final int INVALID_INDIRECTION = 1398079729;
    public static final int INDIRECTION_NOT_FOUND = 1398079730;
    public static final int RECURSIVE_TYPECODE_ERROR = 1398079731;
    public static final int INVALID_SIMPLE_TYPECODE = 1398079732;
    public static final int INVALID_COMPLEX_TYPECODE = 1398079733;
    public static final int INVALID_TYPECODE_KIND_MARSHAL = 1398079734;
    public static final int UNEXPECTED_UNION_DEFAULT = 1398079735;
    public static final int ILLEGAL_UNION_DISCRIMINATOR_TYPE = 1398079736;
    public static final int COULD_NOT_SKIP_BYTES = 1398079737;
    public static final int BAD_CHUNK_LENGTH = 1398079738;
    public static final int UNABLE_TO_LOCATE_REP_ID_ARRAY = 1398079739;
    public static final int BAD_FIXED = 1398079740;
    public static final int READ_OBJECT_LOAD_CLASS_FAILURE = 1398079741;
    public static final int COULD_NOT_INSTANTIATE_HELPER = 1398079742;
    public static final int BAD_TOA_OAID = 1398079743;
    public static final int COULD_NOT_INVOKE_HELPER_READ_METHOD = 1398079744;
    public static final int COULD_NOT_FIND_CLASS = 1398079745;
    public static final int BAD_ARGUMENTS_NVLIST = 1398079746;
    public static final int STUB_CREATE_ERROR = 1398079747;
    public static final int JAVA_SERIALIZATION_EXCEPTION = 1398079748;
    public static final int GENERIC_NO_IMPL = 1398079689;
    public static final int CONTEXT_NOT_IMPLEMENTED = 1398079690;
    public static final int GETINTERFACE_NOT_IMPLEMENTED = 1398079691;
    public static final int SEND_DEFERRED_NOTIMPLEMENTED = 1398079692;
    public static final int LONG_DOUBLE_NOT_IMPLEMENTED = 1398079693;
    public static final int NO_SERVER_SC_IN_DISPATCH = 1398079689;
    public static final int ORB_CONNECT_ERROR = 1398079690;
    public static final int ADAPTER_INACTIVE_IN_ACTIVATION = 1398079691;
    public static final int LOCATE_UNKNOWN_OBJECT = 1398079689;
    public static final int BAD_SERVER_ID = 1398079690;
    public static final int BAD_SKELETON = 1398079691;
    public static final int SERVANT_NOT_FOUND = 1398079692;
    public static final int NO_OBJECT_ADAPTER_FACTORY = 1398079693;
    public static final int BAD_ADAPTER_ID = 1398079694;
    public static final int DYN_ANY_DESTROYED = 1398079695;
    public static final int REQUEST_CANCELED = 1398079689;
    public static final int UNKNOWN_CORBA_EXC = 1398079689;
    public static final int RUNTIMEEXCEPTION = 1398079690;
    public static final int UNKNOWN_SERVER_ERROR = 1398079691;
    public static final int UNKNOWN_DSI_SYSEX = 1398079692;
    public static final int UNKNOWN_SYSEX = 1398079693;
    public static final int WRONG_INTERFACE_DEF = 1398079694;
    public static final int NO_INTERFACE_DEF_STUB = 1398079695;
    public static final int UNKNOWN_EXCEPTION_IN_DISPATCH = 1398079697;
    
    public ORBUtilSystemException(final Logger logger) {
        super(logger);
    }
    
    public static ORBUtilSystemException get(final ORB orb, final String s) {
        return (ORBUtilSystemException)orb.getLogWrapper(s, "ORBUTIL", ORBUtilSystemException.factory);
    }
    
    public static ORBUtilSystemException get(final String s) {
        return (ORBUtilSystemException)ORB.staticGetLogWrapper(s, "ORBUTIL", ORBUtilSystemException.factory);
    }
    
    public BAD_OPERATION adapterIdNotAvailable(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079689, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.adapterIdNotAvailable", null, ORBUtilSystemException.class, bad_OPERATION);
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
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079690, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.serverIdNotAvailable", null, ORBUtilSystemException.class, bad_OPERATION);
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
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079691, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbIdNotAvailable", null, ORBUtilSystemException.class, bad_OPERATION);
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
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079692, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.objectAdapterIdNotAvailable", null, ORBUtilSystemException.class, bad_OPERATION);
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
    
    public BAD_OPERATION connectingServant(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079693, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.connectingServant", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION connectingServant(final CompletionStatus completionStatus) {
        return this.connectingServant(completionStatus, null);
    }
    
    public BAD_OPERATION connectingServant(final Throwable t) {
        return this.connectingServant(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION connectingServant() {
        return this.connectingServant(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION extractWrongType(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079694, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.extractWrongType", new Object[] { o, o2 }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION extractWrongType(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.extractWrongType(completionStatus, null, o, o2);
    }
    
    public BAD_OPERATION extractWrongType(final Throwable t, final Object o, final Object o2) {
        return this.extractWrongType(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public BAD_OPERATION extractWrongType(final Object o, final Object o2) {
        return this.extractWrongType(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public BAD_OPERATION extractWrongTypeList(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079695, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.extractWrongTypeList", new Object[] { o, o2 }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION extractWrongTypeList(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.extractWrongTypeList(completionStatus, null, o, o2);
    }
    
    public BAD_OPERATION extractWrongTypeList(final Throwable t, final Object o, final Object o2) {
        return this.extractWrongTypeList(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public BAD_OPERATION extractWrongTypeList(final Object o, final Object o2) {
        return this.extractWrongTypeList(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public BAD_OPERATION badStringBounds(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079696, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badStringBounds", new Object[] { o, o2 }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION badStringBounds(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.badStringBounds(completionStatus, null, o, o2);
    }
    
    public BAD_OPERATION badStringBounds(final Throwable t, final Object o, final Object o2) {
        return this.badStringBounds(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public BAD_OPERATION badStringBounds(final Object o, final Object o2) {
        return this.badStringBounds(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public BAD_OPERATION insertObjectIncompatible(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079698, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.insertObjectIncompatible", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION insertObjectIncompatible(final CompletionStatus completionStatus) {
        return this.insertObjectIncompatible(completionStatus, null);
    }
    
    public BAD_OPERATION insertObjectIncompatible(final Throwable t) {
        return this.insertObjectIncompatible(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION insertObjectIncompatible() {
        return this.insertObjectIncompatible(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION insertObjectFailed(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079699, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.insertObjectFailed", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION insertObjectFailed(final CompletionStatus completionStatus) {
        return this.insertObjectFailed(completionStatus, null);
    }
    
    public BAD_OPERATION insertObjectFailed(final Throwable t) {
        return this.insertObjectFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION insertObjectFailed() {
        return this.insertObjectFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION extractObjectIncompatible(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079700, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.extractObjectIncompatible", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION extractObjectIncompatible(final CompletionStatus completionStatus) {
        return this.extractObjectIncompatible(completionStatus, null);
    }
    
    public BAD_OPERATION extractObjectIncompatible(final Throwable t) {
        return this.extractObjectIncompatible(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION extractObjectIncompatible() {
        return this.extractObjectIncompatible(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION fixedNotMatch(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079701, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.fixedNotMatch", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION fixedNotMatch(final CompletionStatus completionStatus) {
        return this.fixedNotMatch(completionStatus, null);
    }
    
    public BAD_OPERATION fixedNotMatch(final Throwable t) {
        return this.fixedNotMatch(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION fixedNotMatch() {
        return this.fixedNotMatch(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION fixedBadTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079702, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.fixedBadTypecode", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION fixedBadTypecode(final CompletionStatus completionStatus) {
        return this.fixedBadTypecode(completionStatus, null);
    }
    
    public BAD_OPERATION fixedBadTypecode(final Throwable t) {
        return this.fixedBadTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION fixedBadTypecode() {
        return this.fixedBadTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION setExceptionCalledNullArgs(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079711, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.setExceptionCalledNullArgs", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION setExceptionCalledNullArgs(final CompletionStatus completionStatus) {
        return this.setExceptionCalledNullArgs(completionStatus, null);
    }
    
    public BAD_OPERATION setExceptionCalledNullArgs(final Throwable t) {
        return this.setExceptionCalledNullArgs(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION setExceptionCalledNullArgs() {
        return this.setExceptionCalledNullArgs(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION setExceptionCalledBadType(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079712, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.setExceptionCalledBadType", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION setExceptionCalledBadType(final CompletionStatus completionStatus) {
        return this.setExceptionCalledBadType(completionStatus, null);
    }
    
    public BAD_OPERATION setExceptionCalledBadType(final Throwable t) {
        return this.setExceptionCalledBadType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION setExceptionCalledBadType() {
        return this.setExceptionCalledBadType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION contextCalledOutOfOrder(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079713, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.contextCalledOutOfOrder", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION contextCalledOutOfOrder(final CompletionStatus completionStatus) {
        return this.contextCalledOutOfOrder(completionStatus, null);
    }
    
    public BAD_OPERATION contextCalledOutOfOrder(final Throwable t) {
        return this.contextCalledOutOfOrder(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION contextCalledOutOfOrder() {
        return this.contextCalledOutOfOrder(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION badOrbConfigurator(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079714, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badOrbConfigurator", new Object[] { o }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION badOrbConfigurator(final CompletionStatus completionStatus, final Object o) {
        return this.badOrbConfigurator(completionStatus, null, o);
    }
    
    public BAD_OPERATION badOrbConfigurator(final Throwable t, final Object o) {
        return this.badOrbConfigurator(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_OPERATION badOrbConfigurator(final Object o) {
        return this.badOrbConfigurator(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_OPERATION orbConfiguratorError(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079715, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbConfiguratorError", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION orbConfiguratorError(final CompletionStatus completionStatus) {
        return this.orbConfiguratorError(completionStatus, null);
    }
    
    public BAD_OPERATION orbConfiguratorError(final Throwable t) {
        return this.orbConfiguratorError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION orbConfiguratorError() {
        return this.orbConfiguratorError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION orbDestroyed(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079716, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbDestroyed", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION orbDestroyed(final CompletionStatus completionStatus) {
        return this.orbDestroyed(completionStatus, null);
    }
    
    public BAD_OPERATION orbDestroyed(final Throwable t) {
        return this.orbDestroyed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION orbDestroyed() {
        return this.orbDestroyed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION negativeBounds(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079717, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.negativeBounds", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION negativeBounds(final CompletionStatus completionStatus) {
        return this.negativeBounds(completionStatus, null);
    }
    
    public BAD_OPERATION negativeBounds(final Throwable t) {
        return this.negativeBounds(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION negativeBounds() {
        return this.negativeBounds(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION extractNotInitialized(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079718, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.extractNotInitialized", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION extractNotInitialized(final CompletionStatus completionStatus) {
        return this.extractNotInitialized(completionStatus, null);
    }
    
    public BAD_OPERATION extractNotInitialized(final Throwable t) {
        return this.extractNotInitialized(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION extractNotInitialized() {
        return this.extractNotInitialized(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION extractObjectFailed(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079719, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.extractObjectFailed", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION extractObjectFailed(final CompletionStatus completionStatus) {
        return this.extractObjectFailed(completionStatus, null);
    }
    
    public BAD_OPERATION extractObjectFailed(final Throwable t) {
        return this.extractObjectFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION extractObjectFailed() {
        return this.extractObjectFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION methodNotFoundInTie(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079720, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.methodNotFoundInTie", new Object[] { o, o2 }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION methodNotFoundInTie(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.methodNotFoundInTie(completionStatus, null, o, o2);
    }
    
    public BAD_OPERATION methodNotFoundInTie(final Throwable t, final Object o, final Object o2) {
        return this.methodNotFoundInTie(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public BAD_OPERATION methodNotFoundInTie(final Object o, final Object o2) {
        return this.methodNotFoundInTie(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public BAD_OPERATION classNotFound1(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079721, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.classNotFound1", new Object[] { o }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION classNotFound1(final CompletionStatus completionStatus, final Object o) {
        return this.classNotFound1(completionStatus, null, o);
    }
    
    public BAD_OPERATION classNotFound1(final Throwable t, final Object o) {
        return this.classNotFound1(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_OPERATION classNotFound1(final Object o) {
        return this.classNotFound1(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_OPERATION classNotFound2(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079722, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.classNotFound2", new Object[] { o }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION classNotFound2(final CompletionStatus completionStatus, final Object o) {
        return this.classNotFound2(completionStatus, null, o);
    }
    
    public BAD_OPERATION classNotFound2(final Throwable t, final Object o) {
        return this.classNotFound2(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_OPERATION classNotFound2(final Object o) {
        return this.classNotFound2(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_OPERATION classNotFound3(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079723, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.classNotFound3", new Object[] { o }, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION classNotFound3(final CompletionStatus completionStatus, final Object o) {
        return this.classNotFound3(completionStatus, null, o);
    }
    
    public BAD_OPERATION classNotFound3(final Throwable t, final Object o) {
        return this.classNotFound3(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_OPERATION classNotFound3(final Object o) {
        return this.classNotFound3(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_OPERATION getDelegateServantNotActive(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079724, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getDelegateServantNotActive", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION getDelegateServantNotActive(final CompletionStatus completionStatus) {
        return this.getDelegateServantNotActive(completionStatus, null);
    }
    
    public BAD_OPERATION getDelegateServantNotActive(final Throwable t) {
        return this.getDelegateServantNotActive(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION getDelegateServantNotActive() {
        return this.getDelegateServantNotActive(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION getDelegateWrongPolicy(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079725, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getDelegateWrongPolicy", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION getDelegateWrongPolicy(final CompletionStatus completionStatus) {
        return this.getDelegateWrongPolicy(completionStatus, null);
    }
    
    public BAD_OPERATION getDelegateWrongPolicy(final Throwable t) {
        return this.getDelegateWrongPolicy(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION getDelegateWrongPolicy() {
        return this.getDelegateWrongPolicy(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION setDelegateRequiresStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079726, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.setDelegateRequiresStub", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION setDelegateRequiresStub(final CompletionStatus completionStatus) {
        return this.setDelegateRequiresStub(completionStatus, null);
    }
    
    public BAD_OPERATION setDelegateRequiresStub(final Throwable t) {
        return this.setDelegateRequiresStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION setDelegateRequiresStub() {
        return this.setDelegateRequiresStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION getDelegateRequiresStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079727, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getDelegateRequiresStub", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION getDelegateRequiresStub(final CompletionStatus completionStatus) {
        return this.getDelegateRequiresStub(completionStatus, null);
    }
    
    public BAD_OPERATION getDelegateRequiresStub(final Throwable t) {
        return this.getDelegateRequiresStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION getDelegateRequiresStub() {
        return this.getDelegateRequiresStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION getTypeIdsRequiresStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079728, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getTypeIdsRequiresStub", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION getTypeIdsRequiresStub(final CompletionStatus completionStatus) {
        return this.getTypeIdsRequiresStub(completionStatus, null);
    }
    
    public BAD_OPERATION getTypeIdsRequiresStub(final Throwable t) {
        return this.getTypeIdsRequiresStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION getTypeIdsRequiresStub() {
        return this.getTypeIdsRequiresStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION getOrbRequiresStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079729, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getOrbRequiresStub", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION getOrbRequiresStub(final CompletionStatus completionStatus) {
        return this.getOrbRequiresStub(completionStatus, null);
    }
    
    public BAD_OPERATION getOrbRequiresStub(final Throwable t) {
        return this.getOrbRequiresStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION getOrbRequiresStub() {
        return this.getOrbRequiresStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION connectRequiresStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079730, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.connectRequiresStub", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION connectRequiresStub(final CompletionStatus completionStatus) {
        return this.connectRequiresStub(completionStatus, null);
    }
    
    public BAD_OPERATION connectRequiresStub(final Throwable t) {
        return this.connectRequiresStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION connectRequiresStub() {
        return this.connectRequiresStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION isLocalRequiresStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079731, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.isLocalRequiresStub", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION isLocalRequiresStub(final CompletionStatus completionStatus) {
        return this.isLocalRequiresStub(completionStatus, null);
    }
    
    public BAD_OPERATION isLocalRequiresStub(final Throwable t) {
        return this.isLocalRequiresStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION isLocalRequiresStub() {
        return this.isLocalRequiresStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION requestRequiresStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079732, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.requestRequiresStub", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION requestRequiresStub(final CompletionStatus completionStatus) {
        return this.requestRequiresStub(completionStatus, null);
    }
    
    public BAD_OPERATION requestRequiresStub(final Throwable t) {
        return this.requestRequiresStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION requestRequiresStub() {
        return this.requestRequiresStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION badActivateTieCall(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079733, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badActivateTieCall", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION badActivateTieCall(final CompletionStatus completionStatus) {
        return this.badActivateTieCall(completionStatus, null);
    }
    
    public BAD_OPERATION badActivateTieCall(final Throwable t) {
        return this.badActivateTieCall(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION badActivateTieCall() {
        return this.badActivateTieCall(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION ioExceptionOnClose(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398079734, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.ioExceptionOnClose", null, ORBUtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION ioExceptionOnClose(final CompletionStatus completionStatus) {
        return this.ioExceptionOnClose(completionStatus, null);
    }
    
    public BAD_OPERATION ioExceptionOnClose(final Throwable t) {
        return this.ioExceptionOnClose(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION ioExceptionOnClose() {
        return this.ioExceptionOnClose(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM nullParam(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079689, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.nullParam", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM nullParam(final CompletionStatus completionStatus) {
        return this.nullParam(completionStatus, null);
    }
    
    public BAD_PARAM nullParam(final Throwable t) {
        return this.nullParam(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM nullParam() {
        return this.nullParam(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM unableFindValueFactory(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079690, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.unableFindValueFactory", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM unableFindValueFactory(final CompletionStatus completionStatus) {
        return this.unableFindValueFactory(completionStatus, null);
    }
    
    public BAD_PARAM unableFindValueFactory(final Throwable t) {
        return this.unableFindValueFactory(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM unableFindValueFactory() {
        return this.unableFindValueFactory(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM abstractFromNonAbstract(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079691, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.abstractFromNonAbstract", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM abstractFromNonAbstract(final CompletionStatus completionStatus) {
        return this.abstractFromNonAbstract(completionStatus, null);
    }
    
    public BAD_PARAM abstractFromNonAbstract(final Throwable t) {
        return this.abstractFromNonAbstract(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM abstractFromNonAbstract() {
        return this.abstractFromNonAbstract(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidTaggedProfile(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079692, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidTaggedProfile", null, ORBUtilSystemException.class, bad_PARAM);
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
    
    public BAD_PARAM objrefFromForeignOrb(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079693, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.objrefFromForeignOrb", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM objrefFromForeignOrb(final CompletionStatus completionStatus) {
        return this.objrefFromForeignOrb(completionStatus, null);
    }
    
    public BAD_PARAM objrefFromForeignOrb(final Throwable t) {
        return this.objrefFromForeignOrb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM objrefFromForeignOrb() {
        return this.objrefFromForeignOrb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM localObjectNotAllowed(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079694, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.localObjectNotAllowed", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM localObjectNotAllowed(final CompletionStatus completionStatus) {
        return this.localObjectNotAllowed(completionStatus, null);
    }
    
    public BAD_PARAM localObjectNotAllowed(final Throwable t) {
        return this.localObjectNotAllowed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM localObjectNotAllowed() {
        return this.localObjectNotAllowed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM nullObjectReference(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079695, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.nullObjectReference", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM nullObjectReference(final CompletionStatus completionStatus) {
        return this.nullObjectReference(completionStatus, null);
    }
    
    public BAD_PARAM nullObjectReference(final Throwable t) {
        return this.nullObjectReference(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM nullObjectReference() {
        return this.nullObjectReference(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM couldNotLoadClass(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079696, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotLoadClass", new Object[] { o }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM couldNotLoadClass(final CompletionStatus completionStatus, final Object o) {
        return this.couldNotLoadClass(completionStatus, null, o);
    }
    
    public BAD_PARAM couldNotLoadClass(final Throwable t, final Object o) {
        return this.couldNotLoadClass(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM couldNotLoadClass(final Object o) {
        return this.couldNotLoadClass(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM badUrl(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079697, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badUrl", new Object[] { o }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM badUrl(final CompletionStatus completionStatus, final Object o) {
        return this.badUrl(completionStatus, null, o);
    }
    
    public BAD_PARAM badUrl(final Throwable t, final Object o) {
        return this.badUrl(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM badUrl(final Object o) {
        return this.badUrl(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM fieldNotFound(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079698, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.fieldNotFound", new Object[] { o }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM fieldNotFound(final CompletionStatus completionStatus, final Object o) {
        return this.fieldNotFound(completionStatus, null, o);
    }
    
    public BAD_PARAM fieldNotFound(final Throwable t, final Object o) {
        return this.fieldNotFound(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM fieldNotFound(final Object o) {
        return this.fieldNotFound(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM errorSettingField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079699, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.errorSettingField", new Object[] { o, o2 }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM errorSettingField(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.errorSettingField(completionStatus, null, o, o2);
    }
    
    public BAD_PARAM errorSettingField(final Throwable t, final Object o, final Object o2) {
        return this.errorSettingField(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public BAD_PARAM errorSettingField(final Object o, final Object o2) {
        return this.errorSettingField(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public BAD_PARAM boundsErrorInDiiRequest(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079700, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.boundsErrorInDiiRequest", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM boundsErrorInDiiRequest(final CompletionStatus completionStatus) {
        return this.boundsErrorInDiiRequest(completionStatus, null);
    }
    
    public BAD_PARAM boundsErrorInDiiRequest(final Throwable t) {
        return this.boundsErrorInDiiRequest(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM boundsErrorInDiiRequest() {
        return this.boundsErrorInDiiRequest(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM persistentServerInitError(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079701, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.persistentServerInitError", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM persistentServerInitError(final CompletionStatus completionStatus) {
        return this.persistentServerInitError(completionStatus, null);
    }
    
    public BAD_PARAM persistentServerInitError(final Throwable t) {
        return this.persistentServerInitError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM persistentServerInitError() {
        return this.persistentServerInitError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM couldNotCreateArray(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079702, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotCreateArray", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM couldNotCreateArray(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.couldNotCreateArray(completionStatus, null, o, o2, o3);
    }
    
    public BAD_PARAM couldNotCreateArray(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.couldNotCreateArray(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public BAD_PARAM couldNotCreateArray(final Object o, final Object o2, final Object o3) {
        return this.couldNotCreateArray(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public BAD_PARAM couldNotSetArray(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079703, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotSetArray", new Object[] { o, o2, o3, o4, o5 }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM couldNotSetArray(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return this.couldNotSetArray(completionStatus, null, o, o2, o3, o4, o5);
    }
    
    public BAD_PARAM couldNotSetArray(final Throwable t, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return this.couldNotSetArray(CompletionStatus.COMPLETED_NO, t, o, o2, o3, o4, o5);
    }
    
    public BAD_PARAM couldNotSetArray(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return this.couldNotSetArray(CompletionStatus.COMPLETED_NO, null, o, o2, o3, o4, o5);
    }
    
    public BAD_PARAM illegalBootstrapOperation(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079704, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalBootstrapOperation", new Object[] { o }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM illegalBootstrapOperation(final CompletionStatus completionStatus, final Object o) {
        return this.illegalBootstrapOperation(completionStatus, null, o);
    }
    
    public BAD_PARAM illegalBootstrapOperation(final Throwable t, final Object o) {
        return this.illegalBootstrapOperation(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM illegalBootstrapOperation(final Object o) {
        return this.illegalBootstrapOperation(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM bootstrapRuntimeException(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079705, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.bootstrapRuntimeException", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM bootstrapRuntimeException(final CompletionStatus completionStatus) {
        return this.bootstrapRuntimeException(completionStatus, null);
    }
    
    public BAD_PARAM bootstrapRuntimeException(final Throwable t) {
        return this.bootstrapRuntimeException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM bootstrapRuntimeException() {
        return this.bootstrapRuntimeException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM bootstrapException(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079706, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.bootstrapException", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM bootstrapException(final CompletionStatus completionStatus) {
        return this.bootstrapException(completionStatus, null);
    }
    
    public BAD_PARAM bootstrapException(final Throwable t) {
        return this.bootstrapException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM bootstrapException() {
        return this.bootstrapException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM stringExpected(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079707, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.stringExpected", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM stringExpected(final CompletionStatus completionStatus) {
        return this.stringExpected(completionStatus, null);
    }
    
    public BAD_PARAM stringExpected(final Throwable t) {
        return this.stringExpected(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM stringExpected() {
        return this.stringExpected(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidTypecodeKind(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079708, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidTypecodeKind", new Object[] { o }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidTypecodeKind(final CompletionStatus completionStatus, final Object o) {
        return this.invalidTypecodeKind(completionStatus, null, o);
    }
    
    public BAD_PARAM invalidTypecodeKind(final Throwable t, final Object o) {
        return this.invalidTypecodeKind(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM invalidTypecodeKind(final Object o) {
        return this.invalidTypecodeKind(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079709, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.socketFactoryAndContactInfoListAtSameTime", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(final CompletionStatus completionStatus) {
        return this.socketFactoryAndContactInfoListAtSameTime(completionStatus, null);
    }
    
    public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(final Throwable t) {
        return this.socketFactoryAndContactInfoListAtSameTime(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM socketFactoryAndContactInfoListAtSameTime() {
        return this.socketFactoryAndContactInfoListAtSameTime(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079710, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.acceptorsAndLegacySocketFactoryAtSameTime", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(final CompletionStatus completionStatus) {
        return this.acceptorsAndLegacySocketFactoryAtSameTime(completionStatus, null);
    }
    
    public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(final Throwable t) {
        return this.acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime() {
        return this.acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM badOrbForServant(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079711, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badOrbForServant", null, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM badOrbForServant(final CompletionStatus completionStatus) {
        return this.badOrbForServant(completionStatus, null);
    }
    
    public BAD_PARAM badOrbForServant(final Throwable t) {
        return this.badOrbForServant(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM badOrbForServant() {
        return this.badOrbForServant(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM invalidRequestPartitioningPolicyValue(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079712, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningPolicyValue", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidRequestPartitioningPolicyValue(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningPolicyValue(completionStatus, null, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningPolicyValue(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningPolicyValue(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningPolicyValue(final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningPolicyValue(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningComponentValue(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079713, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningComponentValue", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidRequestPartitioningComponentValue(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningComponentValue(completionStatus, null, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningComponentValue(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningComponentValue(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningComponentValue(final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningComponentValue(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningId(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079714, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningId", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM invalidRequestPartitioningId(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningId(completionStatus, null, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningId(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningId(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public BAD_PARAM invalidRequestPartitioningId(final Object o, final Object o2, final Object o3) {
        return this.invalidRequestPartitioningId(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public BAD_PARAM errorInSettingDynamicStubFactoryFactory(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398079715, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.errorInSettingDynamicStubFactoryFactory", new Object[] { o }, ORBUtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM errorInSettingDynamicStubFactoryFactory(final CompletionStatus completionStatus, final Object o) {
        return this.errorInSettingDynamicStubFactoryFactory(completionStatus, null, o);
    }
    
    public BAD_PARAM errorInSettingDynamicStubFactoryFactory(final Throwable t, final Object o) {
        return this.errorInSettingDynamicStubFactoryFactory(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM errorInSettingDynamicStubFactoryFactory(final Object o) {
        return this.errorInSettingDynamicStubFactoryFactory(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public BAD_INV_ORDER dsimethodNotcalled(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079689, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.dsimethodNotcalled", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER dsimethodNotcalled(final CompletionStatus completionStatus) {
        return this.dsimethodNotcalled(completionStatus, null);
    }
    
    public BAD_INV_ORDER dsimethodNotcalled(final Throwable t) {
        return this.dsimethodNotcalled(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER dsimethodNotcalled() {
        return this.dsimethodNotcalled(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER argumentsCalledMultiple(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079690, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.argumentsCalledMultiple", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER argumentsCalledMultiple(final CompletionStatus completionStatus) {
        return this.argumentsCalledMultiple(completionStatus, null);
    }
    
    public BAD_INV_ORDER argumentsCalledMultiple(final Throwable t) {
        return this.argumentsCalledMultiple(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER argumentsCalledMultiple() {
        return this.argumentsCalledMultiple(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER argumentsCalledAfterException(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079691, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.argumentsCalledAfterException", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER argumentsCalledAfterException(final CompletionStatus completionStatus) {
        return this.argumentsCalledAfterException(completionStatus, null);
    }
    
    public BAD_INV_ORDER argumentsCalledAfterException(final Throwable t) {
        return this.argumentsCalledAfterException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER argumentsCalledAfterException() {
        return this.argumentsCalledAfterException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER argumentsCalledNullArgs(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079692, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.argumentsCalledNullArgs", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER argumentsCalledNullArgs(final CompletionStatus completionStatus) {
        return this.argumentsCalledNullArgs(completionStatus, null);
    }
    
    public BAD_INV_ORDER argumentsCalledNullArgs(final Throwable t) {
        return this.argumentsCalledNullArgs(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER argumentsCalledNullArgs() {
        return this.argumentsCalledNullArgs(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER argumentsNotCalled(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079693, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.argumentsNotCalled", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER argumentsNotCalled(final CompletionStatus completionStatus) {
        return this.argumentsNotCalled(completionStatus, null);
    }
    
    public BAD_INV_ORDER argumentsNotCalled(final Throwable t) {
        return this.argumentsNotCalled(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER argumentsNotCalled() {
        return this.argumentsNotCalled(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER setResultCalledMultiple(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079694, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.setResultCalledMultiple", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER setResultCalledMultiple(final CompletionStatus completionStatus) {
        return this.setResultCalledMultiple(completionStatus, null);
    }
    
    public BAD_INV_ORDER setResultCalledMultiple(final Throwable t) {
        return this.setResultCalledMultiple(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER setResultCalledMultiple() {
        return this.setResultCalledMultiple(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER setResultAfterException(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079695, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.setResultAfterException", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER setResultAfterException(final CompletionStatus completionStatus) {
        return this.setResultAfterException(completionStatus, null);
    }
    
    public BAD_INV_ORDER setResultAfterException(final Throwable t) {
        return this.setResultAfterException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER setResultAfterException() {
        return this.setResultAfterException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_INV_ORDER setResultCalledNullArgs(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_INV_ORDER bad_INV_ORDER = new BAD_INV_ORDER(1398079696, completionStatus);
        if (t != null) {
            bad_INV_ORDER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.setResultCalledNullArgs", null, ORBUtilSystemException.class, bad_INV_ORDER);
        }
        return bad_INV_ORDER;
    }
    
    public BAD_INV_ORDER setResultCalledNullArgs(final CompletionStatus completionStatus) {
        return this.setResultCalledNullArgs(completionStatus, null);
    }
    
    public BAD_INV_ORDER setResultCalledNullArgs(final Throwable t) {
        return this.setResultCalledNullArgs(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_INV_ORDER setResultCalledNullArgs() {
        return this.setResultCalledNullArgs(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_TYPECODE badRemoteTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_TYPECODE bad_TYPECODE = new BAD_TYPECODE(1398079689, completionStatus);
        if (t != null) {
            bad_TYPECODE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badRemoteTypecode", null, ORBUtilSystemException.class, bad_TYPECODE);
        }
        return bad_TYPECODE;
    }
    
    public BAD_TYPECODE badRemoteTypecode(final CompletionStatus completionStatus) {
        return this.badRemoteTypecode(completionStatus, null);
    }
    
    public BAD_TYPECODE badRemoteTypecode(final Throwable t) {
        return this.badRemoteTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_TYPECODE badRemoteTypecode() {
        return this.badRemoteTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_TYPECODE unresolvedRecursiveTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_TYPECODE bad_TYPECODE = new BAD_TYPECODE(1398079690, completionStatus);
        if (t != null) {
            bad_TYPECODE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unresolvedRecursiveTypecode", null, ORBUtilSystemException.class, bad_TYPECODE);
        }
        return bad_TYPECODE;
    }
    
    public BAD_TYPECODE unresolvedRecursiveTypecode(final CompletionStatus completionStatus) {
        return this.unresolvedRecursiveTypecode(completionStatus, null);
    }
    
    public BAD_TYPECODE unresolvedRecursiveTypecode(final Throwable t) {
        return this.unresolvedRecursiveTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_TYPECODE unresolvedRecursiveTypecode() {
        return this.unresolvedRecursiveTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE connectFailure(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079689, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.connectFailure", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE connectFailure(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.connectFailure(completionStatus, null, o, o2, o3);
    }
    
    public COMM_FAILURE connectFailure(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.connectFailure(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public COMM_FAILURE connectFailure(final Object o, final Object o2, final Object o3) {
        return this.connectFailure(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public COMM_FAILURE connectionCloseRebind(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079690, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.connectionCloseRebind", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE connectionCloseRebind(final CompletionStatus completionStatus) {
        return this.connectionCloseRebind(completionStatus, null);
    }
    
    public COMM_FAILURE connectionCloseRebind(final Throwable t) {
        return this.connectionCloseRebind(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE connectionCloseRebind() {
        return this.connectionCloseRebind(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE writeErrorSend(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079691, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.writeErrorSend", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE writeErrorSend(final CompletionStatus completionStatus) {
        return this.writeErrorSend(completionStatus, null);
    }
    
    public COMM_FAILURE writeErrorSend(final Throwable t) {
        return this.writeErrorSend(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE writeErrorSend() {
        return this.writeErrorSend(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE getPropertiesError(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079692, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getPropertiesError", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE getPropertiesError(final CompletionStatus completionStatus) {
        return this.getPropertiesError(completionStatus, null);
    }
    
    public COMM_FAILURE getPropertiesError(final Throwable t) {
        return this.getPropertiesError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE getPropertiesError() {
        return this.getPropertiesError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE bootstrapServerNotAvail(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079693, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.bootstrapServerNotAvail", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE bootstrapServerNotAvail(final CompletionStatus completionStatus) {
        return this.bootstrapServerNotAvail(completionStatus, null);
    }
    
    public COMM_FAILURE bootstrapServerNotAvail(final Throwable t) {
        return this.bootstrapServerNotAvail(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE bootstrapServerNotAvail() {
        return this.bootstrapServerNotAvail(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE invokeError(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079694, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invokeError", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE invokeError(final CompletionStatus completionStatus) {
        return this.invokeError(completionStatus, null);
    }
    
    public COMM_FAILURE invokeError(final Throwable t) {
        return this.invokeError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE invokeError() {
        return this.invokeError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079695, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.defaultCreateServerSocketGivenNonIiopClearText", new Object[] { o }, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(final CompletionStatus completionStatus, final Object o) {
        return this.defaultCreateServerSocketGivenNonIiopClearText(completionStatus, null, o);
    }
    
    public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(final Throwable t, final Object o) {
        return this.defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(final Object o) {
        return this.defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public COMM_FAILURE connectionAbort(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079696, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.connectionAbort", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE connectionAbort(final CompletionStatus completionStatus) {
        return this.connectionAbort(completionStatus, null);
    }
    
    public COMM_FAILURE connectionAbort(final Throwable t) {
        return this.connectionAbort(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE connectionAbort() {
        return this.connectionAbort(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE connectionRebind(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079697, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.connectionRebind", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE connectionRebind(final CompletionStatus completionStatus) {
        return this.connectionRebind(completionStatus, null);
    }
    
    public COMM_FAILURE connectionRebind(final Throwable t) {
        return this.connectionRebind(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE connectionRebind() {
        return this.connectionRebind(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE recvMsgError(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079698, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.recvMsgError", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE recvMsgError(final CompletionStatus completionStatus) {
        return this.recvMsgError(completionStatus, null);
    }
    
    public COMM_FAILURE recvMsgError(final Throwable t) {
        return this.recvMsgError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE recvMsgError() {
        return this.recvMsgError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE ioexceptionWhenReadingConnection(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079699, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.ioexceptionWhenReadingConnection", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE ioexceptionWhenReadingConnection(final CompletionStatus completionStatus) {
        return this.ioexceptionWhenReadingConnection(completionStatus, null);
    }
    
    public COMM_FAILURE ioexceptionWhenReadingConnection(final Throwable t) {
        return this.ioexceptionWhenReadingConnection(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE ioexceptionWhenReadingConnection() {
        return this.ioexceptionWhenReadingConnection(CompletionStatus.COMPLETED_NO, null);
    }
    
    public COMM_FAILURE selectionKeyInvalid(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079700, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.selectionKeyInvalid", new Object[] { o }, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE selectionKeyInvalid(final CompletionStatus completionStatus, final Object o) {
        return this.selectionKeyInvalid(completionStatus, null, o);
    }
    
    public COMM_FAILURE selectionKeyInvalid(final Throwable t, final Object o) {
        return this.selectionKeyInvalid(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public COMM_FAILURE selectionKeyInvalid(final Object o) {
        return this.selectionKeyInvalid(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public COMM_FAILURE exceptionInAccept(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079701, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.exceptionInAccept", new Object[] { o }, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE exceptionInAccept(final CompletionStatus completionStatus, final Object o) {
        return this.exceptionInAccept(completionStatus, null, o);
    }
    
    public COMM_FAILURE exceptionInAccept(final Throwable t, final Object o) {
        return this.exceptionInAccept(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public COMM_FAILURE exceptionInAccept(final Object o) {
        return this.exceptionInAccept(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public COMM_FAILURE securityExceptionInAccept(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079702, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.securityExceptionInAccept", new Object[] { o, o2 }, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE securityExceptionInAccept(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.securityExceptionInAccept(completionStatus, null, o, o2);
    }
    
    public COMM_FAILURE securityExceptionInAccept(final Throwable t, final Object o, final Object o2) {
        return this.securityExceptionInAccept(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public COMM_FAILURE securityExceptionInAccept(final Object o, final Object o2) {
        return this.securityExceptionInAccept(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public COMM_FAILURE transportReadTimeoutExceeded(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3, final Object o4) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079703, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.transportReadTimeoutExceeded", new Object[] { o, o2, o3, o4 }, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE transportReadTimeoutExceeded(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3, final Object o4) {
        return this.transportReadTimeoutExceeded(completionStatus, null, o, o2, o3, o4);
    }
    
    public COMM_FAILURE transportReadTimeoutExceeded(final Throwable t, final Object o, final Object o2, final Object o3, final Object o4) {
        return this.transportReadTimeoutExceeded(CompletionStatus.COMPLETED_NO, t, o, o2, o3, o4);
    }
    
    public COMM_FAILURE transportReadTimeoutExceeded(final Object o, final Object o2, final Object o3, final Object o4) {
        return this.transportReadTimeoutExceeded(CompletionStatus.COMPLETED_NO, null, o, o2, o3, o4);
    }
    
    public COMM_FAILURE createListenerFailed(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079704, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.doLog(Level.SEVERE, "ORBUTIL.createListenerFailed", new Object[] { o }, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE createListenerFailed(final CompletionStatus completionStatus, final Object o) {
        return this.createListenerFailed(completionStatus, null, o);
    }
    
    public COMM_FAILURE createListenerFailed(final Throwable t, final Object o) {
        return this.createListenerFailed(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public COMM_FAILURE createListenerFailed(final Object o) {
        return this.createListenerFailed(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public COMM_FAILURE bufferReadManagerTimeout(final CompletionStatus completionStatus, final Throwable t) {
        final COMM_FAILURE comm_FAILURE = new COMM_FAILURE(1398079705, completionStatus);
        if (t != null) {
            comm_FAILURE.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.bufferReadManagerTimeout", null, ORBUtilSystemException.class, comm_FAILURE);
        }
        return comm_FAILURE;
    }
    
    public COMM_FAILURE bufferReadManagerTimeout(final CompletionStatus completionStatus) {
        return this.bufferReadManagerTimeout(completionStatus, null);
    }
    
    public COMM_FAILURE bufferReadManagerTimeout(final Throwable t) {
        return this.bufferReadManagerTimeout(CompletionStatus.COMPLETED_NO, t);
    }
    
    public COMM_FAILURE bufferReadManagerTimeout() {
        return this.bufferReadManagerTimeout(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badStringifiedIorLen(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079689, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badStringifiedIorLen", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badStringifiedIorLen(final CompletionStatus completionStatus) {
        return this.badStringifiedIorLen(completionStatus, null);
    }
    
    public DATA_CONVERSION badStringifiedIorLen(final Throwable t) {
        return this.badStringifiedIorLen(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION badStringifiedIorLen() {
        return this.badStringifiedIorLen(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badStringifiedIor(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079690, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badStringifiedIor", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badStringifiedIor(final CompletionStatus completionStatus) {
        return this.badStringifiedIor(completionStatus, null);
    }
    
    public DATA_CONVERSION badStringifiedIor(final Throwable t) {
        return this.badStringifiedIor(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION badStringifiedIor() {
        return this.badStringifiedIor(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badModifier(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079691, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badModifier", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badModifier(final CompletionStatus completionStatus) {
        return this.badModifier(completionStatus, null);
    }
    
    public DATA_CONVERSION badModifier(final Throwable t) {
        return this.badModifier(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION badModifier() {
        return this.badModifier(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION codesetIncompatible(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079692, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.codesetIncompatible", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION codesetIncompatible(final CompletionStatus completionStatus) {
        return this.codesetIncompatible(completionStatus, null);
    }
    
    public DATA_CONVERSION codesetIncompatible(final Throwable t) {
        return this.codesetIncompatible(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION codesetIncompatible() {
        return this.codesetIncompatible(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badHexDigit(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079693, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badHexDigit", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badHexDigit(final CompletionStatus completionStatus) {
        return this.badHexDigit(completionStatus, null);
    }
    
    public DATA_CONVERSION badHexDigit(final Throwable t) {
        return this.badHexDigit(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION badHexDigit() {
        return this.badHexDigit(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badUnicodePair(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079694, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badUnicodePair", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badUnicodePair(final CompletionStatus completionStatus) {
        return this.badUnicodePair(completionStatus, null);
    }
    
    public DATA_CONVERSION badUnicodePair(final Throwable t) {
        return this.badUnicodePair(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION badUnicodePair() {
        return this.badUnicodePair(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION btcResultMoreThanOneChar(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079695, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.btcResultMoreThanOneChar", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION btcResultMoreThanOneChar(final CompletionStatus completionStatus) {
        return this.btcResultMoreThanOneChar(completionStatus, null);
    }
    
    public DATA_CONVERSION btcResultMoreThanOneChar(final Throwable t) {
        return this.btcResultMoreThanOneChar(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION btcResultMoreThanOneChar() {
        return this.btcResultMoreThanOneChar(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badCodesetsFromClient(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079696, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCodesetsFromClient", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badCodesetsFromClient(final CompletionStatus completionStatus) {
        return this.badCodesetsFromClient(completionStatus, null);
    }
    
    public DATA_CONVERSION badCodesetsFromClient(final Throwable t) {
        return this.badCodesetsFromClient(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION badCodesetsFromClient() {
        return this.badCodesetsFromClient(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION invalidSingleCharCtb(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079697, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidSingleCharCtb", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION invalidSingleCharCtb(final CompletionStatus completionStatus) {
        return this.invalidSingleCharCtb(completionStatus, null);
    }
    
    public DATA_CONVERSION invalidSingleCharCtb(final Throwable t) {
        return this.invalidSingleCharCtb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION invalidSingleCharCtb() {
        return this.invalidSingleCharCtb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badGiop11Ctb(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079698, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badGiop11Ctb", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badGiop11Ctb(final CompletionStatus completionStatus) {
        return this.badGiop11Ctb(completionStatus, null);
    }
    
    public DATA_CONVERSION badGiop11Ctb(final Throwable t) {
        return this.badGiop11Ctb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION badGiop11Ctb() {
        return this.badGiop11Ctb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION badSequenceBounds(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079700, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badSequenceBounds", new Object[] { o, o2 }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badSequenceBounds(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.badSequenceBounds(completionStatus, null, o, o2);
    }
    
    public DATA_CONVERSION badSequenceBounds(final Throwable t, final Object o, final Object o2) {
        return this.badSequenceBounds(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public DATA_CONVERSION badSequenceBounds(final Object o, final Object o2) {
        return this.badSequenceBounds(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public DATA_CONVERSION illegalSocketFactoryType(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079701, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalSocketFactoryType", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION illegalSocketFactoryType(final CompletionStatus completionStatus, final Object o) {
        return this.illegalSocketFactoryType(completionStatus, null, o);
    }
    
    public DATA_CONVERSION illegalSocketFactoryType(final Throwable t, final Object o) {
        return this.illegalSocketFactoryType(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION illegalSocketFactoryType(final Object o) {
        return this.illegalSocketFactoryType(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION badCustomSocketFactory(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079702, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCustomSocketFactory", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badCustomSocketFactory(final CompletionStatus completionStatus, final Object o) {
        return this.badCustomSocketFactory(completionStatus, null, o);
    }
    
    public DATA_CONVERSION badCustomSocketFactory(final Throwable t, final Object o) {
        return this.badCustomSocketFactory(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION badCustomSocketFactory(final Object o) {
        return this.badCustomSocketFactory(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION fragmentSizeMinimum(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079703, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.fragmentSizeMinimum", new Object[] { o, o2 }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION fragmentSizeMinimum(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.fragmentSizeMinimum(completionStatus, null, o, o2);
    }
    
    public DATA_CONVERSION fragmentSizeMinimum(final Throwable t, final Object o, final Object o2) {
        return this.fragmentSizeMinimum(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public DATA_CONVERSION fragmentSizeMinimum(final Object o, final Object o2) {
        return this.fragmentSizeMinimum(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public DATA_CONVERSION fragmentSizeDiv(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079704, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.fragmentSizeDiv", new Object[] { o, o2 }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION fragmentSizeDiv(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.fragmentSizeDiv(completionStatus, null, o, o2);
    }
    
    public DATA_CONVERSION fragmentSizeDiv(final Throwable t, final Object o, final Object o2) {
        return this.fragmentSizeDiv(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public DATA_CONVERSION fragmentSizeDiv(final Object o, final Object o2) {
        return this.fragmentSizeDiv(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public DATA_CONVERSION orbInitializerFailure(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079705, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbInitializerFailure", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION orbInitializerFailure(final CompletionStatus completionStatus, final Object o) {
        return this.orbInitializerFailure(completionStatus, null, o);
    }
    
    public DATA_CONVERSION orbInitializerFailure(final Throwable t, final Object o) {
        return this.orbInitializerFailure(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION orbInitializerFailure(final Object o) {
        return this.orbInitializerFailure(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION orbInitializerType(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079706, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbInitializerType", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION orbInitializerType(final CompletionStatus completionStatus, final Object o) {
        return this.orbInitializerType(completionStatus, null, o);
    }
    
    public DATA_CONVERSION orbInitializerType(final Throwable t, final Object o) {
        return this.orbInitializerType(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION orbInitializerType(final Object o) {
        return this.orbInitializerType(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION orbInitialreferenceSyntax(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079707, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbInitialreferenceSyntax", null, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION orbInitialreferenceSyntax(final CompletionStatus completionStatus) {
        return this.orbInitialreferenceSyntax(completionStatus, null);
    }
    
    public DATA_CONVERSION orbInitialreferenceSyntax(final Throwable t) {
        return this.orbInitialreferenceSyntax(CompletionStatus.COMPLETED_NO, t);
    }
    
    public DATA_CONVERSION orbInitialreferenceSyntax() {
        return this.orbInitialreferenceSyntax(CompletionStatus.COMPLETED_NO, null);
    }
    
    public DATA_CONVERSION acceptorInstantiationFailure(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079708, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.acceptorInstantiationFailure", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION acceptorInstantiationFailure(final CompletionStatus completionStatus, final Object o) {
        return this.acceptorInstantiationFailure(completionStatus, null, o);
    }
    
    public DATA_CONVERSION acceptorInstantiationFailure(final Throwable t, final Object o) {
        return this.acceptorInstantiationFailure(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION acceptorInstantiationFailure(final Object o) {
        return this.acceptorInstantiationFailure(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION acceptorInstantiationTypeFailure(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079709, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.acceptorInstantiationTypeFailure", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION acceptorInstantiationTypeFailure(final CompletionStatus completionStatus, final Object o) {
        return this.acceptorInstantiationTypeFailure(completionStatus, null, o);
    }
    
    public DATA_CONVERSION acceptorInstantiationTypeFailure(final Throwable t, final Object o) {
        return this.acceptorInstantiationTypeFailure(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION acceptorInstantiationTypeFailure(final Object o) {
        return this.acceptorInstantiationTypeFailure(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION illegalContactInfoListFactoryType(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079710, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalContactInfoListFactoryType", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION illegalContactInfoListFactoryType(final CompletionStatus completionStatus, final Object o) {
        return this.illegalContactInfoListFactoryType(completionStatus, null, o);
    }
    
    public DATA_CONVERSION illegalContactInfoListFactoryType(final Throwable t, final Object o) {
        return this.illegalContactInfoListFactoryType(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION illegalContactInfoListFactoryType(final Object o) {
        return this.illegalContactInfoListFactoryType(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION badContactInfoListFactory(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079711, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badContactInfoListFactory", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badContactInfoListFactory(final CompletionStatus completionStatus, final Object o) {
        return this.badContactInfoListFactory(completionStatus, null, o);
    }
    
    public DATA_CONVERSION badContactInfoListFactory(final Throwable t, final Object o) {
        return this.badContactInfoListFactory(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION badContactInfoListFactory(final Object o) {
        return this.badContactInfoListFactory(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION illegalIorToSocketInfoType(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079712, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalIorToSocketInfoType", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION illegalIorToSocketInfoType(final CompletionStatus completionStatus, final Object o) {
        return this.illegalIorToSocketInfoType(completionStatus, null, o);
    }
    
    public DATA_CONVERSION illegalIorToSocketInfoType(final Throwable t, final Object o) {
        return this.illegalIorToSocketInfoType(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION illegalIorToSocketInfoType(final Object o) {
        return this.illegalIorToSocketInfoType(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION badCustomIorToSocketInfo(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079713, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCustomIorToSocketInfo", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badCustomIorToSocketInfo(final CompletionStatus completionStatus, final Object o) {
        return this.badCustomIorToSocketInfo(completionStatus, null, o);
    }
    
    public DATA_CONVERSION badCustomIorToSocketInfo(final Throwable t, final Object o) {
        return this.badCustomIorToSocketInfo(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION badCustomIorToSocketInfo(final Object o) {
        return this.badCustomIorToSocketInfo(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079714, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalIiopPrimaryToContactInfoType", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(final CompletionStatus completionStatus, final Object o) {
        return this.illegalIiopPrimaryToContactInfoType(completionStatus, null, o);
    }
    
    public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(final Throwable t, final Object o) {
        return this.illegalIiopPrimaryToContactInfoType(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(final Object o) {
        return this.illegalIiopPrimaryToContactInfoType(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398079715, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCustomIiopPrimaryToContactInfo", new Object[] { o }, ORBUtilSystemException.class, data_CONVERSION);
        }
        return data_CONVERSION;
    }
    
    public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(final CompletionStatus completionStatus, final Object o) {
        return this.badCustomIiopPrimaryToContactInfo(completionStatus, null, o);
    }
    
    public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(final Throwable t, final Object o) {
        return this.badCustomIiopPrimaryToContactInfo(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(final Object o) {
        return this.badCustomIiopPrimaryToContactInfo(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INV_OBJREF badCorbalocString(final CompletionStatus completionStatus, final Throwable t) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1398079689, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCorbalocString", null, ORBUtilSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF badCorbalocString(final CompletionStatus completionStatus) {
        return this.badCorbalocString(completionStatus, null);
    }
    
    public INV_OBJREF badCorbalocString(final Throwable t) {
        return this.badCorbalocString(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_OBJREF badCorbalocString() {
        return this.badCorbalocString(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INV_OBJREF noProfilePresent(final CompletionStatus completionStatus, final Throwable t) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1398079690, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noProfilePresent", null, ORBUtilSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF noProfilePresent(final CompletionStatus completionStatus) {
        return this.noProfilePresent(completionStatus, null);
    }
    
    public INV_OBJREF noProfilePresent(final Throwable t) {
        return this.noProfilePresent(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INV_OBJREF noProfilePresent() {
        return this.noProfilePresent(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE cannotCreateOrbidDb(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079689, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.cannotCreateOrbidDb", null, ORBUtilSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE cannotCreateOrbidDb(final CompletionStatus completionStatus) {
        return this.cannotCreateOrbidDb(completionStatus, null);
    }
    
    public INITIALIZE cannotCreateOrbidDb(final Throwable t) {
        return this.cannotCreateOrbidDb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE cannotCreateOrbidDb() {
        return this.cannotCreateOrbidDb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE cannotReadOrbidDb(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079690, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.cannotReadOrbidDb", null, ORBUtilSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE cannotReadOrbidDb(final CompletionStatus completionStatus) {
        return this.cannotReadOrbidDb(completionStatus, null);
    }
    
    public INITIALIZE cannotReadOrbidDb(final Throwable t) {
        return this.cannotReadOrbidDb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE cannotReadOrbidDb() {
        return this.cannotReadOrbidDb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE cannotWriteOrbidDb(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079691, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.cannotWriteOrbidDb", null, ORBUtilSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE cannotWriteOrbidDb(final CompletionStatus completionStatus) {
        return this.cannotWriteOrbidDb(completionStatus, null);
    }
    
    public INITIALIZE cannotWriteOrbidDb(final Throwable t) {
        return this.cannotWriteOrbidDb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE cannotWriteOrbidDb() {
        return this.cannotWriteOrbidDb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079692, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getServerPortCalledBeforeEndpointsInitialized", null, ORBUtilSystemException.class, initialize);
        }
        return initialize;
    }
    
    public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(final CompletionStatus completionStatus) {
        return this.getServerPortCalledBeforeEndpointsInitialized(completionStatus, null);
    }
    
    public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(final Throwable t) {
        return this.getServerPortCalledBeforeEndpointsInitialized(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INITIALIZE getServerPortCalledBeforeEndpointsInitialized() {
        return this.getServerPortCalledBeforeEndpointsInitialized(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INITIALIZE persistentServerportNotSet(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079693, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.persistentServerportNotSet", null, ORBUtilSystemException.class, initialize);
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
    
    public INITIALIZE persistentServeridNotSet(final CompletionStatus completionStatus, final Throwable t) {
        final INITIALIZE initialize = new INITIALIZE(1398079694, completionStatus);
        if (t != null) {
            initialize.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.persistentServeridNotSet", null, ORBUtilSystemException.class, initialize);
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
    
    public INTERNAL nonExistentOrbid(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079689, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.nonExistentOrbid", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL nonExistentOrbid(final CompletionStatus completionStatus) {
        return this.nonExistentOrbid(completionStatus, null);
    }
    
    public INTERNAL nonExistentOrbid(final Throwable t) {
        return this.nonExistentOrbid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL nonExistentOrbid() {
        return this.nonExistentOrbid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL noServerSubcontract(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079690, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noServerSubcontract", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL noServerSubcontract(final CompletionStatus completionStatus) {
        return this.noServerSubcontract(completionStatus, null);
    }
    
    public INTERNAL noServerSubcontract(final Throwable t) {
        return this.noServerSubcontract(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL noServerSubcontract() {
        return this.noServerSubcontract(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL serverScTempSize(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079691, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.serverScTempSize", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL serverScTempSize(final CompletionStatus completionStatus) {
        return this.serverScTempSize(completionStatus, null);
    }
    
    public INTERNAL serverScTempSize(final Throwable t) {
        return this.serverScTempSize(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL serverScTempSize() {
        return this.serverScTempSize(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL noClientScClass(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079692, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noClientScClass", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL noClientScClass(final CompletionStatus completionStatus) {
        return this.noClientScClass(completionStatus, null);
    }
    
    public INTERNAL noClientScClass(final Throwable t) {
        return this.noClientScClass(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL noClientScClass() {
        return this.noClientScClass(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL serverScNoIiopProfile(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079693, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.serverScNoIiopProfile", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL serverScNoIiopProfile(final CompletionStatus completionStatus) {
        return this.serverScNoIiopProfile(completionStatus, null);
    }
    
    public INTERNAL serverScNoIiopProfile(final Throwable t) {
        return this.serverScNoIiopProfile(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL serverScNoIiopProfile() {
        return this.serverScNoIiopProfile(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL getSystemExReturnedNull(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079694, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getSystemExReturnedNull", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL getSystemExReturnedNull(final CompletionStatus completionStatus) {
        return this.getSystemExReturnedNull(completionStatus, null);
    }
    
    public INTERNAL getSystemExReturnedNull(final Throwable t) {
        return this.getSystemExReturnedNull(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL getSystemExReturnedNull() {
        return this.getSystemExReturnedNull(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL peekstringFailed(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079695, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.peekstringFailed", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL peekstringFailed(final CompletionStatus completionStatus) {
        return this.peekstringFailed(completionStatus, null);
    }
    
    public INTERNAL peekstringFailed(final Throwable t) {
        return this.peekstringFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL peekstringFailed() {
        return this.peekstringFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL getLocalHostFailed(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079696, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.getLocalHostFailed", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL getLocalHostFailed(final CompletionStatus completionStatus) {
        return this.getLocalHostFailed(completionStatus, null);
    }
    
    public INTERNAL getLocalHostFailed(final Throwable t) {
        return this.getLocalHostFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL getLocalHostFailed() {
        return this.getLocalHostFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badLocateRequestStatus(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079698, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badLocateRequestStatus", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badLocateRequestStatus(final CompletionStatus completionStatus) {
        return this.badLocateRequestStatus(completionStatus, null);
    }
    
    public INTERNAL badLocateRequestStatus(final Throwable t) {
        return this.badLocateRequestStatus(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badLocateRequestStatus() {
        return this.badLocateRequestStatus(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL stringifyWriteError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079699, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.stringifyWriteError", null, ORBUtilSystemException.class, internal);
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
    
    public INTERNAL badGiopRequestType(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079700, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badGiopRequestType", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badGiopRequestType(final CompletionStatus completionStatus) {
        return this.badGiopRequestType(completionStatus, null);
    }
    
    public INTERNAL badGiopRequestType(final Throwable t) {
        return this.badGiopRequestType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badGiopRequestType() {
        return this.badGiopRequestType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL errorUnmarshalingUserexc(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079701, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.errorUnmarshalingUserexc", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorUnmarshalingUserexc(final CompletionStatus completionStatus) {
        return this.errorUnmarshalingUserexc(completionStatus, null);
    }
    
    public INTERNAL errorUnmarshalingUserexc(final Throwable t) {
        return this.errorUnmarshalingUserexc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL errorUnmarshalingUserexc() {
        return this.errorUnmarshalingUserexc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL requestdispatcherregistryError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079702, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.requestdispatcherregistryError", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL requestdispatcherregistryError(final CompletionStatus completionStatus) {
        return this.requestdispatcherregistryError(completionStatus, null);
    }
    
    public INTERNAL requestdispatcherregistryError(final Throwable t) {
        return this.requestdispatcherregistryError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL requestdispatcherregistryError() {
        return this.requestdispatcherregistryError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL locationforwardError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079703, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.locationforwardError", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL locationforwardError(final CompletionStatus completionStatus) {
        return this.locationforwardError(completionStatus, null);
    }
    
    public INTERNAL locationforwardError(final Throwable t) {
        return this.locationforwardError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL locationforwardError() {
        return this.locationforwardError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL wrongClientsc(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079704, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.wrongClientsc", null, ORBUtilSystemException.class, internal);
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
    
    public INTERNAL badServantReadObject(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079705, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badServantReadObject", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badServantReadObject(final CompletionStatus completionStatus) {
        return this.badServantReadObject(completionStatus, null);
    }
    
    public INTERNAL badServantReadObject(final Throwable t) {
        return this.badServantReadObject(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badServantReadObject() {
        return this.badServantReadObject(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL multIiopProfNotSupported(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079706, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.multIiopProfNotSupported", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL multIiopProfNotSupported(final CompletionStatus completionStatus) {
        return this.multIiopProfNotSupported(completionStatus, null);
    }
    
    public INTERNAL multIiopProfNotSupported(final Throwable t) {
        return this.multIiopProfNotSupported(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL multIiopProfNotSupported() {
        return this.multIiopProfNotSupported(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL giopMagicError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079708, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.giopMagicError", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL giopMagicError(final CompletionStatus completionStatus) {
        return this.giopMagicError(completionStatus, null);
    }
    
    public INTERNAL giopMagicError(final Throwable t) {
        return this.giopMagicError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL giopMagicError() {
        return this.giopMagicError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL giopVersionError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079709, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.giopVersionError", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL giopVersionError(final CompletionStatus completionStatus) {
        return this.giopVersionError(completionStatus, null);
    }
    
    public INTERNAL giopVersionError(final Throwable t) {
        return this.giopVersionError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL giopVersionError() {
        return this.giopVersionError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL illegalReplyStatus(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079710, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalReplyStatus", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL illegalReplyStatus(final CompletionStatus completionStatus) {
        return this.illegalReplyStatus(completionStatus, null);
    }
    
    public INTERNAL illegalReplyStatus(final Throwable t) {
        return this.illegalReplyStatus(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL illegalReplyStatus() {
        return this.illegalReplyStatus(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL illegalGiopMsgType(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079711, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalGiopMsgType", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL illegalGiopMsgType(final CompletionStatus completionStatus) {
        return this.illegalGiopMsgType(completionStatus, null);
    }
    
    public INTERNAL illegalGiopMsgType(final Throwable t) {
        return this.illegalGiopMsgType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL illegalGiopMsgType() {
        return this.illegalGiopMsgType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL fragmentationDisallowed(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079712, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.fragmentationDisallowed", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL fragmentationDisallowed(final CompletionStatus completionStatus) {
        return this.fragmentationDisallowed(completionStatus, null);
    }
    
    public INTERNAL fragmentationDisallowed(final Throwable t) {
        return this.fragmentationDisallowed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL fragmentationDisallowed() {
        return this.fragmentationDisallowed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badReplystatus(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079713, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badReplystatus", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badReplystatus(final CompletionStatus completionStatus) {
        return this.badReplystatus(completionStatus, null);
    }
    
    public INTERNAL badReplystatus(final Throwable t) {
        return this.badReplystatus(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badReplystatus() {
        return this.badReplystatus(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL ctbConverterFailure(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079714, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.ctbConverterFailure", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL ctbConverterFailure(final CompletionStatus completionStatus) {
        return this.ctbConverterFailure(completionStatus, null);
    }
    
    public INTERNAL ctbConverterFailure(final Throwable t) {
        return this.ctbConverterFailure(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL ctbConverterFailure() {
        return this.ctbConverterFailure(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL btcConverterFailure(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079715, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.btcConverterFailure", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL btcConverterFailure(final CompletionStatus completionStatus) {
        return this.btcConverterFailure(completionStatus, null);
    }
    
    public INTERNAL btcConverterFailure(final Throwable t) {
        return this.btcConverterFailure(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL btcConverterFailure() {
        return this.btcConverterFailure(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL wcharArrayUnsupportedEncoding(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079716, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.wcharArrayUnsupportedEncoding", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL wcharArrayUnsupportedEncoding(final CompletionStatus completionStatus) {
        return this.wcharArrayUnsupportedEncoding(completionStatus, null);
    }
    
    public INTERNAL wcharArrayUnsupportedEncoding(final Throwable t) {
        return this.wcharArrayUnsupportedEncoding(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL wcharArrayUnsupportedEncoding() {
        return this.wcharArrayUnsupportedEncoding(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL illegalTargetAddressDisposition(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079717, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalTargetAddressDisposition", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL illegalTargetAddressDisposition(final CompletionStatus completionStatus) {
        return this.illegalTargetAddressDisposition(completionStatus, null);
    }
    
    public INTERNAL illegalTargetAddressDisposition(final Throwable t) {
        return this.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL illegalTargetAddressDisposition() {
        return this.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL nullReplyInGetAddrDisposition(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079718, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.nullReplyInGetAddrDisposition", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL nullReplyInGetAddrDisposition(final CompletionStatus completionStatus) {
        return this.nullReplyInGetAddrDisposition(completionStatus, null);
    }
    
    public INTERNAL nullReplyInGetAddrDisposition(final Throwable t) {
        return this.nullReplyInGetAddrDisposition(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL nullReplyInGetAddrDisposition() {
        return this.nullReplyInGetAddrDisposition(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079719, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbTargetAddrPreferenceInExtractObjectkeyInvalid", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(final CompletionStatus completionStatus) {
        return this.orbTargetAddrPreferenceInExtractObjectkeyInvalid(completionStatus, null);
    }
    
    public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(final Throwable t) {
        return this.orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid() {
        return this.orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL invalidIsstreamedTckind(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079720, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidIsstreamedTckind", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidIsstreamedTckind(final CompletionStatus completionStatus, final Object o) {
        return this.invalidIsstreamedTckind(completionStatus, null, o);
    }
    
    public INTERNAL invalidIsstreamedTckind(final Throwable t, final Object o) {
        return this.invalidIsstreamedTckind(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL invalidIsstreamedTckind(final Object o) {
        return this.invalidIsstreamedTckind(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL invalidJdk131PatchLevel(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079721, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidJdk131PatchLevel", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidJdk131PatchLevel(final CompletionStatus completionStatus) {
        return this.invalidJdk131PatchLevel(completionStatus, null);
    }
    
    public INTERNAL invalidJdk131PatchLevel(final Throwable t) {
        return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL invalidJdk131PatchLevel() {
        return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL svcctxUnmarshalError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079722, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.svcctxUnmarshalError", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL svcctxUnmarshalError(final CompletionStatus completionStatus) {
        return this.svcctxUnmarshalError(completionStatus, null);
    }
    
    public INTERNAL svcctxUnmarshalError(final Throwable t) {
        return this.svcctxUnmarshalError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL svcctxUnmarshalError() {
        return this.svcctxUnmarshalError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL nullIor(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079723, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.nullIor", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL nullIor(final CompletionStatus completionStatus) {
        return this.nullIor(completionStatus, null);
    }
    
    public INTERNAL nullIor(final Throwable t) {
        return this.nullIor(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL nullIor() {
        return this.nullIor(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unsupportedGiopVersion(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079724, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unsupportedGiopVersion", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unsupportedGiopVersion(final CompletionStatus completionStatus, final Object o) {
        return this.unsupportedGiopVersion(completionStatus, null, o);
    }
    
    public INTERNAL unsupportedGiopVersion(final Throwable t, final Object o) {
        return this.unsupportedGiopVersion(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL unsupportedGiopVersion(final Object o) {
        return this.unsupportedGiopVersion(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL applicationExceptionInSpecialMethod(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079725, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.applicationExceptionInSpecialMethod", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL applicationExceptionInSpecialMethod(final CompletionStatus completionStatus) {
        return this.applicationExceptionInSpecialMethod(completionStatus, null);
    }
    
    public INTERNAL applicationExceptionInSpecialMethod(final Throwable t) {
        return this.applicationExceptionInSpecialMethod(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL applicationExceptionInSpecialMethod() {
        return this.applicationExceptionInSpecialMethod(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL statementNotReachable1(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079726, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable1", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL statementNotReachable1(final CompletionStatus completionStatus) {
        return this.statementNotReachable1(completionStatus, null);
    }
    
    public INTERNAL statementNotReachable1(final Throwable t) {
        return this.statementNotReachable1(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL statementNotReachable1() {
        return this.statementNotReachable1(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL statementNotReachable2(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079727, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable2", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL statementNotReachable2(final CompletionStatus completionStatus) {
        return this.statementNotReachable2(completionStatus, null);
    }
    
    public INTERNAL statementNotReachable2(final Throwable t) {
        return this.statementNotReachable2(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL statementNotReachable2() {
        return this.statementNotReachable2(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL statementNotReachable3(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079728, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable3", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL statementNotReachable3(final CompletionStatus completionStatus) {
        return this.statementNotReachable3(completionStatus, null);
    }
    
    public INTERNAL statementNotReachable3(final Throwable t) {
        return this.statementNotReachable3(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL statementNotReachable3() {
        return this.statementNotReachable3(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL statementNotReachable4(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079729, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.statementNotReachable4", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL statementNotReachable4(final CompletionStatus completionStatus) {
        return this.statementNotReachable4(completionStatus, null);
    }
    
    public INTERNAL statementNotReachable4(final Throwable t) {
        return this.statementNotReachable4(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL statementNotReachable4() {
        return this.statementNotReachable4(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL statementNotReachable5(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079730, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable5", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL statementNotReachable5(final CompletionStatus completionStatus) {
        return this.statementNotReachable5(completionStatus, null);
    }
    
    public INTERNAL statementNotReachable5(final Throwable t) {
        return this.statementNotReachable5(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL statementNotReachable5() {
        return this.statementNotReachable5(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL statementNotReachable6(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079731, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable6", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL statementNotReachable6(final CompletionStatus completionStatus) {
        return this.statementNotReachable6(completionStatus, null);
    }
    
    public INTERNAL statementNotReachable6(final Throwable t) {
        return this.statementNotReachable6(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL statementNotReachable6() {
        return this.statementNotReachable6(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unexpectedDiiException(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079732, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unexpectedDiiException", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unexpectedDiiException(final CompletionStatus completionStatus) {
        return this.unexpectedDiiException(completionStatus, null);
    }
    
    public INTERNAL unexpectedDiiException(final Throwable t) {
        return this.unexpectedDiiException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL unexpectedDiiException() {
        return this.unexpectedDiiException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL methodShouldNotBeCalled(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079733, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.methodShouldNotBeCalled", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL methodShouldNotBeCalled(final CompletionStatus completionStatus) {
        return this.methodShouldNotBeCalled(completionStatus, null);
    }
    
    public INTERNAL methodShouldNotBeCalled(final Throwable t) {
        return this.methodShouldNotBeCalled(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL methodShouldNotBeCalled() {
        return this.methodShouldNotBeCalled(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL cancelNotSupported(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079734, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.cancelNotSupported", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL cancelNotSupported(final CompletionStatus completionStatus) {
        return this.cancelNotSupported(completionStatus, null);
    }
    
    public INTERNAL cancelNotSupported(final Throwable t) {
        return this.cancelNotSupported(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL cancelNotSupported() {
        return this.cancelNotSupported(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL emptyStackRunServantPostInvoke(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079735, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.emptyStackRunServantPostInvoke", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL emptyStackRunServantPostInvoke(final CompletionStatus completionStatus) {
        return this.emptyStackRunServantPostInvoke(completionStatus, null);
    }
    
    public INTERNAL emptyStackRunServantPostInvoke(final Throwable t) {
        return this.emptyStackRunServantPostInvoke(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL emptyStackRunServantPostInvoke() {
        return this.emptyStackRunServantPostInvoke(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL problemWithExceptionTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079736, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.problemWithExceptionTypecode", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL problemWithExceptionTypecode(final CompletionStatus completionStatus) {
        return this.problemWithExceptionTypecode(completionStatus, null);
    }
    
    public INTERNAL problemWithExceptionTypecode(final Throwable t) {
        return this.problemWithExceptionTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL problemWithExceptionTypecode() {
        return this.problemWithExceptionTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL illegalSubcontractId(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079737, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalSubcontractId", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL illegalSubcontractId(final CompletionStatus completionStatus, final Object o) {
        return this.illegalSubcontractId(completionStatus, null, o);
    }
    
    public INTERNAL illegalSubcontractId(final Throwable t, final Object o) {
        return this.illegalSubcontractId(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL illegalSubcontractId(final Object o) {
        return this.illegalSubcontractId(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL badSystemExceptionInLocateReply(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079738, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badSystemExceptionInLocateReply", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badSystemExceptionInLocateReply(final CompletionStatus completionStatus) {
        return this.badSystemExceptionInLocateReply(completionStatus, null);
    }
    
    public INTERNAL badSystemExceptionInLocateReply(final Throwable t) {
        return this.badSystemExceptionInLocateReply(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badSystemExceptionInLocateReply() {
        return this.badSystemExceptionInLocateReply(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badSystemExceptionInReply(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079739, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badSystemExceptionInReply", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badSystemExceptionInReply(final CompletionStatus completionStatus) {
        return this.badSystemExceptionInReply(completionStatus, null);
    }
    
    public INTERNAL badSystemExceptionInReply(final Throwable t) {
        return this.badSystemExceptionInReply(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badSystemExceptionInReply() {
        return this.badSystemExceptionInReply(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badCompletionStatusInLocateReply(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079740, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCompletionStatusInLocateReply", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badCompletionStatusInLocateReply(final CompletionStatus completionStatus, final Object o) {
        return this.badCompletionStatusInLocateReply(completionStatus, null, o);
    }
    
    public INTERNAL badCompletionStatusInLocateReply(final Throwable t, final Object o) {
        return this.badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL badCompletionStatusInLocateReply(final Object o) {
        return this.badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL badCompletionStatusInReply(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079741, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCompletionStatusInReply", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badCompletionStatusInReply(final CompletionStatus completionStatus, final Object o) {
        return this.badCompletionStatusInReply(completionStatus, null, o);
    }
    
    public INTERNAL badCompletionStatusInReply(final Throwable t, final Object o) {
        return this.badCompletionStatusInReply(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL badCompletionStatusInReply(final Object o) {
        return this.badCompletionStatusInReply(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL badkindCannotOccur(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079742, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badkindCannotOccur", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badkindCannotOccur(final CompletionStatus completionStatus) {
        return this.badkindCannotOccur(completionStatus, null);
    }
    
    public INTERNAL badkindCannotOccur(final Throwable t) {
        return this.badkindCannotOccur(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badkindCannotOccur() {
        return this.badkindCannotOccur(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL errorResolvingAlias(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079743, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.errorResolvingAlias", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorResolvingAlias(final CompletionStatus completionStatus) {
        return this.errorResolvingAlias(completionStatus, null);
    }
    
    public INTERNAL errorResolvingAlias(final Throwable t) {
        return this.errorResolvingAlias(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL errorResolvingAlias() {
        return this.errorResolvingAlias(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL tkLongDoubleNotSupported(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079744, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.tkLongDoubleNotSupported", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL tkLongDoubleNotSupported(final CompletionStatus completionStatus) {
        return this.tkLongDoubleNotSupported(completionStatus, null);
    }
    
    public INTERNAL tkLongDoubleNotSupported(final Throwable t) {
        return this.tkLongDoubleNotSupported(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL tkLongDoubleNotSupported() {
        return this.tkLongDoubleNotSupported(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL typecodeNotSupported(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079745, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.typecodeNotSupported", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL typecodeNotSupported(final CompletionStatus completionStatus) {
        return this.typecodeNotSupported(completionStatus, null);
    }
    
    public INTERNAL typecodeNotSupported(final Throwable t) {
        return this.typecodeNotSupported(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL typecodeNotSupported() {
        return this.typecodeNotSupported(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL boundsCannotOccur(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079747, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.boundsCannotOccur", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL boundsCannotOccur(final CompletionStatus completionStatus) {
        return this.boundsCannotOccur(completionStatus, null);
    }
    
    public INTERNAL boundsCannotOccur(final Throwable t) {
        return this.boundsCannotOccur(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL boundsCannotOccur() {
        return this.boundsCannotOccur(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL numInvocationsAlreadyZero(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079749, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.numInvocationsAlreadyZero", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL numInvocationsAlreadyZero(final CompletionStatus completionStatus) {
        return this.numInvocationsAlreadyZero(completionStatus, null);
    }
    
    public INTERNAL numInvocationsAlreadyZero(final Throwable t) {
        return this.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL numInvocationsAlreadyZero() {
        return this.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL errorInitBadserveridhandler(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079750, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.errorInitBadserveridhandler", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorInitBadserveridhandler(final CompletionStatus completionStatus) {
        return this.errorInitBadserveridhandler(completionStatus, null);
    }
    
    public INTERNAL errorInitBadserveridhandler(final Throwable t) {
        return this.errorInitBadserveridhandler(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL errorInitBadserveridhandler() {
        return this.errorInitBadserveridhandler(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL noToa(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079751, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noToa", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL noToa(final CompletionStatus completionStatus) {
        return this.noToa(completionStatus, null);
    }
    
    public INTERNAL noToa(final Throwable t) {
        return this.noToa(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL noToa() {
        return this.noToa(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL noPoa(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079752, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noPoa", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL noPoa(final CompletionStatus completionStatus) {
        return this.noPoa(completionStatus, null);
    }
    
    public INTERNAL noPoa(final Throwable t) {
        return this.noPoa(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL noPoa() {
        return this.noPoa(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL invocationInfoStackEmpty(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079753, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invocationInfoStackEmpty", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invocationInfoStackEmpty(final CompletionStatus completionStatus) {
        return this.invocationInfoStackEmpty(completionStatus, null);
    }
    
    public INTERNAL invocationInfoStackEmpty(final Throwable t) {
        return this.invocationInfoStackEmpty(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL invocationInfoStackEmpty() {
        return this.invocationInfoStackEmpty(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badCodeSetString(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079754, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCodeSetString", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badCodeSetString(final CompletionStatus completionStatus) {
        return this.badCodeSetString(completionStatus, null);
    }
    
    public INTERNAL badCodeSetString(final Throwable t) {
        return this.badCodeSetString(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badCodeSetString() {
        return this.badCodeSetString(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unknownNativeCodeset(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079755, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unknownNativeCodeset", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unknownNativeCodeset(final CompletionStatus completionStatus, final Object o) {
        return this.unknownNativeCodeset(completionStatus, null, o);
    }
    
    public INTERNAL unknownNativeCodeset(final Throwable t, final Object o) {
        return this.unknownNativeCodeset(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL unknownNativeCodeset(final Object o) {
        return this.unknownNativeCodeset(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL unknownConversionCodeSet(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079756, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unknownConversionCodeSet", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unknownConversionCodeSet(final CompletionStatus completionStatus, final Object o) {
        return this.unknownConversionCodeSet(completionStatus, null, o);
    }
    
    public INTERNAL unknownConversionCodeSet(final Throwable t, final Object o) {
        return this.unknownConversionCodeSet(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL unknownConversionCodeSet(final Object o) {
        return this.unknownConversionCodeSet(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL invalidCodeSetNumber(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079757, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidCodeSetNumber", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidCodeSetNumber(final CompletionStatus completionStatus) {
        return this.invalidCodeSetNumber(completionStatus, null);
    }
    
    public INTERNAL invalidCodeSetNumber(final Throwable t) {
        return this.invalidCodeSetNumber(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL invalidCodeSetNumber() {
        return this.invalidCodeSetNumber(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL invalidCodeSetString(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079758, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidCodeSetString", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidCodeSetString(final CompletionStatus completionStatus, final Object o) {
        return this.invalidCodeSetString(completionStatus, null, o);
    }
    
    public INTERNAL invalidCodeSetString(final Throwable t, final Object o) {
        return this.invalidCodeSetString(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL invalidCodeSetString(final Object o) {
        return this.invalidCodeSetString(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL invalidCtbConverterName(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079759, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidCtbConverterName", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidCtbConverterName(final CompletionStatus completionStatus, final Object o) {
        return this.invalidCtbConverterName(completionStatus, null, o);
    }
    
    public INTERNAL invalidCtbConverterName(final Throwable t, final Object o) {
        return this.invalidCtbConverterName(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL invalidCtbConverterName(final Object o) {
        return this.invalidCtbConverterName(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL invalidBtcConverterName(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079760, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidBtcConverterName", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidBtcConverterName(final CompletionStatus completionStatus, final Object o) {
        return this.invalidBtcConverterName(completionStatus, null, o);
    }
    
    public INTERNAL invalidBtcConverterName(final Throwable t, final Object o) {
        return this.invalidBtcConverterName(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL invalidBtcConverterName(final Object o) {
        return this.invalidBtcConverterName(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL couldNotDuplicateCdrInputStream(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079761, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotDuplicateCdrInputStream", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL couldNotDuplicateCdrInputStream(final CompletionStatus completionStatus) {
        return this.couldNotDuplicateCdrInputStream(completionStatus, null);
    }
    
    public INTERNAL couldNotDuplicateCdrInputStream(final Throwable t) {
        return this.couldNotDuplicateCdrInputStream(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL couldNotDuplicateCdrInputStream() {
        return this.couldNotDuplicateCdrInputStream(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL bootstrapApplicationException(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079762, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.bootstrapApplicationException", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL bootstrapApplicationException(final CompletionStatus completionStatus) {
        return this.bootstrapApplicationException(completionStatus, null);
    }
    
    public INTERNAL bootstrapApplicationException(final Throwable t) {
        return this.bootstrapApplicationException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL bootstrapApplicationException() {
        return this.bootstrapApplicationException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL duplicateIndirectionOffset(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079763, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.duplicateIndirectionOffset", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL duplicateIndirectionOffset(final CompletionStatus completionStatus) {
        return this.duplicateIndirectionOffset(completionStatus, null);
    }
    
    public INTERNAL duplicateIndirectionOffset(final Throwable t) {
        return this.duplicateIndirectionOffset(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL duplicateIndirectionOffset() {
        return this.duplicateIndirectionOffset(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badMessageTypeForCancel(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079764, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badMessageTypeForCancel", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badMessageTypeForCancel(final CompletionStatus completionStatus) {
        return this.badMessageTypeForCancel(completionStatus, null);
    }
    
    public INTERNAL badMessageTypeForCancel(final Throwable t) {
        return this.badMessageTypeForCancel(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badMessageTypeForCancel() {
        return this.badMessageTypeForCancel(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL duplicateExceptionDetailMessage(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079765, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.duplicateExceptionDetailMessage", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL duplicateExceptionDetailMessage(final CompletionStatus completionStatus) {
        return this.duplicateExceptionDetailMessage(completionStatus, null);
    }
    
    public INTERNAL duplicateExceptionDetailMessage(final Throwable t) {
        return this.duplicateExceptionDetailMessage(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL duplicateExceptionDetailMessage() {
        return this.duplicateExceptionDetailMessage(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL badExceptionDetailMessageServiceContextType(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079766, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badExceptionDetailMessageServiceContextType", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badExceptionDetailMessageServiceContextType(final CompletionStatus completionStatus) {
        return this.badExceptionDetailMessageServiceContextType(completionStatus, null);
    }
    
    public INTERNAL badExceptionDetailMessageServiceContextType(final Throwable t) {
        return this.badExceptionDetailMessageServiceContextType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badExceptionDetailMessageServiceContextType() {
        return this.badExceptionDetailMessageServiceContextType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079767, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unexpectedDirectByteBufferWithNonChannelSocket", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(final CompletionStatus completionStatus) {
        return this.unexpectedDirectByteBufferWithNonChannelSocket(completionStatus, null);
    }
    
    public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(final Throwable t) {
        return this.unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket() {
        return this.unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079768, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unexpectedNonDirectByteBufferWithChannelSocket", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(final CompletionStatus completionStatus) {
        return this.unexpectedNonDirectByteBufferWithChannelSocket(completionStatus, null);
    }
    
    public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(final Throwable t) {
        return this.unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket() {
        return this.unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL invalidContactInfoListIteratorFailureException(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079770, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidContactInfoListIteratorFailureException", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidContactInfoListIteratorFailureException(final CompletionStatus completionStatus) {
        return this.invalidContactInfoListIteratorFailureException(completionStatus, null);
    }
    
    public INTERNAL invalidContactInfoListIteratorFailureException(final Throwable t) {
        return this.invalidContactInfoListIteratorFailureException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL invalidContactInfoListIteratorFailureException() {
        return this.invalidContactInfoListIteratorFailureException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL remarshalWithNowhereToGo(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079771, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.remarshalWithNowhereToGo", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL remarshalWithNowhereToGo(final CompletionStatus completionStatus) {
        return this.remarshalWithNowhereToGo(completionStatus, null);
    }
    
    public INTERNAL remarshalWithNowhereToGo(final Throwable t) {
        return this.remarshalWithNowhereToGo(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL remarshalWithNowhereToGo() {
        return this.remarshalWithNowhereToGo(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL exceptionWhenSendingCloseConnection(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079772, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.exceptionWhenSendingCloseConnection", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL exceptionWhenSendingCloseConnection(final CompletionStatus completionStatus) {
        return this.exceptionWhenSendingCloseConnection(completionStatus, null);
    }
    
    public INTERNAL exceptionWhenSendingCloseConnection(final Throwable t) {
        return this.exceptionWhenSendingCloseConnection(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL exceptionWhenSendingCloseConnection() {
        return this.exceptionWhenSendingCloseConnection(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL invocationErrorInReflectiveTie(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079773, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invocationErrorInReflectiveTie", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invocationErrorInReflectiveTie(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.invocationErrorInReflectiveTie(completionStatus, null, o, o2);
    }
    
    public INTERNAL invocationErrorInReflectiveTie(final Throwable t, final Object o, final Object o2) {
        return this.invocationErrorInReflectiveTie(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL invocationErrorInReflectiveTie(final Object o, final Object o2) {
        return this.invocationErrorInReflectiveTie(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL badHelperWriteMethod(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079774, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badHelperWriteMethod", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badHelperWriteMethod(final CompletionStatus completionStatus, final Object o) {
        return this.badHelperWriteMethod(completionStatus, null, o);
    }
    
    public INTERNAL badHelperWriteMethod(final Throwable t, final Object o) {
        return this.badHelperWriteMethod(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL badHelperWriteMethod(final Object o) {
        return this.badHelperWriteMethod(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL badHelperReadMethod(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079775, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badHelperReadMethod", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badHelperReadMethod(final CompletionStatus completionStatus, final Object o) {
        return this.badHelperReadMethod(completionStatus, null, o);
    }
    
    public INTERNAL badHelperReadMethod(final Throwable t, final Object o) {
        return this.badHelperReadMethod(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL badHelperReadMethod(final Object o) {
        return this.badHelperReadMethod(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL badHelperIdMethod(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079776, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badHelperIdMethod", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badHelperIdMethod(final CompletionStatus completionStatus, final Object o) {
        return this.badHelperIdMethod(completionStatus, null, o);
    }
    
    public INTERNAL badHelperIdMethod(final Throwable t, final Object o) {
        return this.badHelperIdMethod(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL badHelperIdMethod(final Object o) {
        return this.badHelperIdMethod(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL writeUndeclaredException(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079777, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.writeUndeclaredException", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL writeUndeclaredException(final CompletionStatus completionStatus, final Object o) {
        return this.writeUndeclaredException(completionStatus, null, o);
    }
    
    public INTERNAL writeUndeclaredException(final Throwable t, final Object o) {
        return this.writeUndeclaredException(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL writeUndeclaredException(final Object o) {
        return this.writeUndeclaredException(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL readUndeclaredException(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079778, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.readUndeclaredException", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL readUndeclaredException(final CompletionStatus completionStatus, final Object o) {
        return this.readUndeclaredException(completionStatus, null, o);
    }
    
    public INTERNAL readUndeclaredException(final Throwable t, final Object o) {
        return this.readUndeclaredException(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL readUndeclaredException(final Object o) {
        return this.readUndeclaredException(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL unableToSetSocketFactoryOrb(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079779, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unableToSetSocketFactoryOrb", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unableToSetSocketFactoryOrb(final CompletionStatus completionStatus) {
        return this.unableToSetSocketFactoryOrb(completionStatus, null);
    }
    
    public INTERNAL unableToSetSocketFactoryOrb(final Throwable t) {
        return this.unableToSetSocketFactoryOrb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL unableToSetSocketFactoryOrb() {
        return this.unableToSetSocketFactoryOrb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL unexpectedException(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079780, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unexpectedException", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL unexpectedException(final CompletionStatus completionStatus) {
        return this.unexpectedException(completionStatus, null);
    }
    
    public INTERNAL unexpectedException(final Throwable t) {
        return this.unexpectedException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL unexpectedException() {
        return this.unexpectedException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL noInvocationHandler(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079781, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noInvocationHandler", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL noInvocationHandler(final CompletionStatus completionStatus, final Object o) {
        return this.noInvocationHandler(completionStatus, null, o);
    }
    
    public INTERNAL noInvocationHandler(final Throwable t, final Object o) {
        return this.noInvocationHandler(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL noInvocationHandler(final Object o) {
        return this.noInvocationHandler(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL invalidBuffMgrStrategy(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079782, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidBuffMgrStrategy", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL invalidBuffMgrStrategy(final CompletionStatus completionStatus, final Object o) {
        return this.invalidBuffMgrStrategy(completionStatus, null, o);
    }
    
    public INTERNAL invalidBuffMgrStrategy(final Throwable t, final Object o) {
        return this.invalidBuffMgrStrategy(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL invalidBuffMgrStrategy(final Object o) {
        return this.invalidBuffMgrStrategy(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL javaStreamInitFailed(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079783, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.javaStreamInitFailed", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL javaStreamInitFailed(final CompletionStatus completionStatus) {
        return this.javaStreamInitFailed(completionStatus, null);
    }
    
    public INTERNAL javaStreamInitFailed(final Throwable t) {
        return this.javaStreamInitFailed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL javaStreamInitFailed() {
        return this.javaStreamInitFailed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL duplicateOrbVersionServiceContext(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079784, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.duplicateOrbVersionServiceContext", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL duplicateOrbVersionServiceContext(final CompletionStatus completionStatus) {
        return this.duplicateOrbVersionServiceContext(completionStatus, null);
    }
    
    public INTERNAL duplicateOrbVersionServiceContext(final Throwable t) {
        return this.duplicateOrbVersionServiceContext(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL duplicateOrbVersionServiceContext() {
        return this.duplicateOrbVersionServiceContext(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL duplicateSendingContextServiceContext(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079785, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.duplicateSendingContextServiceContext", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL duplicateSendingContextServiceContext(final CompletionStatus completionStatus) {
        return this.duplicateSendingContextServiceContext(completionStatus, null);
    }
    
    public INTERNAL duplicateSendingContextServiceContext(final Throwable t) {
        return this.duplicateSendingContextServiceContext(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL duplicateSendingContextServiceContext() {
        return this.duplicateSendingContextServiceContext(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL workQueueThreadInterrupted(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079786, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.workQueueThreadInterrupted", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workQueueThreadInterrupted(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.workQueueThreadInterrupted(completionStatus, null, o, o2);
    }
    
    public INTERNAL workQueueThreadInterrupted(final Throwable t, final Object o, final Object o2) {
        return this.workQueueThreadInterrupted(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL workQueueThreadInterrupted(final Object o, final Object o2) {
        return this.workQueueThreadInterrupted(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL workerThreadCreated(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079792, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.workerThreadCreated", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workerThreadCreated(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.workerThreadCreated(completionStatus, null, o, o2);
    }
    
    public INTERNAL workerThreadCreated(final Throwable t, final Object o, final Object o2) {
        return this.workerThreadCreated(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL workerThreadCreated(final Object o, final Object o2) {
        return this.workerThreadCreated(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL workerThreadThrowableFromRequestWork(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398079797, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.workerThreadThrowableFromRequestWork", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workerThreadThrowableFromRequestWork(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.workerThreadThrowableFromRequestWork(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL workerThreadThrowableFromRequestWork(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.workerThreadThrowableFromRequestWork(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL workerThreadThrowableFromRequestWork(final Object o, final Object o2, final Object o3) {
        return this.workerThreadThrowableFromRequestWork(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL workerThreadNotNeeded(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398079798, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.workerThreadNotNeeded", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workerThreadNotNeeded(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.workerThreadNotNeeded(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL workerThreadNotNeeded(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.workerThreadNotNeeded(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL workerThreadNotNeeded(final Object o, final Object o2, final Object o3) {
        return this.workerThreadNotNeeded(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL workerThreadDoWorkThrowable(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079799, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.workerThreadDoWorkThrowable", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workerThreadDoWorkThrowable(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.workerThreadDoWorkThrowable(completionStatus, null, o, o2);
    }
    
    public INTERNAL workerThreadDoWorkThrowable(final Throwable t, final Object o, final Object o2) {
        return this.workerThreadDoWorkThrowable(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL workerThreadDoWorkThrowable(final Object o, final Object o2) {
        return this.workerThreadDoWorkThrowable(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL workerThreadCaughtUnexpectedThrowable(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079800, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.workerThreadCaughtUnexpectedThrowable", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workerThreadCaughtUnexpectedThrowable(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.workerThreadCaughtUnexpectedThrowable(completionStatus, null, o, o2);
    }
    
    public INTERNAL workerThreadCaughtUnexpectedThrowable(final Throwable t, final Object o, final Object o2) {
        return this.workerThreadCaughtUnexpectedThrowable(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL workerThreadCaughtUnexpectedThrowable(final Object o, final Object o2) {
        return this.workerThreadCaughtUnexpectedThrowable(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL workerThreadCreationFailure(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079801, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.SEVERE)) {
            this.doLog(Level.SEVERE, "ORBUTIL.workerThreadCreationFailure", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workerThreadCreationFailure(final CompletionStatus completionStatus, final Object o) {
        return this.workerThreadCreationFailure(completionStatus, null, o);
    }
    
    public INTERNAL workerThreadCreationFailure(final Throwable t, final Object o) {
        return this.workerThreadCreationFailure(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL workerThreadCreationFailure(final Object o) {
        return this.workerThreadCreationFailure(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL workerThreadSetNameFailure(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398079802, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.workerThreadSetNameFailure", new Object[] { o, o2, o3 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workerThreadSetNameFailure(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.workerThreadSetNameFailure(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL workerThreadSetNameFailure(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.workerThreadSetNameFailure(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL workerThreadSetNameFailure(final Object o, final Object o2, final Object o3) {
        return this.workerThreadSetNameFailure(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL workQueueRequestWorkNoWorkFound(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079804, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.workQueueRequestWorkNoWorkFound", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL workQueueRequestWorkNoWorkFound(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.workQueueRequestWorkNoWorkFound(completionStatus, null, o, o2);
    }
    
    public INTERNAL workQueueRequestWorkNoWorkFound(final Throwable t, final Object o, final Object o2) {
        return this.workQueueRequestWorkNoWorkFound(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL workQueueRequestWorkNoWorkFound(final Object o, final Object o2) {
        return this.workQueueRequestWorkNoWorkFound(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL threadPoolCloseError(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398079814, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.threadPoolCloseError", null, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL threadPoolCloseError(final CompletionStatus completionStatus) {
        return this.threadPoolCloseError(completionStatus, null);
    }
    
    public INTERNAL threadPoolCloseError(final Throwable t) {
        return this.threadPoolCloseError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL threadPoolCloseError() {
        return this.threadPoolCloseError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL threadGroupIsDestroyed(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079815, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.threadGroupIsDestroyed", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL threadGroupIsDestroyed(final CompletionStatus completionStatus, final Object o) {
        return this.threadGroupIsDestroyed(completionStatus, null, o);
    }
    
    public INTERNAL threadGroupIsDestroyed(final Throwable t, final Object o) {
        return this.threadGroupIsDestroyed(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL threadGroupIsDestroyed(final Object o) {
        return this.threadGroupIsDestroyed(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL threadGroupHasActiveThreadsInClose(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079816, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.threadGroupHasActiveThreadsInClose", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL threadGroupHasActiveThreadsInClose(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.threadGroupHasActiveThreadsInClose(completionStatus, null, o, o2);
    }
    
    public INTERNAL threadGroupHasActiveThreadsInClose(final Throwable t, final Object o, final Object o2) {
        return this.threadGroupHasActiveThreadsInClose(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL threadGroupHasActiveThreadsInClose(final Object o, final Object o2) {
        return this.threadGroupHasActiveThreadsInClose(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL threadGroupHasSubGroupsInClose(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079817, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.threadGroupHasSubGroupsInClose", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL threadGroupHasSubGroupsInClose(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.threadGroupHasSubGroupsInClose(completionStatus, null, o, o2);
    }
    
    public INTERNAL threadGroupHasSubGroupsInClose(final Throwable t, final Object o, final Object o2) {
        return this.threadGroupHasSubGroupsInClose(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL threadGroupHasSubGroupsInClose(final Object o, final Object o2) {
        return this.threadGroupHasSubGroupsInClose(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public INTERNAL threadGroupDestroyFailed(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398079818, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.threadGroupDestroyFailed", new Object[] { o }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL threadGroupDestroyFailed(final CompletionStatus completionStatus, final Object o) {
        return this.threadGroupDestroyFailed(completionStatus, null, o);
    }
    
    public INTERNAL threadGroupDestroyFailed(final Throwable t, final Object o) {
        return this.threadGroupDestroyFailed(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL threadGroupDestroyFailed(final Object o) {
        return this.threadGroupDestroyFailed(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL interruptedJoinCallWhileClosingThreadPool(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final INTERNAL internal = new INTERNAL(1398079819, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.interruptedJoinCallWhileClosingThreadPool", new Object[] { o, o2 }, ORBUtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL interruptedJoinCallWhileClosingThreadPool(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.interruptedJoinCallWhileClosingThreadPool(completionStatus, null, o, o2);
    }
    
    public INTERNAL interruptedJoinCallWhileClosingThreadPool(final Throwable t, final Object o, final Object o2) {
        return this.interruptedJoinCallWhileClosingThreadPool(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public INTERNAL interruptedJoinCallWhileClosingThreadPool(final Object o, final Object o2) {
        return this.interruptedJoinCallWhileClosingThreadPool(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL chunkOverflow(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079689, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.chunkOverflow", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL chunkOverflow(final CompletionStatus completionStatus) {
        return this.chunkOverflow(completionStatus, null);
    }
    
    public MARSHAL chunkOverflow(final Throwable t) {
        return this.chunkOverflow(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL chunkOverflow() {
        return this.chunkOverflow(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL unexpectedEof(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079690, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unexpectedEof", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unexpectedEof(final CompletionStatus completionStatus) {
        return this.unexpectedEof(completionStatus, null);
    }
    
    public MARSHAL unexpectedEof(final Throwable t) {
        return this.unexpectedEof(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL unexpectedEof() {
        return this.unexpectedEof(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL readObjectException(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079691, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.readObjectException", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL readObjectException(final CompletionStatus completionStatus) {
        return this.readObjectException(completionStatus, null);
    }
    
    public MARSHAL readObjectException(final Throwable t) {
        return this.readObjectException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL readObjectException() {
        return this.readObjectException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL characterOutofrange(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079692, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.characterOutofrange", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL characterOutofrange(final CompletionStatus completionStatus) {
        return this.characterOutofrange(completionStatus, null);
    }
    
    public MARSHAL characterOutofrange(final Throwable t) {
        return this.characterOutofrange(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL characterOutofrange() {
        return this.characterOutofrange(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL dsiResultException(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079693, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.dsiResultException", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL dsiResultException(final CompletionStatus completionStatus) {
        return this.dsiResultException(completionStatus, null);
    }
    
    public MARSHAL dsiResultException(final Throwable t) {
        return this.dsiResultException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL dsiResultException() {
        return this.dsiResultException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL iiopinputstreamGrow(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079694, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.iiopinputstreamGrow", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL iiopinputstreamGrow(final CompletionStatus completionStatus) {
        return this.iiopinputstreamGrow(completionStatus, null);
    }
    
    public MARSHAL iiopinputstreamGrow(final Throwable t) {
        return this.iiopinputstreamGrow(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL iiopinputstreamGrow() {
        return this.iiopinputstreamGrow(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL endOfStream(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079695, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.endOfStream", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL endOfStream(final CompletionStatus completionStatus) {
        return this.endOfStream(completionStatus, null);
    }
    
    public MARSHAL endOfStream(final Throwable t) {
        return this.endOfStream(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL endOfStream() {
        return this.endOfStream(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL invalidObjectKey(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079696, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidObjectKey", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL invalidObjectKey(final CompletionStatus completionStatus) {
        return this.invalidObjectKey(completionStatus, null);
    }
    
    public MARSHAL invalidObjectKey(final Throwable t) {
        return this.invalidObjectKey(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL invalidObjectKey() {
        return this.invalidObjectKey(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL malformedUrl(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final MARSHAL marshal = new MARSHAL(1398079697, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.malformedUrl", new Object[] { o, o2 }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL malformedUrl(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.malformedUrl(completionStatus, null, o, o2);
    }
    
    public MARSHAL malformedUrl(final Throwable t, final Object o, final Object o2) {
        return this.malformedUrl(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public MARSHAL malformedUrl(final Object o, final Object o2) {
        return this.malformedUrl(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL valuehandlerReadError(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079698, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.valuehandlerReadError", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL valuehandlerReadError(final CompletionStatus completionStatus) {
        return this.valuehandlerReadError(completionStatus, null);
    }
    
    public MARSHAL valuehandlerReadError(final Throwable t) {
        return this.valuehandlerReadError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL valuehandlerReadError() {
        return this.valuehandlerReadError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL valuehandlerReadException(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079699, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.valuehandlerReadException", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL valuehandlerReadException(final CompletionStatus completionStatus) {
        return this.valuehandlerReadException(completionStatus, null);
    }
    
    public MARSHAL valuehandlerReadException(final Throwable t) {
        return this.valuehandlerReadException(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL valuehandlerReadException() {
        return this.valuehandlerReadException(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL badKind(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079700, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badKind", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badKind(final CompletionStatus completionStatus) {
        return this.badKind(completionStatus, null);
    }
    
    public MARSHAL badKind(final Throwable t) {
        return this.badKind(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL badKind() {
        return this.badKind(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL cnfeReadClass(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079701, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.cnfeReadClass", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL cnfeReadClass(final CompletionStatus completionStatus, final Object o) {
        return this.cnfeReadClass(completionStatus, null, o);
    }
    
    public MARSHAL cnfeReadClass(final Throwable t, final Object o) {
        return this.cnfeReadClass(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL cnfeReadClass(final Object o) {
        return this.cnfeReadClass(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL badRepIdIndirection(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079702, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badRepIdIndirection", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badRepIdIndirection(final CompletionStatus completionStatus, final Object o) {
        return this.badRepIdIndirection(completionStatus, null, o);
    }
    
    public MARSHAL badRepIdIndirection(final Throwable t, final Object o) {
        return this.badRepIdIndirection(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL badRepIdIndirection(final Object o) {
        return this.badRepIdIndirection(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL badCodebaseIndirection(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079703, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badCodebaseIndirection", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badCodebaseIndirection(final CompletionStatus completionStatus, final Object o) {
        return this.badCodebaseIndirection(completionStatus, null, o);
    }
    
    public MARSHAL badCodebaseIndirection(final Throwable t, final Object o) {
        return this.badCodebaseIndirection(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL badCodebaseIndirection(final Object o) {
        return this.badCodebaseIndirection(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL unknownCodeset(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079704, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unknownCodeset", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unknownCodeset(final CompletionStatus completionStatus, final Object o) {
        return this.unknownCodeset(completionStatus, null, o);
    }
    
    public MARSHAL unknownCodeset(final Throwable t, final Object o) {
        return this.unknownCodeset(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL unknownCodeset(final Object o) {
        return this.unknownCodeset(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL wcharDataInGiop10(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079705, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.wcharDataInGiop10", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL wcharDataInGiop10(final CompletionStatus completionStatus) {
        return this.wcharDataInGiop10(completionStatus, null);
    }
    
    public MARSHAL wcharDataInGiop10(final Throwable t) {
        return this.wcharDataInGiop10(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL wcharDataInGiop10() {
        return this.wcharDataInGiop10(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL negativeStringLength(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079706, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.negativeStringLength", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL negativeStringLength(final CompletionStatus completionStatus, final Object o) {
        return this.negativeStringLength(completionStatus, null, o);
    }
    
    public MARSHAL negativeStringLength(final Throwable t, final Object o) {
        return this.negativeStringLength(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL negativeStringLength(final Object o) {
        return this.negativeStringLength(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL expectedTypeNullAndNoRepId(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079707, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.expectedTypeNullAndNoRepId", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL expectedTypeNullAndNoRepId(final CompletionStatus completionStatus) {
        return this.expectedTypeNullAndNoRepId(completionStatus, null);
    }
    
    public MARSHAL expectedTypeNullAndNoRepId(final Throwable t) {
        return this.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL expectedTypeNullAndNoRepId() {
        return this.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL readValueAndNoRepId(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079708, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.readValueAndNoRepId", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL readValueAndNoRepId(final CompletionStatus completionStatus) {
        return this.readValueAndNoRepId(completionStatus, null);
    }
    
    public MARSHAL readValueAndNoRepId(final Throwable t) {
        return this.readValueAndNoRepId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL readValueAndNoRepId() {
        return this.readValueAndNoRepId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL unexpectedEnclosingValuetype(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final MARSHAL marshal = new MARSHAL(1398079710, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unexpectedEnclosingValuetype", new Object[] { o, o2 }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unexpectedEnclosingValuetype(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.unexpectedEnclosingValuetype(completionStatus, null, o, o2);
    }
    
    public MARSHAL unexpectedEnclosingValuetype(final Throwable t, final Object o, final Object o2) {
        return this.unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public MARSHAL unexpectedEnclosingValuetype(final Object o, final Object o2) {
        return this.unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL positiveEndTag(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final MARSHAL marshal = new MARSHAL(1398079711, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.positiveEndTag", new Object[] { o, o2 }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL positiveEndTag(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.positiveEndTag(completionStatus, null, o, o2);
    }
    
    public MARSHAL positiveEndTag(final Throwable t, final Object o, final Object o2) {
        return this.positiveEndTag(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public MARSHAL positiveEndTag(final Object o, final Object o2) {
        return this.positiveEndTag(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL nullOutCall(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079712, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.nullOutCall", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL nullOutCall(final CompletionStatus completionStatus) {
        return this.nullOutCall(completionStatus, null);
    }
    
    public MARSHAL nullOutCall(final Throwable t) {
        return this.nullOutCall(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL nullOutCall() {
        return this.nullOutCall(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL writeLocalObject(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079713, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.writeLocalObject", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL writeLocalObject(final CompletionStatus completionStatus) {
        return this.writeLocalObject(completionStatus, null);
    }
    
    public MARSHAL writeLocalObject(final Throwable t) {
        return this.writeLocalObject(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL writeLocalObject() {
        return this.writeLocalObject(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL badInsertobjParam(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079714, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badInsertobjParam", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badInsertobjParam(final CompletionStatus completionStatus, final Object o) {
        return this.badInsertobjParam(completionStatus, null, o);
    }
    
    public MARSHAL badInsertobjParam(final Throwable t, final Object o) {
        return this.badInsertobjParam(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL badInsertobjParam(final Object o) {
        return this.badInsertobjParam(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL customWrapperWithCodebase(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079715, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.customWrapperWithCodebase", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL customWrapperWithCodebase(final CompletionStatus completionStatus) {
        return this.customWrapperWithCodebase(completionStatus, null);
    }
    
    public MARSHAL customWrapperWithCodebase(final Throwable t) {
        return this.customWrapperWithCodebase(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL customWrapperWithCodebase() {
        return this.customWrapperWithCodebase(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL customWrapperIndirection(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079716, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.customWrapperIndirection", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL customWrapperIndirection(final CompletionStatus completionStatus) {
        return this.customWrapperIndirection(completionStatus, null);
    }
    
    public MARSHAL customWrapperIndirection(final Throwable t) {
        return this.customWrapperIndirection(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL customWrapperIndirection() {
        return this.customWrapperIndirection(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL customWrapperNotSingleRepid(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079717, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.customWrapperNotSingleRepid", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL customWrapperNotSingleRepid(final CompletionStatus completionStatus) {
        return this.customWrapperNotSingleRepid(completionStatus, null);
    }
    
    public MARSHAL customWrapperNotSingleRepid(final Throwable t) {
        return this.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL customWrapperNotSingleRepid() {
        return this.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL badValueTag(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079718, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badValueTag", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badValueTag(final CompletionStatus completionStatus, final Object o) {
        return this.badValueTag(completionStatus, null, o);
    }
    
    public MARSHAL badValueTag(final Throwable t, final Object o) {
        return this.badValueTag(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL badValueTag(final Object o) {
        return this.badValueTag(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL badTypecodeForCustomValue(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079719, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badTypecodeForCustomValue", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badTypecodeForCustomValue(final CompletionStatus completionStatus) {
        return this.badTypecodeForCustomValue(completionStatus, null);
    }
    
    public MARSHAL badTypecodeForCustomValue(final Throwable t) {
        return this.badTypecodeForCustomValue(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL badTypecodeForCustomValue() {
        return this.badTypecodeForCustomValue(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL errorInvokingHelperWrite(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079720, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.errorInvokingHelperWrite", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL errorInvokingHelperWrite(final CompletionStatus completionStatus) {
        return this.errorInvokingHelperWrite(completionStatus, null);
    }
    
    public MARSHAL errorInvokingHelperWrite(final Throwable t) {
        return this.errorInvokingHelperWrite(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL errorInvokingHelperWrite() {
        return this.errorInvokingHelperWrite(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL badDigitInFixed(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079721, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badDigitInFixed", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badDigitInFixed(final CompletionStatus completionStatus) {
        return this.badDigitInFixed(completionStatus, null);
    }
    
    public MARSHAL badDigitInFixed(final Throwable t) {
        return this.badDigitInFixed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL badDigitInFixed() {
        return this.badDigitInFixed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL refTypeIndirType(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079722, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.refTypeIndirType", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL refTypeIndirType(final CompletionStatus completionStatus) {
        return this.refTypeIndirType(completionStatus, null);
    }
    
    public MARSHAL refTypeIndirType(final Throwable t) {
        return this.refTypeIndirType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL refTypeIndirType() {
        return this.refTypeIndirType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL badReservedLength(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079723, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badReservedLength", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badReservedLength(final CompletionStatus completionStatus) {
        return this.badReservedLength(completionStatus, null);
    }
    
    public MARSHAL badReservedLength(final Throwable t) {
        return this.badReservedLength(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL badReservedLength() {
        return this.badReservedLength(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL nullNotAllowed(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079724, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.nullNotAllowed", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL nullNotAllowed(final CompletionStatus completionStatus) {
        return this.nullNotAllowed(completionStatus, null);
    }
    
    public MARSHAL nullNotAllowed(final Throwable t) {
        return this.nullNotAllowed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL nullNotAllowed() {
        return this.nullNotAllowed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL unionDiscriminatorError(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079726, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unionDiscriminatorError", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unionDiscriminatorError(final CompletionStatus completionStatus) {
        return this.unionDiscriminatorError(completionStatus, null);
    }
    
    public MARSHAL unionDiscriminatorError(final Throwable t) {
        return this.unionDiscriminatorError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL unionDiscriminatorError() {
        return this.unionDiscriminatorError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL cannotMarshalNative(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079727, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.cannotMarshalNative", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL cannotMarshalNative(final CompletionStatus completionStatus) {
        return this.cannotMarshalNative(completionStatus, null);
    }
    
    public MARSHAL cannotMarshalNative(final Throwable t) {
        return this.cannotMarshalNative(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL cannotMarshalNative() {
        return this.cannotMarshalNative(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL cannotMarshalBadTckind(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079728, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.cannotMarshalBadTckind", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL cannotMarshalBadTckind(final CompletionStatus completionStatus) {
        return this.cannotMarshalBadTckind(completionStatus, null);
    }
    
    public MARSHAL cannotMarshalBadTckind(final Throwable t) {
        return this.cannotMarshalBadTckind(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL cannotMarshalBadTckind() {
        return this.cannotMarshalBadTckind(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL invalidIndirection(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079729, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidIndirection", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL invalidIndirection(final CompletionStatus completionStatus, final Object o) {
        return this.invalidIndirection(completionStatus, null, o);
    }
    
    public MARSHAL invalidIndirection(final Throwable t, final Object o) {
        return this.invalidIndirection(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL invalidIndirection(final Object o) {
        return this.invalidIndirection(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL indirectionNotFound(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079730, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.indirectionNotFound", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL indirectionNotFound(final CompletionStatus completionStatus, final Object o) {
        return this.indirectionNotFound(completionStatus, null, o);
    }
    
    public MARSHAL indirectionNotFound(final Throwable t, final Object o) {
        return this.indirectionNotFound(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL indirectionNotFound(final Object o) {
        return this.indirectionNotFound(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL recursiveTypecodeError(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079731, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.recursiveTypecodeError", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL recursiveTypecodeError(final CompletionStatus completionStatus) {
        return this.recursiveTypecodeError(completionStatus, null);
    }
    
    public MARSHAL recursiveTypecodeError(final Throwable t) {
        return this.recursiveTypecodeError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL recursiveTypecodeError() {
        return this.recursiveTypecodeError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL invalidSimpleTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079732, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidSimpleTypecode", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL invalidSimpleTypecode(final CompletionStatus completionStatus) {
        return this.invalidSimpleTypecode(completionStatus, null);
    }
    
    public MARSHAL invalidSimpleTypecode(final Throwable t) {
        return this.invalidSimpleTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL invalidSimpleTypecode() {
        return this.invalidSimpleTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL invalidComplexTypecode(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079733, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidComplexTypecode", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL invalidComplexTypecode(final CompletionStatus completionStatus) {
        return this.invalidComplexTypecode(completionStatus, null);
    }
    
    public MARSHAL invalidComplexTypecode(final Throwable t) {
        return this.invalidComplexTypecode(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL invalidComplexTypecode() {
        return this.invalidComplexTypecode(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL invalidTypecodeKindMarshal(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079734, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.invalidTypecodeKindMarshal", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL invalidTypecodeKindMarshal(final CompletionStatus completionStatus) {
        return this.invalidTypecodeKindMarshal(completionStatus, null);
    }
    
    public MARSHAL invalidTypecodeKindMarshal(final Throwable t) {
        return this.invalidTypecodeKindMarshal(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL invalidTypecodeKindMarshal() {
        return this.invalidTypecodeKindMarshal(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL unexpectedUnionDefault(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079735, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unexpectedUnionDefault", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unexpectedUnionDefault(final CompletionStatus completionStatus) {
        return this.unexpectedUnionDefault(completionStatus, null);
    }
    
    public MARSHAL unexpectedUnionDefault(final Throwable t) {
        return this.unexpectedUnionDefault(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL unexpectedUnionDefault() {
        return this.unexpectedUnionDefault(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL illegalUnionDiscriminatorType(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079736, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.illegalUnionDiscriminatorType", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL illegalUnionDiscriminatorType(final CompletionStatus completionStatus) {
        return this.illegalUnionDiscriminatorType(completionStatus, null);
    }
    
    public MARSHAL illegalUnionDiscriminatorType(final Throwable t) {
        return this.illegalUnionDiscriminatorType(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL illegalUnionDiscriminatorType() {
        return this.illegalUnionDiscriminatorType(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL couldNotSkipBytes(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final MARSHAL marshal = new MARSHAL(1398079737, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotSkipBytes", new Object[] { o, o2 }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL couldNotSkipBytes(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.couldNotSkipBytes(completionStatus, null, o, o2);
    }
    
    public MARSHAL couldNotSkipBytes(final Throwable t, final Object o, final Object o2) {
        return this.couldNotSkipBytes(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public MARSHAL couldNotSkipBytes(final Object o, final Object o2) {
        return this.couldNotSkipBytes(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL badChunkLength(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final MARSHAL marshal = new MARSHAL(1398079738, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badChunkLength", new Object[] { o, o2 }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badChunkLength(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.badChunkLength(completionStatus, null, o, o2);
    }
    
    public MARSHAL badChunkLength(final Throwable t, final Object o, final Object o2) {
        return this.badChunkLength(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public MARSHAL badChunkLength(final Object o, final Object o2) {
        return this.badChunkLength(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL unableToLocateRepIdArray(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079739, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unableToLocateRepIdArray", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unableToLocateRepIdArray(final CompletionStatus completionStatus, final Object o) {
        return this.unableToLocateRepIdArray(completionStatus, null, o);
    }
    
    public MARSHAL unableToLocateRepIdArray(final Throwable t, final Object o) {
        return this.unableToLocateRepIdArray(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL unableToLocateRepIdArray(final Object o) {
        return this.unableToLocateRepIdArray(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL badFixed(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final MARSHAL marshal = new MARSHAL(1398079740, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badFixed", new Object[] { o, o2 }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badFixed(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.badFixed(completionStatus, null, o, o2);
    }
    
    public MARSHAL badFixed(final Throwable t, final Object o, final Object o2) {
        return this.badFixed(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public MARSHAL badFixed(final Object o, final Object o2) {
        return this.badFixed(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL readObjectLoadClassFailure(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2) {
        final MARSHAL marshal = new MARSHAL(1398079741, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.readObjectLoadClassFailure", new Object[] { o, o2 }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL readObjectLoadClassFailure(final CompletionStatus completionStatus, final Object o, final Object o2) {
        return this.readObjectLoadClassFailure(completionStatus, null, o, o2);
    }
    
    public MARSHAL readObjectLoadClassFailure(final Throwable t, final Object o, final Object o2) {
        return this.readObjectLoadClassFailure(CompletionStatus.COMPLETED_NO, t, o, o2);
    }
    
    public MARSHAL readObjectLoadClassFailure(final Object o, final Object o2) {
        return this.readObjectLoadClassFailure(CompletionStatus.COMPLETED_NO, null, o, o2);
    }
    
    public MARSHAL couldNotInstantiateHelper(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079742, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotInstantiateHelper", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL couldNotInstantiateHelper(final CompletionStatus completionStatus, final Object o) {
        return this.couldNotInstantiateHelper(completionStatus, null, o);
    }
    
    public MARSHAL couldNotInstantiateHelper(final Throwable t, final Object o) {
        return this.couldNotInstantiateHelper(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL couldNotInstantiateHelper(final Object o) {
        return this.couldNotInstantiateHelper(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL badToaOaid(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079743, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badToaOaid", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badToaOaid(final CompletionStatus completionStatus) {
        return this.badToaOaid(completionStatus, null);
    }
    
    public MARSHAL badToaOaid(final Throwable t) {
        return this.badToaOaid(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL badToaOaid() {
        return this.badToaOaid(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL couldNotInvokeHelperReadMethod(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079744, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotInvokeHelperReadMethod", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL couldNotInvokeHelperReadMethod(final CompletionStatus completionStatus, final Object o) {
        return this.couldNotInvokeHelperReadMethod(completionStatus, null, o);
    }
    
    public MARSHAL couldNotInvokeHelperReadMethod(final Throwable t, final Object o) {
        return this.couldNotInvokeHelperReadMethod(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL couldNotInvokeHelperReadMethod(final Object o) {
        return this.couldNotInvokeHelperReadMethod(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public MARSHAL couldNotFindClass(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079745, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.couldNotFindClass", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL couldNotFindClass(final CompletionStatus completionStatus) {
        return this.couldNotFindClass(completionStatus, null);
    }
    
    public MARSHAL couldNotFindClass(final Throwable t) {
        return this.couldNotFindClass(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL couldNotFindClass() {
        return this.couldNotFindClass(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL badArgumentsNvlist(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079746, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.badArgumentsNvlist", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL badArgumentsNvlist(final CompletionStatus completionStatus) {
        return this.badArgumentsNvlist(completionStatus, null);
    }
    
    public MARSHAL badArgumentsNvlist(final Throwable t) {
        return this.badArgumentsNvlist(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL badArgumentsNvlist() {
        return this.badArgumentsNvlist(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL stubCreateError(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398079747, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.stubCreateError", null, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL stubCreateError(final CompletionStatus completionStatus) {
        return this.stubCreateError(completionStatus, null);
    }
    
    public MARSHAL stubCreateError(final Throwable t) {
        return this.stubCreateError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL stubCreateError() {
        return this.stubCreateError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL javaSerializationException(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398079748, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.javaSerializationException", new Object[] { o }, ORBUtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL javaSerializationException(final CompletionStatus completionStatus, final Object o) {
        return this.javaSerializationException(completionStatus, null, o);
    }
    
    public MARSHAL javaSerializationException(final Throwable t, final Object o) {
        return this.javaSerializationException(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public MARSHAL javaSerializationException(final Object o) {
        return this.javaSerializationException(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public NO_IMPLEMENT genericNoImpl(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398079689, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.genericNoImpl", null, ORBUtilSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT genericNoImpl(final CompletionStatus completionStatus) {
        return this.genericNoImpl(completionStatus, null);
    }
    
    public NO_IMPLEMENT genericNoImpl(final Throwable t) {
        return this.genericNoImpl(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT genericNoImpl() {
        return this.genericNoImpl(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT contextNotImplemented(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398079690, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.contextNotImplemented", null, ORBUtilSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT contextNotImplemented(final CompletionStatus completionStatus) {
        return this.contextNotImplemented(completionStatus, null);
    }
    
    public NO_IMPLEMENT contextNotImplemented(final Throwable t) {
        return this.contextNotImplemented(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT contextNotImplemented() {
        return this.contextNotImplemented(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT getinterfaceNotImplemented(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398079691, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.getinterfaceNotImplemented", null, ORBUtilSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT getinterfaceNotImplemented(final CompletionStatus completionStatus) {
        return this.getinterfaceNotImplemented(completionStatus, null);
    }
    
    public NO_IMPLEMENT getinterfaceNotImplemented(final Throwable t) {
        return this.getinterfaceNotImplemented(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT getinterfaceNotImplemented() {
        return this.getinterfaceNotImplemented(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT sendDeferredNotimplemented(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398079692, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.sendDeferredNotimplemented", null, ORBUtilSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT sendDeferredNotimplemented(final CompletionStatus completionStatus) {
        return this.sendDeferredNotimplemented(completionStatus, null);
    }
    
    public NO_IMPLEMENT sendDeferredNotimplemented(final Throwable t) {
        return this.sendDeferredNotimplemented(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT sendDeferredNotimplemented() {
        return this.sendDeferredNotimplemented(CompletionStatus.COMPLETED_NO, null);
    }
    
    public NO_IMPLEMENT longDoubleNotImplemented(final CompletionStatus completionStatus, final Throwable t) {
        final NO_IMPLEMENT no_IMPLEMENT = new NO_IMPLEMENT(1398079693, completionStatus);
        if (t != null) {
            no_IMPLEMENT.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.longDoubleNotImplemented", null, ORBUtilSystemException.class, no_IMPLEMENT);
        }
        return no_IMPLEMENT;
    }
    
    public NO_IMPLEMENT longDoubleNotImplemented(final CompletionStatus completionStatus) {
        return this.longDoubleNotImplemented(completionStatus, null);
    }
    
    public NO_IMPLEMENT longDoubleNotImplemented(final Throwable t) {
        return this.longDoubleNotImplemented(CompletionStatus.COMPLETED_NO, t);
    }
    
    public NO_IMPLEMENT longDoubleNotImplemented() {
        return this.longDoubleNotImplemented(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER noServerScInDispatch(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398079689, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noServerScInDispatch", null, ORBUtilSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER noServerScInDispatch(final CompletionStatus completionStatus) {
        return this.noServerScInDispatch(completionStatus, null);
    }
    
    public OBJ_ADAPTER noServerScInDispatch(final Throwable t) {
        return this.noServerScInDispatch(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER noServerScInDispatch() {
        return this.noServerScInDispatch(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER orbConnectError(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398079690, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.orbConnectError", null, ORBUtilSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER orbConnectError(final CompletionStatus completionStatus) {
        return this.orbConnectError(completionStatus, null);
    }
    
    public OBJ_ADAPTER orbConnectError(final Throwable t) {
        return this.orbConnectError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER orbConnectError() {
        return this.orbConnectError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJ_ADAPTER adapterInactiveInActivation(final CompletionStatus completionStatus, final Throwable t) {
        final OBJ_ADAPTER obj_ADAPTER = new OBJ_ADAPTER(1398079691, completionStatus);
        if (t != null) {
            obj_ADAPTER.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.adapterInactiveInActivation", null, ORBUtilSystemException.class, obj_ADAPTER);
        }
        return obj_ADAPTER;
    }
    
    public OBJ_ADAPTER adapterInactiveInActivation(final CompletionStatus completionStatus) {
        return this.adapterInactiveInActivation(completionStatus, null);
    }
    
    public OBJ_ADAPTER adapterInactiveInActivation(final Throwable t) {
        return this.adapterInactiveInActivation(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJ_ADAPTER adapterInactiveInActivation() {
        return this.adapterInactiveInActivation(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST locateUnknownObject(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079689, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.locateUnknownObject", null, ORBUtilSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST locateUnknownObject(final CompletionStatus completionStatus) {
        return this.locateUnknownObject(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST locateUnknownObject(final Throwable t) {
        return this.locateUnknownObject(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST locateUnknownObject() {
        return this.locateUnknownObject(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST badServerId(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079690, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.badServerId", null, ORBUtilSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST badServerId(final CompletionStatus completionStatus) {
        return this.badServerId(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST badServerId(final Throwable t) {
        return this.badServerId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST badServerId() {
        return this.badServerId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST badSkeleton(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079691, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badSkeleton", null, ORBUtilSystemException.class, object_NOT_EXIST);
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
    
    public OBJECT_NOT_EXIST servantNotFound(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079692, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.servantNotFound", null, ORBUtilSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST servantNotFound(final CompletionStatus completionStatus) {
        return this.servantNotFound(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST servantNotFound(final Throwable t) {
        return this.servantNotFound(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST servantNotFound() {
        return this.servantNotFound(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST noObjectAdapterFactory(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079693, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noObjectAdapterFactory", null, ORBUtilSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST noObjectAdapterFactory(final CompletionStatus completionStatus) {
        return this.noObjectAdapterFactory(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST noObjectAdapterFactory(final Throwable t) {
        return this.noObjectAdapterFactory(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST noObjectAdapterFactory() {
        return this.noObjectAdapterFactory(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST badAdapterId(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079694, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.badAdapterId", null, ORBUtilSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST badAdapterId(final CompletionStatus completionStatus) {
        return this.badAdapterId(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST badAdapterId(final Throwable t) {
        return this.badAdapterId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST badAdapterId() {
        return this.badAdapterId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public OBJECT_NOT_EXIST dynAnyDestroyed(final CompletionStatus completionStatus, final Throwable t) {
        final OBJECT_NOT_EXIST object_NOT_EXIST = new OBJECT_NOT_EXIST(1398079695, completionStatus);
        if (t != null) {
            object_NOT_EXIST.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.dynAnyDestroyed", null, ORBUtilSystemException.class, object_NOT_EXIST);
        }
        return object_NOT_EXIST;
    }
    
    public OBJECT_NOT_EXIST dynAnyDestroyed(final CompletionStatus completionStatus) {
        return this.dynAnyDestroyed(completionStatus, null);
    }
    
    public OBJECT_NOT_EXIST dynAnyDestroyed(final Throwable t) {
        return this.dynAnyDestroyed(CompletionStatus.COMPLETED_NO, t);
    }
    
    public OBJECT_NOT_EXIST dynAnyDestroyed() {
        return this.dynAnyDestroyed(CompletionStatus.COMPLETED_NO, null);
    }
    
    public TRANSIENT requestCanceled(final CompletionStatus completionStatus, final Throwable t) {
        final TRANSIENT transient1 = new TRANSIENT(1398079689, completionStatus);
        if (t != null) {
            transient1.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.requestCanceled", null, ORBUtilSystemException.class, transient1);
        }
        return transient1;
    }
    
    public TRANSIENT requestCanceled(final CompletionStatus completionStatus) {
        return this.requestCanceled(completionStatus, null);
    }
    
    public TRANSIENT requestCanceled(final Throwable t) {
        return this.requestCanceled(CompletionStatus.COMPLETED_NO, t);
    }
    
    public TRANSIENT requestCanceled() {
        return this.requestCanceled(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownCorbaExc(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079689, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unknownCorbaExc", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownCorbaExc(final CompletionStatus completionStatus) {
        return this.unknownCorbaExc(completionStatus, null);
    }
    
    public UNKNOWN unknownCorbaExc(final Throwable t) {
        return this.unknownCorbaExc(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownCorbaExc() {
        return this.unknownCorbaExc(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN runtimeexception(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079690, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.runtimeexception", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN runtimeexception(final CompletionStatus completionStatus) {
        return this.runtimeexception(completionStatus, null);
    }
    
    public UNKNOWN runtimeexception(final Throwable t) {
        return this.runtimeexception(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN runtimeexception() {
        return this.runtimeexception(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownServerError(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079691, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unknownServerError", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownServerError(final CompletionStatus completionStatus) {
        return this.unknownServerError(completionStatus, null);
    }
    
    public UNKNOWN unknownServerError(final Throwable t) {
        return this.unknownServerError(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownServerError() {
        return this.unknownServerError(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownDsiSysex(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079692, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unknownDsiSysex", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownDsiSysex(final CompletionStatus completionStatus) {
        return this.unknownDsiSysex(completionStatus, null);
    }
    
    public UNKNOWN unknownDsiSysex(final Throwable t) {
        return this.unknownDsiSysex(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownDsiSysex() {
        return this.unknownDsiSysex(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownSysex(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079693, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.unknownSysex", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownSysex(final CompletionStatus completionStatus) {
        return this.unknownSysex(completionStatus, null);
    }
    
    public UNKNOWN unknownSysex(final Throwable t) {
        return this.unknownSysex(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownSysex() {
        return this.unknownSysex(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN wrongInterfaceDef(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079694, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.wrongInterfaceDef", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN wrongInterfaceDef(final CompletionStatus completionStatus) {
        return this.wrongInterfaceDef(completionStatus, null);
    }
    
    public UNKNOWN wrongInterfaceDef(final Throwable t) {
        return this.wrongInterfaceDef(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN wrongInterfaceDef() {
        return this.wrongInterfaceDef(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN noInterfaceDefStub(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079695, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "ORBUTIL.noInterfaceDefStub", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN noInterfaceDefStub(final CompletionStatus completionStatus) {
        return this.noInterfaceDefStub(completionStatus, null);
    }
    
    public UNKNOWN noInterfaceDefStub(final Throwable t) {
        return this.noInterfaceDefStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN noInterfaceDefStub() {
        return this.noInterfaceDefStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public UNKNOWN unknownExceptionInDispatch(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398079697, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "ORBUTIL.unknownExceptionInDispatch", null, ORBUtilSystemException.class, unknown);
        }
        return unknown;
    }
    
    public UNKNOWN unknownExceptionInDispatch(final CompletionStatus completionStatus) {
        return this.unknownExceptionInDispatch(completionStatus, null);
    }
    
    public UNKNOWN unknownExceptionInDispatch(final Throwable t) {
        return this.unknownExceptionInDispatch(CompletionStatus.COMPLETED_NO, t);
    }
    
    public UNKNOWN unknownExceptionInDispatch() {
        return this.unknownExceptionInDispatch(CompletionStatus.COMPLETED_NO, null);
    }
    
    static {
        ORBUtilSystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new ORBUtilSystemException(logger);
            }
        };
    }
}
