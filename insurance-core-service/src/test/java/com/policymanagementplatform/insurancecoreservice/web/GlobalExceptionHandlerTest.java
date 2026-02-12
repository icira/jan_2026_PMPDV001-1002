package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    /*
    LANES ADDED:
    - Added tests for IllegalStateException -> 409 and IllegalArgumentException -> 400.
    WHY:
    - Part 2 adds these handler methods and we want them covered + verified.
    */

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundShouldReturn404AndBody() {
        NotFoundException ex = new NotFoundException("Client not found");

        ResponseEntity<Map<String, Object>> response = handler.notFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().get("error"));
        assertEquals("Client not found", response.getBody().get("message"));
    }

    @Test
    void conflictShouldReturn409AndBody() {
        ConflictException ex = new ConflictException("Identification number already exists");

        ResponseEntity<Map<String, Object>> response = handler.conflict(ex);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().get("error"));
        assertEquals("Identification number already exists", response.getBody().get("message"));
    }

    @Test
    void validationShouldReturn400AndDetailsForFieldErrorsIncludingInvalidFallback() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");

        bindingResult.addError(new FieldError("target", "name", "must not be blank"));

        bindingResult.addError(new FieldError(
                "target",
                "email",
                null,
                false,
                null,
                null,
                null
        ));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.validation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().get("error"));

        Object detailsObj = response.getBody().get("details");
        assertNotNull(detailsObj);
        assertTrue(detailsObj instanceof List<?>);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> details = (List<Map<String, String>>) detailsObj;

        assertEquals(2, details.size());

        assertEquals("name", details.get(0).get("field"));
        assertEquals("must not be blank", details.get(0).get("message"));

        assertEquals("email", details.get(1).get("field"));
        assertEquals("invalid", details.get(1).get("message"));
    }

    @Test
    void unexpectedShouldReturn500AndGenericBody() {
        Exception ex = new RuntimeException("Boom");

        ResponseEntity<Map<String, Object>> response = handler.unexpected(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().get("error"));
        assertEquals("Unexpected error", response.getBody().get("message"));
    }

    @Test
    void illegalStateShouldReturn409AndBody() {
        IllegalStateException ex = new IllegalStateException("Invalid state transition");

        ResponseEntity<Map<String, Object>> response = handler.illegalState(ex);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("CONFLICT", response.getBody().get("error"));
        assertEquals("Invalid state transition", response.getBody().get("message"));
    }

    @Test
    void illegalArgumentShouldReturn400AndBody() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad input");

        ResponseEntity<Map<String, Object>> response = handler.illegalArgument(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().get("error"));
        assertEquals("Bad input", response.getBody().get("message"));
    }
}
