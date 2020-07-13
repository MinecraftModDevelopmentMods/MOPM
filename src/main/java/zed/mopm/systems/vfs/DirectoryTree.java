package zed.mopm.systems.vfs;

import zed.mopm.util.ModLiterals;
import zed.mopm.util.logger.PrintUtils;

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

	public Directory getDirectoryByIndices(final int... indices)
		throws IndexOutOfBoundsException {
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

	public boolean stepBack() {
		if (this.currentDirectory.equals(this.rootDirectory)) {
			return false;
		}
		this.currentDirectory =
			this.currentDirectory.getDirectoryInfo().parentDirectory;
		return true;
	}

	public Directory createAndStepInto(final String name) {
		this.currentDirectory = this.currentDirectory.createDirectory(name);
		return this.currentDirectory;
	}

	public boolean swapDirectories(final Directory swap, final Directory with) {
		if (this.isAncestorOf(swap, with) || this.isAncestorOf(with, swap)) {
			return false;
		}

		final DirectoryInfo swapInfo = swap.getDirectoryInfo();
		final DirectoryInfo withInfo = with.getDirectoryInfo();
		final int withIndex = withInfo.index;
		swapInfo.parentDirectory.getSubDirectories().set(swapInfo.index, with);
		withInfo.parentDirectory.getSubDirectories().set(withIndex, swap);
		return true;
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

	public static void main(final String[] args) {
		DirectoryTree tree = new DirectoryTree();
		Directory d11 = tree.rootDirectory.createDirectory("1:1");
		Directory d12 = tree.rootDirectory.createDirectory("1:2");
		Directory d13 = tree.rootDirectory.createDirectory("1:3");
		Directory d21 = d12.createDirectory("2:1");
		Directory d31 = d21.createDirectory("3:1");
		Directory d32 = d21.createDirectory("3:2");
		Directory d33 = d21.createDirectory("3:3");
		Directory d22 = d12.createDirectory("2:2");
		Directory d311 = d22.createDirectory("3:1:1");
		Directory d322 = d22.createDirectory("3:2:2");
		Directory d333 = d22.createDirectory("3:3:3");
		Directory d344 = d22.createDirectory("3:4:4");
		Directory d41 = d333.createDirectory("4:1");
		Directory d51 = d41.createDirectory("5:1");
		Directory d61 = d51.createDirectory("6:1");
		Directory d71 = d61.createDirectory("7:1");
		Directory d81 = d71.createDirectory("8:1");
		Directory d82 = d71.createDirectory("8:2");

		System.out.println("Printing JSON: ");
		PrintUtils.println(tree.rootDirectory.toJsonTree(), "UTF8");

		System.out.println("Initial: ");
		PrintUtils.println(tree.rootDirectory.toVisualTree(), "UTF8");

		System.out.println("Relocating 2:2 under 3:3: ");
		d22.relocateDirectory(d33);
		PrintUtils.println(tree.rootDirectory.toVisualTree(), "UTF8");

		System.out.println("Same parent swap 1:1 w/ 1:2: ");
		tree.swapDirectories(d11, d12);
		PrintUtils.println(tree.rootDirectory.toVisualTree(), "UTF8");

		System.out.println("Cross tree swap 1:3 w/ 8:2: ");
		tree.swapDirectories(d13, d82);
		PrintUtils.println(tree.rootDirectory.toVisualTree(), "UTF8");

		System.out.println("Ancestor swap 3:3:3 w/ 4:1: ");
		if (!tree.swapDirectories(d333, d41)) {
			System.out.println("Unable to swap: 3:3:3 is an ancestor of 4:1");
		}
		if (!tree.swapDirectories(d41, d333)) {
			System.out.println("Unable to swap: 3:3:3 is an ancestor of 4:1");
		}
		PrintUtils.println(tree.rootDirectory.toVisualTree(), "UTF8");

		System.out.println("Renaming 4:1 to HELLO WORLD!: ");
		d41.renameDirectory("HELLO WORLD!");
		PrintUtils.println(tree.rootDirectory.toVisualTree(), "UTF8");

		System.out.println("Path to root: " + tree.rootDirectory.getDirectoryInfo().getPath(true));
		System.out.println("Path to Hello World: " + d41.getDirectoryInfo().getPath(true));
	}
}
