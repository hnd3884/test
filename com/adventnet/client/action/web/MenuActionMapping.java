package com.adventnet.client.action.web;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import org.apache.struts.action.ActionForward;
import com.adventnet.persistence.DataObject;
import org.apache.struts.action.ActionMapping;

public class MenuActionMapping extends ActionMapping
{
    private DataObject menuItemDO;
    
    public ActionForward findForward(final String name) {
        try {
            if (this.menuItemDO.containsTable("Forward")) {
                final Iterator forwardRows = this.menuItemDO.getRows("Forward");
                while (forwardRows.hasNext()) {
                    final Row forwardRow = forwardRows.next();
                    final String forwardName = (String)forwardRow.get("NAME");
                    if (forwardName.equals(name)) {
                        final ActionForward forward = new ActionForward();
                        forward.setName(forwardName);
                        forward.setPath((String)forwardRow.get("PATH"));
                        final Boolean redirect = (Boolean)forwardRow.get("REDIRECT");
                        if (redirect != null) {
                            forward.setRedirect((boolean)redirect);
                        }
                        return forward;
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return super.findForward(name);
    }
    
    public void setDataObject(final DataObject menuItemDO) {
        this.menuItemDO = menuItemDO;
    }
}
