package springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import springbootdeveloper.service.UserDetailService;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailService userService;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() { // 정적 리소스 등 파일에 스프링 시큐리티 기능을 사용하지 않는
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())  // h2console
                .requestMatchers(new AntPathRequestMatcher("/static/**"));  // static 하위 파일들 
    }

    // 특정 http 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(auth -> auth  // 인증, 인가 설정
                        .requestMatchers(  // 특정 요청과 일치하는 url에 대한 액세스를 설정
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll() // 누구나 접근 가능하게 설정 (인증/인가 없이 접근 가능)
                        .anyRequest().authenticated()) // any~ 위에 설정한 url이외의  요청에 대해서 설정 + auth~ 별도의 인가는 필요하지 않지만 인증이 성공된 상태여야 접근 가능
                .formLogin(formLogin -> formLogin  // 폼 기반 로그인 설정
                        .loginPage("/login") // 로그인 페이지 경로 설정
                        .defaultSuccessUrl("/articles")  // 로그인 완료 후 이동 경로
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")  // 로그아웃 완료 시 이동 경로
                        .invalidateHttpSession(true)  // 로그아웃 이후 세션을 전체 삭제할지 여부
                )
                .csrf(AbstractHttpConfigurer::disable)  // csrf 비활성화(원래는 활성화 하는게 좋음. 실습이라 비활성화)
                .build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception{
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // 사용자 정보 서비스 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(authProvider);
    }

    // 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
