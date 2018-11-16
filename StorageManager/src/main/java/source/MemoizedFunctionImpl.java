package source;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MemoizedFunctionImpl<T, R> implements MemoizedFunction<T, R> {

    Function<T, R> function;
    private ConcurrentHashMap<T, LazyValue<R>> values = new ConcurrentHashMap<>();

    MemoizedFunctionImpl(Function<T, R> function) {
        this.function = function;
    }


    @Override
    public R apply(T argument) throws RecursiveComputationException, InterruptedException {
        return values.computeIfAbsent(argument, (T t) -> new LazyValueImpl<>(() -> function.apply(t))).get();
    }

    @Override
    public boolean isComputedAt(Object argument) {
        return values.containsKey(argument) && values.get(argument).isReady();
    }
}
