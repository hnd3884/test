package com.adventnet.tools.prevalent;

public final class NRearward
{
    private boolean rearward;
    private String[] props;
    private String userName;
    private String yek;
    private int product;
    
    private NRearward() {
        this.rearward = false;
        this.props = null;
        this.userName = null;
        this.yek = null;
        this.product = -1;
    }
    
    public NRearward(final String user, final String yek, final int prod) {
        this.rearward = false;
        this.props = null;
        this.userName = null;
        this.yek = null;
        this.product = -1;
        this.userName = user;
        this.yek = yek;
        this.product = prod;
    }
    
    public void onceMore() {
        this.compareTheValues(new String(this.decode()));
    }
    
    private void compareTheValues(final String valueString) {
        (this.props = new String[5])[0] = valueString.substring(8, 15);
        this.props[1] = valueString.substring(0, 4);
        this.props[2] = valueString.substring(6, 8);
        if (valueString.substring(4, 5).equals("1")) {
            this.props[3] = "0";
        }
        else if (valueString.substring(4, 5).equals("0")) {
            this.props[3] = "1";
        }
        else {
            this.props[3] = valueString.substring(4, 5);
        }
        this.props[4] = valueString.substring(5, 6);
        if (this.props[0].equals(this.userName)) {
            this.rearward = true;
        }
        else {
            this.props = null;
        }
    }
    
    private void compareThis(final String valueString) {
        (this.props = new String[5])[0] = valueString.substring(1, 9);
        this.props[1] = valueString.substring(9, 12);
        this.props[2] = "none";
        this.props[3] = valueString.substring(12, 13);
        this.props[4] = valueString.substring(13, 14);
        if (this.props[0].equals(this.processUserName(this.userName))) {
            this.rearward = true;
        }
        else {
            this.props = null;
        }
    }
    
    private void compareLatest(final String valueString) {
        (this.props = new String[5])[0] = valueString.substring(1, 8);
        this.props[1] = valueString.substring(8, 10);
        this.props[2] = valueString.substring(12, 14);
        this.props[3] = valueString.substring(10, 11);
        this.props[4] = valueString.substring(11, 12);
        int prod = 0;
        try {
            prod = Integer.parseInt(this.props[2]);
        }
        catch (final NumberFormatException nfex) {
            prod = -2;
        }
        if (this.props[0].equals(this.processLatestUserName(this.userName)) && prod == this.product) {
            this.rearward = true;
        }
        else {
            this.props = null;
        }
    }
    
    public String[] getPropValues() {
        return this.props;
    }
    
    private char[] decode() {
        final DString k = new DString();
        final String keyString = DString.decode(this.yek);
        final char[] decodedString = new char[15];
        try {
            for (int i = 0; i < 15; i += 2) {
                decodedString[i] = (char)(~(~keyString.charAt(i)) - 5);
            }
            for (int t_count = 13, j = 1; t_count > 0; t_count -= 2, j += 2) {
                decodedString[t_count] = (char)(~(~keyString.charAt(j)) - 5);
            }
        }
        catch (final Exception ex) {}
        return decodedString;
    }
    
    private boolean validateLatestKey() {
        final String lyek = this.yek;
        this.compareLatest(new String(this.decode()));
        if (this.isValidationOk()) {
            Clientele.getInstance().getRegisterState(this.props[0], this.props[1], this.yek);
            return true;
        }
        return false;
    }
    
    private boolean validateKey() {
        final String lyek = this.yek;
        this.compareTheValues(new String(this.decode()));
        if (this.isValidationOk()) {
            Clientele.getInstance().getRegisterState(this.props[0], this.props[1], this.yek);
            return true;
        }
        return false;
    }
    
    private boolean validateAnotherKey() {
        final String lyek = this.yek;
        this.compareThis(new String(this.decode()));
        if (this.isValidationOk()) {
            Clientele.getInstance().getRegisterState(this.props[0], this.props[1], this.yek);
            return true;
        }
        return false;
    }
    
    public boolean isValidationOk() {
        return this.rearward;
    }
    
    public void getRegistered() {
        Label_0047: {
            if (this.userName != null) {
                if (this.yek != null) {
                    break Label_0047;
                }
            }
            try {
                final Indication in = Indication.getInstance();
                in.deSerialize();
                this.userName = in.getTheUserName();
                this.yek = in.getTheKey();
            }
            catch (final Exception ex) {
                this.rearward = false;
            }
        }
        if (this.validateKey()) {
            this.rearward = true;
            return;
        }
        if (this.validateAnotherKey()) {
            this.rearward = true;
            return;
        }
        if (this.validateLatestKey()) {
            this.rearward = true;
            return;
        }
        this.props = new String[5];
        for (int i = 0; i < 5; ++i) {
            this.props[i] = "0";
        }
    }
    
    private String processUserName(final String userName) {
        final StringBuffer stringbuffer = new StringBuffer();
        final String s = userName;
        final int i = s.length();
        if (i >= 16) {
            for (int j = 0; j < 16; j += 2) {
                stringbuffer.append(s.charAt(j));
            }
        }
        else if (i >= 8) {
            for (int k = 0; k < 8; ++k) {
                stringbuffer.append(s.charAt(k));
            }
        }
        else {
            stringbuffer.append(s);
            for (int l = 8 - i, i2 = 1; i2 <= l; ++i2) {
                stringbuffer.append("@");
            }
        }
        return stringbuffer.toString();
    }
    
    private String processLatestUserName(final String userName) {
        final StringBuffer stringbuffer = new StringBuffer();
        final String s = userName;
        final int l = s.length();
        if (l >= 14) {
            for (int i = 0; i < 14; i += 2) {
                stringbuffer.append(s.charAt(i));
            }
        }
        else if (l >= 7) {
            for (int i = 0; i < 7; ++i) {
                stringbuffer.append(s.charAt(i));
            }
        }
        else {
            stringbuffer.append(s);
            for (int padlength = 7 - l, j = 1; j <= padlength; ++j) {
                stringbuffer.append("@");
            }
        }
        return stringbuffer.toString();
    }
}
