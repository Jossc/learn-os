package com.cn.jvm.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: JTSExample
 * @Author: 一方通行
 * @Date: 2024-03-29
 * @Version:v1.0
 */
public class JTSExample {
    public static void main(String[] args) {
        GeometryFactory geometryFactory = new GeometryFactory();
        String ll = "[{\"latitude\":39.996409,\"longitude\":116.65859978034769},{\"latitude\":39.975533937517106,\"longitude\":116.65774301908326},{\"latitude\":39.95485991329739,\"longitude\":116.65518098636927},{\"latitude\":39.93458602949566,\"longitude\":116.65093835598134},{\"latitude\":39.914907534695764,\"longitude\":116.64505598676934},{\"latitude\":39.89601394355998,\"longitude\":116.6375905291644},{\"latitude\":39.87808721169923,\"longitude\":116.62861387960432},{\"latitude\":39.86129998334109,\"longitude\":116.61821248813149},{\"latitude\":39.845813928671475,\"longitude\":116.60648652583176},{\"latitude\":39.831778186862394,\"longitude\":116.59354892013215},{\"latitude\":39.81932792978019,\"longitude\":116.57952426724796},{\"latitude\":39.80858306020655,\"longitude\":116.56454763225312},{\"latitude\":39.79964705710923,\"longitude\":116.54876324832969},{\"latitude\":39.79260597908298,\"longitude\":116.53232312772347},{\"latitude\":39.787527635558206,\"longitude\":116.515385597783},{\"latitude\":39.784460933759185,\"longitude\":116.49811377618057},{\"latitude\":39.78343540770066,\"longitude\":116.480674},{\"latitude\":39.784460933759185,\"longitude\":116.46323422381941},{\"latitude\":39.787527635558206,\"longitude\":116.44596240221699},{\"latitude\":39.79260597908298,\"longitude\":116.42902487227651},{\"latitude\":39.79964705710923,\"longitude\":116.4125847516703},{\"latitude\":39.80858306020655,\"longitude\":116.39680036774686},{\"latitude\":39.81932792978019,\"longitude\":116.38182373275203},{\"latitude\":39.831778186862394,\"longitude\":116.36779907986784},{\"latitude\":39.845813928671475,\"longitude\":116.35486147416823},{\"latitude\":39.86129998334109,\"longitude\":116.3431355118685},{\"latitude\":39.87808721169923,\"longitude\":116.33273412039567},{\"latitude\":39.89601394355998,\"longitude\":116.32375747083559},{\"latitude\":39.914907534695764,\"longitude\":116.31629201323065},{\"latitude\":39.93458602949566,\"longitude\":116.31040964401865},{\"latitude\":39.95485991329739,\"longitude\":116.30616701363071},{\"latitude\":39.975533937517106,\"longitude\":116.30360498091673},{\"latitude\":39.996409,\"longitude\":116.3027482196523},{\"latitude\":40.017284062482894,\"longitude\":116.30360498091673},{\"latitude\":40.03795808670261,\"longitude\":116.30616701363071},{\"latitude\":40.05823197050434,\"longitude\":116.31040964401865},{\"latitude\":40.077910465304235,\"longitude\":116.31629201323065},{\"latitude\":40.09680405644002,\"longitude\":116.32375747083559},{\"latitude\":40.11473078830077,\"longitude\":116.33273412039567},{\"latitude\":40.13151801665891,\"longitude\":116.3431355118685},{\"latitude\":40.147004071328524,\"longitude\":116.35486147416823},{\"latitude\":40.161039813137606,\"longitude\":116.36779907986784},{\"latitude\":40.17349007021981,\"longitude\":116.38182373275203},{\"latitude\":40.18423493979345,\"longitude\":116.39680036774686},{\"latitude\":40.19317094289077,\"longitude\":116.4125847516703},{\"latitude\":40.20021202091702,\"longitude\":116.42902487227651},{\"latitude\":40.205290364441794,\"longitude\":116.44596240221699},{\"latitude\":40.208357066240815,\"longitude\":116.46323422381941},{\"latitude\":40.20938259229934,\"longitude\":116.480674},{\"latitude\":40.208357066240815,\"longitude\":116.49811377618057},{\"latitude\":40.205290364441794,\"longitude\":116.515385597783},{\"latitude\":40.20021202091702,\"longitude\":116.53232312772347},{\"latitude\":40.19317094289077,\"longitude\":116.54876324832969},{\"latitude\":40.18423493979345,\"longitude\":116.56454763225312},{\"latitude\":40.17349007021981,\"longitude\":116.57952426724796},{\"latitude\":40.161039813137606,\"longitude\":116.59354892013215},{\"latitude\":40.147004071328524,\"longitude\":116.60648652583176},{\"latitude\":40.13151801665891,\"longitude\":116.61821248813149},{\"latitude\":40.11473078830077,\"longitude\":116.62861387960432},{\"latitude\":40.09680405644002,\"longitude\":116.6375905291644},{\"latitude\":40.077910465304235,\"longitude\":116.64505598676934},{\"latitude\":40.05823197050434,\"longitude\":116.65093835598134},{\"latitude\":40.03795808670261,\"longitude\":116.65518098636927},{\"latitude\":40.017284062482894,\"longitude\":116.65774301908326},{\"latitude\":39.996409,\"longitude\":116.65859978034769}]";
        Point111 point111 = new Point111();
        point111.setLat(39.784460933759185);
        point111.setLon(116.46323422381941);
        List<Point111> pts = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(ll);
        jsonArray.stream().map(d -> JSON.parseObject(d.toString())).forEach(object -> {
            Point111 p = new Point111();
            p.setLat(object.getDouble("latitude"));
            p.setLon(object.getDouble("longitude"));
            pts.add(p);
        });

        List<Coordinate> coordinateList = new ArrayList<>();
        for (Point111 pp : pts) {
            final Coordinate coordinate = new Coordinate(pp.getLat(), pp.getLon());
            coordinateList.add(coordinate);
        }

        final Coordinate coordinate = new Coordinate(pts.get(0).getLat(), pts.get(0).getLon());
        coordinateList.add(coordinate);
        Coordinate[] array = new Coordinate[coordinateList.size()];
        for (int i = 0; i < coordinateList.size(); i++) {
            array[i] = coordinateList.get(i);
        }
        LinearRing ring = geometryFactory.createLinearRing(array);
        // 使用线性环创建多边形
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        // 创建一个点
        Point point = geometryFactory.createPoint(new Coordinate(40.077910465304235, 116.31629201323065));

        // 判断点是否在多边形内
        boolean isInPolygon = point.within(polygon);

        System.out.println("点是否在多边形内: " + isInPolygon);
    }
}
