package com.steadystate.css.parser;

import java.util.ArrayList;
import com.steadystate.css.parser.media.MediaQuery;
import java.util.List;
import org.w3c.css.sac.SACMediaList;

public class SACMediaListImpl extends LocatableImpl implements SACMediaList
{
    private final List<MediaQuery> mediaQueries_;
    
    public SACMediaListImpl() {
        this.mediaQueries_ = new ArrayList<MediaQuery>();
    }
    
    public int getLength() {
        return this.mediaQueries_.size();
    }
    
    public String item(final int index) {
        return this.mediaQuery(index).getMedia();
    }
    
    public MediaQuery mediaQuery(final int index) {
        return this.mediaQueries_.get(index);
    }
    
    public void add(final String s) {
        this.add(new MediaQuery(s));
    }
    
    public void add(final MediaQuery mediaQuery) {
        this.mediaQueries_.add(mediaQuery);
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int len = this.getLength(), i = 0; i < len; ++i) {
            sb.append(this.item(i));
            if (i < len - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
