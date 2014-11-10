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

    public static interface Action<Type, Return> {
        public Return action(final Type data);
    }

}
