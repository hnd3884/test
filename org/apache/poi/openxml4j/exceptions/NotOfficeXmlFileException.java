package org.apache.poi.openxml4j.exceptions;

import org.apache.poi.UnsupportedFileFormatException;

public class NotOfficeXmlFileException extends UnsupportedFileFormatException
{
    public NotOfficeXmlFileException(final String message) {
        super(message);
    }
    
    public NotOfficeXmlFileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
