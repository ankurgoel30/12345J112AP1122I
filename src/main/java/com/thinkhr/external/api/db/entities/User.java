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

import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class User implements SearchableEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="contactID")
	private Integer userId;

	@NotNull
	@Column(name="First_Name", nullable=false)
	private String firstName;
	
	@NotNull
	@Column(name="Last_Name")
	private String lastName;
	
	@NotNull
	@Column(name="UserName")
	private String userName;

	@NotNull
	@Email
	@Column(name="Email")
	private String email;
	
	@NotNull
	@Column(name="t1_roleId")
	private Integer roleId;
	
	@Column(name="accountID")
	private String accountId;

	@NotNull
	@Column(name="blockedAccount",nullable=false)
	private Integer blockedAccount;
	
	@Column(name = "brokerID")
	private Integer brokerId;
	
	@Column(name="client_id")
	private Integer companyId;

	@Column(name="client_name")
	private String companyName;
	
	@Column(name="contact_Type")
	private String userType;
	
	@Column(name="Fax")
	private String fax;
	
	@Column(name="Location")
	private String location;
	
	@NotNull
	@Column(name="mkdate", nullable=false)
	private String mkdate;

	@Column(name="Mobile")
	private String mobile;
	
	@Column(name="Phone")
	private String phone;
	
	@NotNull
	@Column(name="search_help")
	private String searchHelp;
	
	@Column(name="Title")
	private String title;
	
	@JsonIgnore
	@Column(name = "bounced")
	private Integer bounced;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "activationDate")
	private Date activationDate;

	@NotNull
	@Column(name="codevalid", nullable=false)
	private String codevalid;

	@Column(name = "active")
	@JsonIgnore
	private Integer isActive;

	@Column(name = "addedBy")
	private String addedBy;

	@Column(name="client_hours")
	private String clientHours;

	@Column(name="client_status")
	private String clientStatus;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deactivationDate")
	private Date deactivationDate;

	@Column(name="deactivationID")
	private Integer deactivationId;

	@Column(name="decision_maker")
	private Integer decisionMaker;

	@Column(name="deleted")
	private Integer deleted;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="expirationDate")
	private Date expirationDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="firstMail")
	private Date firstMail;

	@Lob
	@Column(name="firstMailMessage")
	private String firstMailMessage;

	@Column(name="firstMailSuccess")
	private String firstMailSuccess;

	@Column(name="has_SPD")
	private Integer hasSPD;

	@Column(name="hrhID")
	private Integer hrhId;

	@Column(name="International")
	private Integer international;

	@Column(name="learn_reminder")
	private Integer learnReminder;

	@Column(name="learn_sync")
	private Integer learnSync;

	@Column(name="mailStatus")
	private Integer mailStatus;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="mailTime")
	private Date mailTime;

	@Column(name="master")
	private Integer master;

	@Column(name="master_backup")
	private Integer masterBackup;

	@Column(name="modified")
	private Integer modified;

	@JsonIgnore
	@Size(min = 1, max = 25)
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

	@Column(name="Phone_Backup")
	private String phoneBackup;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = VALID_FORMAT_YYYY_MM_DD)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="reminder")
	private Date reminder;

	@Column(name="salesforceID")
	@JsonIgnore
	private String salesforceID;

	@Column(name="specialBlast")
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
	private Date terms;

	@Column(name="testAccount")
	private Integer testAccount;

	@NotNull
	@Column(name="update_password", nullable = false)
	@Size(min = 1, max = 1)
	private String updatePassword;
	
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
		searchColumns.add("clientName");
		return searchColumns;
	}
}