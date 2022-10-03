package com.sun.corba.se.impl.corba;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.corba.se.impl.encoding.WrapperInputStream;
import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeReader;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA.Any;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;

public final class TypeCodeImpl extends TypeCode
{
    protected static final int tk_indirect = -1;
    private static final int EMPTY = 0;
    private static final int SIMPLE = 1;
    private static final int COMPLEX = 2;
    private static final int[] typeTable;
    static final String[] kindNames;
    private int _kind;
    private String _id;
    private String _name;
    private int _memberCount;
    private String[] _memberNames;
    private TypeCodeImpl[] _memberTypes;
    private AnyImpl[] _unionLabels;
    private TypeCodeImpl _discriminator;
    private int _defaultIndex;
    private int _length;
    private TypeCodeImpl _contentType;
    private short _digits;
    private short _scale;
    private short _type_modifier;
    private TypeCodeImpl _concrete_base;
    private short[] _memberAccess;
    private TypeCodeImpl _parent;
    private int _parentOffset;
    private TypeCodeImpl _indirectType;
    private byte[] outBuffer;
    private boolean cachingEnabled;
    private ORB _orb;
    private ORBUtilSystemException wrapper;
    
    public TypeCodeImpl(final ORB orb) {
        this._kind = 0;
        this._id = "";
        this._name = "";
        this._memberCount = 0;
        this._memberNames = null;
        this._memberTypes = null;
        this._unionLabels = null;
        this._discriminator = null;
        this._defaultIndex = -1;
        this._length = 0;
        this._contentType = null;
        this._digits = 0;
        this._scale = 0;
        this._type_modifier = -1;
        this._concrete_base = null;
        this._memberAccess = null;
        this._parent = null;
        this._parentOffset = 0;
        this._indirectType = null;
        this.outBuffer = null;
        this.cachingEnabled = false;
        this._orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.presentation");
    }
    
    public TypeCodeImpl(final ORB orb, final TypeCode typeCode) {
        this(orb);
        if (typeCode instanceof TypeCodeImpl) {
            final TypeCodeImpl typeCodeImpl = (TypeCodeImpl)typeCode;
            if (typeCodeImpl._kind == -1) {
                throw this.wrapper.badRemoteTypecode();
            }
            if (typeCodeImpl._kind == 19 && typeCodeImpl._contentType == null) {
                throw this.wrapper.badRemoteTypecode();
            }
        }
        this._kind = typeCode.kind().value();
        try {
            switch (this._kind) {
                case 29: {
                    this._type_modifier = typeCode.type_modifier();
                    final TypeCode concrete_base_type = typeCode.concrete_base_type();
                    if (concrete_base_type != null) {
                        this._concrete_base = convertToNative(this._orb, concrete_base_type);
                    }
                    else {
                        this._concrete_base = null;
                    }
                    this._memberAccess = new short[typeCode.member_count()];
                    for (int i = 0; i < typeCode.member_count(); ++i) {
                        this._memberAccess[i] = typeCode.member_visibility(i);
                    }
                }
                case 15:
                case 16:
                case 22: {
                    this._memberTypes = new TypeCodeImpl[typeCode.member_count()];
                    for (int j = 0; j < typeCode.member_count(); ++j) {
                        (this._memberTypes[j] = convertToNative(this._orb, typeCode.member_type(j))).setParent(this);
                    }
                }
                case 17: {
                    this._memberNames = new String[typeCode.member_count()];
                    for (int k = 0; k < typeCode.member_count(); ++k) {
                        this._memberNames[k] = typeCode.member_name(k);
                    }
                    this._memberCount = typeCode.member_count();
                }
                case 14:
                case 21:
                case 30:
                case 31:
                case 32: {
                    this.setId(typeCode.id());
                    this._name = typeCode.name();
                    break;
                }
            }
            switch (this._kind) {
                case 16: {
                    this._discriminator = convertToNative(this._orb, typeCode.discriminator_type());
                    this._defaultIndex = typeCode.default_index();
                    this._unionLabels = new AnyImpl[this._memberCount];
                    for (int l = 0; l < this._memberCount; ++l) {
                        this._unionLabels[l] = new AnyImpl(this._orb, typeCode.member_label(l));
                    }
                    break;
                }
            }
            switch (this._kind) {
                case 18:
                case 19:
                case 20:
                case 27: {
                    this._length = typeCode.length();
                    break;
                }
            }
            switch (this._kind) {
                case 19:
                case 20:
                case 21:
                case 30: {
                    this._contentType = convertToNative(this._orb, typeCode.content_type());
                    break;
                }
            }
        }
        catch (final Bounds bounds) {}
        catch (final BadKind badKind) {}
    }
    
