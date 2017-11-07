package com.thinkhr.external.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.validation.constraints.Min;

import lombok.Data;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Company Model is a user facing POJO object for
 * Company entity.
 * 
 * @author Surabhi Bhawsar
 * @since 2017-11-04
 *
 */

@Data
public class CompanyModel implements Serializable {

	private Integer companyId;
	private String searchHelp;
	private String companyType;

	@NotEmpty
	@Length(max=20)
	private String companyName;
	
	private String displayName;
	private String aspect;
	@Min(value=0)
	private Integer broker;
	private String companyPhone;
	private String website;
	private Date companySince;
	private String tempID;
	private int companyStatus;
	private int enhancedPassword;
	private String companyHours;
	private int issuesBroker;
	private int issuescompany;
	private int issueFrequency;
	private String industry;
	private String companySize;
	private int actualCompanySize;
	private String salesNotes;
	private int customCompany;
	private String groupID;
	private Date deactivationDate;
	private int deactivationID;
	private int login;
	private String producer;
	private String specialDomain;
	private String addedBy;
	private String channel;
	private int directID;
	private int resellerID;
	private int parentID;
	private int familiesID;
	private String referral;
	private int tally;
	private int optOut;
	private int optOutWelcome;
	private int newsletterID;
	private int newsletterPrivateLabel;
	private String officeLocation;
	private String partnercompanyType;
	private int marketID;
	private String marketCode;
	private Timestamp suspended;
	private String marketingCampaign;
	private int marketingFree;
	private int avoidTerms;
	private String custom1;
	private String custom2;
	private String custom3;
	private String custom4;
	private String custom5;
	private Timestamp customDate;
	private int noReporting;
	private int noTerms;
	private Date expiryDate;
	private int partnerAdmin;
	private int level;
	private String brainID;
	private String tokenID;
	private String subscriptionID;
	private int posters;
	private int complyLinks;
	private int resources;
	private int newLook;
	private int customStyle;
	private int setupFee;
	private int customerSuccessManager;
	private int trial;
	private int upsellLearn;
	private String salesRep;
	private int exported;
	private int directLanding;
	private String revenue;
	private int workplaceUsers;
	private int tempcompanyStatus;
	private Date renewalDate;
	private int reManager;
	private int partnerManager;
	private int autoWelcomeEmail;
	private int contactAssignments;
	private String salesforceID;
	private String specialNote;
	private int sourceID;
}
