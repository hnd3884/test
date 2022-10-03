package jdk.jfr.events;

import jdk.jfr.StackTrace;
import jdk.jfr.Enabled;
import jdk.jfr.Registered;
import jdk.jfr.Event;

@Registered(false)
@Enabled(false)
@StackTrace(false)
abstract class AbstractJDKEvent extends Event
{
}
