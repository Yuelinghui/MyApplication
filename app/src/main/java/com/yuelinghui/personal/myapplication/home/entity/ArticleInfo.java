package com.yuelinghui.personal.myapplication.home.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class ArticleInfo implements Serializable{
    public String date;
    public List<StoriesInfo> stories;
    public List<StoriesInfo> top_stories;
}
