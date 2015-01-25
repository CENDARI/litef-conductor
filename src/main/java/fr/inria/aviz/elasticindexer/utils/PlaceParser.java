package fr.inria.aviz.elasticindexer.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class PlaceParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class PlaceParser {
    static final private Logger logger = Logger.getLogger(PlaceParser.class);
    private static String googleKey = null;
    private static final String GOOGLE_API = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String GETTY_API = "http://ref.dariah.eu/tgnsearch/tgnquery2.xql";
    private static ObjectMapper mapper = new ObjectMapper();
    private static DocumentBuilder builder;
    
    private static final DocumentBuilder getBuilder() {
        if (builder == null) try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setValidating(false);
            dbf.setIgnoringComments(false);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setNamespaceAware(true);
            builder = dbf.newDocumentBuilder();
            builder.setEntityResolver(
                    new EntityResolver() {
                        public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
                        IOException {
                            return new InputSource(new StringReader(""));
                        }
                    });
        }
        catch(Exception e) {
            logger.error("Cannot create xml parser", e);
        }
        return builder;
    }

    /**
     * Resolve a place name into a LAT,lON string
     * @param name a place name
     * @return a LAT,LON string or null
     */
    public static String resolvePlace(String name) {
        try {
            URL api = new URL(GETTY_API+"?ac="+URLEncoder.encode(name,"utf-8"));
            HttpURLConnection conn = (HttpURLConnection) api.openConnection();
            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/xml");
     
            if (conn.getResponseCode() != 200) {
                logger.error("Failed : HTTP error code : "+ conn.getResponseCode());
            }
            else {
                DocumentBuilder db = getBuilder();
                if (db != null) {
                    Document doc = db.parse(conn.getInputStream());
              
                    doc.getDocumentElement().normalize();
                    NodeList terms = doc.getElementsByTagName("term");
                    int placeCount = terms.getLength();
                    System.out.println("Read "+placeCount+" places for "+name);
                    for (int i = 0; i < placeCount; i++) {
                        Node term = terms.item(i);
                        if (term.getNodeType() != Node.ELEMENT_NODE)
                            continue; // skip over non-elements
                        NodeList nodes = term.getChildNodes();
                        String fullname = null, 
//                                path = null,
                                lat = null, 
                                lon = null;
                        for (int j = 0; j < nodes.getLength(); j++) {
                            Node node = nodes.item(j);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element e = (Element)node;
                                if (e.getLocalName().equals("name"))
                                    fullname = e.getTextContent();
//                                else if (e.getLocalName().equals("path"))
//                                    path = e.getTextContent();
                                else if (e.getLocalName().equals("latitude"))
                                    lat =  e.getTextContent();
                                else if (e.getLocalName().equals("longitude"))
                                    lon =  e.getTextContent();
                            }
                        }
                        if (fullname != null && lat != null && lon != null) {
                            String loc = lat+", "+lon;
                            return loc;
                        }
                    }
                    
//                    TransformerFactory tf = TransformerFactory.newInstance();
//
//                    Transformer transformer = tf.newTransformer();
//                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//                    StringWriter writer = new StringWriter();
//                    transformer.transform(new DOMSource(doc), new StreamResult(writer));
//                    String output = writer.getBuffer().toString();
//                    System.out.println(output);
                }
            }
        }
        catch(Exception e) {
            logger.error("Getty service cannot parse place", e);
            // ignore for now
        }
        if (googleKey == null) return null;
        try {
            URL api = new URL(GOOGLE_API+"?address="+URLEncoder.encode(name,"utf-8")+"&key="+googleKey);
            JsonNode root = mapper.readTree(api);
            JsonNode locNode = root
                    .path("results")
                    .path(0)
                    .path("geometry")
                    .path("location");
            String loc = locNode.get("lat")+", "+locNode.get("lng");
            return loc;
        }
        catch(Exception e) {
        }
        return null;
    }
    
    /**
     * @return the googleKey
     */
    public static String getGoogleKey() {
        return googleKey;
    }
    
    /**
     * @param key the googleKey to set
     */
    public static void setGoogleKey(String key) {
        googleKey = key;
    }
}
