/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.springone.routingbrokersample.function;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 *
 * @author Oleg Zhurakousky
 * @author Spencer Gibb
 *
 */
@SpringBootApplication
public class RoutingClientFunctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutingClientFunctionApplication.class, args);
	}

	@Bean
	public Function<String, String> uppercase() {
		return v -> {
			System.out.println("Uppercasing: " + v);
			return v.toUpperCase();
		};
	}

	@Bean
	// FOR DEMO PURPOSES ONLY
	public Function<String, String> id(Environment env) {
		String instance = env.getProperty("io.rsocket.routing.client.tags.instance_name", "N/A");
		return v -> {
			String result = v + ":" + instance;
			System.out.println("Adding id: " + result);
			return result;
		};
	}


	@Bean
	public Function<String, String> echo() {
		return v -> {
			System.out.println("Echo: " + v);
			return v;
		};
	}


	@Bean
	public Function<String, String> reverse() {
		return s -> {
			System.out.println("Reversing: " + s);
			return new StringBuilder(s).reverse().toString();
		};
	}

}
