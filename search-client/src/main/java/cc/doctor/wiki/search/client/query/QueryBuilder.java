package cc.doctor.wiki.search.client.query;

import cc.doctor.wiki.common.Range;
import cc.doctor.wiki.search.client.query.grammar.Predication;
import cc.doctor.wiki.search.client.query.grammar.Relation;

import java.io.Serializable;

/**
 * Created by doctor on 2017/3/14.
 */
public class QueryBuilder implements Serializable{
    private static final long serialVersionUID = 4180277648048134627L;
    private StringBuilder query;

    public QueryBuilder(StringBuilder query) {
        this.query = query;
    }

    public static QueryBuilder queryBuilder() {
        return new QueryBuilder(new StringBuilder());
    }

    public static QueryBuilder eq(String field, Object value) {
        return new QueryBuilder(new StringBuilder("(" + field + Predication.EQUAL + value + ")"));
    }

    public static QueryBuilder gt(String field, Object value) {
        return new QueryBuilder(new StringBuilder("(" + field + Predication.GREAT_THAN + value + ")"));
    }

    public static QueryBuilder lt(String field, Object value) {
        return new QueryBuilder(new StringBuilder("(" + field + Predication.LESS_THAN + value + ")"));
    }

    public static QueryBuilder gte(String field, Object value) {
        return new QueryBuilder(new StringBuilder("(" + field + Predication.GREAT_THAN_EQUAL + value + ")"));
    }

    public static QueryBuilder lte(String field, Object value) {
        return new QueryBuilder(new StringBuilder("(" + field + Predication.LESS_THAN_EQUAL + value + ")"));
    }

    public static QueryBuilder range(String field, Range range) {
        QueryBuilder left = null, right = null;
        if (range.getLeft() != null) {
            if (range.isLeftClose()) {
                left = gte(field, range.getLeft());
            } else {
                left = gt(field, range.getLeft());
            }
        }

        if (range.getRight() != null) {
            if (range.isLeftClose()) {
                right = lte(field, range.getRight());
            } else {
                right = lt(field, range.getRight());
            }
        }
        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return new QueryBuilder(new StringBuilder("(" + left.and(right) + ")"));
        }
    }

    public static QueryBuilder prefix(String field, String prefix) {
        return new QueryBuilder(new StringBuilder("(" + field + Predication.PREFIX + prefix + ")"));
    }

    public static QueryBuilder match(String field, String prefix) {
        return new QueryBuilder(new StringBuilder("(" + field + Predication.MATCH + prefix + ")"));
    }

    public QueryBuilder and(QueryBuilder queryBuilder) {
        query.append(queryBuilder.query).append(Relation.AND);
        return this;
    }

    public QueryBuilder or(QueryBuilder queryBuilder) {
        query.append(queryBuilder.query).append(Relation.OR);
        return this;
    }

    @Override
    public String toString() {
        return query.toString();
    }

    public static void main(String[] args) {
        QueryBuilder queryBuilder = QueryBuilder.range("a", new Range<Integer>(1, 10)).and(QueryBuilder.prefix("b", "abc"));
        System.out.println(queryBuilder);
    }
}
