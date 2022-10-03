package javax.mail.search;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Message;

public final class BodyTerm extends StringTerm
{
    private static final long serialVersionUID = -4888862527916911385L;
    
    public BodyTerm(final String pattern) {
        super(pattern);
    }
    
    @Override
    public boolean match(final Message msg) {
        return this.matchPart(msg);
    }
    
    private boolean matchPart(final Part p) {
        try {
            if (p.isMimeType("text/*")) {
                final String s = (String)p.getContent();
                return s != null && super.match(s);
            }
            if (p.isMimeType("multipart/*")) {
                final Multipart mp = (Multipart)p.getContent();
                for (int count = mp.getCount(), i = 0; i < count; ++i) {
                    if (this.matchPart(mp.getBodyPart(i))) {
                        return true;
                    }
                }
            }
            else if (p.isMimeType("message/rfc822")) {
                return this.matchPart((Part)p.getContent());
            }
        }
        catch (final MessagingException ex) {}
        catch (final IOException ex2) {}
        catch (final RuntimeException ex3) {}
        return false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof BodyTerm && super.equals(obj);
    }
}
