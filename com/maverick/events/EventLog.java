package com.maverick.events;

public final class EventLog
{
    public static void LogEvent(final Object o, final String s) {
        EventServiceImplementation.getInstance().fireEvent(new Event(o, 110, true).addAttribute("LOG_MESSAGE", s));
    }
    
    public static void LogErrorEvent(final Object o, final String s) {
        EventServiceImplementation.getInstance().fireEvent(new Event(o, 113, true).addAttribute("LOG_MESSAGE", s));
    }
    
    public static void LogDebugEvent(final Object o, final String s) {
        EventServiceImplementation.getInstance().fireEvent(new Event(o, 111, true).addAttribute("LOG_MESSAGE", s));
    }
    
    public static void LogEvent(final Object o, final String s, final Throwable t) {
        EventServiceImplementation.getInstance().fireEvent(new Event(o, 112, true).addAttribute("LOG_MESSAGE", s).addAttribute("THROWABLE", t));
    }
}
