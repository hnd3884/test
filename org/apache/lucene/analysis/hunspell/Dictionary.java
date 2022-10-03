package org.apache.lucene.analysis.hunspell;

import org.apache.lucene.util.RamUsageEstimator;
import java.util.Comparator;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.automaton.RegExp;
import java.util.Locale;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.IntsRefBuilder;
import java.util.Iterator;
import java.util.Arrays;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.TreeMap;
import java.nio.charset.CharsetDecoder;
import java.io.OutputStream;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.IntSequenceOutputs;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.OfflineSorter;
import java.util.List;
import java.text.ParseException;
import java.io.IOException;
import java.util.Collections;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.lucene.util.CharsRef;
import java.nio.file.Path;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.automaton.CharacterRunAutomaton;
import java.util.ArrayList;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.FST;

public class Dictionary
{
    static final char[] NOFLAGS;
    private static final String ALIAS_KEY = "AF";
    private static final String MORPH_ALIAS_KEY = "AM";
    private static final String PREFIX_KEY = "PFX";
    private static final String SUFFIX_KEY = "SFX";
    private static final String FLAG_KEY = "FLAG";
    private static final String COMPLEXPREFIXES_KEY = "COMPLEXPREFIXES";
    private static final String CIRCUMFIX_KEY = "CIRCUMFIX";
    private static final String IGNORE_KEY = "IGNORE";
    private static final String ICONV_KEY = "ICONV";
    private static final String OCONV_KEY = "OCONV";
    private static final String FULLSTRIP_KEY = "FULLSTRIP";
    private static final String LANG_KEY = "LANG";
    private static final String KEEPCASE_KEY = "KEEPCASE";
    private static final String NEEDAFFIX_KEY = "NEEDAFFIX";
    private static final String PSEUDOROOT_KEY = "PSEUDOROOT";
    private static final String ONLYINCOMPOUND_KEY = "ONLYINCOMPOUND";
    private static final String NUM_FLAG_TYPE = "num";
    private static final String UTF8_FLAG_TYPE = "UTF-8";
    private static final String LONG_FLAG_TYPE = "long";
    private static final String PREFIX_CONDITION_REGEX_PATTERN = "%s.*";
    private static final String SUFFIX_CONDITION_REGEX_PATTERN = ".*%s";
    FST<IntsRef> prefixes;
    FST<IntsRef> suffixes;
    ArrayList<CharacterRunAutomaton> patterns;
    FST<IntsRef> words;
    BytesRefHash flagLookup;
    char[] stripData;
    int[] stripOffsets;
    byte[] affixData;
    private int currentAffix;
    private FlagParsingStrategy flagParsingStrategy;
    private String[] aliases;
    private int aliasCount;
    private String[] morphAliases;
    private int morphAliasCount;
    private String[] stemExceptions;
    private int stemExceptionCount;
    boolean hasStemExceptions;
    private final Path tempDir;
    boolean ignoreCase;
    boolean complexPrefixes;
    boolean twoStageAffix;
    int circumfix;
    int keepcase;
    int needaffix;
    int onlyincompound;
    private char[] ignore;
    FST<CharsRef> iconv;
    FST<CharsRef> oconv;
    boolean needsInputCleaning;
    boolean needsOutputCleaning;
    boolean fullStrip;
    String language;
    boolean alternateCasing;
    static final Pattern ENCODING_PATTERN;
    static final Map<String, String> CHARSET_ALIASES;
    final char FLAG_SEPARATOR = '\u001f';
    final char MORPH_SEPARATOR = '\u001e';
    
    public Dictionary(final InputStream affix, final InputStream dictionary) throws IOException, ParseException {
        this(affix, Collections.singletonList(dictionary), false);
    }
    
