package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ByteStringBuffer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Set;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LogMessage implements Serializable
{
    private static final String TIMESTAMP_SEC_FORMAT = "'['dd/MMM/yyyy:HH:mm:ss Z']'";
    private static final String TIMESTAMP_MS_FORMAT = "'['dd/MMM/yyyy:HH:mm:ss.SSS Z']'";
    private static final ThreadLocal<SimpleDateFormat> dateSecFormat;
    private static final ThreadLocal<SimpleDateFormat> dateMsFormat;
    private static final long serialVersionUID = -1210050773534504972L;
    private final Date timestamp;
    private final Map<String, String> namedValues;
    private final Set<String> unnamedValues;
    private final String messageString;
    
    protected LogMessage(final LogMessage m) {
        this.timestamp = m.timestamp;
        this.unnamedValues = m.unnamedValues;
        this.namedValues = m.namedValues;
        this.messageString = m.messageString;
    }
    
    protected LogMessage(final String s) throws LogException {
        this.messageString = s;
        final int bracketPos = s.indexOf(93);
        if (bracketPos < 0) {
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_NO_TIMESTAMP.get());
        }
        final String timestampString = s.substring(0, bracketPos + 1);
        SimpleDateFormat f;
        if (timestampIncludesMilliseconds(timestampString)) {
            f = LogMessage.dateMsFormat.get();
            if (f == null) {
                f = new SimpleDateFormat("'['dd/MMM/yyyy:HH:mm:ss.SSS Z']'");
                f.setLenient(false);
                LogMessage.dateMsFormat.set(f);
            }
        }
        else {
            f = LogMessage.dateSecFormat.get();
            if (f == null) {
                f = new SimpleDateFormat("'['dd/MMM/yyyy:HH:mm:ss Z']'");
                f.setLenient(false);
                LogMessage.dateSecFormat.set(f);
            }
        }
        try {
            this.timestamp = f.parse(timestampString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_TIMESTAMP.get(StaticUtils.getExceptionMessage(e)), e);
        }
        final LinkedHashMap<String, String> named = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(10));
        final LinkedHashSet<String> unnamed = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(10));
        parseTokens(s, bracketPos + 1, named, unnamed);
        this.namedValues = Collections.unmodifiableMap((Map<? extends String, ? extends String>)named);
        this.unnamedValues = Collections.unmodifiableSet((Set<? extends String>)unnamed);
    }
    
    private static void parseTokens(final String s, final int startPos, final Map<String, String> named, final Set<String> unnamed) throws LogException {
        boolean inQuotes = false;
        final StringBuilder buffer = new StringBuilder();
        for (int p = startPos; p < s.length(); ++p) {
            final char c = s.charAt(p);
            if (c == ' ' && !inQuotes) {
                if (buffer.length() > 0) {
                    processToken(s, buffer.toString(), named, unnamed);
                    buffer.delete(0, buffer.length());
                }
            }
            else if (c == '\"') {
                inQuotes = !inQuotes;
            }
            else {
                buffer.append(c);
            }
        }
        if (buffer.length() > 0) {
            processToken(s, buffer.toString(), named, unnamed);
        }
    }
    
    private static void processToken(final String s, final String token, final Map<String, String> named, final Set<String> unnamed) throws LogException {
        final int equalPos = token.indexOf(61);
        if (equalPos < 0) {
            unnamed.add(token);
        }
        else {
            final String name = token.substring(0, equalPos);
            final String value = processValue(s, token.substring(equalPos + 1));
            named.put(name, value);
        }
    }
    
    private static String processValue(final String s, final String v) throws LogException {
        final ByteStringBuffer b = new ByteStringBuffer();
        for (int i = 0; i < v.length(); ++i) {
            final char c = v.charAt(i);
            if (c != '\"') {
                if (c == '#') {
                    if (i > v.length() - 3) {
                        throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_ESCAPED_CHARACTER.get(v));
                    }
                    byte rawByte = 0;
                    for (int j = 0; j < 2; ++j) {
                        rawByte <<= 4;
                        switch (v.charAt(++i)) {
                            case '0': {
                                break;
                            }
                            case '1': {
                                rawByte |= 0x1;
                                break;
                            }
                            case '2': {
                                rawByte |= 0x2;
                                break;
                            }
                            case '3': {
                                rawByte |= 0x3;
                                break;
                            }
                            case '4': {
                                rawByte |= 0x4;
                                break;
                            }
                            case '5': {
                                rawByte |= 0x5;
                                break;
                            }
                            case '6': {
                                rawByte |= 0x6;
                                break;
                            }
                            case '7': {
                                rawByte |= 0x7;
                                break;
                            }
                            case '8': {
                                rawByte |= 0x8;
                                break;
                            }
                            case '9': {
                                rawByte |= 0x9;
                                break;
                            }
                            case 'A':
                            case 'a': {
                                rawByte |= 0xA;
                                break;
                            }
                            case 'B':
                            case 'b': {
                                rawByte |= 0xB;
                                break;
                            }
                            case 'C':
                            case 'c': {
                                rawByte |= 0xC;
                                break;
                            }
                            case 'D':
                            case 'd': {
                                rawByte |= 0xD;
                                break;
                            }
                            case 'E':
                            case 'e': {
                                rawByte |= 0xE;
                                break;
                            }
                            case 'F':
                            case 'f': {
                                rawByte |= 0xF;
                                break;
                            }
                            default: {
                                throw new LogException(s, LogMessages.ERR_LOG_MESSAGE_INVALID_ESCAPED_CHARACTER.get(v));
                            }
                        }
                    }
                    b.append(rawByte);
                }
                else {
                    b.append(c);
                }
            }
        }
        return b.toString();
    }
    
    private static boolean timestampIncludesMilliseconds(final String timestamp) {
        return timestamp.length() > 21 && timestamp.charAt(21) == '.';
    }
    
    public final Date getTimestamp() {
        return this.timestamp;
    }
    
    public final Map<String, String> getNamedValues() {
        return this.namedValues;
    }
    
    public final String getNamedValue(final String name) {
        return this.namedValues.get(name);
    }
    
    public final Boolean getNamedValueAsBoolean(final String name) {
        final String s = this.namedValues.get(name);
        if (s == null) {
            return null;
        }
        final String lowerValue = StaticUtils.toLowerCase(s);
        if (lowerValue.equals("true") || lowerValue.equals("t") || lowerValue.equals("yes") || lowerValue.equals("y") || lowerValue.equals("on") || lowerValue.equals("1")) {
            return Boolean.TRUE;
        }
        if (lowerValue.equals("false") || lowerValue.equals("f") || lowerValue.equals("no") || lowerValue.equals("n") || lowerValue.equals("off") || lowerValue.equals("0")) {
            return Boolean.FALSE;
        }
        return null;
    }
    
    public final Double getNamedValueAsDouble(final String name) {
        final String s = this.namedValues.get(name);
        if (s == null) {
            return null;
        }
        try {
            return Double.valueOf(s);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public final Integer getNamedValueAsInteger(final String name) {
        final String s = this.namedValues.get(name);
        if (s == null) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public final Long getNamedValueAsLong(final String name) {
        final String s = this.namedValues.get(name);
        if (s == null) {
            return null;
        }
        try {
            return Long.valueOf(s);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public final Set<String> getUnnamedValues() {
        return this.unnamedValues;
    }
    
    public final boolean hasUnnamedValue(final String value) {
        return this.unnamedValues.contains(value);
    }
    
    @Override
    public final String toString() {
        return this.messageString;
    }
    
    static {
        dateSecFormat = new ThreadLocal<SimpleDateFormat>();
        dateMsFormat = new ThreadLocal<SimpleDateFormat>();
    }
}
