package com.carad.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarDto {
    private String id;
    private String region;
    private String price;
    private String year;
    private String manufacturer;
    private String model;
    private String fuel;
    private String transmission;
    private String type;
    private String paint_color;
    private String description;
    private String posting_date;
    private String phone;

}