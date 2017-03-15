package cc.doctor.wiki.search.client.index.schema.typehandlers;

/**
 * Created by doctor on 2017/3/3.
 */
public interface TypeHandler<F, T> {
    T transfer(F from);
}
