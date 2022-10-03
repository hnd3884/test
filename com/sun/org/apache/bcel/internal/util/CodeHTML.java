package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import java.util.BitSet;
import java.io.PrintWriter;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.Constants;

final class CodeHTML implements Constants
{
    private String class_name;
    private Method[] methods;
    private PrintWriter file;
    private BitSet goto_set;
    private ConstantPool constant_pool;
    private ConstantHTML constant_html;
    private static boolean wide;
    
    CodeHTML(final String dir, final String class_name, final Method[] methods, final ConstantPool constant_pool, final ConstantHTML constant_html) throws IOException {
        this.class_name = class_name;
        this.methods = methods;
        this.constant_pool = constant_pool;
        this.constant_html = constant_html;
        (this.file = new PrintWriter(new FileOutputStream(dir + class_name + "_code.html"))).println("<HTML><BODY BGCOLOR=\"#C0C0C0\">");
        for (int i = 0; i < methods.length; ++i) {
            this.writeMethod(methods[i], i);
        }
        this.file.println("</BODY></HTML>");
        this.file.close();
    }
    
    private final String codeToHTML(final ByteSequence bytes, final int method_number) throws IOException {
        final short opcode = (short)bytes.readUnsignedByte();
        int default_offset = 0;
        int no_pad_bytes = 0;
        final StringBuffer buf = new StringBuffer("<TT>" + CodeHTML.OPCODE_NAMES[opcode] + "</TT></TD><TD>");
        if (opcode == 170 || opcode == 171) {
            final int remainder = bytes.getIndex() % 4;
            no_pad_bytes = ((remainder == 0) ? 0 : (4 - remainder));
            for (int i = 0; i < no_pad_bytes; ++i) {
                bytes.readByte();
            }
            default_offset = bytes.readInt();
        }
        switch (opcode) {
            case 170: {
                final int low = bytes.readInt();
                final int high = bytes.readInt();
                final int offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
                default_offset += offset;
                buf.append("<TABLE BORDER=1><TR>");
                final int[] jump_table = new int[high - low + 1];
                for (int j = 0; j < jump_table.length; ++j) {
                    jump_table[j] = offset + bytes.readInt();
                    buf.append("<TH>" + (low + j) + "</TH>");
                }
                buf.append("<TH>default</TH></TR>\n<TR>");
                for (int j = 0; j < jump_table.length; ++j) {
                    buf.append("<TD><A HREF=\"#code" + method_number + "@" + jump_table[j] + "\">" + jump_table[j] + "</A></TD>");
                }
                buf.append("<TD><A HREF=\"#code" + method_number + "@" + default_offset + "\">" + default_offset + "</A></TD></TR>\n</TABLE>\n");
                break;
            }
            case 171: {
                final int npairs = bytes.readInt();
                final int offset = bytes.getIndex() - 8 - no_pad_bytes - 1;
                final int[] jump_table = new int[npairs];
                default_offset += offset;
                buf.append("<TABLE BORDER=1><TR>");
                for (int i = 0; i < npairs; ++i) {
                    final int match = bytes.readInt();
                    jump_table[i] = offset + bytes.readInt();
                    buf.append("<TH>" + match + "</TH>");
                }
                buf.append("<TH>default</TH></TR>\n<TR>");
                for (int i = 0; i < npairs; ++i) {
                    buf.append("<TD><A HREF=\"#code" + method_number + "@" + jump_table[i] + "\">" + jump_table[i] + "</A></TD>");
                }
                buf.append("<TD><A HREF=\"#code" + method_number + "@" + default_offset + "\">" + default_offset + "</A></TD></TR>\n</TABLE>\n");
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
                final int index = bytes.getIndex() + bytes.readShort() - 1;
                buf.append("<A HREF=\"#code" + method_number + "@" + index + "\">" + index + "</A>");
                break;
            }
            case 200:
            case 201: {
                final int windex = bytes.getIndex() + bytes.readInt() - 1;
                buf.append("<A HREF=\"#code" + method_number + "@" + windex + "\">" + windex + "</A>");
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
                if (CodeHTML.wide) {
                    vindex = bytes.readShort();
                    CodeHTML.wide = false;
                }
                else {
                    vindex = bytes.readUnsignedByte();
                }
                buf.append("%" + vindex);
                break;
            }
            case 196: {
                CodeHTML.wide = true;
                buf.append("(wide)");
                break;
            }
            case 188: {
                buf.append("<FONT COLOR=\"#00FF00\">" + CodeHTML.TYPE_NAMES[bytes.readByte()] + "</FONT>");
                break;
            }
            case 178:
            case 179:
            case 180:
            case 181: {
                int index = bytes.readShort();
                final ConstantFieldref c1 = (ConstantFieldref)this.constant_pool.getConstant(index, (byte)9);
                final int class_index = c1.getClassIndex();
                String name = this.constant_pool.getConstantString(class_index, (byte)7);
                name = Utility.compactClassName(name, false);
                index = c1.getNameAndTypeIndex();
                final String field_name = this.constant_pool.constantToString(index, (byte)12);
                if (name.equals(this.class_name)) {
                    buf.append("<A HREF=\"" + this.class_name + "_methods.html#field" + field_name + "\" TARGET=Methods>" + field_name + "</A>\n");
                    break;
                }
                buf.append(this.constant_html.referenceConstant(class_index) + "." + field_name);
                break;
            }
            case 187:
            case 192:
            case 193: {
                final int index = bytes.readShort();
                buf.append(this.constant_html.referenceConstant(index));
                break;
            }
            case 182:
            case 183:
            case 184:
            case 185: {
                final int m_index = bytes.readShort();
                int index;
                int class_index;
                if (opcode == 185) {
                    final int nargs = bytes.readUnsignedByte();
                    final int reserved = bytes.readUnsignedByte();
                    final ConstantInterfaceMethodref c2 = (ConstantInterfaceMethodref)this.constant_pool.getConstant(m_index, (byte)11);
                    class_index = c2.getClassIndex();
                    final String str = this.constant_pool.constantToString(c2);
                    index = c2.getNameAndTypeIndex();
                }
                else {
                    final ConstantMethodref c3 = (ConstantMethodref)this.constant_pool.getConstant(m_index, (byte)10);
                    class_index = c3.getClassIndex();
                    final String str = this.constant_pool.constantToString(c3);
                    index = c3.getNameAndTypeIndex();
                }
                final String name = Class2HTML.referenceClass(class_index);
                final String str = Class2HTML.toHTML(this.constant_pool.constantToString(this.constant_pool.getConstant(index, (byte)12)));
                final ConstantNameAndType c4 = (ConstantNameAndType)this.constant_pool.getConstant(index, (byte)12);
                final String signature = this.constant_pool.constantToString(c4.getSignatureIndex(), (byte)1);
                final String[] args = Utility.methodSignatureArgumentTypes(signature, false);
                final String type = Utility.methodSignatureReturnType(signature, false);
                buf.append(name + ".<A HREF=\"" + this.class_name + "_cp.html#cp" + m_index + "\" TARGET=ConstantPool>" + str + "</A>(");
                for (int k = 0; k < args.length; ++k) {
                    buf.append(Class2HTML.referenceType(args[k]));
                    if (k < args.length - 1) {
                        buf.append(", ");
                    }
                }
                buf.append("):" + Class2HTML.referenceType(type));
                break;
            }
            case 19:
            case 20: {
                final int index = bytes.readShort();
                buf.append("<A HREF=\"" + this.class_name + "_cp.html#cp" + index + "\" TARGET=\"ConstantPool\">" + Class2HTML.toHTML(this.constant_pool.constantToString(index, this.constant_pool.getConstant(index).getTag())) + "</a>");
                break;
            }
            case 18: {
                final int index = bytes.readUnsignedByte();
                buf.append("<A HREF=\"" + this.class_name + "_cp.html#cp" + index + "\" TARGET=\"ConstantPool\">" + Class2HTML.toHTML(this.constant_pool.constantToString(index, this.constant_pool.getConstant(index).getTag())) + "</a>");
                break;
            }
            case 189: {
                final int index = bytes.readShort();
                buf.append(this.constant_html.referenceConstant(index));
                break;
            }
            case 197: {
                final int index = bytes.readShort();
                final int dimensions = bytes.readByte();
                buf.append(this.constant_html.referenceConstant(index) + ":" + dimensions + "-dimensional");
                break;
            }
            case 132: {
                int vindex;
                int constant;
                if (CodeHTML.wide) {
                    vindex = bytes.readShort();
                    constant = bytes.readShort();
                    CodeHTML.wide = false;
                }
                else {
                    vindex = bytes.readUnsignedByte();
                    constant = bytes.readByte();
                }
                buf.append("%" + vindex + " " + constant);
                break;
            }
            default: {
                if (CodeHTML.NO_OF_OPERANDS[opcode] > 0) {
                    for (int l = 0; l < CodeHTML.TYPE_OF_OPERANDS[opcode].length; ++l) {
                        switch (CodeHTML.TYPE_OF_OPERANDS[opcode][l]) {
                            case 8: {
                                buf.append(bytes.readUnsignedByte());
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
                                System.exit(-1);
                                break;
                            }
                        }
                        buf.append("&nbsp;");
                    }
                    break;
                }
                break;
            }
        }
        buf.append("</TD>");
        return buf.toString();
    }
    
