package com.finpro.twogoods.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Customer extends BaseEntity{

	@Column(name = "full_name", nullable = false)
	private  String fullName;

	@Column(nullable = false)
	private String address;
	@Column(name = "birth_date", nullable = false)
	private LocalDateTime birthDate;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user;


//	helper
//=------------------==> student Response
}
