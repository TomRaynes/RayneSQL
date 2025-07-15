package RayneSQL.database;

import java.util.concurrent.locks.Lock;

public interface CheckedRunnable {
    interface TypeRunnable<T> {
        T run() throws Exception;
    }

    interface VoidRunnable {
        void run() throws Exception;
    }

    static void executeUnderLock(Lock lock, VoidRunnable task) throws Exception {
        lock.lock();

        try {
            task.run();
        }
        finally {
            lock.unlock();
        }
    }

    static <T> T executeUnderLock(Lock lock, TypeRunnable<T> task) throws Exception {
        lock.lock();

        try {
            return task.run();
        }
        finally {
            lock.unlock();
        }
    }
}
