package org.apache.lucene.analysis;

import java.io.IOException;
import org.apache.lucene.util.RollingBuffer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.BytesRef;

public class TokenStreamToAutomaton
{
    private boolean preservePositionIncrements;
    private boolean unicodeArcs;
    public static final int POS_SEP = 31;
    public static final int HOLE = 30;
    
    public TokenStreamToAutomaton() {
        this.preservePositionIncrements = true;
    }
    
    public void setPreservePositionIncrements(final boolean enablePositionIncrements) {
        this.preservePositionIncrements = enablePositionIncrements;
    }
    
    public void setUnicodeArcs(final boolean unicodeArcs) {
        this.unicodeArcs = unicodeArcs;
    }
    
    protected BytesRef changeToken(final BytesRef in) {
        return in;
    }
    
    public Automaton toAutomaton(final TokenStream in) throws IOException {
        final Automaton.Builder builder = new Automaton.Builder();
        builder.createState();
        final TermToBytesRefAttribute termBytesAtt = in.addAttribute(TermToBytesRefAttribute.class);
        final PositionIncrementAttribute posIncAtt = in.addAttribute(PositionIncrementAttribute.class);
        final PositionLengthAttribute posLengthAtt = in.addAttribute(PositionLengthAttribute.class);
        final OffsetAttribute offsetAtt = in.addAttribute(OffsetAttribute.class);
        in.reset();
        final RollingBuffer<Position> positions = new Positions();
        int pos = -1;
        Position posData = null;
        int maxOffset = 0;
        while (in.incrementToken()) {
            int posInc = posIncAtt.getPositionIncrement();
            if (!this.preservePositionIncrements && posInc > 1) {
                posInc = 1;
            }
            assert posInc > 0;
            if (posInc > 0) {
                pos += posInc;
                posData = positions.get(pos);
                assert posData.leaving == -1;
                if (posData.arriving == -1) {
                    if (pos == 0) {
                        posData.leaving = 0;
                    }
                    else {
                        posData.leaving = builder.createState();
                        addHoles(builder, positions, pos);
                    }
                }
                else {
                    posData.leaving = builder.createState();
                    builder.addTransition(posData.arriving, posData.leaving, 31);
                    if (posInc > 1) {
                        addHoles(builder, positions, pos);
                    }
                }
                positions.freeBefore(pos);
            }
            final int endPos = pos + posLengthAtt.getPositionLength();
            final BytesRef termUTF8 = this.changeToken(termBytesAtt.getBytesRef());
            int[] termUnicode = null;
            final Position endPosData = positions.get(endPos);
            if (endPosData.arriving == -1) {
                endPosData.arriving = builder.createState();
            }
            int termLen;
            if (this.unicodeArcs) {
                final String utf16 = termUTF8.utf8ToString();
                termUnicode = new int[utf16.codePointCount(0, utf16.length())];
                termLen = termUnicode.length;
                int i = 0;
                int j = 0;
                while (i < utf16.length()) {
                    final int cp = termUnicode[j++] = utf16.codePointAt(i);
                    i += Character.charCount(cp);
                }
            }
            else {
                termLen = termUTF8.length;
            }
            int state = posData.leaving;
            for (int byteIDX = 0; byteIDX < termLen; ++byteIDX) {
                final int nextState = (byteIDX == termLen - 1) ? endPosData.arriving : builder.createState();
                int c;
                if (this.unicodeArcs) {
                    c = termUnicode[byteIDX];
                }
                else {
                    c = (termUTF8.bytes[termUTF8.offset + byteIDX] & 0xFF);
                }
                builder.addTransition(state, nextState, c);
                state = nextState;
            }
            maxOffset = Math.max(maxOffset, offsetAtt.endOffset());
        }
        in.end();
        int endState = -1;
        if (offsetAtt.endOffset() > maxOffset) {
            endState = builder.createState();
            builder.setAccept(endState, true);
        }
        ++pos;
        while (pos <= positions.getMaxPos()) {
            posData = positions.get(pos);
            if (posData.arriving != -1) {
                if (endState != -1) {
                    builder.addTransition(posData.arriving, endState, 31);
                }
                else {
                    builder.setAccept(posData.arriving, true);
                }
            }
            ++pos;
        }
        return builder.finish();
    }
    
    private static void addHoles(final Automaton.Builder builder, final RollingBuffer<Position> positions, int pos) {
        for (Position posData = positions.get(pos), prevPosData = positions.get(pos - 1); posData.arriving == -1 || prevPosData.leaving == -1; posData = prevPosData, prevPosData = positions.get(pos - 1)) {
            if (posData.arriving == -1) {
                builder.addTransition(posData.arriving = builder.createState(), posData.leaving, 31);
            }
            if (prevPosData.leaving == -1) {
                if (pos == 1) {
                    prevPosData.leaving = 0;
                }
                else {
                    prevPosData.leaving = builder.createState();
                }
                if (prevPosData.arriving != -1) {
                    builder.addTransition(prevPosData.arriving, prevPosData.leaving, 31);
                }
            }
            builder.addTransition(prevPosData.leaving, posData.arriving, 30);
            if (--pos <= 0) {
                break;
            }
        }
    }
    
    private static class Position implements RollingBuffer.Resettable
    {
        int arriving;
        int leaving;
        
        private Position() {
            this.arriving = -1;
            this.leaving = -1;
        }
        
        @Override
        public void reset() {
            this.arriving = -1;
            this.leaving = -1;
        }
    }
    
    private static class Positions extends RollingBuffer<Position>
    {
        @Override
        protected Position newInstance() {
            return new Position();
        }
    }
}
