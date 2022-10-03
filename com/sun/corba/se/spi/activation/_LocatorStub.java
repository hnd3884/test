package com.sun.corba.se.spi.activation;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.MARSHAL;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import org.omg.CORBA.portable.ObjectImpl;

public class _LocatorStub extends ObjectImpl implements Locator
{
    private static String[] __ids;
    
    @Override
    public ServerLocation locateServer(final int n, final String s) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("locateServer", true);
            ServerIdHelper.write(request, n);
            request.write_string(s);
            inputStream = this._invoke(request);
            return ServerLocationHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/NoSuchEndPoint:1.0")) {
                throw NoSuchEndPointHelper.read(inputStream);
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
            return this.locateServer(n, s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public ServerLocationPerORB locateServerForORB(final int n, final String s) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("locateServerForORB", true);
            ServerIdHelper.write(request, n);
            ORBidHelper.write(request, s);
            inputStream = this._invoke(request);
            return ServerLocationPerORBHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/InvalidORBid:1.0")) {
                throw InvalidORBidHelper.read(inputStream);
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
            return this.locateServerForORB(n, s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public int getEndpoint(final String s) throws NoSuchEndPoint {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("getEndpoint", true);
            request.write_string(s);
            inputStream = this._invoke(request);
            return TCPPortHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/NoSuchEndPoint:1.0")) {
                throw NoSuchEndPointHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.getEndpoint(s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public int getServerPortForType(final ServerLocationPerORB serverLocationPerORB, final String s) throws NoSuchEndPoint {
        InputStream inputStream = null;
        try {
            final OutputStream request = this._request("getServerPortForType", true);
            ServerLocationPerORBHelper.write(request, serverLocationPerORB);
            request.write_string(s);
            inputStream = this._invoke(request);
            return TCPPortHelper.read(inputStream);
        }
        catch (final ApplicationException ex) {
            inputStream = ex.getInputStream();
            final String id = ex.getId();
            if (id.equals("IDL:activation/NoSuchEndPoint:1.0")) {
                throw NoSuchEndPointHelper.read(inputStream);
            }
            throw new MARSHAL(id);
        }
        catch (final RemarshalException ex2) {
            return this.getServerPortForType(serverLocationPerORB, s);
        }
        finally {
            this._releaseReply(inputStream);
        }
    }
    
    @Override
    public String[] _ids() {
        return _LocatorStub.__ids.clone();
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
        _LocatorStub.__ids = new String[] { "IDL:activation/Locator:1.0" };
    }
}
