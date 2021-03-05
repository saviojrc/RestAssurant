package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {
	private String uriOla = "http://restapi.wcaquino.me/ola";
	

	@Test
	public void testeOlaMundo() {

		Response response = request(Method.GET, uriOla);

		assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		assertTrue(response.getStatusCode() == 200);
		assertTrue("O status code deveria ser 201", response.getStatusCode() == 200);
		assertEquals(200, response.statusCode());

		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

	}

	@Test
	public void devoConhecerOutrasFormasRestAssurant() {
		Response response = RestAssured.request(Method.GET, uriOla);

		ValidatableResponse validacao = response.then();

		validacao.statusCode(200);

		get(uriOla).then().statusCode(200);

		given().when().get(uriOla).then().statusCode(200);

	}

	@Test
	public void devoConhecerMatchersHamcrest() {
		assertThat("Maria", is("Maria"));
		assertThat(128, is(128));
		assertThat(128, isA(Integer.class));
		assertThat(128d, isA(Double.class));
		assertThat(128d, greaterThan(20d));
		assertThat(128d, Matchers.lessThan(130d));

		List<Integer> impares = asList(1, 3, 5, 7, 9);

		assertThat(impares, hasSize(5));
		assertThat(impares, contains(1, 3, 5, 7, 9));
		assertThat(impares, containsInAnyOrder(1, 3, 5, 9, 7));
		assertThat(impares, hasItem(1));
		assertThat(impares, hasItems(1, 5));

		assertThat("Maria", is(not("João")));
		assertThat("Maria", not("João"));
		assertThat("Joaquina", anyOf(is("Maria"), is("Joaquina")));
		assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));

	}
	
	
	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get(uriOla)
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(containsString("Mundo"))
			.body(is(not(nullValue())));
			
	}

}
