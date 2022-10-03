package com.adventnet.ems.utils;

import java.io.IOException;

public class Browser
{
    public void displayURL(String s) {
        final String s2 = "rundll32";
        final String s3 = "url.dll,FileProtocolHandler";
        final String s4 = "netscape";
        final String s5 = "-remote openURL";
        final boolean windowsPlatform = this.isWindowsPlatform();
        String s6 = null;
        try {
            if (windowsPlatform) {
                if (s.startsWith("file")) {
                    s = s.replace('/', '\\');
                    s = "file://" + s.substring(7);
                }
                s6 = s2 + " " + s3 + " " + s;
                Runtime.getRuntime().exec(s6);
            }
            else {
                s6 = s4 + " " + s5 + "(" + "file://" + System.getProperty("user.dir") + "/" + s + ")";
                final Process exec = Runtime.getRuntime().exec(s6);
                try {
                    if (exec.waitFor() != 0) {
                        s6 = s4 + " " + s;
                        final Thread thread = new Thread(new BrowserInvoker(Runtime.getRuntime().exec(s6), s));
                        thread.setPriority(3);
                        thread.start();
                    }
                }
                catch (final InterruptedException ex) {
                    System.err.println("Error bringing up browser, cmd='" + s6 + "'");
                    System.err.println("Caught: " + ex);
                }
            }
        }
        catch (final IOException ex2) {
            System.err.println("Could not invoke browser, command=" + s6);
            System.err.println("Caught: " + ex2);
            final Thread thread2 = new Thread(new BrowserInvoker(null, s));
            thread2.setPriority(3);
            thread2.start();
        }
    }
    
    private boolean isWindowsPlatform() {
        final String property = System.getProperty("os.name");
        return property != null && property.startsWith("Windows");
    }
    
    class BrowserInvoker implements Runnable
    {
        Process p;
        String url;
        
        public BrowserInvoker(final Process p3, final String url) {
            this.p = p3;
            this.url = url;
        }
        
        public void run() {
            if (this.p != null) {
                try {
                    this.p.waitFor();
                }
                catch (final Exception ex) {
                    System.err.println(ex);
                }
            }
        }
    }
}
