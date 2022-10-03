package org.apache.commons.validator;

import org.apache.oro.text.perl.Perl5Util;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.validator.util.Flags;
import java.io.Serializable;

public class UrlValidator implements Serializable
{
    public static final int ALLOW_ALL_SCHEMES = 1;
    public static final int ALLOW_2_SLASHES = 2;
    public static final int NO_FRAGMENTS = 4;
    private static final String ALPHA_CHARS = "a-zA-Z";
    private static final String ALPHA_NUMERIC_CHARS = "a-zA-Z\\d";
    private static final String SPECIAL_CHARS = ";/@&=,.?:+$";
    private static final String VALID_CHARS = "[^\\s;/@&=,.?:+$]";
    private static final String SCHEME_CHARS = "a-zA-Z";
    private static final String AUTHORITY_CHARS = "a-zA-Z\\d\\-\\.";
    private static final String ATOM = "[^\\s;/@&=,.?:+$]+";
    private static final String URL_PATTERN = "/^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?/";
    private static final int PARSE_URL_SCHEME = 2;
    private static final int PARSE_URL_AUTHORITY = 4;
    private static final int PARSE_URL_PATH = 5;
    private static final int PARSE_URL_QUERY = 7;
    private static final int PARSE_URL_FRAGMENT = 9;
    private static final String SCHEME_PATTERN = "/^[a-zA-Z]/";
    private static final String AUTHORITY_PATTERN = "/^([a-zA-Z\\d\\-\\.]*)(:\\d*)?(.*)?/";
    private static final int PARSE_AUTHORITY_HOST_IP = 1;
    private static final int PARSE_AUTHORITY_PORT = 2;
    private static final int PARSE_AUTHORITY_EXTRA = 3;
    private static final String PATH_PATTERN = "/^(/[-\\w:@&?=+,.!/~*'%$_;]*)?$/";
    private static final String QUERY_PATTERN = "/^(.*)$/";
    private static final String LEGAL_ASCII_PATTERN = "/^[\\000-\\177]+$/";
    private static final String IP_V4_DOMAIN_PATTERN = "/^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$/";
    private static final String DOMAIN_PATTERN = "/^[^\\s;/@&=,.?:+$]+(\\.[^\\s;/@&=,.?:+$]+)*$/";
    private static final String PORT_PATTERN = "/^:(\\d{1,5})$/";
    private static final String ATOM_PATTERN = "/([^\\s;/@&=,.?:+$]+)/";
    private static final String ALPHA_PATTERN = "/^[a-zA-Z]/";
    private Flags options;
    private Set allowedSchemes;
    protected String[] defaultSchemes;
    
    public UrlValidator() {
        this(null);
    }
    
    public UrlValidator(final String[] schemes) {
        this(schemes, 0);
    }
    
    public UrlValidator(final int options) {
        this(null, options);
    }
    
    public UrlValidator(String[] schemes, final int options) {
        this.options = null;
        this.allowedSchemes = new HashSet();
        this.defaultSchemes = new String[] { "http", "https", "ftp" };
        this.options = new Flags(options);
        if (this.options.isOn(1L)) {
            return;
        }
        if (schemes == null) {
            schemes = this.defaultSchemes;
        }
        this.allowedSchemes.addAll(Arrays.asList(schemes));
    }
    
    public boolean isValid(final String value) {
        if (value == null) {
            return false;
        }
        final Perl5Util matchUrlPat = new Perl5Util();
        final Perl5Util matchAsciiPat = new Perl5Util();
        return matchAsciiPat.match("/^[\\000-\\177]+$/", value) && matchUrlPat.match("/^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?/", value) && this.isValidScheme(matchUrlPat.group(2)) && this.isValidAuthority(matchUrlPat.group(4)) && this.isValidPath(matchUrlPat.group(5)) && this.isValidQuery(matchUrlPat.group(7)) && this.isValidFragment(matchUrlPat.group(9));
    }
    
    protected boolean isValidScheme(final String scheme) {
        if (scheme == null) {
            return false;
        }
        final Perl5Util schemeMatcher = new Perl5Util();
        return schemeMatcher.match("/^[a-zA-Z]/", scheme) && (!this.options.isOff(1L) || this.allowedSchemes.contains(scheme));
    }
    
