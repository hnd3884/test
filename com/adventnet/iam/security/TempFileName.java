package com.adventnet.iam.security;

public class TempFileName
{
    private static final int FINE_NAME_PATH_ATTRUBUTE_LIMIT = 100;
    private final String path;
    private final String requestId;
    private final long threadId;
    
    public TempFileName(final String path, final String requestId, final long threadId) {
        this.path = path;
        this.requestId = requestId;
        this.threadId = threadId;
    }
    
    public TempFileName(final String path) {
        this(path, SecurityUtil.getCurrentRequestID(), Thread.currentThread().getId());
    }
    
    public TempFileName(final ActionRule actionRule) {
        this(actionRule.getTempFileNamePathAttribute());
    }
    
    public TempFileName() {
        this((String)null);
    }
    
    public static String trimPath(final String path) {
        if (path == null) {
            return "";
        }
        final String alphanumericPath = path.replaceAll("\\W+", "_");
        final String truncatedPath = (alphanumericPath.length() > 100) ? alphanumericPath.substring(0, 100) : alphanumericPath;
        final String underScoreTrimmedPath = trimUnderscore(truncatedPath);
        return underScoreTrimmedPath;
    }
    
    private static String trimUnderscore(final String s) {
        final char[] value = s.toCharArray();
        int len;
        int st;
        char[] val;
        for (len = value.length, st = 0, val = value; st < len && val[st] == '_'; ++st) {}
        while (st < len && val[len - 1] == '_') {
            --len;
        }
        return (st > 0 || len < value.length) ? s.substring(st, len) : s;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getRequestId() {
        return this.requestId;
    }
    
    public long threadId() {
        return this.threadId;
    }
    
    public String getTempFileName() {
        final StringBuilder sb = new StringBuilder();
        if (this.path != null && !this.path.isEmpty()) {
            sb.append(this.path).append('_');
        }
        if (this.requestId != null) {
            sb.append(this.requestId).append('_').append(this.threadId).append('_');
        }
        return sb.append(System.currentTimeMillis()).toString();
    }
}
