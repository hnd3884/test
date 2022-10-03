package org.omg.DynamicAny;

import org.omg.CORBA.Object;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.ObjectImpl;

public class _DynAnyFactoryStub extends ObjectImpl implements DynAnyFactory
{
    public static final Class _opsClass;
    private static String[] __ids;
    
    @Override
    public DynAny create_dyn_any(final Any any) throws InconsistentTypeCode {
        final ServantObject servant_preinvoke = this._servant_preinvoke("create_dyn_any", _DynAnyFactoryStub._opsClass);
        final DynAnyFactoryOperations dynAnyFactoryOperations = (DynAnyFactoryOperations)servant_preinvoke.servant;
        try {
            return dynAnyFactoryOperations.create_dyn_any(any);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public DynAny create_dyn_any_from_type_code(final TypeCode typeCode) throws InconsistentTypeCode {
        final ServantObject servant_preinvoke = this._servant_preinvoke("create_dyn_any_from_type_code", _DynAnyFactoryStub._opsClass);
        final DynAnyFactoryOperations dynAnyFactoryOperations = (DynAnyFactoryOperations)servant_preinvoke.servant;
        try {
            return dynAnyFactoryOperations.create_dyn_any_from_type_code(typeCode);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public String[] _ids() {
        return _DynAnyFactoryStub.__ids.clone();
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
        _opsClass = DynAnyFactoryOperations.class;
        _DynAnyFactoryStub.__ids = new String[] { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };
    }
}
