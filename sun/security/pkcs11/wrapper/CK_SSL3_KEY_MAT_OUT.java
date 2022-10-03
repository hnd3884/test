package sun.security.pkcs11.wrapper;

public class CK_SSL3_KEY_MAT_OUT
{
    public long hClientMacSecret;
    public long hServerMacSecret;
    public long hClientKey;
    public long hServerKey;
    public byte[] pIVClient;
    public byte[] pIVServer;
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append("hClientMacSecret: ");
        sb.append(this.hClientMacSecret);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("hServerMacSecret: ");
        sb.append(this.hServerMacSecret);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("hClientKey: ");
        sb.append(this.hClientKey);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("hServerKey: ");
        sb.append(this.hServerKey);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pIVClient: ");
        sb.append(Functions.toHexString(this.pIVClient));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pIVServer: ");
        sb.append(Functions.toHexString(this.pIVServer));
        return sb.toString();
    }
}
