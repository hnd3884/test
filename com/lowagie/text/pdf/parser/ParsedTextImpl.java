package com.lowagie.text.pdf.parser;

public abstract class ParsedTextImpl implements TextAssemblyBuffer
{
    private final String text;
    private float _ascent;
    private float _descent;
    private Vector _startPoint;
    private Vector _endPoint;
    float _spaceWidth;
    Vector _baseline;
    
    ParsedTextImpl(final String text, final Vector startPoint, final Vector endPoint, final Vector baseline, final float ascent, final float descent, final float spaceWidth) {
        this._baseline = baseline;
        this.text = text;
        this._startPoint = startPoint;
        this._endPoint = endPoint;
        this._ascent = ascent;
        this._descent = descent;
        this._spaceWidth = spaceWidth;
    }
    
    @Override
    public String getText() {
        return this.text;
    }
    
    public float getSingleSpaceWidth() {
        return this._spaceWidth;
    }
    
    public float getAscent() {
        return this._ascent;
    }
    
    public float getDescent() {
        return this._descent;
    }
    
    public float getWidth() {
        return this.getEndPoint().subtract(this.getStartPoint()).length();
    }
    
    public Vector getStartPoint() {
        return this._startPoint;
    }
    
    public Vector getEndPoint() {
        return this._endPoint;
    }
    
    public Vector getBaseline() {
        return this._baseline;
    }
    
    public abstract boolean shouldNotSplit();
    
    public abstract boolean breakBefore();
}
