package com.cn.jvm.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DistanceUtil {
    /**
     * 判断是否在电子围栏范围内
     */
    public static void main(String[] args) {
        String ll = "[{\"latitude\":39.996409,\"longitude\":116.65859978034769},{\"latitude\":39.975533937517106,\"longitude\":116.65774301908326},{\"latitude\":39.95485991329739,\"longitude\":116.65518098636927},{\"latitude\":39.93458602949566,\"longitude\":116.65093835598134},{\"latitude\":39.914907534695764,\"longitude\":116.64505598676934},{\"latitude\":39.89601394355998,\"longitude\":116.6375905291644},{\"latitude\":39.87808721169923,\"longitude\":116.62861387960432},{\"latitude\":39.86129998334109,\"longitude\":116.61821248813149},{\"latitude\":39.845813928671475,\"longitude\":116.60648652583176},{\"latitude\":39.831778186862394,\"longitude\":116.59354892013215},{\"latitude\":39.81932792978019,\"longitude\":116.57952426724796},{\"latitude\":39.80858306020655,\"longitude\":116.56454763225312},{\"latitude\":39.79964705710923,\"longitude\":116.54876324832969},{\"latitude\":39.79260597908298,\"longitude\":116.53232312772347},{\"latitude\":39.787527635558206,\"longitude\":116.515385597783},{\"latitude\":39.784460933759185,\"longitude\":116.49811377618057},{\"latitude\":39.78343540770066,\"longitude\":116.480674},{\"latitude\":39.784460933759185,\"longitude\":116.46323422381941},{\"latitude\":39.787527635558206,\"longitude\":116.44596240221699},{\"latitude\":39.79260597908298,\"longitude\":116.42902487227651},{\"latitude\":39.79964705710923,\"longitude\":116.4125847516703},{\"latitude\":39.80858306020655,\"longitude\":116.39680036774686},{\"latitude\":39.81932792978019,\"longitude\":116.38182373275203},{\"latitude\":39.831778186862394,\"longitude\":116.36779907986784},{\"latitude\":39.845813928671475,\"longitude\":116.35486147416823},{\"latitude\":39.86129998334109,\"longitude\":116.3431355118685},{\"latitude\":39.87808721169923,\"longitude\":116.33273412039567},{\"latitude\":39.89601394355998,\"longitude\":116.32375747083559},{\"latitude\":39.914907534695764,\"longitude\":116.31629201323065},{\"latitude\":39.93458602949566,\"longitude\":116.31040964401865},{\"latitude\":39.95485991329739,\"longitude\":116.30616701363071},{\"latitude\":39.975533937517106,\"longitude\":116.30360498091673},{\"latitude\":39.996409,\"longitude\":116.3027482196523},{\"latitude\":40.017284062482894,\"longitude\":116.30360498091673},{\"latitude\":40.03795808670261,\"longitude\":116.30616701363071},{\"latitude\":40.05823197050434,\"longitude\":116.31040964401865},{\"latitude\":40.077910465304235,\"longitude\":116.31629201323065},{\"latitude\":40.09680405644002,\"longitude\":116.32375747083559},{\"latitude\":40.11473078830077,\"longitude\":116.33273412039567},{\"latitude\":40.13151801665891,\"longitude\":116.3431355118685},{\"latitude\":40.147004071328524,\"longitude\":116.35486147416823},{\"latitude\":40.161039813137606,\"longitude\":116.36779907986784},{\"latitude\":40.17349007021981,\"longitude\":116.38182373275203},{\"latitude\":40.18423493979345,\"longitude\":116.39680036774686},{\"latitude\":40.19317094289077,\"longitude\":116.4125847516703},{\"latitude\":40.20021202091702,\"longitude\":116.42902487227651},{\"latitude\":40.205290364441794,\"longitude\":116.44596240221699},{\"latitude\":40.208357066240815,\"longitude\":116.46323422381941},{\"latitude\":40.20938259229934,\"longitude\":116.480674},{\"latitude\":40.208357066240815,\"longitude\":116.49811377618057},{\"latitude\":40.205290364441794,\"longitude\":116.515385597783},{\"latitude\":40.20021202091702,\"longitude\":116.53232312772347},{\"latitude\":40.19317094289077,\"longitude\":116.54876324832969},{\"latitude\":40.18423493979345,\"longitude\":116.56454763225312},{\"latitude\":40.17349007021981,\"longitude\":116.57952426724796},{\"latitude\":40.161039813137606,\"longitude\":116.59354892013215},{\"latitude\":40.147004071328524,\"longitude\":116.60648652583176},{\"latitude\":40.13151801665891,\"longitude\":116.61821248813149},{\"latitude\":40.11473078830077,\"longitude\":116.62861387960432},{\"latitude\":40.09680405644002,\"longitude\":116.6375905291644},{\"latitude\":40.077910465304235,\"longitude\":116.64505598676934},{\"latitude\":40.05823197050434,\"longitude\":116.65093835598134},{\"latitude\":40.03795808670261,\"longitude\":116.65518098636927},{\"latitude\":40.017284062482894,\"longitude\":116.65774301908326},{\"latitude\":39.996409,\"longitude\":116.65859978034769}]";
        Point111 point111 = new Point111();
        point111.setLat(39.99646);
        point111.setLon(116.48155);
        List<Point111> pts = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(ll);
        jsonArray.stream().map(d -> JSON.parseObject(d.toString())).forEach(object -> {
            Point111 p = new Point111();
            p.setLat(object.getDouble("latitude"));
            p.setLon(object.getDouble("longitude"));
            pts.add(p);
        });
        final boolean inPolygon = isInPolygon(point111, pts);
        System.out.println("是否在电子围栏内：" + inPolygon);


    }


    /**
     * 判断点是否在多边形内
     *
     * @param point111 测试点
     * @param pts   多边形的点
     * @return boolean
     */
    public static boolean isInPolygon(Point111 point111, List<Point111> pts) {

        if (pts.isEmpty()) {
            return false;
        }
        if (Objects.isNull(point111)) {
            return false;
        }
        Point2D.Double pom = new Point2D.Double();
        int N = pts.size();
        boolean boundOrVertex = true;
        //交叉点数量
        int intersectCount = 0;
        //浮点类型计算时候与0比较时候的容差
        double precision = 2e-10;
        //临近顶点
        Point111 p1, p2;
        //当前点
        Point111 p = point111;

        p1 = pts.get(0);
        for (int i = 1; i <= N; ++i) {
            if (p.equals(p1)) {
                return boundOrVertex;
            }

            p2 = pts.get(i % N);
            if (p.getLon() < Math.min(p1.getLon(), p2.getLon()) || p.getLon() > Math.max(p1.getLon(), p2.getLon())) {
                p1 = p2;
                continue;
            }

            //射线穿过算法
            if (p.getLon() > Math.min(p1.getLon(), p2.getLon()) && p.getLon() < Math.max(p1.getLon(), p2.getLon())) {
                if (p.getLat() <= Math.max(p1.getLat(), p2.getLat())) {
                    if (p1.getLon() == p2.getLon() && p.getLat() >= Math.min(p1.getLat(), p2.getLat())) {
                        return boundOrVertex;
                    }

                    if (p1.getLat() == p2.getLat()) {
                        if (p1.getLat() == p.getLat()) {
                            return boundOrVertex;
                        } else {
                            ++intersectCount;
                        }
                    } else {
                        double xinters = (p.getLon() - p1.getLon()) * (p2.getLat() - p1.getLat()) / (p2.getLon() - p1.getLon()) + p1.getLat();
                        if (Math.abs(p.getLat() - xinters) < precision) {
                            return boundOrVertex;
                        }

                        if (p.getLat() < xinters) {
                            ++intersectCount;
                        }
                    }
                }
            } else {
                if (p.getLon() == p2.getLon() && p.getLat() <= p2.getLat()) {
                    Point111 p3 = pts.get((i + 1) % N);
                    if (p.getLon() >= Math.min(p1.getLon(), p3.getLon()) && p.getLon() <= Math.max(p1.getLon(), p3.getLon())) {
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;
        }
        //偶数在多边形外
        if (intersectCount % 2 == 0) {
            return false;
        }//奇数在多边形内
        else {
            return true;
        }
    }
}
