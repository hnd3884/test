package org.apache.lucene.analysis.util;

import java.util.ArrayList;
import java.nio.charset.CharsetDecoder;
import java.io.Reader;
import java.io.InputStream;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.core.StopFilter;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.apache.lucene.util.Version;
import java.util.Map;

public abstract class AbstractAnalysisFactory
{
    public static final String LUCENE_MATCH_VERSION_PARAM = "luceneMatchVersion";
    private final Map<String, String> originalArgs;
    protected final Version luceneMatchVersion;
    private boolean isExplicitLuceneMatchVersion;
    private static final Pattern ITEM_PATTERN;
    private static final String CLASS_NAME = "class";
    
    protected AbstractAnalysisFactory(final Map<String, String> args) {
        this.isExplicitLuceneMatchVersion = false;
        this.originalArgs = Collections.unmodifiableMap((Map<? extends String, ? extends String>)new HashMap<String, String>(args));
        final String version = this.get(args, "luceneMatchVersion");
        if (version == null) {
            this.luceneMatchVersion = Version.LATEST;
        }
        else {
            try {
                this.luceneMatchVersion = Version.parseLeniently(version);
            }
            catch (final ParseException pe) {
                throw new IllegalArgumentException(pe);
            }
        }
        args.remove("class");
    }
    
    public final Map<String, String> getOriginalArgs() {
        return this.originalArgs;
    }
    
    public final Version getLuceneMatchVersion() {
        return this.luceneMatchVersion;
    }
    
    public String require(final Map<String, String> args, final String name) {
        final String s = args.remove(name);
        if (s == null) {
            throw new IllegalArgumentException("Configuration Error: missing parameter '" + name + "'");
        }
        return s;
    }
    
    public String require(final Map<String, String> args, final String name, final Collection<String> allowedValues) {
        return this.require(args, name, allowedValues, true);
    }
    
    public String require(final Map<String, String> args, final String name, final Collection<String> allowedValues, final boolean caseSensitive) {
        final String s = args.remove(name);
        if (s == null) {
            throw new IllegalArgumentException("Configuration Error: missing parameter '" + name + "'");
        }
        for (final String allowedValue : allowedValues) {
            if (caseSensitive) {
                if (s.equals(allowedValue)) {
                    return s;
                }
                continue;
            }
            else {
                if (s.equalsIgnoreCase(allowedValue)) {
                    return s;
                }
                continue;
            }
        }
        throw new IllegalArgumentException("Configuration Error: '" + name + "' value must be one of " + allowedValues);
    }
    
    public String get(final Map<String, String> args, final String name) {
        return args.remove(name);
    }
    
    public String get(final Map<String, String> args, final String name, final String defaultVal) {
        final String s = args.remove(name);
        return (s == null) ? defaultVal : s;
    }
    
    public String get(final Map<String, String> args, final String name, final Collection<String> allowedValues) {
        return this.get(args, name, allowedValues, null);
    }
    
    public String get(final Map<String, String> args, final String name, final Collection<String> allowedValues, final String defaultVal) {
        return this.get(args, name, allowedValues, defaultVal, true);
    }
    
    public String get(final Map<String, String> args, final String name, final Collection<String> allowedValues, final String defaultVal, final boolean caseSensitive) {
        final String s = args.remove(name);
        if (s == null) {
            return defaultVal;
        }
        for (final String allowedValue : allowedValues) {
            if (caseSensitive) {
                if (s.equals(allowedValue)) {
                    return s;
                }
                continue;
            }
            else {
                if (s.equalsIgnoreCase(allowedValue)) {
                    return s;
                }
                continue;
            }
        }
        throw new IllegalArgumentException("Configuration Error: '" + name + "' value must be one of " + allowedValues);
    }
    
    protected final int requireInt(final Map<String, String> args, final String name) {
        return Integer.parseInt(this.require(args, name));
    }
    
    protected final int getInt(final Map<String, String> args, final String name, final int defaultVal) {
        final String s = args.remove(name);
        return (s == null) ? defaultVal : Integer.parseInt(s);
    }
    
    protected final boolean requireBoolean(final Map<String, String> args, final String name) {
        return Boolean.parseBoolean(this.require(args, name));
    }
    
