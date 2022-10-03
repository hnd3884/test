package com.adventnet.cli.rmi;

import com.adventnet.cli.transport.CLIProtocolOptions;
import java.util.Properties;
import com.adventnet.cli.CLIMessage;
import java.rmi.MarshalException;
import java.io.IOException;
import java.rmi.server.SkeletonMismatchException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.Remote;
import java.rmi.server.Operation;
import java.rmi.server.Skeleton;

public final class CLISessionImpl_Skel implements Skeleton
{
    private static final Operation[] operations;
    private static final long interfaceHash = -2688865229007395434L;
    
    static {
        operations = new Operation[] { new Operation("void addCLIClient(com.adventnet.cli.rmi.CLIClient)"), new Operation("void addConnectionListener(com.adventnet.cli.rmi.ConnectionListener)"), new Operation("void close()"), new Operation("int getCLIClientsSize()"), new Operation("java.lang.String getCLIPrompt()"), new Operation("java.util.Properties getCLIPromptAction()"), new Operation("com.adventnet.cli.transport.CLIProtocolOptions getCLIProtocolOptions()"), new Operation("int getDebugLevel()"), new Operation("java.lang.String getInitialMessage()"), new Operation("java.lang.String getInterruptCmd()"), new Operation("int getKeepAliveTimeout()"), new Operation("int getMaxConnections()"), new Operation("int getRequestTimeout()"), new Operation("com.adventnet.cli.rmi.CLIResourceManager getResourceManager()"), new Operation("java.lang.String getTransportProviderClassName()"), new Operation("boolean isSetDebug()"), new Operation("boolean isSetIgnoreSpecialCharacters()"), new Operation("boolean isSetPooling()"), new Operation("void open()"), new Operation("void removeCLIClient(com.adventnet.cli.rmi.CLIClient)"), new Operation("void removeConnectionListener(com.adventnet.cli.rmi.ConnectionListener)"), new Operation("int send(com.adventnet.cli.CLIMessage)"), new Operation("void setCLIPrompt(java.lang.String)"), new Operation("void setCLIPromptAction(java.util.Properties)"), new Operation("void setCLIProtocolOptions(com.adventnet.cli.transport.CLIProtocolOptions)"), new Operation("void setDebug(boolean)"), new Operation("void setDebugLevel(int)"), new Operation("void setIgnoreSpecialCharacters(boolean)"), new Operation("void setInterruptCmd(java.lang.String)"), new Operation("void setKeepAliveTimeout(int)"), new Operation("void setMaxConnections(int)"), new Operation("void setPooling(boolean)"), new Operation("void setRequestTimeout(int)"), new Operation("void setTransportProviderClassName(java.lang.String)"), new Operation("com.adventnet.cli.CLIMessage syncSend(com.adventnet.cli.CLIMessage)") };
    }
    
