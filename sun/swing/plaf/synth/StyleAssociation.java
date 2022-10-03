package sun.swing.plaf.synth;

import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.plaf.synth.SynthStyle;

public class StyleAssociation
{
    private SynthStyle _style;
    private Pattern _pattern;
    private Matcher _matcher;
    private int _id;
    
    public static StyleAssociation createStyleAssociation(final String s, final SynthStyle synthStyle) throws PatternSyntaxException {
        return createStyleAssociation(s, synthStyle, 0);
    }
    
    public static StyleAssociation createStyleAssociation(final String s, final SynthStyle synthStyle, final int n) throws PatternSyntaxException {
        return new StyleAssociation(s, synthStyle, n);
    }
    
    private StyleAssociation(final String s, final SynthStyle style, final int id) throws PatternSyntaxException {
        this._style = style;
        this._pattern = Pattern.compile(s);
        this._id = id;
    }
    
    public int getID() {
        return this._id;
    }
    
    public synchronized boolean matches(final CharSequence charSequence) {
        if (this._matcher == null) {
            this._matcher = this._pattern.matcher(charSequence);
        }
        else {
            this._matcher.reset(charSequence);
        }
        return this._matcher.matches();
    }
    
    public String getText() {
        return this._pattern.pattern();
    }
    
    public SynthStyle getStyle() {
        return this._style;
    }
}
