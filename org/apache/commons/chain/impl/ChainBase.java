package org.apache.commons.chain.impl;

import org.apache.commons.chain.Filter;
import org.apache.commons.chain.Context;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Chain;

public class ChainBase implements Chain
{
    protected Command[] commands;
    protected boolean frozen;
    
    public ChainBase() {
        this.commands = new Command[0];
        this.frozen = false;
    }
    
    public ChainBase(final Command command) {
        this.commands = new Command[0];
        this.frozen = false;
        this.addCommand(command);
    }
    
    public ChainBase(final Command[] commands) {
        this.commands = new Command[0];
        this.frozen = false;
        if (commands == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < commands.length; ++i) {
            this.addCommand(commands[i]);
        }
    }
    
    public ChainBase(final Collection commands) {
        this.commands = new Command[0];
        this.frozen = false;
        if (commands == null) {
            throw new IllegalArgumentException();
        }
        final Iterator elements = commands.iterator();
        while (elements.hasNext()) {
            this.addCommand(elements.next());
        }
    }
    
    public void addCommand(final Command command) {
        if (command == null) {
            throw new IllegalArgumentException();
        }
        if (this.frozen) {
            throw new IllegalStateException();
        }
        final Command[] results = new Command[this.commands.length + 1];
        System.arraycopy(this.commands, 0, results, 0, this.commands.length);
        results[this.commands.length] = command;
        this.commands = results;
    }
    
    public boolean execute(final Context context) throws Exception {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        this.frozen = true;
        boolean saveResult = false;
        Exception saveException = null;
        int i;
        int n;
        for (i = 0, n = this.commands.length, i = 0; i < n; ++i) {
            try {
                saveResult = this.commands[i].execute(context);
                if (saveResult) {
                    break;
                }
            }
            catch (final Exception e) {
                saveException = e;
                break;
            }
        }
        if (i >= n) {
            --i;
        }
        boolean handled = false;
        boolean result = false;
        for (int j = i; j >= 0; --j) {
            if (this.commands[j] instanceof Filter) {
                try {
                    result = ((Filter)this.commands[j]).postprocess(context, saveException);
                    if (result) {
                        handled = true;
                    }
                }
                catch (final Exception ex) {}
            }
        }
        if (saveException != null && !handled) {
            throw saveException;
        }
        return saveResult;
    }
    
    Command[] getCommands() {
        return this.commands;
    }
}
