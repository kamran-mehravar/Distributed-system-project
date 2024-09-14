package com.carad.api.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdvertisementDto {
    private String id;
    private String title;
    private String description;
    private String price;
    private String region;
    private String manufacturer;
    private String productionYear;
    private String username;
}