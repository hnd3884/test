package org.antlr.v4.runtime;

import java.util.Iterator;
import org.antlr.v4.runtime.misc.Interval;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenStreamRewriter
{
    public static final String DEFAULT_PROGRAM_NAME = "default";
    public static final int PROGRAM_INIT_SIZE = 100;
    public static final int MIN_TOKEN_INDEX = 0;
    protected final TokenStream tokens;
    protected final Map<String, List<RewriteOperation>> programs;
    protected final Map<String, Integer> lastRewriteTokenIndexes;
    
    public TokenStreamRewriter(final TokenStream tokens) {
        this.tokens = tokens;
        (this.programs = new HashMap<String, List<RewriteOperation>>()).put("default", new ArrayList<RewriteOperation>(100));
        this.lastRewriteTokenIndexes = new HashMap<String, Integer>();
    }
    
    public final TokenStream getTokenStream() {
        return this.tokens;
    }
    
    public void rollback(final int instructionIndex) {
        this.rollback("default", instructionIndex);
    }
    
    public void rollback(final String programName, final int instructionIndex) {
        final List<RewriteOperation> is = this.programs.get(programName);
        if (is != null) {
            this.programs.put(programName, is.subList(0, instructionIndex));
        }
    }
    
    public void deleteProgram() {
        this.deleteProgram("default");
    }
    
    public void deleteProgram(final String programName) {
        this.rollback(programName, 0);
    }
    
    public void insertAfter(final Token t, final Object text) {
        this.insertAfter("default", t, text);
    }
    
    public void insertAfter(final int index, final Object text) {
        this.insertAfter("default", index, text);
    }
    
    public void insertAfter(final String programName, final Token t, final Object text) {
        this.insertAfter(programName, t.getTokenIndex(), text);
    }
    
    public void insertAfter(final String programName, final int index, final Object text) {
        this.insertBefore(programName, index + 1, text);
    }
    
    public void insertBefore(final Token t, final Object text) {
        this.insertBefore("default", t, text);
    }
    
    public void insertBefore(final int index, final Object text) {
        this.insertBefore("default", index, text);
    }
    
    public void insertBefore(final String programName, final Token t, final Object text) {
        this.insertBefore(programName, t.getTokenIndex(), text);
    }
    
    public void insertBefore(final String programName, final int index, final Object text) {
        final RewriteOperation op = new InsertBeforeOp(index, text);
        final List<RewriteOperation> rewrites = this.getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
    }
    
    public void replace(final int index, final Object text) {
        this.replace("default", index, index, text);
    }
    
    public void replace(final int from, final int to, final Object text) {
        this.replace("default", from, to, text);
    }
    
    public void replace(final Token indexT, final Object text) {
        this.replace("default", indexT, indexT, text);
    }
    
    public void replace(final Token from, final Token to, final Object text) {
        this.replace("default", from, to, text);
    }
    
    public void replace(final String programName, final int from, final int to, final Object text) {
        if (from > to || from < 0 || to < 0 || to >= this.tokens.size()) {
            throw new IllegalArgumentException("replace: range invalid: " + from + ".." + to + "(size=" + this.tokens.size() + ")");
        }
        final RewriteOperation op = new ReplaceOp(from, to, text);
        final List<RewriteOperation> rewrites = this.getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
    }
    
    public void replace(final String programName, final Token from, final Token to, final Object text) {
        this.replace(programName, from.getTokenIndex(), to.getTokenIndex(), text);
    }
    
    public void delete(final int index) {
        this.delete("default", index, index);
    }
    
    public void delete(final int from, final int to) {
        this.delete("default", from, to);
    }
    
    public void delete(final Token indexT) {
        this.delete("default", indexT, indexT);
    }
    
    public void delete(final Token from, final Token to) {
        this.delete("default", from, to);
    }
    
    public void delete(final String programName, final int from, final int to) {
        this.replace(programName, from, to, null);
    }
    
    public void delete(final String programName, final Token from, final Token to) {
        this.replace(programName, from, to, null);
    }
    
    public int getLastRewriteTokenIndex() {
        return this.getLastRewriteTokenIndex("default");
    }
    
    protected int getLastRewriteTokenIndex(final String programName) {
        final Integer I = this.lastRewriteTokenIndexes.get(programName);
        if (I == null) {
            return -1;
        }
        return I;
    }
    
    protected void setLastRewriteTokenIndex(final String programName, final int i) {
        this.lastRewriteTokenIndexes.put(programName, i);
    }
    
    protected List<RewriteOperation> getProgram(final String name) {
        List<RewriteOperation> is = this.programs.get(name);
        if (is == null) {
            is = this.initializeProgram(name);
        }
        return is;
    }
    
    private List<RewriteOperation> initializeProgram(final String name) {
        final List<RewriteOperation> is = new ArrayList<RewriteOperation>(100);
        this.programs.put(name, is);
        return is;
    }
    
    public String getText() {
        return this.getText("default", Interval.of(0, this.tokens.size() - 1));
    }
    
    public String getText(final String programName) {
        return this.getText(programName, Interval.of(0, this.tokens.size() - 1));
    }
    
    public String getText(final Interval interval) {
        return this.getText("default", interval);
    }
    
    public String getText(final String programName, final Interval interval) {
        final List<RewriteOperation> rewrites = this.programs.get(programName);
        int start = interval.a;
        int stop = interval.b;
        if (stop > this.tokens.size() - 1) {
            stop = this.tokens.size() - 1;
        }
        if (start < 0) {
            start = 0;
        }
        if (rewrites == null || rewrites.isEmpty()) {
            return this.tokens.getText(interval);
        }
        final StringBuilder buf = new StringBuilder();
        final Map<Integer, RewriteOperation> indexToOp = this.reduceToSingleOperationPerIndex(rewrites);
        int i = start;
        while (i <= stop && i < this.tokens.size()) {
            final RewriteOperation op = indexToOp.get(i);
            indexToOp.remove(i);
            final Token t = this.tokens.get(i);
            if (op == null) {
                if (t.getType() != -1) {
                    buf.append(t.getText());
                }
                ++i;
            }
            else {
                i = op.execute(buf);
            }
        }
        if (stop == this.tokens.size() - 1) {
            for (final RewriteOperation op2 : indexToOp.values()) {
                if (op2.index >= this.tokens.size() - 1) {
                    buf.append(op2.text);
                }
            }
        }
        return buf.toString();
    }
    
    protected Map<Integer, RewriteOperation> reduceToSingleOperationPerIndex(final List<RewriteOperation> rewrites) {
        for (int i = 0; i < rewrites.size(); ++i) {
            final RewriteOperation op = rewrites.get(i);
            if (op != null) {
                if (op instanceof ReplaceOp) {
                    final ReplaceOp rop = rewrites.get(i);
                    final List<? extends InsertBeforeOp> inserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
                    for (final InsertBeforeOp iop : inserts) {
                        if (iop.index == rop.index) {
                            rewrites.set(iop.instructionIndex, null);
                            rop.text = iop.text.toString() + ((rop.text != null) ? rop.text.toString() : "");
                        }
                        else {
                            if (iop.index <= rop.index || iop.index > rop.lastIndex) {
                                continue;
                            }
                            rewrites.set(iop.instructionIndex, null);
                        }
                    }
                    final List<? extends ReplaceOp> prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
                    for (final ReplaceOp prevRop : prevReplaces) {
                        if (prevRop.index >= rop.index && prevRop.lastIndex <= rop.lastIndex) {
                            rewrites.set(prevRop.instructionIndex, null);
                        }
                        else {
                            final boolean disjoint = prevRop.lastIndex < rop.index || prevRop.index > rop.lastIndex;
                            final boolean same = prevRop.index == rop.index && prevRop.lastIndex == rop.lastIndex;
                            if (prevRop.text == null && rop.text == null && !disjoint) {
                                rewrites.set(prevRop.instructionIndex, null);
                                rop.index = Math.min(prevRop.index, rop.index);
                                rop.lastIndex = Math.max(prevRop.lastIndex, rop.lastIndex);
                                System.out.println("new rop " + rop);
                            }
                            else {
                                if (!disjoint && !same) {
                                    throw new IllegalArgumentException("replace op boundaries of " + rop + " overlap with previous " + prevRop);
                                }
                                continue;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < rewrites.size(); ++i) {
            final RewriteOperation op = rewrites.get(i);
            if (op != null) {
                if (op instanceof InsertBeforeOp) {
                    final InsertBeforeOp iop2 = rewrites.get(i);
                    final List<? extends InsertBeforeOp> prevInserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
                    for (final InsertBeforeOp prevIop : prevInserts) {
                        if (prevIop.index == iop2.index) {
                            iop2.text = this.catOpText(iop2.text, prevIop.text);
                            rewrites.set(prevIop.instructionIndex, null);
                        }
                    }
                    final List<? extends ReplaceOp> prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
                    for (final ReplaceOp rop2 : prevReplaces) {
                        if (iop2.index == rop2.index) {
                            rop2.text = this.catOpText(iop2.text, rop2.text);
                            rewrites.set(i, null);
                        }
                        else {
                            if (iop2.index >= rop2.index && iop2.index <= rop2.lastIndex) {
                                throw new IllegalArgumentException("insert op " + iop2 + " within boundaries of previous " + rop2);
                            }
                            continue;
                        }
                    }
                }
            }
        }
        final Map<Integer, RewriteOperation> m = new HashMap<Integer, RewriteOperation>();
        for (int j = 0; j < rewrites.size(); ++j) {
            final RewriteOperation op2 = rewrites.get(j);
            if (op2 != null) {
                if (m.get(op2.index) != null) {
                    throw new Error("should only be one op per index");
                }
                m.put(op2.index, op2);
            }
        }
        return m;
    }
    
    protected String catOpText(final Object a, final Object b) {
        String x = "";
        String y = "";
        if (a != null) {
            x = a.toString();
        }
        if (b != null) {
            y = b.toString();
        }
        return x + y;
    }
    
    protected <T extends RewriteOperation> List<? extends T> getKindOfOps(final List<? extends RewriteOperation> rewrites, final Class<T> kind, final int before) {
        final List<T> ops = new ArrayList<T>();
        for (int i = 0; i < before && i < rewrites.size(); ++i) {
            final RewriteOperation op = (RewriteOperation)rewrites.get(i);
            if (op != null) {
                if (kind.isInstance(op)) {
                    ops.add(kind.cast(op));
                }
            }
        }
        return (List<? extends T>)ops;
    }
    
    public class RewriteOperation
    {
        protected int instructionIndex;
        protected int index;
        protected Object text;
        
        protected RewriteOperation(final int index) {
            this.index = index;
        }
        
        protected RewriteOperation(final int index, final Object text) {
            this.index = index;
            this.text = text;
        }
        
        public int execute(final StringBuilder buf) {
            return this.index;
        }
        
        @Override
        public String toString() {
            String opName = this.getClass().getName();
            final int $index = opName.indexOf(36);
            opName = opName.substring($index + 1, opName.length());
            return "<" + opName + "@" + TokenStreamRewriter.this.tokens.get(this.index) + ":\"" + this.text + "\">";
        }
    }
    
    class InsertBeforeOp extends RewriteOperation
    {
        public InsertBeforeOp(final int index, final Object text) {
            super(index, text);
        }
        
        @Override
        public int execute(final StringBuilder buf) {
            buf.append(this.text);
            if (TokenStreamRewriter.this.tokens.get(this.index).getType() != -1) {
                buf.append(TokenStreamRewriter.this.tokens.get(this.index).getText());
            }
            return this.index + 1;
        }
    }
    
    class ReplaceOp extends RewriteOperation
    {
        protected int lastIndex;
        
        public ReplaceOp(final int from, final int to, final Object text) {
            super(from, text);
            this.lastIndex = to;
        }
        
        @Override
        public int execute(final StringBuilder buf) {
            if (this.text != null) {
                buf.append(this.text);
            }
            return this.lastIndex + 1;
        }
        
        @Override
        public String toString() {
            if (this.text == null) {
                return "<DeleteOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ">";
            }
            return "<ReplaceOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ":\"" + this.text + "\">";
        }
    }
}
