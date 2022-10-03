package com.sun.xml.internal.stream;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import javax.xml.stream.XMLReporter;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;

public class StaxErrorReporter extends XMLErrorReporter
{
    protected XMLReporter fXMLReporter;
    
    public StaxErrorReporter(final PropertyManager propertyManager) {
        this.fXMLReporter = null;
        this.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter());
        this.reset(propertyManager);
    }
    
    public StaxErrorReporter() {
        this.fXMLReporter = null;
        this.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter());
    }
    
    public void reset(final PropertyManager propertyManager) {
        this.fXMLReporter = (XMLReporter)propertyManager.getProperty("javax.xml.stream.reporter");
    }
    
    @Override
    public String reportError(final XMLLocator location, final String domain, final String key, final Object[] arguments, final short severity) throws XNIException {
        final MessageFormatter messageFormatter = this.getMessageFormatter(domain);
        String message;
        if (messageFormatter != null) {
            message = messageFormatter.formatMessage(this.fLocale, key, arguments);
        }
        else {
            final StringBuffer str = new StringBuffer();
            str.append(domain);
            str.append('#');
            str.append(key);
            final int argCount = (arguments != null) ? arguments.length : 0;
            if (argCount > 0) {
                str.append('?');
                for (int i = 0; i < argCount; ++i) {
                    str.append(arguments[i]);
                    if (i < argCount - 1) {
                        str.append('&');
                    }
                }
            }
            message = str.toString();
        }
        switch (severity) {
            case 0: {
                try {
                    if (this.fXMLReporter != null) {
                        this.fXMLReporter.report(message, "WARNING", null, this.convertToStaxLocation(location));
                    }
                    break;
                }
                catch (final XMLStreamException ex) {
                    throw new XNIException(ex);
                }
            }
            case 1: {
                try {
                    if (this.fXMLReporter != null) {
                        this.fXMLReporter.report(message, "ERROR", null, this.convertToStaxLocation(location));
                    }
                    break;
                }
                catch (final XMLStreamException ex) {
                    throw new XNIException(ex);
                }
            }
            case 2: {
                if (!this.fContinueAfterFatalError) {
                    throw new XNIException(message);
                }
                break;
            }
        }
        return message;
    }
    
    Location convertToStaxLocation(final XMLLocator location) {
        return new Location() {
            @Override
            public int getColumnNumber() {
                return location.getColumnNumber();
            }
            
            @Override
            public int getLineNumber() {
                return location.getLineNumber();
            }
            
            @Override
            public String getPublicId() {
                return location.getPublicId();
            }
            
            @Override
            public String getSystemId() {
                return location.getLiteralSystemId();
            }
            
            @Override
            public int getCharacterOffset() {
                return location.getCharacterOffset();
            }
            
            public String getLocationURI() {
                return "";
            }
        };
    }
}
