package com.unboundid.ldap.listener;

import com.unboundid.util.StaticUtils;
import java.util.Iterator;
import java.util.Date;
import com.unboundid.ldap.protocol.UnbindRequestProtocolOp;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.ToCodeHelper;
import com.unboundid.ldap.sdk.ToCodeArgHelper;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.AbandonRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ToCodeRequestHandler extends LDAPListenerRequestHandler
{
    private final AtomicBoolean firstMessage;
    private final boolean includeProcessing;
    private final LDAPListenerClientConnection clientConnection;
    private final LDAPListenerRequestHandler requestHandler;
    private final PrintStream logStream;
    private final ThreadLocal<List<String>> lineLists;
    
    public ToCodeRequestHandler(final String outputFilePath, final boolean includeProcessing, final LDAPListenerRequestHandler requestHandler) throws IOException {
        this(new File(outputFilePath), includeProcessing, requestHandler);
    }
    
    public ToCodeRequestHandler(final File outputFile, final boolean includeProcessing, final LDAPListenerRequestHandler requestHandler) throws IOException {
        this(new FileOutputStream(outputFile, true), includeProcessing, requestHandler);
    }
    
    public ToCodeRequestHandler(final OutputStream outputStream, final boolean includeProcessing, final LDAPListenerRequestHandler requestHandler) {
        this.logStream = new PrintStream(outputStream, true);
        this.includeProcessing = includeProcessing;
        this.requestHandler = requestHandler;
        this.firstMessage = new AtomicBoolean(true);
        this.lineLists = new ThreadLocal<List<String>>();
        this.clientConnection = null;
    }
    
    private ToCodeRequestHandler(final ToCodeRequestHandler parentHandler, final LDAPListenerClientConnection connection) throws LDAPException {
        this.logStream = parentHandler.logStream;
        this.includeProcessing = parentHandler.includeProcessing;
        this.requestHandler = parentHandler.requestHandler.newInstance(connection);
        this.firstMessage = parentHandler.firstMessage;
        this.clientConnection = connection;
        this.lineLists = parentHandler.lineLists;
    }
    
    @Override
    public ToCodeRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        return new ToCodeRequestHandler(this, connection);
    }
    
    @Override
    public void closeInstance() {
        this.requestHandler.closeInstance();
        if (this.clientConnection == null) {
            synchronized (this.logStream) {
                this.logStream.close();
            }
        }
    }
    
    @Override
    public void processAbandonRequest(final int messageID, final AbandonRequestProtocolOp request, final List<Control> controls) {
        if (this.includeProcessing) {
            final List<String> lineList = this.getLineList(messageID);
            final ArrayList<ToCodeArgHelper> args = new ArrayList<ToCodeArgHelper>(2);
            args.add(ToCodeArgHelper.createRaw("asyncRequestID" + request.getIDToAbandon(), "Async Request ID"));
            if (!controls.isEmpty()) {
                final Control[] controlArray = new Control[controls.size()];
                controls.toArray(controlArray);
                args.add(ToCodeArgHelper.createControlArray(controlArray, "Request Controls"));
            }
            ToCodeHelper.generateMethodCall(lineList, 0, null, null, "connection.abandon", args);
            this.writeLines(lineList);
        }
        this.requestHandler.processAbandonRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "Add";
        final AddRequest addRequest = request.toAddRequest(getControlArray(controls));
        addRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processAddRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "Bind";
        final BindRequest bindRequest = request.toBindRequest(getControlArray(controls));
        bindRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processBindRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "Compare";
        final CompareRequest compareRequest = request.toCompareRequest(getControlArray(controls));
        compareRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processCompareRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "Delete";
        final DeleteRequest deleteRequest = request.toDeleteRequest(getControlArray(controls));
        deleteRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processDeleteRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "Extended";
        final ExtendedRequest extendedRequest = request.toExtendedRequest(getControlArray(controls));
        extendedRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processExtendedRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "Modify";
        final ModifyRequest modifyRequest = request.toModifyRequest(getControlArray(controls));
        modifyRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processModifyRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "ModifyDN";
        final ModifyDNRequest modifyDNRequest = request.toModifyDNRequest(getControlArray(controls));
        modifyDNRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processModifyDNRequest(messageID, request, controls);
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        final List<String> lineList = this.getLineList(messageID);
        final String requestID = "conn" + this.clientConnection.getConnectionID() + "Msg" + messageID + "Search";
        final SearchRequest searchRequest = request.toSearchRequest(getControlArray(controls));
        searchRequest.toCode(lineList, requestID, 0, this.includeProcessing);
        this.writeLines(lineList);
        return this.requestHandler.processSearchRequest(messageID, request, controls);
    }
    
    @Override
    public void processUnbindRequest(final int messageID, final UnbindRequestProtocolOp request, final List<Control> controls) {
        if (this.includeProcessing) {
            final List<String> lineList = this.getLineList(messageID);
            final ArrayList<ToCodeArgHelper> args = new ArrayList<ToCodeArgHelper>(1);
            if (!controls.isEmpty()) {
                final Control[] controlArray = new Control[controls.size()];
                controls.toArray(controlArray);
                args.add(ToCodeArgHelper.createControlArray(controlArray, "Request Controls"));
            }
            ToCodeHelper.generateMethodCall(lineList, 0, null, null, "connection.close", args);
            this.writeLines(lineList);
        }
        this.requestHandler.processUnbindRequest(messageID, request, controls);
    }
    
    private List<String> getLineList(final int messageID) {
        List<String> lineList = this.lineLists.get();
        if (lineList == null) {
            lineList = new ArrayList<String>(20);
            this.lineLists.set(lineList);
        }
        else {
            lineList.clear();
        }
        lineList.add("// Time:  " + new Date());
        lineList.add("// Client Address: " + this.clientConnection.getSocket().getInetAddress().getHostAddress() + ':' + this.clientConnection.getSocket().getPort());
        lineList.add("// Server Address: " + this.clientConnection.getSocket().getLocalAddress().getHostAddress() + ':' + this.clientConnection.getSocket().getLocalPort());
        lineList.add("// Connection ID: " + this.clientConnection.getConnectionID());
        lineList.add("// Message ID: " + messageID);
        return lineList;
    }
    
    private void writeLines(final List<String> lineList) {
        synchronized (this.logStream) {
            if (!this.firstMessage.compareAndSet(true, false)) {
                this.logStream.println();
                this.logStream.println();
            }
            for (final String s : lineList) {
                this.logStream.println(s);
            }
        }
    }
    
    private static Control[] getControlArray(final List<Control> controls) {
        if (controls == null || controls.isEmpty()) {
            return StaticUtils.NO_CONTROLS;
        }
        final Control[] controlArray = new Control[controls.size()];
        return controls.toArray(controlArray);
    }
}
