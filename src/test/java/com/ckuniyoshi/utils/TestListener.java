package com.ckuniyoshi.utils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.List;


public class TestListener implements ITestListener {
    private List<String> failedTests = new ArrayList<>();
    private List<Integer> caseNumbers = new ArrayList<>();
    private List<String> failureMessages = new ArrayList<>();

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests.add(result.getMethod().getMethodName());
        caseNumbers.add(result.getMethod().getCurrentInvocationCount() + 1);
        failureMessages.add(result.getThrowable().getMessage());

    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Puedes loggear los casos que pasaron si es necesario
    }

    @Override
    public void onFinish(ITestContext context) {
        // Imprime detalles de los casos fallidos
        System.out.println(" ");
        System.out.println(
                "****************************************************************************************************************************************************************");
        for (int i = 0; i < failedTests.size(); i++) {
            System.out.println("Falló el caso número: " + caseNumbers.get(i) + " - Motivo: " + failureMessages.get(i));

        }
    }
}