package com.me.mdm.agent.handlers.windows;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.ArrayList;
import java.util.List;

public class Windows11CommandEvaluator implements CommandApplicableInterface
{
    @Override
    public List getRemovalList() {
        final List<Item> list = new ArrayList<Item>();
        list.add(this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/System/AllowTelemetry"));
        return list;
    }
    
    private Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
}
