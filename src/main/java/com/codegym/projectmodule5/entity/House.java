package com.codegym.projectmodule5.entity;

import com.codegym.projectmodule5.entity.Booking;
import com.codegym.projectmodule5.entity.Image;
import com.codegym.projectmodule5.entity.Review;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.HouseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "houses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;
    private String address;

    @Enumerated(EnumType.STRING)
    private HouseStatus status;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "house")
    private List<Image> images;

    @OneToMany(mappedBy = "house")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "house")
    private List<Review> reviews;

    public List<String> getImageUrls() {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
    }
}