    public TypeCodeImpl(final ORB orb, final int kind) {
        this(orb);
        switch (this._kind = kind) {
            case 14: {
                this.setId("IDL:omg.org/CORBA/Object:1.0");
                this._name = "Object";
                break;
            }
            case 18:
            case 27: {
                this._length = 0;
                break;
            }
            case 29: {
                this._concrete_base = null;
                break;
            }
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final String id, final String name, final StructMember[] array) {
        this(orb);
        if (kind == 15 || kind == 22) {
            this._kind = kind;
            this.setId(id);
            this._name = name;
            this._memberCount = array.length;
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];
            for (int i = 0; i < this._memberCount; ++i) {
                this._memberNames[i] = array[i].name;
                (this._memberTypes[i] = convertToNative(this._orb, array[i].type)).setParent(this);
            }
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final String id, final String name, final TypeCode typeCode, final UnionMember[] array) {
        this(orb);
        if (kind == 16) {
            this._kind = kind;
            this.setId(id);
            this._name = name;
            this._memberCount = array.length;
            this._discriminator = convertToNative(this._orb, typeCode);
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];
            this._unionLabels = new AnyImpl[this._memberCount];
            for (int i = 0; i < this._memberCount; ++i) {
                this._memberNames[i] = array[i].name;
                (this._memberTypes[i] = convertToNative(this._orb, array[i].type)).setParent(this);
                this._unionLabels[i] = new AnyImpl(this._orb, array[i].label);
                if (this._unionLabels[i].type().kind() == TCKind.tk_octet && this._unionLabels[i].extract_octet() == 0) {
                    this._defaultIndex = i;
                }
            }
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final String id, final String name, final short type_modifier, final TypeCode typeCode, final ValueMember[] array) {
        this(orb);
        if (kind == 29) {
            this._kind = kind;
            this.setId(id);
            this._name = name;
            this._type_modifier = type_modifier;
            if (typeCode != null) {
                this._concrete_base = convertToNative(this._orb, typeCode);
            }
            this._memberCount = array.length;
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];
            this._memberAccess = new short[this._memberCount];
            for (int i = 0; i < this._memberCount; ++i) {
                this._memberNames[i] = array[i].name;
                (this._memberTypes[i] = convertToNative(this._orb, array[i].type)).setParent(this);
                this._memberAccess[i] = array[i].access;
            }
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final String id, final String name, final String[] array) {
        this(orb);
        if (kind == 17) {
            this._kind = kind;
            this.setId(id);
            this._name = name;
            this._memberCount = array.length;
            this._memberNames = new String[this._memberCount];
            for (int i = 0; i < this._memberCount; ++i) {
                this._memberNames[i] = array[i];
            }
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final String id, final String name, final TypeCode typeCode) {
        this(orb);
        if (kind == 21 || kind == 30) {
            this._kind = kind;
            this.setId(id);
            this._name = name;
            this._contentType = convertToNative(this._orb, typeCode);
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final String id, final String name) {
        this(orb);
        if (kind == 14 || kind == 31 || kind == 32) {
            this._kind = kind;
            this.setId(id);
            this._name = name;
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final int length) {
        this(orb);
        if (length < 0) {
            throw this.wrapper.negativeBounds();
        }
        if (kind == 18 || kind == 27) {
            this._kind = kind;
            this._length = length;
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final int length, final TypeCode typeCode) {
        this(orb);
        if (kind == 19 || kind == 20) {
            this._kind = kind;
            this._length = length;
            this._contentType = convertToNative(this._orb, typeCode);
        }
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final int length, final int parentOffset) {
        this(orb);
        if (kind == 19) {
            this._kind = kind;
            this._length = length;
            this._parentOffset = parentOffset;
        }
    }
    
    public TypeCodeImpl(final ORB orb, final String id) {
        this(orb);
        this._kind = -1;
        this._id = id;
        this.tryIndirectType();
    }
    
    public TypeCodeImpl(final ORB orb, final int kind, final short digits, final short scale) {
        this(orb);
        if (kind == 28) {
            this._kind = kind;
            this._digits = digits;
            this._scale = scale;
        }
    }
    
    protected static TypeCodeImpl convertToNative(final ORB orb, final TypeCode typeCode) {
        if (typeCode instanceof TypeCodeImpl) {
            return (TypeCodeImpl)typeCode;
        }
        return new TypeCodeImpl(orb, typeCode);
    }
    
    public static CDROutputStream newOutputStream(final ORB orb) {
        return OutputStreamFactory.newTypeCodeOutputStream(orb);
    }
    
    private TypeCodeImpl indirectType() {
        this._indirectType = this.tryIndirectType();
        if (this._indirectType == null) {
            throw this.wrapper.unresolvedRecursiveTypecode();
        }
        return this._indirectType;
    }
    
    private TypeCodeImpl tryIndirectType() {
        if (this._indirectType != null) {
            return this._indirectType;
        }
        this.setIndirectType(this._orb.getTypeCode(this._id));
        return this._indirectType;
    }
    
    private void setIndirectType(final TypeCodeImpl indirectType) {
        this._indirectType = indirectType;
        if (this._indirectType != null) {
            try {
                this._id = this._indirectType.id();
            }
            catch (final BadKind badKind) {
                throw this.wrapper.badkindCannotOccur();
            }
        }
    }
    
    private void setId(final String id) {
        this._id = id;
        if (this._orb instanceof TypeCodeFactory) {
            this._orb.setTypeCode(this._id, this);
        }
    }
    
    private void setParent(final TypeCodeImpl parent) {
        this._parent = parent;
    }
    
    private TypeCodeImpl getParentAtLevel(final int n) {
        if (n == 0) {
            return this;
        }
        if (this._parent == null) {
            throw this.wrapper.unresolvedRecursiveTypecode();
        }
        return this._parent.getParentAtLevel(n - 1);
    }
    
    private TypeCodeImpl lazy_content_type() {
        if (this._contentType == null && this._kind == 19 && this._parentOffset > 0 && this._parent != null) {
            final TypeCodeImpl parentAtLevel = this.getParentAtLevel(this._parentOffset);
            if (parentAtLevel != null && parentAtLevel._id != null) {
                this._contentType = new TypeCodeImpl(this._orb, parentAtLevel._id);
            }
        }
        return this._contentType;
    }
    
    private TypeCode realType(final TypeCode typeCode) {
        TypeCode content_type = typeCode;
        try {
            while (content_type.kind().value() == 21) {
                content_type = content_type.content_type();
            }
        }
        catch (final BadKind badKind) {
            throw this.wrapper.badkindCannotOccur();
        }
        return content_type;
    }
    
    @Override
    public final boolean equal(final TypeCode typeCode) {
        if (typeCode == this) {
            return true;
        }
        try {
            if (this._kind == -1) {
                if (this._id != null && typeCode.id() != null) {
                    return this._id.equals(typeCode.id());
                }
                return this._id == null && typeCode.id() == null;
            }
            else {
                if (this._kind != typeCode.kind().value()) {
                    return false;
                }
                Label_0799: {
                    switch (TypeCodeImpl.typeTable[this._kind]) {
                        case 0: {
                            return true;
                        }
                        case 1: {
                            switch (this._kind) {
                                case 18:
                                case 27: {
                                    return this._length == typeCode.length();
                                }
                                case 28: {
                                    return this._digits == typeCode.fixed_digits() && this._scale == typeCode.fixed_scale();
                                }
                                default: {
                                    return false;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (this._kind) {
                                case 14: {
                                    return this._id.compareTo(typeCode.id()) == 0 || this._id.compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0 || typeCode.id().compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0;
                                }
                                case 31:
                                case 32: {
                                    return this._id.compareTo(typeCode.id()) == 0;
                                }
                                case 15:
                                case 22: {
                                    if (this._memberCount != typeCode.member_count()) {
                                        return false;
                                    }
                                    if (this._id.compareTo(typeCode.id()) != 0) {
                                        return false;
                                    }
                                    for (int i = 0; i < this._memberCount; ++i) {
                                        if (!this._memberTypes[i].equal(typeCode.member_type(i))) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                                case 16: {
                                    if (this._memberCount != typeCode.member_count()) {
                                        return false;
                                    }
                                    if (this._id.compareTo(typeCode.id()) != 0) {
                                        return false;
                                    }
                                    if (this._defaultIndex != typeCode.default_index()) {
                                        return false;
                                    }
                                    if (!this._discriminator.equal(typeCode.discriminator_type())) {
                                        return false;
                                    }
                                    for (int j = 0; j < this._memberCount; ++j) {
                                        if (!this._unionLabels[j].equal(typeCode.member_label(j))) {
                                            return false;
                                        }
                                    }
                                    for (int k = 0; k < this._memberCount; ++k) {
                                        if (!this._memberTypes[k].equal(typeCode.member_type(k))) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                                case 17: {
                                    return this._id.compareTo(typeCode.id()) == 0 && this._memberCount == typeCode.member_count();
                                }
                                case 19:
                                case 20: {
                                    return this._length == typeCode.length() && this.lazy_content_type().equal(typeCode.content_type());
                                }
                                case 29: {
                                    if (this._memberCount != typeCode.member_count()) {
                                        return false;
                                    }
                                    if (this._id.compareTo(typeCode.id()) != 0) {
                                        return false;
                                    }
                                    for (int l = 0; l < this._memberCount; ++l) {
                                        if (this._memberAccess[l] != typeCode.member_visibility(l) || !this._memberTypes[l].equal(typeCode.member_type(l))) {
                                            return false;
                                        }
                                    }
                                    if (this._type_modifier == typeCode.type_modifier()) {
                                        return false;
                                    }
                                    final TypeCode concrete_base_type = typeCode.concrete_base_type();
                                    return (this._concrete_base != null || concrete_base_type == null) && (this._concrete_base == null || concrete_base_type != null) && this._concrete_base.equal(concrete_base_type);
                                }
                                case 21:
                                case 30: {
                                    return this._id.compareTo(typeCode.id()) == 0 && this._contentType.equal(typeCode.content_type());
                                }
                                default: {
                                    break Label_0799;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        catch (final Bounds bounds) {}
        catch (final BadKind badKind) {}
        return false;
    }
    
    @Override
    public boolean equivalent(final TypeCode typeCode) {
        if (typeCode == this) {
            return true;
        }
        final TypeCode realType = this.realType((this._kind == -1) ? this.indirectType() : this);
        final TypeCode realType2 = this.realType(typeCode);
        if (realType.kind().value() != realType2.kind().value()) {
            return false;
        }
        try {
            final String id = this.id();
            final String id2 = typeCode.id();
            if (id != null && id2 != null) {
                return id.equals(id2);
            }
        }
        catch (final BadKind badKind) {}
        final int value = realType.kind().value();
        try {
            if ((value == 15 || value == 16 || value == 17 || value == 22 || value == 29) && realType.member_count() != realType2.member_count()) {
                return false;
            }
            if (value == 16 && realType.default_index() != realType2.default_index()) {
                return false;
            }
            if ((value == 18 || value == 27 || value == 19 || value == 20) && realType.length() != realType2.length()) {
                return false;
            }
            if (value == 28 && (realType.fixed_digits() != realType2.fixed_digits() || realType.fixed_scale() != realType2.fixed_scale())) {
                return false;
            }
            if (value == 16) {
                for (int i = 0; i < realType.member_count(); ++i) {
                    if (realType.member_label(i) != realType2.member_label(i)) {
                        return false;
                    }
                }
                if (!realType.discriminator_type().equivalent(realType2.discriminator_type())) {
                    return false;
                }
            }
            if ((value == 21 || value == 30 || value == 19 || value == 20) && !realType.content_type().equivalent(realType2.content_type())) {
                return false;
            }
            if (value == 15 || value == 16 || value == 22 || value == 29) {
                for (int j = 0; j < realType.member_count(); ++j) {
                    if (!realType.member_type(j).equivalent(realType2.member_type(j))) {
                        return false;
                    }
                }
            }
        }
        catch (final BadKind badKind2) {
            throw this.wrapper.badkindCannotOccur();
        }
        catch (final Bounds bounds) {
            throw this.wrapper.boundsCannotOccur();
        }
        return true;
    }
    
    @Override
    public TypeCode get_compact_typecode() {
        return this;
    }
    
    @Override
    public TCKind kind() {
        if (this._kind == -1) {
            return this.indirectType().kind();
        }
        return TCKind.from_int(this._kind);
    }
    
    public boolean is_recursive() {
        return this._kind == -1;
    }
    
    @Override
    public String id() throws BadKind {
        switch (this._kind) {
            case -1:
            case 14:
            case 15:
            case 16:
            case 17:
            case 21:
            case 22:
            case 29:
            case 30:
            case 31:
            case 32: {
                return this._id;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public String name() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().name();
            }
            case 14:
            case 15:
            case 16:
            case 17:
            case 21:
            case 22:
            case 29:
            case 30:
            case 31:
            case 32: {
                return this._name;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public int member_count() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().member_count();
            }
            case 15:
            case 16:
            case 17:
            case 22:
            case 29: {
                return this._memberCount;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public String member_name(final int n) throws BadKind, Bounds {
        switch (this._kind) {
            case -1: {
                return this.indirectType().member_name(n);
            }
            case 15:
            case 16:
            case 17:
            case 22:
            case 29: {
                try {
                    return this._memberNames[n];
                }
                catch (final ArrayIndexOutOfBoundsException ex) {
                    throw new Bounds();
                }
                break;
            }
        }
        throw new BadKind();
    }
    
    @Override
    public TypeCode member_type(final int n) throws BadKind, Bounds {
        switch (this._kind) {
            case -1: {
                return this.indirectType().member_type(n);
            }
            case 15:
            case 16:
            case 22:
            case 29: {
                try {
                    return this._memberTypes[n];
                }
                catch (final ArrayIndexOutOfBoundsException ex) {
                    throw new Bounds();
                }
                break;
            }
        }
        throw new BadKind();
    }
    
    @Override
    public Any member_label(final int n) throws BadKind, Bounds {
        switch (this._kind) {
            case -1: {
                return this.indirectType().member_label(n);
            }
            case 16: {
                try {
                    return new AnyImpl(this._orb, this._unionLabels[n]);
                }
                catch (final ArrayIndexOutOfBoundsException ex) {
                    throw new Bounds();
                }
                break;
            }
        }
        throw new BadKind();
    }
    
    @Override
    public TypeCode discriminator_type() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().discriminator_type();
            }
            case 16: {
                return this._discriminator;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public int default_index() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().default_index();
            }
            case 16: {
                return this._defaultIndex;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public int length() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().length();
            }
            case 18:
            case 19:
            case 20:
            case 27: {
                return this._length;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public TypeCode content_type() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().content_type();
            }
            case 19: {
                return this.lazy_content_type();
            }
            case 20:
            case 21:
            case 30: {
                return this._contentType;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public short fixed_digits() throws BadKind {
        switch (this._kind) {
            case 28: {
                return this._digits;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public short fixed_scale() throws BadKind {
        switch (this._kind) {
            case 28: {
                return this._scale;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public short member_visibility(final int n) throws BadKind, Bounds {
        switch (this._kind) {
            case -1: {
                return this.indirectType().member_visibility(n);
            }
            case 29: {
                try {
                    return this._memberAccess[n];
                }
                catch (final ArrayIndexOutOfBoundsException ex) {
                    throw new Bounds();
                }
                break;
            }
        }
        throw new BadKind();
    }
    
    @Override
    public short type_modifier() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().type_modifier();
            }
            case 29: {
                return this._type_modifier;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    @Override
    public TypeCode concrete_base_type() throws BadKind {
        switch (this._kind) {
            case -1: {
                return this.indirectType().concrete_base_type();
            }
            case 29: {
                return this._concrete_base;
            }
            default: {
                throw new BadKind();
            }
        }
    }
    
    public void read_value(final InputStream inputStream) {
        if (inputStream instanceof TypeCodeReader) {
            if (this.read_value_kind((TypeCodeReader)inputStream)) {
                this.read_value_body(inputStream);
            }
        }
        else if (inputStream instanceof CDRInputStream) {
            final WrapperInputStream wrapperInputStream = new WrapperInputStream((CDRInputStream)inputStream);
            if (this.read_value_kind((TypeCodeReader)wrapperInputStream)) {
                this.read_value_body(wrapperInputStream);
            }
        }
        else {
            this.read_value_kind(inputStream);
            this.read_value_body(inputStream);
        }
    }
    
    private void read_value_recursive(final TypeCodeInputStream typeCodeInputStream) {
        if (typeCodeInputStream instanceof TypeCodeReader) {
            if (this.read_value_kind((TypeCodeReader)typeCodeInputStream)) {
                this.read_value_body(typeCodeInputStream);
            }
        }
        else {
            this.read_value_kind((InputStream)typeCodeInputStream);
            this.read_value_body(typeCodeInputStream);
        }
    }
    
    boolean read_value_kind(final TypeCodeReader typeCodeReader) {
        this._kind = typeCodeReader.read_long();
        final int n = typeCodeReader.getTopLevelPosition() - 4;
        if ((this._kind < 0 || this._kind > TypeCodeImpl.typeTable.length) && this._kind != -1) {
            throw this.wrapper.cannotMarshalBadTckind();
        }
        if (this._kind == 31) {
            throw this.wrapper.cannotMarshalNative();
        }
        final TypeCodeReader topLevelStream = typeCodeReader.getTopLevelStream();
        if (this._kind != -1) {
            topLevelStream.addTypeCodeAtPosition(this, n);
            return true;
        }
        final int read_long = typeCodeReader.read_long();
        if (read_long > -4) {
            throw this.wrapper.invalidIndirection(new Integer(read_long));
        }
        final int n2 = typeCodeReader.getTopLevelPosition() - 4 + read_long;
        final TypeCodeImpl typeCodeAtPosition = topLevelStream.getTypeCodeAtPosition(n2);
        if (typeCodeAtPosition == null) {
            throw this.wrapper.indirectionNotFound(new Integer(n2));
        }
        this.setIndirectType(typeCodeAtPosition);
        return false;
    }
    
    void read_value_kind(final InputStream inputStream) {
        this._kind = inputStream.read_long();
        if ((this._kind < 0 || this._kind > TypeCodeImpl.typeTable.length) && this._kind != -1) {
            throw this.wrapper.cannotMarshalBadTckind();
        }
        if (this._kind == 31) {
            throw this.wrapper.cannotMarshalNative();
        }
        if (this._kind == -1) {
            throw this.wrapper.recursiveTypecodeError();
        }
    }
    
    void read_value_body(final InputStream inputStream) {
        Label_1273: {
            switch (TypeCodeImpl.typeTable[this._kind]) {
                case 1: {
                    switch (this._kind) {
                        case 18:
                        case 27: {
                            this._length = inputStream.read_long();
                            break Label_1273;
                        }
                        case 28: {
                            this._digits = inputStream.read_ushort();
                            this._scale = inputStream.read_short();
                            break Label_1273;
                        }
                        default: {
                            throw this.wrapper.invalidSimpleTypecode();
                        }
                    }
                    break;
                }
                case 2: {
                    final TypeCodeInputStream encapsulation = TypeCodeInputStream.readEncapsulation(inputStream, inputStream.orb());
                    switch (this._kind) {
                        case 14:
                        case 32: {
                            this.setId(encapsulation.read_string());
                            this._name = encapsulation.read_string();
                            break Label_1273;
                        }
                        case 16: {
                            this.setId(encapsulation.read_string());
                            this._name = encapsulation.read_string();
                            (this._discriminator = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                            this._defaultIndex = encapsulation.read_long();
                            this._memberCount = encapsulation.read_long();
                            this._unionLabels = new AnyImpl[this._memberCount];
                            this._memberNames = new String[this._memberCount];
                            this._memberTypes = new TypeCodeImpl[this._memberCount];
                            for (int i = 0; i < this._memberCount; ++i) {
                                this._unionLabels[i] = new AnyImpl((ORB)inputStream.orb());
                                if (i == this._defaultIndex) {
                                    this._unionLabels[i].insert_octet(encapsulation.read_octet());
                                }
                                else {
                                    switch (this.realType(this._discriminator).kind().value()) {
                                        case 2: {
                                            this._unionLabels[i].insert_short(encapsulation.read_short());
                                            break;
                                        }
                                        case 3: {
                                            this._unionLabels[i].insert_long(encapsulation.read_long());
                                            break;
                                        }
                                        case 4: {
                                            this._unionLabels[i].insert_ushort(encapsulation.read_short());
                                            break;
                                        }
                                        case 5: {
                                            this._unionLabels[i].insert_ulong(encapsulation.read_long());
                                            break;
                                        }
                                        case 6: {
                                            this._unionLabels[i].insert_float(encapsulation.read_float());
                                            break;
                                        }
                                        case 7: {
                                            this._unionLabels[i].insert_double(encapsulation.read_double());
                                            break;
                                        }
                                        case 8: {
                                            this._unionLabels[i].insert_boolean(encapsulation.read_boolean());
                                            break;
                                        }
                                        case 9: {
                                            this._unionLabels[i].insert_char(encapsulation.read_char());
                                            break;
                                        }
                                        case 17: {
                                            this._unionLabels[i].type(this._discriminator);
                                            this._unionLabels[i].insert_long(encapsulation.read_long());
                                            break;
                                        }
                                        case 23: {
                                            this._unionLabels[i].insert_longlong(encapsulation.read_longlong());
                                            break;
                                        }
                                        case 24: {
                                            this._unionLabels[i].insert_ulonglong(encapsulation.read_longlong());
                                            break;
                                        }
                                        case 26: {
                                            this._unionLabels[i].insert_wchar(encapsulation.read_wchar());
                                            break;
                                        }
                                        default: {
                                            throw this.wrapper.invalidComplexTypecode();
                                        }
                                    }
                                }
                                this._memberNames[i] = encapsulation.read_string();
                                (this._memberTypes[i] = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                                this._memberTypes[i].setParent(this);
                            }
                            break Label_1273;
                        }
                        case 17: {
                            this.setId(encapsulation.read_string());
                            this._name = encapsulation.read_string();
                            this._memberCount = encapsulation.read_long();
                            this._memberNames = new String[this._memberCount];
                            for (int j = 0; j < this._memberCount; ++j) {
                                this._memberNames[j] = encapsulation.read_string();
                            }
                            break Label_1273;
                        }
                        case 19: {
                            (this._contentType = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                            this._length = encapsulation.read_long();
                            break Label_1273;
                        }
                        case 20: {
                            (this._contentType = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                            this._length = encapsulation.read_long();
                            break Label_1273;
                        }
                        case 21:
                        case 30: {
                            this.setId(encapsulation.read_string());
                            this._name = encapsulation.read_string();
                            (this._contentType = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                            break Label_1273;
                        }
                        case 15:
                        case 22: {
                            this.setId(encapsulation.read_string());
                            this._name = encapsulation.read_string();
                            this._memberCount = encapsulation.read_long();
                            this._memberNames = new String[this._memberCount];
                            this._memberTypes = new TypeCodeImpl[this._memberCount];
                            for (int k = 0; k < this._memberCount; ++k) {
                                this._memberNames[k] = encapsulation.read_string();
                                (this._memberTypes[k] = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                                this._memberTypes[k].setParent(this);
                            }
                            break Label_1273;
                        }
                        case 29: {
                            this.setId(encapsulation.read_string());
                            this._name = encapsulation.read_string();
                            this._type_modifier = encapsulation.read_short();
                            (this._concrete_base = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                            if (this._concrete_base.kind().value() == 0) {
                                this._concrete_base = null;
                            }
                            this._memberCount = encapsulation.read_long();
                            this._memberNames = new String[this._memberCount];
                            this._memberTypes = new TypeCodeImpl[this._memberCount];
                            this._memberAccess = new short[this._memberCount];
                            for (int l = 0; l < this._memberCount; ++l) {
                                this._memberNames[l] = encapsulation.read_string();
                                (this._memberTypes[l] = new TypeCodeImpl((ORB)inputStream.orb())).read_value_recursive(encapsulation);
                                this._memberTypes[l].setParent(this);
                                this._memberAccess[l] = encapsulation.read_short();
                            }
                            break Label_1273;
                        }
                        default: {
                            throw this.wrapper.invalidTypecodeKindMarshal();
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public void write_value(final OutputStream outputStream) {
        if (outputStream instanceof TypeCodeOutputStream) {
            this.write_value((TypeCodeOutputStream)outputStream);
        }
        else {
            TypeCodeOutputStream wrapOutputStream = null;
            if (this.outBuffer == null) {
                wrapOutputStream = TypeCodeOutputStream.wrapOutputStream(outputStream);
                this.write_value(wrapOutputStream);
                if (this.cachingEnabled) {
                    this.outBuffer = wrapOutputStream.getTypeCodeBuffer();
                }
            }
            if (this.cachingEnabled && this.outBuffer != null) {
                outputStream.write_long(this._kind);
                outputStream.write_octet_array(this.outBuffer, 0, this.outBuffer.length);
            }
            else {
                wrapOutputStream.writeRawBuffer(outputStream, this._kind);
            }
        }
    }
    
    public void write_value(final TypeCodeOutputStream typeCodeOutputStream) {
        if (this._kind == 31) {
            throw this.wrapper.cannotMarshalNative();
        }
        final TypeCodeOutputStream topLevelStream = typeCodeOutputStream.getTopLevelStream();
        if (this._kind == -1) {
            final int positionForID = topLevelStream.getPositionForID(this._id);
            typeCodeOutputStream.getTopLevelPosition();
            typeCodeOutputStream.writeIndirection(-1, positionForID);
            return;
        }
        typeCodeOutputStream.write_long(this._kind);
        topLevelStream.addIDAtPosition(this._id, typeCodeOutputStream.getTopLevelPosition() - 4);
        Label_1075: {
            switch (TypeCodeImpl.typeTable[this._kind]) {
                case 1: {
                    switch (this._kind) {
                        case 18:
                        case 27: {
                            typeCodeOutputStream.write_long(this._length);
                            break Label_1075;
                        }
                        case 28: {
                            typeCodeOutputStream.write_ushort(this._digits);
                            typeCodeOutputStream.write_short(this._scale);
                            break Label_1075;
                        }
                        default: {
                            throw this.wrapper.invalidSimpleTypecode();
                        }
                    }
                    break;
                }
                case 2: {
                    final TypeCodeOutputStream encapsulation = typeCodeOutputStream.createEncapsulation(typeCodeOutputStream.orb());
                    switch (this._kind) {
                        case 14:
                        case 32: {
                            encapsulation.write_string(this._id);
                            encapsulation.write_string(this._name);
                            break;
                        }
                        case 16: {
                            encapsulation.write_string(this._id);
                            encapsulation.write_string(this._name);
                            this._discriminator.write_value(encapsulation);
                            encapsulation.write_long(this._defaultIndex);
                            encapsulation.write_long(this._memberCount);
                            for (int i = 0; i < this._memberCount; ++i) {
                                if (i == this._defaultIndex) {
                                    encapsulation.write_octet(this._unionLabels[i].extract_octet());
                                }
                                else {
                                    switch (this.realType(this._discriminator).kind().value()) {
                                        case 2: {
                                            encapsulation.write_short(this._unionLabels[i].extract_short());
                                            break;
                                        }
                                        case 3: {
                                            encapsulation.write_long(this._unionLabels[i].extract_long());
                                            break;
                                        }
                                        case 4: {
                                            encapsulation.write_short(this._unionLabels[i].extract_ushort());
                                            break;
                                        }
                                        case 5: {
                                            encapsulation.write_long(this._unionLabels[i].extract_ulong());
                                            break;
                                        }
                                        case 6: {
                                            encapsulation.write_float(this._unionLabels[i].extract_float());
                                            break;
                                        }
                                        case 7: {
                                            encapsulation.write_double(this._unionLabels[i].extract_double());
                                            break;
                                        }
                                        case 8: {
                                            encapsulation.write_boolean(this._unionLabels[i].extract_boolean());
                                            break;
                                        }
                                        case 9: {
                                            encapsulation.write_char(this._unionLabels[i].extract_char());
                                            break;
                                        }
                                        case 17: {
                                            encapsulation.write_long(this._unionLabels[i].extract_long());
                                            break;
                                        }
                                        case 23: {
                                            encapsulation.write_longlong(this._unionLabels[i].extract_longlong());
                                            break;
                                        }
                                        case 24: {
                                            encapsulation.write_longlong(this._unionLabels[i].extract_ulonglong());
                                            break;
                                        }
                                        case 26: {
                                            encapsulation.write_wchar(this._unionLabels[i].extract_wchar());
                                            break;
                                        }
                                        default: {
                                            throw this.wrapper.invalidComplexTypecode();
                                        }
                                    }
                                }
                                encapsulation.write_string(this._memberNames[i]);
                                this._memberTypes[i].write_value(encapsulation);
                            }
                            break;
                        }
                        case 17: {
                            encapsulation.write_string(this._id);
                            encapsulation.write_string(this._name);
                            encapsulation.write_long(this._memberCount);
                            for (int j = 0; j < this._memberCount; ++j) {
                                encapsulation.write_string(this._memberNames[j]);
                            }
                            break;
                        }
                        case 19: {
                            this.lazy_content_type().write_value(encapsulation);
                            encapsulation.write_long(this._length);
                            break;
                        }
                        case 20: {
                            this._contentType.write_value(encapsulation);
                            encapsulation.write_long(this._length);
                            break;
                        }
                        case 21:
                        case 30: {
                            encapsulation.write_string(this._id);
                            encapsulation.write_string(this._name);
                            this._contentType.write_value(encapsulation);
                            break;
                        }
                        case 15:
                        case 22: {
                            encapsulation.write_string(this._id);
                            encapsulation.write_string(this._name);
                            encapsulation.write_long(this._memberCount);
                            for (int k = 0; k < this._memberCount; ++k) {
                                encapsulation.write_string(this._memberNames[k]);
                                this._memberTypes[k].write_value(encapsulation);
                            }
                            break;
                        }
                        case 29: {
                            encapsulation.write_string(this._id);
                            encapsulation.write_string(this._name);
                            encapsulation.write_short(this._type_modifier);
                            if (this._concrete_base == null) {
                                this._orb.get_primitive_tc(0).write_value(encapsulation);
                            }
                            else {
                                this._concrete_base.write_value(encapsulation);
                            }
                            encapsulation.write_long(this._memberCount);
                            for (int l = 0; l < this._memberCount; ++l) {
                                encapsulation.write_string(this._memberNames[l]);
                                this._memberTypes[l].write_value(encapsulation);
                                encapsulation.write_short(this._memberAccess[l]);
                            }
                            break;
                        }
                        default: {
                            throw this.wrapper.invalidTypecodeKindMarshal();
                        }
                    }
                    encapsulation.writeOctetSequenceTo(typeCodeOutputStream);
                    break;
                }
            }
        }
    }
    
    protected void copy(final org.omg.CORBA.portable.InputStream inputStream, final org.omg.CORBA.portable.OutputStream outputStream) {
        switch (this._kind) {
            case 0:
            case 1:
            case 31:
            case 32: {
                break;
            }
            case 2:
            case 4: {
                outputStream.write_short(inputStream.read_short());
                break;
            }
            case 3:
            case 5: {
                outputStream.write_long(inputStream.read_long());
                break;
            }
            case 6: {
                outputStream.write_float(inputStream.read_float());
                break;
            }
            case 7: {
                outputStream.write_double(inputStream.read_double());
                break;
            }
            case 23:
            case 24: {
                outputStream.write_longlong(inputStream.read_longlong());
                break;
            }
            case 25: {
                throw this.wrapper.tkLongDoubleNotSupported();
            }
            case 8: {
                outputStream.write_boolean(inputStream.read_boolean());
                break;
            }
            case 9: {
                outputStream.write_char(inputStream.read_char());
                break;
            }
            case 26: {
                outputStream.write_wchar(inputStream.read_wchar());
                break;
            }
            case 10: {
                outputStream.write_octet(inputStream.read_octet());
                break;
            }
            case 18: {
                final String read_string = inputStream.read_string();
                if (this._length != 0 && read_string.length() > this._length) {
                    throw this.wrapper.badStringBounds(new Integer(read_string.length()), new Integer(this._length));
                }
                outputStream.write_string(read_string);
                break;
            }
            case 27: {
                final String read_wstring = inputStream.read_wstring();
                if (this._length != 0 && read_wstring.length() > this._length) {
                    throw this.wrapper.badStringBounds(new Integer(read_wstring.length()), new Integer(this._length));
                }
                outputStream.write_wstring(read_wstring);
                break;
            }
            case 28: {
                outputStream.write_ushort(inputStream.read_ushort());
                outputStream.write_short(inputStream.read_short());
                break;
            }
            case 11: {
                final Any create_any = ((CDRInputStream)inputStream).orb().create_any();
                final TypeCodeImpl typeCodeImpl = new TypeCodeImpl((ORB)outputStream.orb());
                typeCodeImpl.read_value((InputStream)inputStream);
                typeCodeImpl.write_value((OutputStream)outputStream);
                create_any.read_value(inputStream, typeCodeImpl);
                create_any.write_value(outputStream);
                break;
            }
            case 12: {
                outputStream.write_TypeCode(inputStream.read_TypeCode());
                break;
            }
            case 13: {
                outputStream.write_Principal(inputStream.read_Principal());
                break;
            }
            case 14: {
                outputStream.write_Object(inputStream.read_Object());
                break;
            }
            case 22: {
                outputStream.write_string(inputStream.read_string());
            }
            case 15:
            case 29: {
                for (int i = 0; i < this._memberTypes.length; ++i) {
                    this._memberTypes[i].copy(inputStream, outputStream);
                }
                break;
            }
            case 16: {
                final AnyImpl anyImpl = new AnyImpl((ORB)inputStream.orb());
                switch (this.realType(this._discriminator).kind().value()) {
                    case 2: {
                        final short read_short = inputStream.read_short();
                        anyImpl.insert_short(read_short);
                        outputStream.write_short(read_short);
                        break;
                    }
                    case 3: {
                        final int read_long = inputStream.read_long();
                        anyImpl.insert_long(read_long);
                        outputStream.write_long(read_long);
                        break;
                    }
                    case 4: {
                        final short read_short2 = inputStream.read_short();
                        anyImpl.insert_ushort(read_short2);
                        outputStream.write_short(read_short2);
                        break;
                    }
                    case 5: {
                        final int read_long2 = inputStream.read_long();
                        anyImpl.insert_ulong(read_long2);
                        outputStream.write_long(read_long2);
                        break;
                    }
                    case 6: {
                        final float read_float = inputStream.read_float();
                        anyImpl.insert_float(read_float);
                        outputStream.write_float(read_float);
                        break;
                    }
                    case 7: {
                        final double read_double = inputStream.read_double();
                        anyImpl.insert_double(read_double);
                        outputStream.write_double(read_double);
                        break;
                    }
                    case 8: {
                        final boolean read_boolean = inputStream.read_boolean();
                        anyImpl.insert_boolean(read_boolean);
                        outputStream.write_boolean(read_boolean);
                        break;
                    }
                    case 9: {
                        final char read_char = inputStream.read_char();
                        anyImpl.insert_char(read_char);
                        outputStream.write_char(read_char);
                        break;
                    }
                    case 17: {
                        final int read_long3 = inputStream.read_long();
                        anyImpl.type(this._discriminator);
                        anyImpl.insert_long(read_long3);
                        outputStream.write_long(read_long3);
                        break;
                    }
                    case 23: {
                        final long read_longlong = inputStream.read_longlong();
                        anyImpl.insert_longlong(read_longlong);
                        outputStream.write_longlong(read_longlong);
                        break;
                    }
                    case 24: {
                        final long read_longlong2 = inputStream.read_longlong();
                        anyImpl.insert_ulonglong(read_longlong2);
                        outputStream.write_longlong(read_longlong2);
                        break;
                    }
                    case 26: {
                        final char read_wchar = inputStream.read_wchar();
                        anyImpl.insert_wchar(read_wchar);
                        outputStream.write_wchar(read_wchar);
                        break;
                    }
                    default: {
                        throw this.wrapper.illegalUnionDiscriminatorType();
                    }
                }
                int j;
                for (j = 0; j < this._unionLabels.length; ++j) {
                    if (anyImpl.equal(this._unionLabels[j])) {
                        this._memberTypes[j].copy(inputStream, outputStream);
                        break;
                    }
                }
                if (j == this._unionLabels.length && this._defaultIndex != -1) {
                    this._memberTypes[this._defaultIndex].copy(inputStream, outputStream);
                    break;
                }
                break;
            }
            case 17: {
                outputStream.write_long(inputStream.read_long());
                break;
            }
            case 19: {
                final int read_long4 = inputStream.read_long();
                if (this._length != 0 && read_long4 > this._length) {
                    throw this.wrapper.badSequenceBounds(new Integer(read_long4), new Integer(this._length));
                }
                outputStream.write_long(read_long4);
                this.lazy_content_type();
                for (int k = 0; k < read_long4; ++k) {
                    this._contentType.copy(inputStream, outputStream);
                }
                break;
            }
            case 20: {
                for (int l = 0; l < this._length; ++l) {
                    this._contentType.copy(inputStream, outputStream);
                }
                break;
            }
            case 21:
            case 30: {
                this._contentType.copy(inputStream, outputStream);
                break;
            }
            case -1: {
                this.indirectType().copy(inputStream, outputStream);
                break;
            }
            default: {
                throw this.wrapper.invalidTypecodeKindMarshal();
            }
        }
    }
    
    protected static short digits(final BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return 0;
        }
        short n = (short)bigDecimal.unscaledValue().toString().length();
        if (bigDecimal.signum() == -1) {
            --n;
        }
        return n;
    }
    
    protected static short scale(final BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return 0;
        }
        return (short)bigDecimal.scale();
    }
    
    int currentUnionMemberIndex(final Any any) throws BadKind {
        if (this._kind != 16) {
            throw new BadKind();
        }
        try {
            for (int i = 0; i < this.member_count(); ++i) {
                if (this.member_label(i).equal(any)) {
                    return i;
                }
            }
            if (this._defaultIndex != -1) {
                return this._defaultIndex;
            }
        }
        catch (final BadKind badKind) {}
        catch (final Bounds bounds) {}
        return -1;
    }
    
    public String description() {
        return "TypeCodeImpl with kind " + this._kind + " and id " + this._id;
    }
    
    @Override
    public String toString() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        this.printStream(new PrintStream(byteArrayOutputStream, true));
        return super.toString() + " =\n" + byteArrayOutputStream.toString();
    }
    
    public void printStream(final PrintStream printStream) {
        this.printStream(printStream, 0);
    }
    
    private void printStream(final PrintStream printStream, final int n) {
        if (this._kind == -1) {
            printStream.print("indirect " + this._id);
            return;
        }
        switch (this._kind) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 23:
            case 24:
            case 25:
            case 26:
            case 31: {
                printStream.print(TypeCodeImpl.kindNames[this._kind] + " " + this._name);
                break;
            }
            case 15:
            case 22:
            case 29: {
                printStream.println(TypeCodeImpl.kindNames[this._kind] + " " + this._name + " = {");
                for (int i = 0; i < this._memberCount; ++i) {
                    printStream.print(this.indent(n + 1));
                    if (this._memberTypes[i] != null) {
                        this._memberTypes[i].printStream(printStream, n + 1);
                    }
                    else {
                        printStream.print("<unknown type>");
                    }
                    printStream.println(" " + this._memberNames[i] + ";");
                }
                printStream.print(this.indent(n) + "}");
                break;
            }
            case 16: {
                printStream.print("union " + this._name + "...");
                break;
            }
            case 17: {
                printStream.print("enum " + this._name + "...");
                break;
            }
            case 18: {
                if (this._length == 0) {
                    printStream.print("unbounded string " + this._name);
                    break;
                }
                printStream.print("bounded string(" + this._length + ") " + this._name);
                break;
            }
            case 19:
            case 20: {
                printStream.println(TypeCodeImpl.kindNames[this._kind] + "[" + this._length + "] " + this._name + " = {");
                printStream.print(this.indent(n + 1));
                if (this.lazy_content_type() != null) {
                    this.lazy_content_type().printStream(printStream, n + 1);
                }
                printStream.println(this.indent(n) + "}");
                break;
            }
            case 21: {
                printStream.print("alias " + this._name + " = " + ((this._contentType != null) ? this._contentType._name : "<unresolved>"));
                break;
            }
            case 27: {
                printStream.print("wstring[" + this._length + "] " + this._name);
                break;
            }
            case 28: {
                printStream.print("fixed(" + this._digits + ", " + this._scale + ") " + this._name);
                break;
            }
            case 30: {
                printStream.print("valueBox " + this._name + "...");
                break;
            }
            case 32: {
                printStream.print("abstractInterface " + this._name + "...");
                break;
            }
            default: {
                printStream.print("<unknown type>");
                break;
            }
        }
    }
    
    private String indent(final int n) {
        String string = "";
        for (int i = 0; i < n; ++i) {
            string += "  ";
        }
        return string;
    }
    
    protected void setCaching(final boolean cachingEnabled) {
        if (!(this.cachingEnabled = cachingEnabled)) {
            this.outBuffer = null;
        }
    }
    
    static {
        typeTable = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 1, 2, 2, 2, 2, 0, 0, 0, 0, 1, 1, 2, 2, 2, 2 };
        kindNames = new String[] { "null", "void", "short", "long", "ushort", "ulong", "float", "double", "boolean", "char", "octet", "any", "typecode", "principal", "objref", "struct", "union", "enum", "string", "sequence", "array", "alias", "exception", "longlong", "ulonglong", "longdouble", "wchar", "wstring", "fixed", "value", "valueBox", "native", "abstractInterface" };
    }
}
