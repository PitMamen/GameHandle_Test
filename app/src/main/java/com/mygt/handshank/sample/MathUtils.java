package com.mygt.handshank.sample;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by chengkai on 2017/1/11.
 */
public class MathUtils {
    public MathUtils() {
    }

    public static int getDistance(PointF a, PointF b) {
        return getDistance(a.x, a.y, b.x, b.y);
    }

    public static int getDistance(float x1, float y1, float x2, float y2) {
        float lenX = x1 - x2;
        float lenY = y1 - y2;
        return (int)Math.sqrt(lenX * lenX + lenY * lenY);
    }

    public static Point getPointByCutLength(Point A, Point B, int cutLength) {
        float radian = getRadian(A, B);
        return new Point(A.x + (int)((double)cutLength * Math.cos((double)radian)), A.y + (int)((double)cutLength * Math.sin((double)radian)));
    }

    public static float getRadian(Point A, Point B) {
        float lenA = (float)(B.x - A.x);
        float lenB = (float)(B.y - A.y);
        float lenC = (float)Math.sqrt((double)(lenA * lenA + lenB * lenB));
        float radian = (float)Math.acos((double)(lenA / lenC));
        radian *= (float)(B.y < A.y?-1:1);
        return radian;
    }

    public static double angle2Radian(double angle) {
        return angle / 180.0D * 3.141592653589793D;
    }

    public static double radian2Angle(double radian) {
        return radian / 3.141592653589793D * 180.0D;
    }
}
