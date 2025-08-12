package com.codegym.projectmodule5.dto.request.housedto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterHouseRequest {
    private String location;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy = "id"; // id, price, title
    private String sortDirection = "asc"; // asc, desc
    private Integer page = 0;
    private Integer size = 10;
}