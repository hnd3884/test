package javax.xml.crypto.dsig.spec;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public final class XPathFilter2ParameterSpec implements TransformParameterSpec
{
    private final List xPathList;
    
    public XPathFilter2ParameterSpec(final List list) {
        if (list == null) {
            throw new NullPointerException("xPathList cannot be null");
        }
        final ArrayList list2 = new ArrayList(list);
        if (list2.isEmpty()) {
            throw new IllegalArgumentException("xPathList cannot be empty");
        }
        for (int size = list2.size(), i = 0; i < size; ++i) {
            if (!(list2.get(i) instanceof XPathType)) {
                throw new ClassCastException("xPathList[" + i + "] is not a valid type");
            }
        }
        this.xPathList = Collections.unmodifiableList((List<?>)list2);
    }
    
    public List getXPathList() {
        return this.xPathList;
    }
}
