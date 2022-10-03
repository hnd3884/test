package javax.imageio.plugins.bmp;

import com.sun.imageio.plugins.bmp.BMPCompressionTypes;
import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class BMPImageWriteParam extends ImageWriteParam
{
    private boolean topDown;
    
    public BMPImageWriteParam(final Locale locale) {
        super(locale);
        this.topDown = false;
        this.compressionTypes = BMPCompressionTypes.getCompressionTypes();
        this.canWriteCompressed = true;
        this.compressionMode = 3;
        this.compressionType = this.compressionTypes[0];
    }
    
    public BMPImageWriteParam() {
        this(null);
    }
    
    public void setTopDown(final boolean topDown) {
        this.topDown = topDown;
    }
    
    public boolean isTopDown() {
        return this.topDown;
    }
}
