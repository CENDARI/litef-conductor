package fr.inria.aviz.elasticindexer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.inria.aviz.elasticindexer.tika.CendariProperties;
import fr.inria.aviz.elasticindexer.utils.TextCleaner;

/**
 * Singleton class, indexes documents.
 *
 * Indexer is a Facade to elasticsearch for indexing documents.
 */
public class Indexer {
    private static final Logger logger = Logger.getLogger(Indexer.class.getName());
    private static Indexer instance_;
    private final Tika tika = new Tika();
    private final Properties props = new Properties();
    private Client es;
    private Node node = null;
    private boolean esChecked;
    private ObjectMapper mapper = new ObjectMapper();

    /** Resource name for the main index */
    public static final String ES_INDEX = "elasticindexer.elasticsearch.index";
    /** Resource name for the document type */
    public static final String ES_TYPE = "elasticindexer.elasticsearch.type";
    /** Resource name for the elasticsearch cluster name */
    public static final String ES_CLUSTER = "elasticindexer.elasticsearch.cluster";
    /** Resource name for the elasticsearch host name, defaults to localhost */
    public static final String ES_HOST1 = "elasticindexer.elasticsearch.host1";
    /** Resource name for the elasticsearch port number, defaults to 9300 */
    public static final String ES_PORT1 = "elasticindexer.elasticsearch.port1";
    /** Resource name for the second elasticsearch host name, defaults to none */
    public static final String ES_HOST2 = "elasticindexer.elasticsearch.host2";
    /** Resource name for the second elasticsearch port number, defaults to 9300 */
    public static final String ES_PORT2 = "elasticindexer.elasticsearch.port2";

    /**
     * @return the Indexer singleton instance, creating it if necessary.
     */
    public static Indexer instance() {
        if (instance_ == null) {
            instance_ = new Indexer();
        }
        return instance_;
    }

    private Indexer() {
        loadProps();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.setSerializationInclusion(Include.NON_NULL);

    }

