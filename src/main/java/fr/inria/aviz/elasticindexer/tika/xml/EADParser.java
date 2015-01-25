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
 * Class EADParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class EADParser extends AbstractXMLParser {
    private static final String NAMESPACE_URI_EAD = "";
    
    private static final Set<MediaType> SUPPORTED_TYPES =
            Collections.singleton(MediaType.application("ead+xml"));
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }
    
    private static ContentHandler getEADHandler(
            Metadata metadata, Property property, String element, 
            String ...context) {
        return new ContextualElementMetadataHandler(
                NAMESPACE_URI_EAD, element,
                metadata, property, context);
    }

    protected ContentHandler getContentHandler(
            ContentHandler handler, Metadata metadata, ParseContext context) {
        ContentHandler defaultContentHandler = new TextContentHandler(handler, true);
        return new TeeContentHandler(
                defaultContentHandler,
                getEADHandler(metadata, TikaCoreProperties.TITLE, 
                        "titleproper", "titlestmt"),
                //TODO check the specific EAD elements
                getEADHandler(metadata, TikaCoreProperties.KEYWORDS, 
                        "term", "keywords"),
                //getTEIHandler(metadata, TikaCoreProperties.CREATOR, "creator"),
                getEADHandler(metadata, TikaCoreProperties.DESCRIPTION, 
                        "description"),
                //getEADHandler(metadata, TikaCoreProperties.PUBLISHER, "publisher"),
                getEADHandler(metadata, TikaCoreProperties.CONTRIBUTOR, 
                        "name", "titleStmt", "respStmt"),
                //getEADHandler(metadata, CendariProperties.DATE, "date"),
                new ElementAttributeMetadataHandler(
                        NAMESPACE_URI_EAD, "normal",
                            metadata, CendariProperties.DATE, "unitdate") {
                    protected void addMetadata(String date) {
                        String[] dates = date.split("/");
                        for (String d : dates) 
                            super.addMetadata(d);
                    };
                },
                //getEADHandler(metadata, TikaCoreProperties.CREATED, "date"),
                //getEADHandler(metadata, TikaCoreProperties.TYPE, "type"),
                //getEADHandler(metadata, TikaCoreProperties.FORMAT, "format"),
                //getEADHandler(metadata, TikaCoreProperties.IDENTIFIER, "identifier"),
                getEADHandler(metadata, TikaCoreProperties.RIGHTS, "licence"),
                new AttributeMetadataHandler(NAMESPACE_URI_XML, "lang", metadata, 
                        CendariProperties.LANG),
                new AttributeMetadataHandler(NAMESPACE_URI_EAD, "langcode", metadata, 
                        CendariProperties.LANG)
        );
    }

}
