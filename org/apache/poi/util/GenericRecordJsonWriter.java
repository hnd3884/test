package org.apache.poi.util;

import java.io.Flushable;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Color;
import java.util.regex.Matcher;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Base64;
import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.File;
import java.util.AbstractMap;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import java.io.Closeable;

public class GenericRecordJsonWriter implements Closeable
{
    private static final String TABS;
    private static final String ZEROS = "0000000000000000";
    private static final Pattern ESC_CHARS;
    private static final String NL;
    private static final List<Map.Entry<Class, GenericRecordHandler>> handler;
    protected final AppendableWriter aw;
    protected final PrintWriter fw;
    protected int indent;
    protected boolean withComments;
    protected int childIndex;
    
    private static void handler(final Class c, final GenericRecordHandler printer) {
        GenericRecordJsonWriter.handler.add(new AbstractMap.SimpleEntry<Class, GenericRecordHandler>(c, printer));
    }
    
    public GenericRecordJsonWriter(final File fileName) throws IOException {
        this.indent = 0;
        this.withComments = true;
        this.childIndex = 0;
        final OutputStream os = "null".equals(fileName.getName()) ? new NullOutputStream() : new FileOutputStream(fileName);
        this.aw = new AppendableWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        this.fw = new PrintWriter(this.aw);
    }
    
    public GenericRecordJsonWriter(final Appendable buffer) {
        this.indent = 0;
        this.withComments = true;
        this.childIndex = 0;
        this.aw = new AppendableWriter(buffer);
        this.fw = new PrintWriter(this.aw);
    }
    
    public static String marshal(final GenericRecord record) {
        return marshal(record, true);
    }
    
    public static String marshal(final GenericRecord record, final boolean withComments) {
        final StringBuilder sb = new StringBuilder();
        try (final GenericRecordJsonWriter w = new GenericRecordJsonWriter(sb)) {
            w.setWithComments(withComments);
            w.write(record);
            return sb.toString();
        }
        catch (final IOException e) {
            return "{}";
        }
    }
    
    public void setWithComments(final boolean withComments) {
        this.withComments = withComments;
    }
    
    @Override
    public void close() throws IOException {
        this.fw.close();
    }
    
    protected String tabs() {
        return GenericRecordJsonWriter.TABS.substring(0, Math.min(this.indent, GenericRecordJsonWriter.TABS.length()));
    }
    
    public void write(final GenericRecord record) {
        final String tabs = this.tabs();
        final Enum type = record.getGenericRecordType();
        final String recordName = (type != null) ? type.name() : record.getClass().getSimpleName();
        this.fw.append(tabs);
        this.fw.append("{");
        if (this.withComments) {
            this.fw.append("   /* ");
            this.fw.append(recordName);
            if (this.childIndex > 0) {
                this.fw.append(" - index: ");
                this.fw.print(this.childIndex);
            }
            this.fw.append(" */");
        }
        this.fw.println();
        final boolean hasProperties = this.writeProperties(record);
        this.fw.println();
        this.writeChildren(record, hasProperties);
        this.fw.append(tabs);
        this.fw.append("}");
    }
    
    protected boolean writeProperties(final GenericRecord record) {
        final Map<String, Supplier<?>> prop = record.getGenericProperties();
        if (prop == null || prop.isEmpty()) {
            return false;
        }
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        final long cnt = prop.entrySet().stream().filter(e -> this.writeProp(e.getKey(), e.getValue())).count();
        this.childIndex = oldChildIndex;
        return cnt > 0L;
    }
    
    protected boolean writeChildren(final GenericRecord record, final boolean hasProperties) {
        final List<? extends GenericRecord> list = record.getGenericChildren();
        if (list == null || list.isEmpty()) {
            return false;
        }
        ++this.indent;
        this.aw.setHoldBack(this.tabs() + (hasProperties ? ", " : "") + "\"children\": [" + GenericRecordJsonWriter.NL);
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        final long cnt = list.stream().filter(l -> this.writeValue(null, l) && ++this.childIndex > 0).count();
        this.childIndex = oldChildIndex;
        this.aw.setHoldBack(null);
        if (cnt > 0L) {
            this.fw.println();
            this.fw.println(this.tabs() + "]");
        }
        --this.indent;
        return cnt > 0L;
    }
    
