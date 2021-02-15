package com.myorg;

import software.amazon.awscdk.core.App;

import java.util.Arrays;

public class CursoAwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new CursoAwsCdkStack(app, "CursoAwsCdkStack");

        app.synth();
    }
}
