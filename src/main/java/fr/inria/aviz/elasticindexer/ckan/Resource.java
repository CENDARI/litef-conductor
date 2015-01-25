package fr.inria.aviz.elasticindexer.ckan;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class Resource implements a CKAN resource
 * 
 * @author Jean-Daniel Fekete
 */
public class Resource extends HashMap<String,Object> {
    static final Logger logger = Logger.getLogger(Resource.class);
    
    /**
     * 
     */
    public Resource() {
    }
    
    /**
     * Creates a resource from a map
     * @param other the other map
     */
    Resource(Map<String,Object> other) {
        super(other);
    }

    /**
     * Read the resource file from JSON format.
     * @param json the json-encoded string
     * @param mapper the object mapper for json
     * @return a Resouce or null
     */
    public static Resource fromJSON(String json, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, Resource.class);
        }
        catch(Exception e) {
            logger.error("Reading Resource json", e);
            return null;
        }
    }
    
    /**
     * Reads a Resource from an JSON encoded input stream.
     * @param in the input stream
     * @param mapper the object mapper for json
     * @return a Resource or null
     */
    public static Resource fromJSON(InputStream in, ObjectMapper mapper) {
        try {
            return mapper.readValue(in, Resource.class);
        }
        catch(Exception e) {
            logger.error("Reading Resource json", e);
            return null;
        }
    }
}
