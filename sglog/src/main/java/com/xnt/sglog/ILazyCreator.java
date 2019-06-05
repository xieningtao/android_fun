package com.xnt.sglog;

public abstract class ILazyCreator<T> {
    private volatile T instance;

    public T getInstance() {
        if (instance == null) {
            synchronized(ILazyCreator.class) {
                if (instance == null) {
                    instance = create();
                }
            }
        }

        return instance;
    }

    protected abstract T create();
}
