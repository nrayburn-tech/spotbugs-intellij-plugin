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

package org.jetbrains.plugins.spotbugs.core;


import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.NewVirtualFile;
import com.intellij.util.Function;
import com.intellij.util.containers.CollectionFactory;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Set;


/**
 * Based on {@link com.intellij.compiler.impl.TranslatingCompilerFilesMonitor} of IC-140.2285.5.
 *
 * $Date: 2015-02-14 16:52:01 +0100 (Sa, 14 Feb 2015) $
 *
 * @author $Author: reto.merz@gmail.com $
 * @version $Revision: 343 $
 * @since 0.9.995
 */
final class ChangeCollector implements VirtualFileListener {


	ChangeCollector() {
	}


	@Override
	public void propertyChanged(@NotNull final VirtualFilePropertyEvent event) {
		if (VirtualFile.PROP_NAME.equals(event.getPropertyName())) {
			final VirtualFile eventFile = event.getFile();
			if (isInContentOfOpenedProject(eventFile)) {
				collectPathsAndNotify(eventFile, NOTIFY_CHANGED);
			}
		}
	}


	@Override
	public void contentsChanged(@NotNull final VirtualFileEvent event) {
		collectPathsAndNotify(event.getFile(), NOTIFY_CHANGED);
	}


	@Override
	public void fileCreated(@NotNull final VirtualFileEvent event) {
		collectPathsAndNotify(event.getFile(), NOTIFY_CHANGED);
	}


	@Override
	public void fileCopied(@NotNull final VirtualFileCopyEvent event) {
		collectPathsAndNotify(event.getFile(), NOTIFY_CHANGED);
	}


	@Override
	public void fileMoved(@NotNull VirtualFileMoveEvent event) {
		collectPathsAndNotify(event.getFile(), NOTIFY_CHANGED);
	}


	private interface FileProcessor {
		void execute(VirtualFile file);
	}


	private static void processRecursively(final VirtualFile fromFile, final boolean dbOnly, final FileProcessor processor) {
		if (!(fromFile.getFileSystem() instanceof LocalFileSystem)) {
			return;
		}

		VfsUtilCore.visitChildrenRecursively(fromFile, new VirtualFileVisitor<VirtualFile>() {
			@NotNull @Override
			public Result visitFileEx(@NotNull VirtualFile file) {
				if (isIgnoredByBuild(file)) {
					return SKIP_CHILDREN;
				}

				if (!file.isDirectory()) {
					processor.execute(file);
				}
				return CONTINUE;
			}

			@Nullable
			@Override
			public Iterable<VirtualFile> getChildrenIterable(@NotNull VirtualFile file) {
				if (dbOnly) {
					return file.isDirectory()? ((NewVirtualFile)file).iterInDbChildren() : null;
				}
				if (file.equals(fromFile) || !file.isDirectory()) {
					return null; // skipping additional checks for the initial file and non-directory files
				}
				// optimization: for all files that are not under content of currently opened projects iterate over DB children
				return isInContentOfOpenedProject(file)? null : ((NewVirtualFile)file).iterInDbChildren();
			}
		});
	}


	private static boolean isInContentOfOpenedProject(@NotNull final VirtualFile file) {
		// probably need a read action to ensure that the project was not disposed during the iteration over the project list
		for (Project project : ProjectManager.getInstance().getOpenProjects()) {
			if (!project.isInitialized()) {
				continue;
			}
			if (ProjectRootManager.getInstance(project).getFileIndex().isInContent(file)) {
				return true;
			}
		}
		return false;
	}


	private static final Function<Collection<File>, Void> NOTIFY_CHANGED = files -> {
		notifyFilesChanged(files);
		return null;
	};


	private static void collectPathsAndNotify(final VirtualFile file, final Function<Collection<File>, Void> notification) {
		final Set<String> pathsToMark = CollectionFactory.createFilePathSet();
		if (!isIgnoredOrUnderIgnoredDirectory(file)) {
			final boolean inContent = isInContentOfOpenedProject(file);
			processRecursively(file, !inContent, file1 -> pathsToMark.add(file1.getPath()));
		}
		if (!pathsToMark.isEmpty()) {
			notification.fun(pathsToMark.stream().map(File::new).collect(Collectors.toSet()));
		}
	}


	private static boolean isIgnoredOrUnderIgnoredDirectory(final VirtualFile file) {
		if (isIgnoredByBuild(file)) {
			return true;
		}
		final FileTypeManager fileTypeManager = FileTypeManager.getInstance();
		VirtualFile current = file.getParent();
		while (current != null) {
			if (fileTypeManager.isFileIgnored(current)) {
				return true;
			}
			current = current.getParent();
		}
		return false;
	}


	private static boolean isIgnoredByBuild(VirtualFile file) {
		return
				FileTypeManager.getInstance().isFileIgnored(file) ||
						ProjectUtil.isProjectOrWorkspaceFile( file )        ||
						FileUtil.isAncestor( PathManager.getConfigPath(), file.getPath(), false); // is config file
	}


	private static void notifyFilesChanged(Collection<File> paths) {
		if (!paths.isEmpty()) {
			Changes.INSTANCE.addChanged( paths );
		}
	}
}
