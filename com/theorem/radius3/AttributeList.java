package com.theorem.radius3;

import com.theorem.radius3.radutil.Util;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.net.UnknownHostException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import com.theorem.radius3.radutil.MD5Digest;
import java.util.Date;
import com.theorem.radius3.radutil.ByteIterator;
import java.net.InetAddress;
import com.theorem.radius3.radutil.RadRand;
import java.io.Serializable;

public class AttributeList implements Serializable
{
    protected Attribute[] a;
    protected int b;
    private transient RadRand c;
    private boolean d;
    
    public AttributeList(final Attribute[] a) {
        this.b = 0;
        this.d = false;
        if (a == null) {
            return;
        }
        this.a = a;
        this.b = a.length;
    }
    
    public AttributeList(final AttributeList list) {
        this.b = 0;
        this.d = false;
        this.a();
        this.mergeAttributes(list);
    }
    
    public AttributeList() {
        this.b = 0;
        this.d = false;
        this.a();
    }
    
    private final void a() {
        this.a = new Attribute[10];
        this.b = 0;
    }
    
    public final void addAttribute(final Attribute attribute) {
        if (attribute == null) {
            return;
        }
        if (this.b >= this.a.length) {
            final Attribute[] a = new Attribute[this.a.length + 10];
            System.arraycopy(this.a, 0, a, 0, this.b);
            this.a = a;
        }
        this.a[this.b++] = attribute;
    }
    
    public final void addAttribute(final int n, final byte[] array) {
        this.addAttribute(new Attribute(n, array));
    }
    
    public final void addAttribute(final int n, final int n2, final InetAddress inetAddress) {
        this.addAttribute(n, n2, inetAddress.getAddress());
    }
    
    public final void addAttribute(final int n, final int n2, final int n3) {
        this.addAttribute(n, n2, ByteIterator.toBytes(n3));
    }
    
    public final void addAttribute(final int n) {
        this.addAttribute(new Attribute(n));
    }
    
    public final void addAttribute(final int n, final int n2, final String s) {
        this.addAttribute(n, n2, toUTF8(s));
    }
    
    public final void addAttribute(final int n, final int n2, final byte[] array) {
        this.addAttribute(new Attribute(n, n2, array));
    }
    
    public final void addAttribute(final int n, final int n2) {
        this.addAttribute(n, ByteIterator.toBytes(n2));
    }
    
    public final void addAttribute(final int n, final String s) {
        this.addAttribute(n, toUTF8(s));
    }
    
    public final void addAttribute(final int n, final Date date) {
        this.addAttribute(n, (int)date.getTime() / 1000);
    }
    
    public final void addAttribute(final int n, final InetAddress inetAddress) {
        this.addAttribute(n, inetAddress.getAddress());
    }
    
    public final void addAttribute(final int n, final String s, final String s2) {
        this.addAttribute(n, toUTF8(s));
    }
    
    public final void addAttribute(final VendorSpecific vendorSpecific) {
        if (vendorSpecific == null) {
            return;
        }
        this.addAttribute(vendorSpecific.getAttribute());
    }
    
    public final boolean setAttribute(final int n, final byte[] array) {
        boolean b = false;
        for (int i = 0; i < this.b; ++i) {
            if (this.a[i].b == n) {
                b = true;
                this.a[i] = new Attribute(n, array);
                break;
            }
        }
        return b;
    }
    
    public final void clearAttributes() {
        this.a();
    }
    
    public final void createCHAPChallenge(final byte[] array) {
        final byte[] array2 = new byte[17];
        if (this.c == null) {
            this.c = new RadRand();
        }
        final byte[] array3 = new byte[16];
        this.c.nextBytes(array3);
        this.addAttribute(60, array3);
        array2[0] = this.c.nextByte();
        final MessageDigest value = MD5Digest.get();
        value.update(array2, 0, 1);
        value.update(array);
        value.update(array3);
        System.arraycopy(value.digest(), 0, array2, 1, 16);
        this.addAttribute(3, array2);
    }
    
