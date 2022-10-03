package org.htmlparser.util;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.InputStream;
import org.htmlparser.util.sort.Ordered;
import org.htmlparser.util.sort.Sort;

public class Translate
{
    public static boolean DECODE_LINE_BY_LINE;
    public static boolean ENCODE_HEXADECIMAL;
    protected static final CharacterReference[] mCharacterReferences;
    protected static final int BREAKPOINT = 256;
    protected static final CharacterReference[] mCharacterList;
    
    private Translate() {
    }
    
    protected static int lookup(final CharacterReference[] array, final char ref, int lo, int hi) {
        int ret = -1;
        int num = hi - lo + 1;
        while (-1 == ret && lo <= hi) {
            final int half = num / 2;
            final int mid = lo + ((0x0 != (num & 0x1)) ? half : (half - 1));
            final int result = ref - array[mid].getCharacter();
            if (0 == result) {
                ret = mid;
            }
            else if (0 > result) {
                hi = mid - 1;
                num = ((0x0 != (num & 0x1)) ? half : (half - 1));
            }
            else {
                lo = mid + 1;
                num = half;
            }
        }
        if (-1 == ret) {
            ret = lo;
        }
        return ret;
    }
    
    public static CharacterReference lookup(final char character) {
        CharacterReference ret;
        if (character < '\u0100') {
            ret = Translate.mCharacterList[character];
        }
        else {
            final int index = lookup(Translate.mCharacterList, character, 256, Translate.mCharacterList.length - 1);
            if (index < Translate.mCharacterList.length) {
                ret = Translate.mCharacterList[index];
                if (character != ret.getCharacter()) {
                    ret = null;
                }
            }
            else {
                ret = null;
            }
        }
        return ret;
    }
    
    protected static CharacterReference lookup(final CharacterReference key) {
        CharacterReference ret = null;
        int index = Sort.bsearch(Translate.mCharacterReferences, key);
        final String string = key.getKernel();
        if (index < Translate.mCharacterReferences.length) {
            ret = Translate.mCharacterReferences[index];
            final String kernel = ret.getKernel();
            if (!string.regionMatches(0, kernel, 0, kernel.length())) {
                ret = null;
            }
        }
        if (null == ret) {
            final char character = string.charAt(0);
            while (--index >= 0) {
                final CharacterReference test = Translate.mCharacterReferences[index];
                final String kernel = test.getKernel();
                if (character != kernel.charAt(0)) {
                    break;
                }
                if (string.regionMatches(0, kernel, 0, kernel.length())) {
                    ret = test;
                    break;
                }
            }
        }
        return ret;
    }
    
    public static CharacterReference lookup(final String kernel, final int start, final int end) {
        final CharacterReferenceEx probe = new CharacterReferenceEx();
        probe.setKernel(kernel);
        probe.setStart(start);
        probe.setEnd(end);
        return lookup(probe);
    }
    
