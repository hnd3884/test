package org.glassfish.jersey.message.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Cookie;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Date;
import java.text.ParseException;
import javax.ws.rs.core.MediaType;

public abstract class HttpHeaderReader
{
    private static final ListElementCreator<MatchingEntityTag> MATCHING_ENTITY_TAG_CREATOR;
    private static final ListElementCreator<MediaType> MEDIA_TYPE_CREATOR;
    private static final ListElementCreator<AcceptableMediaType> ACCEPTABLE_MEDIA_TYPE_CREATOR;
    private static final ListElementCreator<QualitySourceMediaType> QUALITY_SOURCE_MEDIA_TYPE_CREATOR;
    private static final ListElementCreator<AcceptableToken> ACCEPTABLE_TOKEN_CREATOR;
    private static final ListElementCreator<AcceptableLanguageTag> LANGUAGE_CREATOR;
    
    public abstract boolean hasNext();
    
    public abstract boolean hasNextSeparator(final char p0, final boolean p1);
    
    public abstract Event next() throws ParseException;
    
    public abstract Event next(final boolean p0) throws ParseException;
    
    protected abstract Event next(final boolean p0, final boolean p1) throws ParseException;
    
    protected abstract CharSequence nextSeparatedString(final char p0, final char p1) throws ParseException;
    
    protected abstract Event getEvent();
    
    public abstract CharSequence getEventValue();
    
    public abstract CharSequence getRemainder();
    
    public abstract int getIndex();
    
    public final CharSequence nextToken() throws ParseException {
        final Event e = this.next(false);
        if (e != Event.Token) {
            throw new ParseException("Next event is not a Token", this.getIndex());
        }
        return this.getEventValue();
    }
    
    public final void nextSeparator(final char c) throws ParseException {
        final Event e = this.next(false);
        if (e != Event.Separator) {
            throw new ParseException("Next event is not a Separator", this.getIndex());
        }
        if (c != this.getEventValue().charAt(0)) {
            throw new ParseException("Expected separator '" + c + "' instead of '" + this.getEventValue().charAt(0) + "'", this.getIndex());
        }
    }
    
    public final CharSequence nextQuotedString() throws ParseException {
        final Event e = this.next(false);
        if (e != Event.QuotedString) {
            throw new ParseException("Next event is not a Quoted String", this.getIndex());
        }
        return this.getEventValue();
    }
    
    public final CharSequence nextTokenOrQuotedString() throws ParseException {
        return this.nextTokenOrQuotedString(false);
    }
    
    private CharSequence nextTokenOrQuotedString(final boolean preserveBackslash) throws ParseException {
        final Event e = this.next(false, preserveBackslash);
        if (e != Event.Token && e != Event.QuotedString) {
            throw new ParseException("Next event is not a Token or a Quoted String, " + (Object)this.getEventValue(), this.getIndex());
        }
        return this.getEventValue();
    }
    
    public static HttpHeaderReader newInstance(final String header) {
        return new HttpHeaderReaderImpl(header);
    }
    
    public static HttpHeaderReader newInstance(final String header, final boolean processComments) {
        return new HttpHeaderReaderImpl(header, processComments);
    }
    
    public static Date readDate(final String date) throws ParseException {
        return HttpDateFormat.readDate(date);
    }
    
    public static int readQualityFactor(final CharSequence q) throws ParseException {
        if (q == null || q.length() == 0) {
            throw new ParseException("Quality value cannot be null or an empty String", 0);
        }
        int index = 0;
        final int length = q.length();
        if (length > 5) {
            throw new ParseException("Quality value is greater than the maximum length, 5", 0);
        }
        char c;
        final char wholeNumber = c = q.charAt(index++);
        if (c == '0' || c == '1') {
            if (index == length) {
                return (c - '0') * 1000;
            }
            c = q.charAt(index++);
            if (c != '.') {
                throw new ParseException("Error parsing Quality value: a decimal place is expected rather than '" + c + "'", index);
            }
            if (index == length) {
                return (c - '0') * 1000;
            }
        }
        else {
            if (c != '.') {
                throw new ParseException("Error parsing Quality value: a decimal numeral '0' or '1' is expected rather than '" + c + "'", index);
            }
            if (index == length) {
                throw new ParseException("Error parsing Quality value: a decimal numeral is expected after the decimal point", index);
            }
        }
        int value = 0;
        int exponent = 100;
        while (index < length) {
            c = q.charAt(index++);
            if (c < '0' || c > '9') {
                throw new ParseException("Error parsing Quality value: a decimal numeral is expected rather than '" + c + "'", index);
            }
            value += (c - '0') * exponent;
            exponent /= 10;
        }
        if (wholeNumber != '1') {
            return value;
        }
        if (value > 0) {
            throw new ParseException("The Quality value, " + (Object)q + ", is greater than 1", index);
        }
        return 1000;
    }
    
