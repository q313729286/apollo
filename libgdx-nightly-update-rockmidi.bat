set libgdx=libgdx-nightly-latest
::echo %libgdx%
set project=rockmidi
::echo %project%
::pause

set project_core=.\%project%\trunk\%project%\libs
::echo %project_core%
set project_android=.\%project%\trunk\%project%-android\libs
::echo %project_android%
set project_desktop=.\%project%\trunk\%project%-desktop\libs
::echo %project_desktop%
set project_html=.\%project%\trunk\%project%-html\war\WEB-INF\lib
::echo %project_html%
::pause

::echo "copy core project libs"
xcopy /Y %libgdx%\gdx.jar %project_core%\gdx.jar
xcopy /Y %libgdx%\sources\gdx-sources.jar %project_core%\gdx-sources.jar
::pause

::echo "copy android project libs"
xcopy /Y %libgdx%\gdx-backend-android.jar %project_android%\gdx-backend-android.jar
xcopy /Y %libgdx%\sources\gdx-backend-android-sources.jar %project_android%\gdx-backend-android-sources.jar
xcopy /Y %libgdx%\armeabi\*.so %project_android%\armeabi\*.so
xcopy /Y %libgdx%\armeabi-v7a\*.so %project_android%\armeabi-v7a\*.so
::pause

::echo "copy desktop project libs"
xcopy /Y %libgdx%\gdx-natives.jar %project_desktop%\gdx-natives.jar
xcopy /Y %libgdx%\gdx-backend-lwjgl.jar %project_desktop%\gdx-backend-lwjgl.jar
xcopy /Y %libgdx%\gdx-backend-lwjgl-natives.jar %project_desktop%\gdx-backend-lwjgl-natives.jar
xcopy /Y %libgdx%\sources\gdx-backend-lwjgl-sources.jar %project_desktop%\gdx-backend-lwjgl-sources.jar
::pause

::echo "copy html project libs"
xcopy /Y %libgdx%\gdx-backend-gwt.jar %project_html%\gdx-backend-gwt.jar
xcopy /Y %libgdx%\sources\gdx-backend-gwt-sources.jar %project_html%\gdx-backend-gwt-sources.jar
pause