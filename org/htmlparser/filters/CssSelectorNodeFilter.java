package org.htmlparser.filters;

import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.Node;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.htmlparser.NodeFilter;

public class CssSelectorNodeFilter implements NodeFilter
{
    private static Pattern tokens;
    private static final int COMMENT = 1;
    private static final int QUOTEDSTRING = 2;
    private static final int RELATION = 3;
    private static final int NAME = 4;
    private static final int COMBINATOR = 5;
    private static final int DELIM = 6;
    private static final int COMMA = 7;
    private NodeFilter therule;
    private Matcher m;
    private int tokentype;
    private String token;
    
    public CssSelectorNodeFilter(final String selector) {
        this.m = null;
        this.tokentype = 0;
        this.token = null;
        this.m = CssSelectorNodeFilter.tokens.matcher(selector);
        if (this.nextToken()) {
            this.therule = this.parse();
        }
    }
    
    public boolean accept(final Node node) {
        return this.therule.accept(node);
    }
    
    private boolean nextToken() {
        if (this.m != null && this.m.find()) {
            for (int i = 1; i < this.m.groupCount(); ++i) {
                if (null != this.m.group(i)) {
                    this.tokentype = i;
                    this.token = this.m.group(i);
                    return true;
                }
            }
        }
        this.tokentype = 0;
        this.token = null;
        return false;
    }
    
    private NodeFilter parse() {
        NodeFilter ret = null;
        do {
            switch (this.tokentype) {
                case 1:
                case 4:
                case 6: {
                    if (ret == null) {
                        ret = this.parseSimple();
                        continue;
                    }
                    ret = new AndFilter(ret, this.parseSimple());
                    continue;
                }
                default: {
                    continue;
                }
                case 5: {
                    switch (this.token.charAt(0)) {
                        case '+': {
                            ret = new AdjacentFilter(ret);
                            break;
                        }
                        case '>': {
                            ret = new HasParentFilter(ret);
                            break;
                        }
                        default: {
                            ret = new HasAncestorFilter(ret);
                            break;
                        }
                    }
                    this.nextToken();
                    continue;
                }
                case 7: {
                    ret = new OrFilter(ret, this.parse());
                    this.nextToken();
                    continue;
                }
            }
        } while (this.token != null);
        return ret;
    }
    
    private NodeFilter parseSimple() {
        boolean done = false;
        NodeFilter ret = null;
        if (this.token != null) {
            do {
                switch (this.tokentype) {
                    case 1: {
                        this.nextToken();
                        continue;
                    }
                    case 4: {
                        if ("*".equals(this.token)) {
                            ret = new YesFilter();
                        }
                        else if (ret == null) {
                            ret = new TagNameFilter(unescape(this.token));
                        }
                        else {
                            ret = new AndFilter(ret, new TagNameFilter(unescape(this.token)));
                        }
                        this.nextToken();
                        continue;
                    }
                    case 6: {
                        switch (this.token.charAt(0)) {
                            case '.': {
                                this.nextToken();
                                if (this.tokentype != 4) {
                                    throw new IllegalArgumentException("Syntax error at " + this.token);
                                }
                                if (ret == null) {
                                    ret = new HasAttributeFilter("class", unescape(this.token));
                                    break;
                                }
                                ret = new AndFilter(ret, new HasAttributeFilter("class", unescape(this.token)));
                                break;
                            }
                            case '#': {
                                this.nextToken();
                                if (this.tokentype != 4) {
                                    throw new IllegalArgumentException("Syntax error at " + this.token);
                                }
                                if (ret == null) {
                                    ret = new HasAttributeFilter("id", unescape(this.token));
                                    break;
                                }
                                ret = new AndFilter(ret, new HasAttributeFilter("id", unescape(this.token)));
                                break;
                            }
                            case ':': {
                                this.nextToken();
                                if (ret == null) {
                                    ret = this.parsePseudoClass();
                                    break;
                                }
                                ret = new AndFilter(ret, this.parsePseudoClass());
                                break;
                            }
                            case '[': {
                                this.nextToken();
                                if (ret == null) {
                                    ret = this.parseAttributeExp();
                                    break;
                                }
                                ret = new AndFilter(ret, this.parseAttributeExp());
                                break;
                            }
                        }
                        this.nextToken();
                        continue;
                    }
                    default: {
                        done = true;
                        continue;
                    }
                }
            } while (!done && this.token != null);
        }
        return ret;
    }
    