    public void dispatch(final Remote remote, final RemoteCall remoteCall, int n, final long n2) throws Exception {
        if (n < 0) {
            if (n2 == -7654427063607400320L) {
                n = 0;
            }
            else if (n2 == 3258586727660034206L) {
                n = 1;
            }
            else if (n2 == -4742752445160157748L) {
                n = 2;
            }
            else if (n2 == -7988495090462633788L) {
                n = 3;
            }
            else if (n2 == 5833517618819025275L) {
                n = 4;
            }
            else if (n2 == 5097756750083467711L) {
                n = 5;
            }
            else if (n2 == 4469770184685420537L) {
                n = 6;
            }
            else if (n2 == 8250172403747277032L) {
                n = 7;
            }
            else if (n2 == 5801693936419199023L) {
                n = 8;
            }
            else if (n2 == 4708491262652393107L) {
                n = 9;
            }
            else if (n2 == 9049373075317492944L) {
                n = 10;
            }
            else if (n2 == -6614033420789451520L) {
                n = 11;
            }
            else if (n2 == -8372516440506460413L) {
                n = 12;
            }
            else if (n2 == 7803003237860320922L) {
                n = 13;
            }
            else if (n2 == 7797290759689912709L) {
                n = 14;
            }
            else if (n2 == 103246940272821447L) {
                n = 15;
            }
            else if (n2 == -703676580845550411L) {
                n = 16;
            }
            else if (n2 == 8716287632483877530L) {
                n = 17;
            }
            else if (n2 == 2108930122558793662L) {
                n = 18;
            }
            else if (n2 == 8132075043954259895L) {
                n = 19;
            }
            else if (n2 == 2025112918616732512L) {
                n = 20;
            }
            else if (n2 == -2150685831246371616L) {
                n = 21;
            }
            else if (n2 == 507499627533133429L) {
                n = 22;
            }
            else if (n2 == -2704595152191911123L) {
                n = 23;
            }
            else if (n2 == -2959263091887086154L) {
                n = 24;
            }
            else if (n2 == -3195502464907809318L) {
                n = 25;
            }
            else if (n2 == 4300081604050436896L) {
                n = 26;
            }
            else if (n2 == 3699933648070141756L) {
                n = 27;
            }
            else if (n2 == -9026050086541106607L) {
                n = 28;
            }
            else if (n2 == 9212739374310597693L) {
                n = 29;
            }
            else if (n2 == -3216999682298717359L) {
                n = 30;
            }
            else if (n2 == 3910746245484721574L) {
                n = 31;
            }
            else if (n2 == -400062043927202424L) {
                n = 32;
            }
            else if (n2 == 6172408367923534378L) {
                n = 33;
            }
            else {
                if (n2 != -2000283983769136908L) {
                    throw new UnmarshalException("invalid method hash");
                }
                n = 34;
            }
        }
        else if (n2 != -2688865229007395434L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        final CLISessionImpl cliSessionImpl = (CLISessionImpl)remote;
        while (true) {
            switch (n) {
                case 0: {
                    try {
                        final CLIClient cliClient = (CLIClient)remoteCall.getInputStream().readObject();
                    }
                    catch (final IOException ex) {
                        throw new UnmarshalException("error unmarshalling arguments", ex);
                    }
                    catch (final ClassNotFoundException ex2) {
                        throw new UnmarshalException("error unmarshalling arguments", ex2);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
                case 1: {
                    ConnectionListener connectionListener = null;
                    Label_0833: {
                        break Label_0833;
                        final CLIClient cliClient;
                        cliSessionImpl.addCLIClient(cliClient);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex3) {
                            throw new MarshalException("error marshalling return", ex3);
                        }
                        try {
                            connectionListener = (ConnectionListener)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex4) {
                            throw new UnmarshalException("error unmarshalling arguments", ex4);
                        }
                        catch (final ClassNotFoundException ex5) {
                            throw new UnmarshalException("error unmarshalling arguments", ex5);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                    cliSessionImpl.addConnectionListener(connectionListener);
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex6) {
                        throw new MarshalException("error marshalling return", ex6);
                    }
                }
                case 2: {
                    remoteCall.releaseInputStream();
                    cliSessionImpl.close();
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex7) {
                        throw new MarshalException("error marshalling return", ex7);
                    }
                }
                case 3: {
                    remoteCall.releaseInputStream();
                    final int cliClientsSize = cliSessionImpl.getCLIClientsSize();
                    try {
                        remoteCall.getResultStream(true).writeInt(cliClientsSize);
                        return;
                    }
                    catch (final IOException ex8) {
                        throw new MarshalException("error marshalling return", ex8);
                    }
                }
                case 4: {
                    remoteCall.releaseInputStream();
                    final String cliPrompt = cliSessionImpl.getCLIPrompt();
                    try {
                        remoteCall.getResultStream(true).writeObject(cliPrompt);
                        return;
                    }
                    catch (final IOException ex9) {
                        throw new MarshalException("error marshalling return", ex9);
                    }
                }
                case 5: {
                    remoteCall.releaseInputStream();
                    final Properties cliPromptAction = cliSessionImpl.getCLIPromptAction();
                    try {
                        remoteCall.getResultStream(true).writeObject(cliPromptAction);
                        return;
                    }
                    catch (final IOException ex10) {
                        throw new MarshalException("error marshalling return", ex10);
                    }
                }
                case 6: {
                    remoteCall.releaseInputStream();
                    final CLIProtocolOptions cliProtocolOptions = cliSessionImpl.getCLIProtocolOptions();
                    try {
                        remoteCall.getResultStream(true).writeObject(cliProtocolOptions);
                        return;
                    }
                    catch (final IOException ex11) {
                        throw new MarshalException("error marshalling return", ex11);
                    }
                }
                case 7: {
                    remoteCall.releaseInputStream();
                    final int debugLevel = cliSessionImpl.getDebugLevel();
                    try {
                        remoteCall.getResultStream(true).writeInt(debugLevel);
                        return;
                    }
                    catch (final IOException ex12) {
                        throw new MarshalException("error marshalling return", ex12);
                    }
                }
                case 8: {
                    remoteCall.releaseInputStream();
                    final String initialMessage = cliSessionImpl.getInitialMessage();
                    try {
                        remoteCall.getResultStream(true).writeObject(initialMessage);
                        return;
                    }
                    catch (final IOException ex13) {
                        throw new MarshalException("error marshalling return", ex13);
                    }
                }
                case 9: {
                    remoteCall.releaseInputStream();
                    final String interruptCmd = cliSessionImpl.getInterruptCmd();
                    try {
                        remoteCall.getResultStream(true).writeObject(interruptCmd);
                        return;
                    }
                    catch (final IOException ex14) {
                        throw new MarshalException("error marshalling return", ex14);
                    }
                }
                case 10: {
                    remoteCall.releaseInputStream();
                    final int keepAliveTimeout = cliSessionImpl.getKeepAliveTimeout();
                    try {
                        remoteCall.getResultStream(true).writeInt(keepAliveTimeout);
                        return;
                    }
                    catch (final IOException ex15) {
                        throw new MarshalException("error marshalling return", ex15);
                    }
                }
                case 11: {
                    remoteCall.releaseInputStream();
                    final int maxConnections = cliSessionImpl.getMaxConnections();
                    try {
                        remoteCall.getResultStream(true).writeInt(maxConnections);
                        return;
                    }
                    catch (final IOException ex16) {
                        throw new MarshalException("error marshalling return", ex16);
                    }
                }
                case 12: {
                    remoteCall.releaseInputStream();
                    final int requestTimeout = cliSessionImpl.getRequestTimeout();
                    try {
                        remoteCall.getResultStream(true).writeInt(requestTimeout);
                        return;
                    }
                    catch (final IOException ex17) {
                        throw new MarshalException("error marshalling return", ex17);
                    }
                }
                case 13: {
                    remoteCall.releaseInputStream();
                    final CLIResourceManager resourceManager = cliSessionImpl.getResourceManager();
                    try {
                        remoteCall.getResultStream(true).writeObject(resourceManager);
                        return;
                    }
                    catch (final IOException ex18) {
                        throw new MarshalException("error marshalling return", ex18);
                    }
                }
                case 14: {
                    remoteCall.releaseInputStream();
                    final String transportProviderClassName = cliSessionImpl.getTransportProviderClassName();
                    try {
                        remoteCall.getResultStream(true).writeObject(transportProviderClassName);
                        return;
                    }
                    catch (final IOException ex19) {
                        throw new MarshalException("error marshalling return", ex19);
                    }
                }
                case 15: {
                    remoteCall.releaseInputStream();
                    final boolean setDebug = cliSessionImpl.isSetDebug();
                    try {
                        remoteCall.getResultStream(true).writeBoolean(setDebug);
                        return;
                    }
                    catch (final IOException ex20) {
                        throw new MarshalException("error marshalling return", ex20);
                    }
                }
                case 16: {
                    remoteCall.releaseInputStream();
                    final boolean setIgnoreSpecialCharacters = cliSessionImpl.isSetIgnoreSpecialCharacters();
                    try {
                        remoteCall.getResultStream(true).writeBoolean(setIgnoreSpecialCharacters);
                        return;
                    }
                    catch (final IOException ex21) {
                        throw new MarshalException("error marshalling return", ex21);
                    }
                }
                case 17: {
                    remoteCall.releaseInputStream();
                    final boolean setPooling = cliSessionImpl.isSetPooling();
                    try {
                        remoteCall.getResultStream(true).writeBoolean(setPooling);
                        return;
                    }
                    catch (final IOException ex22) {
                        throw new MarshalException("error marshalling return", ex22);
                    }
                }
                case 19: {
                    continue;
                }
                case 18: {
                    remoteCall.releaseInputStream();
                    cliSessionImpl.open();
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex23) {
                        throw new MarshalException("error marshalling return", ex23);
                    }
                    try {
                        final CLIClient cliClient2 = (CLIClient)remoteCall.getInputStream().readObject();
                    }
                    catch (final IOException ex24) {
                        throw new UnmarshalException("error unmarshalling arguments", ex24);
                    }
                    catch (final ClassNotFoundException ex25) {
                        throw new UnmarshalException("error unmarshalling arguments", ex25);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
                case 20: {
                    Label_1867: {
                        break Label_1867;
                        final CLIClient cliClient2;
                        cliSessionImpl.removeCLIClient(cliClient2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex26) {
                            throw new MarshalException("error marshalling return", ex26);
                        }
                        try {
                            final ConnectionListener connectionListener2 = (ConnectionListener)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex27) {
                            throw new UnmarshalException("error unmarshalling arguments", ex27);
                        }
                        catch (final ClassNotFoundException ex28) {
                            throw new UnmarshalException("error unmarshalling arguments", ex28);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 21: {
                    Label_1988: {
                        break Label_1988;
                        final ConnectionListener connectionListener2;
                        cliSessionImpl.removeConnectionListener(connectionListener2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex29) {
                            throw new MarshalException("error marshalling return", ex29);
                        }
                        try {
                            final CLIMessage cliMessage = (CLIMessage)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex30) {
                            throw new UnmarshalException("error unmarshalling arguments", ex30);
                        }
                        catch (final ClassNotFoundException ex31) {
                            throw new UnmarshalException("error unmarshalling arguments", ex31);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 22: {
                    Label_2121: {
                        break Label_2121;
                        final CLIMessage cliMessage;
                        final int send = cliSessionImpl.send(cliMessage);
                        try {
                            remoteCall.getResultStream(true).writeInt(send);
                            return;
                        }
                        catch (final IOException ex32) {
                            throw new MarshalException("error marshalling return", ex32);
                        }
                        try {
                            final String cliPrompt2 = (String)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex33) {
                            throw new UnmarshalException("error unmarshalling arguments", ex33);
                        }
                        catch (final ClassNotFoundException ex34) {
                            throw new UnmarshalException("error unmarshalling arguments", ex34);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 23: {
                    Label_2242: {
                        break Label_2242;
                        final String cliPrompt2;
                        cliSessionImpl.setCLIPrompt(cliPrompt2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex35) {
                            throw new MarshalException("error marshalling return", ex35);
                        }
                        try {
                            final Properties cliPromptAction2 = (Properties)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex36) {
                            throw new UnmarshalException("error unmarshalling arguments", ex36);
                        }
                        catch (final ClassNotFoundException ex37) {
                            throw new UnmarshalException("error unmarshalling arguments", ex37);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 24: {
                    Label_2363: {
                        break Label_2363;
                        final Properties cliPromptAction2;
                        cliSessionImpl.setCLIPromptAction(cliPromptAction2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex38) {
                            throw new MarshalException("error marshalling return", ex38);
                        }
                        try {
                            final CLIProtocolOptions cliProtocolOptions2 = (CLIProtocolOptions)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex39) {
                            throw new UnmarshalException("error unmarshalling arguments", ex39);
                        }
                        catch (final ClassNotFoundException ex40) {
                            throw new UnmarshalException("error unmarshalling arguments", ex40);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 25: {
                    Label_2484: {
                        break Label_2484;
                        final CLIProtocolOptions cliProtocolOptions2;
                        cliSessionImpl.setCLIProtocolOptions(cliProtocolOptions2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex41) {
                            throw new MarshalException("error marshalling return", ex41);
                        }
                        try {
                            final boolean boolean1 = remoteCall.getInputStream().readBoolean();
                        }
                        catch (final IOException ex42) {
                            throw new UnmarshalException("error unmarshalling arguments", ex42);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 26: {
                    Label_2588: {
                        break Label_2588;
                        final boolean boolean1;
                        cliSessionImpl.setDebug(boolean1);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex43) {
                            throw new MarshalException("error marshalling return", ex43);
                        }
                        try {
                            final int int1 = remoteCall.getInputStream().readInt();
                        }
                        catch (final IOException ex44) {
                            throw new UnmarshalException("error unmarshalling arguments", ex44);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 27: {
                    Label_2692: {
                        break Label_2692;
                        final int int1;
                        cliSessionImpl.setDebugLevel(int1);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex45) {
                            throw new MarshalException("error marshalling return", ex45);
                        }
                        try {
                            final boolean boolean2 = remoteCall.getInputStream().readBoolean();
                        }
                        catch (final IOException ex46) {
                            throw new UnmarshalException("error unmarshalling arguments", ex46);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 28: {
                    Label_2796: {
                        break Label_2796;
                        final boolean boolean2;
                        cliSessionImpl.setIgnoreSpecialCharacters(boolean2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex47) {
                            throw new MarshalException("error marshalling return", ex47);
                        }
                        try {
                            final String interruptCmd2 = (String)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex48) {
                            throw new UnmarshalException("error unmarshalling arguments", ex48);
                        }
                        catch (final ClassNotFoundException ex49) {
                            throw new UnmarshalException("error unmarshalling arguments", ex49);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 29: {
                    Label_2917: {
                        break Label_2917;
                        final String interruptCmd2;
                        cliSessionImpl.setInterruptCmd(interruptCmd2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex50) {
                            throw new MarshalException("error marshalling return", ex50);
                        }
                        try {
                            final int int2 = remoteCall.getInputStream().readInt();
                        }
                        catch (final IOException ex51) {
                            throw new UnmarshalException("error unmarshalling arguments", ex51);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 30: {
                    Label_3021: {
                        break Label_3021;
                        final int int2;
                        cliSessionImpl.setKeepAliveTimeout(int2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex52) {
                            throw new MarshalException("error marshalling return", ex52);
                        }
                        try {
                            final int int3 = remoteCall.getInputStream().readInt();
                        }
                        catch (final IOException ex53) {
                            throw new UnmarshalException("error unmarshalling arguments", ex53);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 31: {
                    Label_3125: {
                        break Label_3125;
                        final int int3;
                        cliSessionImpl.setMaxConnections(int3);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex54) {
                            throw new MarshalException("error marshalling return", ex54);
                        }
                        try {
                            final boolean boolean3 = remoteCall.getInputStream().readBoolean();
                        }
                        catch (final IOException ex55) {
                            throw new UnmarshalException("error unmarshalling arguments", ex55);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 32: {
                    Label_3229: {
                        break Label_3229;
                        final boolean boolean3;
                        cliSessionImpl.setPooling(boolean3);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex56) {
                            throw new MarshalException("error marshalling return", ex56);
                        }
                        try {
                            final int int4 = remoteCall.getInputStream().readInt();
                        }
                        catch (final IOException ex57) {
                            throw new UnmarshalException("error unmarshalling arguments", ex57);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 33: {
                    Label_3333: {
                        break Label_3333;
                        final int int4;
                        cliSessionImpl.setRequestTimeout(int4);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex58) {
                            throw new MarshalException("error marshalling return", ex58);
                        }
                        try {
                            final String transportProviderClassName2 = (String)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex59) {
                            throw new UnmarshalException("error unmarshalling arguments", ex59);
                        }
                        catch (final ClassNotFoundException ex60) {
                            throw new UnmarshalException("error unmarshalling arguments", ex60);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                }
                case 34: {
                    CLIMessage cliMessage2 = null;
                    Label_3454: {
                        break Label_3454;
                        final String transportProviderClassName2;
                        cliSessionImpl.setTransportProviderClassName(transportProviderClassName2);
                        try {
                            remoteCall.getResultStream(true);
                            return;
                        }
                        catch (final IOException ex61) {
                            throw new MarshalException("error marshalling return", ex61);
                        }
                        try {
                            cliMessage2 = (CLIMessage)remoteCall.getInputStream().readObject();
                        }
                        catch (final IOException ex62) {
                            throw new UnmarshalException("error unmarshalling arguments", ex62);
                        }
                        catch (final ClassNotFoundException ex63) {
                            throw new UnmarshalException("error unmarshalling arguments", ex63);
                        }
                        finally {
                            remoteCall.releaseInputStream();
                        }
                    }
                    final CLIMessage syncSend = cliSessionImpl.syncSend(cliMessage2);
                    try {
                        remoteCall.getResultStream(true).writeObject(syncSend);
                        return;
                    }
                    catch (final IOException ex64) {
                        throw new MarshalException("error marshalling return", ex64);
                    }
                    break;
                }
            }
            break;
        }
        throw new UnmarshalException("invalid method number");
    }
    
    public Operation[] getOperations() {
        return CLISessionImpl_Skel.operations.clone();
    }
}
