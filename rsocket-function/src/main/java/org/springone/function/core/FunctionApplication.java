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
package org.springone.function.core;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.catalog.SimpleFunctionRegistry.FunctionInvocationWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeType;

import reactor.core.publisher.Flux;

/**
 *
 * @author Oleg Zhurakousky
 *
 */
@SpringBootApplication
public class FunctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunctionApplication.class, "--spring.cloud.function.definition=uppercase|reverse");
//		ApplicationContext context = SpringApplication.run(FunctionApplication.class);
//		FunctionCatalog catalog = context.getBean(FunctionCatalog.class);
////		FunctionInvocationWrapper
//		Function<Flux, Flux> function = catalog.lookup("pojoUppercase|reverseReactive");
//		function.apply(Flux.just("oleg", "bob"))).subscribe(System.out::println);
	}

	@Bean
	public Function<String, String> uppercase() {
		return v -> {
			System.out.println("Uppercasing: " + v);
			return v.toUpperCase();
		};
	}

//	@Bean
//	public Function<String, String> echo() {
//		return v -> {
//			System.out.println("Echo: " + v);
//			return v;
//		};
//	}
//
//	@Bean
//	public PojoUppercase pojoUppercase() {
//		return new PojoUppercase();
//	}
//
	@Bean
	public Function<Person, String> reverse() {
		return p -> {
			System.out.println("Reversing: " + p);
			return new StringBuilder(p.getName()).reverse().toString();
		};
	}
//
//	@Bean
//	public Function<Flux<Person>, Flux<String>> reverseReactive() {
//		return flux -> flux.map(p -> {
//			System.out.println("Reversing: " + p);
//			return new StringBuilder(p.getName()).reverse().toString();
//		});
//
//	}
//
	@Bean
	public MessageConverter stringToPersonConverter() {
		return new AbstractMessageConverter(MimeType.valueOf("application/json")) {

			@Override
			protected boolean supports(Class<?> clazz) {
				return Person.class.isAssignableFrom(clazz);
			}

			@Override
			@Nullable
			protected Object convertFromInternal(
					Message<?> message, Class<?> targetClass, @Nullable Object conversionHint) {
				String name = (String) message.getPayload();
				Person person = new Person();
				person.setName(name);
				return person;
			}

			@Override
			@Nullable
			protected Object convertToInternal(
					Object payload, @Nullable MessageHeaders headers, @Nullable Object conversionHint) {

				return ((Person) payload).getName().getBytes(StandardCharsets.UTF_8);
			}
		};
	}
//
	private static class Person {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "Person: " + this.name;
		}
	}

	public static class PojoUppercase {

		public String uppercaseMe(String input) {
			return input.toUpperCase();
		}
	}

}
