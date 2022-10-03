package com.sun.corba.se.impl.dynamicany;

import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

abstract class DynAnyComplexImpl extends DynAnyConstructedImpl
{
    String[] names;
    NameValuePair[] nameValuePairs;
    NameDynAnyPair[] nameDynAnyPairs;
    
    private DynAnyComplexImpl() {
        this(null, null, false);
    }
    
    protected DynAnyComplexImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
        this.names = null;
        this.nameValuePairs = null;
        this.nameDynAnyPairs = null;
    }
    
    protected DynAnyComplexImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.names = null;
        this.nameValuePairs = null;
        this.nameDynAnyPairs = null;
        this.index = 0;
    }
    
    public String current_member_name() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (!this.checkInitComponents() || this.index < 0 || this.index >= this.names.length) {
            throw new InvalidValue();
        }
        return this.names[this.index];
    }
    
    public TCKind current_member_kind() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (!this.checkInitComponents() || this.index < 0 || this.index >= this.components.length) {
            throw new InvalidValue();
        }
        return this.components[this.index].type().kind();
    }
    
    public void set_members(final NameValuePair[] array) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (array == null || array.length == 0) {
            this.clearData();
            return;
        }
        final TypeCode type = this.any.type();
        int member_count = 0;
        try {
            member_count = type.member_count();
        }
        catch (final BadKind badKind) {}
        if (member_count != array.length) {
            this.clearData();
            throw new InvalidValue();
        }
        this.allocComponents(array);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                this.clearData();
                throw new InvalidValue();
            }
            final String id = array[i].id;
            String member_name = null;
            try {
                member_name = type.member_name(i);
            }
            catch (final BadKind badKind2) {}
            catch (final Bounds bounds) {}
            if (!member_name.equals(id) && !id.equals("")) {
                this.clearData();
                throw new TypeMismatch();
            }
            final Any value = array[i].value;
            TypeCode member_type = null;
            try {
                member_type = type.member_type(i);
            }
            catch (final BadKind badKind3) {}
            catch (final Bounds bounds2) {}
            if (!member_type.equal(value.type())) {
                this.clearData();
                throw new TypeMismatch();
            }
            DynAny mostDerivedDynAny;
            try {
                mostDerivedDynAny = DynAnyUtil.createMostDerivedDynAny(value, this.orb, false);
            }
            catch (final InconsistentTypeCode inconsistentTypeCode) {
                throw new InvalidValue();
            }
            this.addComponent(i, id, value, mostDerivedDynAny);
        }
        this.index = ((array.length == 0) ? -1 : 0);
        this.representations = 4;
    }
    
    public void set_members_as_dyn_any(final NameDynAnyPair[] array) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (array == null || array.length == 0) {
            this.clearData();
            return;
        }
        final TypeCode type = this.any.type();
        int member_count = 0;
        try {
            member_count = type.member_count();
        }
        catch (final BadKind badKind) {}
        if (member_count != array.length) {
            this.clearData();
            throw new InvalidValue();
        }
        this.allocComponents(array);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                this.clearData();
                throw new InvalidValue();
            }
            final String id = array[i].id;
            String member_name = null;
            try {
                member_name = type.member_name(i);
            }
            catch (final BadKind badKind2) {}
            catch (final Bounds bounds) {}
            if (!member_name.equals(id) && !id.equals("")) {
                this.clearData();
                throw new TypeMismatch();
            }
            final DynAny value = array[i].value;
            final Any any = this.getAny(value);
            TypeCode member_type = null;
            try {
                member_type = type.member_type(i);
            }
            catch (final BadKind badKind3) {}
            catch (final Bounds bounds2) {}
            if (!member_type.equal(any.type())) {
                this.clearData();
                throw new TypeMismatch();
            }
            this.addComponent(i, id, any, value);
        }
        this.index = ((array.length == 0) ? -1 : 0);
        this.representations = 4;
    }
    
    private void allocComponents(final int n) {
        this.components = new DynAny[n];
        this.names = new String[n];
        this.nameValuePairs = new NameValuePair[n];
        this.nameDynAnyPairs = new NameDynAnyPair[n];
        for (int i = 0; i < n; ++i) {
            this.nameValuePairs[i] = new NameValuePair();
            this.nameDynAnyPairs[i] = new NameDynAnyPair();
        }
    }
    
    private void allocComponents(final NameValuePair[] nameValuePairs) {
        this.components = new DynAny[nameValuePairs.length];
        this.names = new String[nameValuePairs.length];
        this.nameValuePairs = nameValuePairs;
        this.nameDynAnyPairs = new NameDynAnyPair[nameValuePairs.length];
        for (int i = 0; i < nameValuePairs.length; ++i) {
            this.nameDynAnyPairs[i] = new NameDynAnyPair();
        }
    }
    
    private void allocComponents(final NameDynAnyPair[] nameDynAnyPairs) {
        this.components = new DynAny[nameDynAnyPairs.length];
        this.names = new String[nameDynAnyPairs.length];
        this.nameValuePairs = new NameValuePair[nameDynAnyPairs.length];
        for (int i = 0; i < nameDynAnyPairs.length; ++i) {
            this.nameValuePairs[i] = new NameValuePair();
        }
        this.nameDynAnyPairs = nameDynAnyPairs;
    }
    
    private void addComponent(final int n, final String s, final Any value, final DynAny value2) {
        this.components[n] = value2;
        this.names[n] = ((s != null) ? s : "");
        this.nameValuePairs[n].id = s;
        this.nameValuePairs[n].value = value;
        this.nameDynAnyPairs[n].id = s;
        this.nameDynAnyPairs[n].value = value2;
        if (value2 instanceof DynAnyImpl) {
            ((DynAnyImpl)value2).setStatus((byte)1);
        }
    }
    
    @Override
    protected boolean initializeComponentsFromAny() {
        final TypeCode type = this.any.type();
        TypeCode member_type = null;
        DynAny mostDerivedDynAny = null;
        String member_name = null;
        int member_count = 0;
        try {
            member_count = type.member_count();
        }
        catch (final BadKind badKind) {}
        final InputStream create_input_stream = this.any.create_input_stream();
        this.allocComponents(member_count);
        for (int i = 0; i < member_count; ++i) {
            try {
                member_name = type.member_name(i);
                member_type = type.member_type(i);
            }
            catch (final BadKind badKind2) {}
            catch (final Bounds bounds) {}
            final Any anyFromStream = DynAnyUtil.extractAnyFromStream(member_type, create_input_stream, this.orb);
            try {
                mostDerivedDynAny = DynAnyUtil.createMostDerivedDynAny(anyFromStream, this.orb, false);
            }
            catch (final InconsistentTypeCode inconsistentTypeCode) {}
            this.addComponent(i, member_name, anyFromStream, mostDerivedDynAny);
        }
        return true;
    }
    
    @Override
    protected boolean initializeComponentsFromTypeCode() {
        final TypeCode type = this.any.type();
        TypeCode member_type = null;
        DynAny mostDerivedDynAny = null;
        int member_count = 0;
        try {
            member_count = type.member_count();
        }
        catch (final BadKind badKind) {}
        this.allocComponents(member_count);
        for (int i = 0; i < member_count; ++i) {
            String member_name = null;
            try {
                member_name = type.member_name(i);
                member_type = type.member_type(i);
            }
            catch (final BadKind badKind2) {}
            catch (final Bounds bounds) {}
            try {
                mostDerivedDynAny = DynAnyUtil.createMostDerivedDynAny(member_type, this.orb);
            }
            catch (final InconsistentTypeCode inconsistentTypeCode) {}
            this.addComponent(i, member_name, this.getAny(mostDerivedDynAny), mostDerivedDynAny);
        }
        return true;
    }
    
    @Override
    protected void clearData() {
        super.clearData();
        this.names = null;
        this.nameValuePairs = null;
        this.nameDynAnyPairs = null;
    }
}
