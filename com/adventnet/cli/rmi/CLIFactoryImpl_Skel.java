package com.adventnet.cli.rmi;

import java.io.ObjectInput;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.io.IOException;
import java.rmi.MarshalException;
import java.rmi.server.SkeletonMismatchException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.Remote;
import java.rmi.server.Operation;
import java.rmi.server.Skeleton;

public final class CLIFactoryImpl_Skel implements Skeleton
{
    private static final Operation[] operations;
    private static final long interfaceHash = 4102910071669140523L;
    
    static {
        operations = new Operation[] { new Operation("com.adventnet.cli.rmi.CLIResourceManager createCLIResourceManager()"), new Operation("com.adventnet.cli.rmi.CLISession createCLISession(com.adventnet.cli.transport.CLIProtocolOptions)"), new Operation("com.adventnet.cli.rmi.CLISession createCLISession(com.adventnet.cli.transport.CLIProtocolOptions, boolean)") };
    }
    
    public void dispatch(final Remote remote, final RemoteCall remoteCall, int n, final long n2) throws Exception {
        if (n < 0) {
            if (n2 == 88892228454991705L) {
                n = 0;
            }
            else if (n2 == 3822035896352519788L) {
                n = 1;
            }
            else {
                if (n2 != -7388077998818797900L) {
                    throw new UnmarshalException("invalid method hash");
                }
                n = 2;
            }
        }
        else if (n2 != 4102910071669140523L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        final CLIFactoryImpl cliFactoryImpl = (CLIFactoryImpl)remote;
        while (true) {
            switch (n) {
                case 1: {
                    continue;
                }
                case 0: {
                    remoteCall.releaseInputStream();
                    final CLIResourceManager cliResourceManager = cliFactoryImpl.createCLIResourceManager();
                    try {
                        remoteCall.getResultStream(true).writeObject(cliResourceManager);
                        return;
                    }
                    catch (final IOException ex) {
                        throw new MarshalException("error marshalling return", ex);
                    }
                    try {
                        final CLIProtocolOptions cliProtocolOptions = (CLIProtocolOptions)remoteCall.getInputStream().readObject();
                    }
                    catch (final IOException ex2) {
                        throw new UnmarshalException("error unmarshalling arguments", ex2);
                    }
                    catch (final ClassNotFoundException ex3) {
                        throw new UnmarshalException("error unmarshalling arguments", ex3);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
                case 2: {
                    CLIProtocolOptions cliProtocolOptions2 = null;
                    boolean boolean1 = false;
                    Label_0289: {
                        break Label_0289;
                        final CLIProtocolOptions cliProtocolOptions;
                        final CLISession cliSession = cliFactoryImpl.createCLISession(cliProtocolOptions);
                        try {
                            remoteCall.getResultStream(true).writeObject(cliSession);
                            return;
                        }
                        catch (final IOException ex4) {
                            throw new MarshalException("error marshalling return", ex4);
                        }
                        try {
                            final ObjectInput inputStream = remoteCall.getInputStream();
                            cliProtocolOptions2 = (CLIProtocolOptions)inputStream.readObject();
                            boolean1 = inputStream.readBoolean();
                        }
                        catch (final IOException ex5) {
                            throw new UnmarshalException("error unmarshalling arguments", ex5);
                        }
                        catch (final ClassNotFoundException ex6) {
                            throw new UnmarshalException("error unmarshalling arguments", ex6);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                    final CLISession cliSession2 = cliFactoryImpl.createCLISession(cliProtocolOptions2, boolean1);
                    try {
                        remoteCall.getResultStream(true).writeObject(cliSession2);
                        return;
                    }
                    catch (final IOException ex7) {
                        throw new MarshalException("error marshalling return", ex7);
                    }
                    break;
                }
            }
            break;
        }
        throw new UnmarshalException("invalid method number");
    }
    
    public Operation[] getOperations() {
        return CLIFactoryImpl_Skel.operations.clone();
    }
}
