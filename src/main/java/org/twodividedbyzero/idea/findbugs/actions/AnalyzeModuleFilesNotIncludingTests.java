package org.twodividedbyzero.idea.findbugs.actions;

public final class AnalyzeModuleFilesNotIncludingTests extends AnalyzeModuleFiles {
    public AnalyzeModuleFilesNotIncludingTests() {
        super(false);
    }
}
