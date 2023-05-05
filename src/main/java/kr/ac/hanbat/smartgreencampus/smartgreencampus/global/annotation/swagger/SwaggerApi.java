package kr.ac.hanbat.smartgreencampus.smartgreencampus.global.annotation.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Operation
@ApiResponses(value = {
        @ApiResponse(responseCode = "200")
})
public @interface SwaggerApi {
    String summary() default "";
    Class<?> implementation() default Void.class;
}