    protected final boolean getBoolean(final Map<String, String> args, final String name, final boolean defaultVal) {
        final String s = args.remove(name);
        return (s == null) ? defaultVal : Boolean.parseBoolean(s);
    }
    
    protected final float requireFloat(final Map<String, String> args, final String name) {
        return Float.parseFloat(this.require(args, name));
    }
    
    protected final float getFloat(final Map<String, String> args, final String name, final float defaultVal) {
        final String s = args.remove(name);
        return (s == null) ? defaultVal : Float.parseFloat(s);
    }
    
    public char requireChar(final Map<String, String> args, final String name) {
        return this.require(args, name).charAt(0);
    }
    
    public char getChar(final Map<String, String> args, final String name, final char defaultValue) {
        final String s = args.remove(name);
        if (s == null) {
            return defaultValue;
        }
        if (s.length() != 1) {
            throw new IllegalArgumentException(name + " should be a char. \"" + s + "\" is invalid");
        }
        return s.charAt(0);
    }
    
    public Set<String> getSet(final Map<String, String> args, final String name) {
        final String s = args.remove(name);
        if (s == null) {
            return null;
        }
        Set<String> set = null;
        final Matcher matcher = AbstractAnalysisFactory.ITEM_PATTERN.matcher(s);
        if (matcher.find()) {
            set = new HashSet<String>();
            set.add(matcher.group(0));
            while (matcher.find()) {
                set.add(matcher.group(0));
            }
        }
        return set;
    }
    
    protected final Pattern getPattern(final Map<String, String> args, final String name) {
        try {
            return Pattern.compile(this.require(args, name));
        }
        catch (final PatternSyntaxException e) {
            throw new IllegalArgumentException("Configuration Error: '" + name + "' can not be parsed in " + this.getClass().getSimpleName(), e);
        }
    }
    
    protected final CharArraySet getWordSet(final ResourceLoader loader, final String wordFiles, final boolean ignoreCase) throws IOException {
        final List<String> files = this.splitFileNames(wordFiles);
        CharArraySet words = null;
        if (files.size() > 0) {
            words = new CharArraySet(files.size() * 10, ignoreCase);
            for (final String file : files) {
                final List<String> wlist = this.getLines(loader, file.trim());
                words.addAll(StopFilter.makeStopSet(wlist, ignoreCase));
            }
        }
        return words;
    }
    
    protected final List<String> getLines(final ResourceLoader loader, final String resource) throws IOException {
        return WordlistLoader.getLines(loader.openResource(resource), StandardCharsets.UTF_8);
    }
    
    protected final CharArraySet getSnowballWordSet(final ResourceLoader loader, final String wordFiles, final boolean ignoreCase) throws IOException {
        final List<String> files = this.splitFileNames(wordFiles);
        CharArraySet words = null;
        if (files.size() > 0) {
            words = new CharArraySet(files.size() * 10, ignoreCase);
            for (final String file : files) {
                InputStream stream = null;
                Reader reader = null;
                try {
                    stream = loader.openResource(file.trim());
                    final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
                    reader = new InputStreamReader(stream, decoder);
                    WordlistLoader.getSnowballWordSet(reader, words);
                }
                finally {
                    IOUtils.closeWhileHandlingException(new Closeable[] { reader, stream });
                }
            }
        }
        return words;
    }
    
    protected final List<String> splitFileNames(final String fileNames) {
        if (fileNames == null) {
            return Collections.emptyList();
        }
        final List<String> result = new ArrayList<String>();
        for (final String file : fileNames.split("(?<!\\\\),")) {
            result.add(file.replaceAll("\\\\(?=,)", ""));
        }
        return result;
    }
    
    public String getClassArg() {
        if (null != this.originalArgs) {
            final String className = this.originalArgs.get("class");
            if (null != className) {
                return className;
            }
        }
        return this.getClass().getName();
    }
    
    public boolean isExplicitLuceneMatchVersion() {
        return this.isExplicitLuceneMatchVersion;
    }
    
    public void setExplicitLuceneMatchVersion(final boolean isExplicitLuceneMatchVersion) {
        this.isExplicitLuceneMatchVersion = isExplicitLuceneMatchVersion;
    }
    
    static {
        ITEM_PATTERN = Pattern.compile("[^,\\s]+");
    }
}
