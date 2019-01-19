package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Note;

//This will be AUTO IMPLEMENTED by Spring into a Bean called NoteRepository
//CRUD refers Create, Read, Update, Delete

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

	//User findOne(String name);
	
	//@Query("SELECT t FROM Note t WHERE t.created_by = ?1")
	//public List<Note> findNotes(String createdBy);
	
	public Note findById(Integer id);
}