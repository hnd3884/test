package org.apache.commons.chain.generic;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;

public class CopyCommand implements Command
{
    private String fromKey;
    private String toKey;
    private String value;
    
    public CopyCommand() {
        this.fromKey = null;
        this.toKey = null;
        this.value = null;
    }
    
    public String getFromKey() {
        return this.fromKey;
    }
    
    public void setFromKey(final String fromKey) {
        this.fromKey = fromKey;
    }
    
    public String getToKey() {
        return this.toKey;
    }
    
    public void setToKey(final String toKey) {
        this.toKey = toKey;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public boolean execute(final Context context) throws Exception {
        Object value = this.value;
        if (value == null) {
            value = context.get(this.getFromKey());
        }
        if (value != null) {
            context.put(this.getToKey(), value);
        }
        else {
            context.remove(this.getToKey());
        }
        return false;
    }
}
