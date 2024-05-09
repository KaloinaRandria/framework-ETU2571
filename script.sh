src="src"
archive="framework-2571.jar"
lib="/home/kaloina/classpath"

rm -rf tempJava
mkdir javacompiler
mkdir tempJava

find "$src" -name '*.java' -exec cp {} tempJava/ \;
javac -cp .:"$lib/*" -d "$classes" tempJava/*.java

jar cf "$archive" -C javacompiler/ .
