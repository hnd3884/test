package org.apache.taglibs.standard.lang.jstl.parser;

import java.util.Enumeration;
import java.io.Reader;
import java.io.InputStream;
import org.apache.taglibs.standard.lang.jstl.NullLiteral;
import org.apache.taglibs.standard.lang.jstl.FloatingPointLiteral;
import org.apache.taglibs.standard.lang.jstl.IntegerLiteral;
import org.apache.taglibs.standard.lang.jstl.StringLiteral;
import org.apache.taglibs.standard.lang.jstl.BooleanLiteral;
import org.apache.taglibs.standard.lang.jstl.Literal;
import org.apache.taglibs.standard.lang.jstl.ArraySuffix;
import org.apache.taglibs.standard.lang.jstl.PropertySuffix;
import org.apache.taglibs.standard.lang.jstl.FunctionInvocation;
import org.apache.taglibs.standard.lang.jstl.NamedValue;
import org.apache.taglibs.standard.lang.jstl.ValueSuffix;
import org.apache.taglibs.standard.lang.jstl.ComplexValue;
import org.apache.taglibs.standard.lang.jstl.UnaryOperator;
import org.apache.taglibs.standard.lang.jstl.UnaryOperatorExpression;
import org.apache.taglibs.standard.lang.jstl.EmptyOperator;
import org.apache.taglibs.standard.lang.jstl.UnaryMinusOperator;
import org.apache.taglibs.standard.lang.jstl.NotOperator;
import org.apache.taglibs.standard.lang.jstl.ModulusOperator;
import org.apache.taglibs.standard.lang.jstl.DivideOperator;
import org.apache.taglibs.standard.lang.jstl.MultiplyOperator;
import org.apache.taglibs.standard.lang.jstl.MinusOperator;
import org.apache.taglibs.standard.lang.jstl.PlusOperator;
import org.apache.taglibs.standard.lang.jstl.LessThanOrEqualsOperator;
import org.apache.taglibs.standard.lang.jstl.GreaterThanOrEqualsOperator;
import org.apache.taglibs.standard.lang.jstl.GreaterThanOperator;
import org.apache.taglibs.standard.lang.jstl.LessThanOperator;
import org.apache.taglibs.standard.lang.jstl.NotEqualsOperator;
import org.apache.taglibs.standard.lang.jstl.EqualsOperator;
import org.apache.taglibs.standard.lang.jstl.AndOperator;
import org.apache.taglibs.standard.lang.jstl.BinaryOperator;
import org.apache.taglibs.standard.lang.jstl.BinaryOperatorExpression;
import org.apache.taglibs.standard.lang.jstl.OrOperator;
import org.apache.taglibs.standard.lang.jstl.Expression;
import java.util.List;
import org.apache.taglibs.standard.lang.jstl.ExpressionString;
import java.util.ArrayList;
import java.util.Vector;

public class ELParser implements ELParserConstants
{
    public ELParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    public boolean lookingAhead;
    private boolean jj_semLA;
    private int jj_gen;
    private final int[] jj_la1;
    private final int[] jj_la1_0;
    private final int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private Vector jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    
    public static void main(final String[] args) throws ParseException {
        final ELParser parser = new ELParser(System.in);
        parser.ExpressionString();
    }
    
