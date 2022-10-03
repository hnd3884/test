package com.sun.org.apache.xml.internal.utils;

import java.net.URLConnection;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.PrintWriter;
import javax.xml.transform.ErrorListener;
import org.xml.sax.ErrorHandler;

public class ListingErrorHandler implements ErrorHandler, ErrorListener
{
    protected PrintWriter m_pw;
    protected boolean throwOnWarning;
    protected boolean throwOnError;
    protected boolean throwOnFatalError;
    
    public ListingErrorHandler(final PrintWriter pw) {
        this.m_pw = null;
        this.throwOnWarning = false;
        this.throwOnError = true;
        this.throwOnFatalError = true;
        if (null == pw) {
            throw new NullPointerException(XMLMessages.createXMLMessage("ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER", null));
        }
        this.m_pw = pw;
    }
    
    public ListingErrorHandler() {
        this.m_pw = null;
        this.throwOnWarning = false;
        this.throwOnError = true;
        this.throwOnFatalError = true;
        this.m_pw = new PrintWriter(System.err, true);
    }
    
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("warning: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnWarning()) {
            throw exception;
        }
    }
    
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("error: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnError()) {
            throw exception;
        }
    }
    
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("fatalError: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnFatalError()) {
            throw exception;
        }
    }
    
    @Override
    public void warning(final TransformerException exception) throws TransformerException {
        logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("warning: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnWarning()) {
            throw exception;
        }
    }
    
    @Override
    public void error(final TransformerException exception) throws TransformerException {
        logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("error: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnError()) {
            throw exception;
        }
    }
    
    @Override
    public void fatalError(final TransformerException exception) throws TransformerException {
        logExceptionLocation(this.m_pw, exception);
        this.m_pw.println("error: " + exception.getMessage());
        this.m_pw.flush();
        if (this.getThrowOnError()) {
            throw exception;
        }
    }
    
    public static void logExceptionLocation(PrintWriter pw, final Throwable exception) {
        if (null == pw) {
            pw = new PrintWriter(System.err, true);
        }
        SourceLocator locator = null;
        Throwable cause = exception;
        do {
            if (cause instanceof SAXParseException) {
                locator = new SAXSourceLocator((SAXParseException)cause);
            }
            else if (cause instanceof TransformerException) {
                final SourceLocator causeLocator = ((TransformerException)cause).getLocator();
                if (null != causeLocator) {
                    locator = causeLocator;
                }
            }
            if (cause instanceof TransformerException) {
                cause = ((TransformerException)cause).getCause();
            }
            else if (cause instanceof WrappedRuntimeException) {
                cause = ((WrappedRuntimeException)cause).getException();
            }
            else if (cause instanceof SAXException) {
                cause = ((SAXException)cause).getException();
            }
            else {
                cause = null;
            }
        } while (null != cause);
        if (null != locator) {
            final String id = (locator.getPublicId() != locator.getPublicId()) ? locator.getPublicId() : ((null != locator.getSystemId()) ? locator.getSystemId() : "SystemId-Unknown");
            pw.print(id + ":Line=" + locator.getLineNumber() + ";Column=" + locator.getColumnNumber() + ": ");
            pw.println("exception:" + exception.getMessage());
            pw.println("root-cause:" + ((null != cause) ? cause.getMessage() : "null"));
            logSourceLine(pw, locator);
        }
        else {
            pw.print("SystemId-Unknown:locator-unavailable: ");
            pw.println("exception:" + exception.getMessage());
            pw.println("root-cause:" + ((null != cause) ? cause.getMessage() : "null"));
        }
    }
    
    public static void logSourceLine(PrintWriter pw, final SourceLocator locator) {
        if (null == locator) {
            return;
        }
        if (null == pw) {
            pw = new PrintWriter(System.err, true);
        }
        final String url = locator.getSystemId();
        if (null == url) {
            pw.println("line: (No systemId; cannot read file)");
            pw.println();
            return;
        }
        try {
            final int line = locator.getLineNumber();
            final int column = locator.getColumnNumber();
            pw.println("line: " + getSourceLine(url, line));
            final StringBuffer buf = new StringBuffer("line: ");
            for (int i = 1; i < column; ++i) {
                buf.append(' ');
            }
            buf.append('^');
            pw.println(buf.toString());
        }
        catch (final Exception e) {
            pw.println("line: logSourceLine unavailable due to: " + e.getMessage());
            pw.println();
        }
    }
    
    protected static String getSourceLine(final String sourceUrl, final int lineNum) throws Exception {
        URL url = null;
        try {
            url = new URL(sourceUrl);
        }
        catch (final MalformedURLException mue) {
            final int indexOfColon = sourceUrl.indexOf(58);
            final int indexOfSlash = sourceUrl.indexOf(47);
            if (indexOfColon != -1 && indexOfSlash != -1 && indexOfColon < indexOfSlash) {
                throw mue;
            }
            url = new URL(SystemIDResolver.getAbsoluteURI(sourceUrl));
        }
        String line = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            final URLConnection uc = url.openConnection();
            is = uc.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            for (int i = 1; i <= lineNum; ++i) {
                line = br.readLine();
            }
        }
        finally {
            br.close();
            is.close();
        }
        return line;
    }
    
    public void setThrowOnWarning(final boolean b) {
        this.throwOnWarning = b;
    }
    
    public boolean getThrowOnWarning() {
        return this.throwOnWarning;
    }
    
    public void setThrowOnError(final boolean b) {
        this.throwOnError = b;
    }
    
    public boolean getThrowOnError() {
        return this.throwOnError;
    }
    
    public void setThrowOnFatalError(final boolean b) {
        this.throwOnFatalError = b;
    }
    
    public boolean getThrowOnFatalError() {
        return this.throwOnFatalError;
    }
}
