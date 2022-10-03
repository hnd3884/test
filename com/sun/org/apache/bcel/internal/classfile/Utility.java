package com.sun.org.apache.bcel.internal.classfile;

import java.io.FilterWriter;
import java.io.FilterReader;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.CharArrayReader;
import java.io.Writer;
import java.io.CharArrayWriter;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.io.IOException;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.sun.org.apache.bcel.internal.Constants;

public abstract class Utility
{
    private static int consumed_chars;
    private static boolean wide;
    private static final int FREE_CHARS = 48;
    private static int[] CHAR_MAP;
    private static int[] MAP_CHAR;
    private static final char ESCAPE_CHAR = '$';
    
    public static final String accessToString(final int access_flags) {
        return accessToString(access_flags, false);
    }
    
    public static final String accessToString(final int access_flags, final boolean for_class) {
        final StringBuffer buf = new StringBuffer();
        int p = 0;
        int i = 0;
        while (p < 2048) {
            p = pow2(i);
            Label_0081: {
                if ((access_flags & p) != 0x0) {
                    if (for_class) {
                        if (p == 32) {
                            break Label_0081;
                        }
                        if (p == 512) {
                            break Label_0081;
                        }
                    }
                    buf.append(Constants.ACCESS_NAMES[i] + " ");
                }
            }
            ++i;
        }
        return buf.toString().trim();
    }
    
    public static final String classOrInterface(final int access_flags) {
        return ((access_flags & 0x200) != 0x0) ? "interface" : "class";
    }
    
    public static final String codeToString(final byte[] code, final ConstantPool constant_pool, final int index, final int length, final boolean verbose) {
        final StringBuffer buf = new StringBuffer(code.length * 20);
        final ByteSequence stream = new ByteSequence(code);
        try {
            for (int i = 0; i < index; ++i) {
                codeToString(stream, constant_pool, verbose);
            }
            int i = 0;
            while (stream.available() > 0) {
                if (length < 0 || i < length) {
                    final String indices = fillup(stream.getIndex() + ":", 6, true, ' ');
                    buf.append(indices + codeToString(stream, constant_pool, verbose) + '\n');
                }
                ++i;
            }
        }
        catch (final IOException e) {
            System.out.println(buf.toString());
            e.printStackTrace();
            throw new ClassFormatException("Byte code error: " + e);
        }
        return buf.toString();
    }
    
    public static final String codeToString(final byte[] code, final ConstantPool constant_pool, final int index, final int length) {
        return codeToString(code, constant_pool, index, length, true);
    }
    
