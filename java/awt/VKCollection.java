package java.awt;

import java.util.HashMap;
import java.util.Map;

class VKCollection
{
    Map<Integer, String> code2name;
    Map<String, Integer> name2code;
    
    public VKCollection() {
        this.code2name = new HashMap<Integer, String>();
        this.name2code = new HashMap<String, Integer>();
    }
    
    public synchronized void put(final String s, final Integer n) {
        assert s != null && n != null;
        assert this.findName(n) == null;
        assert this.findCode(s) == null;
        this.code2name.put(n, s);
        this.name2code.put(s, n);
    }
    
    public synchronized Integer findCode(final String s) {
        assert s != null;
        return this.name2code.get(s);
    }
    
    public synchronized String findName(final Integer n) {
        assert n != null;
        return this.code2name.get(n);
    }
}