    public final Object ExpressionString() throws ParseException {
        Object ret = "";
        List elems = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                ret = this.AttrValueString();
                break;
            }
            case 2: {
                ret = this.AttrValueExpression();
                break;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 1:
                case 2: {
                    Object elem = null;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 1: {
                            elem = this.AttrValueString();
                            break;
                        }
                        case 2: {
                            elem = this.AttrValueExpression();
                            break;
                        }
                        default: {
                            this.jj_la1[2] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    if (elems == null) {
                        elems = new ArrayList();
                        elems.add(ret);
                    }
                    elems.add(elem);
                    continue;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    if (elems != null) {
                        ret = new ExpressionString(elems.toArray());
                    }
                    return ret;
                }
            }
        }
    }
    
    public final String AttrValueString() throws ParseException {
        final Token t = this.jj_consume_token(1);
        return t.image;
    }
    
    public final Expression AttrValueExpression() throws ParseException {
        this.jj_consume_token(2);
        final Expression exp = this.Expression();
        this.jj_consume_token(15);
        return exp;
    }
    
    public final Expression Expression() throws ParseException {
        final Expression ret = this.OrExpression();
        return ret;
    }
    
    public final Expression OrExpression() throws ParseException {
        List operators = null;
        List expressions = null;
        final Expression startExpression = this.AndExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 46:
                case 47: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 46: {
                            this.jj_consume_token(46);
                            break;
                        }
                        case 47: {
                            this.jj_consume_token(47);
                            break;
                        }
                        default: {
                            this.jj_la1[4] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    final BinaryOperator operator = OrOperator.SINGLETON;
                    final Expression expression = this.AndExpression();
                    if (operators == null) {
                        operators = new ArrayList();
                        expressions = new ArrayList();
                    }
                    operators.add(operator);
                    expressions.add(expression);
                    continue;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    if (operators != null) {
                        return new BinaryOperatorExpression(startExpression, operators, expressions);
                    }
                    return startExpression;
                }
            }
        }
    }
    
    public final Expression AndExpression() throws ParseException {
        List operators = null;
        List expressions = null;
        final Expression startExpression = this.EqualityExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 44:
                case 45: {
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 44: {
                            this.jj_consume_token(44);
                            break;
                        }
                        case 45: {
                            this.jj_consume_token(45);
                            break;
                        }
                        default: {
                            this.jj_la1[6] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    final BinaryOperator operator = AndOperator.SINGLETON;
                    final Expression expression = this.EqualityExpression();
                    if (operators == null) {
                        operators = new ArrayList();
                        expressions = new ArrayList();
                    }
                    operators.add(operator);
                    expressions.add(expression);
                    continue;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    if (operators != null) {
                        return new BinaryOperatorExpression(startExpression, operators, expressions);
                    }
                    return startExpression;
                }
            }
        }
    }
    
    public final Expression EqualityExpression() throws ParseException {
        List operators = null;
        List expressions = null;
        final Expression startExpression = this.RelationalExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 21:
                case 22:
                case 27:
                case 28: {
                    BinaryOperator operator = null;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 21:
                        case 22: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 21: {
                                    this.jj_consume_token(21);
                                    break;
                                }
                                case 22: {
                                    this.jj_consume_token(22);
                                    break;
                                }
                                default: {
                                    this.jj_la1[8] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = EqualsOperator.SINGLETON;
                            break;
                        }
                        case 27:
                        case 28: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 27: {
                                    this.jj_consume_token(27);
                                    break;
                                }
                                case 28: {
                                    this.jj_consume_token(28);
                                    break;
                                }
                                default: {
                                    this.jj_la1[9] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = NotEqualsOperator.SINGLETON;
                            break;
                        }
                        default: {
                            this.jj_la1[10] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    final Expression expression = this.RelationalExpression();
                    if (operators == null) {
                        operators = new ArrayList();
                        expressions = new ArrayList();
                    }
                    operators.add(operator);
                    expressions.add(expression);
                    continue;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    if (operators != null) {
                        return new BinaryOperatorExpression(startExpression, operators, expressions);
                    }
                    return startExpression;
                }
            }
        }
    }
    
    public final Expression RelationalExpression() throws ParseException {
        List operators = null;
        List expressions = null;
        final Expression startExpression = this.AddExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 17:
                case 18:
                case 19:
                case 20:
                case 23:
                case 24:
                case 25:
                case 26: {
                    BinaryOperator operator = null;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 19:
                        case 20: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 19: {
                                    this.jj_consume_token(19);
                                    break;
                                }
                                case 20: {
                                    this.jj_consume_token(20);
                                    break;
                                }
                                default: {
                                    this.jj_la1[12] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = LessThanOperator.SINGLETON;
                            break;
                        }
                        case 17:
                        case 18: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 17: {
                                    this.jj_consume_token(17);
                                    break;
                                }
                                case 18: {
                                    this.jj_consume_token(18);
                                    break;
                                }
                                default: {
                                    this.jj_la1[13] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = GreaterThanOperator.SINGLETON;
                            break;
                        }
                        case 25:
                        case 26: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 25: {
                                    this.jj_consume_token(25);
                                    break;
                                }
                                case 26: {
                                    this.jj_consume_token(26);
                                    break;
                                }
                                default: {
                                    this.jj_la1[14] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = GreaterThanOrEqualsOperator.SINGLETON;
                            break;
                        }
                        case 23:
                        case 24: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 23: {
                                    this.jj_consume_token(23);
                                    break;
                                }
                                case 24: {
                                    this.jj_consume_token(24);
                                    break;
                                }
                                default: {
                                    this.jj_la1[15] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = LessThanOrEqualsOperator.SINGLETON;
                            break;
                        }
                        default: {
                            this.jj_la1[16] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    final Expression expression = this.AddExpression();
                    if (operators == null) {
                        operators = new ArrayList();
                        expressions = new ArrayList();
                    }
                    operators.add(operator);
                    expressions.add(expression);
                    continue;
                }
                default: {
                    this.jj_la1[11] = this.jj_gen;
                    if (operators != null) {
                        return new BinaryOperatorExpression(startExpression, operators, expressions);
                    }
                    return startExpression;
                }
            }
        }
    }
    
    public final Expression AddExpression() throws ParseException {
        List operators = null;
        List expressions = null;
        final Expression startExpression = this.MultiplyExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 35:
                case 36: {
                    BinaryOperator operator = null;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 35: {
                            this.jj_consume_token(35);
                            operator = PlusOperator.SINGLETON;
                            break;
                        }
                        case 36: {
                            this.jj_consume_token(36);
                            operator = MinusOperator.SINGLETON;
                            break;
                        }
                        default: {
                            this.jj_la1[18] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    final Expression expression = this.MultiplyExpression();
                    if (operators == null) {
                        operators = new ArrayList();
                        expressions = new ArrayList();
                    }
                    operators.add(operator);
                    expressions.add(expression);
                    continue;
                }
                default: {
                    this.jj_la1[17] = this.jj_gen;
                    if (operators != null) {
                        return new BinaryOperatorExpression(startExpression, operators, expressions);
                    }
                    return startExpression;
                }
            }
        }
    }
    
    public final Expression MultiplyExpression() throws ParseException {
        List operators = null;
        List expressions = null;
        final Expression startExpression = this.UnaryExpression();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 37:
                case 38:
                case 39:
                case 40:
                case 41: {
                    BinaryOperator operator = null;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 37: {
                            this.jj_consume_token(37);
                            operator = MultiplyOperator.SINGLETON;
                            break;
                        }
                        case 38:
                        case 39: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 38: {
                                    this.jj_consume_token(38);
                                    break;
                                }
                                case 39: {
                                    this.jj_consume_token(39);
                                    break;
                                }
                                default: {
                                    this.jj_la1[20] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = DivideOperator.SINGLETON;
                            break;
                        }
                        case 40:
                        case 41: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 40: {
                                    this.jj_consume_token(40);
                                    break;
                                }
                                case 41: {
                                    this.jj_consume_token(41);
                                    break;
                                }
                                default: {
                                    this.jj_la1[21] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = ModulusOperator.SINGLETON;
                            break;
                        }
                        default: {
                            this.jj_la1[22] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    final Expression expression = this.UnaryExpression();
                    if (operators == null) {
                        operators = new ArrayList();
                        expressions = new ArrayList();
                    }
                    operators.add(operator);
                    expressions.add(expression);
                    continue;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                    if (operators != null) {
                        return new BinaryOperatorExpression(startExpression, operators, expressions);
                    }
                    return startExpression;
                }
            }
        }
    }
    
    public final Expression UnaryExpression() throws ParseException {
        UnaryOperator singleOperator = null;
        List operators = null;
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 36:
                case 42:
                case 43:
                case 48: {
                    UnaryOperator operator = null;
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 42:
                        case 43: {
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 42: {
                                    this.jj_consume_token(42);
                                    break;
                                }
                                case 43: {
                                    this.jj_consume_token(43);
                                    break;
                                }
                                default: {
                                    this.jj_la1[24] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            operator = NotOperator.SINGLETON;
                            break;
                        }
                        case 36: {
                            this.jj_consume_token(36);
                            operator = UnaryMinusOperator.SINGLETON;
                            break;
                        }
                        case 48: {
                            this.jj_consume_token(48);
                            operator = EmptyOperator.SINGLETON;
                            break;
                        }
                        default: {
                            this.jj_la1[25] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    if (singleOperator == null) {
                        singleOperator = operator;
                        continue;
                    }
                    if (operators == null) {
                        operators = new ArrayList();
                        operators.add(singleOperator);
                        operators.add(operator);
                        continue;
                    }
                    operators.add(operator);
                    continue;
                }
                default: {
                    this.jj_la1[23] = this.jj_gen;
                    final Expression expression = this.Value();
                    if (operators != null) {
                        return new UnaryOperatorExpression(null, operators, expression);
                    }
                    if (singleOperator != null) {
                        return new UnaryOperatorExpression(singleOperator, null, expression);
                    }
                    return expression;
                }
            }
        }
    }
    
    public final Expression Value() throws ParseException {
        List suffixes = null;
        final Expression prefix = this.ValuePrefix();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 16:
                case 33: {
                    final ValueSuffix suffix = this.ValueSuffix();
                    if (suffixes == null) {
                        suffixes = new ArrayList();
                    }
                    suffixes.add(suffix);
                    continue;
                }
                default: {
                    this.jj_la1[26] = this.jj_gen;
                    if (suffixes == null) {
                        return prefix;
                    }
                    return new ComplexValue(prefix, suffixes);
                }
            }
        }
    }
    
    public final Expression ValuePrefix() throws ParseException {
        Expression ret = null;
        Label_0253: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 7:
                case 8:
                case 10:
                case 12:
                case 13:
                case 14: {
                    ret = this.Literal();
                    break;
                }
                case 29: {
                    this.jj_consume_token(29);
                    ret = this.Expression();
                    this.jj_consume_token(30);
                    break;
                }
                default: {
                    this.jj_la1[27] = this.jj_gen;
                    if (this.jj_2_1(Integer.MAX_VALUE)) {
                        ret = this.FunctionInvocation();
                        break;
                    }
                    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                        case 49: {
                            ret = this.NamedValue();
                            break Label_0253;
                        }
                        default: {
                            this.jj_la1[28] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    break;
                }
            }
        }
        return ret;
    }
    
    public final NamedValue NamedValue() throws ParseException {
        final Token t = this.jj_consume_token(49);
        return new NamedValue(t.image);
    }
    
    public final FunctionInvocation FunctionInvocation() throws ParseException {
        final List argumentList = new ArrayList();
        final String qualifiedName = this.QualifiedName();
        this.jj_consume_token(29);
        Label_0327: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 7:
                case 8:
                case 10:
                case 12:
                case 13:
                case 14:
                case 29:
                case 36:
                case 42:
                case 43:
                case 48:
                case 49: {
                    Expression exp = this.Expression();
                    argumentList.add(exp);
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 31: {
                                this.jj_consume_token(31);
                                exp = this.Expression();
                                argumentList.add(exp);
                                continue;
                            }
                            default: {
                                this.jj_la1[29] = this.jj_gen;
                                break Label_0327;
                            }
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[30] = this.jj_gen;
                    break;
                }
            }
        }
        this.jj_consume_token(30);
        final String allowed = System.getProperty("javax.servlet.jsp.functions.allowed");
        if (allowed == null || !allowed.equalsIgnoreCase("true")) {
            throw new ParseException("EL functions are not supported.");
        }
        return new FunctionInvocation(qualifiedName, argumentList);
    }
    
    public final ValueSuffix ValueSuffix() throws ParseException {
        ValueSuffix suffix = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 16: {
                suffix = this.PropertySuffix();
                break;
            }
            case 33: {
                suffix = this.ArraySuffix();
                break;
            }
            default: {
                this.jj_la1[31] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return suffix;
    }
    
    public final PropertySuffix PropertySuffix() throws ParseException {
        this.jj_consume_token(16);
        final String property = this.Identifier();
        return new PropertySuffix(property);
    }
    
    public final ArraySuffix ArraySuffix() throws ParseException {
        this.jj_consume_token(33);
        final Expression index = this.Expression();
        this.jj_consume_token(34);
        return new ArraySuffix(index);
    }
    
    public final Literal Literal() throws ParseException {
        Literal ret = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 12:
            case 13: {
                ret = this.BooleanLiteral();
                break;
            }
            case 7: {
                ret = this.IntegerLiteral();
                break;
            }
            case 8: {
                ret = this.FloatingPointLiteral();
                break;
            }
            case 10: {
                ret = this.StringLiteral();
                break;
            }
            case 14: {
                ret = this.NullLiteral();
                break;
            }
            default: {
                this.jj_la1[32] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return ret;
    }
    
    public final BooleanLiteral BooleanLiteral() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 12: {
                this.jj_consume_token(12);
                return BooleanLiteral.TRUE;
            }
            case 13: {
                this.jj_consume_token(13);
                return BooleanLiteral.FALSE;
            }
            default: {
                this.jj_la1[33] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final StringLiteral StringLiteral() throws ParseException {
        final Token t = this.jj_consume_token(10);
        return StringLiteral.fromToken(t.image);
    }
    
    public final IntegerLiteral IntegerLiteral() throws ParseException {
        final Token t = this.jj_consume_token(7);
        return new IntegerLiteral(t.image);
    }
    
    public final FloatingPointLiteral FloatingPointLiteral() throws ParseException {
        final Token t = this.jj_consume_token(8);
        return new FloatingPointLiteral(t.image);
    }
    
    public final NullLiteral NullLiteral() throws ParseException {
        this.jj_consume_token(14);
        return NullLiteral.SINGLETON;
    }
    
    public final String Identifier() throws ParseException {
        final Token t = this.jj_consume_token(49);
        return t.image;
    }
    
    public final String QualifiedName() throws ParseException {
        String prefix = null;
        String localPart = null;
        if (this.jj_2_2(Integer.MAX_VALUE)) {
            prefix = this.Identifier();
            this.jj_consume_token(32);
        }
        localPart = this.Identifier();
        if (prefix == null) {
            return localPart;
        }
        return prefix + ":" + localPart;
    }
    
    private final boolean jj_2_1(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        final boolean retval = !this.jj_3_1();
        this.jj_save(0, xla);
        return retval;
    }
    
    private final boolean jj_2_2(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        final boolean retval = !this.jj_3_2();
        this.jj_save(1, xla);
        return retval;
    }
    
    private final boolean jj_3R_13() {
        return this.jj_3R_12() || ((this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos) && (this.jj_scan_token(32) || (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos && false)));
    }
    
    private final boolean jj_3_2() {
        return this.jj_3R_12() || ((this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos) && (this.jj_scan_token(32) || (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos && false)));
    }
    
    private final boolean jj_3_1() {
        return this.jj_3R_11() || ((this.jj_la != 0 || this.jj_scanpos != this.jj_lastpos) && (this.jj_scan_token(29) || (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos && false)));
    }
    
    private final boolean jj_3R_12() {
        return this.jj_scan_token(49) || (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos && false);
    }
    
    private final boolean jj_3R_11() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_13()) {
            this.jj_scanpos = xsp;
        }
        else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            return false;
        }
        return this.jj_3R_12() || (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos && false);
    }
    
    public ELParser(final InputStream stream) {
        this.lookingAhead = false;
        this.jj_la1 = new int[34];
        this.jj_la1_0 = new int[] { 6, 6, 6, 0, 0, 0, 0, 408944640, 6291456, 402653184, 408944640, 127795200, 1572864, 393216, 100663296, 25165824, 127795200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 65536, 536900992, 0, Integer.MIN_VALUE, 536900992, 65536, 30080, 12288 };
        this.jj_la1_1 = new int[] { 0, 0, 0, 49152, 49152, 12288, 12288, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 992, 192, 768, 992, 68624, 3072, 68624, 2, 0, 131072, 0, 199696, 2, 0, 0 };
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ELParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final InputStream stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public ELParser(final Reader stream) {
        this.lookingAhead = false;
        this.jj_la1 = new int[34];
        this.jj_la1_0 = new int[] { 6, 6, 6, 0, 0, 0, 0, 408944640, 6291456, 402653184, 408944640, 127795200, 1572864, 393216, 100663296, 25165824, 127795200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 65536, 536900992, 0, Integer.MIN_VALUE, 536900992, 65536, 30080, 12288 };
        this.jj_la1_1 = new int[] { 0, 0, 0, 49152, 49152, 12288, 12288, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 992, 192, 768, 992, 68624, 3072, 68624, 2, 0, 131072, 0, 199696, 2, 0, 0 };
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ELParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public ELParser(final ELParserTokenManager tm) {
        this.lookingAhead = false;
        this.jj_la1 = new int[34];
        this.jj_la1_0 = new int[] { 6, 6, 6, 0, 0, 0, 0, 408944640, 6291456, 402653184, 408944640, 127795200, 1572864, 393216, 100663296, 25165824, 127795200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 65536, 536900992, 0, Integer.MIN_VALUE, 536900992, 65536, 30080, 12288 };
        this.jj_la1_1 = new int[] { 0, 0, 0, 49152, 49152, 12288, 12288, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 992, 192, 768, 992, 68624, 3072, 68624, 2, 0, 131072, 0, 199696, 2, 0, 0 };
        this.jj_2_rtns = new JJCalls[2];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final ELParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 34; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    private final Token jj_consume_token(final int kind) throws ParseException {
        final Token oldToken;
        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    for (JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }
    
    private final boolean jj_scan_token(final int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                final Token jj_scanpos = this.jj_scanpos;
                final Token nextToken = this.token_source.getNextToken();
                jj_scanpos.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            }
            else {
                final Token next = this.jj_scanpos.next;
                this.jj_scanpos = next;
                this.jj_lastpos = next;
            }
        }
        else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok;
            for (tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
                ++i;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        return this.jj_scanpos.kind != kind;
    }
    
    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }
    
    public final Token getToken(final int index) {
        Token t = this.lookingAhead ? this.jj_scanpos : this.token;
        for (int i = 0; i < index; ++i) {
            if (t.next != null) {
                t = t.next;
            }
            else {
                final Token token = t;
                final Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                t = nextToken;
            }
        }
        return t;
    }
    
    private final int jj_ntk() {
        final Token next = this.token.next;
        this.jj_nt = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            return this.jj_ntk = nextToken.kind;
        }
        return this.jj_ntk = this.jj_nt.kind;
    }
    
    private void jj_add_error_token(final int kind, final int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        }
        else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
            boolean exists = false;
            final Enumeration enum_ = this.jj_expentries.elements();
            while (enum_.hasMoreElements()) {
                final int[] oldentry = enum_.nextElement();
                if (oldentry.length == this.jj_expentry.length) {
                    exists = true;
                    for (int j = 0; j < this.jj_expentry.length; ++j) {
                        if (oldentry[j] != this.jj_expentry[j]) {
                            exists = false;
                            break;
                        }
                    }
                    if (exists) {
                        break;
                    }
                    continue;
                }
            }
            if (!exists) {
                this.jj_expentries.addElement(this.jj_expentry);
            }
            if (pos != 0) {
                this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
        }
    }
    
    public final ParseException generateParseException() {
        this.jj_expentries.removeAllElements();
        final boolean[] la1tokens = new boolean[54];
        for (int i = 0; i < 54; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 34; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((this.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((this.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 54; ++i) {
            if (la1tokens[i]) {
                (this.jj_expentry = new int[1])[0] = i;
                this.jj_expentries.addElement(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        final int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int k = 0; k < this.jj_expentries.size(); ++k) {
            exptokseq[k] = this.jj_expentries.elementAt(k);
        }
        return new ParseException(this.token, exptokseq, ELParser.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    private final void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 2; ++i) {
            JJCalls p = this.jj_2_rtns[i];
            do {
                if (p.gen > this.jj_gen) {
                    this.jj_la = p.arg;
                    final Token first = p.first;
                    this.jj_scanpos = first;
                    this.jj_lastpos = first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
                            break;
                        }
                        case 1: {
                            this.jj_3_2();
                            break;
                        }
                    }
                }
                p = p.next;
            } while (p != null);
        }
        this.jj_rescan = false;
    }
    
    private final void jj_save(final int index, final int xla) {
        JJCalls p;
        for (p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
            if (p.next == null) {
                final JJCalls jjCalls = p;
                final JJCalls next = new JJCalls();
                jjCalls.next = next;
                p = next;
                break;
            }
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }
    
    static final class JJCalls
    {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
}
