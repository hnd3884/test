package org.apache.commons.net.imap;

import org.apache.commons.net.MalformedServerReplyException;
import java.util.regex.Matcher;
import java.io.IOException;
import java.util.regex.Pattern;

public final class IMAPReply
{
    public static final int OK = 0;
    public static final int NO = 1;
    public static final int BAD = 2;
    public static final int CONT = 3;
    public static final int PARTIAL = 3;
    private static final String IMAP_OK = "OK";
    private static final String IMAP_NO = "NO";
    private static final String IMAP_BAD = "BAD";
    private static final String IMAP_UNTAGGED_PREFIX = "* ";
    private static final String IMAP_CONTINUATION_PREFIX = "+";
    private static final String TAGGED_RESPONSE = "^\\w+ (\\S+).*";
    private static final Pattern TAGGED_PATTERN;
    private static final String UNTAGGED_RESPONSE = "^\\* (\\S+).*";
    private static final Pattern UNTAGGED_PATTERN;
    private static final Pattern LITERAL_PATTERN;
    
    private IMAPReply() {
    }
    
    public static boolean isUntagged(final String line) {
        return line.startsWith("* ");
    }
    
    public static boolean isContinuation(final String line) {
        return line.startsWith("+");
    }
    
    public static int getReplyCode(final String line) throws IOException {
        return getReplyCode(line, IMAPReply.TAGGED_PATTERN);
    }
    
    public static int literalCount(final String line) {
        final Matcher m = IMAPReply.LITERAL_PATTERN.matcher(line);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }
    
    public static int getUntaggedReplyCode(final String line) throws IOException {
        return getReplyCode(line, IMAPReply.UNTAGGED_PATTERN);
    }
    
    private static int getReplyCode(final String line, final Pattern pattern) throws IOException {
        if (isContinuation(line)) {
            return 3;
        }
        final Matcher m = pattern.matcher(line);
        if (m.matches()) {
            final String code = m.group(1);
            if (code.equals("OK")) {
                return 0;
            }
            if (code.equals("BAD")) {
                return 2;
            }
            if (code.equals("NO")) {
                return 1;
            }
        }
        throw new MalformedServerReplyException("Received unexpected IMAP protocol response from server: '" + line + "'.");
    }
    
    public static boolean isSuccess(final int replyCode) {
        return replyCode == 0;
    }
    
    public static boolean isContinuation(final int replyCode) {
        return replyCode == 3;
    }
    
    static {
        TAGGED_PATTERN = Pattern.compile("^\\w+ (\\S+).*");
        UNTAGGED_PATTERN = Pattern.compile("^\\* (\\S+).*");
        LITERAL_PATTERN = Pattern.compile("\\{(\\d+)\\}$");
    }
}
