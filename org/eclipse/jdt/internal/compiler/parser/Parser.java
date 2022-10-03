package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.parser.diagnose.DiagnoseParser;
import org.eclipse.jdt.internal.compiler.parser.diagnose.RangeUtil;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.ast.IntersectionCastTypeReference;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CombinedBinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import java.util.Properties;
import java.io.InputStream;
import java.io.BufferedInputStream;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;
import java.io.File;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.StringTokenizer;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.io.IOException;
import org.eclipse.jdt.internal.compiler.ReadManager;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;

public class Parser implements TerminalTokens, ParserBasicInformation, ConflictedParser, OperatorIds, TypeIds
{
    protected static final int THIS_CALL = 3;
    protected static final int SUPER_CALL = 2;
    public static final char[] FALL_THROUGH_TAG;
    public static final char[] CASES_OMITTED_TAG;
    public static char[] asb;
    public static char[] asr;
    protected static final int AstStackIncrement = 100;
    public static char[] base_action;
    public static final int BracketKinds = 3;
    public static short[] check_table;
    public static final int CurlyBracket = 2;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_AUTOMATON = false;
    private static final String EOF_TOKEN = "$eof";
    private static final String ERROR_TOKEN = "$error";
    protected static final int ExpressionStackIncrement = 100;
    protected static final int GenericsStackIncrement = 10;
    private static final String FILEPREFIX = "parser";
    public static char[] in_symb;
    private static final String INVALID_CHARACTER = "Invalid Character";
    public static char[] lhs;
    public static String[] name;
    public static char[] nasb;
    public static char[] nasr;
    public static char[] non_terminal_index;
    private static final String READABLE_NAMES_FILE = "readableNames";
    public static String[] readableName;
    public static byte[] rhs;
    public static int[] reverse_index;
    public static char[] recovery_templates_index;
    public static char[] recovery_templates;
    public static char[] statements_recovery_filter;
    public static long[] rules_compliance;
    public static final int RoundBracket = 0;
    public static byte[] scope_la;
    public static char[] scope_lhs;
    public static char[] scope_prefix;
    public static char[] scope_rhs;
    public static char[] scope_state;
    public static char[] scope_state_set;
    public static char[] scope_suffix;
    public static final int SquareBracket = 1;
    protected static final int StackIncrement = 255;
    public static char[] term_action;
    public static byte[] term_check;
    public static char[] terminal_index;
    private static final String UNEXPECTED_EOF = "Unexpected End Of File";
    public static boolean VERBOSE_RECOVERY;
    protected static final int HALT = 0;
    protected static final int RESTART = 1;
    protected static final int RESUME = 2;
    public Scanner scanner;
    public int currentToken;
    protected int astLengthPtr;
    protected int[] astLengthStack;
    protected int astPtr;
    protected ASTNode[] astStack;
    public CompilationUnitDeclaration compilationUnit;
    protected RecoveredElement currentElement;
    protected boolean diet;
    protected int dietInt;
    protected int endPosition;
    protected int endStatementPosition;
    protected int expressionLengthPtr;
    protected int[] expressionLengthStack;
    protected int expressionPtr;
    protected Expression[] expressionStack;
    protected int rBracketPosition;
    public int firstToken;
    protected int typeAnnotationPtr;
    protected int typeAnnotationLengthPtr;
    protected Annotation[] typeAnnotationStack;
    protected int[] typeAnnotationLengthStack;
    protected static final int TypeAnnotationStackIncrement = 100;
    protected int genericsIdentifiersLengthPtr;
    protected int[] genericsIdentifiersLengthStack;
    protected int genericsLengthPtr;
    protected int[] genericsLengthStack;
    protected int genericsPtr;
    protected ASTNode[] genericsStack;
    protected boolean hasError;
    protected boolean hasReportedError;
    protected int identifierLengthPtr;
    protected int[] identifierLengthStack;
    protected long[] identifierPositionStack;
    protected int identifierPtr;
    protected char[][] identifierStack;
    protected boolean ignoreNextOpeningBrace;
    protected boolean ignoreNextClosingBrace;
    protected int intPtr;
    protected int[] intStack;
    public int lastAct;
    protected int lastCheckPoint;
    protected int lastErrorEndPosition;
    protected int lastErrorEndPositionBeforeRecovery;
    protected int lastIgnoredToken;
    protected int nextIgnoredToken;
    protected int listLength;
    protected int listTypeParameterLength;
    protected int lParenPos;
    protected int rParenPos;
    protected int modifiers;
    protected int modifiersSourceStart;
    protected int colonColonStart;
    protected int[] nestedMethod;
    protected int forStartPosition;
    protected int nestedType;
    protected int dimensions;
    ASTNode[] noAstNodes;
    Expression[] noExpressions;
    protected boolean optimizeStringLiterals;
    protected CompilerOptions options;
    protected ProblemReporter problemReporter;
    protected int rBraceStart;
    protected int rBraceEnd;
    protected int rBraceSuccessorStart;
    protected int realBlockPtr;
    protected int[] realBlockStack;
    protected int recoveredStaticInitializerStart;
    public ReferenceContext referenceContext;
    public boolean reportOnlyOneSyntaxError;
    public boolean reportSyntaxErrorIsRequired;
    protected boolean restartRecovery;
    protected boolean annotationRecoveryActivated;
    protected int lastPosistion;
    public boolean methodRecoveryActivated;
    protected boolean statementRecoveryActivated;
    protected TypeDeclaration[] recoveredTypes;
    protected int recoveredTypePtr;
    protected int nextTypeStart;
    protected TypeDeclaration pendingRecoveredType;
    public RecoveryScanner recoveryScanner;
    protected int[] stack;
    protected int stateStackTop;
    protected int synchronizedBlockSourceStart;
    protected int[] variablesCounter;
    protected boolean checkExternalizeStrings;
    protected boolean recordStringLiterals;
    public Javadoc javadoc;
    public JavadocParser javadocParser;
    protected int lastJavadocEnd;
    public ReadManager readManager;
    protected int valueLambdaNestDepth;
    private int[] stateStackLengthStack;
    protected boolean parsingJava8Plus;
    protected int unstackedAct;
    private boolean haltOnSyntaxError;
    private boolean tolerateDefaultClassMethods;
    private boolean processingLambdaParameterList;
    private boolean expectTypeAnnotation;
    private boolean reparsingLambdaExpression;
    
    static {
        FALL_THROUGH_TAG = "$FALL-THROUGH$".toCharArray();
        CASES_OMITTED_TAG = "$CASES-OMITTED$".toCharArray();
        Parser.asb = null;
        Parser.asr = null;
        Parser.base_action = null;
        Parser.check_table = null;
        Parser.in_symb = null;
        Parser.lhs = null;
        Parser.name = null;
        Parser.nasb = null;
        Parser.nasr = null;
        Parser.non_terminal_index = null;
        Parser.readableName = null;
        Parser.rhs = null;
        Parser.reverse_index = null;
        Parser.recovery_templates_index = null;
        Parser.recovery_templates = null;
        Parser.statements_recovery_filter = null;
        Parser.rules_compliance = null;
        Parser.scope_la = null;
        Parser.scope_lhs = null;
        Parser.scope_prefix = null;
        Parser.scope_rhs = null;
        Parser.scope_state = null;
        Parser.scope_state_set = null;
        Parser.scope_suffix = null;
        Parser.term_action = null;
        Parser.term_check = null;
        Parser.terminal_index = null;
        Parser.VERBOSE_RECOVERY = false;
        try {
            initTables();
        }
        catch (final IOException ex) {
            throw new ExceptionInInitializerError(ex.getMessage());
        }
    }
    
    public static int asi(final int state) {
        return Parser.asb[original_state(state)];
    }
    
    public static final short base_check(final int i) {
        return Parser.check_table[i - 801];
    }
    
    private static final void buildFile(final String filename, final List listToDump) {
        BufferedWriter writer = null;
        Label_0102: {
            try {
                writer = new BufferedWriter(new FileWriter(filename));
                final Iterator iterator = listToDump.iterator();
                while (iterator.hasNext()) {
                    writer.write(String.valueOf(iterator.next()));
                }
                writer.flush();
            }
            catch (final IOException ex) {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (final IOException ex2) {}
                }
                break Label_0102;
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (final IOException ex3) {}
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (final IOException ex4) {}
            }
        }
        System.out.println(String.valueOf(filename) + " creation complete");
    }
    
    private static void buildFileForCompliance(final String file, final int length, final String[] tokens) {
        final byte[] result = new byte[length * 8];
        for (int i = 0; i < tokens.length; i += 3) {
            if ("2".equals(tokens[i])) {
                final int index = Integer.parseInt(tokens[i + 1]);
                final String token = tokens[i + 2].trim();
                long compliance = 0L;
                if ("1.4".equals(token)) {
                    compliance = 3145728L;
                }
                else if ("1.5".equals(token)) {
                    compliance = 3211264L;
                }
                else if ("1.6".equals(token)) {
                    compliance = 3276800L;
                }
                else if ("1.7".equals(token)) {
                    compliance = 3342336L;
                }
                else if ("1.8".equals(token)) {
                    compliance = 3407872L;
                }
                else if ("recovery".equals(token)) {
                    compliance = Long.MAX_VALUE;
                }
                final int j = index * 8;
                result[j] = (byte)(compliance >>> 56);
                result[j + 1] = (byte)(compliance >>> 48);
                result[j + 2] = (byte)(compliance >>> 40);
                result[j + 3] = (byte)(compliance >>> 32);
                result[j + 4] = (byte)(compliance >>> 24);
                result[j + 5] = (byte)(compliance >>> 16);
                result[j + 6] = (byte)(compliance >>> 8);
                result[j + 7] = (byte)compliance;
            }
        }
        buildFileForTable(file, result);
    }
    
    private static final String[] buildFileForName(final String filename, String contents) {
        String[] result = new String[contents.length()];
        result[0] = null;
        int resultCount = 1;
        final StringBuffer buffer = new StringBuffer();
        int start = contents.indexOf("name[]");
        start = contents.indexOf(34, start);
        final int end = contents.indexOf("};", start);
        contents = contents.substring(start, end);
        boolean addLineSeparator = false;
        int tokenStart = -1;
        StringBuffer currentToken = new StringBuffer();
        for (int i = 0; i < contents.length(); ++i) {
            final char c = contents.charAt(i);
            if (c == '\"') {
                if (tokenStart == -1) {
                    tokenStart = i + 1;
                }
                else {
                    if (addLineSeparator) {
                        buffer.append('\n');
                        result[resultCount++] = currentToken.toString();
                        currentToken = new StringBuffer();
                    }
                    String token = contents.substring(tokenStart, i);
                    if (token.equals("$error")) {
                        token = "Invalid Character";
                    }
                    else if (token.equals("$eof")) {
                        token = "Unexpected End Of File";
                    }
                    buffer.append(token);
                    currentToken.append(token);
                    addLineSeparator = true;
                    tokenStart = -1;
                }
            }
            if (tokenStart == -1 && c == '+') {
                addLineSeparator = false;
            }
        }
        if (currentToken.length() > 0) {
            result[resultCount++] = currentToken.toString();
        }
        buildFileForTable(filename, buffer.toString().toCharArray());
        System.arraycopy(result, 0, result = new String[resultCount], 0, resultCount);
        return result;
    }
    
    private static void buildFileForReadableName(final String file, final char[] newLhs, final char[] newNonTerminalIndex, final String[] newName, final String[] tokens) {
        final ArrayList entries = new ArrayList();
        final boolean[] alreadyAdded = new boolean[newName.length];
        for (int i = 0; i < tokens.length; i += 3) {
            if ("1".equals(tokens[i])) {
                final int index = newNonTerminalIndex[newLhs[Integer.parseInt(tokens[i + 1])]];
                final StringBuffer buffer = new StringBuffer();
                if (!alreadyAdded[index]) {
                    alreadyAdded[index] = true;
                    buffer.append(newName[index]);
                    buffer.append('=');
                    buffer.append(tokens[i + 2].trim());
                    buffer.append('\n');
                    entries.add(String.valueOf(buffer));
                }
            }
        }
        int i;
        for (i = 1; !"Invalid Character".equals(newName[i]); ++i) {}
        ++i;
        while (i < alreadyAdded.length) {
            if (!alreadyAdded[i]) {
                System.out.println(String.valueOf(newName[i]) + " has no readable name");
            }
            ++i;
        }
        Collections.sort((List<Comparable>)entries);
        buildFile(file, entries);
    }
    
    private static final void buildFileForTable(final String filename, final byte[] bytes) {
        FileOutputStream stream = null;
        Label_0062: {
            try {
                stream = new FileOutputStream(filename);
                stream.write(bytes);
            }
            catch (final IOException ex) {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (final IOException ex2) {}
                }
                break Label_0062;
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (final IOException ex3) {}
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (final IOException ex4) {}
            }
        }
        System.out.println(String.valueOf(filename) + " creation complete");
    }
    
    private static final void buildFileForTable(final String filename, final char[] chars) {
        final byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; ++i) {
            bytes[2 * i] = (byte)(chars[i] >>> 8);
            bytes[2 * i + 1] = (byte)(chars[i] & '\u00ff');
        }
        FileOutputStream stream = null;
        Label_0112: {
            try {
                stream = new FileOutputStream(filename);
                stream.write(bytes);
            }
            catch (final IOException ex) {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (final IOException ex2) {}
                }
                break Label_0112;
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (final IOException ex3) {}
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (final IOException ex4) {}
            }
        }
        System.out.println(String.valueOf(filename) + " creation complete");
    }
    
    private static final byte[] buildFileOfByteFor(final String filename, final String tag, final String[] tokens) {
        int i = 0;
        while (!tokens[i++].equals(tag)) {}
        byte[] bytes = new byte[tokens.length];
        int ic = 0;
        String token;
        while (!(token = tokens[i++]).equals("}")) {
            final int c = Integer.parseInt(token);
            bytes[ic++] = (byte)c;
        }
        System.arraycopy(bytes, 0, bytes = new byte[ic], 0, ic);
        buildFileForTable(filename, bytes);
        return bytes;
    }
    
    private static final char[] buildFileOfIntFor(final String filename, final String tag, final String[] tokens) {
        int i = 0;
        while (!tokens[i++].equals(tag)) {}
        char[] chars = new char[tokens.length];
        int ic = 0;
        String token;
        while (!(token = tokens[i++]).equals("}")) {
            final int c = Integer.parseInt(token);
            chars[ic++] = (char)c;
        }
        System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
        buildFileForTable(filename, chars);
        return chars;
    }
    
    private static final void buildFileOfShortFor(final String filename, final String tag, final String[] tokens) {
        int i = 0;
        while (!tokens[i++].equals(tag)) {}
        char[] chars = new char[tokens.length];
        int ic = 0;
        String token;
        while (!(token = tokens[i++]).equals("}")) {
            final int c = Integer.parseInt(token);
            chars[ic++] = (char)(c + 32768);
        }
        System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
        buildFileForTable(filename, chars);
    }
    
    private static void buildFilesForRecoveryTemplates(final String indexFilename, final String templatesFilename, final char[] newTerminalIndex, final char[] newNonTerminalIndex, final String[] newName, final char[] newLhs, final String[] tokens) {
        final int[] newReverse = computeReverseTable(newTerminalIndex, newNonTerminalIndex, newName);
        final char[] newRecoveyTemplatesIndex = new char[newNonTerminalIndex.length];
        char[] newRecoveyTemplates = new char[newNonTerminalIndex.length];
        int newRecoveyTemplatesPtr = 0;
        for (int i = 0; i < tokens.length; i += 3) {
            if ("3".equals(tokens[i])) {
                int length = newRecoveyTemplates.length;
                if (length == newRecoveyTemplatesPtr + 1) {
                    System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
                }
                newRecoveyTemplates[newRecoveyTemplatesPtr++] = '\0';
                final int index = newLhs[Integer.parseInt(tokens[i + 1])];
                newRecoveyTemplatesIndex[index] = (char)newRecoveyTemplatesPtr;
                final String token = tokens[i + 2].trim();
                final StringTokenizer st = new StringTokenizer(token, " ");
                final String[] terminalNames = new String[st.countTokens()];
                int t = 0;
                while (st.hasMoreTokens()) {
                    terminalNames[t++] = st.nextToken();
                }
                for (int j = 0; j < terminalNames.length; ++j) {
                    final int symbol = getSymbol(terminalNames[j], newName, newReverse);
                    if (symbol > -1) {
                        length = newRecoveyTemplates.length;
                        if (length == newRecoveyTemplatesPtr + 1) {
                            System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
                        }
                        newRecoveyTemplates[newRecoveyTemplatesPtr++] = (char)symbol;
                    }
                }
            }
        }
        newRecoveyTemplates[newRecoveyTemplatesPtr++] = '\0';
        System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[newRecoveyTemplatesPtr], 0, newRecoveyTemplatesPtr);
        buildFileForTable(indexFilename, newRecoveyTemplatesIndex);
        buildFileForTable(templatesFilename, newRecoveyTemplates);
    }
    
    private static void buildFilesForStatementsRecoveryFilter(final String filename, final char[] newNonTerminalIndex, final char[] newLhs, final String[] tokens) {
        final char[] newStatementsRecoveryFilter = new char[newNonTerminalIndex.length];
        for (int i = 0; i < tokens.length; i += 3) {
            if ("4".equals(tokens[i])) {
                final int index = newLhs[Integer.parseInt(tokens[i + 1])];
                newStatementsRecoveryFilter[index] = '\u0001';
            }
        }
        buildFileForTable(filename, newStatementsRecoveryFilter);
    }
    
    public static final void buildFilesFromLPG(final String dataFilename, final String dataFilename2) {
        char[] contents = CharOperation.NO_CHAR;
        try {
            contents = Util.getFileCharContent(new File(dataFilename), null);
        }
        catch (final IOException ex) {
            System.out.println(Messages.parser_incorrectPath);
            return;
        }
        StringTokenizer st = new StringTokenizer(new String(contents), " \t\n\r[]={,;");
        String[] tokens = new String[st.countTokens()];
        int j = 0;
        while (st.hasMoreTokens()) {
            tokens[j++] = st.nextToken();
        }
        int i = 0;
        final char[] newLhs = buildFileOfIntFor("parser" + ++i + ".rsc", "lhs", tokens);
        buildFileOfShortFor("parser" + ++i + ".rsc", "check_table", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "asb", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "asr", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "nasb", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "nasr", tokens);
        final char[] newTerminalIndex = buildFileOfIntFor("parser" + ++i + ".rsc", "terminal_index", tokens);
        final char[] newNonTerminalIndex = buildFileOfIntFor("parser" + ++i + ".rsc", "non_terminal_index", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "term_action", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "scope_prefix", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "scope_suffix", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "scope_lhs", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "scope_state_set", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "scope_rhs", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "scope_state", tokens);
        buildFileOfIntFor("parser" + ++i + ".rsc", "in_symb", tokens);
        final byte[] newRhs = buildFileOfByteFor("parser" + ++i + ".rsc", "rhs", tokens);
        buildFileOfByteFor("parser" + ++i + ".rsc", "term_check", tokens);
        buildFileOfByteFor("parser" + ++i + ".rsc", "scope_la", tokens);
        final String[] newName = buildFileForName("parser" + ++i + ".rsc", new String(contents));
        contents = CharOperation.NO_CHAR;
        try {
            contents = Util.getFileCharContent(new File(dataFilename2), null);
        }
        catch (final IOException ex2) {
            System.out.println(Messages.parser_incorrectPath);
            return;
        }
        st = new StringTokenizer(new String(contents), "\t\n\r#");
        tokens = new String[st.countTokens()];
        j = 0;
        while (st.hasMoreTokens()) {
            tokens[j++] = st.nextToken();
        }
        buildFileForCompliance("parser" + ++i + ".rsc", newRhs.length, tokens);
        buildFileForReadableName("readableNames.props", newLhs, newNonTerminalIndex, newName, tokens);
        buildFilesForRecoveryTemplates("parser" + ++i + ".rsc", "parser" + ++i + ".rsc", newTerminalIndex, newNonTerminalIndex, newName, newLhs, tokens);
        buildFilesForStatementsRecoveryFilter("parser" + ++i + ".rsc", newNonTerminalIndex, newLhs, tokens);
        System.out.println(Messages.parser_moveFiles);
    }
    
    protected static int[] computeReverseTable(final char[] newTerminalIndex, final char[] newNonTerminalIndex, final String[] newName) {
        final int[] newReverseTable = new int[newName.length];
        int j = 0;
    Label_0083:
        while (j < newName.length) {
            while (true) {
                for (int k = 0; k < newTerminalIndex.length; ++k) {
                    if (newTerminalIndex[k] == j) {
                        newReverseTable[j] = k;
                        ++j;
                        continue Label_0083;
                    }
                }
                for (int k = 0; k < newNonTerminalIndex.length; ++k) {
                    if (newNonTerminalIndex[k] == j) {
                        newReverseTable[j] = -k;
                        break;
                    }
                }
                continue;
            }
        }
        return newReverseTable;
    }
    
    private static int getSymbol(final String terminalName, final String[] newName, final int[] newReverse) {
        for (int j = 0; j < newName.length; ++j) {
            if (terminalName.equals(newName[j])) {
                return newReverse[j];
            }
        }
        return -1;
    }
    
    public static int in_symbol(final int state) {
        return Parser.in_symb[original_state(state)];
    }
    
    public static final void initTables() throws IOException {
        int i = 0;
        Parser.lhs = readTable("parser" + ++i + ".rsc");
        final char[] chars = readTable("parser" + ++i + ".rsc");
        Parser.check_table = new short[chars.length];
        int c = chars.length;
        while (c-- > 0) {
            Parser.check_table[c] = (short)(chars[c] - '\u8000');
        }
        Parser.asb = readTable("parser" + ++i + ".rsc");
        Parser.asr = readTable("parser" + ++i + ".rsc");
        Parser.nasb = readTable("parser" + ++i + ".rsc");
        Parser.nasr = readTable("parser" + ++i + ".rsc");
        Parser.terminal_index = readTable("parser" + ++i + ".rsc");
        Parser.non_terminal_index = readTable("parser" + ++i + ".rsc");
        Parser.term_action = readTable("parser" + ++i + ".rsc");
        Parser.scope_prefix = readTable("parser" + ++i + ".rsc");
        Parser.scope_suffix = readTable("parser" + ++i + ".rsc");
        Parser.scope_lhs = readTable("parser" + ++i + ".rsc");
        Parser.scope_state_set = readTable("parser" + ++i + ".rsc");
        Parser.scope_rhs = readTable("parser" + ++i + ".rsc");
        Parser.scope_state = readTable("parser" + ++i + ".rsc");
        Parser.in_symb = readTable("parser" + ++i + ".rsc");
        Parser.rhs = readByteTable("parser" + ++i + ".rsc");
        Parser.term_check = readByteTable("parser" + ++i + ".rsc");
        Parser.scope_la = readByteTable("parser" + ++i + ".rsc");
        Parser.name = readNameTable("parser" + ++i + ".rsc");
        Parser.rules_compliance = readLongTable("parser" + ++i + ".rsc");
        Parser.readableName = readReadableNameTable("readableNames.props");
        Parser.reverse_index = computeReverseTable(Parser.terminal_index, Parser.non_terminal_index, Parser.name);
        Parser.recovery_templates_index = readTable("parser" + ++i + ".rsc");
        Parser.recovery_templates = readTable("parser" + ++i + ".rsc");
        Parser.statements_recovery_filter = readTable("parser" + ++i + ".rsc");
        Parser.base_action = Parser.lhs;
    }
    
    public static int nasi(final int state) {
        return Parser.nasb[original_state(state)];
    }
    
    public static int ntAction(final int state, final int sym) {
        return Parser.base_action[state + sym];
    }
    
    protected static int original_state(final int state) {
        return -base_check(state);
    }
    
    protected static byte[] readByteTable(final String filename) throws IOException {
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        }
        finally {
            try {
                stream.close();
            }
            catch (final IOException ex) {}
        }
        try {
            stream.close();
        }
        catch (final IOException ex2) {}
        return bytes;
    }
    
    protected static long[] readLongTable(final String filename) throws IOException {
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        }
        finally {
            try {
                stream.close();
            }
            catch (final IOException ex) {}
        }
        try {
            stream.close();
        }
        catch (final IOException ex2) {}
        final int length = bytes.length;
        if (length % 8 != 0) {
            throw new IOException(Messages.bind(Messages.parser_corruptedFile, filename));
        }
        final long[] longs = new long[length / 8];
        int i = 0;
        int longIndex = 0;
        do {
            longs[longIndex++] = ((long)(bytes[i++] & 0xFF) << 56) + ((long)(bytes[i++] & 0xFF) << 48) + ((long)(bytes[i++] & 0xFF) << 40) + ((long)(bytes[i++] & 0xFF) << 32) + ((long)(bytes[i++] & 0xFF) << 24) + ((long)(bytes[i++] & 0xFF) << 16) + ((long)(bytes[i++] & 0xFF) << 8) + (bytes[i++] & 0xFF);
        } while (i != length);
        return longs;
    }
    
    protected static String[] readNameTable(final String filename) throws IOException {
        final char[] contents = readTable(filename);
        final char[][] nameAsChar = CharOperation.splitOn('\n', contents);
        final String[] result = new String[nameAsChar.length + 1];
        result[0] = null;
        for (int i = 0; i < nameAsChar.length; ++i) {
            result[i + 1] = new String(nameAsChar[i]);
        }
        return result;
    }
    
    protected static String[] readReadableNameTable(final String filename) {
        String[] result = new String[Parser.name.length];
        final InputStream is = Parser.class.getResourceAsStream(filename);
        final Properties props = new Properties();
        try {
            props.load(is);
        }
        catch (final IOException ex) {
            result = Parser.name;
            return result;
        }
        for (int i = 0; i < 119; ++i) {
            result[i] = Parser.name[i];
        }
        for (int i = 118; i < Parser.name.length; ++i) {
            final String n = props.getProperty(Parser.name[i]);
            if (n != null && n.length() > 0) {
                result[i] = n;
            }
            else {
                result[i] = Parser.name[i];
            }
        }
        return result;
    }
    
    protected static char[] readTable(final String filename) throws IOException {
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        }
        finally {
            try {
                stream.close();
            }
            catch (final IOException ex) {}
        }
        try {
            stream.close();
        }
        catch (final IOException ex2) {}
        final int length = bytes.length;
        if ((length & 0x1) != 0x0) {
            throw new IOException(Messages.bind(Messages.parser_corruptedFile, filename));
        }
        final char[] chars = new char[length / 2];
        int i = 0;
        int charIndex = 0;
        do {
            chars[charIndex++] = (char)(((bytes[i++] & 0xFF) << 8) + (bytes[i++] & 0xFF));
        } while (i != length);
        return chars;
    }
    
    public static int tAction(final int state, final int sym) {
        return Parser.term_action[(Parser.term_check[Parser.base_action[state] + sym] == sym) ? (Parser.base_action[state] + sym) : Parser.base_action[state]];
    }
    
    public Parser() {
        this.astStack = new ASTNode[100];
        this.diet = false;
        this.dietInt = 0;
        this.expressionStack = new Expression[100];
        this.typeAnnotationStack = new Annotation[100];
        this.genericsIdentifiersLengthStack = new int[10];
        this.genericsLengthStack = new int[10];
        this.genericsStack = new ASTNode[10];
        this.lastErrorEndPositionBeforeRecovery = -1;
        this.colonColonStart = -1;
        this.forStartPosition = 0;
        this.noAstNodes = new ASTNode[100];
        this.noExpressions = new Expression[100];
        this.optimizeStringLiterals = true;
        this.reportOnlyOneSyntaxError = false;
        this.reportSyntaxErrorIsRequired = true;
        this.annotationRecoveryActivated = true;
        this.methodRecoveryActivated = false;
        this.statementRecoveryActivated = false;
        this.stack = new int[255];
        this.valueLambdaNestDepth = -1;
        this.stateStackLengthStack = new int[0];
        this.unstackedAct = 16382;
        this.haltOnSyntaxError = false;
        this.tolerateDefaultClassMethods = false;
        this.processingLambdaParameterList = false;
        this.expectTypeAnnotation = false;
        this.reparsingLambdaExpression = false;
    }
    
    public Parser(final ProblemReporter problemReporter, final boolean optimizeStringLiterals) {
        this.astStack = new ASTNode[100];
        this.diet = false;
        this.dietInt = 0;
        this.expressionStack = new Expression[100];
        this.typeAnnotationStack = new Annotation[100];
        this.genericsIdentifiersLengthStack = new int[10];
        this.genericsLengthStack = new int[10];
        this.genericsStack = new ASTNode[10];
        this.lastErrorEndPositionBeforeRecovery = -1;
        this.colonColonStart = -1;
        this.forStartPosition = 0;
        this.noAstNodes = new ASTNode[100];
        this.noExpressions = new Expression[100];
        this.optimizeStringLiterals = true;
        this.reportOnlyOneSyntaxError = false;
        this.reportSyntaxErrorIsRequired = true;
        this.annotationRecoveryActivated = true;
        this.methodRecoveryActivated = false;
        this.statementRecoveryActivated = false;
        this.stack = new int[255];
        this.valueLambdaNestDepth = -1;
        this.stateStackLengthStack = new int[0];
        this.unstackedAct = 16382;
        this.haltOnSyntaxError = false;
        this.tolerateDefaultClassMethods = false;
        this.processingLambdaParameterList = false;
        this.expectTypeAnnotation = false;
        this.reparsingLambdaExpression = false;
        this.problemReporter = problemReporter;
        this.options = problemReporter.options;
        this.optimizeStringLiterals = optimizeStringLiterals;
        this.initializeScanner();
        this.parsingJava8Plus = (this.options.sourceLevel >= 3407872L);
        this.astLengthStack = new int[50];
        this.expressionLengthStack = new int[30];
        this.typeAnnotationLengthStack = new int[30];
        this.intStack = new int[50];
        this.identifierStack = new char[30][];
        this.identifierLengthStack = new int[30];
        this.nestedMethod = new int[30];
        this.realBlockStack = new int[30];
        this.identifierPositionStack = new long[30];
        this.variablesCounter = new int[30];
        this.javadocParser = this.createJavadocParser();
    }
    
    protected void annotationRecoveryCheckPoint(final int start, final int end) {
        if (this.lastCheckPoint < end) {
            this.lastCheckPoint = end + 1;
        }
    }
    
    public void arrayInitializer(final int length) {
        final ArrayInitializer ai = new ArrayInitializer();
        if (length != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ai.expressions = new Expression[length], 0, length);
        }
        this.pushOnExpressionStack(ai);
        ai.sourceEnd = this.endStatementPosition;
        ai.sourceStart = this.intStack[this.intPtr--];
    }
    
    protected void blockReal() {
        final int[] realBlockStack = this.realBlockStack;
        final int realBlockPtr = this.realBlockPtr;
        ++realBlockStack[realBlockPtr];
    }
    
    public RecoveredElement buildInitialRecoveryState() {
        this.lastCheckPoint = 0;
        this.lastErrorEndPositionBeforeRecovery = this.scanner.currentPosition;
        RecoveredElement element = null;
        if (this.referenceContext instanceof CompilationUnitDeclaration) {
            element = new RecoveredUnit(this.compilationUnit, 0, this);
            this.compilationUnit.currentPackage = null;
            this.compilationUnit.imports = null;
            this.compilationUnit.types = null;
            this.currentToken = 0;
            this.listLength = 0;
            this.listTypeParameterLength = 0;
            this.endPosition = 0;
            this.endStatementPosition = 0;
            return element;
        }
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            element = new RecoveredMethod((AbstractMethodDeclaration)this.referenceContext, null, 0, this);
            this.lastCheckPoint = ((AbstractMethodDeclaration)this.referenceContext).bodyStart;
            if (this.statementRecoveryActivated) {
                element = element.add(new Block(0), 0);
            }
        }
        else if (this.referenceContext instanceof TypeDeclaration) {
            final TypeDeclaration type = (TypeDeclaration)this.referenceContext;
            final FieldDeclaration[] fieldDeclarations = type.fields;
            for (int length = (fieldDeclarations == null) ? 0 : fieldDeclarations.length, i = 0; i < length; ++i) {
                final FieldDeclaration field = fieldDeclarations[i];
                if (field != null && field.getKind() == 2 && ((Initializer)field).block != null && field.declarationSourceStart <= this.scanner.initialPosition && this.scanner.initialPosition <= field.declarationSourceEnd && this.scanner.eofPosition <= field.declarationSourceEnd + 1) {
                    element = new RecoveredInitializer(field, null, 1, this);
                    this.lastCheckPoint = field.declarationSourceStart;
                    break;
                }
            }
        }
        if (element == null) {
            return element;
        }
        for (int j = 0; j <= this.astPtr; ++j) {
            final ASTNode node = this.astStack[j];
            if (node instanceof AbstractMethodDeclaration) {
                final AbstractMethodDeclaration method = (AbstractMethodDeclaration)node;
                if (method.declarationSourceEnd == 0) {
                    element = element.add(method, 0);
                    this.lastCheckPoint = method.bodyStart;
                }
                else {
                    element = element.add(method, 0);
                    this.lastCheckPoint = method.declarationSourceEnd + 1;
                }
            }
            else if (node instanceof Initializer) {
                final Initializer initializer = (Initializer)node;
                if (initializer.block != null) {
                    if (initializer.declarationSourceEnd == 0) {
                        element = element.add(initializer, 1);
                        this.lastCheckPoint = initializer.sourceStart;
                    }
                    else {
                        element = element.add(initializer, 0);
                        this.lastCheckPoint = initializer.declarationSourceEnd + 1;
                    }
                }
            }
            else if (node instanceof FieldDeclaration) {
                final FieldDeclaration field2 = (FieldDeclaration)node;
                if (field2.declarationSourceEnd == 0) {
                    element = element.add(field2, 0);
                    if (field2.initialization == null) {
                        this.lastCheckPoint = field2.sourceEnd + 1;
                    }
                    else {
                        this.lastCheckPoint = field2.initialization.sourceEnd + 1;
                    }
                }
                else {
                    element = element.add(field2, 0);
                    this.lastCheckPoint = field2.declarationSourceEnd + 1;
                }
            }
            else if (node instanceof TypeDeclaration) {
                final TypeDeclaration type2 = (TypeDeclaration)node;
                if ((type2.modifiers & 0x4000) == 0x0) {
                    if (type2.declarationSourceEnd == 0) {
                        element = element.add(type2, 0);
                        this.lastCheckPoint = type2.bodyStart;
                    }
                    else {
                        element = element.add(type2, 0);
                        this.lastCheckPoint = type2.declarationSourceEnd + 1;
                    }
                }
            }
            else {
                if (node instanceof ImportReference) {
                    final ImportReference importRef = (ImportReference)node;
                    element = element.add(importRef, 0);
                    this.lastCheckPoint = importRef.declarationSourceEnd + 1;
                }
                if (this.statementRecoveryActivated) {
                    if (node instanceof Block) {
                        final Block block = (Block)node;
                        element = element.add(block, 0);
                        this.lastCheckPoint = block.sourceEnd + 1;
                    }
                    else if (node instanceof LocalDeclaration) {
                        final LocalDeclaration statement = (LocalDeclaration)node;
                        element = element.add(statement, 0);
                        this.lastCheckPoint = statement.sourceEnd + 1;
                    }
                    else if (node instanceof Expression) {
                        if (node instanceof Assignment || node instanceof PrefixExpression || node instanceof PostfixExpression || node instanceof MessageSend || node instanceof AllocationExpression) {
                            final Expression statement2 = (Expression)node;
                            element = element.add(statement2, 0);
                            if (statement2.statementEnd != -1) {
                                this.lastCheckPoint = statement2.statementEnd + 1;
                            }
                            else {
                                this.lastCheckPoint = statement2.sourceEnd + 1;
                            }
                        }
                    }
                    else if (node instanceof Statement) {
                        final Statement statement3 = (Statement)node;
                        element = element.add(statement3, 0);
                        this.lastCheckPoint = statement3.sourceEnd + 1;
                    }
                }
            }
        }
        if (this.statementRecoveryActivated && this.pendingRecoveredType != null && this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
            element = element.add(this.pendingRecoveredType, 0);
            this.lastCheckPoint = this.pendingRecoveredType.declarationSourceEnd + 1;
            this.pendingRecoveredType = null;
        }
        return element;
    }
    
    protected void checkAndSetModifiers(final int flag) {
        if ((this.modifiers & flag) != 0x0) {
            this.modifiers |= 0x400000;
        }
        this.modifiers |= flag;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = this.scanner.startPosition;
        }
        if (this.currentElement != null) {
            this.currentElement.addModifier(flag, this.modifiersSourceStart);
        }
    }
    
    public void checkComment() {
        if ((!this.diet || this.dietInt != 0) && this.scanner.commentPtr >= 0) {
            this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        }
        int lastComment = this.scanner.commentPtr;
        if (this.modifiersSourceStart >= 0) {
            while (lastComment >= 0) {
                int commentSourceStart = this.scanner.commentStarts[lastComment];
                if (commentSourceStart < 0) {
                    commentSourceStart = -commentSourceStart;
                }
                if (commentSourceStart <= this.modifiersSourceStart) {
                    break;
                }
                --lastComment;
            }
        }
        if (lastComment >= 0) {
            int lastCommentStart = this.scanner.commentStarts[0];
            if (lastCommentStart < 0) {
                lastCommentStart = -lastCommentStart;
            }
            if (this.forStartPosition != 0 || this.forStartPosition < lastCommentStart) {
                this.modifiersSourceStart = lastCommentStart;
            }
            while (lastComment >= 0 && this.scanner.commentStops[lastComment] < 0) {
                --lastComment;
            }
            if (lastComment >= 0 && this.javadocParser != null) {
                final int commentEnd = this.scanner.commentStops[lastComment] - 1;
                if (this.javadocParser.shouldReportProblems) {
                    this.javadocParser.reportProblems = (this.currentElement == null || commentEnd > this.lastJavadocEnd);
                }
                else {
                    this.javadocParser.reportProblems = false;
                }
                if (this.javadocParser.checkDeprecation(lastComment)) {
                    this.checkAndSetModifiers(1048576);
                }
                this.javadoc = this.javadocParser.docComment;
                if (this.currentElement == null) {
                    this.lastJavadocEnd = commentEnd;
                }
            }
        }
    }
    
    protected void checkNonNLSAfterBodyEnd(final int declarationEnd) {
        if (this.scanner.currentPosition - 1 <= declarationEnd) {
            this.scanner.eofPosition = ((declarationEnd < Integer.MAX_VALUE) ? (declarationEnd + 1) : declarationEnd);
            try {
                while (this.scanner.getNextToken() != 60) {}
            }
            catch (final InvalidInputException ex) {}
        }
    }
    
    protected void classInstanceCreation(final boolean isQualified) {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 1 && this.astStack[this.astPtr] == null) {
            --this.astPtr;
            AllocationExpression alloc;
            if (isQualified) {
                alloc = new QualifiedAllocationExpression();
            }
            else {
                alloc = new AllocationExpression();
            }
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[length], 0, length);
            }
            this.checkForDiamond(alloc.type = this.getTypeReference(0));
            alloc.sourceStart = this.intStack[this.intPtr--];
            this.pushOnExpressionStack(alloc);
        }
        else {
            this.dispatchDeclarationInto(length);
            final TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (anonymousTypeDeclaration.allocation != null) {
                anonymousTypeDeclaration.allocation.sourceEnd = this.endStatementPosition;
                this.checkForDiamond(anonymousTypeDeclaration.allocation.type);
            }
            if (length == 0 && !this.containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                final TypeDeclaration typeDeclaration = anonymousTypeDeclaration;
                typeDeclaration.bits |= 0x8;
            }
            --this.astPtr;
            --this.astLengthPtr;
        }
    }
    
    protected void checkForDiamond(final TypeReference allocType) {
        if (allocType instanceof ParameterizedSingleTypeReference) {
            final ParameterizedSingleTypeReference type = (ParameterizedSingleTypeReference)allocType;
            if (type.typeArguments == TypeReference.NO_TYPE_ARGUMENTS) {
                if (this.options.sourceLevel < 3342336L) {
                    this.problemReporter().diamondNotBelow17(allocType);
                }
                if (this.options.sourceLevel > 3145728L) {
                    final ParameterizedSingleTypeReference parameterizedSingleTypeReference = type;
                    parameterizedSingleTypeReference.bits |= 0x80000;
                }
            }
        }
        else if (allocType instanceof ParameterizedQualifiedTypeReference) {
            final ParameterizedQualifiedTypeReference type2 = (ParameterizedQualifiedTypeReference)allocType;
            if (type2.typeArguments[type2.typeArguments.length - 1] == TypeReference.NO_TYPE_ARGUMENTS) {
                if (this.options.sourceLevel < 3342336L) {
                    this.problemReporter().diamondNotBelow17(allocType, type2.typeArguments.length - 1);
                }
                if (this.options.sourceLevel > 3145728L) {
                    final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = type2;
                    parameterizedQualifiedTypeReference.bits |= 0x80000;
                }
            }
        }
    }
    
    protected ParameterizedQualifiedTypeReference computeQualifiedGenericsFromRightSide(final TypeReference rightSide, final int dim, final Annotation[][] annotationsOnDimensions) {
        int tokensSize;
        int nameSize = tokensSize = this.identifierLengthStack[this.identifierLengthPtr];
        if (rightSide instanceof ParameterizedSingleTypeReference) {
            ++tokensSize;
        }
        else if (rightSide instanceof SingleTypeReference) {
            ++tokensSize;
        }
        else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
            tokensSize += ((QualifiedTypeReference)rightSide).tokens.length;
        }
        else if (rightSide instanceof QualifiedTypeReference) {
            tokensSize += ((QualifiedTypeReference)rightSide).tokens.length;
        }
        final TypeReference[][] typeArguments = new TypeReference[tokensSize][];
        final char[][] tokens = new char[tokensSize][];
        final long[] positions = new long[tokensSize];
        Annotation[][] typeAnnotations = null;
        if (rightSide instanceof ParameterizedSingleTypeReference) {
            final ParameterizedSingleTypeReference singleParameterizedTypeReference = (ParameterizedSingleTypeReference)rightSide;
            tokens[nameSize] = singleParameterizedTypeReference.token;
            positions[nameSize] = ((long)singleParameterizedTypeReference.sourceStart << 32) + singleParameterizedTypeReference.sourceEnd;
            typeArguments[nameSize] = singleParameterizedTypeReference.typeArguments;
            if (singleParameterizedTypeReference.annotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                typeAnnotations[nameSize] = singleParameterizedTypeReference.annotations[0];
            }
        }
        else if (rightSide instanceof SingleTypeReference) {
            final SingleTypeReference singleTypeReference = (SingleTypeReference)rightSide;
            tokens[nameSize] = singleTypeReference.token;
            positions[nameSize] = ((long)singleTypeReference.sourceStart << 32) + singleTypeReference.sourceEnd;
            if (singleTypeReference.annotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                typeAnnotations[nameSize] = singleTypeReference.annotations[0];
            }
        }
        else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
            final ParameterizedQualifiedTypeReference parameterizedTypeReference = (ParameterizedQualifiedTypeReference)rightSide;
            final TypeReference[][] rightSideTypeArguments = parameterizedTypeReference.typeArguments;
            System.arraycopy(rightSideTypeArguments, 0, typeArguments, nameSize, rightSideTypeArguments.length);
            final char[][] rightSideTokens = parameterizedTypeReference.tokens;
            System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
            final long[] rightSidePositions = parameterizedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
            final Annotation[][] rightSideAnnotations = parameterizedTypeReference.annotations;
            if (rightSideAnnotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                System.arraycopy(rightSideAnnotations, 0, typeAnnotations, nameSize, rightSideAnnotations.length);
            }
        }
        else if (rightSide instanceof QualifiedTypeReference) {
            final QualifiedTypeReference qualifiedTypeReference = (QualifiedTypeReference)rightSide;
            final char[][] rightSideTokens2 = qualifiedTypeReference.tokens;
            System.arraycopy(rightSideTokens2, 0, tokens, nameSize, rightSideTokens2.length);
            final long[] rightSidePositions2 = qualifiedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions2, 0, positions, nameSize, rightSidePositions2.length);
            final Annotation[][] rightSideAnnotations2 = qualifiedTypeReference.annotations;
            if (rightSideAnnotations2 != null) {
                typeAnnotations = new Annotation[tokensSize][];
                System.arraycopy(rightSideAnnotations2, 0, typeAnnotations, nameSize, rightSideAnnotations2.length);
            }
        }
        final int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        final TypeReference[] currentTypeArguments = new TypeReference[currentTypeArgumentsLength];
        this.genericsPtr -= currentTypeArgumentsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, currentTypeArguments, 0, currentTypeArgumentsLength);
        if (nameSize == 1) {
            tokens[0] = this.identifierStack[this.identifierPtr];
            positions[0] = this.identifierPositionStack[this.identifierPtr--];
            typeArguments[0] = currentTypeArguments;
        }
        else {
            this.identifierPtr -= nameSize;
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, nameSize);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, nameSize);
            typeArguments[nameSize - 1] = currentTypeArguments;
        }
        --this.identifierLengthPtr;
        final ParameterizedQualifiedTypeReference typeRef = new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, annotationsOnDimensions, positions);
        while (nameSize > 0) {
            final int length;
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                if (typeAnnotations == null) {
                    typeAnnotations = new Annotation[tokensSize][];
                }
                final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
                final int typeAnnotationPtr = this.typeAnnotationPtr - length;
                this.typeAnnotationPtr = typeAnnotationPtr;
                System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, typeAnnotations[nameSize - 1] = new Annotation[length], 0, length);
                if (nameSize == 1) {
                    typeRef.sourceStart = typeAnnotations[0][0].sourceStart;
                }
            }
            --nameSize;
        }
        if ((typeRef.annotations = typeAnnotations) != null) {
            final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = typeRef;
            parameterizedQualifiedTypeReference.bits |= 0x100000;
        }
        return typeRef;
    }
    
    protected void concatExpressionLists() {
        final int[] expressionLengthStack = this.expressionLengthStack;
        final int expressionLengthPtr = this.expressionLengthPtr - 1;
        expressionLengthStack[this.expressionLengthPtr = expressionLengthPtr] = expressionLengthStack[expressionLengthPtr] + 1;
    }
    
    protected void concatGenericsLists() {
        final int[] genericsLengthStack = this.genericsLengthStack;
        final int n = this.genericsLengthPtr - 1;
        genericsLengthStack[n] += this.genericsLengthStack[this.genericsLengthPtr--];
    }
    
    protected void concatNodeLists() {
        final int[] astLengthStack = this.astLengthStack;
        final int n = this.astLengthPtr - 1;
        astLengthStack[n] += this.astLengthStack[this.astLengthPtr--];
    }
    
    protected void consumeAdditionalBound() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }
    
    protected void consumeAdditionalBound1() {
    }
    
    protected void consumeAdditionalBoundList() {
        this.concatGenericsLists();
    }
    
    protected void consumeAdditionalBoundList1() {
        this.concatGenericsLists();
    }
    
    protected boolean isIndirectlyInsideLambdaExpression() {
        return false;
    }
    
    protected void consumeAllocationHeader() {
        if (this.currentElement == null) {
            return;
        }
        if (this.currentToken == 49) {
            final TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
            anonymousType.name = CharOperation.NO_CHAR;
            final TypeDeclaration typeDeclaration = anonymousType;
            typeDeclaration.bits |= 0x300;
            anonymousType.sourceStart = this.intStack[this.intPtr--];
            anonymousType.declarationSourceStart = anonymousType.sourceStart;
            anonymousType.sourceEnd = this.rParenPos;
            final QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
            alloc.type = this.getTypeReference(0);
            alloc.sourceStart = anonymousType.sourceStart;
            alloc.sourceEnd = anonymousType.sourceEnd;
            final TypeDeclaration typeDeclaration2 = anonymousType;
            final int currentPosition = this.scanner.currentPosition;
            typeDeclaration2.bodyStart = currentPosition;
            this.lastCheckPoint = currentPosition;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            this.lastIgnoredToken = -1;
            if (this.isIndirectlyInsideLambdaExpression()) {
                this.ignoreNextOpeningBrace = true;
            }
            else {
                this.currentToken = 0;
            }
            return;
        }
        this.lastCheckPoint = this.scanner.startPosition;
        this.restartRecovery = true;
    }
    
    protected void consumeAnnotationAsModifier() {
        final Expression expression = this.expressionStack[this.expressionPtr];
        final int sourceStart = expression.sourceStart;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = sourceStart;
        }
    }
    
    protected void consumeAnnotationName() {
        if (this.currentElement != null && !this.expectTypeAnnotation) {
            final int start = this.intStack[this.intPtr];
            final int end = (int)(this.identifierPositionStack[this.identifierPtr] & 0xFFFFFFFFL);
            this.annotationRecoveryCheckPoint(start, end);
            if (this.annotationRecoveryActivated) {
                this.currentElement = this.currentElement.addAnnotationName(this.identifierPtr, this.identifierLengthPtr, start, 0);
            }
        }
        this.recordStringLiterals = false;
        this.expectTypeAnnotation = false;
    }
    
    protected void consumeAnnotationTypeDeclaration() {
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationInto(length);
        }
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.checkConstructors(this);
        if (this.scanner.containsAssertKeyword) {
            final TypeDeclaration typeDeclaration = typeDecl;
            typeDeclaration.bits |= 0x1;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            final TypeDeclaration typeDeclaration2 = typeDecl;
            typeDeclaration2.bits |= 0x8;
        }
        typeDecl.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }
    
    protected void consumeAnnotationTypeDeclarationHeader() {
        final TypeDeclaration annotationTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 49) {
            annotationTypeDeclaration.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }
    
    protected void consumeAnnotationTypeDeclarationHeaderName() {
        final TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                final TypeDeclaration typeDeclaration = annotationTypeDeclaration;
                typeDeclaration.bits |= 0x400;
            }
        }
        else {
            final TypeDeclaration typeDeclaration2 = annotationTypeDeclaration;
            typeDeclaration2.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        final long pos = this.identifierPositionStack[this.identifierPtr];
        annotationTypeDeclaration.sourceEnd = (int)pos;
        annotationTypeDeclaration.sourceStart = (int)(pos >>> 32);
        annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        --this.intPtr;
        --this.intPtr;
        annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        annotationTypeDeclaration.modifiers = (this.intStack[this.intPtr--] | 0x2000 | 0x200);
        if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
            annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
            --this.intPtr;
        }
        else {
            final int atPosition = this.intStack[this.intPtr--];
            annotationTypeDeclaration.declarationSourceStart = atPosition;
        }
        if ((annotationTypeDeclaration.bits & 0x400) == 0x0 && (annotationTypeDeclaration.bits & 0x100) == 0x0 && this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
            final TypeDeclaration typeDeclaration3 = annotationTypeDeclaration;
            typeDeclaration3.bits |= 0x1000;
        }
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, annotationTypeDeclaration.annotations = new Annotation[length], 0, length);
        }
        annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.sourceEnd + 1;
        annotationTypeDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
        this.pushOnAstStack(annotationTypeDeclaration);
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
    }
    
    protected void consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters() {
        final TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, annotationTypeDeclaration.typeParameters = new TypeParameter[length], 0, length);
        this.problemReporter().invalidUsageOfTypeParametersForAnnotationDeclaration(annotationTypeDeclaration);
        annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.typeParameters[length - 1].declarationSourceEnd + 1;
        this.listTypeParameterLength = 0;
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                final TypeDeclaration typeDeclaration = annotationTypeDeclaration;
                typeDeclaration.bits |= 0x400;
            }
        }
        else {
            final TypeDeclaration typeDeclaration2 = annotationTypeDeclaration;
            typeDeclaration2.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        final long pos = this.identifierPositionStack[this.identifierPtr];
        annotationTypeDeclaration.sourceEnd = (int)pos;
        annotationTypeDeclaration.sourceStart = (int)(pos >>> 32);
        annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        --this.intPtr;
        --this.intPtr;
        annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        annotationTypeDeclaration.modifiers = (this.intStack[this.intPtr--] | 0x2000 | 0x200);
        if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
            annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
            --this.intPtr;
        }
        else {
            final int atPosition = this.intStack[this.intPtr--];
            annotationTypeDeclaration.declarationSourceStart = atPosition;
        }
        if ((annotationTypeDeclaration.bits & 0x400) == 0x0 && (annotationTypeDeclaration.bits & 0x100) == 0x0 && this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
            final TypeDeclaration typeDeclaration3 = annotationTypeDeclaration;
            typeDeclaration3.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, annotationTypeDeclaration.annotations = new Annotation[length], 0, length);
        }
        annotationTypeDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
        this.pushOnAstStack(annotationTypeDeclaration);
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
    }
    
    protected void consumeAnnotationTypeMemberDeclaration() {
        final AnnotationMethodDeclaration annotationMethodDeclaration;
        final AnnotationMethodDeclaration annotationTypeMemberDeclaration = annotationMethodDeclaration = (AnnotationMethodDeclaration)this.astStack[this.astPtr];
        annotationMethodDeclaration.modifiers |= 0x1000000;
        final int declarationEndPosition = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        annotationTypeMemberDeclaration.bodyStart = this.endStatementPosition;
        annotationTypeMemberDeclaration.bodyEnd = declarationEndPosition;
        annotationTypeMemberDeclaration.declarationSourceEnd = declarationEndPosition;
    }
    
    protected void consumeAnnotationTypeMemberDeclarations() {
        this.concatNodeLists();
    }
    
    protected void consumeAnnotationTypeMemberDeclarationsopt() {
        --this.nestedType;
    }
    
    protected void consumeArgumentList() {
        this.concatExpressionLists();
    }
    
    protected void consumeArguments() {
        this.pushOnIntStack(this.rParenPos);
    }
    
    protected void consumeArrayAccess(final boolean unspecifiedReference) {
        Expression exp;
        if (unspecifiedReference) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr;
            final Expression expression = new ArrayReference(this.getUnspecifiedReferenceOptimized(), this.expressionStack[this.expressionPtr]);
            expressionStack[expressionPtr] = expression;
            exp = expression;
        }
        else {
            --this.expressionPtr;
            --this.expressionLengthPtr;
            final Expression[] expressionStack2 = this.expressionStack;
            final int expressionPtr2 = this.expressionPtr;
            final Expression expression2 = new ArrayReference(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1]);
            expressionStack2[expressionPtr2] = expression2;
            exp = expression2;
        }
        exp.sourceEnd = this.endStatementPosition;
    }
    
    protected void consumeArrayCreationExpressionWithInitializer() {
        final ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
        --this.expressionLengthPtr;
        arrayAllocation.initializer = (ArrayInitializer)this.expressionStack[this.expressionPtr--];
        final int length = this.expressionLengthStack[this.expressionLengthPtr--];
        this.expressionPtr -= length;
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, arrayAllocation.dimensions = new Expression[length], 0, length);
        final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(length);
        arrayAllocation.annotationsOnDimensions = annotationsOnDimensions;
        arrayAllocation.type = this.getTypeReference(0);
        final TypeReference type = arrayAllocation.type;
        type.bits |= 0x40000000;
        if (annotationsOnDimensions != null) {
            final ArrayAllocationExpression arrayAllocationExpression = arrayAllocation;
            arrayAllocationExpression.bits |= 0x100000;
            final TypeReference type2 = arrayAllocation.type;
            type2.bits |= 0x100000;
        }
        arrayAllocation.sourceStart = this.intStack[this.intPtr--];
        if (arrayAllocation.initializer == null) {
            arrayAllocation.sourceEnd = this.endStatementPosition;
        }
        else {
            arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd;
        }
        this.pushOnExpressionStack(arrayAllocation);
    }
    
    protected void consumeArrayCreationExpressionWithoutInitializer() {
        final ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
        final int length = this.expressionLengthStack[this.expressionLengthPtr--];
        this.expressionPtr -= length;
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, arrayAllocation.dimensions = new Expression[length], 0, length);
        final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(length);
        arrayAllocation.annotationsOnDimensions = annotationsOnDimensions;
        arrayAllocation.type = this.getTypeReference(0);
        final TypeReference type = arrayAllocation.type;
        type.bits |= 0x40000000;
        if (annotationsOnDimensions != null) {
            final ArrayAllocationExpression arrayAllocationExpression = arrayAllocation;
            arrayAllocationExpression.bits |= 0x100000;
            final TypeReference type2 = arrayAllocation.type;
            type2.bits |= 0x100000;
        }
        arrayAllocation.sourceStart = this.intStack[this.intPtr--];
        if (arrayAllocation.initializer == null) {
            arrayAllocation.sourceEnd = this.endStatementPosition;
        }
        else {
            arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd;
        }
        this.pushOnExpressionStack(arrayAllocation);
    }
    
    protected void consumeArrayCreationHeader() {
    }
    
    protected void consumeArrayInitializer() {
        this.arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
    }
    
    protected void consumeArrayTypeWithTypeArgumentsName() {
        final int[] genericsIdentifiersLengthStack = this.genericsIdentifiersLengthStack;
        final int genericsIdentifiersLengthPtr = this.genericsIdentifiersLengthPtr;
        genericsIdentifiersLengthStack[genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
        this.pushOnGenericsLengthStack(0);
    }
    
    protected void consumeAssertStatement() {
        this.expressionLengthPtr -= 2;
        this.pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }
    
    protected void consumeAssignment() {
        final int op = this.intStack[this.intPtr--];
        --this.expressionPtr;
        --this.expressionLengthPtr;
        final Expression expression = this.expressionStack[this.expressionPtr + 1];
        this.expressionStack[this.expressionPtr] = ((op != 30) ? new CompoundAssignment(this.expressionStack[this.expressionPtr], expression, op, expression.sourceEnd) : new Assignment(this.expressionStack[this.expressionPtr], expression, expression.sourceEnd));
        if (this.pendingRecoveredType != null) {
            if (this.pendingRecoveredType.allocation != null && this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
                this.expressionStack[this.expressionPtr] = this.pendingRecoveredType.allocation;
                this.pendingRecoveredType = null;
                return;
            }
            this.pendingRecoveredType = null;
        }
    }
    
    protected void consumeAssignmentOperator(final int pos) {
        this.pushOnIntStack(pos);
    }
    
    protected void consumeBinaryExpression(final int op) {
        --this.expressionPtr;
        --this.expressionLengthPtr;
        final Expression expr1 = this.expressionStack[this.expressionPtr];
        final Expression expr2 = this.expressionStack[this.expressionPtr + 1];
        switch (op) {
            case 1: {
                this.expressionStack[this.expressionPtr] = new OR_OR_Expression(expr1, expr2, op);
                break;
            }
            case 0: {
                this.expressionStack[this.expressionPtr] = new AND_AND_Expression(expr1, expr2, op);
                break;
            }
            case 14: {
                if (this.optimizeStringLiterals) {
                    if (expr1 instanceof StringLiteral) {
                        if ((expr1.bits & 0x1FE00000) >> 21 != 0) {
                            this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                            break;
                        }
                        if (expr2 instanceof CharLiteral) {
                            this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((CharLiteral)expr2);
                            break;
                        }
                        if (expr2 instanceof StringLiteral) {
                            this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((StringLiteral)expr2);
                            break;
                        }
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                        break;
                    }
                    else {
                        if (expr1 instanceof CombinedBinaryExpression) {
                            final CombinedBinaryExpression cursor;
                            if ((cursor = (CombinedBinaryExpression)expr1).arity < cursor.arityMax) {
                                cursor.left = new BinaryExpression(cursor);
                                final CombinedBinaryExpression combinedBinaryExpression = cursor;
                                ++combinedBinaryExpression.arity;
                            }
                            else {
                                cursor.left = new CombinedBinaryExpression(cursor);
                                cursor.arity = 0;
                                cursor.tuneArityMax();
                            }
                            cursor.right = expr2;
                            cursor.sourceEnd = expr2.sourceEnd;
                            this.expressionStack[this.expressionPtr] = cursor;
                            break;
                        }
                        if (expr1 instanceof BinaryExpression && (expr1.bits & 0xFC0) >> 6 == 14) {
                            this.expressionStack[this.expressionPtr] = new CombinedBinaryExpression(expr1, expr2, 14, 1);
                            break;
                        }
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                        break;
                    }
                }
                else if (expr1 instanceof StringLiteral) {
                    if (expr2 instanceof StringLiteral && (expr1.bits & 0x1FE00000) >> 21 == 0) {
                        this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendsWith((StringLiteral)expr2);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                    break;
                }
                else {
                    if (expr1 instanceof CombinedBinaryExpression) {
                        final CombinedBinaryExpression cursor;
                        if ((cursor = (CombinedBinaryExpression)expr1).arity < cursor.arityMax) {
                            cursor.left = new BinaryExpression(cursor);
                            final CombinedBinaryExpression combinedBinaryExpression2 = cursor;
                            combinedBinaryExpression2.bits &= 0xE01FFFFF;
                            final CombinedBinaryExpression combinedBinaryExpression3 = cursor;
                            ++combinedBinaryExpression3.arity;
                        }
                        else {
                            cursor.left = new CombinedBinaryExpression(cursor);
                            final CombinedBinaryExpression combinedBinaryExpression4 = cursor;
                            combinedBinaryExpression4.bits &= 0xE01FFFFF;
                            cursor.arity = 0;
                            cursor.tuneArityMax();
                        }
                        cursor.right = expr2;
                        cursor.sourceEnd = expr2.sourceEnd;
                        this.expressionStack[this.expressionPtr] = cursor;
                        break;
                    }
                    if (expr1 instanceof BinaryExpression && (expr1.bits & 0xFC0) >> 6 == 14) {
                        this.expressionStack[this.expressionPtr] = new CombinedBinaryExpression(expr1, expr2, 14, 1);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                    break;
                }
                break;
            }
            case 4:
            case 15: {
                --this.intPtr;
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            }
            default: {
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            }
        }
    }
    
    protected void consumeBinaryExpressionWithName(final int op) {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        --this.expressionPtr;
        --this.expressionLengthPtr;
        final Expression expr1 = this.expressionStack[this.expressionPtr + 1];
        final Expression expr2 = this.expressionStack[this.expressionPtr];
        switch (op) {
            case 1: {
                this.expressionStack[this.expressionPtr] = new OR_OR_Expression(expr1, expr2, op);
                break;
            }
            case 0: {
                this.expressionStack[this.expressionPtr] = new AND_AND_Expression(expr1, expr2, op);
                break;
            }
            case 14: {
                if (this.optimizeStringLiterals) {
                    if (!(expr1 instanceof StringLiteral) || (expr1.bits & 0x1FE00000) >> 21 != 0) {
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                        break;
                    }
                    if (expr2 instanceof CharLiteral) {
                        this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((CharLiteral)expr2);
                        break;
                    }
                    if (expr2 instanceof StringLiteral) {
                        this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((StringLiteral)expr2);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                    break;
                }
                else {
                    if (!(expr1 instanceof StringLiteral)) {
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                        break;
                    }
                    if (expr2 instanceof StringLiteral && (expr1.bits & 0x1FE00000) >> 21 == 0) {
                        this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendsWith((StringLiteral)expr2);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                    break;
                }
                break;
            }
            case 4:
            case 15: {
                --this.intPtr;
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            }
            default: {
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            }
        }
    }
    
    protected void consumeBlock() {
        final int statementsLength = this.astLengthStack[this.astLengthPtr--];
        Block block;
        if (statementsLength == 0) {
            block = new Block(0);
            block.sourceStart = this.intStack[this.intPtr--];
            block.sourceEnd = this.endStatementPosition;
            if (!this.containsComment(block.sourceStart, block.sourceEnd)) {
                final Block block2 = block;
                block2.bits |= 0x8;
            }
            --this.realBlockPtr;
        }
        else {
            block = new Block(this.realBlockStack[this.realBlockPtr--]);
            this.astPtr -= statementsLength;
            System.arraycopy(this.astStack, this.astPtr + 1, block.statements = new Statement[statementsLength], 0, statementsLength);
            block.sourceStart = this.intStack[this.intPtr--];
            block.sourceEnd = this.endStatementPosition;
        }
        this.pushOnAstStack(block);
    }
    
    protected void consumeBlockStatement() {
    }
    
    protected void consumeBlockStatements() {
        this.concatNodeLists();
    }
    
    protected void consumeCaseLabel() {
        --this.expressionLengthPtr;
        final Expression expression = this.expressionStack[this.expressionPtr--];
        final CaseStatement caseStatement = new CaseStatement(expression, expression.sourceEnd, this.intStack[this.intPtr--]);
        if (this.hasLeadingTagComment(Parser.FALL_THROUGH_TAG, caseStatement.sourceStart)) {
            final CaseStatement caseStatement2 = caseStatement;
            caseStatement2.bits |= 0x20000000;
        }
        this.pushOnAstStack(caseStatement);
    }
    
    protected void consumeCastExpressionLL1() {
        --this.expressionPtr;
        final Expression exp;
        final Expression cast = this.expressionStack[this.expressionPtr] = new CastExpression(exp = this.expressionStack[this.expressionPtr + 1], (TypeReference)this.expressionStack[this.expressionPtr]);
        --this.expressionLengthPtr;
        this.updateSourcePosition(cast);
        cast.sourceEnd = exp.sourceEnd;
    }
    
    public IntersectionCastTypeReference createIntersectionCastTypeReference(final TypeReference[] typeReferences) {
        if (this.options.sourceLevel < 3407872L) {
            this.problemReporter().intersectionCastNotBelow18(typeReferences);
        }
        return new IntersectionCastTypeReference(typeReferences);
    }
    
    protected void consumeCastExpressionLL1WithBounds() {
        final Expression exp = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        final int length;
        final TypeReference[] bounds = new TypeReference[length = this.expressionLengthStack[this.expressionLengthPtr]];
        System.arraycopy(this.expressionStack, this.expressionPtr -= length - 1, bounds, 0, length);
        final Expression cast = this.expressionStack[this.expressionPtr] = new CastExpression(exp, this.createIntersectionCastTypeReference(bounds));
        this.expressionLengthStack[this.expressionLengthPtr] = 1;
        this.updateSourcePosition(cast);
        cast.sourceEnd = exp.sourceEnd;
    }
    
    protected void consumeCastExpressionWithGenericsArray() {
        TypeReference[] bounds = null;
        final int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        final int end = this.intStack[this.intPtr--];
        final int dim = this.intStack[this.intPtr--];
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        TypeReference castType;
        if (additionalBoundsLength > 0) {
            bounds[0] = this.getTypeReference(dim);
            castType = this.createIntersectionCastTypeReference(bounds);
        }
        else {
            castType = this.getTypeReference(dim);
        }
        final Expression exp;
        final Expression cast = this.expressionStack[this.expressionPtr] = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        --this.intPtr;
        castType.sourceEnd = end - 1;
        final TypeReference typeReference = castType;
        final Expression expression = cast;
        final int sourceStart = this.intStack[this.intPtr--];
        expression.sourceStart = sourceStart;
        typeReference.sourceStart = sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }
    
    protected void consumeCastExpressionWithNameArray() {
        final int end = this.intStack[this.intPtr--];
        TypeReference[] bounds = null;
        final int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        TypeReference castType;
        if (additionalBoundsLength > 0) {
            bounds[0] = this.getTypeReference(this.intStack[this.intPtr--]);
            castType = this.createIntersectionCastTypeReference(bounds);
        }
        else {
            castType = this.getTypeReference(this.intStack[this.intPtr--]);
        }
        final Expression exp;
        final Expression cast = this.expressionStack[this.expressionPtr] = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        castType.sourceEnd = end - 1;
        final TypeReference typeReference = castType;
        final Expression expression = cast;
        final int sourceStart = this.intStack[this.intPtr--];
        expression.sourceStart = sourceStart;
        typeReference.sourceStart = sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }
    
    protected void consumeCastExpressionWithPrimitiveType() {
        TypeReference[] bounds = null;
        final int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        final int end = this.intStack[this.intPtr--];
        TypeReference castType;
        if (additionalBoundsLength > 0) {
            bounds[0] = this.getTypeReference(this.intStack[this.intPtr--]);
            castType = this.createIntersectionCastTypeReference(bounds);
        }
        else {
            castType = this.getTypeReference(this.intStack[this.intPtr--]);
        }
        final Expression exp;
        final Expression cast = this.expressionStack[this.expressionPtr] = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        castType.sourceEnd = end - 1;
        final TypeReference typeReference = castType;
        final Expression expression = cast;
        final int sourceStart = this.intStack[this.intPtr--];
        expression.sourceStart = sourceStart;
        typeReference.sourceStart = sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }
    
    protected void consumeCastExpressionWithQualifiedGenericsArray() {
        TypeReference[] bounds = null;
        final int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        if (additionalBoundsLength > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        final int end = this.intStack[this.intPtr--];
        final int dim = this.intStack[this.intPtr--];
        final Annotation[][] annotationsOnDimensions = (Annotation[][])((dim == 0) ? null : this.getAnnotationsOnDimensions(dim));
        final TypeReference rightSide = this.getTypeReference(0);
        TypeReference castType = this.computeQualifiedGenericsFromRightSide(rightSide, dim, annotationsOnDimensions);
        if (additionalBoundsLength > 0) {
            bounds[0] = castType;
            castType = this.createIntersectionCastTypeReference(bounds);
        }
        --this.intPtr;
        final Expression exp;
        final Expression cast = this.expressionStack[this.expressionPtr] = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType);
        castType.sourceEnd = end - 1;
        final TypeReference typeReference = castType;
        final Expression expression = cast;
        final int sourceStart = this.intStack[this.intPtr--];
        expression.sourceStart = sourceStart;
        typeReference.sourceStart = sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }
    
    protected void consumeCatches() {
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeCatchFormalParameter() {
        --this.identifierLengthPtr;
        final char[] identifierName = this.identifierStack[this.identifierPtr];
        final long namePositions = this.identifierPositionStack[this.identifierPtr--];
        final int extendedDimensions = this.intStack[this.intPtr--];
        TypeReference type = (TypeReference)this.astStack[this.astPtr--];
        if (extendedDimensions > 0) {
            type = this.augmentTypeWithAdditionalDimensions(type, extendedDimensions, null, false);
            type.sourceEnd = this.endPosition;
            if (type instanceof UnionTypeReference) {
                this.problemReporter().illegalArrayOfUnionType(identifierName, type);
            }
        }
        --this.astLengthPtr;
        final int modifierPositions = this.intStack[this.intPtr--];
        --this.intPtr;
        final Argument argument;
        final Argument arg = argument = new Argument(identifierName, namePositions, type, this.intStack[this.intPtr + 1] & 0xFFEFFFFF);
        argument.bits &= 0xFFFFFFFB;
        arg.declarationSourceStart = modifierPositions;
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, arg.annotations = new Annotation[length], 0, length);
        }
        this.pushOnAstStack(arg);
        ++this.listLength;
    }
    
    protected void consumeCatchHeader() {
        if (this.currentElement == null) {
            return;
        }
        if (!(this.currentElement instanceof RecoveredBlock)) {
            if (!(this.currentElement instanceof RecoveredMethod)) {
                return;
            }
            final RecoveredMethod rMethod = (RecoveredMethod)this.currentElement;
            if (rMethod.methodBody != null || rMethod.bracketBalance <= 0) {
                return;
            }
        }
        final Argument arg = (Argument)this.astStack[this.astPtr--];
        final LocalDeclaration localDeclaration = new LocalDeclaration(arg.name, arg.sourceStart, arg.sourceEnd);
        localDeclaration.type = arg.type;
        localDeclaration.declarationSourceStart = arg.declarationSourceStart;
        localDeclaration.declarationSourceEnd = arg.declarationSourceEnd;
        this.currentElement = this.currentElement.add(localDeclaration, 0);
        this.lastCheckPoint = this.scanner.startPosition;
        this.restartRecovery = true;
        this.lastIgnoredToken = -1;
    }
    
    protected void consumeCatchType() {
        final int length = this.astLengthStack[this.astLengthPtr--];
        if (length != 1) {
            final ASTNode[] astStack = this.astStack;
            final int astPtr = this.astPtr - length;
            this.astPtr = astPtr;
            final TypeReference[] typeReferences;
            System.arraycopy(astStack, astPtr + 1, typeReferences = new TypeReference[length], 0, length);
            final UnionTypeReference typeReference = new UnionTypeReference(typeReferences);
            this.pushOnAstStack(typeReference);
            if (this.options.sourceLevel < 3342336L) {
                this.problemReporter().multiCatchNotBelow17(typeReference);
            }
        }
        else {
            this.pushOnAstLengthStack(1);
        }
    }
    
    protected void consumeClassBodyDeclaration() {
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        --nestedMethod[nestedType];
        final Block block = (Block)this.astStack[this.astPtr--];
        --this.astLengthPtr;
        if (this.diet) {
            final Block block2 = block;
            block2.bits &= 0xFFFFFFF7;
        }
        final Initializer initializer3;
        final Initializer initializer2;
        final Initializer initializer = initializer2 = (initializer3 = (Initializer)this.astStack[this.astPtr]);
        final int sourceStart = block.sourceStart;
        initializer2.sourceStart = sourceStart;
        initializer3.declarationSourceStart = sourceStart;
        initializer.block = block;
        --this.intPtr;
        initializer.bodyStart = this.intStack[this.intPtr--];
        --this.realBlockPtr;
        final int javadocCommentStart = this.intStack[this.intPtr--];
        if (javadocCommentStart != -1) {
            initializer.declarationSourceStart = javadocCommentStart;
            initializer.javadoc = this.javadoc;
            this.javadoc = null;
        }
        initializer.bodyEnd = this.endPosition;
        initializer.sourceEnd = this.endStatementPosition;
        initializer.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }
    
    protected void consumeClassBodyDeclarations() {
        this.concatNodeLists();
    }
    
    protected void consumeClassBodyDeclarationsopt() {
        --this.nestedType;
    }
    
    protected void consumeClassBodyopt() {
        this.pushOnAstStack(null);
        this.endPosition = this.rParenPos;
    }
    
    protected void consumeClassDeclaration() {
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationInto(length);
        }
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        final boolean hasConstructor = typeDecl.checkConstructors(this);
        if (!hasConstructor) {
            switch (TypeDeclaration.kind(typeDecl.modifiers)) {
                case 1:
                case 3: {
                    boolean insideFieldInitializer = false;
                    if (this.diet) {
                        for (int i = this.nestedType; i > 0; --i) {
                            if (this.variablesCounter[i] > 0) {
                                insideFieldInitializer = true;
                                break;
                            }
                        }
                    }
                    typeDecl.createDefaultConstructor(!this.diet || this.dietInt != 0 || insideFieldInitializer, true);
                    break;
                }
            }
        }
        if (this.scanner.containsAssertKeyword) {
            final TypeDeclaration typeDeclaration = typeDecl;
            typeDeclaration.bits |= 0x1;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            final TypeDeclaration typeDeclaration2 = typeDecl;
            typeDeclaration2.bits |= 0x8;
        }
        typeDecl.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }
    
    protected void consumeClassHeader() {
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 49) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }
    
    protected void consumeClassHeaderExtends() {
        final TypeReference superClass = this.getTypeReference(0);
        final TypeDeclaration typeDeclaration;
        final TypeDeclaration typeDecl = typeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
        typeDeclaration.bits |= (superClass.bits & 0x100000);
        typeDecl.superclass = superClass;
        final TypeReference typeReference = superClass;
        typeReference.bits |= 0x10;
        typeDecl.bodyStart = typeDecl.superclass.sourceEnd + 1;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }
    
    protected void consumeClassHeaderImplements() {
        final int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        System.arraycopy(this.astStack, this.astPtr + 1, typeDecl.superInterfaces = new TypeReference[length], 0, length);
        final TypeReference[] superinterfaces = typeDecl.superInterfaces;
        for (int i = 0, max = superinterfaces.length; i < max; ++i) {
            final TypeReference typeReference = superinterfaces[i];
            final TypeDeclaration typeDeclaration = typeDecl;
            typeDeclaration.bits |= (typeReference.bits & 0x100000);
            final TypeReference typeReference2 = typeReference;
            typeReference2.bits |= 0x10;
        }
        typeDecl.bodyStart = typeDecl.superInterfaces[length - 1].sourceEnd + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }
    
    protected void consumeClassHeaderName1() {
        final TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                final TypeDeclaration typeDeclaration = typeDecl;
                typeDeclaration.bits |= 0x400;
            }
        }
        else {
            final TypeDeclaration typeDeclaration2 = typeDecl;
            typeDeclaration2.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        final long pos = this.identifierPositionStack[this.identifierPtr];
        typeDecl.sourceEnd = (int)pos;
        typeDecl.sourceStart = (int)(pos >>> 32);
        typeDecl.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
        --this.intPtr;
        typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
        typeDecl.modifiers = this.intStack[this.intPtr--];
        if (typeDecl.modifiersSourceStart >= 0) {
            typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
        }
        if ((typeDecl.bits & 0x400) == 0x0 && (typeDecl.bits & 0x100) == 0x0 && this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
            final TypeDeclaration typeDeclaration3 = typeDecl;
            typeDeclaration3.bits |= 0x1000;
        }
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, typeDecl.annotations = new Annotation[length], 0, length);
        }
        typeDecl.bodyStart = typeDecl.sourceEnd + 1;
        this.pushOnAstStack(typeDecl);
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            this.currentElement = this.currentElement.add(typeDecl, 0);
            this.lastIgnoredToken = -1;
        }
        typeDecl.javadoc = this.javadoc;
        this.javadoc = null;
    }
    
    protected void consumeClassInstanceCreationExpression() {
        this.classInstanceCreation(false);
        this.consumeInvocationExpression();
    }
    
    protected void consumeClassInstanceCreationExpressionName() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
    }
    
    protected void consumeClassInstanceCreationExpressionQualified() {
        this.classInstanceCreation(true);
        final QualifiedAllocationExpression qae = (QualifiedAllocationExpression)this.expressionStack[this.expressionPtr];
        if (qae.anonymousType == null) {
            --this.expressionLengthPtr;
            --this.expressionPtr;
            qae.enclosingInstance = this.expressionStack[this.expressionPtr];
            this.expressionStack[this.expressionPtr] = qae;
        }
        qae.sourceStart = qae.enclosingInstance.sourceStart;
        this.consumeInvocationExpression();
    }
    
    protected void consumeClassInstanceCreationExpressionQualifiedWithTypeArguments() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 1 && this.astStack[this.astPtr] == null) {
            --this.astPtr;
            final QualifiedAllocationExpression alloc = new QualifiedAllocationExpression();
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[length], 0, length);
            }
            this.checkForDiamond(alloc.type = this.getTypeReference(0));
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
            --this.intPtr;
            alloc.sourceStart = this.intStack[this.intPtr--];
            this.pushOnExpressionStack(alloc);
        }
        else {
            this.dispatchDeclarationInto(length);
            final TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (length == 0 && !this.containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                final TypeDeclaration typeDeclaration = anonymousTypeDeclaration;
                typeDeclaration.bits |= 0x8;
            }
            --this.astPtr;
            --this.astLengthPtr;
            final QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
            if (allocationExpression != null) {
                allocationExpression.sourceEnd = this.endStatementPosition;
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
                allocationExpression.sourceStart = this.intStack[this.intPtr--];
                this.checkForDiamond(allocationExpression.type);
            }
        }
        final QualifiedAllocationExpression qae = (QualifiedAllocationExpression)this.expressionStack[this.expressionPtr];
        if (qae.anonymousType == null) {
            --this.expressionLengthPtr;
            --this.expressionPtr;
            qae.enclosingInstance = this.expressionStack[this.expressionPtr];
            this.expressionStack[this.expressionPtr] = qae;
        }
        qae.sourceStart = qae.enclosingInstance.sourceStart;
        this.consumeInvocationExpression();
    }
    
    protected void consumeClassInstanceCreationExpressionWithTypeArguments() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 1 && this.astStack[this.astPtr] == null) {
            --this.astPtr;
            final AllocationExpression alloc = new AllocationExpression();
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[length], 0, length);
            }
            this.checkForDiamond(alloc.type = this.getTypeReference(0));
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
            --this.intPtr;
            alloc.sourceStart = this.intStack[this.intPtr--];
            this.pushOnExpressionStack(alloc);
        }
        else {
            this.dispatchDeclarationInto(length);
            final TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (length == 0 && !this.containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                final TypeDeclaration typeDeclaration = anonymousTypeDeclaration;
                typeDeclaration.bits |= 0x8;
            }
            --this.astPtr;
            --this.astLengthPtr;
            final QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
            if (allocationExpression != null) {
                allocationExpression.sourceEnd = this.endStatementPosition;
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
                allocationExpression.sourceStart = this.intStack[this.intPtr--];
                this.checkForDiamond(allocationExpression.type);
            }
        }
        this.consumeInvocationExpression();
    }
    
    protected void consumeClassOrInterface() {
        final int[] genericsIdentifiersLengthStack = this.genericsIdentifiersLengthStack;
        final int genericsIdentifiersLengthPtr = this.genericsIdentifiersLengthPtr;
        genericsIdentifiersLengthStack[genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
        this.pushOnGenericsLengthStack(0);
    }
    
    protected void consumeClassOrInterfaceName() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
    }
    
    protected void consumeClassTypeElt() {
        this.pushOnAstStack(this.getTypeReference(0));
        ++this.listLength;
    }
    
    protected void consumeClassTypeList() {
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeCompilationUnit() {
    }
    
    protected void consumeConditionalExpression(final int op) {
        this.intPtr -= 2;
        this.expressionPtr -= 2;
        this.expressionLengthPtr -= 2;
        this.expressionStack[this.expressionPtr] = new ConditionalExpression(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], this.expressionStack[this.expressionPtr + 2]);
    }
    
    protected void consumeConditionalExpressionWithName(final int op) {
        this.intPtr -= 2;
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        this.expressionPtr -= 2;
        this.expressionLengthPtr -= 2;
        this.expressionStack[this.expressionPtr] = new ConditionalExpression(this.expressionStack[this.expressionPtr + 2], this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1]);
    }
    
    protected void consumeConstructorBlockStatements() {
        this.concatNodeLists();
    }
    
    protected void consumeConstructorBody() {
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        --nestedMethod[nestedType];
    }
    
    protected void consumeConstructorDeclaration() {
        --this.intPtr;
        --this.intPtr;
        --this.realBlockPtr;
        ExplicitConstructorCall constructorCall = null;
        Statement[] statements = null;
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (!this.options.ignoreMethodBodies) {
                if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
                    System.arraycopy(this.astStack, this.astPtr + 2, statements = new Statement[length - 1], 0, length - 1);
                    constructorCall = (ExplicitConstructorCall)this.astStack[this.astPtr + 1];
                }
                else {
                    System.arraycopy(this.astStack, this.astPtr + 1, statements = new Statement[length], 0, length);
                    constructorCall = SuperReference.implicitSuperConstructorCall();
                }
            }
        }
        else {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                for (int i = this.nestedType; i > 0; --i) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                }
            }
            if (!this.options.ignoreMethodBodies && (!this.diet || insideFieldInitializer)) {
                constructorCall = SuperReference.implicitSuperConstructorCall();
            }
        }
        final ConstructorDeclaration cd = (ConstructorDeclaration)this.astStack[this.astPtr];
        cd.constructorCall = constructorCall;
        cd.statements = statements;
        if (constructorCall != null && cd.constructorCall.sourceEnd == 0) {
            cd.constructorCall.sourceEnd = cd.sourceEnd;
            cd.constructorCall.sourceStart = cd.sourceStart;
        }
        if ((!this.diet || this.dietInt != 0) && statements == null && (constructorCall == null || constructorCall.isImplicitSuper()) && !this.containsComment(cd.bodyStart, this.endPosition)) {
            final ConstructorDeclaration constructorDeclaration = cd;
            constructorDeclaration.bits |= 0x8;
        }
        cd.bodyEnd = this.endPosition;
        cd.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }
    
    protected void consumeConstructorHeader() {
        final AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 49) {
            method.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            if (this.currentToken == 28) {
                final AbstractMethodDeclaration abstractMethodDeclaration = method;
                abstractMethodDeclaration.modifiers |= 0x1000000;
                method.declarationSourceEnd = this.scanner.currentPosition - 1;
                method.bodyEnd = this.scanner.currentPosition - 1;
                if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
                    this.currentElement = this.currentElement.parent;
                }
            }
            this.restartRecovery = true;
        }
    }
    
    protected void consumeConstructorHeaderName() {
        if (this.currentElement != null && this.lastIgnoredToken == 36) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        final ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
        cd.selector = this.identifierStack[this.identifierPtr];
        final long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        cd.declarationSourceStart = this.intStack[this.intPtr--];
        cd.modifiers = this.intStack[this.intPtr--];
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, cd.annotations = new Annotation[length], 0, length);
        }
        cd.javadoc = this.javadoc;
        this.javadoc = null;
        cd.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(cd);
        cd.sourceEnd = this.lParenPos;
        cd.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = cd.bodyStart;
            if ((this.currentElement instanceof RecoveredType && this.lastIgnoredToken != 3) || cd.modifiers != 0) {
                this.currentElement = this.currentElement.add(cd, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }
    
    protected void consumeConstructorHeaderNameWithTypeParameters() {
        if (this.currentElement != null && this.lastIgnoredToken == 36) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        final ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
        cd.selector = this.identifierStack[this.identifierPtr];
        final long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, cd.typeParameters = new TypeParameter[length], 0, length);
        cd.declarationSourceStart = this.intStack[this.intPtr--];
        cd.modifiers = this.intStack[this.intPtr--];
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, cd.annotations = new Annotation[length], 0, length);
        }
        cd.javadoc = this.javadoc;
        this.javadoc = null;
        cd.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(cd);
        cd.sourceEnd = this.lParenPos;
        cd.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = cd.bodyStart;
            if ((this.currentElement instanceof RecoveredType && this.lastIgnoredToken != 3) || cd.modifiers != 0) {
                this.currentElement = this.currentElement.add(cd, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }
    
    protected void consumeCreateInitializer() {
        this.pushOnAstStack(new Initializer(null, 0));
    }
    
    protected void consumeDefaultLabel() {
        final CaseStatement defaultStatement = new CaseStatement(null, this.intStack[this.intPtr--], this.intStack[this.intPtr--]);
        if (this.hasLeadingTagComment(Parser.FALL_THROUGH_TAG, defaultStatement.sourceStart)) {
            final CaseStatement caseStatement = defaultStatement;
            caseStatement.bits |= 0x20000000;
        }
        if (this.hasLeadingTagComment(Parser.CASES_OMITTED_TAG, defaultStatement.sourceStart)) {
            final CaseStatement caseStatement2 = defaultStatement;
            caseStatement2.bits |= 0x40000000;
        }
        this.pushOnAstStack(defaultStatement);
    }
    
    protected void consumeDefaultModifiers() {
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack((this.modifiersSourceStart >= 0) ? this.modifiersSourceStart : this.scanner.startPosition);
        this.resetModifiers();
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumeDiet() {
        this.checkComment();
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
        this.jumpOverMethodBody();
    }
    
    protected void consumeDims() {
        this.pushOnIntStack(this.dimensions);
        this.dimensions = 0;
    }
    
    protected void consumeDimWithOrWithOutExpr() {
        this.pushOnExpressionStack(null);
        if (this.currentElement != null && this.currentToken == 49) {
            this.ignoreNextOpeningBrace = true;
            final RecoveredElement currentElement = this.currentElement;
            ++currentElement.bracketBalance;
        }
    }
    
    protected void consumeDimWithOrWithOutExprs() {
        this.concatExpressionLists();
    }
    
    protected void consumeUnionType() {
        this.pushOnAstStack(this.getTypeReference(this.intStack[this.intPtr--]));
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeUnionTypeAsClassType() {
        this.pushOnAstStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }
    
    protected void consumeEmptyAnnotationTypeMemberDeclarationsopt() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyArgumentListopt() {
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumeEmptyArguments() {
        final FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
        this.pushOnIntStack(fieldDeclaration.sourceEnd);
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumeEmptyArrayInitializer() {
        this.arrayInitializer(0);
    }
    
    protected void consumeEmptyArrayInitializeropt() {
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumeEmptyBlockStatementsopt() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyCatchesopt() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyClassBodyDeclarationsopt() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyDimsopt() {
        this.pushOnIntStack(0);
    }
    
    protected void consumeEmptyEnumDeclarations() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyExpression() {
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumeEmptyForInitopt() {
        this.pushOnAstLengthStack(0);
        this.forStartPosition = 0;
    }
    
    protected void consumeEmptyForUpdateopt() {
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumeEmptyInterfaceMemberDeclarationsopt() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyInternalCompilationUnit() {
        if (this.compilationUnit.isPackageInfo()) {
            this.compilationUnit.types = new TypeDeclaration[1];
            this.compilationUnit.createPackageInfoType();
        }
    }
    
    protected void consumeEmptyMemberValueArrayInitializer() {
        this.arrayInitializer(0);
    }
    
    protected void consumeEmptyMemberValuePairsopt() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyMethodHeaderDefaultValue() {
        final AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        if (method.isAnnotationMethod()) {
            this.pushOnExpressionStackLengthStack(0);
        }
        this.recordStringLiterals = true;
    }
    
    protected void consumeEmptyStatement() {
        final char[] source = this.scanner.source;
        if (source[this.endStatementPosition] == ';') {
            this.pushOnAstStack(new EmptyStatement(this.endStatementPosition, this.endStatementPosition));
        }
        else {
            if (source.length > 5) {
                int c1 = 0;
                int c2 = 0;
                int c3 = 0;
                int c4 = 0;
                int pos;
                for (pos = this.endStatementPosition - 4; source[pos] == 'u'; --pos) {}
                if (source[pos] == '\\' && (c1 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 3])) <= 15 && c1 >= 0 && (c2 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 2])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 1])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition])) <= 15 && c4 >= 0 && (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4) == ';') {
                    this.pushOnAstStack(new EmptyStatement(pos, this.endStatementPosition));
                    return;
                }
            }
            this.pushOnAstStack(new EmptyStatement(this.endPosition + 1, this.endStatementPosition));
        }
    }
    
    protected void consumeEmptySwitchBlock() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeEmptyTypeDeclaration() {
        this.pushOnAstLengthStack(0);
        if (!this.statementRecoveryActivated) {
            this.problemReporter().superfluousSemicolon(this.endPosition + 1, this.endStatementPosition);
        }
        this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }
    
    protected void consumeEnhancedForStatement() {
        --this.astLengthPtr;
        final Statement statement = (Statement)this.astStack[this.astPtr--];
        final ForeachStatement foreachStatement = (ForeachStatement)this.astStack[this.astPtr];
        foreachStatement.action = statement;
        if (statement instanceof EmptyStatement) {
            final Statement statement2 = statement;
            statement2.bits |= 0x1;
        }
        foreachStatement.sourceEnd = this.endStatementPosition;
    }
    
    protected void consumeEnhancedForStatementHeader() {
        final ForeachStatement statement = (ForeachStatement)this.astStack[this.astPtr];
        --this.expressionLengthPtr;
        final Expression collection = this.expressionStack[this.expressionPtr--];
        statement.collection = collection;
        statement.elementVariable.declarationSourceEnd = collection.sourceEnd;
        statement.elementVariable.declarationEnd = collection.sourceEnd;
        statement.sourceEnd = this.rParenPos;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfForeachStatements(statement.elementVariable, collection);
        }
    }
    
    protected void consumeEnhancedForStatementHeaderInit(final boolean hasModifiers) {
        final char[] identifierName = this.identifierStack[this.identifierPtr];
        final long namePosition = this.identifierPositionStack[this.identifierPtr];
        final LocalDeclaration localDeclaration = this.createLocalDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
        localDeclaration.declarationSourceEnd = localDeclaration.declarationEnd;
        final LocalDeclaration localDeclaration2 = localDeclaration;
        localDeclaration2.bits |= 0x10;
        final int extraDims = this.intStack[this.intPtr--];
        final Annotation[][] annotationsOnExtendedDimensions = (Annotation[][])((extraDims == 0) ? null : this.getAnnotationsOnDimensions(extraDims));
        --this.identifierPtr;
        --this.identifierLengthPtr;
        int declarationSourceStart = 0;
        int modifiersValue = 0;
        if (hasModifiers) {
            declarationSourceStart = this.intStack[this.intPtr--];
            modifiersValue = this.intStack[this.intPtr--];
        }
        else {
            this.intPtr -= 2;
        }
        TypeReference type = this.getTypeReference(this.intStack[this.intPtr--]);
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, localDeclaration.annotations = new Annotation[length], 0, length);
            final LocalDeclaration localDeclaration3 = localDeclaration;
            localDeclaration3.bits |= 0x100000;
        }
        if (extraDims != 0) {
            type = this.augmentTypeWithAdditionalDimensions(type, extraDims, annotationsOnExtendedDimensions, false);
        }
        if (hasModifiers) {
            localDeclaration.declarationSourceStart = declarationSourceStart;
            localDeclaration.modifiers = modifiersValue;
        }
        else {
            localDeclaration.declarationSourceStart = type.sourceStart;
        }
        localDeclaration.type = type;
        final LocalDeclaration localDeclaration4 = localDeclaration;
        localDeclaration4.bits |= (type.bits & 0x100000);
        final ForeachStatement iteratorForStatement = new ForeachStatement(localDeclaration, this.intStack[this.intPtr--]);
        this.pushOnAstStack(iteratorForStatement);
        iteratorForStatement.sourceEnd = localDeclaration.declarationSourceEnd;
        this.forStartPosition = 0;
    }
    
    protected void consumeEnterAnonymousClassBody(final boolean qualified) {
        final TypeReference typeReference = this.getTypeReference(0);
        final TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
        anonymousType.name = CharOperation.NO_CHAR;
        final TypeDeclaration typeDeclaration = anonymousType;
        typeDeclaration.bits |= 0x300;
        final TypeDeclaration typeDeclaration2 = anonymousType;
        typeDeclaration2.bits |= (typeReference.bits & 0x100000);
        final QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
        this.markEnclosingMemberWithLocalType();
        this.pushOnAstStack(anonymousType);
        alloc.sourceEnd = this.rParenPos;
        final int argumentLength;
        if ((argumentLength = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= argumentLength;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments = new Expression[argumentLength], 0, argumentLength);
        }
        if (qualified) {
            --this.expressionLengthPtr;
            alloc.enclosingInstance = this.expressionStack[this.expressionPtr--];
        }
        alloc.type = typeReference;
        anonymousType.sourceEnd = alloc.sourceEnd;
        final TypeDeclaration typeDeclaration3 = anonymousType;
        final TypeDeclaration typeDeclaration4 = anonymousType;
        final int sourceStart = alloc.type.sourceStart;
        typeDeclaration4.declarationSourceStart = sourceStart;
        typeDeclaration3.sourceStart = sourceStart;
        alloc.sourceStart = this.intStack[this.intPtr--];
        this.pushOnExpressionStack(alloc);
        anonymousType.bodyStart = this.scanner.currentPosition;
        this.listLength = 0;
        this.scanner.commentPtr = -1;
        if (this.currentElement != null) {
            this.lastCheckPoint = anonymousType.bodyStart;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            if (!(this.currentElement instanceof RecoveredAnnotation)) {
                if (this.isIndirectlyInsideLambdaExpression()) {
                    this.ignoreNextOpeningBrace = true;
                }
                else {
                    this.currentToken = 0;
                }
            }
            else {
                this.ignoreNextOpeningBrace = true;
                final RecoveredElement currentElement = this.currentElement;
                ++currentElement.bracketBalance;
            }
            this.lastIgnoredToken = -1;
        }
    }
    
    protected void consumeEnterCompilationUnit() {
    }
    
    protected void consumeEnterMemberValue() {
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            final RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.hasPendingMemberValueName = true;
        }
    }
    
    protected void consumeEnterMemberValueArrayInitializer() {
        if (this.currentElement != null) {
            this.ignoreNextOpeningBrace = true;
            final RecoveredElement currentElement = this.currentElement;
            ++currentElement.bracketBalance;
        }
    }
    
    protected void consumeEnterVariable() {
        final char[] identifierName = this.identifierStack[this.identifierPtr];
        final long namePosition = this.identifierPositionStack[this.identifierPtr];
        final int extendedDimensions = this.intStack[this.intPtr--];
        final Annotation[][] annotationsOnExtendedDimensions = (Annotation[][])((extendedDimensions == 0) ? null : this.getAnnotationsOnDimensions(extendedDimensions));
        final boolean isLocalDeclaration = this.nestedMethod[this.nestedType] != 0;
        AbstractVariableDeclaration declaration;
        if (isLocalDeclaration) {
            declaration = this.createLocalDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
        }
        else {
            declaration = this.createFieldDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
        }
        --this.identifierPtr;
        --this.identifierLengthPtr;
        final int variableIndex = this.variablesCounter[this.nestedType];
        TypeReference type;
        if (variableIndex == 0) {
            if (isLocalDeclaration) {
                declaration.declarationSourceStart = this.intStack[this.intPtr--];
                declaration.modifiers = this.intStack[this.intPtr--];
                final int length;
                if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                    final Expression[] expressionStack = this.expressionStack;
                    final int expressionPtr = this.expressionPtr - length;
                    this.expressionPtr = expressionPtr;
                    System.arraycopy(expressionStack, expressionPtr + 1, declaration.annotations = new Annotation[length], 0, length);
                }
                type = this.getTypeReference(this.intStack[this.intPtr--]);
                if (declaration.declarationSourceStart == -1) {
                    declaration.declarationSourceStart = type.sourceStart;
                }
                this.pushOnAstStack(type);
            }
            else {
                type = this.getTypeReference(this.intStack[this.intPtr--]);
                this.pushOnAstStack(type);
                declaration.declarationSourceStart = this.intStack[this.intPtr--];
                declaration.modifiers = this.intStack[this.intPtr--];
                final int length;
                if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                    final Expression[] expressionStack2 = this.expressionStack;
                    final int expressionPtr2 = this.expressionPtr - length;
                    this.expressionPtr = expressionPtr2;
                    System.arraycopy(expressionStack2, expressionPtr2 + 1, declaration.annotations = new Annotation[length], 0, length);
                }
                final FieldDeclaration fieldDeclaration = (FieldDeclaration)declaration;
                fieldDeclaration.javadoc = this.javadoc;
            }
            this.javadoc = null;
        }
        else {
            type = (TypeReference)this.astStack[this.astPtr - variableIndex];
            final AbstractVariableDeclaration previousVariable = (AbstractVariableDeclaration)this.astStack[this.astPtr];
            declaration.declarationSourceStart = previousVariable.declarationSourceStart;
            declaration.modifiers = previousVariable.modifiers;
            final Annotation[] annotations = previousVariable.annotations;
            if (annotations != null) {
                final int annotationsLength = annotations.length;
                System.arraycopy(annotations, 0, declaration.annotations = new Annotation[annotationsLength], 0, annotationsLength);
            }
        }
        declaration.type = ((extendedDimensions == 0) ? type : this.augmentTypeWithAdditionalDimensions(type, extendedDimensions, annotationsOnExtendedDimensions, false));
        final AbstractVariableDeclaration abstractVariableDeclaration = declaration;
        abstractVariableDeclaration.bits |= (type.bits & 0x100000);
        final int[] variablesCounter = this.variablesCounter;
        final int nestedType = this.nestedType;
        ++variablesCounter[nestedType];
        this.pushOnAstStack(declaration);
        if (this.currentElement != null) {
            if (!(this.currentElement instanceof RecoveredType) && (this.currentToken == 3 || Util.getLineNumber(declaration.type.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) != Util.getLineNumber((int)(namePosition >>> 32), this.scanner.lineEnds, 0, this.scanner.linePtr))) {
                this.lastCheckPoint = (int)(namePosition >>> 32);
                this.restartRecovery = true;
                return;
            }
            if (isLocalDeclaration) {
                final LocalDeclaration localDecl = (LocalDeclaration)this.astStack[this.astPtr];
                this.lastCheckPoint = localDecl.sourceEnd + 1;
                this.currentElement = this.currentElement.add(localDecl, 0);
            }
            else {
                final FieldDeclaration fieldDecl = (FieldDeclaration)this.astStack[this.astPtr];
                this.lastCheckPoint = fieldDecl.sourceEnd + 1;
                this.currentElement = this.currentElement.add(fieldDecl, 0);
            }
            this.lastIgnoredToken = -1;
        }
    }
    
    protected void consumeEnumBodyNoConstants() {
    }
    
    protected void consumeEnumBodyWithConstants() {
        this.concatNodeLists();
    }
    
    protected void consumeEnumConstantHeader() {
        final FieldDeclaration enumConstant = (FieldDeclaration)this.astStack[this.astPtr];
        final boolean foundOpeningBrace = this.currentToken == 49;
        if (foundOpeningBrace) {
            final TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
            anonymousType.name = CharOperation.NO_CHAR;
            final TypeDeclaration typeDeclaration = anonymousType;
            typeDeclaration.bits |= 0x300;
            final int start = this.scanner.startPosition;
            anonymousType.declarationSourceStart = start;
            anonymousType.sourceStart = start;
            anonymousType.sourceEnd = start;
            anonymousType.modifiers = 0;
            anonymousType.bodyStart = this.scanner.currentPosition;
            this.markEnclosingMemberWithLocalType();
            this.consumeNestedType();
            final int[] variablesCounter = this.variablesCounter;
            final int nestedType = this.nestedType;
            ++variablesCounter[nestedType];
            this.pushOnAstStack(anonymousType);
            final QualifiedAllocationExpression allocationExpression = new QualifiedAllocationExpression(anonymousType);
            allocationExpression.enumConstant = enumConstant;
            final int length;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, allocationExpression.arguments = new Expression[length], 0, length);
            }
            enumConstant.initialization = allocationExpression;
        }
        else {
            final AllocationExpression allocationExpression2 = new AllocationExpression();
            allocationExpression2.enumConstant = enumConstant;
            final int length2;
            if ((length2 = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length2;
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, allocationExpression2.arguments = new Expression[length2], 0, length2);
            }
            enumConstant.initialization = allocationExpression2;
        }
        enumConstant.initialization.sourceStart = enumConstant.declarationSourceStart;
        if (this.currentElement != null) {
            if (foundOpeningBrace) {
                final TypeDeclaration anonymousType = (TypeDeclaration)this.astStack[this.astPtr];
                this.currentElement = this.currentElement.add(anonymousType, 0);
                this.lastCheckPoint = anonymousType.bodyStart;
                this.lastIgnoredToken = -1;
                if (this.isIndirectlyInsideLambdaExpression()) {
                    this.ignoreNextOpeningBrace = true;
                }
                else {
                    this.currentToken = 0;
                }
            }
            else {
                if (this.currentToken == 28) {
                    final RecoveredType currentType = this.currentRecoveryType();
                    if (currentType != null) {
                        currentType.insideEnumConstantPart = false;
                    }
                }
                this.lastCheckPoint = this.scanner.startPosition;
                this.lastIgnoredToken = -1;
                this.restartRecovery = true;
            }
        }
    }
    
    protected void consumeEnumConstantHeaderName() {
        if (this.currentElement != null && ((!(this.currentElement instanceof RecoveredType) && (!(this.currentElement instanceof RecoveredField) || ((RecoveredField)this.currentElement).fieldDeclaration.type != null)) || this.lastIgnoredToken == 3)) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        final long namePosition = this.identifierPositionStack[this.identifierPtr];
        final char[] constantName = this.identifierStack[this.identifierPtr];
        final int sourceEnd = (int)namePosition;
        final FieldDeclaration enumConstant = this.createFieldDeclaration(constantName, (int)(namePosition >>> 32), sourceEnd);
        --this.identifierPtr;
        --this.identifierLengthPtr;
        enumConstant.modifiersSourceStart = this.intStack[this.intPtr--];
        enumConstant.modifiers = this.intStack[this.intPtr--];
        enumConstant.declarationSourceStart = enumConstant.modifiersSourceStart;
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, enumConstant.annotations = new Annotation[length], 0, length);
            final FieldDeclaration fieldDeclaration = enumConstant;
            fieldDeclaration.bits |= 0x100000;
        }
        this.pushOnAstStack(enumConstant);
        if (this.currentElement != null) {
            this.lastCheckPoint = enumConstant.sourceEnd + 1;
            this.currentElement = this.currentElement.add(enumConstant, 0);
        }
        enumConstant.javadoc = this.javadoc;
        this.javadoc = null;
    }
    
    protected void consumeEnumConstantNoClassBody() {
        final int endOfEnumConstant = this.intStack[this.intPtr--];
        final FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
        fieldDeclaration.declarationEnd = endOfEnumConstant;
        fieldDeclaration.declarationSourceEnd = endOfEnumConstant;
        final ASTNode initialization = fieldDeclaration.initialization;
        if (initialization != null) {
            initialization.sourceEnd = endOfEnumConstant;
        }
    }
    
    protected void consumeEnumConstants() {
        this.concatNodeLists();
    }
    
    protected void consumeEnumConstantWithClassBody() {
        this.dispatchDeclarationInto(this.astLengthStack[this.astLengthPtr--]);
        final TypeDeclaration anonymousType = (TypeDeclaration)this.astStack[this.astPtr--];
        --this.astLengthPtr;
        anonymousType.bodyEnd = this.endPosition;
        anonymousType.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        final FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
        fieldDeclaration.declarationEnd = this.endStatementPosition;
        final int declarationSourceEnd = anonymousType.declarationSourceEnd;
        fieldDeclaration.declarationSourceEnd = declarationSourceEnd;
        --this.intPtr;
        this.variablesCounter[this.nestedType] = 0;
        --this.nestedType;
        final ASTNode initialization = fieldDeclaration.initialization;
        if (initialization != null) {
            initialization.sourceEnd = declarationSourceEnd;
        }
    }
    
    protected void consumeEnumDeclaration() {
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationIntoEnumDeclaration(length);
        }
        final TypeDeclaration enumDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
        final boolean hasConstructor = enumDeclaration.checkConstructors(this);
        if (!hasConstructor) {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                for (int i = this.nestedType; i > 0; --i) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                }
            }
            enumDeclaration.createDefaultConstructor(!this.diet || insideFieldInitializer, true);
        }
        if (this.scanner.containsAssertKeyword) {
            final TypeDeclaration typeDeclaration = enumDeclaration;
            typeDeclaration.bits |= 0x1;
        }
        enumDeclaration.addClinit();
        enumDeclaration.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(enumDeclaration.bodyStart, enumDeclaration.bodyEnd)) {
            final TypeDeclaration typeDeclaration2 = enumDeclaration;
            typeDeclaration2.bits |= 0x8;
        }
        enumDeclaration.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }
    
    protected void consumeEnumDeclarations() {
    }
    
    protected void consumeEnumHeader() {
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 49) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }
    
    protected void consumeEnumHeaderName() {
        final TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                final TypeDeclaration typeDeclaration = enumDeclaration;
                typeDeclaration.bits |= 0x400;
            }
        }
        else {
            this.blockReal();
        }
        final long pos = this.identifierPositionStack[this.identifierPtr];
        enumDeclaration.sourceEnd = (int)pos;
        enumDeclaration.sourceStart = (int)(pos >>> 32);
        enumDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
        --this.intPtr;
        enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        enumDeclaration.modifiers = (this.intStack[this.intPtr--] | 0x4000);
        if (enumDeclaration.modifiersSourceStart >= 0) {
            enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
        }
        if ((enumDeclaration.bits & 0x400) == 0x0 && (enumDeclaration.bits & 0x100) == 0x0 && this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
            final TypeDeclaration typeDeclaration2 = enumDeclaration;
            typeDeclaration2.bits |= 0x1000;
        }
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, enumDeclaration.annotations = new Annotation[length], 0, length);
        }
        enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
        this.pushOnAstStack(enumDeclaration);
        this.listLength = 0;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = enumDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(enumDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
        enumDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
    }
    
    protected void consumeEnumHeaderNameWithTypeParameters() {
        final TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, enumDeclaration.typeParameters = new TypeParameter[length], 0, length);
        this.problemReporter().invalidUsageOfTypeParametersForEnumDeclaration(enumDeclaration);
        enumDeclaration.bodyStart = enumDeclaration.typeParameters[length - 1].declarationSourceEnd + 1;
        this.listTypeParameterLength = 0;
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                final TypeDeclaration typeDeclaration = enumDeclaration;
                typeDeclaration.bits |= 0x400;
            }
        }
        else {
            this.blockReal();
        }
        final long pos = this.identifierPositionStack[this.identifierPtr];
        enumDeclaration.sourceEnd = (int)pos;
        enumDeclaration.sourceStart = (int)(pos >>> 32);
        enumDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
        --this.intPtr;
        enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        enumDeclaration.modifiers = (this.intStack[this.intPtr--] | 0x4000);
        if (enumDeclaration.modifiersSourceStart >= 0) {
            enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
        }
        if ((enumDeclaration.bits & 0x400) == 0x0 && (enumDeclaration.bits & 0x100) == 0x0 && this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
            final TypeDeclaration typeDeclaration2 = enumDeclaration;
            typeDeclaration2.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, enumDeclaration.annotations = new Annotation[length], 0, length);
        }
        enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
        this.pushOnAstStack(enumDeclaration);
        this.listLength = 0;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = enumDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(enumDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
        enumDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
    }
    
    protected void consumeEqualityExpression(final int op) {
        --this.expressionPtr;
        --this.expressionLengthPtr;
        this.expressionStack[this.expressionPtr] = new EqualExpression(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], op);
    }
    
    protected void consumeEqualityExpressionWithName(final int op) {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        --this.expressionPtr;
        --this.expressionLengthPtr;
        this.expressionStack[this.expressionPtr] = new EqualExpression(this.expressionStack[this.expressionPtr + 1], this.expressionStack[this.expressionPtr], op);
    }
    
    protected void consumeExitMemberValue() {
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            final RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.hasPendingMemberValueName = false;
            recoveredAnnotation.memberValuPairEqualEnd = -1;
        }
    }
    
    protected void consumeExitTryBlock() {
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
    }
    
    protected void consumeExitVariableWithInitialization() {
        --this.expressionLengthPtr;
        final AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)this.astStack[this.astPtr];
        variableDecl.initialization = this.expressionStack[this.expressionPtr--];
        variableDecl.declarationSourceEnd = variableDecl.initialization.sourceEnd;
        variableDecl.declarationEnd = variableDecl.initialization.sourceEnd;
        this.recoveryExitFromVariable();
    }
    
    protected void consumeExitVariableWithoutInitialization() {
        final AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)this.astStack[this.astPtr];
        variableDecl.declarationSourceEnd = variableDecl.declarationEnd;
        if (this.currentElement != null && this.currentElement instanceof RecoveredField && this.endStatementPosition > variableDecl.sourceEnd) {
            this.currentElement.updateSourceEndIfNecessary(this.endStatementPosition);
        }
        this.recoveryExitFromVariable();
    }
    
    protected void consumeExplicitConstructorInvocation(final int flag, final int recFlag) {
        final int startPosition = this.intStack[this.intPtr--];
        final ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
        }
        switch (flag) {
            case 0: {
                ecc.sourceStart = startPosition;
                break;
            }
            case 1: {
                --this.expressionLengthPtr;
                final ExplicitConstructorCall explicitConstructorCall = ecc;
                final ExplicitConstructorCall explicitConstructorCall2 = ecc;
                final Expression qualification = this.expressionStack[this.expressionPtr--];
                explicitConstructorCall2.qualification = qualification;
                explicitConstructorCall.sourceStart = qualification.sourceStart;
                break;
            }
            case 2: {
                final ExplicitConstructorCall explicitConstructorCall3 = ecc;
                final ExplicitConstructorCall explicitConstructorCall4 = ecc;
                final NameReference unspecifiedReferenceOptimized = this.getUnspecifiedReferenceOptimized();
                explicitConstructorCall4.qualification = unspecifiedReferenceOptimized;
                explicitConstructorCall3.sourceStart = unspecifiedReferenceOptimized.sourceStart;
                break;
            }
        }
        this.pushOnAstStack(ecc);
        ecc.sourceEnd = this.endStatementPosition;
    }
    
    protected void consumeExplicitConstructorInvocationWithTypeArguments(final int flag, final int recFlag) {
        final int startPosition = this.intStack[this.intPtr--];
        final ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
        }
        length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, ecc.typeArguments = new TypeReference[length], 0, length);
        ecc.typeArgumentsSourceStart = this.intStack[this.intPtr--];
        switch (flag) {
            case 0: {
                ecc.sourceStart = startPosition;
                break;
            }
            case 1: {
                --this.expressionLengthPtr;
                final ExplicitConstructorCall explicitConstructorCall = ecc;
                final ExplicitConstructorCall explicitConstructorCall2 = ecc;
                final Expression qualification = this.expressionStack[this.expressionPtr--];
                explicitConstructorCall2.qualification = qualification;
                explicitConstructorCall.sourceStart = qualification.sourceStart;
                break;
            }
            case 2: {
                final ExplicitConstructorCall explicitConstructorCall3 = ecc;
                final ExplicitConstructorCall explicitConstructorCall4 = ecc;
                final NameReference unspecifiedReferenceOptimized = this.getUnspecifiedReferenceOptimized();
                explicitConstructorCall4.qualification = unspecifiedReferenceOptimized;
                explicitConstructorCall3.sourceStart = unspecifiedReferenceOptimized.sourceStart;
                break;
            }
        }
        this.pushOnAstStack(ecc);
        ecc.sourceEnd = this.endStatementPosition;
    }
    
    protected void consumeExpressionStatement() {
        --this.expressionLengthPtr;
        final Expression expression = this.expressionStack[this.expressionPtr--];
        expression.statementEnd = this.endStatementPosition;
        final Expression expression2 = expression;
        expression2.bits |= 0x10;
        this.pushOnAstStack(expression);
    }
    
    protected void consumeFieldAccess(final boolean isSuperAccess) {
        final FieldReference fr = new FieldReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        --this.identifierLengthPtr;
        if (isSuperAccess) {
            fr.sourceStart = this.intStack[this.intPtr--];
            fr.receiver = new SuperReference(fr.sourceStart, this.endPosition);
            this.pushOnExpressionStack(fr);
        }
        else {
            fr.receiver = this.expressionStack[this.expressionPtr];
            fr.sourceStart = fr.receiver.sourceStart;
            this.expressionStack[this.expressionPtr] = fr;
        }
    }
    
    protected void consumeFieldDeclaration() {
        final int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        for (int i = variableDeclaratorsCounter - 1; i >= 0; --i) {
            final FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr - i];
            fieldDeclaration.declarationSourceEnd = this.endStatementPosition;
            fieldDeclaration.declarationEnd = this.endStatementPosition;
        }
        this.updateSourceDeclarationParts(variableDeclaratorsCounter);
        final int endPos = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (endPos != this.endStatementPosition) {
            for (int j = 0; j < variableDeclaratorsCounter; ++j) {
                final FieldDeclaration fieldDeclaration2 = (FieldDeclaration)this.astStack[this.astPtr - j];
                fieldDeclaration2.declarationSourceEnd = endPos;
            }
        }
        final int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
        System.arraycopy(this.astStack, startIndex, this.astStack, startIndex - 1, variableDeclaratorsCounter);
        --this.astPtr;
        this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;
        if (this.currentElement != null) {
            this.lastCheckPoint = endPos + 1;
            if (this.currentElement.parent != null && this.currentElement instanceof RecoveredField && !(this.currentElement instanceof RecoveredInitializer)) {
                this.currentElement = this.currentElement.parent;
            }
            this.restartRecovery = true;
        }
        this.variablesCounter[this.nestedType] = 0;
    }
    
    protected void consumeForceNoDiet() {
        ++this.dietInt;
    }
    
    protected void consumeForInit() {
        this.pushOnAstLengthStack(-1);
        this.forStartPosition = 0;
    }
    
    protected void consumeFormalParameter(final boolean isVarArgs) {
        NameReference qualifyingNameReference = null;
        final boolean isReceiver = this.intStack[this.intPtr--] == 0;
        if (isReceiver) {
            qualifyingNameReference = (NameReference)this.expressionStack[this.expressionPtr--];
            --this.expressionLengthPtr;
        }
        --this.identifierLengthPtr;
        final char[] identifierName = this.identifierStack[this.identifierPtr];
        final long namePositions = this.identifierPositionStack[this.identifierPtr--];
        final int extendedDimensions = this.intStack[this.intPtr--];
        final Annotation[][] annotationsOnExtendedDimensions = (Annotation[][])((extendedDimensions == 0) ? null : this.getAnnotationsOnDimensions(extendedDimensions));
        Annotation[] varArgsAnnotations = null;
        int endOfEllipsis = 0;
        if (isVarArgs) {
            endOfEllipsis = this.intStack[this.intPtr--];
            final int length;
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
                final int typeAnnotationPtr = this.typeAnnotationPtr - length;
                this.typeAnnotationPtr = typeAnnotationPtr;
                System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, varArgsAnnotations = new Annotation[length], 0, length);
            }
        }
        final int firstDimensions = this.intStack[this.intPtr--];
        TypeReference type = this.getTypeReference(firstDimensions);
        if (isVarArgs || extendedDimensions != 0) {
            if (isVarArgs) {
                type = this.augmentTypeWithAdditionalDimensions(type, 1, (Annotation[][])((varArgsAnnotations != null) ? new Annotation[][] { varArgsAnnotations } : null), true);
            }
            if (extendedDimensions != 0) {
                type = this.augmentTypeWithAdditionalDimensions(type, extendedDimensions, annotationsOnExtendedDimensions, false);
            }
            type.sourceEnd = (type.isParameterizedTypeReference() ? this.endStatementPosition : this.endPosition);
        }
        if (isVarArgs) {
            if (extendedDimensions == 0) {
                type.sourceEnd = endOfEllipsis;
            }
            final TypeReference typeReference = type;
            typeReference.bits |= 0x4000;
        }
        final int modifierPositions = this.intStack[this.intPtr--];
        Argument arg;
        if (isReceiver) {
            arg = new Receiver(identifierName, namePositions, type, qualifyingNameReference, this.intStack[this.intPtr--] & 0xFFEFFFFF);
        }
        else {
            arg = new Argument(identifierName, namePositions, type, this.intStack[this.intPtr--] & 0xFFEFFFFF);
        }
        arg.declarationSourceStart = modifierPositions;
        final Argument argument = arg;
        argument.bits |= (type.bits & 0x100000);
        int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, arg.annotations = new Annotation[length], 0, length);
            final Argument argument2 = arg;
            argument2.bits |= 0x100000;
            final RecoveredType currentRecoveryType = this.currentRecoveryType();
            if (currentRecoveryType != null) {
                currentRecoveryType.annotationsConsumed(arg.annotations);
            }
        }
        this.pushOnAstStack(arg);
        ++this.listLength;
        if (isVarArgs) {
            if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                this.problemReporter().invalidUsageOfVarargs(arg);
            }
            else if (!this.statementRecoveryActivated && extendedDimensions > 0) {
                this.problemReporter().illegalExtendedDimensions(arg);
            }
        }
    }
    
    protected Annotation[][] getAnnotationsOnDimensions(final int dimensionsCount) {
        Annotation[][] dimensionsAnnotations = null;
        if (dimensionsCount > 0) {
            for (int i = 0; i < dimensionsCount; ++i) {
                Annotation[] annotations = null;
                final int length;
                if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                    final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
                    final int typeAnnotationPtr = this.typeAnnotationPtr - length;
                    this.typeAnnotationPtr = typeAnnotationPtr;
                    System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, annotations = new Annotation[length], 0, length);
                    if (dimensionsAnnotations == null) {
                        dimensionsAnnotations = new Annotation[dimensionsCount][];
                    }
                    dimensionsAnnotations[dimensionsCount - i - 1] = annotations;
                }
            }
        }
        return dimensionsAnnotations;
    }
    
    protected void consumeFormalParameterList() {
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeFormalParameterListopt() {
        this.pushOnAstLengthStack(0);
    }
    
    protected void consumeGenericType() {
    }
    
    protected void consumeGenericTypeArrayType() {
    }
    
    protected void consumeGenericTypeNameArrayType() {
    }
    
    protected void consumeGenericTypeWithDiamond() {
        this.pushOnGenericsLengthStack(-1);
        this.concatGenericsLists();
        --this.intPtr;
    }
    
    protected void consumeImportDeclaration() {
        final ImportReference impt = (ImportReference)this.astStack[this.astPtr];
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = this.flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }
    
    protected void consumeImportDeclarations() {
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeInsideCastExpression() {
    }
    
    protected void consumeInsideCastExpressionLL1() {
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnExpressionStack(this.getTypeReference(0));
    }
    
    protected void consumeInsideCastExpressionLL1WithBounds() {
        final int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        final TypeReference[] bounds = new TypeReference[additionalBoundsLength + 1];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        bounds[0] = this.getTypeReference(0);
        for (int i = 0; i <= additionalBoundsLength; ++i) {
            this.pushOnExpressionStack(bounds[i]);
            if (i > 0) {
                final int[] expressionLengthStack = this.expressionLengthStack;
                final int expressionLengthPtr = this.expressionLengthPtr - 1;
                expressionLengthStack[this.expressionLengthPtr = expressionLengthPtr] = expressionLengthStack[expressionLengthPtr] + 1;
            }
        }
    }
    
    protected void consumeInsideCastExpressionWithQualifiedGenerics() {
    }
    
    protected void consumeInstanceOfExpression() {
        final Expression exp = this.expressionStack[this.expressionPtr] = new InstanceOfExpression(this.expressionStack[this.expressionPtr], this.getTypeReference(this.intStack[this.intPtr--]));
        if (exp.sourceEnd == 0) {
            exp.sourceEnd = this.scanner.startPosition - 1;
        }
    }
    
    protected void consumeInstanceOfExpressionWithName() {
        final TypeReference reference = this.getTypeReference(this.intStack[this.intPtr--]);
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        final Expression exp = this.expressionStack[this.expressionPtr] = new InstanceOfExpression(this.expressionStack[this.expressionPtr], reference);
        if (exp.sourceEnd == 0) {
            exp.sourceEnd = this.scanner.startPosition - 1;
        }
    }
    
    protected void consumeInterfaceDeclaration() {
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationInto(length);
        }
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.checkConstructors(this);
        final FieldDeclaration[] fields = typeDecl.fields;
        for (int fieldCount = (fields == null) ? 0 : fields.length, i = 0; i < fieldCount; ++i) {
            final FieldDeclaration field = fields[i];
            if (field instanceof Initializer) {
                this.problemReporter().interfaceCannotHaveInitializers(typeDecl.name, field);
            }
        }
        if (this.scanner.containsAssertKeyword) {
            final TypeDeclaration typeDeclaration = typeDecl;
            typeDeclaration.bits |= 0x1;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            final TypeDeclaration typeDeclaration2 = typeDecl;
            typeDeclaration2.bits |= 0x8;
        }
        typeDecl.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }
    
    protected void consumeInterfaceHeader() {
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 49) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }
    
    protected void consumeInterfaceHeaderExtends() {
        final int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        System.arraycopy(this.astStack, this.astPtr + 1, typeDecl.superInterfaces = new TypeReference[length], 0, length);
        final TypeReference[] superinterfaces = typeDecl.superInterfaces;
        for (int i = 0, max = superinterfaces.length; i < max; ++i) {
            final TypeReference typeReference = superinterfaces[i];
            final TypeDeclaration typeDeclaration = typeDecl;
            typeDeclaration.bits |= (typeReference.bits & 0x100000);
            final TypeReference typeReference2 = typeReference;
            typeReference2.bits |= 0x10;
        }
        typeDecl.bodyStart = typeDecl.superInterfaces[length - 1].sourceEnd + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }
    
    protected void consumeInterfaceHeaderName1() {
        final TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                final TypeDeclaration typeDeclaration = typeDecl;
                typeDeclaration.bits |= 0x400;
            }
        }
        else {
            final TypeDeclaration typeDeclaration2 = typeDecl;
            typeDeclaration2.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        final long pos = this.identifierPositionStack[this.identifierPtr];
        typeDecl.sourceEnd = (int)pos;
        typeDecl.sourceStart = (int)(pos >>> 32);
        typeDecl.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
        --this.intPtr;
        typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
        typeDecl.modifiers = (this.intStack[this.intPtr--] | 0x200);
        if (typeDecl.modifiersSourceStart >= 0) {
            typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
        }
        if ((typeDecl.bits & 0x400) == 0x0 && (typeDecl.bits & 0x100) == 0x0 && this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
            final TypeDeclaration typeDeclaration3 = typeDecl;
            typeDeclaration3.bits |= 0x1000;
        }
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, typeDecl.annotations = new Annotation[length], 0, length);
        }
        typeDecl.bodyStart = typeDecl.sourceEnd + 1;
        this.pushOnAstStack(typeDecl);
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            this.currentElement = this.currentElement.add(typeDecl, 0);
            this.lastIgnoredToken = -1;
        }
        typeDecl.javadoc = this.javadoc;
        this.javadoc = null;
    }
    
    protected void consumeInterfaceMemberDeclarations() {
        this.concatNodeLists();
    }
    
    protected void consumeInterfaceMemberDeclarationsopt() {
        --this.nestedType;
    }
    
    protected void consumeInterfaceType() {
        this.pushOnAstStack(this.getTypeReference(0));
        ++this.listLength;
    }
    
    protected void consumeInterfaceTypeList() {
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeInternalCompilationUnit() {
        if (this.compilationUnit.isPackageInfo()) {
            this.compilationUnit.types = new TypeDeclaration[1];
            this.compilationUnit.createPackageInfoType();
        }
    }
    
    protected void consumeInternalCompilationUnitWithTypes() {
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (this.compilationUnit.isPackageInfo()) {
                this.compilationUnit.types = new TypeDeclaration[length + 1];
                this.astPtr -= length;
                System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 1, length);
                this.compilationUnit.createPackageInfoType();
            }
            else {
                this.compilationUnit.types = new TypeDeclaration[length];
                this.astPtr -= length;
                System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 0, length);
            }
        }
    }
    
    protected void consumeInvalidAnnotationTypeDeclaration() {
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated) {
            this.problemReporter().illegalLocalTypeDeclaration(typeDecl);
        }
        --this.astPtr;
        this.pushOnAstLengthStack(-1);
        this.concatNodeLists();
    }
    
    protected void consumeInvalidConstructorDeclaration() {
        final ConstructorDeclaration cd = (ConstructorDeclaration)this.astStack[this.astPtr];
        cd.bodyEnd = this.endPosition;
        cd.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        final ConstructorDeclaration constructorDeclaration = cd;
        constructorDeclaration.modifiers |= 0x1000000;
    }
    
    protected void consumeInvalidConstructorDeclaration(final boolean hasBody) {
        if (hasBody) {
            --this.intPtr;
        }
        if (hasBody) {
            --this.realBlockPtr;
        }
        final int length;
        if (hasBody && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
        }
        final ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)this.astStack[this.astPtr];
        constructorDeclaration.bodyEnd = this.endStatementPosition;
        constructorDeclaration.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (!hasBody) {
            final ConstructorDeclaration constructorDeclaration2 = constructorDeclaration;
            constructorDeclaration2.modifiers |= 0x1000000;
        }
    }
    
    protected void consumeInvalidEnumDeclaration() {
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated) {
            this.problemReporter().illegalLocalTypeDeclaration(typeDecl);
        }
        --this.astPtr;
        this.pushOnAstLengthStack(-1);
        this.concatNodeLists();
    }
    
    protected void consumeInvalidInterfaceDeclaration() {
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated) {
            this.problemReporter().illegalLocalTypeDeclaration(typeDecl);
        }
        --this.astPtr;
        this.pushOnAstLengthStack(-1);
        this.concatNodeLists();
    }
    
    protected void consumeInterfaceMethodDeclaration(final boolean hasSemicolonBody) {
        int explicitDeclarations = 0;
        Statement[] statements = null;
        if (!hasSemicolonBody) {
            --this.intPtr;
            --this.intPtr;
            explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
            final int length;
            if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
                if (this.options.ignoreMethodBodies) {
                    this.astPtr -= length;
                }
                else {
                    final ASTNode[] astStack = this.astStack;
                    final int astPtr = this.astPtr - length;
                    this.astPtr = astPtr;
                    System.arraycopy(astStack, astPtr + 1, statements = new Statement[length], 0, length);
                }
            }
        }
        final MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        md.statements = statements;
        md.explicitDeclarations = explicitDeclarations;
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        final boolean isDefault = (md.modifiers & 0x10000) != 0x0;
        final boolean isStatic = (md.modifiers & 0x8) != 0x0;
        final boolean bodyAllowed = isDefault || isStatic;
        if (this.parsingJava8Plus) {
            if (bodyAllowed && hasSemicolonBody) {
                final MethodDeclaration methodDeclaration = md;
                methodDeclaration.modifiers |= 0x1000000;
            }
        }
        else {
            if (isDefault) {
                this.problemReporter().defaultMethodsNotBelow18(md);
            }
            if (isStatic) {
                this.problemReporter().staticInterfaceMethodsNotBelow18(md);
            }
        }
        if (!bodyAllowed && !this.statementRecoveryActivated && !hasSemicolonBody) {
            this.problemReporter().abstractMethodNeedingNoBody(md);
        }
    }
    
    protected void consumeLabel() {
    }
    
    protected void consumeLeftParen() {
        this.pushOnIntStack(this.lParenPos);
    }
    
    protected void consumeLocalVariableDeclaration() {
        final int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        final int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
        System.arraycopy(this.astStack, startIndex, this.astStack, startIndex - 1, variableDeclaratorsCounter);
        --this.astPtr;
        this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;
        this.variablesCounter[this.nestedType] = 0;
        this.forStartPosition = 0;
    }
    
    protected void consumeLocalVariableDeclarationStatement() {
        final int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        if (variableDeclaratorsCounter == 1) {
            final LocalDeclaration localDeclaration = (LocalDeclaration)this.astStack[this.astPtr];
            if (localDeclaration.isRecoveredFromLoneIdentifier()) {
                Expression left;
                if (localDeclaration.type instanceof QualifiedTypeReference) {
                    final QualifiedTypeReference qtr = (QualifiedTypeReference)localDeclaration.type;
                    left = new QualifiedNameReference(qtr.tokens, qtr.sourcePositions, 0, 0);
                }
                else {
                    left = new SingleNameReference(localDeclaration.type.getLastToken(), 0L);
                }
                left.sourceStart = localDeclaration.type.sourceStart;
                left.sourceEnd = localDeclaration.type.sourceEnd;
                final Expression right = new SingleNameReference(localDeclaration.name, 0L);
                right.sourceStart = localDeclaration.sourceStart;
                right.sourceEnd = localDeclaration.sourceEnd;
                final Assignment assignment = new Assignment(left, right, 0);
                int end = this.endStatementPosition;
                assignment.sourceEnd = ((end == localDeclaration.sourceEnd) ? (++end) : end);
                assignment.statementEnd = end;
                this.astStack[this.astPtr] = assignment;
                if (this.recoveryScanner != null) {
                    RecoveryScannerData data;
                    int position;
                    for (data = this.recoveryScanner.getData(), position = data.insertedTokensPtr; position > 0 && data.insertedTokensPosition[position] == data.insertedTokensPosition[position - 1]; --position) {}
                    if (position >= 0) {
                        this.recoveryScanner.insertTokenAhead(70, position);
                    }
                }
                if (this.currentElement != null) {
                    this.lastCheckPoint = assignment.sourceEnd + 1;
                    this.currentElement = this.currentElement.add(assignment, 0);
                }
                return;
            }
        }
        final int[] realBlockStack = this.realBlockStack;
        final int realBlockPtr = this.realBlockPtr;
        ++realBlockStack[realBlockPtr];
        for (int i = variableDeclaratorsCounter - 1; i >= 0; --i) {
            final LocalDeclaration localDeclaration2 = (LocalDeclaration)this.astStack[this.astPtr - i];
            localDeclaration2.declarationSourceEnd = this.endStatementPosition;
            localDeclaration2.declarationEnd = this.endStatementPosition;
        }
    }
    
    protected void consumeMarkerAnnotation(final boolean isTypeAnnotation) {
        MarkerAnnotation markerAnnotation = null;
        final int oldIndex = this.identifierPtr;
        final TypeReference typeReference = this.getAnnotationType();
        markerAnnotation = new MarkerAnnotation(typeReference, this.intStack[this.intPtr--]);
        markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
        if (isTypeAnnotation) {
            this.pushOnTypeAnnotationStack(markerAnnotation);
        }
        else {
            this.pushOnExpressionStack(markerAnnotation);
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(markerAnnotation);
        }
        this.recordStringLiterals = true;
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(markerAnnotation, oldIndex);
        }
    }
    
    protected void consumeMemberValueArrayInitializer() {
        this.arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
    }
    
    protected void consumeMemberValueAsName() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
    }
    
    protected void consumeMemberValuePair() {
        final char[] simpleName = this.identifierStack[this.identifierPtr];
        final long position = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final int end = (int)position;
        final int start = (int)(position >>> 32);
        final Expression value = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        final MemberValuePair memberValuePair = new MemberValuePair(simpleName, start, end, value);
        this.pushOnAstStack(memberValuePair);
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            final RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.setKind(1);
        }
    }
    
    protected void consumeMemberValuePairs() {
        this.concatNodeLists();
    }
    
    protected void consumeMemberValues() {
        this.concatExpressionLists();
    }
    
    protected void consumeMethodBody() {
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        --nestedMethod[nestedType];
    }
    
    protected void consumeMethodDeclaration(final boolean isNotAbstract, final boolean isDefaultMethod) {
        if (isNotAbstract) {
            --this.intPtr;
            --this.intPtr;
        }
        int explicitDeclarations = 0;
        Statement[] statements = null;
        if (isNotAbstract) {
            explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
            final int length;
            if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
                if (this.options.ignoreMethodBodies) {
                    this.astPtr -= length;
                }
                else {
                    final ASTNode[] astStack = this.astStack;
                    final int astPtr = this.astPtr - length;
                    this.astPtr = astPtr;
                    System.arraycopy(astStack, astPtr + 1, statements = new Statement[length], 0, length);
                }
            }
        }
        final MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        md.statements = statements;
        md.explicitDeclarations = explicitDeclarations;
        if (!isNotAbstract) {
            final MethodDeclaration methodDeclaration = md;
            methodDeclaration.modifiers |= 0x1000000;
        }
        else if ((!this.diet || this.dietInt != 0) && statements == null && !this.containsComment(md.bodyStart, this.endPosition)) {
            final MethodDeclaration methodDeclaration2 = md;
            methodDeclaration2.bits |= 0x8;
        }
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (isDefaultMethod && !this.tolerateDefaultClassMethods) {
            if (this.options.sourceLevel >= 3407872L) {
                this.problemReporter().defaultModifierIllegallySpecified(md.sourceStart, md.sourceEnd);
            }
            else {
                this.problemReporter().illegalModifierForMethod(md);
            }
        }
    }
    
    protected void consumeMethodHeader() {
        final AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 49) {
            method.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            if (this.currentToken == 28) {
                final AbstractMethodDeclaration abstractMethodDeclaration = method;
                abstractMethodDeclaration.modifiers |= 0x1000000;
                method.declarationSourceEnd = this.scanner.currentPosition - 1;
                method.bodyEnd = this.scanner.currentPosition - 1;
                if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
                    this.currentElement = this.currentElement.parent;
                }
            }
            else if (this.currentToken == 49 && this.currentElement instanceof RecoveredMethod && ((RecoveredMethod)this.currentElement).methodDeclaration != method) {
                this.ignoreNextOpeningBrace = true;
                final RecoveredElement currentElement = this.currentElement;
                ++currentElement.bracketBalance;
            }
            this.restartRecovery = true;
        }
    }
    
    protected void consumeMethodHeaderDefaultValue() {
        final MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        final int length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length == 1) {
            --this.intPtr;
            --this.intPtr;
            if (md.isAnnotationMethod()) {
                ((AnnotationMethodDeclaration)md).defaultValue = this.expressionStack[this.expressionPtr];
                final MethodDeclaration methodDeclaration = md;
                methodDeclaration.modifiers |= 0x20000;
            }
            --this.expressionPtr;
            this.recordStringLiterals = true;
        }
        if (this.currentElement != null && md.isAnnotationMethod()) {
            this.currentElement.updateSourceEndIfNecessary(((AnnotationMethodDeclaration)md).defaultValue.sourceEnd);
        }
    }
    
    protected void consumeMethodHeaderExtendedDims() {
        final MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        final int extendedDimensions = this.intStack[this.intPtr--];
        if (md.isAnnotationMethod()) {
            ((AnnotationMethodDeclaration)md).extendedDimensions = extendedDimensions;
        }
        if (extendedDimensions != 0) {
            md.sourceEnd = this.endPosition;
            md.returnType = this.augmentTypeWithAdditionalDimensions(md.returnType, extendedDimensions, this.getAnnotationsOnDimensions(extendedDimensions), false);
            final MethodDeclaration methodDeclaration = md;
            methodDeclaration.bits |= (md.returnType.bits & 0x100000);
            if (this.currentToken == 49) {
                md.bodyStart = this.endPosition + 1;
            }
            if (this.currentElement != null) {
                this.lastCheckPoint = md.bodyStart;
            }
        }
    }
    
    protected void consumeMethodHeaderName(final boolean isAnnotationMethod) {
        MethodDeclaration md = null;
        if (isAnnotationMethod) {
            md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
            this.recordStringLiterals = false;
        }
        else {
            md = new MethodDeclaration(this.compilationUnit.compilationResult);
        }
        md.selector = this.identifierStack[this.identifierPtr];
        final long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        md.returnType = this.getTypeReference(this.intStack[this.intPtr--]);
        final MethodDeclaration methodDeclaration = md;
        methodDeclaration.bits |= (md.returnType.bits & 0x100000);
        md.declarationSourceStart = this.intStack[this.intPtr--];
        md.modifiers = this.intStack[this.intPtr--];
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, md.annotations = new Annotation[length], 0, length);
        }
        md.javadoc = this.javadoc;
        this.javadoc = null;
        md.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(md);
        md.sourceEnd = this.lParenPos;
        md.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType || Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
                this.lastCheckPoint = md.bodyStart;
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            }
            else {
                this.lastCheckPoint = md.sourceStart;
                this.restartRecovery = true;
            }
        }
    }
    
    protected void consumeMethodHeaderNameWithTypeParameters(final boolean isAnnotationMethod) {
        MethodDeclaration md = null;
        if (isAnnotationMethod) {
            md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
            this.recordStringLiterals = false;
        }
        else {
            md = new MethodDeclaration(this.compilationUnit.compilationResult);
        }
        md.selector = this.identifierStack[this.identifierPtr];
        final long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final TypeReference returnType = this.getTypeReference(this.intStack[this.intPtr--]);
        if (isAnnotationMethod) {
            this.rejectIllegalLeadingTypeAnnotations(returnType);
        }
        md.returnType = returnType;
        final MethodDeclaration methodDeclaration = md;
        methodDeclaration.bits |= (returnType.bits & 0x100000);
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, md.typeParameters = new TypeParameter[length], 0, length);
        md.declarationSourceStart = this.intStack[this.intPtr--];
        md.modifiers = this.intStack[this.intPtr--];
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, md.annotations = new Annotation[length], 0, length);
        }
        md.javadoc = this.javadoc;
        this.javadoc = null;
        md.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(md);
        md.sourceEnd = this.lParenPos;
        md.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            final boolean isType;
            if ((isType = (this.currentElement instanceof RecoveredType)) || Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
                if (isType) {
                    ((RecoveredType)this.currentElement).pendingTypeParameters = null;
                }
                this.lastCheckPoint = md.bodyStart;
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            }
            else {
                this.lastCheckPoint = md.sourceStart;
                this.restartRecovery = true;
            }
        }
    }
    
    protected void consumeMethodHeaderRightParen() {
        final int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        final AbstractMethodDeclaration md = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        md.sourceEnd = this.rParenPos;
        if (length != 0) {
            final Argument arg = (Argument)this.astStack[this.astPtr + 1];
            if (arg.isReceiver()) {
                md.receiver = (Receiver)arg;
                if (length > 1) {
                    System.arraycopy(this.astStack, this.astPtr + 2, md.arguments = new Argument[length - 1], 0, length - 1);
                }
                final Annotation[] annotations = arg.annotations;
                if (annotations != null && annotations.length > 0) {
                    final TypeReference type = arg.type;
                    if (type.annotations == null) {
                        final TypeReference typeReference = type;
                        typeReference.bits |= 0x100000;
                        type.annotations = new Annotation[type.getAnnotatableLevels()][];
                        final AbstractMethodDeclaration abstractMethodDeclaration = md;
                        abstractMethodDeclaration.bits |= 0x100000;
                    }
                    type.annotations[0] = annotations;
                    final int annotationSourceStart = annotations[0].sourceStart;
                    if (type.sourceStart > annotationSourceStart) {
                        type.sourceStart = annotationSourceStart;
                    }
                    arg.annotations = null;
                }
                final AbstractMethodDeclaration abstractMethodDeclaration2 = md;
                abstractMethodDeclaration2.bits |= (arg.type.bits & 0x100000);
            }
            else {
                System.arraycopy(this.astStack, this.astPtr + 1, md.arguments = new Argument[length], 0, length);
                for (int i = 0, max = md.arguments.length; i < max; ++i) {
                    if ((md.arguments[i].bits & 0x100000) != 0x0) {
                        final AbstractMethodDeclaration abstractMethodDeclaration3 = md;
                        abstractMethodDeclaration3.bits |= 0x100000;
                        break;
                    }
                }
            }
        }
        md.bodyStart = this.rParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = md.bodyStart;
            if (this.currentElement.parseTree() == md) {
                return;
            }
            if (md.isConstructor() && (length != 0 || this.currentToken == 49 || this.currentToken == 112)) {
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }
    
    protected void consumeMethodHeaderThrowsClause() {
        final int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        final AbstractMethodDeclaration md = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        System.arraycopy(this.astStack, this.astPtr + 1, md.thrownExceptions = new TypeReference[length], 0, length);
        md.sourceEnd = md.thrownExceptions[length - 1].sourceEnd;
        md.bodyStart = md.thrownExceptions[length - 1].sourceEnd + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = md.bodyStart;
        }
    }
    
    protected void consumeInvocationExpression() {
    }
    
    protected void consumeMethodInvocationName() {
        final MessageSend m = this.newMessageSend();
        m.sourceEnd = this.rParenPos;
        final MessageSend messageSend = m;
        final MessageSend messageSend2 = m;
        final long nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        messageSend2.nameSourcePosition = nameSourcePosition;
        messageSend.sourceStart = (int)(nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        if (this.identifierLengthStack[this.identifierLengthPtr] == 1) {
            m.receiver = ThisReference.implicitThis();
            --this.identifierLengthPtr;
        }
        else {
            final int[] identifierLengthStack = this.identifierLengthStack;
            final int identifierLengthPtr = this.identifierLengthPtr;
            --identifierLengthStack[identifierLengthPtr];
            m.receiver = this.getUnspecifiedReference();
            m.sourceStart = m.receiver.sourceStart;
        }
        final int length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--];
        if (length != 0) {
            final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
            final int typeAnnotationPtr = this.typeAnnotationPtr - length;
            this.typeAnnotationPtr = typeAnnotationPtr;
            final Annotation[] typeAnnotations;
            System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, typeAnnotations = new Annotation[length], 0, length);
            this.problemReporter().misplacedTypeAnnotations(typeAnnotations[0], typeAnnotations[typeAnnotations.length - 1]);
        }
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }
    
    protected void consumeMethodInvocationNameWithTypeArguments() {
        final MessageSend m = this.newMessageSendWithTypeArguments();
        m.sourceEnd = this.rParenPos;
        final MessageSend messageSend = m;
        final MessageSend messageSend2 = m;
        final long nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        messageSend2.nameSourcePosition = nameSourcePosition;
        messageSend.sourceStart = (int)(nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
        --this.intPtr;
        m.receiver = this.getUnspecifiedReference();
        m.sourceStart = m.receiver.sourceStart;
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }
    
    protected void consumeMethodInvocationPrimary() {
        final MessageSend messageSend2;
        final MessageSend messageSend;
        final MessageSend m = messageSend = (messageSend2 = this.newMessageSend());
        final long nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        messageSend.nameSourcePosition = nameSourcePosition;
        messageSend2.sourceStart = (int)(nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        m.receiver = this.expressionStack[this.expressionPtr];
        m.sourceStart = m.receiver.sourceStart;
        m.sourceEnd = this.rParenPos;
        this.expressionStack[this.expressionPtr] = m;
        this.consumeInvocationExpression();
    }
    
    protected void consumeMethodInvocationPrimaryWithTypeArguments() {
        final MessageSend messageSendWithTypeArguments;
        final MessageSend messageSend;
        final MessageSend m = messageSend = (messageSendWithTypeArguments = this.newMessageSendWithTypeArguments());
        final long nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        messageSend.nameSourcePosition = nameSourcePosition;
        messageSendWithTypeArguments.sourceStart = (int)(nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
        --this.intPtr;
        m.receiver = this.expressionStack[this.expressionPtr];
        m.sourceStart = m.receiver.sourceStart;
        m.sourceEnd = this.rParenPos;
        this.expressionStack[this.expressionPtr] = m;
        this.consumeInvocationExpression();
    }
    
    protected void consumeMethodInvocationSuper() {
        final MessageSend m = this.newMessageSend();
        m.sourceStart = this.intStack[this.intPtr--];
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }
    
    protected void consumeMethodInvocationSuperWithTypeArguments() {
        final MessageSend m = this.newMessageSendWithTypeArguments();
        --this.intPtr;
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
        m.sourceStart = this.intStack[this.intPtr--];
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }
    
    protected void consumeModifiers() {
        final int savedModifiersSourceStart = this.modifiersSourceStart;
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        if (this.modifiersSourceStart >= savedModifiersSourceStart) {
            this.modifiersSourceStart = savedModifiersSourceStart;
        }
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
    }
    
    protected void consumeModifiers2() {
        final int[] expressionLengthStack = this.expressionLengthStack;
        final int n = this.expressionLengthPtr - 1;
        expressionLengthStack[n] += this.expressionLengthStack[this.expressionLengthPtr--];
    }
    
    protected void consumeMultipleResources() {
        this.concatNodeLists();
    }
    
    protected void consumeTypeAnnotation() {
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3407872L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            final Annotation annotation = this.typeAnnotationStack[this.typeAnnotationPtr];
            this.problemReporter().invalidUsageOfTypeAnnotations(annotation);
        }
        this.dimensions = this.intStack[this.intPtr--];
    }
    
    protected void consumeOneMoreTypeAnnotation() {
        final int[] typeAnnotationLengthStack = this.typeAnnotationLengthStack;
        final int typeAnnotationLengthPtr = this.typeAnnotationLengthPtr - 1;
        typeAnnotationLengthStack[this.typeAnnotationLengthPtr = typeAnnotationLengthPtr] = typeAnnotationLengthStack[typeAnnotationLengthPtr] + 1;
    }
    
    protected void consumeNameArrayType() {
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
    }
    
    protected void consumeNestedMethod() {
        this.jumpOverMethodBody();
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.pushOnIntStack(this.scanner.currentPosition);
        this.consumeOpenBlock();
    }
    
    protected void consumeNestedType() {
        final int length = this.nestedMethod.length;
        if (++this.nestedType >= length) {
            System.arraycopy(this.nestedMethod, 0, this.nestedMethod = new int[length + 30], 0, length);
            System.arraycopy(this.variablesCounter, 0, this.variablesCounter = new int[length + 30], 0, length);
        }
        this.nestedMethod[this.nestedType] = 0;
        this.variablesCounter[this.nestedType] = 0;
    }
    
    protected void consumeNormalAnnotation(final boolean isTypeAnnotation) {
        NormalAnnotation normalAnnotation = null;
        final int oldIndex = this.identifierPtr;
        final TypeReference typeReference = this.getAnnotationType();
        normalAnnotation = new NormalAnnotation(typeReference, this.intStack[this.intPtr--]);
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            final ASTNode[] astStack = this.astStack;
            final int astPtr = this.astPtr - length;
            this.astPtr = astPtr;
            System.arraycopy(astStack, astPtr + 1, normalAnnotation.memberValuePairs = new MemberValuePair[length], 0, length);
        }
        normalAnnotation.declarationSourceEnd = this.rParenPos;
        if (isTypeAnnotation) {
            this.pushOnTypeAnnotationStack(normalAnnotation);
        }
        else {
            this.pushOnExpressionStack(normalAnnotation);
        }
        if (this.currentElement != null) {
            this.annotationRecoveryCheckPoint(normalAnnotation.sourceStart, normalAnnotation.declarationSourceEnd);
            if (this.currentElement instanceof RecoveredAnnotation) {
                this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(normalAnnotation, oldIndex);
            }
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(normalAnnotation);
        }
        this.recordStringLiterals = true;
    }
    
    protected void consumeOneDimLoop(final boolean isAnnotated) {
        ++this.dimensions;
        if (!isAnnotated) {
            this.pushOnTypeAnnotationLengthStack(0);
        }
    }
    
    protected void consumeOnlySynchronized() {
        this.pushOnIntStack(this.synchronizedBlockSourceStart);
        this.resetModifiers();
        --this.expressionLengthPtr;
    }
    
    protected void consumeOnlyTypeArguments() {
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            final int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeArguments((TypeReference)this.genericsStack[this.genericsPtr - length + 1], (TypeReference)this.genericsStack[this.genericsPtr]);
        }
    }
    
    protected void consumeOnlyTypeArgumentsForCastExpression() {
    }
    
    protected void consumeOpenBlock() {
        this.pushOnIntStack(this.scanner.startPosition);
        final int stackLength = this.realBlockStack.length;
        if (++this.realBlockPtr >= stackLength) {
            System.arraycopy(this.realBlockStack, 0, this.realBlockStack = new int[stackLength + 255], 0, stackLength);
        }
        this.realBlockStack[this.realBlockPtr] = 0;
    }
    
    protected void consumePackageComment() {
        if (this.options.sourceLevel >= 3211264L) {
            this.checkComment();
            this.resetModifiers();
        }
    }
    
    protected void consumePackageDeclaration() {
        final ImportReference impt = this.compilationUnit.currentPackage;
        this.compilationUnit.javadoc = this.javadoc;
        this.javadoc = null;
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = this.flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
    }
    
    protected void consumePackageDeclarationName() {
        final int length;
        final char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        final long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        final ImportReference impt = new ImportReference(tokens, positions, false, 0);
        this.compilationUnit.currentPackage = impt;
        if (this.currentToken == 28) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        }
        else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (this.javadoc != null) {
            impt.declarationSourceStart = this.javadoc.sourceStart;
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.restartRecovery = true;
        }
    }
    
    protected void consumePackageDeclarationNameWithModifiers() {
        int length;
        final char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        final long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        int packageModifiersSourceEnd;
        final int packageModifiersSourceStart = packageModifiersSourceEnd = this.intStack[this.intPtr--];
        final int packageModifiers = this.intStack[this.intPtr--];
        final ImportReference impt = new ImportReference(tokens, positions, false, packageModifiers);
        this.compilationUnit.currentPackage = impt;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            final Expression[] expressionStack = this.expressionStack;
            final int expressionPtr = this.expressionPtr - length;
            this.expressionPtr = expressionPtr;
            System.arraycopy(expressionStack, expressionPtr + 1, impt.annotations = new Annotation[length], 0, length);
            impt.declarationSourceStart = packageModifiersSourceStart;
            packageModifiersSourceEnd = this.intStack[this.intPtr--] - 2;
        }
        else {
            impt.declarationSourceStart = this.intStack[this.intPtr--];
            packageModifiersSourceEnd = impt.declarationSourceStart - 2;
            if (this.javadoc != null) {
                impt.declarationSourceStart = this.javadoc.sourceStart;
            }
        }
        if (packageModifiers != 0) {
            this.problemReporter().illegalModifiers(packageModifiersSourceStart, packageModifiersSourceEnd);
        }
        if (this.currentToken == 28) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        }
        else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.restartRecovery = true;
        }
    }
    
    protected void consumePostfixExpression() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
    }
    
    protected void consumePrimaryNoNewArray() {
        final Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
        this.updateSourcePosition(parenthesizedExpression);
        final int numberOfParenthesis = (parenthesizedExpression.bits & 0x1FE00000) >> 21;
        final Expression expression = parenthesizedExpression;
        expression.bits &= 0xE01FFFFF;
        final Expression expression2 = parenthesizedExpression;
        expression2.bits |= numberOfParenthesis + 1 << 21;
    }
    
    protected void consumePrimaryNoNewArrayArrayType() {
        --this.intPtr;
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        final ClassLiteralAccess cla;
        this.pushOnExpressionStack(cla = new ClassLiteralAccess(this.intStack[this.intPtr--], this.getTypeReference(this.intStack[this.intPtr--])));
        this.rejectIllegalTypeAnnotations(cla.type);
    }
    
    protected void consumePrimaryNoNewArrayName() {
        --this.intPtr;
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        final TypeReference typeReference = this.getTypeReference(0);
        this.rejectIllegalTypeAnnotations(typeReference);
        this.pushOnExpressionStack(new ClassLiteralAccess(this.intStack[this.intPtr--], typeReference));
    }
    
    protected void rejectIllegalLeadingTypeAnnotations(final TypeReference typeReference) {
        final Annotation[][] annotations = typeReference.annotations;
        if (annotations != null && annotations[0] != null) {
            this.problemReporter().misplacedTypeAnnotations(annotations[0][0], annotations[0][annotations[0].length - 1]);
            annotations[0] = null;
        }
    }
    
    private void rejectIllegalTypeAnnotations(final TypeReference typeReference) {
        this.rejectIllegalTypeAnnotations(typeReference, false);
    }
    
    private void rejectIllegalTypeAnnotations(final TypeReference typeReference, final boolean tolerateAnnotationsOnDimensions) {
        Annotation[][] annotations = typeReference.annotations;
        for (int i = 0, length = (annotations == null) ? 0 : annotations.length; i < length; ++i) {
            final Annotation[] misplacedAnnotations = annotations[i];
            if (misplacedAnnotations != null) {
                this.problemReporter().misplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
            }
        }
        annotations = typeReference.getAnnotationsOnDimensions(true);
        boolean tolerated = false;
        for (int j = 0, length2 = (annotations == null) ? 0 : annotations.length; j < length2; ++j) {
            final Annotation[] misplacedAnnotations = annotations[j];
            if (misplacedAnnotations != null) {
                if (tolerateAnnotationsOnDimensions) {
                    this.problemReporter().toleratedMisplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
                    tolerated = true;
                }
                else {
                    this.problemReporter().misplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
                }
            }
        }
        if (!tolerated) {
            typeReference.setAnnotationsOnDimensions(typeReference.annotations = null);
            typeReference.bits &= 0xFFEFFFFF;
        }
    }
    
    protected void consumeQualifiedSuperReceiver() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        final TypeReference typeReference = this.getTypeReference(0);
        this.rejectIllegalTypeAnnotations(typeReference);
        this.pushOnExpressionStack(new QualifiedSuperReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }
    
    protected void consumePrimaryNoNewArrayNameThis() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        final TypeReference typeReference = this.getTypeReference(0);
        this.rejectIllegalTypeAnnotations(typeReference);
        this.pushOnExpressionStack(new QualifiedThisReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }
    
    protected void consumePrimaryNoNewArrayPrimitiveArrayType() {
        --this.intPtr;
        final ClassLiteralAccess cla;
        this.pushOnExpressionStack(cla = new ClassLiteralAccess(this.intStack[this.intPtr--], this.getTypeReference(this.intStack[this.intPtr--])));
        this.rejectIllegalTypeAnnotations(cla.type, true);
    }
    
    protected void consumePrimaryNoNewArrayPrimitiveType() {
        --this.intPtr;
        final ClassLiteralAccess cla;
        this.pushOnExpressionStack(cla = new ClassLiteralAccess(this.intStack[this.intPtr--], this.getTypeReference(0)));
        this.rejectIllegalTypeAnnotations(cla.type);
    }
    
    protected void consumePrimaryNoNewArrayThis() {
        this.pushOnExpressionStack(new ThisReference(this.intStack[this.intPtr--], this.endPosition));
    }
    
    protected void consumePrimaryNoNewArrayWithName() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        final Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
        this.updateSourcePosition(parenthesizedExpression);
        final int numberOfParenthesis = (parenthesizedExpression.bits & 0x1FE00000) >> 21;
        final Expression expression = parenthesizedExpression;
        expression.bits &= 0xE01FFFFF;
        final Expression expression2 = parenthesizedExpression;
        expression2.bits |= numberOfParenthesis + 1 << 21;
    }
    
    protected void consumePrimitiveArrayType() {
    }
    
    protected void consumePrimitiveType() {
        this.pushOnIntStack(0);
    }
    
    protected void consumePushLeftBrace() {
        this.pushOnIntStack(this.endPosition);
    }
    
    protected void consumePushModifiers() {
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumePushCombineModifiers() {
        --this.intPtr;
        int newModifiers = this.intStack[this.intPtr--] | 0x10000;
        this.intPtr -= 2;
        if ((this.intStack[this.intPtr - 1] & newModifiers) != 0x0) {
            newModifiers |= 0x400000;
        }
        final int[] intStack = this.intStack;
        final int n = this.intPtr - 1;
        intStack[n] |= newModifiers;
        final int[] expressionLengthStack = this.expressionLengthStack;
        final int n2 = this.expressionLengthPtr - 1;
        expressionLengthStack[n2] += this.expressionLengthStack[this.expressionLengthPtr--];
        if (this.currentElement != null) {
            this.currentElement.addModifier(newModifiers, this.intStack[this.intPtr]);
        }
    }
    
    protected void consumePushModifiersForHeader() {
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
        this.pushOnExpressionStackLengthStack(0);
    }
    
    protected void consumePushPosition() {
        this.pushOnIntStack(this.endPosition);
    }
    
    protected void consumePushRealModifiers() {
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
    }
    
    protected void consumeQualifiedName(final boolean qualifiedNameIsAnnotated) {
        final int[] identifierLengthStack = this.identifierLengthStack;
        final int identifierLengthPtr = this.identifierLengthPtr - 1;
        identifierLengthStack[this.identifierLengthPtr = identifierLengthPtr] = identifierLengthStack[identifierLengthPtr] + 1;
        if (!qualifiedNameIsAnnotated) {
            this.pushOnTypeAnnotationLengthStack(0);
        }
    }
    
    protected void consumeUnannotatableQualifiedName() {
        final int[] identifierLengthStack = this.identifierLengthStack;
        final int identifierLengthPtr = this.identifierLengthPtr - 1;
        identifierLengthStack[this.identifierLengthPtr = identifierLengthPtr] = identifierLengthStack[identifierLengthPtr] + 1;
    }
    
    protected void consumeRecoveryMethodHeaderName() {
        boolean isAnnotationMethod = false;
        if (this.currentElement instanceof RecoveredType) {
            isAnnotationMethod = ((((RecoveredType)this.currentElement).typeDeclaration.modifiers & 0x2000) != 0x0);
        }
        else {
            final RecoveredType recoveredType = this.currentElement.enclosingType();
            if (recoveredType != null) {
                isAnnotationMethod = ((recoveredType.typeDeclaration.modifiers & 0x2000) != 0x0);
            }
        }
        this.consumeMethodHeaderName(isAnnotationMethod);
    }
    
    protected void consumeRecoveryMethodHeaderNameWithTypeParameters() {
        boolean isAnnotationMethod = false;
        if (this.currentElement instanceof RecoveredType) {
            isAnnotationMethod = ((((RecoveredType)this.currentElement).typeDeclaration.modifiers & 0x2000) != 0x0);
        }
        else {
            final RecoveredType recoveredType = this.currentElement.enclosingType();
            if (recoveredType != null) {
                isAnnotationMethod = ((recoveredType.typeDeclaration.modifiers & 0x2000) != 0x0);
            }
        }
        this.consumeMethodHeaderNameWithTypeParameters(isAnnotationMethod);
    }
    
    protected void consumeReduceImports() {
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.imports = new ImportReference[length], 0, length);
        }
    }
    
    protected void consumeReferenceType() {
        this.pushOnIntStack(0);
    }
    
    protected void consumeReferenceType1() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }
    
    protected void consumeReferenceType2() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }
    
    protected void consumeReferenceType3() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }
    
    protected void consumeResourceAsLocalVariableDeclaration() {
        this.consumeLocalVariableDeclaration();
    }
    
    protected void consumeResourceSpecification() {
    }
    
    protected void consumeResourceOptionalTrailingSemiColon(final boolean punctuated) {
        final LocalDeclaration localDeclaration = (LocalDeclaration)this.astStack[this.astPtr];
        if (punctuated) {
            localDeclaration.declarationSourceEnd = this.endStatementPosition;
        }
    }
    
    protected void consumeRestoreDiet() {
        --this.dietInt;
    }
    
    protected void consumeRightParen() {
        this.pushOnIntStack(this.rParenPos);
    }
    
    protected void consumeNonTypeUseName() {
        for (int i = this.identifierLengthStack[this.identifierLengthPtr]; i > 0 && this.typeAnnotationLengthPtr >= 0; --i) {
            final int length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--];
            if (length != 0) {
                final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
                final int typeAnnotationPtr = this.typeAnnotationPtr - length;
                this.typeAnnotationPtr = typeAnnotationPtr;
                final Annotation[] typeAnnotations;
                System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, typeAnnotations = new Annotation[length], 0, length);
                this.problemReporter().misplacedTypeAnnotations(typeAnnotations[0], typeAnnotations[typeAnnotations.length - 1]);
            }
        }
    }
    
    protected void consumeZeroTypeAnnotations() {
        this.pushOnTypeAnnotationLengthStack(0);
    }
    
    protected void consumeRule(final int act) {
        switch (act) {
            case 35: {
                this.consumePrimitiveType();
                break;
            }
            case 49: {
                this.consumeReferenceType();
                break;
            }
            case 53: {
                this.consumeClassOrInterfaceName();
                break;
            }
            case 54: {
                this.consumeClassOrInterface();
                break;
            }
            case 55: {
                this.consumeGenericType();
                break;
            }
            case 56: {
                this.consumeGenericTypeWithDiamond();
                break;
            }
            case 57: {
                this.consumeArrayTypeWithTypeArgumentsName();
                break;
            }
            case 58: {
                this.consumePrimitiveArrayType();
                break;
            }
            case 59: {
                this.consumeNameArrayType();
                break;
            }
            case 60: {
                this.consumeGenericTypeNameArrayType();
                break;
            }
            case 61: {
                this.consumeGenericTypeArrayType();
                break;
            }
            case 63: {
                this.consumeZeroTypeAnnotations();
                break;
            }
            case 68: {
                this.consumeUnannotatableQualifiedName();
                break;
            }
            case 69: {
                this.consumeQualifiedName(false);
                break;
            }
            case 70: {
                this.consumeQualifiedName(true);
                break;
            }
            case 71: {
                this.consumeZeroTypeAnnotations();
                break;
            }
            case 75: {
                this.consumeOneMoreTypeAnnotation();
                break;
            }
            case 76: {
                this.consumeTypeAnnotation();
                break;
            }
            case 77: {
                this.consumeTypeAnnotation();
                break;
            }
            case 78: {
                this.consumeTypeAnnotation();
                break;
            }
            case 79: {
                this.consumeAnnotationName();
                break;
            }
            case 80: {
                this.consumeNormalAnnotation(true);
                break;
            }
            case 81: {
                this.consumeMarkerAnnotation(true);
                break;
            }
            case 82: {
                this.consumeSingleMemberAnnotation(true);
                break;
            }
            case 83: {
                this.consumeNonTypeUseName();
                break;
            }
            case 84: {
                this.consumeZeroTypeAnnotations();
                break;
            }
            case 85: {
                this.consumeExplicitThisParameter(false);
                break;
            }
            case 86: {
                this.consumeExplicitThisParameter(true);
                break;
            }
            case 87: {
                this.consumeVariableDeclaratorIdParameter();
                break;
            }
            case 88: {
                this.consumeCompilationUnit();
                break;
            }
            case 89: {
                this.consumeInternalCompilationUnit();
                break;
            }
            case 90: {
                this.consumeInternalCompilationUnit();
                break;
            }
            case 91: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 92: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 93: {
                this.consumeInternalCompilationUnit();
                break;
            }
            case 94: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 95: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 96: {
                this.consumeEmptyInternalCompilationUnit();
                break;
            }
            case 97: {
                this.consumeReduceImports();
                break;
            }
            case 98: {
                this.consumeEnterCompilationUnit();
                break;
            }
            case 114: {
                this.consumeCatchHeader();
                break;
            }
            case 116: {
                this.consumeImportDeclarations();
                break;
            }
            case 118: {
                this.consumeTypeDeclarations();
                break;
            }
            case 119: {
                this.consumePackageDeclaration();
                break;
            }
            case 120: {
                this.consumePackageDeclarationNameWithModifiers();
                break;
            }
            case 121: {
                this.consumePackageDeclarationName();
                break;
            }
            case 122: {
                this.consumePackageComment();
                break;
            }
            case 127: {
                this.consumeImportDeclaration();
                break;
            }
            case 128: {
                this.consumeSingleTypeImportDeclarationName();
                break;
            }
            case 129: {
                this.consumeImportDeclaration();
                break;
            }
            case 130: {
                this.consumeTypeImportOnDemandDeclarationName();
                break;
            }
            case 133: {
                this.consumeEmptyTypeDeclaration();
                break;
            }
            case 137: {
                this.consumeModifiers2();
                break;
            }
            case 149: {
                this.consumeAnnotationAsModifier();
                break;
            }
            case 150: {
                this.consumeClassDeclaration();
                break;
            }
            case 151: {
                this.consumeClassHeader();
                break;
            }
            case 152: {
                this.consumeTypeHeaderNameWithTypeParameters();
                break;
            }
            case 154: {
                this.consumeClassHeaderName1();
                break;
            }
            case 155: {
                this.consumeClassHeaderExtends();
                break;
            }
            case 156: {
                this.consumeClassHeaderImplements();
                break;
            }
            case 158: {
                this.consumeInterfaceTypeList();
                break;
            }
            case 159: {
                this.consumeInterfaceType();
                break;
            }
            case 162: {
                this.consumeClassBodyDeclarations();
                break;
            }
            case 166: {
                this.consumeClassBodyDeclaration();
                break;
            }
            case 167: {
                this.consumeDiet();
                break;
            }
            case 168: {
                this.consumeClassBodyDeclaration();
                break;
            }
            case 169: {
                this.consumeCreateInitializer();
                break;
            }
            case 176: {
                this.consumeEmptyTypeDeclaration();
                break;
            }
            case 179: {
                this.consumeFieldDeclaration();
                break;
            }
            case 181: {
                this.consumeVariableDeclarators();
                break;
            }
            case 184: {
                this.consumeEnterVariable();
                break;
            }
            case 185: {
                this.consumeExitVariableWithInitialization();
                break;
            }
            case 186: {
                this.consumeExitVariableWithoutInitialization();
                break;
            }
            case 187: {
                this.consumeForceNoDiet();
                break;
            }
            case 188: {
                this.consumeRestoreDiet();
                break;
            }
            case 193: {
                this.consumeMethodDeclaration(true, false);
                break;
            }
            case 194: {
                this.consumeMethodDeclaration(true, true);
                break;
            }
            case 195: {
                this.consumeMethodDeclaration(false, false);
                break;
            }
            case 196: {
                this.consumeMethodHeader();
                break;
            }
            case 197: {
                this.consumeMethodHeader();
                break;
            }
            case 198: {
                this.consumeMethodHeaderNameWithTypeParameters(false);
                break;
            }
            case 199: {
                this.consumeMethodHeaderName(false);
                break;
            }
            case 200: {
                this.consumeMethodHeaderNameWithTypeParameters(false);
                break;
            }
            case 201: {
                this.consumeMethodHeaderName(false);
                break;
            }
            case 202: {
                this.consumePushCombineModifiers();
                break;
            }
            case 203: {
                this.consumeMethodHeaderRightParen();
                break;
            }
            case 204: {
                this.consumeMethodHeaderExtendedDims();
                break;
            }
            case 205: {
                this.consumeMethodHeaderThrowsClause();
                break;
            }
            case 206: {
                this.consumeConstructorHeader();
                break;
            }
            case 207: {
                this.consumeConstructorHeaderNameWithTypeParameters();
                break;
            }
            case 208: {
                this.consumeConstructorHeaderName();
                break;
            }
            case 210: {
                this.consumeFormalParameterList();
                break;
            }
            case 211: {
                this.consumeFormalParameter(false);
                break;
            }
            case 212: {
                this.consumeFormalParameter(true);
                break;
            }
            case 213: {
                this.consumeFormalParameter(true);
                break;
            }
            case 214: {
                this.consumeCatchFormalParameter();
                break;
            }
            case 215: {
                this.consumeCatchType();
                break;
            }
            case 216: {
                this.consumeUnionTypeAsClassType();
                break;
            }
            case 217: {
                this.consumeUnionType();
                break;
            }
            case 219: {
                this.consumeClassTypeList();
                break;
            }
            case 220: {
                this.consumeClassTypeElt();
                break;
            }
            case 221: {
                this.consumeMethodBody();
                break;
            }
            case 222: {
                this.consumeNestedMethod();
                break;
            }
            case 223: {
                this.consumeStaticInitializer();
                break;
            }
            case 224: {
                this.consumeStaticOnly();
                break;
            }
            case 225: {
                this.consumeConstructorDeclaration();
                break;
            }
            case 226: {
                this.consumeInvalidConstructorDeclaration();
                break;
            }
            case 227: {
                this.consumeExplicitConstructorInvocation(0, 3);
                break;
            }
            case 228: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(0, 3);
                break;
            }
            case 229: {
                this.consumeExplicitConstructorInvocation(0, 2);
                break;
            }
            case 230: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(0, 2);
                break;
            }
            case 231: {
                this.consumeExplicitConstructorInvocation(1, 2);
                break;
            }
            case 232: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(1, 2);
                break;
            }
            case 233: {
                this.consumeExplicitConstructorInvocation(2, 2);
                break;
            }
            case 234: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(2, 2);
                break;
            }
            case 235: {
                this.consumeExplicitConstructorInvocation(1, 3);
                break;
            }
            case 236: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(1, 3);
                break;
            }
            case 237: {
                this.consumeExplicitConstructorInvocation(2, 3);
                break;
            }
            case 238: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(2, 3);
                break;
            }
            case 239: {
                this.consumeInterfaceDeclaration();
                break;
            }
            case 240: {
                this.consumeInterfaceHeader();
                break;
            }
            case 241: {
                this.consumeTypeHeaderNameWithTypeParameters();
                break;
            }
            case 243: {
                this.consumeInterfaceHeaderName1();
                break;
            }
            case 244: {
                this.consumeInterfaceHeaderExtends();
                break;
            }
            case 247: {
                this.consumeInterfaceMemberDeclarations();
                break;
            }
            case 248: {
                this.consumeEmptyTypeDeclaration();
                break;
            }
            case 250: {
                this.consumeInterfaceMethodDeclaration(false);
                break;
            }
            case 251: {
                this.consumeInterfaceMethodDeclaration(false);
                break;
            }
            case 252: {
                this.consumeInterfaceMethodDeclaration(true);
                break;
            }
            case 253: {
                this.consumeInvalidConstructorDeclaration(true);
                break;
            }
            case 254: {
                this.consumeInvalidConstructorDeclaration(false);
                break;
            }
            case 265: {
                this.consumePushLeftBrace();
                break;
            }
            case 266: {
                this.consumeEmptyArrayInitializer();
                break;
            }
            case 267: {
                this.consumeArrayInitializer();
                break;
            }
            case 268: {
                this.consumeArrayInitializer();
                break;
            }
            case 270: {
                this.consumeVariableInitializers();
                break;
            }
            case 271: {
                this.consumeBlock();
                break;
            }
            case 272: {
                this.consumeOpenBlock();
                break;
            }
            case 273: {
                this.consumeBlockStatement();
                break;
            }
            case 274: {
                this.consumeBlockStatements();
                break;
            }
            case 281: {
                this.consumeInvalidInterfaceDeclaration();
                break;
            }
            case 282: {
                this.consumeInvalidAnnotationTypeDeclaration();
                break;
            }
            case 283: {
                this.consumeInvalidEnumDeclaration();
                break;
            }
            case 284: {
                this.consumeLocalVariableDeclarationStatement();
                break;
            }
            case 285: {
                this.consumeLocalVariableDeclaration();
                break;
            }
            case 286: {
                this.consumeLocalVariableDeclaration();
                break;
            }
            case 287: {
                this.consumePushModifiers();
                break;
            }
            case 288: {
                this.consumePushModifiersForHeader();
                break;
            }
            case 289: {
                this.consumePushRealModifiers();
                break;
            }
            case 316: {
                this.consumeEmptyStatement();
                break;
            }
            case 317: {
                this.consumeStatementLabel();
                break;
            }
            case 318: {
                this.consumeStatementLabel();
                break;
            }
            case 319: {
                this.consumeLabel();
                break;
            }
            case 320: {
                this.consumeExpressionStatement();
                break;
            }
            case 329: {
                this.consumeStatementIfNoElse();
                break;
            }
            case 330: {
                this.consumeStatementIfWithElse();
                break;
            }
            case 331: {
                this.consumeStatementIfWithElse();
                break;
            }
            case 332: {
                this.consumeStatementSwitch();
                break;
            }
            case 333: {
                this.consumeEmptySwitchBlock();
                break;
            }
            case 336: {
                this.consumeSwitchBlock();
                break;
            }
            case 338: {
                this.consumeSwitchBlockStatements();
                break;
            }
            case 339: {
                this.consumeSwitchBlockStatement();
                break;
            }
            case 341: {
                this.consumeSwitchLabels();
                break;
            }
            case 342: {
                this.consumeCaseLabel();
                break;
            }
            case 343: {
                this.consumeDefaultLabel();
                break;
            }
            case 344: {
                this.consumeStatementWhile();
                break;
            }
            case 345: {
                this.consumeStatementWhile();
                break;
            }
            case 346: {
                this.consumeStatementDo();
                break;
            }
            case 347: {
                this.consumeStatementFor();
                break;
            }
            case 348: {
                this.consumeStatementFor();
                break;
            }
            case 349: {
                this.consumeForInit();
                break;
            }
            case 353: {
                this.consumeStatementExpressionList();
                break;
            }
            case 354: {
                this.consumeSimpleAssertStatement();
                break;
            }
            case 355: {
                this.consumeAssertStatement();
                break;
            }
            case 356: {
                this.consumeStatementBreak();
                break;
            }
            case 357: {
                this.consumeStatementBreakWithLabel();
                break;
            }
            case 358: {
                this.consumeStatementContinue();
                break;
            }
            case 359: {
                this.consumeStatementContinueWithLabel();
                break;
            }
            case 360: {
                this.consumeStatementReturn();
                break;
            }
            case 361: {
                this.consumeStatementThrow();
                break;
            }
            case 362: {
                this.consumeStatementSynchronized();
                break;
            }
            case 363: {
                this.consumeOnlySynchronized();
                break;
            }
            case 364: {
                this.consumeStatementTry(false, false);
                break;
            }
            case 365: {
                this.consumeStatementTry(true, false);
                break;
            }
            case 366: {
                this.consumeStatementTry(false, true);
                break;
            }
            case 367: {
                this.consumeStatementTry(true, true);
                break;
            }
            case 368: {
                this.consumeResourceSpecification();
                break;
            }
            case 369: {
                this.consumeResourceOptionalTrailingSemiColon(false);
                break;
            }
            case 370: {
                this.consumeResourceOptionalTrailingSemiColon(true);
                break;
            }
            case 371: {
                this.consumeSingleResource();
                break;
            }
            case 372: {
                this.consumeMultipleResources();
                break;
            }
            case 373: {
                this.consumeResourceOptionalTrailingSemiColon(true);
                break;
            }
            case 374: {
                this.consumeResourceAsLocalVariableDeclaration();
                break;
            }
            case 375: {
                this.consumeResourceAsLocalVariableDeclaration();
                break;
            }
            case 377: {
                this.consumeExitTryBlock();
                break;
            }
            case 379: {
                this.consumeCatches();
                break;
            }
            case 380: {
                this.consumeStatementCatch();
                break;
            }
            case 382: {
                this.consumeLeftParen();
                break;
            }
            case 383: {
                this.consumeRightParen();
                break;
            }
            case 388: {
                this.consumePrimaryNoNewArrayThis();
                break;
            }
            case 389: {
                this.consumePrimaryNoNewArray();
                break;
            }
            case 390: {
                this.consumePrimaryNoNewArrayWithName();
                break;
            }
            case 393: {
                this.consumePrimaryNoNewArrayNameThis();
                break;
            }
            case 394: {
                this.consumeQualifiedSuperReceiver();
                break;
            }
            case 395: {
                this.consumePrimaryNoNewArrayName();
                break;
            }
            case 396: {
                this.consumePrimaryNoNewArrayArrayType();
                break;
            }
            case 397: {
                this.consumePrimaryNoNewArrayPrimitiveArrayType();
                break;
            }
            case 398: {
                this.consumePrimaryNoNewArrayPrimitiveType();
                break;
            }
            case 404: {
                this.consumeReferenceExpressionTypeArgumentsAndTrunk(false);
                break;
            }
            case 405: {
                this.consumeReferenceExpressionTypeArgumentsAndTrunk(true);
                break;
            }
            case 406: {
                this.consumeReferenceExpressionTypeForm(true);
                break;
            }
            case 407: {
                this.consumeReferenceExpressionTypeForm(false);
                break;
            }
            case 408: {
                this.consumeReferenceExpressionGenericTypeForm();
                break;
            }
            case 409: {
                this.consumeReferenceExpressionPrimaryForm();
                break;
            }
            case 410: {
                this.consumeReferenceExpressionPrimaryForm();
                break;
            }
            case 411: {
                this.consumeReferenceExpressionSuperForm();
                break;
            }
            case 412: {
                this.consumeEmptyTypeArguments();
                break;
            }
            case 414: {
                this.consumeIdentifierOrNew(false);
                break;
            }
            case 415: {
                this.consumeIdentifierOrNew(true);
                break;
            }
            case 416: {
                this.consumeLambdaExpression();
                break;
            }
            case 417: {
                this.consumeNestedLambda();
                break;
            }
            case 418: {
                this.consumeTypeElidedLambdaParameter(false);
                break;
            }
            case 424: {
                this.consumeFormalParameterList();
                break;
            }
            case 425: {
                this.consumeTypeElidedLambdaParameter(true);
                break;
            }
            case 428: {
                this.consumeElidedLeftBraceAndReturn();
                break;
            }
            case 429: {
                this.consumeAllocationHeader();
                break;
            }
            case 430: {
                this.consumeClassInstanceCreationExpressionWithTypeArguments();
                break;
            }
            case 431: {
                this.consumeClassInstanceCreationExpression();
                break;
            }
            case 432: {
                this.consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            }
            case 433: {
                this.consumeClassInstanceCreationExpressionQualified();
                break;
            }
            case 434: {
                this.consumeClassInstanceCreationExpressionQualified();
                break;
            }
            case 435: {
                this.consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            }
            case 436: {
                this.consumeEnterInstanceCreationArgumentList();
                break;
            }
            case 437: {
                this.consumeClassInstanceCreationExpressionName();
                break;
            }
            case 438: {
                this.consumeClassBodyopt();
                break;
            }
            case 440: {
                this.consumeEnterAnonymousClassBody(false);
                break;
            }
            case 441: {
                this.consumeClassBodyopt();
                break;
            }
            case 443: {
                this.consumeEnterAnonymousClassBody(true);
                break;
            }
            case 445: {
                this.consumeArgumentList();
                break;
            }
            case 446: {
                this.consumeArrayCreationHeader();
                break;
            }
            case 447: {
                this.consumeArrayCreationHeader();
                break;
            }
            case 448: {
                this.consumeArrayCreationExpressionWithoutInitializer();
                break;
            }
            case 449: {
                this.consumeArrayCreationExpressionWithInitializer();
                break;
            }
            case 450: {
                this.consumeArrayCreationExpressionWithoutInitializer();
                break;
            }
            case 451: {
                this.consumeArrayCreationExpressionWithInitializer();
                break;
            }
            case 453: {
                this.consumeDimWithOrWithOutExprs();
                break;
            }
            case 455: {
                this.consumeDimWithOrWithOutExpr();
                break;
            }
            case 456: {
                this.consumeDims();
                break;
            }
            case 459: {
                this.consumeOneDimLoop(false);
                break;
            }
            case 460: {
                this.consumeOneDimLoop(true);
                break;
            }
            case 461: {
                this.consumeFieldAccess(false);
                break;
            }
            case 462: {
                this.consumeFieldAccess(true);
                break;
            }
            case 463: {
                this.consumeFieldAccess(false);
                break;
            }
            case 464: {
                this.consumeMethodInvocationName();
                break;
            }
            case 465: {
                this.consumeMethodInvocationNameWithTypeArguments();
                break;
            }
            case 466: {
                this.consumeMethodInvocationPrimaryWithTypeArguments();
                break;
            }
            case 467: {
                this.consumeMethodInvocationPrimary();
                break;
            }
            case 468: {
                this.consumeMethodInvocationPrimary();
                break;
            }
            case 469: {
                this.consumeMethodInvocationPrimaryWithTypeArguments();
                break;
            }
            case 470: {
                this.consumeMethodInvocationSuperWithTypeArguments();
                break;
            }
            case 471: {
                this.consumeMethodInvocationSuper();
                break;
            }
            case 472: {
                this.consumeArrayAccess(true);
                break;
            }
            case 473: {
                this.consumeArrayAccess(false);
                break;
            }
            case 474: {
                this.consumeArrayAccess(false);
                break;
            }
            case 476: {
                this.consumePostfixExpression();
                break;
            }
            case 479: {
                this.consumeUnaryExpression(14, true);
                break;
            }
            case 480: {
                this.consumeUnaryExpression(13, true);
                break;
            }
            case 481: {
                this.consumePushPosition();
                break;
            }
            case 484: {
                this.consumeUnaryExpression(14);
                break;
            }
            case 485: {
                this.consumeUnaryExpression(13);
                break;
            }
            case 487: {
                this.consumeUnaryExpression(14, false);
                break;
            }
            case 488: {
                this.consumeUnaryExpression(13, false);
                break;
            }
            case 490: {
                this.consumeUnaryExpression(12);
                break;
            }
            case 491: {
                this.consumeUnaryExpression(11);
                break;
            }
            case 493: {
                this.consumeCastExpressionWithPrimitiveType();
                break;
            }
            case 494: {
                this.consumeCastExpressionWithGenericsArray();
                break;
            }
            case 495: {
                this.consumeCastExpressionWithQualifiedGenericsArray();
                break;
            }
            case 496: {
                this.consumeCastExpressionLL1();
                break;
            }
            case 497: {
                this.consumeCastExpressionLL1WithBounds();
                break;
            }
            case 498: {
                this.consumeCastExpressionWithNameArray();
                break;
            }
            case 499: {
                this.consumeZeroAdditionalBounds();
                break;
            }
            case 503: {
                this.consumeOnlyTypeArgumentsForCastExpression();
                break;
            }
            case 504: {
                this.consumeInsideCastExpression();
                break;
            }
            case 505: {
                this.consumeInsideCastExpressionLL1();
                break;
            }
            case 506: {
                this.consumeInsideCastExpressionLL1WithBounds();
                break;
            }
            case 507: {
                this.consumeInsideCastExpressionWithQualifiedGenerics();
                break;
            }
            case 509: {
                this.consumeBinaryExpression(15);
                break;
            }
            case 510: {
                this.consumeBinaryExpression(9);
                break;
            }
            case 511: {
                this.consumeBinaryExpression(16);
                break;
            }
            case 513: {
                this.consumeBinaryExpression(14);
                break;
            }
            case 514: {
                this.consumeBinaryExpression(13);
                break;
            }
            case 516: {
                this.consumeBinaryExpression(10);
                break;
            }
            case 517: {
                this.consumeBinaryExpression(17);
                break;
            }
            case 518: {
                this.consumeBinaryExpression(19);
                break;
            }
            case 520: {
                this.consumeBinaryExpression(4);
                break;
            }
            case 521: {
                this.consumeBinaryExpression(6);
                break;
            }
            case 522: {
                this.consumeBinaryExpression(5);
                break;
            }
            case 523: {
                this.consumeBinaryExpression(7);
                break;
            }
            case 525: {
                this.consumeInstanceOfExpression();
                break;
            }
            case 527: {
                this.consumeEqualityExpression(18);
                break;
            }
            case 528: {
                this.consumeEqualityExpression(29);
                break;
            }
            case 530: {
                this.consumeBinaryExpression(2);
                break;
            }
            case 532: {
                this.consumeBinaryExpression(8);
                break;
            }
            case 534: {
                this.consumeBinaryExpression(3);
                break;
            }
            case 536: {
                this.consumeBinaryExpression(0);
                break;
            }
            case 538: {
                this.consumeBinaryExpression(1);
                break;
            }
            case 540: {
                this.consumeConditionalExpression(23);
                break;
            }
            case 543: {
                this.consumeAssignment();
                break;
            }
            case 545: {
                this.ignoreExpressionAssignment();
                break;
            }
            case 546: {
                this.consumeAssignmentOperator(30);
                break;
            }
            case 547: {
                this.consumeAssignmentOperator(15);
                break;
            }
            case 548: {
                this.consumeAssignmentOperator(9);
                break;
            }
            case 549: {
                this.consumeAssignmentOperator(16);
                break;
            }
            case 550: {
                this.consumeAssignmentOperator(14);
                break;
            }
            case 551: {
                this.consumeAssignmentOperator(13);
                break;
            }
            case 552: {
                this.consumeAssignmentOperator(10);
                break;
            }
            case 553: {
                this.consumeAssignmentOperator(17);
                break;
            }
            case 554: {
                this.consumeAssignmentOperator(19);
                break;
            }
            case 555: {
                this.consumeAssignmentOperator(2);
                break;
            }
            case 556: {
                this.consumeAssignmentOperator(8);
                break;
            }
            case 557: {
                this.consumeAssignmentOperator(3);
                break;
            }
            case 558: {
                this.consumeExpression();
                break;
            }
            case 561: {
                this.consumeEmptyExpression();
                break;
            }
            case 566: {
                this.consumeEmptyClassBodyDeclarationsopt();
                break;
            }
            case 567: {
                this.consumeClassBodyDeclarationsopt();
                break;
            }
            case 568: {
                this.consumeDefaultModifiers();
                break;
            }
            case 569: {
                this.consumeModifiers();
                break;
            }
            case 570: {
                this.consumeEmptyBlockStatementsopt();
                break;
            }
            case 572: {
                this.consumeEmptyDimsopt();
                break;
            }
            case 574: {
                this.consumeEmptyArgumentListopt();
                break;
            }
            case 578: {
                this.consumeFormalParameterListopt();
                break;
            }
            case 582: {
                this.consumeEmptyInterfaceMemberDeclarationsopt();
                break;
            }
            case 583: {
                this.consumeInterfaceMemberDeclarationsopt();
                break;
            }
            case 584: {
                this.consumeNestedType();
                break;
            }
            case 585: {
                this.consumeEmptyForInitopt();
                break;
            }
            case 587: {
                this.consumeEmptyForUpdateopt();
                break;
            }
            case 591: {
                this.consumeEmptyCatchesopt();
                break;
            }
            case 593: {
                this.consumeEnumDeclaration();
                break;
            }
            case 594: {
                this.consumeEnumHeader();
                break;
            }
            case 595: {
                this.consumeEnumHeaderName();
                break;
            }
            case 596: {
                this.consumeEnumHeaderNameWithTypeParameters();
                break;
            }
            case 597: {
                this.consumeEnumBodyNoConstants();
                break;
            }
            case 598: {
                this.consumeEnumBodyNoConstants();
                break;
            }
            case 599: {
                this.consumeEnumBodyWithConstants();
                break;
            }
            case 600: {
                this.consumeEnumBodyWithConstants();
                break;
            }
            case 602: {
                this.consumeEnumConstants();
                break;
            }
            case 603: {
                this.consumeEnumConstantHeaderName();
                break;
            }
            case 604: {
                this.consumeEnumConstantHeader();
                break;
            }
            case 605: {
                this.consumeEnumConstantWithClassBody();
                break;
            }
            case 606: {
                this.consumeEnumConstantNoClassBody();
                break;
            }
            case 607: {
                this.consumeArguments();
                break;
            }
            case 608: {
                this.consumeEmptyArguments();
                break;
            }
            case 610: {
                this.consumeEnumDeclarations();
                break;
            }
            case 611: {
                this.consumeEmptyEnumDeclarations();
                break;
            }
            case 613: {
                this.consumeEnhancedForStatement();
                break;
            }
            case 614: {
                this.consumeEnhancedForStatement();
                break;
            }
            case 615: {
                this.consumeEnhancedForStatementHeaderInit(false);
                break;
            }
            case 616: {
                this.consumeEnhancedForStatementHeaderInit(true);
                break;
            }
            case 617: {
                this.consumeEnhancedForStatementHeader();
                break;
            }
            case 618: {
                this.consumeImportDeclaration();
                break;
            }
            case 619: {
                this.consumeSingleStaticImportDeclarationName();
                break;
            }
            case 620: {
                this.consumeImportDeclaration();
                break;
            }
            case 621: {
                this.consumeStaticImportOnDemandDeclarationName();
                break;
            }
            case 622: {
                this.consumeTypeArguments();
                break;
            }
            case 623: {
                this.consumeOnlyTypeArguments();
                break;
            }
            case 625: {
                this.consumeTypeArgumentList1();
                break;
            }
            case 627: {
                this.consumeTypeArgumentList();
                break;
            }
            case 628: {
                this.consumeTypeArgument();
                break;
            }
            case 632: {
                this.consumeReferenceType1();
                break;
            }
            case 633: {
                this.consumeTypeArgumentReferenceType1();
                break;
            }
            case 635: {
                this.consumeTypeArgumentList2();
                break;
            }
            case 638: {
                this.consumeReferenceType2();
                break;
            }
            case 639: {
                this.consumeTypeArgumentReferenceType2();
                break;
            }
            case 641: {
                this.consumeTypeArgumentList3();
                break;
            }
            case 644: {
                this.consumeReferenceType3();
                break;
            }
            case 645: {
                this.consumeWildcard();
                break;
            }
            case 646: {
                this.consumeWildcardWithBounds();
                break;
            }
            case 647: {
                this.consumeWildcardBoundsExtends();
                break;
            }
            case 648: {
                this.consumeWildcardBoundsSuper();
                break;
            }
            case 649: {
                this.consumeWildcard1();
                break;
            }
            case 650: {
                this.consumeWildcard1WithBounds();
                break;
            }
            case 651: {
                this.consumeWildcardBounds1Extends();
                break;
            }
            case 652: {
                this.consumeWildcardBounds1Super();
                break;
            }
            case 653: {
                this.consumeWildcard2();
                break;
            }
            case 654: {
                this.consumeWildcard2WithBounds();
                break;
            }
            case 655: {
                this.consumeWildcardBounds2Extends();
                break;
            }
            case 656: {
                this.consumeWildcardBounds2Super();
                break;
            }
            case 657: {
                this.consumeWildcard3();
                break;
            }
            case 658: {
                this.consumeWildcard3WithBounds();
                break;
            }
            case 659: {
                this.consumeWildcardBounds3Extends();
                break;
            }
            case 660: {
                this.consumeWildcardBounds3Super();
                break;
            }
            case 661: {
                this.consumeTypeParameterHeader();
                break;
            }
            case 662: {
                this.consumeTypeParameters();
                break;
            }
            case 664: {
                this.consumeTypeParameterList();
                break;
            }
            case 666: {
                this.consumeTypeParameterWithExtends();
                break;
            }
            case 667: {
                this.consumeTypeParameterWithExtendsAndBounds();
                break;
            }
            case 669: {
                this.consumeAdditionalBoundList();
                break;
            }
            case 670: {
                this.consumeAdditionalBound();
                break;
            }
            case 672: {
                this.consumeTypeParameterList1();
                break;
            }
            case 673: {
                this.consumeTypeParameter1();
                break;
            }
            case 674: {
                this.consumeTypeParameter1WithExtends();
                break;
            }
            case 675: {
                this.consumeTypeParameter1WithExtendsAndBounds();
                break;
            }
            case 677: {
                this.consumeAdditionalBoundList1();
                break;
            }
            case 678: {
                this.consumeAdditionalBound1();
                break;
            }
            case 684: {
                this.consumeUnaryExpression(14);
                break;
            }
            case 685: {
                this.consumeUnaryExpression(13);
                break;
            }
            case 688: {
                this.consumeUnaryExpression(12);
                break;
            }
            case 689: {
                this.consumeUnaryExpression(11);
                break;
            }
            case 692: {
                this.consumeBinaryExpression(15);
                break;
            }
            case 693: {
                this.consumeBinaryExpressionWithName(15);
                break;
            }
            case 694: {
                this.consumeBinaryExpression(9);
                break;
            }
            case 695: {
                this.consumeBinaryExpressionWithName(9);
                break;
            }
            case 696: {
                this.consumeBinaryExpression(16);
                break;
            }
            case 697: {
                this.consumeBinaryExpressionWithName(16);
                break;
            }
            case 699: {
                this.consumeBinaryExpression(14);
                break;
            }
            case 700: {
                this.consumeBinaryExpressionWithName(14);
                break;
            }
            case 701: {
                this.consumeBinaryExpression(13);
                break;
            }
            case 702: {
                this.consumeBinaryExpressionWithName(13);
                break;
            }
            case 704: {
                this.consumeBinaryExpression(10);
                break;
            }
            case 705: {
                this.consumeBinaryExpressionWithName(10);
                break;
            }
            case 706: {
                this.consumeBinaryExpression(17);
                break;
            }
            case 707: {
                this.consumeBinaryExpressionWithName(17);
                break;
            }
            case 708: {
                this.consumeBinaryExpression(19);
                break;
            }
            case 709: {
                this.consumeBinaryExpressionWithName(19);
                break;
            }
            case 711: {
                this.consumeBinaryExpression(4);
                break;
            }
            case 712: {
                this.consumeBinaryExpressionWithName(4);
                break;
            }
            case 713: {
                this.consumeBinaryExpression(6);
                break;
            }
            case 714: {
                this.consumeBinaryExpressionWithName(6);
                break;
            }
            case 715: {
                this.consumeBinaryExpression(5);
                break;
            }
            case 716: {
                this.consumeBinaryExpressionWithName(5);
                break;
            }
            case 717: {
                this.consumeBinaryExpression(7);
                break;
            }
            case 718: {
                this.consumeBinaryExpressionWithName(7);
                break;
            }
            case 720: {
                this.consumeInstanceOfExpressionWithName();
                break;
            }
            case 721: {
                this.consumeInstanceOfExpression();
                break;
            }
            case 723: {
                this.consumeEqualityExpression(18);
                break;
            }
            case 724: {
                this.consumeEqualityExpressionWithName(18);
                break;
            }
            case 725: {
                this.consumeEqualityExpression(29);
                break;
            }
            case 726: {
                this.consumeEqualityExpressionWithName(29);
                break;
            }
            case 728: {
                this.consumeBinaryExpression(2);
                break;
            }
            case 729: {
                this.consumeBinaryExpressionWithName(2);
                break;
            }
            case 731: {
                this.consumeBinaryExpression(8);
                break;
            }
            case 732: {
                this.consumeBinaryExpressionWithName(8);
                break;
            }
            case 734: {
                this.consumeBinaryExpression(3);
                break;
            }
            case 735: {
                this.consumeBinaryExpressionWithName(3);
                break;
            }
            case 737: {
                this.consumeBinaryExpression(0);
                break;
            }
            case 738: {
                this.consumeBinaryExpressionWithName(0);
                break;
            }
            case 740: {
                this.consumeBinaryExpression(1);
                break;
            }
            case 741: {
                this.consumeBinaryExpressionWithName(1);
                break;
            }
            case 743: {
                this.consumeConditionalExpression(23);
                break;
            }
            case 744: {
                this.consumeConditionalExpressionWithName(23);
                break;
            }
            case 748: {
                this.consumeAnnotationTypeDeclarationHeaderName();
                break;
            }
            case 749: {
                this.consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
                break;
            }
            case 750: {
                this.consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
                break;
            }
            case 751: {
                this.consumeAnnotationTypeDeclarationHeaderName();
                break;
            }
            case 752: {
                this.consumeAnnotationTypeDeclarationHeader();
                break;
            }
            case 753: {
                this.consumeAnnotationTypeDeclaration();
                break;
            }
            case 755: {
                this.consumeEmptyAnnotationTypeMemberDeclarationsopt();
                break;
            }
            case 756: {
                this.consumeAnnotationTypeMemberDeclarationsopt();
                break;
            }
            case 758: {
                this.consumeAnnotationTypeMemberDeclarations();
                break;
            }
            case 759: {
                this.consumeMethodHeaderNameWithTypeParameters(true);
                break;
            }
            case 760: {
                this.consumeMethodHeaderName(true);
                break;
            }
            case 761: {
                this.consumeEmptyMethodHeaderDefaultValue();
                break;
            }
            case 762: {
                this.consumeMethodHeaderDefaultValue();
                break;
            }
            case 763: {
                this.consumeMethodHeader();
                break;
            }
            case 764: {
                this.consumeAnnotationTypeMemberDeclaration();
                break;
            }
            case 772: {
                this.consumeAnnotationName();
                break;
            }
            case 773: {
                this.consumeNormalAnnotation(false);
                break;
            }
            case 774: {
                this.consumeEmptyMemberValuePairsopt();
                break;
            }
            case 777: {
                this.consumeMemberValuePairs();
                break;
            }
            case 778: {
                this.consumeMemberValuePair();
                break;
            }
            case 779: {
                this.consumeEnterMemberValue();
                break;
            }
            case 780: {
                this.consumeExitMemberValue();
                break;
            }
            case 782: {
                this.consumeMemberValueAsName();
                break;
            }
            case 785: {
                this.consumeMemberValueArrayInitializer();
                break;
            }
            case 786: {
                this.consumeMemberValueArrayInitializer();
                break;
            }
            case 787: {
                this.consumeEmptyMemberValueArrayInitializer();
                break;
            }
            case 788: {
                this.consumeEmptyMemberValueArrayInitializer();
                break;
            }
            case 789: {
                this.consumeEnterMemberValueArrayInitializer();
                break;
            }
            case 791: {
                this.consumeMemberValues();
                break;
            }
            case 792: {
                this.consumeMarkerAnnotation(false);
                break;
            }
            case 793: {
                this.consumeSingleMemberAnnotationMemberValue();
                break;
            }
            case 794: {
                this.consumeSingleMemberAnnotation(false);
                break;
            }
            case 795: {
                this.consumeRecoveryMethodHeaderNameWithTypeParameters();
                break;
            }
            case 796: {
                this.consumeRecoveryMethodHeaderName();
                break;
            }
            case 797: {
                this.consumeRecoveryMethodHeaderNameWithTypeParameters();
                break;
            }
            case 798: {
                this.consumeRecoveryMethodHeaderName();
                break;
            }
            case 799: {
                this.consumeMethodHeader();
                break;
            }
            case 800: {
                this.consumeMethodHeader();
                break;
            }
        }
    }
    
    protected void consumeVariableDeclaratorIdParameter() {
        this.pushOnIntStack(1);
    }
    
    protected void consumeExplicitThisParameter(final boolean isQualified) {
        NameReference qualifyingNameReference = null;
        if (isQualified) {
            qualifyingNameReference = this.getUnspecifiedReference(false);
        }
        this.pushOnExpressionStack(qualifyingNameReference);
        final int thisStart = this.intStack[this.intPtr--];
        this.pushIdentifier(ConstantPool.This, ((long)thisStart << 32) + (thisStart + 3));
        this.pushOnIntStack(0);
        this.pushOnIntStack(0);
    }
    
    protected boolean isAssistParser() {
        return false;
    }
    
    protected void consumeNestedLambda() {
        this.consumeNestedType();
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        final LambdaExpression lambda = new LambdaExpression(this.compilationUnit.compilationResult, this.isAssistParser());
        this.pushOnAstStack(lambda);
        this.processingLambdaParameterList = true;
    }
    
    protected void consumeLambdaHeader() {
        final int arrowPosition = this.scanner.currentPosition - 1;
        Argument[] arguments = null;
        final int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        if (length != 0) {
            System.arraycopy(this.astStack, this.astPtr + 1, arguments = new Argument[length], 0, length);
        }
        for (final Argument argument : arguments) {
            if (argument.isReceiver()) {
                this.problemReporter().illegalThis(argument);
            }
            if (argument.name.length == 1 && argument.name[0] == '_') {
                this.problemReporter().illegalUseOfUnderscoreAsAnIdentifier(argument.sourceStart, argument.sourceEnd, true);
            }
        }
        final LambdaExpression lexp = (LambdaExpression)this.astStack[this.astPtr];
        lexp.setArguments(arguments);
        lexp.setArrowPosition(arrowPosition);
        lexp.sourceEnd = this.intStack[this.intPtr--];
        lexp.sourceStart = this.intStack[this.intPtr--];
        lexp.hasParentheses = (this.scanner.getSource()[lexp.sourceStart] == '(');
        this.listLength -= ((arguments == null) ? 0 : arguments.length);
        this.processingLambdaParameterList = false;
        if (this.currentElement != null) {
            this.lastCheckPoint = arrowPosition + 1;
            final RecoveredElement currentElement = this.currentElement;
            ++currentElement.lambdaNestLevel;
        }
    }
    
    protected void consumeLambdaExpression() {
        --this.nestedType;
        --this.astLengthPtr;
        Statement body = (Statement)this.astStack[this.astPtr--];
        if (body instanceof Block && this.options.ignoreMethodBodies) {
            final Statement oldBody = body;
            body = new Block(0);
            body.sourceStart = oldBody.sourceStart;
            body.sourceEnd = oldBody.sourceEnd;
        }
        final LambdaExpression lexp = (LambdaExpression)this.astStack[this.astPtr--];
        --this.astLengthPtr;
        lexp.setBody(body);
        lexp.sourceEnd = body.sourceEnd;
        if (body instanceof Expression) {
            final Expression expression = (Expression)body;
            expression.statementEnd = body.sourceEnd;
        }
        if (!this.parsingJava8Plus) {
            this.problemReporter().lambdaExpressionsNotBelow18(lexp);
        }
        this.pushOnExpressionStack(lexp);
        if (this.currentElement != null) {
            this.lastCheckPoint = body.sourceEnd + 1;
            final RecoveredElement currentElement = this.currentElement;
            --currentElement.lambdaNestLevel;
        }
        this.referenceContext.compilationResult().hasFunctionalTypes = true;
        this.markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.LAMBDA);
        if (lexp.compilationResult.getCompilationUnit() == null) {
            final int length = lexp.sourceEnd - lexp.sourceStart + 1;
            System.arraycopy(this.scanner.getSource(), lexp.sourceStart, lexp.text = new char[length], 0, length);
        }
    }
    
    protected Argument typeElidedArgument() {
        --this.identifierLengthPtr;
        final char[] identifierName = this.identifierStack[this.identifierPtr];
        final long namePositions = this.identifierPositionStack[this.identifierPtr--];
        final Argument arg = new Argument(identifierName, namePositions, null, 0, true);
        arg.declarationSourceStart = (int)(namePositions >>> 32);
        return arg;
    }
    
    protected void consumeTypeElidedLambdaParameter(final boolean parenthesized) {
        int modifier = 0;
        int annotationLength = 0;
        int modifiersStart = 0;
        if (parenthesized) {
            modifiersStart = this.intStack[this.intPtr--];
            modifier = this.intStack[this.intPtr--];
            annotationLength = this.expressionLengthStack[this.expressionLengthPtr--];
            this.expressionPtr -= annotationLength;
        }
        final Argument arg = this.typeElidedArgument();
        if (modifier != 0 || annotationLength != 0) {
            this.problemReporter().illegalModifiersForElidedType(arg);
            arg.declarationSourceStart = modifiersStart;
        }
        if (!parenthesized) {
            this.pushOnIntStack(arg.declarationSourceStart);
            this.pushOnIntStack(arg.declarationSourceEnd);
        }
        this.pushOnAstStack(arg);
        ++this.listLength;
    }
    
    protected void consumeElidedLeftBraceAndReturn() {
        final int stackLength = this.stateStackLengthStack.length;
        if (++this.valueLambdaNestDepth >= stackLength) {
            System.arraycopy(this.stateStackLengthStack, 0, this.stateStackLengthStack = new int[stackLength + 4], 0, stackLength);
        }
        this.stateStackLengthStack[this.valueLambdaNestDepth] = this.stateStackTop;
    }
    
    protected void consumeExpression() {
        if (this.valueLambdaNestDepth >= 0 && this.stateStackLengthStack[this.valueLambdaNestDepth] == this.stateStackTop - 1) {
            --this.valueLambdaNestDepth;
            this.scanner.ungetToken(this.currentToken);
            this.currentToken = 66;
            final Expression exp = this.expressionStack[this.expressionPtr--];
            --this.expressionLengthPtr;
            this.pushOnAstStack(exp);
        }
    }
    
    protected void consumeIdentifierOrNew(final boolean newForm) {
        if (newForm) {
            final int newStart = this.intStack[this.intPtr--];
            this.pushIdentifier(ConstantPool.Init, ((long)newStart << 32) + (newStart + 2));
        }
    }
    
    protected void consumeEmptyTypeArguments() {
        this.pushOnGenericsLengthStack(0);
    }
    
    public ReferenceExpression newReferenceExpression() {
        return new ReferenceExpression();
    }
    
    protected void consumeReferenceExpressionTypeForm(final boolean isPrimitive) {
        final ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        final int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        final char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            --this.intPtr;
        }
        final int dimension = this.intStack[this.intPtr--];
        boolean typeAnnotatedName = false;
        for (int i = this.identifierLengthStack[this.identifierLengthPtr], j = 0; i > 0 && this.typeAnnotationLengthPtr >= 0; --i, ++j) {
            length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr - j];
            if (length != 0) {
                typeAnnotatedName = true;
                break;
            }
        }
        if (dimension > 0 || typeAnnotatedName) {
            if (!isPrimitive) {
                this.pushOnGenericsLengthStack(0);
                this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
            }
            referenceExpression.initialize(this.compilationUnit.compilationResult, this.getTypeReference(dimension), typeArguments, selector, sourceEnd);
        }
        else {
            referenceExpression.initialize(this.compilationUnit.compilationResult, this.getUnspecifiedReference(), typeArguments, selector, sourceEnd);
        }
        this.consumeReferenceExpression(referenceExpression);
    }
    
    protected void consumeReferenceExpressionPrimaryForm() {
        final ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        final int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        final char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            --this.intPtr;
        }
        final Expression primary = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        referenceExpression.initialize(this.compilationUnit.compilationResult, primary, typeArguments, selector, sourceEnd);
        this.consumeReferenceExpression(referenceExpression);
    }
    
    protected void consumeReferenceExpressionSuperForm() {
        final ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        final int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        final char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            --this.intPtr;
        }
        final SuperReference superReference = new SuperReference(this.intStack[this.intPtr--], this.endPosition);
        referenceExpression.initialize(this.compilationUnit.compilationResult, superReference, typeArguments, selector, sourceEnd);
        this.consumeReferenceExpression(referenceExpression);
    }
    
    protected void consumeReferenceExpression(final ReferenceExpression referenceExpression) {
        this.pushOnExpressionStack(referenceExpression);
        if (!this.parsingJava8Plus) {
            this.problemReporter().referenceExpressionsNotBelow18(referenceExpression);
        }
        if (referenceExpression.compilationResult.getCompilationUnit() == null) {
            final int length = referenceExpression.sourceEnd - referenceExpression.sourceStart + 1;
            System.arraycopy(this.scanner.getSource(), referenceExpression.sourceStart, referenceExpression.text = new char[length], 0, length);
        }
        this.referenceContext.compilationResult().hasFunctionalTypes = true;
        this.markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.METHOD_REFERENCE);
    }
    
    protected void consumeReferenceExpressionTypeArgumentsAndTrunk(final boolean qualified) {
        this.pushOnIntStack(qualified ? 1 : 0);
        this.pushOnIntStack(this.scanner.startPosition - 1);
    }
    
    protected void consumeReferenceExpressionGenericTypeForm() {
        final ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        final int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        final char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        final int length = this.genericsLengthStack[this.genericsLengthPtr--];
        if (length > 0) {
            this.genericsPtr -= length;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments = new TypeReference[length], 0, length);
            --this.intPtr;
        }
        final int typeSourceEnd = this.intStack[this.intPtr--];
        final boolean qualified = this.intStack[this.intPtr--] != 0;
        final int dims = this.intStack[this.intPtr--];
        TypeReference type;
        if (qualified) {
            final Annotation[][] annotationsOnDimensions = (Annotation[][])((dims == 0) ? null : this.getAnnotationsOnDimensions(dims));
            final TypeReference rightSide = this.getTypeReference(0);
            type = this.computeQualifiedGenericsFromRightSide(rightSide, dims, annotationsOnDimensions);
        }
        else {
            this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
            type = this.getTypeReference(dims);
        }
        --this.intPtr;
        type.sourceEnd = typeSourceEnd;
        referenceExpression.initialize(this.compilationUnit.compilationResult, type, typeArguments, selector, sourceEnd);
        this.consumeReferenceExpression(referenceExpression);
    }
    
    protected void consumeEnterInstanceCreationArgumentList() {
    }
    
    protected void consumeSimpleAssertStatement() {
        --this.expressionLengthPtr;
        this.pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }
    
    protected void consumeSingleMemberAnnotation(final boolean isTypeAnnotation) {
        SingleMemberAnnotation singleMemberAnnotation = null;
        final int oldIndex = this.identifierPtr;
        final TypeReference typeReference = this.getAnnotationType();
        singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.intStack[this.intPtr--]);
        singleMemberAnnotation.memberValue = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
        if (isTypeAnnotation) {
            this.pushOnTypeAnnotationStack(singleMemberAnnotation);
        }
        else {
            this.pushOnExpressionStack(singleMemberAnnotation);
        }
        if (this.currentElement != null) {
            this.annotationRecoveryCheckPoint(singleMemberAnnotation.sourceStart, singleMemberAnnotation.declarationSourceEnd);
            if (this.currentElement instanceof RecoveredAnnotation) {
                this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(singleMemberAnnotation, oldIndex);
            }
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(singleMemberAnnotation);
        }
        this.recordStringLiterals = true;
    }
    
    protected void consumeSingleMemberAnnotationMemberValue() {
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            final RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.setKind(2);
        }
    }
    
    protected void consumeSingleResource() {
    }
    
    protected void consumeSingleStaticImportDeclarationName() {
        final int length;
        final char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        final long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        final ImportReference impt;
        this.pushOnAstStack(impt = new ImportReference(tokens, positions, false, 8));
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
        if (this.currentToken == 28) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        }
        else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            impt.modifiers = 0;
            this.problemReporter().invalidUsageOfStaticImports(impt);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }
    
    protected void consumeSingleTypeImportDeclarationName() {
        final int length;
        final char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        final long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        final ImportReference impt;
        this.pushOnAstStack(impt = new ImportReference(tokens, positions, false, 0));
        if (this.currentToken == 28) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        }
        else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }
    
    protected void consumeStatementBreak() {
        this.pushOnAstStack(new BreakStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
        if (this.pendingRecoveredType != null) {
            if (this.pendingRecoveredType.allocation == null && this.endPosition <= this.pendingRecoveredType.declarationSourceEnd) {
                this.astStack[this.astPtr] = this.pendingRecoveredType;
                this.pendingRecoveredType = null;
                return;
            }
            this.pendingRecoveredType = null;
        }
    }
    
    protected void consumeStatementBreakWithLabel() {
        this.pushOnAstStack(new BreakStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        --this.identifierLengthPtr;
    }
    
    protected void consumeStatementCatch() {
        --this.astLengthPtr;
        this.listLength = 0;
    }
    
    protected void consumeStatementContinue() {
        this.pushOnAstStack(new ContinueStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
    }
    
    protected void consumeStatementContinueWithLabel() {
        this.pushOnAstStack(new ContinueStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        --this.identifierLengthPtr;
    }
    
    protected void consumeStatementDo() {
        --this.intPtr;
        final Statement statement = (Statement)this.astStack[this.astPtr];
        --this.expressionLengthPtr;
        this.astStack[this.astPtr] = new DoStatement(this.expressionStack[this.expressionPtr--], statement, this.intStack[this.intPtr--], this.endStatementPosition);
    }
    
    protected void consumeStatementExpressionList() {
        this.concatExpressionLists();
    }
    
    protected void consumeStatementFor() {
        Expression cond = null;
        boolean scope = true;
        --this.astLengthPtr;
        final Statement statement = (Statement)this.astStack[this.astPtr--];
        int length;
        Statement[] updates;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) == 0) {
            updates = null;
        }
        else {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, updates = new Statement[length], 0, length);
        }
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
            cond = this.expressionStack[this.expressionPtr--];
        }
        Statement[] inits;
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 0) {
            inits = null;
            scope = false;
        }
        else if (length == -1) {
            scope = false;
            length = this.expressionLengthStack[this.expressionLengthPtr--];
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, inits = new Statement[length], 0, length);
        }
        else {
            this.astPtr -= length;
            System.arraycopy(this.astStack, this.astPtr + 1, inits = new Statement[length], 0, length);
        }
        this.pushOnAstStack(new ForStatement(inits, cond, updates, statement, scope, this.intStack[this.intPtr--], this.endStatementPosition));
    }
    
    protected void consumeStatementIfNoElse() {
        --this.expressionLengthPtr;
        final Statement thenStatement = (Statement)this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new IfStatement(this.expressionStack[this.expressionPtr--], thenStatement, this.intStack[this.intPtr--], this.endStatementPosition);
    }
    
    protected void consumeStatementIfWithElse() {
        --this.expressionLengthPtr;
        --this.astLengthPtr;
        this.astStack[--this.astPtr] = new IfStatement(this.expressionStack[this.expressionPtr--], (Statement)this.astStack[this.astPtr], (Statement)this.astStack[this.astPtr + 1], this.intStack[this.intPtr--], this.endStatementPosition);
    }
    
    protected void consumeStatementLabel() {
        final Statement statement = (Statement)this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new LabeledStatement(this.identifierStack[this.identifierPtr], statement, this.identifierPositionStack[this.identifierPtr--], this.endStatementPosition);
        --this.identifierLengthPtr;
    }
    
    protected void consumeStatementReturn() {
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
            this.pushOnAstStack(new ReturnStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        }
        else {
            this.pushOnAstStack(new ReturnStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
        }
    }
    
    protected void consumeStatementSwitch() {
        final SwitchStatement switchStatement = new SwitchStatement();
        --this.expressionLengthPtr;
        switchStatement.expression = this.expressionStack[this.expressionPtr--];
        final int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            System.arraycopy(this.astStack, this.astPtr + 1, switchStatement.statements = new Statement[length], 0, length);
        }
        switchStatement.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        this.pushOnAstStack(switchStatement);
        switchStatement.blockStart = this.intStack[this.intPtr--];
        switchStatement.sourceStart = this.intStack[this.intPtr--];
        switchStatement.sourceEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(switchStatement.blockStart, switchStatement.sourceEnd)) {
            final SwitchStatement switchStatement2 = switchStatement;
            switchStatement2.bits |= 0x8;
        }
    }
    
    protected void consumeStatementSynchronized() {
        if (this.astLengthStack[this.astLengthPtr] == 0) {
            this.astLengthStack[this.astLengthPtr] = 1;
            --this.expressionLengthPtr;
            this.astStack[++this.astPtr] = new SynchronizedStatement(this.expressionStack[this.expressionPtr--], null, this.intStack[this.intPtr--], this.endStatementPosition);
        }
        else {
            --this.expressionLengthPtr;
            this.astStack[this.astPtr] = new SynchronizedStatement(this.expressionStack[this.expressionPtr--], (Block)this.astStack[this.astPtr], this.intStack[this.intPtr--], this.endStatementPosition);
        }
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
    }
    
    protected void consumeStatementThrow() {
        --this.expressionLengthPtr;
        this.pushOnAstStack(new ThrowStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
    }
    
    protected void consumeStatementTry(final boolean withFinally, final boolean hasResources) {
        final TryStatement tryStmt = new TryStatement();
        if (withFinally) {
            --this.astLengthPtr;
            tryStmt.finallyBlock = (Block)this.astStack[this.astPtr--];
        }
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (length == 1) {
                tryStmt.catchBlocks = new Block[] { (Block)this.astStack[this.astPtr--] };
                tryStmt.catchArguments = new Argument[] { (Argument)this.astStack[this.astPtr--] };
            }
            else {
                final TryStatement tryStatement = tryStmt;
                final Block[] catchBlocks = new Block[length];
                tryStatement.catchBlocks = catchBlocks;
                final Block[] bks = catchBlocks;
                final TryStatement tryStatement2 = tryStmt;
                final Argument[] catchArguments = new Argument[length];
                tryStatement2.catchArguments = catchArguments;
                final Argument[] args = catchArguments;
                while (length-- > 0) {
                    bks[length] = (Block)this.astStack[this.astPtr--];
                    args[length] = (Argument)this.astStack[this.astPtr--];
                }
            }
        }
        --this.astLengthPtr;
        tryStmt.tryBlock = (Block)this.astStack[this.astPtr--];
        if (hasResources) {
            length = this.astLengthStack[this.astLengthPtr--];
            final LocalDeclaration[] resources = new LocalDeclaration[length];
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, resources, 0, length);
            tryStmt.resources = resources;
            if (this.options.sourceLevel < 3342336L) {
                this.problemReporter().autoManagedResourcesNotBelow17(resources);
            }
        }
        tryStmt.sourceEnd = this.endStatementPosition;
        tryStmt.sourceStart = this.intStack[this.intPtr--];
        this.pushOnAstStack(tryStmt);
    }
    
    protected void consumeStatementWhile() {
        --this.expressionLengthPtr;
        final Statement statement = (Statement)this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new WhileStatement(this.expressionStack[this.expressionPtr--], statement, this.intStack[this.intPtr--], this.endStatementPosition);
    }
    
    protected void consumeStaticImportOnDemandDeclarationName() {
        final int length;
        final char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        final long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        final ImportReference impt;
        this.pushOnAstStack(impt = new ImportReference(tokens, positions, true, 8));
        impt.trailingStarPosition = this.intStack[this.intPtr--];
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
        if (this.currentToken == 28) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        }
        else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            impt.modifiers = 0;
            this.problemReporter().invalidUsageOfStaticImports(impt);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }
    
    protected void consumeStaticInitializer() {
        final Block block = (Block)this.astStack[this.astPtr];
        if (this.diet) {
            final Block block2 = block;
            block2.bits &= 0xFFFFFFF7;
        }
        final Initializer initializer = new Initializer(block, 8);
        this.astStack[this.astPtr] = initializer;
        initializer.sourceEnd = this.endStatementPosition;
        initializer.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        --nestedMethod[nestedType];
        initializer.declarationSourceStart = this.intStack[this.intPtr--];
        initializer.bodyStart = this.intStack[this.intPtr--];
        initializer.bodyEnd = this.endPosition;
        initializer.javadoc = this.javadoc;
        this.javadoc = null;
        if (this.currentElement != null) {
            this.lastCheckPoint = initializer.declarationSourceEnd;
            this.currentElement = this.currentElement.add(initializer, 0);
            this.lastIgnoredToken = -1;
        }
    }
    
    protected void consumeStaticOnly() {
        final int savedModifiersSourceStart = this.modifiersSourceStart;
        this.checkComment();
        if (this.modifiersSourceStart >= savedModifiersSourceStart) {
            this.modifiersSourceStart = savedModifiersSourceStart;
        }
        this.pushOnIntStack(this.scanner.currentPosition);
        this.pushOnIntStack((this.modifiersSourceStart >= 0) ? this.modifiersSourceStart : this.scanner.startPosition);
        this.jumpOverMethodBody();
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.resetModifiers();
        --this.expressionLengthPtr;
        if (this.currentElement != null) {
            this.recoveredStaticInitializerStart = this.intStack[this.intPtr];
        }
    }
    
    protected void consumeSwitchBlock() {
        this.concatNodeLists();
    }
    
    protected void consumeSwitchBlockStatement() {
        this.concatNodeLists();
    }
    
    protected void consumeSwitchBlockStatements() {
        this.concatNodeLists();
    }
    
    protected void consumeSwitchLabels() {
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeToken(final int type) {
        switch (type) {
            case 110: {
                this.consumeLambdaHeader();
                break;
            }
            case 7: {
                this.colonColonStart = this.scanner.currentPosition - 2;
                break;
            }
            case 50: {
                this.flushCommentsDefinedPriorTo(this.scanner.currentPosition);
                break;
            }
            case 22: {
                this.pushIdentifier();
                if (this.scanner.useAssertAsAnIndentifier && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                    final long positions = this.identifierPositionStack[this.identifierPtr];
                    if (!this.statementRecoveryActivated) {
                        this.problemReporter().useAssertAsAnIdentifier((int)(positions >>> 32), (int)positions);
                    }
                }
                if (!this.scanner.useEnumAsAnIndentifier || this.lastErrorEndPositionBeforeRecovery >= this.scanner.currentPosition) {
                    break;
                }
                final long positions = this.identifierPositionStack[this.identifierPtr];
                if (!this.statementRecoveryActivated) {
                    this.problemReporter().useEnumAsAnIdentifier((int)(positions >>> 32), (int)positions);
                    break;
                }
                break;
            }
            case 68: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 51: {
                this.checkAndSetModifiers(1024);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 57: {
                this.checkAndSetModifiers(2048);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 52: {
                this.checkAndSetModifiers(16);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 53: {
                this.checkAndSetModifiers(256);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 54: {
                this.checkAndSetModifiers(2);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 55: {
                this.checkAndSetModifiers(4);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 56: {
                this.checkAndSetModifiers(1);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 58: {
                this.checkAndSetModifiers(128);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 59: {
                this.checkAndSetModifiers(64);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 40: {
                this.checkAndSetModifiers(8);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 41: {
                this.synchronizedBlockSourceStart = this.scanner.startPosition;
                this.checkAndSetModifiers(32);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 108: {
                this.pushIdentifier(-6);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 97: {
                this.pushIdentifier(-5);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 98: {
                this.pushIdentifier(-3);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 101: {
                this.pushIdentifier(-2);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 102: {
                this.pushIdentifier(-8);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 103: {
                this.pushIdentifier(-9);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 105: {
                this.pushIdentifier(-10);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 106: {
                this.pushIdentifier(-7);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 107: {
                this.pushIdentifier(-4);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 43: {
                this.pushOnExpressionStack(IntLiteral.buildIntLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 44: {
                this.pushOnExpressionStack(LongLiteral.buildLongLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 45: {
                this.pushOnExpressionStack(new FloatLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 46: {
                this.pushOnExpressionStack(new DoubleLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 47: {
                this.pushOnExpressionStack(new CharLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 48: {
                StringLiteral stringLiteral;
                if (this.recordStringLiterals && !this.reparsingLambdaExpression && this.checkExternalizeStrings && this.lastPosistion < this.scanner.currentPosition && !this.statementRecoveryActivated) {
                    stringLiteral = this.createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
                    this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
                }
                else {
                    stringLiteral = this.createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, 0);
                }
                this.pushOnExpressionStack(stringLiteral);
                break;
            }
            case 38: {
                this.pushOnExpressionStack(new FalseLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 42: {
                this.pushOnExpressionStack(new TrueLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 39: {
                this.pushOnExpressionStack(new NullLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 34:
            case 35: {
                this.endPosition = this.scanner.currentPosition - 1;
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 77: {
                this.forStartPosition = this.scanner.startPosition;
            }
            case 71:
            case 72:
            case 73:
            case 74:
            case 76:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 95:
            case 99:
            case 104: {
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 36: {
                this.resetModifiers();
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 67: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 69: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 75: {
                this.pushOnIntStack(this.scanner.startPosition);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            }
            case 64: {
                this.rBracketPosition = this.scanner.startPosition;
                this.endPosition = this.scanner.startPosition;
                this.endStatementPosition = this.scanner.currentPosition - 1;
                break;
            }
            case 49: {
                this.endStatementPosition = this.scanner.currentPosition - 1;
            }
            case 4:
            case 5:
            case 62:
            case 63: {
                this.endPosition = this.scanner.startPosition;
                break;
            }
            case 1:
            case 2: {
                this.endPosition = this.scanner.startPosition;
                this.endStatementPosition = this.scanner.currentPosition - 1;
                break;
            }
            case 28:
            case 32: {
                this.endStatementPosition = this.scanner.currentPosition - 1;
                this.endPosition = this.scanner.startPosition - 1;
                break;
            }
            case 25: {
                this.rParenPos = this.scanner.currentPosition - 1;
                break;
            }
            case 24: {
                this.lParenPos = this.scanner.startPosition;
                break;
            }
            case 27: {
                this.expectTypeAnnotation = true;
                this.pushOnIntStack(this.dimensions);
                this.dimensions = 0;
            }
            case 37: {
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 29: {
                this.pushOnIntStack(this.scanner.startPosition);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            }
            case 11: {
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 113: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            }
            case 70: {
                if (this.currentElement == null || !(this.currentElement instanceof RecoveredAnnotation)) {
                    break;
                }
                final RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
                if (recoveredAnnotation.memberValuPairEqualEnd == -1) {
                    recoveredAnnotation.memberValuPairEqualEnd = this.scanner.currentPosition - 1;
                    break;
                }
                break;
            }
            case 6: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            }
        }
    }
    
    protected void consumeTypeArgument() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }
    
    protected void consumeTypeArgumentList() {
        this.concatGenericsLists();
    }
    
    protected void consumeTypeArgumentList1() {
        this.concatGenericsLists();
    }
    
    protected void consumeTypeArgumentList2() {
        this.concatGenericsLists();
    }
    
    protected void consumeTypeArgumentList3() {
        this.concatGenericsLists();
    }
    
    protected void consumeTypeArgumentReferenceType1() {
        this.concatGenericsLists();
        this.pushOnGenericsStack(this.getTypeReference(0));
        --this.intPtr;
    }
    
    protected void consumeTypeArgumentReferenceType2() {
        this.concatGenericsLists();
        this.pushOnGenericsStack(this.getTypeReference(0));
        --this.intPtr;
    }
    
    protected void consumeTypeArguments() {
        this.concatGenericsLists();
        --this.intPtr;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            final int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeArguments((TypeReference)this.genericsStack[this.genericsPtr - length + 1], (TypeReference)this.genericsStack[this.genericsPtr]);
        }
    }
    
    protected void consumeTypeDeclarations() {
        this.concatNodeLists();
    }
    
    protected void consumeTypeHeaderNameWithTypeParameters() {
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        final int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeDecl.typeParameters = new TypeParameter[length], 0, length);
        typeDecl.bodyStart = typeDecl.typeParameters[length - 1].declarationSourceEnd + 1;
        this.listTypeParameterLength = 0;
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType) {
                final RecoveredType recoveredType = (RecoveredType)this.currentElement;
                recoveredType.pendingTypeParameters = null;
                this.lastCheckPoint = typeDecl.bodyStart;
            }
            else {
                this.lastCheckPoint = typeDecl.bodyStart;
                this.currentElement = this.currentElement.add(typeDecl, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }
    
    protected void consumeTypeImportOnDemandDeclarationName() {
        final int length;
        final char[][] tokens = new char[length = this.identifierLengthStack[this.identifierLengthPtr--]][];
        this.identifierPtr -= length;
        final long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        final ImportReference impt;
        this.pushOnAstStack(impt = new ImportReference(tokens, positions, true, 0));
        impt.trailingStarPosition = this.intStack[this.intPtr--];
        if (this.currentToken == 28) {
            impt.declarationSourceEnd = this.scanner.currentPosition - 1;
        }
        else {
            impt.declarationSourceEnd = impt.sourceEnd;
        }
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }
    
    protected void consumeTypeParameter1() {
    }
    
    protected void consumeTypeParameter1WithExtends() {
        final TypeReference superType = (TypeReference)this.genericsStack[this.genericsPtr--];
        --this.genericsLengthPtr;
        final TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = superType.sourceEnd;
        typeParameter.type = superType;
        final TypeReference typeReference = superType;
        typeReference.bits |= 0x10;
        final TypeParameter typeParameter2 = typeParameter;
        typeParameter2.bits |= (superType.bits & 0x100000);
        this.genericsStack[this.genericsPtr] = typeParameter;
    }
    
    protected void consumeTypeParameter1WithExtendsAndBounds() {
        final int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        final TypeReference[] bounds = new TypeReference[additionalBoundsLength];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
        final TypeReference superType = this.getTypeReference(this.intStack[this.intPtr--]);
        final TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
        typeParameter.type = superType;
        final TypeParameter typeParameter2 = typeParameter;
        typeParameter2.bits |= (superType.bits & 0x100000);
        final TypeReference typeReference = superType;
        typeReference.bits |= 0x10;
        typeParameter.bounds = bounds;
        for (int i = 0, max = bounds.length; i < max; ++i) {
            final TypeReference typeReference2;
            final TypeReference bound = typeReference2 = bounds[i];
            typeReference2.bits |= 0x10;
            final TypeParameter typeParameter3 = typeParameter;
            typeParameter3.bits |= (bound.bits & 0x100000);
        }
    }
    
    protected void consumeTypeParameterHeader() {
        final TypeParameter typeParameter = new TypeParameter();
        final int length;
        if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
            final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
            final int typeAnnotationPtr = this.typeAnnotationPtr - length;
            this.typeAnnotationPtr = typeAnnotationPtr;
            System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, typeParameter.annotations = new Annotation[length], 0, length);
            final TypeParameter typeParameter2 = typeParameter;
            typeParameter2.bits |= 0x100000;
        }
        final long pos = this.identifierPositionStack[this.identifierPtr];
        final int end = (int)pos;
        typeParameter.declarationSourceEnd = end;
        typeParameter.sourceEnd = end;
        final int start = (int)(pos >>> 32);
        typeParameter.declarationSourceStart = start;
        typeParameter.sourceStart = start;
        typeParameter.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        this.pushOnGenericsStack(typeParameter);
        ++this.listTypeParameterLength;
    }
    
    protected void consumeTypeParameterList() {
        this.concatGenericsLists();
    }
    
    protected void consumeTypeParameterList1() {
        this.concatGenericsLists();
    }
    
    protected void consumeTypeParameters() {
        final int startPos = this.intStack[this.intPtr--];
        if (this.currentElement != null && this.currentElement instanceof RecoveredType) {
            final RecoveredType recoveredType = (RecoveredType)this.currentElement;
            final int length = this.genericsLengthStack[this.genericsLengthPtr];
            final TypeParameter[] typeParameters = new TypeParameter[length];
            System.arraycopy(this.genericsStack, this.genericsPtr - length + 1, typeParameters, 0, length);
            recoveredType.add(typeParameters, startPos);
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 3211264L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            final int length2 = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeParameters((TypeParameter)this.genericsStack[this.genericsPtr - length2 + 1], (TypeParameter)this.genericsStack[this.genericsPtr]);
        }
    }
    
    protected void consumeTypeParameterWithExtends() {
        final TypeReference superType = this.getTypeReference(this.intStack[this.intPtr--]);
        final TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = superType.sourceEnd;
        typeParameter.type = superType;
        final TypeParameter typeParameter2 = typeParameter;
        typeParameter2.bits |= (superType.bits & 0x100000);
        final TypeReference typeReference = superType;
        typeReference.bits |= 0x10;
    }
    
    protected void consumeTypeParameterWithExtendsAndBounds() {
        final int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        final TypeReference[] bounds = new TypeReference[additionalBoundsLength];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
        final TypeReference superType = this.getTypeReference(this.intStack[this.intPtr--]);
        final TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.type = superType;
        final TypeParameter typeParameter2 = typeParameter;
        typeParameter2.bits |= (superType.bits & 0x100000);
        final TypeReference typeReference = superType;
        typeReference.bits |= 0x10;
        typeParameter.bounds = bounds;
        typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
        for (int i = 0, max = bounds.length; i < max; ++i) {
            final TypeReference typeReference2;
            final TypeReference bound = typeReference2 = bounds[i];
            typeReference2.bits |= 0x10;
            final TypeParameter typeParameter3 = typeParameter;
            typeParameter3.bits |= (bound.bits & 0x100000);
        }
    }
    
    protected void consumeZeroAdditionalBounds() {
        if (this.currentToken == 25) {
            this.pushOnGenericsLengthStack(0);
        }
    }
    
    protected void consumeUnaryExpression(final int op) {
        final Expression exp = this.expressionStack[this.expressionPtr];
        Expression r;
        if (op == 13) {
            if (exp instanceof IntLiteral) {
                final IntLiteral intLiteral = (IntLiteral)exp;
                final IntLiteral convertToMinValue = intLiteral.convertToMinValue();
                if (convertToMinValue == intLiteral) {
                    r = new UnaryExpression(exp, op);
                }
                else {
                    r = convertToMinValue;
                }
            }
            else if (exp instanceof LongLiteral) {
                final LongLiteral longLiteral = (LongLiteral)exp;
                final LongLiteral convertToMinValue2 = longLiteral.convertToMinValue();
                if (convertToMinValue2 == longLiteral) {
                    r = new UnaryExpression(exp, op);
                }
                else {
                    r = convertToMinValue2;
                }
            }
            else {
                r = new UnaryExpression(exp, op);
            }
        }
        else {
            r = new UnaryExpression(exp, op);
        }
        r.sourceStart = this.intStack[this.intPtr--];
        r.sourceEnd = exp.sourceEnd;
        this.expressionStack[this.expressionPtr] = r;
    }
    
    protected void consumeUnaryExpression(final int op, final boolean post) {
        final Expression leftHandSide = this.expressionStack[this.expressionPtr];
        if (leftHandSide instanceof Reference) {
            if (post) {
                this.expressionStack[this.expressionPtr] = new PostfixExpression(leftHandSide, IntLiteral.One, op, this.endStatementPosition);
            }
            else {
                this.expressionStack[this.expressionPtr] = new PrefixExpression(leftHandSide, IntLiteral.One, op, this.intStack[this.intPtr--]);
            }
        }
        else {
            if (!post) {
                --this.intPtr;
            }
            if (!this.statementRecoveryActivated) {
                this.problemReporter().invalidUnaryExpression(leftHandSide);
            }
        }
    }
    
    protected void consumeVariableDeclarators() {
        this.optimizedConcatNodeLists();
    }
    
    protected void consumeVariableInitializers() {
        this.concatExpressionLists();
    }
    
    protected void consumeWildcard() {
        final Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }
    
    protected void consumeWildcard1() {
        final Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }
    
    protected void consumeWildcard1WithBounds() {
    }
    
    protected void consumeWildcard2() {
        final Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }
    
    protected void consumeWildcard2WithBounds() {
    }
    
    protected void consumeWildcard3() {
        final Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }
    
    protected void consumeWildcard3WithBounds() {
    }
    
    protected void consumeWildcardBounds1Extends() {
        final Wildcard wildcard = new Wildcard(1);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }
    
    protected void consumeWildcardBounds1Super() {
        final Wildcard wildcard = new Wildcard(2);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }
    
    protected void consumeWildcardBounds2Extends() {
        final Wildcard wildcard = new Wildcard(1);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }
    
    protected void consumeWildcardBounds2Super() {
        final Wildcard wildcard = new Wildcard(2);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }
    
    protected void consumeWildcardBounds3Extends() {
        final Wildcard wildcard = new Wildcard(1);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }
    
    protected void consumeWildcardBounds3Super() {
        final Wildcard wildcard = new Wildcard(2);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }
    
    protected void consumeWildcardBoundsExtends() {
        final Wildcard wildcard = new Wildcard(1);
        wildcard.bound = this.getTypeReference(this.intStack[this.intPtr--]);
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }
    
    protected void consumeWildcardBoundsSuper() {
        final Wildcard wildcard = new Wildcard(2);
        wildcard.bound = this.getTypeReference(this.intStack[this.intPtr--]);
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }
    
    protected void consumeWildcardWithBounds() {
    }
    
    public boolean containsComment(final int sourceStart, final int sourceEnd) {
        for (int iComment = this.scanner.commentPtr; iComment >= 0; --iComment) {
            int commentStart = this.scanner.commentStarts[iComment];
            if (commentStart < 0) {
                commentStart = -commentStart;
            }
            if (commentStart >= sourceStart) {
                if (commentStart <= sourceEnd) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public MethodDeclaration convertToMethodDeclaration(final ConstructorDeclaration c, final CompilationResult compilationResult) {
        final MethodDeclaration m = new MethodDeclaration(compilationResult);
        m.typeParameters = c.typeParameters;
        m.sourceStart = c.sourceStart;
        m.sourceEnd = c.sourceEnd;
        m.bodyStart = c.bodyStart;
        m.bodyEnd = c.bodyEnd;
        m.declarationSourceEnd = c.declarationSourceEnd;
        m.declarationSourceStart = c.declarationSourceStart;
        m.selector = c.selector;
        m.statements = c.statements;
        m.modifiers = c.modifiers;
        m.annotations = c.annotations;
        m.arguments = c.arguments;
        m.thrownExceptions = c.thrownExceptions;
        m.explicitDeclarations = c.explicitDeclarations;
        m.returnType = null;
        m.javadoc = c.javadoc;
        m.bits = c.bits;
        return m;
    }
    
    protected TypeReference augmentTypeWithAdditionalDimensions(final TypeReference typeReference, final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        return typeReference.augmentTypeWithAdditionalDimensions(additionalDimensions, additionalAnnotations, isVarargs);
    }
    
    protected FieldDeclaration createFieldDeclaration(final char[] fieldDeclarationName, final int sourceStart, final int sourceEnd) {
        return new FieldDeclaration(fieldDeclarationName, sourceStart, sourceEnd);
    }
    
    protected JavadocParser createJavadocParser() {
        return new JavadocParser(this);
    }
    
    protected LocalDeclaration createLocalDeclaration(final char[] localDeclarationName, final int sourceStart, final int sourceEnd) {
        return new LocalDeclaration(localDeclarationName, sourceStart, sourceEnd);
    }
    
    protected StringLiteral createStringLiteral(final char[] token, final int start, final int end, final int lineNumber) {
        return new StringLiteral(token, start, end, lineNumber);
    }
    
    protected RecoveredType currentRecoveryType() {
        if (this.currentElement == null) {
            return null;
        }
        if (this.currentElement instanceof RecoveredType) {
            return (RecoveredType)this.currentElement;
        }
        return this.currentElement.enclosingType();
    }
    
    public CompilationUnitDeclaration dietParse(final ICompilationUnit sourceUnit, final CompilationResult compilationResult) {
        final boolean old = this.diet;
        final int oldInt = this.dietInt;
        CompilationUnitDeclaration parsedUnit;
        try {
            this.dietInt = 0;
            this.diet = true;
            parsedUnit = this.parse(sourceUnit, compilationResult);
        }
        finally {
            this.diet = old;
            this.dietInt = oldInt;
        }
        this.diet = old;
        this.dietInt = oldInt;
        return parsedUnit;
    }
    
    protected void dispatchDeclarationInto(final int length) {
        if (length == 0) {
            return;
        }
        final int[] flag = new int[length + 1];
        int size1 = 0;
        int size2 = 0;
        int size3 = 0;
        boolean hasAbstractMethods = false;
        for (int i = length - 1; i >= 0; --i) {
            final ASTNode astNode = this.astStack[this.astPtr--];
            if (astNode instanceof AbstractMethodDeclaration) {
                flag[i] = 2;
                ++size2;
                if (((AbstractMethodDeclaration)astNode).isAbstract()) {
                    hasAbstractMethods = true;
                }
            }
            else if (astNode instanceof TypeDeclaration) {
                flag[i] = 3;
                ++size3;
            }
            else {
                flag[i] = 1;
                ++size1;
            }
        }
        final TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (size1 != 0) {
            typeDecl.fields = new FieldDeclaration[size1];
        }
        if (size2 != 0) {
            typeDecl.methods = new AbstractMethodDeclaration[size2];
            if (hasAbstractMethods) {
                final TypeDeclaration typeDeclaration = typeDecl;
                typeDeclaration.bits |= 0x800;
            }
        }
        if (size3 != 0) {
            typeDecl.memberTypes = new TypeDeclaration[size3];
        }
        size2 = (size1 = (size3 = 0));
        int flagI = flag[0];
        int start = 0;
        for (int end = 0; end <= length; ++end) {
            if (flagI != flag[end]) {
                switch (flagI) {
                    case 1: {
                        final int length2;
                        size1 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.fields, size1 - length2, length2);
                        break;
                    }
                    case 2: {
                        final int length2;
                        size2 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.methods, size2 - length2, length2);
                        break;
                    }
                    case 3: {
                        final int length2;
                        size3 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.memberTypes, size3 - length2, length2);
                        break;
                    }
                }
                flagI = flag[start = end];
            }
        }
        if (typeDecl.memberTypes != null) {
            for (int j = typeDecl.memberTypes.length - 1; j >= 0; --j) {
                typeDecl.memberTypes[j].enclosingType = typeDecl;
            }
        }
    }
    
    protected void dispatchDeclarationIntoEnumDeclaration(final int length) {
        if (length == 0) {
            return;
        }
        final int[] flag = new int[length + 1];
        int size1 = 0;
        int size2 = 0;
        int size3 = 0;
        final TypeDeclaration enumDeclaration = (TypeDeclaration)this.astStack[this.astPtr - length];
        boolean hasAbstractMethods = false;
        int enumConstantsCounter = 0;
        for (int i = length - 1; i >= 0; --i) {
            final ASTNode astNode = this.astStack[this.astPtr--];
            if (astNode instanceof AbstractMethodDeclaration) {
                flag[i] = 2;
                ++size2;
                if (((AbstractMethodDeclaration)astNode).isAbstract()) {
                    hasAbstractMethods = true;
                }
            }
            else if (astNode instanceof TypeDeclaration) {
                flag[i] = 3;
                ++size3;
            }
            else if (astNode instanceof FieldDeclaration) {
                flag[i] = 1;
                ++size1;
                if (((FieldDeclaration)astNode).getKind() == 3) {
                    ++enumConstantsCounter;
                }
            }
        }
        if (size1 != 0) {
            enumDeclaration.fields = new FieldDeclaration[size1];
        }
        if (size2 != 0) {
            enumDeclaration.methods = new AbstractMethodDeclaration[size2];
            if (hasAbstractMethods) {
                final TypeDeclaration typeDeclaration = enumDeclaration;
                typeDeclaration.bits |= 0x800;
            }
        }
        if (size3 != 0) {
            enumDeclaration.memberTypes = new TypeDeclaration[size3];
        }
        size2 = (size1 = (size3 = 0));
        int flagI = flag[0];
        int start = 0;
        for (int end = 0; end <= length; ++end) {
            if (flagI != flag[end]) {
                switch (flagI) {
                    case 1: {
                        final int length2;
                        size1 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.fields, size1 - length2, length2);
                        break;
                    }
                    case 2: {
                        final int length2;
                        size2 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.methods, size2 - length2, length2);
                        break;
                    }
                    case 3: {
                        final int length2;
                        size3 += (length2 = end - start);
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.memberTypes, size3 - length2, length2);
                        break;
                    }
                }
                flagI = flag[start = end];
            }
        }
        if (enumDeclaration.memberTypes != null) {
            for (int j = enumDeclaration.memberTypes.length - 1; j >= 0; --j) {
                enumDeclaration.memberTypes[j].enclosingType = enumDeclaration;
            }
        }
        enumDeclaration.enumConstantsCounter = enumConstantsCounter;
    }
    
    protected CompilationUnitDeclaration endParse(final int act) {
        this.lastAct = act;
        if (this.statementRecoveryActivated) {
            final RecoveredElement recoveredElement = this.buildInitialRecoveryState();
            if (recoveredElement != null) {
                recoveredElement.topElement().updateParseTree();
            }
            if (this.hasError) {
                this.resetStacks();
            }
        }
        else if (this.currentElement != null) {
            if (Parser.VERBOSE_RECOVERY) {
                System.out.print(Messages.parser_syntaxRecovery);
                System.out.println("--------------------------");
                System.out.println(this.compilationUnit);
                System.out.println("----------------------------------");
            }
            this.currentElement.topElement().updateParseTree();
        }
        else if (this.diet & Parser.VERBOSE_RECOVERY) {
            System.out.print(Messages.parser_regularParse);
            System.out.println("--------------------------");
            System.out.println(this.compilationUnit);
            System.out.println("----------------------------------");
        }
        this.persistLineSeparatorPositions();
        for (int i = 0; i < this.scanner.foundTaskCount; ++i) {
            if (!this.statementRecoveryActivated) {
                this.problemReporter().task(new String(this.scanner.foundTaskTags[i]), new String(this.scanner.foundTaskMessages[i]), (this.scanner.foundTaskPriorities[i] == null) ? null : new String(this.scanner.foundTaskPriorities[i]), this.scanner.foundTaskPositions[i][0], this.scanner.foundTaskPositions[i][1]);
            }
        }
        this.javadoc = null;
        return this.compilationUnit;
    }
    
    public int flushCommentsDefinedPriorTo(int position) {
        final int lastCommentIndex = this.scanner.commentPtr;
        if (lastCommentIndex < 0) {
            return position;
        }
        int index;
        int validCount;
        for (index = lastCommentIndex, validCount = 0; index >= 0; --index, ++validCount) {
            int commentEnd = this.scanner.commentStops[index];
            if (commentEnd < 0) {
                commentEnd = -commentEnd;
            }
            if (commentEnd <= position) {
                break;
            }
        }
        if (validCount > 0) {
            int immediateCommentEnd = -this.scanner.commentStops[index + 1];
            if (immediateCommentEnd > 0) {
                --immediateCommentEnd;
                if (Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(immediateCommentEnd, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
                    position = immediateCommentEnd;
                    --validCount;
                    ++index;
                }
            }
        }
        if (index < 0) {
            return position;
        }
        switch (validCount) {
            case 0: {
                break;
            }
            case 2: {
                this.scanner.commentStarts[0] = this.scanner.commentStarts[index + 1];
                this.scanner.commentStops[0] = this.scanner.commentStops[index + 1];
                this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index + 1];
                this.scanner.commentStarts[1] = this.scanner.commentStarts[index + 2];
                this.scanner.commentStops[1] = this.scanner.commentStops[index + 2];
                this.scanner.commentTagStarts[1] = this.scanner.commentTagStarts[index + 2];
                break;
            }
            case 1: {
                this.scanner.commentStarts[0] = this.scanner.commentStarts[index + 1];
                this.scanner.commentStops[0] = this.scanner.commentStops[index + 1];
                this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index + 1];
                break;
            }
            default: {
                System.arraycopy(this.scanner.commentStarts, index + 1, this.scanner.commentStarts, 0, validCount);
                System.arraycopy(this.scanner.commentStops, index + 1, this.scanner.commentStops, 0, validCount);
                System.arraycopy(this.scanner.commentTagStarts, index + 1, this.scanner.commentTagStarts, 0, validCount);
                break;
            }
        }
        this.scanner.commentPtr = validCount - 1;
        return position;
    }
    
    protected TypeReference getAnnotationType() {
        final int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length == 1) {
            return new SingleTypeReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        }
        final char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        final long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        return new QualifiedTypeReference(tokens, positions);
    }
    
    public int getFirstToken() {
        return this.firstToken;
    }
    
    public int[] getJavaDocPositions() {
        int javadocCount = 0;
        final int max = this.scanner.commentPtr;
        for (int i = 0; i <= max; ++i) {
            if (this.scanner.commentStarts[i] >= 0 && this.scanner.commentStops[i] > 0) {
                ++javadocCount;
            }
        }
        if (javadocCount == 0) {
            return null;
        }
        final int[] positions = new int[2 * javadocCount];
        int index = 0;
        for (int j = 0; j <= max; ++j) {
            final int commentStart = this.scanner.commentStarts[j];
            if (commentStart >= 0) {
                final int commentStop = this.scanner.commentStops[j];
                if (commentStop > 0) {
                    positions[index++] = commentStart;
                    positions[index++] = commentStop - 1;
                }
            }
        }
        return positions;
    }
    
    public void getMethodBodies(final CompilationUnitDeclaration unit) {
        if (unit == null) {
            return;
        }
        if (unit.ignoreMethodBodies) {
            unit.ignoreFurtherInvestigation = true;
            return;
        }
        if ((unit.bits & 0x10) != 0x0) {
            return;
        }
        final int[] oldLineEnds = this.scanner.lineEnds;
        final int oldLinePtr = this.scanner.linePtr;
        final CompilationResult compilationResult = unit.compilationResult;
        final char[] contents = (this.readManager != null) ? this.readManager.getContents(compilationResult.compilationUnit) : compilationResult.compilationUnit.getContents();
        this.scanner.setSource(contents, compilationResult);
        if (this.javadocParser != null && this.javadocParser.checkDocComment) {
            this.javadocParser.scanner.setSource(contents);
        }
        if (unit.types != null) {
            for (int i = 0, length = unit.types.length; i < length; ++i) {
                unit.types[i].parseMethods(this, unit);
            }
        }
        unit.bits |= 0x10;
        this.scanner.lineEnds = oldLineEnds;
        this.scanner.linePtr = oldLinePtr;
    }
    
    protected char getNextCharacter(final char[] comment, final int[] index) {
        char nextCharacter = comment[index[0]++];
        switch (nextCharacter) {
            case '\\': {
                final int n = 0;
                ++index[n];
                while (comment[index[0]] == 'u') {
                    final int n2 = 0;
                    ++index[n2];
                }
                final int c1;
                final int c2;
                final int c3;
                final int c4;
                if ((c1 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) <= 15 && c1 >= 0 && (c2 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(comment[index[0]++])) <= 15 && c4 >= 0) {
                    nextCharacter = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
                    break;
                }
                break;
            }
        }
        return nextCharacter;
    }
    
    protected Expression getTypeReference(final Expression exp) {
        exp.bits &= 0xFFFFFFF8;
        exp.bits |= 0x4;
        return exp;
    }
    
    protected void annotateTypeReference(final Wildcard ref) {
        final int length;
        if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
            if (ref.annotations == null) {
                ref.annotations = new Annotation[ref.getAnnotatableLevels()][];
            }
            final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
            final int typeAnnotationPtr = this.typeAnnotationPtr - length;
            this.typeAnnotationPtr = typeAnnotationPtr;
            System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, ref.annotations[0] = new Annotation[length], 0, length);
            if (ref.sourceStart > ref.annotations[0][0].sourceStart) {
                ref.sourceStart = ref.annotations[0][0].sourceStart;
            }
            ref.bits |= 0x100000;
        }
        if (ref.bound != null) {
            ref.bits |= (ref.bound.bits & 0x100000);
        }
    }
    
    protected TypeReference getTypeReference(final int dim) {
        Annotation[][] annotationsOnDimensions = null;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        TypeReference ref;
        if (length < 0) {
            if (dim > 0) {
                annotationsOnDimensions = this.getAnnotationsOnDimensions(dim);
            }
            ref = TypeReference.baseTypeReference(-length, dim, annotationsOnDimensions);
            ref.sourceStart = this.intStack[this.intPtr--];
            if (dim == 0) {
                ref.sourceEnd = this.intStack[this.intPtr--];
            }
            else {
                --this.intPtr;
                ref.sourceEnd = this.rBracketPosition;
            }
        }
        else {
            final int numberOfIdentifiers = this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr--];
            if (length != numberOfIdentifiers || this.genericsLengthStack[this.genericsLengthPtr] != 0) {
                ref = this.getTypeReferenceForGenericType(dim, length, numberOfIdentifiers);
            }
            else if (length == 1) {
                --this.genericsLengthPtr;
                if (dim == 0) {
                    ref = new SingleTypeReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
                }
                else {
                    annotationsOnDimensions = this.getAnnotationsOnDimensions(dim);
                    ref = new ArrayTypeReference(this.identifierStack[this.identifierPtr], dim, annotationsOnDimensions, this.identifierPositionStack[this.identifierPtr--]);
                    ref.sourceEnd = this.endPosition;
                    if (annotationsOnDimensions != null) {
                        final TypeReference typeReference = ref;
                        typeReference.bits |= 0x100000;
                    }
                }
            }
            else {
                --this.genericsLengthPtr;
                final char[][] tokens = new char[length][];
                this.identifierPtr -= length;
                final long[] positions = new long[length];
                System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
                System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
                if (dim == 0) {
                    ref = new QualifiedTypeReference(tokens, positions);
                }
                else {
                    annotationsOnDimensions = this.getAnnotationsOnDimensions(dim);
                    ref = new ArrayQualifiedTypeReference(tokens, dim, annotationsOnDimensions, positions);
                    ref.sourceEnd = this.endPosition;
                    if (annotationsOnDimensions != null) {
                        final TypeReference typeReference2 = ref;
                        typeReference2.bits |= 0x100000;
                    }
                }
            }
        }
        final int levels = ref.getAnnotatableLevels();
        for (int i = levels - 1; i >= 0; --i) {
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                if (ref.annotations == null) {
                    ref.annotations = new Annotation[levels][];
                }
                final Annotation[] typeAnnotationStack = this.typeAnnotationStack;
                final int typeAnnotationPtr = this.typeAnnotationPtr - length;
                this.typeAnnotationPtr = typeAnnotationPtr;
                System.arraycopy(typeAnnotationStack, typeAnnotationPtr + 1, ref.annotations[i] = new Annotation[length], 0, length);
                if (i == 0) {
                    ref.sourceStart = ref.annotations[0][0].sourceStart;
                }
                final TypeReference typeReference3 = ref;
                typeReference3.bits |= 0x100000;
            }
        }
        return ref;
    }
    
    protected TypeReference getTypeReferenceForGenericType(final int dim, final int identifierLength, final int numberOfIdentifiers) {
        final Annotation[][] annotationsOnDimensions = (Annotation[][])((dim == 0) ? null : this.getAnnotationsOnDimensions(dim));
        if (identifierLength == 1 && numberOfIdentifiers == 1) {
            final int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
            TypeReference[] typeArguments = null;
            if (currentTypeArgumentsLength < 0) {
                typeArguments = TypeReference.NO_TYPE_ARGUMENTS;
            }
            else {
                typeArguments = new TypeReference[currentTypeArgumentsLength];
                this.genericsPtr -= currentTypeArgumentsLength;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, currentTypeArgumentsLength);
            }
            final ParameterizedSingleTypeReference parameterizedSingleTypeReference = new ParameterizedSingleTypeReference(this.identifierStack[this.identifierPtr], typeArguments, dim, annotationsOnDimensions, this.identifierPositionStack[this.identifierPtr--]);
            if (dim != 0) {
                parameterizedSingleTypeReference.sourceEnd = this.endStatementPosition;
            }
            return parameterizedSingleTypeReference;
        }
        final TypeReference[][] typeArguments2 = new TypeReference[numberOfIdentifiers][];
        final char[][] tokens = new char[numberOfIdentifiers][];
        final long[] positions = new long[numberOfIdentifiers];
        int index = numberOfIdentifiers;
        int currentIdentifiersLength = identifierLength;
        while (index > 0) {
            final int currentTypeArgumentsLength2 = this.genericsLengthStack[this.genericsLengthPtr--];
            if (currentTypeArgumentsLength2 > 0) {
                this.genericsPtr -= currentTypeArgumentsLength2;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments2[index - 1] = new TypeReference[currentTypeArgumentsLength2], 0, currentTypeArgumentsLength2);
            }
            else if (currentTypeArgumentsLength2 < 0) {
                typeArguments2[index - 1] = TypeReference.NO_TYPE_ARGUMENTS;
            }
            switch (currentIdentifiersLength) {
                case 1: {
                    tokens[index - 1] = this.identifierStack[this.identifierPtr];
                    positions[index - 1] = this.identifierPositionStack[this.identifierPtr--];
                    break;
                }
                default: {
                    this.identifierPtr -= currentIdentifiersLength;
                    System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, index - currentIdentifiersLength, currentIdentifiersLength);
                    System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, index - currentIdentifiersLength, currentIdentifiersLength);
                    break;
                }
            }
            index -= currentIdentifiersLength;
            if (index > 0) {
                currentIdentifiersLength = this.identifierLengthStack[this.identifierLengthPtr--];
            }
        }
        final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = new ParameterizedQualifiedTypeReference(tokens, typeArguments2, dim, annotationsOnDimensions, positions);
        if (dim != 0) {
            parameterizedQualifiedTypeReference.sourceEnd = this.endStatementPosition;
        }
        return parameterizedQualifiedTypeReference;
    }
    
    protected NameReference getUnspecifiedReference() {
        return this.getUnspecifiedReference(true);
    }
    
    protected NameReference getUnspecifiedReference(final boolean rejectTypeAnnotations) {
        if (rejectTypeAnnotations) {
            this.consumeNonTypeUseName();
        }
        final int length;
        NameReference ref;
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1) {
            ref = new SingleNameReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        }
        else {
            final char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            final long[] positions = new long[length];
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            ref = new QualifiedNameReference(tokens, positions, (int)(this.identifierPositionStack[this.identifierPtr + 1] >> 32), (int)this.identifierPositionStack[this.identifierPtr + length]);
        }
        return ref;
    }
    
    protected NameReference getUnspecifiedReferenceOptimized() {
        this.consumeNonTypeUseName();
        final int length;
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1) {
            final SingleNameReference singleNameReference;
            final NameReference ref = singleNameReference = new SingleNameReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
            singleNameReference.bits &= 0xFFFFFFF8;
            final NameReference nameReference = ref;
            nameReference.bits |= 0x3;
            return ref;
        }
        final char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        final long[] positions = new long[length];
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        final QualifiedNameReference qualifiedNameReference;
        final NameReference ref = qualifiedNameReference = new QualifiedNameReference(tokens, positions, (int)(this.identifierPositionStack[this.identifierPtr + 1] >> 32), (int)this.identifierPositionStack[this.identifierPtr + length]);
        qualifiedNameReference.bits &= 0xFFFFFFF8;
        final NameReference nameReference2 = ref;
        nameReference2.bits |= 0x3;
        return ref;
    }
    
    public void goForBlockStatementsopt() {
        this.firstToken = 63;
        this.scanner.recordLineSeparator = false;
    }
    
    public void goForBlockStatementsOrCatchHeader() {
        this.firstToken = 6;
        this.scanner.recordLineSeparator = false;
    }
    
    public void goForClassBodyDeclarations() {
        this.firstToken = 21;
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForCompilationUnit() {
        this.firstToken = 1;
        this.scanner.foundTaskCount = 0;
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForExpression(final boolean recordLineSeparator) {
        this.firstToken = 8;
        this.scanner.recordLineSeparator = recordLineSeparator;
    }
    
    public void goForFieldDeclaration() {
        this.firstToken = 30;
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForGenericMethodDeclaration() {
        this.firstToken = 9;
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForHeaders() {
        final RecoveredType currentType = this.currentRecoveryType();
        if (currentType != null && currentType.insideEnumConstantPart) {
            this.firstToken = 62;
        }
        else {
            this.firstToken = 16;
        }
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForImportDeclaration() {
        this.firstToken = 31;
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForInitializer() {
        this.firstToken = 14;
        this.scanner.recordLineSeparator = false;
    }
    
    public void goForMemberValue() {
        this.firstToken = 31;
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForMethodBody() {
        this.firstToken = 2;
        this.scanner.recordLineSeparator = false;
    }
    
    public void goForPackageDeclaration() {
        this.firstToken = 29;
        this.scanner.recordLineSeparator = true;
    }
    
    public void goForTypeDeclaration() {
        this.firstToken = 4;
        this.scanner.recordLineSeparator = true;
    }
    
    public boolean hasLeadingTagComment(final char[] commentPrefixTag, final int rangeEnd) {
        int iComment = this.scanner.commentPtr;
        if (iComment < 0) {
            return false;
        }
        final int iStatement = this.astLengthPtr;
        if (iStatement < 0 || this.astLengthStack[iStatement] <= 1) {
            return false;
        }
        final ASTNode lastNode = this.astStack[this.astPtr];
        final int rangeStart = lastNode.sourceEnd;
        while (iComment >= 0) {
            int commentStart = this.scanner.commentStarts[iComment];
            if (commentStart < 0) {
                commentStart = -commentStart;
            }
            if (commentStart < rangeStart) {
                return false;
            }
            Label_0206: {
                if (commentStart <= rangeEnd) {
                    final char[] source = this.scanner.source;
                    int charPos;
                    for (charPos = commentStart + 2; charPos < rangeEnd; ++charPos) {
                        final char c = source[charPos];
                        if (c >= '\u0080') {
                            break;
                        }
                        if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x100) == 0x0) {
                            break;
                        }
                    }
                    int iTag = 0;
                    final int length = commentPrefixTag.length;
                    while (iTag < length) {
                        if (charPos >= rangeEnd || source[charPos] != commentPrefixTag[iTag]) {
                            if (iTag == 0) {
                                return false;
                            }
                            break Label_0206;
                        }
                        else {
                            ++iTag;
                            ++charPos;
                        }
                    }
                    return true;
                }
            }
            --iComment;
        }
        return false;
    }
    
    protected void ignoreNextClosingBrace() {
        this.ignoreNextClosingBrace = true;
    }
    
    protected void ignoreExpressionAssignment() {
        --this.intPtr;
        final ArrayInitializer arrayInitializer = (ArrayInitializer)this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        if (!this.statementRecoveryActivated) {
            this.problemReporter().arrayConstantsOnlyInArrayInitializers(arrayInitializer.sourceStart, arrayInitializer.sourceEnd);
        }
    }
    
    public void initialize() {
        this.initialize(false);
    }
    
    public void initialize(final boolean parsingCompilationUnit) {
        this.javadoc = null;
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.typeAnnotationLengthPtr = -1;
        this.typeAnnotationPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        this.nestedMethod[this.nestedType = 0] = 0;
        this.variablesCounter[this.nestedType] = 0;
        this.dimensions = 0;
        this.realBlockPtr = -1;
        this.compilationUnit = null;
        this.referenceContext = null;
        this.endStatementPosition = 0;
        this.valueLambdaNestDepth = -1;
        final int astLength = this.astStack.length;
        if (this.noAstNodes.length < astLength) {
            this.noAstNodes = new ASTNode[astLength];
        }
        System.arraycopy(this.noAstNodes, 0, this.astStack, 0, astLength);
        final int expressionLength = this.expressionStack.length;
        if (this.noExpressions.length < expressionLength) {
            this.noExpressions = new Expression[expressionLength];
        }
        System.arraycopy(this.noExpressions, 0, this.expressionStack, 0, expressionLength);
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        this.scanner.eofPosition = Integer.MAX_VALUE;
        this.recordStringLiterals = true;
        final boolean checkNLS = this.options.getSeverity(256) != 256;
        this.checkExternalizeStrings = checkNLS;
        this.scanner.checkNonExternalizedStringLiterals = (parsingCompilationUnit && checkNLS);
        this.scanner.checkUninternedIdentityComparison = (parsingCompilationUnit && this.options.complainOnUninternedIdentityComparison);
        this.scanner.lastPosition = -1;
        this.resetModifiers();
        this.lastCheckPoint = -1;
        this.currentElement = null;
        this.restartRecovery = false;
        this.hasReportedError = false;
        this.recoveredStaticInitializerStart = 0;
        this.lastIgnoredToken = -1;
        this.lastErrorEndPosition = -1;
        this.lastErrorEndPositionBeforeRecovery = -1;
        this.lastJavadocEnd = -1;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        this.lastPosistion = -1;
        this.rBraceStart = 0;
        this.rBraceEnd = 0;
        this.rBraceSuccessorStart = 0;
        this.rBracketPosition = 0;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
    }
    
    public void initializeScanner() {
        this.scanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, this.options.taskTags, this.options.taskPriorities, this.options.isTaskCaseSensitive);
    }
    
    public void jumpOverMethodBody() {
        if (this.diet && this.dietInt == 0) {
            this.scanner.diet = true;
        }
    }
    
    private void jumpOverType() {
        if (this.recoveredTypes != null && this.nextTypeStart > -1 && this.nextTypeStart < this.scanner.currentPosition) {
            final TypeDeclaration typeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
            final boolean isAnonymous = typeDeclaration.allocation != null;
            this.scanner.startPosition = typeDeclaration.declarationSourceEnd + 1;
            this.scanner.currentPosition = typeDeclaration.declarationSourceEnd + 1;
            this.scanner.diet = false;
            if (!isAnonymous) {
                ((RecoveryScanner)this.scanner).setPendingTokens(new int[] { 28, 73 });
            }
            else {
                ((RecoveryScanner)this.scanner).setPendingTokens(new int[] { 22, 70, 22 });
            }
            this.pendingRecoveredType = typeDeclaration;
            try {
                this.currentToken = this.scanner.getNextToken();
            }
            catch (final InvalidInputException ex) {}
            if (++this.recoveredTypePtr < this.recoveredTypes.length) {
                final TypeDeclaration nextTypeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
                this.nextTypeStart = ((nextTypeDeclaration.allocation == null) ? nextTypeDeclaration.declarationSourceStart : nextTypeDeclaration.allocation.sourceStart);
            }
            else {
                this.nextTypeStart = Integer.MAX_VALUE;
            }
        }
    }
    
    protected void markEnclosingMemberWithLocalType() {
        if (this.currentElement != null) {
            return;
        }
        this.markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.LOCAL);
    }
    
    protected void markEnclosingMemberWithLocalOrFunctionalType(final LocalTypeKind context) {
        for (int i = this.astPtr; i >= 0; --i) {
            final ASTNode node = this.astStack[i];
            if (node instanceof AbstractMethodDeclaration || node instanceof FieldDeclaration || (node instanceof TypeDeclaration && ((TypeDeclaration)node).declarationSourceEnd == 0)) {
                switch (context) {
                    case METHOD_REFERENCE: {
                        final ASTNode astNode = node;
                        astNode.bits |= 0x200000;
                        break;
                    }
                    case LAMBDA: {
                        final ASTNode astNode2 = node;
                        astNode2.bits |= 0x200000;
                    }
                    case LOCAL: {
                        final ASTNode astNode3 = node;
                        astNode3.bits |= 0x2;
                        break;
                    }
                }
                return;
            }
        }
        if (this.referenceContext instanceof AbstractMethodDeclaration || this.referenceContext instanceof TypeDeclaration) {
            final ASTNode node2 = (ASTNode)this.referenceContext;
            switch (context) {
                case METHOD_REFERENCE: {
                    final ASTNode astNode4 = node2;
                    astNode4.bits |= 0x200000;
                    break;
                }
                case LAMBDA: {
                    final ASTNode astNode5 = node2;
                    astNode5.bits |= 0x200000;
                }
                case LOCAL: {
                    final ASTNode astNode6 = node2;
                    astNode6.bits |= 0x2;
                    break;
                }
            }
        }
    }
    
    protected boolean moveRecoveryCheckpoint() {
        int pos = this.lastCheckPoint;
        this.scanner.startPosition = pos;
        this.scanner.currentPosition = pos;
        this.scanner.diet = false;
        if (this.restartRecovery) {
            this.lastIgnoredToken = -1;
            return this.scanner.insideRecovery = true;
        }
        this.lastIgnoredToken = this.nextIgnoredToken;
        this.nextIgnoredToken = -1;
        do {
            try {
                this.scanner.lookBack[0] = (this.scanner.lookBack[1] = 0);
                this.nextIgnoredToken = this.scanner.getNextToken();
                if (this.scanner.currentPosition == this.scanner.startPosition) {
                    final Scanner scanner = this.scanner;
                    ++scanner.currentPosition;
                    this.nextIgnoredToken = -1;
                }
            }
            catch (final InvalidInputException ex) {
                pos = this.scanner.currentPosition;
                continue;
            }
            finally {
                this.scanner.lookBack[0] = (this.scanner.lookBack[1] = 0);
            }
            this.scanner.lookBack[0] = (this.scanner.lookBack[1] = 0);
        } while (this.nextIgnoredToken < 0);
        if (this.nextIgnoredToken == 60 && this.currentToken == 60) {
            return false;
        }
        this.lastCheckPoint = this.scanner.currentPosition;
        this.scanner.startPosition = pos;
        this.scanner.currentPosition = pos;
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        return true;
    }
    
    protected MessageSend newMessageSend() {
        final MessageSend m = new MessageSend();
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, m.arguments = new Expression[length], 0, length);
        }
        return m;
    }
    
    protected MessageSend newMessageSendWithTypeArguments() {
        final MessageSend m = new MessageSend();
        final int length;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, m.arguments = new Expression[length], 0, length);
        }
        return m;
    }
    
    protected void optimizedConcatNodeLists() {
        final int[] astLengthStack = this.astLengthStack;
        final int astLengthPtr = this.astLengthPtr - 1;
        astLengthStack[this.astLengthPtr = astLengthPtr] = astLengthStack[astLengthPtr] + 1;
    }
    
    @Override
    public boolean atConflictScenario(int token) {
        if (this.unstackedAct == 16382) {
            return false;
        }
        if (token != 37) {
            token = ((token == 24) ? 50 : 83);
        }
        return this.automatonWillShift(token, this.unstackedAct);
    }
    
    protected void parse() {
        final boolean isDietParse = this.diet;
        final int oldFirstToken = this.getFirstToken();
        this.hasError = false;
        this.hasReportedError = false;
        int act = 1580;
        this.unstackedAct = 16382;
        this.stateStackTop = -1;
        this.currentToken = this.getFirstToken();
        Label_0614: {
            try {
                this.scanner.setActiveParser(this);
            Label_0196:
                while (true) {
                    final int stackLength = this.stack.length;
                    if (++this.stateStackTop >= stackLength) {
                        System.arraycopy(this.stack, 0, this.stack = new int[stackLength + 255], 0, stackLength);
                    }
                    this.stack[this.stateStackTop] = act;
                    act = (this.unstackedAct = tAction(act, this.currentToken));
                    if (act == 16382 || this.restartRecovery) {
                        final int errorPos = this.scanner.currentPosition - 1;
                        if (!this.hasReportedError) {
                            this.hasError = true;
                        }
                        final int previousToken = this.currentToken;
                        switch (this.resumeOnSyntaxError()) {
                            case 0: {
                                break Label_0196;
                            }
                            case 1: {
                                if (act == 16382 && previousToken != 0) {
                                    this.lastErrorEndPosition = errorPos;
                                }
                                act = 1580;
                                this.stateStackTop = -1;
                                this.currentToken = this.getFirstToken();
                                continue;
                            }
                            case 2: {
                                if (act == 16382) {
                                    act = this.stack[this.stateStackTop--];
                                    continue;
                                }
                                break;
                            }
                        }
                    }
                    if (act <= 800) {
                        --this.stateStackTop;
                    }
                    else if (act > 16382) {
                        this.consumeToken(this.currentToken);
                        if (this.currentElement != null) {
                            final boolean oldValue = this.recordStringLiterals;
                            this.recordStringLiterals = false;
                            this.recoveryTokenCheck();
                            this.recordStringLiterals = oldValue;
                        }
                        try {
                            this.currentToken = this.scanner.getNextToken();
                        }
                        catch (final InvalidInputException e) {
                            if (!this.hasReportedError) {
                                this.problemReporter().scannerError(this, e.getMessage());
                                this.hasReportedError = true;
                            }
                            this.lastCheckPoint = this.scanner.currentPosition;
                            this.currentToken = 0;
                            this.restartRecovery = true;
                        }
                        if (this.statementRecoveryActivated) {
                            this.jumpOverType();
                        }
                        act -= 16382;
                        this.unstackedAct = act;
                    }
                    else {
                        if (act >= 16381) {
                            break Label_0614;
                        }
                        this.consumeToken(this.currentToken);
                        if (this.currentElement != null) {
                            final boolean oldValue = this.recordStringLiterals;
                            this.recordStringLiterals = false;
                            this.recoveryTokenCheck();
                            this.recordStringLiterals = oldValue;
                        }
                        try {
                            this.currentToken = this.scanner.getNextToken();
                        }
                        catch (final InvalidInputException e) {
                            if (!this.hasReportedError) {
                                this.problemReporter().scannerError(this, e.getMessage());
                                this.hasReportedError = true;
                            }
                            this.lastCheckPoint = this.scanner.currentPosition;
                            this.currentToken = 0;
                            this.restartRecovery = true;
                        }
                        if (this.statementRecoveryActivated) {
                            this.jumpOverType();
                            continue;
                        }
                        continue;
                    }
                    do {
                        this.stateStackTop -= Parser.rhs[act] - 1;
                        this.unstackedAct = ntAction(this.stack[this.stateStackTop], Parser.lhs[act]);
                        this.consumeRule(act);
                        act = this.unstackedAct;
                    } while (act <= 800);
                }
                act = 16382;
            }
            finally {
                this.unstackedAct = 16382;
                this.scanner.setActiveParser(null);
            }
        }
        this.unstackedAct = 16382;
        this.scanner.setActiveParser(null);
        this.endParse(act);
        final NLSTag[] tags = this.scanner.getNLSTags();
        if (tags != null) {
            this.compilationUnit.nlsTags = tags;
        }
        this.scanner.checkNonExternalizedStringLiterals = false;
        if (this.scanner.checkUninternedIdentityComparison) {
            this.compilationUnit.validIdentityComparisonLines = this.scanner.getIdentityComparisonLines();
            this.scanner.checkUninternedIdentityComparison = false;
        }
        if (this.reportSyntaxErrorIsRequired && this.hasError && !this.statementRecoveryActivated) {
            if (!this.options.performStatementsRecovery) {
                this.reportSyntaxErrors(isDietParse, oldFirstToken);
            }
            else {
                final RecoveryScannerData data = this.referenceContext.compilationResult().recoveryScannerData;
                if (this.recoveryScanner == null) {
                    this.recoveryScanner = new RecoveryScanner(this.scanner, data);
                }
                else {
                    this.recoveryScanner.setData(data);
                }
                this.recoveryScanner.setSource(this.scanner.source);
                this.recoveryScanner.lineEnds = this.scanner.lineEnds;
                this.recoveryScanner.linePtr = this.scanner.linePtr;
                this.reportSyntaxErrors(isDietParse, oldFirstToken);
                if (data == null) {
                    this.referenceContext.compilationResult().recoveryScannerData = this.recoveryScanner.getData();
                }
                if (this.methodRecoveryActivated && this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = false;
                    this.recoverStatements();
                    this.methodRecoveryActivated = true;
                    this.lastAct = 16382;
                }
            }
        }
        this.problemReporter.referenceContext = null;
    }
    
    public void parse(final ConstructorDeclaration cd, final CompilationUnitDeclaration unit, final boolean recordLineSeparator) {
        final boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
        if (this.options.performMethodsFullRecovery) {
            this.methodRecoveryActivated = true;
            this.ignoreNextOpeningBrace = true;
        }
        this.initialize();
        this.goForBlockStatementsopt();
        if (recordLineSeparator) {
            this.scanner.recordLineSeparator = true;
        }
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.pushOnRealBlockStack(0);
        this.referenceContext = cd;
        this.compilationUnit = unit;
        this.scanner.resetTo(cd.bodyStart, cd.bodyEnd);
        Label_0199: {
            try {
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0199;
            }
            finally {
                final int[] nestedMethod2 = this.nestedMethod;
                final int nestedType2 = this.nestedType;
                --nestedMethod2[nestedType2];
                if (this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = oldMethodRecoveryActivated;
                }
            }
            final int[] nestedMethod3 = this.nestedMethod;
            final int nestedType3 = this.nestedType;
            --nestedMethod3[nestedType3];
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        this.checkNonNLSAfterBodyEnd(cd.declarationSourceEnd);
        if (this.lastAct == 16382) {
            cd.bits |= 0x80000;
            this.initialize();
            return;
        }
        cd.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        final int length;
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (!this.options.ignoreMethodBodies) {
                if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
                    System.arraycopy(this.astStack, this.astPtr + 2, cd.statements = new Statement[length - 1], 0, length - 1);
                    cd.constructorCall = (ExplicitConstructorCall)this.astStack[this.astPtr + 1];
                }
                else {
                    System.arraycopy(this.astStack, this.astPtr + 1, cd.statements = new Statement[length], 0, length);
                    cd.constructorCall = SuperReference.implicitSuperConstructorCall();
                }
            }
        }
        else {
            if (!this.options.ignoreMethodBodies) {
                cd.constructorCall = SuperReference.implicitSuperConstructorCall();
            }
            if (!this.containsComment(cd.bodyStart, cd.bodyEnd)) {
                cd.bits |= 0x8;
            }
        }
        final ExplicitConstructorCall explicitConstructorCall = cd.constructorCall;
        if (explicitConstructorCall != null && explicitConstructorCall.sourceEnd == 0) {
            explicitConstructorCall.sourceEnd = cd.sourceEnd;
            explicitConstructorCall.sourceStart = cd.sourceStart;
        }
    }
    
    public void parse(final FieldDeclaration field, final TypeDeclaration type, final CompilationUnitDeclaration unit, final char[] initializationSource) {
        this.initialize();
        this.goForExpression(true);
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.referenceContext = type;
        this.compilationUnit = unit;
        this.scanner.setSource(initializationSource);
        this.scanner.resetTo(0, initializationSource.length - 1);
        Label_0116: {
            try {
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0116;
            }
            finally {
                final int[] nestedMethod2 = this.nestedMethod;
                final int nestedType2 = this.nestedType;
                --nestedMethod2[nestedType2];
            }
            final int[] nestedMethod3 = this.nestedMethod;
            final int nestedType3 = this.nestedType;
            --nestedMethod3[nestedType3];
        }
        if (this.lastAct == 16382) {
            field.bits |= 0x80000;
            return;
        }
        field.initialization = this.expressionStack[this.expressionPtr];
        if ((type.bits & 0x2) != 0x0) {
            field.bits |= 0x2;
        }
    }
    
    public CompilationUnitDeclaration parse(final ICompilationUnit sourceUnit, final CompilationResult compilationResult) {
        return this.parse(sourceUnit, compilationResult, -1, -1);
    }
    
    public CompilationUnitDeclaration parse(final ICompilationUnit sourceUnit, final CompilationResult compilationResult, final int start, final int end) {
        try {
            this.initialize(true);
            this.goForCompilationUnit();
            final CompilationUnitDeclaration compilationUnitDeclaration = new CompilationUnitDeclaration(this.problemReporter, compilationResult, 0);
            this.compilationUnit = compilationUnitDeclaration;
            this.referenceContext = compilationUnitDeclaration;
            char[] contents;
            try {
                contents = ((this.readManager != null) ? this.readManager.getContents(sourceUnit) : sourceUnit.getContents());
            }
            catch (final AbortCompilationUnit abortException) {
                this.problemReporter().cannotReadSource(this.compilationUnit, abortException, this.options.verbose);
                contents = CharOperation.NO_CHAR;
            }
            this.scanner.setSource(contents);
            this.compilationUnit.sourceEnd = this.scanner.source.length - 1;
            if (end != -1) {
                this.scanner.resetTo(start, end);
            }
            if (this.javadocParser != null && this.javadocParser.checkDocComment) {
                this.javadocParser.scanner.setSource(contents);
                if (end != -1) {
                    this.javadocParser.scanner.resetTo(start, end);
                }
            }
            this.parse();
        }
        finally {
            final CompilationUnitDeclaration unit = this.compilationUnit;
            this.compilationUnit = null;
            if (!this.diet) {
                final CompilationUnitDeclaration compilationUnitDeclaration2 = unit;
                compilationUnitDeclaration2.bits |= 0x10;
            }
        }
        final CompilationUnitDeclaration unit = this.compilationUnit;
        this.compilationUnit = null;
        if (!this.diet) {
            final CompilationUnitDeclaration compilationUnitDeclaration3 = unit;
            compilationUnitDeclaration3.bits |= 0x10;
        }
        return unit;
    }
    
    public void parse(final Initializer initializer, final TypeDeclaration type, final CompilationUnitDeclaration unit) {
        final boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
        if (this.options.performMethodsFullRecovery) {
            this.methodRecoveryActivated = true;
        }
        this.initialize();
        this.goForBlockStatementsopt();
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.pushOnRealBlockStack(0);
        this.referenceContext = type;
        this.compilationUnit = unit;
        this.scanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
        Label_0182: {
            try {
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0182;
            }
            finally {
                final int[] nestedMethod2 = this.nestedMethod;
                final int nestedType2 = this.nestedType;
                --nestedMethod2[nestedType2];
                if (this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = oldMethodRecoveryActivated;
                }
            }
            final int[] nestedMethod3 = this.nestedMethod;
            final int nestedType3 = this.nestedType;
            --nestedMethod3[nestedType3];
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        this.checkNonNLSAfterBodyEnd(initializer.declarationSourceEnd);
        if (this.lastAct == 16382) {
            initializer.bits |= 0x80000;
            return;
        }
        initializer.block.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        final int length;
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) > 0) {
            final ASTNode[] astStack = this.astStack;
            final int astPtr = this.astPtr - length;
            this.astPtr = astPtr;
            System.arraycopy(astStack, astPtr + 1, initializer.block.statements = new Statement[length], 0, length);
        }
        else if (!this.containsComment(initializer.block.sourceStart, initializer.block.sourceEnd)) {
            final Block block = initializer.block;
            block.bits |= 0x8;
        }
        if ((type.bits & 0x2) != 0x0) {
            initializer.bits |= 0x2;
        }
    }
    
    public void parse(final MethodDeclaration md, final CompilationUnitDeclaration unit) {
        if (md.isAbstract()) {
            return;
        }
        if (md.isNative()) {
            return;
        }
        if ((md.modifiers & 0x1000000) != 0x0) {
            return;
        }
        final boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
        if (this.options.performMethodsFullRecovery) {
            this.ignoreNextOpeningBrace = true;
            this.methodRecoveryActivated = true;
            this.rParenPos = md.sourceEnd;
        }
        this.initialize();
        this.goForBlockStatementsopt();
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.pushOnRealBlockStack(0);
        this.referenceContext = md;
        this.compilationUnit = unit;
        this.scanner.resetTo(md.bodyStart, md.bodyEnd);
        Label_0219: {
            try {
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0219;
            }
            finally {
                final int[] nestedMethod2 = this.nestedMethod;
                final int nestedType2 = this.nestedType;
                --nestedMethod2[nestedType2];
                if (this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = oldMethodRecoveryActivated;
                }
            }
            final int[] nestedMethod3 = this.nestedMethod;
            final int nestedType3 = this.nestedType;
            --nestedMethod3[nestedType3];
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        this.checkNonNLSAfterBodyEnd(md.declarationSourceEnd);
        if (this.lastAct == 16382) {
            md.bits |= 0x80000;
            return;
        }
        md.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        final int length;
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (this.options.ignoreMethodBodies) {
                this.astPtr -= length;
            }
            else {
                final ASTNode[] astStack = this.astStack;
                final int astPtr = this.astPtr - length;
                this.astPtr = astPtr;
                System.arraycopy(astStack, astPtr + 1, md.statements = new Statement[length], 0, length);
            }
        }
        else if (!this.containsComment(md.bodyStart, md.bodyEnd)) {
            md.bits |= 0x8;
        }
    }
    
    public ASTNode[] parseClassBodyDeclarations(final char[] source, final int offset, final int length, final CompilationUnitDeclaration unit) {
        final boolean oldDiet = this.diet;
        final int oldInt = this.dietInt;
        final boolean oldTolerateDefaultClassMethods = this.tolerateDefaultClassMethods;
        this.initialize();
        this.goForClassBodyDeclarations();
        this.scanner.setSource(source);
        this.scanner.resetTo(offset, offset + length - 1);
        if (this.javadocParser != null && this.javadocParser.checkDocComment) {
            this.javadocParser.scanner.setSource(source);
            this.javadocParser.scanner.resetTo(offset, offset + length - 1);
        }
        this.nestedType = 1;
        final TypeDeclaration referenceContextTypeDeclaration = new TypeDeclaration(unit.compilationResult);
        referenceContextTypeDeclaration.name = Util.EMPTY_STRING.toCharArray();
        referenceContextTypeDeclaration.fields = new FieldDeclaration[0];
        this.compilationUnit = unit;
        (unit.types = new TypeDeclaration[1])[0] = referenceContextTypeDeclaration;
        this.referenceContext = unit;
        Label_0255: {
            try {
                this.diet = true;
                this.dietInt = 0;
                this.tolerateDefaultClassMethods = this.parsingJava8Plus;
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0255;
            }
            finally {
                this.diet = oldDiet;
                this.dietInt = oldInt;
                this.tolerateDefaultClassMethods = oldTolerateDefaultClassMethods;
            }
            this.diet = oldDiet;
            this.dietInt = oldInt;
            this.tolerateDefaultClassMethods = oldTolerateDefaultClassMethods;
        }
        ASTNode[] result = null;
        if (this.lastAct == 16382) {
            if (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
                return null;
            }
            final List bodyDeclarations = new ArrayList();
            final ASTVisitor visitor = new ASTVisitor() {
                @Override
                public boolean visit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
                    if (!methodDeclaration.isDefaultConstructor()) {
                        bodyDeclarations.add(methodDeclaration);
                    }
                    return false;
                }
                
                @Override
                public boolean visit(final FieldDeclaration fieldDeclaration, final MethodScope scope) {
                    bodyDeclarations.add(fieldDeclaration);
                    return false;
                }
                
                @Override
                public boolean visit(final TypeDeclaration memberTypeDeclaration, final ClassScope scope) {
                    bodyDeclarations.add(memberTypeDeclaration);
                    return false;
                }
            };
            unit.ignoreFurtherInvestigation = false;
            unit.traverse(visitor, unit.scope);
            unit.ignoreFurtherInvestigation = true;
            result = bodyDeclarations.toArray(new ASTNode[bodyDeclarations.size()]);
        }
        else {
            final int astLength;
            if (this.astLengthPtr > -1 && (astLength = this.astLengthStack[this.astLengthPtr--]) != 0) {
                result = new ASTNode[astLength];
                this.astPtr -= astLength;
                System.arraycopy(this.astStack, this.astPtr + 1, result, 0, astLength);
            }
            else {
                result = new ASTNode[0];
            }
        }
        boolean containsInitializers = false;
        TypeDeclaration typeDeclaration = null;
        for (int i = 0, max = result.length; i < max; ++i) {
            final ASTNode node = result[i];
            if (node instanceof TypeDeclaration) {
                ((TypeDeclaration)node).parseMethods(this, unit);
            }
            else if (node instanceof AbstractMethodDeclaration) {
                ((AbstractMethodDeclaration)node).parseStatements(this, unit);
            }
            else if (node instanceof FieldDeclaration) {
                final FieldDeclaration fieldDeclaration = (FieldDeclaration)node;
                switch (fieldDeclaration.getKind()) {
                    case 2: {
                        containsInitializers = true;
                        if (typeDeclaration == null) {
                            typeDeclaration = referenceContextTypeDeclaration;
                        }
                        if (typeDeclaration.fields == null) {
                            (typeDeclaration.fields = new FieldDeclaration[1])[0] = fieldDeclaration;
                            break;
                        }
                        final int length2 = typeDeclaration.fields.length;
                        final FieldDeclaration[] temp = new FieldDeclaration[length2 + 1];
                        System.arraycopy(typeDeclaration.fields, 0, temp, 0, length2);
                        temp[length2] = fieldDeclaration;
                        typeDeclaration.fields = temp;
                        break;
                    }
                }
            }
            if ((node.bits & 0x80000) != 0x0 && !this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
                return null;
            }
        }
        if (containsInitializers) {
            final FieldDeclaration[] fieldDeclarations = typeDeclaration.fields;
            for (int j = 0, max2 = fieldDeclarations.length; j < max2; ++j) {
                final Initializer initializer = (Initializer)fieldDeclarations[j];
                initializer.parseStatements(this, typeDeclaration, unit);
                if ((initializer.bits & 0x80000) != 0x0 && !this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
                    return null;
                }
            }
        }
        return result;
    }
    
    public Expression parseLambdaExpression(final char[] source, final int offset, final int length, final CompilationUnitDeclaration unit, final boolean recordLineSeparators) {
        this.haltOnSyntaxError = true;
        this.reparsingLambdaExpression = true;
        return this.parseExpression(source, offset, length, unit, recordLineSeparators);
    }
    
    public Expression parseExpression(final char[] source, final int offset, final int length, final CompilationUnitDeclaration unit, final boolean recordLineSeparators) {
        this.initialize();
        this.goForExpression(recordLineSeparators);
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.referenceContext = unit;
        this.compilationUnit = unit;
        this.scanner.setSource(source);
        this.scanner.resetTo(offset, offset + length - 1);
        Label_0118: {
            try {
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0118;
            }
            finally {
                final int[] nestedMethod2 = this.nestedMethod;
                final int nestedType2 = this.nestedType;
                --nestedMethod2[nestedType2];
            }
            final int[] nestedMethod3 = this.nestedMethod;
            final int nestedType3 = this.nestedType;
            --nestedMethod3[nestedType3];
        }
        if (this.lastAct == 16382) {
            return null;
        }
        return this.expressionStack[this.expressionPtr];
    }
    
    public Expression parseMemberValue(final char[] source, final int offset, final int length, final CompilationUnitDeclaration unit) {
        this.initialize();
        this.goForMemberValue();
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.referenceContext = unit;
        this.compilationUnit = unit;
        this.scanner.setSource(source);
        this.scanner.resetTo(offset, offset + length - 1);
        Label_0116: {
            try {
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0116;
            }
            finally {
                final int[] nestedMethod2 = this.nestedMethod;
                final int nestedType2 = this.nestedType;
                --nestedMethod2[nestedType2];
            }
            final int[] nestedMethod3 = this.nestedMethod;
            final int nestedType3 = this.nestedType;
            --nestedMethod3[nestedType3];
        }
        if (this.lastAct == 16382) {
            return null;
        }
        return this.expressionStack[this.expressionPtr];
    }
    
    public void parseStatements(final ReferenceContext rc, final int start, final int end, final TypeDeclaration[] types, final CompilationUnitDeclaration unit) {
        final boolean oldStatementRecoveryEnabled = this.statementRecoveryActivated;
        this.statementRecoveryActivated = true;
        this.initialize();
        this.goForBlockStatementsopt();
        final int[] nestedMethod = this.nestedMethod;
        final int nestedType = this.nestedType;
        ++nestedMethod[nestedType];
        this.pushOnRealBlockStack(0);
        this.pushOnAstLengthStack(0);
        this.referenceContext = rc;
        this.compilationUnit = unit;
        this.pendingRecoveredType = null;
        if (types != null && types.length > 0) {
            this.recoveredTypes = types;
            this.recoveredTypePtr = 0;
            this.nextTypeStart = ((this.recoveredTypes[0].allocation == null) ? this.recoveredTypes[0].declarationSourceStart : this.recoveredTypes[0].allocation.sourceStart);
        }
        else {
            this.recoveredTypes = null;
            this.recoveredTypePtr = -1;
            this.nextTypeStart = -1;
        }
        this.scanner.resetTo(start, end);
        this.lastCheckPoint = this.scanner.initialPosition;
        this.stateStackTop = -1;
        Label_0258: {
            try {
                this.parse();
            }
            catch (final AbortCompilation abortCompilation) {
                this.lastAct = 16382;
                break Label_0258;
            }
            finally {
                final int[] nestedMethod2 = this.nestedMethod;
                final int nestedType2 = this.nestedType;
                --nestedMethod2[nestedType2];
                this.recoveredTypes = null;
                this.statementRecoveryActivated = oldStatementRecoveryEnabled;
            }
            final int[] nestedMethod3 = this.nestedMethod;
            final int nestedType3 = this.nestedType;
            --nestedMethod3[nestedType3];
            this.recoveredTypes = null;
            this.statementRecoveryActivated = oldStatementRecoveryEnabled;
        }
        this.checkNonNLSAfterBodyEnd(end);
    }
    
    public void persistLineSeparatorPositions() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
    }
    
    protected void prepareForBlockStatements() {
        this.nestedMethod[this.nestedType = 0] = 1;
        this.variablesCounter[this.nestedType] = 0;
        this.realBlockStack[this.realBlockPtr = 1] = 0;
    }
    
    public ProblemReporter problemReporter() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
        this.problemReporter.referenceContext = this.referenceContext;
        return this.problemReporter;
    }
    
    protected void pushIdentifier(final char[] identifier, final long position) {
        int stackLength = this.identifierStack.length;
        if (++this.identifierPtr >= stackLength) {
            System.arraycopy(this.identifierStack, 0, this.identifierStack = new char[stackLength + 20][], 0, stackLength);
            System.arraycopy(this.identifierPositionStack, 0, this.identifierPositionStack = new long[stackLength + 20], 0, stackLength);
        }
        this.identifierStack[this.identifierPtr] = identifier;
        this.identifierPositionStack[this.identifierPtr] = position;
        stackLength = this.identifierLengthStack.length;
        if (++this.identifierLengthPtr >= stackLength) {
            System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.identifierLengthStack[this.identifierLengthPtr] = 1;
        if (this.parsingJava8Plus && identifier.length == 1 && identifier[0] == '_' && !this.processingLambdaParameterList) {
            this.problemReporter().illegalUseOfUnderscoreAsAnIdentifier((int)(position >>> 32), (int)position, false);
        }
    }
    
    protected void pushIdentifier() {
        this.pushIdentifier(this.scanner.getCurrentIdentifierSource(), ((long)this.scanner.startPosition << 32) + (this.scanner.currentPosition - 1));
    }
    
    protected void pushIdentifier(final int flag) {
        final int stackLength = this.identifierLengthStack.length;
        if (++this.identifierLengthPtr >= stackLength) {
            System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.identifierLengthStack[this.identifierLengthPtr] = flag;
    }
    
    protected void pushOnAstLengthStack(final int pos) {
        final int stackLength = this.astLengthStack.length;
        if (++this.astLengthPtr >= stackLength) {
            System.arraycopy(this.astLengthStack, 0, this.astLengthStack = new int[stackLength + 255], 0, stackLength);
        }
        this.astLengthStack[this.astLengthPtr] = pos;
    }
    
    protected void pushOnAstStack(final ASTNode node) {
        int stackLength = this.astStack.length;
        if (++this.astPtr >= stackLength) {
            System.arraycopy(this.astStack, 0, this.astStack = new ASTNode[stackLength + 100], 0, stackLength);
            this.astPtr = stackLength;
        }
        this.astStack[this.astPtr] = node;
        stackLength = this.astLengthStack.length;
        if (++this.astLengthPtr >= stackLength) {
            System.arraycopy(this.astLengthStack, 0, this.astLengthStack = new int[stackLength + 100], 0, stackLength);
        }
        this.astLengthStack[this.astLengthPtr] = 1;
    }
    
    protected void pushOnTypeAnnotationStack(final Annotation annotation) {
        int stackLength = this.typeAnnotationStack.length;
        if (++this.typeAnnotationPtr >= stackLength) {
            System.arraycopy(this.typeAnnotationStack, 0, this.typeAnnotationStack = new Annotation[stackLength + 100], 0, stackLength);
        }
        this.typeAnnotationStack[this.typeAnnotationPtr] = annotation;
        stackLength = this.typeAnnotationLengthStack.length;
        if (++this.typeAnnotationLengthPtr >= stackLength) {
            System.arraycopy(this.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack = new int[stackLength + 100], 0, stackLength);
        }
        this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr] = 1;
    }
    
    protected void pushOnTypeAnnotationLengthStack(final int pos) {
        final int stackLength = this.typeAnnotationLengthStack.length;
        if (++this.typeAnnotationLengthPtr >= stackLength) {
            System.arraycopy(this.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack = new int[stackLength + 100], 0, stackLength);
        }
        this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr] = pos;
    }
    
    protected void pushOnExpressionStack(final Expression expr) {
        int stackLength = this.expressionStack.length;
        if (++this.expressionPtr >= stackLength) {
            System.arraycopy(this.expressionStack, 0, this.expressionStack = new Expression[stackLength + 100], 0, stackLength);
        }
        this.expressionStack[this.expressionPtr] = expr;
        stackLength = this.expressionLengthStack.length;
        if (++this.expressionLengthPtr >= stackLength) {
            System.arraycopy(this.expressionLengthStack, 0, this.expressionLengthStack = new int[stackLength + 100], 0, stackLength);
        }
        this.expressionLengthStack[this.expressionLengthPtr] = 1;
    }
    
    protected void pushOnExpressionStackLengthStack(final int pos) {
        final int stackLength = this.expressionLengthStack.length;
        if (++this.expressionLengthPtr >= stackLength) {
            System.arraycopy(this.expressionLengthStack, 0, this.expressionLengthStack = new int[stackLength + 255], 0, stackLength);
        }
        this.expressionLengthStack[this.expressionLengthPtr] = pos;
    }
    
    protected void pushOnGenericsIdentifiersLengthStack(final int pos) {
        final int stackLength = this.genericsIdentifiersLengthStack.length;
        if (++this.genericsIdentifiersLengthPtr >= stackLength) {
            System.arraycopy(this.genericsIdentifiersLengthStack, 0, this.genericsIdentifiersLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] = pos;
    }
    
    protected void pushOnGenericsLengthStack(final int pos) {
        final int stackLength = this.genericsLengthStack.length;
        if (++this.genericsLengthPtr >= stackLength) {
            System.arraycopy(this.genericsLengthStack, 0, this.genericsLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.genericsLengthStack[this.genericsLengthPtr] = pos;
    }
    
    protected void pushOnGenericsStack(final ASTNode node) {
        int stackLength = this.genericsStack.length;
        if (++this.genericsPtr >= stackLength) {
            System.arraycopy(this.genericsStack, 0, this.genericsStack = new ASTNode[stackLength + 10], 0, stackLength);
        }
        this.genericsStack[this.genericsPtr] = node;
        stackLength = this.genericsLengthStack.length;
        if (++this.genericsLengthPtr >= stackLength) {
            System.arraycopy(this.genericsLengthStack, 0, this.genericsLengthStack = new int[stackLength + 10], 0, stackLength);
        }
        this.genericsLengthStack[this.genericsLengthPtr] = 1;
    }
    
    protected void pushOnIntStack(final int pos) {
        final int stackLength = this.intStack.length;
        if (++this.intPtr >= stackLength) {
            System.arraycopy(this.intStack, 0, this.intStack = new int[stackLength + 255], 0, stackLength);
        }
        this.intStack[this.intPtr] = pos;
    }
    
    protected void pushOnRealBlockStack(final int i) {
        final int stackLength = this.realBlockStack.length;
        if (++this.realBlockPtr >= stackLength) {
            System.arraycopy(this.realBlockStack, 0, this.realBlockStack = new int[stackLength + 255], 0, stackLength);
        }
        this.realBlockStack[this.realBlockPtr] = i;
    }
    
    protected void recoverStatements() {
        class MethodVisitor extends ASTVisitor
        {
            public ASTVisitor typeVisitor;
            TypeDeclaration enclosingType;
            TypeDeclaration[] types;
            int typePtr;
            
            MethodVisitor() {
                this.types = new TypeDeclaration[0];
                this.typePtr = -1;
            }
            
            @Override
            public void endVisit(final ConstructorDeclaration constructorDeclaration, final ClassScope scope) {
                this.endVisitMethod(constructorDeclaration, scope);
            }
            
            @Override
            public void endVisit(final Initializer initializer, final MethodScope scope) {
                if (initializer.block == null) {
                    return;
                }
                TypeDeclaration[] foundTypes = null;
                int length = 0;
                if (this.typePtr > -1) {
                    length = this.typePtr + 1;
                    foundTypes = new TypeDeclaration[length];
                    System.arraycopy(this.types, 0, foundTypes, 0, length);
                }
                final ReferenceContext oldContext = Parser.this.referenceContext;
                Parser.this.recoveryScanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
                final Scanner oldScanner = Parser.this.scanner;
                Parser.this.scanner = Parser.this.recoveryScanner;
                Parser.this.parseStatements(this.enclosingType, initializer.bodyStart, initializer.bodyEnd, foundTypes, Parser.this.compilationUnit);
                Parser.this.scanner = oldScanner;
                Parser.this.referenceContext = oldContext;
                for (int i = 0; i < length; ++i) {
                    foundTypes[i].traverse(this.typeVisitor, scope);
                }
            }
            
            @Override
            public void endVisit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
                this.endVisitMethod(methodDeclaration, scope);
            }
            
            private void endVisitMethod(final AbstractMethodDeclaration methodDeclaration, final ClassScope scope) {
                TypeDeclaration[] foundTypes = null;
                int length = 0;
                if (this.typePtr > -1) {
                    length = this.typePtr + 1;
                    foundTypes = new TypeDeclaration[length];
                    System.arraycopy(this.types, 0, foundTypes, 0, length);
                }
                final ReferenceContext oldContext = Parser.this.referenceContext;
                Parser.this.recoveryScanner.resetTo(methodDeclaration.bodyStart, methodDeclaration.bodyEnd);
                final Scanner oldScanner = Parser.this.scanner;
                Parser.this.scanner = Parser.this.recoveryScanner;
                Parser.this.parseStatements(methodDeclaration, methodDeclaration.bodyStart, methodDeclaration.bodyEnd, foundTypes, Parser.this.compilationUnit);
                Parser.this.scanner = oldScanner;
                Parser.this.referenceContext = oldContext;
                for (int i = 0; i < length; ++i) {
                    foundTypes[i].traverse(this.typeVisitor, scope);
                }
            }
            
            @Override
            public boolean visit(final ConstructorDeclaration constructorDeclaration, final ClassScope scope) {
                this.typePtr = -1;
                return true;
            }
            
            @Override
            public boolean visit(final Initializer initializer, final MethodScope scope) {
                this.typePtr = -1;
                return initializer.block != null;
            }
            
            @Override
            public boolean visit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
                this.typePtr = -1;
                return true;
            }
            
            private boolean visit(final TypeDeclaration typeDeclaration) {
                if (this.types.length <= ++this.typePtr) {
                    final int length = this.typePtr;
                    System.arraycopy(this.types, 0, this.types = new TypeDeclaration[length * 2 + 1], 0, length);
                }
                this.types[this.typePtr] = typeDeclaration;
                return false;
            }
            
            @Override
            public boolean visit(final TypeDeclaration typeDeclaration, final BlockScope scope) {
                return this.visit(typeDeclaration);
            }
            
            @Override
            public boolean visit(final TypeDeclaration typeDeclaration, final ClassScope scope) {
                return this.visit(typeDeclaration);
            }
        }
        final MethodVisitor methodVisitor = new MethodVisitor();
        class TypeVisitor extends ASTVisitor
        {
            public MethodVisitor methodVisitor;
            TypeDeclaration[] types;
            int typePtr;
            
            TypeVisitor() {
                this.types = new TypeDeclaration[0];
                this.typePtr = -1;
            }
            
            @Override
            public void endVisit(final TypeDeclaration typeDeclaration, final BlockScope scope) {
                this.endVisitType();
            }
            
            @Override
            public void endVisit(final TypeDeclaration typeDeclaration, final ClassScope scope) {
                this.endVisitType();
            }
            
            private void endVisitType() {
                --this.typePtr;
            }
            
            @Override
            public boolean visit(final ConstructorDeclaration constructorDeclaration, final ClassScope scope) {
                if (constructorDeclaration.isDefaultConstructor()) {
                    return false;
                }
                constructorDeclaration.traverse(this.methodVisitor, scope);
                return false;
            }
            
            @Override
            public boolean visit(final Initializer initializer, final MethodScope scope) {
                if (initializer.block == null) {
                    return false;
                }
                this.methodVisitor.enclosingType = this.types[this.typePtr];
                initializer.traverse(this.methodVisitor, scope);
                return false;
            }
            
            @Override
            public boolean visit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
                methodDeclaration.traverse(this.methodVisitor, scope);
                return false;
            }
            
            private boolean visit(final TypeDeclaration typeDeclaration) {
                if (this.types.length <= ++this.typePtr) {
                    final int length = this.typePtr;
                    System.arraycopy(this.types, 0, this.types = new TypeDeclaration[length * 2 + 1], 0, length);
                }
                this.types[this.typePtr] = typeDeclaration;
                return true;
            }
            
            @Override
            public boolean visit(final TypeDeclaration typeDeclaration, final BlockScope scope) {
                return this.visit(typeDeclaration);
            }
            
            @Override
            public boolean visit(final TypeDeclaration typeDeclaration, final ClassScope scope) {
                return this.visit(typeDeclaration);
            }
        }
        final TypeVisitor typeVisitor = new TypeVisitor();
        methodVisitor.typeVisitor = typeVisitor;
        typeVisitor.methodVisitor = methodVisitor;
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration)this.referenceContext).traverse(methodVisitor, (ClassScope)null);
        }
        else if (this.referenceContext instanceof TypeDeclaration) {
            final TypeDeclaration typeContext = (TypeDeclaration)this.referenceContext;
            for (int length = typeContext.fields.length, i = 0; i < length; ++i) {
                final FieldDeclaration fieldDeclaration = typeContext.fields[i];
                switch (fieldDeclaration.getKind()) {
                    case 2: {
                        final Initializer initializer = (Initializer)fieldDeclaration;
                        if (initializer.block == null) {
                            break;
                        }
                        methodVisitor.enclosingType = typeContext;
                        initializer.traverse(methodVisitor, null);
                        break;
                    }
                }
            }
        }
    }
    
    public void recoveryExitFromVariable() {
        if (this.currentElement != null && this.currentElement.parent != null) {
            if (this.currentElement instanceof RecoveredLocalVariable) {
                final int end = ((RecoveredLocalVariable)this.currentElement).localDeclaration.sourceEnd;
                this.currentElement.updateSourceEndIfNecessary(end);
                this.currentElement = this.currentElement.parent;
            }
            else if (this.currentElement instanceof RecoveredField && !(this.currentElement instanceof RecoveredInitializer) && this.currentElement.bracketBalance <= 0) {
                final int end = ((RecoveredField)this.currentElement).fieldDeclaration.sourceEnd;
                this.currentElement.updateSourceEndIfNecessary(end);
                this.currentElement = this.currentElement.parent;
            }
        }
    }
    
    public void recoveryTokenCheck() {
        Label_0400: {
            switch (this.currentToken) {
                case 48: {
                    if (this.recordStringLiterals && this.checkExternalizeStrings && this.lastPosistion < this.scanner.currentPosition && !this.statementRecoveryActivated) {
                        final StringLiteral stringLiteral = this.createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
                        this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
                    }
                    break Label_0400;
                }
                case 49: {
                    RecoveredElement newElement = null;
                    if (!this.ignoreNextOpeningBrace) {
                        newElement = this.currentElement.updateOnOpeningBrace(this.scanner.startPosition - 1, this.scanner.currentPosition - 1);
                    }
                    this.lastCheckPoint = this.scanner.currentPosition;
                    if (newElement != null) {
                        this.restartRecovery = true;
                        this.currentElement = newElement;
                    }
                    break Label_0400;
                }
                case 32: {
                    if (this.ignoreNextClosingBrace) {
                        this.ignoreNextClosingBrace = false;
                        break Label_0400;
                    }
                    this.rBraceStart = this.scanner.startPosition - 1;
                    this.rBraceEnd = this.scanner.currentPosition - 1;
                    this.endPosition = this.flushCommentsDefinedPriorTo(this.rBraceEnd);
                    final RecoveredElement newElement = this.currentElement.updateOnClosingBrace(this.scanner.startPosition, this.rBraceEnd);
                    this.lastCheckPoint = this.scanner.currentPosition;
                    if (newElement != this.currentElement) {
                        this.currentElement = newElement;
                    }
                    break Label_0400;
                }
                case 28: {
                    this.endStatementPosition = this.scanner.currentPosition - 1;
                    this.endPosition = this.scanner.startPosition - 1;
                    final RecoveredType currentType = this.currentRecoveryType();
                    if (currentType != null) {
                        currentType.insideEnumConstantPart = false;
                        break;
                    }
                    break;
                }
            }
            if (this.rBraceEnd > this.rBraceSuccessorStart && this.scanner.currentPosition != this.scanner.startPosition) {
                this.rBraceSuccessorStart = this.scanner.startPosition;
            }
        }
        this.ignoreNextOpeningBrace = false;
    }
    
    protected void reportSyntaxErrors(final boolean isDietParse, final int oldFirstToken) {
        if (this.referenceContext instanceof MethodDeclaration) {
            final MethodDeclaration methodDeclaration = (MethodDeclaration)this.referenceContext;
            if ((methodDeclaration.bits & 0x20) != 0x0) {
                return;
            }
        }
        this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        this.scanner.recordLineSeparator = false;
        final int start = this.scanner.initialPosition;
        final int end = (this.scanner.eofPosition == Integer.MAX_VALUE) ? this.scanner.eofPosition : (this.scanner.eofPosition - 1);
        if (isDietParse) {
            final TypeDeclaration[] types = this.compilationUnit.types;
            final int[][] intervalToSkip = RangeUtil.computeDietRange(types);
            final DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, intervalToSkip[0], intervalToSkip[1], intervalToSkip[2], this.options);
            diagnoseParser.diagnoseParse(false);
            this.reportSyntaxErrorsForSkippedMethod(types);
            this.scanner.resetTo(start, end);
        }
        else {
            final DiagnoseParser diagnoseParser2 = new DiagnoseParser(this, oldFirstToken, start, end, this.options);
            diagnoseParser2.diagnoseParse(this.options.performStatementsRecovery);
        }
    }
    
    private void reportSyntaxErrorsForSkippedMethod(final TypeDeclaration[] types) {
        if (types != null) {
            for (int i = 0; i < types.length; ++i) {
                final TypeDeclaration[] memberTypes = types[i].memberTypes;
                if (memberTypes != null) {
                    this.reportSyntaxErrorsForSkippedMethod(memberTypes);
                }
                final AbstractMethodDeclaration[] methods = types[i].methods;
                if (methods != null) {
                    for (int j = 0; j < methods.length; ++j) {
                        final AbstractMethodDeclaration method = methods[j];
                        if ((method.bits & 0x20) != 0x0) {
                            if (method.isAnnotationMethod()) {
                                final DiagnoseParser diagnoseParser = new DiagnoseParser(this, 29, method.declarationSourceStart, method.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            }
                            else {
                                final DiagnoseParser diagnoseParser = new DiagnoseParser(this, 9, method.declarationSourceStart, method.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            }
                        }
                    }
                }
                final FieldDeclaration[] fields = types[i].fields;
                if (fields != null) {
                    for (int length = fields.length, k = 0; k < length; ++k) {
                        if (fields[k] instanceof Initializer) {
                            final Initializer initializer = (Initializer)fields[k];
                            if ((initializer.bits & 0x20) != 0x0) {
                                final DiagnoseParser diagnoseParser2 = new DiagnoseParser(this, 14, initializer.declarationSourceStart, initializer.declarationSourceEnd, this.options);
                                diagnoseParser2.diagnoseParse(this.options.performStatementsRecovery);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void resetModifiers() {
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
        this.scanner.commentPtr = -1;
    }
    
    protected void resetStacks() {
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.typeAnnotationLengthPtr = -1;
        this.typeAnnotationPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        this.nestedMethod[this.nestedType = 0] = 0;
        this.variablesCounter[this.nestedType] = 0;
        this.dimensions = 0;
        this.realBlockStack[this.realBlockPtr = 0] = 0;
        this.recoveredStaticInitializerStart = 0;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
        this.valueLambdaNestDepth = -1;
    }
    
    protected int resumeAfterRecovery() {
        if (!this.methodRecoveryActivated && !this.statementRecoveryActivated) {
            this.resetStacks();
            this.resetModifiers();
            if (!this.moveRecoveryCheckpoint()) {
                return 0;
            }
            if (this.referenceContext instanceof CompilationUnitDeclaration) {
                this.goForHeaders();
                this.diet = true;
                this.dietInt = 0;
                return 1;
            }
            return 0;
        }
        else {
            if (this.statementRecoveryActivated) {
                return 0;
            }
            this.resetStacks();
            this.resetModifiers();
            if (!this.moveRecoveryCheckpoint()) {
                return 0;
            }
            this.goForHeaders();
            return 1;
        }
    }
    
    protected int resumeOnSyntaxError() {
        if (this.haltOnSyntaxError) {
            return 0;
        }
        if (this.currentElement == null) {
            this.javadoc = null;
            if (this.statementRecoveryActivated) {
                return 0;
            }
            this.currentElement = this.buildInitialRecoveryState();
        }
        if (this.currentElement == null) {
            return 0;
        }
        if (this.restartRecovery) {
            this.restartRecovery = false;
        }
        this.updateRecoveryState();
        if (this.getFirstToken() == 21 && this.referenceContext instanceof CompilationUnitDeclaration) {
            final TypeDeclaration typeDeclaration = new TypeDeclaration(this.referenceContext.compilationResult());
            typeDeclaration.name = Util.EMPTY_STRING.toCharArray();
            this.currentElement = this.currentElement.add(typeDeclaration, 0);
        }
        if (this.lastPosistion < this.scanner.currentPosition) {
            this.lastPosistion = this.scanner.currentPosition;
            this.scanner.lastPosition = this.scanner.currentPosition;
        }
        return this.resumeAfterRecovery();
    }
    
    public void setMethodsFullRecovery(final boolean enabled) {
        this.options.performMethodsFullRecovery = enabled;
    }
    
    public void setStatementsRecovery(final boolean enabled) {
        if (enabled) {
            this.options.performMethodsFullRecovery = true;
        }
        this.options.performStatementsRecovery = enabled;
    }
    
    @Override
    public String toString() {
        String s = "lastCheckpoint : int = " + String.valueOf(this.lastCheckPoint) + "\n";
        s = String.valueOf(s) + "identifierStack : char[" + (this.identifierPtr + 1) + "][] = {";
        for (int i = 0; i <= this.identifierPtr; ++i) {
            s = String.valueOf(s) + "\"" + String.valueOf(this.identifierStack[i]) + "\",";
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "identifierLengthStack : int[" + (this.identifierLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.identifierLengthPtr; ++i) {
            s = String.valueOf(s) + this.identifierLengthStack[i] + ",";
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "astLengthStack : int[" + (this.astLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.astLengthPtr; ++i) {
            s = String.valueOf(s) + this.astLengthStack[i] + ",";
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "astPtr : int = " + String.valueOf(this.astPtr) + "\n";
        s = String.valueOf(s) + "intStack : int[" + (this.intPtr + 1) + "] = {";
        for (int i = 0; i <= this.intPtr; ++i) {
            s = String.valueOf(s) + this.intStack[i] + ",";
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "expressionLengthStack : int[" + (this.expressionLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.expressionLengthPtr; ++i) {
            s = String.valueOf(s) + this.expressionLengthStack[i] + ",";
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "expressionPtr : int = " + String.valueOf(this.expressionPtr) + "\n";
        s = String.valueOf(s) + "genericsIdentifiersLengthStack : int[" + (this.genericsIdentifiersLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.genericsIdentifiersLengthPtr; ++i) {
            s = String.valueOf(s) + this.genericsIdentifiersLengthStack[i] + ",";
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "genericsLengthStack : int[" + (this.genericsLengthPtr + 1) + "] = {";
        for (int i = 0; i <= this.genericsLengthPtr; ++i) {
            s = String.valueOf(s) + this.genericsLengthStack[i] + ",";
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "genericsPtr : int = " + String.valueOf(this.genericsPtr) + "\n";
        s = String.valueOf(s) + "\n\n\n----------------Scanner--------------\n" + this.scanner.toString();
        return s;
    }
    
    protected void updateRecoveryState() {
        this.currentElement.updateFromParserState();
        this.recoveryTokenCheck();
    }
    
    protected void updateSourceDeclarationParts(final int variableDeclaratorsCounter) {
        final int endTypeDeclarationPosition = -1 + this.astStack[this.astPtr - variableDeclaratorsCounter + 1].sourceStart;
        for (int i = 0; i < variableDeclaratorsCounter - 1; ++i) {
            final FieldDeclaration field = (FieldDeclaration)this.astStack[this.astPtr - i - 1];
            field.endPart1Position = endTypeDeclarationPosition;
            field.endPart2Position = -1 + this.astStack[this.astPtr - i].sourceStart;
        }
        FieldDeclaration field;
        (field = (FieldDeclaration)this.astStack[this.astPtr]).endPart1Position = endTypeDeclarationPosition;
        field.endPart2Position = field.declarationSourceEnd;
    }
    
    protected void updateSourcePosition(final Expression exp) {
        exp.sourceEnd = this.intStack[this.intPtr--];
        exp.sourceStart = this.intStack[this.intPtr--];
    }
    
    public void copyState(final Parser from) {
        final Parser parser = from;
        this.stateStackTop = parser.stateStackTop;
        this.unstackedAct = parser.unstackedAct;
        this.identifierPtr = parser.identifierPtr;
        this.identifierLengthPtr = parser.identifierLengthPtr;
        this.astPtr = parser.astPtr;
        this.astLengthPtr = parser.astLengthPtr;
        this.expressionPtr = parser.expressionPtr;
        this.expressionLengthPtr = parser.expressionLengthPtr;
        this.genericsPtr = parser.genericsPtr;
        this.genericsLengthPtr = parser.genericsLengthPtr;
        this.genericsIdentifiersLengthPtr = parser.genericsIdentifiersLengthPtr;
        this.typeAnnotationPtr = parser.typeAnnotationPtr;
        this.typeAnnotationLengthPtr = parser.typeAnnotationLengthPtr;
        this.intPtr = parser.intPtr;
        this.nestedType = parser.nestedType;
        this.realBlockPtr = parser.realBlockPtr;
        this.valueLambdaNestDepth = parser.valueLambdaNestDepth;
        int length;
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        System.arraycopy(parser.identifierStack, 0, this.identifierStack = new char[length = parser.identifierStack.length][], 0, length);
        System.arraycopy(parser.identifierLengthStack, 0, this.identifierLengthStack = new int[length = parser.identifierLengthStack.length], 0, length);
        System.arraycopy(parser.identifierPositionStack, 0, this.identifierPositionStack = new long[length = parser.identifierPositionStack.length], 0, length);
        System.arraycopy(parser.astStack, 0, this.astStack = new ASTNode[length = parser.astStack.length], 0, length);
        System.arraycopy(parser.astLengthStack, 0, this.astLengthStack = new int[length = parser.astLengthStack.length], 0, length);
        System.arraycopy(parser.expressionStack, 0, this.expressionStack = new Expression[length = parser.expressionStack.length], 0, length);
        System.arraycopy(parser.expressionLengthStack, 0, this.expressionLengthStack = new int[length = parser.expressionLengthStack.length], 0, length);
        System.arraycopy(parser.genericsStack, 0, this.genericsStack = new ASTNode[length = parser.genericsStack.length], 0, length);
        System.arraycopy(parser.genericsLengthStack, 0, this.genericsLengthStack = new int[length = parser.genericsLengthStack.length], 0, length);
        System.arraycopy(parser.genericsIdentifiersLengthStack, 0, this.genericsIdentifiersLengthStack = new int[length = parser.genericsIdentifiersLengthStack.length], 0, length);
        System.arraycopy(parser.typeAnnotationStack, 0, this.typeAnnotationStack = new Annotation[length = parser.typeAnnotationStack.length], 0, length);
        System.arraycopy(parser.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack = new int[length = parser.typeAnnotationLengthStack.length], 0, length);
        System.arraycopy(parser.intStack, 0, this.intStack = new int[length = parser.intStack.length], 0, length);
        System.arraycopy(parser.nestedMethod, 0, this.nestedMethod = new int[length = parser.nestedMethod.length], 0, length);
        System.arraycopy(parser.realBlockStack, 0, this.realBlockStack = new int[length = parser.realBlockStack.length], 0, length);
        System.arraycopy(parser.stateStackLengthStack, 0, this.stateStackLengthStack = new int[length = parser.stateStackLengthStack.length], 0, length);
        System.arraycopy(parser.variablesCounter, 0, this.variablesCounter = new int[length = parser.variablesCounter.length], 0, length);
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        System.arraycopy(parser.stack, 0, this.stack = new int[length = parser.stack.length], 0, length);
        this.listLength = parser.listLength;
        this.listTypeParameterLength = parser.listTypeParameterLength;
        this.dimensions = parser.dimensions;
        this.recoveredStaticInitializerStart = parser.recoveredStaticInitializerStart;
    }
    
    public int automatonState() {
        return this.stack[this.stateStackTop];
    }
    
    public boolean automatonWillShift(final int token, int lastAction) {
        int stackTop = this.stateStackTop;
        int stackTopState = this.stack[stackTop];
        int highWaterMark = stackTop;
        if (lastAction <= 800) {
            --stackTop;
            lastAction += 16382;
        }
        while (true) {
            if (lastAction > 16382) {
                lastAction -= 16382;
                do {
                    stackTop -= Parser.rhs[lastAction] - 1;
                    if (stackTop < highWaterMark) {
                        stackTopState = this.stack[highWaterMark = stackTop];
                    }
                    lastAction = ntAction(stackTopState, Parser.lhs[lastAction]);
                } while (lastAction <= 800);
            }
            highWaterMark = ++stackTop;
            stackTopState = lastAction;
            lastAction = tAction(lastAction, token);
            if (lastAction > 800) {
                break;
            }
            --stackTop;
            lastAction += 16382;
        }
        return lastAction != 16382;
    }
    
    private enum LocalTypeKind
    {
        LOCAL("LOCAL", 0), 
        METHOD_REFERENCE("METHOD_REFERENCE", 1), 
        LAMBDA("LAMBDA", 2);
        
        private LocalTypeKind(final String s, final int n) {
        }
    }
}
