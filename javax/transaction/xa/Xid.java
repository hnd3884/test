package javax.transaction.xa;

public interface Xid
{
    public static final int MAXGTRIDSIZE = 64;
    public static final int MAXBQUALSIZE = 64;
    
    int getFormatId();
    
    byte[] getGlobalTransactionId();
    
    byte[] getBranchQualifier();
}
