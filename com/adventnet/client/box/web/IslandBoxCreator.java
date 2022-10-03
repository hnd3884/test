package com.adventnet.client.box.web;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.iam.xss.IAMEncoder;

public class IslandBoxCreator extends DefaultBoxCreator
{
    @Override
    public String getHtmlForBoxPrefix() {
        final StringBuffer boxPrefix = new StringBuffer();
        boxPrefix.append("<TABLE cellspacing='0' class='").append(IAMEncoder.encodeHTMLAttribute(this.boxConfig)).append("' id='").append(IAMEncoder.encodeHTMLAttribute(this.boxId)).append("'>");
        boxPrefix.append("<TR><TD class='boxTL'></TD>");
        boxPrefix.append("<TD class='boxHeader'>");
        try {
            final Criteria criteria = new Criteria(new Column("ACClientProps", "PARAMNAME"), (Object)"BOX_HEADER_LINK", 0);
            final DataObject dataObject = DataAccess.get("ACClientProps", criteria);
            final Row row = dataObject.getRow("ACClientProps");
            if (!dataObject.isEmpty() && row != null && row.get("PARAMVALUE").toString().equals("false")) {
                boxPrefix.append(IAMEncoder.encodeHTML(this.title));
            }
            else {
                boxPrefix.append("<div class='boxTitleDiv' onClick=\"javascript:").append(this.getActionString()).append("\">").append(IAMEncoder.encodeHTML(this.title)).append("</div>");
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        boxPrefix.append("</TD><TD class='boxCtrlButtonPane' nowrap>");
        boxPrefix.append(this.toolBarSnippet);
        boxPrefix.append("<input type='button' class='").append(this.getMinimizeBtnClass()).append("' id = '").append(this.getMinimizeImgId()).append("' onClick = \"").append(this.getActionString()).append("\" />");
        boxPrefix.append("<input type='button' class='").append(this.getMaximizeBtnClass()).append("' id = '").append(this.getMaximizeImgId()).append("' onClick = \"").append(this.getActionString()).append("\"/>");
        boxPrefix.append("</TD><TD class='boxTR'>&nbsp;</TD></TR>");
        boxPrefix.append("<TR>");
        boxPrefix.append("<TD class='boxML'>&nbsp;</TD><TD colspan='2' id='").append(IAMEncoder.encodeHTMLAttribute(this.getBoxMaxContentId())).append("' class='").append(this.getBoxMaxContentClass()).append("'>");
        return boxPrefix.toString();
    }
    
    @Override
    public String getHtmlForBoxSuffix() {
        final StringBuffer boxSuffix = new StringBuffer();
        boxSuffix.append("</TD><TD class='boxMR'>&nbsp;</TD></TR><TR><TD class='boxBL'>&nbsp;</TD><TD colspan='2' class='boxBC'>&nbsp;</TD><TD class='boxBR'>&nbsp;</TD></TR></TABLE>");
        return boxSuffix.toString();
    }
}
