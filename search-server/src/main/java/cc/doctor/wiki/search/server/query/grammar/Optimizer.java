package cc.doctor.wiki.search.server.query.grammar;

/**
 * Created by doctor on 2017/3/20.
 * 语法优化器 todo
 * 1、合并两个范围成一个范围
 * 2、将前缀为空的前缀组合成match_anything查询
 */
public class Optimizer {
    public static final Optimizer optimizer = new Optimizer();
    private Optimizer() {
    }

    public GrammarParser.GrammarNode optimize(GrammarParser.GrammarNode grammarNode) {
        return grammarNode;
    }
}
