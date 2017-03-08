package cc.doctor.wiki.search.server.index.store.schema.reader;

import cc.doctor.wiki.exceptions.schema.PropertyConfigException;
import cc.doctor.wiki.exceptions.schema.SchemaException;
import cc.doctor.wiki.exceptions.schema.TypeHandlerNotFoundException;
import cc.doctor.wiki.search.server.index.store.schema.Schema;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/3.
 */
public class XmlSchemaReader implements SchemaReader {
    private static final Logger log = LoggerFactory.getLogger(SchemaReader.class);

    public Schema read(InputStream inputStream) {
        Schema schema = new Schema();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();

            // iterate through child elements of root
            for (Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element element = (Element) i.next();
                String name = element.getName();
                if (name != null) {
                    if (name.equals("indexName")) {
                        schema.setIndexName(element.getData().toString());
                    } else if (name.equals("alias")) {
                        schema.setAlias(element.getData().toString());
                    } else if (name.equals("dynamic")) {
                        schema.setAlias(element.getData().toString());
                    } else if (name.equals("typeHandlers")) {
                        setTypeHandlers(schema, element);
                    } else if (name.equals("properties")) {
                        setProperties(schema, element);
                    }
                }
            }
        } catch (DocumentException e) {
            log.error("", e);
            throw new SchemaException(e);
        }
        return schema;
    }

    public void setTypeHandlers(Schema schema, Element element) {
        List<Schema.TypeHandlerNode> typeHandlers = new LinkedList<Schema.TypeHandlerNode>();
        List elements = element.elements();
        for (Object o : elements) {
            Element typeHandlerElement = (Element) o;
            Element nameNode = typeHandlerElement.element("name");
            Element classNode = typeHandlerElement.element("class");
            if (nameNode == null) {
                throw new TypeHandlerNotFoundException("TypeHandler not found.");
            }
            typeHandlers.add(new Schema.TypeHandlerNode(nameNode.getName(), classNode == null ? null : classNode.getName()));
        }
        schema.setTypeHandlers(typeHandlers);
    }

    public void setProperties(Schema schema, Element element) {
        List<Schema.Property> properties = new LinkedList<Schema.Property>();
        List elements = element.elements();
        for (Object o : elements) {
            Element propertyElement = (Element) o;
            Element nameNode = propertyElement.element("name");
            Element typeNode = propertyElement.element("type");
            Element patternNode = propertyElement.element("pattern");
            Element typeHandlerNode = propertyElement.element("typeHandler");
            Element indexNode = propertyElement.element("index");
            List<String> filterNames = new LinkedList<String>();
            Element filtersNode = propertyElement.element("filters");
            if (filtersNode != null) {
                List<Element> filters = (List<Element>) filtersNode.elements();
                for (Element filter : filters) {
                    filterNames.add(filter.getData().toString());
                }
            }
            Element tokenizerNode = propertyElement.element("tokenizer");
            Element sourceNode = propertyElement.element("source");
            if (nameNode == null) {
                throw new PropertyConfigException("Property name forgot.");
            }
            properties.add(new Schema.Property(
                    nameNode.getData().toString(),
                    typeNode == null ? null : typeNode.getData().toString(),
                    patternNode == null ? null : patternNode.getData().toString(),
                    typeHandlerNode == null ? null : typeHandlerNode.getData().toString(),
                    indexNode == null ? null : indexNode.getData().toString(),
                    filterNames,
                    tokenizerNode == null ? null : tokenizerNode.getData().toString(),
                    sourceNode == null ? null : sourceNode.getData().toString()
            ));
        }
        schema.setProperties(properties);
    }
}
