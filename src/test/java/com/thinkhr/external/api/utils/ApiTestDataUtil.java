package com.thinkhr.external.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkhr.external.api.db.entities.Company;
import com.thinkhr.external.api.db.entities.CustomFields;
import com.thinkhr.external.api.db.entities.User;
import com.thinkhr.external.api.model.FileImportResult;

/**
 * Utility class to keep all utilities required for Junits
 * 
 * @author Surabhi Bhawsar
 * @Since 2017-11-06
 *
 */
public class ApiTestDataUtil {

	public static final String API_BASE_PATH = "/v1/";
	public static final String COMPANY_API_BASE_PATH = "/v1/companies/";
	public static final String USER_API_BASE_PATH = "/v1/users/";
	public static final String COMPANY_API_REQUEST_PARAM_OFFSET = "offset";
	public static final String COMPANY_API_REQUEST_PARAM_LIMIT = "limit";
	public static final String COMPANY_API_REQUEST_PARAM_SORT = "sort";
	public static final String COMPANY_API_REQUEST_PARAM_SEARCH_SPEC = "searchSpec";
	
	/**
	 * Convert object into Json String
	 * 
	 * @param object
	 * @param kclass
	 * @return
	 * @throws JAXBException 
	 * @throws PropertyException 
	 */
	public static String getJsonString(Object object) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper.writeValueAsString(object);	
	}
	

	/**
	 * Create a Company Model for given inputs
	 * 
	 * @param companyId
	 * @param companyName
	 * @param companyType
	 * @param displayName
	 * @param companySince
	 * @param specialNotes
	 * @return
	 */
	public static Company createCompany(Integer companyId, String companyName,
			String companyType, String displayName, 
			Date companySince, String specialNotes, String searchHelp) {
		Company company = new Company();
		if (companyId != null) {
			company.setCompanyId(companyId);
		}
		company.setCompanyName(companyName);
		company.setCompanyType(companyType);
		company.setDisplayName(displayName);
		company.setCompanySince(companySince);
		company.setSpecialNote(specialNotes);
		company.setSearchHelp(searchHelp); 
		company.setIsActive(1);
		return company;
	}
	
	/**
	 * Create a User entity for given inputs
	 * 
	 * @param contactId
	 * @param firstName
	 * @param lastName
	 * @param searchHelp
	 * @return
	 */
	public static User createUser(Integer userId, String firstName, String lastName,
			String email, String userName, String companyName) {
		User user = new User();
		if (userId != null) {
			user.setUserId(userId);
		}
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setUserName(userName);
		user.setCompanyName(companyName);   
		return user;
	}
	
	/**
	 * 
	 * @return
	 */
	public static User createUser() {
		return createUser(1, "Surabhi", "Bhawsar", "surabhi.bhawsar@pepcus.com", "sbhawsar", "Pepcus");
	}
	
	/**
	 * Creates a user response object
	 * 
	 * @param user
	 * @param httpStatus
	 * @return
	 */
	public static ResponseEntity<User> createUserResponseEntity(User user, HttpStatus httpStatus) {
		return new ResponseEntity<User>(user, httpStatus);
	}

	/**
	 * Creates a company response object
	 * 
	 * @param company
	 * @param httpStatus
	 * @return
	 */
	public static ResponseEntity<Company> createCompanyResponseEntity(Company company, HttpStatus httpStatus) {
		return new ResponseEntity<Company>(company, httpStatus);
	}
	
	/**
	 * createCompanyIdResponseEntity
	 * 
	 * @param companyId
	 * @param httpStatus
	 * @return
	 */
	public static ResponseEntity<Integer> createCompanyIdResponseEntity(Integer companyId, HttpStatus httpStatus) {
		return new ResponseEntity<Integer>(companyId, httpStatus);
	}
	
	/**
	 * createContactIdResponseEntity
	 * 
	 * @param contactId
	 * @param httpStatus
	 * @return
	 */
	public static ResponseEntity<Integer> createUserIdResponseEntity(Integer userId, HttpStatus httpStatus) {
		return new ResponseEntity<Integer>(userId, httpStatus);
	}

	/**
	 * Create Model object for Company
	 * 
	 * @return
	 */
	public static Company createCompany() {
		return createCompany(1, "Pepcus", "Software", "PEP", new Date(), "Special", "This is search help");
	}

	/**
	 * Create List for Company objects
	 * 
	 * @return
	 */
	public static List<Company> createCompanies() {
		List<Company> companies = new ArrayList<Company>();
		companies.add(createCompany(1, "Pepcus", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help1"));
		companies.add(createCompany(2, "Google", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help2"));
		companies.add(createCompany(3, "Facebook", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help3"));
		companies.add(createCompany(4, "Suzuki", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help4"));
		companies.add(createCompany(5, "General Motors", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help5"));
		companies.add(createCompany(6, "L & T", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help6"));
		companies.add(createCompany(7, "General Electric", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help7"));
		companies.add(createCompany(8, "Oracle", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help8"));
		companies.add(createCompany(9, "Microsoft", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help9"));
		companies.add(createCompany(10, "Thinkhr", "IT", "PEP", new Date(), "IT comp at Indore", "This is search help10"));
		return companies;

	}
	
	/**
	 * Create List for User objects
	 * 
	 * @return
	 */
	public static List<User> createUserList() {
		List<User> users = new ArrayList<User>();
		
		users.add(createUser(1, "Isha", "Khandelwal", "isha.khandelwal@gmail.com", "ishaa", "ThinkHR"));
		users.add(createUser(2, "Sharmila", "Tagore", "stagore@gmail.com", "stagore", "ASI"));
		users.add(createUser(3, "Surabhi", "Bhawsar", "sbhawsar@gmail.com", "sbhawsar", "Pepcus"));
		users.add(createUser(4, "Shubham", "Solanki", "ssolanki@gmail.com", "ssolanki", "Pepcus"));
		users.add(createUser(5, "Ajay", "Jain", "ajain@gmail.com", "ajain", "TCS"));
		users.add(createUser(6, "Sandeep", "Vishwakarma", "svishwakarma@gmail.com", "svishwakarma", "CIS"));
		users.add(createUser(7, "Sushil", "Mahajan", "smahajan@gmail.com", "smahajan", "ASI"));
		users.add(createUser(8, "Sumedha", "Wani", "swani@gmail.com", "swani", "InfoBeans"));
		users.add(createUser(9, "Mohit", "Jain", "mjain@gmail.com", "mjain", "Pepcus"));
		users.add(createUser(10, "Avi", "Jain", "ajain@gmail.com", "ajain", "Pepcus"));
		
		return users;
	}

    /**
     * Create List for CustomFields object
     * 
     * @return
     */
    public static List<CustomFields> createCustomFieldsList() {
        List<CustomFields> customFields = new ArrayList<CustomFields>();

        customFields.add(createCustomFields(2, 15472, "COMPANY", "1", "c1", "CORRELATION_ID", "0"));
        customFields.add(createCustomFields(3, 15472, "COMPANY", "2", "c2", "GROUP_ID", "0"));
        return customFields;
    }

    public static CustomFields createCustomFields(Integer id, Integer companyId, String customFieldType,
            String customFieldColumnName, String customFieldDisplayKey, String customFieldDisplayLabel,
            String isImportRequired) {
        CustomFields customFields = new CustomFields();
        if (id != null) {
            customFields.setId(id);
        }
        customFields.setCompanyId(companyId);
        customFields.setCustomFieldType(customFieldType);
        customFields.setCustomFieldColumnName(customFieldColumnName);
        customFields.setCustomFieldDisplayKey(customFieldDisplayKey);
        customFields.setCustomFieldDisplayLabel(customFieldDisplayLabel);
        customFields.setIsImportRequired(isImportRequired);

        return customFields;
    }

    public static String getCsvRecord() {
        String record = "Adams Radio of Tallahassee LLC 123,Adams Radio of Tallahassee LLC,9522320916,3000 Olson Rd,,Tallahassee,FL,32308,Other,0,,5331058,56,11078286,Non PEO";
        return record;
    }

    public static String getCsvRecordForDuplicate() {
        String record = "Pepcus,Adams Radio of Tallahassee LLC,9522320916,3000 Olson Rd,,Tallahassee,FL,32308,Other,0,,5331058,56,11078286,Non PEO";
        return record;
    }

    public static String getCsvRecordForSpecialCase() {
        String record = "Paychex,Adams Radio of Tallahassee LLC,9522320916,3000 Olson Rd,,Tallahassee,FL,32308,Other,0,,5331058,56,11078286,Non PEO";
        return record;
    }

    public static FileImportResult createFileImportResultWithNoFailedRecords() {
        FileImportResult fileImportResult = new FileImportResult();
        fileImportResult.setTotalRecords(10);
        fileImportResult.setNumSuccessRecords(10);
        fileImportResult.setNumFailedRecords(0);
        return fileImportResult;
    }

    public static FileImportResult createFileImportResultWithFailedRecords() {
        FileImportResult fileImportResult = new FileImportResult();
        fileImportResult.setTotalRecords(10);
        fileImportResult.setNumSuccessRecords(7);
        fileImportResult.addFailedRecord(3, "", "Blank Record", "Skipped");
        fileImportResult.addFailedRecord(6,
                "Paychex,Adams Radio of Tallahassee LLC,9522320916,3000 Olson Rd,,Tallahassee,FL,32308,Other,0,,5331058,56,11078286,Non PEO",
                "Duplicate", "Skipped");
        fileImportResult.addFailedRecord(8, "Paychex,Adams Radio of Tallahassee LLC,", "Missing Fields in Record",
                "Record not added");
        return fileImportResult;
    }

    public static ResponseEntity<InputStreamResource> createInputStreamResponseEntityForBulkUpload()
            throws FileNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-disposition", "attachment;filename=companiesImportResult.csv");
        
        File responseFile = new File("src/test/resources/testdata/20_ResponseCSV.csv");
        
        ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok().headers(headers)
                .contentLength(responseFile.length()).contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(new FileInputStream(responseFile)));

        return responseEntity;
    }

    public static MockMultipartFile createMockMultipartFile() throws IOException {
        File file = new File("src/test/resources/testdata/8_Example10Rec.csv");

        FileInputStream input = null;
        MockMultipartFile multipartFile = null;

        input = new FileInputStream(file);

        multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

        return multipartFile;
    }

    public static MockMultipartFile createMockMultipartFile_EmptyFile() throws IOException {
        File file = new File("src/test/resources/testdata/1_EmptyCSV.csv");

        FileInputStream input = null;
        MockMultipartFile multipartFile = null;

        input = new FileInputStream(file);

        multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

        return multipartFile;
    }

    public static MockMultipartFile createMockMultipartFile_InvalidExtension() throws IOException {
        File file = new File("src/test/resources/testdata/2_InvalidExtension.xlsx");

        FileInputStream input = null;
        MockMultipartFile multipartFile = null;

        input = new FileInputStream(file);

        multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

        return multipartFile;
    }

    public static MockMultipartFile createMockMultipartFile_MissingHeaders() throws IOException {
        File file = new File("src/test/resources/testdata/3_MissingHeader.csv");

        FileInputStream input = null;
        MockMultipartFile multipartFile = null;

        input = new FileInputStream(file);

        multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

        return multipartFile;
    }

    public static MockMultipartFile createMockMultipartFile_MaxRecordExceed() throws IOException {
        File file = new File("src/test/resources/testdata/5_10000Rec_ExceedMaxRec3500.csv");

        FileInputStream input = null;
        MockMultipartFile multipartFile = null;

        input = new FileInputStream(file);

        multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));

        return multipartFile;
    }

    /**
     * 
     * @return
     */
    public static List<String> getCompanyColumnList() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("client_name");
        columnList.add("display_name");
        columnList.add("client_phone");
        columnList.add("industry");
        columnList.add("companysize");
        columnList.add("producer");
        columnList.add("custom1");
        columnList.add("custom2");
        columnList.add("custom3");
        columnList.add("custom4");
        return columnList;
    }

    public static List<Object> getCompanyColumnValuesList() {
        List<Object> columnValuesList = new ArrayList<Object>();
        columnValuesList.add("Pepcus Software Services");
        columnValuesList.add("Pepcus");
        columnValuesList.add("3457893455");
        columnValuesList.add("IT");
        columnValuesList.add("20");
        columnValuesList.add("AJain");
        columnValuesList.add("dummy_business");
        columnValuesList.add("dummy_branch");
        columnValuesList.add("dummy_client");
        columnValuesList.add("dummy_client_type");
        return columnValuesList;
    }

    public static List<Object> getLocationsColumnValuesList() {
        List<Object> locationValuesList = new ArrayList<Object>();
        locationValuesList.add("Eastern County");
        locationValuesList.add("dummy_address");
        locationValuesList.add("Texas City");
        locationValuesList.add("TX");
        locationValuesList.add("452001");
        return locationValuesList;
    }

    /**
     * 
     * @return
     */
    public static List<String> getLocationColumnList() {
        List<String> columnList = new ArrayList<String>();
        columnList.add("address");
        columnList.add("address2");
        columnList.add("city");
        columnList.add("state");
        columnList.add("zip");
        return columnList;
    }

    /**
     * 
     * @return
     */
    public static String testQueryForCompany() {
        return "INSERT INTO clients(client_name,display_name,client_phone,industry,companysize,producer,custom1,custom2,custom3,custom4,"
                + "search_help,client_type,special_note,client_since,t1_is_active) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
    }

    /**
     * 
     * @return
     */
    public static String testQueryForLocation() {
        return "INSERT INTO locations(address,address2,city,state,zip,client_id) VALUES(?,?,?,?,?,?) ";
    }

    /**
     * 
     * @return
     */
    public static String[] getAllHeadersForCompany() {
        String[] requiredHeaders = { "CLIENT_NAME", "DISPLAY_NAME", "PHONE", "ADDRESS", "ADDRESS2", "CITY", "STATE", "ZIP", "INDUSTRY",
                "COMPANY_SIZE", "PRODUCER", "BUSINESS_ID", "BRANCH_ID", "CLIENT_ID", "CLIENT_TYPE" };
        return requiredHeaders;
    }

    /**
     * 
     * @return
     */
    public static String[] getAvailableHeadersForCompany() {
        String[] requiredHeaders = { "CLIENT_NAME", "DISPLAY_NAME", "PHONE", "ADDRESS", "ADDRESS2", "CITY", "STATE", "ZIP" };
        return requiredHeaders;
    }

    public static Map<String, String> getColumnsToHeadersMap() {
        Map<String, String> columnToHeaderMap = new HashMap<String, String>();
        columnToHeaderMap.put("companyName", "CLIENT_NAME");
        columnToHeaderMap.put("displayName", "DISPLAY_NAME");
        columnToHeaderMap.put("companyPhone", "PHONE");
        columnToHeaderMap.put("industry", "INDUSTRY");
        columnToHeaderMap.put("companySize", "COMPANY_SIZE");
        columnToHeaderMap.put("producer", "PRODUCER");
        return columnToHeaderMap;
    }

    public static Map<String, String> getAllColumnsToHeadersMap() {
        Map<String, String> columnToHeaderMap = new HashMap<String, String>();
        columnToHeaderMap.put("companyName", "CLIENT_NAME");
        columnToHeaderMap.put("displayName", "DISPLAY_NAME");
        columnToHeaderMap.put("companyPhone", "PHONE");
        columnToHeaderMap.put("industry", "INDUSTRY");
        columnToHeaderMap.put("companySize", "COMPANY_SIZE");
        columnToHeaderMap.put("producer", "PRODUCER");
        columnToHeaderMap.put("custom1", "BUSINESS_ID");
        columnToHeaderMap.put("custom2", "BRANCH_ID");
        columnToHeaderMap.put("custom3", "CLIENT_ID");
        columnToHeaderMap.put("custom4", "CLIENT_TYPE");
        return columnToHeaderMap;
    }

    public static Map<String, Integer> getHeaderIndexMap() {
        Map<String, Integer> headerIndexMap = new HashMap<String, Integer>();
        headerIndexMap.put("CLIENT_NAME", 0);
        headerIndexMap.put("DISPLAY_NAME", 1);
        headerIndexMap.put("PHONE", 2);
        headerIndexMap.put("INDUSTRY", 3);
        headerIndexMap.put("COMPANY_SIZE", 4);
        headerIndexMap.put("PRODUCER", 5);
        return headerIndexMap;
    }

    public static String getFileRecord() {
        return "Pepcus Software Services, pepcus, 9213234567, IT, 20, Ajay Jain";
    }

}
