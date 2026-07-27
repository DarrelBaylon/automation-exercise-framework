package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer{

	// Retries are DISABLED during debugging so the report shows the true
	// first-run result of each test (exactly 26 unique tests, with no retry
	// re-executing a test and turning a first-run PASS into a later FAIL).
	//
	// TestNG only ever calls retry() after a test FAILS, and this returns true
	// at most retryCount times, so a passing test is never re-executed.
	//
	// To re-enable a single retry for genuinely flaky tests once the locators
	// are confirmed stable, set RETRY_COUNT back to 1.
	private static final int RETRY_COUNT = 0;

	private int count = 0;

	public boolean retry(ITestResult result) {
		if (count < RETRY_COUNT) {
			count++;
			return true;
		}
		return false;
	}

}
