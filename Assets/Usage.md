# RayneSQL Usage

### 1. `CREATE` and `USE` a  database
```sql
CREATE DATABASE data;  
[OK]
USE data;
[OK]
```

### 2. `CREATE` a table
Create an empty table:
```sql
CREATE TABLE employees;
[OK]
```
Or specify columns:
```sql
CREATE TABLE employees (Name, Department, Salary);
[OK]
```

### 3. `INSERT` into a table
If columns were specified:
```sql
INSERT INTO employees VALUES ('John', 'IT', 50000);
[OK]
INSERT INTO employees VALUES ('Paul', 'Sales', '30000');
[OK]
INSERT INTO employees VALUES ('George', 'IT', '80000');
[OK]
INSERT INTO employees VALUES ('Ringo', 'Finance', '120000');
[OK]
```

### 4. `SELECT` data from a table
Select all data:
```sql
SELECT * FROM employees;
[OK]
id   Name     Department   Salary   
1    John     IT           50000    
2    Paul     Sales        30000    
3    George   IT           80000    
4    Ringo    Finance      120000
```
Select data that satisfies a condition:
```sql
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
```sql
SELECT name, id FROM employees WHERE department!='Sales';
[OK]
Name     id   
John     1    
George   3    
Ringo    4    
```
### 5. `ALTER` a table's column structure
Add a column:
```sql
ALTER TABLE employees ADD Age;
[OK]
SELECT * FROM employees;
[OK]
id   Name     Department   Salary   Age    
1    John     IT           50000    NULL   
2    Paul     Sales        30000    NULL   
3    George   IT           80000    NULL   
4    Ringo    Finance      120000   NULL   
```
Remove a column:
```sql
ALTER TABLE employees DROP Age;
[OK]
SELECT * FROM employees;
[OK]
id   Name     Department   Salary   
1    John     IT           50000    
2    Paul     Sales        30000    
3    George   IT           80000    
4    Ringo    Finance      120000   

```
### 6. `UPDATE` a table's data
```sql
UPDATE employees SET department='Finance' WHERE name=='George';
[OK]
SQL:> SELECT * FROM employees;
[OK]
id   Name     Department   Salary   
1    John     IT           50000    
2    Paul     Sales        30000    
3    George   Finance      80000    
4    Ringo    Finance      120000
```

### 7. `DELETE` a table row
```sql
DELETE FROM employees WHERE id==1;
[OK]
SELECT * FROM employees;
[OK]
id   Name     Department   Salary   
2    Paul     Sales        30000    
3    George   Finance      80000    
4    Ringo    Finance      120000   
```

### 8. `JOIN` tables
Given the tables 'transactions' and 'accounts':
```sql
          transactions                                  accounts
id   AccountNumber   Date                       id   Name      Number     
1    34574246        30/10/1971                 1    Roger     90182061   
2    90182061        1/3/1973                   2    David     34574246   
3    15579109        12/9/1975                  3    Richard   15579109   
4    61425454        21/1/1977                  4    Nick      61425454
5    34574246        30/11/1979   
6    90182061        21/3/1983
```
To join these tables on AccountNumber and number:
```sql
JOIN transactions AND accounts ON AccountNumber AND number;
[OK]
id   transactions.Date   accounts.Name   
1    1/3/1973            Roger           
2    21/3/1983           Roger           
3    30/10/1971          David           
4    30/11/1979          David           
5    12/9/1975           Richard         
6    21/1/1977           Nick            
```

### 9. `DROP` a table from the active database
```sql
DROP TABLE accounts;
[OK]
```

### 10. `DROP` a database
```sql
DROP DATABASE data;
[OK]
```