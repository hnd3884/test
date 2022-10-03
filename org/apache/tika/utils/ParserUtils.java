package org.apache.tika.utils;

import java.io.IOException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.io.TemporaryResources;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.Arrays;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.Parser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;

public class ParserUtils
{
    public static final Property EMBEDDED_PARSER;
    
    public static Metadata cloneMetadata(final Metadata m) {
        final Metadata clone = new Metadata();
        for (final String n : m.names()) {
            if (!m.isMultiValued(n)) {
                clone.set(n, m.get(n));
            }
            else {
                final String[] values;
                final String[] vals = values = m.getValues(n);
                for (final String val : values) {
                    clone.add(n, val);
                }
            }
        }
        return clone;
    }
    
    public static String getParserClassname(final Parser parser) {
        if (parser instanceof ParserDecorator) {
            return ((ParserDecorator)parser).getWrappedParser().getClass().getName();
        }
        return parser.getClass().getName();
    }
    
    public static void recordParserDetails(final Parser parser, final Metadata metadata) {
        final String className = getParserClassname(parser);
        final String[] parsedBys = metadata.getValues(TikaCoreProperties.TIKA_PARSED_BY);
        if (parsedBys == null || parsedBys.length == 0) {
            metadata.add(TikaCoreProperties.TIKA_PARSED_BY, className);
        }
        else if (Arrays.stream(parsedBys).noneMatch(className::equals)) {
            metadata.add(TikaCoreProperties.TIKA_PARSED_BY, className);
        }
    }
    
    public static void recordParserFailure(final Parser parser, final Throwable failure, final Metadata metadata) {
        final String trace = ExceptionUtils.getStackTrace(failure);
        metadata.add(TikaCoreProperties.EMBEDDED_EXCEPTION, trace);
        metadata.add(ParserUtils.EMBEDDED_PARSER, getParserClassname(parser));
    }
    
    public static InputStream ensureStreamReReadable(final InputStream stream, final TemporaryResources tmp) throws IOException {
        if (stream instanceof RereadableInputStream) {
            return stream;
        }
        TikaInputStream tstream = TikaInputStream.cast(stream);
        if (tstream == null) {
            tstream = TikaInputStream.get(stream, tmp);
        }
        if (tstream.getInputStreamFactory() != null) {
            return (InputStream)tstream;
        }
        tstream.getFile();
        tstream.mark(-1);
        return (InputStream)tstream;
    }
    
    public static InputStream streamResetForReRead(final InputStream stream, final TemporaryResources tmp) throws IOException {
        if (stream instanceof RereadableInputStream) {
            ((RereadableInputStream)stream).rewind();
            return stream;
        }
        final TikaInputStream tstream = (TikaInputStream)stream;
        if (tstream.getInputStreamFactory() != null) {
            return (InputStream)TikaInputStream.get(tstream.getInputStreamFactory(), tmp);
        }
        tstream.reset();
        tstream.mark(-1);
        return (InputStream)tstream;
    }
    
    static {
        EMBEDDED_PARSER = Property.internalText("X-TIKA:EXCEPTION:embedded_parser");
    }
}