    public static int readQualityFactorParameter(final HttpHeaderReader reader) throws ParseException {
        while (reader.hasNext()) {
            reader.nextSeparator(';');
            if (!reader.hasNext()) {
                return 1000;
            }
            final CharSequence name = reader.nextToken();
            reader.nextSeparator('=');
            final CharSequence value = reader.nextTokenOrQuotedString();
            if (name.length() == 1 && (name.charAt(0) == 'q' || name.charAt(0) == 'Q')) {
                return readQualityFactor(value);
            }
        }
        return 1000;
    }
    
    public static Map<String, String> readParameters(final HttpHeaderReader reader) throws ParseException {
        return readParameters(reader, false);
    }
    
    public static Map<String, String> readParameters(final HttpHeaderReader reader, final boolean fileNameFix) throws ParseException {
        Map<String, String> m = null;
        while (reader.hasNext()) {
            reader.nextSeparator(';');
            while (reader.hasNextSeparator(';', true)) {
                reader.next();
            }
            if (!reader.hasNext()) {
                break;
            }
            final String name = reader.nextToken().toString().toLowerCase();
            reader.nextSeparator('=');
            String value;
            if ("filename".equals(name) && fileNameFix) {
                value = reader.nextTokenOrQuotedString(true).toString();
                value = value.substring(value.lastIndexOf(92) + 1);
            }
            else {
                value = reader.nextTokenOrQuotedString(false).toString();
            }
            if (m == null) {
                m = new LinkedHashMap<String, String>();
            }
            m.put(name, value);
        }
        return m;
    }
    
    public static Map<String, Cookie> readCookies(final String header) {
        return CookiesParser.parseCookies(header);
    }
    
    public static Cookie readCookie(final String header) {
        return CookiesParser.parseCookie(header);
    }
    
    public static NewCookie readNewCookie(final String header) {
        return CookiesParser.parseNewCookie(header);
    }
    
