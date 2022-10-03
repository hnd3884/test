package ar.com.fernandospr.wns.model.builders;

import ar.com.fernandospr.wns.model.WnsBinding;
import ar.com.fernandospr.wns.model.WnsVisual;
import ar.com.fernandospr.wns.model.WnsTile;

public class WnsTileV2Builder extends WnsAbstractBuilder<WnsTileV2Builder>
{
    private WnsTile tile;
    
    public WnsTileV2Builder() {
        this.tile = new WnsTile();
    }
    
    public WnsTileV2Builder getThis() {
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
    
    public WnsTileV2Builder visualContentId(final String contentId) {
        this.getVisual().contentId = contentId;
        return this;
    }
    
    public WnsTileV2Builder bindigContentId(final String contentId) {
        this.getBinding().contentId = contentId;
        return this;
    }
    
    @Override
    protected WnsTileV2Builder bindingTemplate(final String template) {
        return super.bindingTemplate(template).visualVersion(2);
    }
    
    private WnsTileV2Builder bindingTemplate(final String template, final String fallback) {
        this.getBinding().fallback = fallback;
        return this.bindingTemplate(template);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150Block(final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquare150x150Block", "TileSquareBlock").setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150Text01(final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquare150x150Text01", "TileSquareText01").setBindingTextFields(textField1, textField2, textField3, textField4);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150Text02(final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquare150x150Text02", "TileSquareText02").setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150Text03(final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquare150x150Text03", "TileSquareText03").setBindingTextFields(textField1, textField2, textField3, textField4);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150Text04(final String textField1) {
        return this.bindingTemplate("TileSquare150x150Text04", "TileSquareText04").setBindingTextFields(textField1);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150Image(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileSquare150x150Image", "TileSquareImage").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150PeekImageAndText01(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquare150x150PeekImageAndText01", "TileSquarePeekImageAndText01").setBindingTextFields(textField1, textField2, textField3, textField4).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150PeekImageAndText02(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquare150x150PeekImageAndText02", "TileSquarePeekImageAndText02").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150PeekImageAndText03(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquare150x150PeekImageAndText03", "TileSquarePeekImageAndText03").setBindingTextFields(textField1, textField2, textField3, textField4).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare150x150PeekImageAndText04(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileSquare150x150PeekImageAndText04", "TileSquarePeekImageAndText04").setBindingImages(imgSrc1).setBindingTextFields(textField1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWide310x150Text01", "TileWideText01").setBindingTextFields(textField1, textField2, textField3, textField4, textField5);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text02(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileWide310x150Text02", "TileWideText02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text03(final String textField1) {
        return this.bindingTemplate("TileWide310x150Text03", "TileWideText03").setBindingTextFields(textField1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text04(final String textField1) {
        return this.bindingTemplate("TileWide310x150Text04", "TileWideText04").setBindingTextFields(textField1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text05(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWide310x150Text05", "TileWideText05").setBindingTextFields(textField1, textField2, textField3, textField4, textField5);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10) {
        return this.bindingTemplate("TileWide310x150Text06", "TileWideText06").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text07(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileWide310x150Text07", "TileWideText07").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text08(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10) {
        return this.bindingTemplate("TileWide310x150Text08", "TileWideText08").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text09(final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150Text09", "TileWideText09").setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text10(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileWide310x150Text10", "TileWideText10").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Text11(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10) {
        return this.bindingTemplate("TileWide310x150Text11", "TileWideText11").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150Image(final String imgSrc1) {
        return this.bindingTemplate("TileWide310x150Image", "TileWideImage").setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150ImageCollection(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5) {
        return this.bindingTemplate("TileWide310x150ImageCollection", "TileWideImageCollection").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150ImageAndText01(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWide310x150ImageAndText01", "TileWideImageAndText01").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150ImageAndText02(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150ImageAndText02", "TileWideImageAndText02").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150BlockAndText01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6) {
        return this.bindingTemplate("TileWide310x150BlockAndText01", "TileWideBlockAndText01").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150BlockAndText02(final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("TileWide310x150BlockAndText02", "TileWideBlockAndText02").setBindingTextFields(textField1, textField2, textField3);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150SmallImageAndText01(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWide310x150SmallImageAndText01", "TileWideSmallImageAndText01").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150SmallImageAndText02(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWide310x150SmallImageAndText02", "TileWideSmallImageAndText02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150SmallImageAndText03(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWide310x150SmallImageAndText03", "TileWideSmallImageAndText03").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150SmallImageAndText04(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150SmallImageAndText04", "TileWideSmallImageAndText04").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150SmallImageAndText05(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150SmallImageAndText05", "TileWideSmallImageAndText05").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageCollection01(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150PeekImageCollection01", "TileWidePeekImageCollection01").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageCollection02(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWide310x150PeekImageCollection02", "TileWidePeekImageCollection02").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1, textField2, textField3, textField4, textField5);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageCollection03(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1) {
        return this.bindingTemplate("TileWide310x150PeekImageCollection03", "TileWidePeekImageCollection03").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageCollection04(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1) {
        return this.bindingTemplate("TileWide310x150PeekImageCollection04", "TileWidePeekImageCollection04").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageCollection05(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String imgSrc6, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150PeekImageCollection05", "TileWidePeekImageCollection05").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5, imgSrc6).setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageCollection06(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String imgSrc6, final String textField1) {
        return this.bindingTemplate("TileWide310x150PeekImageCollection06", "TileWidePeekImageCollection06").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5, imgSrc6).setBindingTextFields(textField1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageAndText01(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWide310x150PeekImageAndText01", "TileWidePeekImageAndText01").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImageAndText02(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWide310x150PeekImageAndText02", "TileWidePeekImageAndText02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImage01(final String imgSrc1, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150PeekImage01", "TileWidePeekImage01").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImage02(final String imgSrc1, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileWide310x150PeekImage02", "TileWidePeekImage02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImage03(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWide310x150PeekImage03", "TileWidePeekImage03").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImage04(final String imgSrc1, final String textField1) {
        return this.bindingTemplate("TileWide310x150PeekImage04", "TileWidePeekImage04").setBindingTextFields(textField1).setBindingImages(imgSrc1);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImage05(final String imgSrc1, final String imgSrc2, final String textField1, final String textField2) {
        return this.bindingTemplate("TileWide310x150PeekImage05", "TileWidePeekImage05").setBindingTextFields(textField1, textField2).setBindingImages(imgSrc1, imgSrc2);
    }
    
    public WnsTileV2Builder bindingTemplateTileWide310x150PeekImage06(final String imgSrc1, final String imgSrc2, final String textField1) {
        return this.bindingTemplate("TileWide310x150PeekImage06", "TileWidePeekImage06").setBindingTextFields(textField1).setBindingImages(imgSrc1, imgSrc2);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10) {
        return this.bindingTemplate("TileSquare310x310Text01").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text02(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10, final String textField11, final String textField12, final String textField13, final String textField14, final String textField15, final String textField16, final String textField17, final String textField18, final String textField19) {
        return this.bindingTemplate("TileSquare310x310Text02").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11, textField12, textField13, textField14, textField15, textField16, textField17, textField18, textField19);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text03(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10, final String textField11) {
        return this.bindingTemplate("TileSquare310x310Text03").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text04(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10, final String textField11, final String textField12, final String textField13, final String textField14, final String textField15, final String textField16, final String textField17, final String textField18, final String textField19, final String textField20) {
        return this.bindingTemplate("TileSquare310x310Text04").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11, textField12, textField13, textField14, textField15, textField16, textField17, textField18, textField19, textField20);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text05(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10, final String textField11, final String textField12, final String textField13, final String textField14, final String textField15, final String textField16, final String textField17, final String textField18, final String textField19) {
        return this.bindingTemplate("TileSquare310x310Text05").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11, textField12, textField13, textField14, textField15, textField16, textField17, textField18, textField19);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text06(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10, final String textField11, final String textField12, final String textField13, final String textField14, final String textField15, final String textField16, final String textField17, final String textField18, final String textField19, final String textField20, final String textField21, final String textField22) {
        return this.bindingTemplate("TileSquare310x310Text06").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11, textField12, textField13, textField14, textField15, textField16, textField17, textField18, textField19, textField20, textField21, textField22);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text07(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10, final String textField11, final String textField12, final String textField13, final String textField14, final String textField15, final String textField16, final String textField17, final String textField18, final String textField19) {
        return this.bindingTemplate("TileSquare310x310Text07").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11, textField12, textField13, textField14, textField15, textField16, textField17, textField18, textField19);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text08(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9, final String textField10, final String textField11, final String textField12, final String textField13, final String textField14, final String textField15, final String textField16, final String textField17, final String textField18, final String textField19, final String textField20, final String textField21, final String textField22) {
        return this.bindingTemplate("TileSquare310x310Text08").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11, textField12, textField13, textField14, textField15, textField16, textField17, textField18, textField19, textField20, textField21, textField22);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Text09(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5) {
        return this.bindingTemplate("TileSquare310x310Text09").setBindingTextFields(textField1, textField2, textField3, textField4, textField5);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310TextList01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileSquare310x310TextList01").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310TextList02(final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("TileSquare310x310TextList02").setBindingTextFields(textField1, textField2, textField3);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310TextList03(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6) {
        return this.bindingTemplate("TileSquare310x310TextList03").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310BlockAndText01(final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileSquare310x310BlockAndText01").setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310Image(final String imgSrc) {
        return this.bindingTemplate("TileSquare310x310Image").setBindingImages(imgSrc);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageCollection(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String imgSrc6) {
        return this.bindingTemplate("TileSquare310x310ImageCollection").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5, imgSrc6);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310BlockAndText02(final String imgSrc, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7) {
        return this.bindingTemplate("TileSquare310x310BlockAndText02").setBindingImages(imgSrc).setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageAndText01(final String imgSrc, final String textField) {
        return this.bindingTemplate("TileSquare310x310ImageAndText01").setBindingImages(imgSrc).setBindingTextFields(textField);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageAndText02(final String imgSrc, final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquare310x310ImageAndText02").setBindingImages(imgSrc).setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageAndTextOverlay01(final String imgSrc, final String textField) {
        return this.bindingTemplate("TileSquare310x310ImageAndTextOverlay01").setBindingImages(imgSrc).setBindingTextFields(textField);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageAndTextOverlay02(final String imgSrc, final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquare310x310ImageAndTextOverlay02").setBindingImages(imgSrc).setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageAndTextOverlay03(final String imgSrc, final String textField1, final String textField2, final String textField3, final String textField4) {
        return this.bindingTemplate("TileSquare310x310ImageAndTextOverlay03").setBindingImages(imgSrc).setBindingTextFields(textField1, textField2, textField3, textField4);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageCollectionAndText01(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField) {
        return this.bindingTemplate("TileSquare310x310ImageCollectionAndText01").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310ImageCollectionAndText02(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String imgSrc4, final String imgSrc5, final String textField1, final String textField2) {
        return this.bindingTemplate("TileSquare310x310ImageCollectionAndText02").setBindingImages(imgSrc1, imgSrc2, imgSrc3, imgSrc4, imgSrc5).setBindingTextFields(textField1, textField2);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310SmallImagesAndTextList01(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7, final String textField8, final String textField9) {
        return this.bindingTemplate("TileSquare310x310SmallImagesAndTextList01").setBindingImages(imgSrc1, imgSrc2, imgSrc3).setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310SmallImagesAndTextList02(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("TileSquare310x310SmallImagesAndTextList02").setBindingImages(imgSrc1, imgSrc2, imgSrc3).setBindingTextFields(textField1, textField2, textField3);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310SmallImagesAndTextList03(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6) {
        return this.bindingTemplate("TileSquare310x310SmallImagesAndTextList03").setBindingImages(imgSrc1, imgSrc2, imgSrc3).setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310SmallImagesAndTextList04(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6) {
        return this.bindingTemplate("TileSquare310x310SmallImagesAndTextList04").setBindingImages(imgSrc1, imgSrc2, imgSrc3).setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310SmallImagesAndTextList05(final String imgSrc1, final String imgSrc2, final String imgSrc3, final String textField1, final String textField2, final String textField3, final String textField4, final String textField5, final String textField6, final String textField7) {
        return this.bindingTemplate("TileSquare310x310SmallImagesAndTextList05").setBindingImages(imgSrc1, imgSrc2, imgSrc3).setBindingTextFields(textField1, textField2, textField3, textField4, textField5, textField6, textField7);
    }
    
    public WnsTileV2Builder bindingTemplateTileSquare310x310SmallImageAndText01(final String imgSrc, final String textField1, final String textField2, final String textField3) {
        return this.bindingTemplate("TileSquare310x310SmallImageAndText01").setBindingImages(imgSrc).setBindingTextFields(textField1, textField2, textField3);
    }
    
    public WnsTile build() {
        return this.tile;
    }
}
