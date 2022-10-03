package com.adventnet.cli.ssh.sshwindow;

import java.io.ObjectInput;
import java.rmi.MarshalException;
import java.io.IOException;
import java.rmi.server.SkeletonMismatchException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.Remote;
import java.rmi.server.Operation;
import java.rmi.server.Skeleton;

public final class SshProxyClientAPIImpl_Skel implements Skeleton
{
    private static final Operation[] operations;
    private static final long interfaceHash = 8499256766154638547L;
    
    static {
        operations = new Operation[] { new Operation("int connect(java.lang.String, int)"), new Operation("int connect(java.lang.String, int, java.lang.String)"), new Operation("void disconnect(int)"), new Operation("java.lang.String login(int, java.lang.String, java.lang.String)"), new Operation("int read(int, byte[])"), new Operation("byte readAsByteArray(int)[]"), new Operation("void setSocketTimeout(int, int)"), new Operation("void write(int, byte)"), new Operation("void write(int, byte[])") };
    }
    
    public void dispatch(final Remote remote, final RemoteCall remoteCall, int n, final long n2) throws Exception {
        if (n < 0) {
            if (n2 == 48749161004939641L) {
                n = 0;
            }
            else if (n2 == 7403871753764022759L) {
                n = 1;
            }
            else if (n2 == 3674334102525678583L) {
                n = 2;
            }
            else if (n2 == -5233700984916094130L) {
                n = 3;
            }
            else if (n2 == 1097993668938678719L) {
                n = 4;
            }
            else if (n2 == -4467217384959979745L) {
                n = 5;
            }
            else if (n2 == -6726805466994499671L) {
                n = 6;
            }
            else if (n2 == 3309430870527555919L) {
                n = 7;
            }
            else {
                if (n2 != -6047237704507632803L) {
                    throw new UnmarshalException("invalid method hash");
                }
                n = 8;
            }
        }
        else if (n2 != 8499256766154638547L) {
            throw new SkeletonMismatchException("interface hash mismatch");
        }
        final SshProxyClientAPIImpl sshProxyClientAPIImpl = (SshProxyClientAPIImpl)remote;
        switch (n) {
            case 0: {
                try {
                    final ObjectInput inputStream = remoteCall.getInputStream();
                    final String s = (String)inputStream.readObject();
                    final int int1 = inputStream.readInt();
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
                Label_0364: {
                    break Label_0364;
                    final String s;
                    final int int1;
                    final int connect = sshProxyClientAPIImpl.connect(s, int1);
                    try {
                        remoteCall.getResultStream(true).writeInt(connect);
                        return;
                    }
                    catch (final IOException ex3) {
                        throw new MarshalException("error marshalling return", ex3);
                    }
                    try {
                        final ObjectInput inputStream2 = remoteCall.getInputStream();
                        final String s2 = (String)inputStream2.readObject();
                        final int int2 = inputStream2.readInt();
                        final String s3 = (String)inputStream2.readObject();
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
            }
            case 2: {
                Label_0522: {
                    break Label_0522;
                    final String s2;
                    final int int2;
                    final String s3;
                    final int connect2 = sshProxyClientAPIImpl.connect(s2, int2, s3);
                    try {
                        remoteCall.getResultStream(true).writeInt(connect2);
                        return;
                    }
                    catch (final IOException ex6) {
                        throw new MarshalException("error marshalling return", ex6);
                    }
                    try {
                        final int int3 = remoteCall.getInputStream().readInt();
                    }
                    catch (final IOException ex7) {
                        throw new UnmarshalException("error unmarshalling arguments", ex7);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
            }
            case 3: {
                Label_0626: {
                    break Label_0626;
                    final int int3;
                    sshProxyClientAPIImpl.disconnect(int3);
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex8) {
                        throw new MarshalException("error marshalling return", ex8);
                    }
                    try {
                        final ObjectInput inputStream3 = remoteCall.getInputStream();
                        final int int4 = inputStream3.readInt();
                        final String s4 = (String)inputStream3.readObject();
                        final String s5 = (String)inputStream3.readObject();
                    }
                    catch (final IOException ex9) {
                        throw new UnmarshalException("error unmarshalling arguments", ex9);
                    }
                    catch (final ClassNotFoundException ex10) {
                        throw new UnmarshalException("error unmarshalling arguments", ex10);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
            }
            case 4: {
                Label_0784: {
                    break Label_0784;
                    final int int4;
                    final String s4;
                    final String s5;
                    final String login = sshProxyClientAPIImpl.login(int4, s4, s5);
                    try {
                        remoteCall.getResultStream(true).writeObject(login);
                        return;
                    }
                    catch (final IOException ex11) {
                        throw new MarshalException("error marshalling return", ex11);
                    }
                    try {
                        final ObjectInput inputStream4 = remoteCall.getInputStream();
                        final int int5 = inputStream4.readInt();
                        final byte[] array = (byte[])inputStream4.readObject();
                    }
                    catch (final IOException ex12) {
                        throw new UnmarshalException("error unmarshalling arguments", ex12);
                    }
                    catch (final ClassNotFoundException ex13) {
                        throw new UnmarshalException("error unmarshalling arguments", ex13);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
            }
            case 5: {
                Label_0928: {
                    break Label_0928;
                    final int int5;
                    final byte[] array;
                    final int read = sshProxyClientAPIImpl.read(int5, array);
                    try {
                        remoteCall.getResultStream(true).writeInt(read);
                        return;
                    }
                    catch (final IOException ex14) {
                        throw new MarshalException("error marshalling return", ex14);
                    }
                    try {
                        final int int6 = remoteCall.getInputStream().readInt();
                    }
                    catch (final IOException ex15) {
                        throw new UnmarshalException("error unmarshalling arguments", ex15);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
            }
            case 6: {
                Label_1044: {
                    break Label_1044;
                    final int int6;
                    final byte[] asByteArray = sshProxyClientAPIImpl.readAsByteArray(int6);
                    try {
                        remoteCall.getResultStream(true).writeObject(asByteArray);
                        return;
                    }
                    catch (final IOException ex16) {
                        throw new MarshalException("error marshalling return", ex16);
                    }
                    try {
                        final ObjectInput inputStream5 = remoteCall.getInputStream();
                        final int int7 = inputStream5.readInt();
                        final int int8 = inputStream5.readInt();
                    }
                    catch (final IOException ex17) {
                        throw new UnmarshalException("error unmarshalling arguments", ex17);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
            }
            case 7: {
                Label_1159: {
                    break Label_1159;
                    final int int7;
                    final int int8;
                    sshProxyClientAPIImpl.setSocketTimeout(int7, int8);
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex18) {
                        throw new MarshalException("error marshalling return", ex18);
                    }
                    try {
                        final ObjectInput inputStream6 = remoteCall.getInputStream();
                        final int int9 = inputStream6.readInt();
                        final byte byte1 = inputStream6.readByte();
                    }
                    catch (final IOException ex19) {
                        throw new UnmarshalException("error unmarshalling arguments", ex19);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
            }
            case 8: {
                int int10 = 0;
                byte[] array2 = null;
                Label_1274: {
                    break Label_1274;
                    final int int9;
                    final byte byte1;
                    sshProxyClientAPIImpl.write(int9, byte1);
                    try {
                        remoteCall.getResultStream(true);
                        return;
                    }
                    catch (final IOException ex20) {
                        throw new MarshalException("error marshalling return", ex20);
                    }
                    try {
                        final ObjectInput inputStream7 = remoteCall.getInputStream();
                        int10 = inputStream7.readInt();
                        array2 = (byte[])inputStream7.readObject();
                    }
                    catch (final IOException ex21) {
                        throw new UnmarshalException("error unmarshalling arguments", ex21);
                    }
                    catch (final ClassNotFoundException ex22) {
                        throw new UnmarshalException("error unmarshalling arguments", ex22);
                    }
                    finally {
                        remoteCall.releaseInputStream();
                    }
                }
                sshProxyClientAPIImpl.write(int10, array2);
                try {
                    remoteCall.getResultStream(true);
                    return;
                }
                catch (final IOException ex23) {
                    throw new MarshalException("error marshalling return", ex23);
                }
                break;
            }
        }
        throw new UnmarshalException("invalid method number");
    }
    
    public Operation[] getOperations() {
        return SshProxyClientAPIImpl_Skel.operations.clone();
    }
}
