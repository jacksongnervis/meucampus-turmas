package br.edu.ifsul.sapucaia.meucampus_turmas.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex,
                                                                             HttpServletRequest request) {
        return montarErro(ex.getStatusCode(), ex.getReason(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                     HttpServletRequest request) {
        return montarErro(BAD_REQUEST, extrairErro(ex), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                            HttpServletRequest request) {
        if (ex.getMostSpecificCause() instanceof ResponseStatusException rse) {
            return handleResponseStatusException(rse, request);
        }

        return montarErro(BAD_REQUEST, "Formato de requisição inválido.", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex,
                                                                  HttpServletRequest request) {
        return montarErro(BAD_REQUEST,
                "Parâmetro '" + ex.getName() + "' com valor inválido: " + ex.getValue(), request);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, Object>> handleSortInvalido(PropertyReferenceException ex,
                                                                  HttpServletRequest request) {
        return montarErro(BAD_REQUEST, "Parâmetro de ordenação inválido.", request);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, Object>> handleServicoIndisponivel(ResourceAccessException ex,
                                                                         HttpServletRequest request) {
        return montarErro(SERVICE_UNAVAILABLE,
                "Serviço dependente indisponível ou tempo limite excedido. Tente novamente mais tarde.",
                request);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleErroServicoDependente(RestClientResponseException ex,
                                                                           HttpServletRequest request) {
        return montarErro(SERVICE_UNAVAILABLE,
                "Serviço dependente respondeu com erro (" + ex.getStatusCode().value() + ").",
                request);
    }

    private ResponseEntity<Map<String, Object>> montarErro(HttpStatusCode statusCode, String mensagem,
                                                           HttpServletRequest request) {
        String errorPhrase = "Error";
        if (statusCode instanceof HttpStatus status) {
            errorPhrase = status.getReasonPhrase();
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", statusCode.value());
        body.put("error", errorPhrase);
        body.put("message", mensagem);
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, statusCode);
    }

    private String extrairErro(MethodArgumentNotValidException ex) {
        Optional<ObjectError> erroOpt = ex.getBindingResult().getAllErrors()
                .stream()
                .findFirst();

        if (erroOpt.isPresent()) {
            FieldError erro = (FieldError) erroOpt.get();
            return "Campo " + erro.getField() + " " + erro.getDefaultMessage();
        } else {
            return "Erro de validação.";
        }
    }
}