    private NodeFilter parsePseudoClass() {
        throw new IllegalArgumentException("pseudoclasses not implemented yet");
    }
    
    private NodeFilter parseAttributeExp() {
        NodeFilter ret = null;
        if (this.tokentype == 4) {
            final String attrib = this.token;
            this.nextToken();
            if ("]".equals(this.token)) {
                ret = new HasAttributeFilter(unescape(attrib));
            }
            else if (this.tokentype == 3) {
                String val = null;
                final String rel = this.token;
                this.nextToken();
                if (this.tokentype == 2) {
                    val = unescape(this.token.substring(1, this.token.length() - 1));
                }
                else if (this.tokentype == 4) {
                    val = unescape(this.token);
                }
                if ("~=".equals(rel) && val != null) {
                    ret = new AttribMatchFilter(unescape(attrib), "\\b" + val.replaceAll("([^a-zA-Z0-9])", "\\\\$1") + "\\b");
                }
                else if ("=".equals(rel) && val != null) {
                    ret = new HasAttributeFilter(attrib, val);
                }
            }
        }
        if (ret == null) {
            throw new IllegalArgumentException("Syntax error at " + this.token + this.tokentype);
        }
        this.nextToken();
        return ret;
    }
    
    public static String unescape(final String escaped) {
        final StringBuffer result = new StringBuffer(escaped.length());
        final Matcher m = Pattern.compile("\\\\(?:([a-fA-F0-9]{2,6})|(.))").matcher(escaped);
        while (m.find()) {
            if (m.group(1) != null) {
                m.appendReplacement(result, String.valueOf((char)Integer.parseInt(m.group(1), 16)));
            }
            else {
                if (m.group(2) == null) {
                    continue;
                }
                m.appendReplacement(result, m.group(2));
            }
        }
        m.appendTail(result);
        return result.toString();
    }
    
    static {
        CssSelectorNodeFilter.tokens = Pattern.compile("(/\\*.*?\\*/) | (   \".*?[^\"]\" | '.*?[^']' | \"\" | '' ) | ( [\\~\\*\\$\\^]? = ) | ( [a-zA-Z_\\*](?:[a-zA-Z0-9_-]|\\\\.)* ) | \\s*( [+>~\\s] )\\s* | ( [\\.\\[\\]\\#\\:)(] ) | ( [\\,] ) | ( . )", 38);
    }
    
    private static class HasAncestorFilter implements NodeFilter
    {
        private NodeFilter atest;
        
        public HasAncestorFilter(final NodeFilter n) {
            this.atest = n;
        }
        
        public boolean accept(Node n) {
            while (n != null) {
                n = n.getParent();
                if (this.atest.accept(n)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private static class AdjacentFilter implements NodeFilter
    {
        private NodeFilter sibtest;
        
        public AdjacentFilter(final NodeFilter n) {
            this.sibtest = n;
        }
        
        public boolean accept(final Node n) {
            if (n.getParent() != null) {
                final NodeList l = n.getParent().getChildren();
                for (int i = 0; i < l.size(); ++i) {
                    if (l.elementAt(i) == n && i > 0) {
                        return this.sibtest.accept(l.elementAt(i - 1));
                    }
                }
            }
            return false;
        }
    }
    
    private static class YesFilter implements NodeFilter
    {
        public boolean accept(final Node n) {
            return true;
        }
    }
    
    private static class AttribMatchFilter implements NodeFilter
    {
        private Pattern rel;
        private String attrib;
        
        public AttribMatchFilter(final String attrib, final String regex) {
            this.rel = Pattern.compile(regex);
            this.attrib = attrib;
        }
        
        public boolean accept(final Node node) {
            return node instanceof Tag && ((Tag)node).getAttribute(this.attrib) != null && (this.rel == null || this.rel.matcher(((Tag)node).getAttribute(this.attrib)).find());
        }
    }
}
