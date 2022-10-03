package org.omg.CosNaming;

import org.omg.CORBA.Object;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.ObjectImpl;

public class _BindingIteratorStub extends ObjectImpl implements BindingIterator
{
    private static String[] __ids;
    
    @Override
    public boolean next_one(final BindingHolder bindingHolder) {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("next_one", true));
            final boolean read_boolean = inputStream.read_boolean();
            bindingHolder.value = BindingHelper.read(inputStream);
            return read_boolean;
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.next_one(bindingHolder);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public boolean next_n(final int n, final BindingListHolder bindingListHolder) {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("next_n", true);
            request.write_ulong(n);
            inputStream = this._invoke(request);
            final boolean read_boolean = inputStream.read_boolean();
            bindingListHolder.value = BindingListHelper.read(inputStream);
            return read_boolean;
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.next_n(n, bindingListHolder);
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
        return _BindingIteratorStub.__ids.clone();
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
        _BindingIteratorStub.__ids = new String[] { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
    }
}
