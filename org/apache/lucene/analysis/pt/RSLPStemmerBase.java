package org.apache.lucene.analysis.pt;

import java.util.Collection;
import java.util.Arrays;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StemmerUtil;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class RSLPStemmerBase
{
    private static final Pattern headerPattern;
    private static final Pattern stripPattern;
    private static final Pattern repPattern;
    private static final Pattern excPattern;
    
    protected static Map<String, Step> parse(final Class<? extends RSLPStemmerBase> clazz, final String resource) {
        try {
            final InputStream is = clazz.getResourceAsStream(resource);
            final LineNumberReader r = new LineNumberReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            final Map<String, Step> steps = new HashMap<String, Step>();
            String step;
            while ((step = readLine(r)) != null) {
                final Step s = parseStep(r, step);
                steps.put(s.name, s);
            }
            r.close();
            return steps;
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Step parseStep(final LineNumberReader r, final String header) throws IOException {
        final Matcher matcher = RSLPStemmerBase.headerPattern.matcher(header);
        if (!matcher.find()) {
            throw new RuntimeException("Illegal Step header specified at line " + r.getLineNumber());
        }
        assert matcher.groupCount() == 4;
        final String name = matcher.group(1);
        final int min = Integer.parseInt(matcher.group(2));
        final int type = Integer.parseInt(matcher.group(3));
        final String[] suffixes = parseList(matcher.group(4));
        final Rule[] rules = parseRules(r, type);
        return new Step(name, rules, min, suffixes);
    }
    
    private static Rule[] parseRules(final LineNumberReader r, final int type) throws IOException {
        final List<Rule> rules = new ArrayList<Rule>();
        String line;
        while ((line = readLine(r)) != null) {
            Matcher matcher = RSLPStemmerBase.stripPattern.matcher(line);
            if (matcher.matches()) {
                rules.add(new Rule(matcher.group(1), Integer.parseInt(matcher.group(2)), ""));
            }
            else {
                matcher = RSLPStemmerBase.repPattern.matcher(line);
                if (matcher.matches()) {
                    rules.add(new Rule(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3)));
                }
                else {
                    matcher = RSLPStemmerBase.excPattern.matcher(line);
                    if (!matcher.matches()) {
                        throw new RuntimeException("Illegal Step rule specified at line " + r.getLineNumber());
                    }
                    if (type == 0) {
                        rules.add(new RuleWithSuffixExceptions(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), parseList(matcher.group(4))));
                    }
                    else {
                        rules.add(new RuleWithSetExceptions(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), parseList(matcher.group(4))));
                    }
                }
            }
            if (line.endsWith(";")) {
                return rules.toArray(new Rule[rules.size()]);
            }
        }
        return null;
    }
    
    private static String[] parseList(final String s) {
        if (s.length() == 0) {
            return null;
        }
        final String[] list = s.split(",");
        for (int i = 0; i < list.length; ++i) {
            list[i] = parseString(list[i].trim());
        }
        return list;
    }
    
    private static String parseString(final String s) {
        return s.substring(1, s.length() - 1);
    }
    
    private static String readLine(final LineNumberReader r) throws IOException {
        String line = null;
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                return line;
            }
        }
        return line;
    }
    
    static {
        headerPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*(0|1),\\s*\\{(.*)\\},\\s*$");
        stripPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+)\\s*\\}\\s*(,|(\\}\\s*;))$");
        repPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*\"([^\"]*)\"\\}\\s*(,|(\\}\\s*;))$");
        excPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*\"([^\"]*)\",\\s*\\{(.*)\\}\\s*\\}\\s*(,|(\\}\\s*;))$");
    }
    
    protected static class Rule
    {
        protected final char[] suffix;
        protected final char[] replacement;
        protected final int min;
        
        public Rule(final String suffix, final int min, final String replacement) {
            this.suffix = suffix.toCharArray();
            this.replacement = replacement.toCharArray();
            this.min = min;
        }
        
        public boolean matches(final char[] s, final int len) {
            return len - this.suffix.length >= this.min && StemmerUtil.endsWith(s, len, this.suffix);
        }
        
        public int replace(final char[] s, final int len) {
            if (this.replacement.length > 0) {
                System.arraycopy(this.replacement, 0, s, len - this.suffix.length, this.replacement.length);
            }
            return len - this.suffix.length + this.replacement.length;
        }
    }
    
    protected static class RuleWithSetExceptions extends Rule
    {
        protected final CharArraySet exceptions;
        
        public RuleWithSetExceptions(final String suffix, final int min, final String replacement, final String[] exceptions) {
            super(suffix, min, replacement);
            for (int i = 0; i < exceptions.length; ++i) {
                if (!exceptions[i].endsWith(suffix)) {
                    throw new RuntimeException("useless exception '" + exceptions[i] + "' does not end with '" + suffix + "'");
                }
            }
            this.exceptions = new CharArraySet(Arrays.asList(exceptions), false);
        }
        
        @Override
        public boolean matches(final char[] s, final int len) {
            return super.matches(s, len) && !this.exceptions.contains(s, 0, len);
        }
    }
    
    protected static class RuleWithSuffixExceptions extends Rule
    {
        protected final char[][] exceptions;
        
        public RuleWithSuffixExceptions(final String suffix, final int min, final String replacement, final String[] exceptions) {
            super(suffix, min, replacement);
            for (int i = 0; i < exceptions.length; ++i) {
                if (!exceptions[i].endsWith(suffix)) {
                    throw new RuntimeException("warning: useless exception '" + exceptions[i] + "' does not end with '" + suffix + "'");
                }
            }
            this.exceptions = new char[exceptions.length][];
            for (int i = 0; i < exceptions.length; ++i) {
                this.exceptions[i] = exceptions[i].toCharArray();
            }
        }
        
        @Override
        public boolean matches(final char[] s, final int len) {
            if (!super.matches(s, len)) {
                return false;
            }
            for (int i = 0; i < this.exceptions.length; ++i) {
                if (StemmerUtil.endsWith(s, len, this.exceptions[i])) {
                    return false;
                }
            }
            return true;
        }
    }
    
    protected static class Step
    {
        protected final String name;
        protected final Rule[] rules;
        protected final int min;
        protected final char[][] suffixes;
        
        public Step(final String name, final Rule[] rules, int min, final String[] suffixes) {
            this.name = name;
            this.rules = rules;
            if (min == 0) {
                min = Integer.MAX_VALUE;
                for (final Rule r : rules) {
                    min = Math.min(min, r.min + r.suffix.length);
                }
            }
            this.min = min;
            if (suffixes == null || suffixes.length == 0) {
                this.suffixes = null;
            }
            else {
                this.suffixes = new char[suffixes.length][];
                for (int i = 0; i < suffixes.length; ++i) {
                    this.suffixes[i] = suffixes[i].toCharArray();
                }
            }
        }
        
        public int apply(final char[] s, final int len) {
            if (len < this.min) {
                return len;
            }
            if (this.suffixes != null) {
                boolean found = false;
                for (int i = 0; i < this.suffixes.length; ++i) {
                    if (StemmerUtil.endsWith(s, len, this.suffixes[i])) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return len;
                }
            }
            for (int j = 0; j < this.rules.length; ++j) {
                if (this.rules[j].matches(s, len)) {
                    return this.rules[j].replace(s, len);
                }
            }
            return len;
        }
    }
}
