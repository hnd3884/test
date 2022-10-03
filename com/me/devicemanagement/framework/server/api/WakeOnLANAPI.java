package com.me.devicemanagement.framework.server.api;

import java.util.HashSet;
import java.util.Properties;
import java.util.ArrayList;
import java.util.HashMap;

public interface WakeOnLANAPI
{
    HashMap<String, String> triggerWOLFromDCAgent(final HashMap<String, ArrayList<Properties>> p0);
    
    HashMap<String, String> triggerWOLFromDCAgent(final HashSet<String> p0);
    
    HashMap<String, String> getDCAgentDetails();
}
