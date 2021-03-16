package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.Matchers;
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
			.body("error", is("Usuário inexistente"));
	}
	
	@Test
	public void deveVerificarListaRaiz() {
		String uri = "http://restapi.wcaquino.me/users";
		given()
		.when()
			.get(uri)
		.then()
			.statusCode(200)
			.body("$",hasSize(3))
			.body("name", hasItems("João da Silva","Maria Joaquina","Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho","Luizinho")))
			.body("salary", contains(1234.5678f,2500,null))
			;
	}
	
	
	@Test
	public void devoFazerVerificacoesAvancadas() {
		String uri = "http://restapi.wcaquino.me/users";
		given()
		.when()
			.get(uri)
		.then()
			.statusCode(200)
			.body("$",hasSize(3))
			.body("age.findAll{ it <= 25 }.size()", is(2))
			.body("age.findAll{ it <= 25 && it >20 }.size()", is(1))
			.body("findAll{ it.age <= 25 && it.age >20 }.name", hasItem("Maria Joaquina"))
			.body("findAll{ it.age <= 25 }[0].name", is("Maria Joaquina"))
			.body("findAll{ it.age <= 25 }[-1].name", is("Ana Júlia"))
			.body("find{ it.age <= 25 }.name", is("Maria Joaquina"))
			.body("findAll{ it.name.contains('n')}.name",hasItems("Maria Joaquina","Ana Júlia"))
			.body("findAll{ it.name.length() > 10 }.name",hasItems("João da Silva","Maria Joaquina"))
			.body("name.collect{ it.toUpperCase() }", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{ it.startsWith('Maria')}.collect{ it.toUpperCase() }", hasItem("MARIA JOAQUINA"))
			.body("name.findAll{ it.startsWith('Maria')}.collect{ it.toUpperCase() }.toArray()",anyOf(arrayContaining("MARIA JOAQUINA"),arrayWithSize(1)))
			.body("age.collect{ it * 2 }",hasItems(60,50,40))
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it !=null}.sum()", is(closeTo(3734.5678f, 0.001)))
			.body("salary.findAll{it !=null}.sum()", allOf(Matchers.greaterThan(3000d),lessThan(5000d)))
			;
	}
	
	
	@Test
	public void devoUnirJsonPathComJava() {
		String uri = "http://restapi.wcaquino.me/users";
		ArrayList<String> nomes = 
				given()
				.when()
					.get(uri)
					.then()
						.statusCode(200)
						.extract().path("name.findAll{ it.startsWith('Maria')}");
		
		assertEquals(1, nomes.size());
		assertTrue(nomes.get(0).equalsIgnoreCase("Maria Joaquina"));
		assertEquals(nomes.get(0).toUpperCase(), "maria joaquina".toUpperCase());
					
	}
	
	
	
}
