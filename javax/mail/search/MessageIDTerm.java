package javax.mail.search;

import javax.mail.Message;

public final class MessageIDTerm extends StringTerm
{
    private static final long serialVersionUID = -2121096296454691963L;
    
    public MessageIDTerm(final String msgid) {
        super(msgid);
    }
    
    @Override
    public boolean match(final Message msg) {
        String[] s;
        try {
            s = msg.getHeader("Message-ID");
        }
        catch (final Exception e) {
            return false;
        }
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length; ++i) {
            if (super.match(s[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof MessageIDTerm && super.equals(obj);
    }
}
