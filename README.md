# Automation Exercise Selenium WebDriver Framework

A Selenium WebDriver automation testing project that automates all 26 official test cases from the Automation Exercise website.

## Group Members

1. Baylon, Darrel Andrew P.
2. Espinili, Ryel Jan P.
3. Gandionco, Jhomari C.

## Project Description

This project is a Java-based web automation framework developed for the Automation Exercise website:

https://automationexercise.com

It automates the 26 required functional test cases covering:

- User registration
- Login and logout
- Contact form submission
- Product searching
- Product details
- Shopping cart operations
- Product categories and brands
- Checkout and payment
- Address verification
- Invoice downloading
- Product reviews
- Subscription
- Recommended products
- Page scrolling

The project follows the Page Object Model design pattern to separate page locators, page actions, test logic, and reusable browser utilities.

## Technologies Used

- Java 21
- Selenium WebDriver 4.25.0
- TestNG 7.7.0
- Apache Maven
- WebDriverManager 5.8.0
- Extent Reports
- Google Chrome
- Git and GitHub
- Visual Studio Code

## Framework Design

The project uses the Page Object Model design pattern.

The framework is separated into the following components:

- `pageObjects` — stores web element locators
- `pageEvents` — stores page actions and validations
- `base` — stores WebDriver setup and reusable utilities
- `regression` — stores the automated test cases
- `utilities` — stores supporting classes such as reporting and retry configuration
- `resources` — stores configuration and supporting test files

This structure makes the framework easier to read, maintain, and update.

## Project Structure

```text
automation-exercise-framework/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── base/
│   │       ├── pageEvents/
│   │       ├── pageObjects/
│   │       └── utilities/
│   └── test/
│       ├── java/
│       │   └── regression/
│       └── resources/
├── pom.xml
├── testng.xml
├── README.md
└── .gitignore
```

## Automated Test Cases

The framework automates the following 26 test cases:

1. Register User
2. Login User with Correct Email and Password
3. Login User with Incorrect Email and Password
4. Logout User
5. Register User with Existing Email
6. Contact Us Form
7. Verify Test Cases Page
8. Verify All Products and Product Detail Page
9. Search Product
10. Verify Subscription on Home Page
11. Verify Subscription on Cart Page
12. Add Products to Cart
13. Verify Product Quantity in Cart
14. Place Order: Register While Checkout
15. Place Order: Register Before Checkout
16. Place Order: Login Before Checkout
17. Remove Products from Cart
18. View Category Products
19. View and Cart Brand Products
20. Search Products and Verify Cart After Login
21. Add Review on Product
22. Add to Cart from Recommended Items
23. Verify Address Details on Checkout Page
24. Download Invoice After Purchase Order
25. Verify Scroll Up Using Arrow Button
26. Verify Scroll Up Without Arrow Button

## Prerequisites

Install the following software before running the project:

- Java Development Kit 21
- Apache Maven
- Google Chrome
- Git
- Visual Studio Code, IntelliJ IDEA, Eclipse, or another Java IDE

Verify Java and Maven using:

```bash
java -version
mvn -version
```

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR-USERNAME/automation-exercise-framework.git
```

Replace `YOUR-USERNAME` with the GitHub username of the repository owner.

### 2. Open the Project Folder

```bash
cd automation-exercise-framework
```

Make sure the current folder contains:

```text
pom.xml
testng.xml
src
README.md
```

### 3. Install Maven Dependencies

```bash
mvn clean compile
```

Maven will automatically download the required libraries from the `pom.xml` file.

WebDriverManager will automatically manage the ChromeDriver required by Google Chrome.

## Running the Tests

### Run All 26 Test Cases

```bash
mvn clean test
```

### Run One Test Case

Example:

```bash
mvn "-Dtest=AutomationExerciseTestCases#tc_19_ViewAndCartBrandProducts" test
```

Replace the method name with the test case method that you want to execute.

## Final Test Result

The complete regression suite was successfully executed using:

```bash
mvn clean test
```

Final result:

```text
Tests run: 26
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## Test Reports and Screenshots

The framework automatically generates reports and screenshots after test execution.

Generated files can be found in folders such as:

```text
Reports/REGRESSION_CHROME/
target/surefire-reports/
```

Screenshots are stored inside the report image folder.

Generated report folders may be excluded from GitHub through `.gitignore` because they are recreated whenever the test suite runs.

## Retry Configuration

The project includes a Retry Analyzer, but automatic retries are disabled:

```text
RETRY_COUNT = 0
```

This ensures that the final result represents the actual first execution of every test case.

## Important Notes

- Do not manually interact with the automated Chrome browser while the tests are running.
- Do not close advertisements or browser windows manually.
- The complete test suite may take approximately 40 minutes because it runs all 26 test cases sequentially.
- Internet access is required because the tests run on the live Automation Exercise website.
- Test execution time may vary depending on internet speed and website response time.

## Academic Integrity

This project was developed for academic purposes as part of the IT Elective course.

All members are expected to understand the framework, automated test cases, Page Object Model implementation, and testing process.

## Repository

GitHub Repository:

```text
https://github.com/darrelbaylon/automation-exercise-framework
```