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

	@Column(name="blockedAccount",nullable=false)
	private Integer blockedAccount;

	private Integer bounced;

	private Integer brokerId;

	@Column(name="client_hours")
	private String clientHours;

	@Column(name="client_id")
	private Integer clientId;

	@Column(name="client_name")
	private String clientName;

	@Column(name="client_status")
	private String clientStatus;

	@Column(name="codevalid", nullable=false)
	private String codevalid;

	@Column(name="contact_Type")
	private String contactType;

	@Temporal(TemporalType.DATE)
	private Date deactivationDate;

	@Column(name="deactivationID")
	private Integer deactivationId;

	@Column(name="decision_maker")
	private Integer decisionMaker;

	private Integer deleted;

	private String email;

	@Temporal(TemporalType.DATE)
	private Date expirationDate;

	private String fax;

	@Column(name="first_Name")
	private String firstName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date firstMail;

	@Lob
	private String firstMailMessage;

	private String firstMailSuccess;

	@Column(name="has_SPD")
	private Integer hasSPD;

	@Column(name="hrhID")
	private Integer hrhId;

	private Integer international;

	@Column(name="last_Name")
	private String lastName;

	@Column(name="learn_reminder")
	private Integer learnReminder;

	@Column(name="learn_sync")
	private Integer learnSync;

	private String location;

	private Integer mailStatus;

	@Temporal(TemporalType.TIMESTAMP)
	private Date mailTime;

	private Integer master;

	@Column(name="master_backup")
	private Integer masterBackup;

	@Column(name="mkdate", nullable=false)
	private String mkdate;

	private String mobile;

	private Integer modified;

	private String password;

	@Column(name="password_apps")
	private String passwordApps;

	@Column(name="password_enc")
	private String passwordEnc;

	@Column(name="password_reset")
	private Integer passwordReset;

	private String phone;

	@Column(name="phone_backup")
	private String phoneBackup;

	@Temporal(TemporalType.DATE)
	private Date reminder;

	private String salesforceID;

	@Column(name="search_help")
	private String searchHelp;

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

	private String tempId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date terms;

	private Integer testAccount;

	private String title;

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