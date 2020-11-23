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

import java.lang.reflect.Type;
import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.json.JacksonMapper;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Oleg Zhurakousky
 *
 */
@SpringBootApplication
public class FunctionApplication {

	public static void main(String[] args) {
		Product p = new Product();
		p.setName("bike");
		Event e = new Event<>();
		e.setKey("purchase");
		e.setData(p);
		JacksonMapper mapper = new JacksonMapper(new ObjectMapper());


		String jsonString = new String(mapper.toJson(e));
		System.out.println(jsonString);

		Type type = ResolvableType.forClassWithGenerics(Event.class, String.class, Product.class).getType();

//		Object o = mapper.fromJson(jsonString, type);
//		System.out.println(o);




		ApplicationContext context = SpringApplication.run(FunctionApplication.class);
	}

	@Bean
    public Consumer<Event<String, Product>> receive1() {
        return data -> {
//            logger.info("Data received from customer-1..." + data);
            Product product = data.getData(); // <-- the code is broken here.
            System.out.println(product);
//            logger.info(" product id is " + product.getId());
        };
    }

	public static class Product {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Event<K, V> {

		private K key;

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		private V data;

		public V getData() {
			return data;
		}

		public void setData(V data) {
			this.data = data;
		}
	}
}