package com.infy.infyinterns;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.infyinterns.dto.MentorDTO;
import com.infy.infyinterns.dto.ProjectDTO;
import com.infy.infyinterns.entity.Mentor;
import com.infy.infyinterns.entity.Project;
import com.infy.infyinterns.exception.InfyInternException;
import com.infy.infyinterns.repository.MentorRepository;
import com.infy.infyinterns.service.ProjectAllocationService;
import com.infy.infyinterns.service.ProjectAllocationServiceImpl;

@SpringBootTest
public class InfyInternsApplicationTests {

	@Mock
	private MentorRepository mentorRepository;

	@InjectMocks
	private ProjectAllocationService projectAllocationService = new ProjectAllocationServiceImpl();

	@Test
	public void allocateProjectCannotAllocateTest() throws Exception {

		Mentor mentor=new Mentor();
		mentor.setMentorId(1002);
		mentor.setNumberOfProjectsMentored(3);
		MentorDTO mentorDTO=new MentorDTO();
		mentorDTO.setMentorId(1002);
		
		ProjectDTO  projectDTO=new ProjectDTO();
		projectDTO.setProjectName("citymapper");
		projectDTO.setIdeaOwner(10025);
		projectDTO.setReleaseDate(LocalDate.now().plusMonths(3));
		projectDTO.setMentorDTO(mentorDTO);
		
		Mockito.when(mentorRepository.findById(Mockito.anyInt()))
		.thenReturn(Optional.of(mentor));
		
		InfyInternException exception = Assertions.assertThrows(InfyInternException.class,() -> projectAllocationService.allocateProject(projectDTO));
		
		Assertions.assertEquals("Service.CANNOT_ALLOCATE_PROJECT", exception.getMessage());
		
		

	}

	
	public void allocateProjectMentorNotFoundTest() throws Exception {
	
			Mentor mentor=new Mentor();
			mentor.setMentorId(1015);
			mentor.setNumberOfProjectsMentored(2);
			mentor.setMentorName("Abc");
			
			MentorDTO mentorDTO=new MentorDTO();
			mentorDTO.setMentorId(1012);
			
			ProjectDTO projectDTO=new ProjectDTO();
			projectDTO.setProjectName("cityMapper");
			projectDTO.setIdeaOwner(10025);
			projectDTO.setReleaseDate(LocalDate.now().plusMonths(3));
			projectDTO.setMentorDTO(mentorDTO);
			
			Mockito.when(mentorRepository.findById(Mockito.anyInt()))
			.thenReturn(Optional.empty());
			
			InfyInternException exception=Assertions.assertThrows(InfyInternException.class, () -> projectAllocationService.allocateProject(projectDTO));
			
			Assertions.assertEquals("Service.MENTOR_NOT_FOUND", exception.getMessage());
			
	}
}