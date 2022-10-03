package ar.com.fernandospr.wns.model.builders;

import ar.com.fernandospr.wns.model.WnsBinding;
import ar.com.fernandospr.wns.model.WnsVisual;
import ar.com.fernandospr.wns.model.WnsTile;

public class WnsTileBuilder extends WnsAbstractBuilder<WnsTileBuilder>
{
    private WnsTile tile;
    
    public WnsTileBuilder() {
        this.tile = new WnsTile();
    }
    
    public WnsTileBuilder getThis() {
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
    
    public WnsTileBuilder visualContentId(final String contentId) {
        this.getVisual().contentId = contentId;
        return this;
    }
    
    public WnsTileBuilder bindigContentId(final String contentId) {
        this.getBinding().contentId = contentId;
        return this;
    }
    
    public WnsTileBuilder bindingTemplateTileSquareBlock(final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquareBlock").setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileBuilder bindingTemplateTileSquareText01(final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquareText01").setBindingTextFields(textField1, textField2, textField3, textField4);
    }
    
    public WnsTileBuilder bindingTemplateTileSquareText02(final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquareText02").setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileBuilder bindingTemplateTileSquareText03(final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquareText03").setBindingTextFields(textField1, textField2, textField3, textField4);
    }
    
    public WnsTileBuilder bindingTemplateTileSquareText04(final String textField1) {
        return this.bindingTemplate("TileSquareText04").setBindingTextFields(textField1);
    }
    
    public WnsTileBuilder bindingTemplateTileSquareImage(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileSquareImage").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileSquarePeekImageAndText01(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquarePeekImageAndText01").setBindingTextFields(textField1, textField2, textField3, textField4).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileSquarePeekImageAndText02(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquarePeekImageAndText02").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileSquarePeekImageAndText03(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquarePeekImageAndText03").setBindingTextFields(textField1, textField2, textField3, textField4).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileSquarePeekImageAndText04(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileSquarePeekImageAndText04").setBindingImages(imgSrc1).setBindingTextFields(textField1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWideText01").setBindingTextFields(textField1, textField2, textField3, textField4, textField5);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText02(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileWideText02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText03(final String textField1) {
        return this.bindingTemplate("TileWideText03").setBindingTextFields(textField1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText04(final String textField1) {
        return this.bindingTemplate("TileWideText04").setBindingTextFields(textField1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText05(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWideText05").setBindingTextFields(textField1, textField2, textField3, textField4, textField5);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText06(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10) {
        return this.bindingTemplate("TileWideText06").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText07(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileWideText07").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText08(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10) {
        return this.bindingTemplate("TileWideText08").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText09(final String textField1, final String textField2) {
        return this.bindingTemplate("TileWideText09").setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText10(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileWideText10").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileBuilder bindingTemplateTileWideText11(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10) {
        return this.bindingTemplate("TileWideText11").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10);
    }
    
    public WnsTileBuilder bindingTemplateTileWideImage(final String imgSrc1) {
        return this.bindingTemplate("TileWideImage").setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideImageCollection(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5) {
        return this.bindingTemplate("TileWideImageCollection").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5);
    }
    
    public WnsTileBuilder bindingTemplateTileWideImageAndText01(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWideImageAndText01").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideImageAndText02(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWideImageAndText02").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideBlockAndText01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6) {
        return this.bindingTemplate("TileWideBlockAndText01").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6);
    }
    
    public WnsTileBuilder bindingTemplateTileWideBlockAndText02(final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("TileWideBlockAndText02").setBindingTextFields(textField1, textField2, textField3);
    }
    
    public WnsTileBuilder bindingTemplateTileWideSmallImageAndText01(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWideSmallImageAndText01").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideSmallImageAndText02(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWideSmallImageAndText02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideSmallImageAndText03(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWideSmallImageAndText03").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideSmallImageAndText04(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWideSmallImageAndText04").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWideSmallImageAndText05(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWideSmallImageAndText05").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageCollection01(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWidePeekImageCollection01").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageCollection02(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWidePeekImageCollection02").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1, textField2, textField3, textField4, textField5);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageCollection03(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1) {
        return this.bindingTemplate("TileWidePeekImageCollection03").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageCollection04(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1) {
        return this.bindingTemplate("TileWidePeekImageCollection04").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageCollection05(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String imgSrc6, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWidePeekImageCollection05").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5, imgSrc6).setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageCollection06(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String imgSrc6, final String textField1) {
        return this.bindingTemplate("TileWidePeekImageCollection06").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5, imgSrc6).setBindingTextFields(textField1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageAndText01(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWidePeekImageAndText01").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImageAndText02(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWidePeekImageAndText02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImage01(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWidePeekImage01").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImage02(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWidePeekImage02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImage03(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWidePeekImage03").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImage04(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWidePeekImage04").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImage05(final String imgSrc1, final String imgSrc2, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWidePeekImage05").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1, imgSrc2);
    }
    
    public WnsTileBuilder bindingTemplateTileWidePeekImage06(final String imgSrc1, final String imgSrc2, final String textField1) {
        return this.bindingTemplate("TileWidePeekImage06").setBindingTextFields(textField1).setBindingImages(imgSrc1, imgSrc2);
    }
    
    public WnsTile build() {
        return this.tile;
    }
}
