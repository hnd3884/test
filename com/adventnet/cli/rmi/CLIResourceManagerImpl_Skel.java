package com.adventnet.cli.rmi;

import java.io.IOException;
import java.rmi.MarshalException;
import java.rmi.server.SkeletonMismatchException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.Remote;
import java.rmi.server.Operation;
import java.rmi.server.Skeleton;

public final class CLIResourceManagerImpl_Skel implements Skeleton
{
    private static final Operation[] operations;
    private static final long interfaceHash = 174905849328115593L;
    
    static {
        operations = new Operation[] { new Operation("void closeAllConnections()"), new Operation("int getKeepAliveTimeout()"), new Operation("int getMaxConnections()"), new Operation("int getSystemWideMaxConnections()"), new Operation("boolean isSetPooling()"), new Operation("void setKeepAliveTimeout(int)"), new Operation("void setMaxConnections(int)"), new Operation("void setPooling(boolean)"), new Operation("void setSystemWideMaxConnections(int)") };
    }
    
    public void dispatch(final Remote remote, final RemoteCall remoteCall, int n, final long n2) throws Exception {
        if (n < 0) {
            if (n2 == 5714694664026780380L) {
                n = 0;
            }
            else if (n2 == 9049373075317492944L) {
                n = 1;
            }
            else if (n2 == -6614033420789451520L) {
                n = 2;
            }
            else if (n2 == 4301691336213711973L) {
                n = 3;
            }
            else if (n2 == 8716287632483877530L) {
                n = 4;
            }
            else if (n2 == 9212739374310597693L) {
                n = 5;
            }
            else if (n2 == -3216999682298717359L) {
                n = 6;
            }
            else if (n2 == 3910746245484721574L) {
                n = 7;
            }
            else {
                if (n2 != -5549271102658825530L) {
                    throw new UnmarshalException("invalid method hash");
                }
                n = 8;
            }
        }
        else if (n2 != 174905849328115593L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        final CLIResourceManagerImpl cliResourceManagerImpl = (CLIResourceManagerImpl)remote;
        while (true) {
            switch (n) {
                case 0: {
                    remoteCall.releaseInputStream();
                    cliResourceManagerImpl.closeAllConnections();
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex) {
                        throw new MarshalException("error marshalling return", ex);
                    }
                }
                case 1: {
                    remoteCall.releaseInputStream();
                    final int keepAliveTimeout = cliResourceManagerImpl.getKeepAliveTimeout();
                    try {
                        remoteCall.getResultStream(true).writeInt(keepAliveTimeout);
                        return;
                    }
                    catch (final IOException ex2) {
                        throw new MarshalException("error marshalling return", ex2);
                    }
                }
                case 2: {
                    remoteCall.releaseInputStream();
                    final int maxConnections = cliResourceManagerImpl.getMaxConnections();
                    try {
                        remoteCall.getResultStream(true).writeInt(maxConnections);
                        return;
                    }
                    catch (final IOException ex3) {
                        throw new MarshalException("error marshalling return", ex3);
                    }
                }
                case 3: {
                    remoteCall.releaseInputStream();
                    final int systemWideMaxConnections = cliResourceManagerImpl.getSystemWideMaxConnections();
                    try {
                        remoteCall.getResultStream(true).writeInt(systemWideMaxConnections);
                        return;
                    }
                    catch (final IOException ex4) {
                        throw new MarshalException("error marshalling return", ex4);
                    }
                }
                case 5: {
                    continue;
                }
                case 4: {
                    remoteCall.releaseInputStream();
                    final boolean setPooling = cliResourceManagerImpl.isSetPooling();
                    try {
                        remoteCall.getResultStream(true).writeBoolean(setPooling);
                        return;
                    }
                    catch (final IOException ex5) {
                        throw new MarshalException("error marshalling return", ex5);
                    }
                    try {
                        final int int1 = remoteCall.getInputStream().readInt();
                    }
                    catch (final IOException ex6) {
                        throw new UnmarshalException("error unmarshalling arguments", ex6);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
                case 6: {
                    Label_0552: {
                        break Label_0552;
                        final int int1;
                        cliResourceManagerImpl.setKeepAliveTimeout(int1);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex7) {
                            throw new MarshalException("error marshalling return", ex7);
                        }
                        try {
                            final int int2 = remoteCall.getInputStream().readInt();
                        }
                        catch (final IOException ex8) {
                            throw new UnmarshalException("error unmarshalling arguments", ex8);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 7: {
                    Label_0656: {
                        break Label_0656;
                        final int int2;
                        cliResourceManagerImpl.setMaxConnections(int2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex9) {
                            throw new MarshalException("error marshalling return", ex9);
                        }
                        try {
                            final boolean boolean1 = remoteCall.getInputStream().readBoolean();
                        }
                        catch (final IOException ex10) {
                            throw new UnmarshalException("error unmarshalling arguments", ex10);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 8: {
                    int int3 = 0;
                    Label_0760: {
                        break Label_0760;
                        final boolean boolean1;
                        cliResourceManagerImpl.setPooling(boolean1);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex11) {
                            throw new MarshalException("error marshalling return", ex11);
                        }
                        try {
                            int3 = remoteCall.getInputStream().readInt();
                        }
                        catch (final IOException ex12) {
                            throw new UnmarshalException("error unmarshalling arguments", ex12);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                    cliResourceManagerImpl.setSystemWideMaxConnections(int3);
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex13) {
                        throw new MarshalException("error marshalling return", ex13);
                    }
                    break;
                }
            }
            break;
        }
        throw new UnmarshalException("invalid method number");
    }
    
    public Operation[] getOperations() {
        return CLIResourceManagerImpl_Skel.operations.clone();
    }
}
