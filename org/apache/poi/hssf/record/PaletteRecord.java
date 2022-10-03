package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import java.util.Iterator;
import org.apache.poi.util.LittleEndianOutput;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

public final class PaletteRecord extends StandardRecord
{
    public static final short sid = 146;
    public static final byte STANDARD_PALETTE_SIZE = 56;
    public static final short FIRST_COLOR_INDEX = 8;
    private final ArrayList<PColor> _colors;
    
    public PaletteRecord() {
        this._colors = new ArrayList<PColor>();
        final PColor[] defaultPalette = createDefaultPalette();
        this._colors.ensureCapacity(defaultPalette.length);
        Collections.addAll(this._colors, defaultPalette);
    }
    
    public PaletteRecord(final PaletteRecord other) {
        super(other);
        (this._colors = new ArrayList<PColor>()).ensureCapacity(other._colors.size());
        other._colors.stream().map((Function<? super Object, ?>)PColor::new).forEach(this._colors::add);
    }
    
    public PaletteRecord(final RecordInputStream in) {
        this._colors = new ArrayList<PColor>();
        final int field_1_numcolors = in.readShort();
        this._colors.ensureCapacity(field_1_numcolors);
        for (int k = 0; k < field_1_numcolors; ++k) {
            this._colors.add(new PColor(in));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[PALETTE]\n");
        buffer.append("  numcolors     = ").append(this._colors.size()).append('\n');
        for (int i = 0; i < this._colors.size(); ++i) {
            final PColor c = this._colors.get(i);
            buffer.append("* colornum      = ").append(i).append('\n');
            buffer.append(c);
            buffer.append("/*colornum      = ").append(i).append('\n');
        }
        buffer.append("[/PALETTE]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._colors.size());
        for (final PColor color : this._colors) {
            color.serialize(out);
        }
    }
    
    @Override
    protected int getDataSize() {
        return 2 + this._colors.size() * 4;
    }
    
    @Override
    public short getSid() {
        return 146;
    }
    
    public byte[] getColor(final int byteIndex) {
        final int i = byteIndex - 8;
        if (i < 0 || i >= this._colors.size()) {
            return null;
        }
        return this._colors.get(i).getTriplet();
    }
    
    public void setColor(final short byteIndex, final byte red, final byte green, final byte blue) {
        final int i = byteIndex - 8;
        if (i < 0 || i >= 56) {
            return;
        }
        while (this._colors.size() <= i) {
            this._colors.add(new PColor(0, 0, 0));
        }
        final PColor custColor = new PColor(red, green, blue);
        this._colors.set(i, custColor);
    }
    
    @Override
    public PaletteRecord copy() {
        return new PaletteRecord(this);
    }
    
    private static PColor[] createDefaultPalette() {
        return new PColor[] { pc(0, 0, 0), pc(255, 255, 255), pc(255, 0, 0), pc(0, 255, 0), pc(0, 0, 255), pc(255, 255, 0), pc(255, 0, 255), pc(0, 255, 255), pc(128, 0, 0), pc(0, 128, 0), pc(0, 0, 128), pc(128, 128, 0), pc(128, 0, 128), pc(0, 128, 128), pc(192, 192, 192), pc(128, 128, 128), pc(153, 153, 255), pc(153, 51, 102), pc(255, 255, 204), pc(204, 255, 255), pc(102, 0, 102), pc(255, 128, 128), pc(0, 102, 204), pc(204, 204, 255), pc(0, 0, 128), pc(255, 0, 255), pc(255, 255, 0), pc(0, 255, 255), pc(128, 0, 128), pc(128, 0, 0), pc(0, 128, 128), pc(0, 0, 255), pc(0, 204, 255), pc(204, 255, 255), pc(204, 255, 204), pc(255, 255, 153), pc(153, 204, 255), pc(255, 153, 204), pc(204, 153, 255), pc(255, 204, 153), pc(51, 102, 255), pc(51, 204, 204), pc(153, 204, 0), pc(255, 204, 0), pc(255, 153, 0), pc(255, 102, 0), pc(102, 102, 153), pc(150, 150, 150), pc(0, 51, 102), pc(51, 153, 102), pc(0, 51, 0), pc(51, 51, 0), pc(153, 51, 0), pc(153, 51, 102), pc(51, 51, 153), pc(51, 51, 51) };
    }
    
    private static PColor pc(final int r, final int g, final int b) {
        return new PColor(r, g, b);
    }
    
    private static final class PColor
    {
        public static final short ENCODED_SIZE = 4;
        private final int _red;
        private final int _green;
        private final int _blue;
        
        public PColor(final int red, final int green, final int blue) {
            this._red = red;
            this._green = green;
            this._blue = blue;
        }
        
        public PColor(final PColor other) {
            this._red = other._red;
            this._green = other._green;
            this._blue = other._blue;
        }
        
        public PColor(final RecordInputStream in) {
            this._red = in.readByte();
            this._green = in.readByte();
            this._blue = in.readByte();
            in.readByte();
        }
        
        public byte[] getTriplet() {
            return new byte[] { (byte)this._red, (byte)this._green, (byte)this._blue };
        }
        
        public void serialize(final LittleEndianOutput out) {
            out.writeByte(this._red);
            out.writeByte(this._green);
            out.writeByte(this._blue);
            out.writeByte(0);
        }
        
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            buffer.append("  red   = ").append(this._red & 0xFF).append('\n');
            buffer.append("  green = ").append(this._green & 0xFF).append('\n');
            buffer.append("  blue  = ").append(this._blue & 0xFF).append('\n');
            return buffer.toString();
        }
    }
}
