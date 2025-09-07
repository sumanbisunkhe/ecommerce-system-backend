package com.example.ecommerce.dto;

import com.example.ecommerce.enums.Gender;
import com.example.ecommerce.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDto {


    @NotBlank(message = "Username is required")
//    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;


    @NotBlank(message = "Email is required")
//    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name is required")
//    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    //    @Size(max = 50, message = "Middle name cannot exceed 50 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
//    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Country is required")
    private String country;


}
