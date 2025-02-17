//package com.project.shopapp.user;
//
//import com.project.shopapp.entity.UserEntity;
//import com.project.shopapp.repository.UserRepostiory;
//import com.project.shopapp.service.impl.UserService;
//import lombok.Setter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles(value = "dev")
//public class UserServiceTest {
//    @Mock
//    UserRepostiory userRepostiory;
//
//    @Mock
//    PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    UserService userService;
//
//    private UserEntity user;
//
//    @BeforeEach
//    void setUp() {
//        user = new UserEntity();
//        user.setId(1L);
//        user.setEmail("duongkien@gmail.com");
//        user.setFullName("kien");
//        user.setPassword("12345");
//        user.setActive(1);
//    }
//
//    @Test
//    void testChangePassword_Success() {
//        UserEntity user = new UserEntity();
//        user.setId(1L);
//        user.setPassword("encryptedPassword");
//
//        // given
//        when(userRepostiory.findById(user.getId())).thenReturn(Optional.of(user));
//        when(this.passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
//        when(this.passwordEncoder.encode(anyString())).thenReturn("encryptedNewPassword");
//        when(this.userRepostiory.save(user)).thenReturn(user);
//
//        // when
//        this.userService.changePassword(user.getId(),
//                "unencryptedOldPassword", "123456", "123456");
//
//        // then
//        assertEquals(user.getPassword(), "encryptedNewPassword");
//        verify(this.userRepostiory, times(1)).save(user);
//    }
//}
