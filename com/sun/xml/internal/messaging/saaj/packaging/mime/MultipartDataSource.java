package com.sun.xml.internal.messaging.saaj.packaging.mime;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import javax.activation.DataSource;

public interface MultipartDataSource extends DataSource
{
    int getCount();
    
    MimeBodyPart getBodyPart(final int p0) throws MessagingException;
}
