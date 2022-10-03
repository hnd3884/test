package javax.activation;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.Externalizable;

public class MimeType implements Externalizable
{
    private String primaryType;
    private String subType;
    private MimeTypeParameterList parameters;
    private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
    
    public MimeType() {
        this.primaryType = "application";
        this.subType = "*";
        this.parameters = new MimeTypeParameterList();
    }
    
    public MimeType(final String s) throws MimeTypeParseException {
        this.parse(s);
    }
    
    public MimeType(final String s, final String s2) throws MimeTypeParseException {
        if (!this.isValidToken(s)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        this.primaryType = s.toLowerCase();
        if (this.isValidToken(s2)) {
            this.subType = s2.toLowerCase();
            this.parameters = new MimeTypeParameterList();
            return;
        }
        throw new MimeTypeParseException("Sub type is invalid.");
    }
    
    public String getBaseType() {
        return String.valueOf(this.primaryType) + "/" + this.subType;
    }
    
    public String getParameter(final String s) {
        return this.parameters.get(s);
    }
    
    public MimeTypeParameterList getParameters() {
        return this.parameters;
    }
    
    public String getPrimaryType() {
        return this.primaryType;
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    private static boolean isTokenChar(final char c) {
        return c > ' ' && c < '\u007f' && "()<>@,;:/[]?=\\\"".indexOf(c) < 0;
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
    
    public boolean match(final String s) throws MimeTypeParseException {
        return this.match(new MimeType(s));
    }
    
    public boolean match(final MimeType mimeType) {
        return this.primaryType.equals(mimeType.getPrimaryType()) && (this.subType.equals("*") || mimeType.getSubType().equals("*") || this.subType.equals(mimeType.getSubType()));
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
            this.primaryType = s.substring(0, index).trim().toLowerCase();
            this.subType = s.substring(index + 1).trim().toLowerCase();
            this.parameters = new MimeTypeParameterList();
        }
        else {
            if (index >= index2) {
                throw new MimeTypeParseException("Unable to find a sub type.");
            }
            this.primaryType = s.substring(0, index).trim().toLowerCase();
            this.subType = s.substring(index + 1, index2).trim().toLowerCase();
            this.parameters = new MimeTypeParameterList(s.substring(index2));
        }
        if (!this.isValidToken(this.primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        if (!this.isValidToken(this.subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        try {
            this.parse(objectInput.readUTF());
        }
        catch (final MimeTypeParseException ex) {
            throw new IOException(ex.toString());
        }
    }
    
    public void removeParameter(final String s) {
        this.parameters.remove(s);
    }
    
    public void setParameter(final String s, final String s2) {
        this.parameters.set(s, s2);
    }
    
    public void setPrimaryType(final String s) throws MimeTypeParseException {
        if (!this.isValidToken(this.primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        this.primaryType = s.toLowerCase();
    }
    
    public void setSubType(final String s) throws MimeTypeParseException {
        if (!this.isValidToken(this.subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
        this.subType = s.toLowerCase();
    }
    
    public String toString() {
        return String.valueOf(this.getBaseType()) + this.parameters.toString();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeUTF(this.toString());
        objectOutput.flush();
    }
}
