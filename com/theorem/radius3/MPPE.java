package com.theorem.radius3;

public final class MPPE
{
    private boolean a;
    private boolean b;
    private byte[] c;
    private byte[] d;
    private byte[] e;
    private byte[] f;
    private byte[] g;
    
    protected MPPE() {
        this.a = false;
        this.b = false;
    }
    
    protected final void a(final AttributeList list, final boolean b, final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[4];
        if (list.getVendorSpecific(311).length == 0) {
            return;
        }
        final Attribute[] vendorSpecific = list.getVendorSpecific(311, 8);
        if (vendorSpecific.length == 0 && vendorSpecific[0].length != 4) {
            this.c[3] = 6;
        }
        else {
            this.c = vendorSpecific[0].getAttributeData();
        }
        final Attribute[] vendorSpecific2 = list.getVendorSpecific(311, 7);
        if (vendorSpecific2.length == 0 && vendorSpecific2[0].length != 4) {
            array3[3] = 1;
        }
        else {
            vendorSpecific2[0].getAttributeData();
        }
        final Attribute[] vendorSpecific3 = list.getVendorSpecific(311, 12);
        if (vendorSpecific3.length > 0) {
            byte[] e = vendorSpecific3[0].getAttributeData();
            if (e == null || e.length != 32) {
                return;
            }
            this.b = true;
            if (b) {
                e = RADIUSEncrypt.decrypt(e, array, array2);
            }
            this.e = e;
        }
        else {
            final Attribute[] vendorSpecific4 = list.getVendorSpecific(311, 16);
            final Attribute[] vendorSpecific5 = list.getVendorSpecific(311, 17);
            if (vendorSpecific4.length != 0 && vendorSpecific5.length != 0) {
                this.a = true;
                this.f = vendorSpecific4[0].getAttributeData();
                this.g = vendorSpecific5[0].getAttributeData();
                if (b) {
                    if (this.f == null || this.f.length == 34) {
                        this.f = RADIUSEncrypt.saltDecode(this.f, array, array2);
                    }
                    if (this.g == null || this.g.length == 34) {
                        this.g = RADIUSEncrypt.saltDecode(this.g, array, array2);
                    }
                }
            }
        }
    }
    
    public final boolean hasV1Keys() {
        return this.b;
    }
    
    public final boolean hasV2Keys() {
        return this.a;
    }
    
    public final byte[] getEncryptionPolicy() {
        return this.d;
    }
    
    public final byte[] getSendKey() {
        return this.f;
    }
    
    public final byte[] getReceiveKey() {
        return this.g;
    }
    
    public final byte[] getMPPEKeys() {
        return this.e;
    }
    
    public final byte[] getNTKey() {
        if (this.hasV1Keys()) {
            final byte[] array = new byte[16];
            System.arraycopy(this.e, 8, array, 0, 16);
            return array;
        }
        return null;
    }
}
