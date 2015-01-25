package fr.inria.aviz.elasticindexer.tika.xml;

import java.util.Collections;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.AttributeMetadataHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.TextContentHandler;
import org.xml.sax.ContentHandler;
import fr.inria.aviz.elasticindexer.tika.CendariProperties;

/**
 * Class EAGParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class EAGParser extends AbstractXMLParser {
private static final String NAMESPACE_URI_EAG = "http://www.ministryculture.es/";
    
    private static final Set<MediaType> SUPPORTED_TYPES =
            Collections.singleton(MediaType.application("eag+xml"));
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }
    
    private static ContentHandler getEAGHandler(
            Metadata metadata, Property property, String element, 
            String ...context) {
        return new ContextualElementMetadataHandler(
                NAMESPACE_URI_EAG, element,
                metadata, property, context);
    }

    protected ContentHandler getContentHandler(
            ContentHandler handler, Metadata metadata, ParseContext context) {
        ContentHandler defaultContentHandler = new TextContentHandler(handler, true);
        return new TeeContentHandler(
                defaultContentHandler,
                getEAGHandler(metadata, TikaCoreProperties.TITLE, 
                        "autform", "identity"),
                //TODO check the specific EAD elements
                getEAGHandler(metadata, TikaCoreProperties.KEYWORDS, 
                        "subject", "controlaccess"),
                //getTEIHandler(metadata, TikaCoreProperties.CREATOR, "creator"),
                getEAGHandler(metadata, TikaCoreProperties.DESCRIPTION, 
                        "description"),
                //getEADHandler(metadata, TikaCoreProperties.PUBLISHER, "publisher"),
                //getEADHandler(metadata, TikaCoreProperties.CREATED, "date"),
                //getEADHandler(metadata, TikaCoreProperties.TYPE, "type"),
                //getEADHandler(metadata, TikaCoreProperties.FORMAT, "format"),
                getEAGHandler(metadata, TikaCoreProperties.IDENTIFIER, "repositoryid"),
                //getEAGHandler(metadata, TikaCoreProperties.RIGHTS, "licence"),
                getEAGHandler(metadata, CendariProperties.PLACE, "location", "desc"),
                TEIParser.getTEIHandler(metadata, TikaCoreProperties.CONTRIBUTOR,
                        "persName", "respevent", "person"),
                TEIParser.getTEIHandler(metadata, CendariProperties.ORGANIZATION, 
                        "orgName"),
                getEAGHandler(metadata, CendariProperties.ORGANIZATION, "autform"),
                getEAGHandler(metadata, CendariProperties.REFERENCE, "source"),
                new AttributeMetadataHandler(NAMESPACE_URI_XML, "lang", metadata, 
                        CendariProperties.LANG)
        );
    }
}
