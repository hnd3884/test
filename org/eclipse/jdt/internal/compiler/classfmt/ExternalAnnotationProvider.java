package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.lookup.SignatureWrapper;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import java.util.Iterator;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import java.util.HashMap;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ExternalAnnotationProvider
{
    public static final String ANNOTATION_FILE_EXTENSION = "eea";
    public static final String CLASS_PREFIX = "class ";
    public static final String SUPER_PREFIX = "super ";
    public static final char NULLABLE = '0';
    public static final char NONNULL = '1';
    public static final char NO_ANNOTATION = '@';
    public static final String ANNOTATION_FILE_SUFFIX = ".eea";
    private static final String TYPE_PARAMETER_PREFIX = " <";
    private String typeName;
    String typeParametersAnnotationSource;
    Map<String, String> supertypeAnnotationSources;
    private Map<String, String> methodAnnotationSources;
    private Map<String, String> fieldAnnotationSources;
    SingleMarkerAnnotation NULLABLE_ANNOTATION;
    SingleMarkerAnnotation NONNULL_ANNOTATION;
    
    public ExternalAnnotationProvider(final InputStream input, final String typeName) throws IOException {
        this.typeName = typeName;
        this.initialize(input);
    }
    
    private void initialize(final InputStream input) throws IOException {
        Throwable t = null;
        try {
            final LineNumberReader reader = new LineNumberReader(new InputStreamReader(input));
            try {
                assertClassHeader(reader.readLine(), this.typeName);
                String line;
                if ((line = reader.readLine()) == null) {
                    return;
                }
                if (line.startsWith(" <")) {
                    if ((line = reader.readLine()) == null) {
                        return;
                    }
                    if (line.startsWith(" <")) {
                        this.typeParametersAnnotationSource = line.substring(" <".length());
                        if ((line = reader.readLine()) == null) {
                            return;
                        }
                    }
                }
                String pendingLine;
                do {
                    pendingLine = null;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String rawSig = null;
                    String annotSig = null;
                    String selector = line;
                    final boolean isSuper = selector.startsWith("super ");
                    if (isSuper) {
                        selector = selector.substring("super ".length());
                    }
                    int errLine = -1;
                    try {
                        line = reader.readLine();
                        if (line != null && !line.isEmpty() && line.charAt(0) == ' ') {
                            rawSig = line.substring(1);
                        }
                        else {
                            errLine = reader.getLineNumber();
                        }
                        line = reader.readLine();
                        if (line == null) {
                            continue;
                        }
                        if (line.isEmpty()) {
                            continue;
                        }
                        if (line.charAt(0) != ' ') {
                            pendingLine = line;
                            continue;
                        }
                        annotSig = line.substring(1);
                    }
                    catch (final Exception ex) {}
                    if (rawSig == null || annotSig == null) {
                        if (errLine == -1) {
                            errLine = reader.getLineNumber();
                        }
                        throw new IOException("Illegal format for annotation file at line " + errLine);
                    }
                    annotSig = trimTail(annotSig);
                    if (isSuper) {
                        if (this.supertypeAnnotationSources == null) {
                            this.supertypeAnnotationSources = new HashMap<String, String>();
                        }
                        this.supertypeAnnotationSources.put(String.valueOf('L') + selector + rawSig + ';', annotSig);
                    }
                    else if (rawSig.contains("(")) {
                        if (this.methodAnnotationSources == null) {
                            this.methodAnnotationSources = new HashMap<String, String>();
                        }
                        this.methodAnnotationSources.put(String.valueOf(selector) + rawSig, annotSig);
                    }
                    else {
                        if (this.fieldAnnotationSources == null) {
                            this.fieldAnnotationSources = new HashMap<String, String>();
                        }
                        this.fieldAnnotationSources.put(String.valueOf(selector) + ':' + rawSig, annotSig);
                    }
                } while ((line = pendingLine) != null || (line = reader.readLine()) != null);
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        finally {
            if (t == null) {
                final Throwable t2;
                t = t2;
            }
            else {
                final Throwable t2;
                if (t != t2) {
                    t.addSuppressed(t2);
                }
            }
        }
    }
    
    public static void assertClassHeader(String line, final String typeName) throws IOException {
        if (line == null || !line.startsWith("class ")) {
            throw new IOException("missing class header in annotation file");
        }
        line = line.substring("class ".length());
        if (!trimTail(line).equals(typeName)) {
            throw new IOException("mismatching class name in annotation file, expected " + typeName + ", but header said " + line);
        }
    }
    
    public static String extractSignature(final String line) {
        if (line == null || line.isEmpty() || line.charAt(0) != ' ') {
            return null;
        }
        return trimTail(line.substring(1));
    }
    
    protected static String trimTail(final String line) {
        int tail = line.indexOf(32);
        if (tail == -1) {
            tail = line.indexOf(9);
        }
        if (tail != -1) {
            return line.substring(0, tail);
        }
        return line;
    }
    
    public ITypeAnnotationWalker forTypeHeader(final LookupEnvironment environment) {
        if (this.typeParametersAnnotationSource != null || this.supertypeAnnotationSources != null) {
            return new DispatchingAnnotationWalker(environment);
        }
        return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }
    
    public ITypeAnnotationWalker forMethod(final char[] selector, final char[] signature, final LookupEnvironment environment) {
        final Map<String, String> sources = this.methodAnnotationSources;
        if (sources != null) {
            final String source = sources.get(String.valueOf(CharOperation.concat(selector, signature)));
            if (source != null) {
                return new MethodAnnotationWalker(source.toCharArray(), 0, environment);
            }
        }
        return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }
    
    public ITypeAnnotationWalker forField(final char[] selector, final char[] signature, final LookupEnvironment environment) {
        if (this.fieldAnnotationSources != null) {
            final String source = this.fieldAnnotationSources.get(String.valueOf(CharOperation.concat(selector, signature, ':')));
            if (source != null) {
                return new FieldAnnotationWalker(source.toCharArray(), 0, environment);
            }
        }
        return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("External Annotations for ").append(this.typeName).append('\n');
        sb.append("Methods:\n");
        if (this.methodAnnotationSources != null) {
            for (final Map.Entry<String, String> e : this.methodAnnotationSources.entrySet()) {
                sb.append('\t').append(e.getKey()).append('\n');
            }
        }
        return sb.toString();
    }
    
    void initAnnotations(final LookupEnvironment environment) {
        if (this.NULLABLE_ANNOTATION == null) {
            this.NULLABLE_ANNOTATION = new SingleMarkerAnnotation(this) {
                @Override
                public char[] getTypeName() {
                    return this.getBinaryTypeName(environment.getNullableAnnotationName());
                }
            };
        }
        if (this.NONNULL_ANNOTATION == null) {
            this.NONNULL_ANNOTATION = new SingleMarkerAnnotation(this) {
                @Override
                public char[] getTypeName() {
                    return this.getBinaryTypeName(environment.getNonNullAnnotationName());
                }
            };
        }
    }
    
    public class TypeParametersAnnotationWalker extends BasicAnnotationWalker
    {
        int[] rankStarts;
        int currentRank;
        
        TypeParametersAnnotationWalker(final char[] source, final int pos, final int rank, int[] rankStarts, final LookupEnvironment environment) {
            super(source, pos, environment);
            this.currentRank = rank;
            if (rankStarts != null) {
                this.rankStarts = rankStarts;
            }
            else {
                final int length = source.length;
                rankStarts = new int[length];
                int curRank = 0;
                int depth = 0;
                boolean pendingVariable = true;
            Label_0306:
                for (int i = pos; i < length; ++i) {
                    switch (this.source[i]) {
                        case '<': {
                            ++depth;
                            break;
                        }
                        case '>': {
                            if (--depth < 0) {
                                break Label_0306;
                            }
                            break;
                        }
                        case ';': {
                            if (depth == 0 && i + 1 < length && this.source[i + 1] != ':') {
                                pendingVariable = true;
                                break;
                            }
                            break;
                        }
                        case ':': {
                            if (depth == 0) {
                                pendingVariable = true;
                            }
                            ++i;
                            while (i < length && this.source[i] == '[') {
                                ++i;
                            }
                            if (i < length && this.source[i] == 'L') {
                                for (int currentdepth = depth; i < length && (currentdepth != depth || this.source[i] != ';'); ++i) {
                                    if (this.source[i] == '<') {
                                        ++currentdepth;
                                    }
                                    if (this.source[i] == '>') {
                                        --currentdepth;
                                    }
                                }
                            }
                            --i;
                            break;
                        }
                        default: {
                            if (pendingVariable) {
                                pendingVariable = false;
                                rankStarts[curRank++] = i;
                                break;
                            }
                            break;
                        }
                    }
                }
                System.arraycopy(rankStarts, 0, this.rankStarts = new int[curRank], 0, curRank);
            }
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameter(final boolean isClassTypeParameter, final int rank) {
            if (rank == this.currentRank) {
                return this;
            }
            if (rank < this.rankStarts.length) {
                return new TypeParametersAnnotationWalker(this.source, this.rankStarts[rank], rank, this.rankStarts, this.environment);
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(final boolean isClassTypeParameter, final int parameterRank) {
            return new TypeParametersAnnotationWalker(this.source, this.rankStarts[parameterRank], parameterRank, this.rankStarts, this.environment);
        }
        
        @Override
        public ITypeAnnotationWalker toTypeBound(final short boundIndex) {
            int p = this.pos;
            int i = this.currentTypeBound;
            while (true) {
                final int colon = CharOperation.indexOf(':', this.source, p);
                if (colon != -1) {
                    p = colon + 1;
                }
                if (++i > boundIndex) {
                    break;
                }
                p = this.wrapperWithStart(p).computeEnd() + 1;
            }
            this.pos = p;
            this.currentTypeBound = boundIndex;
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toField() {
            throw new UnsupportedOperationException("Cannot navigate to fields");
        }
        
        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            throw new UnsupportedOperationException("Cannot navigate to method return");
        }
        
        @Override
        public ITypeAnnotationWalker toMethodParameter(final short index) {
            throw new UnsupportedOperationException("Cannot navigate to method parameter");
        }
        
        @Override
        public ITypeAnnotationWalker toThrows(final int index) {
            throw new UnsupportedOperationException("Cannot navigate to throws");
        }
        
        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(final int currentTypeId) {
            if (this.pos != -1 && this.pos < this.source.length - 1) {
                switch (this.source[this.pos]) {
                    case '0': {
                        return new IBinaryAnnotation[] { ExternalAnnotationProvider.this.NULLABLE_ANNOTATION };
                    }
                    case '1': {
                        return new IBinaryAnnotation[] { ExternalAnnotationProvider.this.NONNULL_ANNOTATION };
                    }
                }
            }
            return super.getAnnotationsAtCursor(currentTypeId);
        }
    }
    
    abstract class SingleMarkerAnnotation implements IBinaryAnnotation
    {
        @Override
        public IBinaryElementValuePair[] getElementValuePairs() {
            return ElementValuePairInfo.NoMembers;
        }
        
        protected char[] getBinaryTypeName(final char[][] name) {
            return CharOperation.concat('L', CharOperation.concatWith(name, '/'), ';');
        }
    }
    
    class DispatchingAnnotationWalker implements ITypeAnnotationWalker
    {
        private LookupEnvironment environment;
        private TypeParametersAnnotationWalker typeParametersWalker;
        
        public DispatchingAnnotationWalker(final LookupEnvironment environment) {
            this.environment = environment;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameter(final boolean isClassTypeParameter, final int rank) {
            final String source = ExternalAnnotationProvider.this.typeParametersAnnotationSource;
            if (source != null) {
                if (this.typeParametersWalker == null) {
                    this.typeParametersWalker = new TypeParametersAnnotationWalker(source.toCharArray(), 0, 0, null, this.environment);
                }
                return this.typeParametersWalker.toTypeParameter(isClassTypeParameter, rank);
            }
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(final boolean isClassTypeParameter, final int parameterRank) {
            if (this.typeParametersWalker != null) {
                return this.typeParametersWalker.toTypeParameterBounds(isClassTypeParameter, parameterRank);
            }
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toSupertype(final short index, final char[] superTypeSignature) {
            final Map<String, String> sources = ExternalAnnotationProvider.this.supertypeAnnotationSources;
            if (sources != null) {
                final String source = sources.get(String.valueOf(superTypeSignature));
                if (source != null) {
                    return new SuperTypesAnnotationWalker(source.toCharArray(), this.environment);
                }
            }
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toField() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toThrows(final int rank) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeArgument(final int rank) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toMethodParameter(final short index) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeBound(final short boundIndex) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toReceiver() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toWildcardBound() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toNextArrayDimension() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toNextNestedType() {
            return this;
        }
        
        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(final int currentTypeId) {
            return DispatchingAnnotationWalker.NO_ANNOTATIONS;
        }
    }
    
    abstract class BasicAnnotationWalker implements ITypeAnnotationWalker
    {
        char[] source;
        SignatureWrapper wrapper;
        int pos;
        int prevTypeArgStart;
        int currentTypeBound;
        LookupEnvironment environment;
        
        BasicAnnotationWalker(final char[] source, final int pos, final LookupEnvironment environment) {
            this.source = source;
            this.pos = pos;
            ExternalAnnotationProvider.this.initAnnotations(this.environment = environment);
        }
        
        SignatureWrapper wrapperWithStart(final int start) {
            if (this.wrapper == null) {
                this.wrapper = new SignatureWrapper(this.source);
            }
            this.wrapper.start = start;
            return this.wrapper;
        }
        
        @Override
        public ITypeAnnotationWalker toReceiver() {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameter(final boolean isClassTypeParameter, final int rank) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(final boolean isClassTypeParameter, final int parameterRank) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeBound(final short boundIndex) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toSupertype(final short index, final char[] superTypeSignature) {
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeArgument(final int rank) {
            if (rank == 0) {
                final int start = CharOperation.indexOf('<', this.source, this.pos) + 1;
                this.prevTypeArgStart = start;
                return new MethodAnnotationWalker(this.source, start, this.environment);
            }
            int next = this.prevTypeArgStart;
            Label_0103: {
                switch (this.source[next]) {
                    case '*': {
                        break Label_0103;
                    }
                    case '+':
                    case '-': {
                        ++next;
                        break;
                    }
                }
                next = this.wrapperWithStart(next).computeEnd();
            }
            ++next;
            this.prevTypeArgStart = next;
            return new MethodAnnotationWalker(this.source, next, this.environment);
        }
        
        @Override
        public ITypeAnnotationWalker toWildcardBound() {
            switch (this.source[this.pos]) {
                case '+':
                case '-': {
                    return new MethodAnnotationWalker(this.source, this.pos + 1, this.environment);
                }
                default: {
                    return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
                }
            }
        }
        
        @Override
        public ITypeAnnotationWalker toNextArrayDimension() {
            if (this.source[this.pos] == '[') {
                int newPos = this.pos + 1;
                switch (this.source[newPos]) {
                    case '0':
                    case '1': {
                        ++newPos;
                        break;
                    }
                }
                return new MethodAnnotationWalker(this.source, newPos, this.environment);
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toNextNestedType() {
            return this;
        }
        
        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(final int currentTypeId) {
            Label_0150: {
                if (this.pos != -1 && this.pos < this.source.length - 2) {
                    switch (this.source[this.pos]) {
                        case '*':
                        case '+':
                        case '-':
                        case 'L':
                        case 'T':
                        case '[': {
                            switch (this.source[this.pos + 1]) {
                                case '0': {
                                    return new IBinaryAnnotation[] { ExternalAnnotationProvider.this.NULLABLE_ANNOTATION };
                                }
                                case '1': {
                                    return new IBinaryAnnotation[] { ExternalAnnotationProvider.this.NONNULL_ANNOTATION };
                                }
                                default: {
                                    break Label_0150;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            return BasicAnnotationWalker.NO_ANNOTATIONS;
        }
    }
    
    class SuperTypesAnnotationWalker extends BasicAnnotationWalker
    {
        SuperTypesAnnotationWalker(final char[] source, final LookupEnvironment environment) {
            super(source, 0, environment);
        }
        
        @Override
        public ITypeAnnotationWalker toField() {
            throw new UnsupportedOperationException("Supertype has no field annotations");
        }
        
        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            throw new UnsupportedOperationException("Supertype has no method return");
        }
        
        @Override
        public ITypeAnnotationWalker toMethodParameter(final short index) {
            throw new UnsupportedOperationException("Supertype has no method parameter");
        }
        
        @Override
        public ITypeAnnotationWalker toThrows(final int index) {
            throw new UnsupportedOperationException("Supertype has no throws");
        }
    }
    
    class MethodAnnotationWalker extends BasicAnnotationWalker implements IMethodAnnotationWalker
    {
        int prevParamStart;
        TypeParametersAnnotationWalker typeParametersWalker;
        
        MethodAnnotationWalker(final char[] source, final int pos, final LookupEnvironment environment) {
            super(source, pos, environment);
        }
        
        int typeEnd(int start) {
            while (this.source[start] == '[') {
                ++start;
                final char an = this.source[start];
                if (an == '0' || an == '1') {
                    ++start;
                }
            }
            final int end = this.wrapperWithStart(start).computeEnd();
            return end;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameter(final boolean isClassTypeParameter, final int rank) {
            if (this.source[0] != '<') {
                return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
            }
            if (this.typeParametersWalker == null) {
                return this.typeParametersWalker = new TypeParametersAnnotationWalker(this.source, this.pos + 1, rank, null, this.environment);
            }
            return this.typeParametersWalker.toTypeParameter(isClassTypeParameter, rank);
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(final boolean isClassTypeParameter, final int parameterRank) {
            if (this.typeParametersWalker != null) {
                return this.typeParametersWalker.toTypeParameterBounds(isClassTypeParameter, parameterRank);
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            final int close = CharOperation.indexOf(')', this.source);
            if (close != -1) {
                this.pos = close + 1;
                return this;
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        
        @Override
        public ITypeAnnotationWalker toMethodParameter(final short index) {
            if (index == 0) {
                final int start = CharOperation.indexOf('(', this.source) + 1;
                this.prevParamStart = start;
                this.pos = start;
                return this;
            }
            int end = this.typeEnd(this.prevParamStart);
            ++end;
            this.prevParamStart = end;
            this.pos = end;
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toThrows(final int index) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toField() {
            throw new UnsupportedOperationException("Methods have no fields");
        }
        
        @Override
        public int getParameterCount() {
            int count = 0;
            for (int start = CharOperation.indexOf('(', this.source) + 1; start < this.source.length && this.source[start] != ')'; start = this.typeEnd(start) + 1, ++count) {}
            return count;
        }
    }
    
    class FieldAnnotationWalker extends BasicAnnotationWalker
    {
        public FieldAnnotationWalker(final char[] source, final int pos, final LookupEnvironment environment) {
            super(source, pos, environment);
        }
        
        @Override
        public ITypeAnnotationWalker toField() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            throw new UnsupportedOperationException("Field has no method return");
        }
        
        @Override
        public ITypeAnnotationWalker toMethodParameter(final short index) {
            throw new UnsupportedOperationException("Field has no method parameter");
        }
        
        @Override
        public ITypeAnnotationWalker toThrows(final int index) {
            throw new UnsupportedOperationException("Field has no throws");
        }
    }
    
    public interface IMethodAnnotationWalker extends ITypeAnnotationWalker
    {
        int getParameterCount();
    }
}
