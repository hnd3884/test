package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class _RepositoryImplBase extends ObjectImpl implements Repository, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final Integer n = _RepositoryImplBase._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        OutputStream outputStream = null;
        switch (n) {
            case 0: {
                try {
                    final int registerServer = this.registerServer(ServerDefHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    outputStream.write_long(registerServer);
                }
                catch (final ServerAlreadyRegistered serverAlreadyRegistered) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerAlreadyRegisteredHelper.write(outputStream, serverAlreadyRegistered);
                }
                catch (final BadServerDefinition badServerDefinition) {
                    outputStream = responseHandler.createExceptionReply();
                    BadServerDefinitionHelper.write(outputStream, badServerDefinition);
                }
                break;
            }
            case 1: {
                try {
                    this.unregisterServer(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotRegistered serverNotRegistered) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
                }
                break;
            }
            case 2: {
                try {
                    final ServerDef server = this.getServer(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    ServerDefHelper.write(outputStream, server);
                }
                catch (final ServerNotRegistered serverNotRegistered2) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered2);
                }
                break;
            }
            case 3: {
                try {
                    final boolean installed = this.isInstalled(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    outputStream.write_boolean(installed);
                }
                catch (final ServerNotRegistered serverNotRegistered3) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered3);
                }
                break;
            }
            case 4: {
                try {
                    this.install(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotRegistered serverNotRegistered4) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered4);
                }
                catch (final ServerAlreadyInstalled serverAlreadyInstalled) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerAlreadyInstalledHelper.write(outputStream, serverAlreadyInstalled);
                }
                break;
            }
            case 5: {
                try {
                    this.uninstall(ServerIdHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final ServerNotRegistered serverNotRegistered5) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered5);
                }
                catch (final ServerAlreadyUninstalled serverAlreadyUninstalled) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerAlreadyUninstalledHelper.write(outputStream, serverAlreadyUninstalled);
                }
                break;
            }
            case 6: {
                final int[] listRegisteredServers = this.listRegisteredServers();
                outputStream = responseHandler.createReply();
                ServerIdsHelper.write(outputStream, listRegisteredServers);
                break;
            }
            case 7: {
                final String[] applicationNames = this.getApplicationNames();
                outputStream = responseHandler.createReply();
                StringSeqHelper.write(outputStream, applicationNames);
                break;
            }
            case 8: {
                try {
                    final int serverID = this.getServerID(inputStream.read_string());
                    outputStream = responseHandler.createReply();
                    outputStream.write_long(serverID);
                }
                catch (final ServerNotRegistered serverNotRegistered6) {
                    outputStream = responseHandler.createExceptionReply();
                    ServerNotRegisteredHelper.write(outputStream, serverNotRegistered6);
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
        return _RepositoryImplBase.__ids.clone();
    }
    
    static {
        (_RepositoryImplBase._methods = new Hashtable()).put("registerServer", new Integer(0));
        _RepositoryImplBase._methods.put("unregisterServer", new Integer(1));
        _RepositoryImplBase._methods.put("getServer", new Integer(2));
        _RepositoryImplBase._methods.put("isInstalled", new Integer(3));
        _RepositoryImplBase._methods.put("install", new Integer(4));
        _RepositoryImplBase._methods.put("uninstall", new Integer(5));
        _RepositoryImplBase._methods.put("listRegisteredServers", new Integer(6));
        _RepositoryImplBase._methods.put("getApplicationNames", new Integer(7));
        _RepositoryImplBase._methods.put("getServerID", new Integer(8));
        _RepositoryImplBase.__ids = new String[] { "IDL:activation/Repository:1.0" };
    }
}
