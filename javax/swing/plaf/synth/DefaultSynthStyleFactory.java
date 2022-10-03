package javax.swing.plaf.synth;

import java.awt.Font;
import javax.swing.plaf.FontUIResource;
import javax.swing.JComponent;
import java.util.regex.PatternSyntaxException;
import sun.swing.plaf.synth.DefaultSynthStyle;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import sun.swing.BakedArrayList;
import sun.swing.plaf.synth.StyleAssociation;
import java.util.List;

class DefaultSynthStyleFactory extends SynthStyleFactory
{
    public static final int NAME = 0;
    public static final int REGION = 1;
    private List<StyleAssociation> _styles;
    private BakedArrayList _tmpList;
    private Map<BakedArrayList, SynthStyle> _resolvedStyles;
    private SynthStyle _defaultStyle;
    
    DefaultSynthStyleFactory() {
        this._tmpList = new BakedArrayList(5);
        this._styles = new ArrayList<StyleAssociation>();
        this._resolvedStyles = new HashMap<BakedArrayList, SynthStyle>();
    }
    
    public synchronized void addStyle(final DefaultSynthStyle defaultSynthStyle, String s, final int n) throws PatternSyntaxException {
        if (s == null) {
            s = ".*";
        }
        if (n == 0) {
            this._styles.add(StyleAssociation.createStyleAssociation(s, defaultSynthStyle, n));
        }
        else if (n == 1) {
            this._styles.add(StyleAssociation.createStyleAssociation(s.toLowerCase(), defaultSynthStyle, n));
        }
    }
    
    @Override
    public synchronized SynthStyle getStyle(final JComponent component, final Region region) {
        final BakedArrayList tmpList = this._tmpList;
        tmpList.clear();
        this.getMatchingStyles(tmpList, component, region);
        if (tmpList.size() == 0) {
            return this.getDefaultStyle();
        }
        tmpList.cacheHashCode();
        SynthStyle synthStyle = this.getCachedStyle(tmpList);
        if (synthStyle == null) {
            synthStyle = this.mergeStyles(tmpList);
            if (synthStyle != null) {
                this.cacheStyle(tmpList, synthStyle);
            }
        }
        return synthStyle;
    }
    
    private SynthStyle getDefaultStyle() {
        if (this._defaultStyle == null) {
            this._defaultStyle = new DefaultSynthStyle();
            ((DefaultSynthStyle)this._defaultStyle).setFont(new FontUIResource("Dialog", 0, 12));
        }
        return this._defaultStyle;
    }
    
    private void getMatchingStyles(final List list, final JComponent component, final Region region) {
        final String lowerCaseName = region.getLowerCaseName();
        String name = component.getName();
        if (name == null) {
            name = "";
        }
        for (int i = this._styles.size() - 1; i >= 0; --i) {
            final StyleAssociation styleAssociation = this._styles.get(i);
            String s;
            if (styleAssociation.getID() == 0) {
                s = name;
            }
            else {
                s = lowerCaseName;
            }
            if (styleAssociation.matches(s) && list.indexOf(styleAssociation.getStyle()) == -1) {
                list.add(styleAssociation.getStyle());
            }
        }
    }
    
    private void cacheStyle(final List list, final SynthStyle synthStyle) {
        this._resolvedStyles.put(new BakedArrayList(list), synthStyle);
    }
    
    private SynthStyle getCachedStyle(final List list) {
        if (list.size() == 0) {
            return null;
        }
        return this._resolvedStyles.get(list);
    }
    
    private SynthStyle mergeStyles(final List list) {
        final int size = list.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return (SynthStyle)list.get(0).clone();
        }
        DefaultSynthStyle addTo = (DefaultSynthStyle)list.get(size - 1).clone();
        for (int i = size - 2; i >= 0; --i) {
            addTo = ((DefaultSynthStyle)list.get(i)).addTo(addTo);
        }
        return addTo;
    }
}
