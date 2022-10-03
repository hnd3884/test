package org.apache.lucene.codecs;

import java.util.Set;
import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.NamedSPILoader;

public abstract class PostingsFormat implements NamedSPILoader.NamedSPI
{
    public static final PostingsFormat[] EMPTY;
    private final String name;
    
    protected PostingsFormat(final String name) {
        NamedSPILoader.checkServiceName(name);
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    public abstract FieldsConsumer fieldsConsumer(final SegmentWriteState p0) throws IOException;
    
    public abstract FieldsProducer fieldsProducer(final SegmentReadState p0) throws IOException;
    
    @Override
    public String toString() {
        return "PostingsFormat(name=" + this.name + ")";
    }
    
    public static PostingsFormat forName(final String name) {
        return Holder.getLoader().lookup(name);
    }
    
    public static Set<String> availablePostingsFormats() {
        return Holder.getLoader().availableServices();
    }
    
    public static void reloadPostingsFormats(final ClassLoader classloader) {
        Holder.getLoader().reload(classloader);
    }
    
    static {
        EMPTY = new PostingsFormat[0];
    }
    
    private static final class Holder
    {
        private static final NamedSPILoader<PostingsFormat> LOADER;
        
        static NamedSPILoader<PostingsFormat> getLoader() {
            if (Holder.LOADER == null) {
                throw new IllegalStateException("You tried to lookup a PostingsFormat by name before all formats could be initialized. This likely happens if you call PostingsFormat#forName from a PostingsFormat's ctor.");
            }
            return Holder.LOADER;
        }
        
        static {
            LOADER = new NamedSPILoader<PostingsFormat>(PostingsFormat.class);
        }
    }
}
