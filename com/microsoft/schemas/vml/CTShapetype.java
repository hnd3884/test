package com.microsoft.schemas.vml;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import com.microsoft.schemas.office.office.STTrueFalseBlank;
import com.microsoft.schemas.office.office.STBWMode;
import com.microsoft.schemas.office.office.STConnectorType;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.office.office.STHrAlign;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlString;
import com.microsoft.schemas.office.office.CTComplex;
import com.microsoft.schemas.office.powerpoint.CTRel;
import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.word.CTBorder;
import com.microsoft.schemas.office.word.CTAnchorLock;
import com.microsoft.schemas.office.word.CTWrap;
import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.office.office.CTClipPath;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.CTCallout;
import com.microsoft.schemas.office.office.CTExtrusion;
import com.microsoft.schemas.office.office.CTSkew;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTShapetype extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShapetype.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshapetype5c6ftype");
    
    List<CTPath> getPathList();
    
    @Deprecated
    CTPath[] getPathArray();
    
    CTPath getPathArray(final int p0);
    
    int sizeOfPathArray();
    
    void setPathArray(final CTPath[] p0);
    
    void setPathArray(final int p0, final CTPath p1);
    
    CTPath insertNewPath(final int p0);
    
    CTPath addNewPath();
    
    void removePath(final int p0);
    
    List<CTFormulas> getFormulasList();
    
    @Deprecated
    CTFormulas[] getFormulasArray();
    
    CTFormulas getFormulasArray(final int p0);
    
    int sizeOfFormulasArray();
    
    void setFormulasArray(final CTFormulas[] p0);
    
    void setFormulasArray(final int p0, final CTFormulas p1);
    
    CTFormulas insertNewFormulas(final int p0);
    
    CTFormulas addNewFormulas();
    
    void removeFormulas(final int p0);
    
    List<CTHandles> getHandlesList();
    
    @Deprecated
    CTHandles[] getHandlesArray();
    
    CTHandles getHandlesArray(final int p0);
    
    int sizeOfHandlesArray();
    
    void setHandlesArray(final CTHandles[] p0);
    
    void setHandlesArray(final int p0, final CTHandles p1);
    
    CTHandles insertNewHandles(final int p0);
    
    CTHandles addNewHandles();
    
    void removeHandles(final int p0);
    
    List<CTFill> getFillList();
    
    @Deprecated
    CTFill[] getFillArray();
    
    CTFill getFillArray(final int p0);
    
    int sizeOfFillArray();
    
    void setFillArray(final CTFill[] p0);
    
    void setFillArray(final int p0, final CTFill p1);
    
    CTFill insertNewFill(final int p0);
    
    CTFill addNewFill();
    
    void removeFill(final int p0);
    
    List<CTStroke> getStrokeList();
    
    @Deprecated
    CTStroke[] getStrokeArray();
    
    CTStroke getStrokeArray(final int p0);
    
    int sizeOfStrokeArray();
    
    void setStrokeArray(final CTStroke[] p0);
    
    void setStrokeArray(final int p0, final CTStroke p1);
    
    CTStroke insertNewStroke(final int p0);
    
    CTStroke addNewStroke();
    
    void removeStroke(final int p0);
    
    List<CTShadow> getShadowList();
    
    @Deprecated
    CTShadow[] getShadowArray();
    
    CTShadow getShadowArray(final int p0);
    
    int sizeOfShadowArray();
    
    void setShadowArray(final CTShadow[] p0);
    
    void setShadowArray(final int p0, final CTShadow p1);
    
    CTShadow insertNewShadow(final int p0);
    
    CTShadow addNewShadow();
    
    void removeShadow(final int p0);
    
    List<CTTextbox> getTextboxList();
    
    @Deprecated
    CTTextbox[] getTextboxArray();
    
    CTTextbox getTextboxArray(final int p0);
    
    int sizeOfTextboxArray();
    
    void setTextboxArray(final CTTextbox[] p0);
    
    void setTextboxArray(final int p0, final CTTextbox p1);
    
    CTTextbox insertNewTextbox(final int p0);
    
    CTTextbox addNewTextbox();
    
    void removeTextbox(final int p0);
    
    List<CTTextPath> getTextpathList();
    
    @Deprecated
    CTTextPath[] getTextpathArray();
    
    CTTextPath getTextpathArray(final int p0);
    
    int sizeOfTextpathArray();
    
    void setTextpathArray(final CTTextPath[] p0);
    
    void setTextpathArray(final int p0, final CTTextPath p1);
    
    CTTextPath insertNewTextpath(final int p0);
    
    CTTextPath addNewTextpath();
    
    void removeTextpath(final int p0);
    
    List<CTImageData> getImagedataList();
    
    @Deprecated
    CTImageData[] getImagedataArray();
    
    CTImageData getImagedataArray(final int p0);
    
    int sizeOfImagedataArray();
    
    void setImagedataArray(final CTImageData[] p0);
    
    void setImagedataArray(final int p0, final CTImageData p1);
    
    CTImageData insertNewImagedata(final int p0);
    
    CTImageData addNewImagedata();
    
    void removeImagedata(final int p0);
    
    List<CTSkew> getSkewList();
    
    @Deprecated
    CTSkew[] getSkewArray();
    
    CTSkew getSkewArray(final int p0);
    
    int sizeOfSkewArray();
    
    void setSkewArray(final CTSkew[] p0);
    
    void setSkewArray(final int p0, final CTSkew p1);
    
    CTSkew insertNewSkew(final int p0);
    
    CTSkew addNewSkew();
    
    void removeSkew(final int p0);
    
    List<CTExtrusion> getExtrusionList();
    
    @Deprecated
    CTExtrusion[] getExtrusionArray();
    
    CTExtrusion getExtrusionArray(final int p0);
    
    int sizeOfExtrusionArray();
    
    void setExtrusionArray(final CTExtrusion[] p0);
    
    void setExtrusionArray(final int p0, final CTExtrusion p1);
    
    CTExtrusion insertNewExtrusion(final int p0);
    
    CTExtrusion addNewExtrusion();
    
    void removeExtrusion(final int p0);
    
    List<CTCallout> getCalloutList();
    
    @Deprecated
    CTCallout[] getCalloutArray();
    
    CTCallout getCalloutArray(final int p0);
    
    int sizeOfCalloutArray();
    
    void setCalloutArray(final CTCallout[] p0);
    
    void setCalloutArray(final int p0, final CTCallout p1);
    
    CTCallout insertNewCallout(final int p0);
    
    CTCallout addNewCallout();
    
    void removeCallout(final int p0);
    
    List<CTLock> getLockList();
    
    @Deprecated
    CTLock[] getLockArray();
    
    CTLock getLockArray(final int p0);
    
    int sizeOfLockArray();
    
    void setLockArray(final CTLock[] p0);
    
    void setLockArray(final int p0, final CTLock p1);
    
    CTLock insertNewLock(final int p0);
    
    CTLock addNewLock();
    
    void removeLock(final int p0);
    
    List<CTClipPath> getClippathList();
    
    @Deprecated
    CTClipPath[] getClippathArray();
    
    CTClipPath getClippathArray(final int p0);
    
    int sizeOfClippathArray();
    
    void setClippathArray(final CTClipPath[] p0);
    
    void setClippathArray(final int p0, final CTClipPath p1);
    
    CTClipPath insertNewClippath(final int p0);
    
    CTClipPath addNewClippath();
    
    void removeClippath(final int p0);
    
    List<CTSignatureLine> getSignaturelineList();
    
    @Deprecated
    CTSignatureLine[] getSignaturelineArray();
    
    CTSignatureLine getSignaturelineArray(final int p0);
    
    int sizeOfSignaturelineArray();
    
    void setSignaturelineArray(final CTSignatureLine[] p0);
    
    void setSignaturelineArray(final int p0, final CTSignatureLine p1);
    
    CTSignatureLine insertNewSignatureline(final int p0);
    
    CTSignatureLine addNewSignatureline();
    
    void removeSignatureline(final int p0);
    
    List<CTWrap> getWrapList();
    
    @Deprecated
    CTWrap[] getWrapArray();
    
    CTWrap getWrapArray(final int p0);
    
    int sizeOfWrapArray();
    
    void setWrapArray(final CTWrap[] p0);
    
    void setWrapArray(final int p0, final CTWrap p1);
    
    CTWrap insertNewWrap(final int p0);
    
    CTWrap addNewWrap();
    
    void removeWrap(final int p0);
    
    List<CTAnchorLock> getAnchorlockList();
    
    @Deprecated
    CTAnchorLock[] getAnchorlockArray();
    
    CTAnchorLock getAnchorlockArray(final int p0);
    
    int sizeOfAnchorlockArray();
    
    void setAnchorlockArray(final CTAnchorLock[] p0);
    
    void setAnchorlockArray(final int p0, final CTAnchorLock p1);
    
    CTAnchorLock insertNewAnchorlock(final int p0);
    
    CTAnchorLock addNewAnchorlock();
    
    void removeAnchorlock(final int p0);
    
    List<CTBorder> getBordertopList();
    
    @Deprecated
    CTBorder[] getBordertopArray();
    
    CTBorder getBordertopArray(final int p0);
    
    int sizeOfBordertopArray();
    
    void setBordertopArray(final CTBorder[] p0);
    
    void setBordertopArray(final int p0, final CTBorder p1);
    
    CTBorder insertNewBordertop(final int p0);
    
    CTBorder addNewBordertop();
    
    void removeBordertop(final int p0);
    
    List<CTBorder> getBorderbottomList();
    
    @Deprecated
    CTBorder[] getBorderbottomArray();
    
    CTBorder getBorderbottomArray(final int p0);
    
    int sizeOfBorderbottomArray();
    
    void setBorderbottomArray(final CTBorder[] p0);
    
    void setBorderbottomArray(final int p0, final CTBorder p1);
    
    CTBorder insertNewBorderbottom(final int p0);
    
    CTBorder addNewBorderbottom();
    
    void removeBorderbottom(final int p0);
    
    List<CTBorder> getBorderleftList();
    
    @Deprecated
    CTBorder[] getBorderleftArray();
    
    CTBorder getBorderleftArray(final int p0);
    
    int sizeOfBorderleftArray();
    
    void setBorderleftArray(final CTBorder[] p0);
    
    void setBorderleftArray(final int p0, final CTBorder p1);
    
    CTBorder insertNewBorderleft(final int p0);
    
    CTBorder addNewBorderleft();
    
    void removeBorderleft(final int p0);
    
    List<CTBorder> getBorderrightList();
    
    @Deprecated
    CTBorder[] getBorderrightArray();
    
    CTBorder getBorderrightArray(final int p0);
    
    int sizeOfBorderrightArray();
    
    void setBorderrightArray(final CTBorder[] p0);
    
    void setBorderrightArray(final int p0, final CTBorder p1);
    
    CTBorder insertNewBorderright(final int p0);
    
    CTBorder addNewBorderright();
    
    void removeBorderright(final int p0);
    
    List<CTClientData> getClientDataList();
    
    @Deprecated
    CTClientData[] getClientDataArray();
    
    CTClientData getClientDataArray(final int p0);
    
    int sizeOfClientDataArray();
    
    void setClientDataArray(final CTClientData[] p0);
    
    void setClientDataArray(final int p0, final CTClientData p1);
    
    CTClientData insertNewClientData(final int p0);
    
    CTClientData addNewClientData();
    
    void removeClientData(final int p0);
    
    List<CTRel> getTextdataList();
    
    @Deprecated
    CTRel[] getTextdataArray();
    
    CTRel getTextdataArray(final int p0);
    
    int sizeOfTextdataArray();
    
    void setTextdataArray(final CTRel[] p0);
    
    void setTextdataArray(final int p0, final CTRel p1);
    
    CTRel insertNewTextdata(final int p0);
    
    CTRel addNewTextdata();
    
    void removeTextdata(final int p0);
    
    CTComplex getComplex();
    
    boolean isSetComplex();
    
    void setComplex(final CTComplex p0);
    
    CTComplex addNewComplex();
    
    void unsetComplex();
    
    String getId();
    
    XmlString xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlString p0);
    
    void unsetId();
    
    String getStyle();
    
    XmlString xgetStyle();
    
    boolean isSetStyle();
    
    void setStyle(final String p0);
    
    void xsetStyle(final XmlString p0);
    
    void unsetStyle();
    
    String getHref();
    
    XmlString xgetHref();
    
    boolean isSetHref();
    
    void setHref(final String p0);
    
    void xsetHref(final XmlString p0);
    
    void unsetHref();
    
    String getTarget();
    
    XmlString xgetTarget();
    
    boolean isSetTarget();
    
    void setTarget(final String p0);
    
    void xsetTarget(final XmlString p0);
    
    void unsetTarget();
    
    String getClass1();
    
    XmlString xgetClass1();
    
    boolean isSetClass1();
    
    void setClass1(final String p0);
    
    void xsetClass1(final XmlString p0);
    
    void unsetClass1();
    
    String getTitle();
    
    XmlString xgetTitle();
    
    boolean isSetTitle();
    
    void setTitle(final String p0);
    
    void xsetTitle(final XmlString p0);
    
    void unsetTitle();
    
    String getAlt();
    
    XmlString xgetAlt();
    
    boolean isSetAlt();
    
    void setAlt(final String p0);
    
    void xsetAlt(final XmlString p0);
    
    void unsetAlt();
    
    String getCoordsize();
    
    XmlString xgetCoordsize();
    
    boolean isSetCoordsize();
    
    void setCoordsize(final String p0);
    
    void xsetCoordsize(final XmlString p0);
    
    void unsetCoordsize();
    
    String getCoordorigin();
    
    XmlString xgetCoordorigin();
    
    boolean isSetCoordorigin();
    
    void setCoordorigin(final String p0);
    
    void xsetCoordorigin(final XmlString p0);
    
    void unsetCoordorigin();
    
    String getWrapcoords();
    
    XmlString xgetWrapcoords();
    
    boolean isSetWrapcoords();
    
    void setWrapcoords(final String p0);
    
    void xsetWrapcoords(final XmlString p0);
    
    void unsetWrapcoords();
    
    STTrueFalse.Enum getPrint();
    
    STTrueFalse xgetPrint();
    
    boolean isSetPrint();
    
    void setPrint(final STTrueFalse.Enum p0);
    
    void xsetPrint(final STTrueFalse p0);
    
    void unsetPrint();
    
    String getSpid();
    
    XmlString xgetSpid();
    
    boolean isSetSpid();
    
    void setSpid(final String p0);
    
    void xsetSpid(final XmlString p0);
    
    void unsetSpid();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getOned();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetOned();
    
    boolean isSetOned();
    
    void setOned(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetOned(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetOned();
    
    BigInteger getRegroupid();
    
    XmlInteger xgetRegroupid();
    
    boolean isSetRegroupid();
    
    void setRegroupid(final BigInteger p0);
    
    void xsetRegroupid(final XmlInteger p0);
    
    void unsetRegroupid();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getDoubleclicknotify();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetDoubleclicknotify();
    
    boolean isSetDoubleclicknotify();
    
    void setDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetDoubleclicknotify();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getButton();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetButton();
    
    boolean isSetButton();
    
    void setButton(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetButton(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetButton();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getUserhidden();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetUserhidden();
    
    boolean isSetUserhidden();
    
    void setUserhidden(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetUserhidden(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetUserhidden();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getBullet();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetBullet();
    
    boolean isSetBullet();
    
    void setBullet(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetBullet(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetBullet();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getHr();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetHr();
    
    boolean isSetHr();
    
    void setHr(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetHr(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetHr();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getHrstd();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetHrstd();
    
    boolean isSetHrstd();
    
    void setHrstd(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetHrstd(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetHrstd();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getHrnoshade();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetHrnoshade();
    
    boolean isSetHrnoshade();
    
    void setHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetHrnoshade();
    
    float getHrpct();
    
    XmlFloat xgetHrpct();
    
    boolean isSetHrpct();
    
    void setHrpct(final float p0);
    
    void xsetHrpct(final XmlFloat p0);
    
    void unsetHrpct();
    
    STHrAlign.Enum getHralign();
    
    STHrAlign xgetHralign();
    
    boolean isSetHralign();
    
    void setHralign(final STHrAlign.Enum p0);
    
    void xsetHralign(final STHrAlign p0);
    
    void unsetHralign();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowincell();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetAllowincell();
    
    boolean isSetAllowincell();
    
    void setAllowincell(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetAllowincell(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetAllowincell();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowoverlap();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetAllowoverlap();
    
    boolean isSetAllowoverlap();
    
    void setAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetAllowoverlap();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getUserdrawn();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetUserdrawn();
    
    boolean isSetUserdrawn();
    
    void setUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetUserdrawn();
    
    String getBordertopcolor();
    
    XmlString xgetBordertopcolor();
    
    boolean isSetBordertopcolor();
    
    void setBordertopcolor(final String p0);
    
    void xsetBordertopcolor(final XmlString p0);
    
    void unsetBordertopcolor();
    
    String getBorderleftcolor();
    
    XmlString xgetBorderleftcolor();
    
    boolean isSetBorderleftcolor();
    
    void setBorderleftcolor(final String p0);
    
    void xsetBorderleftcolor(final XmlString p0);
    
    void unsetBorderleftcolor();
    
    String getBorderbottomcolor();
    
    XmlString xgetBorderbottomcolor();
    
    boolean isSetBorderbottomcolor();
    
    void setBorderbottomcolor(final String p0);
    
    void xsetBorderbottomcolor(final XmlString p0);
    
    void unsetBorderbottomcolor();
    
    String getBorderrightcolor();
    
    XmlString xgetBorderrightcolor();
    
    boolean isSetBorderrightcolor();
    
    void setBorderrightcolor(final String p0);
    
    void xsetBorderrightcolor(final XmlString p0);
    
    void unsetBorderrightcolor();
    
    BigInteger getDgmlayout();
    
    XmlInteger xgetDgmlayout();
    
    boolean isSetDgmlayout();
    
    void setDgmlayout(final BigInteger p0);
    
    void xsetDgmlayout(final XmlInteger p0);
    
    void unsetDgmlayout();
    
    BigInteger getDgmnodekind();
    
    XmlInteger xgetDgmnodekind();
    
    boolean isSetDgmnodekind();
    
    void setDgmnodekind(final BigInteger p0);
    
    void xsetDgmnodekind(final XmlInteger p0);
    
    void unsetDgmnodekind();
    
    BigInteger getDgmlayoutmru();
    
    XmlInteger xgetDgmlayoutmru();
    
    boolean isSetDgmlayoutmru();
    
    void setDgmlayoutmru(final BigInteger p0);
    
    void xsetDgmlayoutmru(final XmlInteger p0);
    
    void unsetDgmlayoutmru();
    
    STInsetMode.Enum getInsetmode();
    
    STInsetMode xgetInsetmode();
    
    boolean isSetInsetmode();
    
    void setInsetmode(final STInsetMode.Enum p0);
    
    void xsetInsetmode(final STInsetMode p0);
    
    void unsetInsetmode();
    
    String getChromakey();
    
    STColorType xgetChromakey();
    
    boolean isSetChromakey();
    
    void setChromakey(final String p0);
    
    void xsetChromakey(final STColorType p0);
    
    void unsetChromakey();
    
    STTrueFalse.Enum getFilled();
    
    STTrueFalse xgetFilled();
    
    boolean isSetFilled();
    
    void setFilled(final STTrueFalse.Enum p0);
    
    void xsetFilled(final STTrueFalse p0);
    
    void unsetFilled();
    
    String getFillcolor();
    
    STColorType xgetFillcolor();
    
    boolean isSetFillcolor();
    
    void setFillcolor(final String p0);
    
    void xsetFillcolor(final STColorType p0);
    
    void unsetFillcolor();
    
    String getOpacity();
    
    XmlString xgetOpacity();
    
    boolean isSetOpacity();
    
    void setOpacity(final String p0);
    
    void xsetOpacity(final XmlString p0);
    
    void unsetOpacity();
    
    STTrueFalse.Enum getStroked();
    
    STTrueFalse xgetStroked();
    
    boolean isSetStroked();
    
    void setStroked(final STTrueFalse.Enum p0);
    
    void xsetStroked(final STTrueFalse p0);
    
    void unsetStroked();
    
    String getStrokecolor();
    
    STColorType xgetStrokecolor();
    
    boolean isSetStrokecolor();
    
    void setStrokecolor(final String p0);
    
    void xsetStrokecolor(final STColorType p0);
    
    void unsetStrokecolor();
    
    String getStrokeweight();
    
    XmlString xgetStrokeweight();
    
    boolean isSetStrokeweight();
    
    void setStrokeweight(final String p0);
    
    void xsetStrokeweight(final XmlString p0);
    
    void unsetStrokeweight();
    
    STTrueFalse.Enum getInsetpen();
    
    STTrueFalse xgetInsetpen();
    
    boolean isSetInsetpen();
    
    void setInsetpen(final STTrueFalse.Enum p0);
    
    void xsetInsetpen(final STTrueFalse p0);
    
    void unsetInsetpen();
    
    float getSpt();
    
    XmlFloat xgetSpt();
    
    boolean isSetSpt();
    
    void setSpt(final float p0);
    
    void xsetSpt(final XmlFloat p0);
    
    void unsetSpt();
    
    STConnectorType.Enum getConnectortype();
    
    STConnectorType xgetConnectortype();
    
    boolean isSetConnectortype();
    
    void setConnectortype(final STConnectorType.Enum p0);
    
    void xsetConnectortype(final STConnectorType p0);
    
    void unsetConnectortype();
    
    STBWMode.Enum getBwmode();
    
    STBWMode xgetBwmode();
    
    boolean isSetBwmode();
    
    void setBwmode(final STBWMode.Enum p0);
    
    void xsetBwmode(final STBWMode p0);
    
    void unsetBwmode();
    
    STBWMode.Enum getBwpure();
    
    STBWMode xgetBwpure();
    
    boolean isSetBwpure();
    
    void setBwpure(final STBWMode.Enum p0);
    
    void xsetBwpure(final STBWMode p0);
    
    void unsetBwpure();
    
    STBWMode.Enum getBwnormal();
    
    STBWMode xgetBwnormal();
    
    boolean isSetBwnormal();
    
    void setBwnormal(final STBWMode.Enum p0);
    
    void xsetBwnormal(final STBWMode p0);
    
    void unsetBwnormal();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getForcedash();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetForcedash();
    
    boolean isSetForcedash();
    
    void setForcedash(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetForcedash(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetForcedash();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getOleicon();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetOleicon();
    
    boolean isSetOleicon();
    
    void setOleicon(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetOleicon(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetOleicon();
    
    STTrueFalseBlank.Enum getOle();
    
    STTrueFalseBlank xgetOle();
    
    boolean isSetOle();
    
    void setOle(final STTrueFalseBlank.Enum p0);
    
    void xsetOle(final STTrueFalseBlank p0);
    
    void unsetOle();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getPreferrelative();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetPreferrelative();
    
    boolean isSetPreferrelative();
    
    void setPreferrelative(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetPreferrelative(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetPreferrelative();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getCliptowrap();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetCliptowrap();
    
    boolean isSetCliptowrap();
    
    void setCliptowrap(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetCliptowrap(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetCliptowrap();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getClip();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetClip();
    
    boolean isSetClip();
    
    void setClip(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetClip(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetClip();
    
    String getAdj();
    
    XmlString xgetAdj();
    
    boolean isSetAdj();
    
    void setAdj(final String p0);
    
    void xsetAdj(final XmlString p0);
    
    void unsetAdj();
    
    String getPath2();
    
    XmlString xgetPath2();
    
    boolean isSetPath2();
    
    void setPath2(final String p0);
    
    void xsetPath2(final XmlString p0);
    
    void unsetPath2();
    
    String getMaster();
    
    XmlString xgetMaster();
    
    boolean isSetMaster();
    
    void setMaster(final String p0);
    
    void xsetMaster(final XmlString p0);
    
    void unsetMaster();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShapetype.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShapetype newInstance() {
            return (CTShapetype)getTypeLoader().newInstance(CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype newInstance(final XmlOptions xmlOptions) {
            return (CTShapetype)getTypeLoader().newInstance(CTShapetype.type, xmlOptions);
        }
        
        public static CTShapetype parse(final String s) throws XmlException {
            return (CTShapetype)getTypeLoader().parse(s, CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapetype)getTypeLoader().parse(s, CTShapetype.type, xmlOptions);
        }
        
        public static CTShapetype parse(final File file) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(file, CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(file, CTShapetype.type, xmlOptions);
        }
        
        public static CTShapetype parse(final URL url) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(url, CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(url, CTShapetype.type, xmlOptions);
        }
        
        public static CTShapetype parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(inputStream, CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(inputStream, CTShapetype.type, xmlOptions);
        }
        
        public static CTShapetype parse(final Reader reader) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(reader, CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapetype)getTypeLoader().parse(reader, CTShapetype.type, xmlOptions);
        }
        
        public static CTShapetype parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShapetype)getTypeLoader().parse(xmlStreamReader, CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapetype)getTypeLoader().parse(xmlStreamReader, CTShapetype.type, xmlOptions);
        }
        
        public static CTShapetype parse(final Node node) throws XmlException {
            return (CTShapetype)getTypeLoader().parse(node, CTShapetype.type, (XmlOptions)null);
        }
        
        public static CTShapetype parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapetype)getTypeLoader().parse(node, CTShapetype.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShapetype parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShapetype)getTypeLoader().parse(xmlInputStream, CTShapetype.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShapetype parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShapetype)getTypeLoader().parse(xmlInputStream, CTShapetype.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapetype.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapetype.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
