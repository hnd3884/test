package sun.security.provider.certpath;

import java.util.Arrays;
import sun.security.util.DerValue;
import java.security.PublicKey;
import java.io.IOException;
import sun.security.x509.KeyIdentifier;
import javax.security.auth.x500.X500Principal;

public final class ResponderId
{
    private Type type;
    private X500Principal responderName;
    private KeyIdentifier responderKeyId;
    private byte[] encodedRid;
    
    public ResponderId(final X500Principal responderName) throws IOException {
        this.responderName = responderName;
        this.responderKeyId = null;
        this.encodedRid = this.principalToBytes();
        this.type = Type.BY_NAME;
    }
    
    public ResponderId(final PublicKey publicKey) throws IOException {
        this.responderKeyId = new KeyIdentifier(publicKey);
        this.responderName = null;
        this.encodedRid = this.keyIdToBytes();
        this.type = Type.BY_KEY;
    }
    
    public ResponderId(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.isContextSpecific((byte)Type.BY_NAME.value()) && derValue.isConstructed()) {
            this.responderName = new X500Principal(derValue.getDataBytes());
            this.encodedRid = this.principalToBytes();
            this.type = Type.BY_NAME;
        }
        else {
            if (!derValue.isContextSpecific((byte)Type.BY_KEY.value()) || !derValue.isConstructed()) {
                throw new IOException("Invalid ResponderId content");
            }
            this.responderKeyId = new KeyIdentifier(new DerValue(derValue.getDataBytes()));
            this.encodedRid = this.keyIdToBytes();
            this.type = Type.BY_KEY;
        }
    }
    
    public byte[] getEncoded() {
        return this.encodedRid.clone();
    }
    
    public Type getType() {
        return this.type;
    }
    
    public int length() {
        return this.encodedRid.length;
    }
    
    public X500Principal getResponderName() {
        return this.responderName;
    }
    
    public KeyIdentifier getKeyIdentifier() {
        return this.responderKeyId;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof ResponderId && Arrays.equals(this.encodedRid, ((ResponderId)o).getEncoded())));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.encodedRid);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        switch (this.type) {
            case BY_NAME: {
                sb.append(this.type).append(": ").append(this.responderName);
                break;
            }
            case BY_KEY: {
                sb.append(this.type).append(": ");
                final byte[] identifier = this.responderKeyId.getIdentifier();
                for (int length = identifier.length, i = 0; i < length; ++i) {
                    sb.append(String.format("%02X", identifier[i]));
                }
                break;
            }
            default: {
                sb.append("Unknown ResponderId Type: ").append(this.type);
                break;
            }
        }
        return sb.toString();
    }
    
    private byte[] principalToBytes() throws IOException {
        return new DerValue(DerValue.createTag((byte)(-128), true, (byte)Type.BY_NAME.value()), this.responderName.getEncoded()).toByteArray();
    }
    
    private byte[] keyIdToBytes() throws IOException {
        return new DerValue(DerValue.createTag((byte)(-128), true, (byte)Type.BY_KEY.value()), new DerValue((byte)4, this.responderKeyId.getIdentifier()).toByteArray()).toByteArray();
    }
    
    public enum Type
    {
        BY_NAME(1, "byName"), 
        BY_KEY(2, "byKey");
        
        private final int tagNumber;
        private final String ridTypeName;
        
        private Type(final int tagNumber, final String ridTypeName) {
            this.tagNumber = tagNumber;
            this.ridTypeName = ridTypeName;
        }
        
        public int value() {
            return this.tagNumber;
        }
        
        @Override
        public String toString() {
            return this.ridTypeName;
        }
    }
}
