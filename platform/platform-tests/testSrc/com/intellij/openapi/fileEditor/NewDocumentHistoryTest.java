// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.fileEditor;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.fileEditor.impl.IdeDocumentHistoryImpl;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.PlatformTestUtil;

/**
 * @author Dmitry Avdeev
 */
public class NewDocumentHistoryTest extends HeavyFileEditorManagerTestCase {
  private IdeDocumentHistoryImpl myHistory;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myHistory = new IdeDocumentHistoryImpl(getProject(), myManager);
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      Disposer.dispose(myHistory);
    }
    finally {
      myHistory = null;
      super.tearDown();
    }
  }

  public void testBackNavigationBetweenEditors() {
    PlatformTestUtil.registerExtension(FileEditorProvider.EP_FILE_EDITOR_PROVIDER, new FileEditorManagerTest.MyFileEditorProvider(), myFixture.getTestRootDisposable());
    VirtualFile file = getFile("/src/1.txt");
    assertNotNull(file);
    FileEditor[] editors = myManager.openFile(file, true);
    assertEquals(2, editors.length);
    assertEquals("Text", myManager.getSelectedEditor(file).getName());
    myManager.setSelectedEditor(file, "mock");
    assertEquals("mockEditor", myManager.getSelectedEditor(file).getName());
    myManager.closeAllFiles();

    myHistory.back();
    assertEquals("mockEditor", myManager.getSelectedEditor(file).getName());
  }

  public void testSelectFileOnNavigation() {
    VirtualFile file1 = getFile("/src/1.txt");
    myManager.openFile(file1, true);
    VirtualFile file2 = getFile("/src/2.txt");
    myManager.openFile(file2, true);
    NavigationUtil.activateFileWithPsiElement(PsiManager.getInstance(getProject()).findFile(file1));
    VirtualFile[] files = myManager.getSelectedFiles();
    assertEquals(1, files.length);
    assertEquals("1.txt", files[0].getName());
  }
}
