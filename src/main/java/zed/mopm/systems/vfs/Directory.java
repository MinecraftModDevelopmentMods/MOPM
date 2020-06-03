package zed.mopm.systems.vfs;

import java.util.ArrayList;
import java.util.List;

public class Directory {

	/**
	 * Contains the general information and description
	 * details of this directory.
	 */
	private final DirectoryInfo directoryInfo;

	/**
	 * The sub-directories contained under the parent directory, this directory.
	 * The parent directory is always the calling instance.
	 */
	private final List<Directory> directories;

	/**
	 * A file in the terminology of this program
	 * is a World, Server, etc. Files are just something
	 * a folder holds.
	 */
	private final List<TreeEntry> files;

	public Directory(final DirectoryInfo info) {
		this.directories = new ArrayList<>();
		this.files = new ArrayList<>();
		this.directoryInfo = info;
	}

	/**
	 * Returns the basic directory information of this directory.
	 * @return The directory information of this directory.
	 */
	public DirectoryInfo getDirectoryInfo() {
		return this.directoryInfo;
	}

	/**
	 * Returns a list of all the files this directory contains.
	 * @return A list of files.
	 */
	public List<TreeEntry> getFiles() {
		return this.files;
	}

	/**
	 * Returns a list of all of the sub-directories this directory contains.
	 * @return A list of directories.
	 */
	protected List<Directory> getSubDirectories() {
		return this.directories;
	}

	/**
	 * Indicator of if this directory is a leaf within the file system.
	 * A directory is a leaf if it has no children.
	 * @return
	 * Returns true if this directory does not have children. <br>
	 * Returns false if this directory has children.
	 */
	public boolean isLeaf() {
		return this.directories.isEmpty();
	}

	/**
	 * Creates a directory to this directory.
	 * @param name The name of the new directory.
	 */
	public void createDirectory(final String name) {
		int depth = this.directoryInfo.depth + 1;
		int index = this.directories.size();
		DirectoryInfo directory = new DirectoryInfo(
			this.directoryInfo.getRootSystem(),
			this,
			name,
			depth,
			index
		);
		directories.add(new Directory(directory));
	}

	/**
	 * Removes a directory instance from this directory.
	 * @param directory The directory instance to remove from this directory.
	 * @return
	 * Returns the instance if the directory was successfully removes. <br>
	 * Returns null if the instance is not a child of this directory.
	 */
	public Directory removeDirectory(final Directory directory) {
		if (this.directories.contains(directory)) {
			return this.removeDirectory(directory.directoryInfo.index);
		}
		return null;
	}

	/**
	 * Removes a directory at specified index from this directory.
	 * @param index The index of the directory to be removed from this directory.
	 * @return Returns the instance of the directory that was removed.
	 */
	public Directory removeDirectory(final int index) {
		for (int i = index + 1; i < this.directories.size(); i++) {
			this.directories.get(i).directoryInfo.index -= 1;
		}
		final Directory removedDirectory = this.directories.remove(index);
		final DirectoryTree directorySystem = this.directoryInfo.getRootSystem();
		directorySystem.relocateAllEntries(removedDirectory, directorySystem.getRootDirectory());
		return removedDirectory;
	}

	/**
	 * Renames this directory.
	 * @param name The new name of this directory.
	 * @return Returns the old name of the directory.
	 */
	public String renameDirectory(final String name) {
		final String oldName = this.directoryInfo.name;
		this.directoryInfo.name = name;
		return oldName;
	}

	/**
	 * Renames a subdirectory under this directory.
	 * @param directory The directory under this to be renamed.
	 * @param name The new name of the directory.
	 * @return Returns the directory that got renamed.
	 */
	public Directory renameDirectory(final Directory directory, final String name) {
		if (this.directories.contains(directory)) {
			return this.renameDirectory(directory.directoryInfo.index, name);
		}
		return null;
	}

	/**
	 * Renames a subdirectory under this directory.
	 * @param index The index of the directory in this to be renamed.
	 * @param name The new name of the directory.
	 * @return Returns the directory that got renamed.
	 */
	public Directory renameDirectory(final int index, final String name) {
		final Directory directory = this.directories.get(index);
		directory.directoryInfo.name = name;
		return directory;
	}

	/**
	 * Moves this directory to another directory. All subsequent directories and
	 * entries are moved along with this directory. The new parent of this directory
	 * is passed to this method.
	 * @param toDirectory The directory that will be designated as the parent of this
	 *                    directory.
	 * @return
	 * Returns true if the directory was successfully moves. <br>
	 * Returns false if this directory cannot be found in its parent's set of directories.
	 */
	public boolean relocateDirectory(final Directory toDirectory) {
		if (this.directoryInfo.parentDirectory.removeDirectory(this) != null) {
			this.directoryInfo.depth = toDirectory.directoryInfo.depth + 1;
			this.directoryInfo.index = toDirectory.directories.size();
			toDirectory.directories.add(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		else if (!(o instanceof Directory)) {
			return false;
		}
		else {
			final Directory obj = (Directory) o;
			return this.directoryInfo.equals(obj.directoryInfo);
		}
	}
}
