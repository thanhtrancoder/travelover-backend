package hcmute.kltn.Backend.model.base.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private String message;
    private int pageSize;
    private int pageNumber;
    private Object data;
}
