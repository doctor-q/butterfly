package cc.doctor.wiki.search.server.index.store.indices.indexer;

import cc.doctor.wiki.common.Range;
import cc.doctor.wiki.exceptions.query.QueryException;
import cc.doctor.wiki.search.client.index.schema.Schema;
import cc.doctor.wiki.search.server.index.store.indices.indexer.datastruct.TrieTree;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.utils.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by doctor on 2017/3/3.
 * 字符串使用有trie树
 */
public class TrieTreeIndexer extends AbstractIndexer {
    Map<String, TrieTree<WordInfo>> fieldTrieTree = new HashMap<>();

    private TrieTree<WordInfo> getOrCreateTree(String field) {
        TrieTree<WordInfo> trieTree = fieldTrieTree.get(field);
        if (trieTree == null) {
            trieTree = new TrieTree<>();
            fieldTrieTree.put(field, trieTree);
        }
        return trieTree;
    }
    @Override
    public void insertWordInner(Long docId, String field, Object value) {
        TrieTree<WordInfo> trieTree = getOrCreateTree(field);
        trieTree.insertWord(value.toString(), new WordInfo(new WordInfo.InvertedNode(null, 0, 0)));
    }

    @Override
    public void deleteWord(Schema schema, String property, Object word) {

    }

    @Override
    public WordInfo getWordInfoInner(String field, Object value) {
        if (field == null || value == null) {
            return null;
        }
        TrieTree<WordInfo> trieTree = fieldTrieTree.get(field);
        if (trieTree == null) {
            return null;
        }
        TrieTree.TreeNode<WordInfo> node = trieTree.getNode(value.toString());
        return node == null ? null : node.getData();
    }

    @Override
    public List<WordInfo> getWordInfoGreatThanInner(String field, Object value) {
        throw new QueryException("String cannot compare.");
    }

    @Override
    public List<WordInfo> getWordInfoGreatThanEqualInner(String field, Object value) {
        throw new QueryException("String cannot compare.");
    }

    @Override
    public List<WordInfo> getWordInfoLessThanInner(String field, Object value) {
        throw new QueryException("String cannot compare.");
    }

    @Override
    public List<WordInfo> getWordInfoLessThanEqualInner(String field, Object value) {
        throw new QueryException("String cannot compare.");
    }

    @Override
    public List<WordInfo> getWordInfoRangeInner(String field, Range range) {
        throw new QueryException("String cannot compare.");
    }

    @Override
    public List<WordInfo> getWordInfoPrefixInner(String field, Object value) {
        if (field == null || value == null) {
            return null;
        }
        TrieTree<WordInfo> trieTree = fieldTrieTree.get(field);
        if (trieTree == null) {
            return null;
        }
        return CollectionUtils.transfer(trieTree.getPrefixDataNodes(value.toString()), new CollectionUtils.Function<TrieTree.TreeNode<WordInfo>, WordInfo>() {

            @Override
            public WordInfo transfer(TrieTree.TreeNode<WordInfo> from) {
                return from.getData();
            }
        });
    }

    @Override
    public List<WordInfo> getWordInfoMatchInner(String field, Object value) {
        return null;
    }
}
