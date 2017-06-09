package cc.doctor.search.server.query.grammar;

import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.exceptions.query.QueryGrammarException;
import cc.doctor.search.client.query.grammar.Predication;
import cc.doctor.search.client.query.grammar.Relation;

import java.util.Stack;

/**
 * Created by doctor on 2017/3/13.
 * 生成语法树
 */
public class GrammarParser {

    public static final GrammarParser grammarParser = new GrammarParser();

    private GrammarParser() {
    }

    /**
     * 将后缀查询query生成语法树
     * @param query 查询表达式
     * @return 查询节点或连接节点
     */
    public GrammarNode parseQuery(final String query) {
        Stack queryStack = new Stack();
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if (ch == ')') {
                GrammarNode grammarNode = readQueryNode(queryStack);
                if (grammarNode == null) {
                    throw new QueryGrammarException("Query grammar error.");
                }
                queryStack.push(grammarNode);
            } else if (ch == '&') {
                Tuple<GrammarNode, GrammarNode> grammarNodeTuple = readGrammarNodes(queryStack);
                ConnectNode connectNode = new ConnectNode();
                connectNode.setRelation(Relation.AND);
                connectNode.setGrammarNodeTuple(grammarNodeTuple);
                queryStack.push(connectNode);
            } else if (ch == '|') {
                Tuple<GrammarNode, GrammarNode> grammarNodeTuple = readGrammarNodes(queryStack);
                ConnectNode connectNode = new ConnectNode();
                connectNode.setRelation(Relation.OR);
                connectNode.setGrammarNodeTuple(grammarNodeTuple);
                queryStack.push(connectNode);
            } else {
                queryStack.push(String.valueOf(ch));
            }
        }
        return Optimizer.optimizer.optimize((GrammarNode) queryStack.pop());
    }

    private Tuple<GrammarNode, GrammarNode> readGrammarNodes(Stack queryStack) {
        GrammarNode grammarNode1 = (GrammarNode) queryStack.pop();
        GrammarNode grammarNode2 = (GrammarNode) queryStack.pop();
        return new Tuple<>(grammarNode1, grammarNode2);
    }

    private GrammarNode readQueryNode(Stack queryStack) {
        Object pop = queryStack.pop();
        if (pop instanceof GrammarNode) {
            queryStack.pop();
            return (GrammarNode) pop;
        }
        String ch;
        StringBuilder queryStringBuilder = new StringBuilder();
        while (!(ch = (String) pop).equals("(")) {
            queryStringBuilder.append(ch);
            pop = queryStack.pop();
        }
        String queryString = queryStringBuilder.reverse().toString();
        if (queryString.contains(Predication.GREAT_THAN_EQUAL.getOperator())) {
            String[] split = queryString.split(Predication.GREAT_THAN_EQUAL.getOperator());
            return new QueryNode(split[0], split[1], Predication.GREAT_THAN_EQUAL);
        } else if (queryString.contains(Predication.LESS_THAN_EQUAL.getOperator())) {
            String[] split = queryString.split(Predication.LESS_THAN_EQUAL.getOperator());
            return new QueryNode(split[0], split[1], Predication.LESS_THAN_EQUAL);
        } else if (queryString.contains(Predication.GREAT_THAN.getOperator())) {
            String[] split = queryString.split(Predication.GREAT_THAN.getOperator());
            return new QueryNode(split[0], split[1], Predication.GREAT_THAN);
        } else if (queryString.contains(Predication.LESS_THAN.getOperator())) {
            String[] split = queryString.split(Predication.LESS_THAN.getOperator());
            return new QueryNode(split[0], split[1], Predication.LESS_THAN);
        } else if (queryString.contains(Predication.EQUAL.getOperator())) {
            String[] split = queryString.split(Predication.EQUAL.getOperator());
            return new QueryNode(split[0], split[1], Predication.EQUAL);
        } else if (queryString.contains(Predication.PREFIX.getOperator())) {
            String[] split = queryString.split("\\" + Predication.PREFIX.getOperator());
            return new QueryNode(split[0], split[1], Predication.PREFIX);
        } else if (queryString.contains(Predication.MATCH.getOperator())) {
            String[] split = queryString.split(Predication.MATCH.getOperator());
            return new QueryNode(split[0], split[1], Predication.MATCH);
        }
        return null;
    }

    public interface GrammarNode {

    }

    public class ConnectNode implements GrammarNode {
        private Relation relation;
        private Tuple<GrammarNode, GrammarNode> grammarNodeTuple;

        public Relation getRelation() {
            return relation;
        }

        public void setRelation(Relation relation) {
            this.relation = relation;
        }

        public Tuple<GrammarNode, GrammarNode> getGrammarNodeTuple() {
            return grammarNodeTuple;
        }

        public void setGrammarNodeTuple(Tuple<GrammarNode, GrammarNode> grammarNodeTuple) {
            this.grammarNodeTuple = grammarNodeTuple;
        }

        public boolean isAllQueryNode() {
            return (grammarNodeTuple.getT1() instanceof QueryNode) && (grammarNodeTuple.getT2() instanceof QueryNode);
        }
    }

    public class QueryNode implements GrammarNode {
        private String field;
        private String value;
        private Predication predication;

        public QueryNode(String field, String value, Predication predication) {
            this.field = field;
            this.value = value;
            this.predication = predication;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Predication getPredication() {
            return predication;
        }

        public void setPredication(Predication predication) {
            this.predication = predication;
        }
    }

    public static void main(String[] args) {
        GrammarNode grammarNode = grammarParser.parseQuery("(((a=1)(b=2)&)(c:3)|)(d^abc)&");
        System.out.println(grammarNode);
    }
}