    private final void findGotos(final ByteSequence bytes, final Method method, final Code code) throws IOException {
        this.goto_set = new BitSet(bytes.available());
        if (code != null) {
            final CodeException[] ce = code.getExceptionTable();
            for (int len = ce.length, i = 0; i < len; ++i) {
                this.goto_set.set(ce[i].getStartPC());
                this.goto_set.set(ce[i].getEndPC());
                this.goto_set.set(ce[i].getHandlerPC());
            }
            final Attribute[] attributes = code.getAttributes();
            for (int j = 0; j < attributes.length; ++j) {
                if (attributes[j].getTag() == 5) {
                    final LocalVariable[] vars = ((LocalVariableTable)attributes[j]).getLocalVariableTable();
                    for (int k = 0; k < vars.length; ++k) {
                        final int start = vars[k].getStartPC();
                        final int end = start + vars[k].getLength();
                        this.goto_set.set(start);
                        this.goto_set.set(end);
                    }
                    break;
                }
            }
        }
        int l = 0;
        while (bytes.available() > 0) {
            final int opcode = bytes.readUnsignedByte();
            switch (opcode) {
                case 170:
                case 171: {
                    final int remainder = bytes.getIndex() % 4;
                    final int no_pad_bytes = (remainder == 0) ? 0 : (4 - remainder);
                    for (int k = 0; k < no_pad_bytes; ++k) {
                        bytes.readByte();
                    }
                    int default_offset = bytes.readInt();
                    if (opcode == 170) {
                        final int low = bytes.readInt();
                        final int high = bytes.readInt();
                        final int offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
                        default_offset += offset;
                        this.goto_set.set(default_offset);
                        for (int m = 0; m < high - low + 1; ++m) {
                            final int index = offset + bytes.readInt();
                            this.goto_set.set(index);
                        }
                        break;
                    }
                    final int npairs = bytes.readInt();
                    final int offset = bytes.getIndex() - 8 - no_pad_bytes - 1;
                    default_offset += offset;
                    this.goto_set.set(default_offset);
                    for (int j2 = 0; j2 < npairs; ++j2) {
                        final int match = bytes.readInt();
                        final int index = offset + bytes.readInt();
                        this.goto_set.set(index);
                    }
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
                    final int index = bytes.getIndex() + bytes.readShort() - 1;
                    this.goto_set.set(index);
                    break;
                }
                case 200:
                case 201: {
                    final int index = bytes.getIndex() + bytes.readInt() - 1;
                    this.goto_set.set(index);
                    break;
                }
                default: {
                    bytes.unreadByte();
                    this.codeToHTML(bytes, 0);
                    break;
                }
            }
            ++l;
        }
    }
    
