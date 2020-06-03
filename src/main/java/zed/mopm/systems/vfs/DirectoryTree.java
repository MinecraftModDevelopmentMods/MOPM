package zed.mopm.systems.vfs;

import zed.mopm.util.ModLiterals;

public class DirectoryTree {
	private Directory rootDirectory;
	private Directory currentDirectory;

	/**
	 * Creates a new virtual file system with a root directory called root.
	 */
	public DirectoryTree() {
		DirectoryInfo info = new DirectoryInfo(
			this,
			null,
			ModLiterals.ROOT_NAME,
			0,
			0
		);
		this.rootDirectory = new Directory(info);
		this.currentDirectory = this.rootDirectory;
	}

	/**
	 * Returns the root directory of the virtual file system. The root is the
	 * base location for all virtual file systems.
	 * @return The root of the virtual file system.
	 */
	public Directory getRootDirectory() {
		return this.rootDirectory;
	}

	/**
	 * Returns the current directory as defined by the user location of use.
	 * The user location is where the user has currently navigated to within the
	 * directory tree.
	 * @return The current directory.
	 */
	public Directory getCurrentDirectory() {
		return this.currentDirectory;
	}

	// :: Todo: Get directory at path.
	public Directory getDirectoryByPath(final String uniquePath) {
		return null;
	}

	public Directory getDirectoryByIndices(final int... indices) {
		Directory ret = rootDirectory;
		for (int i : indices) {
			ret = ret.getSubDirectories().get(i);
		}
		return ret;
	}

	/**
	 * Sets the what the programmer may consider the currently active directory
	 * so it may be more easily referenced.
	 * @param directory The directory that shall be considered the new current directory.
	 */
	public void setCurrentDirectory(final Directory directory) {
		this.currentDirectory = directory;
	}

	/**
	 * Relocates all entries in the sub-tree rooted at a directory and relocates
	 * to another specified directory.
	 * @param fromDirectory The directory that will act as a root.
	 * @param toDirectory The directory to relocate all entries to.
	 */
	public void relocateAllEntries(final Directory fromDirectory, final Directory toDirectory) {
		toDirectory.getFiles().addAll(fromDirectory.getFiles());
		fromDirectory.getFiles().clear();
		for (final Directory dir : fromDirectory.getSubDirectories()) {
			this.relocateAllEntries(dir, toDirectory);
		}
	}

	/**
	 * Checks if some directory is the ancestor of another directory.
	 * A directory is an ancestor if the descendant can trace its lineage to the ancestor.
	 * @param ancestor A directory that may or may not be the ancestor to some directory.
	 * @param descendant A directory that may or may not be the descendant of the ancestor.
	 * @return
	 * Returns true if the ancestor directory is the ancestor to the descendant directory. <br>
	 * Returns false if the descendant directory does not belong to the ancestor directory.
	 */
	public boolean isAncestorOf(final Directory ancestor, final Directory descendant) {
		for (Directory dir = descendant; dir != null; dir = dir.getDirectoryInfo().parentDirectory) {
			if (dir.equals(ancestor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "";
	}

	public static void main(final String[] args) {
		DirectoryTree tree = new DirectoryTree();
		TreeEntry<String> entry = new TreeEntry<>("Test", null);
		TreeEntry<Integer> entry1 = new TreeEntry<>(10, null);
		tree.currentDirectory.createDirectory("1:1");
		tree.currentDirectory.createDirectory("1:2");
		tree.currentDirectory.createDirectory("1:3");
		tree.currentDirectory.createDirectory("1:4");
		tree.setCurrentDirectory(tree.currentDirectory.getSubDirectories().get(1));
		tree.currentDirectory.createDirectory("2:1");
		tree.currentDirectory.createDirectory("2:2");
		tree.currentDirectory.createDirectory("2:3");
		tree.currentDirectory.createDirectory("2:4");
		tree.setCurrentDirectory(tree.currentDirectory.getSubDirectories().get(2));
		tree.currentDirectory.createDirectory("3:1");
		tree.currentDirectory.createDirectory("3:2");
		tree.currentDirectory.createDirectory("3:3");
		tree.currentDirectory.createDirectory("3:4");
		tree.setCurrentDirectory(tree.currentDirectory.getSubDirectories().get(3));
		tree.currentDirectory.createDirectory("FINAL!");
		System.out.println("Pathing to: " + tree.getDirectoryByIndices(1, 2, 3, 0).getDirectoryInfo().name);
		/* root
		 * ┬
		 * ├─ 1:1
		 * ├─ 1:2
		 * │  ├─ 2:1
		 * │  ├─ 2:2
		 * │  ├─ 2:3
		 * │  │  ├─ 3:1
		 * │  │  ├─ 3:2
		 * │  │  ├─ 3:3
		 * │  │  └─ 3:4
		 * │  │     └─ FINAL!
		 * │  └─ 2:4
		 * ├─ 1:3
		 * └─ 1:4
		 */
	}
}
