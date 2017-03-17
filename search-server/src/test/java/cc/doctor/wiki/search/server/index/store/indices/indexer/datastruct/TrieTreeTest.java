package cc.doctor.wiki.search.server.index.store.indices.indexer.datastruct;

import org.junit.Test;

import java.util.List;

/**
 * Created by doctor on 2017/3/17.
 * abcd acd abd bcde bd cfd
 */
public class TrieTreeTest {
    TrieTree<Integer> trieTree = new TrieTree<>();
    @Test
    public void insertWord() throws Exception {
        String[] words = new String[] {"abcd", "acd", "abd", "bcde", "bd", "cfd"};
        for (int i = 0; i < words.length; i++) {
            trieTree.insertWord(words[i], i);
        }
        System.out.println(trieTree);
    }

    @Test
    public void getNode() throws Exception {
        insertWord();
        TrieTree.TreeNode abd = trieTree.getNode("abd");
        System.out.println(abd.getData());
        TrieTree.TreeNode ab = trieTree.getNode("ab");
        System.out.println(ab.getData());
        TrieTree.TreeNode bce = trieTree.getNode("bce");
        System.out.println(bce);
    }

    @Test
    public void getPrefixDataNodes() throws Exception {
        insertWord();
        List<TrieTree.TreeNode<Integer>> nodes = trieTree.getPrefixDataNodes("a");   //0,1,2
        for (TrieTree.TreeNode node : nodes) {
            System.out.println(node.getData());
        }
        nodes = trieTree.getPrefixDataNodes("ab");   //0,2
        for (TrieTree.TreeNode node : nodes) {
            System.out.println(node.getData());
        }
        nodes = trieTree.getPrefixDataNodes("ad");   //no
        for (TrieTree.TreeNode node : nodes) {
            System.out.println(node.getData());
        }
    }

}