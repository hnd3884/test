package sun.security.pkcs11.wrapper;

public class CK_MECHANISM_INFO
{
    public long ulMinKeySize;
    public final int iMinKeySize;
    public long ulMaxKeySize;
    public final int iMaxKeySize;
    public long flags;
    
    public CK_MECHANISM_INFO(final long ulMinKeySize, final long ulMaxKeySize, final long flags) {
        this.ulMinKeySize = ulMinKeySize;
        this.ulMaxKeySize = ulMaxKeySize;
        this.iMinKeySize = ((ulMinKeySize < 2147483647L && ulMinKeySize > 0L) ? ((int)ulMinKeySize) : 0);
        this.iMaxKeySize = ((ulMaxKeySize < 2147483647L && ulMaxKeySize > 0L) ? ((int)ulMaxKeySize) : Integer.MAX_VALUE);
        this.flags = flags;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("ulMinKeySize: ");
        sb.append(String.valueOf(this.ulMinKeySize));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulMaxKeySize: ");
        sb.append(String.valueOf(this.ulMaxKeySize));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("flags: ");
        sb.append(String.valueOf(this.flags));
        sb.append(" = ");
        sb.append(Functions.mechanismInfoFlagsToString(this.flags));
        return sb.toString();
    }
}
