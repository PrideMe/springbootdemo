package com.wangjikai.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author wang.
 * @date 2018/6/5.
 * Description:
 */
public class HelloWorld  extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button btn = new Button();
        btn.setText("say hello world");

        btn.setOnAction((e) -> {
            System.out.println("hello world。。。");
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        //场景
        Scene scene = new Scene(root, 600, 450);
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);


        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
