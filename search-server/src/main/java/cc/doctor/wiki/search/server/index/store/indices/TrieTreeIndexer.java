package cc.doctor.wiki.search.server.index.store.indices;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.search.server.index.store.indices.inverted.WordInfo;
import cc.doctor.wiki.search.server.index.store.schema.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/3/3.
 * 字符串使用有trie树
 */
public class TrieTreeIndexer extends AbstractIndexer {
    private Map<Character, StateNode> stateNodeMap = new HashMap<>();

    @Override
    public void loadIndexes(Schema schema) {

    }

    @Override
    public void insertWordInner(Schema schema, String property, Object word) {
        char[] chars = word.toString().toLowerCase().toCharArray();
        StateNode stateNode = stateNodeMap.get(chars[0]);
        if (stateNode != null) {
            Tuple<Integer, StateNode> last = findLast(stateNode, chars);
            createStateList(last.getT2(), chars, last.getT1());
        } else {
            StateNode first = new StateNode();
            first.setC(chars[0]);
            stateNodeMap.put(chars[0], first);
            createStateList(first, chars, 1);
        }

    }

    //接入当前状态机
    private void createStateList(StateNode prev, char[] chars, Integer index) {
        for (int i = index; i < chars.length; i++) {
            StateNode stateNode = new StateNode();
            stateNode.setC(chars[i]);
            prev.nexts.put(chars[i], stateNode);
            prev = stateNode;
        }
        prev.setWordInfo(new WordInfo(0, new String(chars)));
    }

    /**
     * 查找状态机,找到这个图中匹配字符串的最后一个
     *
     * @param stateNode 当前状态机
     * @param chars     待匹配的字符串序列
     * @return 找到的最后一个状态机和字符串序列匹配到的位置
     */
    private Tuple<Integer, StateNode> findLast(StateNode stateNode, char[] chars) {
        int i = 0;
        if (stateNode.getC() != chars[i]) {
            return new Tuple<>(i, stateNode);
        }
        StateNode currentStateNode = stateNode;
        StateNode nextStateNode;
        while (i <= chars.length) {
            nextStateNode = currentStateNode.nexts.get(chars[++i]);
            if (nextStateNode == null) {
                break;
            }
            currentStateNode = nextStateNode;
        }
        return new Tuple<>(i, currentStateNode);
    }

    @Override
    public void deleteWord(Schema schema, String property, Object word) {

    }

    public static class StateNode {
        char c;
        Map<Character, StateNode> nexts = new HashMap<>();
        WordInfo wordInfo;  //词的结尾会挂倒排

        public char getC() {
            return c;
        }

        public void setC(char c) {
            this.c = c;
        }

        public Map<Character, StateNode> getNexts() {
            return nexts;
        }

        public void setNexts(Map<Character, StateNode> nexts) {
            this.nexts = nexts;
        }

        public WordInfo getWordInfo() {
            return wordInfo;
        }

        public void setWordInfo(WordInfo wordInfo) {
            this.wordInfo = wordInfo;
        }
    }

    public static void main(String[] args) {
        TrieTreeIndexer trieTreeIndexer = new TrieTreeIndexer();
        String[] strings = {"abcdefg", "abcrty"};
        Schema schema = new Schema();
        for (String string : strings) {
            trieTreeIndexer.insertWordInner(schema, "name", string);
        }
        System.out.println(trieTreeIndexer);
    }
}
