@echo off
setlocal

REM Garante que todos os comandos sejam executados a partir da pasta do projeto.
cd /d "%~dp0"

echo ==========================================
echo Sistema Gerador de Provas
echo ==========================================
echo.

echo Verificando servico MySQL80...
REM O sistema usa o MySQL instalado no Windows; se estiver parado, tenta iniciar.
sc query MySQL80 | findstr /I "RUNNING" >nul
if errorlevel 1 (
    echo Iniciando MySQL80...
    net start MySQL80
    if errorlevel 1 (
        echo.
        echo Nao foi possivel iniciar o MySQL80 automaticamente.
        echo Abra o MySQL manualmente ou execute este arquivo como administrador.
        echo.
        pause
        exit /b 1
    )
) else (
    echo MySQL80 ja esta rodando.
)

echo.
echo Compilando e iniciando o Tomcat...
REM O PowerShell faz o preparo do ambiente, build Maven e start do Tomcat.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\start-local.ps1"
if errorlevel 1 (
    echo.
    echo Ocorreu um erro ao iniciar o projeto.
    echo.
    pause
    exit /b 1
)

echo.
echo Abrindo no navegador...
REM Aguarda alguns segundos para o Tomcat terminar de publicar a aplicacao.
timeout /t 5 /nobreak >nul
start "" "http://localhost:8080/gerador-provas/home"

echo.
echo Projeto aberto em http://localhost:8080/gerador-provas/home
timeout /t 3 /nobreak >nul