    public Dictionary(final InputStream affix, final List<InputStream> dictionaries, final boolean ignoreCase) throws IOException, ParseException {
        this.patterns = new ArrayList<CharacterRunAutomaton>();
        this.flagLookup = new BytesRefHash();
        this.affixData = new byte[64];
        this.currentAffix = 0;
        this.flagParsingStrategy = new SimpleFlagParsingStrategy();
        this.aliasCount = 0;
        this.morphAliasCount = 0;
        this.stemExceptions = new String[8];
        this.stemExceptionCount = 0;
        this.tempDir = OfflineSorter.getDefaultTempDir();
        this.circumfix = -1;
        this.keepcase = -1;
        this.needaffix = -1;
        this.onlyincompound = -1;
        this.ignoreCase = ignoreCase;
        this.needsInputCleaning = ignoreCase;
        this.needsOutputCleaning = false;
        this.flagLookup.add(new BytesRef());
        final Path aff = Files.createTempFile(this.tempDir, "affix", "aff", (FileAttribute<?>[])new FileAttribute[0]);
        final OutputStream out = new BufferedOutputStream(Files.newOutputStream(aff, new OpenOption[0]));
        InputStream aff2 = null;
        InputStream aff3 = null;
        boolean success = false;
        try {
            final byte[] buffer = new byte[8192];
            int len;
            while ((len = affix.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            aff2 = new BufferedInputStream(Files.newInputStream(aff, new OpenOption[0]));
            final String encoding = getDictionaryEncoding(aff2);
            final CharsetDecoder decoder = this.getJavaEncoding(encoding);
            aff3 = new BufferedInputStream(Files.newInputStream(aff, new OpenOption[0]));
            this.readAffixFile(aff3, decoder);
            final IntSequenceOutputs o = IntSequenceOutputs.getSingleton();
            final Builder<IntsRef> b = (Builder<IntsRef>)new Builder(FST.INPUT_TYPE.BYTE4, (Outputs)o);
            this.readDictionaryFiles(dictionaries, decoder, b);
            this.words = (FST<IntsRef>)b.finish();
            this.aliases = null;
            this.morphAliases = null;
            success = true;
        }
        finally {
            IOUtils.closeWhileHandlingException(new Closeable[] { out, aff2, aff3 });
            if (success) {
                Files.delete(aff);
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { aff });
            }
        }
    }
    
    IntsRef lookupWord(final char[] word, final int offset, final int length) {
        return this.lookup(this.words, word, offset, length);
    }
    
    IntsRef lookupPrefix(final char[] word, final int offset, final int length) {
        return this.lookup(this.prefixes, word, offset, length);
    }
    
    IntsRef lookupSuffix(final char[] word, final int offset, final int length) {
        return this.lookup(this.suffixes, word, offset, length);
    }
    
    IntsRef lookup(final FST<IntsRef> fst, final char[] word, final int offset, final int length) {
        if (fst == null) {
            return null;
        }
        final FST.BytesReader bytesReader = fst.getBytesReader();
        final FST.Arc<IntsRef> arc = (FST.Arc<IntsRef>)fst.getFirstArc(new FST.Arc());
        IntsRef output;
        final IntsRef NO_OUTPUT = output = (IntsRef)fst.outputs.getNoOutput();
        final int l = offset + length;
        try {
            for (int i = offset, cp = 0; i < l; i += Character.charCount(cp)) {
                cp = Character.codePointAt(word, i, l);
                if (fst.findTargetArc(cp, (FST.Arc)arc, (FST.Arc)arc, bytesReader) == null) {
                    return null;
                }
                if (arc.output != NO_OUTPUT) {
                    output = (IntsRef)fst.outputs.add((Object)output, arc.output);
                }
            }
            if (fst.findTargetArc(-1, (FST.Arc)arc, (FST.Arc)arc, bytesReader) == null) {
                return null;
            }
            if (arc.output != NO_OUTPUT) {
                return (IntsRef)fst.outputs.add((Object)output, arc.output);
            }
            return output;
        }
        catch (final IOException bogus) {
            throw new RuntimeException(bogus);
        }
    }
    
    private void readAffixFile(final InputStream affixStream, final CharsetDecoder decoder) throws IOException, ParseException {
        final TreeMap<String, List<Integer>> prefixes = new TreeMap<String, List<Integer>>();
        final TreeMap<String, List<Integer>> suffixes = new TreeMap<String, List<Integer>>();
        final Map<String, Integer> seenPatterns = new HashMap<String, Integer>();
        seenPatterns.put(".*", 0);
        this.patterns.add(null);
        final Map<String, Integer> seenStrips = new LinkedHashMap<String, Integer>();
        seenStrips.put("", 0);
        final LineNumberReader reader = new LineNumberReader(new InputStreamReader(affixStream, decoder));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (reader.getLineNumber() == 1 && line.startsWith("\ufeff")) {
                line = line.substring(1);
            }
            if (line.startsWith("AF")) {
                this.parseAlias(line);
            }
            else if (line.startsWith("AM")) {
                this.parseMorphAlias(line);
            }
            else if (line.startsWith("PFX")) {
                this.parseAffix(prefixes, line, reader, "%s.*", seenPatterns, seenStrips);
            }
            else if (line.startsWith("SFX")) {
                this.parseAffix(suffixes, line, reader, ".*%s", seenPatterns, seenStrips);
            }
            else if (line.startsWith("FLAG")) {
                this.flagParsingStrategy = getFlagParsingStrategy(line);
            }
            else if (line.equals("COMPLEXPREFIXES")) {
                this.complexPrefixes = true;
            }
            else if (line.startsWith("CIRCUMFIX")) {
                final String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new ParseException("Illegal CIRCUMFIX declaration", reader.getLineNumber());
                }
                this.circumfix = this.flagParsingStrategy.parseFlag(parts[1]);
            }
            else if (line.startsWith("KEEPCASE")) {
                final String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new ParseException("Illegal KEEPCASE declaration", reader.getLineNumber());
                }
                this.keepcase = this.flagParsingStrategy.parseFlag(parts[1]);
            }
            else if (line.startsWith("NEEDAFFIX") || line.startsWith("PSEUDOROOT")) {
                final String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new ParseException("Illegal NEEDAFFIX declaration", reader.getLineNumber());
                }
                this.needaffix = this.flagParsingStrategy.parseFlag(parts[1]);
            }
            else if (line.startsWith("ONLYINCOMPOUND")) {
                final String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new ParseException("Illegal ONLYINCOMPOUND declaration", reader.getLineNumber());
                }
                this.onlyincompound = this.flagParsingStrategy.parseFlag(parts[1]);
            }
            else if (line.startsWith("IGNORE")) {
                final String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new ParseException("Illegal IGNORE declaration", reader.getLineNumber());
                }
                Arrays.sort(this.ignore = parts[1].toCharArray());
                this.needsInputCleaning = true;
            }
            else if (line.startsWith("ICONV") || line.startsWith("OCONV")) {
                final String[] parts = line.split("\\s+");
                final String type = parts[0];
                if (parts.length != 2) {
                    throw new ParseException("Illegal " + type + " declaration", reader.getLineNumber());
                }
                final int num = Integer.parseInt(parts[1]);
                final FST<CharsRef> res = this.parseConversions(reader, num);
                if (type.equals("ICONV")) {
                    this.iconv = res;
                    this.needsInputCleaning |= (this.iconv != null);
                }
                else {
                    this.oconv = res;
                    this.needsOutputCleaning |= (this.oconv != null);
                }
            }
            else if (line.startsWith("FULLSTRIP")) {
                this.fullStrip = true;
            }
            else {
                if (!line.startsWith("LANG")) {
                    continue;
                }
                this.language = line.substring("LANG".length()).trim();
                this.alternateCasing = ("tr_TR".equals(this.language) || "az_AZ".equals(this.language));
            }
        }
        this.prefixes = this.affixFST(prefixes);
        this.suffixes = this.affixFST(suffixes);
        int totalChars = 0;
        for (final String strip : seenStrips.keySet()) {
            totalChars += strip.length();
        }
        this.stripData = new char[totalChars];
        this.stripOffsets = new int[seenStrips.size() + 1];
        int currentOffset = 0;
        int currentIndex = 0;
        for (final String strip2 : seenStrips.keySet()) {
            this.stripOffsets[currentIndex++] = currentOffset;
            strip2.getChars(0, strip2.length(), this.stripData, currentOffset);
            currentOffset += strip2.length();
        }
        assert currentIndex == seenStrips.size();
        this.stripOffsets[currentIndex] = currentOffset;
    }
    
    private FST<IntsRef> affixFST(final TreeMap<String, List<Integer>> affixes) throws IOException {
        final IntSequenceOutputs outputs = IntSequenceOutputs.getSingleton();
        final Builder<IntsRef> builder = (Builder<IntsRef>)new Builder(FST.INPUT_TYPE.BYTE4, (Outputs)outputs);
        final IntsRefBuilder scratch = new IntsRefBuilder();
        for (final Map.Entry<String, List<Integer>> entry : affixes.entrySet()) {
            Util.toUTF32((CharSequence)entry.getKey(), scratch);
            final List<Integer> entries = entry.getValue();
            final IntsRef output = new IntsRef(entries.size());
            for (final Integer c : entries) {
                output.ints[output.length++] = c;
            }
            builder.add(scratch.get(), (Object)output);
        }
        return (FST<IntsRef>)builder.finish();
    }
    
    static String escapeDash(final String re) {
        final StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < re.length(); ++i) {
            final char c = re.charAt(i);
            if (c == '-') {
                escaped.append("\\-");
            }
            else {
                escaped.append(c);
                if (c == '\\' && i + 1 < re.length()) {
                    escaped.append(re.charAt(i + 1));
                    ++i;
                }
            }
        }
        return escaped.toString();
    }
    
    private void parseAffix(final TreeMap<String, List<Integer>> affixes, final String header, final LineNumberReader reader, final String conditionPattern, final Map<String, Integer> seenPatterns, final Map<String, Integer> seenStrips) throws IOException, ParseException {
        final BytesRefBuilder scratch = new BytesRefBuilder();
        final StringBuilder sb = new StringBuilder();
        final String[] args = header.split("\\s+");
        final boolean crossProduct = args[2].equals("Y");
        final boolean isSuffix = conditionPattern == ".*%s";
        final int numLines = Integer.parseInt(args[3]);
        this.affixData = ArrayUtil.grow(this.affixData, (this.currentAffix << 3) + (numLines << 3));
        final ByteArrayDataOutput affixWriter = new ByteArrayDataOutput(this.affixData, this.currentAffix << 3, numLines << 3);
        for (int i = 0; i < numLines; ++i) {
            assert affixWriter.getPosition() == this.currentAffix << 3;
            final String line = reader.readLine();
            final String[] ruleArgs = line.split("\\s+");
            if (ruleArgs.length < 4) {
                throw new ParseException("The affix file contains a rule with less than four elements: " + line, reader.getLineNumber());
            }
            final char flag = this.flagParsingStrategy.parseFlag(ruleArgs[1]);
            final String strip = ruleArgs[2].equals("0") ? "" : ruleArgs[2];
            String affixArg = ruleArgs[3];
            char[] appendFlags = null;
            final int flagSep = affixArg.lastIndexOf(47);
            if (flagSep != -1) {
                String flagPart = affixArg.substring(flagSep + 1);
                affixArg = affixArg.substring(0, flagSep);
                if (this.aliasCount > 0) {
                    flagPart = this.getAliasValue(Integer.parseInt(flagPart));
                }
                appendFlags = this.flagParsingStrategy.parseFlags(flagPart);
                Arrays.sort(appendFlags);
                this.twoStageAffix = true;
            }
            if ("0".equals(affixArg)) {
                affixArg = "";
            }
            String condition = (ruleArgs.length > 4) ? ruleArgs[4] : ".";
            if (condition.startsWith("[") && condition.indexOf(93) == -1) {
                condition += "]";
            }
            if (condition.indexOf(45) >= 0) {
                condition = escapeDash(condition);
            }
            String regex;
            if (".".equals(condition)) {
                regex = ".*";
            }
            else if (condition.equals(strip)) {
                regex = ".*";
            }
            else {
                regex = String.format(Locale.ROOT, conditionPattern, condition);
            }
            Integer patternIndex = seenPatterns.get(regex);
            if (patternIndex == null) {
                patternIndex = this.patterns.size();
                if (patternIndex > 32767) {
                    throw new UnsupportedOperationException("Too many patterns, please report this to dev@lucene.apache.org");
                }
                seenPatterns.put(regex, patternIndex);
                final CharacterRunAutomaton pattern = new CharacterRunAutomaton(new RegExp(regex, 0).toAutomaton());
                this.patterns.add(pattern);
            }
            Integer stripOrd = seenStrips.get(strip);
            if (stripOrd == null) {
                stripOrd = seenStrips.size();
                seenStrips.put(strip, stripOrd);
                if (stripOrd > 65535) {
                    throw new UnsupportedOperationException("Too many unique strips, please report this to dev@lucene.apache.org");
                }
            }
            if (appendFlags == null) {
                appendFlags = Dictionary.NOFLAGS;
            }
            encodeFlags(scratch, appendFlags);
            int appendFlagsOrd = this.flagLookup.add(scratch.get());
            if (appendFlagsOrd < 0) {
                appendFlagsOrd = -appendFlagsOrd - 1;
            }
            else if (appendFlagsOrd > 32767) {
                throw new UnsupportedOperationException("Too many unique append flags, please report this to dev@lucene.apache.org");
            }
            affixWriter.writeShort((short)flag);
            affixWriter.writeShort((short)(int)stripOrd);
            final int patternOrd = patternIndex << 1 | (crossProduct ? 1 : 0);
            affixWriter.writeShort((short)patternOrd);
            affixWriter.writeShort((short)appendFlagsOrd);
            if (this.needsInputCleaning) {
                final CharSequence cleaned = this.cleanInput(affixArg, sb);
                affixArg = cleaned.toString();
            }
            if (isSuffix) {
                affixArg = new StringBuilder(affixArg).reverse().toString();
            }
            List<Integer> list = affixes.get(affixArg);
            if (list == null) {
                list = new ArrayList<Integer>();
                affixes.put(affixArg, list);
            }
            list.add(this.currentAffix);
            ++this.currentAffix;
        }
    }
    
    private FST<CharsRef> parseConversions(final LineNumberReader reader, final int num) throws IOException, ParseException {
        final Map<String, String> mappings = new TreeMap<String, String>();
        for (int i = 0; i < num; ++i) {
            final String line = reader.readLine();
            final String[] parts = line.split("\\s+");
            if (parts.length != 3) {
                throw new ParseException("invalid syntax: " + line, reader.getLineNumber());
            }
            if (mappings.put(parts[1], parts[2]) != null) {
                throw new IllegalStateException("duplicate mapping specified for: " + parts[1]);
            }
        }
        final Outputs<CharsRef> outputs = (Outputs<CharsRef>)CharSequenceOutputs.getSingleton();
        final Builder<CharsRef> builder = (Builder<CharsRef>)new Builder(FST.INPUT_TYPE.BYTE2, (Outputs)outputs);
        final IntsRefBuilder scratchInts = new IntsRefBuilder();
        for (final Map.Entry<String, String> entry : mappings.entrySet()) {
            Util.toUTF16((CharSequence)entry.getKey(), scratchInts);
            builder.add(scratchInts.get(), (Object)new CharsRef((String)entry.getValue()));
        }
        return (FST<CharsRef>)builder.finish();
    }
    
    static String getDictionaryEncoding(final InputStream affix) throws IOException, ParseException {
        final StringBuilder encoding = new StringBuilder();
        while (true) {
            encoding.setLength(0);
            int ch;
            while ((ch = affix.read()) >= 0 && ch != 10) {
                if (ch != 13) {
                    encoding.append((char)ch);
                }
            }
            if (encoding.length() == 0 || encoding.charAt(0) == '#' || encoding.toString().trim().length() == 0) {
                if (ch < 0) {
                    throw new ParseException("Unexpected end of affix file.", 0);
                }
                continue;
            }
            else {
                final Matcher matcher = Dictionary.ENCODING_PATTERN.matcher(encoding);
                if (matcher.find()) {
                    final int last = matcher.end();
                    return encoding.substring(last).trim();
                }
                continue;
            }
        }
    }
    
    private CharsetDecoder getJavaEncoding(String encoding) {
        if ("ISO8859-14".equals(encoding)) {
            return new ISO8859_14Decoder();
        }
        final String canon = Dictionary.CHARSET_ALIASES.get(encoding);
        if (canon != null) {
            encoding = canon;
        }
        final Charset charset = Charset.forName(encoding);
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE);
    }
    
    static FlagParsingStrategy getFlagParsingStrategy(final String flagLine) {
        final String[] parts = flagLine.split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Illegal FLAG specification: " + flagLine);
        }
        final String flagType = parts[1];
        if ("num".equals(flagType)) {
            return new NumFlagParsingStrategy();
        }
        if ("UTF-8".equals(flagType)) {
            return new SimpleFlagParsingStrategy();
        }
        if ("long".equals(flagType)) {
            return new DoubleASCIIFlagParsingStrategy();
        }
        throw new IllegalArgumentException("Unknown flag type: " + flagType);
    }
    
    String unescapeEntry(final String entry) {
        final StringBuilder sb = new StringBuilder();
        final int end = morphBoundary(entry);
        for (int i = 0; i < end; ++i) {
            final char ch = entry.charAt(i);
            if (ch == '\\' && i + 1 < entry.length()) {
                sb.append(entry.charAt(i + 1));
                ++i;
            }
            else if (ch == '/') {
                sb.append('\u001f');
            }
            else if (ch != '\u001e') {
                if (ch != '\u001f') {
                    sb.append(ch);
                }
            }
        }
        sb.append('\u001e');
        if (end < entry.length()) {
            for (int i = end; i < entry.length(); ++i) {
                final char c = entry.charAt(i);
                if (c != '\u001f') {
                    if (c != '\u001e') {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
    
    static int morphBoundary(final String line) {
        int end = indexOfSpaceOrTab(line, 0);
        if (end == -1) {
            return line.length();
        }
        while (end >= 0 && end < line.length() && line.charAt(end) != '\t' && (end + 3 >= line.length() || !Character.isLetter(line.charAt(end + 1)) || !Character.isLetter(line.charAt(end + 2)) || line.charAt(end + 3) != ':')) {
            end = indexOfSpaceOrTab(line, end + 1);
        }
        if (end == -1) {
            return line.length();
        }
        return end;
    }
    
    static int indexOfSpaceOrTab(final String text, final int start) {
        final int pos1 = text.indexOf(9, start);
        final int pos2 = text.indexOf(32, start);
        if (pos1 >= 0 && pos2 >= 0) {
            return Math.min(pos1, pos2);
        }
        return Math.max(pos1, pos2);
    }
    
    private void readDictionaryFiles(final List<InputStream> dictionaries, final CharsetDecoder decoder, final Builder<IntsRef> words) throws IOException {
        final BytesRefBuilder flagsScratch = new BytesRefBuilder();
        final IntsRefBuilder scratchInts = new IntsRefBuilder();
        final StringBuilder sb = new StringBuilder();
        final Path unsorted = Files.createTempFile(this.tempDir, "unsorted", "dat", (FileAttribute<?>[])new FileAttribute[0]);
        try (final OfflineSorter.ByteSequencesWriter writer = new OfflineSorter.ByteSequencesWriter(unsorted)) {
            for (final InputStream dictionary : dictionaries) {
                final BufferedReader lines = new BufferedReader(new InputStreamReader(dictionary, decoder));
                String line = lines.readLine();
                while ((line = lines.readLine()) != null) {
                    if (!line.isEmpty() && line.charAt(0) != '/' && line.charAt(0) != '#') {
                        if (line.charAt(0) == '\t') {
                            continue;
                        }
                        line = this.unescapeEntry(line);
                        if (!this.hasStemExceptions) {
                            final int morphStart = line.indexOf(30);
                            if (morphStart >= 0 && morphStart < line.length()) {
                                this.hasStemExceptions = (this.parseStemException(line.substring(morphStart + 1)) != null);
                            }
                        }
                        if (this.needsInputCleaning) {
                            int flagSep = line.indexOf(31);
                            if (flagSep == -1) {
                                flagSep = line.indexOf(30);
                            }
                            if (flagSep == -1) {
                                final CharSequence cleansed = this.cleanInput(line, sb);
                                writer.write(cleansed.toString().getBytes(StandardCharsets.UTF_8));
                            }
                            else {
                                final String text = line.substring(0, flagSep);
                                final CharSequence cleansed2 = this.cleanInput(text, sb);
                                if (cleansed2 != sb) {
                                    sb.setLength(0);
                                    sb.append(cleansed2);
                                }
                                sb.append(line.substring(flagSep));
                                writer.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        else {
                            writer.write(line.getBytes(StandardCharsets.UTF_8));
                        }
                    }
                }
            }
        }
        final Path sorted = Files.createTempFile(this.tempDir, "sorted", "dat", (FileAttribute<?>[])new FileAttribute[0]);
        final OfflineSorter sorter = new OfflineSorter((Comparator)new Comparator<BytesRef>() {
            BytesRef scratch1 = new BytesRef();
            BytesRef scratch2 = new BytesRef();
            
            @Override
            public int compare(final BytesRef o1, final BytesRef o2) {
                this.scratch1.bytes = o1.bytes;
                this.scratch1.offset = o1.offset;
                this.scratch1.length = o1.length;
                for (int i = this.scratch1.length - 1; i >= 0; --i) {
                    if (this.scratch1.bytes[this.scratch1.offset + i] == 31 || this.scratch1.bytes[this.scratch1.offset + i] == 30) {
                        this.scratch1.length = i;
                        break;
                    }
                }
                this.scratch2.bytes = o2.bytes;
                this.scratch2.offset = o2.offset;
                this.scratch2.length = o2.length;
                for (int i = this.scratch2.length - 1; i >= 0; --i) {
                    if (this.scratch2.bytes[this.scratch2.offset + i] == 31 || this.scratch2.bytes[this.scratch2.offset + i] == 30) {
                        this.scratch2.length = i;
                        break;
                    }
                }
                final int cmp = this.scratch1.compareTo(this.scratch2);
                if (cmp == 0) {
                    return o1.compareTo(o2);
                }
                return cmp;
            }
        });
        boolean success = false;
        try {
            sorter.sort(unsorted, sorted);
            success = true;
        }
        finally {
            if (success) {
                Files.delete(unsorted);
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { unsorted });
            }
        }
        boolean success2 = false;
        final OfflineSorter.ByteSequencesReader reader = new OfflineSorter.ByteSequencesReader(sorted);
        try {
            final BytesRefBuilder scratchLine = new BytesRefBuilder();
            String currentEntry = null;
            IntsRefBuilder currentOrds = new IntsRefBuilder();
            while (reader.read(scratchLine)) {
                final String line2 = scratchLine.get().utf8ToString();
                final int flagSep2 = line2.indexOf(31);
                char[] wordForm;
                int end;
                String entry;
                if (flagSep2 == -1) {
                    wordForm = Dictionary.NOFLAGS;
                    end = line2.indexOf(30);
                    entry = line2.substring(0, end);
                }
                else {
                    end = line2.indexOf(30);
                    String flagPart = line2.substring(flagSep2 + 1, end);
                    if (this.aliasCount > 0) {
                        flagPart = this.getAliasValue(Integer.parseInt(flagPart));
                    }
                    wordForm = this.flagParsingStrategy.parseFlags(flagPart);
                    Arrays.sort(wordForm);
                    entry = line2.substring(0, flagSep2);
                }
                int stemExceptionID = 0;
                if (this.hasStemExceptions && end + 1 < line2.length()) {
                    final String stemException = this.parseStemException(line2.substring(end + 1));
                    if (stemException != null) {
                        if (this.stemExceptionCount == this.stemExceptions.length) {
                            final int newSize = ArrayUtil.oversize(this.stemExceptionCount + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
                            this.stemExceptions = Arrays.copyOf(this.stemExceptions, newSize);
                        }
                        stemExceptionID = this.stemExceptionCount + 1;
                        this.stemExceptions[this.stemExceptionCount++] = stemException;
                    }
                }
                final int cmp = (currentEntry == null) ? 1 : entry.compareTo(currentEntry);
                if (cmp < 0) {
                    throw new IllegalArgumentException("out of order: " + entry + " < " + currentEntry);
                }
                encodeFlags(flagsScratch, wordForm);
                int ord = this.flagLookup.add(flagsScratch.get());
                if (ord < 0) {
                    ord = -ord - 1;
                }
                if (cmp > 0 && currentEntry != null) {
                    Util.toUTF32((CharSequence)currentEntry, scratchInts);
                    words.add(scratchInts.get(), (Object)currentOrds.get());
                }
                if (cmp > 0 || currentEntry == null) {
                    currentEntry = entry;
                    currentOrds = new IntsRefBuilder();
                }
                if (this.hasStemExceptions) {
                    currentOrds.append(ord);
                    currentOrds.append(stemExceptionID);
                }
                else {
                    currentOrds.append(ord);
                }
            }
            Util.toUTF32((CharSequence)currentEntry, scratchInts);
            words.add(scratchInts.get(), (Object)currentOrds.get());
            success2 = true;
        }
        finally {
            IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)reader });
            if (success2) {
                Files.delete(sorted);
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { sorted });
            }
        }
    }
    
    static char[] decodeFlags(final BytesRef b) {
        if (b.length == 0) {
            return CharsRef.EMPTY_CHARS;
        }
        final int len = b.length >>> 1;
        final char[] flags = new char[len];
        int upto = 0;
        for (int end = b.offset + b.length, i = b.offset; i < end; i += 2) {
            flags[upto++] = (char)(b.bytes[i] << 8 | (b.bytes[i + 1] & 0xFF));
        }
        return flags;
    }
    
    static void encodeFlags(final BytesRefBuilder b, final char[] flags) {
        final int len = flags.length << 1;
        b.grow(len);
        b.clear();
        for (int i = 0; i < flags.length; ++i) {
            final int flag = flags[i];
            b.append((byte)(flag >> 8 & 0xFF));
            b.append((byte)(flag & 0xFF));
        }
    }
    
    private void parseAlias(final String line) {
        final String[] ruleArgs = line.split("\\s+");
        if (this.aliases == null) {
            final int count = Integer.parseInt(ruleArgs[1]);
            this.aliases = new String[count];
        }
        else {
            final String aliasValue = (ruleArgs.length == 1) ? "" : ruleArgs[1];
            this.aliases[this.aliasCount++] = aliasValue;
        }
    }
    
    private String getAliasValue(final int id) {
        try {
            return this.aliases[id - 1];
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Bad flag alias number:" + id, ex);
        }
    }
    
    String getStemException(final int id) {
        return this.stemExceptions[id - 1];
    }
    
    private void parseMorphAlias(final String line) {
        if (this.morphAliases == null) {
            final int count = Integer.parseInt(line.substring(3));
            this.morphAliases = new String[count];
        }
        else {
            final String arg = line.substring(2);
            this.morphAliases[this.morphAliasCount++] = arg;
        }
    }
    
    private String parseStemException(String morphData) {
        if (this.morphAliasCount > 0) {
            try {
                final int alias = Integer.parseInt(morphData.trim());
                morphData = this.morphAliases[alias - 1];
            }
            catch (final NumberFormatException ex) {}
        }
        int index = morphData.indexOf(" st:");
        if (index < 0) {
            index = morphData.indexOf("\tst:");
        }
        if (index >= 0) {
            int endIndex = indexOfSpaceOrTab(morphData, index + 1);
            if (endIndex < 0) {
                endIndex = morphData.length();
            }
            return morphData.substring(index + 4, endIndex);
        }
        return null;
    }
    
    static boolean hasFlag(final char[] flags, final char flag) {
        return Arrays.binarySearch(flags, flag) >= 0;
    }
    
    CharSequence cleanInput(final CharSequence input, final StringBuilder reuse) {
        reuse.setLength(0);
        for (int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);
            if (this.ignore == null || Arrays.binarySearch(this.ignore, ch) < 0) {
                if (this.ignoreCase && this.iconv == null) {
                    ch = this.caseFold(ch);
                }
                reuse.append(ch);
            }
        }
        if (this.iconv != null) {
            try {
                applyMappings(this.iconv, reuse);
            }
            catch (final IOException bogus) {
                throw new RuntimeException(bogus);
            }
            if (this.ignoreCase) {
                for (int i = 0; i < reuse.length(); ++i) {
                    reuse.setCharAt(i, this.caseFold(reuse.charAt(i)));
                }
            }
        }
        return reuse;
    }
    
    char caseFold(final char c) {
        if (!this.alternateCasing) {
            return Character.toLowerCase(c);
        }
        if (c == 'I') {
            return '\u0131';
        }
        if (c == '\u0130') {
            return 'i';
        }
        return Character.toLowerCase(c);
    }
    
    static void applyMappings(final FST<CharsRef> fst, final StringBuilder sb) throws IOException {
        final FST.BytesReader bytesReader = fst.getBytesReader();
        final FST.Arc<CharsRef> firstArc = (FST.Arc<CharsRef>)fst.getFirstArc(new FST.Arc());
        final CharsRef NO_OUTPUT = (CharsRef)fst.outputs.getNoOutput();
        final FST.Arc<CharsRef> arc = (FST.Arc<CharsRef>)new FST.Arc();
        for (int i = 0; i < sb.length(); ++i) {
            arc.copyFrom((FST.Arc)firstArc);
            CharsRef output = NO_OUTPUT;
            int longestMatch = -1;
            CharsRef longestOutput = null;
            for (int j = i; j < sb.length(); ++j) {
                final char ch = sb.charAt(j);
                if (fst.findTargetArc((int)ch, (FST.Arc)arc, (FST.Arc)arc, bytesReader) == null) {
                    break;
                }
                output = (CharsRef)fst.outputs.add((Object)output, arc.output);
                if (arc.isFinal()) {
                    longestOutput = (CharsRef)fst.outputs.add((Object)output, arc.nextFinalOutput);
                    longestMatch = j;
                }
            }
            if (longestMatch >= 0) {
                sb.delete(i, longestMatch + 1);
                sb.insert(i, (CharSequence)longestOutput);
                i += longestOutput.length - 1;
            }
        }
    }
    
    public boolean getIgnoreCase() {
        return this.ignoreCase;
    }
    
    static {
        NOFLAGS = new char[0];
        ENCODING_PATTERN = Pattern.compile("^(\u00ef»¿)?SET\\s+");
        final Map<String, String> m = new HashMap<String, String>();
        m.put("microsoft-cp1251", "windows-1251");
        m.put("TIS620-2533", "TIS-620");
        CHARSET_ALIASES = Collections.unmodifiableMap((Map<? extends String, ? extends String>)m);
    }
    
    abstract static class FlagParsingStrategy
    {
        char parseFlag(final String rawFlag) {
            final char[] flags = this.parseFlags(rawFlag);
            if (flags.length != 1) {
                throw new IllegalArgumentException("expected only one flag, got: " + rawFlag);
            }
            return flags[0];
        }
        
        abstract char[] parseFlags(final String p0);
    }
    
    private static class SimpleFlagParsingStrategy extends FlagParsingStrategy
    {
        public char[] parseFlags(final String rawFlags) {
            return rawFlags.toCharArray();
        }
    }
    
    private static class NumFlagParsingStrategy extends FlagParsingStrategy
    {
        public char[] parseFlags(final String rawFlags) {
            final String[] rawFlagParts = rawFlags.trim().split(",");
            char[] flags = new char[rawFlagParts.length];
            int upto = 0;
            for (int i = 0; i < rawFlagParts.length; ++i) {
                final String replacement = rawFlagParts[i].replaceAll("[^0-9]", "");
                if (!replacement.isEmpty()) {
                    flags[upto++] = (char)Integer.parseInt(replacement);
                }
            }
            if (upto < flags.length) {
                flags = Arrays.copyOf(flags, upto);
            }
            return flags;
        }
    }
    
    private static class DoubleASCIIFlagParsingStrategy extends FlagParsingStrategy
    {
        public char[] parseFlags(final String rawFlags) {
            if (rawFlags.length() == 0) {
                return new char[0];
            }
            final StringBuilder builder = new StringBuilder();
            if (rawFlags.length() % 2 == 1) {
                throw new IllegalArgumentException("Invalid flags (should be even number of characters): " + rawFlags);
            }
            for (int i = 0; i < rawFlags.length(); i += 2) {
                final char f1 = rawFlags.charAt(i);
                final char f2 = rawFlags.charAt(i + 1);
                if (f1 >= '\u0100' || f2 >= '\u0100') {
                    throw new IllegalArgumentException("Invalid flags (LONG flags must be double ASCII): " + rawFlags);
                }
                final char combined = (char)(f1 << 8 | f2);
                builder.append(combined);
            }
            final char[] flags = new char[builder.length()];
            builder.getChars(0, builder.length(), flags, 0);
            return flags;
        }
    }
}
