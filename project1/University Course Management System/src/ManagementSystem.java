import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.*;


public class ManagementSystem {
	Connection con;
	
	public ManagementSystem() {
		try {
			Class.forName( "oracle.jdbc.driver.OracleDriver" );
		}
		catch ( ClassNotFoundException e ) {
			e.printStackTrace();
		}
		try {
			con =
					DriverManager.getConnection( "jdbc:oracle:thin:@claros.cs.purdue.edu:1524:strep","zhou267", "fLL4533H" );
		}
		catch ( SQLException e ){
			e.printStackTrace();
		}
	}
	
	
	public void login() {
		String user_type = null;
		String user_name = null;
		int user_id = -1;
		
		while(user_type == null) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Please identify yourself by entering user id:");
			user_id = scanner.nextInt();
			scanner.nextLine();
			
			String query = "Select * from Users where user_id="+user_id;
			
			
			try {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery( query );
				
				// Get the user type based on user_id
				if( rs.next() ) {
					user_type = rs.getString("user_type");
					user_name = rs.getString("user_name");
				}
				
				if (user_type == null) {
					System.out.println("User id not found.");
				}
				
				rs.close();
				stmt.close();
			}
			catch ( SQLException e ) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Hello "+user_name+"."+" You are logged in as "+user_type+".");
		
		if (user_type.equals("Admin")) {
			showToolsForAdmin(user_id);
		}
		else if (user_type.equals("Faculty")) {
			showToolsForFaculty(user_id);
		}
		else if (user_type.equals("Student")) {
			showToolsForStudent(user_id);
		}
	}
	
	
	public void showToolsForFaculty(int faculty_id) {
		int selection = -1;
		
		while (selection != 0) {
			System.out.println("Main Menu");
			System.out.println("===================================");
			System.out.println("You have following tools available:");
			System.out.println("===================================");
			System.out.println("1: Create/Modify a course.");
			System.out.println("2: Assign students to a course.");
			System.out.println("3: Create/Modify an evaluation.");
			System.out.println("4: Enter grades.");
			System.out.println("5: Get report of classes.");
			System.out.println("6: Get report of students and grades.");
			System.out.println("Or 0: EXIT THE SYSTEM.");
			System.out.println("Please choose one from above:");
			
			Scanner scanner = new Scanner(System.in);
			
			selection = scanner.nextInt();
			scanner.nextLine();
			
			if (selection >= 0 && selection <=6) {
				if (selection == 0) {
					System.out.println("Bye!");
					System.exit(0);
				}
				else if(selection == 1) {
					// create/modify a course
					int mode = -1;
					while (mode != 0 && mode != 1) {
						System.out.println("Enter 0 to create a course, 1 to modify a course");
						
						mode = scanner.nextInt();
						scanner.nextLine();
						
						if (mode == 0) {
							// create a course
							try{
								
								System.out.println("Enter course name:");
								String course_name = scanner.nextLine();
								
								System.out.println("Enter semester:");
								String semester = scanner.nextLine();
								
								System.out.println("Enter year:");
								int year = scanner.nextInt();
								scanner.nextLine();
								
								System.out.println("Enter meets time, i.e 08:30");
								String time = scanner.nextLine();
								
								System.out.println("Enter room:");
								String room = scanner.nextLine();
								
								String createCourse = "insert into Course values (course_sequence.nextval, '"+
										course_name+"','"+semester+"',"+year+",to_date('"+time+"','HH24:MI'),'"+
										room+"',"+faculty_id+")";
								
								System.out.println(createCourse);
								
								Statement stmt = con.createStatement();
								stmt.executeUpdate(createCourse);
								stmt.close();
							}
							catch(Exception e) {
								// e.printStackTrace();
								System.out.println("Create failed. Please check you information.");
								mode = -2;
							}
						}
						else if (mode == 1) {
							// modify a course
							int selected_cid = -1;
							
							String query = "Select course_id, course_name from Course where faculty_id="+faculty_id;
							try {
								Statement stmt = con.createStatement();
								ResultSet rs = stmt.executeQuery( query );
								System.out.println("Enter corresponding course_id to modify following courses.");
								System.out.println("Course ID--Course Name");
								while ( rs.next() ) {
									int course_id = rs.getInt( "course_id" );
									String course_name = rs.getString("course_name");
									System.out.println(course_id+"--------"+course_name);
								}
								rs.close();
								stmt.close();
							}
							catch ( SQLException e ) {
								e.printStackTrace();
								mode = -2;
							}
							
							try{
								
								selected_cid = scanner.nextInt();
								scanner.nextLine();
								
								System.out.println("Enter a new course name:");
								String course_name = scanner.nextLine();
								
								System.out.println("Enter a new semester:");
								String semester = scanner.nextLine();
								
								System.out.println("Enter a new year:");
								int year = scanner.nextInt();
								scanner.nextLine();
								
								System.out.println("Enter a new meets time, i.e 08:30");
								String time = scanner.nextLine();
								
								System.out.println("Enter a new room:");
								String room = scanner.nextLine();
								
								String modifyCourse = "update Course set course_name = '"+
										course_name+"', semester = '"+semester+"', year = "+year+", meets_at = to_date('"+time+"','HH24:MI'), room = '"+
										room+"' where course_id = "+selected_cid;
								
								System.out.println(modifyCourse);
								
								Statement stmt = con.createStatement();
								stmt.executeUpdate(modifyCourse);
								stmt.close();
							}
							catch(Exception e) {
								// e.printStackTrace();
								System.out.println("Modify course failed. Please check you information.");
								mode = -2;
							}
							
						}
						else {
							System.out.println("Invalid selection. Please re-enter.");
						}
					}
				}
				else if(selection == 2) {
					// assign students to a course
					int mode = -2;
					int selected_sid = -1;
					int selected_cid = -1;
					
					while (mode != 0) {
						
						String queryStudent = "Select user_id, user_name from Users where user_type='Student'";
						String queryCourse = "Select course_id, course_name from Course where faculty_id="+faculty_id;
						
						try {
							Statement stmt = con.createStatement();
							
							ResultSet rsStudent = stmt.executeQuery( queryStudent );
							
							System.out.println("Student ID--Student Name");
							while ( rsStudent.next() ) {
								int user_id = rsStudent.getInt( "user_id" );
								String user_name = rsStudent.getString("user_name");
								System.out.println(user_id+"--------"+user_name);
							}
							
							System.out.println("==================");
							
							
							ResultSet rsCourse = stmt.executeQuery( queryCourse );
							
							System.out.println("Course ID--Course Name");						
							while ( rsCourse.next() ) {
								int course_id = rsCourse.getInt( "course_id" );
								String course_name = rsCourse.getString("course_name");
								System.out.println(course_id+"--------"+course_name);
							}
							
							System.out.println("Enter corresponding student id.");
							selected_sid = scanner.nextInt();
							scanner.nextLine();
							
							System.out.println("Enter corresponding course id to be assigned.");
							selected_cid = scanner.nextInt();
							scanner.nextLine();
						
							rsStudent.close();
							rsCourse.close();
							stmt.close();
						}
						catch ( SQLException e ) {
							e.printStackTrace();
							mode = -2;
						}
						
						try{
							
							String assignCourse = "insert into Enroll values ("+
									selected_cid+", "+selected_sid+")";
							
							System.out.println(assignCourse);
							
							Statement stmt = con.createStatement();
							stmt.executeUpdate(assignCourse);
							stmt.close();
							
							mode = 0;
						}
						catch(Exception e) {
							// e.printStackTrace();
							System.out.println("Assign failed. Please check you information.");
							mode = -2;
						}
					}
					
				}
				else if(selection == 3) {
					// create/modify an evaluation
					int mode = -1;
					while (mode != 0 && mode != 1) {
						
						
						System.out.println("Enter 0 to create an evaluation, 1 to modify an evaluation");
						
						mode = scanner.nextInt();
						scanner.nextLine();
						
						if (mode == 0) {
							// create an evaluation
							
							int selected_cid = -1;
							try{
								
								String queryCourse = "Select course_id, course_name from Course where faculty_id="+faculty_id;
								
								try {
									Statement stmt = con.createStatement();
									
									ResultSet rsCourse = stmt.executeQuery( queryCourse );
									
									System.out.println("Course ID--Course Name");						
									while ( rsCourse.next() ) {
										int course_id = rsCourse.getInt( "course_id" );
										String course_name = rsCourse.getString("course_name");
										System.out.println(course_id+"--------"+course_name);
									}
									
									
									System.out.println("Enter corresponding course id to be assigned.");
									selected_cid = scanner.nextInt();
									scanner.nextLine();
								
									rsCourse.close();
									stmt.close();
								}
								catch ( SQLException e ) {
									e.printStackTrace();
									mode = -2;
								}
								
								System.out.println("Enter evaluation type:");
								String eval_type = scanner.nextLine();
								
								System.out.println("Enter weightage:");
								int weightage = scanner.nextInt();
								scanner.nextLine();
								
								System.out.println("Enter due date, i.e 06-MAR-2016 23:59");
								String due_date = scanner.nextLine();
								
								System.out.println("Enter meeting room:");
								String meeting_room = scanner.nextLine();
								
								String createEval = "insert into Evaluation values (eval_sequence.nextval, '"+
										eval_type+"', "+weightage+", to_date('"+due_date+"','DD-MON-YYYY HH24:MI'),'"+
										meeting_room+"')";
								
								String addCourseComponent = "insert into Course_Component values (eval_sequence.currval,"+
								selected_cid+")";
								
								System.out.println(createEval);
								System.out.println(addCourseComponent);
								
								Statement stmt = con.createStatement();
								stmt.executeUpdate(createEval);
								stmt.execute(addCourseComponent);
								stmt.close();
								
							}
							catch(Exception e) {
								e.printStackTrace();
								System.out.println("Create evaluation failed. Please check you information.");
								mode = -2;
							}
						}
						else if (mode == 1) {
							// modify an evaluation
							int selected_eid = -1;
							java.sql.Date preDate = null;
							
							String query = "Select * from Evaluation";
							try {
								Statement stmt = con.createStatement();
								ResultSet rs = stmt.executeQuery( query );
								
								System.out.println("Eval ID--Eval type--Weightage--Due date--Meeting room");
								
								while ( rs.next() ) {
									System.out.println(rs.getInt(1)+"--"+rs.getString(2)+"--"+rs.getInt(3)+
											"--"+rs.getDate(4)+"--"+rs.getString(5));
								}
								
								System.out.println("Enter corresponding Eval ID to modify listed evaluation.");
								
								selected_eid = scanner.nextInt();
								scanner.nextLine();
								
								query = "Select * from Evaluation where eval_id="+selected_eid;
								rs = stmt.executeQuery(query);
								
								while ( rs.next() ){
									preDate = rs.getDate(4);
								}
								
								Date utilDate = new Date();
								java.sql.Date currentDate = new java.sql.Date(utilDate.getTime());
								
								System.out.println(preDate);
								System.out.println(currentDate);
								
						        
						        if (currentDate.after(preDate)) {
						        	System.out.println("Cannot modify pass-due evaluation. Start again.");
						        	mode = -2;
						        	continue;
						        }
								
								rs.close();
								stmt.close();
							}
							catch ( SQLException e ) {
								e.printStackTrace();
								mode = -2;
							}
							
							try{
								
								System.out.println("Enter new evaluation type:");
								String eval_type = scanner.nextLine();
								
								System.out.println("Enter new weightage:");
								int weightage = scanner.nextInt();
								scanner.nextLine();
								
								System.out.println("Enter new due date, i.e 06-MAR-2016 23:59");
								String due_date = scanner.nextLine();
								
								
								System.out.println("Enter new meeting room:");
								String meeting_room = scanner.nextLine();
								
								
								String modifyCourse = "update Evaluation set eval_type = '"+
										eval_type+"', weightage = "+weightage+
										", due_date = to_date('"+due_date+"','DD-MON-YYYY HH24:MI'), meeting_room = '"+
										meeting_room+"' where eval_id = "+selected_eid;
								
								
								System.out.println(modifyCourse);
								
								Statement stmt = con.createStatement();
								stmt.executeUpdate(modifyCourse);
								stmt.close();
							}
							catch(Exception e) {
								e.printStackTrace();
								System.out.println("Modify evaluation failed. Please check you information.");
								mode = -2;
							}
							
						}
						else {
							System.out.println("Invalid selection. Please re-enter.");
						}
					}
					
				}
				else if(selection == 4) {
					// enter grades
					int mode = -2;
					int selected_sid = -1;
					int selected_cid = -1;
					int eval_grade = -1;
					
					while (mode != 0) {
						
						/*String queryStudent = "Select user_id, user_name from Users where user_type='Student'";
						String queryCourse = "Select course_id, course_name from Course where faculty_id="+faculty_id;
						
						try {
							Statement stmt = con.createStatement();
							
							ResultSet rsStudent = stmt.executeQuery( queryStudent );
							
							System.out.println("Student ID--Student Name");
							while ( rsStudent.next() ) {
								int user_id = rsStudent.getInt( "user_id" );
								String user_name = rsStudent.getString("user_name");
								System.out.println(user_id+"--------"+user_name);
							}
							
							System.out.println("==================");
							
							
							ResultSet rsCourse = stmt.executeQuery( queryCourse );
							
							System.out.println("Course ID--Course Name");						
							while ( rsCourse.next() ) {
								int course_id = rsCourse.getInt( "course_id" );
								String course_name = rsCourse.getString("course_name");
								System.out.println(course_id+"--------"+course_name);
							}
							
							System.out.println("Enter corresponding student id.");
							selected_sid = scanner.nextInt();
							scanner.nextLine();
							
							System.out.println("Enter corresponding course id.");
							selected_cid = scanner.nextInt();
							scanner.nextLine();
						
							rsStudent.close();
							rsCourse.close();
							stmt.close();
						}
						catch ( SQLException e ) {
							e.printStackTrace();
							mode = -2;
						}*/
						
						try{
							
							System.out.println("Enter corresponding student id.");
							selected_sid = scanner.nextInt();
							scanner.nextLine();
							
							System.out.println("Enter corresponding course id.");
							selected_cid = scanner.nextInt();
							scanner.nextLine();
							
							System.out.println("Enter grade.");
							eval_grade = scanner.nextInt();
							scanner.nextLine();
							
							// select all the eval_id to the course. Then add 
							
							String selectEval = "Select eval_id from Course_Component where course_id="+selected_cid;
							
							System.out.println(selectEval);
							
							Statement stmt = con.createStatement();
							
							ResultSet rsEval = stmt.executeQuery( selectEval );
							
							
							int eval_id = -1;
							String enterGrade = "";
							while ( rsEval.next() ) {
								eval_id = rsEval.getInt( "eval_id" );
								enterGrade = "insert into Grade values ("+eval_id+","+selected_sid+","+eval_grade+")";
								System.out.println(enterGrade);
								
								stmt.execute(enterGrade);
							}
							
							stmt.close();
							mode = 0;
						}
						catch(Exception e) {
							// e.printStackTrace();
							System.out.println("Assign failed. Please check you information.");
							mode = -2;
						}
					}
					
				}
				else if(selection == 5) {
					// report of classes
					int course_id = 0;
					
					String courseName = "";
					java.sql.Date meetsAt = null;
					String roomNo = "";
					int student_count = 0;
					int eval_count = 0;
					
					
					
					String query = "Select * from Course where faculty_id="+faculty_id;
					String countStu = "";
					String countEval = "";
					
					try {
						Statement stmt = con.createStatement();
						ResultSet rsCourse = stmt.executeQuery( query );
						/*ResultSet rsMix = null;*/
						
						Statement stmtStudent;
						Statement stmtEval;
						
						System.out.println("CourseName---meetsAt---roomNo---numStudents---numEvaluations");
						
						while ( rsCourse.next() ) {
							course_id = rsCourse.getInt("course_id");
							
							courseName = rsCourse.getString("course_name");
							meetsAt = rsCourse.getDate("meets_at");
							roomNo = rsCourse.getString("room");
							
							countStu = "Select student_id from Enroll where course_id="+course_id;
							countEval = "Select eval_id from Course_Component where course_id="+course_id;
							
							stmtStudent = con.createStatement();
							ResultSet rsStudent = stmtStudent.executeQuery(countStu);
							while (rsStudent.next()) {
								student_count++;
							}
							stmtStudent.close();			
							rsStudent.close();
							
							stmtEval = con.createStatement();
							ResultSet rsEval = stmtEval.executeQuery(countEval);
							while (rsEval.next()) {
								eval_count++;
							}
							stmtEval.close();
							rsEval.close();
							
							System.out.println(courseName+"---"+meetsAt+"---"+roomNo+"---"+student_count+"---"+eval_count);
						}
						
						rsCourse.close();
						stmt.close();
					}
					catch ( SQLException e ) {
						e.printStackTrace();
					}
				}
				else {
					//report of students and grades
					
					int course_id = 0;
					int student_id = 0;
					
					String courseName = "";
					String semester = "";
					int year = 0;
					String studentName = "";
					int currentGrade = 0;
					
					
					
					String query = "Select * from Enroll inner join Course on Enroll.course_id = Course.course_id where Course.faculty_id="+faculty_id;
					
					try {
						Statement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery( query );
						/*ResultSet rsMix = null;*/
						
						Statement stmtMix = con.createStatement();
						ResultSet rsMix;
						
						
						
						Statement stmtWeight = con.createStatement();
						ResultSet rsWeight;
						
						System.out.println("CourseName---semester---year---studentName---currentGrade");
						
						while ( rs.next() ) {
							course_id = rs.getInt("course_id");
							student_id = rs.getInt("student_id");
							
							courseName = rs.getString("course_name");
							semester = rs.getString("semester");
							year = rs.getInt("year");
							
							String sname = "Select user_name from Users where user_id="+student_id;
							rsMix = stmtMix.executeQuery( sname );
							while (rsMix.next()) {
								studentName = rsMix.getString("user_name");
							}
							
							Statement stmtEval = con.createStatement();
							ResultSet rsEval;
							
							String evalID = "Select eval_id, eval_grade from Grade where student_id="+student_id;
							rsEval = stmtEval.executeQuery(evalID);
							
							int grade = 0;
							int weight = 0;
							while (rsEval.next()) {
								grade = rsEval.getInt("eval_grade");
								
								String weightage = "Select weightage from Evaluation where eval_id="+rsEval.getInt("eval_id");
								rsWeight = stmtWeight.executeQuery(weightage);
								while (rsWeight.next()) {
									weight = rsWeight.getInt("weightage");
								}
								
								currentGrade+=(int)0.01*(weight*grade);
							}
							
							System.out.println(courseName+"---"+semester+"---"+year+"---"+studentName+"---"+currentGrade+"%");
						}
						
						rs.close();
						stmt.close();
					}
					catch ( SQLException e ) {
						e.printStackTrace();
					}
					
				}
				
			}
			else {
				System.out.println("Invalid selection. Please re-enter.");
			}
		}
	}
	
	
	public void showToolsForStudent(int student_id) {
		int selection = -1;
		
		while (selection != 0) {
			System.out.println("You have following tools available:");
			System.out.println("1: Get calendar of evaluations.");
			System.out.println("2: Get my courses information.");
			System.out.println("3: View my grades.");
			System.out.println("Or 0: EXIT THE SYSTEM.");
			System.out.println("Please choose one from above:");
			
			Scanner scanner = new Scanner(System.in);
			
			selection = scanner.nextInt();
			scanner.nextLine();
			
			if (selection >= 0 && selection <=3) {
				if (selection == 0) {
					System.out.println("Bye!");
					System.exit(0);
				}
				else if (selection == 1) {
					// Calendar of evaluations
					int eval_id = 0;
					
					String query = "Select eval_id from Course_Component inner join Enroll on Course_Component.course_id=Enroll.course_id where Enroll.student_id="+student_id;
					String evalDetail = "";
					
					try{
						Statement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						
						
						System.out.println("Evaluation Type---Weightage---Due date---Meeting room");
						
						while(rs.next()) {
							eval_id = rs.getInt("eval_id");
							
							evalDetail = "Select * from Evaluation where eval_id="+eval_id;
							
							Statement stmtEval = con.createStatement();
							ResultSet rsEval = stmtEval.executeQuery(evalDetail);
							while (rsEval.next()) {
								System.out.println(rsEval.getString("eval_type")+"------"+rsEval.getInt("weightage")+"---"+
										rsEval.getDate("due_date")+"------"+rsEval.getString("meeting_room"));
							}
							
						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					
				}
				else if (selection == 2) {
					// Get my courses
					int course_id = 0;
					
					String query = "Select course_id from Enroll where student_id="+student_id;
					String courseDetail = "";
					
					try{
						Statement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						
						
						System.out.println("Course name---Semester---Year---Meets at---Room");
						
						while(rs.next()) {
							course_id = rs.getInt("course_id");
							
							courseDetail = "Select * from Course where course_id="+course_id;
							
							Statement stmtEval = con.createStatement();
							ResultSet rsEval = stmtEval.executeQuery(courseDetail);
							while (rsEval.next()) {
								System.out.println(rsEval.getString("course_name")+"------"+rsEval.getString("semester")+"---"+
										rsEval.getInt("year")+"---"+rsEval.getDate("meets_at")+"------"+rsEval.getString("room"));
							}
							
						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else {
					// View grades
					int currentGrade = 0;
					int course_id = 0;
					
					String query = "Select course_id from Enroll where student_id="+student_id;
					String courseDetail = "";
					
					try{
						Statement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						
						
						System.out.println("Course name---Semester---Year---Meets at---Room");
						
						while(rs.next()) {
							currentGrade = 0;
							course_id = rs.getInt("course_id");
							
							courseDetail = "Select course_name from Course where course_id="+course_id;
							
							Statement stmtCourse = con.createStatement();
							ResultSet rsCourse = stmtCourse.executeQuery(courseDetail);
							
							while (rsCourse.next()) {
								System.out.println("Course name :"+rsCourse.getString("course_name"));
								System.out.println("List of evaluations :");
								
								
								int eval_id = 0;
								
								// Get all the evals of this single course
								String eval = "Select eval_id from Course_Component where course_id="+course_id;
								String evalDetail = "";
								String evalGrade = "";
								
								try{
									Statement stmtEval = con.createStatement();
									ResultSet rsEval = stmtEval.executeQuery(eval);
									
									
									System.out.println("Evaluation Type---Weightage---Grade");
									
									while(rsEval.next()) {
										eval_id = rsEval.getInt("eval_id");
										evalDetail = "Select * from Evaluation where eval_id="+eval_id;
										
										Statement stmtEvalDetail = con.createStatement();
										ResultSet rsEvalDetail = stmtEvalDetail.executeQuery(evalDetail);
										
										while (rsEvalDetail.next()) {
											System.out.print(rsEvalDetail.getString("eval_type")+"------------"+rsEvalDetail.getInt("weightage"));
										}
										
										evalGrade = "Select * from Grade where eval_id="+eval_id;
										
										Statement stmtEvalGrade = con.createStatement();
										ResultSet rsEvalGrade = stmtEvalGrade.executeQuery(evalGrade);
										
										while (rsEvalGrade.next()) {
											System.out.print("------"+rsEvalGrade.getInt("eval_grade")+"%");
											System.out.println("");
										}
										
									}
								}
								catch (SQLException e) {
									e.printStackTrace();
								}
								
							}
							
							Statement stmtEval = con.createStatement();
							String evalID = "Select eval_id, eval_grade from Grade where student_id="+student_id;
							ResultSet rsEval = stmtEval.executeQuery(evalID);
							
							int grade = 0;
							int weight = 0;
							while (rsEval.next()) {
								grade = rsEval.getInt("eval_grade");
								
								Statement stmtWeight = con.createStatement();
								String weightage = "Select weightage from Evaluation where eval_id="+rsEval.getInt("eval_id");
								ResultSet rsWeight = stmtWeight.executeQuery(weightage);
								while (rsWeight.next()) {
									weight = rsWeight.getInt("weightage");
								}
								
								currentGrade+=(int)0.01*(weight*grade);
							}
							
							System.out.println("-----------------Final Grade: "+currentGrade+"%");
							
							
						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					
				}
			}
			else {
				System.out.println("Invalid selection. Please re-enter.");
			}
		}
	}
	
	
	public void showToolsForAdmin(int admin_id) {
		int selection = -1;
		
		while (selection != 0) {
			System.out.println("You have following tools available:");
			System.out.println("1: Get department report.");
			System.out.println("2: Get faculty report.");
			System.out.println("Or 0: EXIT THE SYSTEM.");
			System.out.println("Please choose one from above:");
			
			Scanner scanner = new Scanner(System.in);
			
			selection = scanner.nextInt();
			scanner.nextLine();
			
			if (selection >= 0 && selection <=2) {
				if (selection == 0) {
					System.out.println("Bye!");
					System.exit(0);
				}
				else if (selection == 1) {
					// Department Report
					String query = "Select * from Department";
					try{
						Statement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						
						
						System.out.println("Department name---Department head");
						
						while(rs.next()) {
							System.out.println(rs.getString("dept_name")+"------"+rs.getString("dept_head_name"));
						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
					
				}
				else {
					// Faculty Report
					String query = "Select * from Users inner join Roster on Users.user_id = Roster.faculty_id";
					
					try{
						Statement stmt = con.createStatement();
						ResultSet rs = stmt.executeQuery(query);
						
						
						System.out.println("Department name---Faculty name");
						
						while(rs.next()) {
							System.out.println(rs.getString("dept_name")+"------"+rs.getString("user_name"));
						}
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
			}
			else {
				System.out.println("Invalid selection. Please re-enter.");
			}
		}
	}
	
	
	public static void main (String [] args) {
		ManagementSystem ucms = new ManagementSystem();
		ucms.login();
	}
}