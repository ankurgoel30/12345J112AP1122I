package com.thinkhr.external.api.db.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * 
 * Database entity object for Company
 * 
 * Name of database table is clients
 * 
 * @author Ajay Jain
 * @since 2017-11-05
 *
 */

@Entity
@Table(name = "clients")
@Data
@Where(clause="t1_is_active=1")
@DynamicUpdate
@DynamicInsert
@JsonInclude(Include.NON_EMPTY) 
public class Company implements SearchableEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "clientID") 
    private Integer companyId;

    @NotNull
    @Column(name = "search_help") 
    private String searchHelp;

    @NotNull
    @Size(min=1)
    @Column(name = "Client_Type") 
    private String companyType;

    @NotBlank
    @Column(name = "Client_Name") 
    private String companyName;

    @Column(name = "display_name") 
    private String displayName;

    @Column(name = "aspect") 
    private String aspect;

    @Column(name = "Broker") 
    private Integer broker;

    @NotBlank
    @Column(name = "Client_Phone") 
    private String companyPhone;

    @Column(name = "Website") 
    private String website;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Client_Since", nullable=false) 
    private Date companySince;

    @Column(name = "tempID") 
    private String tempID;

    @Column(name = "Client_Status") 
    private Integer companyStatus;

    @Column(name = "enhanced_password") 
    private Integer enhancedPassword;

    @Column(name = "client_hours") 
    private String companyHours;

    @Column(name = "issuesBroker") 
    private String issuesBroker;

    @Column(name = "issuesClient") 
    private Integer issuesCompany;

    @Column(name = "issue_frequency") 
    private Integer issueFrequency;

    @NotBlank
    @Column(name = "industry") 
    private String industry;

    @NotBlank
    @Column(name = "companySize") 
    private String companySize;

    @Column(name = "actualCompanySize") 
    private Integer actualCompanySize;

    @Column(name = "salesNotes") 
    private String salesNotes;

    @Column(name = "customClient") 
    private Integer customCompany;

    @Column(name = "groupID") 
    private String groupID;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "deactivationDate") 
    @Temporal(TemporalType.DATE)
    private Date deactivationDate;

    @Column(name = "deactivationID") 
    private Integer deactivationID;

    @Column(name = "login") 
    private Integer login;

    @Column(name = "producer") 
    private String producer;

    @Column(name = "specialDomain") 
    private String specialDomain;

    @Column(name = "addedBy") 
    private String addedBy;

    @Column(name = "channel") 
    private String channel;

    @Column(name = "directID") 
    private Integer directID;

    @Column(name = "resellerID") 
    private Integer resellerID;

    @Column(name = "parentID") 
    private Integer parentID;

    @Column(name = "familiesID") 
    private Integer familiesID;

    @Column(name = "referral") 
    private String referral;

    @Column(name = "tally") 
    private Integer tally;

    @Column(name = "optOut") 
    private Integer optOut;

    @Column(name = "optOutWelcome") 
    private Integer optOutWelcome;

    @Column(name = "newsletterID") 
    private Integer newsletterID;

    @Column(name = "newsletterPrivateLabel") 
    private Integer newsletterPrivateLabel;

    @Column(name = "officeLocation") 
    private String officeLocation;

    @Column(name = "partnerClientType") 
    private String partnerCompanyType;

    @Column(name = "offering") 
    private String offering;

    @Column(name = "marketID") 
    private Integer marketID;

    @Column(name = "marketCode") 
    private String marketCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "suspended") 
    @Temporal(TemporalType.TIMESTAMP)
    private Date suspended;

    @Column(name = "marketingCampaign") 
    private String marketingCampaign;

    @Column(name = "marketingFree") 
    private Integer marketingFree;

    @Column(name = "avoidTerms") 
    private Integer avoidTerms;

    @Column(name = "custom1") 
    private String custom1;

    @Column(name = "custom2") 
    private String custom2;

    @Column(name = "custom3") 
    private String custom3;

    @Column(name = "custom4") 
    private String custom4;

    @Column(name = "custom5") 
    private String custom5;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "customDate") 
    private Timestamp customDate;

    @Column(name = "noReporting") 
    private Integer noReporting;

    @Column(name = "noTerms") 
    private Integer noTerms;

    @Column(name = "expiryDate") 
    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    @Column(name = "partnerAdmin") 
    private Integer partnerAdmin;

    @Column(name = "level") 
    private Integer level;

    @Column(name = "brainID") 
    private String brainID;

    @Column(name = "tokenID") 
    private String tokenID;

    @Column(name = "subscriptionID") 
    private String subscriptionID;

    @Column(name = "posters") 
    private Integer posters;

    @Column(name = "complyLinks") 
    private Integer complyLinks;

    @Column(name = "resources") 
    private Integer resources;

    @Column(name = "newLook") 
    private Integer newLook;

    @Column(name = "customStyle") 
    private Integer customStyle;

    @Column(name = "setup_fee") 
    private Integer setupFee;

    @Column(name = "customerSuccessManager") 
    private Integer customerSuccessManager;

    @Column(name = "trial") 
    private Integer trial;

    @Column(name = "upsellLearn") 
    private Integer upsellLearn;

    @Column(name = "sales_rep") 
    private String salesRep;

    @Column(name = "exported") 
    private Integer exported;

    @Column(name = "direct_landing") 
    private Integer directLanding;

    @Column(name = "revenue") 
    private String revenue;

    @Column(name = "workplaceUsers") 
    private Integer workplaceUsers;

    @Column(name = "temp_Client_Status") 
    private Integer tempCompanyStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "Renewal_Date") 
    private Date renewalDate;

    @Column(name = "re_manager") 
    private Integer reManager;

    @Column(name = "partner_manager") 
    private Integer partnerManager;

    @Column(name = "auto_welcome_email") 
    private Integer autoWelcomeEmail;

    @Column(name = "contact_assignments") 
    private Integer contactAssignments;

    @Column(name = "salesforceID") 
    private String salesforceID;

    @NotNull
    @Column(name = "special_note", nullable=false) 
    private String specialNote; 

    @Column(name = "sourceID") 
    private Integer sourceId;

    @Column(name = "t1_is_active", updatable= false)
    @JsonIgnore
    private Integer isActive = 1;

    @Column(name = "t1_parent_company_id")
    private Integer parentCompanyId;

    @Column(name = "t1_configuration_id")
    private Integer configurationId;

    @Column(name = "t1_customfield1")
    private String customfield1;

    @Column(name = "t1_customfield2")
    private String customField2;

    @Column(name = "t1_customfield3")
    private String customField3;

    @Column(name = "t1_customfield4")
    private String customField4;

    @Column(name = "t1_display_name")
    private String customField5;

    @Column(name = "t1_email_template_id")
    private String emailTemplateId;

    @NotNull
    @Valid
    @OneToOne(mappedBy = "company",cascade=CascadeType.ALL,fetch=FetchType.LAZY )
    private Location location ;
    
    
    /**
     * Determines whether this company is broker/partner company
     * 
     * @return
     */
    @JsonIgnore
    public boolean isBrokerCompany() {
        return this.getBroker() == null;
    }

    /**
     * Returns fields where "SearchSpec" searching acts on.
     * 
     */
    @Override
    @JsonIgnore
    public List<String> getSearchFields() {
        List<String> searchColumns = new ArrayList<String>();
        searchColumns.add("searchHelp");
        searchColumns.add("companyName");
        searchColumns.add("companyPhone");
        searchColumns.add("website");
        return searchColumns;
    }

    @Override
    @JsonIgnore
    public String getNodeName() {
        return "company";
    }

    @Override
    @JsonIgnore
    public String getMultiDataNodeName() {
        return "companies";
    }
    
  
}
