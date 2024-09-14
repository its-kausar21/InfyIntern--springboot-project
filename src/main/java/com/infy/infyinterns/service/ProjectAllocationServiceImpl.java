package com.infy.infyinterns.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.entity.Project;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.repository.ProjectRepository;

//import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;

@Service(value= "projectService")
@Transactional
public class ProjectAllocationServiceImpl implements ProjectAllocationService {
	
	@Autowired
	private MentorRepository mentorRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	
	
	@Override
	public Integer allocateProject(ProjectDTO project) throws InfyInternException {

		Optional<Mentor> optional=mentorRepository.findById(project.getMentorDTO().getMentorId());
		Mentor mentor=optional.orElseThrow(() -> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		
		if(mentor.getNumberOfProjectsMentored()>=3) {
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		}
		
		Project projectEntity=new Project();
		projectEntity.setIdeaOwner(project.getIdeaOwner());
		projectEntity.setProjectName(project.getProjectName());
		projectEntity.setReleaseDate(project.getReleaseDate());
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
		projectEntity.setMentor(mentor);
		
		projectEntity=projectRepository.save(projectEntity);
		return projectEntity.getProjectId();
	}

	
	@Override
	public List<MentorDTO> getMentors(Integer numberOfProjectsMentored) throws InfyInternException {
		
		List<Mentor> receivedMentorList=mentorRepository.findByNoOfProjectsMentored(numberOfProjectsMentored);
		if(receivedMentorList.isEmpty()) {
			throw new
			InfyInternException("Service.MENTOR_NOT_FOUND”");
		}
		
		List<MentorDTO> filteredMentorList=receivedMentorList.stream()
				.map(mentor -> new MentorDTO(mentor.getMentorId(),
						                       mentor.getMentorName(),
						                        mentor.getNumberOfProjectsMentored()))
				.collect(Collectors.toList());
		
		return filteredMentorList;
		
	}


	@Override
	public void updateProjectMentor(Integer projectId, Integer mentorId) throws InfyInternException {
		
		Optional<Mentor> optionalmentor=mentorRepository.findById(mentorId);
		Mentor mentor=optionalmentor.orElseThrow(()-> new InfyInternException("Service.MENTOR_NOT_FOUND"));
		if(mentor.getNumberOfProjectsMentored()>=3) {
			
			throw new InfyInternException("Service.CANNOT_ALLOCATE_PROJECT");
		}
		
		Optional<Project> projectDetails=projectRepository.findById(projectId);
		Project project=projectDetails.orElseThrow(()-> new InfyInternException("Service.PROJECT_NOT_FOUND"));
		mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()+1);
		project.setMentor(mentor);
	}

	@Override
	public void deleteProject(Integer projectId) throws InfyInternException {
		
		Optional<Project> optionalProject=projectRepository.findById(projectId);
		Project project=optionalProject.orElseThrow(()-> new InfyInternException("Service.PROJECT_NOT_FOUND"));
		if(project.getMentor()==null) {
			
			projectRepository.delete(project);
		}else {
			Mentor mentor=project.getMentor();
			
			mentor.setNumberOfProjectsMentored(mentor.getNumberOfProjectsMentored()-1);
			project.setMentor(null);
			projectRepository.delete(project);
		}
		
	}
}