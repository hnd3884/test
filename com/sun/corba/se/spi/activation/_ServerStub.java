package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Object;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.ObjectImpl;

public class _ServerStub extends ObjectImpl implements Server
{
    private static String[] __ids;
    
    @Override
    public void shutdown() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("shutdown", true));
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            this.shutdown();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void install() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("install", true));
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            this.install();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void uninstall() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("uninstall", true));
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            this.uninstall();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] _ids() {
        return _ServerStub.__ids.clone();
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
        _ServerStub.__ids = new String[] { "IDL:activation/Server:1.0" };
    }
}
