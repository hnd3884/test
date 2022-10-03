package sun.net;

public class URLCanonicalizer
{
    public String canonicalize(String string) {
        String s = string;
        if (string.startsWith("ftp.")) {
            s = "ftp://" + string;
        }
        else if (string.startsWith("gopher.")) {
            s = "gopher://" + string;
        }
        else if (string.startsWith("/")) {
            s = "file:" + string;
        }
        else if (!this.hasProtocolName(string)) {
            if (this.isSimpleHostName(string)) {
                string = "www." + string + ".com";
            }
            s = "http://" + string;
        }
        return s;
    }
    
    public boolean hasProtocolName(final String s) {
        final int index = s.indexOf(58);
        if (index <= 0) {
            return false;
        }
        for (int i = 0; i < index; ++i) {
            final char char1 = s.charAt(i);
            if ((char1 < 'A' || char1 > 'Z') && (char1 < 'a' || char1 > 'z') && char1 != '-') {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isSimpleHostName(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if ((char1 < 'A' || char1 > 'Z') && (char1 < 'a' || char1 > 'z') && (char1 < '0' || char1 > '9') && char1 != '-') {
                return false;
            }
        }
        return true;
    }
}
