package com.google.zxing.oned.rss.expanded.decoders;

final class BlockParsedResult
{
    private final DecodedInformation decodedInformation;
    private final boolean finished;
    
    BlockParsedResult(final boolean finished) {
        this(null, finished);
    }
    
    BlockParsedResult(final DecodedInformation information, final boolean finished) {
        this.finished = finished;
        this.decodedInformation = information;
    }
    
    DecodedInformation getDecodedInformation() {
        return this.decodedInformation;
    }
    
    boolean isFinished() {
        return this.finished;
    }
}
