package com.danilov.promise;

/**
 * Created by Semyon on 10.11.2014.
 */
public class Promise<Type> {

    private Type data;

    private boolean isDone = false;

    private Action<Type, Void> onFinish = null;

    public void finish(final Type data, final boolean success) {
        synchronized (this) {
            isDone = true;
        }
        this.data = data;
        if (onFinish != null) {
            onFinish.action(data);
        }
    }

    public <X> Promise<X> then(final Action<Type, X> action) {
        final Promise<X> nPromise = new Promise<X>();
        this.onFinish = new Action<Type, Void>() {
            @Override
            public Void action(final Type data) {
                X _data = action.action(data);
                nPromise.finish(_data, true);
                return null;
            }
        };
        synchronized (this) {
            if (isDone) {
                this.onFinish.action(data);
            }
        }
        return nPromise;
    }

    public static <X> Promise<X> resolve(final X data) {
        Promise<X> promise = new Promise<X>();
        promise.finish(data, true);
        return promise;
    }

    public static Promise<Object[]> all(final Promise... promises) {
        int count = promises.length;
        final Promise<Object[]> promise = new Promise<Object[]>();
        final Counter counter = new Counter();
        counter.count = count;
        final Object[] dataArray = new Object[promises.length];
        for (int i = 0; i < count; i++) {
            final int num = i;
            promises[i].then(new Action() {
                @Override
                public Object action(final Object data) {
                    dataArray[num] = data;
                    boolean shouldFinish = false;
                    synchronized (counter) {
                        counter.count--;
                        if (counter.count == 0) {
                            shouldFinish = true;
                        }
                    }
                    if (shouldFinish) {
                        promise.finish(dataArray, true);
                    }
                    return null;
                }
            });
        }
        return promise;
    }

    private static class Counter {

        public int count = 0;



    }

    public static interface Action<Type, Return> {
        public Return action(final Type data);
    }

}
