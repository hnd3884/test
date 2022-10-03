package sun.rmi.server;

import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;

public abstract class ActivationGroupInit
{
    public static void main(final String[] array) {
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            final MarshalInputStream marshalInputStream = new MarshalInputStream(System.in);
            ActivationGroup.createGroup((ActivationGroupID)marshalInputStream.readObject(), (ActivationGroupDesc)marshalInputStream.readObject(), marshalInputStream.readLong());
        }
        catch (final Exception ex) {
            System.err.println("Exception in starting ActivationGroupInit:");
            ex.printStackTrace();
        }
        finally {
            try {
                System.in.close();
            }
            catch (final Exception ex2) {}
        }
    }
}
