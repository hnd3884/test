package org.apache.commons.net.telnet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class TelnetClient extends Telnet
{
    private InputStream __input;
    private OutputStream __output;
    protected boolean readerThread;
    private TelnetInputListener inputListener;
    
    public TelnetClient() {
        super("VT100");
        this.readerThread = true;
        this.__input = null;
        this.__output = null;
    }
    
    public TelnetClient(final String termtype) {
        super(termtype);
        this.readerThread = true;
        this.__input = null;
        this.__output = null;
    }
    
    void _flushOutputStream() throws IOException {
        this._output_.flush();
    }
    
    void _closeOutputStream() throws IOException {
        try {
            this._output_.close();
        }
        finally {
            this._output_ = null;
        }
    }
    
    @Override
    protected void _connectAction_() throws IOException {
        super._connectAction_();
        final TelnetInputStream tmp = new TelnetInputStream(this._input_, this, this.readerThread);
        if (this.readerThread) {
            tmp._start();
        }
        this.__input = new BufferedInputStream(tmp);
        this.__output = new TelnetOutputStream(this);
    }
    
    @Override
    public void disconnect() throws IOException {
        try {
            if (this.__input != null) {
                this.__input.close();
            }
            if (this.__output != null) {
                this.__output.close();
            }
        }
        finally {
            this.__output = null;
            this.__input = null;
            super.disconnect();
        }
    }
    
    public OutputStream getOutputStream() {
        return this.__output;
    }
    
    public InputStream getInputStream() {
        return this.__input;
    }
    
    public boolean getLocalOptionState(final int option) {
        return this._stateIsWill(option) && this._requestedWill(option);
    }
    
    public boolean getRemoteOptionState(final int option) {
        return this._stateIsDo(option) && this._requestedDo(option);
    }
    
    public boolean sendAYT(final long timeout) throws IOException, IllegalArgumentException, InterruptedException {
        return this._sendAYT(timeout);
    }
    
    public void sendSubnegotiation(final int[] message) throws IOException, IllegalArgumentException {
        if (message.length < 1) {
            throw new IllegalArgumentException("zero length message");
        }
        this._sendSubnegotiation(message);
    }
    
    public void sendCommand(final byte command) throws IOException, IllegalArgumentException {
        this._sendCommand(command);
    }
    
    public void addOptionHandler(final TelnetOptionHandler opthand) throws InvalidTelnetOptionException, IOException {
        super.addOptionHandler(opthand);
    }
    
    public void deleteOptionHandler(final int optcode) throws InvalidTelnetOptionException, IOException {
        super.deleteOptionHandler(optcode);
    }
    
    public void registerSpyStream(final OutputStream spystream) {
        super._registerSpyStream(spystream);
    }
    
    public void stopSpyStream() {
        super._stopSpyStream();
    }
    
    @Override
    public void registerNotifHandler(final TelnetNotificationHandler notifhand) {
        super.registerNotifHandler(notifhand);
    }
    
    @Override
    public void unregisterNotifHandler() {
        super.unregisterNotifHandler();
    }
    
    public void setReaderThread(final boolean flag) {
        this.readerThread = flag;
    }
    
    public boolean getReaderThread() {
        return this.readerThread;
    }
    
    public synchronized void registerInputListener(final TelnetInputListener listener) {
        this.inputListener = listener;
    }
    
    public synchronized void unregisterInputListener() {
        this.inputListener = null;
    }
    
    void notifyInputListener() {
        final TelnetInputListener listener;
        synchronized (this) {
            listener = this.inputListener;
        }
        if (listener != null) {
            listener.telnetInputAvailable();
        }
    }
}
