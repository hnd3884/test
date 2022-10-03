package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Externalizable;
import java.beans.Beans;

public class CommandInfo
{
    private String verb;
    private String className;
    
    public CommandInfo(final String verb, final String className) {
        this.verb = verb;
        this.className = className;
    }
    
    public String getCommandClass() {
        return this.className;
    }
    
    public String getCommandName() {
        return this.verb;
    }
    
    public Object getCommandObject(final DataHandler dataHandler, final ClassLoader classLoader) throws IOException, ClassNotFoundException {
        final Object instantiate = Beans.instantiate(classLoader, this.className);
        if (instantiate != null) {
            if (instantiate instanceof CommandObject) {
                ((CommandObject)instantiate).setCommandContext(this.verb, dataHandler);
            }
            else if (instantiate instanceof Externalizable && dataHandler != null) {
                final InputStream inputStream = dataHandler.getInputStream();
                if (inputStream != null) {
                    ((Externalizable)instantiate).readExternal(new ObjectInputStream(inputStream));
                }
            }
        }
        return instantiate;
    }
}
