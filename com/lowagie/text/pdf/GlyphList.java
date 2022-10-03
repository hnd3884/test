package com.lowagie.text.pdf;

import java.io.InputStream;
import java.util.StringTokenizer;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.pdf.fonts.FontsResourceAnchor;
import java.util.HashMap;

public class GlyphList
{
    private static HashMap unicode2names;
    private static HashMap names2unicode;
    
    public static int[] nameToUnicode(final String name) {
        return GlyphList.names2unicode.get(name);
    }
    
    public static String unicodeToName(final int num) {
        return GlyphList.unicode2names.get(new Integer(num));
    }
    
    static {
        GlyphList.unicode2names = new HashMap();
        GlyphList.names2unicode = new HashMap();
        InputStream is = null;
        try {
            is = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/glyphlist.txt", new FontsResourceAnchor().getClass().getClassLoader());
            if (is == null) {
                final String msg = "glyphlist.txt not found as resource. (It must exist as resource in the package com.lowagie.text.pdf.fonts)";
                throw new Exception(msg);
            }
            final byte[] buf = new byte[1024];
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (true) {
                final int size = is.read(buf);
                if (size < 0) {
                    break;
                }
                out.write(buf, 0, size);
            }
            is.close();
            is = null;
            final String s = PdfEncodings.convertToString(out.toByteArray(), null);
            final StringTokenizer tk = new StringTokenizer(s, "\r\n");
            while (tk.hasMoreTokens()) {
                final String line = tk.nextToken();
                if (line.startsWith("#")) {
                    continue;
                }
                final StringTokenizer t2 = new StringTokenizer(line, " ;\r\n\t\f");
                String name = null;
                String hex = null;
                if (!t2.hasMoreTokens()) {
                    continue;
                }
                name = t2.nextToken();
                if (!t2.hasMoreTokens()) {
                    continue;
                }
                hex = t2.nextToken();
                final Integer num = Integer.valueOf(hex, 16);
                GlyphList.unicode2names.put(num, name);
                GlyphList.names2unicode.put(name, new int[] { num });
            }
        }
        catch (final Exception e) {
            System.err.println("glyphlist.txt loading error: " + e.getMessage());
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
}