    public static final String codeToString(final ByteSequence bytes, final ConstantPool constant_pool, final boolean verbose) throws IOException {
        final short opcode = (short)bytes.readUnsignedByte();
        int default_offset = 0;
        int no_pad_bytes = 0;
        final StringBuffer buf = new StringBuffer(Constants.OPCODE_NAMES[opcode]);
        if (opcode == 170 || opcode == 171) {
            final int remainder = bytes.getIndex() % 4;
            no_pad_bytes = ((remainder == 0) ? 0 : (4 - remainder));
            for (int i = 0; i < no_pad_bytes; ++i) {
                final byte b;
                if ((b = bytes.readByte()) != 0) {
                    System.err.println("Warning: Padding byte != 0 in " + Constants.OPCODE_NAMES[opcode] + ":" + b);
                }
            }
            default_offset = bytes.readInt();
        }
        switch (opcode) {
            case 170: {
                final int low = bytes.readInt();
                final int high = bytes.readInt();
                final int offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
                default_offset += offset;
                buf.append("\tdefault = " + default_offset + ", low = " + low + ", high = " + high + "(");
                final int[] jump_table = new int[high - low + 1];
                for (int j = 0; j < jump_table.length; ++j) {
                    buf.append(jump_table[j] = offset + bytes.readInt());
                    if (j < jump_table.length - 1) {
                        buf.append(", ");
                    }
                }
                buf.append(")");
                break;
            }
            case 171: {
                final int npairs = bytes.readInt();
                final int offset = bytes.getIndex() - 8 - no_pad_bytes - 1;
                final int[] match = new int[npairs];
                final int[] jump_table = new int[npairs];
                default_offset += offset;
                buf.append("\tdefault = " + default_offset + ", npairs = " + npairs + " (");
                for (int j = 0; j < npairs; ++j) {
                    match[j] = bytes.readInt();
                    jump_table[j] = offset + bytes.readInt();
                    buf.append("(" + match[j] + ", " + jump_table[j] + ")");
                    if (j < npairs - 1) {
                        buf.append(", ");
                    }
                }
                buf.append(")");
                break;
            }
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 198:
            case 199: {
                buf.append("\t\t#" + (bytes.getIndex() - 1 + bytes.readShort()));
                break;
            }
            case 200:
            case 201: {
                buf.append("\t\t#" + (bytes.getIndex() - 1 + bytes.readInt()));
                break;
            }
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 169: {
                int vindex;
                if (Utility.wide) {
                    vindex = bytes.readUnsignedShort();
                    Utility.wide = false;
                }
                else {
                    vindex = bytes.readUnsignedByte();
                }
                buf.append("\t\t%" + vindex);
                break;
            }
            case 196: {
                Utility.wide = true;
                buf.append("\t(wide)");
                break;
            }
            case 188: {
                buf.append("\t\t<" + Constants.TYPE_NAMES[bytes.readByte()] + ">");
                break;
            }
            case 178:
            case 179:
            case 180:
            case 181: {
                final int index = bytes.readUnsignedShort();
                buf.append("\t\t" + constant_pool.constantToString(index, (byte)9) + (verbose ? (" (" + index + ")") : ""));
                break;
            }
            case 187:
            case 192: {
                buf.append("\t");
            }
            case 193: {
                final int index = bytes.readUnsignedShort();
                buf.append("\t<" + constant_pool.constantToString(index, (byte)7) + ">" + (verbose ? (" (" + index + ")") : ""));
                break;
            }
            case 182:
            case 183:
            case 184: {
                final int index = bytes.readUnsignedShort();
                buf.append("\t" + constant_pool.constantToString(index, (byte)10) + (verbose ? (" (" + index + ")") : ""));
                break;
            }
            case 185: {
                final int index = bytes.readUnsignedShort();
                final int nargs = bytes.readUnsignedByte();
                buf.append("\t" + constant_pool.constantToString(index, (byte)11) + (verbose ? (" (" + index + ")\t") : "") + nargs + "\t" + bytes.readUnsignedByte());
                break;
            }
            case 19:
            case 20: {
                final int index = bytes.readUnsignedShort();
                buf.append("\t\t" + constant_pool.constantToString(index, constant_pool.getConstant(index).getTag()) + (verbose ? (" (" + index + ")") : ""));
                break;
            }
            case 18: {
                final int index = bytes.readUnsignedByte();
                buf.append("\t\t" + constant_pool.constantToString(index, constant_pool.getConstant(index).getTag()) + (verbose ? (" (" + index + ")") : ""));
                break;
            }
            case 189: {
                final int index = bytes.readUnsignedShort();
                buf.append("\t\t<" + compactClassName(constant_pool.getConstantString(index, (byte)7), false) + ">" + (verbose ? (" (" + index + ")") : ""));
                break;
            }
            case 197: {
                final int index = bytes.readUnsignedShort();
                final int dimensions = bytes.readUnsignedByte();
                buf.append("\t<" + compactClassName(constant_pool.getConstantString(index, (byte)7), false) + ">\t" + dimensions + (verbose ? (" (" + index + ")") : ""));
                break;
            }
            case 132: {
                int vindex;
                int constant;
                if (Utility.wide) {
                    vindex = bytes.readUnsignedShort();
                    constant = bytes.readShort();
                    Utility.wide = false;
                }
                else {
                    vindex = bytes.readUnsignedByte();
                    constant = bytes.readByte();
                }
                buf.append("\t\t%" + vindex + "\t" + constant);
                break;
            }
            default: {
                if (Constants.NO_OF_OPERANDS[opcode] > 0) {
                    for (int i = 0; i < Constants.TYPE_OF_OPERANDS[opcode].length; ++i) {
                        buf.append("\t\t");
                        switch (Constants.TYPE_OF_OPERANDS[opcode][i]) {
                            case 8: {
                                buf.append(bytes.readByte());
                                break;
                            }
                            case 9: {
                                buf.append(bytes.readShort());
                                break;
                            }
                            case 10: {
                                buf.append(bytes.readInt());
                                break;
                            }
                            default: {
                                System.err.println("Unreachable default case reached!");
                                buf.setLength(0);
                                break;
                            }
                        }
                    }
                    break;
                }
                break;
            }
        }
        return buf.toString();
    }
    
