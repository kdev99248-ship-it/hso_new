@echo off
:loop
mysqldump -u root -ppass 0706665457aa > backup.sql
timeout /t 1800 /nobreak > NUL
goto loop
