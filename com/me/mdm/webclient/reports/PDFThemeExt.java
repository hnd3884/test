package com.me.mdm.webclient.reports;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.awt.Color;
import com.lowagie.text.Font;
import com.adventnet.client.view.pdf.PDFTheme;

public interface PDFThemeExt extends PDFTheme
{
    public static final String SERVER_HOME = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
    
    Font getFont0();
    
    Font getFont1();
    
    Font getFont2();
    
    Font getFont3();
    
    Font getFont4();
    
    Font getLabelTxtFont();
    
    Font getLabelHeaderFont();
    
    Color getthemeColor();
    
    Color getBgColor();
    
    Color getBorderColor();
    
    Color getBorderLightColor();
    
    Color getLabelTxtColor();
    
    Color getLabelBgColor();
    
    Color getTitleBgColor();
    
    Color getInnerTitleBgColor();
    
    String getThemeDir();
}
