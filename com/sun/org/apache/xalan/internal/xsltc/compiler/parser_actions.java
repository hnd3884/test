package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Vector;
import com.sun.java_cup.internal.runtime.Symbol;
import java.util.Stack;
import com.sun.java_cup.internal.runtime.lr_parser;

class parser_actions
{
    private final XPathParser parser;
    
    parser_actions(final XPathParser parser) {
        this.parser = parser;
    }
    
    public final Symbol parser_do_action(final int parser_act_num, final lr_parser parser_parser, final Stack<Symbol> parser_stack, final int parser_top) throws Exception {
        switch (parser_act_num) {
            case 0: {
                final SyntaxTreeNode start_val = (SyntaxTreeNode)parser_stack.get(parser_top - 1).value;
                final Symbol parser_result = new Symbol(0, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, start_val);
                parser_parser.done_parsing();
                return parser_result;
            }
            case 1: {
                final Pattern pattern = (Pattern)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(1, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, pattern);
                return parser_result;
            }
            case 2: {
                final Expression expr = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(1, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, expr);
                return parser_result;
            }
            case 3: {
                final Pattern lpp = (Pattern)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(28, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, lpp);
                return parser_result;
            }
            case 4: {
                final Pattern lpp = (Pattern)parser_stack.get(parser_top - 2).value;
                final Pattern p = (Pattern)parser_stack.get(parser_top - 0).value;
                final Pattern result = new AlternativePattern(lpp, p);
                final Symbol parser_result = new Symbol(28, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result);
                return parser_result;
            }
            case 5: {
                final Pattern result2 = new AbsolutePathPattern(null);
                final Symbol parser_result = new Symbol(29, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result2);
                return parser_result;
            }
            case 6: {
                final RelativePathPattern rpp = (RelativePathPattern)parser_stack.get(parser_top - 0).value;
                final Pattern result3 = new AbsolutePathPattern(rpp);
                final Symbol parser_result = new Symbol(29, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result3);
                return parser_result;
            }
            case 7: {
                final IdKeyPattern ikp = (IdKeyPattern)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(29, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, ikp);
                return parser_result;
            }
            case 8: {
                final IdKeyPattern ikp = (IdKeyPattern)parser_stack.get(parser_top - 2).value;
                final RelativePathPattern rpp2 = (RelativePathPattern)parser_stack.get(parser_top - 0).value;
                final Pattern result = new ParentPattern(ikp, rpp2);
                final Symbol parser_result = new Symbol(29, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result);
                return parser_result;
            }
            case 9: {
                final IdKeyPattern ikp = (IdKeyPattern)parser_stack.get(parser_top - 2).value;
                final RelativePathPattern rpp2 = (RelativePathPattern)parser_stack.get(parser_top - 0).value;
                final Pattern result = new AncestorPattern(ikp, rpp2);
                final Symbol parser_result = new Symbol(29, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result);
                return parser_result;
            }
            case 10: {
                final RelativePathPattern rpp = (RelativePathPattern)parser_stack.get(parser_top - 0).value;
                final Pattern result3 = new AncestorPattern(rpp);
                final Symbol parser_result = new Symbol(29, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result3);
                return parser_result;
            }
            case 11: {
                final RelativePathPattern rpp = (RelativePathPattern)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(29, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, rpp);
                return parser_result;
            }
            case 12: {
                final String l = (String)parser_stack.get(parser_top - 1).value;
                final IdKeyPattern result4 = new IdPattern(l);
                this.parser.setHasIdCall(true);
                final Symbol parser_result = new Symbol(27, parser_stack.get(parser_top - 3).left, parser_stack.get(parser_top - 0).right, result4);
                return parser_result;
            }
            case 13: {
                final String l2 = (String)parser_stack.get(parser_top - 3).value;
                final String l3 = (String)parser_stack.get(parser_top - 1).value;
                final IdKeyPattern result5 = new KeyPattern(l2, l3);
                final Symbol parser_result = new Symbol(27, parser_stack.get(parser_top - 5).left, parser_stack.get(parser_top - 0).right, result5);
                return parser_result;
            }
            case 14: {
                final String l = (String)parser_stack.get(parser_top - 1).value;
                final StepPattern result6 = new ProcessingInstructionPattern(l);
                final Symbol parser_result = new Symbol(30, parser_stack.get(parser_top - 3).left, parser_stack.get(parser_top - 0).right, result6);
                return parser_result;
            }
            case 15: {
                final StepPattern sp = (StepPattern)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(31, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, sp);
                return parser_result;
            }
            case 16: {
                final StepPattern sp = (StepPattern)parser_stack.get(parser_top - 2).value;
                final RelativePathPattern rpp2 = (RelativePathPattern)parser_stack.get(parser_top - 0).value;
                final RelativePathPattern result7 = new ParentPattern(sp, rpp2);
                final Symbol parser_result = new Symbol(31, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result7);
                return parser_result;
            }
            case 17: {
                final StepPattern sp = (StepPattern)parser_stack.get(parser_top - 2).value;
                final RelativePathPattern rpp2 = (RelativePathPattern)parser_stack.get(parser_top - 0).value;
                final RelativePathPattern result7 = new AncestorPattern(sp, rpp2);
                final Symbol parser_result = new Symbol(31, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result7);
                return parser_result;
            }
            case 18: {
                final Object nt = parser_stack.get(parser_top - 0).value;
                final StepPattern result6 = this.parser.createStepPattern(3, nt, null);
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result6);
                return parser_result;
            }
            case 19: {
                final Object nt = parser_stack.get(parser_top - 1).value;
                final Vector pp = (Vector)parser_stack.get(parser_top - 0).value;
                final StepPattern result8 = this.parser.createStepPattern(3, nt, pp);
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result8);
                return parser_result;
            }
            case 20: {
                final StepPattern pip = (StepPattern)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, pip);
                return parser_result;
            }
            case 21: {
                final StepPattern pip = (StepPattern)parser_stack.get(parser_top - 1).value;
                final Vector pp = (Vector)parser_stack.get(parser_top - 0).value;
                final StepPattern result8 = pip.setPredicates(pp);
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result8);
                return parser_result;
            }
            case 22: {
                final Integer axis = (Integer)parser_stack.get(parser_top - 1).value;
                final Object nt2 = parser_stack.get(parser_top - 0).value;
                final StepPattern result8 = this.parser.createStepPattern(axis, nt2, null);
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result8);
                return parser_result;
            }
            case 23: {
                final Integer axis = (Integer)parser_stack.get(parser_top - 2).value;
                final Object nt2 = parser_stack.get(parser_top - 1).value;
                final Vector pp2 = (Vector)parser_stack.get(parser_top - 0).value;
                final StepPattern result9 = this.parser.createStepPattern(axis, nt2, pp2);
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result9);
                return parser_result;
            }
            case 24: {
                final StepPattern result6;
                final StepPattern pip = result6 = (StepPattern)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result6);
                return parser_result;
            }
            case 25: {
                final StepPattern pip = (StepPattern)parser_stack.get(parser_top - 1).value;
                final Vector pp = (Vector)parser_stack.get(parser_top - 0).value;
                final StepPattern result8 = pip.setPredicates(pp);
                final Symbol parser_result = new Symbol(32, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result8);
                return parser_result;
            }
            case 26: {
                final Object nt = parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(33, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, nt);
                return parser_result;
            }
            case 27: {
                final Object result10 = -1;
                final Symbol parser_result = new Symbol(33, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 28: {
                final Object result10 = 3;
                final Symbol parser_result = new Symbol(33, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 29: {
                final Object result10 = 8;
                final Symbol parser_result = new Symbol(33, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 30: {
                final Object result10 = 7;
                final Symbol parser_result = new Symbol(33, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 31: {
                final Object result10 = null;
                final Symbol parser_result = new Symbol(34, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 32: {
                final QName qn = (QName)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(34, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, qn);
                return parser_result;
            }
            case 33: {
                final Integer result11 = 2;
                final Symbol parser_result = new Symbol(42, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 34: {
                final Integer result11 = 3;
                final Symbol parser_result = new Symbol(42, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 35: {
                final Integer result11 = 2;
                final Symbol parser_result = new Symbol(42, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 36: {
                final Expression p2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Vector temp = new Vector();
                temp.add(p2);
                final Symbol parser_result = new Symbol(35, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, temp);
                return parser_result;
            }
            case 37: {
                final Expression p2 = (Expression)parser_stack.get(parser_top - 1).value;
                final Vector pp = (Vector)parser_stack.get(parser_top - 0).value;
                pp.add(0, p2);
                final Symbol parser_result = new Symbol(35, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, pp);
                return parser_result;
            }
            case 38: {
                final Expression e = (Expression)parser_stack.get(parser_top - 1).value;
                final Expression result12 = new Predicate(e);
                final Symbol parser_result = new Symbol(5, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result12);
                return parser_result;
            }
            case 39: {
                final Expression ex = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(2, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, ex);
                return parser_result;
            }
            case 40: {
                final Expression ae = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(8, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, ae);
                return parser_result;
            }
            case 41: {
                final Expression oe = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ae2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new LogicalExpr(0, oe, ae2);
                final Symbol parser_result = new Symbol(8, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 42: {
                final Expression e = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(9, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, e);
                return parser_result;
            }
            case 43: {
                final Expression ae = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ee = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new LogicalExpr(1, ae, ee);
                final Symbol parser_result = new Symbol(9, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 44: {
                final Expression re = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(10, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, re);
                return parser_result;
            }
            case 45: {
                final Expression ee2 = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression re2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new EqualityExpr(0, ee2, re2);
                final Symbol parser_result = new Symbol(10, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 46: {
                final Expression ee2 = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression re2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new EqualityExpr(1, ee2, re2);
                final Symbol parser_result = new Symbol(10, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 47: {
                final Expression ae = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(11, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, ae);
                return parser_result;
            }
            case 48: {
                final Expression re = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ae2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new RelationalExpr(3, re, ae2);
                final Symbol parser_result = new Symbol(11, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 49: {
                final Expression re = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ae2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new RelationalExpr(2, re, ae2);
                final Symbol parser_result = new Symbol(11, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 50: {
                final Expression re = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ae2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new RelationalExpr(5, re, ae2);
                final Symbol parser_result = new Symbol(11, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 51: {
                final Expression re = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ae2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new RelationalExpr(4, re, ae2);
                final Symbol parser_result = new Symbol(11, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 52: {
                final Expression me = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(12, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, me);
                return parser_result;
            }
            case 53: {
                final Expression ae = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression me2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new BinOpExpr(0, ae, me2);
                final Symbol parser_result = new Symbol(12, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 54: {
                final Expression ae = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression me2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new BinOpExpr(1, ae, me2);
                final Symbol parser_result = new Symbol(12, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 55: {
                final Expression ue = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(13, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, ue);
                return parser_result;
            }
            case 56: {
                final Expression me = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ue2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new BinOpExpr(2, me, ue2);
                final Symbol parser_result = new Symbol(13, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 57: {
                final Expression me = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ue2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new BinOpExpr(3, me, ue2);
                final Symbol parser_result = new Symbol(13, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 58: {
                final Expression me = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression ue2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new BinOpExpr(4, me, ue2);
                final Symbol parser_result = new Symbol(13, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 59: {
                final Expression ue = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(14, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, ue);
                return parser_result;
            }
            case 60: {
                final Expression ue = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result12 = new UnaryOpExpr(ue);
                final Symbol parser_result = new Symbol(14, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result12);
                return parser_result;
            }
            case 61: {
                final Expression pe = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(18, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, pe);
                return parser_result;
            }
            case 62: {
                final Expression pe = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression rest = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new UnionPathExpr(pe, rest);
                final Symbol parser_result = new Symbol(18, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 63: {
                final Expression lp = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(19, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, lp);
                return parser_result;
            }
            case 64: {
                final Expression fexp = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(19, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, fexp);
                return parser_result;
            }
            case 65: {
                final Expression fexp = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression rlp = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new FilterParentPath(fexp, rlp);
                final Symbol parser_result = new Symbol(19, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 66: {
                final Expression fexp = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression rlp = (Expression)parser_stack.get(parser_top - 0).value;
                int nodeType = -1;
                if (rlp instanceof Step && this.parser.isElementAxis(((Step)rlp).getAxis())) {
                    nodeType = 1;
                }
                final Step step = new Step(5, nodeType, null);
                FilterParentPath fpp = new FilterParentPath(fexp, step);
                fpp = new FilterParentPath(fpp, rlp);
                if (!(fexp instanceof KeyCall)) {
                    fpp.setDescendantAxis();
                }
                final Symbol parser_result = new Symbol(19, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, fpp);
                return parser_result;
            }
            case 67: {
                final Expression rlp2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(4, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, rlp2);
                return parser_result;
            }
            case 68: {
                final Expression alp = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(4, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, alp);
                return parser_result;
            }
            case 69: {
                final Expression step2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(21, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, step2);
                return parser_result;
            }
            case 70: {
                Expression result14 = null;
                final Expression rlp = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression step3 = (Expression)parser_stack.get(parser_top - 0).value;
                if (rlp instanceof Step && ((Step)rlp).isAbbreviatedDot()) {
                    result14 = step3;
                }
                else if (((Step)step3).isAbbreviatedDot()) {
                    result14 = rlp;
                }
                else {
                    result14 = new ParentLocationPath((RelativeLocationPath)rlp, step3);
                }
                final Symbol parser_result = new Symbol(21, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 71: {
                final Expression arlp = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(21, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, arlp);
                return parser_result;
            }
            case 72: {
                final Expression result14 = new AbsoluteLocationPath();
                final Symbol parser_result = new Symbol(23, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 73: {
                final Expression rlp2 = (Expression)parser_stack.get(parser_top - 0).value;
                final Expression result12 = new AbsoluteLocationPath(rlp2);
                final Symbol parser_result = new Symbol(23, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result12);
                return parser_result;
            }
            case 74: {
                final Expression aalp = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(23, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, aalp);
                return parser_result;
            }
            case 75: {
                Expression result14 = null;
                final Expression rlp = (Expression)parser_stack.get(parser_top - 2).value;
                final Expression step3 = (Expression)parser_stack.get(parser_top - 0).value;
                final Step right = (Step)step3;
                final int axis2 = right.getAxis();
                final int type = right.getNodeType();
                final Vector predicates = right.getPredicates();
                if (axis2 == 3 && type != 2) {
                    if (predicates == null) {
                        right.setAxis(4);
                        if (rlp instanceof Step && ((Step)rlp).isAbbreviatedDot()) {
                            result14 = right;
                        }
                        else {
                            final RelativeLocationPath left = (RelativeLocationPath)rlp;
                            result14 = new ParentLocationPath(left, right);
                        }
                    }
                    else if (rlp instanceof Step && ((Step)rlp).isAbbreviatedDot()) {
                        final Step left2 = new Step(5, 1, null);
                        result14 = new ParentLocationPath(left2, right);
                    }
                    else {
                        final RelativeLocationPath left = (RelativeLocationPath)rlp;
                        final Step mid = new Step(5, 1, null);
                        final ParentLocationPath ppl = new ParentLocationPath(mid, right);
                        result14 = new ParentLocationPath(left, ppl);
                    }
                }
                else if (axis2 == 2 || type == 2) {
                    final RelativeLocationPath left = (RelativeLocationPath)rlp;
                    final Step middle = new Step(5, 1, null);
                    final ParentLocationPath ppl = new ParentLocationPath(middle, right);
                    result14 = new ParentLocationPath(left, ppl);
                }
                else {
                    final RelativeLocationPath left = (RelativeLocationPath)rlp;
                    final Step middle = new Step(5, -1, null);
                    final ParentLocationPath ppl = new ParentLocationPath(middle, right);
                    result14 = new ParentLocationPath(left, ppl);
                }
                final Symbol parser_result = new Symbol(22, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 76: {
                final Expression rlp2 = (Expression)parser_stack.get(parser_top - 0).value;
                int nodeType2 = -1;
                if (rlp2 instanceof Step && this.parser.isElementAxis(((Step)rlp2).getAxis())) {
                    nodeType2 = 1;
                }
                final Step step4 = new Step(5, nodeType2, null);
                final Expression result15 = new AbsoluteLocationPath(this.parser.insertStep(step4, (RelativeLocationPath)rlp2));
                final Symbol parser_result = new Symbol(24, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result15);
                return parser_result;
            }
            case 77: {
                Expression result14 = null;
                final Object ntest = parser_stack.get(parser_top - 0).value;
                if (ntest instanceof Step) {
                    result14 = (Step)ntest;
                }
                else {
                    result14 = new Step(3, this.parser.findNodeType(3, ntest), null);
                }
                final Symbol parser_result = new Symbol(7, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 78: {
                Expression result14 = null;
                final Object ntest = parser_stack.get(parser_top - 1).value;
                final Vector pp2 = (Vector)parser_stack.get(parser_top - 0).value;
                if (ntest instanceof Step) {
                    final Step step = (Step)ntest;
                    step.addPredicates(pp2);
                    result14 = (Step)ntest;
                }
                else {
                    result14 = new Step(3, this.parser.findNodeType(3, ntest), pp2);
                }
                final Symbol parser_result = new Symbol(7, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 79: {
                final Integer axis = (Integer)parser_stack.get(parser_top - 2).value;
                final Object ntest = parser_stack.get(parser_top - 1).value;
                final Vector pp2 = (Vector)parser_stack.get(parser_top - 0).value;
                final Expression result15 = new Step(axis, this.parser.findNodeType(axis, ntest), pp2);
                final Symbol parser_result = new Symbol(7, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result15);
                return parser_result;
            }
            case 80: {
                final Integer axis = (Integer)parser_stack.get(parser_top - 1).value;
                final Object ntest = parser_stack.get(parser_top - 0).value;
                final Expression result13 = new Step(axis, this.parser.findNodeType(axis, ntest), null);
                final Symbol parser_result = new Symbol(7, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 81: {
                final Expression abbrev = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(7, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, abbrev);
                return parser_result;
            }
            case 82: {
                final Integer an = (Integer)parser_stack.get(parser_top - 1).value;
                final Symbol parser_result = new Symbol(41, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, an);
                return parser_result;
            }
            case 83: {
                final Integer result11 = 2;
                final Symbol parser_result = new Symbol(41, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 84: {
                final Integer result11 = 0;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 85: {
                final Integer result11 = 1;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 86: {
                final Integer result11 = 2;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 87: {
                final Integer result11 = 3;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 88: {
                final Integer result11 = 4;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 89: {
                final Integer result11 = 5;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 90: {
                final Integer result11 = 6;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 91: {
                final Integer result11 = 7;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 92: {
                final Integer result11 = 9;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 93: {
                final Integer result11 = 10;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 94: {
                final Integer result11 = 11;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 95: {
                final Integer result11 = 12;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 96: {
                final Integer result11 = 13;
                final Symbol parser_result = new Symbol(40, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result11);
                return parser_result;
            }
            case 97: {
                final Expression result14 = new Step(13, -1, null);
                final Symbol parser_result = new Symbol(20, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 98: {
                final Expression result14 = new Step(10, -1, null);
                final Symbol parser_result = new Symbol(20, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 99: {
                final Expression primary = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(6, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, primary);
                return parser_result;
            }
            case 100: {
                final Expression primary = (Expression)parser_stack.get(parser_top - 1).value;
                final Vector pp = (Vector)parser_stack.get(parser_top - 0).value;
                final Expression result13 = new FilterExpr(primary, pp);
                final Symbol parser_result = new Symbol(6, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result13);
                return parser_result;
            }
            case 101: {
                final Expression vr = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(17, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, vr);
                return parser_result;
            }
            case 102: {
                final Expression ex = (Expression)parser_stack.get(parser_top - 1).value;
                final Symbol parser_result = new Symbol(17, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, ex);
                return parser_result;
            }
            case 103: {
                final String string = (String)parser_stack.get(parser_top - 0).value;
                String namespace = null;
                final int index = string.lastIndexOf(58);
                if (index > 0) {
                    final String prefix = string.substring(0, index);
                    namespace = this.parser._symbolTable.lookupNamespace(prefix);
                }
                final Expression result15 = (namespace == null) ? new LiteralExpr(string) : new LiteralExpr(string, namespace);
                final Symbol parser_result = new Symbol(17, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result15);
                return parser_result;
            }
            case 104: {
                Expression result14 = null;
                final Long num = (Long)parser_stack.get(parser_top - 0).value;
                if (num < -2147483648L || num > 2147483647L) {
                    result14 = new RealExpr(num);
                }
                else if (num == 0.0) {
                    result14 = new RealExpr(num);
                }
                else if (num.intValue() == 0) {
                    result14 = new IntExpr(num.intValue());
                }
                else if (num == 0.0) {
                    result14 = new RealExpr(num);
                }
                else {
                    result14 = new IntExpr(num.intValue());
                }
                final Symbol parser_result = new Symbol(17, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 105: {
                final Double num2 = (Double)parser_stack.get(parser_top - 0).value;
                final Expression result12 = new RealExpr(num2);
                final Symbol parser_result = new Symbol(17, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result12);
                return parser_result;
            }
            case 106: {
                final Expression fc = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(17, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, fc);
                return parser_result;
            }
            case 107: {
                Expression result14 = null;
                final QName varName = (QName)parser_stack.get(parser_top - 0).value;
                final SyntaxTreeNode node = this.parser.lookupName(varName);
                if (node != null) {
                    if (node instanceof Variable) {
                        result14 = new VariableRef((Variable)node);
                    }
                    else if (node instanceof Param) {
                        result14 = new ParameterRef((Param)node);
                    }
                    else {
                        result14 = new UnresolvedRef(varName);
                    }
                }
                if (node == null) {
                    result14 = new UnresolvedRef(varName);
                }
                final Symbol parser_result = new Symbol(15, parser_stack.get(parser_top - 1).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 108: {
                Expression result14 = null;
                final QName fname = (QName)parser_stack.get(parser_top - 2).value;
                if (fname == this.parser.getQNameIgnoreDefaultNs("current")) {
                    result14 = new CurrentCall(fname);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("number")) {
                    result14 = new NumberCall(fname, XPathParser.EmptyArgs);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("string")) {
                    result14 = new StringCall(fname, XPathParser.EmptyArgs);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("concat")) {
                    result14 = new ConcatCall(fname, XPathParser.EmptyArgs);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("true")) {
                    result14 = new BooleanExpr(true);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("false")) {
                    result14 = new BooleanExpr(false);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("name")) {
                    result14 = new NameCall(fname);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("generate-id")) {
                    result14 = new GenerateIdCall(fname, XPathParser.EmptyArgs);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("string-length")) {
                    result14 = new StringLengthCall(fname, XPathParser.EmptyArgs);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("position")) {
                    result14 = new PositionCall(fname);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("last")) {
                    result14 = new LastCall(fname);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("local-name")) {
                    result14 = new LocalNameCall(fname);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("namespace-uri")) {
                    result14 = new NamespaceUriCall(fname);
                }
                else {
                    result14 = new FunctionCall(fname, XPathParser.EmptyArgs);
                }
                final Symbol parser_result = new Symbol(16, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 109: {
                Expression result14 = null;
                final QName fname = (QName)parser_stack.get(parser_top - 3).value;
                final Vector argl = (Vector)parser_stack.get(parser_top - 1).value;
                if (fname == this.parser.getQNameIgnoreDefaultNs("concat")) {
                    result14 = new ConcatCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("number")) {
                    result14 = new NumberCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("document")) {
                    this.parser.setMultiDocument(true);
                    result14 = new DocumentCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("string")) {
                    result14 = new StringCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("boolean")) {
                    result14 = new BooleanCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("name")) {
                    result14 = new NameCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("generate-id")) {
                    result14 = new GenerateIdCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("not")) {
                    result14 = new NotCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("format-number")) {
                    result14 = new FormatNumberCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("unparsed-entity-uri")) {
                    result14 = new UnparsedEntityUriCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("key")) {
                    result14 = new KeyCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("id")) {
                    result14 = new KeyCall(fname, argl);
                    this.parser.setHasIdCall(true);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("ceiling")) {
                    result14 = new CeilingCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("round")) {
                    result14 = new RoundCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("floor")) {
                    result14 = new FloorCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("contains")) {
                    result14 = new ContainsCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("string-length")) {
                    result14 = new StringLengthCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("starts-with")) {
                    result14 = new StartsWithCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("function-available")) {
                    result14 = new FunctionAvailableCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("element-available")) {
                    result14 = new ElementAvailableCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("local-name")) {
                    result14 = new LocalNameCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("lang")) {
                    result14 = new LangCall(fname, argl);
                }
                else if (fname == this.parser.getQNameIgnoreDefaultNs("namespace-uri")) {
                    result14 = new NamespaceUriCall(fname, argl);
                }
                else if (fname == this.parser.getQName("http://xml.apache.org/xalan/xsltc", "xsltc", "cast")) {
                    result14 = new CastCall(fname, argl);
                }
                else if (fname.getLocalPart().equals("nodeset") || fname.getLocalPart().equals("node-set")) {
                    this.parser.setCallsNodeset(true);
                    result14 = new FunctionCall(fname, argl);
                }
                else {
                    result14 = new FunctionCall(fname, argl);
                }
                final Symbol parser_result = new Symbol(16, parser_stack.get(parser_top - 3).left, parser_stack.get(parser_top - 0).right, result14);
                return parser_result;
            }
            case 110: {
                final Expression arg = (Expression)parser_stack.get(parser_top - 0).value;
                final Vector temp = new Vector();
                temp.add(arg);
                final Symbol parser_result = new Symbol(36, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, temp);
                return parser_result;
            }
            case 111: {
                final Expression arg = (Expression)parser_stack.get(parser_top - 2).value;
                final Vector argl2 = (Vector)parser_stack.get(parser_top - 0).value;
                argl2.add(0, arg);
                final Symbol parser_result = new Symbol(36, parser_stack.get(parser_top - 2).left, parser_stack.get(parser_top - 0).right, argl2);
                return parser_result;
            }
            case 112: {
                final QName fname2 = (QName)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(38, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, fname2);
                return parser_result;
            }
            case 113: {
                final QName vname = (QName)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(39, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, vname);
                return parser_result;
            }
            case 114: {
                final Expression ex = (Expression)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(3, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, ex);
                return parser_result;
            }
            case 115: {
                final Object nt = parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(25, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, nt);
                return parser_result;
            }
            case 116: {
                final Object result10 = -1;
                final Symbol parser_result = new Symbol(25, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 117: {
                final Object result10 = 3;
                final Symbol parser_result = new Symbol(25, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 118: {
                final Object result10 = 8;
                final Symbol parser_result = new Symbol(25, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 119: {
                final String l = (String)parser_stack.get(parser_top - 1).value;
                final QName name = this.parser.getQNameIgnoreDefaultNs("name");
                final Expression exp = new EqualityExpr(0, new NameCall(name), new LiteralExpr(l));
                final Vector predicates2 = new Vector();
                predicates2.add(new Predicate(exp));
                final Object result16 = new Step(3, 7, predicates2);
                final Symbol parser_result = new Symbol(25, parser_stack.get(parser_top - 3).left, parser_stack.get(parser_top - 0).right, result16);
                return parser_result;
            }
            case 120: {
                final Object result10 = 7;
                final Symbol parser_result = new Symbol(25, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 121: {
                final Object result10 = null;
                final Symbol parser_result = new Symbol(26, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result10);
                return parser_result;
            }
            case 122: {
                final QName qn = (QName)parser_stack.get(parser_top - 0).value;
                final Symbol parser_result = new Symbol(26, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, qn);
                return parser_result;
            }
            case 123: {
                final String qname = (String)parser_stack.get(parser_top - 0).value;
                final QName result17 = this.parser.getQNameIgnoreDefaultNs(qname);
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result17);
                return parser_result;
            }
            case 124: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("div");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 125: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("mod");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 126: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("key");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 127: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("child");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 128: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("ancestor-or-self");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 129: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("attribute");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 130: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("child");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 131: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("decendant");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 132: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("decendant-or-self");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 133: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("following");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 134: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("following-sibling");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 135: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("namespace");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 136: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("parent");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 137: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("preceding");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 138: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("preceding-sibling");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 139: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("self");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            case 140: {
                final QName result18 = this.parser.getQNameIgnoreDefaultNs("id");
                final Symbol parser_result = new Symbol(37, parser_stack.get(parser_top - 0).left, parser_stack.get(parser_top - 0).right, result18);
                return parser_result;
            }
            default: {
                throw new Exception("Invalid action number found in internal parse table");
            }
        }
    }
}
