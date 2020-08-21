public class NPETestData {
    public static boolean foo(int i) {
        <warning descr="SpotBugs: Check for oddness that won't work for negative numbers
The code uses x % 2 == 1 to check to see if a value is odd, but this won't work for negative numbers (e.g., (-5) % 2 == -1). If this code is intending to check for oddness, consider usi
ng x & 1 == 1, or x % 2 != 0.">return i % 2 == 1;</warning>
    }
    public static void main(String[] args) {
        String s = null;
        <warning descr="SpotBugs: Null pointer dereference
A null pointer is dereferenced here. This will lead to a NullPointerException when the code is executed.">s.length();</warning>
    }

    @Override
    protected void finalize() throws Throwable {
        <warning descr="SpotBugs: Finalizer does nothing but call superclass finalizer
The only thing this finalize() method does is call the superclass's finalize() method, making it redundant. Delete it.">super.finalize();</warning>
    }
}
