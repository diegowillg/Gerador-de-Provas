param(
    # Mantem os mesmos valores usados no db.properties local.
    [string]$DbPassword = "",
    [int]$DbPort = 3308
)

$ErrorActionPreference = "Stop"

# Caminhos principais usados para compilar o projeto e publicar no Tomcat local.
$Root = Split-Path -Parent $PSScriptRoot
$Tools = Join-Path $Root ".tools"
$Maven = Join-Path $Tools "apache-maven-3.9.11\bin\mvn.cmd"
$TomcatDir = Join-Path $Tools "apache-tomcat-10.1.44"

function Set-JavaHome {
    # O Tomcat precisa de JAVA_HOME/JRE_HOME; o Java pode estar instalado sem essas variaveis.
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

    throw "Java was found in PATH, but JAVA_HOME could not be detected."
}

# Prepara ferramentas, db.properties e banco antes do build.
& (Join-Path $PSScriptRoot "setup-local.ps1") -DbPassword $DbPassword -DbPort $DbPort

# Compila o projeto Maven e gera target/gerador-provas.war.
& $Maven -f (Join-Path $Root "pom.xml") clean package
if ($LASTEXITCODE -ne 0) {
    throw "Maven build failed."
}

$WarSource = Join-Path $Root "target\gerador-provas.war"
$WarTarget = Join-Path $TomcatDir "webapps\gerador-provas.war"
$ExplodedTarget = Join-Path $TomcatDir "webapps\gerador-provas"

Set-JavaHome
$env:CATALINA_HOME = $TomcatDir
$env:CATALINA_BASE = $TomcatDir
try {
    # Para uma instancia anterior antes de copiar o WAR novo.
    $PreviousErrorAction = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & (Join-Path $TomcatDir "bin\shutdown.bat") *> $null
} catch {
    # Tomcat may already be stopped.
} finally {
    $ErrorActionPreference = $PreviousErrorAction
}
Start-Sleep -Seconds 3

# Remove a versao antiga publicada para o Tomcat reimplantar tudo limpo.
if (Test-Path $WarTarget) {
    Remove-Item $WarTarget -Force
}
if (Test-Path $ExplodedTarget) {
    Remove-Item $ExplodedTarget -Recurse -Force
}
Copy-Item $WarSource $WarTarget -Force

# Inicia o Tomcat depois que o WAR atualizado foi copiado para webapps.
& (Join-Path $TomcatDir "bin\startup.bat")

Write-Host ""
Write-Host "Projeto iniciado em: http://localhost:8080/gerador-provas/home"
Write-Host "Para parar: .\scripts\stop-local.ps1"
