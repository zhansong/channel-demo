package com.ks3.bean.elasticsearch;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ZHANSONG on 2017/2/15.
 */
@Data
public class User implements Serializable{

    private Integer id;
    private String name;
    private String message;
    private Date postDate;
}