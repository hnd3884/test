package org.jfree.ui.action;

import javax.swing.Action;
import java.util.ArrayList;
import java.util.HashMap;

public class DowngradeActionMap
{
    private final HashMap actionMap;
    private final ArrayList actionList;
    private DowngradeActionMap parent;
    
    public DowngradeActionMap() {
        this.actionMap = new HashMap();
        this.actionList = new ArrayList();
    }
    
    public Object[] allKeys() {
        if (this.parent == null) {
            return this.keys();
        }
        final Object[] parentKeys = this.parent.allKeys();
        final Object[] key = this.keys();
        final Object[] retval = new Object[parentKeys.length + key.length];
        System.arraycopy(key, 0, retval, 0, key.length);
        System.arraycopy(retval, 0, retval, key.length, retval.length);
        return retval;
    }
    
    public void clear() {
        this.actionMap.clear();
        this.actionList.clear();
    }
    
    public Action get(final Object key) {
        final Action retval = this.actionMap.get(key);
        if (retval != null) {
            return retval;
        }
        if (this.parent != null) {
            return this.parent.get(key);
        }
        return null;
    }
    
    public DowngradeActionMap getParent() {
        return this.parent;
    }
    
    public Object[] keys() {
        return this.actionList.toArray();
    }
    
    public void put(final Object key, final Action action) {
        if (action == null) {
            this.remove(key);
        }
        else {
            if (this.actionMap.containsKey(key)) {
                this.remove(key);
            }
            this.actionMap.put(key, action);
            this.actionList.add(key);
        }
    }
    
    public void remove(final Object key) {
        this.actionMap.remove(key);
        this.actionList.remove(key);
    }
    
    public void setParent(final DowngradeActionMap map) {
        this.parent = map;
    }
    
    public int size() {
        return this.actionMap.size();
    }
}
