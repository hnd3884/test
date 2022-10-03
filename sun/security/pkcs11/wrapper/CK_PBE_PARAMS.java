package sun.security.pkcs11.wrapper;

public class CK_PBE_PARAMS
{
    public char[] pInitVector;
    public char[] pPassword;
    public char[] pSalt;
    public long ulIteration;
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("pInitVector: ");
        sb.append(this.pInitVector);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulPasswordLen: ");
        sb.append(this.pPassword.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pPassword: ");
        sb.append(this.pPassword);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulSaltLen: ");
        sb.append(this.pSalt.length);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("pSalt: ");
        sb.append(this.pSalt);
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulIteration: ");
        sb.append(this.ulIteration);
        return sb.toString();
    }
}
