package com.sun.org.apache.bcel.internal.util;

import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import com.sun.org.apache.bcel.internal.generic.ClassGenException;
import java.util.Iterator;
import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import java.util.HashMap;

public class InstructionFinder
{
    private static final int OFFSET = 32767;
    private static final int NO_OPCODES = 256;
    private static final HashMap map;
    private InstructionList il;
    private String il_string;
    private InstructionHandle[] handles;
    
    public InstructionFinder(final InstructionList il) {
        this.il = il;
        this.reread();
    }
    
    public final void reread() {
        final int size = this.il.getLength();
        final char[] buf = new char[size];
        this.handles = this.il.getInstructionHandles();
        for (int i = 0; i < size; ++i) {
            buf[i] = makeChar(this.handles[i].getInstruction().getOpcode());
        }
        this.il_string = new String(buf);
    }
    
    private static final String mapName(final String pattern) {
        final String result = InstructionFinder.map.get(pattern);
        if (result != null) {
            return result;
        }
        for (short i = 0; i < 256; ++i) {
            if (pattern.equals(Constants.OPCODE_NAMES[i])) {
                return "" + makeChar(i);
            }
        }
        throw new RuntimeException("Instruction unknown: " + pattern);
    }
    
    private static final String compilePattern(final String pattern) {
        final String lower = pattern.toLowerCase();
        final StringBuffer buf = new StringBuffer();
        for (int size = pattern.length(), i = 0; i < size; ++i) {
            char ch = lower.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                final StringBuffer name = new StringBuffer();
                while ((Character.isLetterOrDigit(ch) || ch == '_') && i < size) {
                    name.append(ch);
                    if (++i >= size) {
                        break;
                    }
                    ch = lower.charAt(i);
                }
                --i;
                buf.append(mapName(name.toString()));
            }
            else if (!Character.isWhitespace(ch)) {
                buf.append(ch);
            }
        }
        return buf.toString();
    }
    
    private InstructionHandle[] getMatch(final int matched_from, final int match_length) {
        final InstructionHandle[] match = new InstructionHandle[match_length];
        System.arraycopy(this.handles, matched_from, match, 0, match_length);
        return match;
    }
    
    public final Iterator search(final String pattern, final InstructionHandle from, final CodeConstraint constraint) {
        final String search = compilePattern(pattern);
        int start = -1;
        for (int i = 0; i < this.handles.length; ++i) {
            if (this.handles[i] == from) {
                start = i;
                break;
            }
        }
        if (start == -1) {
            throw new ClassGenException("Instruction handle " + from + " not found in instruction list.");
        }
        final Pattern regex = Pattern.compile(search);
        final List<InstructionHandle[]> matches = new ArrayList<InstructionHandle[]>();
        int endExpr;
        for (Matcher matcher = regex.matcher(this.il_string); start < this.il_string.length() && matcher.find(start); start = endExpr) {
            final int startExpr = matcher.start();
            endExpr = matcher.end();
            final int lenExpr = endExpr - startExpr;
            final InstructionHandle[] match = this.getMatch(startExpr, lenExpr);
            if (constraint == null || constraint.checkCode(match)) {
                matches.add(match);
            }
        }
        return matches.iterator();
    }
    
    public final Iterator search(final String pattern) {
        return this.search(pattern, this.il.getStart(), null);
    }
    
    public final Iterator search(final String pattern, final InstructionHandle from) {
        return this.search(pattern, from, null);
    }
    
    public final Iterator search(final String pattern, final CodeConstraint constraint) {
        return this.search(pattern, this.il.getStart(), constraint);
    }
    
    private static final char makeChar(final short opcode) {
        return (char)(opcode + 32767);
    }
    
    public final InstructionList getInstructionList() {
        return this.il;
    }
    
    private static String precompile(final short from, final short to, final short extra) {
        final StringBuffer buf = new StringBuffer("(");
        for (short i = from; i <= to; ++i) {
            buf.append(makeChar(i));
            buf.append('|');
        }
        buf.append(makeChar(extra));
        buf.append(")");
        return buf.toString();
    }
    
    private static final String pattern2string(final String pattern) {
        return pattern2string(pattern, true);
    }
    
    private static final String pattern2string(final String pattern, final boolean make_string) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pattern.length(); ++i) {
            final char ch = pattern.charAt(i);
            if (ch >= '\u7fff') {
                if (make_string) {
                    buf.append(Constants.OPCODE_NAMES[ch - '\u7fff']);
                }
                else {
                    buf.append(ch - '\u7fff');
                }
            }
            else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }
    
    static {
        (map = new HashMap()).put("arithmeticinstruction", "(irem|lrem|iand|ior|ineg|isub|lneg|fneg|fmul|ldiv|fadd|lxor|frem|idiv|land|ixor|ishr|fsub|lshl|fdiv|iadd|lor|dmul|lsub|ishl|imul|lmul|lushr|dneg|iushr|lshr|ddiv|drem|dadd|ladd|dsub)");
        InstructionFinder.map.put("invokeinstruction", "(invokevirtual|invokeinterface|invokestatic|invokespecial)");
        InstructionFinder.map.put("arrayinstruction", "(baload|aastore|saload|caload|fastore|lastore|iaload|castore|iastore|aaload|bastore|sastore|faload|laload|daload|dastore)");
        InstructionFinder.map.put("gotoinstruction", "(goto|goto_w)");
        InstructionFinder.map.put("conversioninstruction", "(d2l|l2d|i2s|d2i|l2i|i2b|l2f|d2f|f2i|i2d|i2l|f2d|i2c|f2l|i2f)");
        InstructionFinder.map.put("localvariableinstruction", "(fstore|iinc|lload|dstore|dload|iload|aload|astore|istore|fload|lstore)");
        InstructionFinder.map.put("loadinstruction", "(fload|dload|lload|iload|aload)");
        InstructionFinder.map.put("fieldinstruction", "(getfield|putstatic|getstatic|putfield)");
        InstructionFinder.map.put("cpinstruction", "(ldc2_w|invokeinterface|multianewarray|putstatic|instanceof|getstatic|checkcast|getfield|invokespecial|ldc_w|invokestatic|invokevirtual|putfield|ldc|new|anewarray)");
        InstructionFinder.map.put("stackinstruction", "(dup2|swap|dup2_x2|pop|pop2|dup|dup2_x1|dup_x2|dup_x1)");
        InstructionFinder.map.put("branchinstruction", "(ifle|if_acmpne|if_icmpeq|if_acmpeq|ifnonnull|goto_w|iflt|ifnull|if_icmpne|tableswitch|if_icmple|ifeq|if_icmplt|jsr_w|if_icmpgt|ifgt|jsr|goto|ifne|ifge|lookupswitch|if_icmpge)");
        InstructionFinder.map.put("returninstruction", "(lreturn|ireturn|freturn|dreturn|areturn|return)");
        InstructionFinder.map.put("storeinstruction", "(istore|fstore|dstore|astore|lstore)");
        InstructionFinder.map.put("select", "(tableswitch|lookupswitch)");
        InstructionFinder.map.put("ifinstruction", "(ifeq|ifgt|if_icmpne|if_icmpeq|ifge|ifnull|ifne|if_icmple|if_icmpge|if_acmpeq|if_icmplt|if_acmpne|ifnonnull|iflt|if_icmpgt|ifle)");
        InstructionFinder.map.put("jsrinstruction", "(jsr|jsr_w)");
        InstructionFinder.map.put("variablelengthinstruction", "(tableswitch|jsr|goto|lookupswitch)");
        InstructionFinder.map.put("unconditionalbranch", "(goto|jsr|jsr_w|athrow|goto_w)");
        InstructionFinder.map.put("constantpushinstruction", "(dconst|bipush|sipush|fconst|iconst|lconst)");
        InstructionFinder.map.put("typedinstruction", "(imul|lsub|aload|fload|lor|new|aaload|fcmpg|iand|iaload|lrem|idiv|d2l|isub|dcmpg|dastore|ret|f2d|f2i|drem|iinc|i2c|checkcast|frem|lreturn|astore|lushr|daload|dneg|fastore|istore|lshl|ldiv|lstore|areturn|ishr|ldc_w|invokeinterface|aastore|lxor|ishl|l2d|i2f|return|faload|sipush|iushr|caload|instanceof|invokespecial|putfield|fmul|ireturn|laload|d2f|lneg|ixor|i2l|fdiv|lastore|multianewarray|i2b|getstatic|i2d|putstatic|fcmpl|saload|ladd|irem|dload|jsr_w|dconst|dcmpl|fsub|freturn|ldc|aconst_null|castore|lmul|ldc2_w|dadd|iconst|f2l|ddiv|dstore|land|jsr|anewarray|dmul|bipush|dsub|sastore|d2i|i2s|lshr|iadd|l2i|lload|bastore|fstore|fneg|iload|fadd|baload|fconst|ior|ineg|dreturn|l2f|lconst|getfield|invokevirtual|invokestatic|iastore)");
        InstructionFinder.map.put("popinstruction", "(fstore|dstore|pop|pop2|astore|putstatic|istore|lstore)");
        InstructionFinder.map.put("allocationinstruction", "(multianewarray|new|anewarray|newarray)");
        InstructionFinder.map.put("indexedinstruction", "(lload|lstore|fload|ldc2_w|invokeinterface|multianewarray|astore|dload|putstatic|instanceof|getstatic|checkcast|getfield|invokespecial|dstore|istore|iinc|ldc_w|ret|fstore|invokestatic|iload|putfield|invokevirtual|ldc|new|aload|anewarray)");
        InstructionFinder.map.put("pushinstruction", "(dup|lload|dup2|bipush|fload|ldc2_w|sipush|lconst|fconst|dload|getstatic|ldc_w|aconst_null|dconst|iload|ldc|iconst|aload)");
        InstructionFinder.map.put("stackproducer", "(imul|lsub|aload|fload|lor|new|aaload|fcmpg|iand|iaload|lrem|idiv|d2l|isub|dcmpg|dup|f2d|f2i|drem|i2c|checkcast|frem|lushr|daload|dneg|lshl|ldiv|ishr|ldc_w|invokeinterface|lxor|ishl|l2d|i2f|faload|sipush|iushr|caload|instanceof|invokespecial|fmul|laload|d2f|lneg|ixor|i2l|fdiv|getstatic|i2b|swap|i2d|dup2|fcmpl|saload|ladd|irem|dload|jsr_w|dconst|dcmpl|fsub|ldc|arraylength|aconst_null|tableswitch|lmul|ldc2_w|iconst|dadd|f2l|ddiv|land|jsr|anewarray|dmul|bipush|dsub|d2i|newarray|i2s|lshr|iadd|lload|l2i|fneg|iload|fadd|baload|fconst|lookupswitch|ior|ineg|lconst|l2f|getfield|invokevirtual|invokestatic)");
        InstructionFinder.map.put("stackconsumer", "(imul|lsub|lor|iflt|fcmpg|if_icmpgt|iand|ifeq|if_icmplt|lrem|ifnonnull|idiv|d2l|isub|dcmpg|dastore|if_icmpeq|f2d|f2i|drem|i2c|checkcast|frem|lreturn|astore|lushr|pop2|monitorexit|dneg|fastore|istore|lshl|ldiv|lstore|areturn|if_icmpge|ishr|monitorenter|invokeinterface|aastore|lxor|ishl|l2d|i2f|return|iushr|instanceof|invokespecial|fmul|ireturn|d2f|lneg|ixor|pop|i2l|ifnull|fdiv|lastore|i2b|if_acmpeq|ifge|swap|i2d|putstatic|fcmpl|ladd|irem|dcmpl|fsub|freturn|ifgt|castore|lmul|dadd|f2l|ddiv|dstore|land|if_icmpne|if_acmpne|dmul|dsub|sastore|ifle|d2i|i2s|lshr|iadd|l2i|bastore|fstore|fneg|fadd|ior|ineg|ifne|dreturn|l2f|if_icmple|getfield|invokevirtual|invokestatic|iastore)");
        InstructionFinder.map.put("exceptionthrower", "(irem|lrem|laload|putstatic|baload|dastore|areturn|getstatic|ldiv|anewarray|iastore|castore|idiv|saload|lastore|fastore|putfield|lreturn|caload|getfield|return|aastore|freturn|newarray|instanceof|multianewarray|athrow|faload|iaload|aaload|dreturn|monitorenter|checkcast|bastore|arraylength|new|invokevirtual|sastore|ldc_w|ireturn|invokespecial|monitorexit|invokeinterface|ldc|invokestatic|daload)");
        InstructionFinder.map.put("loadclass", "(multianewarray|invokeinterface|instanceof|invokespecial|putfield|checkcast|putstatic|invokevirtual|new|getstatic|invokestatic|getfield|anewarray)");
        InstructionFinder.map.put("instructiontargeter", "(ifle|if_acmpne|if_icmpeq|if_acmpeq|ifnonnull|goto_w|iflt|ifnull|if_icmpne|tableswitch|if_icmple|ifeq|if_icmplt|jsr_w|if_icmpgt|ifgt|jsr|goto|ifne|ifge|lookupswitch|if_icmpge)");
        InstructionFinder.map.put("if_icmp", "(if_icmpne|if_icmpeq|if_icmple|if_icmpge|if_icmplt|if_icmpgt)");
        InstructionFinder.map.put("if_acmp", "(if_acmpeq|if_acmpne)");
        InstructionFinder.map.put("if", "(ifeq|ifne|iflt|ifge|ifgt|ifle)");
        InstructionFinder.map.put("iconst", precompile((short)3, (short)8, (short)2));
        InstructionFinder.map.put("lconst", new String(new char[] { '(', makeChar((short)9), '|', makeChar((short)10), ')' }));
        InstructionFinder.map.put("dconst", new String(new char[] { '(', makeChar((short)14), '|', makeChar((short)15), ')' }));
        InstructionFinder.map.put("fconst", new String(new char[] { '(', makeChar((short)11), '|', makeChar((short)12), ')' }));
        InstructionFinder.map.put("iload", precompile((short)26, (short)29, (short)21));
        InstructionFinder.map.put("dload", precompile((short)38, (short)41, (short)24));
        InstructionFinder.map.put("fload", precompile((short)34, (short)37, (short)23));
        InstructionFinder.map.put("aload", precompile((short)42, (short)45, (short)25));
        InstructionFinder.map.put("istore", precompile((short)59, (short)62, (short)54));
        InstructionFinder.map.put("dstore", precompile((short)71, (short)74, (short)57));
        InstructionFinder.map.put("fstore", precompile((short)67, (short)70, (short)56));
        InstructionFinder.map.put("astore", precompile((short)75, (short)78, (short)58));
        for (final String key : InstructionFinder.map.keySet()) {
            final String value = InstructionFinder.map.get(key);
            final char ch = value.charAt(1);
            if (ch < '\u7fff') {
                InstructionFinder.map.put(key, compilePattern(value));
            }
        }
        final StringBuffer buf = new StringBuffer("(");
        for (short j = 0; j < 256; ++j) {
            if (Constants.NO_OF_OPERANDS[j] != -1) {
                buf.append(makeChar(j));
                if (j < 255) {
                    buf.append('|');
                }
            }
        }
        buf.append(')');
        InstructionFinder.map.put("instruction", buf.toString());
    }
    
    public interface CodeConstraint
    {
        boolean checkCode(final InstructionHandle[] p0);
    }
}
