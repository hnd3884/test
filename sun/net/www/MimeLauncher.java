package sun.net.www;

import java.util.StringTokenizer;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.URL;
import java.io.InputStream;
import java.net.URLConnection;

class MimeLauncher extends Thread
{
    URLConnection uc;
    MimeEntry m;
    String genericTempFileTemplate;
    InputStream is;
    String execPath;
    
    MimeLauncher(final MimeEntry m, final URLConnection uc, final InputStream is, final String genericTempFileTemplate, final String s) throws ApplicationLaunchException {
        super(s);
        this.m = m;
        this.uc = uc;
        this.is = is;
        this.genericTempFileTemplate = genericTempFileTemplate;
        final String launchString = this.m.getLaunchString();
        if (!this.findExecutablePath(launchString)) {
            final int index = launchString.indexOf(32);
            String substring;
            if (index != -1) {
                substring = launchString.substring(0, index);
            }
            else {
                substring = launchString;
            }
            throw new ApplicationLaunchException(substring);
        }
    }
    
    protected String getTempFileName(final URL url, final String s) {
        final int lastIndex = s.lastIndexOf("%s");
        String s2 = s.substring(0, lastIndex);
        String substring = "";
        if (lastIndex < s.length() - 2) {
            substring = s.substring(lastIndex + 2);
        }
        long n;
        int index;
        for (n = System.currentTimeMillis() / 1000L; (index = s2.indexOf("%s")) >= 0; s2 = s2.substring(0, index) + n + s2.substring(index + 2)) {}
        final String file = url.getFile();
        String substring2 = "";
        final int lastIndex2 = file.lastIndexOf(46);
        if (lastIndex2 >= 0 && lastIndex2 > file.lastIndexOf(47)) {
            substring2 = file.substring(lastIndex2);
        }
        return s2 + ("HJ" + url.hashCode()) + n + substring2 + substring;
    }
    
    @Override
    public void run() {
        try {
            String s = this.m.getTempFileTemplate();
            if (s == null) {
                s = this.genericTempFileTemplate;
            }
            final String tempFileName = this.getTempFileName(this.uc.getURL(), s);
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(tempFileName);
                final byte[] array = new byte[2048];
                try {
                    int read;
                    while ((read = this.is.read(array)) >= 0) {
                        fileOutputStream.write(array, 0, read);
                    }
                }
                catch (final IOException ex) {}
                finally {
                    fileOutputStream.close();
                    this.is.close();
                }
            }
            catch (final IOException ex2) {}
            String s2;
            int index;
            for (s2 = this.execPath; (index = s2.indexOf("%t")) >= 0; s2 = s2.substring(0, index) + this.uc.getContentType() + s2.substring(index + 2)) {}
            boolean b = false;
            int index2;
            while ((index2 = s2.indexOf("%s")) >= 0) {
                s2 = s2.substring(0, index2) + tempFileName + s2.substring(index2 + 2);
                b = true;
            }
            if (!b) {
                s2 = s2 + " <" + tempFileName;
            }
            Runtime.getRuntime().exec(s2);
        }
        catch (final IOException ex3) {}
    }
    
    private boolean findExecutablePath(final String execPath) {
        if (execPath == null || execPath.length() == 0) {
            return false;
        }
        final int index = execPath.indexOf(32);
        String substring;
        if (index != -1) {
            substring = execPath.substring(0, index);
        }
        else {
            substring = execPath;
        }
        if (new File(substring).isFile()) {
            this.execPath = execPath;
            return true;
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("exec.path"));
        if (s == null) {
            return false;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "|");
        while (stringTokenizer.hasMoreElements()) {
            final String s2 = (String)stringTokenizer.nextElement();
            if (new File(s2 + File.separator + substring).isFile()) {
                this.execPath = s2 + File.separator + execPath;
                return true;
            }
        }
        return false;
    }
}
