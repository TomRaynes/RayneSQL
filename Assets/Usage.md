# RayneSQL Usage

### 1. `CREATE` and `USE` a  database
```
CREATE DATABASE NewDatabase;  
[OK]
USE NewDatabase;
[OK]
```

### 2. `CREATE` a table
Create an empty table:
```
CREATE TABLE employees;
[OK]
```
Or specify columns:
```
CREATE TABLE employees (Name, Department, Salary);
[OK]
```

### 3. `INSERT` into a table
If columns were specified:
```
INSERT INTO employees VALUES ('John', 'IT', 50000);
[OK]
INSERT INTO employees VALUES ('Paul', 'Sales', '30000');
[OK]
INSERT INTO employees VALUES ('George', 'IT', '80000');
[OK]
INSERT INTO employees VALUES ('Ringo', 'Finace', '120000');
[OK]
```

### 4. `SELECT` data from a table
Select all data:
```
SELECT * FROM employees;
[OK]
id   Name     Department   Salary   
1    John     IT           50000    
2    Paul     Sales        30000    
3    George   IT           80000    
4    Ringo    Finace       120000
```
Select data that satisfies a condition:
```
SELECT * FROM employees WHERE department=='IT';
[OK]
id   Name     Department   Salary   
1    John     IT           50000    
3    George   IT           80000    

SELECT * FROM employees WHERE department=='IT' AND salary>75000;
[OK]
id   Name     Department   Salary   
3    George   IT           80000   

SELECT * FROM employees WHERE (department=='IT' AND salary>75000) OR name=='Paul';
[OK]
id   Name     Department   Salary   
2    Paul     Sales        30000    
3    George   IT           80000    
```
Select specific columns:
```
SELECT name, id FROM employees WHERE department!='Sales';
[OK]
Name     id   
John     1    
George   3    
Ringo    4    
```
Any complex condition is supported
e.g., Given the table of cities:
```
id   Name         Population   Country       Region             
1    London       7556900      UK            South East         
2    Manchester   395515       UK            North West         
3    Birmingham   984333       UK            West Midlands      
4    Liverpool    864122       UK            North West         
5    Bristol      617280       UK            South West         
6    Sheffield    685368       UK            North              
7    Nottingham   729977       UK            Midlands           
8    Glasgow      591620       UK            Scotland           
9    Edinburgh    464990       UK            Scotland           
10   Cardiff      447287       UK            Wales              
11   Leicester    508916       UK            Midlands           
12   Leeds        455123       UK            North              
13   Belfast      274770       UK            Northern Ireland   
14   Paris        2103000      France        NULL               
15   Berlin       3432000      Germany       NULL               
16   Rome         2760000      Italy         NULL               
17   Madrid       3277000      Spain         NULL               
18   Amsterdam    921400       Netherlands   NULL               
```
```

```




```

```