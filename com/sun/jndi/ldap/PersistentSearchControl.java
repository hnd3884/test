package com.sun.jndi.ldap;

import java.io.IOException;

public final class PersistentSearchControl extends BasicControl
{
    public static final String OID = "2.16.840.1.113730.3.4.3";
    public static final int ADD = 1;
    public static final int DELETE = 2;
    public static final int MODIFY = 4;
    public static final int RENAME = 8;
    public static final int ANY = 15;
    private int changeTypes;
    private boolean changesOnly;
    private boolean returnControls;
    private static final long serialVersionUID = 6335140491154854116L;
    
    public PersistentSearchControl() throws IOException {
        super("2.16.840.1.113730.3.4.3");
        this.changeTypes = 15;
        this.changesOnly = false;
        this.returnControls = true;
        super.value = this.setEncodedValue();
    }
    
    public PersistentSearchControl(final int changeTypes, final boolean changesOnly, final boolean returnControls, final boolean b) throws IOException {
        super("2.16.840.1.113730.3.4.3", b, null);
        this.changeTypes = 15;
        this.changesOnly = false;
        this.returnControls = true;
        this.changeTypes = changeTypes;
        this.changesOnly = changesOnly;
        this.returnControls = returnControls;
        super.value = this.setEncodedValue();
    }
    
    private byte[] setEncodedValue() throws IOException {
        final BerEncoder berEncoder = new BerEncoder(32);
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(this.changeTypes);
        berEncoder.encodeBoolean(this.changesOnly);
        berEncoder.encodeBoolean(this.returnControls);
        berEncoder.endSeq();
        return berEncoder.getTrimmedBuf();
    }
}
