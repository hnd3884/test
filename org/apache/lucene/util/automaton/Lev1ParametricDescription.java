package org.apache.lucene.util.automaton;

class Lev1ParametricDescription extends LevenshteinAutomata.ParametricDescription
{
    private static final long[] toStates0;
    private static final long[] offsetIncrs0;
    private static final long[] toStates1;
    private static final long[] offsetIncrs1;
    private static final long[] toStates2;
    private static final long[] offsetIncrs2;
    private static final long[] toStates3;
    private static final long[] offsetIncrs3;
    
    @Override
    int transition(final int absState, final int position, final int vector) {
        assert absState != -1;
        int state = absState / (this.w + 1);
        int offset = absState % (this.w + 1);
        assert offset >= 0;
        if (position == this.w) {
            if (state < 2) {
                final int loc = vector * 2 + state;
                offset += this.unpack(Lev1ParametricDescription.offsetIncrs0, loc, 1);
                state = this.unpack(Lev1ParametricDescription.toStates0, loc, 2) - 1;
            }
        }
        else if (position == this.w - 1) {
            if (state < 3) {
                final int loc = vector * 3 + state;
                offset += this.unpack(Lev1ParametricDescription.offsetIncrs1, loc, 1);
                state = this.unpack(Lev1ParametricDescription.toStates1, loc, 2) - 1;
            }
        }
        else if (position == this.w - 2) {
            if (state < 5) {
                final int loc = vector * 5 + state;
                offset += this.unpack(Lev1ParametricDescription.offsetIncrs2, loc, 2);
                state = this.unpack(Lev1ParametricDescription.toStates2, loc, 3) - 1;
            }
        }
        else if (state < 5) {
            final int loc = vector * 5 + state;
            offset += this.unpack(Lev1ParametricDescription.offsetIncrs3, loc, 2);
            state = this.unpack(Lev1ParametricDescription.toStates3, loc, 3) - 1;
        }
        if (state == -1) {
            return -1;
        }
        return state * (this.w + 1) + offset;
    }
    
    public Lev1ParametricDescription(final int w) {
        super(w, 1, new int[] { 0, 1, 0, -1, -1 });
    }
    
    static {
        toStates0 = new long[] { 2L };
        offsetIncrs0 = new long[] { 0L };
        toStates1 = new long[] { 2627L };
        offsetIncrs1 = new long[] { 56L };
        toStates2 = new long[] { 475737946583105539L };
        offsetIncrs2 = new long[] { 366504083456L };
        toStates3 = new long[] { 1625984326543966211L, 50000099178482249L };
        offsetIncrs3 = new long[] { 6148915115578032128L, 21845L };
    }
}
