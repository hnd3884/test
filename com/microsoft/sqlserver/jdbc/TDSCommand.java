package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import java.io.Serializable;

abstract class TDSCommand implements Serializable
{
    private static final long serialVersionUID = 5485075546328951857L;
    static final Logger logger;
    private final String logContext;
    private String traceID;
    private volatile TDSWriter tdsWriter;
    private volatile TDSReader tdsReader;
    private final Object interruptLock;
    private volatile boolean interruptsEnabled;
    private volatile boolean wasInterrupted;
    private volatile String interruptReason;
    private volatile boolean requestComplete;
    private volatile boolean attentionPending;
    private volatile boolean processedResponse;
    private volatile boolean readingResponse;
    private int queryTimeoutSeconds;
    private int cancelQueryTimeoutSeconds;
    private ScheduledFuture<?> timeout;
    protected ArrayList<byte[]> enclaveCEKs;
    private boolean interruptChecked;
    
    abstract boolean doExecute() throws SQLServerException;
    
    final String getLogContext() {
        return this.logContext;
    }
    
    @Override
    public final String toString() {
        if (this.traceID == null) {
            this.traceID = "TDSCommand@" + Integer.toHexString(this.hashCode()) + " (" + this.logContext + ")";
        }
        return this.traceID;
    }
    
    final void log(final Level level, final String message) {
        TDSCommand.logger.log(level, this.toString() + ": " + message);
    }
    
    protected TDSWriter getTDSWriter() {
        return this.tdsWriter;
    }
    
    protected boolean getInterruptsEnabled() {
        return this.interruptsEnabled;
    }
    
    protected void setInterruptsEnabled(final boolean interruptsEnabled) {
        synchronized (this.interruptLock) {
            this.interruptsEnabled = interruptsEnabled;
        }
    }
    
    private boolean wasInterrupted() {
        return this.wasInterrupted;
    }
    
    protected boolean getRequestComplete() {
        return this.requestComplete;
    }
    
    protected void setRequestComplete(final boolean requestComplete) {
        synchronized (this.interruptLock) {
            this.requestComplete = requestComplete;
        }
    }
    
    boolean attentionPending() {
        return this.attentionPending;
    }
    
    protected boolean getProcessedResponse() {
        return this.processedResponse;
    }
    
    protected void setProcessedResponse(final boolean processedResponse) {
        synchronized (this.interruptLock) {
            this.processedResponse = processedResponse;
        }
    }
    
    protected int getQueryTimeoutSeconds() {
        return this.queryTimeoutSeconds;
    }
    
    protected int getCancelQueryTimeoutSeconds() {
        return this.cancelQueryTimeoutSeconds;
    }
    
    final boolean readingResponse() {
        return this.readingResponse;
    }
    
    TDSCommand(final String logContext, final int queryTimeoutSeconds, final int cancelQueryTimeoutSeconds) {
        this.interruptLock = new Object();
        this.interruptsEnabled = false;
        this.wasInterrupted = false;
        this.interruptReason = null;
        this.attentionPending = false;
        this.interruptChecked = false;
        this.logContext = logContext;
        this.queryTimeoutSeconds = queryTimeoutSeconds;
        this.cancelQueryTimeoutSeconds = cancelQueryTimeoutSeconds;
    }
    
    boolean execute(final TDSWriter tdsWriter, final TDSReader tdsReader) throws SQLServerException {
        this.tdsWriter = tdsWriter;
        this.tdsReader = tdsReader;
        assert null != tdsReader;
        try {
            return this.doExecute();
        }
        catch (final SQLServerException e) {
            try {
                if (!this.requestComplete && !tdsReader.getConnection().isClosed()) {
                    this.interrupt(e.getMessage());
                    this.onRequestComplete();
                    this.close();
                }
            }
            catch (final SQLServerException interruptException) {
                if (TDSCommand.logger.isLoggable(Level.FINE)) {
                    TDSCommand.logger.fine(this.toString() + ": Ignoring error in sending attention: " + interruptException.getMessage());
                }
            }
            throw e;
        }
    }
    
