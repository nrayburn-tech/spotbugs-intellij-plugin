public class ClassStartsWithAccessModifier {

    static class ClassStartsWithStatic {
    }

    @SuppressWarnings("UnusedDeclaration")
    class ClassWithAnnotation {
    }

    public void methodStartsWithAccessModifier() {
    }

    void methodStartsWithReturnType() {
    }

    static void methodStartsWithStatic() {
    }

    @SuppressWarnings("UnusedDeclaration")
    public void methodWithAnnotation() {
    }

    // Single-line comment
    static void methodWithSingleLineComment() {
    }

    /* Multi-line
        comment
     */
    public void methodWithMultiLineComment() {
    }

    /** Doc comment */
    void methodWithDocComment() {
    }
}

class ClassStartsWithoutAccessModifier {
}