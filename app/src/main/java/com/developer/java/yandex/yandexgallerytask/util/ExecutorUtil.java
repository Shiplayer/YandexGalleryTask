package com.developer.java.yandex.yandexgallerytask.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Anton on 30.04.2018.
 */

public class ExecutorUtil {
    public static final Executor THREAD_POOL_EXECUTOR = Executors.newCachedThreadPool();
}
