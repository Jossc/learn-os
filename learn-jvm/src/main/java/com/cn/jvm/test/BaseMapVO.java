package com.cn.jvm.test;

import lombok.Data;

/**
 * @ClassName BaseMapVO
 * @Description
 * @Author zhaoyafei
 * @Date 2019/9/20 9:14
 * @Version 1.0
 */
@Data
public class BaseMapVO {

    private Double longitude;

    private Double latitude;

    private Double distanceOrderBy;

    private String distance;

    private Integer uniqueId;
}