    private void writeMethod(final Method method, final int method_number) throws IOException {
        final String signature = method.getSignature();
        final String[] args = Utility.methodSignatureArgumentTypes(signature, false);
        final String type = Utility.methodSignatureReturnType(signature, false);
        final String name = method.getName();
        final String html_name = Class2HTML.toHTML(name);
        String access = Utility.accessToString(method.getAccessFlags());
        access = Utility.replace(access, " ", "&nbsp;");
        final Attribute[] attributes = method.getAttributes();
        this.file.print("<P><B><FONT COLOR=\"#FF0000\">" + access + "</FONT>&nbsp;<A NAME=method" + method_number + ">" + Class2HTML.referenceType(type) + "</A>&nbsp<A HREF=\"" + this.class_name + "_methods.html#method" + method_number + "\" TARGET=Methods>" + html_name + "</A>(");
        for (int i = 0; i < args.length; ++i) {
            this.file.print(Class2HTML.referenceType(args[i]));
            if (i < args.length - 1) {
                this.file.print(",&nbsp;");
            }
        }
        this.file.println(")</B></P>");
        Code c = null;
        byte[] code = null;
        if (attributes.length > 0) {
            this.file.print("<H4>Attributes</H4><UL>\n");
            for (int j = 0; j < attributes.length; ++j) {
                byte tag = attributes[j].getTag();
                if (tag != -1) {
                    this.file.print("<LI><A HREF=\"" + this.class_name + "_attributes.html#method" + method_number + "@" + j + "\" TARGET=Attributes>" + CodeHTML.ATTRIBUTE_NAMES[tag] + "</A></LI>\n");
                }
                else {
                    this.file.print("<LI>" + attributes[j] + "</LI>");
                }
                if (tag == 2) {
                    c = (Code)attributes[j];
                    final Attribute[] attributes2 = c.getAttributes();
                    code = c.getCode();
                    this.file.print("<UL>");
                    for (int k = 0; k < attributes2.length; ++k) {
                        tag = attributes2[k].getTag();
                        this.file.print("<LI><A HREF=\"" + this.class_name + "_attributes.html#method" + method_number + "@" + j + "@" + k + "\" TARGET=Attributes>" + CodeHTML.ATTRIBUTE_NAMES[tag] + "</A></LI>\n");
                    }
                    this.file.print("</UL>");
                }
            }
            this.file.println("</UL>");
        }
        if (code != null) {
            final ByteSequence stream = new ByteSequence(code);
            stream.mark(stream.available());
            this.findGotos(stream, method, c);
            stream.reset();
            this.file.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Byte<BR>offset</TH><TH ALIGN=LEFT>Instruction</TH><TH ALIGN=LEFT>Argument</TH>");
            int l = 0;
            while (stream.available() > 0) {
                final int offset = stream.getIndex();
                final String str = this.codeToHTML(stream, method_number);
                String anchor = "";
                if (this.goto_set.get(offset)) {
                    anchor = "<A NAME=code" + method_number + "@" + offset + "></A>";
                }
                String anchor2;
                if (stream.getIndex() == code.length) {
                    anchor2 = "<A NAME=code" + method_number + "@" + code.length + ">" + offset + "</A>";
                }
                else {
                    anchor2 = "" + offset;
                }
                this.file.println("<TR VALIGN=TOP><TD>" + anchor2 + "</TD><TD>" + anchor + str + "</TR>");
                ++l;
            }
            this.file.println("<TR><TD> </A></TD></TR>");
            this.file.println("</TABLE>");
        }
    }
    
    static {
        CodeHTML.wide = false;
    }
}
