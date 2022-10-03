package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.Utils;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import org.antlr.v4.runtime.misc.MurmurHash;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Recognizer;

public abstract class SemanticContext
{
    public static final SemanticContext NONE;
    
    public abstract boolean eval(final Recognizer<?, ?> p0, final RuleContext p1);
    
    public SemanticContext evalPrecedence(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
        return this;
    }
    
    public static SemanticContext and(final SemanticContext a, final SemanticContext b) {
        if (a == null || a == SemanticContext.NONE) {
            return b;
        }
        if (b == null || b == SemanticContext.NONE) {
            return a;
        }
        final AND result = new AND(a, b);
        if (result.opnds.length == 1) {
            return result.opnds[0];
        }
        return result;
    }
    
    public static SemanticContext or(final SemanticContext a, final SemanticContext b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (a == SemanticContext.NONE || b == SemanticContext.NONE) {
            return SemanticContext.NONE;
        }
        final OR result = new OR(a, b);
        if (result.opnds.length == 1) {
            return result.opnds[0];
        }
        return result;
    }
    
    private static List<PrecedencePredicate> filterPrecedencePredicates(final Collection<? extends SemanticContext> collection) {
        ArrayList<PrecedencePredicate> result = null;
        final Iterator<? extends SemanticContext> iterator = collection.iterator();
        while (iterator.hasNext()) {
            final SemanticContext context = (SemanticContext)iterator.next();
            if (context instanceof PrecedencePredicate) {
                if (result == null) {
                    result = new ArrayList<PrecedencePredicate>();
                }
                result.add((PrecedencePredicate)context);
                iterator.remove();
            }
        }
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    static {
        NONE = new Predicate();
    }
    
    public static class Predicate extends SemanticContext
    {
        public final int ruleIndex;
        public final int predIndex;
        public final boolean isCtxDependent;
        
        protected Predicate() {
            this.ruleIndex = -1;
            this.predIndex = -1;
            this.isCtxDependent = false;
        }
        
        public Predicate(final int ruleIndex, final int predIndex, final boolean isCtxDependent) {
            this.ruleIndex = ruleIndex;
            this.predIndex = predIndex;
            this.isCtxDependent = isCtxDependent;
        }
        
        @Override
        public boolean eval(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
            final RuleContext localctx = this.isCtxDependent ? parserCallStack : null;
            return parser.sempred(localctx, this.ruleIndex, this.predIndex);
        }
        
        @Override
        public int hashCode() {
            int hashCode = MurmurHash.initialize();
            hashCode = MurmurHash.update(hashCode, this.ruleIndex);
            hashCode = MurmurHash.update(hashCode, this.predIndex);
            hashCode = MurmurHash.update(hashCode, this.isCtxDependent ? 1 : 0);
            hashCode = MurmurHash.finish(hashCode, 3);
            return hashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Predicate)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            final Predicate p = (Predicate)obj;
            return this.ruleIndex == p.ruleIndex && this.predIndex == p.predIndex && this.isCtxDependent == p.isCtxDependent;
        }
        
        @Override
        public String toString() {
            return "{" + this.ruleIndex + ":" + this.predIndex + "}?";
        }
    }
    
    public static class PrecedencePredicate extends SemanticContext implements Comparable<PrecedencePredicate>
    {
        public final int precedence;
        
        protected PrecedencePredicate() {
            this.precedence = 0;
        }
        
        public PrecedencePredicate(final int precedence) {
            this.precedence = precedence;
        }
        
        @Override
        public boolean eval(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
            return parser.precpred(parserCallStack, this.precedence);
        }
        
        @Override
        public SemanticContext evalPrecedence(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
            if (parser.precpred(parserCallStack, this.precedence)) {
                return SemanticContext.NONE;
            }
            return null;
        }
        
        @Override
        public int compareTo(final PrecedencePredicate o) {
            return this.precedence - o.precedence;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 1;
            hashCode = 31 * hashCode + this.precedence;
            return hashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof PrecedencePredicate)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            final PrecedencePredicate other = (PrecedencePredicate)obj;
            return this.precedence == other.precedence;
        }
        
