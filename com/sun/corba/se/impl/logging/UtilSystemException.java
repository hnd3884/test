package com.sun.corba.se.impl.logging;

import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.BAD_PARAM;
import java.util.logging.Level;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Logger;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.logging.LogWrapperBase;

public class UtilSystemException extends LogWrapperBase
{
    private static LogWrapperFactory factory;
    public static final int STUB_FACTORY_COULD_NOT_MAKE_STUB = 1398080889;
    public static final int ERROR_IN_MAKE_STUB_FROM_REPOSITORY_ID = 1398080890;
    public static final int CLASS_CAST_EXCEPTION_IN_LOAD_STUB = 1398080891;
    public static final int EXCEPTION_IN_LOAD_STUB = 1398080892;
    public static final int NO_POA = 1398080890;
    public static final int CONNECT_WRONG_ORB = 1398080891;
    public static final int CONNECT_NO_TIE = 1398080892;
    public static final int CONNECT_TIE_WRONG_ORB = 1398080893;
    public static final int CONNECT_TIE_NO_SERVANT = 1398080894;
    public static final int LOAD_TIE_FAILED = 1398080895;
    public static final int BAD_HEX_DIGIT = 1398080889;
    public static final int UNABLE_LOCATE_VALUE_HELPER = 1398080890;
    public static final int INVALID_INDIRECTION = 1398080891;
    public static final int OBJECT_NOT_CONNECTED = 1398080889;
    public static final int COULD_NOT_LOAD_STUB = 1398080890;
    public static final int OBJECT_NOT_EXPORTED = 1398080891;
    public static final int ERROR_SET_OBJECT_FIELD = 1398080889;
    public static final int ERROR_SET_BOOLEAN_FIELD = 1398080890;
    public static final int ERROR_SET_BYTE_FIELD = 1398080891;
    public static final int ERROR_SET_CHAR_FIELD = 1398080892;
    public static final int ERROR_SET_SHORT_FIELD = 1398080893;
    public static final int ERROR_SET_INT_FIELD = 1398080894;
    public static final int ERROR_SET_LONG_FIELD = 1398080895;
    public static final int ERROR_SET_FLOAT_FIELD = 1398080896;
    public static final int ERROR_SET_DOUBLE_FIELD = 1398080897;
    public static final int ILLEGAL_FIELD_ACCESS = 1398080898;
    public static final int BAD_BEGIN_UNMARSHAL_CUSTOM_VALUE = 1398080899;
    public static final int CLASS_NOT_FOUND = 1398080900;
    public static final int UNKNOWN_SYSEX = 1398080889;
    
    public UtilSystemException(final Logger logger) {
        super(logger);
    }
    
    public static UtilSystemException get(final ORB orb, final String s) {
        return (UtilSystemException)orb.getLogWrapper(s, "UTIL", UtilSystemException.factory);
    }
    
    public static UtilSystemException get(final String s) {
        return (UtilSystemException)ORB.staticGetLogWrapper(s, "UTIL", UtilSystemException.factory);
    }
    
    public BAD_OPERATION stubFactoryCouldNotMakeStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080889, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "UTIL.stubFactoryCouldNotMakeStub", null, UtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION stubFactoryCouldNotMakeStub(final CompletionStatus completionStatus) {
        return this.stubFactoryCouldNotMakeStub(completionStatus, null);
    }
    
