package org.apache.xmlbeans.impl.jam.internal.parser;

import org.apache.xmlbeans.impl.jam.mutable.MInvokable;
import java.util.ArrayList;
import java.util.List;

public class ParamStructPool
{
    private static final boolean VERBOSE = true;
    private List mList;
    private int mLength;
    
    public ParamStructPool() {
        this.mList = new ArrayList();
        this.mLength = 0;
    }
    
    public void setParametersOn(final MInvokable e) {
        for (int i = 0; i < this.mLength; ++i) {
            final ParamStruct struct = this.mList.get(i);
            struct.createParameter(e);
        }
    }
    
    public void add(final String type, final String name) {
        ++this.mLength;
        if (this.mLength >= this.mList.size()) {
            this.mList.add(new ParamStruct(type, name));
        }
        else {
            this.mList.get(this.mLength).init(type, name);
        }
    }
    
    public void clear() {
        this.mLength = 0;
    }
}
