package sun.net.www.protocol.file;

import java.io.FilePermission;
import sun.net.www.ParseUtil;
import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.Collections;
import java.text.Collator;
import sun.net.www.MessageHeader;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.io.IOException;
import sun.net.www.MeteredStream;
import sun.net.ProgressSource;
import sun.net.ProgressMonitor;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.net.URL;
import java.security.Permission;
import java.util.List;
import java.io.File;
import java.io.InputStream;
import sun.net.www.URLConnection;

public class FileURLConnection extends URLConnection
{
    static String CONTENT_LENGTH;
    static String CONTENT_TYPE;
    static String TEXT_PLAIN;
    static String LAST_MODIFIED;
    String contentType;
    InputStream is;
    File file;
    String filename;
    boolean isDirectory;
    boolean exists;
    List<String> files;
    long length;
    long lastModified;
    private boolean initializedHeaders;
    Permission permission;
    
    protected FileURLConnection(final URL url, final File file) {
        super(url);
        this.isDirectory = false;
        this.exists = false;
        this.length = -1L;
        this.lastModified = 0L;
        this.initializedHeaders = false;
        this.file = file;
    }
    
    @Override
    public void connect() throws IOException {
        if (!this.connected) {
            try {
                this.filename = this.file.toString();
                this.isDirectory = this.file.isDirectory();
                if (this.isDirectory) {
                    final String[] list = this.file.list();
                    if (list == null) {
                        throw new FileNotFoundException(this.filename + " exists, but is not accessible");
                    }
                    this.files = Arrays.asList(list);
                }
                else {
                    this.is = new BufferedInputStream(new FileInputStream(this.filename));
                    if (ProgressMonitor.getDefault().shouldMeterInput(this.url, "GET")) {
                        this.is = new MeteredStream(this.is, new ProgressSource(this.url, "GET", this.file.length()), this.file.length());
                    }
                }
            }
            catch (final IOException ex) {
                throw ex;
            }
            this.connected = true;
        }
    }
    
    private void initializeHeaders() {
        try {
            this.connect();
            this.exists = this.file.exists();
        }
        catch (final IOException ex) {}
        if (!this.initializedHeaders || !this.exists) {
            this.length = this.file.length();
            this.lastModified = this.file.lastModified();
            if (!this.isDirectory) {
                this.contentType = java.net.URLConnection.getFileNameMap().getContentTypeFor(this.filename);
                if (this.contentType != null) {
                    this.properties.add(FileURLConnection.CONTENT_TYPE, this.contentType);
                }
                this.properties.add(FileURLConnection.CONTENT_LENGTH, String.valueOf(this.length));
                if (this.lastModified != 0L) {
                    final Date date = new Date(this.lastModified);
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    this.properties.add(FileURLConnection.LAST_MODIFIED, simpleDateFormat.format(date));
                }
            }
            else {
                this.properties.add(FileURLConnection.CONTENT_TYPE, FileURLConnection.TEXT_PLAIN);
            }
            this.initializedHeaders = true;
        }
    }
    
    @Override
    public String getHeaderField(final String s) {
        this.initializeHeaders();
        return super.getHeaderField(s);
    }
    
    @Override
    public String getHeaderField(final int n) {
        this.initializeHeaders();
        return super.getHeaderField(n);
    }
    
    @Override
    public int getContentLength() {
        this.initializeHeaders();
        if (this.length > 2147483647L) {
            return -1;
        }
        return (int)this.length;
    }
    
    @Override
    public long getContentLengthLong() {
        this.initializeHeaders();
        return this.length;
    }
    
    @Override
    public String getHeaderFieldKey(final int n) {
        this.initializeHeaders();
        return super.getHeaderFieldKey(n);
    }
    
    @Override
    public MessageHeader getProperties() {
        this.initializeHeaders();
        return super.getProperties();
    }
    
    @Override
    public long getLastModified() {
        this.initializeHeaders();
        return this.lastModified;
    }
    
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        this.connect();
        if (this.is == null) {
            if (!this.isDirectory) {
                throw new FileNotFoundException(this.filename);
            }
            java.net.URLConnection.getFileNameMap();
            final StringBuffer sb = new StringBuffer();
            if (this.files == null) {
                throw new FileNotFoundException(this.filename);
            }
            Collections.sort(this.files, Collator.getInstance());
            for (int i = 0; i < this.files.size(); ++i) {
                sb.append(this.files.get(i));
                sb.append("\n");
            }
            this.is = new ByteArrayInputStream(sb.toString().getBytes());
        }
        return this.is;
    }
    
    @Override
    public Permission getPermission() throws IOException {
        if (this.permission == null) {
            final String decode = ParseUtil.decode(this.url.getPath());
            if (File.separatorChar == '/') {
                this.permission = new FilePermission(decode, "read");
            }
            else {
                this.permission = new FilePermission(decode.replace('/', File.separatorChar), "read");
            }
        }
        return this.permission;
    }
    
    static {
        FileURLConnection.CONTENT_LENGTH = "content-length";
        FileURLConnection.CONTENT_TYPE = "content-type";
        FileURLConnection.TEXT_PLAIN = "text/plain";
        FileURLConnection.LAST_MODIFIED = "last-modified";
    }
}
