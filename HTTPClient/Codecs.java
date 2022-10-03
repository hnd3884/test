package HTTPClient;

import java.net.URL;
import java.net.URLConnection;
import java.io.EOFException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Vector;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class Codecs
{
    private static BitSet BoundChar;
    private static BitSet EBCDICUnsafeChar;
    private static byte[] Base64EncMap;
    private static byte[] Base64DecMap;
    private static char[] UUEncMap;
    private static byte[] UUDecMap;
    private static final String ContDisp = "\r\nContent-Disposition: form-data; name=\"";
    private static final String FileName = "\"; filename=\"";
    private static final String ContType = "\r\nContent-Type: ";
    private static final String Boundary = "\r\n----------ieoau._._+2_8_GoodLuck8.3-dskdfJwSJKl234324jfLdsjfdAuaoei-----";
    private static NVPair[] dummy;
    
    private Codecs() {
    }
    
    public static final String base64Encode(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return new String(base64Encode(str.getBytes("8859_1")), "8859_1");
        }
        catch (final UnsupportedEncodingException uee) {
            throw new Error(uee.toString());
        }
    }
    
    public static final byte[] base64Encode(final byte[] data) {
        if (data == null) {
            return null;
        }
        final byte[] dest = new byte[(data.length + 2) / 3 * 4];
        int sidx = 0;
        int didx = 0;
        while (sidx < data.length - 2) {
            dest[didx++] = Codecs.Base64EncMap[data[sidx] >>> 2 & 0x3F];
            dest[didx++] = Codecs.Base64EncMap[(data[sidx + 1] >>> 4 & 0xF) | (data[sidx] << 4 & 0x3F)];
            dest[didx++] = Codecs.Base64EncMap[(data[sidx + 2] >>> 6 & 0x3) | (data[sidx + 1] << 2 & 0x3F)];
            dest[didx++] = Codecs.Base64EncMap[data[sidx + 2] & 0x3F];
            sidx += 3;
        }
        if (sidx < data.length) {
            dest[didx++] = Codecs.Base64EncMap[data[sidx] >>> 2 & 0x3F];
            if (sidx < data.length - 1) {
                dest[didx++] = Codecs.Base64EncMap[(data[sidx + 1] >>> 4 & 0xF) | (data[sidx] << 4 & 0x3F)];
                dest[didx++] = Codecs.Base64EncMap[data[sidx + 1] << 2 & 0x3F];
            }
            else {
                dest[didx++] = Codecs.Base64EncMap[data[sidx] << 4 & 0x3F];
            }
        }
        while (didx < dest.length) {
            dest[didx] = 61;
            ++didx;
        }
        return dest;
    }
    
    public static final String base64Decode(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return new String(base64Decode(str.getBytes("8859_1")), "8859_1");
        }
        catch (final UnsupportedEncodingException uee) {
            throw new Error(uee.toString());
        }
    }
    
    public static final byte[] base64Decode(final byte[] data) {
        if (data == null) {
            return null;
        }
        int tail;
        for (tail = data.length; data[tail - 1] == 61; --tail) {}
        final byte[] dest = new byte[tail - data.length / 4];
        for (int idx = 0; idx < data.length; ++idx) {
            data[idx] = Codecs.Base64DecMap[data[idx]];
        }
        int sidx = 0;
        int didx;
        for (didx = 0; didx < dest.length - 2; didx += 3) {
            dest[didx] = (byte)((data[sidx] << 2 & 0xFF) | (data[sidx + 1] >>> 4 & 0x3));
            dest[didx + 1] = (byte)((data[sidx + 1] << 4 & 0xFF) | (data[sidx + 2] >>> 2 & 0xF));
            dest[didx + 2] = (byte)((data[sidx + 2] << 6 & 0xFF) | (data[sidx + 3] & 0x3F));
            sidx += 4;
        }
        if (didx < dest.length) {
            dest[didx] = (byte)((data[sidx] << 2 & 0xFF) | (data[sidx + 1] >>> 4 & 0x3));
        }
        if (++didx < dest.length) {
            dest[didx] = (byte)((data[sidx + 1] << 4 & 0xFF) | (data[sidx + 2] >>> 2 & 0xF));
        }
        return dest;
    }
    
    public static final char[] uuencode(final byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length == 0) {
            return new char[0];
        }
        final int line_len = 45;
        final char[] nl = System.getProperty("line.separator", "\n").toCharArray();
        final char[] dest = new char[(data.length + 2) / 3 * 4 + (data.length + line_len - 1) / line_len * (nl.length + 1)];
        int sidx = 0;
        int didx = 0;
        while (sidx + line_len < data.length) {
            dest[didx++] = Codecs.UUEncMap[line_len];
            for (int end = sidx + line_len; sidx < end; sidx += 3) {
                dest[didx++] = Codecs.UUEncMap[data[sidx] >>> 2 & 0x3F];
                dest[didx++] = Codecs.UUEncMap[(data[sidx + 1] >>> 4 & 0xF) | (data[sidx] << 4 & 0x3F)];
                dest[didx++] = Codecs.UUEncMap[(data[sidx + 2] >>> 6 & 0x3) | (data[sidx + 1] << 2 & 0x3F)];
                dest[didx++] = Codecs.UUEncMap[data[sidx + 2] & 0x3F];
            }
            for (int idx = 0; idx < nl.length; ++idx) {
                dest[didx++] = nl[idx];
            }
        }
        dest[didx++] = Codecs.UUEncMap[data.length - sidx];
        while (sidx + 2 < data.length) {
            dest[didx++] = Codecs.UUEncMap[data[sidx] >>> 2 & 0x3F];
            dest[didx++] = Codecs.UUEncMap[(data[sidx + 1] >>> 4 & 0xF) | (data[sidx] << 4 & 0x3F)];
            dest[didx++] = Codecs.UUEncMap[(data[sidx + 2] >>> 6 & 0x3) | (data[sidx + 1] << 2 & 0x3F)];
            dest[didx++] = Codecs.UUEncMap[data[sidx + 2] & 0x3F];
            sidx += 3;
        }
        if (sidx < data.length - 1) {
            dest[didx++] = Codecs.UUEncMap[data[sidx] >>> 2 & 0x3F];
            dest[didx++] = Codecs.UUEncMap[(data[sidx + 1] >>> 4 & 0xF) | (data[sidx] << 4 & 0x3F)];
            dest[didx++] = Codecs.UUEncMap[data[sidx + 1] << 2 & 0x3F];
            dest[didx++] = Codecs.UUEncMap[0];
        }
        else if (sidx < data.length) {
            dest[didx++] = Codecs.UUEncMap[data[sidx] >>> 2 & 0x3F];
            dest[didx++] = Codecs.UUEncMap[data[sidx] << 4 & 0x3F];
            dest[didx++] = Codecs.UUEncMap[0];
            dest[didx++] = Codecs.UUEncMap[0];
        }
        for (int idx2 = 0; idx2 < nl.length; ++idx2) {
            dest[didx++] = nl[idx2];
        }
        if (didx != dest.length) {
            throw new Error("Calculated " + dest.length + " chars but wrote " + didx + " chars!");
        }
        return dest;
    }
    
    private static final byte[] uudecode(final BufferedReader rdr) throws ParseException, IOException {
        String line;
        while ((line = rdr.readLine()) != null && !line.startsWith("begin ")) {}
        if (line == null) {
            throw new ParseException("'begin' line not found");
        }
        final StringTokenizer tok = new StringTokenizer(line);
        tok.nextToken();
        try {
            final int file_mode = Integer.parseInt(tok.nextToken(), 8);
        }
        catch (final Exception ex) {
            throw new ParseException("Invalid mode on line: " + line);
        }
        try {
            final String file_name = tok.nextToken();
        }
        catch (final NoSuchElementException ex2) {
            throw new ParseException("No file name found on line: " + line);
        }
        byte[] body = new byte[1000];
        int off = 0;
        while ((line = rdr.readLine()) != null && !line.equals("end")) {
            final byte[] tmp = uudecode(line.toCharArray());
            if (off + tmp.length > body.length) {
                body = Util.resizeArray(body, off + 1000);
            }
            System.arraycopy(tmp, 0, body, off, tmp.length);
            off += tmp.length;
        }
        if (line == null) {
            throw new ParseException("'end' line not found");
        }
        return Util.resizeArray(body, off);
    }
    
    public static final byte[] uudecode(final char[] data) {
        if (data == null) {
            return null;
        }
        final byte[] dest = new byte[data.length / 4 * 3];
        int sidx = 0;
        int didx = 0;
        while (sidx < data.length) {
            int len;
            int end;
            byte A;
            byte B;
            byte C;
            byte D;
            for (len = Codecs.UUDecMap[data[sidx++]], end = didx + len; didx < end - 2; dest[didx++] = (byte)((A << 2 & 0xFF) | (B >>> 4 & 0x3)), dest[didx++] = (byte)((B << 4 & 0xFF) | (C >>> 2 & 0xF)), dest[didx++] = (byte)((C << 6 & 0xFF) | (D & 0x3F)), sidx += 4) {
                A = Codecs.UUDecMap[data[sidx]];
                B = Codecs.UUDecMap[data[sidx + 1]];
                C = Codecs.UUDecMap[data[sidx + 2]];
                D = Codecs.UUDecMap[data[sidx + 3]];
            }
            if (didx < end) {
                A = Codecs.UUDecMap[data[sidx]];
                B = Codecs.UUDecMap[data[sidx + 1]];
                dest[didx++] = (byte)((A << 2 & 0xFF) | (B >>> 4 & 0x3));
            }
            if (didx < end) {
                final byte B2 = Codecs.UUDecMap[data[sidx + 1]];
                final byte C2 = Codecs.UUDecMap[data[sidx + 2]];
                dest[didx++] = (byte)((B2 << 4 & 0xFF) | (C2 >>> 2 & 0xF));
            }
            while (sidx < data.length && data[sidx] != '\n') {
                if (data[sidx] == '\r') {
                    break;
                }
                ++sidx;
            }
            while (sidx < data.length && (data[sidx] == '\n' || data[sidx] == '\r')) {
                ++sidx;
            }
        }
        return Util.resizeArray(dest, didx);
    }
    
    public static final String quotedPrintableEncode(final String str) {
        if (str == null) {
            return null;
        }
        final char[] map = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final char[] nl = System.getProperty("line.separator", "\n").toCharArray();
        char[] res = new char[(int)(str.length() * 1.5)];
        final char[] src = str.toCharArray();
        int cnt = 0;
        int didx = 1;
        final int last = 0;
        for (int slen = str.length(), sidx = 0; sidx < slen; ++sidx) {
            final char ch = src[sidx];
            if (ch == nl[0] && match(src, sidx, nl)) {
                if (res[didx - 1] == ' ') {
                    res[didx - 1] = '=';
                    res[didx++] = '2';
                    res[didx++] = '0';
                }
                else if (res[didx - 1] == '\t') {
                    res[didx - 1] = '=';
                    res[didx++] = '0';
                    res[didx++] = '9';
                }
                res[didx++] = '\r';
                res[didx++] = '\n';
                sidx += nl.length - 1;
                cnt = didx;
            }
            else if (ch > '~' || (ch < ' ' && ch != '\t') || ch == '=' || Codecs.EBCDICUnsafeChar.get(ch)) {
                res[didx++] = '=';
                res[didx++] = map[(ch & '\u00f0') >>> 4];
                res[didx++] = map[ch & '\u000f'];
            }
            else {
                res[didx++] = ch;
            }
            if (didx > cnt + 70) {
                res[didx++] = '=';
                res[didx++] = '\r';
                res[didx++] = '\n';
                cnt = didx;
            }
            if (didx > res.length - 5) {
                res = Util.resizeArray(res, res.length + 500);
            }
        }
        return String.valueOf(res, 1, didx - 1);
    }
    
    private static final boolean match(final char[] str, final int start, final char[] arr) {
        if (str.length < start + arr.length) {
            return false;
        }
        for (int idx = 1; idx < arr.length; ++idx) {
            if (str[start + idx] != arr[idx]) {
                return false;
            }
        }
        return true;
    }
    
    public static final String quotedPrintableDecode(final String str) throws ParseException {
        if (str == null) {
            return null;
        }
        char[] res = new char[(int)(str.length() * 1.1)];
        final char[] src = str.toCharArray();
        final char[] nl = System.getProperty("line.separator", "\n").toCharArray();
        int last = 0;
        int didx = 0;
        final int slen = str.length();
        int sidx = 0;
        while (sidx < slen) {
            final char ch = src[sidx++];
            if (ch == '=') {
                if (sidx >= slen - 1) {
                    throw new ParseException("Premature end of input detected");
                }
                if (src[sidx] == '\n' || src[sidx] == '\r') {
                    ++sidx;
                    if (src[sidx - 1] == '\r' && src[sidx] == '\n') {
                        ++sidx;
                    }
                }
                else {
                    final int hi = Character.digit(src[sidx], 16);
                    final int lo = Character.digit(src[sidx + 1], 16);
                    if ((hi | lo) < 0) {
                        throw new ParseException(String.valueOf(new String(src, sidx - 1, 3)) + " is an invalid code");
                    }
                    final char repl = (char)(hi << 4 | lo);
                    sidx += 2;
                    res[didx++] = repl;
                }
                last = didx;
            }
            else if (ch == '\n' || ch == '\r') {
                if (ch == '\r' && sidx < slen && src[sidx] == '\n') {
                    ++sidx;
                }
                for (int idx = 0; idx < nl.length; ++idx) {
                    res[last++] = nl[idx];
                }
                didx = last;
            }
            else {
                res[didx++] = ch;
                if (ch != ' ' && ch != '\t') {
                    last = didx;
                }
            }
            if (didx > res.length - nl.length - 2) {
                res = Util.resizeArray(res, res.length + 500);
            }
        }
        return new String(res, 0, didx);
    }
    
    public static final String URLEncode(final String str) {
        if (str == null) {
            return null;
        }
        return URLEncoder.encode(str);
    }
    
    public static final String URLDecode(final String str) throws ParseException {
        if (str == null) {
            return null;
        }
        final char[] res = new char[str.length()];
        int didx = 0;
        for (int sidx = 0; sidx < str.length(); ++sidx) {
            final char ch = str.charAt(sidx);
            if (ch == '+') {
                res[didx++] = ' ';
            }
            else {
                if (ch == '%') {
                    try {
                        res[didx++] = (char)Integer.parseInt(str.substring(sidx + 1, sidx + 3), 16);
                        sidx += 2;
                        continue;
                    }
                    catch (final NumberFormatException ex) {
                        throw new ParseException(String.valueOf(str.substring(sidx, sidx + 3)) + " is an invalid code");
                    }
                }
                res[didx++] = ch;
            }
        }
        return String.valueOf(res, 0, didx);
    }
    
    public static final NVPair[] mpFormDataDecode(final byte[] data, final String cont_type, final String dir) throws IOException, ParseException {
        return mpFormDataDecode(data, cont_type, dir, null);
    }
    
    public static final NVPair[] mpFormDataDecode(final byte[] data, final String cont_type, final String dir, final FilenameMangler mangler) throws IOException, ParseException {
        final String bndstr = Util.getParameter("boundary", cont_type);
        if (bndstr == null) {
            throw new ParseException("'boundary' parameter not found in Content-type: " + cont_type);
        }
        final byte[] srtbndry = ("--" + bndstr + "\r\n").getBytes("8859_1");
        final byte[] boundary = ("\r\n--" + bndstr + "\r\n").getBytes("8859_1");
        final byte[] endbndry = ("\r\n--" + bndstr + "--").getBytes("8859_1");
        final int[] bs = Util.compile_search(srtbndry);
        final int[] bc = Util.compile_search(boundary);
        final int[] be = Util.compile_search(endbndry);
        int start = Util.findStr(srtbndry, bs, data, 0, data.length);
        if (start == -1) {
            throw new ParseException("Starting boundary not found: " + new String(srtbndry, "8859_1"));
        }
        start += srtbndry.length;
        NVPair[] res = new NVPair[10];
        boolean done = false;
        int idx = 0;
        while (!done) {
            int end = Util.findStr(boundary, bc, data, start, data.length);
            if (end == -1) {
                end = Util.findStr(endbndry, be, data, start, data.length);
                if (end == -1) {
                    throw new ParseException("Ending boundary not found: " + new String(endbndry, "8859_1"));
                }
                done = true;
            }
            String name = null;
            String filename = null;
            String cont_disp = null;
            while (true) {
                int n = findEOL(data, start) + 2;
                if (n - 2 > start) {
                    String hdr;
                    byte ch;
                    for (hdr = new String(data, start, n - 2 - start, "8859_1"), start = n; n < data.length - 1 && ((ch = data[n]) == 32 || ch == 9); n = findEOL(data, start) + 2, hdr = String.valueOf(hdr) + new String(data, start, n - 2 - start, "8859_1"), start = n) {}
                    if (!hdr.regionMatches(true, 0, "Content-Disposition", 0, 19)) {
                        continue;
                    }
                    final Vector pcd = Util.parseHeader(hdr.substring(hdr.indexOf(58) + 1));
                    final HttpHeaderElement elem = Util.getElement(pcd, "form-data");
                    if (elem == null) {
                        throw new ParseException("Expected 'Content-Disposition: form-data' in line: " + hdr);
                    }
                    final NVPair[] params = elem.getParams();
                    filename = (name = null);
                    for (int pidx = 0; pidx < params.length; ++pidx) {
                        if (params[pidx].getName().equalsIgnoreCase("name")) {
                            name = params[pidx].getValue();
                        }
                        if (params[pidx].getName().equalsIgnoreCase("filename")) {
                            filename = params[pidx].getValue();
                        }
                    }
                    if (name == null) {
                        throw new ParseException("'name' parameter not found in header: " + hdr);
                    }
                    cont_disp = hdr;
                }
                else {
                    start += 2;
                    if (start > end) {
                        throw new ParseException("End of header not found at offset " + end);
                    }
                    if (cont_disp == null) {
                        throw new ParseException("Missing 'Content-Disposition' header at offset " + start);
                    }
                    String value;
                    if (filename != null) {
                        if (mangler != null) {
                            filename = mangler.mangleFilename(filename, name);
                        }
                        if (filename != null && filename.length() > 0) {
                            final File file = new File(dir, filename);
                            final FileOutputStream out = new FileOutputStream(file);
                            out.write(data, start, end - start);
                            out.close();
                        }
                        value = filename;
                    }
                    else {
                        value = new String(data, start, end - start, "8859_1");
                    }
                    if (idx >= res.length) {
                        res = Util.resizeArray(res, idx + 10);
                    }
                    res[idx] = new NVPair(name, value);
                    start = end + boundary.length;
                    ++idx;
                    break;
                }
            }
        }
        return Util.resizeArray(res, idx);
    }
    
    private static final int findEOL(final byte[] arr, int off) {
        while (off < arr.length - 1 && (arr[off++] != 13 || arr[off] != 10)) {}
        return off - 1;
    }
    
    public static final byte[] mpFormDataEncode(final NVPair[] opts, final NVPair[] files, final NVPair[] ct_hdr) throws IOException {
        return mpFormDataEncode(opts, files, ct_hdr, null);
    }
    
    public static final byte[] mpFormDataEncode(NVPair[] opts, NVPair[] files, final NVPair[] ct_hdr, final FilenameMangler mangler) throws IOException {
        final byte[] boundary = "\r\n----------ieoau._._+2_8_GoodLuck8.3-dskdfJwSJKl234324jfLdsjfdAuaoei-----".getBytes("8859_1");
        final byte[] cont_disp = "\r\nContent-Disposition: form-data; name=\"".getBytes("8859_1");
        final byte[] cont_type = "\r\nContent-Type: ".getBytes("8859_1");
        final byte[] filename = "\"; filename=\"".getBytes("8859_1");
        int len = 0;
        final int hdr_len = boundary.length + cont_disp.length + 1 + 2 + 2;
        if (opts == null) {
            opts = Codecs.dummy;
        }
        if (files == null) {
            files = Codecs.dummy;
        }
        for (int idx = 0; idx < opts.length; ++idx) {
            if (opts[idx] != null) {
                len += hdr_len + opts[idx].getName().length() + opts[idx].getValue().length();
            }
        }
        for (int idx2 = 0; idx2 < files.length; ++idx2) {
            if (files[idx2] != null) {
                final File file = new File(files[idx2].getValue());
                String fname = file.getName();
                if (mangler != null) {
                    fname = mangler.mangleFilename(fname, files[idx2].getName());
                }
                if (fname != null) {
                    len += hdr_len + files[idx2].getName().length() + filename.length;
                    len += (int)(fname.length() + file.length());
                    final String ct = CT.getContentType(file.getName());
                    if (ct != null) {
                        len += cont_type.length + ct.length();
                    }
                }
            }
        }
        if (len == 0) {
            ct_hdr[0] = new NVPair("Content-Type", "application/octet-stream");
            return new byte[0];
        }
        len -= 2;
        len += boundary.length + 2 + 2;
        final byte[] res = new byte[len];
        int pos = 0;
        int new_c = 808464432;
    Label_1167:
        while (new_c != 2054847098) {
            pos = 0;
            while (!Codecs.BoundChar.get(new_c & 0xFF)) {
                ++new_c;
            }
            while (!Codecs.BoundChar.get(new_c >> 8 & 0xFF)) {
                new_c += 256;
            }
            while (!Codecs.BoundChar.get(new_c >> 16 & 0xFF)) {
                new_c += 65536;
            }
            while (!Codecs.BoundChar.get(new_c >> 24 & 0xFF)) {
                new_c += 16777216;
            }
            boundary[40] = (byte)(new_c & 0xFF);
            boundary[42] = (byte)(new_c >> 8 & 0xFF);
            boundary[44] = (byte)(new_c >> 16 & 0xFF);
            boundary[46] = (byte)(new_c >> 24 & 0xFF);
            int off = 2;
            final int[] bnd_cmp = Util.compile_search(boundary);
        Label_1164:
            while (true) {
                for (int idx3 = 0; idx3 < opts.length; ++idx3) {
                    if (opts[idx3] != null) {
                        System.arraycopy(boundary, off, res, pos, boundary.length - off);
                        pos += boundary.length - off;
                        off = 0;
                        final int start = pos;
                        System.arraycopy(cont_disp, 0, res, pos, cont_disp.length);
                        pos += cont_disp.length;
                        final int nlen = opts[idx3].getName().length();
                        System.arraycopy(opts[idx3].getName().getBytes("8859_1"), 0, res, pos, nlen);
                        pos += nlen;
                        res[pos++] = 34;
                        res[pos++] = 13;
                        res[pos++] = 10;
                        res[pos++] = 13;
                        res[pos++] = 10;
                        final int vlen = opts[idx3].getValue().length();
                        System.arraycopy(opts[idx3].getValue().getBytes("8859_1"), 0, res, pos, vlen);
                        pos += vlen;
                        if (pos - start >= boundary.length && Util.findStr(boundary, bnd_cmp, res, start, pos) != -1) {
                            ++new_c;
                            continue Label_1167;
                        }
                    }
                }
                for (int idx4 = 0; idx4 < files.length; ++idx4) {
                    if (files[idx4] != null) {
                        final File file2 = new File(files[idx4].getValue());
                        String fname2 = file2.getName();
                        if (mangler != null) {
                            fname2 = mangler.mangleFilename(fname2, files[idx4].getName());
                        }
                        if (fname2 != null) {
                            System.arraycopy(boundary, off, res, pos, boundary.length - off);
                            pos += boundary.length - off;
                            off = 0;
                            final int start2 = pos;
                            System.arraycopy(cont_disp, 0, res, pos, cont_disp.length);
                            pos += cont_disp.length;
                            int nlen2 = files[idx4].getName().length();
                            System.arraycopy(files[idx4].getName().getBytes("8859_1"), 0, res, pos, nlen2);
                            pos += nlen2;
                            System.arraycopy(filename, 0, res, pos, filename.length);
                            pos += filename.length;
                            nlen2 = fname2.length();
                            System.arraycopy(fname2.getBytes("8859_1"), 0, res, pos, nlen2);
                            pos += nlen2;
                            res[pos++] = 34;
                            final String ct2 = CT.getContentType(file2.getName());
                            if (ct2 != null) {
                                System.arraycopy(cont_type, 0, res, pos, cont_type.length);
                                pos += cont_type.length;
                                System.arraycopy(ct2.getBytes("8859_1"), 0, res, pos, ct2.length());
                                pos += ct2.length();
                            }
                            res[pos++] = 13;
                            res[pos++] = 10;
                            res[pos++] = 13;
                            res[pos++] = 10;
                            nlen2 = (int)file2.length();
                            final FileInputStream fin = new FileInputStream(file2);
                            while (nlen2 > 0) {
                                final int got = fin.read(res, pos, nlen2);
                                nlen2 -= got;
                                pos += got;
                            }
                            fin.close();
                            if (pos - start2 >= boundary.length && Util.findStr(boundary, bnd_cmp, res, start2, pos) != -1) {
                                continue Label_1164;
                            }
                        }
                    }
                }
                break;
            }
            break;
        }
        System.arraycopy(boundary, 0, res, pos, boundary.length);
        pos += boundary.length;
        res[pos++] = 45;
        res[pos++] = 45;
        res[pos++] = 13;
        res[pos++] = 10;
        if (pos != len) {
            throw new Error("Calculated " + len + " bytes but wrote " + pos + " bytes!");
        }
        ct_hdr[0] = new NVPair("Content-Type", "multipart/form-data; boundary=" + new String(boundary, 4, boundary.length - 4, "8859_1"));
        return res;
    }
    
    public static final String nv2query(final NVPair[] pairs) {
        if (pairs == null) {
            return null;
        }
        final StringBuffer qbuf = new StringBuffer();
        for (int idx = 0; idx < pairs.length; ++idx) {
            if (pairs[idx] != null) {
                qbuf.append(String.valueOf(URLEncode(pairs[idx].getName())) + "=" + URLEncode(pairs[idx].getValue()) + "&");
            }
        }
        if (qbuf.length() > 0) {
            qbuf.setLength(qbuf.length() - 1);
        }
        return qbuf.toString();
    }
    
    public static final NVPair[] query2nv(final String query) throws ParseException {
        if (query == null) {
            return null;
        }
        int idx = -1;
        int cnt = 1;
        while ((idx = query.indexOf(38, idx + 1)) != -1) {
            ++cnt;
        }
        final NVPair[] pairs = new NVPair[cnt];
        idx = 0;
        for (cnt = 0; cnt < pairs.length; ++cnt) {
            final int eq = query.indexOf(61, idx);
            int end = query.indexOf(38, idx);
            if (end == -1) {
                end = query.length();
            }
            if (eq == -1 || eq >= end) {
                throw new ParseException("'=' missing in " + query.substring(idx, end));
            }
            pairs[cnt] = new NVPair(URLDecode(query.substring(idx, eq)), URLDecode(query.substring(eq + 1, end)));
            idx = end + 1;
        }
        return pairs;
    }
    
    public static final byte[] chunkedEncode(final byte[] data, final NVPair[] ftrs, final boolean last) {
        return chunkedEncode(data, 0, (data == null) ? 0 : data.length, ftrs, last);
    }
    
    public static final byte[] chunkedEncode(byte[] data, final int off, int len, NVPair[] ftrs, final boolean last) {
        if (data == null) {
            data = new byte[0];
            len = 0;
        }
        if (last && ftrs == null) {
            ftrs = new NVPair[0];
        }
        final String hex_len = Integer.toString(len, 16);
        int res_len = 0;
        if (len > 0) {
            res_len += hex_len.length() + 2 + len + 2;
        }
        if (last) {
            res_len += 3;
            for (int idx = 0; idx < ftrs.length; ++idx) {
                res_len += ftrs[idx].getName().length() + 2 + ftrs[idx].getValue().length() + 2;
            }
            res_len += 2;
        }
        final byte[] res = new byte[res_len];
        int r_off = 0;
        if (len > 0) {
            final int hlen = hex_len.length();
            try {
                System.arraycopy(hex_len.getBytes("8859_1"), 0, res, r_off, hlen);
            }
            catch (final UnsupportedEncodingException uee) {
                throw new Error(uee.toString());
            }
            r_off += hlen;
            res[r_off++] = 13;
            res[r_off++] = 10;
            System.arraycopy(data, off, res, r_off, len);
            r_off += len;
            res[r_off++] = 13;
            res[r_off++] = 10;
        }
        if (last) {
            res[r_off++] = 48;
            res[r_off++] = 13;
            res[r_off++] = 10;
            for (int idx2 = 0; idx2 < ftrs.length; ++idx2) {
                final int nlen = ftrs[idx2].getName().length();
                try {
                    System.arraycopy(ftrs[idx2].getName().getBytes("8859_1"), 0, res, r_off, nlen);
                }
                catch (final UnsupportedEncodingException uee2) {
                    throw new Error(uee2.toString());
                }
                r_off += nlen;
                res[r_off++] = 58;
                res[r_off++] = 32;
                final int vlen = ftrs[idx2].getValue().length();
                try {
                    System.arraycopy(ftrs[idx2].getValue().getBytes("8859_1"), 0, res, r_off, vlen);
                }
                catch (final UnsupportedEncodingException uee3) {
                    throw new Error(uee3.toString());
                }
                r_off += vlen;
                res[r_off++] = 13;
                res[r_off++] = 10;
            }
            res[r_off++] = 13;
            res[r_off++] = 10;
        }
        if (r_off != res.length) {
            throw new Error("Calculated " + res.length + " bytes but wrote " + r_off + " bytes!");
        }
        return res;
    }
    
    public static final Object chunkedDecode(final InputStream input) throws ParseException, IOException {
        final long clen = getChunkLength(input);
        if (clen > 2147483647L) {
            throw new ParseException("Can't deal with chunk lengths greater Integer.MAX_VALUE: " + clen + " > " + Integer.MAX_VALUE);
        }
        if (clen <= 0L) {
            NVPair[] res = new NVPair[0];
            final BufferedReader reader = new BufferedReader(new InputStreamReader(input, "8859_1"));
            String line;
            while ((line = reader.readLine()) != null && line.length() > 0) {
                final int colon = line.indexOf(58);
                if (colon == -1) {
                    throw new ParseException("Error in Footer format: no ':' found in '" + line + "'");
                }
                res = Util.resizeArray(res, res.length + 1);
                res[res.length - 1] = new NVPair(line.substring(0, colon).trim(), line.substring(colon + 1).trim());
            }
            return res;
        }
        byte[] res2;
        int off;
        int len;
        for (res2 = new byte[(int)clen], off = 0, len = 0; len != -1 && off < res2.length; len = input.read(res2, off, res2.length - off), off += len) {}
        if (len == -1) {
            throw new ParseException("Premature EOF while reading chunk;Expected: " + res2.length + " Bytes, " + "Received: " + (off + 1) + " Bytes");
        }
        input.read();
        input.read();
        return res2;
    }
    
    static final long getChunkLength(final InputStream input) throws ParseException, IOException {
        final byte[] hex_len = new byte[16];
        int off = 0;
        int ch;
        while ((ch = input.read()) > 0 && (ch == 32 || ch == 9)) {}
        if (ch < 0) {
            throw new EOFException("Premature EOF while reading chunk length");
        }
        hex_len[off++] = (byte)ch;
        while ((ch = input.read()) > 0 && ch != 13 && ch != 10 && ch != 32 && ch != 9 && ch != 59 && off < hex_len.length) {
            hex_len[off++] = (byte)ch;
        }
        while ((ch == 32 || ch == 9) && (ch = input.read()) > 0) {}
        if (ch == 59) {
            while ((ch = input.read()) > 0 && ch != 13 && ch != 10) {}
        }
        if (ch < 0) {
            throw new EOFException("Premature EOF while reading chunk length");
        }
        if (ch != 10 && (ch != 13 || input.read() != 10)) {
            throw new ParseException("Didn't find valid chunk length: " + new String(hex_len, 0, off, "8859_1"));
        }
        try {
            return Long.parseLong(new String(hex_len, 0, off, "8859_1").trim(), 16);
        }
        catch (final NumberFormatException ex) {
            throw new ParseException("Didn't find valid chunk length: " + new String(hex_len, 0, off, "8859_1"));
        }
    }
    
    static {
        Codecs.BoundChar = new BitSet(256);
        for (int ch = 48; ch <= 57; ++ch) {
            Codecs.BoundChar.set(ch);
        }
        for (int ch2 = 65; ch2 <= 90; ++ch2) {
            Codecs.BoundChar.set(ch2);
        }
        for (int ch3 = 97; ch3 <= 122; ++ch3) {
            Codecs.BoundChar.set(ch3);
        }
        Codecs.BoundChar.set(43);
        Codecs.BoundChar.set(95);
        Codecs.BoundChar.set(45);
        Codecs.BoundChar.set(46);
        (Codecs.EBCDICUnsafeChar = new BitSet(256)).set(33);
        Codecs.EBCDICUnsafeChar.set(34);
        Codecs.EBCDICUnsafeChar.set(35);
        Codecs.EBCDICUnsafeChar.set(36);
        Codecs.EBCDICUnsafeChar.set(64);
        Codecs.EBCDICUnsafeChar.set(91);
        Codecs.EBCDICUnsafeChar.set(92);
        Codecs.EBCDICUnsafeChar.set(93);
        Codecs.EBCDICUnsafeChar.set(94);
        Codecs.EBCDICUnsafeChar.set(96);
        Codecs.EBCDICUnsafeChar.set(123);
        Codecs.EBCDICUnsafeChar.set(124);
        Codecs.EBCDICUnsafeChar.set(125);
        Codecs.EBCDICUnsafeChar.set(126);
        final byte[] map = Codecs.Base64EncMap = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        Codecs.Base64DecMap = new byte[128];
        for (int idx = 0; idx < Codecs.Base64EncMap.length; ++idx) {
            Codecs.Base64DecMap[Codecs.Base64EncMap[idx]] = (byte)idx;
        }
        Codecs.UUEncMap = new char[64];
        for (int idx2 = 0; idx2 < Codecs.UUEncMap.length; ++idx2) {
            Codecs.UUEncMap[idx2] = (char)(idx2 + 32);
        }
        Codecs.UUDecMap = new byte[128];
        for (int idx3 = 0; idx3 < Codecs.UUEncMap.length; ++idx3) {
            Codecs.UUDecMap[Codecs.UUEncMap[idx3]] = (byte)idx3;
        }
        Codecs.dummy = new NVPair[0];
    }
    
    private static class CT extends URLConnection
    {
        protected static final String getContentType(final String fname) {
            return URLConnection.guessContentTypeFromName(fname);
        }
        
        private CT() {
            super(null);
        }
        
        public void connect() {
        }
    }
}
