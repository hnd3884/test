package org.apache.tomcat.util.http.fileupload.util.mime;

final class ParseException extends Exception
{
    private static final long serialVersionUID = 5355281266579392077L;
    
    ParseException(final String message) {
        super(message);
    }
}
