package com.finpro.twogoods.entity;

import com.finpro.twogoods.model.response.RegisterResponse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Customer extends BaseEntity{

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user;


    public RegisterResponse toRegisterResponse() {
        return RegisterResponse.builder()
                .email(user.getEmail())
                .fullName(user.getName())
                .email(user.getEmail())
                .userId(user.getId())
                               .build();
    }
}
