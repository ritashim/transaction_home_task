package moe.rtc.transaction.infra.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import moe.rtc.transaction.infra.web.BaseApiDataResponse;
import moe.rtc.transaction.infra.web.BaseApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;

@Log
@ControllerAdvice
public class ExceptionHandlingAdvice {
    public static final String DEFAULT_INTERNAL_ERROR_CODE = "I_INTERNAL_ERROR";
    public static final String DEFAULT_VALIDATION_ERROR_CODE = "I_VALIDATION_ERROR";
    public static final String DEFAULT_ERROR_MSG = "Internal Error";
    public static final String DEFAULT_VALIDATION_ERROR_MSG = "Validation Error";

    ResourceBundle errMsgBundle = ResourceBundle.getBundle("ErrorMessages");

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public void handleApplicationException(ApplicationException ex, HttpServletResponse resp) throws IOException {
        String errorCode = Objects.toString(ex.getErrorCode(), DEFAULT_INTERNAL_ERROR_CODE);
        String errorMsg;
        try{
            String original = errMsgBundle.getString(errorCode);
            errorMsg = errMsgBundle.getString(errorCode).formatted((Object[]) ex.getErrorMsgFormatString());
        } catch(MissingResourceException | IllegalFormatException e) {
            log.warning("Error code not found in resourceBundle - %s".formatted(errorCode));
            errorMsg = DEFAULT_ERROR_MSG;
        }
        BaseApiResponse respBody = new BaseApiResponse()
                .setBizCode(errorCode)
                .setBizMessage(errorMsg);
        resp.setStatus(ex.getHttpStatus().value());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String respBodyString = ow.writeValueAsString(respBody);
        resp.getWriter().write(respBodyString);
        resp.getWriter().flush();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public void handleValidationException(MethodArgumentNotValidException ex, HttpServletResponse resp) throws IOException {
        List<String> errorCodes = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorCode;
        String errorMsg;
        StringBuilder errorMsgBuilder = new StringBuilder();
        if(errorCodes.size() == 1) {
            errorCode = errorCodes.get(0);
            try{
                String original = errMsgBundle.getString(errorCode);
                errorMsgBuilder.append(errMsgBundle.getString(errorCode));
            } catch(MissingResourceException e) {
                log.warning("Error code not found in resourceBundle - %s".formatted(errorCode));
            }
        } else {
            errorCode = DEFAULT_VALIDATION_ERROR_CODE;
            for(String code : errorCodes) {
                try{
                    String original = errMsgBundle.getString(code);
                    errorMsgBuilder.append(errMsgBundle.getString(code));
                    errorMsgBuilder.append("; ");
                } catch(MissingResourceException e) {
                    log.warning("Error code not found in resourceBundle - %s".formatted(code));
                }
            }
        }
        if(errorMsgBuilder.isEmpty()) {
            errorMsg = DEFAULT_VALIDATION_ERROR_MSG;
        } else {
            errorMsg = errorMsgBuilder.toString();
        }

        BaseApiResponse respBody = new BaseApiResponse()
                .setBizCode(errorCode)
                .setBizMessage(errorMsg);
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String respBodyString = ow.writeValueAsString(respBody);
        resp.getWriter().write(respBodyString);
        resp.getWriter().flush();
    }
}
