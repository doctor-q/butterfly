package cc.doctor.wiki.search.server.query;

import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.server.index.store.shard.ShardService;
import cc.doctor.wiki.search.server.query.grammar.GrammarParser;

import java.util.HashMap;
import java.util.Map;

import static cc.doctor.wiki.search.server.query.grammar.GrammarParser.grammarParser;

/**
 * Created by doctor on 2017/3/14.
 * 索引查询,每个索引配置一个,与IndexManagerInner对等
 */
public class SearcherInner {
    private String indexName;
    private Map<Integer, ShardService> shardServiceMap = new HashMap<>();

    public void query(QueryBuilder queryBuilder) {
        GrammarParser.GrammarNode grammarNode = grammarParser.parseQuery(queryBuilder.toString());
        if (grammarNode instanceof GrammarParser.QueryNode) {
            query((GrammarParser.QueryNode) grammarNode);
        } else {
            query((GrammarParser.ConnectNode) grammarNode);
        }
    }

    public void query(GrammarParser.ConnectNode connectNode) {
        if (connectNode.isAllQueryNode()) {

        }
    }

    public void query(GrammarParser.QueryNode queryNode) {

    }
}
