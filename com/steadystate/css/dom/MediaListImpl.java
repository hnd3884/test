package com.steadystate.css.dom;

import com.steadystate.css.util.LangUtils;
import com.steadystate.css.parser.SACMediaListImpl;
import java.io.IOException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.dom.DOMException;
import org.w3c.css.sac.ErrorHandler;
import com.steadystate.css.util.ThrowCssExceptionErrorHandler;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import java.util.Iterator;
import com.steadystate.css.format.CSSFormat;
import java.util.ArrayList;
import org.w3c.css.sac.Locator;
import com.steadystate.css.userdata.UserDataConstants;
import com.steadystate.css.parser.Locatable;
import org.w3c.css.sac.SACMediaList;
import com.steadystate.css.parser.media.MediaQuery;
import java.util.List;
import org.w3c.dom.stylesheets.MediaList;

public class MediaListImpl extends CSSOMObjectImpl implements MediaList
{
    private static final long serialVersionUID = 6662784733573034870L;
    private List<MediaQuery> mediaQueries_;
    
    public MediaListImpl(final SACMediaList mediaList) {
        this();
        this.setMediaList(mediaList);
        if (mediaList instanceof Locatable) {
            final Locator locator = ((Locatable)mediaList).getLocator();
            if (locator != null) {
                this.setUserData(UserDataConstants.KEY_LOCATOR, locator);
            }
        }
    }
    
    public MediaListImpl() {
        this.mediaQueries_ = new ArrayList<MediaQuery>(10);
    }
    
    public String getMediaText() {
        return this.getMediaText(null);
    }
    
    public String getMediaText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder("");
        boolean isNotFirst = false;
        for (final MediaQuery mediaQuery : this.mediaQueries_) {
            if (isNotFirst) {
                sb.append(", ");
            }
            else {
                isNotFirst = true;
            }
            sb.append(mediaQuery.getCssText(format));
        }
        return sb.toString();
    }
    
    public void setMediaText(final String mediaText) throws DOMException {
        final InputSource source = new InputSource((Reader)new StringReader(mediaText));
        try {
            final CSSOMParser parser = new CSSOMParser();
            parser.setErrorHandler((ErrorHandler)ThrowCssExceptionErrorHandler.INSTANCE);
            final SACMediaList sml = parser.parseMedia(source);
            this.setMediaList(sml);
        }
        catch (final CSSParseException e) {
            throw new DOMException((short)12, e.getLocalizedMessage());
        }
        catch (final IOException e2) {
            throw new DOMException((short)8, e2.getLocalizedMessage());
        }
    }
    
    public int getLength() {
        return this.mediaQueries_.size();
    }
    
    public String item(final int index) {
        final MediaQuery mq = this.mediaQuery(index);
        if (null == mq) {
            return null;
        }
        return mq.getMedia();
    }
    
    public MediaQuery mediaQuery(final int index) {
        if (index < 0 || index >= this.mediaQueries_.size()) {
            return null;
        }
        return this.mediaQueries_.get(index);
    }
    
    public void deleteMedium(final String oldMedium) throws DOMException {
        for (final MediaQuery mediaQuery : this.mediaQueries_) {
            final String str = mediaQuery.getMedia();
            if (str.equalsIgnoreCase(oldMedium)) {
                this.mediaQueries_.remove(mediaQuery);
                return;
            }
        }
        throw new DOMExceptionImpl((short)8, 18);
    }
    
    public void appendMedium(final String newMedium) throws DOMException {
        this.mediaQueries_.add(new MediaQuery(newMedium));
    }
    
    @Override
    public String toString() {
        return this.getMediaText(null);
    }
    
    public void setMedia(final List<String> media) {
        this.mediaQueries_.clear();
        for (final String medium : media) {
            this.mediaQueries_.add(new MediaQuery(medium));
        }
    }
    
    private void setMediaList(final SACMediaList mediaList) {
        if (mediaList instanceof SACMediaListImpl) {
            final SACMediaListImpl impl = (SACMediaListImpl)mediaList;
            for (int i = 0; i < mediaList.getLength(); ++i) {
                this.mediaQueries_.add(impl.mediaQuery(i));
            }
            return;
        }
        for (int j = 0; j < mediaList.getLength(); ++j) {
            this.mediaQueries_.add(new MediaQuery(mediaList.item(j)));
        }
    }
    
    private boolean equalsMedia(final MediaList ml) {
        if (ml == null || this.getLength() != ml.getLength()) {
            return false;
        }
        for (int i = 0; i < this.getLength(); ++i) {
            final String m1 = this.item(i);
            final String m2 = ml.item(i);
            if (!LangUtils.equals(m1, m2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MediaList)) {
            return false;
        }
        final MediaList ml = (MediaList)obj;
        return super.equals(obj) && this.equalsMedia(ml);
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.mediaQueries_);
        return hash;
    }
}
