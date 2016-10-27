package com.yuelinghui.personal.myapplication.home.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class HomeDetailInfo implements Serializable{
    public String body;
    public String image_source;
    public String title;
    public String image;
    public String share_url;
    public int ga_prefix;
    public List<String> images;
    public int type;
    public int id;
    public List<String> css;
}
