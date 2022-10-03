package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynUnion;

public class DynUnionImpl extends DynAnyConstructedImpl implements DynUnion
{
    DynAny discriminator;
    DynAny currentMember;
    int currentMemberIndex;
    
    private DynUnionImpl() {
        this(null, null, false);
    }
    
    protected DynUnionImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
        this.discriminator = null;
        this.currentMember = null;
        this.currentMemberIndex = -1;
    }
    
    protected DynUnionImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.discriminator = null;
        this.currentMember = null;
        this.currentMemberIndex = -1;
    }
    
    @Override
    protected boolean initializeComponentsFromAny() {
        try {
            final InputStream create_input_stream = this.any.create_input_stream();
            final Any anyFromStream = DynAnyUtil.extractAnyFromStream(this.discriminatorType(), create_input_stream, this.orb);
            this.discriminator = DynAnyUtil.createMostDerivedDynAny(anyFromStream, this.orb, false);
            this.currentMemberIndex = this.currentUnionMemberIndex(anyFromStream);
            this.currentMember = DynAnyUtil.createMostDerivedDynAny(DynAnyUtil.extractAnyFromStream(this.memberType(this.currentMemberIndex), create_input_stream, this.orb), this.orb, false);
            this.components = new DynAny[] { this.discriminator, this.currentMember };
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {}
        return true;
    }
    
    @Override
    protected boolean initializeComponentsFromTypeCode() {
        try {
            this.discriminator = DynAnyUtil.createMostDerivedDynAny(this.memberLabel(0), this.orb, false);
            this.index = 0;
            this.currentMemberIndex = 0;
            this.currentMember = DynAnyUtil.createMostDerivedDynAny(this.memberType(0), this.orb);
            this.components = new DynAny[] { this.discriminator, this.currentMember };
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {}
        return true;
    }
    
    private TypeCode discriminatorType() {
        TypeCode discriminator_type = null;
        try {
            discriminator_type = this.any.type().discriminator_type();
        }
        catch (final BadKind badKind) {}
        return discriminator_type;
    }
    
    private int memberCount() {
        int member_count = 0;
        try {
            member_count = this.any.type().member_count();
        }
        catch (final BadKind badKind) {}
        return member_count;
    }
    
    private Any memberLabel(final int n) {
        Any member_label = null;
        try {
            member_label = this.any.type().member_label(n);
        }
        catch (final BadKind badKind) {}
        catch (final Bounds bounds) {}
        return member_label;
    }
    
    private TypeCode memberType(final int n) {
        TypeCode member_type = null;
        try {
            member_type = this.any.type().member_type(n);
        }
        catch (final BadKind badKind) {}
        catch (final Bounds bounds) {}
        return member_type;
    }
    
    private String memberName(final int n) {
        String member_name = null;
        try {
            member_name = this.any.type().member_name(n);
        }
        catch (final BadKind badKind) {}
        catch (final Bounds bounds) {}
        return member_name;
    }
    
    private int defaultIndex() {
        int default_index = -1;
        try {
            default_index = this.any.type().default_index();
        }
        catch (final BadKind badKind) {}
        return default_index;
    }
    
    private int currentUnionMemberIndex(final Any any) {
        for (int memberCount = this.memberCount(), i = 0; i < memberCount; ++i) {
            if (this.memberLabel(i).equal(any)) {
                return i;
            }
        }
        if (this.defaultIndex() != -1) {
            return this.defaultIndex();
        }
        return -1;
    }
    
    @Override
    protected void clearData() {
        super.clearData();
        this.discriminator = null;
        this.currentMember.destroy();
        this.currentMember = null;
        this.currentMemberIndex = -1;
    }
    
    @Override
    public DynAny get_discriminator() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.checkInitComponents() ? this.discriminator : null;
    }
    
    @Override
    public void set_discriminator(DynAny convertToNative) throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (!convertToNative.type().equal(this.discriminatorType())) {
            throw new TypeMismatch();
        }
        convertToNative = DynAnyUtil.convertToNative(convertToNative, this.orb);
        final int currentUnionMemberIndex = this.currentUnionMemberIndex(this.getAny(convertToNative));
        if (currentUnionMemberIndex == -1) {
            this.clearData();
            this.index = 0;
        }
        else {
            this.checkInitComponents();
            if (this.currentMemberIndex == -1 || currentUnionMemberIndex != this.currentMemberIndex) {
                this.clearData();
                this.index = 1;
                this.currentMemberIndex = currentUnionMemberIndex;
                try {
                    this.currentMember = DynAnyUtil.createMostDerivedDynAny(this.memberType(this.currentMemberIndex), this.orb);
                }
                catch (final InconsistentTypeCode inconsistentTypeCode) {}
                this.discriminator = convertToNative;
                this.components = new DynAny[] { this.discriminator, this.currentMember };
                this.representations = 4;
            }
        }
    }
    
    @Override
    public void set_to_default_member() throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        final int defaultIndex = this.defaultIndex();
        if (defaultIndex == -1) {
            throw new TypeMismatch();
        }
        try {
            this.clearData();
            this.index = 1;
            this.currentMemberIndex = defaultIndex;
            this.currentMember = DynAnyUtil.createMostDerivedDynAny(this.memberType(defaultIndex), this.orb);
            this.components = new DynAny[] { this.discriminator, this.currentMember };
            final Any create_any = this.orb.create_any();
            create_any.insert_octet((byte)0);
            this.discriminator = DynAnyUtil.createMostDerivedDynAny(create_any, this.orb, false);
            this.representations = 4;
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {}
    }
    
    @Override
    public void set_to_no_active_member() throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.defaultIndex() != -1) {
            throw new TypeMismatch();
        }
        this.checkInitComponents();
        final Any any = this.getAny(this.discriminator);
        any.type(any.type());
        this.index = 0;
        this.currentMemberIndex = -1;
        this.currentMember.destroy();
        this.currentMember = null;
        this.components[0] = this.discriminator;
        this.representations = 4;
    }
    
    @Override
    public boolean has_no_active_member() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.defaultIndex() != -1) {
            return false;
        }
        this.checkInitComponents();
        return this.checkInitComponents() && this.currentMemberIndex == -1;
    }
    
    @Override
    public TCKind discriminator_kind() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.discriminatorType().kind();
    }
    
    @Override
    public DynAny member() throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (!this.checkInitComponents() || this.currentMemberIndex == -1) {
            throw new InvalidValue();
        }
        return this.currentMember;
    }
    
    @Override
    public String member_name() throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (!this.checkInitComponents() || this.currentMemberIndex == -1) {
            throw new InvalidValue();
        }
        final String memberName = this.memberName(this.currentMemberIndex);
        return (memberName == null) ? "" : memberName;
    }
    
    @Override
    public TCKind member_kind() throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (!this.checkInitComponents() || this.currentMemberIndex == -1) {
            throw new InvalidValue();
        }
        return this.memberType(this.currentMemberIndex).kind();
    }
}
