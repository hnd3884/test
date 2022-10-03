package org.apache.axiom.util.stax;

public final class XMLEventUtils
{
    private XMLEventUtils() {
    }
    
    public static String getEventTypeString(final int event) {
        String state = null;
        switch (event) {
            case 1: {
                state = "START_ELEMENT";
                break;
            }
            case 7: {
                state = "START_DOCUMENT";
                break;
            }
            case 4: {
                state = "CHARACTERS";
                break;
            }
            case 12: {
                state = "CDATA";
                break;
            }
            case 2: {
                state = "END_ELEMENT";
                break;
            }
            case 8: {
                state = "END_DOCUMENT";
                break;
            }
            case 6: {
                state = "SPACE";
                break;
            }
            case 5: {
                state = "COMMENT";
                break;
            }
            case 11: {
                state = "DTD";
                break;
            }
            case 3: {
                state = "PROCESSING_INSTRUCTION";
                break;
            }
            case 9: {
                state = "ENTITY_REFERENCE";
                break;
            }
            default: {
                state = "UNKNOWN_STATE: " + event;
                break;
            }
        }
        return state;
    }
}