    public static String decode(final String string) {
        int amp;
        String ret;
        if (-1 == (amp = string.indexOf(38))) {
            ret = string;
        }
        else {
            CharacterReferenceEx key = null;
            int index = 0;
            final int length = string.length();
            final StringBuffer buffer = new StringBuffer(length);
            while (true) {
                if (index < amp) {
                    buffer.append(string.charAt(index++));
                }
                else {
                    if (++index < length) {
                        char character = string.charAt(index);
                        if ('#' == character) {
                            ++index;
                            int number = 0;
                            int radix = 0;
                            int i = index;
                            for (boolean done = false; i < length && !done; ++i) {
                                character = string.charAt(i);
                                switch (character) {
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
                                        if (0 == radix) {
                                            radix = 10;
                                        }
                                        number = number * radix + (character - '0');
                                        break;
                                    }
                                    case 'A':
                                    case 'B':
                                    case 'C':
                                    case 'D':
                                    case 'E':
                                    case 'F': {
                                        if (16 == radix) {
                                            number = number * radix + (character - 'A' + 10);
                                            break;
                                        }
                                        done = true;
                                        break;
                                    }
                                    case 'a':
                                    case 'b':
                                    case 'c':
                                    case 'd':
                                    case 'e':
                                    case 'f': {
                                        if (16 == radix) {
                                            number = number * radix + (character - 'a' + 10);
                                            break;
                                        }
                                        done = true;
                                        break;
                                    }
                                    case 'X':
                                    case 'x': {
                                        if (0 == radix) {
                                            radix = 16;
                                            break;
                                        }
                                        done = true;
                                        break;
                                    }
                                    case ';': {
                                        done = true;
                                        ++i;
                                        break;
                                    }
                                    default: {
                                        done = true;
                                        break;
                                    }
                                }
                                if (!done) {}
                            }
                            if (0 != number) {
                                buffer.append((char)number);
                                index = (amp = i);
                            }
                        }
                        else if (Character.isLetter(character)) {
                            int i = index + 1;
                            boolean done = false;
                            int semi = length;
                            while (i < length && !done) {
                                character = string.charAt(i);
                                if (';' == character) {
                                    done = true;
                                    semi = i;
                                    ++i;
                                }
                                else if (Character.isLetterOrDigit(character)) {
                                    ++i;
                                }
                                else {
                                    done = true;
                                    semi = i;
                                }
                            }
                            if (null == key) {
                                key = new CharacterReferenceEx();
                            }
                            key.setKernel(string);
                            key.setStart(index);
                            key.setEnd(semi);
                            final CharacterReference item = lookup(key);
                            if (null != item) {
                                buffer.append((char)item.getCharacter());
                                index += item.getKernel().length();
                                if (index < length && ';' == string.charAt(index)) {
                                    ++index;
                                }
                                amp = index;
                            }
                        }
                    }
                    while (amp < index) {
                        buffer.append(string.charAt(amp++));
                    }
                    if (index >= length || -1 == (amp = string.indexOf(38, index))) {
                        break;
                    }
                    continue;
                }
            }
            while (index < length) {
                buffer.append(string.charAt(index++));
            }
            ret = buffer.toString();
        }
        return ret;
    }
    
    public static String decode(final StringBuffer buffer) {
        return decode(buffer.toString());
    }
    
    public static void decode(final InputStream in, final PrintStream out) {
        try {
            Reader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
            }
            catch (final UnsupportedEncodingException use) {
                reader = new BufferedReader(new InputStreamReader(in));
            }
            final StringBuffer buffer = new StringBuffer(1024);
            boolean newlines = false;
            if (Translate.DECODE_LINE_BY_LINE) {
                int character;
                while (-1 != (character = reader.read())) {
                    if (13 == character || 10 == character) {
                        if (!newlines) {
                            final String string = decode(buffer.toString());
                            out.print(string);
                            buffer.setLength(0);
                            newlines = true;
                        }
                        buffer.append((char)character);
                    }
                    else {
                        if (newlines) {
                            out.print(buffer.toString());
                            buffer.setLength(0);
                            newlines = false;
                        }
                        buffer.append((char)character);
                    }
                }
            }
            else {
                int character;
                while (-1 != (character = reader.read())) {
                    buffer.append((char)character);
                }
            }
            if (0 != buffer.length()) {
                if (newlines) {
                    out.print(buffer.toString());
                }
                else {
                    final String string = decode(buffer.toString());
                    out.print(string);
                }
            }
        }
        catch (final IOException ioe) {
            out.println();
            out.println(ioe.getMessage());
        }
        finally {
            out.flush();
        }
    }
    
    public static String encode(final int character) {
        final StringBuffer ret = new StringBuffer(13);
        ret.append("&#");
        if (Translate.ENCODE_HEXADECIMAL) {
            ret.append("x");
            ret.append(Integer.toHexString(character));
        }
        else {
            ret.append(character);
        }
        ret.append(';');
        return ret.toString();
    }
    
    public static String encode(final String string) {
        final StringBuffer ret = new StringBuffer(string.length() * 6);
        for (int length = string.length(), i = 0; i < length; ++i) {
            final char c = string.charAt(i);
            final CharacterReference candidate = lookup(c);
            if (null != candidate) {
                ret.append('&');
                ret.append(candidate.getKernel());
                ret.append(';');
            }
            else if (c >= '\u007f') {
                ret.append("&#");
                if (Translate.ENCODE_HEXADECIMAL) {
                    ret.append("x");
                    ret.append(Integer.toHexString(c));
                }
                else {
                    ret.append((int)c);
                }
                ret.append(';');
            }
            else {
                ret.append(c);
            }
        }
        return ret.toString();
    }
    
    public static void encode(final InputStream in, final PrintStream out) {
        Reader reader;
        PrintWriter output;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "ISO-8859-1")));
        }
        catch (final UnsupportedEncodingException use) {
            reader = new BufferedReader(new InputStreamReader(in));
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
        }
        try {
            int index;
            while (-1 != (index = reader.read())) {
                final char c = (char)index;
                final CharacterReference candidate = lookup(c);
                if (null != candidate) {
                    output.print('&');
                    output.print(candidate.getKernel());
                    output.print(';');
                }
                else if (c >= '\u007f') {
                    output.print("&#");
                    if (Translate.ENCODE_HEXADECIMAL) {
                        output.print("x");
                        output.print(Integer.toHexString(c));
                    }
                    else {
                        output.print((int)c);
                    }
                    output.print(';');
                }
                else {
                    output.print(c);
                }
            }
        }
        catch (final IOException ioe) {
            output.println();
            output.println(ioe.getMessage());
        }
        finally {
            output.flush();
        }
    }
    
    public static void main(final String[] args) {
        final boolean encode = 0 < args.length && args[0].equalsIgnoreCase("-encode");
        if (encode) {
            encode(System.in, System.out);
        }
        else {
            decode(System.in, System.out);
        }
    }
    
    static {
        Translate.DECODE_LINE_BY_LINE = false;
        Translate.ENCODE_HEXADECIMAL = false;
        mCharacterReferences = new CharacterReference[] { new CharacterReference("nbsp", 160), new CharacterReference("iexcl", 161), new CharacterReference("cent", 162), new CharacterReference("pound", 163), new CharacterReference("curren", 164), new CharacterReference("yen", 165), new CharacterReference("brvbar", 166), new CharacterReference("sect", 167), new CharacterReference("uml", 168), new CharacterReference("copy", 169), new CharacterReference("ordf", 170), new CharacterReference("laquo", 171), new CharacterReference("not", 172), new CharacterReference("shy", 173), new CharacterReference("reg", 174), new CharacterReference("macr", 175), new CharacterReference("deg", 176), new CharacterReference("plusmn", 177), new CharacterReference("sup2", 178), new CharacterReference("sup3", 179), new CharacterReference("acute", 180), new CharacterReference("micro", 181), new CharacterReference("para", 182), new CharacterReference("middot", 183), new CharacterReference("cedil", 184), new CharacterReference("sup1", 185), new CharacterReference("ordm", 186), new CharacterReference("raquo", 187), new CharacterReference("frac14", 188), new CharacterReference("frac12", 189), new CharacterReference("frac34", 190), new CharacterReference("iquest", 191), new CharacterReference("Agrave", 192), new CharacterReference("Aacute", 193), new CharacterReference("Acirc", 194), new CharacterReference("Atilde", 195), new CharacterReference("Auml", 196), new CharacterReference("Aring", 197), new CharacterReference("AElig", 198), new CharacterReference("Ccedil", 199), new CharacterReference("Egrave", 200), new CharacterReference("Eacute", 201), new CharacterReference("Ecirc", 202), new CharacterReference("Euml", 203), new CharacterReference("Igrave", 204), new CharacterReference("Iacute", 205), new CharacterReference("Icirc", 206), new CharacterReference("Iuml", 207), new CharacterReference("ETH", 208), new CharacterReference("Ntilde", 209), new CharacterReference("Ograve", 210), new CharacterReference("Oacute", 211), new CharacterReference("Ocirc", 212), new CharacterReference("Otilde", 213), new CharacterReference("Ouml", 214), new CharacterReference("times", 215), new CharacterReference("Oslash", 216), new CharacterReference("Ugrave", 217), new CharacterReference("Uacute", 218), new CharacterReference("Ucirc", 219), new CharacterReference("Uuml", 220), new CharacterReference("Yacute", 221), new CharacterReference("THORN", 222), new CharacterReference("szlig", 223), new CharacterReference("agrave", 224), new CharacterReference("aacute", 225), new CharacterReference("acirc", 226), new CharacterReference("atilde", 227), new CharacterReference("auml", 228), new CharacterReference("aring", 229), new CharacterReference("aelig", 230), new CharacterReference("ccedil", 231), new CharacterReference("egrave", 232), new CharacterReference("eacute", 233), new CharacterReference("ecirc", 234), new CharacterReference("euml", 235), new CharacterReference("igrave", 236), new CharacterReference("iacute", 237), new CharacterReference("icirc", 238), new CharacterReference("iuml", 239), new CharacterReference("eth", 240), new CharacterReference("ntilde", 241), new CharacterReference("ograve", 242), new CharacterReference("oacute", 243), new CharacterReference("ocirc", 244), new CharacterReference("otilde", 245), new CharacterReference("ouml", 246), new CharacterReference("divide", 247), new CharacterReference("oslash", 248), new CharacterReference("ugrave", 249), new CharacterReference("uacute", 250), new CharacterReference("ucirc", 251), new CharacterReference("uuml", 252), new CharacterReference("yacute", 253), new CharacterReference("thorn", 254), new CharacterReference("yuml", 255), new CharacterReference("fnof", 402), new CharacterReference("Alpha", 913), new CharacterReference("Beta", 914), new CharacterReference("Gamma", 915), new CharacterReference("Delta", 916), new CharacterReference("Epsilon", 917), new CharacterReference("Zeta", 918), new CharacterReference("Eta", 919), new CharacterReference("Theta", 920), new CharacterReference("Iota", 921), new CharacterReference("Kappa", 922), new CharacterReference("Lambda", 923), new CharacterReference("Mu", 924), new CharacterReference("Nu", 925), new CharacterReference("Xi", 926), new CharacterReference("Omicron", 927), new CharacterReference("Pi", 928), new CharacterReference("Rho", 929), new CharacterReference("Sigma", 931), new CharacterReference("Tau", 932), new CharacterReference("Upsilon", 933), new CharacterReference("Phi", 934), new CharacterReference("Chi", 935), new CharacterReference("Psi", 936), new CharacterReference("Omega", 937), new CharacterReference("alpha", 945), new CharacterReference("beta", 946), new CharacterReference("gamma", 947), new CharacterReference("delta", 948), new CharacterReference("epsilon", 949), new CharacterReference("zeta", 950), new CharacterReference("eta", 951), new CharacterReference("theta", 952), new CharacterReference("iota", 953), new CharacterReference("kappa", 954), new CharacterReference("lambda", 955), new CharacterReference("mu", 956), new CharacterReference("nu", 957), new CharacterReference("xi", 958), new CharacterReference("omicron", 959), new CharacterReference("pi", 960), new CharacterReference("rho", 961), new CharacterReference("sigmaf", 962), new CharacterReference("sigma", 963), new CharacterReference("tau", 964), new CharacterReference("upsilon", 965), new CharacterReference("phi", 966), new CharacterReference("chi", 967), new CharacterReference("psi", 968), new CharacterReference("omega", 969), new CharacterReference("thetasym", 977), new CharacterReference("upsih", 978), new CharacterReference("piv", 982), new CharacterReference("bull", 8226), new CharacterReference("hellip", 8230), new CharacterReference("prime", 8242), new CharacterReference("Prime", 8243), new CharacterReference("oline", 8254), new CharacterReference("frasl", 8260), new CharacterReference("weierp", 8472), new CharacterReference("image", 8465), new CharacterReference("real", 8476), new CharacterReference("trade", 8482), new CharacterReference("alefsym", 8501), new CharacterReference("larr", 8592), new CharacterReference("uarr", 8593), new CharacterReference("rarr", 8594), new CharacterReference("darr", 8595), new CharacterReference("harr", 8596), new CharacterReference("crarr", 8629), new CharacterReference("lArr", 8656), new CharacterReference("uArr", 8657), new CharacterReference("rArr", 8658), new CharacterReference("dArr", 8659), new CharacterReference("hArr", 8660), new CharacterReference("forall", 8704), new CharacterReference("part", 8706), new CharacterReference("exist", 8707), new CharacterReference("empty", 8709), new CharacterReference("nabla", 8711), new CharacterReference("isin", 8712), new CharacterReference("notin", 8713), new CharacterReference("ni", 8715), new CharacterReference("prod", 8719), new CharacterReference("sum", 8721), new CharacterReference("minus", 8722), new CharacterReference("lowast", 8727), new CharacterReference("radic", 8730), new CharacterReference("prop", 8733), new CharacterReference("infin", 8734), new CharacterReference("ang", 8736), new CharacterReference("and", 8743), new CharacterReference("or", 8744), new CharacterReference("cap", 8745), new CharacterReference("cup", 8746), new CharacterReference("int", 8747), new CharacterReference("there4", 8756), new CharacterReference("sim", 8764), new CharacterReference("cong", 8773), new CharacterReference("asymp", 8776), new CharacterReference("ne", 8800), new CharacterReference("equiv", 8801), new CharacterReference("le", 8804), new CharacterReference("ge", 8805), new CharacterReference("sub", 8834), new CharacterReference("sup", 8835), new CharacterReference("nsub", 8836), new CharacterReference("sube", 8838), new CharacterReference("supe", 8839), new CharacterReference("oplus", 8853), new CharacterReference("otimes", 8855), new CharacterReference("perp", 8869), new CharacterReference("sdot", 8901), new CharacterReference("lceil", 8968), new CharacterReference("rceil", 8969), new CharacterReference("lfloor", 8970), new CharacterReference("rfloor", 8971), new CharacterReference("lang", 9001), new CharacterReference("rang", 9002), new CharacterReference("loz", 9674), new CharacterReference("spades", 9824), new CharacterReference("clubs", 9827), new CharacterReference("hearts", 9829), new CharacterReference("diams", 9830), new CharacterReference("quot", 34), new CharacterReference("amp", 38), new CharacterReference("lt", 60), new CharacterReference("gt", 62), new CharacterReference("OElig", 338), new CharacterReference("oelig", 339), new CharacterReference("Scaron", 352), new CharacterReference("scaron", 353), new CharacterReference("Yuml", 376), new CharacterReference("circ", 710), new CharacterReference("tilde", 732), new CharacterReference("ensp", 8194), new CharacterReference("emsp", 8195), new CharacterReference("thinsp", 8201), new CharacterReference("zwnj", 8204), new CharacterReference("zwj", 8205), new CharacterReference("lrm", 8206), new CharacterReference("rlm", 8207), new CharacterReference("ndash", 8211), new CharacterReference("mdash", 8212), new CharacterReference("lsquo", 8216), new CharacterReference("rsquo", 8217), new CharacterReference("sbquo", 8218), new CharacterReference("ldquo", 8220), new CharacterReference("rdquo", 8221), new CharacterReference("bdquo", 8222), new CharacterReference("dagger", 8224), new CharacterReference("Dagger", 8225), new CharacterReference("permil", 8240), new CharacterReference("lsaquo", 8249), new CharacterReference("rsaquo", 8250), new CharacterReference("euro", 8364) };
        int index = 0;
        for (int i = 0; i < Translate.mCharacterReferences.length; ++i) {
            if (Translate.mCharacterReferences[i].getCharacter() < 256) {
                ++index;
            }
        }
        mCharacterList = new CharacterReference[256 + Translate.mCharacterReferences.length - index];
        index = 256;
        for (int i = 0; i < Translate.mCharacterReferences.length; ++i) {
            final CharacterReference item = Translate.mCharacterReferences[i];
            final int character = Translate.mCharacterReferences[i].getCharacter();
            if (character < 256) {
                Translate.mCharacterList[character] = item;
            }
            else {
                int x;
                for (x = 256; x < index && Translate.mCharacterList[x].getCharacter() <= character; ++x) {}
                for (int y = index - 1; y >= x; --y) {
                    Translate.mCharacterList[y + 1] = Translate.mCharacterList[y];
                }
                Translate.mCharacterList[x] = item;
                ++index;
            }
        }
        Sort.QuickSort(Translate.mCharacterReferences);
    }
}
