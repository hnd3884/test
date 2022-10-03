package com.me.mdm.agent.handlers.windows;

import com.me.mdm.framework.syncml.core.data.Location;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;

public class LTSBCommandEvaluator implements CommandApplicableInterface
{
    @Override
    public List<Item> getRemovalList() {
        final List<Item> list = new ArrayList<Item>();
        list.add(this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/Browser/ClearBrowsingDataOnExit"));
        list.add(this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/Browser/AllowFlashClickToRun"));
        list.add(this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/Browser/AllowAddressBarDropdown"));
        return list;
    }
    
    private Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
}
