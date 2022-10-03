package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.ImageUtil;
import javax.imageio.metadata.IIOMetadataNode;
import com.sun.imageio.plugins.common.I18N;
import org.w3c.dom.Node;
import javax.imageio.metadata.IIOMetadata;

public class WBMPMetadata extends IIOMetadata
{
    static final String nativeMetadataFormatName = "javax_imageio_wbmp_1.0";
    public int wbmpType;
    public int width;
    public int height;
    
    public WBMPMetadata() {
        super(true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", null, null);
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public Node getAsTree(final String s) {
        if (s.equals("javax_imageio_wbmp_1.0")) {
            return this.getNativeTree();
        }
        if (s.equals("javax_imageio_1.0")) {
            return this.getStandardTree();
        }
        throw new IllegalArgumentException(I18N.getString("WBMPMetadata0"));
    }
    
    private Node getNativeTree() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("javax_imageio_wbmp_1.0");
        this.addChildNode(iioMetadataNode, "WBMPType", new Integer(this.wbmpType));
        this.addChildNode(iioMetadataNode, "Width", new Integer(this.width));
        this.addChildNode(iioMetadataNode, "Height", new Integer(this.height));
        return iioMetadataNode;
    }
    
    @Override
    public void setFromTree(final String s, final Node node) {
        throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
    }
    
    @Override
    public void mergeTree(final String s, final Node node) {
        throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
    }
    
    @Override
    public void reset() {
        throw new IllegalStateException(I18N.getString("WBMPMetadata1"));
    }
    
    private IIOMetadataNode addChildNode(final IIOMetadataNode iioMetadataNode, final String s, final Object userObject) {
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode(s);
        if (userObject != null) {
            iioMetadataNode2.setUserObject(userObject);
            iioMetadataNode2.setNodeValue(ImageUtil.convertObjectToString(userObject));
        }
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode2;
    }
    
    @Override
    protected IIOMetadataNode getStandardChromaNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Chroma");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("BlackIsZero");
        iioMetadataNode2.setAttribute("value", "TRUE");
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
    }
    
    @Override
    protected IIOMetadataNode getStandardDimensionNode() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("Dimension");
        final IIOMetadataNode iioMetadataNode2 = new IIOMetadataNode("ImageOrientation");
        iioMetadataNode2.setAttribute("value", "Normal");
        iioMetadataNode.appendChild(iioMetadataNode2);
        return iioMetadataNode;
    }
}
