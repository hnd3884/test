package com.sun.corba.se.spi.activation;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.MARSHAL;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ObjectImpl;

public class _InitialNameServiceStub extends ObjectImpl implements InitialNameService
{
    private static String[] __ids;
    
    @Override
    public void bind(final String s, final Object object, final boolean b) throws NameAlreadyBound {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("bind", true);
            request.write_string(s);
            ObjectHelper.write(request, object);
            request.write_boolean(b);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/InitialNameService/NameAlreadyBound:1.0")) {
                throw NameAlreadyBoundHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.bind(s, object, b);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] _ids() {
        return _InitialNameServiceStub.__ids.clone();
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
        _InitialNameServiceStub.__ids = new String[] { "IDL:activation/InitialNameService:1.0" };
    }
}
