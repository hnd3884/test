package org.apache.lucene.codecs;

import java.util.Objects;
import java.util.Set;
import org.apache.lucene.util.NamedSPILoader;

public abstract class Codec implements NamedSPILoader.NamedSPI
{
    private final String name;
    
    protected Codec(final String name) {
        NamedSPILoader.checkServiceName(name);
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    public abstract PostingsFormat postingsFormat();
    
    public abstract DocValuesFormat docValuesFormat();
    
    public abstract StoredFieldsFormat storedFieldsFormat();
    
    public abstract TermVectorsFormat termVectorsFormat();
    
    public abstract FieldInfosFormat fieldInfosFormat();
    
    public abstract SegmentInfoFormat segmentInfoFormat();
    
    public abstract NormsFormat normsFormat();
    
    public abstract LiveDocsFormat liveDocsFormat();
    
    public abstract CompoundFormat compoundFormat();
    
    public static Codec forName(final String name) {
        return Holder.getLoader().lookup(name);
    }
    
    public static Set<String> availableCodecs() {
        return Holder.getLoader().availableServices();
    }
    
    public static void reloadCodecs(final ClassLoader classloader) {
        Holder.getLoader().reload(classloader);
    }
    
    public static Codec getDefault() {
        if (Holder.defaultCodec == null) {
            throw new IllegalStateException("You tried to lookup the default Codec before all Codecs could be initialized. This likely happens if you try to get it from a Codec's ctor.");
        }
        return Holder.defaultCodec;
    }
    
    public static void setDefault(final Codec codec) {
        Holder.defaultCodec = Objects.requireNonNull(codec);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    private static final class Holder
    {
        private static final NamedSPILoader<Codec> LOADER;
        static Codec defaultCodec;
        
        static NamedSPILoader<Codec> getLoader() {
            if (Holder.LOADER == null) {
                throw new IllegalStateException("You tried to lookup a Codec by name before all Codecs could be initialized. This likely happens if you call Codec#forName from a Codec's ctor.");
            }
            return Holder.LOADER;
        }
        
        static {
            LOADER = new NamedSPILoader<Codec>(Codec.class);
            Holder.defaultCodec = Holder.LOADER.lookup("Lucene54");
        }
    }
}
