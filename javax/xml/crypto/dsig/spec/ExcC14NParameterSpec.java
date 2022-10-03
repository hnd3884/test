package javax.xml.crypto.dsig.spec;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExcC14NParameterSpec implements C14NMethodParameterSpec
{
    private List preList;
    public static final String DEFAULT = "#default";
    
    public ExcC14NParameterSpec() {
        this.preList = Collections.EMPTY_LIST;
    }
    
    public ExcC14NParameterSpec(final List list) {
        if (list == null) {
            throw new NullPointerException("prefixList cannot be null");
        }
        this.preList = new ArrayList(list);
        for (int i = 0; i < this.preList.size(); ++i) {
            if (!(this.preList.get(i) instanceof String)) {
                throw new ClassCastException("not a String");
            }
        }
        this.preList = Collections.unmodifiableList((List<?>)this.preList);
    }
    
    public List getPrefixList() {
        return this.preList;
    }
}
