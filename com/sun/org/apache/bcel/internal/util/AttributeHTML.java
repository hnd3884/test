package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.InnerClass;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.InnerClasses;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.LineNumberTable;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.bcel.internal.classfile.SourceFile;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import java.io.PrintWriter;
import com.sun.org.apache.bcel.internal.Constants;

final class AttributeHTML implements Constants
{
    private String class_name;
    private PrintWriter file;
    private int attr_count;
    private ConstantHTML constant_html;
    private ConstantPool constant_pool;
    
    AttributeHTML(final String dir, final String class_name, final ConstantPool constant_pool, final ConstantHTML constant_html) throws IOException {
        this.attr_count = 0;
        this.class_name = class_name;
        this.constant_pool = constant_pool;
        this.constant_html = constant_html;
        (this.file = new PrintWriter(new FileOutputStream(dir + class_name + "_attributes.html"))).println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
    }
    
    private final String codeLink(final int link, final int method_number) {
        return "<A HREF=\"" + this.class_name + "_code.html#code" + method_number + "@" + link + "\" TARGET=Code>" + link + "</A>";
    }
    
    final void close() {
        this.file.println("</TABLE></BODY></HTML>");
        this.file.close();
    }
    
    final void writeAttribute(final Attribute attribute, final String anchor) throws IOException {
        this.writeAttribute(attribute, anchor, 0);
    }
    
    final void writeAttribute(final Attribute attribute, final String anchor, final int method_number) throws IOException {
        final byte tag = attribute.getTag();
        if (tag == -1) {
            return;
        }
        ++this.attr_count;
        if (this.attr_count % 2 == 0) {
            this.file.print("<TR BGCOLOR=\"#C0C0C0\"><TD>");
        }
        else {
            this.file.print("<TR BGCOLOR=\"#A0A0A0\"><TD>");
        }
        this.file.println("<H4><A NAME=\"" + anchor + "\">" + this.attr_count + " " + AttributeHTML.ATTRIBUTE_NAMES[tag] + "</A></H4>");
        switch (tag) {
            case 2: {
                final Code c = (Code)attribute;
                this.file.print("<UL><LI>Maximum stack size = " + c.getMaxStack() + "</LI>\n<LI>Number of local variables = " + c.getMaxLocals() + "</LI>\n<LI><A HREF=\"" + this.class_name + "_code.html#method" + method_number + "\" TARGET=Code>Byte code</A></LI></UL>\n");
                final CodeException[] ce = c.getExceptionTable();
                final int len = ce.length;
                if (len > 0) {
                    this.file.print("<P><B>Exceptions handled</B><UL>");
                    for (int i = 0; i < len; ++i) {
                        final int catch_type = ce[i].getCatchType();
                        this.file.print("<LI>");
                        if (catch_type != 0) {
                            this.file.print(this.constant_html.referenceConstant(catch_type));
                        }
                        else {
                            this.file.print("Any Exception");
                        }
                        this.file.print("<BR>(Ranging from lines " + this.codeLink(ce[i].getStartPC(), method_number) + " to " + this.codeLink(ce[i].getEndPC(), method_number) + ", handled at line " + this.codeLink(ce[i].getHandlerPC(), method_number) + ")</LI>");
                    }
                    this.file.print("</UL>");
                    break;
                }
                break;
            }
            case 1: {
                final int index = ((ConstantValue)attribute).getConstantValueIndex();
                this.file.print("<UL><LI><A HREF=\"" + this.class_name + "_cp.html#cp" + index + "\" TARGET=\"ConstantPool\">Constant value index(" + index + ")</A></UL>\n");
                break;
            }
            case 0: {
                final int index = ((SourceFile)attribute).getSourceFileIndex();
                this.file.print("<UL><LI><A HREF=\"" + this.class_name + "_cp.html#cp" + index + "\" TARGET=\"ConstantPool\">Source file index(" + index + ")</A></UL>\n");
                break;
            }
            case 3: {
                final int[] indices = ((ExceptionTable)attribute).getExceptionIndexTable();
                this.file.print("<UL>");
                for (int j = 0; j < indices.length; ++j) {
                    this.file.print("<LI><A HREF=\"" + this.class_name + "_cp.html#cp" + indices[j] + "\" TARGET=\"ConstantPool\">Exception class index(" + indices[j] + ")</A>\n");
                }
                this.file.print("</UL>\n");
                break;
            }
            case 4: {
                final LineNumber[] line_numbers = ((LineNumberTable)attribute).getLineNumberTable();
                this.file.print("<P>");
                for (int k = 0; k < line_numbers.length; ++k) {
                    this.file.print("(" + line_numbers[k].getStartPC() + ",&nbsp;" + line_numbers[k].getLineNumber() + ")");
                    if (k < line_numbers.length - 1) {
                        this.file.print(", ");
                    }
                }
                break;
            }
            case 5: {
                final LocalVariable[] vars = ((LocalVariableTable)attribute).getLocalVariableTable();
                this.file.print("<UL>");
                for (int l = 0; l < vars.length; ++l) {
                    final int index = vars[l].getSignatureIndex();
                    String signature = ((ConstantUtf8)this.constant_pool.getConstant(index, (byte)1)).getBytes();
                    signature = Utility.signatureToString(signature, false);
                    final int start = vars[l].getStartPC();
                    final int end = start + vars[l].getLength();
                    this.file.println("<LI>" + Class2HTML.referenceType(signature) + "&nbsp;<B>" + vars[l].getName() + "</B> in slot %" + vars[l].getIndex() + "<BR>Valid from lines <A HREF=\"" + this.class_name + "_code.html#code" + method_number + "@" + start + "\" TARGET=Code>" + start + "</A> to <A HREF=\"" + this.class_name + "_code.html#code" + method_number + "@" + end + "\" TARGET=Code>" + end + "</A></LI>");
                }
                this.file.print("</UL>\n");
                break;
            }
            case 6: {
                final InnerClass[] classes = ((InnerClasses)attribute).getInnerClasses();
                this.file.print("<UL>");
                for (int m = 0; m < classes.length; ++m) {
                    final int index = classes[m].getInnerNameIndex();
                    String name;
                    if (index > 0) {
                        name = ((ConstantUtf8)this.constant_pool.getConstant(index, (byte)1)).getBytes();
                    }
                    else {
                        name = "&lt;anonymous&gt;";
                    }
                    final String access = Utility.accessToString(classes[m].getInnerAccessFlags());
                    this.file.print("<LI><FONT COLOR=\"#FF0000\">" + access + "</FONT> " + this.constant_html.referenceConstant(classes[m].getInnerClassIndex()) + " in&nbsp;class " + this.constant_html.referenceConstant(classes[m].getOuterClassIndex()) + " named " + name + "</LI>\n");
                }
                this.file.print("</UL>\n");
                break;
            }
            default: {
                this.file.print("<P>" + attribute.toString());
                break;
            }
        }
        this.file.println("</TD></TR>");
        this.file.flush();
    }
}
