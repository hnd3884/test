package com.sun.corba.se.spi.activation;

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

public class _ActivatorStub extends ObjectImpl implements Activator
{
    private static String[] __ids;
    
    @Override
    public void active(final int n, final Server server) throws ServerNotRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("active", true);
            ServerIdHelper.write(request, n);
            ServerHelper.write(request, server);
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
            this.active(n, server);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void registerEndpoints(final int n, final String s, final EndPointInfo[] array) throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("registerEndpoints", true);
            ServerIdHelper.write(request, n);
            ORBidHelper.write(request, s);
            EndpointInfoListHelper.write(request, array);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/NoSuchEndPoint:1.0")) {
                throw NoSuchEndPointHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/ORBAlreadyRegistered:1.0")) {
                throw ORBAlreadyRegisteredHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.registerEndpoints(n, s, array);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public int[] getActiveServers() {
        InputStream inputStream = null;
        try {
            inputStream = this._invoke(this._request("getActiveServers", true));
            return ServerIdsHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            throw new MARSHAL(ex.getId());
        }
        catch (final RemarshalException ex2) {
            return this.getActiveServers();
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void activate(final int n) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("activate", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerAlreadyActive:1.0")) {
                throw ServerAlreadyActiveHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/ServerHeldDown:1.0")) {
                throw ServerHeldDownHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.activate(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void shutdown(final int n) throws ServerNotActive, ServerNotRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("shutdown", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/ServerNotActive:1.0")) {
                throw ServerNotActiveHelper.read(inputStream);
            }
            if (id.equals("IDL:activation/ServerNotRegistered:1.0")) {
                throw ServerNotRegisteredHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            this.shutdown(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void install(final int n) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled {
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
            if (id.equals("IDL:activation/ServerHeldDown:1.0")) {
                throw ServerHeldDownHelper.read(inputStream);
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
    public String[] getORBNames(final int n) throws ServerNotRegistered {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("getORBNames", true);
            ServerIdHelper.write(request, n);
            inputStream = this._invoke(request);
            return ORBidListHelper.read(inputStream);
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
            return this.getORBNames(n);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public void uninstall(final int n) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled {
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
            if (id.equals("IDL:activation/ServerHeldDown:1.0")) {
                throw ServerHeldDownHelper.read(inputStream);
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
    public String[] _ids() {
        return _ActivatorStub.__ids.clone();
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
        _ActivatorStub.__ids = new String[] { "IDL:activation/Activator:1.0" };
    }
}
