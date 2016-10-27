package com.yuelinghui.personal.widget.titlebar;

import java.io.Serializable;

/**
 * Created by yuelinghui on 16/10/11.
 */

public class CustomAction implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 标题（在title上直接显示）
     */
    public String menuTitle = null;
    /**
     * 标题描述（弹出菜单内展示文本）
     */
    public String menuDesc = null;
    /**
     * 图片Url
     */
    public String menuImage = null;
    /**
     * 图标本地resId
     */
    public int imgResId = 0;
    /**
     * 菜单项关联数据
     */
    public Object tag = null;
}
