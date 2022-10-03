package org.htmlparser.parserapplications;

import org.htmlparser.tags.BaseHrefTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.LinkTag;
import javax.swing.JFileChooser;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JOptionPane;
import org.htmlparser.util.NodeIterator;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URLConnection;
import org.htmlparser.util.ParserException;
import java.net.URL;
import org.htmlparser.NodeFactory;
import org.htmlparser.Tag;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import java.util.HashSet;
import java.util.ArrayList;

public class SiteCapturer
{
    protected String mSource;
    protected String mTarget;
    protected ArrayList mPages;
    protected HashSet mFinished;
    protected ArrayList mImages;
    protected HashSet mCopied;
    protected Parser mParser;
    protected boolean mCaptureResources;
    protected NodeFilter mFilter;
    protected final int TRANSFER_SIZE = 4096;
    
    public SiteCapturer() {
        this.mSource = null;
        this.mTarget = null;
        this.mPages = new ArrayList();
        this.mFinished = new HashSet();
        this.mImages = new ArrayList();
        this.mCopied = new HashSet();
        this.mParser = new Parser();
        final PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
        factory.registerTag(new LocalLinkTag());
        factory.registerTag(new LocalFrameTag());
        factory.registerTag(new LocalBaseHrefTag());
        factory.registerTag(new LocalImageTag());
        this.mParser.setNodeFactory(factory);
        this.mCaptureResources = true;
        this.mFilter = null;
    }
    
    public String getSource() {
        return this.mSource;
    }
    
    public void setSource(String source) {
        if (source.endsWith("/")) {
            source = source.substring(0, source.length() - 1);
        }
        this.mSource = source;
    }
    
    public String getTarget() {
        return this.mTarget;
    }
    
    public void setTarget(final String target) {
        this.mTarget = target;
    }
    
    public boolean getCaptureResources() {
        return this.mCaptureResources;
    }
    
    public void setCaptureResources(final boolean capture) {
        this.mCaptureResources = capture;
    }
    
    public NodeFilter getFilter() {
        return this.mFilter;
    }
    
    public void setFilter(final NodeFilter filter) {
        this.mFilter = filter;
    }
    
    protected boolean isToBeCaptured(final String link) {
        return link.toLowerCase().startsWith(this.getSource().toLowerCase()) && -1 == link.indexOf("?") && -1 == link.indexOf("#");
    }
    
    protected boolean isHtml(final String link) throws ParserException {
        boolean ret = false;
        try {
            final URL url = new URL(link);
            final URLConnection connection = url.openConnection();
            final String type = connection.getContentType();
            ret = (type != null && type.startsWith("text/html"));
        }
        catch (final Exception e) {
            throw new ParserException("URL " + link + " has a problem", e);
        }
        return ret;
    }
    
    protected String makeLocalLink(final String link, String current) {
        String ret;
        if (link.equals(this.getSource()) || (!this.getSource().endsWith("/") && link.equals(this.getSource() + "/"))) {
            ret = "index.html";
        }
        else if (link.startsWith(this.getSource()) && link.length() > this.getSource().length()) {
            ret = link.substring(this.getSource().length() + 1);
        }
        else {
            ret = link;
        }
        if (null != current && link.startsWith(this.getSource()) && current.length() > this.getSource().length()) {
            current = current.substring(this.getSource().length() + 1);
            int j;
            for (int i = 0; -1 != (j = current.indexOf(47, i)); i = j + 1) {
                ret = "../" + ret;
            }
        }
        return ret;
    }
    
    protected String decode(final String raw) {
        final StringBuffer ret = new StringBuffer(raw.length());
        final int length = raw.length();
        int start = 0;
        int index;
        while (-1 != (index = raw.indexOf(37, start))) {
            ret.append(raw.substring(start, index));
            if (index + 2 < length) {
                try {
                    final int value = Integer.parseInt(raw.substring(index + 1, index + 3), 16);
                    ret.append((char)value);
                    start = index + 3;
                }
                catch (final NumberFormatException nfe) {
                    ret.append('%');
                    start = index + 1;
                }
            }
            else {
                ret.append('%');
                start = index + 1;
            }
        }
        ret.append(raw.substring(start));
        return ret.toString();
    }
    
