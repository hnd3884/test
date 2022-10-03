package javax.mail.search;

import javax.mail.Message;

public final class SubjectTerm extends StringTerm
{
    private static final long serialVersionUID = 7481568618055573432L;
    
    public SubjectTerm(final String pattern) {
        super(pattern);
    }
    
    @Override
    public boolean match(final Message msg) {
        String subj;
        try {
            subj = msg.getSubject();
        }
        catch (final Exception e) {
            return false;
        }
        return subj != null && super.match(subj);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SubjectTerm && super.equals(obj);
    }
}
