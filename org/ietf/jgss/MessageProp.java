package org.ietf.jgss;

public class MessageProp
{
    private boolean privacyState;
    private int qop;
    private boolean dupToken;
    private boolean oldToken;
    private boolean unseqToken;
    private boolean gapToken;
    private int minorStatus;
    private String minorString;
    
    public MessageProp(final boolean b) {
        this(0, b);
    }
    
    public MessageProp(final int qop, final boolean privacyState) {
        this.qop = qop;
        this.privacyState = privacyState;
        this.resetStatusValues();
    }
    
    public int getQOP() {
        return this.qop;
    }
    
    public boolean getPrivacy() {
        return this.privacyState;
    }
    
    public void setQOP(final int qop) {
        this.qop = qop;
    }
    
    public void setPrivacy(final boolean privacyState) {
        this.privacyState = privacyState;
    }
    
    public boolean isDuplicateToken() {
        return this.dupToken;
    }
    
    public boolean isOldToken() {
        return this.oldToken;
    }
    
    public boolean isUnseqToken() {
        return this.unseqToken;
    }
    
    public boolean isGapToken() {
        return this.gapToken;
    }
    
    public int getMinorStatus() {
        return this.minorStatus;
    }
    
    public String getMinorString() {
        return this.minorString;
    }
    
    public void setSupplementaryStates(final boolean dupToken, final boolean oldToken, final boolean unseqToken, final boolean gapToken, final int minorStatus, final String minorString) {
        this.dupToken = dupToken;
        this.oldToken = oldToken;
        this.unseqToken = unseqToken;
        this.gapToken = gapToken;
        this.minorStatus = minorStatus;
        this.minorString = minorString;
    }
    
    private void resetStatusValues() {
        this.dupToken = false;
        this.oldToken = false;
        this.unseqToken = false;
        this.gapToken = false;
        this.minorStatus = 0;
        this.minorString = null;
    }
}
