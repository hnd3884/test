package org.apache.poi.util;

import java.util.Arrays;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Base64;
import java.lang.reflect.Array;
import java.awt.Color;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

public class GenericRecordXmlWriter implements Closeable
{
    private static final String TABS;
    private static final String ZEROS = "0000000000000000";
    private static final Pattern ESC_CHARS;
    private static final List<Map.Entry<Class, GenericRecordHandler>> handler;
    private final PrintWriter fw;
    private int indent;
    private boolean withComments;
    private int childIndex;
    private boolean attributePhase;
    
    private static void handler(final Class c, final GenericRecordHandler printer) {
        GenericRecordXmlWriter.handler.add(new AbstractMap.SimpleEntry<Class, GenericRecordHandler>(c, printer));
    }
    
    public GenericRecordXmlWriter(final File fileName) throws IOException {
        this.indent = 0;
        this.withComments = true;
        this.childIndex = 0;
        this.attributePhase = true;
        final OutputStream os = "null".equals(fileName.getName()) ? new GenericRecordJsonWriter.NullOutputStream() : new FileOutputStream(fileName);
        this.fw = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
    }
    
    public GenericRecordXmlWriter(final Appendable buffer) {
        this.indent = 0;
        this.withComments = true;
        this.childIndex = 0;
        this.attributePhase = true;
        this.fw = new PrintWriter(new GenericRecordJsonWriter.AppendableWriter(buffer));
    }
    
    public static String marshal(final GenericRecord record) {
        return marshal(record, true);
    }
    
