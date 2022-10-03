package com.sun.corba.se.impl.dynamicany;

import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import java.math.BigDecimal;
import org.omg.CORBA.Object;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.corba.AnyImpl;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.TypeCode;

public class DynAnyUtil
{
    static boolean isConsistentType(final TypeCode typeCode) {
        final int value = typeCode.kind().value();
        return value != 13 && value != 31 && value != 32;
    }
    
    static boolean isConstructedDynAny(final DynAny dynAny) {
        final int value = dynAny.type().kind().value();
        return value == 19 || value == 15 || value == 20 || value == 16 || value == 17 || value == 28 || value == 29 || value == 30;
    }
    
    static DynAny createMostDerivedDynAny(final Any any, final ORB orb, final boolean b) throws InconsistentTypeCode {
        if (any == null || !isConsistentType(any.type())) {
            throw new InconsistentTypeCode();
        }
        switch (any.type().kind().value()) {
            case 19: {
                return new DynSequenceImpl(orb, any, b);
            }
            case 15: {
                return new DynStructImpl(orb, any, b);
            }
            case 20: {
                return new DynArrayImpl(orb, any, b);
            }
            case 16: {
                return new DynUnionImpl(orb, any, b);
            }
            case 17: {
                return new DynEnumImpl(orb, any, b);
            }
            case 28: {
                return new DynFixedImpl(orb, any, b);
            }
            case 29: {
                return new DynValueImpl(orb, any, b);
            }
            case 30: {
                return new DynValueBoxImpl(orb, any, b);
            }
            default: {
                return new DynAnyBasicImpl(orb, any, b);
            }
        }
    }
    
    static DynAny createMostDerivedDynAny(final TypeCode typeCode, final ORB orb) throws InconsistentTypeCode {
        if (typeCode == null || !isConsistentType(typeCode)) {
            throw new InconsistentTypeCode();
        }
        switch (typeCode.kind().value()) {
            case 19: {
                return new DynSequenceImpl(orb, typeCode);
            }
            case 15: {
                return new DynStructImpl(orb, typeCode);
            }
            case 20: {
                return new DynArrayImpl(orb, typeCode);
            }
            case 16: {
                return new DynUnionImpl(orb, typeCode);
            }
            case 17: {
                return new DynEnumImpl(orb, typeCode);
            }
            case 28: {
                return new DynFixedImpl(orb, typeCode);
            }
            case 29: {
                return new DynValueImpl(orb, typeCode);
            }
            case 30: {
                return new DynValueBoxImpl(orb, typeCode);
            }
            default: {
                return new DynAnyBasicImpl(orb, typeCode);
            }
        }
    }
    
    static Any extractAnyFromStream(final TypeCode typeCode, final InputStream inputStream, final ORB orb) {
        return AnyImpl.extractAnyFromStream(typeCode, inputStream, orb);
    }
    
    static Any createDefaultAnyOfType(final TypeCode typeCode, final ORB orb) {
        final ORBUtilSystemException value = ORBUtilSystemException.get(orb, "rpc.presentation");
        final Any create_any = orb.create_any();
        switch (typeCode.kind().value()) {
            case 8: {
                create_any.insert_boolean(false);
                break;
            }
            case 2: {
                create_any.insert_short((short)0);
                break;
            }
            case 4: {
                create_any.insert_ushort((short)0);
                break;
            }
            case 3: {
                create_any.insert_long(0);
                break;
            }
            case 5: {
                create_any.insert_ulong(0);
                break;
            }
            case 23: {
                create_any.insert_longlong(0L);
                break;
            }
            case 24: {
                create_any.insert_ulonglong(0L);
                break;
            }
            case 6: {
                create_any.insert_float(0.0f);
                break;
            }
            case 7: {
                create_any.insert_double(0.0);
                break;
            }
            case 10: {
                create_any.insert_octet((byte)0);
                break;
            }
            case 9: {
                create_any.insert_char('\0');
                break;
            }
            case 26: {
                create_any.insert_wchar('\0');
                break;
            }
            case 18: {
                create_any.type(typeCode);
                create_any.insert_string("");
                break;
            }
            case 27: {
                create_any.type(typeCode);
                create_any.insert_wstring("");
                break;
            }
            case 14: {
                create_any.insert_Object(null);
                break;
            }
            case 12: {
                create_any.insert_TypeCode(create_any.type());
                break;
            }
            case 11: {
                create_any.insert_any(orb.create_any());
                break;
            }
            case 15:
            case 16:
            case 17:
            case 19:
            case 20:
            case 22:
            case 29:
            case 30: {
                create_any.type(typeCode);
                break;
            }
            case 28: {
                create_any.insert_fixed(new BigDecimal("0.0"), typeCode);
                break;
            }
            case 1:
            case 13:
            case 21:
            case 31:
            case 32: {
                create_any.type(typeCode);
                break;
            }
            case 0: {
                break;
            }
            case 25: {
                throw value.tkLongDoubleNotSupported();
            }
            default: {
                throw value.typecodeNotSupported();
            }
        }
        return create_any;
    }
    
    static Any copy(final Any any, final ORB orb) {
        return new AnyImpl(orb, any);
    }
    
    static DynAny convertToNative(final DynAny dynAny, final ORB orb) {
        if (dynAny instanceof DynAnyImpl) {
            return dynAny;
        }
        try {
            return createMostDerivedDynAny(dynAny.to_any(), orb, true);
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {
            return null;
        }
    }
    
    static boolean isInitialized(final Any any) {
        final boolean initialized = ((AnyImpl)any).isInitialized();
        switch (any.type().kind().value()) {
            case 18: {
                return initialized && any.extract_string() != null;
            }
            case 27: {
                return initialized && any.extract_wstring() != null;
            }
            default: {
                return initialized;
            }
        }
    }
    
    static boolean set_current_component(final DynAny dynAny, final DynAny dynAny2) {
        if (dynAny2 != null) {
            try {
                dynAny.rewind();
                while (dynAny.current_component() != dynAny2) {
                    if (!dynAny.next()) {
                        return false;
                    }
                }
                return true;
            }
            catch (final TypeMismatch typeMismatch) {}
        }
        return false;
    }
}
