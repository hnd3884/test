package com.sun.corba.se.spi.activation;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.MARSHAL;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import org.omg.CORBA.portable.ObjectImpl;

public class _RepositoryStub extends ObjectImpl implements Repository
{
    private static String[] __ids;
    
    @Override
    public int registerServer(final ServerDef serverDef) throws ServerAlreadyRegistered, BadServerDefinition {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("registerServer", true);
            ServerDefHelper.write(request, serverDef);
            inputStream = this._invoke(request);
            return ServerIdHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerAlreadyRegistered:1.0")) {
                throw ServerAlreadyRegisteredHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/BadServerDefinition:1.0")) {
                throw BadServerDefinitionHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.registerServer(serverDef);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void unregisterServer(final int n) throws ServerNotRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("unregisterServer", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.unregisterServer(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public ServerDef getServer(final int n) throws ServerNotRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("getServer", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
            return ServerDefHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.getServer(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public boolean isInstalled(final int n) throws ServerNotRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("isInstalled", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
            return inputStream.read_boolean();
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.isInstalled(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void install(final int n) throws ServerNotRegistered, ServerAlreadyInstalled {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("install", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/ServerAlreadyInstalled:1.0")) {
                throw ServerAlreadyInstalledHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.install(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void uninstall(final int n) throws ServerNotRegistered, ServerAlreadyUninstalled {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("uninstall", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/ServerAlreadyUninstalled:1.0")) {
                throw ServerAlreadyUninstalledHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.uninstall(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public int[] listRegisteredServers() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("listRegisteredServers", true));
            return ServerIdsHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.listRegisteredServers();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] getApplicationNames() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("getApplicationNames", true));
            return StringSeqHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.getApplicationNames();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public int getServerID(final String s) throws ServerNotRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("getServerID", true);
            request.write_string(s);
            inputStream = this._invoke(request);
            return ServerIdHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.getServerID(s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] _ids() {
        return _RepositoryStub.__ids.clone();
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
        _RepositoryStub.__ids = new String[] { "IDL:activation/Repository:1.0" };
    }
}
