package com.cnielallen.uploaderservice.util;

import java.util.function.Consumer;

public class Wrappers {

    public static <T> Consumer<T> wrap (UnsafeConsumer<T> consumer) {
        return t ->  {
            try {
                consumer.accept(t);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }


    @FunctionalInterface
    public interface UnsafeConsumer<T> {
        void accept(T t) throws Exception;
    }
}
