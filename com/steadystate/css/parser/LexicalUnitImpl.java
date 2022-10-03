package com.steadystate.css.parser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.LexicalUnit;

public class LexicalUnitImpl extends LocatableImpl implements LexicalUnit, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -7260032046960116891L;
    private short lexicalUnitType_;
    private LexicalUnit nextLexicalUnit_;
    private LexicalUnit previousLexicalUnit_;
    private float floatValue_;
    private String dimension_;
    private String functionName_;
    private LexicalUnit parameters_;
    private String stringValue_;
    private String sourceStringValue_;
    private transient String toString_;
    
    public void setLexicalUnitType(final short type) {
        this.lexicalUnitType_ = type;
        this.toString_ = null;
    }
    
    public void setNextLexicalUnit(final LexicalUnit next) {
        this.nextLexicalUnit_ = next;
    }
    
    public void setPreviousLexicalUnit(final LexicalUnit prev) {
        this.previousLexicalUnit_ = prev;
    }
    
    public void setFloatValue(final float floatVal) {
        this.floatValue_ = floatVal;
        this.toString_ = null;
    }
    
    public String getDimension() {
        return this.dimension_;
    }
    
    public void setDimension(final String dimension) {
        this.dimension_ = dimension;
        this.toString_ = null;
    }
    
    public void setFunctionName(final String function) {
        this.functionName_ = function;
        this.toString_ = null;
    }
    
    public void setParameters(final LexicalUnit params) {
        this.parameters_ = params;
        this.toString_ = null;
    }
    
    public void setStringValue(final String stringVal) {
        this.stringValue_ = stringVal;
        this.toString_ = null;
    }
    
    protected LexicalUnitImpl(final LexicalUnit previous, final short type) {
        this();
        this.lexicalUnitType_ = type;
        this.previousLexicalUnit_ = previous;
        if (this.previousLexicalUnit_ != null) {
            ((LexicalUnitImpl)this.previousLexicalUnit_).nextLexicalUnit_ = (LexicalUnit)this;
        }
    }
    
    protected LexicalUnitImpl(final LexicalUnit previous, final int value) {
        this(previous, (short)13);
        this.floatValue_ = (float)value;
    }
    
    protected LexicalUnitImpl(final LexicalUnit previous, final short type, final float value) {
        this(previous, type);
        this.floatValue_ = value;
    }
    
    protected LexicalUnitImpl(final LexicalUnit previous, final short type, final String dimension, final float value) {
        this(previous, type);
        this.dimension_ = dimension;
        this.floatValue_ = value;
    }
    
    protected LexicalUnitImpl(final LexicalUnit previous, final short type, final String value) {
        this(previous, type);
        this.stringValue_ = value;
    }
    
    protected LexicalUnitImpl(final LexicalUnit previous, final short type, final String name, final LexicalUnit params) {
        this(previous, type);
        this.functionName_ = name;
        this.parameters_ = params;
    }
    
    protected LexicalUnitImpl(final LexicalUnit previous, final short type, final String name, final String stringValue) {
        this(previous, type);
        this.functionName_ = name;
        this.stringValue_ = stringValue;
    }
    
    protected LexicalUnitImpl() {
    }
    
    public short getLexicalUnitType() {
        return this.lexicalUnitType_;
    }
    
    public LexicalUnit getNextLexicalUnit() {
        return this.nextLexicalUnit_;
    }
    
    public LexicalUnit getPreviousLexicalUnit() {
        return this.previousLexicalUnit_;
    }
    
    public int getIntegerValue() {
        return (int)this.floatValue_;
    }
    
    public float getFloatValue() {
        return this.floatValue_;
    }
    
    public String getDimensionUnitText() {
        switch (this.lexicalUnitType_) {
            case 15: {
                return "em";
            }
            case 16: {
                return "ex";
            }
            case 17: {
                return "px";
            }
            case 18: {
                return "in";
            }
            case 19: {
                return "cm";
            }
            case 20: {
                return "mm";
            }
            case 21: {
                return "pt";
            }
            case 22: {
                return "pc";
            }
            case 23: {
                return "%";
            }
            case 28: {
                return "deg";
            }
            case 29: {
                return "grad";
            }
            case 30: {
                return "rad";
            }
            case 31: {
                return "ms";
            }
            case 32: {
                return "s";
            }
            case 33: {
                return "Hz";
            }
            case 34: {
                return "kHz";
            }
            case 42: {
                return this.dimension_;
            }
            default: {
                return "";
            }
        }
    }
    
    public String getFunctionName() {
        return this.functionName_;
    }
    
    public LexicalUnit getParameters() {
        return this.parameters_;
    }
    
    public String getStringValue() {
        return this.stringValue_;
    }
    
    public String getSourceStringValue() {
        return this.sourceStringValue_;
    }
    
    public LexicalUnit getSubValues() {
        return this.parameters_;
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        if (null != this.toString_ && (format == null || !format.useSourceStringValues())) {
            return this.toString_;
        }
        final StringBuilder sb = new StringBuilder();
        switch (this.lexicalUnitType_) {
            case 0: {
                sb.append(",");
                break;
            }
            case 1: {
                sb.append("+");
                break;
            }
            case 2: {
                sb.append("-");
                break;
            }
            case 3: {
                sb.append("*");
                break;
            }
            case 4: {
                sb.append("/");
                break;
            }
            case 5: {
                sb.append("%");
                break;
            }
            case 6: {
                sb.append("^");
                break;
            }
            case 7: {
                sb.append("<");
                break;
            }
            case 8: {
                sb.append(">");
                break;
            }
            case 9: {
                sb.append("<=");
                break;
            }
            case 10: {
                sb.append(">=");
                break;
            }
            case 11: {
                sb.append("~");
                break;
            }
            case 12: {
                sb.append("inherit");
                break;
            }
            case 13: {
                sb.append(String.valueOf(this.getIntegerValue()));
                break;
            }
            case 14: {
                sb.append(this.getTrimedFloatValue());
                break;
            }
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 42: {
                sb.append(this.getTrimedFloatValue());
                final String dimUnitText = this.getDimensionUnitText();
                if (null != dimUnitText) {
                    sb.append(dimUnitText);
                    break;
                }
                break;
            }
            case 24: {
                sb.append("url(").append(this.getStringValue()).append(")");
                break;
            }
            case 25: {
                sb.append("counter(");
                this.appendParams(sb);
                sb.append(")");
                break;
            }
            case 26: {
                sb.append("counters(");
                this.appendParams(sb);
                sb.append(")");
                break;
            }
            case 27: {
                sb.append("rgb(");
                this.appendParams(sb);
                sb.append(")");
                break;
            }
            case 35: {
                sb.append(this.getStringValue());
                break;
            }
            case 36: {
                sb.append("\"");
                String value = this.getStringValue();
                if (null != format && format.useSourceStringValues() && this.sourceStringValue_ != null && value != this.sourceStringValue_) {
                    value = this.sourceStringValue_;
                }
                else {
                    value = value.replace("\n", "\\A ").replace("\r", "\\D ");
                }
                sb.append(value);
                sb.append("\"");
                break;
            }
            case 37: {
                sb.append("attr(").append(this.getStringValue()).append(")");
                break;
            }
            case 38: {
                sb.append("rect(");
                this.appendParams(sb);
                sb.append(")");
                break;
            }
            case 39: {
                final String range = this.getStringValue();
                if (null != range) {
                    sb.append(range);
                    break;
                }
                break;
            }
            case 40: {
                final String subExpression = this.getStringValue();
                if (null != subExpression) {
                    sb.append(subExpression);
                    break;
                }
                break;
            }
            case 41: {
                final String functName = this.getFunctionName();
                if (null != functName) {
                    sb.append(functName);
                }
                sb.append('(');
                this.appendParams(sb);
                sb.append(")");
                break;
            }
        }
        return this.toString_ = sb.toString();
    }
    
    public String toString() {
        return this.getCssText(null);
    }
    
    public String toDebugString() {
        final StringBuilder sb = new StringBuilder();
        switch (this.lexicalUnitType_) {
            case 0: {
                sb.append("SAC_OPERATOR_COMMA");
                break;
            }
            case 1: {
                sb.append("SAC_OPERATOR_PLUS");
                break;
            }
            case 2: {
                sb.append("SAC_OPERATOR_MINUS");
                break;
            }
            case 3: {
                sb.append("SAC_OPERATOR_MULTIPLY");
                break;
            }
            case 4: {
                sb.append("SAC_OPERATOR_SLASH");
                break;
            }
            case 5: {
                sb.append("SAC_OPERATOR_MOD");
                break;
            }
            case 6: {
                sb.append("SAC_OPERATOR_EXP");
                break;
            }
            case 7: {
                sb.append("SAC_OPERATOR_LT");
                break;
            }
            case 8: {
                sb.append("SAC_OPERATOR_GT");
                break;
            }
            case 9: {
                sb.append("SAC_OPERATOR_LE");
                break;
            }
            case 10: {
                sb.append("SAC_OPERATOR_GE");
                break;
            }
            case 11: {
                sb.append("SAC_OPERATOR_TILDE");
                break;
            }
            case 12: {
                sb.append("SAC_INHERIT");
                break;
            }
            case 13: {
                sb.append("SAC_INTEGER(").append(String.valueOf(this.getIntegerValue())).append(")");
                break;
            }
            case 14: {
                sb.append("SAC_REAL(").append(this.getTrimedFloatValue()).append(")");
                break;
            }
            case 15: {
                sb.append("SAC_EM(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 16: {
                sb.append("SAC_EX(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 17: {
                sb.append("SAC_PIXEL(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 18: {
                sb.append("SAC_INCH(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 19: {
                sb.append("SAC_CENTIMETER(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 20: {
                sb.append("SAC_MILLIMETER(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 21: {
                sb.append("SAC_POINT(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 22: {
                sb.append("SAC_PICA(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 23: {
                sb.append("SAC_PERCENTAGE(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 28: {
                sb.append("SAC_DEGREE(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 29: {
                sb.append("SAC_GRADIAN(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 30: {
                sb.append("SAC_RADIAN(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 31: {
                sb.append("SAC_MILLISECOND(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 32: {
                sb.append("SAC_SECOND(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 33: {
                sb.append("SAC_HERTZ(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 34: {
                sb.append("SAC_KILOHERTZ(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 42: {
                sb.append("SAC_DIMENSION(").append(this.getTrimedFloatValue()).append(this.getDimensionUnitText()).append(")");
                break;
            }
            case 24: {
                sb.append("SAC_URI(url(").append(this.getStringValue()).append("))");
                break;
            }
            case 25: {
                sb.append("SAC_COUNTER_FUNCTION(counter(");
                this.appendParams(sb);
                sb.append("))");
                break;
            }
            case 26: {
                sb.append("SAC_COUNTERS_FUNCTION(counters(");
                this.appendParams(sb);
                sb.append("))");
                break;
            }
            case 27: {
                sb.append("SAC_RGBCOLOR(rgb(");
                this.appendParams(sb);
                sb.append("))");
                break;
            }
            case 35: {
                sb.append("SAC_IDENT(").append(this.getStringValue()).append(")");
                break;
            }
            case 36: {
                sb.append("SAC_STRING_VALUE(\"").append(this.getStringValue()).append("\")");
                break;
            }
            case 37: {
                sb.append("SAC_ATTR(attr(").append(this.getStringValue()).append("))");
                break;
            }
            case 38: {
                sb.append("SAC_RECT_FUNCTION(rect(");
                this.appendParams(sb);
                sb.append("))");
                break;
            }
            case 39: {
                sb.append("SAC_UNICODERANGE(").append(this.getStringValue()).append(")");
                break;
            }
            case 40: {
                sb.append("SAC_SUB_EXPRESSION(").append(this.getStringValue()).append(")");
                break;
            }
            case 41: {
                sb.append("SAC_FUNCTION(").append(this.getFunctionName()).append("(");
                for (LexicalUnit l = this.parameters_; l != null; l = l.getNextLexicalUnit()) {
                    sb.append(l.toString());
                }
                sb.append("))");
                break;
            }
        }
        return sb.toString();
    }
    
    private void appendParams(final StringBuilder sb) {
        LexicalUnit l = this.parameters_;
        if (l != null) {
            sb.append(l.toString());
            for (l = l.getNextLexicalUnit(); l != null; l = l.getNextLexicalUnit()) {
                if (l.getLexicalUnitType() != 0) {
                    sb.append(" ");
                }
                sb.append(l.toString());
            }
        }
    }
    
    private String getTrimedFloatValue() {
        final float f = this.getFloatValue();
        final int i = (int)f;
        if (f - i == 0.0f) {
            return Integer.toString((int)f);
        }
        final DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(4);
        return decimalFormat.format(f);
    }
    
    public static LexicalUnit createNumber(final LexicalUnit prev, final int i) {
        return (LexicalUnit)new LexicalUnitImpl(prev, i);
    }
    
    public static LexicalUnit createNumber(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)14, f);
    }
    
    public static LexicalUnit createPercentage(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)23, f);
    }
    
    public static LexicalUnit createPixel(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)17, f);
    }
    
    public static LexicalUnit createCentimeter(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)19, f);
    }
    
    public static LexicalUnit createMillimeter(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)20, f);
    }
    
    public static LexicalUnit createInch(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)18, f);
    }
    
    public static LexicalUnit createPoint(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)21, f);
    }
    
    public static LexicalUnit createPica(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)22, f);
    }
    
    public static LexicalUnit createEm(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)15, f);
    }
    
    public static LexicalUnit createEx(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)16, f);
    }
    
    public static LexicalUnit createDegree(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)28, f);
    }
    
    public static LexicalUnit createRadian(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)30, f);
    }
    
    public static LexicalUnit createGradian(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)29, f);
    }
    
    public static LexicalUnit createMillisecond(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)31, f);
    }
    
    public static LexicalUnit createSecond(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)32, f);
    }
    
    public static LexicalUnit createHertz(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)33, f);
    }
    
    public static LexicalUnit createDimension(final LexicalUnit prev, final float f, final String dim) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)42, dim, f);
    }
    
    public static LexicalUnit createKiloHertz(final LexicalUnit prev, final float f) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)34, f);
    }
    
    public static LexicalUnit createCounter(final LexicalUnit prev, final LexicalUnit params) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)25, "counter", params);
    }
    
    public static LexicalUnit createCounters(final LexicalUnit prev, final LexicalUnit params) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)26, "counters", params);
    }
    
    public static LexicalUnit createAttr(final LexicalUnit prev, final String value) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)37, "name", value);
    }
    
    public static LexicalUnit createRect(final LexicalUnit prev, final LexicalUnit params) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)38, "rect", params);
    }
    
    public static LexicalUnit createRgbColor(final LexicalUnit prev, final LexicalUnit params) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)27, "rgb", params);
    }
    
    public static LexicalUnit createFunction(final LexicalUnit prev, final String name, final LexicalUnit params) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)41, name, params);
    }
    
    public static LexicalUnit createString(final LexicalUnit prev, final String value) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)36, value);
    }
    
    public static LexicalUnit createString(final LexicalUnit prev, final String value, final String sourceStringValue) {
        final LexicalUnitImpl unit = new LexicalUnitImpl(prev, (short)36, value);
        unit.sourceStringValue_ = sourceStringValue;
        return (LexicalUnit)unit;
    }
    
    public static LexicalUnit createIdent(final LexicalUnit prev, final String value) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)35, value);
    }
    
    public static LexicalUnit createURI(final LexicalUnit prev, final String value) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)24, value);
    }
    
    public static LexicalUnit createComma(final LexicalUnit prev) {
        return (LexicalUnit)new LexicalUnitImpl(prev, (short)0);
    }
}
