package com.sun.xml.internal.messaging.saaj.util;

import java.util.Iterator;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;

public class MimeHeadersUtil
{
    public static MimeHeaders copy(final MimeHeaders headers) {
        final MimeHeaders newHeaders = new MimeHeaders();
        final Iterator eachHeader = headers.getAllHeaders();
        while (eachHeader.hasNext()) {
            final MimeHeader currentHeader = eachHeader.next();
            newHeaders.addHeader(currentHeader.getName(), currentHeader.getValue());
        }
        return newHeaders;
    }
}
