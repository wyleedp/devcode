@echo off
pushd "%~dp0"
chcp 65001 > nul

java -cp ..\target\devcode-cmd.jar com.github.devcode.cmd.Tail C:\\Users\\wylee\\GoWorks\\20230605144844_세정검사요청서(검사).html

pause