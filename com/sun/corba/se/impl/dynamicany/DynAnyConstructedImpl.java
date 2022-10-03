package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAny;

abstract class DynAnyConstructedImpl extends DynAnyImpl
{
    protected static final byte REPRESENTATION_NONE = 0;
    protected static final byte REPRESENTATION_TYPECODE = 1;
    protected static final byte REPRESENTATION_ANY = 2;
    protected static final byte REPRESENTATION_COMPONENTS = 4;
    protected static final byte RECURSIVE_UNDEF = -1;
    protected static final byte RECURSIVE_NO = 0;
    protected static final byte RECURSIVE_YES = 1;
    protected static final DynAny[] emptyComponents;
    DynAny[] components;
    byte representations;
    byte isRecursive;
    
    private DynAnyConstructedImpl() {
        this(null, null, false);
    }
    
    protected DynAnyConstructedImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
        this.components = DynAnyConstructedImpl.emptyComponents;
        this.representations = 0;
        this.isRecursive = -1;
        if (this.any != null) {
            this.representations = 2;
        }
        this.index = 0;
    }
    
    protected DynAnyConstructedImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.components = DynAnyConstructedImpl.emptyComponents;
        this.representations = 0;
        this.isRecursive = -1;
        if (typeCode != null) {
            this.representations = 1;
        }
        this.index = -1;
    }
    
    protected boolean isRecursive() {
        if (this.isRecursive == -1) {
            final TypeCode type = this.any.type();
            if (type instanceof TypeCodeImpl) {
                if (((TypeCodeImpl)type).is_recursive()) {
                    this.isRecursive = 1;
                }
                else {
                    this.isRecursive = 0;
                }
            }
            else {
                this.isRecursive = 0;
            }
        }
        return this.isRecursive == 1;
    }
    
    @Override
    public DynAny current_component() throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            return null;
        }
        return this.checkInitComponents() ? this.components[this.index] : null;
    }
    
    @Override
    public int component_count() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.checkInitComponents() ? this.components.length : 0;
    }
    
    @Override
    public boolean next() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (!this.checkInitComponents()) {
            return false;
        }
        ++this.index;
        if (this.index >= 0 && this.index < this.components.length) {
            return true;
        }
        this.index = -1;
        return false;
    }
    
    @Override
    public boolean seek(final int index) {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (index < 0) {
            this.index = -1;
            return false;
        }
        if (!this.checkInitComponents()) {
            return false;
        }
        if (index < this.components.length) {
            this.index = index;
            return true;
        }
        return false;
    }
    
    @Override
    public void rewind() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.seek(0);
    }
    
    @Override
    protected void clearData() {
        super.clearData();
        this.components = DynAnyConstructedImpl.emptyComponents;
        this.index = -1;
        this.representations = 0;
    }
    
    @Override
    protected void writeAny(final OutputStream outputStream) {
        this.checkInitAny();
        super.writeAny(outputStream);
    }
    
    protected boolean checkInitComponents() {
        if ((this.representations & 0x4) == 0x0) {
            if ((this.representations & 0x2) != 0x0) {
                if (!this.initializeComponentsFromAny()) {
                    return false;
                }
                this.representations |= 0x4;
            }
            else if ((this.representations & 0x1) != 0x0) {
                if (!this.initializeComponentsFromTypeCode()) {
                    return false;
                }
                this.representations |= 0x4;
            }
        }
        return true;
    }
    
    protected void checkInitAny() {
        if ((this.representations & 0x2) == 0x0) {
            if ((this.representations & 0x4) != 0x0) {
                if (this.initializeAnyFromComponents()) {
                    this.representations |= 0x2;
                }
            }
            else if ((this.representations & 0x1) != 0x0) {
                if (this.representations == 1 && this.isRecursive()) {
                    return;
                }
                if (this.initializeComponentsFromTypeCode()) {
                    this.representations |= 0x4;
                }
                if (this.initializeAnyFromComponents()) {
                    this.representations |= 0x2;
                }
            }
        }
    }
    
    protected abstract boolean initializeComponentsFromAny();
    
    protected abstract boolean initializeComponentsFromTypeCode();
    
    protected boolean initializeAnyFromComponents() {
        final OutputStream create_output_stream = this.any.create_output_stream();
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
    public void assign(final DynAny dynAny) throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.clearData();
        super.assign(dynAny);
        this.representations = 2;
        this.index = 0;
    }
    
    @Override
    public void from_any(final Any any) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.clearData();
        super.from_any(any);
        this.representations = 2;
        this.index = 0;
    }
    
    @Override
    public Any to_any() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.checkInitAny();
        return DynAnyUtil.copy(this.any, this.orb);
    }
    
    @Override
    public boolean equal(final DynAny dynAny) {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (dynAny == this) {
            return true;
        }
        if (!this.any.type().equal(dynAny.type())) {
            return false;
        }
        if (!this.checkInitComponents()) {
            return false;
        }
        DynAny current_component = null;
        try {
            current_component = dynAny.current_component();
            for (int i = 0; i < this.components.length; ++i) {
                if (!dynAny.seek(i)) {
                    return false;
                }
                if (!this.components[i].equal(dynAny.current_component())) {
                    return false;
                }
            }
        }
        catch (final TypeMismatch typeMismatch) {}
        finally {
            DynAnyUtil.set_current_component(dynAny, current_component);
        }
        return true;
    }
    
    @Override
    public void destroy() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.status == 0) {
            this.status = 2;
            for (int i = 0; i < this.components.length; ++i) {
                if (this.components[i] instanceof DynAnyImpl) {
                    ((DynAnyImpl)this.components[i]).setStatus((byte)0);
                }
                this.components[i].destroy();
            }
        }
    }
    
    @Override
    public DynAny copy() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.checkInitAny();
        try {
            return DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, true);
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {
            return null;
        }
    }
    
    @Override
    public void insert_boolean(final boolean b) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_boolean(b);
    }
    
    @Override
    public void insert_octet(final byte b) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_octet(b);
    }
    
    @Override
    public void insert_char(final char c) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_char(c);
    }
    
    @Override
    public void insert_short(final short n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_short(n);
    }
    
    @Override
    public void insert_ushort(final short n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_ushort(n);
    }
    
    @Override
    public void insert_long(final int n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_long(n);
    }
    
    @Override
    public void insert_ulong(final int n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_ulong(n);
    }
    
    @Override
    public void insert_float(final float n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_float(n);
    }
    
    @Override
    public void insert_double(final double n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_double(n);
    }
    
    @Override
    public void insert_string(final String s) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_string(s);
    }
    
    @Override
    public void insert_reference(final Object object) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_reference(object);
    }
    
    @Override
    public void insert_typecode(final TypeCode typeCode) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_typecode(typeCode);
    }
    
    @Override
    public void insert_longlong(final long n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_longlong(n);
    }
    
    @Override
    public void insert_ulonglong(final long n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_ulonglong(n);
    }
    
    @Override
    public void insert_wchar(final char c) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_wchar(c);
    }
    
    @Override
    public void insert_wstring(final String s) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_wstring(s);
    }
    
    @Override
    public void insert_any(final Any any) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_any(any);
    }
    
    @Override
    public void insert_dyn_any(final DynAny dynAny) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_dyn_any(dynAny);
    }
    
    @Override
    public void insert_val(final Serializable s) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        current_component.insert_val(s);
    }
    
    @Override
    public Serializable get_val() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_val();
    }
    
    @Override
    public boolean get_boolean() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_boolean();
    }
    
    @Override
    public byte get_octet() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_octet();
    }
    
    @Override
    public char get_char() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_char();
    }
    
    @Override
    public short get_short() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_short();
    }
    
    @Override
    public short get_ushort() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_ushort();
    }
    
    @Override
    public int get_long() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_long();
    }
    
    @Override
    public int get_ulong() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_ulong();
    }
    
    @Override
    public float get_float() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_float();
    }
    
    @Override
    public double get_double() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_double();
    }
    
    @Override
    public String get_string() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_string();
    }
    
    @Override
    public Object get_reference() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_reference();
    }
    
    @Override
    public TypeCode get_typecode() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_typecode();
    }
    
    @Override
    public long get_longlong() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_longlong();
    }
    
    @Override
    public long get_ulonglong() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_ulonglong();
    }
    
    @Override
    public char get_wchar() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_wchar();
    }
    
    @Override
    public String get_wstring() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_wstring();
    }
    
    @Override
    public Any get_any() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_any();
    }
    
    @Override
    public DynAny get_dyn_any() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.index == -1) {
            throw new InvalidValue();
        }
        final DynAny current_component = this.current_component();
        if (DynAnyUtil.isConstructedDynAny(current_component)) {
            throw new TypeMismatch();
        }
        return current_component.get_dyn_any();
    }
    
    static {
        emptyComponents = new DynAny[0];
    }
}
