package hcmute.kltn.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hcmute.kltn.Backend.model.base.response.dto.Response;
import hcmute.kltn.Backend.model.base.response.dto.ResponseObject;
import hcmute.kltn.Backend.model.base.response.service.IResponseObjectService;
import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceCreate;
import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceDTO;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/generator-sequences")
@Tag(
		name = "Generator Sequence", 
		description = "APIs for managing Generator Sequence - FOR DEV",
		externalDocs = @ExternalDocumentation(
				description = "Update Api History", 
				url = "https://drive.google.com/file/d/1Rxu-cFJjDVtz8Wegkhk4W2MIzzpqJhVM/view?usp=sharing")
		)
@SecurityRequirement(name = "Bearer Authentication")
public class GeneratorSequenceController {
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private IResponseObjectService iResponseObjectService;
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    @Operation(summary = "Create Generator Sequence - ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<ResponseObject> createGenSeq(@RequestBody GeneratorSequenceCreate generatorSequenceCreate) {
    	GeneratorSequenceDTO generatorSequenceDTO = iGeneratorSequenceService.createGenSeq(generatorSequenceCreate);
        
        return iResponseObjectService.success(new Response() {
					{
						setMessage("Create Generator Sequence successfully");
						setData(generatorSequenceDTO);
					}
				});
    }
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
    @Operation(summary = "Update Generator Sequence - ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<ResponseObject> updateGenSeq(@RequestBody GeneratorSequenceDTO generatorSequenceDTO) {
    	GeneratorSequenceDTO generatorSequenceDTONew = iGeneratorSequenceService.updateGenSeq(generatorSequenceDTO);
    	
    	return iResponseObjectService
				.success(new Response() {
					{
						setMessage("Update Generator Sequence successfully");
						setData(generatorSequenceDTONew);
					}
				});
    }
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
    @Operation(summary = "Get detail Generator Sequence - ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<ResponseObject> getDetailGenSeq(@RequestParam String id) {
    	GeneratorSequenceDTO generatorSequenceDTO = iGeneratorSequenceService.getDetailGenSeq(id);
    	
    	return iResponseObjectService.success(new Response() {
					{
						setMessage("Get detail Generator Sequence successfully");
						setData(generatorSequenceDTO);
					}
				});
    }
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
    @Operation(summary = "Get all Generator Sequence - ADMIN")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<ResponseObject> getAllGenSeq() {
        List<GeneratorSequenceDTO> generatorSequenceDTOList = iGeneratorSequenceService.getAllGenSeq();
        
        return iResponseObjectService.success(new Response() {
			{
				setMessage("Get all Generator Sequence");
				setData(generatorSequenceDTOList);
			}
		});
    }

    
//    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
//    @Operation(summary = "Delete Generator Sequence")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    ResponseEntity<ResponseObject> deleteProduct(@PathVariable long id) {
//    	boolean delete = iGeneratorSequenceService.delete(id);
//    	
//    	return iResponseObjectService.success(new Response() {
//					{
//						setMessage("Delete Generator Sequence successfully");
//					}
//				});
//    }
}
