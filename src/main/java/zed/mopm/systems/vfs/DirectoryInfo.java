package zed.mopm.systems.vfs;

import net.minecraftforge.fml.common.Mod;
import zed.mopm.util.ModLiterals;

import java.util.Stack;

public class DirectoryInfo {

	/**
	 * A reference to the directory tree this directory information is apart of.
	 * Used to reference root directory.
	 */
	private DirectoryTree rootSystem;

	/**
	 * A reference to the parent directory this directory information belongs to.
	 */
	protected Directory parentDirectory;

	/**
	 * The displayable name of the directory.
	 */
	protected String name;

	/**
	 * The depth within the tree in which this directory information can be found.
	 */
	protected int depth;

	/**
	 * The index of where this directory information appears under a parent.
	 */
	protected int index;

	public DirectoryInfo() {
		this.rootSystem = null;
		this.parentDirectory = null;
		this.name = null;
		this.depth = -1;
		this.index = -1;
	}

	protected DirectoryInfo(
		final DirectoryTree rootSystem,
		final Directory parentDirectory,
		final String name,
		final int depth,
		final int index
	) {
		this.rootSystem = rootSystem;
		this.parentDirectory = parentDirectory;
		this.name = name;
		this.depth = depth;
		this.index = index;
	}

	/**
	 * Returns a reference to the directory system this belongs to.
	 * @return The root system this directory belongs to.
	 */
	public DirectoryTree getRootSystem() {
		return this.rootSystem;
	}

	public Directory getParentDirectory() {
		return this.parentDirectory;
	}

	public String getName() {
		return name;
	}

	/**
	 * The referable name of the directory. It is the combination of 'name # index'.
	 * The unique name is used to distinguish between two directories of the same name.
	 */
	public String getUniqueName() {
		return new StringBuilder(name).append(ModLiterals.NAME_DELIM).append(index).toString();
	}

	public int getDepth() {
		return depth;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * Constructs the textual path to this directory.
	 * @param isUnique Indicator to use the unique name of directories.
	 * @return Returns the path to this directory.
	 */
	public String getPath(final boolean isUnique) {
		final Stack<String> path = new Stack<>();
		if (isUnique) {
			path.push(this.getUniqueName());
		}
		else {
			path.push(this.name);
		}

		for (Directory directory = this.parentDirectory;
			 directory != null;
			 directory = directory.getDirectoryInfo().parentDirectory
		) {
			path.push(Character.toString(ModLiterals.PATH_DELIM));
			if (isUnique) {
				path.push(directory.getDirectoryInfo().getUniqueName());
			}
			else {
				path.push(directory.getDirectoryInfo().name);
			}
		}

		final StringBuilder builder = new StringBuilder();
		while (!path.isEmpty()) {
			builder.append(path.pop());
		}
		return builder.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		else if (!(o instanceof DirectoryInfo)) {
			return false;
		}
		else {
			final DirectoryInfo obj = (DirectoryInfo) o;
			return this.parentDirectory == obj.parentDirectory
				&& this.getUniqueName().equals(obj.getUniqueName());
		}
	}
}
