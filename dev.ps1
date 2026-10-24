param(
    [ValidateSet('build', 'test', 'run')]
    [string]$Action = 'build'
)
$ErrorActionPreference = 'Stop'
# Keep downloaded tools and caches local to the workspace; make no system changes.
$workspace = Split-Path -Parent $PSScriptRoot
$portableJdk = Get-ChildItem -LiteralPath (Join-Path $workspace '.tools\jdk17') -Directory -ErrorAction SilentlyContinue |
    Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'bin\javac.exe') } |
    Select-Object -First 1
if ($portableJdk) {
    $env:JAVA_HOME = $portableJdk.FullName
}
if (-not $env:JAVA_HOME -or -not (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin\javac.exe'))) {
    throw 'A Java 17 JDK is required. Set JAVA_HOME to your JDK folder, or use the portable JDK in the workspace .tools\jdk17 folder.'
}
$env:GRADLE_USER_HOME = Join-Path $workspace '.tools\gradle'
Push-Location $PSScriptRoot
try {
    if ($Action -eq 'build') {
        & .\gradlew.bat build shadowJar --no-daemon
    } else {
        & .\gradlew.bat $Action --no-daemon
    }
    $result = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $result