    public final String[] getAllStringAttributes(final int n) {
        final int b = this.b;
        int n2 = 0;
        for (int i = 0; i < b; ++i) {
            if (this.a[i].b == n) {
                ++n2;
            }
        }
        if (n2 == 0) {
            return null;
        }
        final String[] array = new String[n2];
        int n3 = 0;
        for (int j = 0; j < b; ++j) {
            if (this.a[j].b == n) {
                array[n3++] = new String(this.a[j].getAttributeData());
            }
        }
        return array;
    }
    
    public final String getStringAttribute(final int n) {
        for (int b = this.b, i = 0; i < b; ++i) {
            if (this.a[i].b == n) {
                try {
                    return new String(this.a[i].d, "UTF8");
                }
                catch (final UnsupportedEncodingException ex) {
                    return new String(this.a[i].d);
                }
            }
        }
        return null;
    }
    
    public final byte[][] getAllBinaryAttributes(final int n) {
        final int b = this.b;
        int n2 = 0;
        for (int i = 0; i < b; ++i) {
            if (this.a[i].b == n) {
                ++n2;
            }
        }
        if (n2 == 0) {
            return null;
        }
        final byte[][] array = new byte[n2][];
        int n3 = 0;
        for (int j = 0; j < b; ++j) {
            if (this.a[j].b == n) {
                final int length = this.a[j].length;
                array[n3++] = this.a[j].d;
            }
        }
        return array;
    }
    
    public final byte[] getBinaryAttribute(final int n) {
        for (int i = 0; i < this.b; ++i) {
            if (this.a[i].b == n) {
                return this.a[i].d;
            }
        }
        return null;
    }
    
    public final boolean loadRadiusAttributes(final byte[] array, final int n, final int n2) throws ArrayIndexOutOfBoundsException {
        return this.loadRadiusAttributes(array, n, n2, false);
    }
    
    public final boolean loadRadiusAttributes(final byte[] array, final int n, final int n2, final boolean b) throws ArrayIndexOutOfBoundsException {
        if (this.b > 0) {
            this.clearAttributes();
        }
        if (n2 == 0) {
            return false;
        }
        int i = n;
        final int n3 = n + n2;
        while (i < n3) {
            final int n4 = array[i++] & 0xFF;
            final int n5 = (array[i++] & 0xFF) - 2;
            if (n5 == 0 && !b) {
                if (this.a(array, n, n2, b)) {
                    return true;
                }
                throw new ArrayIndexOutOfBoundsException("Attribute at position " + this.b + " of type " + Attribute.getName(n4) + " (" + n4 + ") has no data value (forbidden).- " + (n3 - i) + " octets not processed after error.");
            }
            else {
                if (n5 >= 0) {
                    try {
                        final byte[] array2 = new byte[n5];
                        System.arraycopy(array, i, array2, 0, n5);
                        i += n5;
                        this.addAttribute(n4, array2);
                        continue;
                    }
                    catch (final ArrayIndexOutOfBoundsException ex) {
                        if (this.a(array, n, n2, b)) {
                            return true;
                        }
                        throw new ArrayIndexOutOfBoundsException("Attribute at position " + this.b + " of type " + Attribute.getName(n4) + " (" + n4 + ") has an incorrect length of " + (n5 + 2) + " - " + (n3 - i) + " octets not processed after error.");
                    }
                    break;
                }
                if (this.a(array, n, n2, b)) {
                    return true;
                }
                throw new ArrayIndexOutOfBoundsException("Attribute at position " + this.b + " of type " + Attribute.getName(n4) + " (" + n4 + ") has an incorrect length of " + (n5 + 2) + " - " + (n3 - i) + " octets not processed after error.");
            }
        }
        return false;
    }
    
