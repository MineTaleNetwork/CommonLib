package cc.minetale.commonlib.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@UtilityClass
public class AsyncUtil {

    public static void runInOrder(CompletableFuture<?>... futures) {
        CompletableFuture.runAsync(() -> {
            for(var future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
