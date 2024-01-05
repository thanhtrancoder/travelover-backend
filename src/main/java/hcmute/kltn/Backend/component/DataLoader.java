package hcmute.kltn.Backend.component;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import hcmute.kltn.Backend.model.account.dto.AccountDTO;
import hcmute.kltn.Backend.model.account.service.IAccountService;
import hcmute.kltn.Backend.model.commission.dto.CommissionDTO;
import hcmute.kltn.Backend.model.commission.service.ICommissionService;
import hcmute.kltn.Backend.model.generatorSequence.dto.GeneratorSequenceDTO;
import hcmute.kltn.Backend.model.generatorSequence.service.IGeneratorSequenceService;

@Component
public class DataLoader implements CommandLineRunner {
	@Autowired
	private IGeneratorSequenceService iGeneratorSequenceService;
	@Autowired
	private IAccountService iAccountService;
	@Autowired
	private ICommissionService iCommissionService;
	
	private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
	
	@Value("${server.port}")
    private String portServer;

	@Override
	public void run(String... args) throws Exception {
//		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		Date nowDate = new Date();
		
		// GEN DATA FOR GENERATOR SEQUENCE TABLE
		// TableName = Account, Prefix = ACC, Description = table Account
		// TableName = Image, Prefix = IMG, Description = table Image
		// TableName = Tour, Prefix = TR, Description = table Tour
		boolean initCheck = true;
		
		GeneratorSequenceDTO genGenSeqAccount = new GeneratorSequenceDTO();
		genGenSeqAccount.setCollectionName("account");
		genGenSeqAccount.setPrefix("ACC");
		genGenSeqAccount.setNumber(0);
		genGenSeqAccount.setDescription("Account collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqAccount);
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = Account, Prefix = ACC, Description = Account collection");
		} 
		
		
		GeneratorSequenceDTO genGenSeqImage = new GeneratorSequenceDTO();
		genGenSeqImage.setCollectionName("image");
		genGenSeqImage.setPrefix("IMG");
		genGenSeqImage.setNumber(0);
		genGenSeqImage.setDescription("Image collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqImage);
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = Image, Prefix = IMG, Description = Image collection");
		} 
		
		GeneratorSequenceDTO genGenSeqTour = new GeneratorSequenceDTO();
		genGenSeqTour.setCollectionName("tour");
		genGenSeqTour.setPrefix("TR");
		genGenSeqTour.setNumber(0);
		genGenSeqTour.setDescription("Tour collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqTour);
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = Tour, Prefix = TR, Description = Tour collection");
		} 
		
		GeneratorSequenceDTO genGenSeqOrder = new GeneratorSequenceDTO();
		genGenSeqOrder.setCollectionName("order");
		genGenSeqOrder.setPrefix("ODR");
		genGenSeqOrder.setNumber(0);
		genGenSeqOrder.setDescription("Order collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqOrder);
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = Tour, Prefix = TR, Description = Order collection");
		} 
		
		GeneratorSequenceDTO genGenSeqHotel = new GeneratorSequenceDTO();
		genGenSeqHotel.setCollectionName("hotel");
		genGenSeqHotel.setPrefix("HTL");
		genGenSeqHotel.setNumber(0);
		genGenSeqHotel.setDescription("Hotel collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqHotel);
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = Tour, Prefix = TR, Description = Hotel collection");
		} 
		
		GeneratorSequenceDTO genGenSeqDiscount = new GeneratorSequenceDTO();
		genGenSeqDiscount.setCollectionName("discount");
		genGenSeqDiscount.setPrefix("DCT");
		genGenSeqDiscount.setNumber(0);
		genGenSeqDiscount.setDescription("Discount collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqDiscount);
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = Discount, Prefix = DCT, Description = Discount collection");
		} 
		
		GeneratorSequenceDTO genGenSeqURL = new GeneratorSequenceDTO();
		genGenSeqURL.setCollectionName("url");
		genGenSeqURL.setPrefix("URL");
		genGenSeqURL.setNumber(0);
		genGenSeqURL.setDescription("URL collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqDiscount);
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = URL, Prefix = URL, Description = URL collection");
		} 
		
		GeneratorSequenceDTO genGenSeqVNPayment = new GeneratorSequenceDTO();
		genGenSeqVNPayment.setCollectionName("vnpayment");
		genGenSeqVNPayment.setPrefix("VNP");
		genGenSeqVNPayment.setNumber(0);
		genGenSeqVNPayment.setDescription("VNPayment collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqVNPayment);
		
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = VNPayment, Prefix = VNP, Description = VNPayment collection");
		} 
		
		GeneratorSequenceDTO genGenSeqCommission = new GeneratorSequenceDTO();
		genGenSeqCommission.setCollectionName("commission");
		genGenSeqCommission.setPrefix("CMS");
		genGenSeqCommission.setNumber(0);
		genGenSeqCommission.setDescription("Commission collection");
		initCheck = iGeneratorSequenceService.initData(genGenSeqCommission);
		
		if (initCheck) {
			logger.info("Success to gen data for Generator Sequence collection: "
					+ "CollectionName = Commission, Prefix = CMS, Description = Commission collection");
		}
		
		// GEN DATA FOR ACCOUNT TABLE
		// FirstName = dev, LastName = dev, Email = dev@gmail.com, Password = 123456, Role = SUPER_ADMIN
		
		AccountDTO user1 = new AccountDTO();
		user1.setFirstName("dev");
		user1.setLastName("dev");
		user1.setEmail("dev@gmail.com");
//		user1.setPassword("123456");
		user1.setRole("ADMIN");
		initCheck = iAccountService.initData(user1);
		if (initCheck) {
			logger.info("Success to gen data for Account table: "
					+ "FirstName = dev, LastName = dev, Email = dev@gmail.com, Password = 123456, Role = ADMIN");
		} 
		
		// gen commission
		List<CommissionDTO> commissionDTOList = new ArrayList<>();
		commissionDTOList.addAll(iCommissionService.getAllCommission());
		if (commissionDTOList.isEmpty()) {
			CommissionDTO commissionDTO = new CommissionDTO();
			commissionDTO.setName("Default Commission");
			commissionDTO.setRate(10);
			
			CommissionDTO commissionDTONew = new CommissionDTO();
			iCommissionService.initData(commissionDTO);
		}

    	String linkServer = "http://localhost:" + portServer + "/swagger-ui/index.html";
		
		logger.info("Server is running on: " + linkServer);
		System.out.println("Initialized database");
        System.out.println("Server is running on: " + linkServer);
	}
}
