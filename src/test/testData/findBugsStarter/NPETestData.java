public class NPETestData {
    public static void main(String[] args) {
        String s = null;
        <warning descr="SpotBugs: Null pointer dereference
A null pointer is dereferenced here. This will lead to a NullPointerException when the code is executed.">s.length();</warning>
    }
}
