package com.adventnet.client.components.table.web;

import java.util.HashMap;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.components.web.TransformerContext;

public class AddRemoveTransformer extends DefaultTransformer
{
    @Override
    public void renderCell(final TransformerContext tableContext) throws Exception {
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String tableName = (String)tableContext.getAssociatedPropertyValue("TableName");
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<A href=javascript:addTableToList('" + IAMEncoder.encodeJavaScript(tableName) + "') id='" + IAMEncoder.encodeHTMLAttribute(tableName) + "_AE' class='addLink' tablename='" + IAMEncoder.encodeHTMLAttribute(tableName) + "'>");
        buffer.append("Add</a>");
        buffer.append("<Span id='" + IAMEncoder.encodeHTMLAttribute(tableName) + "_AD' style='color:#000000;'>Add</Span>&nbsp;&nbsp;&nbsp;");
        buffer.append("<A href=javascript:removeTableFromList('" + IAMEncoder.encodeJavaScript(tableName) + "') id='" + IAMEncoder.encodeHTMLAttribute(tableName) + "_RE' class='removeLink' tablename='" + IAMEncoder.encodeHTMLAttribute(tableName) + "' >");
        buffer.append("Remove</a>");
        buffer.append("<Span id='" + IAMEncoder.encodeHTMLAttribute(tableName) + "_RD' style='color:#000000;'>Remove</Span>");
        columnProperties.put("VALUE", buffer.toString());
    }
}
