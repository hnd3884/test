package com.unboundid.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.text.ParseException;
import java.util.ArrayList;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ValuePattern implements Serializable
{
    public static final String PUBLIC_JAVADOC_URL = "https://docs.ldap.com/ldap-sdk/docs/javadoc/index.html?com/unboundid/util/ValuePattern.html";
    private static final long serialVersionUID = 4502778464751705304L;
    private final boolean hasBackReference;
    private final String pattern;
    private final ThreadLocal<ArrayList<String>> refLists;
    private final ThreadLocal<StringBuilder> buffers;
    private final ValuePatternComponent[] components;
    
    public ValuePattern(final String s) throws ParseException {
        this(s, null);
    }
    
    public ValuePattern(final String s, final Long r) throws ParseException {
        Validator.ensureNotNull(s);
        this.pattern = s;
        this.refLists = new ThreadLocal<ArrayList<String>>();
        this.buffers = new ThreadLocal<StringBuilder>();
        final AtomicBoolean hasRef = new AtomicBoolean(false);
        Random random;
        if (r == null) {
            random = new Random();
        }
        else {
            random = new Random(r);
        }
        final ArrayList<ValuePatternComponent> l = new ArrayList<ValuePatternComponent>(3);
        parse(s, 0, l, random, hasRef);
        this.hasBackReference = hasRef.get();
        if (this.hasBackReference) {
            int availableReferences = 0;
            for (final ValuePatternComponent c : l) {
                if (c instanceof BackReferenceValuePatternComponent) {
                    final BackReferenceValuePatternComponent brvpc = (BackReferenceValuePatternComponent)c;
                    if (brvpc.getIndex() > availableReferences) {
                        throw new ParseException(UtilityMessages.ERR_REF_VALUE_PATTERN_INVALID_INDEX.get(brvpc.getIndex()), 0);
                    }
                }
                if (c.supportsBackReference()) {
                    ++availableReferences;
                }
            }
        }
        l.toArray(this.components = new ValuePatternComponent[l.size()]);
    }
    
    private static void parse(final String s, final int o, final ArrayList<ValuePatternComponent> l, final Random r, final AtomicBoolean ref) throws ParseException {
        int pos = s.indexOf("[[");
        if (pos >= 0) {
            if (pos > 0) {
                parse(s.substring(0, pos), o, l, r, ref);
            }
            l.add(new StringValuePatternComponent("["));
            if (pos < s.length() - 2) {
                parse(s.substring(pos + 2), o + pos + 2, l, r, ref);
            }
            return;
        }
        pos = s.indexOf("]]");
        if (pos >= 0) {
            if (pos > 0) {
                parse(s.substring(0, pos), o, l, r, ref);
            }
            l.add(new StringValuePatternComponent("]"));
            if (pos < s.length() - 2) {
                parse(s.substring(pos + 2), o + pos + 2, l, r, ref);
            }
            return;
        }
        pos = s.indexOf(91);
        if (pos >= 0) {
            final int closePos = s.indexOf(93);
            if (closePos < 0) {
                throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_UNMATCHED_OPEN.get(o + pos), o + pos);
            }
            if (closePos < pos) {
                throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_UNMATCHED_CLOSE.get(o + closePos), o + closePos);
            }
            if (pos > 0) {
                l.add(new StringValuePatternComponent(s.substring(0, pos)));
            }
            final String bracketedToken = s.substring(pos + 1, closePos);
            Label_0962: {
                if (bracketedToken.startsWith("random:")) {
                    l.add(new RandomCharactersValuePatternComponent(bracketedToken, r.nextLong()));
                }
                else if (bracketedToken.startsWith("file:")) {
                    final String path = bracketedToken.substring(5);
                    try {
                        l.add(new FileValuePatternComponent(path, r.nextLong(), false));
                    }
                    catch (final IOException ioe) {
                        Debug.debugException(ioe);
                        throw new ParseException(UtilityMessages.ERR_FILE_VALUE_PATTERN_NOT_USABLE.get(path, StaticUtils.getExceptionMessage(ioe)), o + pos);
                    }
                }
                else if (bracketedToken.startsWith("randomfile:")) {
                    final String path = bracketedToken.substring(11);
                    try {
                        l.add(new FileValuePatternComponent(path, r.nextLong(), false));
                    }
                    catch (final IOException ioe) {
                        Debug.debugException(ioe);
                        throw new ParseException(UtilityMessages.ERR_FILE_VALUE_PATTERN_NOT_USABLE.get(path, StaticUtils.getExceptionMessage(ioe)), o + pos);
                    }
                }
                else if (bracketedToken.startsWith("sequentialfile:")) {
                    final String path = bracketedToken.substring(15);
                    try {
                        l.add(new FileValuePatternComponent(path, r.nextLong(), true));
                    }
                    catch (final IOException ioe) {
                        Debug.debugException(ioe);
                        throw new ParseException(UtilityMessages.ERR_FILE_VALUE_PATTERN_NOT_USABLE.get(path, StaticUtils.getExceptionMessage(ioe)), o + pos);
                    }
                }
                else if (bracketedToken.startsWith("streamfile:")) {
                    final String path = bracketedToken.substring(11);
                    try {
                        l.add(new StreamFileValuePatternComponent(path));
                    }
                    catch (final IOException ioe) {
                        Debug.debugException(ioe);
                        throw new ParseException(UtilityMessages.ERR_STREAM_FILE_VALUE_PATTERN_NOT_USABLE.get(path, StaticUtils.getExceptionMessage(ioe)), o + pos);
                    }
                }
                else {
                    if (bracketedToken.startsWith("http://")) {
                        try {
                            l.add(new HTTPValuePatternComponent(bracketedToken, r.nextLong()));
                            break Label_0962;
                        }
                        catch (final IOException ioe2) {
                            Debug.debugException(ioe2);
                            throw new ParseException(UtilityMessages.ERR_HTTP_VALUE_PATTERN_NOT_USABLE.get(bracketedToken, StaticUtils.getExceptionMessage(ioe2)), o + pos);
                        }
                    }
                    if (bracketedToken.startsWith("timestamp")) {
                        l.add(new TimestampValuePatternComponent(bracketedToken, r.nextLong()));
                    }
                    else if (bracketedToken.equals("uuid")) {
                        l.add(new UUIDValuePatternComponent());
                    }
                    else if (bracketedToken.startsWith("ref:")) {
                        ref.set(true);
                        final String valueStr = bracketedToken.substring(4);
                        try {
                            final int index = Integer.parseInt(valueStr);
                            if (index == 0) {
                                throw new ParseException(UtilityMessages.ERR_REF_VALUE_PATTERN_ZERO_INDEX.get(), o + pos + 4);
                            }
                            if (index < 0) {
                                throw new ParseException(UtilityMessages.ERR_REF_VALUE_PATTERN_NOT_VALID.get(valueStr), o + pos + 4);
                            }
                            l.add(new BackReferenceValuePatternComponent(index));
                        }
                        catch (final NumberFormatException nfe) {
                            Debug.debugException(nfe);
                            throw new ParseException(UtilityMessages.ERR_REF_VALUE_PATTERN_NOT_VALID.get(valueStr), o + pos + 4);
                        }
                    }
                    else {
                        l.add(parseNumericComponent(s.substring(pos + 1, closePos), o + pos + 1, r));
                    }
                }
            }
            if (closePos < s.length() - 1) {
                parse(s.substring(closePos + 1), o + closePos + 1, l, r, ref);
            }
        }
        else {
            pos = s.indexOf(93);
            if (pos >= 0) {
                throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_UNMATCHED_CLOSE.get(o + pos), o + pos);
            }
            l.add(new StringValuePatternComponent(s));
        }
    }
    
    private static ValuePatternComponent parseNumericComponent(final String s, final int o, final Random r) throws ParseException {
        boolean delimiterFound = false;
        boolean sequential = false;
        int pos = 0;
        long lowerBound = 0L;
    Label_0354:
        while (pos < s.length()) {
            switch (s.charAt(pos)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    break;
                }
                case '-': {
                    if (pos == 0) {
                        break;
                    }
                    delimiterFound = true;
                    sequential = false;
                    try {
                        lowerBound = Long.parseLong(s.substring(0, pos));
                    }
                    catch (final NumberFormatException nfe) {
                        Debug.debugException(nfe);
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_VALUE_NOT_LONG.get(o - 1, Long.MIN_VALUE, Long.MAX_VALUE), o - 1);
                    }
                    ++pos;
                    break Label_0354;
                }
                case ':': {
                    delimiterFound = true;
                    sequential = true;
                    if (pos == 0) {
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_LOWER_BOUND.get(o - 1), o - 1);
                    }
                    try {
                        lowerBound = Long.parseLong(s.substring(0, pos));
                    }
                    catch (final NumberFormatException nfe) {
                        Debug.debugException(nfe);
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_VALUE_NOT_LONG.get(o - 1, Long.MIN_VALUE, Long.MAX_VALUE), o - 1);
                    }
                    ++pos;
                    break Label_0354;
                }
                default: {
                    throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_INVALID_CHARACTER.get(s.charAt(pos), o + pos), o + pos);
                }
            }
            ++pos;
        }
        if (!delimiterFound) {
            throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_NO_DELIMITER.get(o - 1), o - 1);
        }
        boolean hasIncrement = false;
        int startPos = pos;
        long upperBound = lowerBound;
        long increment = 1L;
        String formatString = null;
        delimiterFound = false;
    Label_0877:
        while (pos < s.length()) {
            switch (s.charAt(pos)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    break;
                }
                case '-': {
                    if (pos == startPos) {
                        break;
                    }
                    throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_INVALID_CHARACTER.get('-', o + pos), o + pos);
                }
                case 'x': {
                    delimiterFound = true;
                    hasIncrement = true;
                    if (pos == startPos) {
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_UPPER_BOUND.get(o - 1), o - 1);
                    }
                    try {
                        upperBound = Long.parseLong(s.substring(startPos, pos));
                    }
                    catch (final NumberFormatException nfe2) {
                        Debug.debugException(nfe2);
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_VALUE_NOT_LONG.get(o - 1, Long.MIN_VALUE, Long.MAX_VALUE), o - 1);
                    }
                    ++pos;
                    break Label_0877;
                }
                case '%': {
                    delimiterFound = true;
                    hasIncrement = false;
                    if (pos == startPos) {
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_UPPER_BOUND.get(o - 1), o - 1);
                    }
                    try {
                        upperBound = Long.parseLong(s.substring(startPos, pos));
                    }
                    catch (final NumberFormatException nfe2) {
                        Debug.debugException(nfe2);
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_VALUE_NOT_LONG.get(o - 1, Long.MIN_VALUE, Long.MAX_VALUE), o - 1);
                    }
                    ++pos;
                    break Label_0877;
                }
                default: {
                    throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_INVALID_CHARACTER.get(s.charAt(pos), o + pos), o + pos);
                }
            }
            ++pos;
        }
        if (!delimiterFound) {
            if (pos == startPos) {
                throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_UPPER_BOUND.get(o - 1), o - 1);
            }
            try {
                upperBound = Long.parseLong(s.substring(startPos, pos));
            }
            catch (final NumberFormatException nfe2) {
                Debug.debugException(nfe2);
                throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_VALUE_NOT_LONG.get(o - 1, Long.MIN_VALUE, Long.MAX_VALUE), o - 1);
            }
            if (sequential) {
                return new SequentialValuePatternComponent(lowerBound, upperBound, 1L, null);
            }
            return new RandomValuePatternComponent(lowerBound, upperBound, r.nextLong(), null);
        }
        else {
            Label_1573: {
                if (hasIncrement) {
                    delimiterFound = false;
                    startPos = pos;
                Label_1415:
                    while (pos < s.length()) {
                        switch (s.charAt(pos)) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9': {
                                break;
                            }
                            case '-': {
                                if (pos == startPos) {
                                    break;
                                }
                                throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_INVALID_CHARACTER.get('-', o + pos), o + pos);
                            }
                            case '%': {
                                delimiterFound = true;
                                if (pos == startPos) {
                                    throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_INCREMENT.get(o - 1), o - 1);
                                }
                                if (pos == s.length() - 1) {
                                    throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_FORMAT.get(o - 1), o - 1);
                                }
                                try {
                                    increment = Long.parseLong(s.substring(startPos, pos));
                                }
                                catch (final NumberFormatException nfe2) {
                                    Debug.debugException(nfe2);
                                    throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_VALUE_NOT_LONG.get(o - 1, Long.MIN_VALUE, Long.MAX_VALUE), o - 1);
                                }
                                formatString = s.substring(pos + 1);
                                break Label_1415;
                            }
                            default: {
                                throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_INVALID_CHARACTER.get(s.charAt(pos), o + pos), o + pos);
                            }
                        }
                        ++pos;
                    }
                    if (delimiterFound) {
                        break Label_1573;
                    }
                    if (pos == startPos) {
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_INCREMENT.get(o - 1), o - 1);
                    }
                    try {
                        increment = Long.parseLong(s.substring(startPos, pos));
                        break Label_1573;
                    }
                    catch (final NumberFormatException nfe2) {
                        Debug.debugException(nfe2);
                        throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_VALUE_NOT_LONG.get(o - 1, Long.MIN_VALUE, Long.MAX_VALUE), o - 1);
                    }
                }
                formatString = s.substring(pos);
                if (formatString.length() == 0) {
                    throw new ParseException(UtilityMessages.ERR_VALUE_PATTERN_EMPTY_FORMAT.get(o - 1), o - 1);
                }
            }
            if (sequential) {
                return new SequentialValuePatternComponent(lowerBound, upperBound, increment, formatString);
            }
            return new RandomValuePatternComponent(lowerBound, upperBound, r.nextLong(), formatString);
        }
    }
    
    public String nextValue() {
        StringBuilder buffer = this.buffers.get();
        if (buffer == null) {
            buffer = new StringBuilder();
            this.buffers.set(buffer);
        }
        else {
            buffer.setLength(0);
        }
        ArrayList<String> refList = this.refLists.get();
        if (this.hasBackReference) {
            if (refList == null) {
                refList = new ArrayList<String>(10);
                this.refLists.set(refList);
            }
            else {
                refList.clear();
            }
        }
        for (final ValuePatternComponent c : this.components) {
            if (this.hasBackReference) {
                if (c instanceof BackReferenceValuePatternComponent) {
                    final BackReferenceValuePatternComponent brvpc = (BackReferenceValuePatternComponent)c;
                    final String value = refList.get(brvpc.getIndex() - 1);
                    buffer.append(value);
                    refList.add(value);
                }
                else if (c.supportsBackReference()) {
                    final int startPos = buffer.length();
                    c.append(buffer);
                    refList.add(buffer.substring(startPos));
                }
                else {
                    c.append(buffer);
                }
            }
            else {
                c.append(buffer);
            }
        }
        return buffer.toString();
    }
    
    @Override
    public String toString() {
        return this.pattern;
    }
}
