package org.bouncycastle.util.io.pem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PemObject implements PemObjectGenerator
{
    private static final List EMPTY_LIST;
    private String type;
    private List headers;
    private byte[] content;
    
    public PemObject(final String s, final byte[] array) {
        this(s, PemObject.EMPTY_LIST, array);
    }
    
    public PemObject(final String type, final List list, final byte[] content) {
        this.type = type;
        this.headers = Collections.unmodifiableList((List<?>)list);
        this.content = content;
    }
    
    public String getType() {
        return this.type;
    }
    
    public List getHeaders() {
        return this.headers;
    }
    
    public byte[] getContent() {
        return this.content;
    }
    
    public PemObject generate() throws PemGenerationException {
        return this;
    }
    
    static {
        EMPTY_LIST = Collections.unmodifiableList((List<?>)new ArrayList<Object>());
    }
}
