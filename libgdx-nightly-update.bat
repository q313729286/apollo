set libgdx="libgdx-nightly-latest"
rd %libgdx% /s /q
pause
"C:\Program Files (x86)\HaoZip\HaoZipC.exe" x %libgdx%.zip -o%libgdx%
pause
