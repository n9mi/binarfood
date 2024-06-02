package com.synergy.binarfood.model.product;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class ProductResponse {
    String id;
    String name;
    double price;
}
