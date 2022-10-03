package com.zoho.security.validator.url;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Scheme
{
    private String schemeName;
    List<BitSet> urlcomponents;
    List<String> allowedMimetypes;
    List<String> allowedCharsets;
    List<String> allowedEncoding;
    
    public Scheme() {
        this.schemeName = null;
        this.urlcomponents = null;
        this.allowedMimetypes = null;
        this.allowedCharsets = null;
        this.allowedEncoding = null;
    }
    
    public Scheme(final String schemeName) {
        this.schemeName = null;
        this.urlcomponents = null;
        this.allowedMimetypes = null;
        this.allowedCharsets = null;
        this.allowedEncoding = null;
        this.schemeName = schemeName;
    }
    
    public Scheme(final String schemeVal, final char[] domain_Authority, final char[] path_Info, final char[] query_String, final char[] fragment_part) {
        this.schemeName = null;
        this.urlcomponents = null;
        this.allowedMimetypes = null;
        this.allowedCharsets = null;
        this.allowedEncoding = null;
        this.setSchemeName(schemeVal);
        this.setURLComponents(domain_Authority, path_Info, query_String, fragment_part);
    }
    
    public void setURLComponents(final char[] domain_Authority, final char[] path_Info, final char[] query_String, final char[] fragment_part) {
        if (domain_Authority != null || path_Info != null || query_String != null || fragment_part != null) {
            (this.urlcomponents = new ArrayList<BitSet>()).add(Util.convertCharArrayToBitSet(domain_Authority));
            this.urlcomponents.add(Util.convertCharArrayToBitSet(path_Info));
            this.urlcomponents.add(Util.convertCharArrayToBitSet(query_String));
            this.urlcomponents.add(Util.convertCharArrayToBitSet(fragment_part));
        }
    }
    
    public String getSchemeName() {
        return this.schemeName;
    }
    
    public void setSchemeName(final String name) {
        this.schemeName = name.toLowerCase();
    }
    
    public void setDataURIComponents(final List<String> allowed_Mimetypes, final List<String> allowed_Charsets, final List<String> allowed_Encoding) {
        this.allowedMimetypes = allowed_Mimetypes;
        this.allowedCharsets = allowed_Charsets;
        this.allowedEncoding = allowed_Encoding;
    }
}
