package javax.accessibility;

import java.util.Vector;

public class AccessibleRelationSet
{
    protected Vector<AccessibleRelation> relations;
    
    public AccessibleRelationSet() {
        this.relations = null;
        this.relations = null;
    }
    
    public AccessibleRelationSet(final AccessibleRelation[] array) {
        this.relations = null;
        if (array.length != 0) {
            this.relations = new Vector<AccessibleRelation>(array.length);
            for (int i = 0; i < array.length; ++i) {
                this.add(array[i]);
            }
        }
    }
    
    public boolean add(final AccessibleRelation accessibleRelation) {
        if (this.relations == null) {
            this.relations = new Vector<AccessibleRelation>();
        }
        final AccessibleRelation value = this.get(accessibleRelation.getKey());
        if (value == null) {
            this.relations.addElement(accessibleRelation);
            return true;
        }
        final Object[] target = value.getTarget();
        final Object[] target2 = accessibleRelation.getTarget();
        final int n = target.length + target2.length;
        final Object[] target3 = new Object[n];
        for (int i = 0; i < target.length; ++i) {
            target3[i] = target[i];
        }
        for (int j = target.length, n2 = 0; j < n; ++j, ++n2) {
            target3[j] = target2[n2];
        }
        value.setTarget(target3);
        return true;
    }
    
    public void addAll(final AccessibleRelation[] array) {
        if (array.length != 0) {
            if (this.relations == null) {
                this.relations = new Vector<AccessibleRelation>(array.length);
            }
            for (int i = 0; i < array.length; ++i) {
                this.add(array[i]);
            }
        }
    }
    
    public boolean remove(final AccessibleRelation accessibleRelation) {
        return this.relations != null && this.relations.removeElement(accessibleRelation);
    }
    
    public void clear() {
        if (this.relations != null) {
            this.relations.removeAllElements();
        }
    }
    
    public int size() {
        if (this.relations == null) {
            return 0;
        }
        return this.relations.size();
    }
    
    public boolean contains(final String s) {
        return this.get(s) != null;
    }
    
    public AccessibleRelation get(final String s) {
        if (this.relations == null) {
            return null;
        }
        for (int size = this.relations.size(), i = 0; i < size; ++i) {
            final AccessibleRelation accessibleRelation = this.relations.elementAt(i);
            if (accessibleRelation != null && accessibleRelation.getKey().equals(s)) {
                return accessibleRelation;
            }
        }
        return null;
    }
    
    public AccessibleRelation[] toArray() {
        if (this.relations == null) {
            return new AccessibleRelation[0];
        }
        final AccessibleRelation[] array = new AccessibleRelation[this.relations.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.relations.elementAt(i);
        }
        return array;
    }
    
    @Override
    public String toString() {
        String s = "";
        if (this.relations != null && this.relations.size() > 0) {
            s = this.relations.elementAt(0).toDisplayString();
            for (int i = 1; i < this.relations.size(); ++i) {
                s = s + "," + this.relations.elementAt(i).toDisplayString();
            }
        }
        return s;
    }
}
