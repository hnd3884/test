package HTTPClient;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

class SimpleAuthPrompt implements AuthorizationPrompter
{
    public NVPair getUsernamePassword(final AuthorizationInfo challenge, final boolean forProxy) {
        if (challenge.getScheme().equalsIgnoreCase("SOCKS5")) {
            System.out.println("Enter username and password for SOCKS server on host " + challenge.getHost());
            System.out.println("Authentication Method: username/password");
        }
        else {
            System.out.println("Enter username and password for realm `" + challenge.getRealm() + "' on host " + challenge.getHost() + ":" + challenge.getPort());
            System.out.println("Authentication Scheme: " + challenge.getScheme());
        }
        final BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Username: ");
        System.out.flush();
        String user;
        try {
            user = inp.readLine();
        }
        catch (final IOException ex) {
            return null;
        }
        if (user == null || user.length() == 0) {
            return null;
        }
        echo(false);
        System.out.print("Password: ");
        System.out.flush();
        String pass;
        try {
            pass = inp.readLine();
        }
        catch (final IOException ex2) {
            return null;
        }
        System.out.println();
        echo(true);
        if (pass == null) {
            return null;
        }
        return new NVPair(user, pass);
    }
    
    private static void echo(final boolean on) {
        final String os = System.getProperty("os.name");
        String[] cmd = null;
        if (os.equalsIgnoreCase("Windows 95") || os.equalsIgnoreCase("Windows NT")) {
            cmd = new String[] { "echo", on ? "on" : "off" };
        }
        else if (!os.equalsIgnoreCase("Windows") && !os.equalsIgnoreCase("16-bit Windows") && !os.equalsIgnoreCase("OS/2") && !os.equalsIgnoreCase("Mac OS") && !os.equalsIgnoreCase("MacOS")) {
            if (os.equalsIgnoreCase("OpenVMS") || os.equalsIgnoreCase("VMS")) {
                cmd = new String[] { "SET TERMINAL " + (on ? "/ECHO" : "/NOECHO") };
            }
            else {
                cmd = new String[] { "/bin/sh", "-c", "stty " + (on ? "echo" : "-echo") + " < /dev/tty" };
            }
        }
        if (cmd != null) {
            try {
                Runtime.getRuntime().exec(cmd).waitFor();
            }
            catch (final Exception ex) {}
        }
    }
    
    static boolean canUseCLPrompt() {
        final String os = System.getProperty("os.name");
        return os.indexOf("Linux") >= 0 || os.indexOf("SunOS") >= 0 || os.indexOf("Solaris") >= 0 || os.indexOf("BSD") >= 0 || os.indexOf("AIX") >= 0 || os.indexOf("HP-UX") >= 0 || os.indexOf("IRIX") >= 0 || os.indexOf("OSF") >= 0 || os.indexOf("A/UX") >= 0 || os.indexOf("VMS") >= 0;
    }
}
