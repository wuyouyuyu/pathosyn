package com.wuyouyu.pathosynmod.client.particle;

public class ParticleFrameData {
    /** 指定精灵图的帧起点与尺寸（像素） */
    public static record FrameRect(int x, int y, int width, int height) {}

    // 法阵符文帧表
    public static final FrameRect[] HEALING_FRAMES = {
            new FrameRect(1, 1, 4, 7),
            new FrameRect(7, 1, 4, 7),
            new FrameRect(13, 1, 4, 7),
            // ...
    };

    // 批量自动生成带间隔帧表的静态方法
    public static FrameRect[] generateUniformFrames(
            int frames, int startX, int startY, int width, int height, int gapX, int gapY, int perRow) {
        FrameRect[] rects = new FrameRect[frames];
        for (int i = 0; i < frames; i++) {
            int row = i / perRow;
            int col = i % perRow;
            int x = startX + col * (width + gapX);
            int y = startY + row * (height + gapY);
            rects[i] = new FrameRect(x, y, width, height);
        }
        return rects;
    }


    public static final FrameRect[] RUNE_GRID_FRAMES;
    static {
        RUNE_GRID_FRAMES = generateUniformFrames(
                7,    // 总帧数
                1, 1, // 起点
                4, 7, // 宽高
                2, 0, // 间隔（横向2像素，纵向0）
                7     // 每行7帧
        );
    }

    // 禁止实例化
    private ParticleFrameData() {}
}