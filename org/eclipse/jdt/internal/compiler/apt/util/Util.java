package org.eclipse.jdt.internal.compiler.apt.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import javax.tools.FileObject;
import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.Charset;
import java.io.InputStream;

public final class Util
{
    public static String LINE_SEPARATOR;
    
    static {
        Util.LINE_SEPARATOR = System.getProperty("line.separator");
    }
    
    public static char[] getInputStreamAsCharArray(final InputStream stream, final int length, final String encoding) throws IOException {
        Charset charset = null;
        try {
            charset = Charset.forName(encoding);
        }
        catch (final IllegalCharsetNameException ex) {
            System.err.println("Illegal charset name : " + encoding);
            return null;
        }
        catch (final UnsupportedCharsetException ex2) {
            System.err.println("Unsupported charset : " + encoding);
            return null;
        }
        final CharsetDecoder charsetDecoder = charset.newDecoder();
        charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        final byte[] contents = org.eclipse.jdt.internal.compiler.util.Util.getInputStreamAsByteArray(stream, length);
        final ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
        byteBuffer.put(contents);
        byteBuffer.flip();
        return charsetDecoder.decode(byteBuffer).array();
    }
    
    public static CharSequence getCharContents(final FileObject fileObject, final boolean ignoreEncodingErrors, final byte[] contents, final String encoding) throws IOException {
        if (contents == null) {
            return null;
        }
        Charset charset = null;
        try {
            charset = Charset.forName(encoding);
        }
        catch (final IllegalCharsetNameException ex) {
            System.err.println("Illegal charset name : " + encoding);
            return null;
        }
        catch (final UnsupportedCharsetException ex2) {
            System.err.println("Unsupported charset : " + encoding);
            return null;
        }
        final CharsetDecoder charsetDecoder = charset.newDecoder();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
        byteBuffer.put(contents);
        byteBuffer.flip();
        if (ignoreEncodingErrors) {
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            return charsetDecoder.decode(byteBuffer);
        }
        charsetDecoder.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer out = CharBuffer.allocate(contents.length);
        CoderResult result = null;
        final String replacement = charsetDecoder.replacement();
        final int replacementLength = replacement.length();
        EncodingErrorCollector collector = null;
        while (true) {
            result = charsetDecoder.decode(byteBuffer, out, true);
            if (result.isMalformed() || result.isUnmappable()) {
                if (collector == null) {
                    collector = new EncodingErrorCollector(fileObject, encoding);
                }
                reportEncodingError(collector, out.position(), result.length());
                if (out.position() + replacementLength >= out.capacity()) {
                    final CharBuffer temp = CharBuffer.allocate(out.capacity() * 2);
                    out.flip();
                    temp.put(out);
                    out = temp;
                }
                out.append(replacement);
                byteBuffer.position(byteBuffer.position() + result.length());
            }
            else {
                if (!result.isOverflow()) {
                    break;
                }
                final CharBuffer temp = CharBuffer.allocate(out.capacity() * 2);
                out.flip();
                temp.put(out);
                out = temp;
            }
        }
        out.flip();
        if (collector != null) {
            collector.reportAllEncodingErrors(out.toString());
        }
        return out;
    }
    
    private static void reportEncodingError(final EncodingErrorCollector collector, final int position, final int length) {
        collector.collect(position, -length);
    }
    
    public static class EncodingError
    {
        int position;
        int length;
        
        public EncodingError(final int position, final int length) {
            this.position = position;
            this.length = length;
        }
        
        public String getSource(final char[] unitSource) {
            final int startPosition = this.position;
            final int endPosition = this.position + this.length - 1;
            if (startPosition > endPosition || (startPosition < 0 && endPosition < 0) || unitSource.length == 0) {
                return "No source available";
            }
            final StringBuffer errorBuffer = new StringBuffer();
            errorBuffer.append('\t');
            final int sourceLength = unitSource.length;
            int begin;
            char c;
            for (begin = ((startPosition >= sourceLength) ? (sourceLength - 1) : startPosition); begin > 0 && (c = unitSource[begin - 1]) != '\n' && c != '\r'; --begin) {}
            int end = (endPosition >= sourceLength) ? (sourceLength - 1) : endPosition;
            while (true) {
                while (end + 1 < sourceLength) {
                    if ((c = unitSource[end + 1]) != '\r') {
                        if (c != '\n') {
                            ++end;
                            continue;
                        }
                    }
                    while ((c = unitSource[begin]) == ' ' || c == '\t') {
                        ++begin;
                    }
                    errorBuffer.append(unitSource, begin, end - begin + 1);
                    errorBuffer.append(Util.LINE_SEPARATOR).append("\t");
                    for (int i = begin; i < startPosition; ++i) {
                        errorBuffer.append((unitSource[i] == '\t') ? '\t' : ' ');
                    }
                    for (int i = startPosition; i <= ((endPosition >= sourceLength) ? (sourceLength - 1) : endPosition); ++i) {
                        errorBuffer.append('^');
                    }
                    return errorBuffer.toString();
                }
                continue;
            }
        }
    }
    
    public static class EncodingErrorCollector
    {
        ArrayList<EncodingError> encodingErrors;
        FileObject fileObject;
        String encoding;
        
        public EncodingErrorCollector(final FileObject fileObject, final String encoding) {
            this.encodingErrors = new ArrayList<EncodingError>();
            this.fileObject = fileObject;
            this.encoding = encoding;
        }
        
        public void collect(final int position, final int length) {
            this.encodingErrors.add(new EncodingError(position, length));
        }
        
        public void reportAllEncodingErrors(final String string) {
            final char[] unitSource = string.toCharArray();
            for (final EncodingError error : this.encodingErrors) {
                System.err.println(String.valueOf(this.fileObject.getName()) + " Unmappable character for encoding " + this.encoding);
                System.err.println(error.getSource(unitSource));
            }
        }
    }
}