    void processResponse(final TDSReader tdsReader) throws SQLServerException {
        if (TDSCommand.logger.isLoggable(Level.FINEST)) {
            TDSCommand.logger.finest(this.toString() + ": Processing response");
        }
        try {
            TDSParser.parse(tdsReader, this.getLogContext());
        }
        catch (final SQLServerException e) {
            if (2 != e.getDriverErrorCode()) {
                throw e;
            }
            if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                TDSCommand.logger.finest(this.toString() + ": Ignoring error from database: " + e.getMessage());
            }
        }
    }
    
    final void detach() throws SQLServerException {
        if (TDSCommand.logger.isLoggable(Level.FINEST)) {
            TDSCommand.logger.finest(this + ": detaching...");
        }
        while (this.tdsReader.readPacket()) {}
        assert !this.readingResponse;
    }
    
    final void close() {
        if (TDSCommand.logger.isLoggable(Level.FINEST)) {
            TDSCommand.logger.finest(this + ": closing...");
        }
        if (TDSCommand.logger.isLoggable(Level.FINEST)) {
            TDSCommand.logger.finest(this + ": processing response...");
        }
        while (!this.processedResponse) {
            try {
                this.processResponse(this.tdsReader);
            }
            catch (final SQLServerException e) {
                if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                    TDSCommand.logger.finest(this + ": close ignoring error processing response: " + e.getMessage());
                }
                if (!this.tdsReader.getConnection().isSessionUnAvailable()) {
                    continue;
                }
                this.processedResponse = true;
                this.attentionPending = false;
            }
        }
        if (this.attentionPending) {
            if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                TDSCommand.logger.finest(this + ": processing attention ack...");
            }
            try {
                TDSParser.parse(this.tdsReader, "attention ack");
            }
            catch (final SQLServerException e) {
                if (this.tdsReader.getConnection().isSessionUnAvailable()) {
                    if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                        TDSCommand.logger.finest(this + ": giving up on attention ack after connection closed by exception: " + e);
                    }
                    this.attentionPending = false;
                }
                else if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                    TDSCommand.logger.finest(this + ": ignored exception: " + e);
                }
            }
            if (this.attentionPending) {
                if (TDSCommand.logger.isLoggable(Level.SEVERE)) {
                    TDSCommand.logger.severe(this.toString() + ": expected attn ack missing or not processed; terminating connection...");
                }
                try {
                    this.tdsReader.throwInvalidTDS();
                }
                catch (final SQLServerException e) {
                    if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                        TDSCommand.logger.finest(this + ": ignored expected invalid TDS exception: " + e);
                    }
                    assert this.tdsReader.getConnection().isSessionUnAvailable();
                    this.attentionPending = false;
                }
            }
        }
        assert this.processedResponse && !this.attentionPending;
    }
    
    void interrupt(final String reason) throws SQLServerException {
        synchronized (this.interruptLock) {
            if (this.interruptsEnabled && !this.wasInterrupted()) {
                if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                    TDSCommand.logger.finest(this + ": Raising interrupt for reason:" + reason);
                }
                this.wasInterrupted = true;
                this.interruptReason = reason;
                if (this.requestComplete) {
                    this.attentionPending = this.tdsWriter.sendAttention();
                }
            }
        }
    }
    
    final void checkForInterrupt() throws SQLServerException {
        if (this.wasInterrupted() && !this.interruptChecked) {
            this.interruptChecked = true;
            if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                TDSCommand.logger.finest(this + ": throwing interrupt exception, reason: " + this.interruptReason);
            }
            throw new SQLServerException(this.interruptReason, SQLState.STATEMENT_CANCELED, DriverError.NOT_SET, null);
        }
    }
    
    final void onRequestComplete() throws SQLServerException {
        synchronized (this.interruptLock) {
            assert !this.requestComplete;
            if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                TDSCommand.logger.finest(this + ": request complete");
            }
            this.requestComplete = true;
            if (!this.interruptsEnabled) {
                assert !this.attentionPending;
                assert !this.processedResponse;
                assert !this.readingResponse;
                this.processedResponse = true;
            }
            else if (this.wasInterrupted()) {
                if (this.tdsWriter.isEOMSent()) {
                    this.attentionPending = this.tdsWriter.sendAttention();
                    this.readingResponse = this.attentionPending;
                }
                else {
                    assert !this.attentionPending;
                    this.readingResponse = this.tdsWriter.ignoreMessage();
                }
                this.processedResponse = !this.readingResponse;
            }
            else {
                assert !this.attentionPending;
                assert !this.processedResponse;
                this.readingResponse = true;
            }
        }
    }
    
    final void onResponseEOM() throws SQLServerException {
        boolean readAttentionAck = false;
        synchronized (this.interruptLock) {
            if (this.interruptsEnabled) {
                if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                    TDSCommand.logger.finest(this + ": disabling interrupts");
                }
                readAttentionAck = this.attentionPending;
                this.interruptsEnabled = false;
            }
        }
        if (readAttentionAck) {
            this.tdsReader.readPacket();
        }
        this.readingResponse = false;
    }
    
    final void onTokenEOF() {
        this.processedResponse = true;
    }
    
    final void onAttentionAck() {
        assert this.attentionPending;
        this.attentionPending = false;
    }
    
    final TDSWriter startRequest(final byte tdsMessageType) throws SQLServerException {
        if (TDSCommand.logger.isLoggable(Level.FINEST)) {
            TDSCommand.logger.finest(this + ": starting request...");
        }
        try {
            this.tdsWriter.startMessage(this, tdsMessageType);
        }
        catch (final SQLServerException e) {
            if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                TDSCommand.logger.finest(this + ": starting request: exception: " + e.getMessage());
            }
            throw e;
        }
        synchronized (this.interruptLock) {
            this.requestComplete = false;
            this.readingResponse = false;
            this.processedResponse = false;
            this.attentionPending = false;
            this.wasInterrupted = false;
            this.interruptReason = null;
            this.interruptsEnabled = true;
        }
        return this.tdsWriter;
    }
    
    final TDSReader startResponse() throws SQLServerException {
        return this.startResponse(false);
    }
    
    final TDSReader startResponse(final boolean isAdaptive) throws SQLServerException {
        if (TDSCommand.logger.isLoggable(Level.FINEST)) {
            TDSCommand.logger.finest(this + ": finishing request");
        }
        try {
            this.tdsWriter.endMessage();
        }
        catch (final SQLServerException e) {
            if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                TDSCommand.logger.finest(this + ": finishing request: endMessage threw exception: " + e.getMessage());
            }
            throw e;
        }
        if (this.queryTimeoutSeconds > 0) {
            final SQLServerConnection conn = (this.tdsReader != null) ? this.tdsReader.getConnection() : null;
            this.timeout = this.tdsWriter.getSharedTimer().schedule(new TDSTimeoutTask(this, conn), this.queryTimeoutSeconds);
        }
        if (TDSCommand.logger.isLoggable(Level.FINEST)) {
            TDSCommand.logger.finest(this.toString() + ": Reading response...");
        }
        try {
            if (isAdaptive) {
                this.tdsReader.readPacket();
            }
            else {
                while (this.tdsReader.readPacket()) {}
            }
        }
        catch (final SQLServerException e) {
            if (TDSCommand.logger.isLoggable(Level.FINEST)) {
                TDSCommand.logger.finest(this.toString() + ": Exception reading response: " + e.getMessage());
            }
            throw e;
        }
        finally {
            if (this.timeout != null) {
                this.timeout.cancel(false);
                this.timeout = null;
            }
        }
        return this.tdsReader;
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Command");
    }
}
