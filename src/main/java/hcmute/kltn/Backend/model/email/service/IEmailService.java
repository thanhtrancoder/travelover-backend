package hcmute.kltn.Backend.model.email.service;

import org.springframework.web.multipart.MultipartFile;

import hcmute.kltn.Backend.model.email.dto.EmailDTO;
import hcmute.kltn.Backend.model.order.dto.OrderDTO;

public interface IEmailService {
    public void sendSimpleMail(EmailDTO emailDTO);
    public void sendSimpleMailWithAttachment(EmailDTO emailDTO, MultipartFile file);
    
    public void sendMail(EmailDTO emailDTO);
    public EmailDTO getInfoOrderSuccess(OrderDTO orderDTO, String customerName);
}
