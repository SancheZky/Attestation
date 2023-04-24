package com.example.market.config;

import com.example.market.services.PersonDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig{

    private final PersonDetailsService personDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncode(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // конфигурируем работу спринг.сек.
        httpSecurity//.csrf().disable()       // отключить защиту от межсайтовой подделки запросов
                .authorizeHttpRequests()    // все страницы должны быть защищены аутентификацией
                .requestMatchers("/","/login", "/error", "/registration","/css/**", "/js/**", "/img/**", "/product", "/product/info/{id}", "/product/search").permitAll() // какие страницы достпны всем пользователям
                .requestMatchers("/admin").hasRole("ADMIN") // в админку только  с сролью админа
                .anyRequest().hasAnyRole("USER", "ADMIN")
                //.anyRequest().authenticated()   // для всех остальных  нужна аутентификация
                .and()  // соединитель указывает что дальше настраивается аутентификация и соединяем ее с настройкой доступа
                .formLogin().loginPage("/login")    // url формы для логина
                .loginProcessingUrl("/process_login")   // куда отправлять данные  с формы. Не приджется создавать метод в контроллере и обрабатывать данные с формы
                .defaultSuccessUrl("/account", true) // перейити в случае успешной аутентификации
                .failureUrl("/login?error") // куда перенаправить  в случае неудачи. в запрос будет передан объект error
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login"); // выйти из аккаунта
        return httpSecurity.build();
    }

    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncode());
    }

}
