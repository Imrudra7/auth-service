package in.maithilart.auth.service;

import in.maithilart.auth.dto.LoginRequest;
import in.maithilart.auth.dto.LoginResponse;
import in.maithilart.auth.dto.RegisterRequest;
import in.maithilart.auth.dto.RegisterResponse;
import jakarta.validation.Valid;

public interface IAuthService {

	RegisterResponse register(RegisterRequest request);

	LoginResponse login(@Valid LoginRequest request);

}
