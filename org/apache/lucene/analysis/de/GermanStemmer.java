package org.apache.lucene.analysis.de;

import java.util.Locale;

public class GermanStemmer
{
    private StringBuilder sb;
    private int substCount;
    private static final Locale locale;
    
    public GermanStemmer() {
        this.sb = new StringBuilder();
        this.substCount = 0;
    }
    
    protected String stem(String term) {
        term = term.toLowerCase(GermanStemmer.locale);
        if (!this.isStemmable(term)) {
            return term;
        }
        this.sb.delete(0, this.sb.length());
        this.sb.insert(0, term);
        this.substitute(this.sb);
        this.strip(this.sb);
        this.optimize(this.sb);
        this.resubstitute(this.sb);
        this.removeParticleDenotion(this.sb);
        return this.sb.toString();
    }
    
    private boolean isStemmable(final String term) {
        for (int c = 0; c < term.length(); ++c) {
            if (!Character.isLetter(term.charAt(c))) {
                return false;
            }
        }
        return true;
    }
    
    private void strip(final StringBuilder buffer) {
        boolean doMore = true;
        while (doMore && buffer.length() > 3) {
            if (buffer.length() + this.substCount > 5 && buffer.substring(buffer.length() - 2, buffer.length()).equals("nd")) {
                buffer.delete(buffer.length() - 2, buffer.length());
            }
            else if (buffer.length() + this.substCount > 4 && buffer.substring(buffer.length() - 2, buffer.length()).equals("em")) {
                buffer.delete(buffer.length() - 2, buffer.length());
            }
            else if (buffer.length() + this.substCount > 4 && buffer.substring(buffer.length() - 2, buffer.length()).equals("er")) {
                buffer.delete(buffer.length() - 2, buffer.length());
            }
            else if (buffer.charAt(buffer.length() - 1) == 'e') {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            else if (buffer.charAt(buffer.length() - 1) == 's') {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            else if (buffer.charAt(buffer.length() - 1) == 'n') {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            else if (buffer.charAt(buffer.length() - 1) == 't') {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            else {
                doMore = false;
            }
        }
    }
    
    private void optimize(final StringBuilder buffer) {
        if (buffer.length() > 5 && buffer.substring(buffer.length() - 5, buffer.length()).equals("erin*")) {
            buffer.deleteCharAt(buffer.length() - 1);
            this.strip(buffer);
        }
        if (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == 'z') {
            buffer.setCharAt(buffer.length() - 1, 'x');
        }
    }
    
    private void removeParticleDenotion(final StringBuilder buffer) {
        if (buffer.length() > 4) {
            for (int c = 0; c < buffer.length() - 3; ++c) {
                if (buffer.substring(c, c + 4).equals("gege")) {
                    buffer.delete(c, c + 2);
                    return;
                }
            }
        }
    }
    
    private void substitute(final StringBuilder buffer) {
        this.substCount = 0;
        for (int c = 0; c < buffer.length(); ++c) {
            if (c > 0 && buffer.charAt(c) == buffer.charAt(c - 1)) {
                buffer.setCharAt(c, '*');
            }
            else if (buffer.charAt(c) == '\u00e4') {
                buffer.setCharAt(c, 'a');
            }
            else if (buffer.charAt(c) == '\u00f6') {
                buffer.setCharAt(c, 'o');
            }
            else if (buffer.charAt(c) == '\u00fc') {
                buffer.setCharAt(c, 'u');
            }
            else if (buffer.charAt(c) == '\u00df') {
                buffer.setCharAt(c, 's');
                buffer.insert(c + 1, 's');
                ++this.substCount;
            }
            if (c < buffer.length() - 1) {
                if (c < buffer.length() - 2 && buffer.charAt(c) == 's' && buffer.charAt(c + 1) == 'c' && buffer.charAt(c + 2) == 'h') {
                    buffer.setCharAt(c, '$');
                    buffer.delete(c + 1, c + 3);
                    this.substCount += 2;
                }
                else if (buffer.charAt(c) == 'c' && buffer.charAt(c + 1) == 'h') {
                    buffer.setCharAt(c, '§');
                    buffer.deleteCharAt(c + 1);
                    ++this.substCount;
                }
                else if (buffer.charAt(c) == 'e' && buffer.charAt(c + 1) == 'i') {
                    buffer.setCharAt(c, '%');
                    buffer.deleteCharAt(c + 1);
                    ++this.substCount;
                }
                else if (buffer.charAt(c) == 'i' && buffer.charAt(c + 1) == 'e') {
                    buffer.setCharAt(c, '&');
                    buffer.deleteCharAt(c + 1);
                    ++this.substCount;
                }
                else if (buffer.charAt(c) == 'i' && buffer.charAt(c + 1) == 'g') {
                    buffer.setCharAt(c, '#');
                    buffer.deleteCharAt(c + 1);
                    ++this.substCount;
                }
                else if (buffer.charAt(c) == 's' && buffer.charAt(c + 1) == 't') {
                    buffer.setCharAt(c, '!');
                    buffer.deleteCharAt(c + 1);
                    ++this.substCount;
                }
            }
        }
    }
    
    private void resubstitute(final StringBuilder buffer) {
        for (int c = 0; c < buffer.length(); ++c) {
            if (buffer.charAt(c) == '*') {
                final char x = buffer.charAt(c - 1);
                buffer.setCharAt(c, x);
            }
            else if (buffer.charAt(c) == '$') {
                buffer.setCharAt(c, 's');
                buffer.insert(c + 1, new char[] { 'c', 'h' }, 0, 2);
            }
            else if (buffer.charAt(c) == '§') {
                buffer.setCharAt(c, 'c');
                buffer.insert(c + 1, 'h');
            }
            else if (buffer.charAt(c) == '%') {
                buffer.setCharAt(c, 'e');
                buffer.insert(c + 1, 'i');
            }
            else if (buffer.charAt(c) == '&') {
                buffer.setCharAt(c, 'i');
                buffer.insert(c + 1, 'e');
            }
            else if (buffer.charAt(c) == '#') {
                buffer.setCharAt(c, 'i');
                buffer.insert(c + 1, 'g');
            }
            else if (buffer.charAt(c) == '!') {
                buffer.setCharAt(c, 's');
                buffer.insert(c + 1, 't');
            }
        }
    }
    
    static {
        locale = new Locale("de", "DE");
    }
}