    public void writeError(final String errorMsg) {
        this.fw.append("{ error: ");
        this.printObject("error", errorMsg);
        this.fw.append(" }");
    }
    
    protected boolean writeProp(final String name, final Supplier<?> value) {
        final boolean isNext = this.childIndex > 0;
        this.aw.setHoldBack(isNext ? (GenericRecordJsonWriter.NL + this.tabs() + "\t, ") : (this.tabs() + "\t  "));
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        final boolean written = this.writeValue(name, value.get());
        this.childIndex = oldChildIndex + (written ? 1 : 0);
        this.aw.setHoldBack(null);
        return written;
    }
    
    protected boolean writeValue(final String name, final Object o) {
        if (this.childIndex > 0) {
            this.aw.setHoldBack(",");
        }
        final GenericRecordHandler grh = (o == null) ? GenericRecordJsonWriter::printNull : GenericRecordJsonWriter.handler.stream().filter(h -> matchInstanceOrArray(h.getKey(), o)).findFirst().map((Function<? super Object, ? extends GenericRecordHandler>)Map.Entry::getValue).orElse(null);
        final boolean result = grh != null && grh.print(this, name, o);
        this.aw.setHoldBack(null);
        return result;
    }
    
    protected static boolean matchInstanceOrArray(final Class key, final Object instance) {
        return key.isInstance(instance) || (Array.class.equals(key) && instance.getClass().isArray());
    }
    
    protected void printName(final String name) {
        this.fw.print((name != null) ? ("\"" + name + "\": ") : "");
    }
    
    protected boolean printNull(final String name, final Object o) {
        this.printName(name);
        this.fw.write("null");
        return true;
    }
    
    protected boolean printNumber(final String name, final Object o) {
        final Number n = (Number)o;
        this.printName(name);
        if (o instanceof Float) {
            this.fw.print(n.floatValue());
            return true;
        }
        if (o instanceof Double) {
            this.fw.print(n.doubleValue());
            return true;
        }
        this.fw.print(n.longValue());
        int size;
        if (n instanceof Byte) {
            size = 2;
        }
        else if (n instanceof Short) {
            size = 4;
        }
        else if (n instanceof Integer) {
            size = 8;
        }
        else if (n instanceof Long) {
            size = 16;
        }
        else {
            size = -1;
        }
        final long l = n.longValue();
        if (this.withComments && size > 0 && (l < 0L || l > 9L)) {
            this.fw.write(" /* 0x");
            this.fw.write(trimHex(l, size));
            this.fw.write(" */");
        }
        return true;
    }
    
    protected boolean printBoolean(final String name, final Object o) {
        this.printName(name);
        this.fw.write(((Boolean)o).toString());
        return true;
    }
    
    protected boolean printList(final String name, final Object o) {
        this.printName(name);
        this.fw.println("[");
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        ((List)o).forEach(e -> {
            this.writeValue(null, e);
            ++this.childIndex;
            return;
        });
        this.childIndex = oldChildIndex;
        this.fw.write(this.tabs() + "\t]");
        return true;
    }
    
    protected boolean printGenericRecord(final String name, final Object o) {
        this.printName(name);
        ++this.indent;
        this.write((GenericRecord)o);
        --this.indent;
        return true;
    }
    
    protected boolean printAnnotatedFlag(final String name, final Object o) {
        this.printName(name);
        final GenericRecordUtil.AnnotatedFlag af = (GenericRecordUtil.AnnotatedFlag)o;
        this.fw.print(af.getValue().get().longValue());
        if (this.withComments) {
            this.fw.write(" /* ");
            this.fw.write(af.getDescription());
            this.fw.write(" */ ");
        }
        return true;
    }
    
    protected boolean printBytes(final String name, final Object o) {
        this.printName(name);
        this.fw.write(34);
        this.fw.write(Base64.getEncoder().encodeToString((byte[])o));
        this.fw.write(34);
        return true;
    }
    
    protected boolean printPoint(final String name, final Object o) {
        this.printName(name);
        final Point2D p = (Point2D)o;
        this.fw.write("{ \"x\": " + p.getX() + ", \"y\": " + p.getY() + " }");
        return true;
    }
    
