package sun.net.www.protocol.http.logging;

import java.util.regex.Matcher;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.logging.SimpleFormatter;

public class HttpLogFormatter extends SimpleFormatter
{
    private static volatile Pattern pattern;
    private static volatile Pattern cpattern;
    
    public HttpLogFormatter() {
        if (HttpLogFormatter.pattern == null) {
            HttpLogFormatter.pattern = Pattern.compile("\\{[^\\}]*\\}");
            HttpLogFormatter.cpattern = Pattern.compile("[^,\\] ]{2,}");
        }
    }
    
    @Override
    public String format(final LogRecord logRecord) {
        final String sourceClassName = logRecord.getSourceClassName();
        if (sourceClassName == null || (!sourceClassName.startsWith("sun.net.www.protocol.http") && !sourceClassName.startsWith("sun.net.www.http"))) {
            return super.format(logRecord);
        }
        final String message = logRecord.getMessage();
        final StringBuilder sb = new StringBuilder("HTTP: ");
        if (message.startsWith("sun.net.www.MessageHeader@")) {
            final Matcher matcher = HttpLogFormatter.pattern.matcher(message);
            while (matcher.find()) {
                String s = message.substring(matcher.start() + 1, matcher.end() - 1);
                if (s.startsWith("null: ")) {
                    s = s.substring(6);
                }
                if (s.endsWith(": null")) {
                    s = s.substring(0, s.length() - 6);
                }
                sb.append("\t").append(s).append("\n");
            }
        }
        else if (message.startsWith("Cookies retrieved: {")) {
            String s2 = message.substring(20);
            sb.append("Cookies from handler:\n");
            while (s2.length() >= 7) {
                if (s2.startsWith("Cookie=[")) {
                    String s3 = s2.substring(8);
                    final int index = s3.indexOf("Cookie2=[");
                    if (index > 0) {
                        s3 = s3.substring(0, index - 1);
                        s2 = s3.substring(index);
                    }
                    else {
                        s2 = "";
                    }
                    if (s3.length() < 4) {
                        continue;
                    }
                    final Matcher matcher2 = HttpLogFormatter.cpattern.matcher(s3);
                    while (matcher2.find()) {
                        final int start = matcher2.start();
                        final int end = matcher2.end();
                        if (start >= 0) {
                            sb.append("\t").append(s3.substring(start + 1, (end > 0) ? (end - 1) : (s3.length() - 1))).append("\n");
                        }
                    }
                }
                if (s2.startsWith("Cookie2=[")) {
                    String s4 = s2.substring(9);
                    final int index2 = s4.indexOf("Cookie=[");
                    if (index2 > 0) {
                        s4 = s4.substring(0, index2 - 1);
                        s2 = s4.substring(index2);
                    }
                    else {
                        s2 = "";
                    }
                    final Matcher matcher3 = HttpLogFormatter.cpattern.matcher(s4);
                    while (matcher3.find()) {
                        final int start2 = matcher3.start();
                        final int end2 = matcher3.end();
                        if (start2 >= 0) {
                            sb.append("\t").append(s4.substring(start2 + 1, (end2 > 0) ? (end2 - 1) : (s4.length() - 1))).append("\n");
                        }
                    }
                }
            }
        }
        else {
            sb.append(message).append("\n");
        }
        return sb.toString();
    }
    
    static {
        HttpLogFormatter.pattern = null;
        HttpLogFormatter.cpattern = null;
    }
}
