package sun.security.pkcs11.wrapper;

public class CK_SLOT_INFO
{
    public char[] slotDescription;
    public char[] manufacturerID;
    public long flags;
    public CK_VERSION hardwareVersion;
    public CK_VERSION firmwareVersion;
    
    public CK_SLOT_INFO(final char[] slotDescription, final char[] manufacturerID, final long flags, final CK_VERSION hardwareVersion, final CK_VERSION firmwareVersion) {
        this.slotDescription = slotDescription;
        this.manufacturerID = manufacturerID;
        this.flags = flags;
        this.hardwareVersion = hardwareVersion;
        this.firmwareVersion = firmwareVersion;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("slotDescription: ");
        sb.append(new String(this.slotDescription));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("manufacturerID: ");
        sb.append(new String(this.manufacturerID));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("flags: ");
        sb.append(Functions.slotInfoFlagsToString(this.flags));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("hardwareVersion: ");
        sb.append(this.hardwareVersion.toString());
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("firmwareVersion: ");
        sb.append(this.firmwareVersion.toString());
        return sb.toString();
    }
}
