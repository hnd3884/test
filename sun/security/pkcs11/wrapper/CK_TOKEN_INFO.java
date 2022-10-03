package sun.security.pkcs11.wrapper;

public class CK_TOKEN_INFO
{
    public char[] label;
    public char[] manufacturerID;
    public char[] model;
    public char[] serialNumber;
    public long flags;
    public long ulMaxSessionCount;
    public long ulSessionCount;
    public long ulMaxRwSessionCount;
    public long ulRwSessionCount;
    public long ulMaxPinLen;
    public long ulMinPinLen;
    public long ulTotalPublicMemory;
    public long ulFreePublicMemory;
    public long ulTotalPrivateMemory;
    public long ulFreePrivateMemory;
    public CK_VERSION hardwareVersion;
    public CK_VERSION firmwareVersion;
    public char[] utcTime;
    
    public CK_TOKEN_INFO(final char[] label, final char[] manufacturerID, final char[] model, final char[] serialNumber, final long flags, final long ulMaxSessionCount, final long ulSessionCount, final long ulMaxRwSessionCount, final long ulRwSessionCount, final long ulMaxPinLen, final long ulMinPinLen, final long ulTotalPublicMemory, final long ulFreePublicMemory, final long ulTotalPrivateMemory, final long ulFreePrivateMemory, final CK_VERSION hardwareVersion, final CK_VERSION firmwareVersion, final char[] utcTime) {
        this.label = label;
        this.manufacturerID = manufacturerID;
        this.model = model;
        this.serialNumber = serialNumber;
        this.flags = flags;
        this.ulMaxSessionCount = ulMaxSessionCount;
        this.ulSessionCount = ulSessionCount;
        this.ulMaxRwSessionCount = ulMaxRwSessionCount;
        this.ulRwSessionCount = ulRwSessionCount;
        this.ulMaxPinLen = ulMaxPinLen;
        this.ulMinPinLen = ulMinPinLen;
        this.ulTotalPublicMemory = ulTotalPublicMemory;
        this.ulFreePublicMemory = ulFreePublicMemory;
        this.ulTotalPrivateMemory = ulTotalPrivateMemory;
        this.ulFreePrivateMemory = ulFreePrivateMemory;
        this.hardwareVersion = hardwareVersion;
        this.firmwareVersion = firmwareVersion;
        this.utcTime = utcTime;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ");
        sb.append("label: ");
        sb.append(new String(this.label));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("manufacturerID: ");
        sb.append(new String(this.manufacturerID));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("model: ");
        sb.append(new String(this.model));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("serialNumber: ");
        sb.append(new String(this.serialNumber));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("flags: ");
        sb.append(Functions.tokenInfoFlagsToString(this.flags));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulMaxSessionCount: ");
        sb.append((this.ulMaxSessionCount == 0L) ? "CK_EFFECTIVELY_INFINITE" : ((this.ulMaxSessionCount == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulMaxSessionCount)));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulSessionCount: ");
        sb.append((this.ulSessionCount == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulSessionCount));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulMaxRwSessionCount: ");
        sb.append((this.ulMaxRwSessionCount == 0L) ? "CK_EFFECTIVELY_INFINITE" : ((this.ulMaxRwSessionCount == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulMaxRwSessionCount)));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulRwSessionCount: ");
        sb.append((this.ulRwSessionCount == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulRwSessionCount));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulMaxPinLen: ");
        sb.append(String.valueOf(this.ulMaxPinLen));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulMinPinLen: ");
        sb.append(String.valueOf(this.ulMinPinLen));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulTotalPublicMemory: ");
        sb.append((this.ulTotalPublicMemory == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulTotalPublicMemory));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulFreePublicMemory: ");
        sb.append((this.ulFreePublicMemory == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulFreePublicMemory));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulTotalPrivateMemory: ");
        sb.append((this.ulTotalPrivateMemory == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulTotalPrivateMemory));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("ulFreePrivateMemory: ");
        sb.append((this.ulFreePrivateMemory == -1L) ? "CK_UNAVAILABLE_INFORMATION" : String.valueOf(this.ulFreePrivateMemory));
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("hardwareVersion: ");
        sb.append(this.hardwareVersion.toString());
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("firmwareVersion: ");
        sb.append(this.firmwareVersion.toString());
        sb.append(Constants.NEWLINE);
        sb.append("  ");
        sb.append("utcTime: ");
        sb.append(new String(this.utcTime));
        return sb.toString();
    }
}