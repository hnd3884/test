package java.awt.datatransfer;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Locale;
import java.io.Externalizable;

class MimeType implements Externalizable, Cloneable
{
    static final long serialVersionUID = -6568722458793895906L;
    private String primaryType;
    private String subType;
    private MimeTypeParameterList parameters;
    private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";
    
    public MimeType() {
    }
    
    public MimeType(final String s) throws MimeTypeParseException {
        this.parse(s);
    }
    
    public MimeType(final String s, final String s2) throws MimeTypeParseException {
        this(s, s2, new MimeTypeParameterList());
    }
    
    public MimeType(final String s, final String s2, final MimeTypeParameterList list) throws MimeTypeParseException {
        if (!this.isValidToken(s)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        this.primaryType = s.toLowerCase(Locale.ENGLISH);
        if (this.isValidToken(s2)) {
            this.subType = s2.toLowerCase(Locale.ENGLISH);
            this.parameters = (MimeTypeParameterList)list.clone();
            return;
        }
        throw new MimeTypeParseException("Sub type is invalid.");
    }
    
    @Override
    public int hashCode() {
        return 0 + this.primaryType.hashCode() + this.subType.hashCode() + this.parameters.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MimeType)) {
            return false;
        }
        final MimeType mimeType = (MimeType)o;
        return this.primaryType.equals(mimeType.primaryType) && this.subType.equals(mimeType.subType) && this.parameters.equals(mimeType.parameters);
    }
    
    private void parse(final String s) throws MimeTypeParseException {
        final int index = s.indexOf(47);
        final int index2 = s.indexOf(59);
        if (index < 0 && index2 < 0) {
            throw new MimeTypeParseException("Unable to find a sub type.");
        }
        if (index < 0 && index2 >= 0) {
            throw new MimeTypeParseException("Unable to find a sub type.");
        }
        if (index >= 0 && index2 < 0) {
            this.primaryType = s.substring(0, index).trim().toLowerCase(Locale.ENGLISH);
            this.subType = s.substring(index + 1).trim().toLowerCase(Locale.ENGLISH);
            this.parameters = new MimeTypeParameterList();
        }
        else {
            if (index >= index2) {
                throw new MimeTypeParseException("Unable to find a sub type.");
            }
            this.primaryType = s.substring(0, index).trim().toLowerCase(Locale.ENGLISH);
            this.subType = s.substring(index + 1, index2).trim().toLowerCase(Locale.ENGLISH);
            this.parameters = new MimeTypeParameterList(s.substring(index2));
        }
        if (!this.isValidToken(this.primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        if (!this.isValidToken(this.subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }
    
    public String getPrimaryType() {
        return this.primaryType;
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    public MimeTypeParameterList getParameters() {
        return (MimeTypeParameterList)this.parameters.clone();
    }
    
    public String getParameter(final String s) {
        return this.parameters.get(s);
    }
    
    public void setParameter(final String s, final String s2) {
        this.parameters.set(s, s2);
    }
    
    public void removeParameter(final String s) {
        this.parameters.remove(s);
    }
    
    @Override
    public String toString() {
        return this.getBaseType() + this.parameters.toString();
    }
    
    public String getBaseType() {
        return this.primaryType + "/" + this.subType;
    }
    
    public boolean match(final MimeType mimeType) {
        return mimeType != null && this.primaryType.equals(mimeType.getPrimaryType()) && (this.subType.equals("*") || mimeType.getSubType().equals("*") || this.subType.equals(mimeType.getSubType()));
    }
    
    public boolean match(final String s) throws MimeTypeParseException {
        return s != null && this.match(new MimeType(s));
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final String string = this.toString();
        if (string.length() <= 65535) {
            objectOutput.writeUTF(string);
        }
        else {
            objectOutput.writeByte(0);
            objectOutput.writeByte(0);
            objectOutput.writeInt(string.length());
            objectOutput.write(string.getBytes());
        }
    }
    
    @Override
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        String utf = objectInput.readUTF();
        if (utf == null || utf.length() == 0) {
            final byte[] array = new byte[objectInput.readInt()];
            objectInput.readFully(array);
            utf = new String(array);
        }
        try {
            this.parse(utf);
        }
        catch (final MimeTypeParseException ex) {
            throw new IOException(ex.toString());
        }
    }
    
    public Object clone() {
        MimeType mimeType = null;
        try {
            mimeType = (MimeType)super.clone();
        }
        catch (final CloneNotSupportedException ex) {}
        mimeType.parameters = (MimeTypeParameterList)this.parameters.clone();
        return mimeType;
    }
    
    private static boolean isTokenChar(final char c) {
        return c > ' ' && c < '\u007f' && "()<>@,;:\\\"/[]?=".indexOf(c) < 0;
    }
    
    private boolean isValidToken(final String s) {
        final int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; ++i) {
                if (!isTokenChar(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