    public static final String codeToString(final ByteSequence bytes, final ConstantPool constant_pool) throws IOException {
        return codeToString(bytes, constant_pool, true);
    }
    
    public static final String compactClassName(final String str) {
        return compactClassName(str, true);
    }
    
    public static final String compactClassName(String str, final String prefix, final boolean chopit) {
        final int len = prefix.length();
        str = str.replace('/', '.');
        if (chopit && str.startsWith(prefix) && str.substring(len).indexOf(46) == -1) {
            str = str.substring(len);
        }
        return str;
    }
    
    public static final String compactClassName(final String str, final boolean chopit) {
        return compactClassName(str, "java.lang.", chopit);
    }
    
    private static final boolean is_digit(final char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    private static final boolean is_space(final char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }
    
    public static final int setBit(final int flag, final int i) {
        return flag | pow2(i);
    }
    
    public static final int clearBit(final int flag, final int i) {
        final int bit = pow2(i);
        return ((flag & bit) == 0x0) ? flag : (flag ^ bit);
    }
    
    public static final boolean isSet(final int flag, final int i) {
        return (flag & pow2(i)) != 0x0;
    }
    
    public static final String methodTypeToSignature(final String ret, final String[] argv) throws ClassFormatException {
        final StringBuffer buf = new StringBuffer("(");
        if (argv != null) {
            for (int i = 0; i < argv.length; ++i) {
                final String str = getSignature(argv[i]);
                if (str.endsWith("V")) {
                    throw new ClassFormatException("Invalid type: " + argv[i]);
                }
                buf.append(str);
            }
        }
        final String str = getSignature(ret);
        buf.append(")" + str);
        return buf.toString();
    }
    
    public static final String[] methodSignatureArgumentTypes(final String signature) throws ClassFormatException {
        return methodSignatureArgumentTypes(signature, true);
    }
    
    public static final String[] methodSignatureArgumentTypes(final String signature, final boolean chopit) throws ClassFormatException {
        final ArrayList vec = new ArrayList();
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            for (int index = 1; signature.charAt(index) != ')'; index += Utility.consumed_chars) {
                vec.add(signatureToString(signature.substring(index), chopit));
            }
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        final String[] types = new String[vec.size()];
        vec.toArray(types);
        return types;
    }
    
    public static final String methodSignatureReturnType(final String signature) throws ClassFormatException {
        return methodSignatureReturnType(signature, true);
    }
    
    public static final String methodSignatureReturnType(final String signature, final boolean chopit) throws ClassFormatException {
        String type;
        try {
            final int index = signature.lastIndexOf(41) + 1;
            type = signatureToString(signature.substring(index), chopit);
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        return type;
    }
    
    public static final String methodSignatureToString(final String signature, final String name, final String access) {
        return methodSignatureToString(signature, name, access, true);
    }
    
    public static final String methodSignatureToString(final String signature, final String name, final String access, final boolean chopit) {
        return methodSignatureToString(signature, name, access, chopit, null);
    }
    
    public static final String methodSignatureToString(final String signature, final String name, final String access, final boolean chopit, final LocalVariableTable vars) throws ClassFormatException {
        final StringBuffer buf = new StringBuffer("(");
        int var_index = (access.indexOf("static") < 0) ? 1 : 0;
        String type;
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            int index;
            for (index = 1; signature.charAt(index) != ')'; index += Utility.consumed_chars) {
                final String param_type = signatureToString(signature.substring(index), chopit);
                buf.append(param_type);
                if (vars != null) {
                    final LocalVariable l = vars.getLocalVariable(var_index);
                    if (l != null) {
                        buf.append(" " + l.getName());
                    }
                }
                else {
                    buf.append(" arg" + var_index);
                }
                if ("double".equals(param_type) || "long".equals(param_type)) {
                    var_index += 2;
                }
                else {
                    ++var_index;
                }
                buf.append(", ");
            }
            ++index;
            type = signatureToString(signature.substring(index), chopit);
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        if (buf.length() > 1) {
            buf.setLength(buf.length() - 2);
        }
        buf.append(")");
        return access + ((access.length() > 0) ? " " : "") + type + " " + name + buf.toString();
    }
    
    private static final int pow2(final int n) {
        return 1 << n;
    }
    
    public static final String replace(String str, final String old, final String new_) {
        final StringBuffer buf = new StringBuffer();
        try {
            int index;
            if ((index = str.indexOf(old)) != -1) {
                int old_index;
                for (old_index = 0; (index = str.indexOf(old, old_index)) != -1; old_index = index + old.length()) {
                    buf.append(str.substring(old_index, index));
                    buf.append(new_);
                }
                buf.append(str.substring(old_index));
                str = buf.toString();
            }
        }
        catch (final StringIndexOutOfBoundsException e) {
            System.err.println(e);
        }
        return str;
    }
    
    public static final String signatureToString(final String signature) {
        return signatureToString(signature, true);
    }
    
    public static final String signatureToString(final String signature, final boolean chopit) {
        Utility.consumed_chars = 1;
        try {
            switch (signature.charAt(0)) {
                case 'B': {
                    return "byte";
                }
                case 'C': {
                    return "char";
                }
                case 'D': {
                    return "double";
                }
                case 'F': {
                    return "float";
                }
                case 'I': {
                    return "int";
                }
                case 'J': {
                    return "long";
                }
                case 'L': {
                    final int index = signature.indexOf(59);
                    if (index < 0) {
                        throw new ClassFormatException("Invalid signature: " + signature);
                    }
                    Utility.consumed_chars = index + 1;
                    return compactClassName(signature.substring(1, index), chopit);
                }
                case 'S': {
                    return "short";
                }
                case 'Z': {
                    return "boolean";
                }
                case '[': {
                    final StringBuffer brackets = new StringBuffer();
                    int n;
                    for (n = 0; signature.charAt(n) == '['; ++n) {
                        brackets.append("[]");
                    }
                    final int consumed_chars = n;
                    final String type = signatureToString(signature.substring(n), chopit);
                    Utility.consumed_chars += consumed_chars;
                    return type + brackets.toString();
                }
                case 'V': {
                    return "void";
                }
                default: {
                    throw new ClassFormatException("Invalid signature: `" + signature + "'");
                }
            }
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid signature: " + e + ":" + signature);
        }
    }
    
    public static String getSignature(String type) {
        final StringBuffer buf = new StringBuffer();
        final char[] chars = type.toCharArray();
        boolean char_found = false;
        boolean delim = false;
        int index = -1;
    Label_0162:
        for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ': {
                    if (char_found) {
                        delim = true;
                        break;
                    }
                    break;
                }
                case '[': {
                    if (!char_found) {
                        throw new RuntimeException("Illegal type: " + type);
                    }
                    index = i;
                    break Label_0162;
                }
                default: {
                    char_found = true;
                    if (!delim) {
                        buf.append(chars[i]);
                        break;
                    }
                    break;
                }
            }
        }
        int brackets = 0;
        if (index > 0) {
            brackets = countBrackets(type.substring(index));
        }
        type = buf.toString();
        buf.setLength(0);
        for (int j = 0; j < brackets; ++j) {
            buf.append('[');
        }
        boolean found = false;
        for (int k = 4; k <= 12 && !found; ++k) {
            if (Constants.TYPE_NAMES[k].equals(type)) {
                found = true;
                buf.append(Constants.SHORT_TYPE_NAMES[k]);
            }
        }
        if (!found) {
            buf.append('L' + type.replace('.', '/') + ';');
        }
        return buf.toString();
    }
    
    private static int countBrackets(final String brackets) {
        final char[] chars = brackets.toCharArray();
        int count = 0;
        boolean open = false;
        for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
                case '[': {
                    if (open) {
                        throw new RuntimeException("Illegally nested brackets:" + brackets);
                    }
                    open = true;
                    break;
                }
                case ']': {
                    if (!open) {
                        throw new RuntimeException("Illegally nested brackets:" + brackets);
                    }
                    open = false;
                    ++count;
                    break;
                }
            }
        }
        if (open) {
            throw new RuntimeException("Illegally nested brackets:" + brackets);
        }
        return count;
    }
    
    public static final byte typeOfMethodSignature(final String signature) throws ClassFormatException {
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            final int index = signature.lastIndexOf(41) + 1;
            return typeOfSignature(signature.substring(index));
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
    }
    
    public static final byte typeOfSignature(final String signature) throws ClassFormatException {
        try {
            switch (signature.charAt(0)) {
                case 'B': {
                    return 8;
                }
                case 'C': {
                    return 5;
                }
                case 'D': {
                    return 7;
                }
                case 'F': {
                    return 6;
                }
                case 'I': {
                    return 10;
                }
                case 'J': {
                    return 11;
                }
                case 'L': {
                    return 14;
                }
                case '[': {
                    return 13;
                }
                case 'V': {
                    return 12;
                }
                case 'Z': {
                    return 4;
                }
                case 'S': {
                    return 9;
                }
                default: {
                    throw new ClassFormatException("Invalid method signature: " + signature);
                }
            }
        }
        catch (final StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
    }
    
    public static short searchOpcode(String name) {
        name = name.toLowerCase();
        for (short i = 0; i < Constants.OPCODE_NAMES.length; ++i) {
            if (Constants.OPCODE_NAMES[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }
    
    private static final short byteToShort(final byte b) {
        return (b < 0) ? ((short)(256 + b)) : b;
    }
    
    public static final String toHexString(final byte[] bytes) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            final short b = byteToShort(bytes[i]);
            final String hex = Integer.toString(b, 16);
            if (b < 16) {
                buf.append('0');
            }
            buf.append(hex);
            if (i < bytes.length - 1) {
                buf.append(' ');
            }
        }
        return buf.toString();
    }
    
    public static final String format(final int i, final int length, final boolean left_justify, final char fill) {
        return fillup(Integer.toString(i), length, left_justify, fill);
    }
    
    public static final String fillup(final String str, final int length, final boolean left_justify, final char fill) {
        final int len = length - str.length();
        final char[] buf = new char[(len < 0) ? 0 : len];
        for (int j = 0; j < buf.length; ++j) {
            buf[j] = fill;
        }
        if (left_justify) {
            return str + new String(buf);
        }
        return new String(buf) + str;
    }
    
    static final boolean equals(final byte[] a, final byte[] b) {
        final int size;
        if ((size = a.length) != b.length) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static final void printArray(final PrintStream out, final Object[] obj) {
        out.println(printArray(obj, true));
    }
    
    public static final void printArray(final PrintWriter out, final Object[] obj) {
        out.println(printArray(obj, true));
    }
    
    public static final String printArray(final Object[] obj) {
        return printArray(obj, true);
    }
    
    public static final String printArray(final Object[] obj, final boolean braces) {
        return printArray(obj, braces, false);
    }
    
    public static final String printArray(final Object[] obj, final boolean braces, final boolean quote) {
        if (obj == null) {
            return null;
        }
        final StringBuffer buf = new StringBuffer();
        if (braces) {
            buf.append('{');
        }
        for (int i = 0; i < obj.length; ++i) {
            if (obj[i] != null) {
                buf.append((quote ? "\"" : "") + obj[i].toString() + (quote ? "\"" : ""));
            }
            else {
                buf.append("null");
            }
            if (i < obj.length - 1) {
                buf.append(", ");
            }
        }
        if (braces) {
            buf.append('}');
        }
        return buf.toString();
    }
    
    public static boolean isJavaIdentifierPart(final char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_';
    }
    
    public static String encode(byte[] bytes, final boolean compress) throws IOException {
        if (compress) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final GZIPOutputStream gos = new GZIPOutputStream(baos);
            gos.write(bytes, 0, bytes.length);
            gos.close();
            baos.close();
            bytes = baos.toByteArray();
        }
        final CharArrayWriter caw = new CharArrayWriter();
        final JavaWriter jw = new JavaWriter(caw);
        for (int i = 0; i < bytes.length; ++i) {
            final int in = bytes[i] & 0xFF;
            jw.write(in);
        }
        return caw.toString();
    }
    
    public static byte[] decode(final String s, final boolean uncompress) throws IOException {
        final char[] chars = s.toCharArray();
        final CharArrayReader car = new CharArrayReader(chars);
        final JavaReader jr = new JavaReader(car);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int ch;
        while ((ch = jr.read()) >= 0) {
            bos.write(ch);
        }
        bos.close();
        car.close();
        jr.close();
        byte[] bytes = bos.toByteArray();
        if (uncompress) {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
            final byte[] tmp = new byte[bytes.length * 3];
            int count = 0;
            int b;
            while ((b = gis.read()) >= 0) {
                tmp[count++] = (byte)b;
            }
            bytes = new byte[count];
            System.arraycopy(tmp, 0, bytes, 0, count);
        }
        return bytes;
    }
    
    public static final String convertString(final String label) {
        final char[] ch = label.toCharArray();
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < ch.length; ++i) {
            switch (ch[i]) {
                case '\n': {
                    buf.append("\\n");
                    break;
                }
                case '\r': {
                    buf.append("\\r");
                    break;
                }
                case '\"': {
                    buf.append("\\\"");
                    break;
                }
                case '\'': {
                    buf.append("\\'");
                    break;
                }
                case '\\': {
                    buf.append("\\\\");
                    break;
                }
                default: {
                    buf.append(ch[i]);
                    break;
                }
            }
        }
        return buf.toString();
    }
    
    static {
        Utility.wide = false;
        Utility.CHAR_MAP = new int[48];
        Utility.MAP_CHAR = new int[256];
        int j = 0;
        final int k = 0;
        for (int i = 65; i <= 90; ++i) {
            Utility.CHAR_MAP[j] = i;
            Utility.MAP_CHAR[i] = j;
            ++j;
        }
        for (int i = 103; i <= 122; ++i) {
            Utility.CHAR_MAP[j] = i;
            Utility.MAP_CHAR[i] = j;
            ++j;
        }
        Utility.CHAR_MAP[j] = 36;
        Utility.MAP_CHAR[36] = j;
        ++j;
        Utility.CHAR_MAP[j] = 95;
        Utility.MAP_CHAR[95] = j;
    }
    
    private static class JavaReader extends FilterReader
    {
        public JavaReader(final Reader in) {
            super(in);
        }
        
        @Override
        public int read() throws IOException {
            final int b = this.in.read();
            if (b != 36) {
                return b;
            }
            final int i = this.in.read();
            if (i < 0) {
                return -1;
            }
            if ((i < 48 || i > 57) && (i < 97 || i > 102)) {
                return Utility.MAP_CHAR[i];
            }
            final int j = this.in.read();
            if (j < 0) {
                return -1;
            }
            final char[] tmp = { (char)i, (char)j };
            final int s = Integer.parseInt(new String(tmp), 16);
            return s;
        }
        
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            for (int i = 0; i < len; ++i) {
                cbuf[off + i] = (char)this.read();
            }
            return len;
        }
    }
    
    private static class JavaWriter extends FilterWriter
    {
        public JavaWriter(final Writer out) {
            super(out);
        }
        
        @Override
        public void write(final int b) throws IOException {
            if (Utility.isJavaIdentifierPart((char)b) && b != 36) {
                this.out.write(b);
            }
            else {
                this.out.write(36);
                if (b >= 0 && b < 48) {
                    this.out.write(Utility.CHAR_MAP[b]);
                }
                else {
                    final char[] tmp = Integer.toHexString(b).toCharArray();
                    if (tmp.length == 1) {
                        this.out.write(48);
                        this.out.write(tmp[0]);
                    }
                    else {
                        this.out.write(tmp[0]);
                        this.out.write(tmp[1]);
                    }
                }
            }
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            for (int i = 0; i < len; ++i) {
                this.write(cbuf[off + i]);
            }
        }
        
        @Override
        public void write(final String str, final int off, final int len) throws IOException {
            this.write(str.toCharArray(), off, len);
        }
    }
}
