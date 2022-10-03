package ar.com.fernandospr.wns.model.builders;

import ar.com.fernandospr.wns.model.WnsBinding;
import ar.com.fernandospr.wns.model.WnsVisual;
import ar.com.fernandospr.wns.model.WnsTile;

public class WnsTileV3Builder extends WnsAbstractBuilder<WnsTileV3Builder>
{
    private WnsTile tile;
    
    public WnsTileV3Builder() {
        this.tile = new WnsTile();
    }
    
    public WnsTileV3Builder getThis() {
        return this;
    }
    
    @Override
    protected WnsVisual getVisual() {
        if (this.tile.visual == null) {
            this.tile.visual = new WnsVisual();
        }
        return this.tile.visual;
    }
    
    @Override
    protected WnsBinding getBinding() {
        if (this.getVisual().binding == null) {
            this.getVisual().binding = new WnsBinding();
        }
        return this.tile.visual.binding;
    }
    
    public WnsTileV3Builder visualContentId(final String contentId) {
        this.getVisual().contentId = contentId;
        return this;
    }
    
    public WnsTileV3Builder bindigContentId(final String contentId) {
        this.getBinding().contentId = contentId;
        return this;
    }
    
    @Override
    protected WnsTileV3Builder bindingTemplate(final String template) {
        return super.bindingTemplate(template).visualVersion(3);
    }
    
    public WnsTileV3Builder bindingTemplateTileSquare71x71Image(final String imgSrc) {
        return this.bindingTemplate("TileSquare71x71Image").setBindingImages(imgSrc);
    }
    
    public WnsTileV3Builder bindingTemplateTileSquare71x71IconWithBadge(final String imgSrc) {
        return this.bindingTemplate("TileSquare71x71IconWithBadge").setBindingImages(imgSrc);
    }
    
    public WnsTileV3Builder bindingTemplateTileSquare150x150IconWithBadge(final String imgSrc) {
        return this.bindingTemplate("TileSquare150x150IconWithBadge").setBindingImages(imgSrc);
    }
    
    public WnsTileV3Builder bindingTemplateTileWide310x150IconWithBadgeAndText(final String imgSrc, final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("TileWide310x150IconWithBadgeAndText").setBindingImages(imgSrc).setBindingTextFields(textField1, textField2, textField3);
    }
    
    public WnsTile build() {
        return this.tile;
    }
}
