package org.apache.xmlbeans.impl.tool;

import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import org.apache.xmlbeans.XmlBeans;
import java.io.OutputStream;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.apache.xmlbeans.impl.common.DefaultClassLoaderResourceLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.Map;

public class CommandLine
{
    private Map _options;
    private String[] _badopts;
    private String[] _args;
    private List _files;
    private List _urls;
    private File _baseDir;
    private static final File[] EMPTY_FILEARRAY;
    private static final URL[] EMPTY_URLARRAY;
    
    public CommandLine(final String[] args, final Collection flags, final Collection scheme) {
        if (flags == null || scheme == null) {
            throw new IllegalArgumentException("collection required (use Collections.EMPTY_SET if no options)");
        }
        this._options = new LinkedHashMap();
        final ArrayList badopts = new ArrayList();
        final ArrayList endargs = new ArrayList();
        for (int i = 0; i < args.length; ++i) {
            if (args[i].indexOf(45) == 0) {
                final String opt = args[i].substring(1);
                String val = null;
                if (flags.contains(opt)) {
                    val = "";
                }
                else if (scheme.contains(opt)) {
                    if (i + 1 < args.length) {
                        val = args[++i];
                    }
                    else {
                        val = "";
                    }
                }
                else {
                    badopts.add(args[i]);
                }
                this._options.put(opt, val);
            }
            else {
                endargs.add(args[i]);
            }
        }
        this._badopts = badopts.toArray(new String[badopts.size()]);
        this._args = endargs.toArray(new String[endargs.size()]);
    }
    
    public static void printLicense() {
        try {
            IOUtil.copyCompletely(new DefaultClassLoaderResourceLoader().getResourceAsStream("LICENSE.txt"), System.out);
        }
        catch (final Exception e) {
            System.out.println("License available in this JAR in LICENSE.txt");
        }
    }
    
    public static void printVersion() {
        System.out.println(XmlBeans.getVendor() + ", " + XmlBeans.getTitle() + ".XmlBeans version " + XmlBeans.getVersion());
    }
    
    public String[] args() {
        final String[] result = new String[this._args.length];
        System.arraycopy(this._args, 0, result, 0, this._args.length);
        return result;
    }
    
    public String[] getBadOpts() {
        return this._badopts;
    }
    
    public String getOpt(final String opt) {
        return this._options.get(opt);
    }
    
    private static List collectFiles(final File[] dirs) {
        final List files = new ArrayList();
        for (int i = 0; i < dirs.length; ++i) {
            final File f = dirs[i];
            if (!f.isDirectory()) {
                files.add(f);
            }
            else {
                files.addAll(collectFiles(f.listFiles()));
            }
        }
        return files;
    }
    
    private List getFileList() {
        if (this._files == null) {
            final String[] args = this.args();
            final File[] files = new File[args.length];
            boolean noBaseDir = false;
            for (int i = 0; i < args.length; ++i) {
                files[i] = new File(args[i]);
                if (!noBaseDir && this._baseDir == null) {
                    if (files[i].isDirectory()) {
                        this._baseDir = files[i];
                    }
                    else {
                        this._baseDir = files[i].getParentFile();
                    }
                }
                else {
                    final URI currUri = files[i].toURI();
                    if (this._baseDir != null && this._baseDir.toURI().relativize(currUri).equals(currUri)) {
                        this._baseDir = null;
                        noBaseDir = true;
                    }
                }
            }
            this._files = Collections.unmodifiableList((List<?>)collectFiles(files));
        }
        return this._files;
    }
    
    private List getUrlList() {
        if (this._urls == null) {
            final String[] args = this.args();
            final List urls = new ArrayList();
            for (int i = 0; i < args.length; ++i) {
                if (looksLikeURL(args[i])) {
                    try {
                        urls.add(new URL(args[i]));
                    }
                    catch (final MalformedURLException mfEx) {
                        System.err.println("ignoring invalid url: " + args[i] + ": " + mfEx.getMessage());
                    }
                }
            }
            this._urls = Collections.unmodifiableList((List<?>)urls);
        }
        return this._urls;
    }
    
    private static boolean looksLikeURL(final String str) {
        return str.startsWith("http:") || str.startsWith("https:") || str.startsWith("ftp:") || str.startsWith("file:");
    }
    
    public URL[] getURLs() {
        return this.getUrlList().toArray(CommandLine.EMPTY_URLARRAY);
    }
    
    public File[] getFiles() {
        return this.getFileList().toArray(CommandLine.EMPTY_FILEARRAY);
    }
    
    public File getBaseDir() {
        return this._baseDir;
    }
    
    public File[] filesEndingWith(final String ext) {
        final List result = new ArrayList();
        for (final File f : this.getFileList()) {
            if (f.getName().endsWith(ext) && !looksLikeURL(f.getPath())) {
                result.add(f);
            }
        }
        return result.toArray(CommandLine.EMPTY_FILEARRAY);
    }
    
    static {
        EMPTY_FILEARRAY = new File[0];
        EMPTY_URLARRAY = new URL[0];
    }
}
