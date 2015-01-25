package fr.inria.aviz.elasticindexer.utils;

import java.util.regex.Pattern;

/**
 * Class TextCleaner
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class TextCleaner {
    private static final Pattern SPACES = Pattern.compile("\\s+"); 

    /**
     * Cleanup a specified string, removing extra spaces
     * @param value a string
     * @return its cleaned-up version
     */
    public static String cleanup(String value) {
        value = value.trim();
        value = SPACES.matcher(value).replaceAll(" "); 
        return value;
    }
}
