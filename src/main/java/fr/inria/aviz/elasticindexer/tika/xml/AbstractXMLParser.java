package fr.inria.aviz.elasticindexer.tika.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.EmbeddedContentHandler;
import org.apache.tika.sax.OfflineContentHandler;
import org.apache.tika.sax.TaggedContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class AbstractXMLParser
 * 
 * @author Jean-Daniel Fekete
 */
public abstract class AbstractXMLParser extends AbstractParser
    implements ErrorHandler {
    static final protected Logger logger = Logger.getLogger(AbstractXMLParser.class);
    protected static final String NAMESPACE_URI_XML = "http://www.w3.org/XML/1998/namespace";
    
    protected Transformer transformer;
    
    /**
     * Find XSLT transformer if the resource exists. 
     */
    public AbstractXMLParser() {
        Class me = this.getClass();
        String name = me.getSimpleName();
        URL url = me.getResource(name+".xsl");
        if (url != null) try {
            TransformerFactory factory = TransformerFactory.newInstance();
            
            Source xsl = new StreamSource(url.openStream(),name);
            transformer = factory.newTransformer(xsl);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void parse(
            InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {
        if (metadata.get(Metadata.CONTENT_TYPE) == null) {
            Iterator<MediaType> iter = getSupportedTypes(context).iterator();
            metadata.set(Metadata.CONTENT_TYPE, iter.next().getType());
        }

        final XHTMLContentHandler xhtml =
            new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.startElement("p");

        TaggedContentHandler tagged = new TaggedContentHandler(handler);
        try {
            InputStream in = new CloseShieldInputStream(stream);
            OfflineContentHandler out = new OfflineContentHandler(
                    new EmbeddedContentHandler(
                            getContentHandler(tagged, metadata, context)));
            
            if (transformer != null) {
                transformer.transform(
                        new StreamSource(in), 
                        new SAXResult(out));
            }
            else {
                SAXParser parser = context.getSAXParser();
                parser.getXMLReader().setErrorHandler(this);
                parser.parse(in, out);
            }
        } catch (Exception e) {
            tagged.throwIfCauseOf(e);
            throw new TikaException("XML parse error", e);
        } finally {
            xhtml.endElement("p");
            xhtml.endDocument();
        }
    }
    
    protected abstract ContentHandler getContentHandler(
            ContentHandler handler, 
            Metadata metadata, 
            ParseContext context);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void error(SAXParseException ex) throws SAXException {
        logger.error("Error parsing document", ex);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        logger.error("Fatal error parsing document", ex);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void warning(SAXParseException ex) throws SAXException {
        logger.warn("Warning parsing document", ex);
    }
}
