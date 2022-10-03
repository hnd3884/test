package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynArray;

public class DynArrayImpl extends DynAnyCollectionImpl implements DynArray
{
    private DynArrayImpl() {
        this(null, null, false);
    }
    
    protected DynArrayImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
    }
    
    protected DynArrayImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
    }
    
    @Override
    protected boolean initializeComponentsFromAny() {
        this.any.type();
        final int bound = this.getBound();
        final TypeCode contentType = this.getContentType();
        InputStream create_input_stream;
        try {
            create_input_stream = this.any.create_input_stream();
        }
        catch (final BAD_OPERATION bad_OPERATION) {
            return false;
        }
        this.components = new DynAny[bound];
        this.anys = new Any[bound];
        for (int i = 0; i < bound; ++i) {
            this.anys[i] = DynAnyUtil.extractAnyFromStream(contentType, create_input_stream, this.orb);
            try {
                this.components[i] = DynAnyUtil.createMostDerivedDynAny(this.anys[i], this.orb, false);
            }
            catch (final InconsistentTypeCode inconsistentTypeCode) {}
        }
        return true;
    }
    
    @Override
    protected boolean initializeComponentsFromTypeCode() {
        this.any.type();
        final int bound = this.getBound();
        final TypeCode contentType = this.getContentType();
        this.components = new DynAny[bound];
        this.anys = new Any[bound];
        for (int i = 0; i < bound; ++i) {
            this.createDefaultComponentAt(i, contentType);
        }
        return true;
    }
    
    @Override
    protected void checkValue(final Object[] array) throws InvalidValue {
        if (array == null || array.length != this.getBound()) {
            throw new InvalidValue();
        }
    }
}
