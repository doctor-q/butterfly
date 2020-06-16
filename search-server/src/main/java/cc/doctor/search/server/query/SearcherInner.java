package cc.doctor.search.server.query;

import cc.doctor.search.client.query.QueryBuilder;
import cc.doctor.search.client.query.grammar.Relation;
import cc.doctor.search.store.indices.inverted.InvertedTable;
import cc.doctor.search.store.indices.inverted.WordInfo;
import cc.doctor.search.server.index.shard.ShardService;
import cc.doctor.search.server.query.grammar.GrammarParser;
import cc.doctor.search.common.utils.CollectionUtils;

import java.util.*;

import static cc.doctor.search.server.query.grammar.GrammarParser.grammarParser;
import static cc.doctor.search.store.indices.inverted.merge.ForkJoinInvertedDocMerger.forkJoinInvertedDocMerger;

/**
 * Created by doctor on 2017/3/14.
 * 索引查询,每个索引配置一个,与IndexManagerInner对等
 */
public class SearcherInner {
    private String indexName;
    private Map<Integer, ShardService> shardServiceMap = new HashMap<>();

    public SearcherInner(String indexName) {
        this.indexName = indexName;
    }

    public Iterable<InvertedTable.InvertedDoc> query(QueryBuilder queryBuilder) {
        GrammarParser.GrammarNode grammarNode = grammarParser.parseQuery(queryBuilder.toString());
        Map<Integer, Iterable<WordInfo>> shardWordInfoMap;
        if (grammarNode instanceof GrammarParser.QueryNode) {
            shardWordInfoMap = query((GrammarParser.QueryNode) grammarNode);
        } else {
            shardWordInfoMap = query((GrammarParser.ConnectNode) grammarNode);
        }
        return mergeShardDocs(shardWordInfoMap);
    }

    private Iterable<InvertedTable.InvertedDoc> mergeShardDocs(Map<Integer, Iterable<WordInfo>> shardWordInfoMap) {
        List<Iterable<InvertedTable.InvertedDoc>> invertedDocsList = new LinkedList<>();
        for (Integer shard : shardWordInfoMap.keySet()) {
            Iterable<InvertedTable> invertedTables = shardServiceMap.get(shard).getInvertedTables(shardWordInfoMap.get(shard));
            for (InvertedTable invertedTable : invertedTables) {
                invertedDocsList.add(invertedTable.getInvertedDocs());
            }
        }
        return forkJoinInvertedDocMerger.merge(invertedDocsList);
    }

    public Map<Integer, Iterable<WordInfo>> query(GrammarParser.ConnectNode connectNode) {
        Relation relation = connectNode.getRelation();
        if (connectNode.isAllQueryNode()) {
            GrammarParser.QueryNode queryNode1 = (GrammarParser.QueryNode) connectNode.getGrammarNodeTuple().getT1();
            GrammarParser.QueryNode queryNode2 = (GrammarParser.QueryNode) connectNode.getGrammarNodeTuple().getT1();
            Map<Integer, Iterable<WordInfo>> shardWordInfoMap1 = query(queryNode1);
            Map<Integer, Iterable<WordInfo>> shardWordInfoMap2 = query(queryNode2);
            return connectedTwoQueryNode(relation, shardWordInfoMap1, shardWordInfoMap2);
        } else if (connectNode.getGrammarNodeTuple().getT1() instanceof GrammarParser.QueryNode){
            GrammarParser.QueryNode queryNode1 = (GrammarParser.QueryNode) connectNode.getGrammarNodeTuple().getT1();
            return connectedTwoQueryNode(relation, query(queryNode1), query((GrammarParser.ConnectNode)connectNode.getGrammarNodeTuple().getT2()));
        } else if (connectNode.getGrammarNodeTuple().getT1() instanceof GrammarParser.QueryNode){
            GrammarParser.QueryNode queryNode = (GrammarParser.QueryNode) connectNode.getGrammarNodeTuple().getT2();
            return connectedTwoQueryNode(relation, query((GrammarParser.ConnectNode)connectNode.getGrammarNodeTuple().getT1()), query(queryNode));
        } else {
            GrammarParser.ConnectNode connectNode1 = (GrammarParser.ConnectNode) connectNode.getGrammarNodeTuple().getT1();
            GrammarParser.ConnectNode connectNode2 = (GrammarParser.ConnectNode) connectNode.getGrammarNodeTuple().getT1();
            return connectedTwoQueryNode(relation, query(connectNode1), query(connectNode2));
        }
    }

    //connect(and | or) the two
    public Map<Integer, Iterable<WordInfo>> connectedTwoQueryNode(Relation relation, Map<Integer, Iterable<WordInfo>> shardWordInfoMap1, Map<Integer, Iterable<WordInfo>> shardWordInfoMap2) {
        Map<Integer, Iterable<WordInfo>> shardWordInfoMap = new LinkedHashMap<>();
        if (relation.equals(Relation.AND)) {
            Collection<Integer> commonShards = CollectionUtils.and(shardWordInfoMap1.keySet(), shardWordInfoMap2.keySet());
            for (Integer commonShard : commonShards) {
                Iterable<WordInfo> wordInfoList1 = shardWordInfoMap1.get(commonShard);
                Iterable<WordInfo> wordInfoList2 = shardWordInfoMap2.get(commonShard);
                Iterable<WordInfo> wordInfos = CollectionUtils.and(wordInfoList1, wordInfoList2);
                shardWordInfoMap.put(commonShard, wordInfos);
            }
        } else {
            Collection<Integer> commonShards = CollectionUtils.or(shardWordInfoMap1.keySet(), shardWordInfoMap2.keySet());
            for (Integer commonShard : commonShards) {
                Iterable<WordInfo> wordInfoList1 = shardWordInfoMap1.get(commonShard);
                Iterable<WordInfo> wordInfoList2 = shardWordInfoMap2.get(commonShard);
                Iterable<WordInfo> wordInfos = CollectionUtils.or(wordInfoList1, wordInfoList2);
                shardWordInfoMap.put(commonShard, wordInfos);
            }
        }
        return shardWordInfoMap;
    }

    //query wordinfo from all shards
    public Map<Integer, Iterable<WordInfo>> query(GrammarParser.QueryNode queryNode) {
        Map<Integer, Iterable<WordInfo>> shardWordInfos = new HashMap<>();
        for (ShardService shardService : shardServiceMap.values()) {
            Iterable<WordInfo> wordInfos = shardService.searchInvertedDocs(queryNode);
            if (wordInfos != null) {
                shardWordInfos.put(shardService.getShard(), wordInfos);
            }
        }
        return shardWordInfos;
    }
}
