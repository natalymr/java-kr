import org.junit.Test;
import source.LazyValue;
import source.RecursiveComputationException;
import util.CountingSupplier;
import util.InvocationCountingSupplier;

import static org.junit.Assert.fail;

public class LazyValueTests extends AbstractLazyValueTest {

    @Test
    public void testTrivialGet() throws Throwable {
        doTest(new CountingSupplier<>(42), 42);
    }

    @Test
    public void testNullReturn() throws Throwable {
        doTest(new CountingSupplier<>(() -> null), null);
    }

    @Test
    public void testThrowingSupplier() throws Throwable {
        RuntimeException fail = new RuntimeException("Fail");
        InvocationCountingSupplier<Object> supplier = new CountingSupplier<>(() -> {
            throw fail;
        });
        LazyValue<Object> lazyValue = storageManager.createLazyValue(supplier);
        stressTestLazyValue(lazyValue, supplier, fail);
    }

    @Test(expected = RecursiveComputationException.class)
    public void testRecursionWithoutHandler() throws Throwable {
        doTestWithRecursiveSupplier(null, null);
        fail("Computation didn't threw");
    }

    @Test
    public void testRecursionWithHandler() throws Throwable {
        doTestWithRecursiveSupplier(42, new CountingSupplier<>(() -> 42));
    }

    @Test
    public void testRecursionWithNullReturningHandler() throws Throwable {
        doTestWithRecursiveSupplier(null, new CountingSupplier<>(() -> null));
    }

    @Test
    public void testRecursionWithThrowingHandler() throws Throwable {
        RuntimeException runtimeException = new RuntimeException();
            doTestWithRecursiveSupplier(runtimeException, new CountingSupplier<>(() -> {
            throw runtimeException;
        }));
    }
}