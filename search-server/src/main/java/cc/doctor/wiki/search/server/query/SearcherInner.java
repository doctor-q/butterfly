package cc.doctor.wiki.search.server.query;

import cc.doctor.wiki.search.client.query.QueryBuilder;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.search.server.index.store.shard.ShardService;
import cc.doctor.wiki.search.server.query.grammar.GrammarParser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
            //connect the two
        }
    }

    public void query(GrammarParser.QueryNode queryNode) {
        List<WordInfo> allWordInfos = new LinkedList<>();
        for (ShardService shardService : shardServiceMap.values()) {
            Iterable<WordInfo> wordInfos = shardService.searchInvertedDocs(queryNode);
            for (WordInfo wordInfo : wordInfos) {
                allWordInfos.add(wordInfo);
            }
        }
    }
}
