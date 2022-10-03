package org.apache.xmlbeans.impl.jam.internal;

import org.apache.xmlbeans.impl.jam.JConstructor;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JamClassIterator;
import java.io.PrintWriter;
import org.apache.xmlbeans.impl.jam.JElement;

public class JamPrinter
{
    private static final String INDENT = "  ";
    
    public static JamPrinter newInstance() {
        return new JamPrinter();
    }
    
    private JamPrinter() {
    }
    
    public void print(final JElement root, final PrintWriter out) {
        this.print(root, 0, out);
    }
    
    public void print(final JamClassIterator iter, final PrintWriter out) {
        while (iter.hasNext()) {
            final JClass clazz = iter.nextClass();
            out.println("------------------------------");
            out.println(clazz.getQualifiedName());
            out.println("------------------------------");
            this.print(clazz, out);
            out.println();
        }
    }
    
    private void print(final JElement a, int indent, final PrintWriter out) {
        this.indent(indent, out);
        out.print("[");
        out.print(this.getTypeKey(a));
        out.print("] ");
        if (a instanceof JMethod) {
            out.print(((JMethod)a).getReturnType().getFieldDescriptor());
            out.print(" ");
            out.println(a.getSimpleName());
        }
        else {
            out.println(a.getSimpleName());
        }
        ++indent;
    }
    
    private void print(final JAnnotation[] atts, final int indent, final PrintWriter out) {
        for (int i = 0; i < atts.length; ++i) {
            this.indent(indent, out);
            out.print("<");
            out.print(this.getTypeKey(atts[i]));
            out.print("> ");
            out.print(atts[i].getSimpleName());
        }
    }
    
    private void indent(final int indent, final PrintWriter out) {
        for (int i = 0; i < indent; ++i) {
            out.print("  ");
        }
    }
    
    private String getTypeKey(final Object o) {
        if (o == null) {
            return "[?UNKNOWN!]";
        }
        String type = o.getClass().getName();
        final int lastDot = type.lastIndexOf(".");
        if (lastDot != -1 && lastDot + 1 < type.length()) {
            type = type.substring(lastDot + 1);
        }
        return type;
    }
    
    private static JElement[] getChildrenFor(final JElement parent) {
        final Collection list = new ArrayList();
        if (parent instanceof JClass) {
            list.addAll(Arrays.asList(((JClass)parent).getDeclaredFields()));
            list.addAll(Arrays.asList(((JClass)parent).getDeclaredMethods()));
            list.addAll(Arrays.asList(((JClass)parent).getConstructors()));
            list.addAll(Arrays.asList(((JClass)parent).getClasses()));
        }
        else if (parent instanceof JConstructor) {
            list.addAll(Arrays.asList(((JConstructor)parent).getParameters()));
        }
        else if (parent instanceof JMethod) {
            list.addAll(Arrays.asList(((JMethod)parent).getParameters()));
        }
        final JElement[] out = new JElement[list.size()];
        list.toArray(out);
        return out;
    }
}
