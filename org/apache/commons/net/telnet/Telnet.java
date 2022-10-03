package org.apache.commons.net.telnet;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.SocketClient;

class Telnet extends SocketClient
{
    static final boolean debug = false;
    static final boolean debugoptions = false;
    static final byte[] _COMMAND_DO;
    static final byte[] _COMMAND_DONT;
    static final byte[] _COMMAND_WILL;
    static final byte[] _COMMAND_WONT;
    static final byte[] _COMMAND_SB;
    static final byte[] _COMMAND_SE;
    static final int _WILL_MASK = 1;
    static final int _DO_MASK = 2;
    static final int _REQUESTED_WILL_MASK = 4;
    static final int _REQUESTED_DO_MASK = 8;
    static final int DEFAULT_PORT = 23;
    int[] _doResponse;
    int[] _willResponse;
    int[] _options;
    protected static final int TERMINAL_TYPE = 24;
    protected static final int TERMINAL_TYPE_SEND = 1;
    protected static final int TERMINAL_TYPE_IS = 0;
    static final byte[] _COMMAND_IS;
    private String terminalType;
    private final TelnetOptionHandler[] optionHandlers;
    static final byte[] _COMMAND_AYT;
    private final Object aytMonitor;
    private volatile boolean aytFlag;
    private volatile OutputStream spyStream;
    private TelnetNotificationHandler __notifhand;
    
    Telnet() {
        this.terminalType = null;
        this.aytMonitor = new Object();
        this.aytFlag = true;
        this.spyStream = null;
        this.__notifhand = null;
        this.setDefaultPort(23);
        this._doResponse = new int[256];
        this._willResponse = new int[256];
        this._options = new int[256];
        this.optionHandlers = new TelnetOptionHandler[256];
    }
    
    Telnet(final String termtype) {
        this.terminalType = null;
        this.aytMonitor = new Object();
        this.aytFlag = true;
        this.spyStream = null;
        this.__notifhand = null;
        this.setDefaultPort(23);
        this._doResponse = new int[256];
        this._willResponse = new int[256];
        this._options = new int[256];
        this.terminalType = termtype;
        this.optionHandlers = new TelnetOptionHandler[256];
    }
    
    boolean _stateIsWill(final int option) {
        return (this._options[option] & 0x1) != 0x0;
    }
    
    boolean _stateIsWont(final int option) {
        return !this._stateIsWill(option);
    }
    
    boolean _stateIsDo(final int option) {
        return (this._options[option] & 0x2) != 0x0;
    }
    
    boolean _stateIsDont(final int option) {
        return !this._stateIsDo(option);
    }
    
    boolean _requestedWill(final int option) {
        return (this._options[option] & 0x4) != 0x0;
    }
    
    boolean _requestedWont(final int option) {
        return !this._requestedWill(option);
    }
    
    boolean _requestedDo(final int option) {
        return (this._options[option] & 0x8) != 0x0;
    }
    
    boolean _requestedDont(final int option) {
        return !this._requestedDo(option);
    }
    
    void _setWill(final int option) throws IOException {
        final int[] options = this._options;
        options[option] |= 0x1;
        if (this._requestedWill(option) && this.optionHandlers[option] != null) {
            this.optionHandlers[option].setWill(true);
            final int[] subneg = this.optionHandlers[option].startSubnegotiationLocal();
            if (subneg != null) {
                this._sendSubnegotiation(subneg);
            }
        }
    }
    
    void _setDo(final int option) throws IOException {
        final int[] options = this._options;
        options[option] |= 0x2;
        if (this._requestedDo(option) && this.optionHandlers[option] != null) {
            this.optionHandlers[option].setDo(true);
            final int[] subneg = this.optionHandlers[option].startSubnegotiationRemote();
            if (subneg != null) {
                this._sendSubnegotiation(subneg);
            }
        }
    }
    
    void _setWantWill(final int option) {
        final int[] options = this._options;
        options[option] |= 0x4;
    }
    
    void _setWantDo(final int option) {
        final int[] options = this._options;
        options[option] |= 0x8;
    }
    
