package javax.mail.search;

import javax.mail.Message;

public final class MessageNumberTerm extends IntegerComparisonTerm
{
    private static final long serialVersionUID = -5379625829658623812L;
    
    public MessageNumberTerm(final int number) {
        super(3, number);
    }
    
    @Override
    public boolean match(final Message msg) {
        int msgno;
        try {
            msgno = msg.getMessageNumber();
        }
        catch (final Exception e) {
            return false;
        }
        return super.match(msgno);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof MessageNumberTerm && super.equals(obj);
    }
}
