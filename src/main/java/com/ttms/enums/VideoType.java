package com.ttms.enums;

import lombok.Data;

public enum VideoType {
    Other(1,"其他"),
    Romance(2,"爱情"),
    Comedy(3,"喜剧"),
    Animation(4,"动画"),
    Plot(5,"剧情"),
    Horror(6,"恐怖"),
    Thriller(7,"惊悚"),
    Sci_Fi(8,"科幻"),
    Action(9,"动作"),
    Mystery(10,"悬疑"),
    Crime(11,"犯罪"),
    Adventure(12,"冒险"),
    War(13,"战争"),
    Fantasy(14,"奇幻"),
    Sports(15,"悬疑"),
    Family(16,"运动"),
    Costume(17,"古装"),
    Wuxia(18,"武侠"),
    Western(19,"西部"),
    History(20,"历史"),
    Biography(21,"传记"),
    Musical(22,"歌舞"),
    Film_Noir(23,"黑色电影"),
    Short_Film(24,"短片"),
    Documentary(25,"纪录片"),
    Drama(26,"戏曲"),
    music(27,"音乐"),
    disaster(28,"灾难"),
    youth(29,"青春"),
    children(30,"儿童");

    final int id;
    final String type;
    VideoType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
