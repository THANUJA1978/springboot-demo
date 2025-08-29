package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("✅ Application started...");

		RestTemplate restTemplate = new RestTemplate();

		try {
			// Step 1: Generate Webhook
			String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

			Map<String, String> requestBody = new HashMap<>();
			requestBody.put("name", "MUTYALA THANUJA"); // 🔴 Replace with your name
			requestBody.put("regNo", "22BCE20357"); // 🔴 Replace with your REG no. (even)
			requestBody.put("email", "mtreddy2508@gmail.com"); // 🔴 Replace wmvn clean packageith your email

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<Map> response = restTemplate.exchange(
					generateUrl,
					HttpMethod.POST,
					requestEntity,
					Map.class);

			String webhookUrl = (String) response.getBody().get("webhook");
			String accessToken = (String) response.getBody().get("accessToken");

			System.out.println("✅ Webhook URL: " + webhookUrl);
			System.out.println("✅ Access Token: " + accessToken);

			// Step 2: SQL Query
			String finalQuery = "SELECT " +
					"p.AMOUNT AS SALARY, " +
					"CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
					"FLOOR(DATEDIFF(CURDATE(), e.DOB)/365) AS AGE, " +
					"d.DEPARTMENT_NAME " +
					"FROM PAYMENTS p " +
					"JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
					"JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
					"WHERE DAY(p.PAYMENT_TIME) <> 1 " +
					"AND p.AMOUNT = ( " +
					"    SELECT MAX(p2.AMOUNT) " +
					"    FROM PAYMENTS p2 " +
					"    WHERE DAY(p2.PAYMENT_TIME) <> 1 " +
					");";

			Map<String, String> solutionBody = new HashMap<>();
			solutionBody.put("finalQuery", finalQuery);

			HttpHeaders solutionHeaders = new HttpHeaders();
			solutionHeaders.setContentType(MediaType.APPLICATION_JSON);
			solutionHeaders.set("Authorization", accessToken);

			HttpEntity<Map<String, String>> solutionEntity = new HttpEntity<>(solutionBody, solutionHeaders);

			ResponseEntity<String> solutionResponse = restTemplate.exchange(
					webhookUrl,
					HttpMethod.POST,
					solutionEntity,
					String.class);

			System.out.println("✅ Solution Response: " + solutionResponse.getBody());

		} catch (Exception e) {
			System.err.println("❌ Error occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
