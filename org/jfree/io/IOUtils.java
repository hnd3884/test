package org.jfree.io;

import java.util.StringTokenizer;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.net.URL;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class IOUtils
{
    private static IOUtils instance;
    
    private IOUtils() {
    }
    
    public void copyStreams(final InputStream in, final OutputStream out) throws IOException {
        this.copyStreams(in, out, 4096);
    }
    
    public void copyStreams(final InputStream in, final OutputStream out, final int buffersize) throws IOException {
        final byte[] bytes = new byte[buffersize];
        for (int bytesRead = in.read(bytes); bytesRead > -1; bytesRead = in.read(bytes)) {
            out.write(bytes, 0, bytesRead);
        }
    }
    
    public void copyWriter(final Reader in, final Writer out) throws IOException {
        this.copyWriter(in, out, 4096);
    }
    
    public void copyWriter(final Reader in, final Writer out, final int buffersize) throws IOException {
        final char[] bytes = new char[buffersize];
        for (int bytesRead = in.read(bytes); bytesRead > -1; bytesRead = in.read(bytes)) {
            out.write(bytes, 0, bytesRead);
        }
    }
    
    public String createRelativeURL(final URL url, final URL baseURL) {
        if (url == null) {
            throw new NullPointerException("content url must not be null.");
        }
        if (baseURL == null) {
            throw new NullPointerException("baseURL must not be null.");
        }
        if (!this.isFileStyleProtocol(url) || !this.isSameService(url, baseURL)) {
            return url.toExternalForm();
        }
        final List urlName = this.parseName(this.getPath(url));
        final List baseName = this.parseName(this.getPath(baseURL));
        final String query = this.getQuery(url);
        if (!this.isPath(baseURL)) {
            baseName.remove(baseName.size() - 1);
        }
        if (url.equals(baseURL)) {
            return urlName.get(urlName.size() - 1);
        }
        int commonIndex = this.startsWithUntil(urlName, baseName);
        if (commonIndex == 0) {
            return url.toExternalForm();
        }
        if (commonIndex == urlName.size()) {
            --commonIndex;
        }
        final ArrayList retval = new ArrayList();
        if (baseName.size() >= urlName.size()) {
            for (int levels = baseName.size() - commonIndex, i = 0; i < levels; ++i) {
                retval.add("..");
            }
        }
        retval.addAll(urlName.subList(commonIndex, urlName.size()));
        return this.formatName(retval, query);
    }
    
    private String formatName(final List name, final String query) {
        final StringBuffer b = new StringBuffer();
        final Iterator it = name.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append("/");
            }
        }
        if (query != null) {
            b.append('?');
            b.append(query);
        }
        return b.toString();
    }
    
    public String getFileExtension(final String file) {
        final int idx = file.lastIndexOf(".");
        if (idx < 1) {
            return "";
        }
        return file.substring(idx);
    }
    
    public String getFileName(final URL url) {
        final String file = url.getFile();
        final int last = file.lastIndexOf("/");
        if (last < 0) {
            return file;
        }
        return file.substring(last);
    }
    
    public static IOUtils getInstance() {
        if (IOUtils.instance == null) {
            IOUtils.instance = new IOUtils();
        }
        return IOUtils.instance;
    }
    
    private String getPath(final URL url) {
        final String file = url.getFile();
        final int queryIndex = file.indexOf(63);
        if (queryIndex == -1) {
            return file;
        }
        return file.substring(0, queryIndex);
    }
    
    private String getQuery(final URL url) {
        final String file = url.getFile();
        final int queryIndex = file.indexOf(63);
        if (queryIndex == -1) {
            return null;
        }
        return file.substring(queryIndex + 1);
    }
    
    private boolean isFileStyleProtocol(final URL url) {
        return url.getProtocol().equals("http") || url.getProtocol().equals("https") || url.getProtocol().equals("ftp") || url.getProtocol().equals("file") || url.getProtocol().equals("jar");
    }
    
    private boolean isPath(final URL baseURL) {
        if (this.getPath(baseURL).endsWith("/")) {
            return true;
        }
        if (baseURL.getProtocol().equals("file")) {
            final File f = new File(this.getPath(baseURL));
            try {
                if (f.isDirectory()) {
                    return true;
                }
            }
            catch (final SecurityException ex) {}
        }
        return false;
    }
    
    private boolean isSameService(final URL url, final URL baseUrl) {
        return url.getProtocol().equals(baseUrl.getProtocol()) && url.getHost().equals(baseUrl.getHost()) && url.getPort() == baseUrl.getPort();
    }
    
    public boolean isSubDirectory(File base, File child) throws IOException {
        base = base.getCanonicalFile();
        File parentFile;
        for (child = (parentFile = child.getCanonicalFile()); parentFile != null; parentFile = parentFile.getParentFile()) {
            if (base.equals(parentFile)) {
                return true;
            }
        }
        return false;
    }
    
    private List parseName(final String name) {
        final ArrayList list = new ArrayList();
        final StringTokenizer strTok = new StringTokenizer(name, "/");
        while (strTok.hasMoreElements()) {
            final String s = (String)strTok.nextElement();
            if (s.length() != 0) {
                list.add(s);
            }
        }
        return list;
    }
    
    private int startsWithUntil(final List baseName, final List urlName) {
        final int minIdx = Math.min(urlName.size(), baseName.size());
        for (int i = 0; i < minIdx; ++i) {
            final String baseToken = baseName.get(i);
            final String urlToken = urlName.get(i);
            if (!baseToken.equals(urlToken)) {
                return i;
            }
        }
        return minIdx;
    }
    
    public String stripFileExtension(final String file) {
        final int idx = file.lastIndexOf(".");
        if (idx < 1) {
            return file;
        }
        return file.substring(0, idx);
    }
}
