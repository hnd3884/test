package org.apache.poi.sl.draw.geom;

import java.util.Iterator;
import java.util.HashMap;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.regex.Pattern;

public class Context
{
    private static final Pattern DOUBLE_PATTERN;
    private final Map<String, Double> _ctx;
    private final IAdjustableShape _props;
    private final Rectangle2D _anchor;
    
    public Context(final CustomGeometry geom, final Rectangle2D anchor, final IAdjustableShape props) {
        this._ctx = new HashMap<String, Double>();
        this._props = props;
        this._anchor = anchor;
        for (final Guide gd : geom.adjusts) {
            this.evaluate(gd);
        }
        for (final Guide gd : geom.guides) {
            this.evaluate(gd);
        }
    }
    
    Rectangle2D getShapeAnchor() {
        return this._anchor;
    }
    
    Guide getAdjustValue(final String name) {
        return this._props.getAdjustValue(name);
    }
    
    public double getValue(final String key) {
        if (Context.DOUBLE_PATTERN.matcher(key).matches()) {
            return Double.parseDouble(key);
        }
        return this._ctx.containsKey(key) ? this._ctx.get(key) : this.evaluate(BuiltInGuide.valueOf("_" + key));
    }
    
    public double evaluate(final Formula fmla) {
        final double result = fmla.evaluate(this);
        if (fmla instanceof Guide) {
            final String key = ((Guide)fmla).getName();
            if (key != null) {
                this._ctx.put(key, result);
            }
        }
        return result;
    }
    
    static {
        DOUBLE_PATTERN = Pattern.compile("[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.(\\p{Digit}+)([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
    }
}
