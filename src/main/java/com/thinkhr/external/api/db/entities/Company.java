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
import javax.persistence.Transient;
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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thinkhr.external.api.helpers.JsonDateDeSerializer;

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

    @Column(name = "search_help") 
    private String searchHelp = "";

    @Column(name = "Client_Type"  , updatable=false) 
    private String companyType = "";

    @NotBlank
    @Column(name = "Client_Name") 
    private String companyName;

    @Column(name = "t1_display_name") 
    private String displayName;

    @Column(name = "aspect")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String aspect;

    @Column(name = "Broker") 
    private Integer broker;

    @NotBlank
    @Size(max = 12)
    @Column(name = "Client_Phone") 
    private String companyPhone;

    @Column(name = "Website")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String website;

    @NotNull
    @JsonDeserialize(using = JsonDateDeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Client_Since", nullable=false) 
    private Date companySince = new Date();

    @Column(name = "tempID") 
    private String tempID;

    @Column(name = "Client_Status") 
    private Integer companyStatus;

    @Column(name = "enhanced_password")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer enhancedPassword;

    @Column(name = "client_hours")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String companyHours;

    @Column(name = "issuesBroker") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String issuesBroker;

    @Column(name = "issuesClient") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer issuesCompany;

    @Column(name = "issue_frequency") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer issueFrequency;

    @NotBlank
    @Column(name = "industry") 
    private String industry;

    @NotBlank
    @Column(name = "companySize") 
    private String companySize;

    @Column(name = "actualCompanySize") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer actualCompanySize;

    @Column(name = "salesNotes") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String salesNotes;

    @Column(name = "customClient")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer customCompany;

    @Column(name = "groupID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String groupID;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "deactivationDate") 
    @Temporal(TemporalType.DATE)
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date deactivationDate;

    @Column(name = "deactivationID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer deactivationID;

    @Column(name = "login")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer login;

    @Column(name = "producer") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String producer;

    @Column(name = "specialDomain") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String specialDomain;

    @Column(name = "addedBy") 
    private String addedBy;

    @Column(name = "channel") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String channel;

    @Column(name = "directID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer directID;

    @Column(name = "resellerID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer resellerID;

    @Column(name = "parentID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer parentID;

    @Column(name = "familiesID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer familiesID;

    @Column(name = "referral") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String referral;

    @Column(name = "tally")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer tally;

    @Column(name = "optOut") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer optOut;

    @Column(name = "optOutWelcome") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer optOutWelcome;

    @Column(name = "newsletterID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer newsletterID;

    @Column(name = "newsletterPrivateLabel") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer newsletterPrivateLabel;

    @Column(name = "officeLocation") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String officeLocation;

    @Column(name = "partnerClientType") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String partnerCompanyType;

    @Column(name = "offering") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String offering;

    @Column(name = "marketID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer marketID;

    @Column(name = "marketCode") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String marketCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "suspended") 
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date suspended;

    @Column(name = "marketingCampaign") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String marketingCampaign;

    @Column(name = "marketingFree") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer marketingFree;

    @Column(name = "avoidTerms")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer avoidTerms;

    @Column(name = "t1_customfield1") 
    private String custom1;

    @Column(name = "t1_customfield2") 
    private String custom2;

    @Column(name = "t1_customfield3") 
    private String custom3;

    @Column(name = "t1_customfield4") 
    private String custom4;

    @Column(name = "custom5") 
    private String custom5;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "customDate") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Timestamp customDate;

    @Column(name = "noReporting")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer noReporting;

    @Column(name = "noTerms")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer noTerms;

    @Column(name = "expiryDate") 
    @Temporal(TemporalType.DATE)
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date expiryDate;

    @Column(name = "partnerAdmin") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer partnerAdmin;

    @Column(name = "level") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer level;

    @Column(name = "brainID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String brainID;

    @Column(name = "tokenID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String tokenID;

    @Column(name = "subscriptionID") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String subscriptionID;

    @Column(name = "posters")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer posters;

    @Column(name = "complyLinks")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer complyLinks;

    @Column(name = "resources") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer resources;

    @Column(name = "newLook")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer newLook;

    @Column(name = "customStyle")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer customStyle;

    @Column(name = "setup_fee") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer setupFee;

    @Column(name = "customerSuccessManager") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer customerSuccessManager;

    @Column(name = "trial") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer trial;

    @Column(name = "upsellLearn")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer upsellLearn;

    @Column(name = "sales_rep") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String salesRep;

    @Column(name = "exported")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer exported;

    @Column(name = "direct_landing") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer directLanding;

    @Column(name = "revenue") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private String revenue;

    @Column(name = "workplaceUsers") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer workplaceUsers;

    @Column(name = "temp_Client_Status") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer tempCompanyStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    @Column(name = "Renewal_Date") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date renewalDate;

    @Column(name = "re_manager") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer reManager;

    @Column(name = "partner_manager") 
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer partnerManager;

    @Column(name = "auto_welcome_email")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer autoWelcomeEmail;

    @Column(name = "contact_assignments")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer contactAssignments;

    @Column(name = "salesforceID") 
    private String salesforceID;

    @Column(name = "special_note", nullable=false) 
    private String specialNote = ""; 

    @Column(name = "sourceID")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer sourceId;

    @Column(name = "t1_is_active", updatable= false)
    @JsonIgnore
    private Integer isActive = 1;

    @Column(name = "t1_parent_company_id")
    private Integer parentCompanyId;

    @Column(name = "t1_configuration_id")
    private Integer configurationId;

    @Column(name = "t1_email_template_id")
    private String emailTemplateId;
    
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private String welcomeSenderEmailSubject;
    
    @Transient
    @JsonProperty(access = Access.WRITE_ONLY)
    private String welcomeSenderEmail;

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
        return this.getCompanyId() != null  && this.getCompanyId().equals(this.getBroker());
    }
    
    /**
     * Determines whether this company has learn admin SKUs
     * 
     * TODO: Implement Logic
     * @return
     */
    @JsonIgnore
    public boolean hasLearnAdminSKU() {
        return false;
    }
    
    /**
     * Determines whether this company has Student Permission Only
     * 
     * TODO: Implement Logic
     * @return
     */
    @JsonIgnore
    public boolean hasStudentPermissionOnly() {
        return true;
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
