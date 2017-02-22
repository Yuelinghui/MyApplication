package com.yuelinghui.personal.widget.recyclerview;

import java.util.List;

/**
 * Created by yuelinghui on 17/2/22.
 */

public interface IDataHandler<T extends Model.ItemData> {
    void add(T model);

    void add(T model, int index);

    void add(List<T> models);

    void add(List<T> models, int index);

    void remove(T model);

    void remove(int index, int count);

    void remove(int index);

    void update(int index);

    void update(T model, int index);

    void setDatas(List<T> models);

    void clear();

    int getPosition(T model);
}
