package sun.security.pkcs11.wrapper;

public class CK_SSL3_RANDOM_DATA
{
    public byte[] pClientRandom;
    public byte[] pServerRandom;
    
    public CK_SSL3_RANDOM_DATA(final byte[] pClientRandom, final byte[] pServerRandom) {
        this.pClientRandom = pClientRandom;
        this.pServerRandom = pServerRandom;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append("pClientRandom: ");
        sb.append(Functions.toHexString(this.pClientRandom));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulClientRandomLen: ");
        sb.append(this.pClientRandom.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pServerRandom: ");
        sb.append(Functions.toHexString(this.pServerRandom));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulServerRandomLen: ");
        sb.append(this.pServerRandom.length);
        return sb.toString();
    }
}
