package javax.mail.search;

import javax.mail.Message;
import java.util.Date;

public final class SentDateTerm extends DateTerm
{
    private static final long serialVersionUID = 5647755030530907263L;
    
    public SentDateTerm(final int comparison, final Date date) {
        super(comparison, date);
    }
    
    @Override
    public boolean match(final Message msg) {
        Date d;
        try {
            d = msg.getSentDate();
        }
        catch (final Exception e) {
            return false;
        }
        return d != null && super.match(d);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SentDateTerm && super.equals(obj);
    }
}
