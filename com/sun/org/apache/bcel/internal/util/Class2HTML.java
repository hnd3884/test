package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.Attribute;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import java.io.File;
import java.io.IOException;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.Constants;

public class Class2HTML implements Constants
{
    private JavaClass java_class;
    private String dir;
    private static String class_package;
    private static String class_name;
    private static ConstantPool constant_pool;
    
    public Class2HTML(final JavaClass java_class, final String dir) throws IOException {
        final Method[] methods = java_class.getMethods();
        this.java_class = java_class;
        this.dir = dir;
        Class2HTML.class_name = java_class.getClassName();
        Class2HTML.constant_pool = java_class.getConstantPool();
        final int index = Class2HTML.class_name.lastIndexOf(46);
        if (index > -1) {
            Class2HTML.class_package = Class2HTML.class_name.substring(0, index);
        }
        else {
            Class2HTML.class_package = "";
        }
        final ConstantHTML constant_html = new ConstantHTML(dir, Class2HTML.class_name, Class2HTML.class_package, methods, Class2HTML.constant_pool);
        final AttributeHTML attribute_html = new AttributeHTML(dir, Class2HTML.class_name, Class2HTML.constant_pool, constant_html);
        final MethodHTML method_html = new MethodHTML(dir, Class2HTML.class_name, methods, java_class.getFields(), constant_html, attribute_html);
        this.writeMainHTML(attribute_html);
        new CodeHTML(dir, Class2HTML.class_name, methods, Class2HTML.constant_pool, constant_html);
        attribute_html.close();
    }
    
    public static void _main(final String[] argv) {
        final String[] file_name = new String[argv.length];
        int files = 0;
        ClassParser parser = null;
        JavaClass java_class = null;
        String zip_file = null;
        final char sep = SecuritySupport.getSystemProperty("file.separator").toCharArray()[0];
        String dir = "." + sep;
        try {
            for (int i = 0; i < argv.length; ++i) {
                if (argv[i].charAt(0) == '-') {
                    if (argv[i].equals("-d")) {
                        dir = argv[++i];
                        if (!dir.endsWith("" + sep)) {
                            dir += sep;
                        }
                        new File(dir).mkdirs();
                    }
                    else if (argv[i].equals("-zip")) {
                        zip_file = argv[++i];
                    }
                    else {
                        System.out.println("Unknown option " + argv[i]);
                    }
                }
                else {
                    file_name[files++] = argv[i];
                }
            }
            if (files == 0) {
                System.err.println("Class2HTML: No input files specified.");
            }
            else {
                for (int i = 0; i < files; ++i) {
                    System.out.print("Processing " + file_name[i] + "...");
                    if (zip_file == null) {
                        parser = new ClassParser(file_name[i]);
                    }
                    else {
                        parser = new ClassParser(zip_file, file_name[i]);
                    }
                    java_class = parser.parse();
                    new Class2HTML(java_class, dir);
                    System.out.println("Done.");
                }
            }
        }
        catch (final Exception e) {
            System.out.println(e);
            e.printStackTrace(System.out);
        }
    }
    
    static String referenceClass(final int index) {
        String str = Class2HTML.constant_pool.getConstantString(index, (byte)7);
        str = Utility.compactClassName(str);
        str = Utility.compactClassName(str, Class2HTML.class_package + ".", true);
        return "<A HREF=\"" + Class2HTML.class_name + "_cp.html#cp" + index + "\" TARGET=ConstantPool>" + str + "</A>";
    }
    
    static final String referenceType(String type) {
        String short_type = Utility.compactClassName(type);
        short_type = Utility.compactClassName(short_type, Class2HTML.class_package + ".", true);
        final int index = type.indexOf(91);
        if (index > -1) {
            type = type.substring(0, index);
        }
        if (type.equals("int") || type.equals("short") || type.equals("boolean") || type.equals("void") || type.equals("char") || type.equals("byte") || type.equals("long") || type.equals("double") || type.equals("float")) {
            return "<FONT COLOR=\"#00FF00\">" + type + "</FONT>";
        }
        return "<A HREF=\"" + type + ".html\" TARGET=_top>" + short_type + "</A>";
    }
    
    static String toHTML(final String str) {
        final StringBuffer buf = new StringBuffer();
        try {
            for (int i = 0; i < str.length(); ++i) {
                final char ch;
                switch (ch = str.charAt(i)) {
                    case '<': {
                        buf.append("&lt;");
                        break;
                    }
                    case '>': {
                        buf.append("&gt;");
                        break;
                    }
                    case '\n': {
                        buf.append("\\n");
                        break;
                    }
                    case '\r': {
                        buf.append("\\r");
                        break;
                    }
                    default: {
                        buf.append(ch);
                        break;
                    }
                }
            }
        }
        catch (final StringIndexOutOfBoundsException ex) {}
        return buf.toString();
    }
    
    private void writeMainHTML(final AttributeHTML attribute_html) throws IOException {
        final PrintWriter file = new PrintWriter(new FileOutputStream(this.dir + Class2HTML.class_name + ".html"));
        final Attribute[] attributes = this.java_class.getAttributes();
        file.println("<HTML>\n<HEAD><TITLE>Documentation for " + Class2HTML.class_name + "</TITLE></HEAD>\n<FRAMESET BORDER=1 cols=\"30%,*\">\n<FRAMESET BORDER=1 rows=\"80%,*\">\n<FRAME NAME=\"ConstantPool\" SRC=\"" + Class2HTML.class_name + "_cp.html\"\n MARGINWIDTH=\"0\" MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n<FRAME NAME=\"Attributes\" SRC=\"" + Class2HTML.class_name + "_attributes.html\"\n MARGINWIDTH=\"0\" MARGINHEIGHT=\"0\" FRAMEBORDER=\"1\" SCROLLING=\"AUTO\">\n</FRAMESET>\n<FRAMESET BORDER=1 rows=\"80%,*\">\n<FRAME NAME=\"Code\" SRC=\"" + Class2HTML.class_name + "_code.html\"\n MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n<FRAME NAME=\"Methods\" SRC=\"" + Class2HTML.class_name + "_methods.html\"\n MARGINWIDTH=0 MARGINHEIGHT=0 FRAMEBORDER=1 SCROLLING=\"AUTO\">\n</FRAMESET></FRAMESET></HTML>");
        file.close();
        for (int i = 0; i < attributes.length; ++i) {
            attribute_html.writeAttribute(attributes[i], "class" + i);
        }
    }
}
