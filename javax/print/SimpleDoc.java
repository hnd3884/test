package javax.print;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.CharArrayReader;
import java.io.IOException;
import javax.print.attribute.AttributeSetUtilities;
import sun.reflect.misc.ReflectUtil;
import java.io.InputStream;
import java.io.Reader;
import javax.print.attribute.DocAttributeSet;

public final class SimpleDoc implements Doc
{
    private DocFlavor flavor;
    private DocAttributeSet attributes;
    private Object printData;
    private Reader reader;
    private InputStream inStream;
    
    public SimpleDoc(final Object printData, final DocFlavor flavor, final DocAttributeSet set) {
        if (flavor == null || printData == null) {
            throw new IllegalArgumentException("null argument(s)");
        }
        Class<?> forName;
        try {
            final String representationClassName = flavor.getRepresentationClassName();
            ReflectUtil.checkPackageAccess(representationClassName);
            forName = Class.forName(representationClassName, false, Thread.currentThread().getContextClassLoader());
        }
        catch (final Throwable t) {
            throw new IllegalArgumentException("unknown representation class");
        }
        if (!forName.isInstance(printData)) {
            throw new IllegalArgumentException("data is not of declared type");
        }
        this.flavor = flavor;
        if (set != null) {
            this.attributes = AttributeSetUtilities.unmodifiableView(set);
        }
        this.printData = printData;
    }
    
    @Override
    public DocFlavor getDocFlavor() {
        return this.flavor;
    }
    
    @Override
    public DocAttributeSet getAttributes() {
        return this.attributes;
    }
    
    @Override
    public Object getPrintData() throws IOException {
        return this.printData;
    }
    
    @Override
    public Reader getReaderForText() throws IOException {
        if (this.printData instanceof Reader) {
            return (Reader)this.printData;
        }
        synchronized (this) {
            if (this.reader != null) {
                return this.reader;
            }
            if (this.printData instanceof char[]) {
                this.reader = new CharArrayReader((char[])this.printData);
            }
            else if (this.printData instanceof String) {
                this.reader = new StringReader((String)this.printData);
            }
        }
        return this.reader;
    }
    
    @Override
    public InputStream getStreamForBytes() throws IOException {
        if (this.printData instanceof InputStream) {
            return (InputStream)this.printData;
        }
        synchronized (this) {
            if (this.inStream != null) {
                return this.inStream;
            }
            if (this.printData instanceof byte[]) {
                this.inStream = new ByteArrayInputStream((byte[])this.printData);
            }
        }
        return this.inStream;
    }
}
