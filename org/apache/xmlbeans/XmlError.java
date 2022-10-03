package org.apache.xmlbeans;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URI;
import javax.xml.stream.Location;
import java.util.ResourceBundle;
import java.io.Serializable;

public class XmlError implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final ResourceBundle _bundle;
    private String _message;
    private String _code;
    private String _source;
    private int _severity;
    private int _line;
    private int _column;
    private int _offset;
    private transient XmlCursor _cursor;
    public static final int SEVERITY_ERROR = 0;
    public static final int SEVERITY_WARNING = 1;
    public static final int SEVERITY_INFO = 2;
    
    public XmlError(final XmlError src) {
        this._severity = 0;
        this._line = -1;
        this._column = -1;
        this._offset = -1;
        this._message = src.getMessage();
        this._code = src.getErrorCode();
        this._severity = src.getSeverity();
        this._source = src.getSourceName();
        this._line = src.getLine();
        this._column = src.getColumn();
        this._offset = src.getOffset();
        this._cursor = src.getCursorLocation();
    }
    
    private XmlError(final String message, final String code, final int severity, final String source, final int line, final int column, final int offset, final XmlCursor cursor) {
        this._severity = 0;
        this._line = -1;
        this._column = -1;
        this._offset = -1;
        this._message = message;
        this._code = code;
        this._severity = severity;
        this._source = source;
        this._line = line;
        this._column = column;
        this._offset = offset;
        this._cursor = cursor;
    }
    
    private XmlError(final String code, final Object[] args, final int severity, final String source, final int line, final int column, final int offset, final XmlCursor cursor) {
        this(formattedMessage(code, args), code, severity, source, line, column, offset, cursor);
    }
    
    protected XmlError(final String message, final String code, final int severity, final XmlCursor cursor) {
        this._severity = 0;
        this._line = -1;
        this._column = -1;
        this._offset = -1;
        String source = null;
        int line = -1;
        int column = -1;
        int offset = -1;
        if (cursor != null) {
            source = cursor.documentProperties().getSourceName();
            final XmlCursor c = cursor.newCursor();
            XmlLineNumber ln = (XmlLineNumber)c.getBookmark(XmlLineNumber.class);
            if (ln == null) {
                ln = (XmlLineNumber)c.toPrevBookmark(XmlLineNumber.class);
            }
            if (ln != null) {
                line = ln.getLine();
                column = ln.getColumn();
                offset = ln.getOffset();
            }
            c.dispose();
        }
        this._message = message;
        this._code = code;
        this._severity = severity;
        this._source = source;
        this._line = line;
        this._column = column;
        this._offset = offset;
        this._cursor = cursor;
    }
    
    protected XmlError(final String code, final Object[] args, final int severity, final XmlCursor cursor) {
        this(formattedMessage(code, args), code, severity, cursor);
    }
    
    protected XmlError(final String message, final String code, final int severity, final Location loc) {
        this._severity = 0;
        this._line = -1;
        this._column = -1;
        this._offset = -1;
        String source = null;
        int line = -1;
        int column = -1;
        if (loc != null) {
            line = loc.getLineNumber();
            column = loc.getColumnNumber();
            source = loc.getPublicId();
            if (source == null) {
                source = loc.getSystemId();
            }
        }
        this._message = message;
        this._code = code;
        this._severity = severity;
        this._source = source;
        this._line = line;
        this._column = column;
    }
    
    protected XmlError(final String code, final Object[] args, final int severity, final Location loc) {
        this(formattedMessage(code, args), code, severity, loc);
    }
    
    public static XmlError forMessage(final String message) {
        return forMessage(message, 0);
    }
    
    public static XmlError forMessage(final String message, final int severity) {
        return forSource(message, severity, null);
    }
    
    public static XmlError forMessage(final String code, final Object[] args) {
        return forSource(code, args, 0, null);
    }
    
    public static XmlError forMessage(final String code, final Object[] args, final int severity) {
        return forSource(code, args, severity, null);
    }
    
    public static XmlError forSource(final String message, final String sourceName) {
        return forLocation(message, 0, sourceName, -1, -1, -1);
    }
    
    public static XmlError forSource(final String message, final int severity, final String sourceName) {
        return forLocation(message, severity, sourceName, -1, -1, -1);
    }
    
    public static XmlError forSource(final String code, final Object[] args, final int severity, final String sourceName) {
        return forLocation(code, args, severity, sourceName, -1, -1, -1);
    }
    
    public static XmlError forLocation(final String message, final String sourceName, final Location location) {
        return new XmlError(message, (String)null, 0, sourceName, location.getLineNumber(), location.getColumnNumber(), -1, null);
    }
    
    public static XmlError forLocation(final String message, final String sourceName, final int line, final int column, final int offset) {
        return new XmlError(message, (String)null, 0, sourceName, line, column, offset, null);
    }
    
    public static XmlError forLocation(final String code, final Object[] args, final int severity, final String sourceName, final int line, final int column, final int offset) {
        return new XmlError(code, args, severity, sourceName, line, column, offset, null);
    }
    
    public static XmlError forLocation(final String message, final int severity, final String sourceName, final int line, final int column, final int offset) {
        return new XmlError(message, (String)null, severity, sourceName, line, column, offset, null);
    }
    
    public static XmlError forLocationAndCursor(final String message, final int severity, final String sourceName, final int line, final int column, final int offset, final XmlCursor cursor) {
        return new XmlError(message, (String)null, severity, sourceName, line, column, offset, cursor);
    }
    
    public static XmlError forObject(final String message, final XmlObject xobj) {
        return forObject(message, 0, xobj);
    }
    
    public static XmlError forObject(final String code, final Object[] args, final XmlObject xobj) {
        return forObject(code, args, 0, xobj);
    }
    
    public static XmlError forObject(final String message, final int severity, final XmlObject xobj) {
        if (xobj == null) {
            return forMessage(message, severity);
        }
        final XmlCursor cur = xobj.newCursor();
        final XmlError result = forCursor(message, severity, cur);
        return result;
    }
    
    public static XmlError forObject(final String code, final Object[] args, final int severity, final XmlObject xobj) {
        if (xobj == null) {
            return forMessage(code, args, severity);
        }
        final XmlCursor cur = xobj.newCursor();
        final XmlError result = forCursor(code, args, severity, cur);
        return result;
    }
    
    public static XmlError forCursor(final String message, final XmlCursor cursor) {
        return forCursor(message, 0, cursor);
    }
    
    public static XmlError forCursor(final String code, final Object[] args, final XmlCursor cursor) {
        return forCursor(code, args, 0, cursor);
    }
    
    public static XmlError forCursor(final String message, final int severity, final XmlCursor cursor) {
        return new XmlError(message, (String)null, severity, cursor);
    }
    
    public static XmlError forCursor(final String code, final Object[] args, final int severity, final XmlCursor cursor) {
        return new XmlError(code, args, severity, cursor);
    }
    
    protected static String formattedFileName(final String rawString, final URI base) {
        if (rawString == null) {
            return null;
        }
        URI uri = null;
        try {
            uri = new URI(rawString);
            if (!uri.isAbsolute()) {
                uri = null;
            }
        }
        catch (final URISyntaxException e) {
            uri = null;
        }
        if (uri == null) {
            uri = new File(rawString).toURI();
        }
        if (base != null) {
            uri = base.relativize(uri);
        }
        if (uri.isAbsolute()) {
            if (uri.getScheme().compareToIgnoreCase("file") != 0) {
                return uri.toString();
            }
        }
        else if (base == null || !base.isAbsolute() || base.getScheme().compareToIgnoreCase("file") != 0) {
            return uri.toString();
        }
        try {
            return new File(uri).toString();
        }
        catch (final Exception ex) {}
        return uri.toString();
    }
    
    public static String formattedMessage(final String code, final Object[] args) {
        if (code == null) {
            return null;
        }
        String message;
        try {
            message = MessageFormat.format(XmlError._bundle.getString(code), args);
        }
        catch (final MissingResourceException e) {
            return MessageFormat.format(XmlError._bundle.getString("message.missing.resource"), e.getMessage());
        }
        catch (final IllegalArgumentException e2) {
            return MessageFormat.format(XmlError._bundle.getString("message.pattern.invalid"), e2.getMessage());
        }
        return message;
    }
    
    public int getSeverity() {
        return this._severity;
    }
    
    public String getMessage() {
        return this._message;
    }
    
    public String getErrorCode() {
        return this._code;
    }
    
    public String getSourceName() {
        return this._source;
    }
    
    public int getLine() {
        return this._line;
    }
    
    public int getColumn() {
        return this._column;
    }
    
    public int getOffset() {
        return this._offset;
    }
    
    public Object getLocation(final Object type) {
        if (type == XmlCursor.class) {
            return this._cursor;
        }
        if (type == XmlObject.class && this._cursor != null) {
            return this._cursor.getObject();
        }
        return null;
    }
    
    public XmlCursor getCursorLocation() {
        return (XmlCursor)this.getLocation(XmlCursor.class);
    }
    
    public XmlObject getObjectLocation() {
        return (XmlObject)this.getLocation(XmlObject.class);
    }
    
    @Override
    public String toString() {
        return this.toString(null);
    }
    
    public String toString(final URI base) {
        final StringBuffer sb = new StringBuffer();
        final String source = formattedFileName(this.getSourceName(), base);
        if (source != null) {
            sb.append(source);
            int line = this.getLine();
            if (line < 0) {
                line = 0;
            }
            sb.append(':');
            sb.append(line);
            sb.append(':');
            if (this.getColumn() > 0) {
                sb.append(this.getColumn());
                sb.append(':');
            }
            sb.append(" ");
        }
        switch (this.getSeverity()) {
            case 0: {
                sb.append("error: ");
                break;
            }
            case 1: {
                sb.append("warning: ");
                break;
            }
        }
        if (this.getErrorCode() != null) {
            sb.append(this.getErrorCode()).append(": ");
        }
        final String msg = this.getMessage();
        sb.append((msg == null) ? "<Unspecified message>" : msg);
        return sb.toString();
    }
    
    public static String severityAsString(final int severity) {
        switch (severity) {
            case 0: {
                return "error";
            }
            case 1: {
                return "warning";
            }
            case 2: {
                return "info";
            }
            default: {
                throw new IllegalArgumentException("unknown severity");
            }
        }
    }
    
    static {
        _bundle = ResourceBundle.getBundle("org.apache.xmlbeans.message");
    }
}
