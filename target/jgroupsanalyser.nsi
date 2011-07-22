!define VERSION "1.0.0"
Name "JGroupsAnalyser ${VERSION}"
OutFile "JGroupsAnalyser-${VERSION}.exe"

;--------------------------------
;Include Modern UI


  !include "MUI.nsh"
  !include "WordFunc.nsh"
;--------------------------------
;General

  InstallDir "$PROGRAMFILES\JGroupsAnalyser\JGroupsAnalyser"

  Var MUI_TEMP
  Var STARTMENU_FOLDER
  !define MUI_HEADERIMAGE
;	!define MUI_HEADERIMAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Header\alyseo2.bmp" ; optional

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "gpl.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
 
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\JGroupsAnalyser\JGroupsAnalyser"
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"

  !insertmacro MUI_PAGE_STARTMENU Application $STARTMENU_FOLDER

  !insertmacro MUI_PAGE_INSTFILES

  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;---------------------------------
;Languages

  !insertmacro MUI_LANGUAGE "English"
  !insertmacro MUI_LANGUAGE "French"
  

;--------------------------------
;Installer Sections

Section "CheckTurret" SecNtService


SectionIn 2

	;	Delete configuration directory to clean the plugin cache
	RMDir /r "$INSTDIR\eclipse"
	

	SetOutPath "$INSTDIR\"
	File /r repository
	File /r win32.win32.x86
	
	File Readme.md

	
  ;Store installation folder
  WriteRegStr HKLM "Software\JGroupsAnalyser" "" $INSTDIR

	ReadRegStr $1 HKLM "Software\JavaSoft\Java Runtime Environment\$0" "RuntimeLib"
	ReadRegStr $2 HKLM "Software\JavaSoft\Java Runtime Environment\$0" "JavaHome"
	push $1
	push $2
	
	SetOutPath "$INSTDIR"
	
  WriteUninstaller "$INSTDIR\Uninstall.exe"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application

  CreateDirectory "$SMPROGRAMS\JGroupsAnalyser\$STARTMENU_FOLDER"
  
  CreateShortCut "$SMPROGRAMS\JGroupsAnalyser\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\Uninstall.exe"

  CreateShortCut "$SMPROGRAMS\JGroupsAnalyser\$STARTMENU_FOLDER\JGroupsAnalyser.lnk" "$INSTDIR\win32.win32.x86\JGroupsAnalyser\JGroupsAnalyser.exe"
  
  CreateShortCut "$SMPROGRAMS\Etrali\$STARTMENU_FOLDER\Release Notes.lnk" "$INSTDIR\Readme.txt"

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\JGroupsAnalyser" "JGroupsAnalyser" "JGroupsAnalyser"
WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\JGroupsAnalyser" "Uninstall JGroupsAnalyser" "$\"$INSTDIR\Uninstall.exe$\""
  
  !insertmacro MUI_STARTMENU_WRITE_END


SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecNtService ${LANG_ENGLISH} "JGroupsAnalyser"
  LangString DESC_SecNtService ${LANG_FRENCH} "JGroupsAnalyser v1.0"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  	!insertmacro MUI_DESCRIPTION_TEXT ${SecNtService} $(DESC_SecNtService)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

	Delete "$INSTDIR\Uninstall.exe"

    !insertmacro MUI_STARTMENU_GETFOLDER Application $MUI_TEMP
	Delete "$SMPROGRAMS\JGroupsAnalyser\$MUI_TEMP\Uninstall.lnk"
	Delete "$SMPROGRAMS\JGroupsAnalyser\$MUI_TEMP\Readme.lnk"
	Delete "$SMPROGRAMS\JGroupsAnalyser\$MUI_TEMP\JGroupsAnalyser.lnk"

    StrCpy $MUI_TEMP "$SMPROGRAMS\JGroupsAnalyser\$MUI_TEMP"
	RMDir $MUI_TEMP

	RMDir /r "$INSTDIR"

	DeleteRegKey /ifempty HKLM "Software\JGroupsAnalyser\JGroupsAnalyser"

SectionEnd
 