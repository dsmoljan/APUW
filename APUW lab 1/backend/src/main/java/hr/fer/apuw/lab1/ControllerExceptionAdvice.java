package hr.fer.apuw.lab1;

import hr.fer.apuw.lab1.dto.response.HandledErrorDTO;
import hr.fer.apuw.lab1.exception.RequestDeniedException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

//TODO: po potrebi dodaj jos handlera, npr. za validation, nullpointer itd.

@RestControllerAdvice
public class ControllerExceptionAdvice {

  private final MessageSource messageSource;

  @Autowired
  public ControllerExceptionAdvice(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleEntityNotFoundException(EntityNotFoundException exception) {
    String message = exception.getMessage();

    try {
      message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());
    } catch (Exception ignored) {}

    return Collections.singletonMap("error", message);
  }

  @ExceptionHandler(value = {RequestDeniedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HandledErrorDTO handleBadRequest(RuntimeException exception) {
    HandledErrorDTO errorDTO = new HandledErrorDTO();
    errorDTO.setCode(400);
    String message = exception.getMessage();

    try {
      message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());
    } catch (Exception ignored) {}

    errorDTO.setMessage(message);

    return errorDTO;
  }



}
