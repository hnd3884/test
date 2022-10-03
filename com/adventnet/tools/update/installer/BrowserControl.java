package com.adventnet.tools.update.installer;

public class BrowserControl
{
    private static final String WIN_ID = "Windows";
    private static final String WIN_PATH = "rundll32";
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
    private static final String UNIX_PATH = "netscape";
    private static final String UNIX_FLAG = "-remote openURL";
    
    public static boolean displayURL(String url) {
        final boolean windows = isWindowsPlatform();
        String cmd = null;
        try {
            if (windows) {
                if (url.startsWith("file")) {
                    final String curl = url = "file://" + url.substring(6);
                }
                cmd = "rundll32 url.dll,FileProtocolHandler " + url;
                final Process p = Runtime.getRuntime().exec(cmd);
            }
            else {
                cmd = "netscape -remote openURL(" + url + ")";
                Process p = Runtime.getRuntime().exec(cmd);
                int exitCode = p.waitFor();
                if (exitCode != 0) {
                    cmd = "netscape " + url;
                    p = Runtime.getRuntime().exec(cmd);
                    exitCode = p.waitFor();
                    if (exitCode != 0) {
                        return false;
                    }
                }
            }
        }
        catch (final Exception x) {
            return false;
        }
        return true;
    }
    
    public static boolean isWindowsPlatform() {
        final String os = System.getProperty("os.name");
        return os != null && os.startsWith("Windows");
    }
    
    public static void main(final String[] args) {
        if (args.length >= 1) {
            displayURL(args[0]);
        }
        else {
            displayURL("file://d://swing-1.1/doc/api/index.html");
        }
    }
}