    protected boolean printDimension(final String name, final Object o) {
        this.printName(name);
        final Dimension2D p = (Dimension2D)o;
        this.fw.write("{ \"width\": " + p.getWidth() + ", \"height\": " + p.getHeight() + " }");
        return true;
    }
    
    protected boolean printRectangle(final String name, final Object o) {
        this.printName(name);
        final Rectangle2D p = (Rectangle2D)o;
        this.fw.write("{ \"x\": " + p.getX() + ", \"y\": " + p.getY() + ", \"width\": " + p.getWidth() + ", \"height\": " + p.getHeight() + " }");
        return true;
    }
    
    protected boolean printPath(final String name, final Object o) {
        this.printName(name);
        final PathIterator iter = ((Path2D)o).getPathIterator(null);
        final double[] pnts = new double[6];
        this.fw.write("[");
        this.indent += 2;
        final String t = this.tabs();
        this.indent -= 2;
        boolean isNext = false;
        while (!iter.isDone()) {
            this.fw.println(isNext ? ", " : "");
            this.fw.print(t);
            isNext = true;
            final int segType = iter.currentSegment(pnts);
            this.fw.append("{ \"type\": ");
            switch (segType) {
                case 0: {
                    this.fw.write("\"move\", \"x\": " + pnts[0] + ", \"y\": " + pnts[1]);
                    break;
                }
                case 1: {
                    this.fw.write("\"lineto\", \"x\": " + pnts[0] + ", \"y\": " + pnts[1]);
                    break;
                }
                case 2: {
                    this.fw.write("\"quad\", \"x1\": " + pnts[0] + ", \"y1\": " + pnts[1] + ", \"x2\": " + pnts[2] + ", \"y2\": " + pnts[3]);
                    break;
                }
                case 3: {
                    this.fw.write("\"cubic\", \"x1\": " + pnts[0] + ", \"y1\": " + pnts[1] + ", \"x2\": " + pnts[2] + ", \"y2\": " + pnts[3] + ", \"x3\": " + pnts[4] + ", \"y3\": " + pnts[5]);
                    break;
                }
                case 4: {
                    this.fw.write("\"close\"");
                    break;
                }
            }
            this.fw.append(" }");
            iter.next();
        }
        this.fw.write("]");
        return true;
    }
    
    protected boolean printObject(final String name, final Object o) {
        this.printName(name);
        this.fw.write(34);
        final String str = o.toString();
        final Matcher m = GenericRecordJsonWriter.ESC_CHARS.matcher(str);
        int pos = 0;
        while (m.find()) {
            this.fw.append(str, pos, m.start());
            final String group;
            final String match = group = m.group();
            switch (group) {
                case "\n": {
                    this.fw.write("\\\\n");
                    break;
                }
                case "\r": {
                    this.fw.write("\\\\r");
                    break;
                }
                case "\t": {
                    this.fw.write("\\\\t");
                    break;
                }
                case "\b": {
                    this.fw.write("\\\\b");
                    break;
                }
                case "\f": {
                    this.fw.write("\\\\f");
                    break;
                }
                case "\\": {
                    this.fw.write("\\\\\\\\");
                    break;
                }
                case "\"": {
                    this.fw.write("\\\\\"");
                    break;
                }
                default: {
                    this.fw.write("\\\\u");
                    this.fw.write(trimHex(match.charAt(0), 4));
                    break;
                }
            }
            pos = m.end();
        }
        this.fw.append(str, pos, str.length());
        this.fw.write(34);
        return true;
    }
    
    protected boolean printAffineTransform(final String name, final Object o) {
        this.printName(name);
        final AffineTransform xForm = (AffineTransform)o;
        this.fw.write("{ \"scaleX\": " + xForm.getScaleX() + ", \"shearX\": " + xForm.getShearX() + ", \"transX\": " + xForm.getTranslateX() + ", \"scaleY\": " + xForm.getScaleY() + ", \"shearY\": " + xForm.getShearY() + ", \"transY\": " + xForm.getTranslateY() + " }");
        return true;
    }
    
