package zed.mopm.systems.vfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class DirectoryList extends ArrayList<Directory> {

	public DirectoryList(final int initialCapacity) {
		super(initialCapacity);
	}

	public DirectoryList() {
		super();
	}

	public DirectoryList(final Collection c) {
		super(c);
	}

	@Override
	public Directory set(final int index, final Directory element) {
		element.getDirectoryInfo().index = index;
		return super.set(index, element);
	}

	@Override
	public boolean add(final Directory directory) {
		final int oldIndex = directory.getDirectoryInfo().index;
		directory.getDirectoryInfo().index = this.size();
		final boolean ret = super.add(directory);
		if (!ret) {
			directory.getDirectoryInfo().index = oldIndex;
		}
		return ret;
	}

	@Override
	public void add(final int index, final Directory element) {
		super.add(index, element);
		for (int i = index; i < this.size(); i++) {
			this.get(i).getDirectoryInfo().index = i;
		}
	}

	@Override
	public Directory remove(final int index) {
		final Directory directory = super.remove(index);
		for (int i = index; i < this.size(); i++) {
			this.get(i).getDirectoryInfo().index = i;
		}
		return directory;
	}

	@Override
	public boolean remove(final Object o) {
		final boolean ret = super.remove(o);
		if (ret) {
			for (int i = this.indexOf(o) + 1; i < this.size(); i++) {
				this.get(i).getDirectoryInfo().index = i - 1;
			}
		}
		return ret;
	}

	@Override
	public boolean addAll(final Collection<? extends Directory> c) {
		final int index = this.size();
		final boolean ret = super.addAll(c);
		if (ret) {
			for (int i = index; i < this.size(); i++) {
				this.get(i).getDirectoryInfo().index = i;
			}
		}
		return ret;
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends Directory> c) {
		final boolean ret = super.addAll(index, c);
		if (ret) {
			for (int i = index; i < this.size(); i++) {
				this.get(i).getDirectoryInfo().index = i;
			}
		}
		return ret;
	}

	@Override
	protected void removeRange(final int fromIndex, final int toIndex) {
		super.removeRange(fromIndex, toIndex);
		final int difference = toIndex - fromIndex;
		for (int i = fromIndex; i < this.size(); i++) {
			this.get(i).getDirectoryInfo().index = i - difference;
		}
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		final boolean ret = super.removeAll(c);
		if (ret) {
			this.ensureIndices();;
		}
		return ret;
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		final boolean ret = super.retainAll(c);
		if (ret) {
			this.ensureIndices();;
		}
		return ret;
	}

	@Override
	public boolean removeIf(final Predicate<? super Directory> filter) {
		final boolean ret = super.removeIf(filter);
		if (ret) {
			this.ensureIndices();;
		}
		return ret;
	}

	@Override
	public void replaceAll(final UnaryOperator<Directory> operator) {
		super.replaceAll(operator);
		ensureIndices();
	}

	private void ensureIndices() {
		for (int i = 0; i < this.size(); i++) {
			this.get(i).getDirectoryInfo().index = i;
		}
	}
}
