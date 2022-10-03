package com.maverick.ssh;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class ShellEnvironment
{
    public static final int OS_WINDOWS = 1;
    public static final int OS_LINUX = 2;
    public static final int OS_SOLARIS = 3;
    public static final int OS_AIX = 4;
    public static final int OS_DARWIN = 5;
    public static final int OS_FREEBSD = 6;
    public static final int OS_OPENBSD = 7;
    public static final int OS_NETBSD = 8;
    public static final int OS_UNKNOWN = 99;
    static String g;
    Properties f;
    int c;
    String b;
    SshClient d;
    Shell e;
    
    public ShellEnvironment(final int c) {
        this.f = new Properties();
        this.c = 99;
        this.c = c;
        this.b();
    }
    
    public ShellEnvironment(String lowerCase) {
        this.f = new Properties();
        this.c = 99;
        lowerCase = lowerCase.toLowerCase();
        if (lowerCase.startsWith("sun") || lowerCase.startsWith("solaris")) {
            this.c = 3;
        }
        else if (lowerCase.startsWith("aix")) {
            this.c = 4;
        }
        else if (lowerCase.startsWith("windows")) {
            this.c = 1;
        }
        else if (lowerCase.startsWith("darwin")) {
            this.c = 5;
        }
        else if (lowerCase.startsWith("freebsd")) {
            this.c = 6;
        }
        else if (lowerCase.startsWith("openbsd")) {
            this.c = 7;
        }
        else if (lowerCase.startsWith("netbsd")) {
            this.c = 8;
        }
        else if (lowerCase.startsWith("linux")) {
            this.c = 2;
        }
        else {
            this.c = 99;
        }
        this.b();
    }
    
    ShellEnvironment(final SshClient d, final Shell e) throws SshException, ChannelOpenException {
        this.f = new Properties();
        this.c = 99;
        SshSession sshSession = null;
        this.d = d;
        this.e = e;
        try {
            sshSession = this.c();
            boolean b = false;
            if (sshSession.executeCommand("set")) {
                b = this.b(sshSession.getInputStream());
            }
            sshSession.close();
            if (!b) {
                sshSession = this.c();
                if (sshSession.executeCommand("env")) {
                    b = this.b(sshSession.getInputStream());
                }
                sshSession.close();
            }
            if (!b) {
                sshSession = this.c();
                if (sshSession.executeCommand("cmd.exe /C set")) {
                    this.b(sshSession.getInputStream());
                }
                sshSession.close();
            }
            if (this.f.containsKey("OS") && this.f.getProperty("OS").startsWith("Windows")) {
                if (this.f.containsKey("OSTYPE") && this.f.getProperty("OSTYPE").equals("cygwin")) {
                    this.c = 2;
                    this.b = this.b + " [" + this.f.getProperty("OSTYPE") + "]";
                }
                else {
                    this.c = 1;
                    this.b = "Windows";
                    sshSession = this.c();
                    if (sshSession.executeCommand("cmd.exe /C ver")) {
                        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sshSession.getInputStream()));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.indexOf("Windows") > -1) {
                                this.b = line;
                                break;
                            }
                        }
                        bufferedReader.close();
                    }
                }
                sshSession.close();
            }
            else {
                sshSession = this.c();
                if (sshSession.executeCommand("uname")) {
                    final BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(sshSession.getInputStream()));
                    this.b = bufferedReader2.readLine();
                    if (this.b.startsWith("Sun")) {
                        this.c = 3;
                    }
                    else if (this.b.startsWith("AIX")) {
                        this.c = 4;
                    }
                    else if (this.b.startsWith("Darwin")) {
                        this.c = 5;
                    }
                    else if (this.b.startsWith("FreeBSD")) {
                        this.c = 6;
                    }
                    else if (this.b.startsWith("OpenBSD")) {
                        this.c = 7;
                    }
                    else if (this.b.startsWith("NetBSD")) {
                        this.c = 8;
                    }
                    else if (this.b.startsWith("Linux")) {
                        this.c = 2;
                    }
                    else {
                        this.c = 99;
                    }
                    bufferedReader2.close();
                }
                else {
                    this.c = 99;
                }
                this.b();
                sshSession.close();
                if (this.c != 99 && this.f.containsKey("OSTYPE")) {
                    this.b = this.b + " [" + this.f.getProperty("OSTYPE") + "]";
                }
            }
        }
        catch (final IOException ex) {
            throw new SshException("Error whilst reading remote shell environment", ex);
        }
        finally {
            if (sshSession != null && !sshSession.isClosed()) {
                sshSession.close();
            }
        }
    }
    
    private void b() {
        if (this.c == 3) {
            this.b = "Solaris";
        }
        else if (this.c == 4) {
            this.b = "AIX";
        }
        else if (this.c == 1) {
            this.b = "Windows";
        }
        else if (this.c == 5) {
            this.b = "Darwin";
        }
        else if (this.c == 6) {
            this.b = "FreeBSD";
        }
        else if (this.c == 7) {
            this.b = "OpenBSD";
        }
        else if (this.c == 8) {
            this.b = "NetBSD";
        }
        else if (this.c == 2) {
            this.b = "Linux";
        }
        else {
            this.b = "Unknown";
        }
    }
    
    public Properties getEnvironmentVariables() {
        return this.f;
    }
    
    private SshSession c() throws SshException, ChannelOpenException {
        final SshSession openSessionChannel = this.d.openSessionChannel();
        if (this.e.isAllocatePseudoTerminal()) {
            final PseudoTerminalModes pseudoTerminalModes = new PseudoTerminalModes(this.d);
            pseudoTerminalModes.setTerminalMode(53, false);
            if (!openSessionChannel.requestPseudoTerminal("vt100", 80, 24, 0, 0, pseudoTerminalModes)) {
                throw new SshException("Server failed to allocate a pseudo terminal!", 15);
            }
        }
        openSessionChannel.addChannelEventListener(new ChannelAdapter() {
            public void channelClosed(final SshChannel sshChannel) {
            }
        });
        return openSessionChannel;
    }
    
    private boolean b(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        boolean b = false;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            final int index = line.indexOf(61);
            if (index > -1) {
                ((Hashtable<String, String>)this.f).put(line.substring(0, index), line.substring(index + 1));
                b = true;
            }
        }
        inputStream.close();
        bufferedReader.close();
        return b;
    }
    
    public int getOSType() {
        return this.c;
    }
    
    public String getOperatingSystem() {
        return this.b;
    }
    
    public String getEnvironmentVariable(final String s) {
        return this.f.getProperty(s);
    }
    
    public boolean hasEnvironmentVariable(final String s) {
        return this.f.containsKey(s);
    }
    
    public String getEOL() {
        switch (this.c) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8: {
                return "\r";
            }
            case 1: {
                return "\r\n";
            }
            default: {
                return ShellEnvironment.g;
            }
        }
    }
    
    static {
        ShellEnvironment.g = "\r";
    }
}
