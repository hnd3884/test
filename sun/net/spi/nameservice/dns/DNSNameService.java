package sun.net.spi.nameservice.dns;

import java.util.StringTokenizer;
import java.net.InetAddress;
import java.util.Iterator;
import sun.net.util.IPAddressUtil;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import javax.naming.NamingEnumeration;
import java.util.Collection;
import javax.naming.directory.Attribute;
import java.net.UnknownHostException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.spi.NamingManager;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import sun.net.dns.ResolverConfiguration;
import javax.naming.directory.DirContext;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import sun.net.spi.nameservice.NameService;

public final class DNSNameService implements NameService
{
    private LinkedList<String> domainList;
    private String nameProviderUrl;
    private static ThreadLocal<SoftReference<ThreadContext>> contextRef;
    
    private DirContext getTemporaryContext() throws NamingException {
        final SoftReference softReference = DNSNameService.contextRef.get();
        ThreadContext threadContext = null;
        List<String> nameservers = null;
        if (this.nameProviderUrl == null) {
            nameservers = ResolverConfiguration.open().nameservers();
        }
        if (softReference != null && (threadContext = (ThreadContext)softReference.get()) != null && this.nameProviderUrl == null && !threadContext.nameservers().equals(nameservers)) {
            threadContext = null;
        }
        if (threadContext == null) {
            final Hashtable<String, String> hashtable = new Hashtable<String, String>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            String s = this.nameProviderUrl;
            if (s == null) {
                s = createProviderURL(nameservers);
                if (s.length() == 0) {
                    throw new RuntimeException("bad nameserver configuration");
                }
            }
            hashtable.put("java.naming.provider.url", s);
            DirContext dirContext;
            try {
                dirContext = AccessController.doPrivileged((PrivilegedExceptionAction<DirContext>)new PrivilegedExceptionAction<DirContext>() {
                    @Override
                    public DirContext run() throws NamingException {
                        final Context initialContext = NamingManager.getInitialContext(hashtable);
                        if (!(initialContext instanceof DirContext)) {
                            return null;
                        }
                        return (DirContext)initialContext;
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (NamingException)ex.getException();
            }
            threadContext = new ThreadContext(dirContext, nameservers);
            DNSNameService.contextRef.set(new SoftReference<ThreadContext>(threadContext));
        }
        return threadContext.dirContext();
    }
    
    private ArrayList<String> resolve(final DirContext dirContext, final String s, final String[] array, final int n) throws UnknownHostException {
        final ArrayList list = new ArrayList();
        Attributes attributes;
        try {
            attributes = AccessController.doPrivileged((PrivilegedExceptionAction<Attributes>)new PrivilegedExceptionAction<Attributes>() {
                @Override
                public Attributes run() throws NamingException {
                    return dirContext.getAttributes(s, array);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new UnknownHostException(ex.getException().getMessage());
        }
        final NamingEnumeration<? extends Attribute> all = attributes.getAll();
        if (!all.hasMoreElements()) {
            throw new UnknownHostException("DNS record not found");
        }
        UnknownHostException ex2 = null;
        try {
            while (all.hasMoreElements()) {
                final Attribute attribute = all.next();
                final String id = attribute.getID();
                final NamingEnumeration<?> all2 = attribute.getAll();
                while (all2.hasMoreElements()) {
                    final String s2 = (String)all2.next();
                    if (id.equals("CNAME")) {
                        if (n > 4) {
                            throw new UnknownHostException(s + ": possible CNAME loop");
                        }
                        try {
                            list.addAll(this.resolve(dirContext, s2, array, n + 1));
                        }
                        catch (final UnknownHostException ex3) {
                            if (ex2 != null) {
                                continue;
                            }
                            ex2 = ex3;
                        }
                    }
                    else {
                        list.add(s2);
                    }
                }
            }
        }
        catch (final NamingException ex4) {
            throw new UnknownHostException(ex4.getMessage());
        }
        if (list.isEmpty() && ex2 != null) {
            throw ex2;
        }
        return list;
    }
    
    public DNSNameService() throws Exception {
        this.domainList = null;
        this.nameProviderUrl = null;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.net.spi.nameservice.domain"));
        if (s != null && s.length() > 0) {
            (this.domainList = new LinkedList<String>()).add(s);
        }
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.net.spi.nameservice.nameservers"));
        if (s2 != null && s2.length() > 0) {
            this.nameProviderUrl = createProviderURL(s2);
            if (this.nameProviderUrl.length() == 0) {
                throw new RuntimeException("malformed nameservers property");
            }
        }
        else {
            final List<String> nameservers = ResolverConfiguration.open().nameservers();
            if (nameservers.isEmpty()) {
                throw new RuntimeException("no nameservers provided");
            }
            boolean b = false;
            for (final String s3 : nameservers) {
                if (IPAddressUtil.isIPv4LiteralAddress(s3) || IPAddressUtil.isIPv6LiteralAddress(s3)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                throw new RuntimeException("bad nameserver configuration");
            }
        }
    }
    
    @Override
    public InetAddress[] lookupAllHostAddr(final String s) throws UnknownHostException {
        final String[] array = { "A", "AAAA", "CNAME" };
        DirContext temporaryContext;
        try {
            temporaryContext = this.getTemporaryContext();
        }
        catch (final NamingException ex) {
            throw new Error(ex);
        }
        ArrayList<String> list = null;
        UnknownHostException ex2 = null;
        if (s.indexOf(46) >= 0) {
            try {
                list = this.resolve(temporaryContext, s, array, 0);
            }
            catch (final UnknownHostException ex3) {
                ex2 = ex3;
            }
        }
        if (list == null) {
            boolean b = false;
            Object o;
            if (this.domainList != null) {
                o = this.domainList.iterator();
            }
            else {
                final List<String> searchlist = ResolverConfiguration.open().searchlist();
                if (searchlist.size() > 1) {
                    b = true;
                }
                o = searchlist.iterator();
            }
            while (((Iterator)o).hasNext()) {
                String substring = ((Iterator<String>)o).next();
                int index;
                while ((index = substring.indexOf(".")) != -1 && index < substring.length() - 1) {
                    try {
                        list = this.resolve(temporaryContext, s + "." + substring, array, 0);
                    }
                    catch (final UnknownHostException ex4) {
                        ex2 = ex4;
                        if (!b) {
                            substring = substring.substring(index + 1);
                            continue;
                        }
                    }
                    break;
                }
                if (list != null) {
                    break;
                }
            }
        }
        if (list == null && s.indexOf(46) < 0) {
            list = this.resolve(temporaryContext, s, array, 0);
        }
        if (list == null) {
            assert ex2 != null;
            throw ex2;
        }
        else {
            assert list.size() > 0;
            InetAddress[] array2 = new InetAddress[list.size()];
            int n = 0;
            for (int i = 0; i < list.size(); ++i) {
                final String s2 = list.get(i);
                byte[] array3 = IPAddressUtil.textToNumericFormatV4(s2);
                if (array3 == null) {
                    array3 = IPAddressUtil.textToNumericFormatV6(s2);
                }
                if (array3 != null) {
                    array2[n++] = InetAddress.getByAddress(s, array3);
                }
            }
            if (n == 0) {
                throw new UnknownHostException(s + ": no valid DNS records");
            }
            if (n < list.size()) {
                final InetAddress[] array4 = new InetAddress[n];
                for (int j = 0; j < n; ++j) {
                    array4[j] = array2[j];
                }
                array2 = array4;
            }
            return array2;
        }
    }
    
    @Override
    public String getHostByAddr(final byte[] array) throws UnknownHostException {
        String substring = null;
        try {
            String s = "";
            final String[] array2 = { "PTR" };
            DirContext temporaryContext;
            try {
                temporaryContext = this.getTemporaryContext();
            }
            catch (final NamingException ex) {
                throw new Error(ex);
            }
            if (array.length == 4) {
                for (int i = array.length - 1; i >= 0; --i) {
                    s = s + (array[i] & 0xFF) + ".";
                }
                substring = this.resolve(temporaryContext, s + "IN-ADDR.ARPA.", array2, 0).get(0);
            }
            else if (array.length == 16) {
                for (int j = array.length - 1; j >= 0; --j) {
                    s = s + Integer.toHexString(array[j] & 0xF) + "." + Integer.toHexString((array[j] & 0xF0) >> 4) + ".";
                }
                final String string = s + "IP6.ARPA.";
                try {
                    substring = this.resolve(temporaryContext, string, array2, 0).get(0);
                }
                catch (final UnknownHostException ex2) {
                    substring = null;
                }
                if (substring == null) {
                    substring = this.resolve(temporaryContext, s + "IP6.INT.", array2, 0).get(0);
                }
            }
        }
        catch (final Exception ex3) {
            throw new UnknownHostException(ex3.getMessage());
        }
        if (substring == null) {
            throw new UnknownHostException();
        }
        if (substring.endsWith(".")) {
            substring = substring.substring(0, substring.length() - 1);
        }
        return substring;
    }
    
    private static void appendIfLiteralAddress(final String s, final StringBuffer sb) {
        if (IPAddressUtil.isIPv4LiteralAddress(s)) {
            sb.append("dns://" + s + " ");
        }
        else if (IPAddressUtil.isIPv6LiteralAddress(s)) {
            sb.append("dns://[" + s + "] ");
        }
    }
    
    private static String createProviderURL(final List<String> list) {
        final StringBuffer sb = new StringBuffer();
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            appendIfLiteralAddress(iterator.next(), sb);
        }
        return sb.toString();
    }
    
    private static String createProviderURL(final String s) {
        final StringBuffer sb = new StringBuffer();
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        while (stringTokenizer.hasMoreTokens()) {
            appendIfLiteralAddress(stringTokenizer.nextToken(), sb);
        }
        return sb.toString();
    }
    
    static {
        DNSNameService.contextRef = new ThreadLocal<SoftReference<ThreadContext>>();
    }
    
    private static class ThreadContext
    {
        private DirContext dirCtxt;
        private List<String> nsList;
        
        public ThreadContext(final DirContext dirCtxt, final List<String> nsList) {
            this.dirCtxt = dirCtxt;
            this.nsList = nsList;
        }
        
        public DirContext dirContext() {
            return this.dirCtxt;
        }
        
        public List<String> nameservers() {
            return this.nsList;
        }
    }
}
