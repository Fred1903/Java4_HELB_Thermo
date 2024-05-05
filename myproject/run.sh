mvn clean 
#mvn package  a enlever pck compile sur tt le package

mvn package -DskipTest #permet de skip le dossier test (un peu comme un git ignore)  #fonctionne pas jsp pq ?

java -jar --module-path /usr/share/openjfx/lib --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web target/myproject-1.0-SNAPSHOT.jar 