    protected boolean isValidAuthority(final String authority) {
        if (authority == null) {
            return false;
        }
        final Perl5Util authorityMatcher = new Perl5Util();
        final Perl5Util matchIPV4Pat = new Perl5Util();
        if (!authorityMatcher.match("/^([a-zA-Z\\d\\-\\.]*)(:\\d*)?(.*)?/", authority)) {
            return false;
        }
        boolean ipV4Address = false;
        boolean hostname = false;
        String hostIP = authorityMatcher.group(1);
        ipV4Address = matchIPV4Pat.match("/^(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})$/", hostIP);
        if (ipV4Address) {
            for (int i = 1; i <= 4; ++i) {
                final String ipSegment = matchIPV4Pat.group(i);
                if (ipSegment == null || ipSegment.length() <= 0) {
                    return false;
                }
                try {
                    if (Integer.parseInt(ipSegment) > 255) {
                        return false;
                    }
                }
                catch (final NumberFormatException e) {
                    return false;
                }
            }
        }
        else {
            final Perl5Util domainMatcher = new Perl5Util();
            hostname = domainMatcher.match("/^[^\\s;/@&=,.?:+$]+(\\.[^\\s;/@&=,.?:+$]+)*$/", hostIP);
        }
        if (hostname) {
            final char[] chars = hostIP.toCharArray();
            int size = 1;
            for (int j = 0; j < chars.length; ++j) {
                if (chars[j] == '.') {
                    ++size;
                }
            }
            final String[] domainSegment = new String[size];
            boolean match = true;
            int segmentCount = 0;
            int segmentLength = 0;
            final Perl5Util atomMatcher = new Perl5Util();
            while (match) {
                match = atomMatcher.match("/([^\\s;/@&=,.?:+$]+)/", hostIP);
                if (match) {
                    domainSegment[segmentCount] = atomMatcher.group(1);
                    segmentLength = domainSegment[segmentCount].length() + 1;
                    hostIP = ((segmentLength >= hostIP.length()) ? "" : hostIP.substring(segmentLength));
                    ++segmentCount;
                }
            }
            final String topLevel = domainSegment[segmentCount - 1];
            if (topLevel.length() < 2 || topLevel.length() > 4) {
                return false;
            }
            final Perl5Util alphaMatcher = new Perl5Util();
            if (!alphaMatcher.match("/^[a-zA-Z]/", topLevel.substring(0, 1))) {
                return false;
            }
            if (segmentCount < 2) {
                return false;
            }
        }
        if (!hostname && !ipV4Address) {
            return false;
        }
        final String port = authorityMatcher.group(2);
        if (port != null) {
            final Perl5Util portMatcher = new Perl5Util();
            if (!portMatcher.match("/^:(\\d{1,5})$/", port)) {
                return false;
            }
        }
        final String extra = authorityMatcher.group(3);
        return GenericValidator.isBlankOrNull(extra);
    }
    
    protected boolean isValidPath(final String path) {
        if (path == null) {
            return false;
        }
        final Perl5Util pathMatcher = new Perl5Util();
        if (!pathMatcher.match("/^(/[-\\w:@&?=+,.!/~*'%$_;]*)?$/", path)) {
            return false;
        }
        final int slash2Count = this.countToken("//", path);
        if (this.options.isOff(2L) && slash2Count > 0) {
            return false;
        }
        final int slashCount = this.countToken("/", path);
        final int dot2Count = this.countToken("..", path);
        return dot2Count <= 0 || slashCount - slash2Count - 1 > dot2Count;
    }
    
    protected boolean isValidQuery(final String query) {
        if (query == null) {
            return true;
        }
        final Perl5Util queryMatcher = new Perl5Util();
        return queryMatcher.match("/^(.*)$/", query);
    }
    
    protected boolean isValidFragment(final String fragment) {
        return fragment == null || this.options.isOff(4L);
    }
    
    protected int countToken(final String token, final String target) {
        int tokenIndex;
        int count;
        for (tokenIndex = 0, count = 0; tokenIndex != -1; ++tokenIndex, ++count) {
            tokenIndex = target.indexOf(token, tokenIndex);
            if (tokenIndex > -1) {}
        }
        return count;
    }
}
