package javax.mail;

import javax.activation.DataSource;

public interface MultipartDataSource extends DataSource
{
    int getCount();
    
    BodyPart getBodyPart(final int p0) throws MessagingException;
}
