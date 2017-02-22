package com.yuelinghui.personal.myapplication.home.ui.recyclerview;

import com.yuelinghui.personal.myapplication.home.entity.StoriesInfo;
import com.yuelinghui.personal.widget.recyclerview.Model;

/**
 * Created by yuelinghui on 17/2/22.
 */

public class HomeItemData extends Model.ItemData {
    private StoriesInfo mStoriesInfo;

    public HomeItemData(StoriesInfo articleInfo) {
        mStoriesInfo = articleInfo;
    }

    public StoriesInfo getArticleInfo() {
        return mStoriesInfo;
    }

}