        @Override
        public String toString() {
            return "{" + this.precedence + ">=prec}?";
        }
    }
    
    public abstract static class Operator extends SemanticContext
    {
        public abstract Collection<SemanticContext> getOperands();
    }
    
    public static class AND extends Operator
    {
        public final SemanticContext[] opnds;
        
        public AND(final SemanticContext a, final SemanticContext b) {
            final Set<SemanticContext> operands = new HashSet<SemanticContext>();
            if (a instanceof AND) {
                operands.addAll(Arrays.asList(((AND)a).opnds));
            }
            else {
                operands.add(a);
            }
            if (b instanceof AND) {
                operands.addAll(Arrays.asList(((AND)b).opnds));
            }
            else {
                operands.add(b);
            }
            final List<PrecedencePredicate> precedencePredicates = filterPrecedencePredicates(operands);
            if (!precedencePredicates.isEmpty()) {
                final PrecedencePredicate reduced = Collections.min((Collection<? extends PrecedencePredicate>)precedencePredicates);
                operands.add(reduced);
            }
            this.opnds = operands.toArray(new SemanticContext[operands.size()]);
        }
        
        @Override
        public Collection<SemanticContext> getOperands() {
            return Arrays.asList(this.opnds);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AND)) {
                return false;
            }
            final AND other = (AND)obj;
            return Arrays.equals(this.opnds, other.opnds);
        }
        
        @Override
        public int hashCode() {
            return MurmurHash.hashCode(this.opnds, AND.class.hashCode());
        }
        
        @Override
        public boolean eval(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
            for (final SemanticContext opnd : this.opnds) {
                if (!opnd.eval(parser, parserCallStack)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public SemanticContext evalPrecedence(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
            boolean differs = false;
            final List<SemanticContext> operands = new ArrayList<SemanticContext>();
            for (final SemanticContext context : this.opnds) {
                final SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
                differs |= (evaluated != context);
                if (evaluated == null) {
                    return null;
                }
                if (evaluated != AND.NONE) {
                    operands.add(evaluated);
                }
            }
            if (!differs) {
                return this;
            }
            if (operands.isEmpty()) {
                return AND.NONE;
            }
            SemanticContext result = operands.get(0);
            for (int i = 1; i < operands.size(); ++i) {
                result = SemanticContext.and(result, operands.get(i));
            }
            return result;
        }
        
        @Override
        public String toString() {
            return Utils.join(Arrays.asList(this.opnds).iterator(), "&&");
        }
    }
    
    public static class OR extends Operator
    {
        public final SemanticContext[] opnds;
        
        public OR(final SemanticContext a, final SemanticContext b) {
            final Set<SemanticContext> operands = new HashSet<SemanticContext>();
            if (a instanceof OR) {
                operands.addAll(Arrays.asList(((OR)a).opnds));
            }
            else {
                operands.add(a);
            }
            if (b instanceof OR) {
                operands.addAll(Arrays.asList(((OR)b).opnds));
            }
            else {
                operands.add(b);
            }
            final List<PrecedencePredicate> precedencePredicates = filterPrecedencePredicates(operands);
            if (!precedencePredicates.isEmpty()) {
                final PrecedencePredicate reduced = Collections.max((Collection<? extends PrecedencePredicate>)precedencePredicates);
                operands.add(reduced);
            }
            this.opnds = operands.toArray(new SemanticContext[operands.size()]);
        }
        
        @Override
        public Collection<SemanticContext> getOperands() {
            return Arrays.asList(this.opnds);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OR)) {
                return false;
            }
            final OR other = (OR)obj;
            return Arrays.equals(this.opnds, other.opnds);
        }
        
        @Override
        public int hashCode() {
            return MurmurHash.hashCode(this.opnds, OR.class.hashCode());
        }
        
        @Override
        public boolean eval(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
            for (final SemanticContext opnd : this.opnds) {
                if (opnd.eval(parser, parserCallStack)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public SemanticContext evalPrecedence(final Recognizer<?, ?> parser, final RuleContext parserCallStack) {
            boolean differs = false;
            final List<SemanticContext> operands = new ArrayList<SemanticContext>();
            for (final SemanticContext context : this.opnds) {
                final SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
                differs |= (evaluated != context);
                if (evaluated == OR.NONE) {
                    return OR.NONE;
                }
                if (evaluated != null) {
                    operands.add(evaluated);
                }
            }
            if (!differs) {
                return this;
            }
            if (operands.isEmpty()) {
                return null;
            }
            SemanticContext result = operands.get(0);
            for (int i = 1; i < operands.size(); ++i) {
                result = SemanticContext.or(result, operands.get(i));
            }
            return result;
        }
        
        @Override
        public String toString() {
            return Utils.join(Arrays.asList(this.opnds).iterator(), "||");
        }
    }
}
