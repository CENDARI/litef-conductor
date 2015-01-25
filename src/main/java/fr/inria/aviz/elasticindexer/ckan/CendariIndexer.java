package fr.inria.aviz.elasticindexer.ckan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.inria.aviz.elasticindexer.DocumentInfo;
import fr.inria.aviz.elasticindexer.Indexer;

/**
 * Class CendariIndex
 *
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class CendariIndexer {
    private final Logger logger = Logger.getLogger(CendariIndexer.class);
    protected String location;
    protected String key;
    protected List<Object> dataspaces;
    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a CKANIndexer with a specified location
     * @param location the url of the CKAN server
     * @param key the Cendari user key
     */
    public CendariIndexer(String location, String key) {
        this.key = key;
        this.location = location;
    }

    /**
     * @return a list of packages
     */
    public List<Object> getDataspaces() {
        if (dataspaces == null) try {
            URL url = new URL(location+"v1/dataspaces/");
            URLConnection con = url.openConnection();
            con.setRequestProperty("Authorization", key);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            Map<String,Object> res = mapper.readValue(in, Map.class);
            dataspaces = (List<Object>)res.get("data");
        }
        catch(Exception e) {
            logger.error("Getting package list", e);
        }
        return dataspaces;
    }

    /**
     * Returns the list of resources in the specified package
     * @param pack the package name
     * @return a list of Resources
     */
    public ResourceList getResourceList(String pack) {
        ResourceList resources = new ResourceList();
        while (pack != null) try {
            URL url = new URL(pack);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            http.setInstanceFollowRedirects(true);
            http.setRequestProperty("Authorization", key);
            int status = http.getResponseCode();
            String redirect = http.getHeaderField("Location");
            if (redirect != null)
                System.out.println("Redirected to "+redirect);
            if (status == HttpURLConnection.HTTP_MOVED_PERM ||
                status == HttpURLConnection.HTTP_MOVED_TEMP ||
                status == HttpURLConnection.HTTP_SEE_OTHER) {
                pack = http.getHeaderField("Location");
                logger.info("Redirected to "+pack);
            }
            else if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(http.getInputStream()));
                Map<String,Object> res = mapper.readValue(in, Map.class);
                in.close();

                if (res.get("data") != null) {
                    List<Object> list = (List<Object>)res.get("data");
                    for (Object o : list) {
                        resources.add(new Resource((Map<String,Object>)o));
                    }
                }
                if (res.get("nextPage") != null && res.get("end") != Boolean.TRUE) {
                    pack = (String)res.get("nextPage");
                }
                else {
                    pack = null;
                }
            }
            else {
                pack = null;
            }
        }
        catch(Exception e) {
            logger.error("Getting package content", e);
            pack = null;
        }
        return resources;
    }

    /**
     * Return an input stream for a specified data
     * @param res
     * @return a byte array with the contents
     */
    public byte[] getData(String res) {
        if (res == null) return null;

        byte[] contents = null;
        while (res != null) try {
            URL url = new URL(res);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            http.setInstanceFollowRedirects(true);
            http.setRequestProperty("Authorization", key);
            int status = http.getResponseCode();
            String redirect = http.getHeaderField("Location");
            if (redirect == null && status == HttpURLConnection.HTTP_OK) {
                InputStream in = http.getInputStream();
                contents = IOUtils.toByteArray(in);
                break;
            }
            if (redirect != null) {
                logger.info("Redirecting to "+redirect);
                res = redirect;
            }
            logger.error("Cannot access resource at "+res);
            return null;
        }
        catch(Exception e) {
            logger.error("Getting data content", e);
        }
        return contents;
    }

    /**
     * Main program to test
     * @param arguments
     */
    /* public static void main(String[] arguments) { */
        /* Indexer indexer = Indexer.instance(); */
        /* ObjectMapper mapper = new ObjectMapper(); */
        /* mapper.enable(SerializationFeature.INDENT_OUTPUT); */
        /*  */
        /* String location = arguments[0]; */
        /* String key = arguments[1]; */
        /* CendariIndexer cendari = new CendariIndexer(location, key); */
        /* List<Object> packages = cendari.getDataspaces(); */
        /* for (Object o: packages) { */
        /*     Map<String,Object> p = (Map<String,Object>)o; */
        /*     System.out.println("Package is:"); */
        /*     try { */
        /*         System.out.println(mapper.writeValueAsString(p)); */
        /*     } catch (JsonGenerationException e1) { */
        /*         // TODO Auto-generated catch block */
        /*         e1.printStackTrace(); */
        /*     } catch (JsonMappingException e1) { */
        /*         // TODO Auto-generated catch block */
        /*         e1.printStackTrace(); */
        /*     } catch (IOException e1) { */
        /*         // TODO Auto-generated catch block */
        /*         e1.printStackTrace(); */
        /*     } */
        /*  */
        /*     ResourceList res = cendari.getResourceList((String)p.get("resources")); */
        /*     for (Resource r : res) */
        /*         try { */
        /*             System.out.println(mapper.writeValueAsString(r)); */
        /*             byte[] content = cendari.getData((String)r.get("dataUrl")); */
        /*             if (content == null) { */
        /*                 System.out.println("Cannot get content of dataUrl"+(String)r.get("dataUrl")); */
        /*                 continue; */
        /*             } */
        /*             DocumentInfo info = indexer.parseDocument((String)r.get("name"), null, content, -1); */
        /*             System.out.println(mapper.writeValueAsString(info)); */
        /*             info.setGroups_allowed((String)p.get("name")); */
        /*             indexer.indexDocument(info); */
        /*              */
        /*         } catch (Exception e) { */
        /*             // TODO Auto-generated catch block */
        /*             e.printStackTrace(); */
        /*         } */
        /* } */
    /* } */

}
