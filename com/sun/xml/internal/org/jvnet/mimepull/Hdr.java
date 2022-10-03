package com.sun.xml.internal.org.jvnet.mimepull;

class Hdr implements Header
{
    String name;
    String line;
    
    Hdr(final String l) {
        final int i = l.indexOf(58);
        if (i < 0) {
            this.name = l.trim();
        }
        else {
            this.name = l.substring(0, i).trim();
        }
        this.line = l;
    }
    
    Hdr(final String n, final String v) {
        this.name = n;
        this.line = n + ": " + v;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getValue() {
        final int i = this.line.indexOf(58);
        if (i < 0) {
            return this.line;
        }
        int j;
        if (this.name.equalsIgnoreCase("Content-Description")) {
            for (j = i + 1; j < this.line.length(); ++j) {
                final char c = this.line.charAt(j);
                if (c != '\t' && c != '\r' && c != '\n') {
                    break;
                }
            }
        }
        else {
            for (j = i + 1; j < this.line.length(); ++j) {
                final char c = this.line.charAt(j);
                if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                    break;
                }
            }
        }
        return this.line.substring(j);
    }
}
