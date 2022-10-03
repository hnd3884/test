package org.omg.CORBA;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;

public class _PolicyStub extends ObjectImpl implements Policy
{
    private static String[] __ids;
    
    public _PolicyStub() {
    }
    
    public _PolicyStub(final Delegate delegate) {
        this._set_delegate(delegate);
    }
    
    @Override
    public int policy_type() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("_get_policy_type", true));
            return PolicyTypeHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.policy_type();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public Policy copy() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("copy", true));
            return PolicyHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.copy();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void destroy() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("destroy", true));
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            this.destroy();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] _ids() {
        return _PolicyStub.__ids.clone();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) {
        try {
            this._set_delegate(((ObjectImpl)ORB.init().string_to_object(objectInputStream.readUTF()))._get_delegate());
        }
        catch (final IOException ex) {}
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) {
        try {
            objectOutputStream.writeUTF(ORB.init().object_to_string(this));
        }
        catch (final IOException ex) {}
    }
    
    static {
        _PolicyStub.__ids = new String[] { "IDL:omg.org/CORBA/Policy:1.0" };
    }
}
