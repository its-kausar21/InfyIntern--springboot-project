package com.infy.infyinterns.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.infy.infyinterns.entity.Project;

public interface ProjectRepository extends CrudRepository<Project, Integer>
{

    // add methods if required
	List<Project> findByIdeaOwner(Integer ideaOwner);

}