    public static String marshal(final GenericRecord record, final boolean withComments) {
        final StringBuilder sb = new StringBuilder();
        try (final GenericRecordXmlWriter w = new GenericRecordXmlWriter(sb)) {
            w.setWithComments(withComments);
            w.write(record);
            return sb.toString();
        }
        catch (final IOException e) {
            return "<record/>";
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
        return GenericRecordXmlWriter.TABS.substring(0, Math.min(this.indent, GenericRecordXmlWriter.TABS.length()));
    }
    
    public void write(final GenericRecord record) {
        this.write("record", record);
    }
    
    protected void write(final String name, final GenericRecord record) {
        final String tabs = this.tabs();
        final Enum type = record.getGenericRecordType();
        final String recordName = (type != null) ? type.name() : record.getClass().getSimpleName();
        this.fw.append(tabs);
        this.fw.append("<" + name + " type=\"");
        this.fw.append(recordName);
        this.fw.append("\"");
        if (this.childIndex > 0) {
            this.fw.append(" index=\"");
            this.fw.print(this.childIndex);
            this.fw.append("\"");
        }
        this.attributePhase = true;
        boolean hasComplex = this.writeProperties(record);
        this.attributePhase = false;
        hasComplex |= this.writeChildren(record, hasComplex);
        if (hasComplex) {
            this.fw.append(tabs);
            this.fw.println("</" + name + ">");
        }
        else {
            this.fw.println("/>");
        }
    }
    
    protected boolean writeProperties(final GenericRecord record) {
        final Map<String, Supplier<?>> prop = record.getGenericProperties();
        if (prop == null || prop.isEmpty()) {
            return false;
        }
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        final List<Map.Entry<String, Supplier<?>>> complex = prop.entrySet().stream().flatMap((Function<? super Object, ? extends Stream<?>>)this::writeProp).collect((Collector<? super Object, ?, List<Map.Entry<String, Supplier<?>>>>)Collectors.toList());
        this.attributePhase = false;
        if (!complex.isEmpty()) {
            this.fw.println(">");
            ++this.indent;
            complex.forEach(this::writeProp);
            --this.indent;
        }
        this.childIndex = oldChildIndex;
        return !complex.isEmpty();
    }
    
    protected boolean writeChildren(final GenericRecord record, final boolean hasComplexProperties) {
        final List<? extends GenericRecord> list = record.getGenericChildren();
        if (list == null || list.isEmpty()) {
            return false;
        }
        if (!hasComplexProperties) {
            this.fw.print(">");
        }
        ++this.indent;
        this.fw.println();
        this.fw.println(this.tabs() + "<children>");
        ++this.indent;
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        list.forEach(l -> {
            this.writeValue("record", l);
            ++this.childIndex;
            return;
        });
        this.childIndex = oldChildIndex;
        this.fw.println();
        --this.indent;
        this.fw.println(this.tabs() + "</children>");
        --this.indent;
        return true;
    }
    
    public void writeError(final String errorMsg) {
        this.printObject("error", errorMsg);
    }
    
    protected Stream<Map.Entry<String, Supplier<?>>> writeProp(final Map.Entry<String, Supplier<?>> me) {
        final Object obj = me.getValue().get();
        if (obj == null) {
            return Stream.empty();
        }
        final boolean isComplex = isComplex(obj);
        if (this.attributePhase == isComplex) {
            return (Stream<Map.Entry<String, Supplier<?>>>)(isComplex ? Stream.of(new AbstractMap.SimpleEntry(me.getKey(), () -> obj)) : Stream.empty());
        }
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        this.writeValue(me.getKey(), obj);
        this.childIndex = oldChildIndex;
        return Stream.empty();
    }
    
    protected static boolean isComplex(final Object obj) {
        return !(obj instanceof Number) && !(obj instanceof Boolean) && !(obj instanceof Character) && !(obj instanceof String) && !(obj instanceof Color) && !(obj instanceof Enum);
    }
    
    protected void writeValue(final String name, final Object value) {
        assert name != null;
        if (value instanceof GenericRecord) {
            this.printGenericRecord(name, value);
        }
        else if (value != null) {
            if (name.endsWith(">")) {
                this.fw.print("\t");
            }
            GenericRecordXmlWriter.handler.stream().filter(h -> matchInstanceOrArray(h.getKey(), value)).findFirst().ifPresent(h -> h.getValue().print(this, name, value));
        }
    }
    
    protected static boolean matchInstanceOrArray(final Class key, final Object instance) {
        return key.isInstance(instance) || (Array.class.equals(key) && instance.getClass().isArray());
    }
    
    protected void openName(String name) {
        name = name.replace(">>", ">");
        if (this.attributePhase) {
            this.fw.print(" " + name.replace('>', ' ').trim() + "=\"");
        }
        else {
            this.fw.print(this.tabs() + "<" + name);
            if (name.endsWith(">")) {
                this.fw.println();
            }
        }
    }
    
    protected void closeName(String name) {
        name = name.replace(">>", ">");
        if (this.attributePhase) {
            this.fw.append("\"");
        }
        else if (name.endsWith(">")) {
            this.fw.println(this.tabs() + "\t</" + name);
        }
        else {
            this.fw.println("/>");
        }
    }
    
    protected boolean printNumber(final String name, final Object o) {
        assert this.attributePhase;
        this.openName(name);
        final Number n = (Number)o;
        this.fw.print(n.toString());
        this.closeName(name);
        return true;
    }
    
    protected boolean printBoolean(final String name, final Object o) {
        assert this.attributePhase;
        this.openName(name);
        this.fw.write(((Boolean)o).toString());
        this.closeName(name);
        return true;
    }
    
    protected boolean printList(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name + ">");
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        ((List)o).forEach(e -> {
            this.writeValue("item>", e);
            ++this.childIndex;
            return;
        });
        this.childIndex = oldChildIndex;
        this.closeName(name + ">");
        return true;
    }
    
