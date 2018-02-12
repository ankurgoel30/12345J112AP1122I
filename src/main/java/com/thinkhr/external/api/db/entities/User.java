package com.thinkhr.external.api.db.entities;

import static com.thinkhr.external.api.ApplicationConstants.VALID_FORMAT_YYYY_MM_DD;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;


/**
 * 
 * Database entity object for User
 * 
 * Name of database table is contacts
 * 
 */
@Entity
@Data
@Table(name="contacts")
@Where(clause="active=1")
@DynamicUpdate
@DynamicInsert
@JsonInclude(Include.NON_EMPTY) 
public class User implements SearchableEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="contactID")
    private Integer userId;

    @NotBlank
    @Column(name="First_Name", nullable=false)
    private String firstName;

    @NotBlank
    @Column(name="Last_Name")
    private String lastName;

    @Column(name="UserName")
    private String userName;

    @NotBlank
    @Email
    @Column(name="Email")
    private String email;

    @NotBlank
    @Column(name = "client_name", updatable = false)
    private String companyName;

    @Column(name = "brokerID" , updatable = false)
    private Integer brokerId;

    @Column(name="client_id" , updatable = false)
    private Integer companyId;

    @Column(name="t1_roleId")
    private Integer roleId;

    @Column(name="accountID")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String accountId;

    @Column(name="Mobile")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String mobile;

    @Column(name="Phone")
    @Size(max = 20)
    private String phone;

    @Column(name="Fax")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String fax;

    @Column(name="Location")
    private String location;

    @JsonIgnore
    @Column(name="Password")
    private String password;

    @Column(name="password_apps")
    @JsonIgnore
    private String passwordApps;

    @Column(name="password_enc")
    @JsonIgnore
    private String passwordEnc;

    @Column(name="password_reset")
    @JsonIgnore
    private Integer passwordReset;

    @Column(name="contact_Type")
    private String userType;

    @NotNull
    @Column(name="search_help")
    private String searchHelp = ""; 

    @Column(name="Title")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String title;

    @NotNull
    @Column(name="blockedAccount", nullable=false)
    private Integer blockedAccount = 0;

    @NotNull
    @Column(name="mkdate", nullable=false)
    @JsonProperty(access = Access.WRITE_ONLY)
    private String mkdate = "";

    @JsonIgnore
    @Column(name = "bounced")
    private Integer bounced;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "activationDate")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date activationDate;

    @NotNull
    @Column(name="codevalid", nullable=false)
    @JsonProperty(access = Access.WRITE_ONLY)
    private String codevalid = "";

    @Column(name = "active", updatable = false)
    @JsonIgnore
    private Integer isActive = 1;

    @Column(name = "addedBy")
    private String addedBy;

    @Column(name="client_hours")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String clientHours;

    @Column(name="client_status")
    private String clientStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deactivationDate")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date deactivationDate;

    @Column(name="deactivationID")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer deactivationId;

    @Column(name="decision_maker")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer decisionMaker;

    @Column(name="deleted")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer deleted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="expirationDate")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date expirationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="firstMail")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date firstMail;

    @Lob
    @Column(name="firstMailMessage")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String firstMailMessage;

    @Column(name="firstMailSuccess")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String firstMailSuccess;

    @Column(name="has_SPD")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer hasSPD;

    @Column(name="hrhID")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer hrhId;

    @Column(name="International")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer international;

    @Column(name="learn_reminder")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer learnReminder;

    @Column(name="learn_sync")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer learnSync;

    @Column(name="mailStatus")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer mailStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="mailTime")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date mailTime;

    @Column(name="master")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer master;

    @Column(name="master_backup")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer masterBackup;

    @Column(name="modified")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer modified;

    @Column(name="Phone_Backup")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String phoneBackup;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="reminder")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date reminder;

    @Column(name="salesforceID")
    @JsonIgnore
    private String salesforceID;

    @Column(name="specialBlast")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String specialBlast;

    @Column(name="t1_customfield1")
    private String customField1;

    @Column(name="t1_customfield2")
    private String customField2;

    @Column(name="t1_customfield3")
    private String customField3;

    @Column(name="t1_customfield4")
    private String customField4;


    @Column(name="tempID")
    private String tempId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="terms")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Date terms;

    @Column(name="testAccount")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Integer testAccount;

    @NotNull
    @Column(name="update_password", nullable = false)
    @JsonProperty(access = Access.WRITE_ONLY)
    private String updatePassword = "";

    @Override
    @JsonIgnore
    public List<String> getSearchFields() {
        List<String> searchColumns = new ArrayList<String>();
        searchColumns.add("userName");
        searchColumns.add("title");
        searchColumns.add("searchHelp");
        searchColumns.add("phoneBackup");

        searchColumns.add("phone");
        searchColumns.add("mobile");
        searchColumns.add("lastName");
        searchColumns.add("firstMailMessage");

        searchColumns.add("firstName");
        searchColumns.add("fax");
        searchColumns.add("email");
        searchColumns.add("companyName");
        return searchColumns;
    }

    @Override
    @JsonIgnore
    public String getNodeName() {
        return "user";
    }

    @Override
    @JsonIgnore
    public String getMultiDataNodeName() {
        return "users";
    }


}