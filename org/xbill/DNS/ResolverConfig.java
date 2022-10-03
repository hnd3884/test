package org.xbill.DNS;

import java.io.File;
import java.util.ResourceBundle;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;

public class ResolverConfig
{
    private static String[] servers;
    private static Name[] searchlist;
    private static ResolverConfig currentConfig;
    
    public ResolverConfig() {
        if (this.findProperty()) {
            return;
        }
        if (this.findSunJVM()) {
            return;
        }
        if (ResolverConfig.servers == null || ResolverConfig.searchlist == null) {
            final String OS = System.getProperty("os.name");
            if (OS.indexOf("Windows") != -1) {
                if (OS.indexOf("95") != -1 || OS.indexOf("98") != -1 || OS.indexOf("ME") != -1) {
                    this.find95();
                }
                else {
                    this.findNT();
                }
            }
            else if (OS.indexOf("NetWare") != -1) {
                this.findNetware();
            }
            else {
                this.findUnix();
            }
        }
    }
    
    private void addServer(final String server, final List list) {
        if (list.contains(server)) {
            return;
        }
        if (Options.check("verbose")) {
            System.out.println("adding server " + server);
        }
        list.add(server);
    }
    
    private void addSearch(final String search, final List list) {
        if (Options.check("verbose")) {
            System.out.println("adding search " + search);
        }
        Name name;
        try {
            name = Name.fromString(search, Name.root);
        }
        catch (final TextParseException e) {
            return;
        }
        if (list.contains(name)) {
            return;
        }
        list.add(name);
    }
    
    private void configureFromLists(final List lserver, final List lsearch) {
        if (ResolverConfig.servers == null && lserver.size() > 0) {
            ResolverConfig.servers = lserver.toArray(new String[0]);
        }
        if (ResolverConfig.searchlist == null && lsearch.size() > 0) {
            ResolverConfig.searchlist = lsearch.toArray(new Name[0]);
        }
    }
    
    private boolean findProperty() {
        final List lserver = new ArrayList(0);
        final List lsearch = new ArrayList(0);
        String prop = System.getProperty("dns.server");
        if (prop != null) {
            final StringTokenizer st = new StringTokenizer(prop, ",");
            while (st.hasMoreTokens()) {
                this.addServer(st.nextToken(), lserver);
            }
        }
        prop = System.getProperty("dns.search");
        if (prop != null) {
            final StringTokenizer st = new StringTokenizer(prop, ",");
            while (st.hasMoreTokens()) {
                this.addSearch(st.nextToken(), lsearch);
            }
        }
        this.configureFromLists(lserver, lsearch);
        return ResolverConfig.servers != null && ResolverConfig.searchlist != null;
    }
    
    private boolean findSunJVM() {
        final List lserver = new ArrayList(0);
        final List lsearch = new ArrayList(0);
        List lserver_tmp;
        List lsearch_tmp;
        try {
            final Class[] noClasses = new Class[0];
            final Object[] noObjects = new Object[0];
            final String resConfName = "sun.net.dns.ResolverConfiguration";
            final Class resConfClass = Class.forName(resConfName);
            final Method open = resConfClass.getDeclaredMethod("open", (Class[])noClasses);
            final Object resConf = open.invoke(null, noObjects);
            final Method nameservers = resConfClass.getMethod("nameservers", (Class[])noClasses);
            lserver_tmp = (List)nameservers.invoke(resConf, noObjects);
            final Method searchlist = resConfClass.getMethod("searchlist", (Class[])noClasses);
            lsearch_tmp = (List)searchlist.invoke(resConf, noObjects);
        }
        catch (final Exception e) {
            return false;
        }
        if (lserver_tmp.size() > 0) {
            final Iterator it = lserver_tmp.iterator();
            while (it.hasNext()) {
                this.addServer(it.next(), lserver);
            }
        }
        if (lsearch_tmp.size() > 0) {
            final Iterator it = lsearch_tmp.iterator();
            while (it.hasNext()) {
                this.addSearch(it.next(), lsearch);
            }
        }
        this.configureFromLists(lserver, lsearch);
        return true;
    }
    
