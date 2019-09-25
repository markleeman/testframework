package framework;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyzer for TestNG tests which will re-run a failing test before reporting a failure
  */

public class RetryOnFail implements IRetryAnalyzer  {

    private int count = 0;
    private int maxCount = 2; // Number of re-try attempts

    public boolean isRetryAvailable() {
        return count <= ((Boolean.parseBoolean((System.getProperty("framework.debugMode")))) ? 0 : maxCount);
    }

    @Override
    public boolean retry(ITestResult result) {
        count++;

        if (isRetryAvailable()){
            System.out.println("Retrying test: " + result.getMethod().getMethodName() + " - attempt " + count + "/" + maxCount);
            System.out.println(result.getThrowable().toString());
        }
        else {
            System.out.println("Test failed: " + result.getMethod().getMethodName());
            System.out.println(result.getThrowable().toString());
            result.getThrowable().printStackTrace();
        }

        return isRetryAvailable();
    }
}