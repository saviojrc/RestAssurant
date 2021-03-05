package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {

	
	@Test
	public void deveVericarPrimeiroNivel() {
		String uri = "http://restapi.wcaquino.me/users/1";
		given()
		.when()
			.get(uri)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18));
	}

	@Test
	public void deveVerificarPrimeiroNivelDeOutrasFormas() {
		String uri = "http://restapi.wcaquino.me/users/1";
		Response reposta = request(Method.GET, uri);
		

		// Path
		assertEquals(new Integer(1), reposta.path("id"));
		assertEquals(new Integer(1), reposta.path("%s", "id"));

		JsonPath jPath = new JsonPath(reposta.asString());
		assertEquals(1, jPath.getInt("id"));
		int id = JsonPath.from(reposta.asString()).getInt("id");
		assertEquals(1, id);

	}
	
	@Test
	public void deveVerificarSegundoNivel() {
		String uri = "http://restapi.wcaquino.me/users/2";
		given()
		.when()
			.get(uri)
		.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("endereco.rua", is("Rua dos bobos"));
			
	}
	
	@Test
	public void deveVerificarALista() {
		String uri = "http://restapi.wcaquino.me/users/3";
		given()
		.when()
			.get(uri)
		.then()
			.statusCode(200)
			.body("name", containsString("Ana"))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name",hasItem("Zezinho"))
			.body("filhos.name",hasItems("Zezinho","Luizinho"))
			;
			
	}

	
	@Test
	public void devoRetornarErroQuandoOUsuarioForInexistente() {
		String uri = "http://restapi.wcaquino.me/users/4";
		given()
		.when()
			.get(uri)
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
			;
	}
}
