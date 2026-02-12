package com.policymanagementplatform.insurancecoreservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SecurityConfig.class, SecurityConfigTest.TestController.class})
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ApplicationContext applicationContext;

    @RestController
    static class TestController {
        @GetMapping("/api/test")
        String apiGet() {
            return "api-ok";
        }

        @PostMapping("/api/test")
        String apiPost(@RequestBody(required = false) String body) {
            return "posted:" + (body == null ? "" : body);
        }

        @GetMapping("/anything")
        String otherGet() {
            return "other-ok";
        }
    }

    @Test
    void securityFilterChainBeanIsCreatedAndEndpointsAreAccessibleAndCsrfIsDisabled() throws Exception {

        SecurityFilterChain chain = applicationContext.getBean(SecurityFilterChain.class);
        assertThat(chain).isNotNull();


        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("api-ok"));


        mockMvc.perform(post("/api/test")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("posted:hello"));


        mockMvc.perform(get("/anything"))
                .andExpect(status().isOk())
                .andExpect(content().string("other-ok"));
    }

    @Test
    void formLoginAndHttpBasicAreDisabledFiltersAreAbsent() {

        SecurityFilterChain chain = applicationContext.getBean(SecurityFilterChain.class);

        assertThat(chain).isInstanceOf(DefaultSecurityFilterChain.class);

        DefaultSecurityFilterChain dsc = (DefaultSecurityFilterChain) chain;
        List<?> filters = dsc.getFilters();

        boolean hasFormLoginFilter = filters.stream().anyMatch(UsernamePasswordAuthenticationFilter.class::isInstance);
        boolean hasBasicAuthFilter = filters.stream().anyMatch(BasicAuthenticationFilter.class::isInstance);

        assertThat(hasFormLoginFilter)
                .as("UsernamePasswordAuthenticationFilter should be absent because formLogin is disabled")
                .isFalse();

        assertThat(hasBasicAuthFilter)
                .as("BasicAuthenticationFilter should be absent because httpBasic is disabled")
                .isFalse();
    }
}
