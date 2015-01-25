package fr.inria.aviz.elasticindexer.utils;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.neovisionaries.i18n.LanguageAlpha3Code;
import com.neovisionaries.i18n.LanguageCode;

/**
 * Class LangCleaner
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class LangCleaner {
    private static Logger logger = Logger.getLogger(LangCleaner.class);
    
    private static final HashMap<String, String> char2tochar3= new HashMap<>();
    static {
        for (LanguageCode code : LanguageCode.values()) {
            String al2 = code.name(), al3 = code.getAlpha3().name();
            char2tochar3.put(al2, al3);
        }
        for (LanguageAlpha3Code code : LanguageAlpha3Code.values()) {
            String al3 = code.name(), syn = code.getSynonym().name();
            char2tochar3.put(al3, al3); // id
            if (syn != al3)
                char2tochar3.put(syn, al3); 
        }
    }
    
    private static boolean isLetter(char l) {
        return (l >= 'a' && l <= 'z');
    }

    /**
     * Given a string naming a language, returns is ISO 639-2 code or null
     * @param lang the language name
     * @return a normalized name or null
     */
    public static String cleanLanguage(String lang) {
        lang = lang.trim().toLowerCase();
        int n = lang.length();
        if (n < 2) {
            logger.info("Discarding lang code too short: "+lang);
            return null;
        }
        if (!isLetter(lang.charAt(0)) || !isLetter(lang.charAt(1))) {
            logger.info("Discarding invalid lang code: "+lang);
            return null;
        }
        if (n > 2 && !isLetter(lang.charAt(2)))
            lang = lang.substring(0, 2);
        else if (n > 3)
            lang = lang.substring(0, 3);
                
        String tr = char2tochar3.get(lang);
        if (tr == null) {
            logger.info("Discarding unknown lang code: "+lang);
        }
        return tr;
    }
}
