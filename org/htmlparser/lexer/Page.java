package org.htmlparser.lexer;

import java.net.MalformedURLException;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import java.util.zip.GZIPInputStream;
import java.net.UnknownHostException;
import java.net.URL;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import org.htmlparser.util.ParserException;
import org.htmlparser.http.ConnectionManager;
import java.net.URLConnection;
import java.io.Serializable;

public class Page implements Serializable
{
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String DEFAULT_CONTENT_TYPE = "text/html";
    public static final char EOF = '\uffff';
    protected String mUrl;
    protected String mBaseUrl;
    protected Source mSource;
    protected PageIndex mIndex;
    protected transient URLConnection mConnection;
    protected static ConnectionManager mConnectionManager;
    
    public Page() {
        this("");
    }
    
    public Page(final URLConnection connection) throws ParserException {
        if (null == connection) {
            throw new IllegalArgumentException("connection cannot be null");
        }
        this.setConnection(connection);
        this.mBaseUrl = null;
    }
    
    public Page(final InputStream stream, String charset) throws UnsupportedEncodingException {
        if (null == stream) {
            throw new IllegalArgumentException("stream cannot be null");
        }
        if (null == charset) {
            charset = "ISO-8859-1";
        }
        this.mSource = new InputStreamSource(stream, charset);
        this.mIndex = new PageIndex(this);
        this.mConnection = null;
        this.mUrl = null;
        this.mBaseUrl = null;
    }
    
    public Page(final String text, String charset) {
        if (null == text) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (null == charset) {
            charset = "ISO-8859-1";
        }
        this.mSource = new StringSource(text, charset);
        this.mIndex = new PageIndex(this);
        this.mConnection = null;
        this.mUrl = null;
        this.mBaseUrl = null;
    }
    
    public Page(final String text) {
        this(text, null);
    }
    
    public Page(final Source source) {
        if (null == source) {
            throw new IllegalArgumentException("source cannot be null");
        }
        this.mSource = source;
        this.mIndex = new PageIndex(this);
        this.mConnection = null;
        this.mUrl = null;
        this.mBaseUrl = null;
    }
    
    public static ConnectionManager getConnectionManager() {
        return Page.mConnectionManager;
    }
    
    public static void setConnectionManager(final ConnectionManager manager) {
        Page.mConnectionManager = manager;
    }
    
    public String getCharset(String content) {
        final String CHARSET_STRING = "charset";
        String ret;
        if (null == this.mSource) {
            ret = "ISO-8859-1";
        }
        else {
            ret = this.mSource.getEncoding();
        }
        if (null != content) {
            int index = content.indexOf("charset");
            if (index != -1) {
                content = content.substring(index + "charset".length()).trim();
                if (content.startsWith("=")) {
                    content = content.substring(1).trim();
                    index = content.indexOf(";");
                    if (index != -1) {
                        content = content.substring(0, index);
                    }
                    if (content.startsWith("\"") && content.endsWith("\"") && 1 < content.length()) {
                        content = content.substring(1, content.length() - 1);
                    }
                    if (content.startsWith("'") && content.endsWith("'") && 1 < content.length()) {
                        content = content.substring(1, content.length() - 1);
                    }
                    ret = findCharset(content, ret);
                }
            }
        }
        return ret;
    }
    
