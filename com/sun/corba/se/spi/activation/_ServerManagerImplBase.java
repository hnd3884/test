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

public abstract class _ServerManagerImplBase extends ObjectImpl implements ServerManager, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final Integer n = _ServerManagerImplBase._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        OutputStream outputStream = null;
        switch (n) {
            case 0: {
                try {
                    this.active(ServerIdHelper.read(inputStream), ServerHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotRegistered serverNotRegistered) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
                }
                break;
            }
            case 1: {
                try {
                    this.registerEndpoints(ServerIdHelper.read(inputStream), ORBidHelper.read(inputStream), EndpointInfoListHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotRegistered serverNotRegistered2) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered2);
                }
                catch (final NoSuchEndPoint noSuchEndPoint) {
                    outputStream = responseHandler.createExceptionReply();
                    NoSuchEndPointHelper.write(outputStream, noSuchEndPoint);
                }
                catch (final ORBAlreadyRegistered orbAlreadyRegistered) {
                    outputStream = responseHandler.createExceptionReply();
                    ORBAlreadyRegisteredHelper.write(outputStream, orbAlreadyRegistered);
                }
                break;
            }
            case 2: {
                final int[] activeServers = this.getActiveServers();
                outputStream = responseHandler.createReply();
                ServerIdsHelper.write(outputStream, activeServers);
                break;
            }
            case 3: {
                try {
                    this.activate(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerAlreadyActive serverAlreadyActive) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerAlreadyActiveHelper.write(outputStream, serverAlreadyActive);
                }
                catch (final ServerNotRegistered serverNotRegistered3) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered3);
                }
                catch (final ServerHeldDown serverHeldDown) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerHeldDownHelper.write(outputStream, serverHeldDown);
                }
                break;
            }
            case 4: {
                try {
                    this.shutdown(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotActive serverNotActive) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotActiveHelper.write(outputStream, serverNotActive);
                }
                catch (final ServerNotRegistered serverNotRegistered4) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered4);
                }
                break;
            }
            case 5: {
                try {
                    this.install(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotRegistered serverNotRegistered5) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered5);
                }
                catch (final ServerHeldDown serverHeldDown2) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerHeldDownHelper.write(outputStream, serverHeldDown2);
                }
                catch (final ServerAlreadyInstalled serverAlreadyInstalled) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerAlreadyInstalledHelper.write(outputStream, serverAlreadyInstalled);
                }
                break;
            }
            case 6: {
                try {
                    final String[] orbNames = this.getORBNames(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    ORBidListHelper.write(outputStream, orbNames);
                }
                catch (final ServerNotRegistered serverNotRegistered6) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered6);
                }
                break;
            }
            case 7: {
                try {
                    this.uninstall(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotRegistered serverNotRegistered7) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered7);
                }
                catch (final ServerHeldDown serverHeldDown3) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerHeldDownHelper.write(outputStream, serverHeldDown3);
                }
                catch (final ServerAlreadyUninstalled serverAlreadyUninstalled) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerAlreadyUninstalledHelper.write(outputStream, serverAlreadyUninstalled);
                }
                break;
            }
            case 8: {
                try {
                    final ServerLocation locateServer = this.locateServer(ServerIdHelper.read(inputStream), inputStream.read_string());
                    outputStream = responseHandler.createReply();
                    ServerLocationHelper.write(outputStream, locateServer);
                }
                catch (final NoSuchEndPoint noSuchEndPoint2) {
                    outputStream = responseHandler.createExceptionReply();
                    NoSuchEndPointHelper.write(outputStream, noSuchEndPoint2);
                }
                catch (final ServerNotRegistered serverNotRegistered8) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered8);
                }
                catch (final ServerHeldDown serverHeldDown4) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerHeldDownHelper.write(outputStream, serverHeldDown4);
                }
                break;
            }
            case 9: {
                try {
                    final ServerLocationPerORB locateServerForORB = this.locateServerForORB(ServerIdHelper.read(inputStream), ORBidHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    ServerLocationPerORBHelper.write(outputStream, locateServerForORB);
                }
                catch (final InvalidORBid invalidORBid) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidORBidHelper.write(outputStream, invalidORBid);
                }
                catch (final ServerNotRegistered serverNotRegistered9) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered9);
                }
                catch (final ServerHeldDown serverHeldDown5) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerHeldDownHelper.write(outputStream, serverHeldDown5);
                }
                break;
            }
            case 10: {
                try {
                    final int endpoint = this.getEndpoint(inputStream.read_string());
                    outputStream = responseHandler.createReply();
                    outputStream.write_long(endpoint);
                }
                catch (final NoSuchEndPoint noSuchEndPoint3) {
                    outputStream = responseHandler.createExceptionReply();
                    NoSuchEndPointHelper.write(outputStream, noSuchEndPoint3);
                }
                break;
            }
            case 11: {
                try {
                    final int serverPortForType = this.getServerPortForType(ServerLocationPerORBHelper.read(inputStream), inputStream.read_string());
                    outputStream = responseHandler.createReply();
                    outputStream.write_long(serverPortForType);
                }
                catch (final NoSuchEndPoint noSuchEndPoint4) {
                    outputStream = responseHandler.createExceptionReply();
                    NoSuchEndPointHelper.write(outputStream, noSuchEndPoint4);
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
        return _ServerManagerImplBase.__ids.clone();
    }
    
    static {
        (_ServerManagerImplBase._methods = new Hashtable()).put("active", new Integer(0));
        _ServerManagerImplBase._methods.put("registerEndpoints", new Integer(1));
        _ServerManagerImplBase._methods.put("getActiveServers", new Integer(2));
        _ServerManagerImplBase._methods.put("activate", new Integer(3));
        _ServerManagerImplBase._methods.put("shutdown", new Integer(4));
        _ServerManagerImplBase._methods.put("install", new Integer(5));
        _ServerManagerImplBase._methods.put("getORBNames", new Integer(6));
        _ServerManagerImplBase._methods.put("uninstall", new Integer(7));
        _ServerManagerImplBase._methods.put("locateServer", new Integer(8));
        _ServerManagerImplBase._methods.put("locateServerForORB", new Integer(9));
        _ServerManagerImplBase._methods.put("getEndpoint", new Integer(10));
        _ServerManagerImplBase._methods.put("getServerPortForType", new Integer(11));
        _ServerManagerImplBase.__ids = new String[] { "IDL:activation/ServerManager:1.0", "IDL:activation/Activator:1.0", "IDL:activation/Locator:1.0" };
    }
}
