package org.apache.poi.xddf.usermodel;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STTileFlipMode;

public enum TileFlipMode
{
    NONE(STTileFlipMode.NONE), 
    X(STTileFlipMode.X), 
    XY(STTileFlipMode.XY), 
    Y(STTileFlipMode.Y);
    
    final STTileFlipMode.Enum underlying;
    private static final HashMap<STTileFlipMode.Enum, TileFlipMode> reverse;
    
    private TileFlipMode(final STTileFlipMode.Enum mode) {
        this.underlying = mode;
    }
    
    static TileFlipMode valueOf(final STTileFlipMode.Enum mode) {
        return TileFlipMode.reverse.get(mode);
    }
    
    static {
        reverse = new HashMap<STTileFlipMode.Enum, TileFlipMode>();
        for (final TileFlipMode value : values()) {
            TileFlipMode.reverse.put(value.underlying, value);
        }
    }
}
