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

package io.springone.routingbrokersample.client;

import java.time.Duration;

import io.rsocket.routing.client.spring.RoutingMetadata;
import reactor.core.publisher.Flux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;

/**
 *
 * @author Spencer Gibb
 *
 */
@SpringBootApplication
public class RoutingClientApplication {

	private final RSocketRequester requester;

	private final RoutingMetadata metadata;

	public RoutingClientApplication(RSocketRequester requester, RoutingMetadata metadata) {
		this.requester = requester;
		this.metadata = metadata;
	}

	public static void main(String[] args) {
		SpringApplication.run(RoutingClientApplication.class, args);
	}

	@EventListener
	public void onStart(ApplicationReadyEvent event) {
		Flux.interval(Duration.ofSeconds(1))
				.flatMap(aLong ->
						requester.route("uppercase|id") // used to find function
								.metadata(metadata.address("samplefn"))
								.data("\"hello\"")
								.retrieveMono(String.class))
				.subscribe(System.out::println);
	}
}
