package sun.security.pkcs11.wrapper;

public class CK_INFO
{
    public CK_VERSION cryptokiVersion;
    public char[] manufacturerID;
    public long flags;
    public char[] libraryDescription;
    public CK_VERSION libraryVersion;
    
    public CK_INFO(final CK_VERSION cryptokiVersion, final char[] manufacturerID, final long flags, final char[] libraryDescription, final CK_VERSION libraryVersion) {
        this.cryptokiVersion = cryptokiVersion;
        this.manufacturerID = manufacturerID;
        this.flags = flags;
        this.libraryDescription = libraryDescription;
        this.libraryVersion = libraryVersion;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("cryptokiVersion: ");
        sb.append(this.cryptokiVersion.toString());
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("manufacturerID: ");
        sb.append(new String(this.manufacturerID));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("flags: ");
        sb.append(Functions.toBinaryString(this.flags));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("libraryDescription: ");
        sb.append(new String(this.libraryDescription));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("libraryVersion: ");
        sb.append(this.libraryVersion.toString());
        return sb.toString();
    }
}
