param(
    # Senha do usuario root do MySQL instalado no notebook.
    [string]$DbPassword = "",
    # Porta configurada no MySQL Server 8.0 do notebook.
    [int]$DbPort = 3308
)

$ErrorActionPreference = "Stop"

# A raiz do projeto e calculada a partir da pasta scripts,
# assim o script funciona mesmo quando chamado pelo .bat.
$Root = Split-Path -Parent $PSScriptRoot

# Maven e Tomcat ficam locais ao projeto para nao depender do PATH do Windows.
$Tools = Join-Path $Root ".tools"
$Cache = Join-Path $Tools "cache"
$MavenDir = Join-Path $Tools "apache-maven-3.9.11"
$TomcatDir = Join-Path $Tools "apache-tomcat-10.1.44"
$SchemaMarker = Join-Path $Tools ".mysql-schema-imported"
$DbProperties = Join-Path $Root "src\main\resources\db.properties"

function Install-Zip {
    param(
        [string]$Name,
        [string]$Url,
        [string]$DirectoryName
    )

    # Se a ferramenta ja foi extraida, nao baixa de novo.
    $Destination = Join-Path $Tools $DirectoryName
    if (Test-Path $Destination) {
        Write-Host "$Name already installed."
        return
    }

    # O ZIP baixado fica em cache para acelerar proximas execucoes.
    New-Item -ItemType Directory -Force -Path $Cache | Out-Null
    $ZipPath = Join-Path $Cache "$DirectoryName.zip"
    if (!(Test-Path $ZipPath)) {
        Write-Host "Downloading $Name..."
        Invoke-WebRequest -Uri $Url -OutFile $ZipPath
    }

    Write-Host "Extracting $Name..."
    Expand-Archive -Path $ZipPath -DestinationPath $Tools -Force
}

function Get-MySqlClient {
    # Procura o cliente mysql.exe nos caminhos comuns do instalador oficial.
    $Candidates = @(
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 9.0\bin\mysql.exe"
    )

    foreach ($Candidate in $Candidates) {
        if (Test-Path $Candidate) {
            return $Candidate
        }
    }

    $FromPath = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if ($FromPath) {
        return $FromPath.Source
    }

    throw "mysql.exe not found. Install MySQL Server or add mysql.exe to PATH."
}

# A senha nao fica versionada. Se ja existir db.properties local, reaproveita;
# caso contrario, pede a senha no terminal.
if ([string]::IsNullOrWhiteSpace($DbPassword)) {
    if (Test-Path $DbProperties) {
        $SavedPassword = Select-String -Path $DbProperties -Pattern "^db\.password=(.*)$" | Select-Object -First 1
        if ($SavedPassword) {
            $DbPassword = $SavedPassword.Matches[0].Groups[1].Value
        }
    }

    if ([string]::IsNullOrWhiteSpace($DbPassword)) {
        $DbPassword = Read-Host "Digite a senha do MySQL root"
    }
}

# Garante que Maven e Tomcat existam antes de compilar e publicar o WAR.
Install-Zip "Maven" "https://archive.apache.org/dist/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.zip" "apache-maven-3.9.11"
Install-Zip "Tomcat" "https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.44/bin/apache-tomcat-10.1.44-windows-x64.zip" "apache-tomcat-10.1.44"

# O db.properties e gerado localmente porque contem senha do banco.
@"
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:$DbPort/gerador_provas?useSSL=false&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8
db.user=root
db.password=$DbPassword
"@ | Set-Content -Path $DbProperties -Encoding UTF8

if (!(Test-Path $SchemaMarker)) {
    $Mysql = Get-MySqlClient

    # Confere se a tabela principal ja existe para evitar inserir dados duplicados.
    $SchemaExists = & $Mysql --protocol=tcp -h 127.0.0.1 -P $DbPort -u root "-p$DbPassword" -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='gerador_provas' AND table_name='questoes';"
    if ($LASTEXITCODE -ne 0) {
        throw "Could not connect to MySQL on port $DbPort."
    }

    if ($SchemaExists -eq "0") {
        Write-Host "Importing database schema..."
        $Schema = Join-Path $Root "database\schema.sql"

        # Forca UTF-8 na importacao para preservar acentos no MySQL.
        $OutputEncoding = New-Object System.Text.UTF8Encoding $false
        [Console]::OutputEncoding = $OutputEncoding
        Get-Content $Schema -Encoding UTF8 | & $Mysql --protocol=tcp -h 127.0.0.1 -P $DbPort -u root "-p$DbPassword" --default-character-set=utf8mb4
        if ($LASTEXITCODE -ne 0) {
            throw "Schema import failed."
        }
    } else {
        Write-Host "Database schema already exists."
    }

    New-Item -ItemType File -Force -Path $SchemaMarker | Out-Null
}

Write-Host "Local setup complete."
