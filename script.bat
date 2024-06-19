set "jdk_path=C:\Program Files\Java\jdk-17"

set "src=src"
set "archive=framework_2571.jar"

rmdir /s /q tempJava
mkdir javacompiler
mkdir tempJava

for /r %src% %%f in (*.java) do (
    copy /y "%%f" tempJava\
)
"%jdk_path%\bin\javac" -parameters -d javacompiler tempJava\*.java

"%jdk_path%\bin\jar" cf %archive% -C javacompiler\ .