    private void findResolvConf(final String file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        }
        catch (final FileNotFoundException e) {
            return;
        }
        final InputStreamReader isr = new InputStreamReader(in);
        final BufferedReader br = new BufferedReader(isr);
        final List lserver = new ArrayList(0);
        final List lsearch = new ArrayList(0);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("nameserver")) {
                    final StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    this.addServer(st.nextToken(), lserver);
                }
                else if (line.startsWith("domain")) {
                    final StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    if (!st.hasMoreTokens()) {
                        continue;
                    }
                    if (!lsearch.isEmpty()) {
                        continue;
                    }
                    this.addSearch(st.nextToken(), lsearch);
                }
                else {
                    if (!line.startsWith("search")) {
                        continue;
                    }
                    if (!lsearch.isEmpty()) {
                        lsearch.clear();
                    }
                    final StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    while (st.hasMoreTokens()) {
                        this.addSearch(st.nextToken(), lsearch);
                    }
                }
            }
            br.close();
        }
        catch (final IOException ex) {}
        this.configureFromLists(lserver, lsearch);
    }
    
    private void findUnix() {
        this.findResolvConf("/etc/resolv.conf");
    }
    
    private void findNetware() {
        this.findResolvConf("sys:/etc/resolv.cfg");
    }
    
    private void findWin(final InputStream in) {
        final String packageName = ResolverConfig.class.getPackage().getName();
        final String resPackageName = packageName + ".windows.DNSServer";
        final ResourceBundle res = ResourceBundle.getBundle(resPackageName);
        final String host_name = res.getString("host_name");
        final String primary_dns_suffix = res.getString("primary_dns_suffix");
        final String dns_suffix = res.getString("dns_suffix");
        final String dns_servers = res.getString("dns_servers");
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            final List lserver = new ArrayList();
            final List lsearch = new ArrayList();
            String line = null;
            boolean readingServers = false;
            boolean readingSearches = false;
            while ((line = br.readLine()) != null) {
                final StringTokenizer st = new StringTokenizer(line);
                if (!st.hasMoreTokens()) {
                    readingServers = false;
                    readingSearches = false;
                }
                else {
                    String s = st.nextToken();
                    if (line.indexOf(":") != -1) {
                        readingServers = false;
                        readingSearches = false;
                    }
                    if (line.indexOf(host_name) != -1) {
                        while (st.hasMoreTokens()) {
                            s = st.nextToken();
                        }
                        Name name;
                        try {
                            name = Name.fromString(s, null);
                        }
                        catch (final TextParseException e) {
                            continue;
                        }
                        if (name.labels() == 1) {
                            continue;
                        }
                        this.addSearch(s, lsearch);
                    }
                    else if (line.indexOf(primary_dns_suffix) != -1) {
                        while (st.hasMoreTokens()) {
                            s = st.nextToken();
                        }
                        if (s.equals(":")) {
                            continue;
                        }
                        this.addSearch(s, lsearch);
                        readingSearches = true;
                    }
                    else if (readingSearches || line.indexOf(dns_suffix) != -1) {
                        while (st.hasMoreTokens()) {
                            s = st.nextToken();
                        }
                        if (s.equals(":")) {
                            continue;
                        }
                        this.addSearch(s, lsearch);
                        readingSearches = true;
                    }
                    else {
                        if (!readingServers && line.indexOf(dns_servers) == -1) {
                            continue;
                        }
                        while (st.hasMoreTokens()) {
                            s = st.nextToken();
                        }
                        if (s.equals(":")) {
                            continue;
                        }
                        this.addServer(s, lserver);
                        readingServers = true;
                    }
                }
            }
            this.configureFromLists(lserver, lsearch);
        }
        catch (final IOException e2) {}
        finally {
            try {
                br.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private void find95() {
        final String s = "winipcfg.out";
        try {
            final Process p = Runtime.getRuntime().exec("winipcfg /all /batch " + s);
            p.waitFor();
            final File f = new File(s);
            this.findWin(new FileInputStream(f));
            new File(s).delete();
        }
        catch (final Exception e) {}
    }
    
    private void findNT() {
        try {
            final Process p = Runtime.getRuntime().exec("ipconfig /all");
            this.findWin(p.getInputStream());
            p.destroy();
        }
        catch (final Exception e) {}
    }
    
    public String[] servers() {
        return ResolverConfig.servers;
    }
    
    public String server() {
        if (ResolverConfig.servers == null) {
            return null;
        }
        return ResolverConfig.servers[0];
    }
    
    public Name[] searchPath() {
        return ResolverConfig.searchlist;
    }
    
    public static synchronized ResolverConfig getCurrentConfig() {
        return ResolverConfig.currentConfig;
    }
    
    public static synchronized void refresh() {
        ResolverConfig.currentConfig = new ResolverConfig();
    }
    
    static {
        ResolverConfig.servers = null;
        ResolverConfig.searchlist = null;
        refresh();
    }
}
