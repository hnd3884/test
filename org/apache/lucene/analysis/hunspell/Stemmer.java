package org.apache.lucene.analysis.hunspell;

import java.util.Arrays;
import org.apache.lucene.util.automaton.CharacterRunAutomaton;
import org.apache.lucene.util.fst.Outputs;
import java.util.Iterator;
import org.apache.lucene.analysis.util.CharArraySet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.CharsRef;
import java.util.List;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.BytesRef;

final class Stemmer
{
    private final Dictionary dictionary;
    private final BytesRef scratch;
    private final StringBuilder segment;
    private final ByteArrayDataInput affixReader;
    private final StringBuilder scratchSegment;
    private char[] scratchBuffer;
    private final int formStep;
    private char[] lowerBuffer;
    private char[] titleBuffer;
    private static final int EXACT_CASE = 0;
    private static final int TITLE_CASE = 1;
    private static final int UPPER_CASE = 2;
    final FST.BytesReader[] prefixReaders;
    final FST.Arc<IntsRef>[] prefixArcs;
    final FST.BytesReader[] suffixReaders;
    final FST.Arc<IntsRef>[] suffixArcs;
    
    public Stemmer(final Dictionary dictionary) {
        this.scratch = new BytesRef();
        this.segment = new StringBuilder();
        this.scratchSegment = new StringBuilder();
        this.scratchBuffer = new char[32];
        this.lowerBuffer = new char[8];
        this.titleBuffer = new char[8];
        this.prefixReaders = new FST.BytesReader[3];
        this.prefixArcs = (FST.Arc<IntsRef>[])new FST.Arc[3];
        this.suffixReaders = new FST.BytesReader[3];
        this.suffixArcs = (FST.Arc<IntsRef>[])new FST.Arc[3];
        this.dictionary = dictionary;
        this.affixReader = new ByteArrayDataInput(dictionary.affixData);
        for (int level = 0; level < 3; ++level) {
            if (dictionary.prefixes != null) {
                this.prefixArcs[level] = (FST.Arc<IntsRef>)new FST.Arc();
                this.prefixReaders[level] = dictionary.prefixes.getBytesReader();
            }
            if (dictionary.suffixes != null) {
                this.suffixArcs[level] = (FST.Arc<IntsRef>)new FST.Arc();
                this.suffixReaders[level] = dictionary.suffixes.getBytesReader();
            }
        }
        this.formStep = (dictionary.hasStemExceptions ? 2 : 1);
    }
    
    public List<CharsRef> stem(final String word) {
        return this.stem(word.toCharArray(), word.length());
    }
    
    public List<CharsRef> stem(char[] word, int length) {
        if (this.dictionary.needsInputCleaning) {
            this.scratchSegment.setLength(0);
            this.scratchSegment.append(word, 0, length);
            final CharSequence cleaned = this.dictionary.cleanInput(this.scratchSegment, this.segment);
            this.scratchBuffer = ArrayUtil.grow(this.scratchBuffer, cleaned.length());
            length = this.segment.length();
            this.segment.getChars(0, length, this.scratchBuffer, 0);
            word = this.scratchBuffer;
        }
        final int caseType = this.caseOf(word, length);
        if (caseType == 2) {
            this.caseFoldTitle(word, length);
            this.caseFoldLower(this.titleBuffer, length);
            final List<CharsRef> list = this.doStem(word, length, false);
            list.addAll(this.doStem(this.titleBuffer, length, true));
            list.addAll(this.doStem(this.lowerBuffer, length, true));
            return list;
        }
        if (caseType == 1) {
            this.caseFoldLower(word, length);
            final List<CharsRef> list = this.doStem(word, length, false);
            list.addAll(this.doStem(this.lowerBuffer, length, true));
            return list;
        }
        return this.doStem(word, length, false);
    }
    
    private int caseOf(final char[] word, final int length) {
        if (this.dictionary.ignoreCase || length == 0 || !Character.isUpperCase(word[0])) {
            return 0;
        }
        boolean seenUpper = false;
        boolean seenLower = false;
        for (int i = 1; i < length; ++i) {
            final boolean v = Character.isUpperCase(word[i]);
            seenUpper |= v;
            seenLower |= !v;
        }
        if (!seenLower) {
            return 2;
        }
        if (!seenUpper) {
            return 1;
        }
        return 0;
    }
    