    public static Set<MatchingEntityTag> readMatchingEntityTag(final String header) throws ParseException {
        if ("*".equals(header)) {
            return MatchingEntityTag.ANY_MATCH;
        }
        final HttpHeaderReader reader = new HttpHeaderReaderImpl(header);
        final Set<MatchingEntityTag> l = new HashSet<MatchingEntityTag>(1);
        final HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
        while (reader.hasNext()) {
            l.add(HttpHeaderReader.MATCHING_ENTITY_TAG_CREATOR.create(adapter));
            adapter.reset();
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return l;
    }
    
    public static List<MediaType> readMediaTypes(final List<MediaType> l, final String header) throws ParseException {
        return readList(l, HttpHeaderReader.MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<AcceptableMediaType> readAcceptMediaType(final String header) throws ParseException {
        return readQualifiedList(AcceptableMediaType.COMPARATOR, HttpHeaderReader.ACCEPTABLE_MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<QualitySourceMediaType> readQualitySourceMediaType(final String header) throws ParseException {
        return readQualifiedList(QualitySourceMediaType.COMPARATOR, HttpHeaderReader.QUALITY_SOURCE_MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<QualitySourceMediaType> readQualitySourceMediaType(final String[] header) throws ParseException {
        if (header.length < 2) {
            return readQualitySourceMediaType(header[0]);
        }
        final StringBuilder sb = new StringBuilder();
        for (final String h : header) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(h);
        }
        return readQualitySourceMediaType(sb.toString());
    }
    
    public static List<AcceptableMediaType> readAcceptMediaType(final String header, final List<QualitySourceMediaType> priorityMediaTypes) throws ParseException {
        return readQualifiedList(new Comparator<AcceptableMediaType>() {
            @Override
            public int compare(final AcceptableMediaType o1, final AcceptableMediaType o2) {
                boolean q_o1_set = false;
                int q_o1 = 0;
                boolean q_o2_set = false;
                int q_o2 = 0;
                for (final QualitySourceMediaType priorityType : priorityMediaTypes) {
                    if (!q_o1_set && MediaTypes.typeEqual(o1, priorityType)) {
                        q_o1 = o1.getQuality() * priorityType.getQuality();
                        q_o1_set = true;
                    }
                    else {
                        if (q_o2_set || !MediaTypes.typeEqual(o2, priorityType)) {
                            continue;
                        }
                        q_o2 = o2.getQuality() * priorityType.getQuality();
                        q_o2_set = true;
                    }
                }
                int i = q_o2 - q_o1;
                if (i != 0) {
                    return i;
                }
                i = o2.getQuality() - o1.getQuality();
                if (i != 0) {
                    return i;
                }
                return MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1, o2);
            }
        }, HttpHeaderReader.ACCEPTABLE_MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<AcceptableToken> readAcceptToken(final String header) throws ParseException {
        return readQualifiedList(HttpHeaderReader.ACCEPTABLE_TOKEN_CREATOR, header);
    }
    
    public static List<AcceptableLanguageTag> readAcceptLanguage(final String header) throws ParseException {
        return readQualifiedList(HttpHeaderReader.LANGUAGE_CREATOR, header);
    }
    
    private static <T extends Qualified> List<T> readQualifiedList(final ListElementCreator<T> c, final String header) throws ParseException {
        final List<T> l = (List<T>)readList((ListElementCreator<Object>)c, header);
        Collections.sort(l, Quality.QUALIFIED_COMPARATOR);
        return l;
    }
    
    private static <T> List<T> readQualifiedList(final Comparator<T> comparator, final ListElementCreator<T> c, final String header) throws ParseException {
        final List<T> l = readList(c, header);
        Collections.sort(l, comparator);
        return l;
    }
    
    public static List<String> readStringList(final String header) throws ParseException {
        return readList((ListElementCreator<String>)new ListElementCreator<String>() {
            @Override
            public String create(final HttpHeaderReader reader) throws ParseException {
                reader.hasNext();
                return reader.nextToken().toString();
            }
        }, header);
    }
    
    private static <T> List<T> readList(final ListElementCreator<T> c, final String header) throws ParseException {
        return readList(new ArrayList<T>(), c, header);
    }
    
    private static <T> List<T> readList(final List<T> l, final ListElementCreator<T> c, final String header) throws ParseException {
        final HttpHeaderReader reader = new HttpHeaderReaderImpl(header);
        final HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
        while (reader.hasNext()) {
            l.add(c.create(adapter));
            adapter.reset();
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return l;
    }
    
    static {
        MATCHING_ENTITY_TAG_CREATOR = new ListElementCreator<MatchingEntityTag>() {
            @Override
            public MatchingEntityTag create(final HttpHeaderReader reader) throws ParseException {
                return MatchingEntityTag.valueOf(reader);
            }
        };
        MEDIA_TYPE_CREATOR = new ListElementCreator<MediaType>() {
            @Override
            public MediaType create(final HttpHeaderReader reader) throws ParseException {
                return MediaTypeProvider.valueOf(reader);
            }
        };
        ACCEPTABLE_MEDIA_TYPE_CREATOR = new ListElementCreator<AcceptableMediaType>() {
            @Override
            public AcceptableMediaType create(final HttpHeaderReader reader) throws ParseException {
                return AcceptableMediaType.valueOf(reader);
            }
        };
        QUALITY_SOURCE_MEDIA_TYPE_CREATOR = new ListElementCreator<QualitySourceMediaType>() {
            @Override
            public QualitySourceMediaType create(final HttpHeaderReader reader) throws ParseException {
                return QualitySourceMediaType.valueOf(reader);
            }
        };
        ACCEPTABLE_TOKEN_CREATOR = new ListElementCreator<AcceptableToken>() {
            @Override
            public AcceptableToken create(final HttpHeaderReader reader) throws ParseException {
                return new AcceptableToken(reader);
            }
        };
        LANGUAGE_CREATOR = new ListElementCreator<AcceptableLanguageTag>() {
            @Override
            public AcceptableLanguageTag create(final HttpHeaderReader reader) throws ParseException {
                return new AcceptableLanguageTag(reader);
            }
        };
    }
    
    public enum Event
    {
        Token, 
        QuotedString, 
        Comment, 
        Separator, 
        Control;
    }
    
    private interface ListElementCreator<T>
    {
        T create(final HttpHeaderReader p0) throws ParseException;
    }
}