    private void connectES() {
        if (es != null) return;
        String cluster = props.getProperty(ES_CLUSTER, "elasticsearch"),
                host1 = props.getProperty(ES_HOST1);

        if (host1 != null) {
            String port1 = props.getProperty(ES_PORT1);
            int port = 9300;
            if (port1 != null) try {
                port = Integer.parseInt(port1);
            }
            catch(NumberFormatException e) {
                logger.error("Invalid port number for "+ES_PORT1+": "+port1, e);
            }
            TransportClient tes = new TransportClient(ImmutableSettings.settingsBuilder()
                    .put("cluster.name", cluster)
                    .build());
            tes.addTransportAddress(new InetSocketTransportAddress(host1, port));

            String host2 =  props.getProperty(ES_HOST2),
                    port2 = props.getProperty(ES_PORT2);

            if (host2 != null) {
                port = 9300;
                if (port2 != null) try {
                    port = Integer.parseInt(port2);
                }
                catch(NumberFormatException e) {
                    logger.error("Invalid port number for "+ES_PORT2+": "+port2, e);
                }
                tes.addTransportAddress(new InetSocketTransportAddress(host2, port));
            }
            es = tes;
        }
        else {
            node = NodeBuilder.nodeBuilder().clusterName(cluster).node();
            es = node.client();
        }
        final Indexer that = this;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info("Closing Indexer at Shutdown");
                that.closeES();
            }
        }));
    }

    private void loadProps() {
        try {
            InputStream in = getClass().getResourceAsStream("/elasticindexer.properties");
            props.load(in);
            in.close();
        }
        catch(Exception e) {
            logger.error("Elasticindexer properties not found", e);
        }
    }

    /**
     * Close the ElasticSearch connection.
     */
    public void closeES() {
        if (es == null) return;
        if (node != null)
            node.close();
        else
            es.close();
        es = null;
        node = null;
        _close();
    }

    void _close() {
        esChecked = false;
    }


    /**
     * Delete the index. Use with caution.
     */
    void _deleteIndex() {
        connectES();
        esChecked = false;
        DeleteIndexResponse rep = null;
        try {
            rep = es.admin().
                         indices().
                         prepareDelete(getIndexName()).
                         execute().
                         actionGet();
        }
        catch (IndexMissingException e) {
            return;
        }

        if (! rep.isAcknowledged()) {
            logger.error("Index "+getIndexName()+" has not been deleted");
        }
//        es.admin().indices()
//            .prepareFlush(getIndexName())
//            .execute()
//            .actionGet();
    }

    void _deleteMapping() {
        connectES();
        esChecked = false;
        DeleteMappingResponse rep = null;
        rep = es.admin().indices().
                prepareDeleteMapping(getIndexName()).
                setType(getTypeName()).
                execute().
                actionGet();

        if (! rep.isAcknowledged()) {
            logger.error("Mapping "+getTypeName()+" has not been deleted");
        }
    }

    /**
     * Test the ElasticSearch mapping.
     */
    public void checkESMapping() {
        if (esChecked)
            return;
        connectES();
        esChecked = true;
        String mapping = getMapping();
        IndexMetaData imd = null;

        if (es.admin()
                .indices()
                .prepareExists(getIndexName())
                .execute()
                .actionGet()
                .isExists()) {
            try {
                ClusterState cs = es.admin().
                                 cluster().
                                 prepareState().
                                 setIndices(getIndexName()).
                                 execute().
                                 actionGet().
                                 getState();

                imd = cs.getMetaData().index(getIndexName());
            }
            catch (IndexMissingException e) {
                logger.warn("Cluster state report no index for "+getIndexName()+" when indices was found, recreating...", e);
                createIndex(mapping);
                return;
            }
        }
        else {
            createIndex(mapping);
            return;
        }

        if (imd == null || imd.mapping(getTypeName()) == null) {
            PutMappingResponse response=es.admin().
                    indices().
                    preparePutMapping(getIndexName()).
                    setType(getTypeName()).
                    setSource(mapping).
                    execute().
                    actionGet();

            if (! response.isAcknowledged()) {
                logger.error("Cannot create mapping in "+getIndexName());
            }
            return;
        }
        MappingMetaData mdd = imd.mapping(getTypeName());

        try {
            JsonNode mappingRoot = mapper.readTree(mapping);
            JsonNode mddRoot = mapper.readTree(mdd.source().toString());
            if (! mappingRoot.equals(mddRoot)) {
                logger.error("Mappings differ, should update");
            }
        } catch (IOException e) {
            logger.error("Cannot create json from string", e);
        }
    }

    private void createIndex(String mapping) throws ElasticsearchException {
        logger.info("Creating index "+getIndexName());
        final CreateIndexRequestBuilder createIndexRequestBuilder = es.admin().indices().prepareCreate(getIndexName());
        createIndexRequestBuilder.addMapping(getTypeName(), mapping);
        if (! createIndexRequestBuilder
                .execute()
                .actionGet()
                .isAcknowledged()) {
            logger.error("Index/Mapping not created for index "+getIndexName());
        }
    }

    private String getMapping() {
        String mapping = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            /* mapping = IOUtils.toString( */
            /*         classLoader.getResource("cendari_document_mapping.json"), */
            /*         StandardCharsets.UTF_8); */
            mapping = "";
        }
        catch(Exception e) {
            logger.error("Cannot get cendari mapping file", e);
        }

        return mapping;
    }

    /**
     * @return the mapper
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Refresh the index immediately (it is refreshed every second otherwise).
     */
    public void refreshIndex() {
        checkESMapping();
        es.admin()
            .indices()
            .refresh(new RefreshRequest(getIndexName()))
            .actionGet();
    }

    /**
     * @return the index name
     */
    public String getIndexName() { return props.getProperty(ES_INDEX, "cendari"); }

    /**
     * @return the document type name
     */
    public String getTypeName() { return props.getProperty(ES_TYPE, "document"); }

    /**
     * Inserts the specific JSON representation of a DocumentInfo
     * @param document the json string
     * @return true if the entry has been created, false if it has been updated
     */
    public boolean indexDocument(String document) {
        checkESMapping();
        IndexResponse res = es.prepareIndex(getIndexName(), getTypeName())
                .setSource(document)
                .execute()
                .actionGet();
        return res.isCreated();
    }

    /**
     * Returns the JSON serialization of the specified DocumentInfo.
     * @param document the DocumentInfo
     * @return a JSON string
     * @throws JsonProcessingException if the serializer fails
     */
    public String toJSON(DocumentInfo document) throws JsonProcessingException {
        return document.toJSON(mapper);
    }

    /**
     * Inserts the DocumentInfo
     * @param document the DocumentInfo
     * @return true if the entry has been created, false if it has been updated
     * @throws JsonProcessingException if the document has not been serialized correctly
     */
    public boolean indexDocument(DocumentInfo document) throws JsonProcessingException {
        return indexDocument(toJSON(document));
    }

    /**
     * Search for documents matching a specific query and filter.
     * @param query the query or null
     * @param filter the filter or null
     * @return a list of JSON records
     */
    public String[] searchDocument(QueryBuilder query, FilterBuilder filter) {
        checkESMapping();
        SearchRequestBuilder search = es.prepareSearch(getIndexName())
                .setTypes(getTypeName());
        if (filter != null) {
            query = QueryBuilders.filteredQuery(query, filter);
        }
        if (query != null)
            search.setQuery(query);
        SearchResponse res = search.execute().actionGet();
        String[] ret = new String[(int)res.getHits().getTotalHits()];
        int i = 0;
        for (SearchHit hit : res.getHits()) {
            ret[i++] = hit.getSourceAsString();
        }
        return ret;
    }

    /**
     * Parse a specified document using Tika and returns the related DocumentInfo.
     *
     * @param name document name
     * @param contentType document type or null if unknown
     * @param content document as a byte array
     * @param maxLength maximum length to parse or -1 to parse all
     * @return a DocumentInfo structure filled with the right contents of null if tika has not been able to parse it.
     */
    public DocumentInfo parseDocument(String name, String contentType, byte[] content, int maxLength) {
        return parseDocument(name, contentType, new BytesStreamInput(content, false), maxLength);
    }

    /**
     * Parse a specified document using Tika and returns the related DocumentInfo.
     *
     * @param name document name
     * @param contentType document type or null if unknown
     * @param content document stream, which will be closed at the end of the call
     * @param maxLength maximum length to parse or -1 to parse all
     * @return a DocumentInfo structure filled with the right contents of null if tika has not been able to parse it.
     */
    public DocumentInfo parseDocument(String name, String contentType, InputStream content, int maxLength) {
        Metadata metadata = new Metadata();

        if (name != null) {
            metadata.add(Metadata.RESOURCE_NAME_KEY, name);
        }
        if (contentType != null) {
            metadata.add(Metadata.CONTENT_TYPE, contentType);
        }

        String parsedContent;
        try {
            parsedContent = tika.parseToString(content, metadata, maxLength);
        } catch (IOException | TikaException e) {
            logger.error("Tika parse exception for document "+name, e);
            return null;
        }
        DocumentInfo info = new DocumentInfo();
        parsedContent = TextCleaner.cleanup(parsedContent);
        info.setText(parsedContent);
        info.setUri(name);
        info.setFormat(metadata.get(Metadata.CONTENT_TYPE));
        if (metadata.get(TikaCoreProperties.CREATED) != null)
            info.setDate(metadata.get(TikaCoreProperties.CREATED));
        if (metadata.get(CendariProperties.DATE) != null)
            info.addDate(metadata.getValues(CendariProperties.DATE));
        if (metadata.get(TikaCoreProperties.TITLE) != null)
            info.setTitle(metadata.get(TikaCoreProperties.TITLE));
        if (metadata.get(TikaCoreProperties.CREATOR) != null)
            info.setCreatorName(metadata.get(TikaCoreProperties.CREATOR));
        if (metadata.get(TikaCoreProperties.CREATOR_TOOL) != null)
            info.setApplication(metadata.get(TikaCoreProperties.CREATOR_TOOL));
        if (metadata.get(TikaCoreProperties.KEYWORDS) != null)
            info.setTag(metadata.getValues(TikaCoreProperties.KEYWORDS));
        if (metadata.get("Application-Name") != null)
            info.setApplication(metadata.get(metadata.get("Application-Name")));
        if (metadata.get(TikaCoreProperties.PUBLISHER) != null)
            info.setPublisher(metadata.getValues(TikaCoreProperties.PUBLISHER));
        if (metadata.get("Creation-date") != null)
            info.addDate(metadata.get("Creation-date"));
        if (metadata.get(TikaCoreProperties.MODIFIED) != null)
            info.addDate(metadata.getValues(TikaCoreProperties.MODIFIED));
        if (metadata.get(TikaCoreProperties.CONTRIBUTOR) != null)
            info.setContributorName(metadata.getValues(TikaCoreProperties.CONTRIBUTOR));

        if (metadata.get(CendariProperties.LANG) != null)
            info.setLanguage(metadata.getValues(CendariProperties.LANG));
        else if (metadata.get(TikaCoreProperties.LANGUAGE) != null)
            info.setLanguage(metadata.get(TikaCoreProperties.LANGUAGE));
        else {
            LanguageIdentifier langIdent = new LanguageIdentifier(parsedContent);
//            if (langIdent.isReasonablyCertain())
                info.setLanguage(langIdent.getLanguage());
        }
        if (metadata.get(TikaCoreProperties.LATITUDE)!= null && metadata.get(TikaCoreProperties.LONGITUDE) != null) {
            String latlon =
                    metadata.get(TikaCoreProperties.LATITUDE) +
                    ", "+
                    metadata.get(TikaCoreProperties.LONGITUDE);
            info.setPlace(new Place(null, latlon));
        }
        if (metadata.get(TikaCoreProperties.DESCRIPTION) != null)
            info.put("description", metadata.get(TikaCoreProperties.DESCRIPTION));
        if (metadata.get(CendariProperties.PERSON) != null)
            info.setPersonName(metadata.getValues(CendariProperties.PERSON));
        if (metadata.get(CendariProperties.ORGANIZATION) != null)
            info.setOrg(metadata.getValues(CendariProperties.ORGANIZATION));
        if (metadata.get(CendariProperties.TAG) != null)
            info.setTag(metadata.getValues(CendariProperties.TAG));
        if (metadata.get(CendariProperties.REFERENCE) != null)
            info.setRef(metadata.getValues(CendariProperties.REFERENCE));
        if (metadata.get(CendariProperties.EVENT) != null)
            info.setEvent(metadata.getValues(CendariProperties.EVENT));
        if (metadata.get(CendariProperties.PLACE) != null) {
            String[] names = metadata.getValues(CendariProperties.PLACE);
            info.setPlaceName(names);
//            Place[] places = info.getPlace();
//            for (Place p : places) {
//                p.resolve();
//            }
        }
        return info;
    }

}
