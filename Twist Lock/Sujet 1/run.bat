@echo off

echo .java > exclude.tmp

del /s /q out
rmdir /s /q out
mkdir out

powershell (gci -Path src -Recurse *.java^|Resolve-Path -Relative) > compile.list

javac -encoding utf-8 -d out @compile.list

XCOPY /E /EXCLUDE:exclude.tmp src out

del compile.list
del exclude.tmp

java -cp ./out Jeu
pause
