package com.presidio.rentify.dto.AuthDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {

    @NotBlank(message = "New Password is Required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{6,}$",
            message = "Password Must Be at least 6 Characters long and include at least one UPPERCASE letter, one lowercase letter, one Number, and one Special Character")
    private String newPassword;

    @NotBlank(message = "Repeat Password is Required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{6,}$",
            message = "Password Must Be at least 6 Characters long and include at least one UPPERCASE letter, one lowercase letter, one Number, and one Special Character")
    private String repeatPassword;
}
