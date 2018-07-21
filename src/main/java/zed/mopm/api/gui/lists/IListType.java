package zed.mopm.api.gui.lists;

import java.util.List;

public interface IListType<K> {
    void display(List<K> entries);
}
