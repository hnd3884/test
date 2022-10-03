package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.misc.Predicate;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Utils;
import java.util.Arrays;
import org.antlr.v4.runtime.Parser;
import java.util.List;

public class Trees
{
    public static String toStringTree(final Tree t) {
        return toStringTree(t, (List<String>)null);
    }
    
    public static String toStringTree(final Tree t, final Parser recog) {
        final String[] ruleNames = (String[])((recog != null) ? recog.getRuleNames() : null);
        final List<String> ruleNamesList = (ruleNames != null) ? Arrays.asList(ruleNames) : null;
        return toStringTree(t, ruleNamesList);
    }
    
    public static String toStringTree(final Tree t, final List<String> ruleNames) {
        String s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
        if (t.getChildCount() == 0) {
            return s;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("(");
        s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
        buf.append(s);
        buf.append(' ');
        for (int i = 0; i < t.getChildCount(); ++i) {
            if (i > 0) {
                buf.append(' ');
            }
            buf.append(toStringTree(t.getChild(i), ruleNames));
        }
        buf.append(")");
        return buf.toString();
    }
    
    public static String getNodeText(final Tree t, final Parser recog) {
        final String[] ruleNames = (String[])((recog != null) ? recog.getRuleNames() : null);
        final List<String> ruleNamesList = (ruleNames != null) ? Arrays.asList(ruleNames) : null;
        return getNodeText(t, ruleNamesList);
    }
    
    public static String getNodeText(final Tree t, final List<String> ruleNames) {
        if (ruleNames != null) {
            if (t instanceof RuleContext) {
                final int ruleIndex = ((RuleContext)t).getRuleContext().getRuleIndex();
                final String ruleName = ruleNames.get(ruleIndex);
                final int altNumber = ((RuleContext)t).getAltNumber();
                if (altNumber != 0) {
                    return ruleName + ":" + altNumber;
                }
                return ruleName;
            }
            else {
                if (t instanceof ErrorNode) {
                    return t.toString();
                }
                if (t instanceof TerminalNode) {
                    final Token symbol = ((TerminalNode)t).getSymbol();
                    if (symbol != null) {
                        final String s = symbol.getText();
                        return s;
                    }
                }
            }
        }
        final Object payload = t.getPayload();
        if (payload instanceof Token) {
            return ((Token)payload).getText();
        }
        return t.getPayload().toString();
    }
    
    public static List<Tree> getChildren(final Tree t) {
        final List<Tree> kids = new ArrayList<Tree>();
        for (int i = 0; i < t.getChildCount(); ++i) {
            kids.add(t.getChild(i));
        }
        return kids;
    }
    
    public static List<? extends Tree> getAncestors(Tree t) {
        if (t.getParent() == null) {
            return Collections.emptyList();
        }
        final List<Tree> ancestors = new ArrayList<Tree>();
        for (t = t.getParent(); t != null; t = t.getParent()) {
            ancestors.add(0, t);
        }
        return ancestors;
    }
    
    public static boolean isAncestorOf(final Tree t, final Tree u) {
        if (t == null || u == null || t.getParent() == null) {
            return false;
        }
        for (Tree p = u.getParent(); p != null; p = p.getParent()) {
            if (t == p) {
                return true;
            }
        }
        return false;
    }
    
    public static Collection<ParseTree> findAllTokenNodes(final ParseTree t, final int ttype) {
        return findAllNodes(t, ttype, true);
    }
    
    public static Collection<ParseTree> findAllRuleNodes(final ParseTree t, final int ruleIndex) {
        return findAllNodes(t, ruleIndex, false);
    }
    
    public static List<ParseTree> findAllNodes(final ParseTree t, final int index, final boolean findTokens) {
        final List<ParseTree> nodes = new ArrayList<ParseTree>();
        _findAllNodes(t, index, findTokens, nodes);
        return nodes;
    }
    
    public static void _findAllNodes(final ParseTree t, final int index, final boolean findTokens, final List<? super ParseTree> nodes) {
        if (findTokens && t instanceof TerminalNode) {
            final TerminalNode tnode = (TerminalNode)t;
            if (tnode.getSymbol().getType() == index) {
                nodes.add(t);
            }
        }
        else if (!findTokens && t instanceof ParserRuleContext) {
            final ParserRuleContext ctx = (ParserRuleContext)t;
            if (ctx.getRuleIndex() == index) {
                nodes.add(t);
            }
        }
        for (int i = 0; i < t.getChildCount(); ++i) {
            _findAllNodes(t.getChild(i), index, findTokens, nodes);
        }
    }
    
    public static List<ParseTree> getDescendants(final ParseTree t) {
        final List<ParseTree> nodes = new ArrayList<ParseTree>();
        nodes.add(t);
        for (int n = t.getChildCount(), i = 0; i < n; ++i) {
            nodes.addAll(getDescendants(t.getChild(i)));
        }
        return nodes;
    }
    
    @Deprecated
    public static List<ParseTree> descendants(final ParseTree t) {
        return getDescendants(t);
    }
    
    public static ParserRuleContext getRootOfSubtreeEnclosingRegion(final ParseTree t, final int startTokenIndex, final int stopTokenIndex) {
        for (int n = t.getChildCount(), i = 0; i < n; ++i) {
            final ParseTree child = t.getChild(i);
            final ParserRuleContext r = getRootOfSubtreeEnclosingRegion(child, startTokenIndex, stopTokenIndex);
            if (r != null) {
                return r;
            }
        }
        if (t instanceof ParserRuleContext) {
            final ParserRuleContext r2 = (ParserRuleContext)t;
            if (startTokenIndex >= r2.getStart().getTokenIndex() && (r2.getStop() == null || stopTokenIndex <= r2.getStop().getTokenIndex())) {
                return r2;
            }
        }
        return null;
    }
    
    public static void stripChildrenOutOfRange(final ParserRuleContext t, final ParserRuleContext root, final int startIndex, final int stopIndex) {
        if (t == null) {
            return;
        }
        for (int i = 0; i < t.getChildCount(); ++i) {
            final ParseTree child = t.getChild(i);
            final Interval range = child.getSourceInterval();
            if (child instanceof ParserRuleContext && (range.b < startIndex || range.a > stopIndex) && isAncestorOf(child, root)) {
                final CommonToken abbrev = new CommonToken(0, "...");
                t.children.set(i, new TerminalNodeImpl(abbrev));
            }
        }
    }
    
    public static Tree findNodeSuchThat(final Tree t, final Predicate<Tree> pred) {
        if (pred.test(t)) {
            return t;
        }
        for (int n = t.getChildCount(), i = 0; i < n; ++i) {
            final Tree u = findNodeSuchThat(t.getChild(i), pred);
            if (u != null) {
                return u;
            }
        }
        return null;
    }
    
    private Trees() {
    }
}
