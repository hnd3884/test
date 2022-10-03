package org.apache.lucene.search.suggest.document;

import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.analysis.TokenStreamToAutomaton;
import java.io.IOException;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.automaton.LimitedFiniteStringsIterator;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.FiniteStringsIterator;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.TokenStream;

public final class CompletionTokenStream extends TokenStream
{
    private final PayloadAttribute payloadAttr;
    private final BytesRefBuilderTermAttribute bytesAtt;
    final TokenStream inputTokenStream;
    final boolean preserveSep;
    final boolean preservePositionIncrements;
    final int maxGraphExpansions;
    private FiniteStringsIterator finiteStrings;
    private BytesRef payload;
    private CharTermAttribute charTermAttribute;
    
    CompletionTokenStream(final TokenStream inputTokenStream) {
        this(inputTokenStream, true, true, 10000);
    }
    
    CompletionTokenStream(final TokenStream inputTokenStream, final boolean preserveSep, final boolean preservePositionIncrements, final int maxGraphExpansions) {
        this.payloadAttr = (PayloadAttribute)this.addAttribute((Class)PayloadAttribute.class);
        this.bytesAtt = (BytesRefBuilderTermAttribute)this.addAttribute((Class)BytesRefBuilderTermAttribute.class);
        this.inputTokenStream = inputTokenStream;
        this.preserveSep = preserveSep;
        this.preservePositionIncrements = preservePositionIncrements;
        this.maxGraphExpansions = maxGraphExpansions;
    }
    
    public void setPayload(final BytesRef payload) {
        this.payload = payload;
    }
    
    public boolean incrementToken() throws IOException {
        this.clearAttributes();
        if (this.finiteStrings == null) {
            final Automaton automaton = this.toAutomaton();
            this.finiteStrings = (FiniteStringsIterator)new LimitedFiniteStringsIterator(automaton, this.maxGraphExpansions);
        }
        final IntsRef string = this.finiteStrings.next();
        if (string == null) {
            return false;
        }
        Util.toBytesRef(string, this.bytesAtt.builder());
        if (this.charTermAttribute != null) {
            this.charTermAttribute.setLength(0);
            this.charTermAttribute.append(this.bytesAtt.toUTF16());
        }
        if (this.payload != null) {
            this.payloadAttr.setPayload(this.payload);
        }
        return true;
    }
    
    public void end() throws IOException {
        super.end();
        if (this.finiteStrings == null) {
            this.inputTokenStream.end();
        }
    }
    
    public void close() throws IOException {
        if (this.finiteStrings == null) {
            this.inputTokenStream.close();
        }
    }
    
    public void reset() throws IOException {
        super.reset();
        if (this.hasAttribute((Class)CharTermAttribute.class)) {
            this.charTermAttribute = (CharTermAttribute)this.getAttribute((Class)CharTermAttribute.class);
        }
        this.finiteStrings = null;
    }
    
    public Automaton toAutomaton() throws IOException {
        return this.toAutomaton(false);
    }
    
    public Automaton toAutomaton(final boolean unicodeAware) throws IOException {
        Automaton automaton = null;
        try {
            TokenStreamToAutomaton tsta;
            if (this.preserveSep) {
                tsta = new EscapingTokenStreamToAutomaton('\u001f');
            }
            else {
                tsta = new TokenStreamToAutomaton();
            }
            tsta.setPreservePositionIncrements(this.preservePositionIncrements);
            tsta.setUnicodeArcs(unicodeAware);
            automaton = tsta.toAutomaton(this.inputTokenStream);
        }
        finally {
            IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.inputTokenStream });
        }
        automaton = replaceSep(automaton, this.preserveSep, 31);
        return Operations.determinize(automaton, this.maxGraphExpansions);
    }
    
    private static Automaton replaceSep(final Automaton a, final boolean preserveSep, final int sepLabel) {
        final Automaton result = new Automaton();
        for (int numStates = a.getNumStates(), s = 0; s < numStates; ++s) {
            result.createState();
            result.setAccept(s, a.isAccept(s));
        }
        final Transition t = new Transition();
        final int[] topoSortStates = Operations.topoSortStates(a);
        for (int i = 0; i < topoSortStates.length; ++i) {
            final int state = topoSortStates[topoSortStates.length - 1 - i];
            for (int count = a.initTransition(state, t), j = 0; j < count; ++j) {
                a.getNextTransition(t);
                if (t.min == 31) {
                    assert t.max == 31;
                    if (preserveSep) {
                        result.addTransition(state, t.dest, sepLabel);
                    }
                    else {
                        result.addEpsilon(state, t.dest);
                    }
                }
                else if (t.min == 30) {
                    assert t.max == 30;
                    result.addEpsilon(state, t.dest);
                }
                else {
                    result.addTransition(state, t.dest, t.min, t.max);
                }
            }
        }
        result.finishState();
        return result;
    }
    
    private static final class EscapingTokenStreamToAutomaton extends TokenStreamToAutomaton
    {
        final BytesRefBuilder spare;
        private char sepLabel;
        
        public EscapingTokenStreamToAutomaton(final char sepLabel) {
            this.spare = new BytesRefBuilder();
            this.sepLabel = sepLabel;
        }
        
        protected BytesRef changeToken(final BytesRef in) {
            int upto = 0;
            for (int i = 0; i < in.length; ++i) {
                final byte b = in.bytes[in.offset + i];
                if (b == (byte)this.sepLabel) {
                    this.spare.grow(upto + 2);
                    this.spare.setByteAt(upto++, (byte)this.sepLabel);
                    this.spare.setByteAt(upto++, b);
                }
                else {
                    this.spare.grow(upto + 1);
                    this.spare.setByteAt(upto++, b);
                }
            }
            this.spare.setLength(upto);
            return this.spare.get();
        }
    }
    
    public static final class BytesRefBuilderTermAttributeImpl extends AttributeImpl implements BytesRefBuilderTermAttribute, TermToBytesRefAttribute
    {
        private final BytesRefBuilder bytes;
        private transient CharsRefBuilder charsRef;
        
        public BytesRefBuilderTermAttributeImpl() {
            this.bytes = new BytesRefBuilder();
        }
        
        public BytesRefBuilder builder() {
            return this.bytes;
        }
        
        public BytesRef getBytesRef() {
            return this.bytes.get();
        }
        
        public void clear() {
            this.bytes.clear();
        }
        
        public void copyTo(final AttributeImpl target) {
            final BytesRefBuilderTermAttributeImpl other = (BytesRefBuilderTermAttributeImpl)target;
            other.bytes.copyBytes(this.bytes);
        }
        
        public AttributeImpl clone() {
            final BytesRefBuilderTermAttributeImpl other = new BytesRefBuilderTermAttributeImpl();
            this.copyTo(other);
            return other;
        }
        
        public void reflectWith(final AttributeReflector reflector) {
            reflector.reflect((Class)TermToBytesRefAttribute.class, "bytes", (Object)this.getBytesRef());
        }
        
        public CharSequence toUTF16() {
            if (this.charsRef == null) {
                this.charsRef = new CharsRefBuilder();
            }
            this.charsRef.copyUTF8Bytes(this.getBytesRef());
            return (CharSequence)this.charsRef.get();
        }
    }
    
    public interface BytesRefBuilderTermAttribute extends TermToBytesRefAttribute
    {
        BytesRefBuilder builder();
        
        CharSequence toUTF16();
    }
}