    private final boolean a(final byte[] array, final int n, final int n2, final boolean b) throws ArrayIndexOutOfBoundsException {
        int i = n;
        final int n3 = n + n2;
        if (this.b > 0) {
            this.clearAttributes();
        }
        while (i < n3) {
            final int n4 = (array[i] & 0xFF) << 8 | (array[i + 1] & 0xFF);
            i += 2;
            final int n5 = (array[i++] & 0xFF) - 3;
            if (n5 < 0) {
                return false;
            }
            if (n5 == 0 && !b) {
                return false;
            }
            final byte[] array2 = new byte[n5];
            System.arraycopy(array, i, array2, 0, n5);
            i += n5;
            this.addAttribute(n4, array2);
        }
        return true;
    }
    
    public byte[] createRadiusAttributeBlock() throws ArrayIndexOutOfBoundsException {
        int n = 0;
        for (int i = 0; i < this.b; ++i) {
            n += this.a[i].length;
        }
        final byte[] array = new byte[n];
        int n2 = 0;
        for (int j = 0; j < this.b; ++j) {
            final byte[] attribute = this.a[j].getAttribute();
            final int length = this.a[j].length;
            System.arraycopy(attribute, 0, array, n2, length);
            n2 += length;
        }
        return array;
    }
    
    public final int getSize() {
        int n = 0;
        for (int i = 0; i < this.b; ++i) {
            n += this.a[i].length;
        }
        return n;
    }
    
    public final AttributeList mergeAttributes(final AttributeList list, final AttributeList list2) {
        final AttributeList list3 = new AttributeList();
        if (list == null) {
            return list2;
        }
        if (list2 == null) {
            return list;
        }
        final int size = list.size();
        final int size2 = list2.size();
        final AttributeList list4 = new AttributeList();
        for (int i = 0; i < size; ++i) {
            list4.addAttribute(list.a[i]);
        }
        for (int j = 0; j < size2; ++j) {
            list4.addAttribute(list2.a[j]);
        }
        return list4;
    }
    
    public final void mergeAttributes(final AttributeList list) {
        if (list == null) {
            return;
        }
        final Attribute[] attributes = list.getAttributes();
        for (int i = 0; i < attributes.length; ++i) {
            this.addAttribute(attributes[i]);
        }
    }
    
