package com.adventnet.cli.rmi;

import java.io.ObjectOutput;
import com.adventnet.cli.CLIMessage;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.util.Properties;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.io.IOException;
import java.rmi.MarshalException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.server.Operation;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class CLISessionImpl_Stub extends RemoteStub implements CLISession, Remote
{
    private static final Operation[] operations;
    private static final long interfaceHash = -2688865229007395434L;
    private static final long serialVersionUID = 2L;
    private static boolean useNewInvoke;
    private static Method $method_addCLIClient_0;
    private static Method $method_addConnectionListener_1;
    private static Method $method_close_2;
    private static Method $method_getCLIClientsSize_3;
    private static Method $method_getCLIPrompt_4;
    private static Method $method_getCLIPromptAction_5;
    private static Method $method_getCLIProtocolOptions_6;
    private static Method $method_getDebugLevel_7;
    private static Method $method_getInitialMessage_8;
    private static Method $method_getInterruptCmd_9;
    private static Method $method_getKeepAliveTimeout_10;
    private static Method $method_getMaxConnections_11;
    private static Method $method_getRequestTimeout_12;
    private static Method $method_getResourceManager_13;
    private static Method $method_getTransportProviderClassName_14;
    private static Method $method_isSetDebug_15;
    private static Method $method_isSetIgnoreSpecialCharacters_16;
    private static Method $method_isSetPooling_17;
    private static Method $method_open_18;
    private static Method $method_removeCLIClient_19;
    private static Method $method_removeConnectionListener_20;
    private static Method $method_send_21;
    private static Method $method_setCLIPrompt_22;
    private static Method $method_setCLIPromptAction_23;
    private static Method $method_setCLIProtocolOptions_24;
    private static Method $method_setDebug_25;
    private static Method $method_setDebugLevel_26;
    private static Method $method_setIgnoreSpecialCharacters_27;
    private static Method $method_setInterruptCmd_28;
    private static Method $method_setKeepAliveTimeout_29;
    private static Method $method_setMaxConnections_30;
    private static Method $method_setPooling_31;
    private static Method $method_setRequestTimeout_32;
    private static Method $method_setTransportProviderClassName_33;
    private static Method $method_syncSend_34;
    static /* synthetic */ Class class$java$rmi$server$RemoteRef;
    static /* synthetic */ Class class$java$rmi$Remote;
    static /* synthetic */ Class class$java$lang$reflect$Method;
    static /* synthetic */ Class array$Ljava$lang$Object;
    static /* synthetic */ Class class$com$adventnet$cli$rmi$CLISession;
    static /* synthetic */ Class class$com$adventnet$cli$rmi$CLIClient;
    static /* synthetic */ Class class$com$adventnet$cli$rmi$ConnectionListener;
    static /* synthetic */ Class class$com$adventnet$cli$CLIMessage;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$util$Properties;
    static /* synthetic */ Class class$com$adventnet$cli$transport$CLIProtocolOptions;
    
    static {
        operations = new Operation[] { new Operation("void addCLIClient(com.adventnet.cli.rmi.CLIClient)"), new Operation("void addConnectionListener(com.adventnet.cli.rmi.ConnectionListener)"), new Operation("void close()"), new Operation("int getCLIClientsSize()"), new Operation("java.lang.String getCLIPrompt()"), new Operation("java.util.Properties getCLIPromptAction()"), new Operation("com.adventnet.cli.transport.CLIProtocolOptions getCLIProtocolOptions()"), new Operation("int getDebugLevel()"), new Operation("java.lang.String getInitialMessage()"), new Operation("java.lang.String getInterruptCmd()"), new Operation("int getKeepAliveTimeout()"), new Operation("int getMaxConnections()"), new Operation("int getRequestTimeout()"), new Operation("com.adventnet.cli.rmi.CLIResourceManager getResourceManager()"), new Operation("java.lang.String getTransportProviderClassName()"), new Operation("boolean isSetDebug()"), new Operation("boolean isSetIgnoreSpecialCharacters()"), new Operation("boolean isSetPooling()"), new Operation("void open()"), new Operation("void removeCLIClient(com.adventnet.cli.rmi.CLIClient)"), new Operation("void removeConnectionListener(com.adventnet.cli.rmi.ConnectionListener)"), new Operation("int send(com.adventnet.cli.CLIMessage)"), new Operation("void setCLIPrompt(java.lang.String)"), new Operation("void setCLIPromptAction(java.util.Properties)"), new Operation("void setCLIProtocolOptions(com.adventnet.cli.transport.CLIProtocolOptions)"), new Operation("void setDebug(boolean)"), new Operation("void setDebugLevel(int)"), new Operation("void setIgnoreSpecialCharacters(boolean)"), new Operation("void setInterruptCmd(java.lang.String)"), new Operation("void setKeepAliveTimeout(int)"), new Operation("void setMaxConnections(int)"), new Operation("void setPooling(boolean)"), new Operation("void setRequestTimeout(int)"), new Operation("void setTransportProviderClassName(java.lang.String)"), new Operation("com.adventnet.cli.CLIMessage syncSend(com.adventnet.cli.CLIMessage)") };
        try {
            ((CLISessionImpl_Stub.class$java$rmi$server$RemoteRef != null) ? CLISessionImpl_Stub.class$java$rmi$server$RemoteRef : (CLISessionImpl_Stub.class$java$rmi$server$RemoteRef = class$("java.rmi.server.RemoteRef"))).getMethod("invoke", (CLISessionImpl_Stub.class$java$rmi$Remote != null) ? CLISessionImpl_Stub.class$java$rmi$Remote : (CLISessionImpl_Stub.class$java$rmi$Remote = class$("java.rmi.Remote")), (CLISessionImpl_Stub.class$java$lang$reflect$Method != null) ? CLISessionImpl_Stub.class$java$lang$reflect$Method : (CLISessionImpl_Stub.class$java$lang$reflect$Method = class$("java.lang.reflect.Method")), (CLISessionImpl_Stub.array$Ljava$lang$Object != null) ? CLISessionImpl_Stub.array$Ljava$lang$Object : (CLISessionImpl_Stub.array$Ljava$lang$Object = class$("[Ljava.lang.Object;")), Long.TYPE);
            CLISessionImpl_Stub.useNewInvoke = true;
            CLISessionImpl_Stub.$method_addCLIClient_0 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("addCLIClient", (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLIClient != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLIClient : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLIClient = class$("com.adventnet.cli.rmi.CLIClient")));
            CLISessionImpl_Stub.$method_addConnectionListener_1 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("addConnectionListener", (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$ConnectionListener != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$ConnectionListener : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$ConnectionListener = class$("com.adventnet.cli.rmi.ConnectionListener")));
            CLISessionImpl_Stub.$method_close_2 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("close", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getCLIClientsSize_3 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getCLIClientsSize", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getCLIPrompt_4 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getCLIPrompt", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getCLIPromptAction_5 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getCLIPromptAction", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getCLIProtocolOptions_6 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getCLIProtocolOptions", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getDebugLevel_7 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getDebugLevel", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getInitialMessage_8 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getInitialMessage", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getInterruptCmd_9 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getInterruptCmd", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getKeepAliveTimeout_10 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getKeepAliveTimeout", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getMaxConnections_11 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getMaxConnections", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getRequestTimeout_12 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getRequestTimeout", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getResourceManager_13 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getResourceManager", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_getTransportProviderClassName_14 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("getTransportProviderClassName", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_isSetDebug_15 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("isSetDebug", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_isSetIgnoreSpecialCharacters_16 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("isSetIgnoreSpecialCharacters", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_isSetPooling_17 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("isSetPooling", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_open_18 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("open", (Class[])new Class[0]);
            CLISessionImpl_Stub.$method_removeCLIClient_19 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("removeCLIClient", (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLIClient != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLIClient : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLIClient = class$("com.adventnet.cli.rmi.CLIClient")));
            CLISessionImpl_Stub.$method_removeConnectionListener_20 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("removeConnectionListener", (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$ConnectionListener != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$ConnectionListener : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$ConnectionListener = class$("com.adventnet.cli.rmi.ConnectionListener")));
            CLISessionImpl_Stub.$method_send_21 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("send", (CLISessionImpl_Stub.class$com$adventnet$cli$CLIMessage != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$CLIMessage : (CLISessionImpl_Stub.class$com$adventnet$cli$CLIMessage = class$("com.adventnet.cli.CLIMessage")));
            CLISessionImpl_Stub.$method_setCLIPrompt_22 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setCLIPrompt", (CLISessionImpl_Stub.class$java$lang$String != null) ? CLISessionImpl_Stub.class$java$lang$String : (CLISessionImpl_Stub.class$java$lang$String = class$("java.lang.String")));
            CLISessionImpl_Stub.$method_setCLIPromptAction_23 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setCLIPromptAction", (CLISessionImpl_Stub.class$java$util$Properties != null) ? CLISessionImpl_Stub.class$java$util$Properties : (CLISessionImpl_Stub.class$java$util$Properties = class$("java.util.Properties")));
            CLISessionImpl_Stub.$method_setCLIProtocolOptions_24 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setCLIProtocolOptions", (CLISessionImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions : (CLISessionImpl_Stub.class$com$adventnet$cli$transport$CLIProtocolOptions = class$("com.adventnet.cli.transport.CLIProtocolOptions")));
            CLISessionImpl_Stub.$method_setDebug_25 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setDebug", Boolean.TYPE);
            CLISessionImpl_Stub.$method_setDebugLevel_26 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setDebugLevel", Integer.TYPE);
            CLISessionImpl_Stub.$method_setIgnoreSpecialCharacters_27 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setIgnoreSpecialCharacters", Boolean.TYPE);
            CLISessionImpl_Stub.$method_setInterruptCmd_28 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setInterruptCmd", (CLISessionImpl_Stub.class$java$lang$String != null) ? CLISessionImpl_Stub.class$java$lang$String : (CLISessionImpl_Stub.class$java$lang$String = class$("java.lang.String")));
            CLISessionImpl_Stub.$method_setKeepAliveTimeout_29 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setKeepAliveTimeout", Integer.TYPE);
            CLISessionImpl_Stub.$method_setMaxConnections_30 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setMaxConnections", Integer.TYPE);
            CLISessionImpl_Stub.$method_setPooling_31 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setPooling", Boolean.TYPE);
            CLISessionImpl_Stub.$method_setRequestTimeout_32 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setRequestTimeout", Integer.TYPE);
            CLISessionImpl_Stub.$method_setTransportProviderClassName_33 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("setTransportProviderClassName", (CLISessionImpl_Stub.class$java$lang$String != null) ? CLISessionImpl_Stub.class$java$lang$String : (CLISessionImpl_Stub.class$java$lang$String = class$("java.lang.String")));
            CLISessionImpl_Stub.$method_syncSend_34 = ((CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession : (CLISessionImpl_Stub.class$com$adventnet$cli$rmi$CLISession = class$("com.adventnet.cli.rmi.CLISession"))).getMethod("syncSend", (CLISessionImpl_Stub.class$com$adventnet$cli$CLIMessage != null) ? CLISessionImpl_Stub.class$com$adventnet$cli$CLIMessage : (CLISessionImpl_Stub.class$com$adventnet$cli$CLIMessage = class$("com.adventnet.cli.CLIMessage")));
        }
        catch (final NoSuchMethodException ex) {
            CLISessionImpl_Stub.useNewInvoke = false;
        }
    }
    
    public CLISessionImpl_Stub() {
    }
    
    public CLISessionImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    public void addCLIClient(final CLIClient cliClient) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_addCLIClient_0, new Object[] { cliClient }, -7654427063607400320L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 0, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(cliClient);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void addConnectionListener(final ConnectionListener connectionListener) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_addConnectionListener_1, new Object[] { connectionListener }, 3258586727660034206L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 1, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(connectionListener);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public void close() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_close_2, null, -4742752445160157748L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 2, -2688865229007395434L);
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public int getCLIClientsSize() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLISessionImpl_Stub.$method_getCLIClientsSize_3, null, -7988495090462633788L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 3, -2688865229007395434L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public String getCLIPrompt() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (String)super.ref.invoke(this, CLISessionImpl_Stub.$method_getCLIPrompt_4, null, 5833517618819025275L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 4, -2688865229007395434L);
            super.ref.invoke(call);
            String s;
            try {
                s = (String)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return s;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public Properties getCLIPromptAction() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (Properties)super.ref.invoke(this, CLISessionImpl_Stub.$method_getCLIPromptAction_5, null, 5097756750083467711L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 5, -2688865229007395434L);
            super.ref.invoke(call);
            Properties properties;
            try {
                properties = (Properties)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return properties;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public CLIProtocolOptions getCLIProtocolOptions() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (CLIProtocolOptions)super.ref.invoke(this, CLISessionImpl_Stub.$method_getCLIProtocolOptions_6, null, 4469770184685420537L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 6, -2688865229007395434L);
            super.ref.invoke(call);
            CLIProtocolOptions cliProtocolOptions;
            try {
                cliProtocolOptions = (CLIProtocolOptions)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return cliProtocolOptions;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public int getDebugLevel() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLISessionImpl_Stub.$method_getDebugLevel_7, null, 8250172403747277032L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 7, -2688865229007395434L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public String getInitialMessage() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (String)super.ref.invoke(this, CLISessionImpl_Stub.$method_getInitialMessage_8, null, 5801693936419199023L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 8, -2688865229007395434L);
            super.ref.invoke(call);
            String s;
            try {
                s = (String)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return s;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public String getInterruptCmd() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (String)super.ref.invoke(this, CLISessionImpl_Stub.$method_getInterruptCmd_9, null, 4708491262652393107L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 9, -2688865229007395434L);
            super.ref.invoke(call);
            String s;
            try {
                s = (String)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return s;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public int getKeepAliveTimeout() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLISessionImpl_Stub.$method_getKeepAliveTimeout_10, null, 9049373075317492944L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 10, -2688865229007395434L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public int getMaxConnections() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLISessionImpl_Stub.$method_getMaxConnections_11, null, -6614033420789451520L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 11, -2688865229007395434L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public int getRequestTimeout() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLISessionImpl_Stub.$method_getRequestTimeout_12, null, -8372516440506460413L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 12, -2688865229007395434L);
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public CLIResourceManager getResourceManager() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (CLIResourceManager)super.ref.invoke(this, CLISessionImpl_Stub.$method_getResourceManager_13, null, 7803003237860320922L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 13, -2688865229007395434L);
            super.ref.invoke(call);
            CLIResourceManager cliResourceManager;
            try {
                cliResourceManager = (CLIResourceManager)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return cliResourceManager;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public String getTransportProviderClassName() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (String)super.ref.invoke(this, CLISessionImpl_Stub.$method_getTransportProviderClassName_14, null, 7797290759689912709L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 14, -2688865229007395434L);
            super.ref.invoke(call);
            String s;
            try {
                s = (String)call.getInputStream().readObject();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            catch (final ClassNotFoundException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return s;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public boolean isSetDebug() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (boolean)super.ref.invoke(this, CLISessionImpl_Stub.$method_isSetDebug_15, null, 103246940272821447L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 15, -2688865229007395434L);
            super.ref.invoke(call);
            boolean boolean1;
            try {
                boolean1 = call.getInputStream().readBoolean();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return boolean1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public boolean isSetIgnoreSpecialCharacters() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (boolean)super.ref.invoke(this, CLISessionImpl_Stub.$method_isSetIgnoreSpecialCharacters_16, null, -703676580845550411L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 16, -2688865229007395434L);
            super.ref.invoke(call);
            boolean boolean1;
            try {
                boolean1 = call.getInputStream().readBoolean();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return boolean1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public boolean isSetPooling() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (boolean)super.ref.invoke(this, CLISessionImpl_Stub.$method_isSetPooling_17, null, 8716287632483877530L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 17, -2688865229007395434L);
            super.ref.invoke(call);
            boolean boolean1;
            try {
                boolean1 = call.getInputStream().readBoolean();
            }
            catch (final IOException ex) {
                throw new UnmarshalException("error unmarshalling return", ex);
            }
            finally {
                super.ref.done(call);
            }
            return boolean1;
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void open() throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_open_18, null, 2108930122558793662L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 18, -2688865229007395434L);
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public void removeCLIClient(final CLIClient cliClient) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_removeCLIClient_19, new Object[] { cliClient }, 8132075043954259895L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 19, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(cliClient);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void removeConnectionListener(final ConnectionListener connectionListener) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_removeConnectionListener_20, new Object[] { connectionListener }, 2025112918616732512L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 20, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(connectionListener);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public int send(final CLIMessage cliMessage) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (int)super.ref.invoke(this, CLISessionImpl_Stub.$method_send_21, new Object[] { cliMessage }, -2150685831246371616L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 21, -2688865229007395434L);
            try {
                call.getOutputStream().writeObject(cliMessage);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            super.ref.invoke(call);
            int int1;
            try {
                int1 = call.getInputStream().readInt();
            }
            catch (final IOException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            finally {
                super.ref.done(call);
            }
            return int1;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final RemoteException ex4) {
            throw ex4;
        }
        catch (final Exception ex5) {
            throw new UnexpectedException("undeclared checked exception", ex5);
        }
    }
    
    public void setCLIPrompt(final String s) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setCLIPrompt_22, new Object[] { s }, 507499627533133429L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 22, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(s);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setCLIPromptAction(final Properties properties) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setCLIPromptAction_23, new Object[] { properties }, -2704595152191911123L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 23, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(properties);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setCLIProtocolOptions(final CLIProtocolOptions cliProtocolOptions) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setCLIProtocolOptions_24, new Object[] { cliProtocolOptions }, -2959263091887086154L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 24, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(cliProtocolOptions);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setDebug(final boolean b) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setDebug_25, new Object[] { new Boolean(b) }, -3195502464907809318L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 25, -2688865229007395434L);
                try {
                    call.getOutputStream().writeBoolean(b);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setDebugLevel(final int n) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setDebugLevel_26, new Object[] { new Integer(n) }, 4300081604050436896L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 26, -2688865229007395434L);
                try {
                    call.getOutputStream().writeInt(n);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setIgnoreSpecialCharacters(final boolean b) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setIgnoreSpecialCharacters_27, new Object[] { new Boolean(b) }, 3699933648070141756L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 27, -2688865229007395434L);
                try {
                    call.getOutputStream().writeBoolean(b);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setInterruptCmd(final String s) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setInterruptCmd_28, new Object[] { s }, -9026050086541106607L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 28, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(s);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setKeepAliveTimeout(final int n) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setKeepAliveTimeout_29, new Object[] { new Integer(n) }, 9212739374310597693L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 29, -2688865229007395434L);
                try {
                    call.getOutputStream().writeInt(n);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setMaxConnections(final int n) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setMaxConnections_30, new Object[] { new Integer(n) }, -3216999682298717359L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 30, -2688865229007395434L);
                try {
                    call.getOutputStream().writeInt(n);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setPooling(final boolean b) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setPooling_31, new Object[] { new Boolean(b) }, 3910746245484721574L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 31, -2688865229007395434L);
                try {
                    call.getOutputStream().writeBoolean(b);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setRequestTimeout(final int n) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setRequestTimeout_32, new Object[] { new Integer(n) }, -400062043927202424L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 32, -2688865229007395434L);
                try {
                    call.getOutputStream().writeInt(n);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void setTransportProviderClassName(final String s) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                super.ref.invoke(this, CLISessionImpl_Stub.$method_setTransportProviderClassName_33, new Object[] { s }, 6172408367923534378L);
            }
            else {
                final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 33, -2688865229007395434L);
                try {
                    call.getOutputStream().writeObject(s);
                }
                catch (final IOException ex) {
                    throw new MarshalException("error marshalling arguments", ex);
                }
                super.ref.invoke(call);
                super.ref.done(call);
            }
        }
        catch (final RuntimeException ex2) {
            throw ex2;
        }
        catch (final RemoteException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public CLIMessage syncSend(final CLIMessage cliMessage) throws RemoteException {
        try {
            if (CLISessionImpl_Stub.useNewInvoke) {
                return (CLIMessage)super.ref.invoke(this, CLISessionImpl_Stub.$method_syncSend_34, new Object[] { cliMessage }, -2000283983769136908L);
            }
            final RemoteCall call = super.ref.newCall(this, CLISessionImpl_Stub.operations, 34, -2688865229007395434L);
            try {
                final Object outputStream = call.getOutputStream();
                ((ObjectOutput)outputStream).writeObject(cliMessage);
            }
            catch (final IOException ex) {
                throw new MarshalException("error marshalling arguments", ex);
            }
            super.ref.invoke(call);
            Object outputStream;
            try {
                outputStream = call.getInputStream().readObject();
            }
            catch (final IOException ex2) {
                throw new UnmarshalException("error unmarshalling return", ex2);
            }
            catch (final ClassNotFoundException ex3) {
                throw new UnmarshalException("error unmarshalling return", ex3);
            }
            finally {
                super.ref.done(call);
            }
            return (CLIMessage)outputStream;
        }
        catch (final RuntimeException ex4) {
            throw ex4;
        }
        catch (final RemoteException ex5) {
            throw ex5;
        }
        catch (final Exception ex6) {
            throw new UnexpectedException("undeclared checked exception", ex6);
        }
    }
}