    protected boolean printColor(final String name, final Object o) {
        this.printName(name);
        final int rgb = ((Color)o).getRGB();
        this.fw.print(rgb);
        if (this.withComments) {
            this.fw.write(" /* 0x");
            this.fw.write(trimHex(rgb, 8));
            this.fw.write(" */");
        }
        return true;
    }
    
    protected boolean printArray(final String name, final Object o) {
        this.printName(name);
        this.fw.write("[");
        final int length = Array.getLength(o);
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        while (this.childIndex < length) {
            this.writeValue(null, Array.get(o, this.childIndex));
            ++this.childIndex;
        }
        this.childIndex = oldChildIndex;
        this.fw.write(this.tabs() + "\t]");
        return true;
    }
    
    static String trimHex(final long l, final int size) {
        final String b = Long.toHexString(l);
        final int len = b.length();
        return "0000000000000000".substring(0, Math.max(0, size - len)) + b.substring(Math.max(0, len - size), len);
    }
    
    static {
        ESC_CHARS = Pattern.compile("[\"\\p{Cntrl}\\\\]");
        NL = System.getProperty("line.separator");
        handler = new ArrayList<Map.Entry<Class, GenericRecordHandler>>();
        final char[] t = new char[255];
        Arrays.fill(t, '\t');
        TABS = new String(t);
        handler(String.class, GenericRecordJsonWriter::printObject);
        handler(Number.class, GenericRecordJsonWriter::printNumber);
        handler(Boolean.class, GenericRecordJsonWriter::printBoolean);
        handler(List.class, GenericRecordJsonWriter::printList);
        handler(GenericRecord.class, GenericRecordJsonWriter::printGenericRecord);
        handler(GenericRecordUtil.AnnotatedFlag.class, GenericRecordJsonWriter::printAnnotatedFlag);
        handler(byte[].class, GenericRecordJsonWriter::printBytes);
        handler(Point2D.class, GenericRecordJsonWriter::printPoint);
        handler(Dimension2D.class, GenericRecordJsonWriter::printDimension);
        handler(Rectangle2D.class, GenericRecordJsonWriter::printRectangle);
        handler(Path2D.class, GenericRecordJsonWriter::printPath);
        handler(AffineTransform.class, GenericRecordJsonWriter::printAffineTransform);
        handler(Color.class, GenericRecordJsonWriter::printColor);
        handler(Array.class, GenericRecordJsonWriter::printArray);
        handler(Object.class, GenericRecordJsonWriter::printObject);
    }
    
    static class NullOutputStream extends OutputStream
    {
        @Override
        public void write(final byte[] b, final int off, final int len) {
        }
        
        @Override
        public void write(final int b) {
        }
        
        @Override
        public void write(final byte[] b) {
        }
    }
    
    static class AppendableWriter extends Writer
    {
        private final Appendable appender;
        private final Writer writer;
        private String holdBack;
        
        AppendableWriter(final Appendable buffer) {
            super(buffer);
            this.appender = buffer;
            this.writer = null;
        }
        
        AppendableWriter(final Writer writer) {
            super(writer);
            this.appender = null;
            this.writer = writer;
        }
        
        void setHoldBack(final String holdBack) {
            this.holdBack = holdBack;
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            if (this.holdBack != null) {
                if (this.appender != null) {
                    this.appender.append(this.holdBack);
                }
                else {
                    this.writer.write(this.holdBack);
                }
                this.holdBack = null;
            }
            if (this.appender != null) {
                this.appender.append(String.valueOf(cbuf), off, len);
            }
            else {
                this.writer.write(cbuf, off, len);
            }
        }
        
        @Override
        public void flush() throws IOException {
            final Object o = (this.appender != null) ? this.appender : this.writer;
            if (o instanceof Flushable) {
                ((Flushable)o).flush();
            }
        }
        
        @Override
        public void close() throws IOException {
            this.flush();
            final Object o = (this.appender != null) ? this.appender : this.writer;
            if (o instanceof Closeable) {
                ((Closeable)o).close();
            }
        }
    }
    
    @FunctionalInterface
    protected interface GenericRecordHandler
    {
        boolean print(final GenericRecordJsonWriter p0, final String p1, final Object p2);
    }
}
