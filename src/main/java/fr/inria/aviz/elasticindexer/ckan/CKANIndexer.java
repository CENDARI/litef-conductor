package fr.inria.aviz.elasticindexer.ckan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.inria.aviz.elasticindexer.DocumentInfo;
import fr.inria.aviz.elasticindexer.Indexer;

/**
 * Class CKANIndexer
 *
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class CKANIndexer {
    private final Logger logger = Logger.getLogger(CKANIndexer.class);
    protected String location;
    protected String username;
    protected String[] package_list;
    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a CKANIndexer with a specified location
     * @param location the url of the CKAN server
     * @param username the CKAN user
     */
    public CKANIndexer(String location, String username) {
        this.username = username;
        this.location = location;
    }

    /**
     * Connects to the CKAN server with a specified password (not stored)
     * @param pass the password
     */
    public void login(String pass) {
        //TODO
    }

    /**
     * @return a list of packages
     */
    public String[] getPackageList() {
        if (package_list == null) try {
            URL url = new URL(location+"ckan/api/3/action/package_list");
            URLConnection con = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            Map<String,Object> res = mapper.readValue(in, Map.class);
            //ResultValue res = mapper.readValue(in, ResultValue.class);
            if (res.get("result") != null) {
                List<String> result = (List<String>)res.get("result");
                package_list = new String[result.size()];
                package_list = result.toArray(package_list);
            }
        }
        catch(Exception e) {
            logger.error("Getting package list", e);
        }
        return package_list;
    }

    /**
     * Returns the list of resources in the specified package
     * @param pack the package name
     * @return a list of Resources
     */
    public ResourceList getResourceList(String pack) {
        ResourceList resources = new ResourceList();
        try {
            URL url = new URL(location+"ckan/api/3/action/package_show?id="+pack);
            URLConnection con = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            Map<String,Object> res = mapper.readValue(in, Map.class);
            if (res.get("result") == null)
                return null;
            res = (Map<String,Object>)res.get("result");
            if (res.get("resources") != null) {
                List<Object> list = (List<Object>)res.get("resources");
                for (Object o : list) {
                    resources.add(new Resource((Map<String,Object>)o));
                }
            }
        }
        catch(Exception e) {
            logger.error("Getting package content", e);
        }
        return resources;
    }

    /**
     * Main program to test
     * @param arguments
     */
    /* public static void main(String[] arguments) { */
    /*     Indexer indexer = Indexer.instance(); */
    /*     ObjectMapper mapper = new ObjectMapper(); */
    /*     mapper.enable(SerializationFeature.INDENT_OUTPUT); */
    /*      */
    /*     for (String location: arguments) { */
    /*         CKANIndexer ckan = new CKANIndexer(location, "Jean-Daniel Fekete"); */
    /*         String[] packages = ckan.getPackageList(); */
    /*         for (String s : packages) { */
    /*             System.out.println(s); */
    /*             for (Resource r : ckan.getResourceList(s)) try { */
    /*                 String name = (String)r.get("url"); */
    /*                 DocumentInfo info = indexer.parseDocument(name, null, new URL(name).openStream(), -1); */
    /*                 if (info.getText()!=null &&info.getText().length()!=0) { */
    /*                     if (r.get("created")!=null) */
    /*                         info.setDate((String)r.get("created")); */
    /*                     if (r.get("revision_timestamp")!=null) */
    /*                         info.addDate((String)r.get("revision_timestamp")); */
    /*                     indexer.indexDocument(info); */
    /*                     System.out.println(info.toJSON(mapper)); */
    /*                 } */
    /*             } */
    /*             catch(Exception e) { */
    /*                 e.printStackTrace(); */
    /*             } */
    /*         } */
    /*     } */
    /* } */
}
