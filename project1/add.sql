create table Course(
    course_id       integer NOT NULL,
    course_name     varchar(30),
    semester        varchar(30),
    year            integer,
    meets_at        date,
    room            varchar(10),
    faculty_id      integer,
    PRIMARY KEY     (course_id));


create sequence course_sequence start with 100
increment by 1
minvalue 100
maxvalue 10000;


create table Users(
    user_id         integer NOT NULL,
    user_name       varchar(30),
    user_type       varchar(10),
    PRIMARY KEY     (user_id));


create sequence users_sequence start with 1000
increment by 1
minvalue 1000
maxvalue 10000;


create table Department(
    dept_name       varchar(30) NOT NULL,
    dept_head_name  varchar(30),
    PRIMARY KEY     (dept_name));


create table Evaluation(
    eval_id         integer NOT NULL,
    eval_type       varchar(30),
    weightage       integer,
    due_date        date,
    meeting_room    varchar(30),
    PRIMARY KEY     (eval_id));


create sequence eval_sequence start with 1
increment by 1
minvalue 1
maxvalue 10000;


create table Enroll(
    course_id       integer NOT NULL,
    student_id      integer NOT NULL,
    PRIMARY KEY     (course_id, student_id),
    FOREIGN KEY     (course_id) REFERENCES Course (course_id)
    ON DELETE CASCADE,
    FOREIGN KEY     (student_id) REFERENCES Users (user_id)
    ON DELETE CASCADE);


create table Roster(
    dept_name       varchar(30) NOT NULL,
    faculty_id      integer NOT NULL,
    PRIMARY KEY     (dept_name, faculty_id),
    FOREIGN KEY     (dept_name) REFERENCES Department (dept_name)
    ON DELETE CASCADE,
    FOREIGN KEY     (faculty_id) REFERENCES Users (user_id)
    ON DELETE CASCADE);


create table Grade(
    eval_id         integer NOT NULL,
    student_id      integer NOT NULL,
    eval_grade      integer,
    PRIMARY KEY     (eval_id, student_id),
    FOREIGN KEY     (eval_id) REFERENCES Evaluation (eval_id)
    ON DELETE CASCADE,
    FOREIGN KEY     (student_id) REFERENCES Users (user_id)
    ON DELETE CASCADE);


create table Course_Component(
    eval_id         integer NOT NULL,
    course_id       integer NOT NULL,
    PRIMARY KEY     (eval_id),
    FOREIGN KEY     (eval_id) REFERENCES Evaluation (eval_id)
    ON DELETE CASCADE,
    FOREIGN KEY     (course_id) REFERENCES Course (course_id)
    ON DELETE CASCADE);
