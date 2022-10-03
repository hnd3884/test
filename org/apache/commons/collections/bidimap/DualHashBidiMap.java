package org.apache.commons.collections.bidimap;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections.BidiMap;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class DualHashBidiMap extends AbstractDualBidiMap implements Serializable
{
    private static final long serialVersionUID = 721969328361808L;
    
    public DualHashBidiMap() {
        super(new HashMap(), new HashMap());
    }
    
    public DualHashBidiMap(final Map map) {
        super(new HashMap(), new HashMap());
        this.putAll(map);
    }
    
    protected DualHashBidiMap(final Map normalMap, final Map reverseMap, final BidiMap inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
    }
    
    protected BidiMap createBidiMap(final Map normalMap, final Map reverseMap, final BidiMap inverseBidiMap) {
        return new DualHashBidiMap(normalMap, reverseMap, inverseBidiMap);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.maps[0]);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.maps[0] = new HashMap();
        this.maps[1] = new HashMap();
        final Map map = (Map)in.readObject();
        this.putAll(map);
    }
}
