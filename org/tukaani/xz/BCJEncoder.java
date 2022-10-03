package org.tukaani.xz;

class BCJEncoder extends BCJCoder implements FilterEncoder
{
    private final BCJOptions options;
    private final long filterID;
    private final byte[] props;
    
    BCJEncoder(final BCJOptions bcjOptions, final long filterID) {
        assert BCJCoder.isBCJFilterID(filterID);
        final int startOffset = bcjOptions.getStartOffset();
        if (startOffset == 0) {
            this.props = new byte[0];
        }
        else {
            this.props = new byte[4];
            for (int i = 0; i < 4; ++i) {
                this.props[i] = (byte)(startOffset >>> i * 8);
            }
        }
        this.filterID = filterID;
        this.options = (BCJOptions)bcjOptions.clone();
    }
    
    @Override
    public long getFilterID() {
        return this.filterID;
    }
    
    @Override
    public byte[] getFilterProps() {
        return this.props;
    }
    
    @Override
    public boolean supportsFlushing() {
        return false;
    }
    
    @Override
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream, final ArrayCache arrayCache) {
        return this.options.getOutputStream(finishableOutputStream, arrayCache);
    }
}
