package org.apache.poi.xslf.usermodel;

import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;
import org.apache.poi.util.POILogFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
class XSLFPropertiesDelegate
{
    private static final POILogger LOG;
    
    public static XSLFFillProperties getFillDelegate(final XmlObject props) {
        return getDelegate(XSLFFillProperties.class, props);
    }
    
    public static XSLFGeometryProperties getGeometryDelegate(final XmlObject props) {
        return getDelegate(XSLFGeometryProperties.class, props);
    }
    
    public static XSLFEffectProperties getEffectDelegate(final XmlObject props) {
        return getDelegate(XSLFEffectProperties.class, props);
    }
    
    private static <T> T getDelegate(final Class<T> clazz, final XmlObject props) {
        Object obj = null;
        if (props == null) {
            return null;
        }
        if (props instanceof CTShapeProperties) {
            obj = new ShapeDelegate((CTShapeProperties)props);
        }
        else if (props instanceof CTBackgroundProperties) {
            obj = new BackgroundDelegate((CTBackgroundProperties)props);
        }
        else if (props instanceof CTStyleMatrixReference) {
            obj = new StyleMatrixDelegate((CTStyleMatrixReference)props);
        }
        else if (props instanceof CTTableCellProperties) {
            obj = new TableCellDelegate((CTTableCellProperties)props);
        }
        else if (props instanceof CTNoFillProperties || props instanceof CTSolidColorFillProperties || props instanceof CTGradientFillProperties || props instanceof CTBlipFillProperties || props instanceof CTPatternFillProperties || props instanceof CTGroupFillProperties) {
            obj = new FillPartDelegate(props);
        }
        else if (props instanceof CTFillProperties) {
            obj = new FillDelegate((CTFillProperties)props);
        }
        else if (props instanceof CTLineProperties) {
            obj = new LineStyleDelegate((CTLineProperties)props);
        }
        else {
            if (!(props instanceof CTTextCharacterProperties)) {
                XSLFPropertiesDelegate.LOG.log(7, new Object[] { props.getClass() + " is an unknown properties type" });
                return null;
            }
            obj = new TextCharDelegate((CTTextCharacterProperties)props);
        }
        if (clazz.isInstance(obj)) {
            return (T)obj;
        }
        XSLFPropertiesDelegate.LOG.log(5, new Object[] { obj.getClass() + " doesn't implement " + clazz });
        return null;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSLFPropertiesDelegate.class);
    }
    
    private static class ShapeDelegate implements XSLFFillProperties, XSLFGeometryProperties, XSLFEffectProperties
    {
        final CTShapeProperties props;
        
        ShapeDelegate(final CTShapeProperties props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return this.props.getNoFill();
        }
        
        @Override
        public boolean isSetNoFill() {
            return this.props.isSetNoFill();
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
            this.props.setNoFill(noFill);
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return this.props.addNewNoFill();
        }
        
        @Override
        public void unsetNoFill() {
            this.props.unsetNoFill();
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return this.props.getSolidFill();
        }
        
        @Override
        public boolean isSetSolidFill() {
            return this.props.isSetSolidFill();
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
            this.props.setSolidFill(solidFill);
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return this.props.addNewSolidFill();
        }
        
        @Override
        public void unsetSolidFill() {
            this.props.unsetSolidFill();
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return this.props.getGradFill();
        }
        
        @Override
        public boolean isSetGradFill() {
            return this.props.isSetGradFill();
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
            this.props.setGradFill(gradFill);
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return this.props.addNewGradFill();
        }
        
        @Override
        public void unsetGradFill() {
            this.props.unsetGradFill();
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return this.props.getBlipFill();
        }
        
        @Override
        public boolean isSetBlipFill() {
            return this.props.isSetBlipFill();
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
            this.props.setBlipFill(blipFill);
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return this.props.addNewBlipFill();
        }
        
        @Override
        public void unsetBlipFill() {
            this.props.unsetBlipFill();
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return this.props.getPattFill();
        }
        
        @Override
        public boolean isSetPattFill() {
            return this.props.isSetPattFill();
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
            this.props.setPattFill(pattFill);
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return this.props.addNewPattFill();
        }
        
        @Override
        public void unsetPattFill() {
            this.props.unsetPattFill();
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return this.props.getGrpFill();
        }
        
        @Override
        public boolean isSetGrpFill() {
            return this.props.isSetGrpFill();
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
            this.props.setGrpFill(grpFill);
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return this.props.addNewGrpFill();
        }
        
        @Override
        public void unsetGrpFill() {
            this.props.unsetGrpFill();
        }
        
        @Override
        public CTCustomGeometry2D getCustGeom() {
            return this.props.getCustGeom();
        }
        
        @Override
        public boolean isSetCustGeom() {
            return this.props.isSetCustGeom();
        }
        
        @Override
        public void setCustGeom(final CTCustomGeometry2D custGeom) {
            this.props.setCustGeom(custGeom);
        }
        
        @Override
        public CTCustomGeometry2D addNewCustGeom() {
            return this.props.addNewCustGeom();
        }
        
        @Override
        public void unsetCustGeom() {
            this.props.unsetCustGeom();
        }
        
        @Override
        public CTPresetGeometry2D getPrstGeom() {
            return this.props.getPrstGeom();
        }
        
        @Override
        public boolean isSetPrstGeom() {
            return this.props.isSetPrstGeom();
        }
        
        @Override
        public void setPrstGeom(final CTPresetGeometry2D prstGeom) {
            this.props.setPrstGeom(prstGeom);
        }
        
        @Override
        public CTPresetGeometry2D addNewPrstGeom() {
            return this.props.addNewPrstGeom();
        }
        
        @Override
        public void unsetPrstGeom() {
            this.props.unsetPrstGeom();
        }
        
        @Override
        public CTEffectList getEffectLst() {
            return this.props.getEffectLst();
        }
        
        @Override
        public boolean isSetEffectLst() {
            return this.props.isSetEffectLst();
        }
        
        @Override
        public void setEffectLst(final CTEffectList effectLst) {
            this.props.setEffectLst(effectLst);
        }
        
        @Override
        public CTEffectList addNewEffectLst() {
            return this.props.addNewEffectLst();
        }
        
        @Override
        public void unsetEffectLst() {
            this.props.unsetEffectLst();
        }
        
        @Override
        public CTEffectContainer getEffectDag() {
            return this.props.getEffectDag();
        }
        
        @Override
        public boolean isSetEffectDag() {
            return this.props.isSetEffectDag();
        }
        
        @Override
        public void setEffectDag(final CTEffectContainer effectDag) {
            this.props.setEffectDag(effectDag);
        }
        
        @Override
        public CTEffectContainer addNewEffectDag() {
            return this.props.addNewEffectDag();
        }
        
        @Override
        public void unsetEffectDag() {
            this.props.unsetEffectDag();
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return false;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return null;
        }
        
        @Override
        public boolean isLineStyle() {
            return false;
        }
    }
    
    private static class BackgroundDelegate implements XSLFFillProperties, XSLFEffectProperties
    {
        final CTBackgroundProperties props;
        
        BackgroundDelegate(final CTBackgroundProperties props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return this.props.getNoFill();
        }
        
        @Override
        public boolean isSetNoFill() {
            return this.props.isSetNoFill();
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
            this.props.setNoFill(noFill);
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return this.props.addNewNoFill();
        }
        
        @Override
        public void unsetNoFill() {
            this.props.unsetNoFill();
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return this.props.getSolidFill();
        }
        
        @Override
        public boolean isSetSolidFill() {
            return this.props.isSetSolidFill();
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
            this.props.setSolidFill(solidFill);
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return this.props.addNewSolidFill();
        }
        
        @Override
        public void unsetSolidFill() {
            this.props.unsetSolidFill();
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return this.props.getGradFill();
        }
        
        @Override
        public boolean isSetGradFill() {
            return this.props.isSetGradFill();
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
            this.props.setGradFill(gradFill);
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return this.props.addNewGradFill();
        }
        
        @Override
        public void unsetGradFill() {
            this.props.unsetGradFill();
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return this.props.getBlipFill();
        }
        
        @Override
        public boolean isSetBlipFill() {
            return this.props.isSetBlipFill();
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
            this.props.setBlipFill(blipFill);
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return this.props.addNewBlipFill();
        }
        
        @Override
        public void unsetBlipFill() {
            this.props.unsetBlipFill();
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return this.props.getPattFill();
        }
        
        @Override
        public boolean isSetPattFill() {
            return this.props.isSetPattFill();
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
            this.props.setPattFill(pattFill);
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return this.props.addNewPattFill();
        }
        
        @Override
        public void unsetPattFill() {
            this.props.unsetPattFill();
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return this.props.getGrpFill();
        }
        
        @Override
        public boolean isSetGrpFill() {
            return this.props.isSetGrpFill();
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
            this.props.setGrpFill(grpFill);
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return this.props.addNewGrpFill();
        }
        
        @Override
        public void unsetGrpFill() {
            this.props.unsetGrpFill();
        }
        
        @Override
        public CTEffectList getEffectLst() {
            return this.props.getEffectLst();
        }
        
        @Override
        public boolean isSetEffectLst() {
            return this.props.isSetEffectLst();
        }
        
        @Override
        public void setEffectLst(final CTEffectList effectLst) {
            this.props.setEffectLst(effectLst);
        }
        
        @Override
        public CTEffectList addNewEffectLst() {
            return this.props.addNewEffectLst();
        }
        
        @Override
        public void unsetEffectLst() {
            this.props.unsetEffectLst();
        }
        
        @Override
        public CTEffectContainer getEffectDag() {
            return this.props.getEffectDag();
        }
        
        @Override
        public boolean isSetEffectDag() {
            return this.props.isSetEffectDag();
        }
        
        @Override
        public void setEffectDag(final CTEffectContainer effectDag) {
            this.props.setEffectDag(effectDag);
        }
        
        @Override
        public CTEffectContainer addNewEffectDag() {
            return this.props.addNewEffectDag();
        }
        
        @Override
        public void unsetEffectDag() {
            this.props.unsetEffectDag();
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return false;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return null;
        }
        
        @Override
        public boolean isLineStyle() {
            return false;
        }
    }
    
    private static class TableCellDelegate implements XSLFFillProperties
    {
        final CTTableCellProperties props;
        
        TableCellDelegate(final CTTableCellProperties props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return this.props.getNoFill();
        }
        
        @Override
        public boolean isSetNoFill() {
            return this.props.isSetNoFill();
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
            this.props.setNoFill(noFill);
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return this.props.addNewNoFill();
        }
        
        @Override
        public void unsetNoFill() {
            this.props.unsetNoFill();
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return this.props.getSolidFill();
        }
        
        @Override
        public boolean isSetSolidFill() {
            return this.props.isSetSolidFill();
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
            this.props.setSolidFill(solidFill);
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return this.props.addNewSolidFill();
        }
        
        @Override
        public void unsetSolidFill() {
            this.props.unsetSolidFill();
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return this.props.getGradFill();
        }
        
        @Override
        public boolean isSetGradFill() {
            return this.props.isSetGradFill();
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
            this.props.setGradFill(gradFill);
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return this.props.addNewGradFill();
        }
        
        @Override
        public void unsetGradFill() {
            this.props.unsetGradFill();
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return this.props.getBlipFill();
        }
        
        @Override
        public boolean isSetBlipFill() {
            return this.props.isSetBlipFill();
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
            this.props.setBlipFill(blipFill);
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return this.props.addNewBlipFill();
        }
        
        @Override
        public void unsetBlipFill() {
            this.props.unsetBlipFill();
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return this.props.getPattFill();
        }
        
        @Override
        public boolean isSetPattFill() {
            return this.props.isSetPattFill();
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
            this.props.setPattFill(pattFill);
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return this.props.addNewPattFill();
        }
        
        @Override
        public void unsetPattFill() {
            this.props.unsetPattFill();
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return this.props.getGrpFill();
        }
        
        @Override
        public boolean isSetGrpFill() {
            return this.props.isSetGrpFill();
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
            this.props.setGrpFill(grpFill);
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return this.props.addNewGrpFill();
        }
        
        @Override
        public void unsetGrpFill() {
            this.props.unsetGrpFill();
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return false;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return null;
        }
        
        @Override
        public boolean isLineStyle() {
            return false;
        }
    }
    
    private static class StyleMatrixDelegate implements XSLFFillProperties
    {
        final CTStyleMatrixReference props;
        
        StyleMatrixDelegate(final CTStyleMatrixReference props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return null;
        }
        
        @Override
        public boolean isSetNoFill() {
            return false;
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return null;
        }
        
        @Override
        public void unsetNoFill() {
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return null;
        }
        
        @Override
        public boolean isSetSolidFill() {
            return false;
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return null;
        }
        
        @Override
        public void unsetSolidFill() {
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return null;
        }
        
        @Override
        public boolean isSetGradFill() {
            return false;
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return null;
        }
        
        @Override
        public void unsetGradFill() {
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return null;
        }
        
        @Override
        public boolean isSetBlipFill() {
            return false;
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return null;
        }
        
        @Override
        public void unsetBlipFill() {
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return null;
        }
        
        @Override
        public boolean isSetPattFill() {
            return false;
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return null;
        }
        
        @Override
        public void unsetPattFill() {
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return null;
        }
        
        @Override
        public boolean isSetGrpFill() {
            return false;
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return null;
        }
        
        @Override
        public void unsetGrpFill() {
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return true;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return this.props;
        }
        
        @Override
        public boolean isLineStyle() {
            final XmlCursor cur = this.props.newCursor();
            final String name = cur.getName().getLocalPart();
            cur.dispose();
            return "lnRef".equals(name);
        }
    }
    
    private static class FillDelegate implements XSLFFillProperties
    {
        final CTFillProperties props;
        
        FillDelegate(final CTFillProperties props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return this.props.getNoFill();
        }
        
        @Override
        public boolean isSetNoFill() {
            return this.props.isSetNoFill();
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
            this.props.setNoFill(noFill);
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return this.props.addNewNoFill();
        }
        
        @Override
        public void unsetNoFill() {
            this.props.unsetNoFill();
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return this.props.getSolidFill();
        }
        
        @Override
        public boolean isSetSolidFill() {
            return this.props.isSetSolidFill();
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
            this.props.setSolidFill(solidFill);
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return this.props.addNewSolidFill();
        }
        
        @Override
        public void unsetSolidFill() {
            this.props.unsetSolidFill();
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return this.props.getGradFill();
        }
        
        @Override
        public boolean isSetGradFill() {
            return this.props.isSetGradFill();
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
            this.props.setGradFill(gradFill);
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return this.props.addNewGradFill();
        }
        
        @Override
        public void unsetGradFill() {
            this.props.unsetGradFill();
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return this.props.getBlipFill();
        }
        
        @Override
        public boolean isSetBlipFill() {
            return this.props.isSetBlipFill();
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
            this.props.setBlipFill(blipFill);
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return this.props.addNewBlipFill();
        }
        
        @Override
        public void unsetBlipFill() {
            this.props.unsetBlipFill();
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return this.props.getPattFill();
        }
        
        @Override
        public boolean isSetPattFill() {
            return this.props.isSetPattFill();
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
            this.props.setPattFill(pattFill);
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return this.props.addNewPattFill();
        }
        
        @Override
        public void unsetPattFill() {
            this.props.unsetPattFill();
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return this.props.getGrpFill();
        }
        
        @Override
        public boolean isSetGrpFill() {
            return this.props.isSetGrpFill();
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
            this.props.setGrpFill(grpFill);
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return this.props.addNewGrpFill();
        }
        
        @Override
        public void unsetGrpFill() {
            this.props.unsetGrpFill();
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return false;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return null;
        }
        
        @Override
        public boolean isLineStyle() {
            return false;
        }
    }
    
    private static class FillPartDelegate implements XSLFFillProperties
    {
        final XmlObject props;
        
        FillPartDelegate(final XmlObject props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return this.isSetNoFill() ? ((CTNoFillProperties)this.props) : null;
        }
        
        @Override
        public boolean isSetNoFill() {
            return this.props instanceof CTNoFillProperties;
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return null;
        }
        
        @Override
        public void unsetNoFill() {
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return this.isSetSolidFill() ? ((CTSolidColorFillProperties)this.props) : null;
        }
        
        @Override
        public boolean isSetSolidFill() {
            return this.props instanceof CTSolidColorFillProperties;
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return null;
        }
        
        @Override
        public void unsetSolidFill() {
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return this.isSetGradFill() ? ((CTGradientFillProperties)this.props) : null;
        }
        
        @Override
        public boolean isSetGradFill() {
            return this.props instanceof CTGradientFillProperties;
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return null;
        }
        
        @Override
        public void unsetGradFill() {
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return this.isSetBlipFill() ? ((CTBlipFillProperties)this.props) : null;
        }
        
        @Override
        public boolean isSetBlipFill() {
            return this.props instanceof CTBlipFillProperties;
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return null;
        }
        
        @Override
        public void unsetBlipFill() {
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return this.isSetPattFill() ? ((CTPatternFillProperties)this.props) : null;
        }
        
        @Override
        public boolean isSetPattFill() {
            return this.props instanceof CTPatternFillProperties;
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return null;
        }
        
        @Override
        public void unsetPattFill() {
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return this.isSetGrpFill() ? ((CTGroupFillProperties)this.props) : null;
        }
        
        @Override
        public boolean isSetGrpFill() {
            return this.props instanceof CTGroupFillProperties;
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return null;
        }
        
        @Override
        public void unsetGrpFill() {
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return false;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return null;
        }
        
        @Override
        public boolean isLineStyle() {
            return false;
        }
    }
    
    private static class LineStyleDelegate implements XSLFFillProperties
    {
        final CTLineProperties props;
        
        LineStyleDelegate(final CTLineProperties props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return this.props.getNoFill();
        }
        
        @Override
        public boolean isSetNoFill() {
            return this.props.isSetNoFill();
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
            this.props.setNoFill(noFill);
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return this.props.addNewNoFill();
        }
        
        @Override
        public void unsetNoFill() {
            this.props.unsetNoFill();
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return this.props.getSolidFill();
        }
        
        @Override
        public boolean isSetSolidFill() {
            return this.props.isSetSolidFill();
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
            this.props.setSolidFill(solidFill);
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return this.props.addNewSolidFill();
        }
        
        @Override
        public void unsetSolidFill() {
            this.props.unsetSolidFill();
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return this.props.getGradFill();
        }
        
        @Override
        public boolean isSetGradFill() {
            return this.props.isSetGradFill();
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
            this.props.setGradFill(gradFill);
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return this.props.addNewGradFill();
        }
        
        @Override
        public void unsetGradFill() {
            this.props.unsetGradFill();
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return null;
        }
        
        @Override
        public boolean isSetBlipFill() {
            return false;
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return null;
        }
        
        @Override
        public void unsetBlipFill() {
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return this.props.getPattFill();
        }
        
        @Override
        public boolean isSetPattFill() {
            return this.props.isSetPattFill();
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
            this.props.setPattFill(pattFill);
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return this.props.addNewPattFill();
        }
        
        @Override
        public void unsetPattFill() {
            this.props.unsetPattFill();
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return null;
        }
        
        @Override
        public boolean isSetGrpFill() {
            return false;
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return null;
        }
        
        @Override
        public void unsetGrpFill() {
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return false;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return null;
        }
        
        @Override
        public boolean isLineStyle() {
            return true;
        }
    }
    
    private static class TextCharDelegate implements XSLFFillProperties
    {
        final CTTextCharacterProperties props;
        
        TextCharDelegate(final CTTextCharacterProperties props) {
            this.props = props;
        }
        
        @Override
        public CTNoFillProperties getNoFill() {
            return this.props.getNoFill();
        }
        
        @Override
        public boolean isSetNoFill() {
            return this.props.isSetNoFill();
        }
        
        @Override
        public void setNoFill(final CTNoFillProperties noFill) {
            this.props.setNoFill(noFill);
        }
        
        @Override
        public CTNoFillProperties addNewNoFill() {
            return this.props.addNewNoFill();
        }
        
        @Override
        public void unsetNoFill() {
            this.props.unsetNoFill();
        }
        
        @Override
        public CTSolidColorFillProperties getSolidFill() {
            return this.props.getSolidFill();
        }
        
        @Override
        public boolean isSetSolidFill() {
            return this.props.isSetSolidFill();
        }
        
        @Override
        public void setSolidFill(final CTSolidColorFillProperties solidFill) {
            this.props.setSolidFill(solidFill);
        }
        
        @Override
        public CTSolidColorFillProperties addNewSolidFill() {
            return this.props.addNewSolidFill();
        }
        
        @Override
        public void unsetSolidFill() {
            this.props.unsetSolidFill();
        }
        
        @Override
        public CTGradientFillProperties getGradFill() {
            return this.props.getGradFill();
        }
        
        @Override
        public boolean isSetGradFill() {
            return this.props.isSetGradFill();
        }
        
        @Override
        public void setGradFill(final CTGradientFillProperties gradFill) {
            this.props.setGradFill(gradFill);
        }
        
        @Override
        public CTGradientFillProperties addNewGradFill() {
            return this.props.addNewGradFill();
        }
        
        @Override
        public void unsetGradFill() {
            this.props.unsetGradFill();
        }
        
        @Override
        public CTBlipFillProperties getBlipFill() {
            return this.props.getBlipFill();
        }
        
        @Override
        public boolean isSetBlipFill() {
            return this.props.isSetBlipFill();
        }
        
        @Override
        public void setBlipFill(final CTBlipFillProperties blipFill) {
            this.props.setBlipFill(blipFill);
        }
        
        @Override
        public CTBlipFillProperties addNewBlipFill() {
            return this.props.addNewBlipFill();
        }
        
        @Override
        public void unsetBlipFill() {
            this.props.unsetBlipFill();
        }
        
        @Override
        public CTPatternFillProperties getPattFill() {
            return this.props.getPattFill();
        }
        
        @Override
        public boolean isSetPattFill() {
            return this.props.isSetPattFill();
        }
        
        @Override
        public void setPattFill(final CTPatternFillProperties pattFill) {
            this.props.setPattFill(pattFill);
        }
        
        @Override
        public CTPatternFillProperties addNewPattFill() {
            return this.props.addNewPattFill();
        }
        
        @Override
        public void unsetPattFill() {
            this.props.unsetPattFill();
        }
        
        @Override
        public CTGroupFillProperties getGrpFill() {
            return this.props.getGrpFill();
        }
        
        @Override
        public boolean isSetGrpFill() {
            return this.props.isSetGrpFill();
        }
        
        @Override
        public void setGrpFill(final CTGroupFillProperties grpFill) {
            this.props.setGrpFill(grpFill);
        }
        
        @Override
        public CTGroupFillProperties addNewGrpFill() {
            return this.props.addNewGrpFill();
        }
        
        @Override
        public void unsetGrpFill() {
            this.props.unsetGrpFill();
        }
        
        @Override
        public boolean isSetMatrixStyle() {
            return false;
        }
        
        @Override
        public CTStyleMatrixReference getMatrixStyle() {
            return null;
        }
        
        @Override
        public boolean isLineStyle() {
            return false;
        }
    }
    
    public interface XSLFFillProperties
    {
        CTNoFillProperties getNoFill();
        
        boolean isSetNoFill();
        
        void setNoFill(final CTNoFillProperties p0);
        
        CTNoFillProperties addNewNoFill();
        
        void unsetNoFill();
        
        CTSolidColorFillProperties getSolidFill();
        
        boolean isSetSolidFill();
        
        void setSolidFill(final CTSolidColorFillProperties p0);
        
        CTSolidColorFillProperties addNewSolidFill();
        
        void unsetSolidFill();
        
        CTGradientFillProperties getGradFill();
        
        boolean isSetGradFill();
        
        void setGradFill(final CTGradientFillProperties p0);
        
        CTGradientFillProperties addNewGradFill();
        
        void unsetGradFill();
        
        CTBlipFillProperties getBlipFill();
        
        boolean isSetBlipFill();
        
        void setBlipFill(final CTBlipFillProperties p0);
        
        CTBlipFillProperties addNewBlipFill();
        
        void unsetBlipFill();
        
        CTPatternFillProperties getPattFill();
        
        boolean isSetPattFill();
        
        void setPattFill(final CTPatternFillProperties p0);
        
        CTPatternFillProperties addNewPattFill();
        
        void unsetPattFill();
        
        CTGroupFillProperties getGrpFill();
        
        boolean isSetGrpFill();
        
        void setGrpFill(final CTGroupFillProperties p0);
        
        CTGroupFillProperties addNewGrpFill();
        
        void unsetGrpFill();
        
        boolean isSetMatrixStyle();
        
        CTStyleMatrixReference getMatrixStyle();
        
        boolean isLineStyle();
    }
    
    public interface XSLFEffectProperties
    {
        CTEffectList getEffectLst();
        
        boolean isSetEffectLst();
        
        void setEffectLst(final CTEffectList p0);
        
        CTEffectList addNewEffectLst();
        
        void unsetEffectLst();
        
        CTEffectContainer getEffectDag();
        
        boolean isSetEffectDag();
        
        void setEffectDag(final CTEffectContainer p0);
        
        CTEffectContainer addNewEffectDag();
        
        void unsetEffectDag();
    }
    
    public interface XSLFGeometryProperties
    {
        CTCustomGeometry2D getCustGeom();
        
        boolean isSetCustGeom();
        
        void setCustGeom(final CTCustomGeometry2D p0);
        
        CTCustomGeometry2D addNewCustGeom();
        
        void unsetCustGeom();
        
        CTPresetGeometry2D getPrstGeom();
        
        boolean isSetPrstGeom();
        
        void setPrstGeom(final CTPresetGeometry2D p0);
        
        CTPresetGeometry2D addNewPrstGeom();
        
        void unsetPrstGeom();
    }
}
