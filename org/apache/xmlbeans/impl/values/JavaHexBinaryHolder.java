package org.apache.xmlbeans.impl.values;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.xmlbeans.XmlHexBinary;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.common.QNameHelper;
import java.io.UnsupportedEncodingException;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.SchemaType;
import java.security.MessageDigest;

public abstract class JavaHexBinaryHolder extends XmlObjectBase
{
    protected byte[] _value;
    protected boolean _hashcached;
    protected int hashcode;
    protected static MessageDigest md5;
    
    public JavaHexBinaryHolder() {
        this._hashcached = false;
        this.hashcode = 0;
    }
    
    @Override
    public SchemaType schemaType() {
        return BuiltinSchemaTypeSystem.ST_HEX_BINARY;
    }
    
    @Override
    protected String compute_text(final NamespaceManager nsm) {
        return new String(HexBin.encode(this._value));
    }
    
    @Override
    protected void set_text(final String s) {
        this._hashcached = false;
        if (this._validateOnSet()) {
            this._value = validateLexical(s, this.schemaType(), JavaHexBinaryHolder._voorVc);
        }
        else {
            this._value = lex(s, JavaHexBinaryHolder._voorVc);
        }
    }
    
    @Override
    protected void set_nil() {
        this._hashcached = false;
        this._value = null;
    }
    
    public static byte[] lex(final String v, final ValidationContext context) {
        byte[] vBytes = null;
        try {
            vBytes = v.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {}
        final byte[] bytes = HexBin.decode(vBytes);
        if (bytes == null) {
            context.invalid("hexBinary", new Object[] { "not encoded properly" });
        }
        return bytes;
    }
    
    public static byte[] validateLexical(final String v, final SchemaType sType, final ValidationContext context) {
        final byte[] bytes = lex(v, context);
        if (bytes == null) {
            return null;
        }
        if (!sType.matchPatternFacet(v)) {
            context.invalid("Hex encoded data does not match pattern for " + QNameHelper.readable(sType));
            return null;
        }
        return bytes;
    }
    
    @Override
    public byte[] getByteArrayValue() {
        this.check_dated();
        if (this._value == null) {
            return null;
        }
        final byte[] result = new byte[this._value.length];
        System.arraycopy(this._value, 0, result, 0, this._value.length);
        return result;
    }
    
    @Override
    protected void set_ByteArray(final byte[] ba) {
        this._hashcached = false;
        System.arraycopy(ba, 0, this._value = new byte[ba.length], 0, ba.length);
    }
    
    @Override
    protected boolean equal_to(final XmlObject i) {
        final byte[] ival = ((XmlHexBinary)i).getByteArrayValue();
        return Arrays.equals(this._value, ival);
    }
    
    @Override
    protected int value_hash_code() {
        if (this._hashcached) {
            return this.hashcode;
        }
        this._hashcached = true;
        if (this._value == null) {
            return this.hashcode = 0;
        }
        final byte[] res = JavaHexBinaryHolder.md5.digest(this._value);
        return this.hashcode = res[0] << 24 + res[1] << 16 + res[2] << 8 + res[3];
    }
    
    static {
        try {
            JavaHexBinaryHolder.md5 = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot find MD5 hash Algorithm");
        }
    }
}
