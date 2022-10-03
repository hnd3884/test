package javax.print.attribute;

import java.io.Serializable;

public class HashDocAttributeSet extends HashAttributeSet implements DocAttributeSet, Serializable
{
    private static final long serialVersionUID = -1128534486061432528L;
    
    public HashDocAttributeSet() {
        super(DocAttribute.class);
    }
    
    public HashDocAttributeSet(final DocAttribute docAttribute) {
        super(docAttribute, DocAttribute.class);
    }
    
    public HashDocAttributeSet(final DocAttribute[] array) {
        super(array, DocAttribute.class);
    }
    
    public HashDocAttributeSet(final DocAttributeSet set) {
        super(set, DocAttribute.class);
    }
}
