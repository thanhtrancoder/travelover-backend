package hcmute.kltn.Backend.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;

@ControllerAdvice
public class ExceptionHandlerController {
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	@ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseObject> handleCustomException(CustomException ex) {
		System.out.println(ex.getMessage());
    	return iResponseObjectService.failed(new Response() {
			{
				setMessage(ex.getMessage());
			}
		});
    }
	
	@ExceptionHandler(TryCatchException.class)
    public ResponseEntity<ResponseObject> handleTryCatchException(TryCatchException e) {
		StackTraceElement[] stackTrace = e.getStackTrace();
        String methodName = stackTrace[0].getMethodName();
        
        String messageSplit[] = e.getMessage().split(":");
        String message = messageSplit[messageSplit.length - 1].trim();
        
		System.out.println("Something went wrong: <" + methodName + "> " + message);
		
    	return iResponseObjectService.failed(new Response() {
			{
				setMessage("Something went wrong: <" + methodName + "> " + message);
			}
		});
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        String error = errorMessages.toString();
        if (error.startsWith("[") && error.endsWith("]")) {
        	error = error.substring(1, error.length() - 1);
        }
        char firstChar = Character.toUpperCase(error.charAt(0));
        error = firstChar + error.substring(1);

        final String message = error;
        
        System.out.println(error);
        return iResponseObjectService.failed(new Response() {
			{
				setMessage(message);
			}
		});
    }
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleOtherException(Exception ex) {
    	ex.printStackTrace();

    	return iResponseObjectService.failed(new Response() {
			{
				setMessage(ex.getMessage());
			}
		});
    }
}
