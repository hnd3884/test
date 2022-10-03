package javax.mail.internet;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Locale;
import javax.mail.Address;

public class NewsAddress extends Address
{
    protected String newsgroup;
    protected String host;
    private static final long serialVersionUID = -4203797299824684143L;
    
    public NewsAddress() {
    }
    
    public NewsAddress(final String newsgroup) {
        this(newsgroup, null);
    }
    
    public NewsAddress(final String newsgroup, final String host) {
        this.newsgroup = newsgroup.replaceAll("\\s+", "");
        this.host = host;
    }
    
    @Override
    public String getType() {
        return "news";
    }
    
    public void setNewsgroup(final String newsgroup) {
        this.newsgroup = newsgroup;
    }
    
    public String getNewsgroup() {
        return this.newsgroup;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public String getHost() {
        return this.host;
    }
    
    @Override
    public String toString() {
        return this.newsgroup;
    }
    
    @Override
    public boolean equals(final Object a) {
        if (!(a instanceof NewsAddress)) {
            return false;
        }
        final NewsAddress s = (NewsAddress)a;
        return ((this.newsgroup == null && s.newsgroup == null) || (this.newsgroup != null && this.newsgroup.equals(s.newsgroup))) && ((this.host == null && s.host == null) || (this.host != null && s.host != null && this.host.equalsIgnoreCase(s.host)));
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        if (this.newsgroup != null) {
            hash += this.newsgroup.hashCode();
        }
        if (this.host != null) {
            hash += this.host.toLowerCase(Locale.ENGLISH).hashCode();
        }
        return hash;
    }
    
    public static String toString(final Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return null;
        }
        final StringBuilder s = new StringBuilder(((NewsAddress)addresses[0]).toString());
        int used = s.length();
        for (int i = 1; i < addresses.length; ++i) {
            s.append(",");
            ++used;
            final String ng = ((NewsAddress)addresses[i]).toString();
            if (used + ng.length() > 76) {
                s.append("\r\n\t");
                used = 8;
            }
            s.append(ng);
            used += ng.length();
        }
        return s.toString();
    }
    
    public static NewsAddress[] parse(final String newsgroups) throws AddressException {
        final StringTokenizer st = new StringTokenizer(newsgroups, ",");
        final List<NewsAddress> nglist = new ArrayList<NewsAddress>();
        while (st.hasMoreTokens()) {
            final String ng = st.nextToken();
            nglist.add(new NewsAddress(ng));
        }
        return nglist.toArray(new NewsAddress[nglist.size()]);
    }
}
