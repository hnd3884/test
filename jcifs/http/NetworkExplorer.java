package jcifs.http;

import javax.servlet.http.HttpSession;
import jcifs.smb.DfsReferral;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.util.Base64;
import jcifs.smb.SmbSession;
import jcifs.UniAddress;
import jcifs.netbios.NbtAddress;
import java.io.PrintWriter;
import java.util.ListIterator;
import java.util.Date;
import jcifs.smb.SmbException;
import jcifs.smb.SmbAuthException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import javax.servlet.ServletOutputStream;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFile;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import jcifs.Config;
import jcifs.util.MimeMap;
import jcifs.util.LogStream;
import javax.servlet.http.HttpServlet;

public class NetworkExplorer extends HttpServlet
{
    private static LogStream log;
    private MimeMap mimeMap;
    private String style;
    private NtlmSsp ntlmSsp;
    private boolean credentialsSupplied;
    private boolean enableBasic;
    private boolean insecureBasic;
    private String realm;
    private String defaultDomain;
    
    public void init() throws ServletException {
        final StringBuffer sb = new StringBuffer();
        final byte[] buf = new byte[1024];
        Config.setProperty("jcifs.smb.client.soTimeout", "600000");
        Config.setProperty("jcifs.smb.client.attrExpirationPeriod", "300000");
        final Enumeration e = this.getInitParameterNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            if (name.startsWith("jcifs.")) {
                Config.setProperty(name, this.getInitParameter(name));
            }
        }
        if (Config.getProperty("jcifs.smb.client.username") == null) {
            this.ntlmSsp = new NtlmSsp();
        }
        else {
            this.credentialsSupplied = true;
        }
        try {
            this.mimeMap = new MimeMap();
            final InputStream is = this.getClass().getClassLoader().getResourceAsStream("jcifs/http/ne.css");
            int n;
            while ((n = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, n, "ISO8859_1"));
            }
            this.style = sb.toString();
        }
        catch (final IOException ioe) {
            throw new ServletException(ioe.getMessage());
        }
        this.enableBasic = Config.getBoolean("jcifs.http.enableBasic", false);
        this.insecureBasic = Config.getBoolean("jcifs.http.insecureBasic", false);
        this.realm = Config.getProperty("jcifs.http.basicRealm");
        if (this.realm == null) {
            this.realm = "jCIFS";
        }
        this.defaultDomain = Config.getProperty("jcifs.smb.client.domain");
        final int level;
        if ((level = Config.getInt("jcifs.util.loglevel", -1)) != -1) {
            LogStream.setLevel(level);
        }
        final LogStream log = NetworkExplorer.log;
        if (LogStream.level > 2) {
            try {
                Config.store(NetworkExplorer.log, "JCIFS PROPERTIES");
            }
            catch (final IOException ex) {}
        }
    }
    
    protected void doFile(final HttpServletRequest req, final HttpServletResponse resp, final SmbFile file) throws IOException {
        final byte[] buf = new byte[8192];
        final SmbFileInputStream in = new SmbFileInputStream(file);
        final ServletOutputStream out = resp.getOutputStream();
        final String url = file.getPath();
        resp.setContentType("text/plain");
        int n;
        final String type;
        if ((n = url.lastIndexOf(46)) > 0 && (type = url.substring(n + 1)) != null && type.length() > 1 && type.length() < 6) {
            resp.setContentType(this.mimeMap.getMimeType(type));
        }
        resp.setHeader("Content-Length", file.length() + "");
        resp.setHeader("Accept-Ranges", "Bytes");
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
    }
    
    protected int compareNames(final SmbFile f1, final String f1name, final SmbFile f2) throws IOException {
        if (f1.isDirectory() != f2.isDirectory()) {
            return f1.isDirectory() ? -1 : 1;
        }
        return f1name.compareToIgnoreCase(f2.getName());
    }
    
    protected int compareSizes(final SmbFile f1, final String f1name, final SmbFile f2) throws IOException {
        if (f1.isDirectory() != f2.isDirectory()) {
            return f1.isDirectory() ? -1 : 1;
        }
        if (f1.isDirectory()) {
            return f1name.compareToIgnoreCase(f2.getName());
        }
        final long diff = f1.length() - f2.length();
        if (diff == 0L) {
            return f1name.compareToIgnoreCase(f2.getName());
        }
        return (diff > 0L) ? -1 : 1;
    }
    
    protected int compareTypes(final SmbFile f1, final String f1name, final SmbFile f2) throws IOException {
        if (f1.isDirectory() != f2.isDirectory()) {
            return f1.isDirectory() ? -1 : 1;
        }
        final String f2name = f2.getName();
        if (f1.isDirectory()) {
            return f1name.compareToIgnoreCase(f2name);
        }
        int i = f1name.lastIndexOf(46);
        final String t1 = (i == -1) ? "" : f1name.substring(i + 1);
        i = f2name.lastIndexOf(46);
        final String t2 = (i == -1) ? "" : f2name.substring(i + 1);
        i = t1.compareToIgnoreCase(t2);
        if (i == 0) {
            return f1name.compareToIgnoreCase(f2name);
        }
        return i;
    }
    
    protected int compareDates(final SmbFile f1, final String f1name, final SmbFile f2) throws IOException {
        if (f1.isDirectory() != f2.isDirectory()) {
            return f1.isDirectory() ? -1 : 1;
        }
        if (f1.isDirectory()) {
            return f1name.compareToIgnoreCase(f2.getName());
        }
        return (f1.lastModified() > f2.lastModified()) ? -1 : 1;
    }
    
    protected void doDirectory(final HttpServletRequest req, final HttpServletResponse resp, final SmbFile dir) throws IOException {
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yy h:mm a");
        final GregorianCalendar cal = new GregorianCalendar();
        sdf.setCalendar(cal);
        final SmbFile[] dirents = dir.listFiles();
        final LogStream log = NetworkExplorer.log;
        if (LogStream.level > 2) {
            NetworkExplorer.log.println(dirents.length + " items listed");
        }
        final LinkedList sorted = new LinkedList();
        String fmt;
        if ((fmt = req.getParameter("fmt")) == null) {
            fmt = "col";
        }
        int sort = 0;
        final String str;
        if ((str = req.getParameter("sort")) == null || str.equals("name")) {
            sort = 0;
        }
        else if (str.equals("size")) {
            sort = 1;
        }
        else if (str.equals("type")) {
            sort = 2;
        }
        else if (str.equals("date")) {
            sort = 3;
        }
        int dirCount;
        int fileCount = dirCount = 0;
        int maxLen = 28;
        for (int i = 0; i < dirents.length; ++i) {
            try {
                if (dirents[i].getType() == 16) {
                    continue;
                }
            }
            catch (final SmbAuthException sae) {
                final LogStream log2 = NetworkExplorer.log;
                if (LogStream.level > 2) {
                    sae.printStackTrace(NetworkExplorer.log);
                }
            }
            catch (final SmbException se) {
                final LogStream log3 = NetworkExplorer.log;
                if (LogStream.level > 2) {
                    se.printStackTrace(NetworkExplorer.log);
                }
                if (se.getNtStatus() != -1073741823) {
                    throw se;
                }
            }
            if (dirents[i].isDirectory()) {
                ++dirCount;
            }
            else {
                ++fileCount;
            }
            final String name = dirents[i].getName();
            final LogStream log4 = NetworkExplorer.log;
            if (LogStream.level > 3) {
                NetworkExplorer.log.println(i + ": " + name);
            }
            final int len = name.length();
            if (len > maxLen) {
                maxLen = len;
            }
            final ListIterator iter = sorted.listIterator();
            int j = 0;
            while (iter.hasNext()) {
                if (sort == 0) {
                    if (this.compareNames(dirents[i], name, iter.next()) < 0) {
                        break;
                    }
                }
                else if (sort == 1) {
                    if (this.compareSizes(dirents[i], name, iter.next()) < 0) {
                        break;
                    }
                }
                else if (sort == 2) {
                    if (this.compareTypes(dirents[i], name, iter.next()) < 0) {
                        break;
                    }
                }
                else if (sort == 3 && this.compareDates(dirents[i], name, iter.next()) < 0) {
                    break;
                }
                ++j;
            }
            sorted.add(j, dirents[i]);
        }
        if (maxLen > 50) {
            maxLen = 50;
        }
        maxLen *= 9;
        final PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<html><head><title>Network Explorer</title>");
        out.println("<meta HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\">");
        out.println("<style TYPE=\"text/css\">");
        out.println(this.style);
        if (dirents.length < 200) {
            out.println("    a:hover {");
            out.println("        background: #a2ff01;");
            out.println("    }");
        }
        out.println("</STYLE>");
        out.println("</head><body>");
        out.print("<a class=\"sort\" style=\"width: " + maxLen + ";\" href=\"?fmt=detail&sort=name\">Name</a>");
        out.println("<a class=\"sort\" href=\"?fmt=detail&sort=size\">Size</a>");
        out.println("<a class=\"sort\" href=\"?fmt=detail&sort=type\">Type</a>");
        out.println("<a class=\"sort\" style=\"width: 180\" href=\"?fmt=detail&sort=date\">Modified</a><br clear='all'><p>");
        String path = dir.getCanonicalPath();
        if (path.length() < 7) {
            out.println("<b><big>smb://</big></b><br>");
            path = ".";
        }
        else {
            out.println("<b><big>" + path + "</big></b><br>");
            path = "../";
        }
        out.println(dirCount + fileCount + " objects (" + dirCount + " directories, " + fileCount + " files)<br>");
        out.println("<b><a class=\"plain\" href=\".\">normal</a> | <a class=\"plain\" href=\"?fmt=detail\">detailed</a></b>");
        out.println("<p><table border='0' cellspacing='0' cellpadding='0'><tr><td>");
        out.print("<A style=\"width: " + maxLen);
        out.print("; height: 18;\" HREF=\"");
        out.print(path);
        out.println("\"><b>&uarr;</b></a>");
        if (fmt.equals("detail")) {
            out.println("<br clear='all'>");
        }
        if (path.length() == 1 || dir.getType() != 2) {
            path = "";
        }
        final ListIterator iter = sorted.listIterator();
        while (iter.hasNext()) {
            final SmbFile f = iter.next();
            final String name = f.getName();
            if (fmt.equals("detail")) {
                out.print("<A style=\"width: " + maxLen);
                out.print("; height: 18;\" HREF=\"");
                out.print(path);
                out.print(name);
                if (f.isDirectory()) {
                    out.print("?fmt=detail\"><b>");
                    out.print(name);
                    out.print("</b></a>");
                }
                else {
                    out.print("\"><b>");
                    out.print(name);
                    out.print("</b></a><div align='right'>");
                    out.print(f.length() / 1024L + " KB </div><div>");
                    final int i = name.lastIndexOf(46) + 1;
                    if (i > 1 && name.length() - i < 6) {
                        out.print(name.substring(i).toUpperCase() + "</div class='ext'>");
                    }
                    else {
                        out.print("&nbsp;</div>");
                    }
                    out.print("<div style='width: 180'>");
                    out.print(sdf.format(new Date(f.lastModified())));
                    out.print("</div>");
                }
                out.println("<br clear='all'>");
            }
            else {
                out.print("<A style=\"width: " + maxLen);
                if (f.isDirectory()) {
                    out.print("; height: 18;\" HREF=\"");
                    out.print(path);
                    out.print(name);
                    out.print("\"><b>");
                    out.print(name);
                    out.print("</b></a>");
                }
                else {
                    out.print(";\" HREF=\"");
                    out.print(path);
                    out.print(name);
                    out.print("\"><b>");
                    out.print(name);
                    out.print("</b><br><small>");
                    out.print(f.length() / 1024L + "KB <br>");
                    out.print(sdf.format(new Date(f.lastModified())));
                    out.print("</small>");
                    out.println("</a>");
                }
            }
        }
        out.println("</td></tr></table>");
        out.println("</BODY></HTML>");
        out.close();
    }
    
    private String parseServerAndShare(final String pathInfo) {
        final char[] out = new char[256];
        if (pathInfo == null) {
            return null;
        }
        int len;
        int p;
        int i;
        for (len = pathInfo.length(), i = (p = 0); p < len && pathInfo.charAt(p) == '/'; ++p) {}
        if (p == len) {
            return null;
        }
        char ch;
        while (p < len && (ch = pathInfo.charAt(p)) != '/') {
            out[i++] = ch;
            ++p;
        }
        while (p < len && pathInfo.charAt(p) == '/') {
            ++p;
        }
        if (p < len) {
            out[i++] = '/';
            do {
                ch = (out[i++] = pathInfo.charAt(p++));
            } while (p < len && ch != '/');
        }
        return new String(out, 0, i);
    }
    
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
        String server = null;
        boolean possibleWorkgroup = true;
        NtlmPasswordAuthentication ntlm = null;
        final HttpSession ssn = req.getSession(false);
        final String pathInfo;
        if ((pathInfo = req.getPathInfo()) != null) {
            server = this.parseServerAndShare(pathInfo);
            final int i;
            if (server != null && (i = server.indexOf(47)) > 0) {
                server = server.substring(0, i).toLowerCase();
                possibleWorkgroup = false;
            }
        }
        final String msg = req.getHeader("Authorization");
        final boolean offerBasic = this.enableBasic && (this.insecureBasic || req.isSecure());
        if (msg != null && (msg.startsWith("NTLM ") || (offerBasic && msg.startsWith("Basic ")))) {
            if (msg.startsWith("NTLM ")) {
                UniAddress dc;
                if (pathInfo == null || server == null) {
                    final String mb = NbtAddress.getByName("\u0001\u0002__MSBROWSE__\u0002", 1, null).getHostAddress();
                    dc = UniAddress.getByName(mb);
                }
                else {
                    dc = UniAddress.getByName(server, possibleWorkgroup);
                }
                req.getSession();
                final byte[] challenge = SmbSession.getChallenge(dc);
                if ((ntlm = NtlmSsp.authenticate(req, resp, challenge)) == null) {
                    return;
                }
            }
            else {
                final String auth = new String(Base64.decode(msg.substring(6)), "US-ASCII");
                int index = auth.indexOf(58);
                String user = (index != -1) ? auth.substring(0, index) : auth;
                final String password = (index != -1) ? auth.substring(index + 1) : "";
                index = user.indexOf(92);
                if (index == -1) {
                    index = user.indexOf(47);
                }
                final String domain = (index != -1) ? user.substring(0, index) : this.defaultDomain;
                user = ((index != -1) ? user.substring(index + 1) : user);
                ntlm = new NtlmPasswordAuthentication(domain, user, password);
            }
            req.getSession().setAttribute("npa-" + server, (Object)ntlm);
        }
        else if (!this.credentialsSupplied) {
            if (ssn != null) {
                ntlm = (NtlmPasswordAuthentication)ssn.getAttribute("npa-" + server);
            }
            if (ntlm == null) {
                resp.setHeader("WWW-Authenticate", "NTLM");
                if (offerBasic) {
                    resp.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
                }
                resp.setHeader("Connection", "close");
                resp.setStatus(401);
                resp.flushBuffer();
                return;
            }
        }
        try {
            SmbFile file;
            if (ntlm != null) {
                file = new SmbFile("smb:/" + pathInfo, ntlm);
            }
            else if (server == null) {
                file = new SmbFile("smb://");
            }
            else {
                file = new SmbFile("smb:/" + pathInfo);
            }
            if (file.isDirectory()) {
                this.doDirectory(req, resp, file);
            }
            else {
                this.doFile(req, resp, file);
            }
        }
        catch (final SmbAuthException sae) {
            if (ssn != null) {
                ssn.removeAttribute("npa-" + server);
            }
            if (sae.getNtStatus() == -1073741819) {
                resp.sendRedirect(req.getRequestURL().toString());
                return;
            }
            resp.setHeader("WWW-Authenticate", "NTLM");
            if (offerBasic) {
                resp.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
            }
            resp.setHeader("Connection", "close");
            resp.setStatus(401);
            resp.flushBuffer();
        }
        catch (final DfsReferral dr) {
            StringBuffer redir = req.getRequestURL();
            final String qs = req.getQueryString();
            redir = new StringBuffer(redir.substring(0, redir.length() - req.getPathInfo().length()));
            redir.append(dr.node.replace('\\', '/'));
            redir.append('/');
            if (qs != null) {
                redir.append(req.getQueryString());
            }
            resp.sendRedirect(redir.toString());
            resp.flushBuffer();
        }
    }
    
    static {
        NetworkExplorer.log = LogStream.getInstance();
    }
}
