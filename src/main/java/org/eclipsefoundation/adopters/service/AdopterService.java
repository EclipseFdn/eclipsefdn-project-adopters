package org.eclipsefoundation.adopters.service;

import java.util.List;

import org.eclipsefoundation.adopters.model.AdoptedProject;
import org.eclipsefoundation.adopters.model.Adopter;
import org.eclipsefoundation.adopters.model.Project;

public interface AdopterService {

	public List<Adopter> getAdopters();
	
	public List<AdoptedProject> getAdoptedProjects(List<Project> projects);
}
