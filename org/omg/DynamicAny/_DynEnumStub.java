package org.omg.DynamicAny;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.portable.ObjectImpl;

public class _DynEnumStub extends ObjectImpl implements DynEnum
{
    public static final Class _opsClass;
    private static String[] __ids;
    
    @Override
    public String get_as_string() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_as_string", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_as_string();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void set_as_string(final String s) throws InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("set_as_string", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.set_as_string(s);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public int get_as_ulong() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_as_ulong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_as_ulong();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void set_as_ulong(final int n) throws InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("set_as_ulong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.set_as_ulong(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public TypeCode type() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("type", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.type();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void assign(final DynAny dynAny) throws TypeMismatch {
        final ServantObject servant_preinvoke = this._servant_preinvoke("assign", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.assign(dynAny);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void from_any(final Any any) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("from_any", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.from_any(any);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public Any to_any() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("to_any", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.to_any();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public boolean equal(final DynAny dynAny) {
        final ServantObject servant_preinvoke = this._servant_preinvoke("equal", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.equal(dynAny);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void destroy() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("destroy", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.destroy();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public DynAny copy() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("copy", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.copy();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_boolean(final boolean b) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_boolean", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_boolean(b);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_octet(final byte b) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_octet", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_octet(b);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_char(final char c) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_char", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_char(c);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_short(final short n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_short", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_short(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_ushort(final short n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_ushort", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_ushort(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_long(final int n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_long", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_long(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_ulong(final int n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_ulong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_ulong(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_float(final float n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_float", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_float(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_double(final double n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_double", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_double(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_string(final String s) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_string", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_string(s);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_reference(final Object object) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_reference", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_reference(object);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_typecode(final TypeCode typeCode) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_typecode", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_typecode(typeCode);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_longlong(final long n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_longlong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_longlong(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_ulonglong(final long n) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_ulonglong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_ulonglong(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_wchar(final char c) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_wchar", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_wchar(c);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_wstring(final String s) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_wstring", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_wstring(s);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_any(final Any any) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_any", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_any(any);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_dyn_any(final DynAny dynAny) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_dyn_any", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_dyn_any(dynAny);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void insert_val(final Serializable s) throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("insert_val", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.insert_val(s);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public boolean get_boolean() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_boolean", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_boolean();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public byte get_octet() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_octet", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_octet();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public char get_char() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_char", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_char();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public short get_short() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_short", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_short();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public short get_ushort() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_ushort", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_ushort();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public int get_long() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_long", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_long();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public int get_ulong() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_ulong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_ulong();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public float get_float() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_float", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_float();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public double get_double() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_double", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_double();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public String get_string() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_string", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_string();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public Object get_reference() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_reference", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_reference();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public TypeCode get_typecode() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_typecode", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_typecode();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public long get_longlong() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_longlong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_longlong();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public long get_ulonglong() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_ulonglong", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_ulonglong();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public char get_wchar() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_wchar", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_wchar();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public String get_wstring() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_wstring", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_wstring();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public Any get_any() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_any", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_any();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public DynAny get_dyn_any() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_dyn_any", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_dyn_any();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public Serializable get_val() throws TypeMismatch, InvalidValue {
        final ServantObject servant_preinvoke = this._servant_preinvoke("get_val", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.get_val();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public boolean seek(final int n) {
        final ServantObject servant_preinvoke = this._servant_preinvoke("seek", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.seek(n);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void rewind() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("rewind", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            dynEnumOperations.rewind();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public boolean next() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("next", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.next();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public int component_count() {
        final ServantObject servant_preinvoke = this._servant_preinvoke("component_count", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.component_count();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public DynAny current_component() throws TypeMismatch {
        final ServantObject servant_preinvoke = this._servant_preinvoke("current_component", _DynEnumStub._opsClass);
        final DynEnumOperations dynEnumOperations = (DynEnumOperations)servant_preinvoke.servant;
        try {
            return dynEnumOperations.current_component();
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public String[] _ids() {
        return _DynEnumStub.__ids.clone();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException {
        final String utf = objectInputStream.readUTF();
        final ORB init = ORB.init((String[])null, null);
        try {
            this._set_delegate(((ObjectImpl)init.string_to_object(utf))._get_delegate());
        }
        finally {
            init.destroy();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final ORB init = ORB.init((String[])null, null);
        try {
            objectOutputStream.writeUTF(init.object_to_string(this));
        }
        finally {
            init.destroy();
        }
    }
    
    static {
        _opsClass = DynEnumOperations.class;
        _DynEnumStub.__ids = new String[] { "IDL:omg.org/DynamicAny/DynEnum:1.0", "IDL:omg.org/DynamicAny/DynAny:1.0" };
    }
}
