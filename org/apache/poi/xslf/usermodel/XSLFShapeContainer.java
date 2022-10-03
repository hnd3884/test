package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.AutoShape;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.ShapeContainer;

public interface XSLFShapeContainer extends ShapeContainer<XSLFShape, XSLFTextParagraph>
{
    XSLFAutoShape createAutoShape();
    
    XSLFFreeformShape createFreeform();
    
    XSLFTextBox createTextBox();
    
    XSLFConnectorShape createConnector();
    
    XSLFGroupShape createGroup();
    
    XSLFPictureShape createPicture(final PictureData p0);
    
    void clear();
}
