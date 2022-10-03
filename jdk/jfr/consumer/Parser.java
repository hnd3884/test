package jdk.jfr.consumer;

import java.io.IOException;
import jdk.jfr.internal.consumer.RecordingInput;

abstract class Parser
{
    abstract Object parse(final RecordingInput p0) throws IOException;
}