    protected boolean printArray(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name + ">");
        final int length = Array.getLength(o);
        final int oldChildIndex = this.childIndex;
        this.childIndex = 0;
        while (this.childIndex < length) {
            this.writeValue("item>", Array.get(o, this.childIndex));
            ++this.childIndex;
        }
        this.childIndex = oldChildIndex;
        this.closeName(name + ">");
        return true;
    }
    
    protected void printGenericRecord(final String name, final Object value) {
        this.write(name, (GenericRecord)value);
    }
    
    protected boolean printAnnotatedFlag(final String name, final Object o) {
        assert !this.attributePhase;
        final GenericRecordUtil.AnnotatedFlag af = (GenericRecordUtil.AnnotatedFlag)o;
        final Number n = af.getValue().get();
        int len;
        if (n instanceof Byte) {
            len = 2;
        }
        else if (n instanceof Short) {
            len = 4;
        }
        else if (n instanceof Integer) {
            len = 8;
        }
        else {
            len = 16;
        }
        this.openName(name);
        this.fw.print(" flag=\"0x");
        this.fw.print(this.trimHex(n.longValue(), len));
        this.fw.print('\"');
        if (this.withComments) {
            this.fw.print(" description=\"");
            this.fw.print(af.getDescription());
            this.fw.print("\"");
        }
        this.closeName(name);
        return true;
    }
    
    protected boolean printBytes(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name + ">");
        this.fw.write(Base64.getEncoder().encodeToString((byte[])o));
        this.closeName(name + ">");
        return true;
    }
    
    protected boolean printPoint(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name);
        final Point2D p = (Point2D)o;
        this.fw.println(" x=\"" + p.getX() + "\" y=\"" + p.getY() + "\"/>");
        this.closeName(name);
        return true;
    }
    
    protected boolean printDimension(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name);
        final Dimension2D p = (Dimension2D)o;
        this.fw.println(" width=\"" + p.getWidth() + "\" height=\"" + p.getHeight() + "\"/>");
        this.closeName(name);
        return true;
    }
    
    protected boolean printRectangle(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name);
        final Rectangle2D p = (Rectangle2D)o;
        this.fw.println(" x=\"" + p.getX() + "\" y=\"" + p.getY() + "\" width=\"" + p.getWidth() + "\" height=\"" + p.getHeight() + "\"/>");
        this.closeName(name);
        return true;
    }
    
    protected boolean printPath(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name + ">");
        final PathIterator iter = ((Path2D)o).getPathIterator(null);
        final double[] pnts = new double[6];
        this.indent += 2;
        final String t = this.tabs();
        this.indent -= 2;
        while (!iter.isDone()) {
            this.fw.print(t);
            final int segType = iter.currentSegment(pnts);
            this.fw.print("<pathelement ");
            switch (segType) {
                case 0: {
                    this.fw.print("type=\"move\" x=\"" + pnts[0] + "\" y=\"" + pnts[1] + "\"");
                    break;
                }
                case 1: {
                    this.fw.print("type=\"lineto\" x=\"" + pnts[0] + "\" y=\"" + pnts[1] + "\"");
                    break;
                }
                case 2: {
                    this.fw.print("type=\"quad\" x1=\"" + pnts[0] + "\" y1=\"" + pnts[1] + "\" x2=\"" + pnts[2] + "\" y2=\"" + pnts[3] + "\"");
                    break;
                }
                case 3: {
                    this.fw.print("type=\"cubic\" x1=\"" + pnts[0] + "\" y1=\"" + pnts[1] + "\" x2=\"" + pnts[2] + "\" y2=\"" + pnts[3] + "\" x3=\"" + pnts[4] + "\" y3=\"" + pnts[5] + "\"");
                    break;
                }
                case 4: {
                    this.fw.print("type=\"close\"");
                    break;
                }
            }
            this.fw.println("/>");
            iter.next();
        }
        this.closeName(name + ">");
        return true;
    }
    
    protected boolean printObject(final String name, final Object o) {
        this.openName(name + ">");
        final String str = o.toString();
        final Matcher m = GenericRecordXmlWriter.ESC_CHARS.matcher(str);
        int pos = 0;
        while (m.find()) {
            this.fw.write(str, pos, m.start());
            final String group;
            final String match = group = m.group();
            switch (group) {
                case "<": {
                    this.fw.write("&lt;");
                    break;
                }
                case ">": {
                    this.fw.write("&gt;");
                    break;
                }
                case "&": {
                    this.fw.write("&amp;");
                    break;
                }
                case "'": {
                    this.fw.write("&apos;");
                    break;
                }
                case "\"": {
                    this.fw.write("&quot;");
                    break;
                }
                default: {
                    this.fw.write("&#x");
                    this.fw.write(Long.toHexString(match.codePointAt(0)));
                    this.fw.write(";");
                    break;
                }
            }
            pos = m.end();
        }
        this.fw.append(str, pos, str.length());
        this.closeName(name + ">");
        return true;
    }
    
    protected boolean printAffineTransform(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name);
        final AffineTransform xForm = (AffineTransform)o;
        this.fw.write("<" + name + " scaleX=\"" + xForm.getScaleX() + "\" shearX=\"" + xForm.getShearX() + "\" transX=\"" + xForm.getTranslateX() + "\" scaleY=\"" + xForm.getScaleY() + "\" shearY=\"" + xForm.getShearY() + "\" transY=\"" + xForm.getTranslateY() + "\"/>");
        this.closeName(name);
        return true;
    }
    
    protected boolean printColor(final String name, final Object o) {
        assert this.attributePhase;
        this.openName(name);
        final int rgb = ((Color)o).getRGB();
        this.fw.print("0x" + this.trimHex(rgb, 8));
        this.closeName(name);
        return true;
    }
    
    protected boolean printBufferedImage(final String name, final Object o) {
        assert !this.attributePhase;
        this.openName(name);
        final BufferedImage bi = (BufferedImage)o;
        this.fw.println(" width=\"" + bi.getWidth() + "\" height=\"" + bi.getHeight() + "\" bands=\"" + bi.getColorModel().getNumComponents() + "\"");
        this.closeName(name);
        return true;
    }
    
    protected String trimHex(final long l, final int size) {
        final String b = Long.toHexString(l);
        final int len = b.length();
        return "0000000000000000".substring(0, Math.max(0, size - len)) + b.substring(Math.max(0, len - size), len);
    }
    
    static {
        ESC_CHARS = Pattern.compile("[<>&'\"\\p{Cntrl}]");
        handler = new ArrayList<Map.Entry<Class, GenericRecordHandler>>();
        final char[] t = new char[255];
        Arrays.fill(t, '\t');
        TABS = new String(t);
        handler(String.class, GenericRecordXmlWriter::printObject);
        handler(Number.class, GenericRecordXmlWriter::printNumber);
        handler(Boolean.class, GenericRecordXmlWriter::printBoolean);
        handler(List.class, GenericRecordXmlWriter::printList);
        handler(GenericRecordUtil.AnnotatedFlag.class, GenericRecordXmlWriter::printAnnotatedFlag);
        handler(byte[].class, GenericRecordXmlWriter::printBytes);
        handler(Point2D.class, GenericRecordXmlWriter::printPoint);
        handler(Dimension2D.class, GenericRecordXmlWriter::printDimension);
        handler(Rectangle2D.class, GenericRecordXmlWriter::printRectangle);
        handler(Path2D.class, GenericRecordXmlWriter::printPath);
        handler(AffineTransform.class, GenericRecordXmlWriter::printAffineTransform);
        handler(Color.class, GenericRecordXmlWriter::printColor);
        handler(BufferedImage.class, GenericRecordXmlWriter::printBufferedImage);
        handler(Array.class, GenericRecordXmlWriter::printArray);
        handler(Object.class, GenericRecordXmlWriter::printObject);
    }
    
    @FunctionalInterface
    protected interface GenericRecordHandler
    {
        boolean print(final GenericRecordXmlWriter p0, final String p1, final Object p2);
    }
}
