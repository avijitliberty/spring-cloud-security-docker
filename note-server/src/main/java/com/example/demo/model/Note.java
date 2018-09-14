package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity  // This tells Hibernate to make a table out of this class
@Table(name = "note")
public class Note {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "note_id")
	private int id;
	// Topic
	@Column(name = "topic")
	private String topic;
	// Subject
	@Column(name = "subject")
	private String subject;
	// Body
	@Column(name = "body")
	private String body;
		
	public Note(int id, String topic, String subject, String body) {
		super();
		this.id = id;
		this.topic = topic;
		this.subject = subject;
		this.body = body;
	}
	public Note() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "Note [topic=" + topic + ", subject=" + subject + ", body=" + body + "]";
	}
	
}
