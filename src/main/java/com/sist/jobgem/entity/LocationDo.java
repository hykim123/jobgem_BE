package com.sist.jobgem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "location_do")
public class LocationDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ld_idx", nullable = false)
    private Integer id;

    @Size(max = 5)
    @Column(name = "ld_name", length = 5)
    private String ldName;

}