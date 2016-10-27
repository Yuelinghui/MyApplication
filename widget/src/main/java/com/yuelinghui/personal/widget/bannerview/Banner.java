package com.yuelinghui.personal.widget.bannerview;

import java.io.Serializable;

/**
 * Created by yuelinghui on 16/10/13.
 */

public class Banner implements Serializable{

    public int id;

    public String imageUrl;

    public String describe;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Banner) {
            Banner banner = (Banner) obj;
            if (banner.id != this.id) {
                return false;
            } else if (!banner.imageUrl.equals(this.imageUrl)) {
                return false;
            } else if (!banner.describe.equals(this.describe)) {
                return false;
            }
            return true;
        }
        return false;
    }
}
