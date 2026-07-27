package utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestListener;
import org.testng.annotations.ITestAnnotation;

public class SuiteListener implements ITestListener, IAnnotationTransformer{

	// Uses the public org.testng.IAnnotationTransformer interface.
	// The reference framework imported org.testng.internal.annotations.IAnnotationTransformer,
	// which is an internal TestNG package; the public interface has the same transform
	// signature and is the supported way to do this on TestNG 7.7.0.
	@SuppressWarnings("rawtypes")
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {

		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}

}