    private void caseFoldTitle(final char[] word, final int length) {
        System.arraycopy(word, 0, this.titleBuffer = ArrayUtil.grow(this.titleBuffer, length), 0, length);
        for (int i = 1; i < length; ++i) {
            this.titleBuffer[i] = this.dictionary.caseFold(this.titleBuffer[i]);
        }
    }
    
    private void caseFoldLower(final char[] word, final int length) {
        System.arraycopy(word, 0, this.lowerBuffer = ArrayUtil.grow(this.lowerBuffer, length), 0, length);
        this.lowerBuffer[0] = this.dictionary.caseFold(this.lowerBuffer[0]);
    }
    
    private List<CharsRef> doStem(final char[] word, final int length, final boolean caseVariant) {
        final List<CharsRef> stems = new ArrayList<CharsRef>();
        final IntsRef forms = this.dictionary.lookupWord(word, 0, length);
        if (forms != null) {
            for (int i = 0; i < forms.length; i += this.formStep) {
                final boolean checkKeepCase = caseVariant && this.dictionary.keepcase != -1;
                final boolean checkNeedAffix = this.dictionary.needaffix != -1;
                final boolean checkOnlyInCompound = this.dictionary.onlyincompound != -1;
                if (checkKeepCase || checkNeedAffix || checkOnlyInCompound) {
                    this.dictionary.flagLookup.get(forms.ints[forms.offset + i], this.scratch);
                    final char[] wordFlags = Dictionary.decodeFlags(this.scratch);
                    if (checkKeepCase && Dictionary.hasFlag(wordFlags, (char)this.dictionary.keepcase)) {
                        continue;
                    }
                    if (checkNeedAffix && Dictionary.hasFlag(wordFlags, (char)this.dictionary.needaffix)) {
                        continue;
                    }
                    if (checkOnlyInCompound && Dictionary.hasFlag(wordFlags, (char)this.dictionary.onlyincompound)) {
                        continue;
                    }
                }
                stems.add(this.newStem(word, length, forms, i));
            }
        }
        try {
            final boolean v = stems.addAll(this.stem(word, length, -1, -1, -1, 0, true, true, false, false, caseVariant));
        }
        catch (final IOException bogus) {
            throw new RuntimeException(bogus);
        }
        return stems;
    }
    
    public List<CharsRef> uniqueStems(final char[] word, final int length) {
        final List<CharsRef> stems = this.stem(word, length);
        if (stems.size() < 2) {
            return stems;
        }
        final CharArraySet terms = new CharArraySet(8, this.dictionary.ignoreCase);
        final List<CharsRef> deduped = new ArrayList<CharsRef>();
        for (final CharsRef s : stems) {
            if (!terms.contains((CharSequence)s)) {
                deduped.add(s);
                terms.add((CharSequence)s);
            }
        }
        return deduped;
    }
    
    private CharsRef newStem(final char[] buffer, final int length, final IntsRef forms, final int formID) {
        String exception;
        if (this.dictionary.hasStemExceptions) {
            final int exceptionID = forms.ints[forms.offset + formID + 1];
            if (exceptionID > 0) {
                exception = this.dictionary.getStemException(exceptionID);
            }
            else {
                exception = null;
            }
        }
        else {
            exception = null;
        }
        if (this.dictionary.needsOutputCleaning) {
            this.scratchSegment.setLength(0);
            if (exception != null) {
                this.scratchSegment.append(exception);
            }
            else {
                this.scratchSegment.append(buffer, 0, length);
            }
            try {
                Dictionary.applyMappings(this.dictionary.oconv, this.scratchSegment);
            }
            catch (final IOException bogus) {
                throw new RuntimeException(bogus);
            }
            final char[] cleaned = new char[this.scratchSegment.length()];
            this.scratchSegment.getChars(0, cleaned.length, cleaned, 0);
            return new CharsRef(cleaned, 0, cleaned.length);
        }
        if (exception != null) {
            return new CharsRef(exception);
        }
        return new CharsRef(buffer, 0, length);
    }
    
