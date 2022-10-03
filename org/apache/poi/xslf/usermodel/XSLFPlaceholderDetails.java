package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMaster;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMaster;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.STPlaceholderSize;
import java.util.function.Function;
import java.util.function.Consumer;
import org.openxmlformats.schemas.presentationml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.presentationml.x2006.main.STPlaceholderType;
import org.apache.poi.sl.usermodel.Placeholder;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.apache.poi.sl.usermodel.PlaceholderDetails;

public class XSLFPlaceholderDetails implements PlaceholderDetails
{
    private final XSLFShape shape;
    private CTPlaceholder _ph;
    private static final QName[] NV_CONTAINER;
    private static final QName[] NV_PROPS;
    
    XSLFPlaceholderDetails(final XSLFShape shape) {
        this.shape = shape;
    }
    
    public Placeholder getPlaceholder() {
        final CTPlaceholder ph = this.getCTPlaceholder(false);
        if (ph == null || (!ph.isSetType() && !ph.isSetIdx())) {
            return null;
        }
        return Placeholder.lookupOoxml(ph.getType().intValue());
    }
    
    public void setPlaceholder(final Placeholder placeholder) {
        final CTPlaceholder ph = this.getCTPlaceholder(placeholder != null);
        if (ph != null) {
            if (placeholder != null) {
                ph.setType(STPlaceholderType.Enum.forInt(placeholder.ooxmlId));
            }
            else {
                this.getNvProps().unsetPh();
            }
        }
    }
    
    public boolean isVisible() {
        final CTPlaceholder ph = this.getCTPlaceholder(false);
        if (ph == null || !ph.isSetType()) {
            return true;
        }
        final CTHeaderFooter hf = this.getHeaderFooter(false);
        if (hf == null) {
            return false;
        }
        final Placeholder pl = Placeholder.lookupOoxml(ph.getType().intValue());
        if (pl == null) {
            return true;
        }
        switch (pl) {
            case DATETIME: {
                return !hf.isSetDt() || hf.getDt();
            }
            case FOOTER: {
                return !hf.isSetFtr() || hf.getFtr();
            }
            case HEADER: {
                return !hf.isSetHdr() || hf.getHdr();
            }
            case SLIDE_NUMBER: {
                return !hf.isSetSldNum() || hf.getSldNum();
            }
            default: {
                return true;
            }
        }
    }
    
    public void setVisible(final boolean isVisible) {
        final Placeholder ph = this.getPlaceholder();
        if (ph == null) {
            return;
        }
        Function<CTHeaderFooter, Consumer<Boolean>> fun = null;
        switch (ph) {
            case DATETIME: {
                final CTHeaderFooter hf;
                fun = (Function<CTHeaderFooter, Consumer<Boolean>>)(hf -> hf::setDt);
                break;
            }
            case FOOTER: {
                final CTHeaderFooter hf;
                fun = (Function<CTHeaderFooter, Consumer<Boolean>>)(hf -> hf::setFtr);
                break;
            }
            case HEADER: {
                final CTHeaderFooter hf;
                fun = (Function<CTHeaderFooter, Consumer<Boolean>>)(hf -> hf::setHdr);
                break;
            }
            case SLIDE_NUMBER: {
                final CTHeaderFooter hf;
                fun = (Function<CTHeaderFooter, Consumer<Boolean>>)(hf -> hf::setSldNum);
                break;
            }
            default: {
                return;
            }
        }
        final CTHeaderFooter hf = this.getHeaderFooter(true);
        if (hf == null) {
            return;
        }
        fun.apply(hf).accept(isVisible);
    }
    
    public PlaceholderDetails.PlaceholderSize getSize() {
        final CTPlaceholder ph = this.getCTPlaceholder(false);
        if (ph == null || !ph.isSetSz()) {
            return null;
        }
        switch (ph.getSz().intValue()) {
            case 1: {
                return PlaceholderDetails.PlaceholderSize.full;
            }
            case 2: {
                return PlaceholderDetails.PlaceholderSize.half;
            }
            case 3: {
                return PlaceholderDetails.PlaceholderSize.quarter;
            }
            default: {
                return null;
            }
        }
    }
    
    public void setSize(final PlaceholderDetails.PlaceholderSize size) {
        final CTPlaceholder ph = this.getCTPlaceholder(false);
        if (ph == null) {
            return;
        }
        if (size == null) {
            ph.unsetSz();
            return;
        }
        switch (size) {
            case full: {
                ph.setSz(STPlaceholderSize.FULL);
                break;
            }
            case half: {
                ph.setSz(STPlaceholderSize.HALF);
                break;
            }
            case quarter: {
                ph.setSz(STPlaceholderSize.QUARTER);
                break;
            }
        }
    }
    
    CTPlaceholder getCTPlaceholder(final boolean create) {
        if (this._ph != null) {
            return this._ph;
        }
        final CTApplicationNonVisualDrawingProps nv = this.getNvProps();
        if (nv == null) {
            return null;
        }
        return this._ph = ((nv.isSetPh() || !create) ? nv.getPh() : nv.addNewPh());
    }
    
    private CTApplicationNonVisualDrawingProps getNvProps() {
        try {
            return this.shape.selectProperty(CTApplicationNonVisualDrawingProps.class, null, new QName[][] { XSLFPlaceholderDetails.NV_CONTAINER, XSLFPlaceholderDetails.NV_PROPS });
        }
        catch (final XmlException e) {
            return null;
        }
    }
    
    private CTHeaderFooter getHeaderFooter(final boolean create) {
        final XSLFSheet sheet = this.shape.getSheet();
        final XSLFSheet master = (XSLFSheet)((sheet instanceof MasterSheet && !(sheet instanceof XSLFSlideLayout)) ? sheet : sheet.getMasterSheet());
        if (master instanceof XSLFSlideMaster) {
            final CTSlideMaster ct = ((XSLFSlideMaster)master).getXmlObject();
            return (ct.isSetHf() || !create) ? ct.getHf() : ct.addNewHf();
        }
        if (master instanceof XSLFNotesMaster) {
            final CTNotesMaster ct2 = ((XSLFNotesMaster)master).getXmlObject();
            return (ct2.isSetHf() || !create) ? ct2.getHf() : ct2.addNewHf();
        }
        return null;
    }
    
    public String getText() {
        return null;
    }
    
    public void setText(final String text) {
    }
    
    static {
        NV_CONTAINER = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvCxnSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGrpSpPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPicPr"), new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGraphicFramePr") };
        NV_PROPS = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPr") };
    }
}
