module br.unesp.fct.sgfp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    // Permite ao módulo ler e utilizar a API do JUnit 5 nos testes
    requires org.junit.jupiter.api;

    // Permite a reflexão para o JavaFX e o JUnit 5
    opens br.unesp.fct.sgfp to javafx.fxml, org.junit.platform.commons;

    exports br.unesp.fct.sgfp;
    exports br.unesp.fct.sgfp.controller;
    opens br.unesp.fct.sgfp.controller to javafx.fxml, org.junit.platform.commons;
}