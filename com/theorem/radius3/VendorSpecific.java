package com.theorem.radius3;

import java.util.Enumeration;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.radutil.ByteIterator;

public final class VendorSpecific extends AttributeList
{
    public static final boolean LONG_TAGS = true;
    public static final boolean SHORT_TAGS = false;
    private byte[] a;
    private int b;
    private byte[] c;
    private boolean d;
    private byte[] e;
    private boolean f;
    
    public VendorSpecific(final int b) {
        this.b = 0;
        this.d = false;
        this.f = false;
        this.a = ByteIterator.toBytes(b);
        this.b = b;
    }
    
    public VendorSpecific(final int b, final boolean f) {
        this.b = 0;
        this.d = false;
        this.f = false;
        this.a = ByteIterator.toBytes(b);
        this.b = b;
        this.f = f;
    }
    
    public VendorSpecific(final Attribute attribute) {
        this.b = 0;
        this.d = false;
        this.f = false;
        this.a(attribute.getAttributeData());
    }
    
    public VendorSpecific(final Attribute attribute, final boolean f) {
        this.b = 0;
        this.d = false;
        this.f = false;
        if (attribute == null) {
            this.c = new byte[0];
            return;
        }
        this.f = f;
        this.a(attribute.getAttributeData());
    }
    
    public VendorSpecific(final byte[] array) {
        this.b = 0;
        this.d = false;
        this.f = false;
        if (array != null && array.length != 0) {
            this.a(array);
            return;
        }
        this.c = new byte[0];
    }
    
    public VendorSpecific(final byte[] array, final boolean f) {
        this.b = 0;
        this.d = false;
        this.f = false;
        if (array == null || array.length == 0) {
            this.c = new byte[0];
            return;
        }
        this.f = f;
        this.a(array);
    }
    
    private final void a(byte[] e) {
        if (e == null) {
            e = new byte[0];
            return;
        }
        if (e.length < 4) {
            return;
        }
        this.b = ByteIterator.toInt(e);
        (this.a = new byte[4])[0] = e[0];
        this.a[1] = e[1];
        this.a[2] = e[2];
        this.a[3] = e[3];
        final byte[] c = new byte[e.length - 4];
        System.arraycopy(e, 4, c, 0, c.length);
        try {
            this.f = super.loadRadiusAttributes(c, 0, c.length);
            if (this.size() > 0) {
                this.d = true;
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            this.c = c;
            super.a = new Attribute[0];
        }
        this.e = e;
    }
    
    public final void setTagLength(final boolean f) {
        this.f = f;
    }
    
    public final VendorSpecific setLongTag(final boolean f) {
        this.f = f;
        return this;
    }
    
    public final VendorSpecific setLongTag() {
        this.f = true;
        return this;
    }
    
    public final boolean hasVendorAttributes() {
        return this.d;
    }
    
    public final Attribute setData(final byte[] c) {
        this.c = c;
        this.clearAttributes();
        return this.getAttribute();
    }
    
    public final byte[] getRawData() {
        return this.c;
    }
    
    public final Attribute getAttribute() {
        final byte[] array = (this.c != null) ? this.c : this.createRadiusAttributeBlock();
        final byte[] array2 = new byte[4 + array.length];
        array2[0] = this.a[0];
        array2[1] = this.a[1];
        array2[2] = this.a[2];
        array2[3] = this.a[3];
        System.arraycopy(array, 0, array2, 4, array.length);
        return new Attribute(26, array2);
    }
    
    public final int getVendorID() {
        return this.b;
    }
    
    public static void addVendor(final String s, final int n) {
        AttributeName.add(s, n);
    }
    
    public final byte[] createRadiusAttributeBlock() {
        if (this.f) {
            final Attribute[] attributes = this.getAttributes();
            final int length = attributes.length;
            for (int i = 0; i < attributes.length; ++i) {
                attributes[i].setLongTag();
            }
        }
        return super.createRadiusAttributeBlock();
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer();
        final String vendor = AttributeName.getVendor(this.b);
        sb.append("Vendor-Specific ID: ");
        if (vendor != null) {
            sb.append(vendor).append(" (").append(this.b).append(")");
        }
        else {
            sb.append(this.b);
        }
        sb.append(", VSA Count: ").append(this.size());
        if (this.f) {
            sb.append(" (Long tag fields)");
        }
        sb.append('\n');
        if (this.c != null) {
            sb.append("   Data: ").append(Util.toHexString(this.c)).append('\n');
        }
        try {
            final Enumeration elements = this.elements();
            while (elements.hasMoreElements()) {
                final Attribute attribute = elements.nextElement();
                attribute.setLongTag(this.f);
                sb.append("   ");
                sb.append(attribute.toString(this.b)).append('\n');
            }
        }
        catch (final Exception ex) {
            if (this.e != null) {
                sb.append("Error decoding Vendor-Specific sub-attributes. Attribute Data: ").append(Util.toHexString(this.e));
            }
            else {
                sb.append("Error decoding Vendor-Specific sub-attributes. Can't display raw data.");
            }
        }
        return sb.toString();
    }
}
