package cc.doctor.wiki.search.client.index.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/3.
 */
public class Schema implements Serializable {
    private static final long serialVersionUID = -5358709288352559858L;
    private String indexName;   //索引名
    private String alias;       //别名,通过索引名别名任意一个均可以访问
    private int replicate;      //副本数目
    private int shards;      //分片数目
    private String dynamic;  //是否自动探测格式
    private List<Property> properties = new LinkedList<>();//字段
    private Map<String, Property> propertyMap = new HashMap<>();
    private List<String> filters = new LinkedList<>();   //自定义前置过滤器
    private List<TypeHandlerNode> typeHandlers = new LinkedList<>();//自定义类型转换器
    private List<String> tokenizers = new LinkedList<>(); //自定义分词器

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getReplicate() {
        return replicate;
    }

    public void setReplicate(int replicate) {
        this.replicate = replicate;
    }

    public int getShards() {
        return shards;
    }

    public void setShards(int shards) {
        this.shards = shards;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
        for (Property property : properties) {
            propertyMap.put(property.name, property);
        }
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    public List<TypeHandlerNode> getTypeHandlers() {
        return typeHandlers;
    }

    public void setTypeHandlers(List<TypeHandlerNode> typeHandlers) {
        this.typeHandlers = typeHandlers;
    }

    public List<String> getTokenizers() {
        return tokenizers;
    }

    public void setTokenizers(List<String> tokenizers) {
        this.tokenizers = tokenizers;
    }

    public Property getPropertyByName(String property) {
        return propertyMap.get(property);
    }

    public void addProperty(Property property) {
        if (property != null) {
            this.properties.add(property);
            this.propertyMap.put(property.getName(), property);
        }
    }

    /**
     * 域定义,索引相关
     */
    public static class Property {
        String name;    //字段名称
        String type;    //索引格式
        String pattern;  //type是date类型
        String typeHandler; //格式转换,将源格式转换成索引格式
        String index;   //分词方式,不索引,不分词,分词
        List<String> filters;   //过滤器
        String tokenizer;   //分词器
        String source;  //是否保留source,默认true

        public Property(String name, String type, String pattern, String typeHandler, String index, List<String> filters, String tokenizer, String source) {
            this.name = name;
            this.type = type;
            this.pattern = pattern;
            this.typeHandler = typeHandler;
            this.index = index;
            this.filters = filters;
            this.tokenizer = tokenizer;
            this.source = source;
        }

        public Property(String property) {
            this.name = property;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getPattern() {
            return pattern;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        public String getIndex() {
            return index;
        }

        public List<String> getFilters() {
            return filters;
        }

        public String getTokenizer() {
            return tokenizer;
        }

        public String getSource() {
            return source;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public void setTypeHandler(String typeHandler) {
            this.typeHandler = typeHandler;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public void setFilters(List<String> filters) {
            this.filters = filters;
        }

        public void setTokenizer(String tokenizer) {
            this.tokenizer = tokenizer;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public String toString() {
            return "Property{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", pattern='" + pattern + '\'' +
                    ", typeHandler='" + typeHandler + '\'' +
                    ", index='" + index + '\'' +
                    ", filters=" + filters +
                    ", tokenizer='" + tokenizer + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }
    }

    public static class TypeHandlerNode {
        String name;
        String clazz;

        public TypeHandlerNode(String name, String clazz) {
            this.name = name;
            this.clazz = clazz;
        }
    }

    @Override
    public String toString() {
        return "Schema{" +
                "indexName='" + indexName + '\'' +
                ", alias='" + alias + '\'' +
                ", dynamic='" + dynamic + '\'' +
                ", properties=" + properties +
                ", propertyMap=" + propertyMap +
                ", filters=" + filters +
                ", typeHandlers=" + typeHandlers +
                ", tokenizers=" + tokenizers +
                '}';
    }
}
