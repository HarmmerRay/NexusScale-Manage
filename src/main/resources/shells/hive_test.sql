-- 创建数据库
CREATE DATABASE IF NOT EXISTS demo_db;
-- 使用创建的数据库
USE demo_db;

-- 创建一个简单的学生表
CREATE TABLE IF NOT EXISTS students (
                                        id INT,
                                        name STRING,
                                        age INT,
                                        gender STRING
)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ',';

-- 插入单条数据
INSERT INTO TABLE students VALUES (1, 'Alice', 20, 'Female');

-- 插入多条数据
INSERT INTO TABLE students VALUES
(2, 'Bob', 21, 'Male'),
(3, 'Charlie', 22, 'Male');

-- 查询所有学生信息
SELECT * FROM students;

-- 查询年龄大于20岁的学生信息
SELECT * FROM students WHERE age > 20;