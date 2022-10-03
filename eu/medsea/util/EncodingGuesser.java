package eu.medsea.util;

import java.util.HashMap;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.SortedMap;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.util.TreeSet;
import java.util.Map;
import java.util.Collection;
import eu.medsea.mimeutil.MimeUtil2;

public class EncodingGuesser
{
    private static final long serialVersionUID = -247389882161262839L;
    private static final MimeUtil2.MimeLogger log;
    private static String defaultJVMEncoding;
    private static Collection supportedEncodings;
    private static Map boms;
    
    public static boolean isKnownEncoding(final String encoding) {
        return EncodingGuesser.supportedEncodings.contains(encoding);
    }
    
    public static Collection getPossibleEncodings(final byte[] data) {
        final Collection possibleEncodings = new TreeSet();
        if (data == null || data.length == 0) {
            return possibleEncodings;
        }
        String encoding = null;
        final Iterator it = EncodingGuesser.supportedEncodings.iterator();
        while (it.hasNext()) {
            try {
                encoding = it.next();
                final int lengthBOM = getLengthBOM(encoding, data);
                String test = new String(getByteArraySubArray(data, lengthBOM, data.length - lengthBOM), encoding);
                if (test.length() > 1) {
                    test = test.substring(0, test.length() - 2);
                }
                byte[] compare = null;
                try {
                    compare = test.getBytes(encoding);
                }
                catch (final UnsupportedOperationException ignore) {
                    continue;
                }
                if (!compareByteArrays(data, lengthBOM, compare, 0, compare.length)) {
                    continue;
                }
                if (lengthBOM != 0) {
                    possibleEncodings.clear();
                    possibleEncodings.add(encoding);
                    return possibleEncodings;
                }
                possibleEncodings.add(encoding);
            }
            catch (final UnsupportedEncodingException uee) {
                EncodingGuesser.log.error("The encoding [" + encoding + "] is not supported by your JVM.");
            }
            catch (final Exception e) {
                EncodingGuesser.log.error(e.getLocalizedMessage(), e);
            }
        }
        return possibleEncodings;
    }
    
    public static boolean removeEncoding(final String encoding) {
        return EncodingGuesser.supportedEncodings.remove(encoding);
    }
    
    public static boolean removeEncodings(final String[] encodings) {
        boolean removedAtLeast_1 = false;
        for (int i = 0; i < encodings.length; ++i) {
            if (removeEncoding(encodings[i])) {
                removedAtLeast_1 = true;
            }
        }
        return removedAtLeast_1;
    }
    
    public static Collection getValidEncodings(final String[] encodings) {
        final Collection c = new ArrayList();
        for (int i = 0; i < encodings.length; ++i) {
            if (EncodingGuesser.supportedEncodings.contains(encodings[i])) {
                c.add(encodings[i]);
            }
        }
        return c;
    }
    
    public static String getDefaultEncoding() {
        return EncodingGuesser.defaultJVMEncoding;
    }
    
    public static Collection getSupportedEncodings() {
        return EncodingGuesser.supportedEncodings;
    }
    
    public static Collection setSupportedEncodings(final Collection encodings) {
        final Collection current = new TreeSet();
        Iterator it = EncodingGuesser.supportedEncodings.iterator();
        while (it.hasNext()) {
            current.add(it.next());
        }
        if (encodings != null) {
            EncodingGuesser.supportedEncodings.clear();
            it = encodings.iterator();
            while (it.hasNext()) {
                EncodingGuesser.supportedEncodings.add(it.next());
            }
        }
        return current;
    }
    
    public static int getLengthBOM(final String encoding, final byte[] data) {
        if (!EncodingGuesser.boms.containsKey(encoding)) {
            return 0;
        }
        final byte[] bom = EncodingGuesser.boms.get(encoding);
        if (compareByteArrays(bom, 0, data, 0, bom.length)) {
            return bom.length;
        }
        return 0;
    }
    
    public static byte[] getByteArraySubArray(final byte[] a, final int offset, final int length) {
        if (offset + length > a.length) {
            return a;
        }
        final byte[] data = new byte[length];
        for (int i = 0; i < length; ++i) {
            data[i] = a[offset + i];
        }
        return data;
    }
    
    public static boolean compareByteArrays(final byte[] a, final int aOffset, final byte[] b, final int bOffset, final int length) {
        if (a.length < aOffset + length || b.length < bOffset + length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (a[aOffset + i] != b[bOffset + i]) {
                return false;
            }
        }
        return true;
    }
    
    public static Collection getCanonicalEncodingNamesSupportedByJVM() {
        final Collection encodings = new TreeSet();
        final SortedMap charSets = Charset.availableCharsets();
        final Collection charSetNames = charSets.keySet();
        final Iterator it = charSetNames.iterator();
        while (it.hasNext()) {
            encodings.add(it.next());
        }
        if (EncodingGuesser.log.isDebugEnabled()) {
            EncodingGuesser.log.debug("The following [" + encodings.size() + "] encodings will be used: " + encodings);
        }
        return encodings;
    }
    
    static {
        log = new MimeUtil2.MimeLogger(EncodingGuesser.class.getName());
        EncodingGuesser.defaultJVMEncoding = Charset.forName(new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding()).name();
        EncodingGuesser.supportedEncodings = new TreeSet();
        (EncodingGuesser.boms = new HashMap()).put("UTF-32BE", new byte[] { 0, 0, -2, -1 });
        EncodingGuesser.boms.put("UTF-32LE", new byte[] { -1, -2, 0, 0 });
        EncodingGuesser.boms.put("UTF-16BE", new byte[] { -2, -1 });
        EncodingGuesser.boms.put("UTF-16LE", new byte[] { -1, -2 });
        EncodingGuesser.boms.put("UTF-8", new byte[] { -17, -69, -65 });
        EncodingGuesser.boms.put("UTF-7", new byte[] { 43, 47, 118 });
        EncodingGuesser.boms.put("UTF-1", new byte[] { -9, 100, 76 });
        EncodingGuesser.boms.put("UTF-EBCDIC", new byte[] { -35, 115, 102, 115 });
        EncodingGuesser.boms.put("SCSU", new byte[] { 14, -2, -1 });
        EncodingGuesser.boms.put("BOCU-1", new byte[] { -5, -18, 40 });
    }
}
