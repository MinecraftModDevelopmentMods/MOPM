package zed.mopm.systems.vfs;

import com.google.gson.*;
import zed.mopm.util.JsonLiterals;

import java.util.ArrayList;
import java.util.List;

public class Directory {

	/**
	 * <p>Contains the general information and description
	 * details of this directory.</p>
	 */
	private final DirectoryInfo directoryInfo;

	/**
	 * <p>The sub-directories contained under the parent directory, this directory.
	 * The parent directory is always the calling instance.</p>
	 */
	private final List<Directory> directories;

	/**
	 * <p>A file in the terminology of this program
	 * is a World, Server, etc. Files are just something
	 * a folder holds.</p>
	 */
	private final List<TreeEntry> files;

	public Directory(final DirectoryInfo info) {
		this.directories = new DirectoryList();
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
	 * Creates a new directory and adds it to this directory.
	 * @param name The name of the new directory.
	 * @return Returns the new directory.
	 */
	public Directory createDirectory(final String name) {
		int depth = this.directoryInfo.depth + 1;
		DirectoryInfo directory = new DirectoryInfo(
			this.directoryInfo.getRootSystem(),
			this,
			name,
			depth,
			-1
		);
		this.directories.add(new Directory(directory));
		return this.directories.get(directories.size() - 1);
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
		if (!this.directoryInfo.getRootSystem().isAncestorOf(this, toDirectory)
			&& this.directoryInfo.parentDirectory.removeDirectory(this) != null
		) {
			this.directoryInfo.depth = toDirectory.directoryInfo.depth + 1;
			this.directoryInfo.parentDirectory = toDirectory;
			toDirectory.directories.add(this);
			return true;
		}
		return false;
	}

	/**
	 * Constructs a visual that represents a tree structure starting from this node.
	 * The tree displays the names of subsequent nodes and the order of which they appear
	 * in the subsequent tree.
	 * @return Returns a string that visualizes the tree starting at this node.
	 */
	public String toVisualTree() {
		final StringBuilder treeVisual = new StringBuilder();
		this.toVisualTree(treeVisual, "", true);
		return treeVisual.toString();
	}

	/**
	 * Helper method to {@link Directory#toVisualTree()}. This method is responsible
	 * for the tree construction logic.
	 * @param treeVisual The builder that is storing the tree visual.
	 * @param indent The level in which nodes are displayed on.
	 * @param isLast Indicator if a node is the last of it's parent.
	 */
	private void toVisualTree(final StringBuilder treeVisual, String indent, final boolean isLast) {
		treeVisual.append(indent);
		if (isLast) {
			treeVisual.append("\u2514\u2500 ");
			indent += "   ";
		}
		else {
			treeVisual.append("\u251C\u2500 ");
			indent += "\u2502  ";
		}
		treeVisual.append(this.directoryInfo.getUniqueName()).append("\n");

		for (final Directory directory : this.directories) {
			boolean last = directory.directoryInfo.index == this.directories.size() - 1;
			directory.toVisualTree(treeVisual, indent, last);
		}
	}

	public String toJsonTree() {
		final JsonObject jsonTree = new JsonObject();
		this.toJsonTree(jsonTree);
		return new GsonBuilder().setPrettyPrinting().create().toJson(jsonTree);
	}

	private void toJsonTree(final JsonObject jsonTree) {
		jsonTree.addProperty(JsonLiterals.DIR_NAME, this.directoryInfo.name);
		final JsonArray childList = new JsonArray();
		for (final Directory directory : this.directories) {
			childList.add(new JsonObject());
			directory.toJsonTree(childList.get(directory.directoryInfo.index).getAsJsonObject());
		}
		jsonTree.add(JsonLiterals.DIR_CHILDREN, childList);
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
