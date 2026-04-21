package com.bassem.bsn.auth;

import com.bassem.bsn.common.CommonResponse;
import com.bassem.bsn.email.EmailService;
import com.bassem.bsn.email.EmailTemplateName;
import com.bassem.bsn.exception.OperationNotPermittedException;
import com.bassem.bsn.role.RoleRepository;
import com.bassem.bsn.security.JwtService;
import com.bassem.bsn.user.Token;
import com.bassem.bsn.user.TokenRepository;
import com.bassem.bsn.user.User;
import com.bassem.bsn.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url}")
    private String ActivationUrl;

    public CommonResponse<String> register(@Valid RegistrationRequest request) throws MessagingException {
        var userRole=roleRepository.findByName("USER").orElseThrow(()->new IllegalStateException("User role not found"));
        if (!emailService.validateEmail(request.getEmail())) {
            throw new OperationNotPermittedException("Registration failed. Your email is not valid.");
        }
        var user= User.builder().
                roles(List.of(userRole)).
                firstname(request.getFirstName()).
                lastname(request.getLastName()).
                email(request.getEmail()).
                accountLocked(false).
                enabled(false).
                password(passwordEncoder.encode(request.getPassword())).
                build();
        userRepository.save(user);
        sendValidationEmail(user);
        return  CommonResponse.<String>builder()
                .data("Registration successful.")
                .build();
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var token =generateAndSaveActivationToken(user);
        emailService.sendEmail(user.getEmail(),
                user.fullname(),
                EmailTemplateName.ACTIVATION_ACCOUNT,
                ActivationUrl,token,
                "Account activation");
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationToken(6);
        var token= Token.builder().
                token(generatedToken).
                createdAt(LocalDateTime.now()).
                expiresAt(LocalDateTime.now().plusMinutes(60)).
                user(user).
                build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationToken(int length) {
        String characters="123456789";
        StringBuilder tokenBuilder=new StringBuilder();
        SecureRandom random=new SecureRandom();
        for (int i = 0; i < length; i++) {
            tokenBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return tokenBuilder.toString();
    }

    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        var auth=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var claims=new HashMap<String,Object>();
        var user=(User)auth.getPrincipal();
        claims.put("full name",user.fullname());
        var jwtToken= jwtService.generateToken(user,claims);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken=tokenRepository.findByToken(token)
                //todo exception has to be defined
                .orElseThrow(()->new RuntimeException("Token Not Found"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Token Expired.A new token has been sent to the same email address");
        }
        var user=userRepository.findById(savedToken.getUser().getId()).orElseThrow(()->new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
