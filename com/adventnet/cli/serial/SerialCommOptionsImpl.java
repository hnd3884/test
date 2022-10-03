package com.adventnet.cli.serial;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.cli.transport.CLIProtocolOptions;

public class SerialCommOptionsImpl implements CLIProtocolOptions
{
    public static final int DATABITS_5 = 5;
    public static final int DATABITS_6 = 6;
    public static final int DATABITS_7 = 7;
    public static final int DATABITS_8 = 8;
    public static final int STOPBITS_1 = 1;
    public static final int STOPBITS_2 = 2;
    public static final int STOPBITS_1_5 = 3;
    public static final int FLOWCONTROL_NONE = 0;
    public static final int FLOWCONTROL_RTSCTS_IN = 1;
    public static final int FLOWCONTROL_RTSCTS_OUT = 2;
    public static final int FLOWCONTROL_XONXOFF_IN = 4;
    public static final int FLOWCONTROL_XONXOFF_OUT = 8;
    public static final int PARITY_NONE = 0;
    public static final int PARITY_ODD = 1;
    public static final int PARITY_EVEN = 2;
    public static final int PARITY_MARK = 3;
    public static final int PARITY_SPACE = 4;
    String portId;
    int baudRate;
    int dataBits;
    int stopBits;
    int parity;
    int flowcontrol;
    String initialLog;
    
    public SerialCommOptionsImpl() {
        this.portId = null;
        this.baudRate = 9600;
        this.dataBits = 8;
        this.stopBits = 1;
        this.parity = 0;
        this.flowcontrol = 0;
        this.initialLog = null;
    }
    
    public void setPortId(final String portId) {
        this.portId = portId;
    }
    
    public String getPortId() {
        return this.portId;
    }
    
    public void setBaudRate(final int baudRate) {
        this.baudRate = baudRate;
    }
    
    public int getBaudRate() {
        return this.baudRate;
    }
    
    public void setStopBits(final int stopBits) {
        if (stopBits == 1 || stopBits == 2) {
            this.stopBits = stopBits;
        }
    }
    
    public int getStopBits() {
        return this.stopBits;
    }
    
    public void setFlowControlMode(final int flowcontrol) {
        this.flowcontrol = flowcontrol;
    }
    
    public int getFlowControlMode() {
        return this.flowcontrol;
    }
    
    public void setParity(final int parity) {
        this.parity = parity;
    }
    
    public int getParity() {
        return this.parity;
    }
    
    public void setDataBits(final int dataBits) {
        this.dataBits = dataBits;
    }
    
    public int getDataBits() {
        return this.dataBits;
    }
    
    public void setSerialCommParameters(final int baudRate, final int dataBits, final int stopBits, final int parity) {
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }
    
    public Object getID() {
        return this.portId;
    }
    
    public void setInitialMessage(final String initialLog) {
        this.initialLog = initialLog;
    }
    
    public String getInitialMessage() {
        return this.initialLog;
    }
    
    public Object clone() {
        final SerialCommOptionsImpl serialCommOptionsImpl = new SerialCommOptionsImpl();
        serialCommOptionsImpl.setSerialCommParameters(this.getBaudRate(), this.getDataBits(), this.getStopBits(), this.getParity());
        serialCommOptionsImpl.setPortId(this.getPortId());
        serialCommOptionsImpl.setFlowControlMode(this.getFlowControlMode());
        return serialCommOptionsImpl;
    }
    
    public void setProperties(final Properties properties) {
        if (properties.get("PortId") != null && ((Hashtable<K, Object>)properties).get("PortId").toString().length() > 0) {
            this.setPortId(((Hashtable<K, String>)properties).get("PortId"));
        }
        if (properties.get("BaudRate") != null && ((Hashtable<K, Object>)properties).get("BaudRate").toString().length() > 0) {
            this.setBaudRate(new Integer(((Hashtable<K, Object>)properties).get("BaudRate").toString()));
        }
        if (properties.get("StopBits") != null && ((Hashtable<K, Object>)properties).get("StopBits").toString().length() > 0) {
            this.setStopBits(new Integer(((Hashtable<K, Object>)properties).get("StopBits").toString()));
        }
        if (properties.get("FlowControlMode") != null && ((Hashtable<K, Object>)properties).get("FlowControlMode").toString().length() > 0) {
            this.setFlowControlMode(new Integer(((Hashtable<K, Object>)properties).get("FlowControlMode").toString()));
        }
        if (properties.get("Parity") != null && ((Hashtable<K, Object>)properties).get("Parity").toString().length() > 0) {
            this.setParity(new Integer(((Hashtable<K, Object>)properties).get("Parity").toString()));
        }
        if (properties.get("DataBits") != null && ((Hashtable<K, Object>)properties).get("DataBits").toString().length() > 0) {
            this.setDataBits(new Integer(((Hashtable<K, Object>)properties).get("DataBits").toString()));
        }
    }
    
    public Properties getProperties() {
        final Properties properties = new Properties();
        if (this.getPortId() != null) {
            properties.setProperty("PortId", this.getPortId());
        }
        properties.setProperty("BaudRate", new Integer(this.getBaudRate()).toString());
        properties.setProperty("StopBits", new Integer(this.getStopBits()).toString());
        properties.setProperty("FlowControlMode", new Integer(this.getFlowControlMode()).toString());
        properties.setProperty("Parity", new Integer(this.getParity()).toString());
        properties.setProperty("DataBits", new Integer(this.getDataBits()).toString());
        return properties;
    }
}
