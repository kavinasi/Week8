package com.leszko.calculator;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CalculatorController {
	@Autowired
	private Calculator calculator;

	@Autowired
	private CalculationRepository calculationRepository;

	@RequestMapping("/sum")
	String sum(@RequestParam("a") Integer a,
			@RequestParam("b") Integer b) {
		String result = String.valueOf(calculator.sum(a, b));
		calculationRepository.save(new Calculation(a.toString(), b.toString(), result, Timestamp.from(Instant.now())));
		return result;
	}

        @RequestMapping("/div")
        String div(@RequestParam("a") Integer a,
                        @RequestParam("b") Integer b) {
                String result = b!=0 ? String.valueOf(calculator.div(a, b)) : "Division by 0";
                calculationRepository.save(new Calculation(a.toString(), b.toString(), result, Timestamp.from(Instant.now())));
                return result;
        }
}

