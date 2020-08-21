/*
 * Copyright 2020 SpotBugs plugin contributors
 *
 * This file is part of IntelliJ SpotBugs plugin.
 *
 * IntelliJ SpotBugs plugin is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IntelliJ SpotBugs plugin is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IntelliJ SpotBugs plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.jetbrains.plugins.spotbugs.common.util;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;

public class GetElementAtLineTest extends JavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.configureByFile("ClassStartsWithAccessModifier.java");
    }

    public void testGetClassStartsWithAccessModifier() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 0);
        assertNotNull(element);
        assertEquals("ClassStartsWithAccessModifier", element.getText());
    }

    public void testGetClassStartsWithoutAccessModifier() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 37);
        assertNotNull(element);
        assertEquals("ClassStartsWithoutAccessModifier", element.getText());
    }

    public void testGetClassStartsWithStatic() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 2);
        assertNotNull(element);
        assertEquals("ClassStartsWithStatic", element.getText());
    }

    public void testGetClassWithAnnotation() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 6);
        assertNotNull(element);
        assertEquals("ClassWithAnnotation", element.getText());
    }

    public void testGetMethodStartsWithAccessModifier() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 9);
        assertNotNull(element);
        assertEquals("methodStartsWithAccessModifier", element.getText());
    }

    public void testGetMethodStartsWithReturnType() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 12);
        assertNotNull(element);
        assertEquals("methodStartsWithReturnType", element.getText());
    }

    public void testGetMethodStartsWithStatic() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 15);
        assertNotNull(element);
        assertEquals("methodStartsWithStatic", element.getText());
    }

    public void testGetMethodWithAnnotation() {
        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 19);
        assertNotNull(element);
        assertEquals("methodWithAnnotation", element.getText());
    }

//    public void testGetMethodWithSingleLineComment() {
//        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 23);
//        assertNotNull(element);
//        assertEquals("methodWithSingleLineComment", element.getText());
//    }
//
//    public void testGetMethodWithMultiLineComment() {
//        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 29);
//        assertNotNull(element);
//        assertEquals("methodWithMultiLineComment", element.getText());
//    }
//
//    public void testGetMethodWithDocComment() {
//        PsiElement element = IdeaUtilImpl.getElementAtLine(myFixture.getFile(), 33);
//        assertNotNull(element);
//        assertEquals("methodWithDocComment", element.getText());
//    }
}
