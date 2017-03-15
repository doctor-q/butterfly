package cc.doctor.wiki.search.client.rpc;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by doctor on 2017/3/15.
 */
public class ResponseFuture<T extends Serializable> implements Serializable{
    private static final long serialVersionUID = -5330839917951351693L;
    private T data;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void setData(T data) {
        this.data = data;
        countDownLatch.countDown();
    }

    public T getData() {
        return data;
    }

    public void await(long timeout) {
        try {
            countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }
}
