package com.xiaojuchefu.prism.monitor;

public class PrismConstants {

    public class Event {

        public final static int TOUCH = 0;
        public final static int BACK = 1;
        public final static int BACKGROUND = 2;
        public final static int FOREGROUND = 3;
        public final static int DIALOG_SHOW = 4;
        public final static int DIALOG_CLOSE = 5;
        public final static int ACTIVITY_START = 6;

    }

    /**
     * 总结：
     *
     * android操作行为标识 = window信息(w) + viewId信息(vi)+ 响应链信息(vp) + 列表信息(vl) + 区位信息(vq) + 参考信息(vr) + 功能信息(vf 0.0.4版本是没有实现)
     *
     *
     * 操作行为指令格式说明:
     * https://github.com/didi/DiDiPrism/blob/master/Doc/%E6%93%8D%E4%BD%9C%E8%A1%8C%E4%B8%BA%E6%8C%87%E4%BB%A4/%E6%93%8D%E4%BD%9C%E8%A1%8C%E4%B8%BA%E6%8C%87%E4%BB%A4%E6%A0%BC%E5%BC%8F%E8%AF%B4%E6%98%8E.md
     *
     * 参考：
     * https://github.com/didi/DiDiPrism/blob/master/Android/README.md
     */
    public class Symbol {

        public static final String DIVIDER = "_^_"; //连接符 例如 window信息(w) _^_ 响应链信息(vp)
        public static final String DIVIDER_INNER = "_&_"; //相当于等于号 例如 w_&_com.xiaojuchefu.prism.TestActivity_&_1

        public static final String WINDOW = "w";

        public static final String VIEW_ID = "vi"; // view id
        public static final String VIEW_REFERENCE = "vr"; //参考信息 : view RepresentativeContent [l_image] 代表本地图片 [r_image] 代表网络图片

        public static final String VIEW_QUADRANT = "vq"; //区位信息

        public static final String VIEW_LIST = "vl"; //列表信息 ，vl_&_l:4,2 标识该列表是 父view的 第三个孩子，别点击的item的position是4

        public static final String VIEW_PATH = "vp"; // view 的 路径，路径不一定是全路径 ，只包含主要节点 ，基本准则就是 只要通过 vp 能找到对应 view就行

        public static final String WEB_URL = "wu";

        public static final String VIEW_TAG = "vf";

        public static final String ACTIVITY_NAME = "an";

    }

}