    public BAD_OPERATION stubFactoryCouldNotMakeStub(final Throwable t) {
        return this.stubFactoryCouldNotMakeStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION stubFactoryCouldNotMakeStub() {
        return this.stubFactoryCouldNotMakeStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION errorInMakeStubFromRepositoryId(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080890, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "UTIL.errorInMakeStubFromRepositoryId", null, UtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION errorInMakeStubFromRepositoryId(final CompletionStatus completionStatus) {
        return this.errorInMakeStubFromRepositoryId(completionStatus, null);
    }
    
    public BAD_OPERATION errorInMakeStubFromRepositoryId(final Throwable t) {
        return this.errorInMakeStubFromRepositoryId(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION errorInMakeStubFromRepositoryId() {
        return this.errorInMakeStubFromRepositoryId(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION classCastExceptionInLoadStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080891, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "UTIL.classCastExceptionInLoadStub", null, UtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION classCastExceptionInLoadStub(final CompletionStatus completionStatus) {
        return this.classCastExceptionInLoadStub(completionStatus, null);
    }
    
    public BAD_OPERATION classCastExceptionInLoadStub(final Throwable t) {
        return this.classCastExceptionInLoadStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION classCastExceptionInLoadStub() {
        return this.classCastExceptionInLoadStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_OPERATION exceptionInLoadStub(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_OPERATION bad_OPERATION = new BAD_OPERATION(1398080892, completionStatus);
        if (t != null) {
            bad_OPERATION.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "UTIL.exceptionInLoadStub", null, UtilSystemException.class, bad_OPERATION);
        }
        return bad_OPERATION;
    }
    
    public BAD_OPERATION exceptionInLoadStub(final CompletionStatus completionStatus) {
        return this.exceptionInLoadStub(completionStatus, null);
    }
    
    public BAD_OPERATION exceptionInLoadStub(final Throwable t) {
        return this.exceptionInLoadStub(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_OPERATION exceptionInLoadStub() {
        return this.exceptionInLoadStub(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM noPoa(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080890, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.noPoa", null, UtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM noPoa(final CompletionStatus completionStatus) {
        return this.noPoa(completionStatus, null);
    }
    
    public BAD_PARAM noPoa(final Throwable t) {
        return this.noPoa(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM noPoa() {
        return this.noPoa(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM connectWrongOrb(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080891, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "UTIL.connectWrongOrb", null, UtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM connectWrongOrb(final CompletionStatus completionStatus) {
        return this.connectWrongOrb(completionStatus, null);
    }
    
    public BAD_PARAM connectWrongOrb(final Throwable t) {
        return this.connectWrongOrb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM connectWrongOrb() {
        return this.connectWrongOrb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM connectNoTie(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080892, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.connectNoTie", null, UtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM connectNoTie(final CompletionStatus completionStatus) {
        return this.connectNoTie(completionStatus, null);
    }
    
    public BAD_PARAM connectNoTie(final Throwable t) {
        return this.connectNoTie(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM connectNoTie() {
        return this.connectNoTie(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM connectTieWrongOrb(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080893, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.connectTieWrongOrb", null, UtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM connectTieWrongOrb(final CompletionStatus completionStatus) {
        return this.connectTieWrongOrb(completionStatus, null);
    }
    
    public BAD_PARAM connectTieWrongOrb(final Throwable t) {
        return this.connectTieWrongOrb(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM connectTieWrongOrb() {
        return this.connectTieWrongOrb(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM connectTieNoServant(final CompletionStatus completionStatus, final Throwable t) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080894, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.connectTieNoServant", null, UtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM connectTieNoServant(final CompletionStatus completionStatus) {
        return this.connectTieNoServant(completionStatus, null);
    }
    
    public BAD_PARAM connectTieNoServant(final Throwable t) {
        return this.connectTieNoServant(CompletionStatus.COMPLETED_NO, t);
    }
    
    public BAD_PARAM connectTieNoServant() {
        return this.connectTieNoServant(CompletionStatus.COMPLETED_NO, null);
    }
    
    public BAD_PARAM loadTieFailed(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final BAD_PARAM bad_PARAM = new BAD_PARAM(1398080895, completionStatus);
        if (t != null) {
            bad_PARAM.initCause(t);
        }
        if (this.logger.isLoggable(Level.FINE)) {
            this.doLog(Level.FINE, "UTIL.loadTieFailed", new Object[] { o }, UtilSystemException.class, bad_PARAM);
        }
        return bad_PARAM;
    }
    
    public BAD_PARAM loadTieFailed(final CompletionStatus completionStatus, final Object o) {
        return this.loadTieFailed(completionStatus, null, o);
    }
    
    public BAD_PARAM loadTieFailed(final Throwable t, final Object o) {
        return this.loadTieFailed(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public BAD_PARAM loadTieFailed(final Object o) {
        return this.loadTieFailed(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public DATA_CONVERSION badHexDigit(final CompletionStatus completionStatus, final Throwable t) {
        final DATA_CONVERSION data_CONVERSION = new DATA_CONVERSION(1398080889, completionStatus);
        if (t != null) {
            data_CONVERSION.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.badHexDigit", null, UtilSystemException.class, data_CONVERSION);
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
    
    public MARSHAL unableLocateValueHelper(final CompletionStatus completionStatus, final Throwable t) {
        final MARSHAL marshal = new MARSHAL(1398080890, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.unableLocateValueHelper", null, UtilSystemException.class, marshal);
        }
        return marshal;
    }
    
    public MARSHAL unableLocateValueHelper(final CompletionStatus completionStatus) {
        return this.unableLocateValueHelper(completionStatus, null);
    }
    
    public MARSHAL unableLocateValueHelper(final Throwable t) {
        return this.unableLocateValueHelper(CompletionStatus.COMPLETED_NO, t);
    }
    
    public MARSHAL unableLocateValueHelper() {
        return this.unableLocateValueHelper(CompletionStatus.COMPLETED_NO, null);
    }
    
    public MARSHAL invalidIndirection(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final MARSHAL marshal = new MARSHAL(1398080891, completionStatus);
        if (t != null) {
            marshal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.invalidIndirection", new Object[] { o }, UtilSystemException.class, marshal);
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
    
    public INV_OBJREF objectNotConnected(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1398080889, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.objectNotConnected", new Object[] { o }, UtilSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF objectNotConnected(final CompletionStatus completionStatus, final Object o) {
        return this.objectNotConnected(completionStatus, null, o);
    }
    
    public INV_OBJREF objectNotConnected(final Throwable t, final Object o) {
        return this.objectNotConnected(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INV_OBJREF objectNotConnected(final Object o) {
        return this.objectNotConnected(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INV_OBJREF couldNotLoadStub(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1398080890, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.couldNotLoadStub", new Object[] { o }, UtilSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF couldNotLoadStub(final CompletionStatus completionStatus, final Object o) {
        return this.couldNotLoadStub(completionStatus, null, o);
    }
    
    public INV_OBJREF couldNotLoadStub(final Throwable t, final Object o) {
        return this.couldNotLoadStub(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INV_OBJREF couldNotLoadStub(final Object o) {
        return this.couldNotLoadStub(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INV_OBJREF objectNotExported(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INV_OBJREF inv_OBJREF = new INV_OBJREF(1398080891, completionStatus);
        if (t != null) {
            inv_OBJREF.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.objectNotExported", new Object[] { o }, UtilSystemException.class, inv_OBJREF);
        }
        return inv_OBJREF;
    }
    
    public INV_OBJREF objectNotExported(final CompletionStatus completionStatus, final Object o) {
        return this.objectNotExported(completionStatus, null, o);
    }
    
    public INV_OBJREF objectNotExported(final Throwable t, final Object o) {
        return this.objectNotExported(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INV_OBJREF objectNotExported(final Object o) {
        return this.objectNotExported(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL errorSetObjectField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080889, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetObjectField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetObjectField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetObjectField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetObjectField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetObjectField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetObjectField(final Object o, final Object o2, final Object o3) {
        return this.errorSetObjectField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetBooleanField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080890, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetBooleanField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetBooleanField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetBooleanField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetBooleanField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetBooleanField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetBooleanField(final Object o, final Object o2, final Object o3) {
        return this.errorSetBooleanField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetByteField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080891, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetByteField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetByteField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetByteField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetByteField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetByteField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetByteField(final Object o, final Object o2, final Object o3) {
        return this.errorSetByteField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetCharField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080892, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetCharField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetCharField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetCharField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetCharField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetCharField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetCharField(final Object o, final Object o2, final Object o3) {
        return this.errorSetCharField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetShortField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080893, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetShortField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetShortField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetShortField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetShortField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetShortField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetShortField(final Object o, final Object o2, final Object o3) {
        return this.errorSetShortField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetIntField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080894, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetIntField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetIntField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetIntField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetIntField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetIntField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetIntField(final Object o, final Object o2, final Object o3) {
        return this.errorSetIntField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetLongField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080895, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetLongField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetLongField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetLongField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetLongField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetLongField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetLongField(final Object o, final Object o2, final Object o3) {
        return this.errorSetLongField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetFloatField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080896, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetFloatField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetFloatField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetFloatField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetFloatField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetFloatField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetFloatField(final Object o, final Object o2, final Object o3) {
        return this.errorSetFloatField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL errorSetDoubleField(final CompletionStatus completionStatus, final Throwable t, final Object o, final Object o2, final Object o3) {
        final INTERNAL internal = new INTERNAL(1398080897, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.errorSetDoubleField", new Object[] { o, o2, o3 }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL errorSetDoubleField(final CompletionStatus completionStatus, final Object o, final Object o2, final Object o3) {
        return this.errorSetDoubleField(completionStatus, null, o, o2, o3);
    }
    
    public INTERNAL errorSetDoubleField(final Throwable t, final Object o, final Object o2, final Object o3) {
        return this.errorSetDoubleField(CompletionStatus.COMPLETED_NO, t, o, o2, o3);
    }
    
    public INTERNAL errorSetDoubleField(final Object o, final Object o2, final Object o3) {
        return this.errorSetDoubleField(CompletionStatus.COMPLETED_NO, null, o, o2, o3);
    }
    
    public INTERNAL illegalFieldAccess(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080898, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.illegalFieldAccess", new Object[] { o }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL illegalFieldAccess(final CompletionStatus completionStatus, final Object o) {
        return this.illegalFieldAccess(completionStatus, null, o);
    }
    
    public INTERNAL illegalFieldAccess(final Throwable t, final Object o) {
        return this.illegalFieldAccess(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL illegalFieldAccess(final Object o) {
        return this.illegalFieldAccess(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public INTERNAL badBeginUnmarshalCustomValue(final CompletionStatus completionStatus, final Throwable t) {
        final INTERNAL internal = new INTERNAL(1398080899, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.badBeginUnmarshalCustomValue", null, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL badBeginUnmarshalCustomValue(final CompletionStatus completionStatus) {
        return this.badBeginUnmarshalCustomValue(completionStatus, null);
    }
    
    public INTERNAL badBeginUnmarshalCustomValue(final Throwable t) {
        return this.badBeginUnmarshalCustomValue(CompletionStatus.COMPLETED_NO, t);
    }
    
    public INTERNAL badBeginUnmarshalCustomValue() {
        return this.badBeginUnmarshalCustomValue(CompletionStatus.COMPLETED_NO, null);
    }
    
    public INTERNAL classNotFound(final CompletionStatus completionStatus, final Throwable t, final Object o) {
        final INTERNAL internal = new INTERNAL(1398080900, completionStatus);
        if (t != null) {
            internal.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.classNotFound", new Object[] { o }, UtilSystemException.class, internal);
        }
        return internal;
    }
    
    public INTERNAL classNotFound(final CompletionStatus completionStatus, final Object o) {
        return this.classNotFound(completionStatus, null, o);
    }
    
    public INTERNAL classNotFound(final Throwable t, final Object o) {
        return this.classNotFound(CompletionStatus.COMPLETED_NO, t, o);
    }
    
    public INTERNAL classNotFound(final Object o) {
        return this.classNotFound(CompletionStatus.COMPLETED_NO, null, o);
    }
    
    public UNKNOWN unknownSysex(final CompletionStatus completionStatus, final Throwable t) {
        final UNKNOWN unknown = new UNKNOWN(1398080889, completionStatus);
        if (t != null) {
            unknown.initCause(t);
        }
        if (this.logger.isLoggable(Level.WARNING)) {
            this.doLog(Level.WARNING, "UTIL.unknownSysex", null, UtilSystemException.class, unknown);
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
    
    static {
        UtilSystemException.factory = new LogWrapperFactory() {
            @Override
            public LogWrapperBase create(final Logger logger) {
                return new UtilSystemException(logger);
            }
        };
    }
}
