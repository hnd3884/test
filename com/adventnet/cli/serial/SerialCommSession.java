package com.adventnet.cli.serial;

import java.io.IOException;
import javax.comm.SerialPortEvent;
import javax.comm.UnsupportedCommOperationException;
import java.util.Enumeration;
import com.adventnet.cli.util.CLILogMgr;
import javax.comm.CommPortIdentifier;
import java.io.OutputStream;
import java.io.InputStream;
import javax.comm.SerialPort;
import javax.comm.SerialPortEventListener;

public class SerialCommSession implements SerialPortEventListener
{
    SerialPort serialPort;
    InputStream portIs;
    OutputStream portOs;
    int readTimeout;
    String portId;
    int flowcontrol;
    
    public SerialCommSession() {
        this.serialPort = null;
        this.portIs = null;
        this.portOs = null;
        this.readTimeout = 2000;
        this.portId = null;
        this.flowcontrol = 0;
    }
    
    public SerialCommSession(final String portId) {
        this.serialPort = null;
        this.portIs = null;
        this.portOs = null;
        this.readTimeout = 2000;
        this.portId = null;
        this.flowcontrol = 0;
        this.portId = portId;
    }
    
    public void open() throws Exception {
        this.open(this.portId);
    }
    
    public void open(final String portId) throws Exception {
        this.portId = portId;
        final Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier commPortIdentifier = null;
        while (portIdentifiers.hasMoreElements()) {
            final CommPortIdentifier commPortIdentifier2 = portIdentifiers.nextElement();
            if (commPortIdentifier2.getPortType() == 1 && commPortIdentifier2.getName().equals(portId)) {
                commPortIdentifier = commPortIdentifier2;
                break;
            }
        }
        if (commPortIdentifier == null) {
            throw new Exception("Port not Found");
        }
        this.serialPort = (SerialPort)commPortIdentifier.open("SerialCommSession", 2000);
        this.portIs = this.serialPort.getInputStream();
        this.portOs = this.serialPort.getOutputStream();
        this.serialPort.addEventListener((SerialPortEventListener)this);
        this.serialPort.notifyOnDataAvailable(true);
        CLILogMgr.setDebugMessage("CLIUSER", "SerialCommSession: session opened successfully", 4, null);
    }
    
    public void setSerialCommParameters(final int n, final int n2, final int n3, final int n4) throws UnsupportedCommOperationException {
        this.serialPort.setSerialPortParams(n, n2, n3, n4);
    }
    
    public void setFlowControlMode(final int flowcontrol) {
        this.flowcontrol = flowcontrol;
    }
    
    public int getFlowControlMode() {
        return this.flowcontrol;
    }
    
    public void serialEvent(final SerialPortEvent serialPortEvent) {
        switch (serialPortEvent.getEventType()) {
            case 1: {
                this.notifyDataAvailable();
                break;
            }
        }
    }
    
    synchronized void notifyDataAvailable() {
        try {
            this.notifyAll();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void write(final byte[] array) throws IOException {
        CLILogMgr.setDebugMessage("CLIUSER", "SerialCommSession: sending data " + array.length, 4, null);
        this.portOs.write(array);
    }
    
    public byte[] read() throws IOException {
        return this.readBytes();
    }
    
    public void close() throws Exception {
        if (this.serialPort != null) {
            this.serialPort.removeEventListener();
        }
        if (this.portIs != null) {
            this.portIs.close();
            this.portIs = null;
        }
        if (this.portOs != null) {
            this.portOs.close();
            this.portOs = null;
        }
        this.serialPort.close();
        this.serialPort = null;
        CLILogMgr.setDebugMessage("CLIUSER", "SerialCommSession: closing session", 4, null);
    }
    
    synchronized byte[] readBytes() throws IOException {
        boolean b = false;
        final StringBuffer sb = new StringBuffer();
        final byte[] array = new byte[1024];
        byte[] bytes = null;
        try {
            this.wait(this.readTimeout);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        while (this.portIs.available() > 0) {
            sb.append(new String(array, 0, this.portIs.read(array)));
        }
        if (sb.length() > 0) {
            bytes = sb.toString().getBytes();
            b = true;
        }
        if (b) {
            CLILogMgr.setDebugMessage("CLIUSER", "SerialCommSession: received data " + bytes.length, 4, null);
            return bytes;
        }
        return null;
    }
    
    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public int getReadTimeout() {
        return this.readTimeout;
    }
    
    public void setPortId(final String portId) {
        this.portId = portId;
    }
    
    public String getPortId() {
        return this.portId;
    }
}
