package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static io.restassured.RestAssured.responseSpecification;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class UserXMLTest {
	
	private static RequestSpecification reqSpec;
	private static ResponseSpecification resSpec;
	
	
	@BeforeClass
	public static void setup() {
		baseURI = "https://restapi.wcaquino.me";
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.log(LogDetail.ALL);
		reqSpec = reqBuilder.build();

		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectStatusCode(200);
		resSpec = resBuilder.build();

		requestSpecification = reqSpec;
		responseSpecification = resSpec;
	}
	
	@Test
	public void devoTrabalharComXML() {
		given()
		.when()
			.get("/usersXML/3")
		.then()
			.statusCode(200)
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			.detachRootPath("filhos")
			.body("filhos.name[0]",is("Zezinho"))
			.body("filhos.name[1]",is("Luizinho"))
			.appendRootPath("filhos")
			.body("name",hasItem("Luizinho"))
			.body("name",hasItems("Luizinho","Zezinho"))
			;
		
	}
	
	
	
	@Test
	public void devoFazerPesquisasAvancadasComXML() {
		
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body("users.user.size()", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("users.user.@id", hasItems("1","2","3"))
			.body("users.user.find{it.age ==25}.name", is("Maria Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina","Ana Julia"))
			.body("users.user.salary.find{it !=null}.toDouble()", is(1234.5678d))
			.body("users.user.age.collect{it.toInteger() *2 }", hasItems(40,50,60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
			;
	}
	
	
	@Test
	public void devoFazerPesquisasAvancadasComXMLEJava() {
		ArrayList<NodeImpl> names = given()
				.when()
					.get("/usersXML")
				.then()
					.statusCode(200)
					.extract().path("users.user.name.findAll{it.toString().contains('n')}");
		
		
		assertEquals(2, names.size());
		assertEquals("Maria Joaquina".toUpperCase(), names.get(0).toString().toUpperCase());
		assertTrue("ANA JULIA".equalsIgnoreCase(names.get(1).toString()));
		
		
	}
	
	
	@Test
	public void devoFazerPesquisasAvancadasComXpath() {
		
		
		
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(hasXPath("count(/users/user)",is("3")))
			.body(hasXPath("(/users/user[@id='1'])"))
			.body(hasXPath("(//user[@id='2'])"))
			.body(hasXPath("//name[text() = 'Luizinho']/../../name",is("Ana Julia")))
				.body(hasXPath("//name[text()='Ana Julia']/following-sibling::filhos",
						allOf(containsString("Zezinho"), containsString("Luizinho"))))
			.body(hasXPath("/users/user/name",is("Jo??o da Silva")))	
			.body(hasXPath("//name",is("Jo??o da Silva")))
			.body(hasXPath("/users/user[2]/name",is("Maria Joaquina")))
			.body(hasXPath("/users/user[last()]/name",is("Ana Julia")))
			.body(hasXPath("count(/users/user/name[contains(., 'n')])",is("2")))
			.body(hasXPath("//user[age < 24]/name",is("Ana Julia")))
			.body(hasXPath("//user[age > 20 and age < 30]/name",is("Maria Joaquina")))
			;
		
	}
	
	

}
