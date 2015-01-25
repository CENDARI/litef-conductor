package fr.inria.aviz.elasticindexer.tika.xml;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.xml.AttributeMetadataHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import fr.inria.aviz.elasticindexer.utils.TextCleaner;

/**
 * Class ElementAttributeMetadataHandler
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision$
 */
public class ElementAttributeMetadataHandler extends AttributeMetadataHandler {
    private String elemName;
    
    /**
     * Creates a Attribute handler for a specific attribute on a specific element
     * @param uri the namespace
     * @param localName the attribute name
     * @param metadata the metadata to populate
     * @param property the metadata propery to populate
     * @param elemName the element name to match in the same namespace
     */
    public ElementAttributeMetadataHandler(
            String uri, String localName, Metadata metadata,
            Property property, String elemName) {
        super(uri, localName, metadata, property);
        this.elemName = elemName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(
            String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (localName.equals(this.elemName))
            super.startElement(uri, localName, qName, attributes);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void addMetadata(String value) {
        super.addMetadata(TextCleaner.cleanup(value));
    }

}
