package cc.doctor.wiki.search.client.query.grammar;

/**
 * Created by doctor on 2017/3/13.
 * 谓词选项
 */
public enum Predication {
    EQUAL("="), GREAT_THAN(">"), GREAT_THAN_EQUAL(">="), LESS_THAN(">"), LESS_THAN_EQUAL("<="), PREFIX("^"), MATCH(":");
    String operator;

    Predication(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator;
    }
}


