package source;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class LazyValueImpl<R> implements LazyValue<R> {

    final Lock lock = new ReentrantLock();

    R field = null;
    Supplier<R> supplier;
    boolean isReady = false;
    boolean computedSuccessfully = false;
    RuntimeException error;

    LazyValueImpl(Supplier<R> supplier) {
        this.supplier = supplier;
    }

    @Override
    public R get() throws RecursiveComputationException, InterruptedException {

        if (!isReady) {
            lock.lock();
            if (!isReady) {
                try {
                    field = supplier.get();

                    computedSuccessfully = true;
                    isReady = true;

                    return field;

                } catch (RuntimeException e) {
                    error = e;

                    computedSuccessfully = false;
                    isReady = true;

                    throw error;

                } finally {

                    lock.unlock();
                }
            }
        }

        if (isReady && !computedSuccessfully) {
            throw error;
        }

        return field;
    }

    @Override
    public boolean isReady() {
        return isReady;
    }
}
