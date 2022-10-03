package org.tukaani.xz;

class DeltaEncoder extends DeltaCoder implements FilterEncoder
{
    private final DeltaOptions options;
    private final byte[] props;
    
    DeltaEncoder(final DeltaOptions deltaOptions) {
        (this.props = new byte[1])[0] = (byte)(deltaOptions.getDistance() - 1);
        this.options = (DeltaOptions)deltaOptions.clone();
    }
    
    @Override
    public long getFilterID() {
        return 3L;
    }
    
    @Override
    public byte[] getFilterProps() {
        return this.props;
    }
    
    @Override
    public boolean supportsFlushing() {
        return true;
    }
    
    @Override
    public FinishableOutputStream getOutputStream(final FinishableOutputStream finishableOutputStream, final ArrayCache arrayCache) {
        return this.options.getOutputStream(finishableOutputStream, arrayCache);
    }
}
