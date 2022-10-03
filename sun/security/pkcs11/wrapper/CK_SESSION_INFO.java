package sun.security.pkcs11.wrapper;

public class CK_SESSION_INFO
{
    public long slotID;
    public long state;
    public long flags;
    public long ulDeviceError;
    
    public CK_SESSION_INFO(final long slotID, final long state, final long flags, final long ulDeviceError) {
        this.slotID = slotID;
        this.state = state;
        this.flags = flags;
        this.ulDeviceError = ulDeviceError;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("slotID: ");
        sb.append(String.valueOf(this.slotID));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("state: ");
        sb.append(Functions.sessionStateToString(this.state));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("flags: ");
        sb.append(Functions.sessionInfoFlagsToString(this.flags));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulDeviceError: ");
        sb.append(Functions.toHexString(this.ulDeviceError));
        return sb.toString();
    }
}
