test: java.policy compile-tests

java.policy: FORCE
	@echo "Creating tests java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

compile-tests:
	make -C ../Client all
	make -C ../Server all
	javac -cp /usr/share/java/junit4.jar Tests/TestResourceManager.java

run-tests:
	java -cp .:/usr/share/java/junit4.jar:../Server/RMIInterface.jar:../Server/ServerCommon.jar:../Server/Server/Common org.junit.runner.JUnitCore Tests/TestResourceManager

clean:
