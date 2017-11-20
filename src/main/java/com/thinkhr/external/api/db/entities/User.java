package com.thinkhr.external.api.db.entities;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class User implements SearchableEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer contactId;

	@Column(name="accountID")
	private String accountId;

	@Temporal(TemporalType.DATE)
	private Date activationDate;

	private Integer active;

	private String addedBy;

	@NotNull
	@Column(name="blockedAccount",nullable=false)
	private Integer blockedAccount;

	@JsonIgnore
	private Integer bounced;

	private Integer brokerId;

	@Column(name="client_hours")
	@JsonIgnore
	private String clientHours;

	@Column(name="client_id")
	private Integer clientId;

	@Column(name="client_name")
	private String clientName;

	@Column(name="client_status")
	@JsonIgnore
	private String clientStatus;

	@NotNull
	@Column(name="codevalid", nullable=false)
	private String codevalid;

	@Column(name="contact_Type")
	private String contactType;

	@Temporal(TemporalType.DATE)
	private Date deactivationDate;

	@Column(name="deactivationID")
	@JsonIgnore
	private Integer deactivationId;

	@Column(name="decision_maker")
	@JsonIgnore
	private Integer decisionMaker;

	private Integer deleted;

	private String email;

	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date expirationDate;

	private String fax;
	
	@Transient
	@JsonInclude(Include.NON_NULL)
	private Company company;

	@NotNull
	@Size(min = 1, max = 50)
	@Column(name="first_Name")
	private String firstName;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date firstMail;

	@Lob
	@JsonIgnore
	private String firstMailMessage;

	@JsonIgnore
	private String firstMailSuccess;

	@Column(name="has_SPD")
	@JsonIgnore
	private Integer hasSPD;

	@Column(name="hrhID")
	@JsonIgnore
	private Integer hrhId;

	@JsonIgnore
	private Integer international;

	@Column(name="last_Name")
	private String lastName;

	@Column(name="learn_reminder")
	@JsonIgnore
	private Integer learnReminder;

	@JsonIgnore
	@Column(name="learn_sync")
	private Integer learnSync;

	private String location;

	@JsonIgnore
	private Integer mailStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date mailTime;

	@JsonIgnore
	private Integer master;

	@Column(name="master_backup")
	@JsonIgnore
	private Integer masterBackup;

	@NotNull
	@Column(name="mkdate", nullable=false)
	private String mkdate;

	private String mobile;

	private Integer modified;

	@JsonIgnore
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

	private String phone;

	@Column(name="phone_backup")
	@JsonIgnore
	private String phoneBackup;

	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date reminder;

	@JsonIgnore
	private String salesforceID;

	@NotNull
	@Size(min = 1)
	@Column(name="search_help")
	private String searchHelp;

	@JsonIgnore
	private String specialBlast;

	@Column(name="t1_customfield1")
	private String t1Customfield1;

	@Column(name="t1_customfield2")
	private String t1Customfield2;

	@Column(name="t1_customfield3")
	private String t1Customfield3;

	@Column(name="t1_customfield4")
	private String t1Customfield4;

	@Column(name="t1_roleId")
	private Integer t1RoleId;

	@JsonIgnore
	private String tempId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date terms;

	@JsonIgnore
	private Integer testAccount;

	private String title;

	@NotNull
	@Column(name="update_password", nullable=false)
	private String updatePassword;

	private String userName;

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