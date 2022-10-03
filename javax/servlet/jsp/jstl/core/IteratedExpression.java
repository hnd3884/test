package javax.servlet.jsp.jstl.core;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map;
import java.util.Enumeration;
import java.util.Collection;
import javax.el.ELContext;
import java.util.Iterator;
import javax.el.ValueExpression;

public final class IteratedExpression
{
    protected final ValueExpression orig;
    protected final String delims;
    private Object originalListObject;
    private Iterator currentListObject;
    private int currentIndex;
    private TypesEnum type;
    
    public IteratedExpression(final ValueExpression orig, final String delims) {
        this.originalListObject = null;
        this.currentListObject = null;
        this.currentIndex = 0;
        this.type = TypesEnum.Undefined;
        this.orig = orig;
        this.delims = delims;
    }
    
    public Object getItem(final ELContext context, final int i) {
        if (this.originalListObject == null) {
            this.originalListObject = this.orig.getValue(context);
            if (this.originalListObject instanceof Collection) {
                this.type = TypesEnum.ACollection;
            }
            else if (this.originalListObject instanceof Iterator) {
                this.type = TypesEnum.AnIterator;
            }
            else if (this.originalListObject instanceof Enumeration) {
                this.type = TypesEnum.AnEnumeration;
            }
            else if (this.originalListObject instanceof Map) {
                this.type = TypesEnum.AMap;
            }
            else {
                if (!(this.originalListObject instanceof String)) {
                    throw new RuntimeException("IteratedExpression.getItem: Object not of correct type.");
                }
                this.type = TypesEnum.AString;
            }
            this.currentListObject = this.returnNewIterator(this.originalListObject, this.type);
        }
        Object currentObject = null;
        if (i < this.currentIndex) {
            this.currentListObject = this.returnNewIterator(this.originalListObject, this.type);
            this.currentIndex = 0;
        }
        while (this.currentIndex <= i) {
            if (!this.currentListObject.hasNext()) {
                throw new RuntimeException("IteratedExpression.getItem: Index out of Bounds");
            }
            currentObject = this.currentListObject.next();
            ++this.currentIndex;
        }
        return currentObject;
    }
    
    public ValueExpression getValueExpression() {
        return this.orig;
    }
    
    private Iterator returnNewIterator(final Object o, final TypesEnum type) {
        Iterator i = null;
        switch (type) {
            case ACollection: {
                i = ((Collection)o).iterator();
                break;
            }
            case AnIterator: {
                if (this.currentListObject == null) {
                    final Vector v = new Vector();
                    final Iterator myI = (Iterator)o;
                    while (myI.hasNext()) {
                        v.add(myI.next());
                    }
                    this.originalListObject = v;
                }
                i = ((Vector)this.originalListObject).iterator();
                break;
            }
            case AnEnumeration: {
                if (this.currentListObject == null) {
                    final Vector v = new Vector();
                    final Enumeration myE = (Enumeration)o;
                    while (myE.hasMoreElements()) {
                        v.add(myE.nextElement());
                    }
                    this.originalListObject = v;
                }
                i = ((Vector)this.originalListObject).iterator();
                break;
            }
            case AMap: {
                final Set s = ((Map)o).entrySet();
                i = s.iterator();
                break;
            }
            case AString: {
                if (this.currentListObject == null) {
                    final Vector v2 = new Vector();
                    final StringTokenizer st = new StringTokenizer((String)o, this.delims);
                    while (st.hasMoreElements()) {
                        v2.add(st.nextElement());
                    }
                    this.originalListObject = v2;
                }
                i = ((Vector)this.originalListObject).iterator();
                break;
            }
        }
        return i;
    }
    
    private enum TypesEnum
    {
        Undefined, 
        ACollection, 
        AnIterator, 
        AnEnumeration, 
        AMap, 
        AString;
    }
}
