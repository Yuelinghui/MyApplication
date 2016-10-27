package com.yuelinghui.personal.myapplication.home.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuelinghui on 16/9/30.
 */

public class StoriesInfo implements Serializable {

    public List<String> images;
    public int type;
    public int id;
    public String ga_prefix;
    public String title;
    public String image;
    public boolean multipic;
}
