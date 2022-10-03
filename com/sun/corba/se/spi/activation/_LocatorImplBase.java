package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class _LocatorImplBase extends ObjectImpl implements Locator, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final Integer n = _LocatorImplBase._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        OutputStream outputStream = null;
        switch (n) {
            case 0: {
                try {
                    final ServerLocation locateServer = this.locateServer(ServerIdHelper.read(inputStream), inputStream.read_string());
                    outputStream = responseHandler.createReply();
                    ServerLocationHelper.write(outputStream, locateServer);
                }
                catch (final NoSuchEndPoint noSuchEndPoint) {
                    outputStream = responseHandler.createExceptionReply();
                    NoSuchEndPointHelper.write(outputStream, noSuchEndPoint);
                }
                catch (final ServerNotRegistered serverNotRegistered) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
                }
                catch (final ServerHeldDown serverHeldDown) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerHeldDownHelper.write(outputStream, serverHeldDown);
                }
                break;
            }
            case 1: {
                try {
                    final ServerLocationPerORB locateServerForORB = this.locateServerForORB(ServerIdHelper.read(inputStream), ORBidHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    ServerLocationPerORBHelper.write(outputStream, locateServerForORB);
                }
                catch (final InvalidORBid invalidORBid) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidORBidHelper.write(outputStream, invalidORBid);
                }
                catch (final ServerNotRegistered serverNotRegistered2) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered2);
                }
                catch (final ServerHeldDown serverHeldDown2) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerHeldDownHelper.write(outputStream, serverHeldDown2);
                }
                break;
            }
            case 2: {
                try {
                    final int endpoint = this.getEndpoint(inputStream.read_string());
                    outputStream = responseHandler.createReply();
                    outputStream.write_long(endpoint);
                }
                catch (final NoSuchEndPoint noSuchEndPoint2) {
                    outputStream = responseHandler.createExceptionReply();
                    NoSuchEndPointHelper.write(outputStream, noSuchEndPoint2);
                }
                break;
            }
            case 3: {
                try {
                    final int serverPortForType = this.getServerPortForType(ServerLocationPerORBHelper.read(inputStream), inputStream.read_string());
                    outputStream = responseHandler.createReply();
                    outputStream.write_long(serverPortForType);
                }
                catch (final NoSuchEndPoint noSuchEndPoint3) {
                    outputStream = responseHandler.createExceptionReply();
                    NoSuchEndPointHelper.write(outputStream, noSuchEndPoint3);
                }
                break;
            }
            default: {
                throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
            }
        }
        return outputStream;
    }
    
    @Override
    public String[] _ids() {
        return _LocatorImplBase.__ids.clone();
    }
    
    static {
        (_LocatorImplBase._methods = new Hashtable()).put("locateServer", new Integer(0));
        _LocatorImplBase._methods.put("locateServerForORB", new Integer(1));
        _LocatorImplBase._methods.put("getEndpoint", new Integer(2));
        _LocatorImplBase._methods.put("getServerPortForType", new Integer(3));
        _LocatorImplBase.__ids = new String[] { "IDL:activation/Locator:1.0" };
    }
}
