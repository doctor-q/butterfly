package cc.doctor.wiki.search.server.index.store.indices.indexer.datastruct;

import cc.doctor.wiki.common.Tuple;

import java.io.Serializable;
import java.util.*;

/**
 * Created by doctor on 2017/3/17.
 * trie 树
 */
public class TrieTree<T> implements Serializable {
    //trie树索引,每份索引(shard)包含一个
    private Map<Character, TreeNode<T>> treeNodeMap = new HashMap<>();

    public void insertWord(String word, T data) {
        char[] chars = word.toLowerCase().toCharArray();
        TreeNode<T> treeNode = treeNodeMap.get(chars[0]);
        if (treeNode != null) {
            Tuple<Integer, TreeNode<T>> last = findLast(treeNode, chars);
            connectNodeList(last.getT2(), chars, last.getT1(), data);
        } else {
            TreeNode<T> first = new TreeNode<>();
            first.setC(chars[0]);
            treeNodeMap.put(chars[0], first);
            connectNodeList(first, chars, 1, data);
        }
    }

    /**
     * 连接当前节点
     *
     * @param prev  当前节点
     * @param chars 待连接的字符串
     * @param index 连接字符串的位置
     * @param data  连接到结尾的数据
     */
    private void connectNodeList(TreeNode<T> prev, char[] chars, Integer index, T data) {
        for (int i = index; i < chars.length; i++) {
            TreeNode<T> treeNode = new TreeNode<>();
            treeNode.setC(chars[i]);
            prev.nexts.put(chars[i], treeNode);
            prev = treeNode;
        }
        prev.setData(data);
    }

    /**
     * 查找状态机,找到这个图中匹配字符串的最后一个
     *
     * @param treeNode 当前节点
     * @param chars    待匹配的字符串序列
     * @return 找到的最后一个节点和字符串序列匹配到的位置
     */
    private Tuple<Integer, TreeNode<T>> findLast(TreeNode<T> treeNode, char[] chars) {
        int i = 0;
        TreeNode<T> currentTreeNode = treeNode;
        while (i < chars.length) {
            if (++i == chars.length) {
                i--;
                break;
            }
            TreeNode<T> nextNode = currentTreeNode.getNexts().get(chars[i]);
            if (nextNode == null) {
                break;
            }
            currentTreeNode = nextNode;
        }
        return new Tuple<>(i, currentTreeNode);
    }

    /**
     * 查询单词节点信息,返回节点,如果树的路径不能构成这个单词,返回空
     */
    public TreeNode<T> getNode(String word) {
        if (word == null) {
            return null;
        }
        char[] chars = word.toCharArray();
        TreeNode<T> root = treeNodeMap.get(chars[0]);
        if (root == null) {
            return null;
        }
        Tuple<Integer, TreeNode<T>> last = findLast(root, chars);
        if (last.getT1() == chars.length - 1 && last.getT2().getC() == chars[chars.length - 1]) {
            return last.getT2();
        }
        return null;
    }

    /**
     * 获取某个前缀对应的所有数据节点(data!=null)
     */
    public List<TreeNode<T>> getPrefixDataNodes(String prefix) {
        List<TreeNode<T>> dataNodes = new LinkedList<>();
        TreeNode<T> prefixNode = getNode(prefix);
        if (prefixNode == null) {
            return dataNodes;
        }
        getAllTreeNodes(dataNodes, prefixNode);
        Iterator<TreeNode<T>> iterator = dataNodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getData() == null) {
                iterator.remove();
            }
        }
        return dataNodes;
    }

    private void getAllTreeNodes(List<TreeNode<T>> dataNodes, TreeNode<T> root) {
        dataNodes.add(root);
        for (TreeNode<T> child : root.getNexts().values()) {
            getAllTreeNodes(dataNodes, child);
        }
    }

    public static class TreeNode<T> implements Serializable {
        private static final long serialVersionUID = 8499720875321195345L;
        char c;
        Map<Character, TreeNode<T>> nexts = new HashMap<>();
        T data;  //词的结尾会挂倒排

        public char getC() {
            return c;
        }

        public void setC(char c) {
            this.c = c;
        }

        public Map<Character, TreeNode<T>> getNexts() {
            return nexts;
        }

        public void setNexts(Map<Character, TreeNode<T>> nexts) {
            this.nexts = nexts;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
