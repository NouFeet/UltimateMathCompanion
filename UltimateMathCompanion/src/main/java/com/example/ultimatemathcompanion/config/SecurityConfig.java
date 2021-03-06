package com.example.ultimatemathcompanion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource securityDataSource;

    public SecurityConfig(DataSource securityDataSource) {
        this.securityDataSource = securityDataSource;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/js/**", "/js/view/**", "/images/**", "/images/social-media/**", "/css/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requiresChannel()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(securityDataSource)
                .usersByUsernameQuery("select username, password, enabled from calculator.users where username = ?")
                .authoritiesByUsernameQuery("select username, authority from calculator.authorities where username = ?");
    }

}
