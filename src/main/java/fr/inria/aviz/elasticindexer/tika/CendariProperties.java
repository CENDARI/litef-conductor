package fr.inria.aviz.elasticindexer.tika;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;

/**
 * Class Cendari
 * 
 * A collection of Metadata properties recognized by Cendari indexer
 * 
 * @author Jean-Daniel Fekete
 */
public class CendariProperties {
    //private static final String NAMESPACE_URI_XML = "http://www.w3.org/XML/1998/namespace";
    private static final String PREFIX_XML = "xml";
    
    private static final String PREFIX_CENDARI = "cendari";
    
    /**
     * A language of the intellectual content of the resource. Recommended
     * best practice is to use RFC 3066 [RFC3066], which, in conjunction
     * with ISO 639 [ISO639], defines two- and three-letter primary language
     * tags with optional subtags. Examples include "en" or "eng" for English,
     * "akk" for Akkadian, and "en-GB" for English used in the United Kingdom.
     */
    public static final Property LANG = Property.internalTextBag(
            PREFIX_XML + Metadata.NAMESPACE_PREFIX_DELIMITER + "lang");
    
    /**
     * 
     */
    public static final Property PLACE = Property.internalTextBag(
            PREFIX_CENDARI + Metadata.NAMESPACE_PREFIX_DELIMITER + "place");

    /**
     * 
     */
    public static final Property PERSON = Property.internalTextBag(
            PREFIX_CENDARI + Metadata.NAMESPACE_PREFIX_DELIMITER + "person");
    
    /**
     * 
     */
    public static final Property DATE = Property.internalTextBag(
            PREFIX_CENDARI + Metadata.NAMESPACE_PREFIX_DELIMITER + "date");
    
    /**
     * Event type
     */
    public static final Property EVENT = Property.internalTextBag(
            PREFIX_CENDARI + Metadata.NAMESPACE_PREFIX_DELIMITER + "event");
    
    /**
     * Name of organizations
     */
    public static final Property ORGANIZATION = Property.internalTextBag(
            PREFIX_CENDARI + Metadata.NAMESPACE_PREFIX_DELIMITER + "organization");
    
    /**
     * External reference such as URI or DOI.
     */
    public static final Property REFERENCE = Property.internalTextBag(
            PREFIX_CENDARI + Metadata.NAMESPACE_PREFIX_DELIMITER + "reference");
    
    /**
     * Tag names.
     */
    public static final Property TAG = Property.internalTextBag(
            PREFIX_CENDARI + Metadata.NAMESPACE_PREFIX_DELIMITER + "tag");
    
    //TODO finish adding all the Cendari properties
}
