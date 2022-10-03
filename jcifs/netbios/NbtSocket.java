package jcifs.netbios;

import java.io.OutputStream;
import java.io.InputStream;
import jcifs.Config;
import java.io.IOException;
import java.net.InetAddress;
import jcifs.util.LogStream;
import java.net.Socket;

public class NbtSocket extends Socket
{
    private static final int SSN_SRVC_PORT = 139;
    private static final int BUFFER_SIZE = 512;
    private static final int DEFAULT_SO_TIMEOUT = 5000;
    private static LogStream log;
    private NbtAddress address;
    private Name calledName;
    private int soTimeout;
    
    public NbtSocket() {
    }
    
    public NbtSocket(final NbtAddress address, final int port) throws IOException {
        this(address, port, null, 0);
    }
    
    public NbtSocket(final NbtAddress address, final int port, final InetAddress localAddr, final int localPort) throws IOException {
        this(address, null, port, localAddr, localPort);
    }
    
    public NbtSocket(final NbtAddress address, final String calledName, final int port, final InetAddress localAddr, final int localPort) throws IOException {
        super(address.getInetAddress(), (port == 0) ? 139 : port, localAddr, localPort);
        this.address = address;
        if (calledName == null) {
            this.calledName = address.hostName;
        }
        else {
            this.calledName = new Name(calledName, 32, null);
        }
        this.soTimeout = Config.getInt("jcifs.netbios.soTimeout", 5000);
        this.connect();
    }
    
    public NbtAddress getNbtAddress() {
        return this.address;
    }
    
    public InputStream getInputStream() throws IOException {
        return new SocketInputStream(super.getInputStream());
    }
    
    public OutputStream getOutputStream() throws IOException {
        return new SocketOutputStream(super.getOutputStream());
    }
    
    public int getPort() {
        return super.getPort();
    }
    
    public InetAddress getLocalAddress() {
        return super.getLocalAddress();
    }
    
    public int getLocalPort() {
        return super.getLocalPort();
    }
    
    public String toString() {
        return "NbtSocket[addr=" + this.address + ",port=" + super.getPort() + ",localport=" + super.getLocalPort() + "]";
    }
    
    private void connect() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: newarray        B
        //     5: astore_1        /* buffer */
        //     6: aload_0         /* this */
        //     7: invokespecial   java/net/Socket.getInputStream:()Ljava/io/InputStream;
        //    10: astore_3        /* in */
        //    11: aload_0         /* this */
        //    12: invokespecial   java/net/Socket.getOutputStream:()Ljava/io/OutputStream;
        //    15: astore          out
        //    17: new             Ljcifs/netbios/SessionRequestPacket;
        //    20: dup            
        //    21: aload_0         /* this */
        //    22: getfield        jcifs/netbios/NbtSocket.calledName:Ljcifs/netbios/Name;
        //    25: getstatic       jcifs/netbios/NbtAddress.localhost:Ljcifs/netbios/NbtAddress;
        //    28: getfield        jcifs/netbios/NbtAddress.hostName:Ljcifs/netbios/Name;
        //    31: invokespecial   jcifs/netbios/SessionRequestPacket.<init>:(Ljcifs/netbios/Name;Ljcifs/netbios/Name;)V
        //    34: astore          ssp0
        //    36: aload           out
        //    38: aload_1         /* buffer */
        //    39: iconst_0       
        //    40: aload           ssp0
        //    42: aload_1         /* buffer */
        //    43: iconst_0       
        //    44: invokevirtual   jcifs/netbios/SessionServicePacket.writeWireFormat:([BI)I
        //    47: invokevirtual   java/io/OutputStream.write:([BII)V
        //    50: aload_0         /* this */
        //    51: aload_0         /* this */
        //    52: getfield        jcifs/netbios/NbtSocket.soTimeout:I
        //    55: invokevirtual   jcifs/netbios/NbtSocket.setSoTimeout:(I)V
        //    58: aload           ssp0
        //    60: pop            
        //    61: aload_3         /* in */
        //    62: aload_1         /* buffer */
        //    63: iconst_0       
        //    64: invokestatic    jcifs/netbios/SessionServicePacket.readPacketType:(Ljava/io/InputStream;[BI)I
        //    67: istore_2        /* type */
        //    68: goto            80
        //    71: astore          ioe
        //    73: aload_0         /* this */
        //    74: invokevirtual   jcifs/netbios/NbtSocket.close:()V
        //    77: aload           ioe
        //    79: athrow         
        //    80: iload_2         /* type */
        //    81: lookupswitch {
        //               -1: 181
        //              130: 116
        //              131: 156
        //          default: 191
        //        }
        //   116: getstatic       jcifs/netbios/NbtSocket.log:Ljcifs/util/LogStream;
        //   119: pop            
        //   120: getstatic       jcifs/util/LogStream.level:I
        //   123: iconst_2       
        //   124: if_icmple       155
        //   127: getstatic       jcifs/netbios/NbtSocket.log:Ljcifs/util/LogStream;
        //   130: new             Ljava/lang/StringBuffer;
        //   133: dup            
        //   134: invokespecial   java/lang/StringBuffer.<init>:()V
        //   137: ldc             "session established ok with "
        //   139: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   142: aload_0         /* this */
        //   143: getfield        jcifs/netbios/NbtSocket.address:Ljcifs/netbios/NbtAddress;
        //   146: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/Object;)Ljava/lang/StringBuffer;
        //   149: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //   152: invokevirtual   jcifs/util/LogStream.println:(Ljava/lang/String;)V
        //   155: return         
        //   156: aload_3        
        //   157: invokevirtual   java/io/InputStream.read:()I
        //   160: sipush          255
        //   163: iand           
        //   164: istore          errorCode
        //   166: aload_0         /* this */
        //   167: invokevirtual   jcifs/netbios/NbtSocket.close:()V
        //   170: new             Ljcifs/netbios/NbtException;
        //   173: dup            
        //   174: iconst_2       
        //   175: iload           errorCode
        //   177: invokespecial   jcifs/netbios/NbtException.<init>:(II)V
        //   180: athrow         
        //   181: new             Ljcifs/netbios/NbtException;
        //   184: dup            
        //   185: iconst_2       
        //   186: iconst_m1      
        //   187: invokespecial   jcifs/netbios/NbtException.<init>:(II)V
        //   190: athrow         
        //   191: aload_0         /* this */
        //   192: invokevirtual   jcifs/netbios/NbtSocket.close:()V
        //   195: new             Ljcifs/netbios/NbtException;
        //   198: dup            
        //   199: iconst_2       
        //   200: iconst_0       
        //   201: invokespecial   jcifs/netbios/NbtException.<init>:(II)V
        //   204: athrow         
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  6      68     71     80     Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2945)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2501)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void close() throws IOException {
        final LogStream log = NbtSocket.log;
        if (LogStream.level > 3) {
            NbtSocket.log.println("close: " + this);
        }
        super.close();
    }
    
    static {
        NbtSocket.log = LogStream.getInstance();
    }
}
