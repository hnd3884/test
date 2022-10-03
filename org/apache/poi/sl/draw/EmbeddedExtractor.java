package org.apache.poi.sl.draw;

import java.util.function.Supplier;
import java.util.Collections;

public interface EmbeddedExtractor
{
    default Iterable<EmbeddedPart> getEmbeddings() {
        return (Iterable<EmbeddedPart>)Collections.emptyList();
    }
    
    public static class EmbeddedPart
    {
        private String name;
        private Supplier<byte[]> data;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public Supplier<byte[]> getData() {
            return this.data;
        }
        
        public void setData(final Supplier<byte[]> data) {
            this.data = data;
        }
    }
}
