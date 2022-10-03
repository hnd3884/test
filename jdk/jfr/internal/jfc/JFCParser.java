package jdk.jfr.internal.jfc;

import java.io.CharArrayWriter;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.org.xml.sax.InputSource;
import java.io.CharArrayReader;
import jdk.internal.util.xml.impl.SAXParserImpl;
import jdk.internal.org.xml.sax.SAXException;
import jdk.jfr.internal.PrivateAccess;
import java.text.ParseException;
import java.io.IOException;
import jdk.jfr.Configuration;
import java.io.Reader;

final class JFCParser
{
    static final String FILE_EXTENSION = ".jfc";
    private static final int MAXIMUM_FILE_SIZE = 1048576;
    
    public static Configuration createConfiguration(final String s, final Reader reader) throws IOException, ParseException {
        return createConfiguration(s, readContent(reader));
    }
    
    public static Configuration createConfiguration(final String s, final String s2) throws IOException, ParseException {
        try {
            final JFCParserHandler jfcParserHandler = new JFCParserHandler();
            parseXML(s2, jfcParserHandler);
            return PrivateAccess.getInstance().newConfiguration(s, jfcParserHandler.label, jfcParserHandler.description, jfcParserHandler.provider, jfcParserHandler.settings, s2);
        }
        catch (final IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), -1);
        }
        catch (final SAXException ex2) {
            final ParseException ex3 = new ParseException("Error reading JFC file. " + ex2.getMessage(), -1);
            ex3.initCause(ex2);
            throw ex3;
        }
    }
    
    private static void parseXML(final String s, final JFCParserHandler jfcParserHandler) throws SAXException, IOException {
        new SAXParserImpl().parse(new InputSource(new CharArrayReader(s.toCharArray())), jfcParserHandler);
    }
    
    private static String readContent(final Reader reader) throws IOException {
        final CharArrayWriter charArrayWriter = new CharArrayWriter(1024);
        int n = 0;
        int read;
        while ((read = reader.read()) != -1) {
            charArrayWriter.write(read);
            if (++n >= 1048576) {
                throw new IOException("Presets with more than 1048576 characters can't be read.");
            }
        }
        return new String(charArrayWriter.toCharArray());
    }
}
