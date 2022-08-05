package com.yazukov.shop.entity;

import com.yazukov.shop.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String model;
    private String description;
    private String photoUri;
    private BigDecimal price;
    private Long qty;
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "products")
    private Set<Order> orders;

    public Product(ProductDto productDto){
        this.id = productDto.getId();
        this.name = productDto.getName();
        this.model = productDto.getModel();
        this.description = productDto.getDescription();
        this.photoUri = productDto.getPhotoUri();
        this.price = productDto.getPrice();
        this.qty = productDto.getQty();
    }
}
