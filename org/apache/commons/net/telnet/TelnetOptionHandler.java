package org.apache.commons.net.telnet;

public abstract class TelnetOptionHandler
{
    private int optionCode;
    private boolean initialLocal;
    private boolean initialRemote;
    private boolean acceptLocal;
    private boolean acceptRemote;
    private boolean doFlag;
    private boolean willFlag;
    
    public TelnetOptionHandler(final int optcode, final boolean initlocal, final boolean initremote, final boolean acceptlocal, final boolean acceptremote) {
        this.optionCode = -1;
        this.initialLocal = false;
        this.initialRemote = false;
        this.acceptLocal = false;
        this.acceptRemote = false;
        this.doFlag = false;
        this.willFlag = false;
        this.optionCode = optcode;
        this.initialLocal = initlocal;
        this.initialRemote = initremote;
        this.acceptLocal = acceptlocal;
        this.acceptRemote = acceptremote;
    }
    
    public int getOptionCode() {
        return this.optionCode;
    }
    
    public boolean getAcceptLocal() {
        return this.acceptLocal;
    }
    
    public boolean getAcceptRemote() {
        return this.acceptRemote;
    }
    
    public void setAcceptLocal(final boolean accept) {
        this.acceptLocal = accept;
    }
    
    public void setAcceptRemote(final boolean accept) {
        this.acceptRemote = accept;
    }
    
    public boolean getInitLocal() {
        return this.initialLocal;
    }
    
    public boolean getInitRemote() {
        return this.initialRemote;
    }
    
    public void setInitLocal(final boolean init) {
        this.initialLocal = init;
    }
    
    public void setInitRemote(final boolean init) {
        this.initialRemote = init;
    }
    
    public int[] answerSubnegotiation(final int[] suboptionData, final int suboptionLength) {
        return null;
    }
    
    public int[] startSubnegotiationLocal() {
        return null;
    }
    
    public int[] startSubnegotiationRemote() {
        return null;
    }
    
    boolean getWill() {
        return this.willFlag;
    }
    
    void setWill(final boolean state) {
        this.willFlag = state;
    }
    
    boolean getDo() {
        return this.doFlag;
    }
    
    void setDo(final boolean state) {
        this.doFlag = state;
    }
}
