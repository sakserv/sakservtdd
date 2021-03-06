package com.sakserv.sis.studentinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sakserv.sis.DateUtil;

abstract public class SessionTest {

	private Session session;
	
	private static final String DEPARTMENT_CODE = "ENGL";
	private static final String COURSE_NUMBER = "101";
	private static final Date START_DATE = DateUtil.createDate(2003, 1, 6); // year, month, day
	private static final int COURSE_CREDITS = 3; 
	
	@Before
	public void setUp() {
		session = createSession(new Course(DEPARTMENT_CODE, COURSE_NUMBER),
				START_DATE);
		session.setNumberOfCredits(COURSE_CREDITS);
	}
	
	abstract protected Session createSession(Course course, Date startDate);
	
	@Test
	public void testCreate() {
		assertEquals(DEPARTMENT_CODE, session.getDepartment());
		assertEquals(COURSE_NUMBER, session.getNumber());
		assertEquals(0, session.getNumberOfStudents());
		assertEquals(START_DATE, session.getStartDate());
		
		final String firstStudentName = "Jane Doe";
		Student firstStudent = new Student(firstStudentName);
		assertEquals(firstStudentName, firstStudent.getName());
		assertEquals("Jane", firstStudent.getFirstName());
		assertEquals("Doe", firstStudent.getLastName());
		assertEquals("", firstStudent.getMiddleName());
		
	}
	
	@Test
	public void testEnrollStudent() {

		Student student1 = new Student("Cain DiVoe");
		session.enroll(student1);
		assertEquals(COURSE_CREDITS, student1.getCreditHours());
		assertEquals(1, session.getNumberOfStudents());
		assertEquals(student1, session.getStudentByIndex(0));
		
		Student student2 = new Student("Coralee DeVaughn");
		session.enroll(student2);
		assertEquals(COURSE_CREDITS, student1.getCreditHours());
		assertEquals(2, session.getNumberOfStudents());
		
		assertEquals(student1, session.getStudentByIndex(0));
		assertEquals(student2, session.getStudentByIndex(1));
		
	}
	
	@Test
	public void testSessionComparable() {
		final Date date = new Date();
		Session firstSession = createSession(new Course("CMSC", "101"), date);
		Session secondSession = createSession(new Course("ENGL", "101"), date);
		
		assertTrue(firstSession.compareTo(secondSession) < 0);
		assertTrue(secondSession.compareTo(firstSession) > 0);
		
		Session thirdSession = createSession(new Course("CMSC", "101"), date);
		assertEquals(0, firstSession.compareTo(thirdSession));
		
		Session fourthSession = createSession(new Course("CMSC", "210"), date);
		assertTrue(thirdSession.compareTo(fourthSession) < 0);
		assertTrue(fourthSession.compareTo(thirdSession) > 0);
		
	}
	
	@Test
	public void testAverageGpaForPartTimeStudents() {
		session.enroll(createFullTimeStudent());
		
		Student partTimer1 = new Student("1");
		partTimer1.addGrade(Student.Grade.A);
		session.enroll(partTimer1);
		
		session.enroll(createFullTimeStudent());
		
		Student partTimer2 = new Student("2");
		partTimer2.addGrade(Student.Grade.B);
		session.enroll(partTimer2);
		
		assertEquals(3.5, session.averageGpaForPartTimeStudents(), 0.05);
		
		
	}
	
	@Test
	public void testIterate() {
		enrollStudents(session);
		
		List<Student> results = new ArrayList<Student>();
		for (Student student: session) {
			results.add(student);
		}
		assertEquals(session.getAllStudents(), results);
	}
	
	@Test
	public void testSessionUrl() throws SessionException {
		final String url = "http://course.langrsoft.com/cmsc300";
		session.setUrl(url);
		assertEquals(url, session.getUrl().toString());
	}
	
	@Test
	public void testInvalidSessionUrl() {
		final String url = "httsp://course.langrsoft.com/cmsc300";
		try {
			session.setUrl(url);
			fail("expected SessionException due to bad protocol");
		}
		catch (SessionException expectedException) {
			Throwable cause = expectedException.getCause();
			assertEquals(MalformedURLException.class, cause.getClass());
		}
	}
	
	private void enrollStudents(Session session) {
		session.enroll(new Student("1"));
		session.enroll(new Student("2"));
		session.enroll(new Student("3"));
	}
	
	private Student createFullTimeStudent() {
		Student student = new Student("a");
		student.addCreditHours(Student.MIN_FULL_TIME_CREDITS);
		return student;
	}
}
