package org.apache.lucene.search.postingshighlight;

public class DefaultPassageFormatter extends PassageFormatter
{
    protected final String preTag;
    protected final String postTag;
    protected final String ellipsis;
    protected final boolean escape;
    
    public DefaultPassageFormatter() {
        this("<b>", "</b>", "... ", false);
    }
    
    public DefaultPassageFormatter(final String preTag, final String postTag, final String ellipsis, final boolean escape) {
        if (preTag == null || postTag == null || ellipsis == null) {
            throw new NullPointerException();
        }
        this.preTag = preTag;
        this.postTag = postTag;
        this.ellipsis = ellipsis;
        this.escape = escape;
    }
    
    @Override
    public String format(final Passage[] passages, final String content) {
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (final Passage passage : passages) {
            if (passage.startOffset > pos && pos > 0) {
                sb.append(this.ellipsis);
            }
            pos = passage.startOffset;
            for (int i = 0; i < passage.numMatches; ++i) {
                final int start = passage.matchStarts[i];
                final int end = passage.matchEnds[i];
                if (start > pos) {
                    this.append(sb, content, pos, start);
                }
                if (end > pos) {
                    sb.append(this.preTag);
                    this.append(sb, content, Math.max(pos, start), end);
                    sb.append(this.postTag);
                    pos = end;
                }
            }
            this.append(sb, content, pos, Math.max(pos, passage.endOffset));
            pos = passage.endOffset;
        }
        return sb.toString();
    }
    
    protected void append(final StringBuilder dest, final String content, final int start, final int end) {
        if (this.escape) {
            for (int i = start; i < end; ++i) {
                final char ch = content.charAt(i);
                switch (ch) {
                    case '&': {
                        dest.append("&amp;");
                        break;
                    }
                    case '<': {
                        dest.append("&lt;");
                        break;
                    }
                    case '>': {
                        dest.append("&gt;");
                        break;
                    }
                    case '\"': {
                        dest.append("&quot;");
                        break;
                    }
                    case '\'': {
                        dest.append("&#x27;");
                        break;
                    }
                    case '/': {
                        dest.append("&#x2F;");
                        break;
                    }
                    default: {
                        if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                            dest.append(ch);
                            break;
                        }
                        if (ch < '\u00ff') {
                            dest.append("&#");
                            dest.append((int)ch);
                            dest.append(";");
                            break;
                        }
                        dest.append(ch);
                        break;
                    }
                }
            }
        }
        else {
            dest.append(content, start, end);
        }
    }
}
