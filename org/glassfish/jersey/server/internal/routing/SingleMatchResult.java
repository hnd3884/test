package org.glassfish.jersey.server.internal.routing;

import java.util.regex.MatchResult;

final class SingleMatchResult implements MatchResult
{
    private final String path;
    
    public SingleMatchResult(final String path) {
        this.path = stripMatrixParams(path);
    }
    
    private static String stripMatrixParams(final String path) {
        int e = path.indexOf(59);
        if (e == -1) {
            return path;
        }
        int s = 0;
        final StringBuilder sb = new StringBuilder();
        do {
            sb.append(path, s, e);
            s = path.indexOf(47, e + 1);
            if (s == -1) {
                break;
            }
            e = path.indexOf(59, s);
        } while (e != -1);
        if (s != -1) {
            sb.append(path, s, path.length());
        }
        return sb.toString();
    }
    
    @Override
    public int start() {
        return 0;
    }
    
    @Override
    public int start(final int group) {
        if (group == 0) {
            return this.start();
        }
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public int end() {
        return this.path.length();
    }
    
    @Override
    public int end(final int group) {
        if (group == 0) {
            return this.end();
        }
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public String group() {
        return this.path;
    }
    
    @Override
    public String group(final int group) {
        if (group == 0) {
            return this.group();
        }
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public int groupCount() {
        return 0;
    }
}
