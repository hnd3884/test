package com.theorem.radius3.dictionary;

import java.util.Date;
import java.math.BigInteger;
import com.theorem.radius3.radutil.Util;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.theorem.radius3.radutil.ByteIterator;
import com.theorem.radius3.VendorSpecific;
import com.theorem.radius3.Attribute;
import java.util.StringTokenizer;
import com.theorem.radius3.AttributeList;

public final class ParseAttribute extends RADIUSDictionary
{
    private static final char[] a;
    private static final String b;
    private static final char[] c;
    private static final String d;
    
    public final void merge(final RADIUSDictionary radiusDictionary) {
        super.merge(radiusDictionary);
    }
    
    public final AttributeList toAttributes(final String s) {
        return this.toAttributes(new String[] { s });
    }
    
    public final AttributeList toAttributes(final String[] array) {
        final AttributeList list = new AttributeList();
        for (int i = 0; i < array.length; ++i) {
            list.mergeAttributes(this.b(array[i]));
        }
        return list;
    }
    
    public final void addAttribute(final String s, final int n, final int n2) {
        super.addAttribute(s, n, n2, 0);
    }
    
    private final AttributeList b(String c) throws IllegalArgumentException {
        final AttributeList list = new AttributeList();
        if (!c.startsWith("#") && !c.startsWith(";")) {
            c = this.c(c);
            final StringTokenizer stringTokenizer = new StringTokenizer(c, " \t");
            while (stringTokenizer.hasMoreTokens()) {
                list.addAttribute(this.convertToAttribute(new VP(stringTokenizer.nextToken())));
            }
            return list;
        }
        return list;
    }
    
    private final String c(String s) throws IllegalArgumentException {
        s = this.a(s, "\\\"", ParseAttribute.b);
        s = this.a(s, "\t", " ");
        boolean b = false;
        final StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\"') {
                b = !b;
            }
            if (b && char1 == ' ') {
                sb.setCharAt(i, '\u0002');
            }
        }
        if (b) {
            throw new IllegalArgumentException("Quotation mismatch.");
        }
        return sb.toString();
    }
    
    public final Attribute convertToAttribute(final VP vp) throws IllegalArgumentException {
        if (vp.c != null) {
            final int vendorId = this.getVendorId(vp.c);
            if (vendorId == -1) {
                throw new IllegalArgumentException("Vendor name [" + vp.c + "] does not exist.");
            }
            return this.convertToAttributeValue(vendorId, this.getTag(vendorId, vp.b), vp);
        }
        else {
            final int tag = this.getTag(vp.b);
            if (tag == 0) {
                throw new IllegalArgumentException("Attribute name [" + vp.b + "] does not exist.");
            }
            return this.convertToAttributeValue(tag, vp);
        }
    }
    
    public final Attribute convertToAttributeValue(final int n, final int n2, final VP vp) throws IllegalArgumentException {
        final VendorSpecific vendorSpecific = new VendorSpecific(n);
        vendorSpecific.addAttribute(this.convertToAttributeValue(n2, vp));
        return vendorSpecific.getAttribute();
    }
    
    public final Attribute convertToAttributeValue(final int n, final VP vp) throws IllegalArgumentException {
        final String a = vp.a;
        switch (a.charAt(0)) {
            case '#': {
                try {
                    return new Attribute(n, ByteIterator.decoct(Integer.parseInt(a.substring(1))));
                }
                catch (final NumberFormatException ex) {
                    throw new IllegalArgumentException("Attribute value is not a number: " + vp.a());
                }
            }
            case '@': {
                try {
                    return new Attribute(n, InetAddress.getByName(a.substring(1)).getAddress());
                }
                catch (final UnknownHostException ex2) {
                    throw new IllegalArgumentException("Attribute value is not an IP Address: " + vp.a());
                }
            }
            case '!': {
                final Date parse = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss").parse(a.substring(1), new ParsePosition(0));
                if (parse == null) {
                    throw new IllegalArgumentException("Attribute value is not a Time: " + vp.a());
                }
                return new Attribute(n, (int)(parse.getTime() / 1000L));
            }
            case '\"': {
                if (a.charAt(a.length() - 1) != '\"') {
                    throw new IllegalArgumentException("Missing trailing quotation mark: " + vp.a());
                }
                if (a.length() > 255) {
                    throw new IllegalArgumentException("Attribute string value is too long (max=253): " + vp.a());
                }
                return new Attribute(n, Util.toUTF8(d(a.substring(1, a.length() - 1))));
            }
            default: {
                Label_0496: {
                    if (!a.startsWith("0x")) {
                        if (!a.startsWith("0X")) {
                            break Label_0496;
                        }
                    }
                    try {
                        final byte[] byteArray = new BigInteger(a.substring(2), 16).toByteArray();
                        if (byteArray.length > 253) {
                            throw new IllegalArgumentException("Attribute binary value is too long (max=253): " + vp.a());
                        }
                        return new Attribute(n, byteArray);
                    }
                    catch (final NumberFormatException ex3) {
                        throw new IllegalArgumentException("Attribute value is not a hex string: " + vp.a());
                    }
                }
                final int valueName = Attribute.getValueName(n, a);
                if (valueName == 0) {
                    throw new IllegalArgumentException("Can't find integer representation value for : " + vp.a());
                }
                d(a);
                return new Attribute(n, ByteIterator.decoct(valueName));
            }
        }
    }
    
    private static String d(String replace) {
        replace = replace.replace(ParseAttribute.a[0], '\"').replace('\u0002', ' ');
        return replace;
    }
    
    private final String a(final String s, final String s2, final String s3) {
        if (s2.length() == 1 && s3.length() == 1) {
            return s.replace(s2.charAt(0), s3.charAt(0));
        }
        final int length = s2.length();
        int n = 0;
        final StringBuffer sb = new StringBuffer();
        int index;
        while ((index = s.indexOf(s2, n)) >= 0) {
            sb.append(s.substring(n, index));
            sb.append(s3);
            n = index + length;
        }
        if (n == 0) {
            return s;
        }
        sb.append(s.substring(n));
        return sb.toString();
    }
    
    static {
        a = new char[] { '\u0001' };
        b = new String(ParseAttribute.a);
        c = new char[] { '\u0002' };
        d = new String(ParseAttribute.c);
    }
    
    static class VP
    {
        String a;
        String b;
        String c;
        
        VP(final String s) throws IllegalArgumentException {
            this.a(s);
        }
        
        final void a(final String s) throws IllegalArgumentException {
            final int index = s.indexOf(61);
            if (index < 0) {
                throw new IllegalArgumentException("Error locating '=' in attribute [" + s + " ]");
            }
            this.c = null;
            this.b = s.substring(0, index);
            this.a = s.substring(index + 1);
            final int index2;
            if ((index2 = this.b.indexOf(46)) >= 0) {
                this.c = this.b.substring(0, index2);
                this.b = this.b.substring(index2 + 1);
            }
        }
        
        public final String toString() {
            String string = "VP: [";
            if (this.c != null) {
                string = string + this.c + ".";
            }
            return string + this.b + "] = [" + this.a + "]";
        }
        
        final String a() {
            return d(this.a);
        }
    }
}
