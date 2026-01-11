# Manual Deployment Script for AgenceTransportPART3
# This script helps diagnose and fix GlassFish deployment issues

Write-Host "=== GlassFish Deployment Diagnostic Tool ===" -ForegroundColor Cyan
Write-Host ""

# Check if GlassFish is running
Write-Host "1. Checking GlassFish status..." -ForegroundColor Yellow
$glassfishPort = netstat -ano | findstr ":8080.*LISTENING"
$adminPort = netstat -ano | findstr ":4848.*LISTENING"

if ($glassfishPort) {
    Write-Host "   [OK] GlassFish HTTP port (8080) is LISTENING" -ForegroundColor Green
} else {
    Write-Host "   [ERROR] GlassFish HTTP port (8080) is NOT running" -ForegroundColor Red
    Write-Host "   -> Start GlassFish from NetBeans Services tab" -ForegroundColor Yellow
    exit 1
}

if ($adminPort) {
    Write-Host "   [OK] GlassFish Admin port (4848) is LISTENING" -ForegroundColor Green
} else {
    Write-Host "   [ERROR] GlassFish Admin port (4848) is NOT running" -ForegroundColor Red
    exit 1
}

# Check Derby database
Write-Host ""
Write-Host "2. Checking Derby database..." -ForegroundColor Yellow
$derbyPort = netstat -ano | findstr ":1527.*LISTENING"

if ($derbyPort) {
    Write-Host "   [OK] Derby database (1527) is LISTENING" -ForegroundColor Green
} else {
    Write-Host "   [ERROR] Derby database (1527) is NOT running" -ForegroundColor Red
    Write-Host "   -> Start Derby from NetBeans Services tab" -ForegroundColor Yellow
    exit 1
}

# Check for TIME_WAIT connections (indicates deployment issues)
Write-Host ""
Write-Host "3. Checking admin port health..." -ForegroundColor Yellow
$timeWaitCount = (netstat -ano | findstr ":4848.*TIME_WAIT" | Measure-Object).Count

if ($timeWaitCount -gt 10) {
    Write-Host "   [WARNING] Found $timeWaitCount TIME_WAIT connections on admin port" -ForegroundColor Yellow
    Write-Host "   -> This suggests repeated failed deployments" -ForegroundColor Yellow
    Write-Host "   -> Recommendation: Restart GlassFish server" -ForegroundColor Cyan
} else {
    Write-Host "   [OK] Admin port health looks good ($timeWaitCount TIME_WAIT connections)" -ForegroundColor Green
}

# Check if WAR file exists
Write-Host ""
Write-Host "4. Checking WAR file..." -ForegroundColor Yellow
$warPath = "target\AgenceTransportPART3-1.0.war"

if (Test-Path $warPath) {
    $warSize = (Get-Item $warPath).Length / 1KB
    $warSizeRounded = [math]::Round($warSize, 2)
    Write-Host "   [OK] WAR file exists: $warPath ($warSizeRounded KB)" -ForegroundColor Green
} else {
    Write-Host "   [ERROR] WAR file not found: $warPath" -ForegroundColor Red
    Write-Host "   -> Run 'mvn clean package' or Clean and Build in NetBeans" -ForegroundColor Yellow
    exit 1
}

# Provide deployment options
Write-Host ""
Write-Host "=== Deployment Options ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Option 1: Restart GlassFish (Recommended)" -ForegroundColor Green
Write-Host "   1. Stop GlassFish in NetBeans (Services -> GlassFish -> Stop)"
Write-Host "   2. Wait 15-20 seconds"
Write-Host "   3. Start GlassFish (Services -> GlassFish -> Start)"
Write-Host "   4. Right-click project -> Clean and Build"
Write-Host "   5. Right-click project -> Run"
Write-Host ""
Write-Host "Option 2: Manual Deployment via Admin Console" -ForegroundColor Green
Write-Host "   1. Open browser: http://localhost:4848"
Write-Host "   2. Go to Applications -> Deploy"
$fullWarPath = Join-Path (Get-Location).Path $warPath
Write-Host "   3. Select file: $fullWarPath"
Write-Host "   4. Click OK"
Write-Host ""
Write-Host "Option 3: Check Server Logs" -ForegroundColor Green
Write-Host "   -> NetBeans: Window -> Output -> GlassFish Server tab"
Write-Host "   -> Look for SQLException, NameNotFoundException, or DeploymentException"
Write-Host ""

# Offer to open admin console
Write-Host "Would you like to open the GlassFish Admin Console? (Y/N): " -NoNewline -ForegroundColor Cyan
$response = Read-Host

if ($response -eq "Y" -or $response -eq "y") {
    Start-Process "http://localhost:4848"
    Write-Host "Opening admin console..." -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Diagnostic Complete ===" -ForegroundColor Cyan
