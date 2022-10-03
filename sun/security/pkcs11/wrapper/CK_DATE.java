package sun.security.pkcs11.wrapper;

public class CK_DATE implements Cloneable
{
    public char[] year;
    public char[] month;
    public char[] day;
    
    public CK_DATE(final char[] year, final char[] month, final char[] day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
    
    public Object clone() {
        CK_DATE ck_DATE;
        try {
            ck_DATE = (CK_DATE)super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw (RuntimeException)new RuntimeException("Clone error").initCause(ex);
        }
        ck_DATE.year = this.year.clone();
        ck_DATE.month = this.month.clone();
        ck_DATE.day = this.day.clone();
        return ck_DATE;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(new String(this.day));
        sb.append('.');
        sb.append(new String(this.month));
        sb.append('.');
        sb.append(new String(this.year));
        sb.append(" (DD.MM.YYYY)");
        return sb.toString();
    }
}
