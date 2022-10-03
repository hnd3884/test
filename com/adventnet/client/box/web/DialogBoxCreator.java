package com.adventnet.client.box.web;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;

public class DialogBoxCreator extends IslandBoxCreator
{
    @Override
    public String getHtmlForBoxPrefix() {
        final StringBuffer boxPrefix = new StringBuffer();
        boxPrefix.append("<TABLE cellspacing='0' cellpadding='0' class='").append(this.boxConfig).append("' id='").append(this.boxId).append("'>");
        boxPrefix.append("<TR><TD class='boxTL'></TD>");
        boxPrefix.append("<TD class='boxHeader drag' onMouseDown='captureDialog(event)'>");
        try {
            final Criteria criteria = new Criteria(new Column("ACClientProps", "PARAMNAME"), (Object)"BOX_HEADER_LINK", 0);
            final DataObject dataObject = DataAccess.get("ACClientProps", criteria);
            final Row row = dataObject.getRow("ACClientProps");
            if (!dataObject.isEmpty() && row != null && row.get("PARAMVALUE").toString().equals("false")) {
                boxPrefix.append(this.title);
            }
            else {
                boxPrefix.append("<div class='boxTitleDiv' onClick=\"").append(this.getActionString()).append("\">").append(this.title).append("</div>");
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
        boxPrefix.append("<input type='button' class='closeButton' onClick = 'closeDialog()'/>");
        boxPrefix.append("</TD><TD class='boxTR'>&nbsp;</TD></TR>");
        boxPrefix.append("<TR>");
        boxPrefix.append("<TD class='boxML'>&nbsp;</TD><TD colspan='2' id='").append(this.getBoxMaxContentId()).append("' class='").append(this.getBoxMaxContentClass()).append("'>");
        return boxPrefix.toString();
    }
}