    void _setWont(final int option) {
        final int[] options = this._options;
        options[option] &= 0xFFFFFFFE;
        if (this.optionHandlers[option] != null) {
            this.optionHandlers[option].setWill(false);
        }
    }
    
    void _setDont(final int option) {
        final int[] options = this._options;
        options[option] &= 0xFFFFFFFD;
        if (this.optionHandlers[option] != null) {
            this.optionHandlers[option].setDo(false);
        }
    }
    
    void _setWantWont(final int option) {
        final int[] options = this._options;
        options[option] &= 0xFFFFFFFB;
    }
    
    void _setWantDont(final int option) {
        final int[] options = this._options;
        options[option] &= 0xFFFFFFF7;
    }
    
    void _processCommand(final int command) {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(5, command);
        }
    }
    
    void _processDo(final int option) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(1, option);
        }
        boolean acceptNewState = false;
        if (this.optionHandlers[option] != null) {
            acceptNewState = this.optionHandlers[option].getAcceptLocal();
        }
        else if (option == 24 && this.terminalType != null && this.terminalType.length() > 0) {
            acceptNewState = true;
        }
        if (this._willResponse[option] > 0) {
            final int[] willResponse = this._willResponse;
            --willResponse[option];
            if (this._willResponse[option] > 0 && this._stateIsWill(option)) {
                final int[] willResponse2 = this._willResponse;
                --willResponse2[option];
            }
        }
        if (this._willResponse[option] == 0) {
            if (this._requestedWont(option)) {
                if (acceptNewState) {
                    this._setWantWill(option);
                    this._sendWill(option);
                }
                else {
                    final int[] willResponse3 = this._willResponse;
                    ++willResponse3[option];
                    this._sendWont(option);
                }
            }
        }
        this._setWill(option);
    }
    
    void _processDont(final int option) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(2, option);
        }
        if (this._willResponse[option] > 0) {
            final int[] willResponse = this._willResponse;
            --willResponse[option];
            if (this._willResponse[option] > 0 && this._stateIsWont(option)) {
                final int[] willResponse2 = this._willResponse;
                --willResponse2[option];
            }
        }
        if (this._willResponse[option] == 0 && this._requestedWill(option)) {
            if (this._stateIsWill(option) || this._requestedWill(option)) {
                this._sendWont(option);
            }
            this._setWantWont(option);
        }
        this._setWont(option);
    }
    
    void _processWill(final int option) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(3, option);
        }
        boolean acceptNewState = false;
        if (this.optionHandlers[option] != null) {
            acceptNewState = this.optionHandlers[option].getAcceptRemote();
        }
        if (this._doResponse[option] > 0) {
            final int[] doResponse = this._doResponse;
            --doResponse[option];
            if (this._doResponse[option] > 0 && this._stateIsDo(option)) {
                final int[] doResponse2 = this._doResponse;
                --doResponse2[option];
            }
        }
        if (this._doResponse[option] == 0 && this._requestedDont(option)) {
            if (acceptNewState) {
                this._setWantDo(option);
                this._sendDo(option);
            }
            else {
                final int[] doResponse3 = this._doResponse;
                ++doResponse3[option];
                this._sendDont(option);
            }
        }
        this._setDo(option);
    }
    
    void _processWont(final int option) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(4, option);
        }
        if (this._doResponse[option] > 0) {
            final int[] doResponse = this._doResponse;
            --doResponse[option];
            if (this._doResponse[option] > 0 && this._stateIsDont(option)) {
                final int[] doResponse2 = this._doResponse;
                --doResponse2[option];
            }
        }
        if (this._doResponse[option] == 0 && this._requestedDo(option)) {
            if (this._stateIsDo(option) || this._requestedDo(option)) {
                this._sendDont(option);
            }
            this._setWantDont(option);
        }
        this._setDont(option);
    }
    
    void _processSuboption(final int[] suboption, final int suboptionLength) throws IOException {
        if (suboptionLength > 0) {
            if (this.optionHandlers[suboption[0]] != null) {
                final int[] responseSuboption = this.optionHandlers[suboption[0]].answerSubnegotiation(suboption, suboptionLength);
                this._sendSubnegotiation(responseSuboption);
            }
            else if (suboptionLength > 1 && suboption[0] == 24 && suboption[1] == 1) {
                this._sendTerminalType();
            }
        }
    }
    
    final synchronized void _sendTerminalType() throws IOException {
        if (this.terminalType != null) {
            this._output_.write(Telnet._COMMAND_SB);
            this._output_.write(Telnet._COMMAND_IS);
            this._output_.write(this.terminalType.getBytes(this.getCharset()));
            this._output_.write(Telnet._COMMAND_SE);
            this._output_.flush();
        }
    }
    
    final synchronized void _sendSubnegotiation(final int[] subn) throws IOException {
        if (subn != null) {
            this._output_.write(Telnet._COMMAND_SB);
            for (final int element : subn) {
                final byte b = (byte)element;
                if (b == -1) {
                    this._output_.write(b);
                }
                this._output_.write(b);
            }
            this._output_.write(Telnet._COMMAND_SE);
            this._output_.flush();
        }
    }
    
    final synchronized void _sendCommand(final byte cmd) throws IOException {
        this._output_.write(255);
        this._output_.write(cmd);
        this._output_.flush();
    }
    
    final synchronized void _processAYTResponse() {
        if (!this.aytFlag) {
            synchronized (this.aytMonitor) {
                this.aytFlag = true;
                this.aytMonitor.notifyAll();
            }
        }
    }
    
    @Override
    protected void _connectAction_() throws IOException {
        for (int ii = 0; ii < 256; ++ii) {
            this._doResponse[ii] = 0;
            this._willResponse[ii] = 0;
            this._options[ii] = 0;
            if (this.optionHandlers[ii] != null) {
                this.optionHandlers[ii].setDo(false);
                this.optionHandlers[ii].setWill(false);
            }
        }
        super._connectAction_();
        this._input_ = new BufferedInputStream(this._input_);
        this._output_ = new BufferedOutputStream(this._output_);
        for (int ii = 0; ii < 256; ++ii) {
            if (this.optionHandlers[ii] != null) {
                if (this.optionHandlers[ii].getInitLocal()) {
                    this._requestWill(this.optionHandlers[ii].getOptionCode());
                }
                if (this.optionHandlers[ii].getInitRemote()) {
                    this._requestDo(this.optionHandlers[ii].getOptionCode());
                }
            }
        }
    }
    
    final synchronized void _sendDo(final int option) throws IOException {
        this._output_.write(Telnet._COMMAND_DO);
        this._output_.write(option);
        this._output_.flush();
    }
    
    final synchronized void _requestDo(final int option) throws IOException {
        if ((this._doResponse[option] == 0 && this._stateIsDo(option)) || this._requestedDo(option)) {
            return;
        }
        this._setWantDo(option);
        final int[] doResponse = this._doResponse;
        ++doResponse[option];
        this._sendDo(option);
    }
    
    final synchronized void _sendDont(final int option) throws IOException {
        this._output_.write(Telnet._COMMAND_DONT);
        this._output_.write(option);
        this._output_.flush();
    }
    
    final synchronized void _requestDont(final int option) throws IOException {
        if ((this._doResponse[option] == 0 && this._stateIsDont(option)) || this._requestedDont(option)) {
            return;
        }
        this._setWantDont(option);
        final int[] doResponse = this._doResponse;
        ++doResponse[option];
        this._sendDont(option);
    }
    
    final synchronized void _sendWill(final int option) throws IOException {
        this._output_.write(Telnet._COMMAND_WILL);
        this._output_.write(option);
        this._output_.flush();
    }
    
    final synchronized void _requestWill(final int option) throws IOException {
        if ((this._willResponse[option] == 0 && this._stateIsWill(option)) || this._requestedWill(option)) {
            return;
        }
        this._setWantWill(option);
        final int[] doResponse = this._doResponse;
        ++doResponse[option];
        this._sendWill(option);
    }
    
    final synchronized void _sendWont(final int option) throws IOException {
        this._output_.write(Telnet._COMMAND_WONT);
        this._output_.write(option);
        this._output_.flush();
    }
    
    final synchronized void _requestWont(final int option) throws IOException {
        if ((this._willResponse[option] == 0 && this._stateIsWont(option)) || this._requestedWont(option)) {
            return;
        }
        this._setWantWont(option);
        final int[] doResponse = this._doResponse;
        ++doResponse[option];
        this._sendWont(option);
    }
    
    final synchronized void _sendByte(final int b) throws IOException {
        this._output_.write(b);
        this._spyWrite(b);
    }
    
    final boolean _sendAYT(final long timeout) throws IOException, IllegalArgumentException, InterruptedException {
        boolean retValue = false;
        synchronized (this.aytMonitor) {
            synchronized (this) {
                this.aytFlag = false;
                this._output_.write(Telnet._COMMAND_AYT);
                this._output_.flush();
            }
            this.aytMonitor.wait(timeout);
            if (!this.aytFlag) {
                retValue = false;
                this.aytFlag = true;
            }
            else {
                retValue = true;
            }
        }
        return retValue;
    }
    
    void addOptionHandler(final TelnetOptionHandler opthand) throws InvalidTelnetOptionException, IOException {
        final int optcode = opthand.getOptionCode();
        if (!TelnetOption.isValidOption(optcode)) {
            throw new InvalidTelnetOptionException("Invalid Option Code", optcode);
        }
        if (this.optionHandlers[optcode] == null) {
            this.optionHandlers[optcode] = opthand;
            if (this.isConnected()) {
                if (opthand.getInitLocal()) {
                    this._requestWill(optcode);
                }
                if (opthand.getInitRemote()) {
                    this._requestDo(optcode);
                }
            }
            return;
        }
        throw new InvalidTelnetOptionException("Already registered option", optcode);
    }
    
    void deleteOptionHandler(final int optcode) throws InvalidTelnetOptionException, IOException {
        if (!TelnetOption.isValidOption(optcode)) {
            throw new InvalidTelnetOptionException("Invalid Option Code", optcode);
        }
        if (this.optionHandlers[optcode] == null) {
            throw new InvalidTelnetOptionException("Unregistered option", optcode);
        }
        final TelnetOptionHandler opthand = this.optionHandlers[optcode];
        this.optionHandlers[optcode] = null;
        if (opthand.getWill()) {
            this._requestWont(optcode);
        }
        if (opthand.getDo()) {
            this._requestDont(optcode);
        }
    }
    
    void _registerSpyStream(final OutputStream spystream) {
        this.spyStream = spystream;
    }
    
    void _stopSpyStream() {
        this.spyStream = null;
    }
    
    void _spyRead(final int ch) {
        final OutputStream spy = this.spyStream;
        if (spy != null) {
            try {
                if (ch != 13) {
                    if (ch == 10) {
                        spy.write(13);
                    }
                    spy.write(ch);
                    spy.flush();
                }
            }
            catch (final IOException e) {
                this.spyStream = null;
            }
        }
    }
    
    void _spyWrite(final int ch) {
        if (!this._stateIsDo(1) || !this._requestedDo(1)) {
            final OutputStream spy = this.spyStream;
            if (spy != null) {
                try {
                    spy.write(ch);
                    spy.flush();
                }
                catch (final IOException e) {
                    this.spyStream = null;
                }
            }
        }
    }
    
    public void registerNotifHandler(final TelnetNotificationHandler notifhand) {
        this.__notifhand = notifhand;
    }
    
    public void unregisterNotifHandler() {
        this.__notifhand = null;
    }
    
    static {
        _COMMAND_DO = new byte[] { -1, -3 };
        _COMMAND_DONT = new byte[] { -1, -2 };
        _COMMAND_WILL = new byte[] { -1, -5 };
        _COMMAND_WONT = new byte[] { -1, -4 };
        _COMMAND_SB = new byte[] { -1, -6 };
        _COMMAND_SE = new byte[] { -1, -16 };
        _COMMAND_IS = new byte[] { 24, 0 };
        _COMMAND_AYT = new byte[] { -1, -10 };
    }
}
