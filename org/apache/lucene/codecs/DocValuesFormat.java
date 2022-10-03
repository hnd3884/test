package org.apache.lucene.codecs;

import java.util.Set;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.NamedSPILoader;

public abstract class DocValuesFormat implements NamedSPILoader.NamedSPI
{
    private final String name;
    
    protected DocValuesFormat(final String name) {
        NamedSPILoader.checkServiceName(name);
        this.name = name;
    }
    
    public abstract DocValuesConsumer fieldsConsumer(final SegmentWriteState p0) throws IOException;
    
    public abstract DocValuesProducer fieldsProducer(final SegmentReadState p0) throws IOException;
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "DocValuesFormat(name=" + this.name + ")";
    }
    
    public static DocValuesFormat forName(final String name) {
        return Holder.getLoader().lookup(name);
    }
    
    public static Set<String> availableDocValuesFormats() {
        return Holder.getLoader().availableServices();
    }
    
    public static void reloadDocValuesFormats(final ClassLoader classloader) {
        Holder.getLoader().reload(classloader);
    }
    
    private static final class Holder
    {
        private static final NamedSPILoader<DocValuesFormat> LOADER;
        
        static NamedSPILoader<DocValuesFormat> getLoader() {
            if (Holder.LOADER == null) {
                throw new IllegalStateException("You tried to lookup a DocValuesFormat by name before all formats could be initialized. This likely happens if you call DocValuesFormat#forName from a DocValuesFormat's ctor.");
            }
            return Holder.LOADER;
        }
        
        static {
            LOADER = new NamedSPILoader<DocValuesFormat>(DocValuesFormat.class);
        }
    }
}
