package org.twodividedbyzero.idea.findbugs.actions;

public final class AnalyzeProjectFilesNotIncludingTests extends AnalyzeProjectFiles {
    AnalyzeProjectFilesNotIncludingTests() {
        super(false);
    }
}
