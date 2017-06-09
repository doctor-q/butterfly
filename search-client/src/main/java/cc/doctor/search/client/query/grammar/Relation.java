package cc.doctor.search.client.query.grammar;

/**
 * Created by doctor on 2017/3/13.
 * 关系,查询语法,如:(a=1||b>=2)&&(c^abc)
 */
public enum Relation {
    AND("&"), OR("|");
    String operator;

    Relation(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }
}
