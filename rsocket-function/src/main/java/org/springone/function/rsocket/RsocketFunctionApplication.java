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

package org.springone.function.rsocket;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.config.RoutingFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketClient;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

/**
 *
 * @author Oleg Zhurakousky
 *
 */
@SpringBootApplication
public class RsocketFunctionApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(RsocketFunctionApplication.class,
				"--spring.rsocket.server.port=55555",
				"--spring.cloud.function.routing-expression='echo'");

		RSocketRequester rsocketRequester = context.getBean(RSocketRequester.Builder.class).tcp("localhost", 55555);

		useRSocketRequester(rsocketRequester);
//		useRSocket(rsocketRequester.rsocketClient());
	}

	static void useRSocketRequester(RSocketRequester requester) {
		requester
		.route(RoutingFunction.FUNCTION_NAME)
		.metadata("{\"foo\":\"bob\"}", MimeTypeUtils.APPLICATION_JSON)
		.data("{\"name\":\"oleg\"}")
		.retrieveMono(String.class)
		.subscribe(System.out::println);
	}

	static void useRSocket(RSocketClient rsocket) {
		Flux<String> result = rsocket
				.requestChannel(Flux.just(DefaultPayload.create("\"hello\""), DefaultPayload.create("\"blah\""))).map(Payload::getDataUtf8);
		result.subscribe(System.out::println);
	}


	@Bean
	public Function<String, String> uppercase() {
		return v -> {
			System.out.println("Uppercasing: " + v);
			return v.toUpperCase();
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
	public Function<Person, String> reverse() {
		return p -> {
			System.out.println("Reversing: " + p);
			return new StringBuilder(p.getName()).reverse().toString();
		};
	}

	private static class Person {
		private String name;

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Person: " + this.name;
		}
	}
}
