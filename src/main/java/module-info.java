module br.unesp.fct.sgfp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    opens br.unesp.fct.sgfp to javafx.fxml;
    exports br.unesp.fct.sgfp;

    opens br.unesp.fct.sgfp.controller to javafx.fxml;
    exports br.unesp.fct.sgfp.controller;

    opens br.unesp.fct.sgfp.model;
    exports br.unesp.fct.sgfp.model;

    opens br.unesp.fct.sgfp.view to javafx.fxml;
    exports br.unesp.fct.sgfp.view;
}