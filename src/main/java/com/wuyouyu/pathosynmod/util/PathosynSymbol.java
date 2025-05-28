package com.wuyouyu.pathosynmod.util;

public class PathosynSymbol {
        // 基本参数
        public final int frameIndex;
        public final float r, g, b, a;

        // 精灵图参数
        public static final int RUNE_WIDTH = 4;
        public static final int RUNE_HEIGHT = 7;
        public static final int SHEET_WIDTH = 64;
        public static final int SHEET_HEIGHT = 64;
        public static final int X_OFFSET = 4;  // 左空
        public static final int Y_OFFSET = 7;  // 上空
        public static final int COLUMNS = (SHEET_WIDTH - X_OFFSET) / RUNE_WIDTH;

        // 构造：直接带帧和颜色
        public PathosynSymbol(int frameIndex, float r, float g, float b, float a) {
            this.frameIndex = frameIndex;
            this.r = r; this.g = g; this.b = b; this.a = a;
        }

        // 获取UV的方法
        public static UVRect getUV(int index) {
            int col = index % COLUMNS;
            int row = index / COLUMNS;
            float u0 = (X_OFFSET + col * RUNE_WIDTH) / (float) SHEET_WIDTH;
            float v0 = (Y_OFFSET + row * RUNE_HEIGHT) / (float) SHEET_HEIGHT;
            float u1 = (X_OFFSET + (col + 1) * RUNE_WIDTH) / (float) SHEET_WIDTH;
            float v1 = (Y_OFFSET + (row + 1) * RUNE_HEIGHT) / (float) SHEET_HEIGHT;
            return new UVRect(u0, v0, u1, v1);
        }

        public UVRect getUV() {
            return getUV(this.frameIndex);
        }

        // 存储UV坐标
        public static class UVRect {
            public final float u0, v0, u1, v1;
            public UVRect(float u0, float v0, float u1, float v1) {
                this.u0 = u0; this.v0 = v0; this.u1 = u1; this.v1 = v1;
            }
        }

}
