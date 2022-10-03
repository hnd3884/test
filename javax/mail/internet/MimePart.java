package javax.mail.internet;

import java.util.Enumeration;
import javax.mail.MessagingException;
import javax.mail.Part;

public interface MimePart extends Part
{
    String getHeader(final String p0, final String p1) throws MessagingException;
    
    void addHeaderLine(final String p0) throws MessagingException;
    
    Enumeration<String> getAllHeaderLines() throws MessagingException;
    
    Enumeration<String> getMatchingHeaderLines(final String[] p0) throws MessagingException;
    
    Enumeration<String> getNonMatchingHeaderLines(final String[] p0) throws MessagingException;
    
    String getEncoding() throws MessagingException;
    
    String getContentID() throws MessagingException;
    
    String getContentMD5() throws MessagingException;
    
    void setContentMD5(final String p0) throws MessagingException;
    
    String[] getContentLanguage() throws MessagingException;
    
    void setContentLanguage(final String[] p0) throws MessagingException;
    
    void setText(final String p0) throws MessagingException;
    
    void setText(final String p0, final String p1) throws MessagingException;
    
    void setText(final String p0, final String p1, final String p2) throws MessagingException;
}
