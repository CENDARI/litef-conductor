package fr.inria.aviz.elasticindexer.tika.xml;

import java.util.Collections;
import java.util.Set;

import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.TextContentHandler;
import org.xml.sax.ContentHandler;

/**
 * Class OAIPMHParser
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class OAIPMHParser extends AbstractXMLParser {
    //private static final String NAMESPACE_URI_OAI_PMH = "http://www.openarchives.org/OAI/2.0";
    
    private static final Set<MediaType> SUPPORTED_TYPES =
            Collections.singleton(MediaType.application("oai-pmh+xml"));
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }
    
    private static ContentHandler getHandler(
            Metadata metadata, Property property, String element, 
            String ...context) {
        return new ContextualElementMetadataHandler(
                DublinCore.NAMESPACE_URI_DC, element,
                metadata, property, context);
    }

    protected ContentHandler getContentHandler(
            ContentHandler handler, Metadata metadata, ParseContext context) {
        ContentHandler defaultContentHandler = new TextContentHandler(handler, true);
        return new TeeContentHandler(
                defaultContentHandler,
                getHandler(metadata, TikaCoreProperties.TITLE, "title"),
                getHandler(metadata, TikaCoreProperties.DESCRIPTION, "description"), 
                getHandler(metadata, TikaCoreProperties.PUBLISHER, "publisher"),
                getHandler(metadata, TikaCoreProperties.CREATED, "date"),
                getHandler(metadata, TikaCoreProperties.TYPE, "type"),
                getHandler(metadata, TikaCoreProperties.FORMAT, "format"),
                getHandler(metadata, TikaCoreProperties.IDENTIFIER, "identifier"),
                getHandler(metadata, TikaCoreProperties.RIGHTS, "rights"),
                getHandler(metadata, TikaCoreProperties.LANGUAGE, "language"),
                getHandler(metadata, TikaCoreProperties.COVERAGE, "coverage"),
                getHandler(metadata, TikaCoreProperties.SOURCE, "source")
        );
    }
}
