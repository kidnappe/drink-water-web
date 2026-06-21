@echo off
REM 生成 release 签名密钥库 (KeyStore)
REM 用法: .\create-keystore.bat
REM
REM 运行后会在 android/ 目录下生成 release.keystore 和 keystore.properties
REM 之后执行 .\gradlew assembleRelease 即可签名 APK

set KEYSTORE_FILE=release.keystore
set KEYSTORE_PASS=drinkwater123
set KEY_ALIAS=drinkwater
set KEY_PASS=drinkwater123
set VALIDITY=10000

echo 生成密钥库: %KEYSTORE_FILE%
keytool -genkey -v -keystore %KEYSTORE_FILE% ^
  -alias %KEY_ALIAS% ^
  -keyalg RSA ^
  -keysize 2048 ^
  -validity %VALIDITY% ^
  -storepass %KEYSTORE_PASS% ^
  -keypass %KEY_PASS% ^
  -dname "CN=DrinkWater, OU=Watch, O=DrinkWater, L=Unknown, ST=Unknown, C=CN"

echo.
echo 生成密钥库配置: keystore.properties
(
echo storeFile=%KEYSTORE_FILE%
echo storePassword=%KEYSTORE_PASS%
echo keyAlias=%KEY_ALIAS%
echo keyPassword=%KEY_PASS%
) > keystore.properties

echo.
echo ✅ 完成！运行以下命令构建 Release APK:
echo   cd android
echo   .\gradlew assembleRelease
echo APK 输出: app\build\outputs\apk\release\app-release.apk
pause
