package com.sun.mail.imap;

import javax.mail.MessagingException;
import javax.mail.BodyPart;
import java.util.ArrayList;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import javax.mail.internet.MimePart;
import java.util.List;
import javax.mail.MultipartDataSource;
import javax.mail.internet.MimePartDataSource;

public class IMAPMultipartDataSource extends MimePartDataSource implements MultipartDataSource
{
    private List<IMAPBodyPart> parts;
    
    protected IMAPMultipartDataSource(final MimePart part, final BODYSTRUCTURE[] bs, final String sectionId, final IMAPMessage msg) {
        super(part);
        this.parts = new ArrayList<IMAPBodyPart>(bs.length);
        for (int i = 0; i < bs.length; ++i) {
            this.parts.add(new IMAPBodyPart(bs[i], (sectionId == null) ? Integer.toString(i + 1) : (sectionId + "." + Integer.toString(i + 1)), msg));
        }
    }
    
    @Override
    public int getCount() {
        return this.parts.size();
    }
    
    @Override
    public BodyPart getBodyPart(final int index) throws MessagingException {
        return this.parts.get(index);
    }
}