    private List<CharsRef> stem(final char[] word, final int length, final int previous, final int prevFlag, final int prefixFlag, final int recursionDepth, final boolean doPrefix, final boolean doSuffix, final boolean previousWasPrefix, final boolean circumfix, final boolean caseVariant) throws IOException {
        final List<CharsRef> stems = new ArrayList<CharsRef>();
        if (doPrefix && this.dictionary.prefixes != null) {
            final FST<IntsRef> fst = this.dictionary.prefixes;
            final Outputs<IntsRef> outputs = (Outputs<IntsRef>)fst.outputs;
            final FST.BytesReader bytesReader = this.prefixReaders[recursionDepth];
            final FST.Arc<IntsRef> arc = this.prefixArcs[recursionDepth];
            fst.getFirstArc((FST.Arc)arc);
            IntsRef output;
            final IntsRef NO_OUTPUT = output = (IntsRef)outputs.getNoOutput();
            for (int limit = this.dictionary.fullStrip ? length : (length - 1), i = 0; i < limit; ++i) {
                if (i > 0) {
                    final int ch = word[i - 1];
                    if (fst.findTargetArc(ch, (FST.Arc)arc, (FST.Arc)arc, bytesReader) == null) {
                        break;
                    }
                    if (arc.output != NO_OUTPUT) {
                        output = (IntsRef)fst.outputs.add((Object)output, arc.output);
                    }
                }
                IntsRef prefixes = null;
                if (arc.isFinal()) {
                    prefixes = (IntsRef)fst.outputs.add((Object)output, arc.nextFinalOutput);
                    for (int j = 0; j < prefixes.length; ++j) {
                        final int prefix = prefixes.ints[prefixes.offset + j];
                        if (prefix != previous) {
                            this.affixReader.setPosition(8 * prefix);
                            final char flag = (char)(this.affixReader.readShort() & 0xFFFF);
                            final char stripOrd = (char)(this.affixReader.readShort() & 0xFFFF);
                            int condition = (char)(this.affixReader.readShort() & 0xFFFF);
                            final boolean crossProduct = (condition & 0x1) == 0x1;
                            condition >>>= 1;
                            final char append = (char)(this.affixReader.readShort() & 0xFFFF);
                            boolean compatible;
                            if (recursionDepth == 0) {
                                if (this.dictionary.onlyincompound == -1) {
                                    compatible = true;
                                }
                                else {
                                    this.dictionary.flagLookup.get((int)append, this.scratch);
                                    final char[] appendFlags = Dictionary.decodeFlags(this.scratch);
                                    compatible = !Dictionary.hasFlag(appendFlags, (char)this.dictionary.onlyincompound);
                                }
                            }
                            else if (crossProduct) {
                                this.dictionary.flagLookup.get((int)append, this.scratch);
                                final char[] appendFlags = Dictionary.decodeFlags(this.scratch);
                                assert prevFlag >= 0;
                                final boolean allowed = this.dictionary.onlyincompound == -1 || !Dictionary.hasFlag(appendFlags, (char)this.dictionary.onlyincompound);
                                compatible = (allowed && this.hasCrossCheckedFlag((char)prevFlag, appendFlags, false));
                            }
                            else {
                                compatible = false;
                            }
                            if (compatible) {
                                final int deAffixedStart = i;
                                final int deAffixedLength = length - deAffixedStart;
                                final int stripStart = this.dictionary.stripOffsets[stripOrd];
                                final int stripEnd = this.dictionary.stripOffsets[stripOrd + '\u0001'];
                                final int stripLength = stripEnd - stripStart;
                                if (this.checkCondition(condition, this.dictionary.stripData, stripStart, stripLength, word, deAffixedStart, deAffixedLength)) {
                                    final char[] strippedWord = new char[stripLength + deAffixedLength];
                                    System.arraycopy(this.dictionary.stripData, stripStart, strippedWord, 0, stripLength);
                                    System.arraycopy(word, deAffixedStart, strippedWord, stripLength, deAffixedLength);
                                    final List<CharsRef> stemList = this.applyAffix(strippedWord, strippedWord.length, prefix, -1, recursionDepth, true, circumfix, caseVariant);
                                    stems.addAll(stemList);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (doSuffix && this.dictionary.suffixes != null) {
            final FST<IntsRef> fst = this.dictionary.suffixes;
            final Outputs<IntsRef> outputs = (Outputs<IntsRef>)fst.outputs;
            final FST.BytesReader bytesReader = this.suffixReaders[recursionDepth];
            final FST.Arc<IntsRef> arc = this.suffixArcs[recursionDepth];
            fst.getFirstArc((FST.Arc)arc);
            IntsRef output;
            final IntsRef NO_OUTPUT = output = (IntsRef)outputs.getNoOutput();
            for (int limit = this.dictionary.fullStrip ? 0 : 1, i = length; i >= limit; --i) {
                if (i < length) {
                    final int ch = word[i];
                    if (fst.findTargetArc(ch, (FST.Arc)arc, (FST.Arc)arc, bytesReader) == null) {
                        break;
                    }
                    if (arc.output != NO_OUTPUT) {
                        output = (IntsRef)fst.outputs.add((Object)output, arc.output);
                    }
                }
                IntsRef suffixes = null;
                if (arc.isFinal()) {
                    suffixes = (IntsRef)fst.outputs.add((Object)output, arc.nextFinalOutput);
                    for (int j = 0; j < suffixes.length; ++j) {
                        final int suffix = suffixes.ints[suffixes.offset + j];
                        if (suffix != previous) {
                            this.affixReader.setPosition(8 * suffix);
                            final char flag = (char)(this.affixReader.readShort() & 0xFFFF);
                            final char stripOrd = (char)(this.affixReader.readShort() & 0xFFFF);
                            int condition = (char)(this.affixReader.readShort() & 0xFFFF);
                            final boolean crossProduct = (condition & 0x1) == 0x1;
                            condition >>>= 1;
                            final char append = (char)(this.affixReader.readShort() & 0xFFFF);
                            boolean compatible;
                            if (recursionDepth == 0) {
                                if (this.dictionary.onlyincompound == -1) {
                                    compatible = true;
                                }
                                else {
                                    this.dictionary.flagLookup.get((int)append, this.scratch);
                                    final char[] appendFlags = Dictionary.decodeFlags(this.scratch);
                                    compatible = !Dictionary.hasFlag(appendFlags, (char)this.dictionary.onlyincompound);
                                }
                            }
                            else if (crossProduct) {
                                this.dictionary.flagLookup.get((int)append, this.scratch);
                                final char[] appendFlags = Dictionary.decodeFlags(this.scratch);
                                assert prevFlag >= 0;
                                final boolean allowed = this.dictionary.onlyincompound == -1 || !Dictionary.hasFlag(appendFlags, (char)this.dictionary.onlyincompound);
                                compatible = (allowed && this.hasCrossCheckedFlag((char)prevFlag, appendFlags, previousWasPrefix));
                            }
                            else {
                                compatible = false;
                            }
                            if (compatible) {
                                final int appendLength = length - i;
                                final int deAffixedLength = length - appendLength;
                                final int stripStart = this.dictionary.stripOffsets[stripOrd];
                                final int stripEnd = this.dictionary.stripOffsets[stripOrd + '\u0001'];
                                final int stripLength = stripEnd - stripStart;
                                if (this.checkCondition(condition, word, 0, deAffixedLength, this.dictionary.stripData, stripStart, stripLength)) {
                                    final char[] strippedWord = new char[stripLength + deAffixedLength];
                                    System.arraycopy(word, 0, strippedWord, 0, deAffixedLength);
                                    System.arraycopy(this.dictionary.stripData, stripStart, strippedWord, deAffixedLength, stripLength);
                                    final List<CharsRef> stemList = this.applyAffix(strippedWord, strippedWord.length, suffix, prefixFlag, recursionDepth, false, circumfix, caseVariant);
                                    stems.addAll(stemList);
                                }
                            }
                        }
                    }
                }
            }
        }
        return stems;
    }
    
    private boolean checkCondition(final int condition, final char[] c1, final int c1off, final int c1len, final char[] c2, final int c2off, final int c2len) {
        if (condition != 0) {
            final CharacterRunAutomaton pattern = this.dictionary.patterns.get(condition);
            int state = pattern.getInitialState();
            for (int i = c1off; i < c1off + c1len; ++i) {
                state = pattern.step(state, (int)c1[i]);
                if (state == -1) {
                    return false;
                }
            }
            for (int i = c2off; i < c2off + c2len; ++i) {
                state = pattern.step(state, (int)c2[i]);
                if (state == -1) {
                    return false;
                }
            }
            return pattern.isAccept(state);
        }
        return true;
    }
    
    List<CharsRef> applyAffix(final char[] strippedWord, final int length, final int affix, final int prefixFlag, int recursionDepth, final boolean prefix, boolean circumfix, final boolean caseVariant) throws IOException {
        this.affixReader.setPosition(8 * affix);
        final char flag = (char)(this.affixReader.readShort() & 0xFFFF);
        this.affixReader.skipBytes(2L);
        int condition = (char)(this.affixReader.readShort() & 0xFFFF);
        final boolean crossProduct = (condition & 0x1) == 0x1;
        condition >>>= 1;
        final char append = (char)(this.affixReader.readShort() & 0xFFFF);
        final List<CharsRef> stems = new ArrayList<CharsRef>();
        final IntsRef forms = this.dictionary.lookupWord(strippedWord, 0, length);
        if (forms != null) {
            for (int i = 0; i < forms.length; i += this.formStep) {
                this.dictionary.flagLookup.get(forms.ints[forms.offset + i], this.scratch);
                final char[] wordFlags = Dictionary.decodeFlags(this.scratch);
                if (Dictionary.hasFlag(wordFlags, flag)) {
                    final boolean chainedPrefix = this.dictionary.complexPrefixes && recursionDepth == 1 && prefix;
                    if (!chainedPrefix && prefixFlag >= 0 && !Dictionary.hasFlag(wordFlags, (char)prefixFlag)) {
                        this.dictionary.flagLookup.get((int)append, this.scratch);
                        final char[] appendFlags = Dictionary.decodeFlags(this.scratch);
                        if (!this.hasCrossCheckedFlag((char)prefixFlag, appendFlags, false)) {
                            continue;
                        }
                    }
                    if (this.dictionary.circumfix != -1) {
                        this.dictionary.flagLookup.get((int)append, this.scratch);
                        final char[] appendFlags = Dictionary.decodeFlags(this.scratch);
                        final boolean suffixCircumfix = Dictionary.hasFlag(appendFlags, (char)this.dictionary.circumfix);
                        if (circumfix != suffixCircumfix) {
                            continue;
                        }
                    }
                    if (!caseVariant || this.dictionary.keepcase == -1 || !Dictionary.hasFlag(wordFlags, (char)this.dictionary.keepcase)) {
                        if (this.dictionary.onlyincompound == -1 || !Dictionary.hasFlag(wordFlags, (char)this.dictionary.onlyincompound)) {
                            stems.add(this.newStem(strippedWord, length, forms, i));
                        }
                    }
                }
            }
        }
        if (this.dictionary.circumfix != -1 && !circumfix && prefix) {
            this.dictionary.flagLookup.get((int)append, this.scratch);
            final char[] appendFlags2 = Dictionary.decodeFlags(this.scratch);
            circumfix = Dictionary.hasFlag(appendFlags2, (char)this.dictionary.circumfix);
        }
        if (crossProduct) {
            if (recursionDepth == 0) {
                if (prefix) {
                    stems.addAll(this.stem(strippedWord, length, affix, flag, flag, ++recursionDepth, this.dictionary.complexPrefixes && this.dictionary.twoStageAffix, true, true, circumfix, caseVariant));
                }
                else if (!this.dictionary.complexPrefixes && this.dictionary.twoStageAffix) {
                    stems.addAll(this.stem(strippedWord, length, affix, flag, prefixFlag, ++recursionDepth, false, true, false, circumfix, caseVariant));
                }
            }
            else if (recursionDepth == 1) {
                if (prefix && this.dictionary.complexPrefixes) {
                    stems.addAll(this.stem(strippedWord, length, affix, flag, flag, ++recursionDepth, false, true, true, circumfix, caseVariant));
                }
                else if (!prefix && !this.dictionary.complexPrefixes && this.dictionary.twoStageAffix) {
                    stems.addAll(this.stem(strippedWord, length, affix, flag, prefixFlag, ++recursionDepth, false, true, false, circumfix, caseVariant));
                }
            }
        }
        return stems;
    }
    
    private boolean hasCrossCheckedFlag(final char flag, final char[] flags, final boolean matchEmpty) {
        return (flags.length == 0 && matchEmpty) || Arrays.binarySearch(flags, flag) >= 0;
    }
}
