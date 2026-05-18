$ErrorActionPreference = "Continue"

# Este script para apenas o Tomcat local do projeto.
# O MySQL continua rodando porque e um servico instalado no Windows.
$Root = Split-Path -Parent $PSScriptRoot
$Tools = Join-Path $Root ".tools"
$TomcatDir = Join-Path $Tools "apache-tomcat-10.1.44"

function Set-JavaHome {
    # Necessario para executar o shutdown.bat do Tomcat em maquinas sem JAVA_HOME.
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) {
        return
    }

    $Candidates = @()
    if (Test-Path "C:\Program Files\Java\latest") {
        $Candidates += "C:\Program Files\Java\latest"
    }
    if (Test-Path "C:\Program Files\Java") {
        $Candidates += Get-ChildItem "C:\Program Files\Java" -Directory -Filter "jdk*" | Sort-Object LastWriteTime -Descending | ForEach-Object { $_.FullName }
    }

    foreach ($Candidate in $Candidates) {
        if (Test-Path (Join-Path $Candidate "bin\java.exe")) {
            $env:JAVA_HOME = $Candidate
            $env:JRE_HOME = $Candidate
            return
        }
    }
}

if (Test-Path (Join-Path $TomcatDir "bin\shutdown.bat")) {
    # Define o CATALINA_BASE/HOME para garantir que o shutdown pare o Tomcat correto.
    Set-JavaHome
    $env:CATALINA_HOME = $TomcatDir
    $env:CATALINA_BASE = $TomcatDir
    & (Join-Path $TomcatDir "bin\shutdown.bat")
}

Write-Host "Tomcat stopped. MySQL service was left running."
