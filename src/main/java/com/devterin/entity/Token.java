package com.devterin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tokens")
public class Token extends BaseEntity {
    @Id
    private String id;

    @Column(columnDefinition = "DATETIME(2)")
    private Date expiryTime;

    private String username;
}