    public final AttributeList mergeAttributes(final AttributeList list, final Attribute[] array) {
        final AttributeList list2 = new AttributeList();
        if (list == null) {
            return new AttributeList(array);
        }
        if (array == null) {
            return list;
        }
        final AttributeList list3 = new AttributeList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            list3.addAttribute(list.a[i]);
        }
        for (int length = array.length, j = 0; j < length; ++j) {
            list3.addAttribute(array[j]);
        }
        return list3;
    }
    
    public final void mergeAttributes(final Attribute[] array) {
        if (array == null) {
            return;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            this.addAttribute(array[i]);
        }
    }
    
    public final InetAddress getIPAttribute(final int n) {
        final Attribute[] attributeArray = this.getAttributeArray(n);
        if (attributeArray.length == 0) {
            return null;
        }
        return attributeArray[0].getIP();
    }
    
    public static final InetAddress parseIPv6Attribute(final byte[] array) {
        final InetAddress inetAddress = null;
        if (array.length != 16) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(Integer.toHexString((array[0] & 0xFF) << 8 | (array[1] & 0xFF))).append(':');
        sb.append(Integer.toHexString((array[2] & 0xFF) << 8 | (array[3] & 0xFF))).append(':');
        sb.append(Integer.toHexString((array[4] & 0xFF) << 8 | (array[5] & 0xFF))).append(':');
        sb.append(Integer.toHexString((array[6] & 0xFF) << 8 | (array[7] & 0xFF))).append(':');
        sb.append(Integer.toHexString((array[8] & 0xFF) << 8 | (array[9] & 0xFF))).append(':');
        sb.append(Integer.toHexString((array[10] & 0xFF) << 8 | (array[11] & 0xFF))).append(':');
        sb.append(array[12] & 0xFF).append('.');
        sb.append(array[13] & 0xFF).append('.');
        sb.append(array[14] & 0xFF).append('.');
        sb.append(array[15] & 0xFF);
        try {
            InetAddress.getByName(sb.toString());
        }
        catch (final UnknownHostException ex) {
            return null;
        }
        return inetAddress;
    }
    
    public static final InetAddress parseIPAttribute(final byte[] array) {
        if (array.length != 4) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(array[0] & 0xFF).append('.');
        sb.append(array[1] & 0xFF).append('.');
        sb.append(array[2] & 0xFF).append('.');
        sb.append(array[3] & 0xFF);
        try {
            return InetAddress.getByName(sb.toString());
        }
        catch (final UnknownHostException ex) {
            return null;
        }
    }
    
    public final int getShort(final int n) throws ArrayIndexOutOfBoundsException {
        final byte[] binaryAttribute = this.getBinaryAttribute(n);
        if (binaryAttribute == null) {
            throw new ArrayIndexOutOfBoundsException("No attributes of this type in list.");
        }
        return (binaryAttribute[0] & 0xFF) << 8 | (binaryAttribute[1] & 0xFF);
    }
    
    public final VendorSpecific[] getVendorSpecific(final int n) {
        final ArrayList list = new ArrayList();
        final Attribute[] attributeArray = this.getAttributeArray(26);
        for (int i = 0; i < attributeArray.length; ++i) {
            final VendorSpecific vendorSpecific = new VendorSpecific(attributeArray[i]);
            if (vendorSpecific.getVendorID() == n) {
                list.add(vendorSpecific);
            }
        }
        return list.toArray(new VendorSpecific[list.size()]);
    }
    
    public final Attribute[] getVendorSpecific(final int n, final int n2) {
        final VendorSpecific[] vendorSpecific = this.getVendorSpecific(n);
        final AttributeList list = new AttributeList();
        for (int length = vendorSpecific.length, i = 0; i < length; ++i) {
            list.mergeAttributes(vendorSpecific[i].getAttributeArray(n2));
        }
        return list.getAttributes();
    }
    
    public final int getInt(final int n) throws ArrayIndexOutOfBoundsException {
        final byte[] binaryAttribute = this.getBinaryAttribute(n);
        if (binaryAttribute == null) {
            throw new ArrayIndexOutOfBoundsException("No attributes of this type in list.");
        }
        return binaryAttribute[0] << 24 | (binaryAttribute[1] & 0xFF) << 16 | (binaryAttribute[2] & 0xFF) << 8 | (binaryAttribute[3] & 0xFF);
    }
    
    public final void delete(final int n) {
        this.a(n, false);
    }
    
    public final void deleteAll(final int n) {
        this.a(n, true);
    }
    
    private final void a(final int n, final boolean b) {
        int n2 = 0;
        boolean b2 = true;
        int i = 0;
        int n3 = 0;
        while (i < this.b) {
            if (b2 && this.a[i].b == n) {
                this.a[n3] = this.a[i];
                ++n2;
                b2 = b;
            }
            else {
                this.a[n3++] = this.a[i];
            }
            ++i;
        }
        this.b -= n2;
    }
    
    public final Attribute[] getAttributes() {
        final Attribute[] array = new Attribute[this.b];
        System.arraycopy(this.a, 0, array, 0, this.b);
        return array;
    }
    
    public final Attribute[] getAttributeArray(final int n) {
        final Attribute[] array = new Attribute[this.size(n)];
        for (int n2 = 0, n3 = 0; n3 < this.b && n2 < array.length; ++n3) {
            if (this.a[n3].b == n) {
                array[n2++] = this.a[n3];
            }
        }
        return array;
    }
    
    public final boolean exists(final int n) {
        return this.size(n) > 0;
    }
    
    public final int size(final int n) {
        int n2 = 0;
        for (int i = 0; i < this.b; ++i) {
            if (this.a[i].b == n) {
                ++n2;
            }
        }
        return n2;
    }
    
    public final int size() {
        return this.b;
    }
    
    public final int dataSize() {
        return this.createRadiusAttributeBlock().length;
    }
    
    public final int findPosition(final int n) {
        int n2 = 0;
        for (int i = 0; i < this.b; ++i) {
            if (this.a[i].b == n) {
                return n2;
            }
            n2 += this.a[i].length;
        }
        return -1;
    }
    
    public final Enumeration elements() {
        return new Enumeration() {
            int a = 0;
            
            public final boolean hasMoreElements() {
                return this.a < AttributeList.this.b;
            }
            
            public final Object nextElement() {
                if (!this.hasMoreElements()) {
                    throw new NoSuchElementException("No more attributes in list.");
                }
                return AttributeList.this.a[this.a++];
            }
            
            public final Attribute nextAttribute() {
                if (this.hasMoreElements()) {
                    throw new NoSuchElementException("No more attributes in list.");
                }
                return AttributeList.this.a[this.a++];
            }
        };
    }
    
    public final Attribute getAttributeAt(final int n) {
        if (n >= 0 && n < this.b) {
            return this.a[n];
        }
        return null;
    }
    
    public final void setAttributeAt(final int n, final Attribute attribute) {
        if (n >= 0 && n < this.b) {
            this.a[n] = attribute;
        }
    }
    
    public final AttributeList getAttributeList(final int n) {
        final AttributeList list = new AttributeList();
        for (int i = 0; i < this.b; ++i) {
            if (this.a[i].b == n) {
                list.addAttribute(this.a[i]);
            }
        }
        return list;
    }
    
    public final void encodeAll(final byte[] array, final byte[] array2) {
        for (int i = 0; i < this.b; ++i) {
            final Attribute attribute = this.a[i];
            this.a[i] = new Attribute(attribute.getTag(), RADIUSEncrypt.saltEncode(attribute.getAttributeData(), array, array2));
        }
    }
    
    public final void decodeAll(final byte[] array, final byte[] array2) {
        for (int i = 0; i < this.b; ++i) {
            final Attribute attribute = this.a[i];
            this.a[i] = new Attribute(attribute.getTag(), RADIUSEncrypt.saltDecode(attribute.getAttributeData(), array, array2));
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        final Enumeration elements = this.elements();
        while (elements.hasMoreElements()) {
            final Attribute attribute = elements.nextElement();
            switch (attribute.getTag()) {
                case 26: {
                    sb.append(new VendorSpecific(attribute));
                    continue;
                }
                case 79: {
                    sb.append(attribute).append('\n');
                    if (n != 0) {
                        continue;
                    }
                    n = 1;
                    continue;
                }
                default: {
                    sb.append(attribute).append('\n');
                    continue;
                }
            }
        }
        if (n != 0) {
            try {
                sb.append("EAP-Message Information:\n\t").append(new EAPPacket(this));
            }
            catch (final EAPException ex) {
                sb.append("Error displaying EAP-Message: " + ex.getMessage());
            }
        }
        return sb.toString();
    }
    
    public static final String toHexString(final byte[] array) {
        return Util.toHexString(array);
    }
    
    public static final byte[] toUTF8(final String s) {
        return Util.toUTF8(s);
    }
    
    public static String A_B_C_D(final String s) {
        final char[] array = new char[s.length()];
        s.getChars(0, s.length(), array, 0);
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            final char[] array2 = array;
            final int n2 = i;
            final int n3 = array[i] - '\u0001';
            final int n4 = n;
            n = (char)(n + 1);
            array2[n2] = (char)(n3 ^ n4);
        }
        return new String(array);
    }
}
