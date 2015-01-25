package fr.inria.aviz.elasticindexer.ckan;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

/**
 * Class ResourceList manages a list of resources
 *
 * @author Jean-Daniel Fekete
 */
public class ResourceList extends ArrayList<Resource> {
    static final Logger logger = Logger.getLogger(ResourceList.class);

    /**
     * Returns  a ResourceList from a json string
     * @param json the json string
     * @param mapper the object mapper for json
     * @return a resourceList or null
     */
    public static ResourceList fromJSON(String json, ObjectMapper mapper) {
        try {
            return mapper.readValue(json, ResourceList.class);
        }
        catch(Exception e) {
            Resource.logger.error("Reading ResourceList json", e);
            return null;
        }
    }

    /**
     * Returns  a ResourceList from a json-encoded input stream
     * @param in the input stream
     * @param mapper the object mapper for json
     * @return a resourceList or null
     */
    public static ResourceList fromJSON(InputStream in, ObjectMapper mapper) {
        try {
            return mapper.readValue(in, ResourceList.class);
        }
        catch(Exception e) {
            Resource.logger.error("Reading ResourceList json", e);
            return null;
        }
    }

    /**
     * Test resource loader.
     *
     * @param arguments main args
     */
    /* public static void main(String[] arguments) { */
    /*     ObjectMapper mapper = new ObjectMapper(); */
    /*      */
    /*     for (String filename : arguments) try { */
    /*         InputStream in = new FileInputStream(filename); */
    /*         if (filename.endsWith(".gz")) */
    /*             in = new GZIPInputStream(in); */
    /*         ResourceList res = ResourceList.fromJSON(in, mapper); */
    /*         System.out.println("Size: "+res.size()); */
    /*         in.close(); */
    /*     } */
    /*     catch(Exception e) { */
    /*         logger.error(e); */
    /*     } */
    /* } */


}
