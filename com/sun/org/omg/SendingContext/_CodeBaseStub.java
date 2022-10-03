package com.sun.org.omg.SendingContext;

import org.omg.CORBA.Object;
import java.io.ObjectOutputStream;
import java.io.IOException;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import org.omg.CORBA.portable.OutputStream;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.MARSHAL;
import com.sun.org.omg.CORBA.RepositoryHelper;
import com.sun.org.omg.CORBA.Repository;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;

public class _CodeBaseStub extends ObjectImpl implements CodeBase
{
    private static String[] __ids;
    
    public _CodeBaseStub() {
    }
    
    public _CodeBaseStub(final Delegate delegate) {
        this._set_delegate(delegate);
    }
    
    @Override
    public Repository get_ir() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("get_ir", true));
            return RepositoryHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.get_ir();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String implementation(final String s) {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("implementation", true);
            RepositoryIdHelper.write(request, s);
            inputStream = this._invoke(request);
            return URLHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.implementation(s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] implementations(final String[] array) {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("implementations", true);
            RepositoryIdSeqHelper.write(request, array);
            inputStream = this._invoke(request);
            return URLSeqHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.implementations(array);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public FullValueDescription meta(final String s) {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("meta", true);
            RepositoryIdHelper.write(request, s);
            inputStream = this._invoke(request);
            return FullValueDescriptionHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.meta(s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public FullValueDescription[] metas(final String[] array) {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("metas", true);
            RepositoryIdSeqHelper.write(request, array);
            inputStream = this._invoke(request);
            return ValueDescSeqHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.metas(array);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] bases(final String s) {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("bases", true);
            RepositoryIdHelper.write(request, s);
            inputStream = this._invoke(request);
            return RepositoryIdSeqHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.bases(s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] _ids() {
        return _CodeBaseStub.__ids.clone();
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
        _CodeBaseStub.__ids = new String[] { "IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0" };
    }
}