    public static String findCharset(final String name, final String fallback) {
        String ret;
        try {
            final Class cls = Class.forName("java.nio.charset.Charset");
            Method method = cls.getMethod("forName", String.class);
            Object object = method.invoke(null, name);
            method = cls.getMethod("name", (Class[])new Class[0]);
            object = method.invoke(object, new Object[0]);
            ret = (String)object;
        }
        catch (final ClassNotFoundException cnfe) {
            ret = name;
        }
        catch (final NoSuchMethodException nsme) {
            ret = name;
        }
        catch (final IllegalAccessException ia) {
            ret = name;
        }
        catch (final InvocationTargetException ita) {
            ret = fallback;
            System.out.println("unable to determine cannonical charset name for " + name + " - using " + fallback);
        }
        return ret;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        if (null != this.getConnection()) {
            out.writeBoolean(true);
            out.writeInt(this.mSource.offset());
            final String href = this.getUrl();
            out.writeObject(href);
            this.setUrl(this.getConnection().getURL().toExternalForm());
            final Source source = this.getSource();
            this.mSource = null;
            final PageIndex index = this.mIndex;
            this.mIndex = null;
            out.defaultWriteObject();
            this.mSource = source;
            this.mIndex = index;
        }
        else {
            out.writeBoolean(false);
            final String href = this.getUrl();
            out.writeObject(href);
            this.setUrl(null);
            out.defaultWriteObject();
            this.setUrl(href);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final boolean fromurl = in.readBoolean();
        if (fromurl) {
            final int offset = in.readInt();
            final String href = (String)in.readObject();
            in.defaultReadObject();
            if (null != this.getUrl()) {
                final URL url = new URL(this.getUrl());
                try {
                    this.setConnection(url.openConnection());
                }
                catch (final ParserException pe) {
                    throw new IOException(pe.getMessage());
                }
            }
            final Cursor cursor = new Cursor(this, 0);
            for (int i = 0; i < offset; ++i) {
                try {
                    this.getCharacter(cursor);
                }
                catch (final ParserException pe2) {
                    throw new IOException(pe2.getMessage());
                }
            }
            this.setUrl(href);
        }
        else {
            final String href = (String)in.readObject();
            in.defaultReadObject();
            this.setUrl(href);
        }
    }
    
    public void reset() {
        this.getSource().reset();
        this.mIndex = new PageIndex(this);
    }
    
    public void close() throws IOException {
        if (null != this.getSource()) {
            this.getSource().destroy();
        }
    }
    
    protected void finalize() throws Throwable {
        this.close();
    }
    
    public URLConnection getConnection() {
        return this.mConnection;
    }
    
    public void setConnection(final URLConnection connection) throws ParserException {
        this.mConnection = connection;
        try {
            this.getConnection().connect();
        }
        catch (final UnknownHostException uhe) {
            throw new ParserException("Connect to " + this.mConnection.getURL().toExternalForm() + " failed.", uhe);
        }
        catch (final IOException ioe) {
            throw new ParserException("Exception connecting to " + this.mConnection.getURL().toExternalForm() + " (" + ioe.getMessage() + ").", ioe);
        }
        final String type = this.getContentType();
        String charset = this.getCharset(type);
        try {
            final String contentEncoding = connection.getContentEncoding();
            Stream stream;
            if (null != contentEncoding && -1 != contentEncoding.indexOf("gzip")) {
                stream = new Stream(new GZIPInputStream(this.getConnection().getInputStream()));
            }
            else if (null != contentEncoding && -1 != contentEncoding.indexOf("deflate")) {
                stream = new Stream(new InflaterInputStream(this.getConnection().getInputStream(), new Inflater(true)));
            }
            else {
                stream = new Stream(this.getConnection().getInputStream());
            }
            try {
                this.mSource = new InputStreamSource(stream, charset);
            }
            catch (final UnsupportedEncodingException uee) {
                charset = "ISO-8859-1";
                this.mSource = new InputStreamSource(stream, charset);
            }
        }
        catch (final IOException ioe) {
            throw new ParserException("Exception getting input stream from " + this.mConnection.getURL().toExternalForm() + " (" + ioe.getMessage() + ").", ioe);
        }
        this.mUrl = connection.getURL().toExternalForm();
        this.mIndex = new PageIndex(this);
    }
    
    public String getUrl() {
        return this.mUrl;
    }
    
    public void setUrl(final String url) {
        this.mUrl = url;
    }
    
    public String getBaseUrl() {
        return this.mBaseUrl;
    }
    
    public void setBaseUrl(final String url) {
        this.mBaseUrl = url;
    }
    
    public Source getSource() {
        return this.mSource;
    }
    
    public String getContentType() {
        String ret = "text/html";
        final URLConnection connection = this.getConnection();
        if (null != connection) {
            final String content = connection.getHeaderField("Content-Type");
            if (null != content) {
                ret = content;
            }
        }
        return ret;
    }
    
    public char getCharacter(final Cursor cursor) throws ParserException {
        int i = cursor.getPosition();
        final int offset = this.mSource.offset();
        char ret = '\0';
        Label_0181: {
            if (offset == i) {
                try {
                    i = this.mSource.read();
                    if (-1 == i) {
                        ret = '\uffff';
                    }
                    else {
                        ret = (char)i;
                        cursor.advance();
                    }
                    break Label_0181;
                }
                catch (final IOException ioe) {
                    throw new ParserException("problem reading a character at position " + cursor.getPosition(), ioe);
                }
            }
            if (offset <= i) {
                throw new ParserException("attempt to read future characters from source " + i + " > " + this.mSource.offset());
            }
            try {
                ret = this.mSource.getCharacter(i);
            }
            catch (final IOException ioe) {
                throw new ParserException("can't read a character at position " + i, ioe);
            }
            cursor.advance();
        }
        Label_0374: {
            if ('\r' == ret) {
                ret = '\n';
                if (this.mSource.offset() == cursor.getPosition()) {
                    try {
                        i = this.mSource.read();
                        if (-1 != i) {
                            if ('\n' == (char)i) {
                                cursor.advance();
                            }
                            else {
                                try {
                                    this.mSource.unread();
                                }
                                catch (final IOException ioe) {
                                    throw new ParserException("can't unread a character at position " + cursor.getPosition(), ioe);
                                }
                            }
                        }
                        break Label_0374;
                    }
                    catch (final IOException ioe) {
                        throw new ParserException("problem reading a character at position " + cursor.getPosition(), ioe);
                    }
                }
                try {
                    if ('\n' == this.mSource.getCharacter(cursor.getPosition())) {
                        cursor.advance();
                    }
                }
                catch (final IOException ioe) {
                    throw new ParserException("can't read a character at position " + cursor.getPosition(), ioe);
                }
            }
        }
        if ('\n' == ret) {
            this.mIndex.add(cursor);
        }
        return ret;
    }
    
    public void ungetCharacter(final Cursor cursor) throws ParserException {
        cursor.retreat();
        final int i = cursor.getPosition();
        try {
            char ch = this.mSource.getCharacter(i);
            if ('\n' == ch && 0 != i) {
                ch = this.mSource.getCharacter(i - 1);
                if ('\r' == ch) {
                    cursor.retreat();
                }
            }
        }
        catch (final IOException ioe) {
            throw new ParserException("can't read a character at position " + cursor.getPosition(), ioe);
        }
    }
    
    public String getEncoding() {
        return this.getSource().getEncoding();
    }
    
    public void setEncoding(final String character_set) throws ParserException {
        this.getSource().setEncoding(character_set);
    }
    
    public URL constructUrl(final String link, final String base) throws MalformedURLException {
        return this.constructUrl(link, base, false);
    }
    
    public URL constructUrl(final String link, String base, final boolean strict) throws MalformedURLException {
        URL url;
        if (!strict && '?' == link.charAt(0)) {
            final int index;
            if (-1 != (index = base.lastIndexOf(63))) {
                base = base.substring(0, index);
            }
            url = new URL(base + link);
        }
        else {
            url = new URL(new URL(base), link);
        }
        String path = url.getFile();
        boolean modified = false;
        final boolean absolute = link.startsWith("/");
        if (!absolute) {
            while (path.startsWith("/.")) {
                if (path.startsWith("/../")) {
                    path = path.substring(3);
                    modified = true;
                }
                else {
                    if (!path.startsWith("/./") && !path.startsWith("/.")) {
                        break;
                    }
                    path = path.substring(2);
                    modified = true;
                }
            }
        }
        int index;
        while (-1 != (index = path.indexOf("/\\"))) {
            path = path.substring(0, index + 1) + path.substring(index + 2);
            modified = true;
        }
        if (modified) {
            url = new URL(url, path);
        }
        return url;
    }
    
    public String getAbsoluteURL(final String link) {
        return this.getAbsoluteURL(link, false);
    }
    
    public String getAbsoluteURL(final String link, final boolean strict) {
        String ret;
        if (null == link || "".equals(link)) {
            ret = "";
        }
        else {
            try {
                String base = this.getBaseUrl();
                if (null == base) {
                    base = this.getUrl();
                }
                if (null == base) {
                    ret = link;
                }
                else {
                    final URL url = this.constructUrl(link, base, strict);
                    ret = url.toExternalForm();
                }
            }
            catch (final MalformedURLException murle) {
                ret = link;
            }
        }
        return ret;
    }
    
    public int row(final Cursor cursor) {
        return this.mIndex.row(cursor);
    }
    
    public int row(final int position) {
        return this.mIndex.row(position);
    }
    
    public int column(final Cursor cursor) {
        return this.mIndex.column(cursor);
    }
    
    public int column(final int position) {
        return this.mIndex.column(position);
    }
    
    public String getText(final int start, final int end) throws IllegalArgumentException {
        String ret;
        try {
            ret = this.mSource.getString(start, end - start);
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException("can't get the " + (end - start) + "characters at position " + start + " - " + ioe.getMessage());
        }
        return ret;
    }
    
    public void getText(final StringBuffer buffer, int start, int end) throws IllegalArgumentException {
        if (this.mSource.offset() < start || this.mSource.offset() < end) {
            throw new IllegalArgumentException("attempt to extract future characters from source" + start + "|" + end + " > " + this.mSource.offset());
        }
        if (end < start) {
            final int length = end;
            end = start;
            start = length;
        }
        final int length = end - start;
        try {
            this.mSource.getCharacters(buffer, start, length);
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException("can't get the " + (end - start) + "characters at position " + start + " - " + ioe.getMessage());
        }
    }
    
    public String getText() {
        return this.getText(0, this.mSource.offset());
    }
    
    public void getText(final StringBuffer buffer) {
        this.getText(buffer, 0, this.mSource.offset());
    }
    
    public void getText(final char[] array, final int offset, int start, int end) throws IllegalArgumentException {
        if (this.mSource.offset() < start || this.mSource.offset() < end) {
            throw new IllegalArgumentException("attempt to extract future characters from source");
        }
        if (end < start) {
            final int length = end;
            end = start;
            start = length;
        }
        final int length = end - start;
        try {
            this.mSource.getCharacters(array, offset, start, end);
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException("can't get the " + (end - start) + "characters at position " + start + " - " + ioe.getMessage());
        }
    }
    
    public String getLine(final Cursor cursor) {
        int line = this.row(cursor);
        final int size = this.mIndex.size();
        int start;
        int end;
        if (line < size) {
            start = this.mIndex.elementAt(line);
            if (++line <= size) {
                end = this.mIndex.elementAt(line);
            }
            else {
                end = this.mSource.offset();
            }
        }
        else {
            start = this.mIndex.elementAt(line - 1);
            end = this.mSource.offset();
        }
        return this.getText(start, end);
    }
    
    public String getLine(final int position) {
        return this.getLine(new Cursor(this, position));
    }
    
    public String toString() {
        String ret;
        if (this.mSource.offset() > 0) {
            final StringBuffer buffer = new StringBuffer(43);
            int start = this.mSource.offset() - 40;
            if (0 > start) {
                start = 0;
            }
            else {
                buffer.append("...");
            }
            this.getText(buffer, start, this.mSource.offset());
            ret = buffer.toString();
        }
        else {
            ret = super.toString();
        }
        return ret;
    }
    
    static {
        Page.mConnectionManager = new ConnectionManager();
    }
}
