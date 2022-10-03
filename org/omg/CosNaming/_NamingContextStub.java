package org.omg.CosNaming;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.MARSHAL;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ObjectImpl;

public class _NamingContextStub extends ObjectImpl implements NamingContext
{
    private static String[] __ids;
    
    @Override
    public void bind(final NameComponent[] array, final Object object) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("bind", true);
            NameHelper.write(request, array);
            ObjectHelper.write(request, object);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw NotFoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
                throw AlreadyBoundHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.bind(array, object);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void bind_context(final NameComponent[] array, final NamingContext namingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("bind_context", true);
            NameHelper.write(request, array);
            NamingContextHelper.write(request, namingContext);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw NotFoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
                throw AlreadyBoundHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.bind_context(array, namingContext);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void rebind(final NameComponent[] array, final Object object) throws NotFound, CannotProceed, InvalidName {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("rebind", true);
            NameHelper.write(request, array);
            ObjectHelper.write(request, object);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw NotFoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.rebind(array, object);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void rebind_context(final NameComponent[] array, final NamingContext namingContext) throws NotFound, CannotProceed, InvalidName {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("rebind_context", true);
            NameHelper.write(request, array);
            NamingContextHelper.write(request, namingContext);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw NotFoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.rebind_context(array, namingContext);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public Object resolve(final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("resolve", true);
            NameHelper.write(request, array);
            inputStream = this._invoke(request);
            return ObjectHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw NotFoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.resolve(array);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void unbind(final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("unbind", true);
            NameHelper.write(request, array);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw NotFoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.unbind(array);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void list(final int n, final BindingListHolder bindingListHolder, final BindingIteratorHolder bindingIteratorHolder) {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("list", true);
            request.write_ulong(n);
            inputStream = this._invoke(request);
            bindingListHolder.value = BindingListHelper.read(inputStream);
            bindingIteratorHolder.value = BindingIteratorHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            this.list(n, bindingListHolder, bindingIteratorHolder);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public NamingContext new_context() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("new_context", true));
            return NamingContextHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.new_context();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public NamingContext bind_new_context(final NameComponent[] array) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("bind_new_context", true);
            NameHelper.write(request, array);
            inputStream = this._invoke(request);
            return NamingContextHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0")) {
                throw NotFoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0")) {
                throw AlreadyBoundHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0")) {
                throw CannotProceedHelper.read(inputStream);
            }
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0")) {
                throw InvalidNameHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.bind_new_context(array);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void destroy() throws NotEmpty {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("destroy", true));
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:omg.org/CosNaming/NamingContext/NotEmpty:1.0")) {
                throw NotEmptyHelper.read(inputStream);
            }
            throw new MARSHAL(id);
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
        return _NamingContextStub.__ids.clone();
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
        _NamingContextStub.__ids = new String[] { "IDL:omg.org/CosNaming/NamingContext:1.0" };
    }
}
