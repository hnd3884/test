package org.apache.lucene.analysis.charfilter;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import java.util.regex.Matcher;
import java.io.Reader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.analysis.util.ResourceLoader;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.CharFilterFactory;

public class MappingCharFilterFactory extends CharFilterFactory implements ResourceLoaderAware, MultiTermAwareComponent
{
    protected NormalizeCharMap normMap;
    private final String mapping;
    static Pattern p;
    char[] out;
    
    public MappingCharFilterFactory(final Map<String, String> args) {
        super(args);
        this.out = new char[256];
        this.mapping = this.get(args, "mapping");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public void inform(final ResourceLoader loader) throws IOException {
        if (this.mapping != null) {
            List<String> wlist = null;
            final List<String> files = this.splitFileNames(this.mapping);
            wlist = new ArrayList<String>();
            for (final String file : files) {
                final List<String> lines = this.getLines(loader, file.trim());
                wlist.addAll(lines);
            }
            final NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
            this.parseRules(wlist, builder);
            this.normMap = builder.build();
            if (this.normMap.map == null) {
                this.normMap = null;
            }
        }
    }
    
    @Override
    public Reader create(final Reader input) {
        return (Reader)((this.normMap == null) ? input : new MappingCharFilter(this.normMap, input));
    }
    
    protected void parseRules(final List<String> rules, final NormalizeCharMap.Builder builder) {
        for (final String rule : rules) {
            final Matcher m = MappingCharFilterFactory.p.matcher(rule);
            if (!m.find()) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "], file = " + this.mapping);
            }
            builder.add(this.parseString(m.group(1)), this.parseString(m.group(2)));
        }
    }
    
    protected String parseString(final String s) {
        int readPos = 0;
        final int len = s.length();
        int writePos = 0;
        while (readPos < len) {
            char c = s.charAt(readPos++);
            if (c == '\\') {
                if (readPos >= len) {
                    throw new IllegalArgumentException("Invalid escaped char in [" + s + "]");
                }
                c = s.charAt(readPos++);
                switch (c) {
                    case '\\': {
                        c = '\\';
                        break;
                    }
                    case '\"': {
                        c = '\"';
                        break;
                    }
                    case 'n': {
                        c = '\n';
                        break;
                    }
                    case 't': {
                        c = '\t';
                        break;
                    }
                    case 'r': {
                        c = '\r';
                        break;
                    }
                    case 'b': {
                        c = '\b';
                        break;
                    }
                    case 'f': {
                        c = '\f';
                        break;
                    }
                    case 'u': {
                        if (readPos + 3 >= len) {
                            throw new IllegalArgumentException("Invalid escaped char in [" + s + "]");
                        }
                        c = (char)Integer.parseInt(s.substring(readPos, readPos + 4), 16);
                        readPos += 4;
                        break;
                    }
                }
            }
            this.out[writePos++] = c;
        }
        return new String(this.out, 0, writePos);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
    
    static {
        MappingCharFilterFactory.p = Pattern.compile("\"(.*)\"\\s*=>\\s*\"(.*)\"\\s*$");
    }
}
