package sun.rmi.server;

import java.rmi.activation.ActivationException;

public class InactiveGroupException extends ActivationException
{
    private static final long serialVersionUID = -7491041778450214975L;
    
    public InactiveGroupException(final String s) {
        super(s);
    }
}
