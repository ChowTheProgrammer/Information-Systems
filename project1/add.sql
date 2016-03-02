create table Course(
    course_id       integer NOT NULL,
    course_name     varchar(30),
    semester        varchar(30),
    year            integer,
    meets_at        date,
    room            varchar(10),
    faculty_id      integer,
    PRIMARY KEY     (course_id));


create table Users(
    user_id   integer NOT NULL,
    user_name       varchar(30),
    user_type       varchar(10),
    PRIMARY KEY     (user_id));


create table Department(
    dept_id         integer NOT NULL,
    dept_name       varchar(30),
    dept_head_name  varchar(30),
    PRIMARY KEY     (dept_id));


create table Evaluation(
    eval_id         integer NOT NULL,
    eval_type       varchar(30),
    weightage       integer,
    due_date        date,
    meeting_room    varchar(30),
    PRIMARY KEY     (eval_id));


create table Enroll(
    course_id       integer NOT NULL,
    student_id         integer NOT NULL,
    PRIMARY KEY     (course_id, student_id),
    FOREIGN KEY     (course_id) REFERENCES Course (course_id)
    ON DELETE CASCADE,
    FOREIGN KEY     (student_id) REFERENCES Users (user_id)
    ON DELETE CASCADE);


create table Roster(
    dept_id         integer NOT NULL,
    faculty_id      integer NOT NULL,
    PRIMARY KEY     (dept_id, faculty_id),
    FOREIGN KEY     (dept_id) REFERENCES Department (dept_id)
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
