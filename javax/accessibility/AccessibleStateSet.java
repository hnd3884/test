package javax.accessibility;

import java.util.Vector;

public class AccessibleStateSet
{
    protected Vector<AccessibleState> states;
    
    public AccessibleStateSet() {
        this.states = null;
        this.states = null;
    }
    
    public AccessibleStateSet(final AccessibleState[] array) {
        this.states = null;
        if (array.length != 0) {
            this.states = new Vector<AccessibleState>(array.length);
            for (int i = 0; i < array.length; ++i) {
                if (!this.states.contains(array[i])) {
                    this.states.addElement(array[i]);
                }
            }
        }
    }
    
    public boolean add(final AccessibleState accessibleState) {
        if (this.states == null) {
            this.states = new Vector<AccessibleState>();
        }
        if (!this.states.contains(accessibleState)) {
            this.states.addElement(accessibleState);
            return true;
        }
        return false;
    }
    
    public void addAll(final AccessibleState[] array) {
        if (array.length != 0) {
            if (this.states == null) {
                this.states = new Vector<AccessibleState>(array.length);
            }
            for (int i = 0; i < array.length; ++i) {
                if (!this.states.contains(array[i])) {
                    this.states.addElement(array[i]);
                }
            }
        }
    }
    
    public boolean remove(final AccessibleState accessibleState) {
        return this.states != null && this.states.removeElement(accessibleState);
    }
    
    public void clear() {
        if (this.states != null) {
            this.states.removeAllElements();
        }
    }
    
    public boolean contains(final AccessibleState accessibleState) {
        return this.states != null && this.states.contains(accessibleState);
    }
    
    public AccessibleState[] toArray() {
        if (this.states == null) {
            return new AccessibleState[0];
        }
        final AccessibleState[] array = new AccessibleState[this.states.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.states.elementAt(i);
        }
        return array;
    }
    
    @Override
    public String toString() {
        String s = null;
        if (this.states != null && this.states.size() > 0) {
            s = this.states.elementAt(0).toDisplayString();
            for (int i = 1; i < this.states.size(); ++i) {
                s = s + "," + this.states.elementAt(i).toDisplayString();
            }
        }
        return s;
    }
}
