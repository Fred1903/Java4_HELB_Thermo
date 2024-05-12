mvn clean 
#mvn package  a enlever pck compile sur tt le package et on veut pas compile les tests

mvn install -Dmaven.test.skip=true  #permet de ne pas compiler et de skip les tests
# source : https://stackoverflow.com/questions/1607315/build-maven-project-without-running-unit-tests

java -jar --module-path /usr/share/openjfx/lib --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web target/myproject-1.0-SNAPSHOT.jar 
