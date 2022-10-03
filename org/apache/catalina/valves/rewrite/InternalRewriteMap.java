package org.apache.catalina.valves.rewrite;

import org.apache.tomcat.util.buf.UDecoder;
import org.apache.catalina.util.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.util.Locale;

public class InternalRewriteMap
{
    public static RewriteMap toMap(final String name) {
        if ("toupper".equals(name)) {
            return new UpperCase();
        }
        if ("tolower".equals(name)) {
            return new LowerCase();
        }
        if ("escape".equals(name)) {
            return new Escape();
        }
        if ("unescape".equals(name)) {
            return new Unescape();
        }
        return null;
    }
    
    public static class LowerCase implements RewriteMap
    {
        private Locale locale;
        
        public LowerCase() {
            this.locale = Locale.getDefault();
        }
        
        @Override
        public String setParameters(final String params) {
            this.locale = Locale.forLanguageTag(params);
            return null;
        }
        
        @Override
        public String lookup(final String key) {
            if (key != null) {
                return key.toLowerCase(this.locale);
            }
            return null;
        }
    }
    
    public static class UpperCase implements RewriteMap
    {
        private Locale locale;
        
        public UpperCase() {
            this.locale = Locale.getDefault();
        }
        
        @Override
        public String setParameters(final String params) {
            this.locale = Locale.forLanguageTag(params);
            return null;
        }
        
        @Override
        public String lookup(final String key) {
            if (key != null) {
                return key.toUpperCase(this.locale);
            }
            return null;
        }
    }
    
    public static class Escape implements RewriteMap
    {
        private Charset charset;
        
        public Escape() {
            this.charset = StandardCharsets.UTF_8;
        }
        
        @Override
        public String setParameters(final String params) {
            this.charset = Charset.forName(params);
            return null;
        }
        
        @Override
        public String lookup(final String key) {
            if (key != null) {
                return URLEncoder.DEFAULT.encode(key, this.charset);
            }
            return null;
        }
    }
    
    public static class Unescape implements RewriteMap
    {
        private Charset charset;
        
        public Unescape() {
            this.charset = StandardCharsets.UTF_8;
        }
        
        @Override
        public String setParameters(final String params) {
            this.charset = Charset.forName(params);
            return null;
        }
        
        @Override
        public String lookup(final String key) {
            if (key != null) {
                return UDecoder.URLDecode(key, this.charset);
            }
            return null;
        }
    }
}
