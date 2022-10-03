package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynSequence;

public class DynSequenceImpl extends DynAnyCollectionImpl implements DynSequence
{
    private DynSequenceImpl() {
        this(null, null, false);
    }
    
    protected DynSequenceImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
    }
    
    protected DynSequenceImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
    }
    
    @Override
    protected boolean initializeComponentsFromAny() {
        this.any.type();
        final TypeCode contentType = this.getContentType();
        InputStream create_input_stream;
        try {
            create_input_stream = this.any.create_input_stream();
        }
        catch (final BAD_OPERATION bad_OPERATION) {
            return false;
        }
        final int read_long = create_input_stream.read_long();
        this.components = new DynAny[read_long];
        this.anys = new Any[read_long];
        for (int i = 0; i < read_long; ++i) {
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
        this.components = new DynAny[0];
        this.anys = new Any[0];
        return true;
    }
    
    @Override
    protected boolean initializeAnyFromComponents() {
        final OutputStream create_output_stream = this.any.create_output_stream();
        create_output_stream.write_long(this.components.length);
        for (int i = 0; i < this.components.length; ++i) {
            if (this.components[i] instanceof DynAnyImpl) {
                ((DynAnyImpl)this.components[i]).writeAny(create_output_stream);
            }
            else {
                this.components[i].to_any().write_value(create_output_stream);
            }
        }
        this.any.read_value(create_output_stream.create_input_stream(), this.any.type());
        return true;
    }
    
    @Override
    public int get_length() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.checkInitComponents() ? this.components.length : 0;
    }
    
    @Override
    public void set_length(final int n) throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        final int bound = this.getBound();
        if (bound > 0 && n > bound) {
            throw new InvalidValue();
        }
        this.checkInitComponents();
        final int length = this.components.length;
        if (n > length) {
            final DynAny[] components = new DynAny[n];
            final Any[] anys = new Any[n];
            System.arraycopy(this.components, 0, components, 0, length);
            System.arraycopy(this.anys, 0, anys, 0, length);
            this.components = components;
            this.anys = anys;
            final TypeCode contentType = this.getContentType();
            for (int i = length; i < n; ++i) {
                this.createDefaultComponentAt(i, contentType);
            }
            if (this.index == -1) {
                this.index = length;
            }
        }
        else if (n < length) {
            final DynAny[] components2 = new DynAny[n];
            final Any[] anys2 = new Any[n];
            System.arraycopy(this.components, 0, components2, 0, n);
            System.arraycopy(this.anys, 0, anys2, 0, n);
            this.components = components2;
            this.anys = anys2;
            if (n == 0 || this.index >= n) {
                this.index = -1;
            }
        }
        else if (this.index == -1 && n > 0) {
            this.index = 0;
        }
    }
    
    @Override
    protected void checkValue(final Object[] array) throws InvalidValue {
        if (array == null || array.length == 0) {
            this.clearData();
            this.index = -1;
            return;
        }
        this.index = 0;
        final int bound = this.getBound();
        if (bound > 0 && array.length > bound) {
            throw new InvalidValue();
        }
    }
}
