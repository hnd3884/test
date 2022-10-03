package java.rmi.activation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Properties;
import java.rmi.MarshalledObject;
import java.io.Serializable;

public final class ActivationGroupDesc implements Serializable
{
    private String className;
    private String location;
    private MarshalledObject<?> data;
    private CommandEnvironment env;
    private Properties props;
    private static final long serialVersionUID = -4936225423168276595L;
    
    public ActivationGroupDesc(final Properties properties, final CommandEnvironment commandEnvironment) {
        this(null, null, null, properties, commandEnvironment);
    }
    
    public ActivationGroupDesc(final String className, final String location, final MarshalledObject<?> data, final Properties props, final CommandEnvironment env) {
        this.props = props;
        this.env = env;
        this.data = data;
        this.location = location;
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public MarshalledObject<?> getData() {
        return this.data;
    }
    
    public Properties getPropertyOverrides() {
        return (this.props != null) ? ((Properties)this.props.clone()) : null;
    }
    
    public CommandEnvironment getCommandEnvironment() {
        return this.env;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ActivationGroupDesc) {
            final ActivationGroupDesc activationGroupDesc = (ActivationGroupDesc)o;
            if (this.className == null) {
                if (activationGroupDesc.className != null) {
                    return false;
                }
            }
            else if (!this.className.equals(activationGroupDesc.className)) {
                return false;
            }
            if (this.location == null) {
                if (activationGroupDesc.location != null) {
                    return false;
                }
            }
            else if (!this.location.equals(activationGroupDesc.location)) {
                return false;
            }
            if (this.data == null) {
                if (activationGroupDesc.data != null) {
                    return false;
                }
            }
            else if (!this.data.equals(activationGroupDesc.data)) {
                return false;
            }
            if (this.env == null) {
                if (activationGroupDesc.env != null) {
                    return false;
                }
            }
            else if (!this.env.equals(activationGroupDesc.env)) {
                return false;
            }
            if ((this.props != null) ? this.props.equals(activationGroupDesc.props) : (activationGroupDesc.props == null)) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.location == null) ? 0 : (this.location.hashCode() << 24)) ^ ((this.env == null) ? 0 : (this.env.hashCode() << 16)) ^ ((this.className == null) ? 0 : (this.className.hashCode() << 8)) ^ ((this.data == null) ? 0 : this.data.hashCode());
    }
    
    public static class CommandEnvironment implements Serializable
    {
        private static final long serialVersionUID = 6165754737887770191L;
        private String command;
        private String[] options;
        
        public CommandEnvironment(final String command, final String[] array) {
            this.command = command;
            if (array == null) {
                this.options = new String[0];
            }
            else {
                System.arraycopy(array, 0, this.options = new String[array.length], 0, array.length);
            }
        }
        
        public String getCommandPath() {
            return this.command;
        }
        
        public String[] getCommandOptions() {
            return this.options.clone();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof CommandEnvironment) {
                final CommandEnvironment commandEnvironment = (CommandEnvironment)o;
                if (this.command == null) {
                    if (commandEnvironment.command != null) {
                        return false;
                    }
                }
                else if (!this.command.equals(commandEnvironment.command)) {
                    return false;
                }
                if (Arrays.equals(this.options, commandEnvironment.options)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return (this.command == null) ? 0 : this.command.hashCode();
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            if (this.options == null) {
                this.options = new String[0];
            }
        }
    }
}
