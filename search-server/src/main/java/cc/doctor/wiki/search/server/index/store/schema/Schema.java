package cc.doctor.wiki.search.server.index.store.schema;

import java.util.List;

/**
 * Created by doctor on 2017/3/3.
 */
public class Schema {
    String dynamic;  //是否自动探测格式
    List<Property> properties;
    class Property {
        String type;    //索引格式
        String format;  //type是date类型
        String typeHandler; //格式转换,将源格式转换成索引格式
        String index;   //分词方式,不索引,不分词,分词
        List<String> filters;   //过滤器
        String tokenizer;   //分词器
        String source;  //是否保留source,默认true
    }
}
