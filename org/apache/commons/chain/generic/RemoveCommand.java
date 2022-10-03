package org.apache.commons.chain.generic;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.Command;

public class RemoveCommand implements Command
{
    private String fromKey;
    
    public RemoveCommand() {
        this.fromKey = null;
    }
    
    public String getFromKey() {
        return this.fromKey;
    }
    
    public void setFromKey(final String fromKey) {
        this.fromKey = fromKey;
    }
    
    public boolean execute(final Context context) throws Exception {
        context.remove(this.getFromKey());
        return false;
    }
}