    protected void copy() {
        final String link = this.mImages.remove(0);
        this.mCopied.add(link);
        if (this.getCaptureResources()) {
            final String raw = this.makeLocalLink(link, "");
            final String name = this.decode(raw);
            final File file = new File(this.getTarget(), name);
            System.out.println("copying " + link + " to " + file.getAbsolutePath());
            final File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                final URL source = new URL(link);
                final byte[] data = new byte[4096];
                try {
                    final InputStream in = source.openStream();
                    try {
                        final FileOutputStream out = new FileOutputStream(file);
                        try {
                            int read;
                            while (-1 != (read = in.read(data, 0, data.length))) {
                                out.write(data, 0, read);
                            }
                        }
                        finally {
                            out.close();
                        }
                    }
                    catch (final FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    }
                    finally {
                        in.close();
                    }
                }
                catch (final FileNotFoundException fnfe) {
                    System.err.println("broken link " + fnfe.getMessage() + " ignored");
                }
            }
            catch (final MalformedURLException murle) {
                murle.printStackTrace();
            }
            catch (final IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    protected void process(final NodeFilter filter) throws ParserException {
        final String url = this.mPages.remove(0);
        System.out.println("processing " + url);
        this.mFinished.add(url);
        try {
            final int bookmark = this.mPages.size();
            this.mParser.setURL(url);
            NodeList list;
            try {
                list = new NodeList();
                final NodeIterator e = this.mParser.elements();
                while (e.hasMoreNodes()) {
                    list.add(e.nextNode());
                }
            }
            catch (final EncodingChangeException ece) {
                this.mParser.reset();
                list = new NodeList();
                final NodeIterator e2 = this.mParser.elements();
                while (e2.hasMoreNodes()) {
                    list.add(e2.nextNode());
                }
            }
            final NodeList robots = list.extractAllNodesThatMatch(new AndFilter(new NodeClassFilter(MetaTag.class), new HasAttributeFilter("name", "robots")), true);
            if (0 != robots.size()) {
                final MetaTag robot = (MetaTag)robots.elementAt(0);
                final String content = robot.getAttribute("content").toLowerCase();
                if (-1 != content.indexOf("none") || -1 != content.indexOf("nofollow")) {
                    for (int i = bookmark; i < this.mPages.size(); ++i) {
                        this.mPages.remove(i);
                    }
                }
                if (-1 != content.indexOf("none") || -1 != content.indexOf("noindex")) {
                    return;
                }
            }
            if (null != filter) {
                list.keepAllNodesThatMatch(filter, true);
            }
            File file = new File(this.getTarget(), this.makeLocalLink(url, ""));
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            else if (!dir.isDirectory()) {
                dir = new File(dir.getParentFile(), dir.getName() + ".content");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                file = new File(dir, file.getName());
            }
            try {
                final PrintWriter out = new PrintWriter(new FileOutputStream(file));
                for (int i = 0; i < list.size(); ++i) {
                    out.print(list.elementAt(i).toHtml());
                }
                out.close();
            }
            catch (final FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        }
        catch (final ParserException pe) {
            final String message = pe.getMessage();
            if (null == message || !message.endsWith("does not contain text")) {
                throw pe;
            }
            if (!this.mCopied.contains(url) && !this.mImages.contains(url)) {
                this.mImages.add(url);
            }
            this.mFinished.remove(url);
        }
    }
    
    public void capture() {
        this.mPages.clear();
        this.mPages.add(this.getSource());
        while (0 != this.mPages.size()) {
            try {
                this.process(this.getFilter());
                while (0 != this.mImages.size()) {
                    this.copy();
                }
            }
            catch (final ParserException pe) {
                Throwable throwable = pe.getThrowable();
                if (null != throwable) {
                    throwable = throwable.getCause();
                    if (throwable instanceof FileNotFoundException) {
                        System.err.println("broken link " + throwable.getMessage() + " ignored");
                    }
                    else {
                        pe.printStackTrace();
                    }
                }
                else {
                    pe.printStackTrace();
                }
            }
        }
    }
    
    public static void main(final String[] args) throws MalformedURLException, IOException {
        final SiteCapturer worker = new SiteCapturer();
        if (0 >= args.length) {
            final String url = (String)JOptionPane.showInputDialog(null, "Enter the URL to capture:", "Web Site", -1, null, null, "http://htmlparser.sourceforge.net/wiki");
            if (null != url) {
                worker.setSource(url);
            }
            else {
                System.exit(1);
            }
        }
        else {
            worker.setSource(args[0]);
        }
        if (1 >= args.length) {
            final String url = worker.getSource();
            final URL source = new URL(url);
            final String path = new File(new File("." + File.separator), source.getHost() + File.separator).getCanonicalPath();
            final File target = new File(path);
            final JFileChooser chooser = new JFileChooser(target);
            chooser.setDialogType(1);
            chooser.setFileSelectionMode(1);
            chooser.setSelectedFile(target);
            chooser.setMultiSelectionEnabled(false);
            chooser.setDialogTitle("Target Directory");
            final int ret = chooser.showSaveDialog(null);
            if (ret == 0) {
                worker.setTarget(chooser.getSelectedFile().getAbsolutePath());
            }
            else {
                System.exit(1);
            }
        }
        else {
            worker.setTarget(args[1]);
        }
        if (2 >= args.length) {
            final Boolean capture = (Boolean)JOptionPane.showInputDialog(null, "Should resources be captured:", "Capture Resources", -1, null, new Object[] { Boolean.TRUE, Boolean.FALSE }, Boolean.TRUE);
            if (null != capture) {
                worker.setCaptureResources(capture);
            }
            else {
                System.exit(1);
            }
        }
        else {
            worker.setCaptureResources(Boolean.valueOf(args[2]));
        }
        worker.capture();
        System.exit(0);
    }
    
    class LocalLinkTag extends LinkTag
    {
        public void doSemanticAction() throws ParserException {
            String link = this.getLink();
            if (SiteCapturer.this.isToBeCaptured(link)) {
                boolean html;
                if (SiteCapturer.this.mFinished.contains(link)) {
                    html = true;
                }
                else if (SiteCapturer.this.mPages.contains(link)) {
                    html = true;
                }
                else if (SiteCapturer.this.mCopied.contains(link)) {
                    html = false;
                }
                else if (SiteCapturer.this.mImages.contains(link)) {
                    html = false;
                }
                else {
                    html = SiteCapturer.this.isHtml(link);
                    if (html) {
                        SiteCapturer.this.mPages.add(link);
                    }
                    else {
                        SiteCapturer.this.mImages.add(link);
                    }
                }
                if (html || (!html && SiteCapturer.this.getCaptureResources())) {
                    link = SiteCapturer.this.makeLocalLink(link, SiteCapturer.this.mParser.getLexer().getPage().getUrl());
                }
                this.setLink(link);
            }
        }
    }
    
    class LocalFrameTag extends FrameTag
    {
        public void doSemanticAction() throws ParserException {
            String link = this.getFrameLocation();
            if (SiteCapturer.this.isToBeCaptured(link)) {
                boolean html;
                if (SiteCapturer.this.mFinished.contains(link)) {
                    html = true;
                }
                else if (SiteCapturer.this.mPages.contains(link)) {
                    html = true;
                }
                else if (SiteCapturer.this.mCopied.contains(link)) {
                    html = false;
                }
                else if (SiteCapturer.this.mImages.contains(link)) {
                    html = false;
                }
                else {
                    html = SiteCapturer.this.isHtml(link);
                    if (html) {
                        SiteCapturer.this.mPages.add(link);
                    }
                    else {
                        SiteCapturer.this.mImages.add(link);
                    }
                }
                if (html || (!html && SiteCapturer.this.getCaptureResources())) {
                    link = SiteCapturer.this.makeLocalLink(link, SiteCapturer.this.mParser.getLexer().getPage().getUrl());
                }
                this.setFrameLocation(link);
            }
        }
    }
    
    class LocalImageTag extends ImageTag
    {
        public void doSemanticAction() throws ParserException {
            String image = this.getImageURL();
            if (SiteCapturer.this.isToBeCaptured(image)) {
                if (!SiteCapturer.this.mCopied.contains(image) && !SiteCapturer.this.mImages.contains(image)) {
                    SiteCapturer.this.mImages.add(image);
                }
                if (SiteCapturer.this.getCaptureResources()) {
                    image = SiteCapturer.this.makeLocalLink(image, SiteCapturer.this.mParser.getLexer().getPage().getUrl());
                }
                this.setImageURL(image);
            }
        }
    }
    
    class LocalBaseHrefTag extends BaseHrefTag
    {
        public String toHtml() {
            return "";
        }
    }
}
