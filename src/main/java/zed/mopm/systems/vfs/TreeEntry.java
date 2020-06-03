package zed.mopm.systems.vfs;

public class TreeEntry <K> {

	private K element;
	private Directory parentDirectory;

	public TreeEntry (final K element, final Directory parentDirectory) {
		this.element = element;
		this.parentDirectory = parentDirectory;
	}

	/**
	 * Returns the parent directory that contains this entry. Use the parent
	 * in order to retrieve information regarding location to this entry, or
	 * any other directory related information.
	 * @return Returns the parent directory that contains this entry.
	 */
	public Directory getParentDirectory() {
		return this.parentDirectory;
	}

	/**
	 * @return Returns the object that this tree entry contains.
	 */
	public K getElement() {
		return this.element;
	}

	/**
	 * Relocates this entry to another directory. No action will occur if the directory
	 * this entry is being moved to already contains the entry. This entry will also be removed
	 * from its previous parent if successful. If this entry never had a parent, then it is located
	 * to the directory specified.
	 * @param toDirectory The directory to relocate this entry to.
	 * @return
	 * Returns true if this entry was relocated to the specified directory. <br>
	 * Returns false if the new location already contains this entry.
	 */
	public boolean relocateEntry(final Directory toDirectory) {
		if (toDirectory.getFiles().contains(this)) {
			return false;
		}

		if (this.parentDirectory != null && this.parentDirectory.getFiles().contains(this)) {
			this.parentDirectory.getFiles().remove(this);
		}
		toDirectory.getFiles().add(this);
		this.parentDirectory = toDirectory;
		return true;
	}

	@Override
	public boolean equals(final Object o) {
		return ((TreeEntry<K>)o).element.equals(this.element);
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public String toString() {
		return this.element.toString();
	}
}
