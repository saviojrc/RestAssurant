package br.ce.wcaquino.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundo {

	public static void main(String[] args) {
		String uri = "http://restapi.wcaquino.me/ola";
		Response response = RestAssured.request(Method.GET, uri);
		
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		

	}

}
